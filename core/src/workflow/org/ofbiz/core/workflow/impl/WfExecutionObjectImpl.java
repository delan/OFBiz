/*
 * $Id$
 *
 * Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package org.ofbiz.core.workflow.impl;

import java.io.*;
import java.util.*;
import java.sql.Timestamp;

import javax.transaction.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.serialize.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.workflow.*;

import bsh.*;

/**
 * WfExecutionObjectImpl - Workflow Execution Object implementation
 *
 *@author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 *@author     David Ostrovsky (d.ostrovsky@gmx.de)
 *@created    December 18, 2001
 *@version    1.2
 */
public abstract class WfExecutionObjectImpl implements WfExecutionObject {

    public static final String module = WfExecutionObjectImpl.class.getName();

    protected String packageId;
    protected String packageVersion;
    protected String processId;
    protected String processVersion;
    protected String activityId;
    protected String workEffortId;
    protected GenericDelegator delegator;
    protected List history;

    /**
     * Creates a new WfExecutionObjectImpl
     * @param valueObject The GenericValue object for the definition entity
     * @param parentId WorkEffort ID of the parent runtime object (null for process)
     */
    public WfExecutionObjectImpl(GenericValue valueObject, String parentId) throws WfException {
        this.packageId = valueObject.getString("packageId");
        this.packageVersion = valueObject.getString("packageVersion");
        this.processId = valueObject.getString("processId");
        this.processVersion = valueObject.getString("processVersion");
        if (valueObject.getEntityName().equals("WorkflowActivity"))
            this.activityId = valueObject.getString("activityId");
        else
            this.activityId = null;
        this.delegator = valueObject.getDelegator();
        createRuntime(parentId);
    }

    public WfExecutionObjectImpl(GenericDelegator delegator, String workEffortId) throws WfException {
        this.delegator = delegator;
        this.workEffortId = workEffortId;
        this.packageId = getRuntimeObject().getString("workflowPackageId");
        this.packageVersion = getRuntimeObject().getString("workflowPackageVersion");
        this.processId = getRuntimeObject().getString("workflowProcessId");
        this.processVersion = getRuntimeObject().getString("workflowProcessVersion");
        this.activityId = getRuntimeObject().getString("workflowActivityId");
        this.history = null;
        if (Debug.verboseOn()) Debug.logVerbose(" Package ID: " + packageId + " V: " + packageVersion, module);
        if (Debug.verboseOn()) Debug.logVerbose(" Process ID: " + processId + " V: " + processVersion, module);
        if (Debug.verboseOn()) Debug.logVerbose("Activity ID: " + activityId, module);
    }

    // creates the stored runtime workeffort data.
    private void createRuntime(String parentId) throws WfException {
        GenericValue valueObject = getDefinitionObject();
        GenericValue dataObject = null;

        workEffortId = getDelegator().getNextSeqId("WorkEffort").toString();
        Map dataMap = new HashMap();
        String weType = activityId != null ? "ACTIVITY" : "WORK_FLOW";
        dataMap.put("workEffortId", workEffortId);
        dataMap.put("workEffortTypeId", weType);
        dataMap.put("workEffortParentId", parentId);
        dataMap.put("workflowPackageId", packageId);
        dataMap.put("workflowPackageVersion", packageVersion);
        dataMap.put("workflowProcessId", processId);
        dataMap.put("workflowProcessVersion", processVersion);
        dataMap.put("workEffortName", valueObject.getString("objectName"));
        dataMap.put("description", valueObject.getString("description"));
        dataMap.put("createdDate", new Timestamp((new Date()).getTime()));
        dataMap.put("actualStartDate", dataMap.get("createdDate"));
        dataMap.put("lastModifiedDate", dataMap.get("createdDate"));
        dataMap.put("priority", valueObject.getLong("objectPriority"));
        dataMap.put("currentStatusId", getEntityStatus("open.not_running.not_started"));
        if (activityId != null)
            dataMap.put("workflowActivityId", activityId);

        try {
            Collection c = new ArrayList();
            dataObject = getDelegator().makeValue("WorkEffort", dataMap);
            c.add(dataObject);
            getDelegator().storeAll(c);
            if (Debug.infoOn()) Debug.logInfo("Created new runtime object (Workeffort: " + runtimeKey() + ")", module);
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
    }

    /**
     * Getter for attribute 'name'.
     * @throws WfException General workflow exception.
     * @return Name of the object.
     */
    public String name() throws WfException {
        return getRuntimeObject().getString("workEffortName");
    }

    /**
     * Setter for attribute 'name'
     * @param newValue Set the name of the object.
     * @throws WfException General workflow exception.
     */
    public void setName(String newValue) throws WfException {
        GenericValue dataObject = getRuntimeObject();
        try {
            dataObject.set("workEffortName", newValue);
            dataObject.store();
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
    }

    /**
     * Setter for attribute 'priority'.
     * @param newValue
     * @throws WfException General workflow exception
     */
    public void setPriority(long newValue) throws WfException {
        GenericValue dataObject = getRuntimeObject();
        try {
            dataObject.set("priority", new Long(newValue));
            dataObject.store();
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
    }

    /**
     * Getter for attribute 'priority'.
     * @throws WfException General workflow exception.
     * @return Getter Priority of
     */
    public long priority() throws WfException {
        if (getRuntimeObject().get("priority") != null)
            return getRuntimeObject().getLong("priority").longValue();
        return 0; // change to default priority value
    }

    /**
     * Retrieve the current state of this process or activity.
     * @throws WfException General workflow exception
     * @return Current state.
     */
    public String state() throws WfException {
        GenericValue statusObj = null;
        String stateStr = null;
        try {
            statusObj = getRuntimeObject().getRelatedOne("CurrentStatusItem");
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
        if (statusObj != null)
            stateStr = statusObj.getString("statusCode");

        if (stateStr == null)
            throw new WfException("Stored state is not a valid type.");
        return stateStr;
    }

    /**
     * Retrieve the list of all valid states.
     * @throws WfException General workflow exception.
     * @return List of valid states.
     */
    public List validStates() throws WfException {
        String statesArr[] = {"open.running", "open.not_running.not_started", "open.not_running.suspended",
                              "closed.completed", "closed.terminated", "closed.aborted"};
        ArrayList possibleStates = new ArrayList(Arrays.asList(statesArr));
        String currentState = state();
        if (currentState.startsWith("closed"))
            return new ArrayList();
        if (!currentState.startsWith("open"))
            throw new WfException("Currently in an unknown state.");
        if (currentState.equals("open.running")) {
            possibleStates.remove("open.running");
            possibleStates.remove("open.not_running.not_started");
            return possibleStates;
        }
        if (currentState.equals("open.not_running.not_started")) {
            possibleStates.remove("open.not_running.not_started");
            possibleStates.remove("open.not_running.suspended");
            possibleStates.remove("closed.completed");
            possibleStates.remove("closed.terminated");
            possibleStates.remove("closed.aborted");
            return possibleStates;
        }
        if (currentState.equals("open.not_running.suspended")) {
            possibleStates.remove("open.not_running.suspended");
            possibleStates.remove("open.not_running.not_started");
            possibleStates.remove("closed.complete");
            possibleStates.remove("closed.terminated");
            possibleStates.remove("closed.aborted");
            return possibleStates;
        }
        return new ArrayList();
    }

    /**
     * Getter for history count.
     * @throws WfException Generall workflow exception
     * @throws HistoryNotAvailable History can not be retrieved
     * @return Count of history Elements
     */
    public int howManyHistory() throws WfException, HistoryNotAvailable {
        if (history.size() < 1)
            throw new HistoryNotAvailable();
        return history.size();
    }

    /**
     * Abort the execution of this process or activity.
     * @throws WfException General workflow exception.
     * @throws CannotStop The execution cannot be stopped.
     * @throws NotRunning The process or activity is not yet running.
     */
    public void abort() throws WfException, CannotStop, NotRunning {
        String stateStr = "closed.aborted";
        if (!state().equals("open.running"))
            throw new NotRunning();
        if (!validStates().contains(stateStr))
            throw new CannotStop();
        changeState(stateStr);
    }

    /**
     * @throws WfException General workflow exception.
     * @return
     */
    public List whileOpenType() throws WfException {
        String[] list = {"running", "not_running"};
        return Arrays.asList(list);
    }

    /**
     * @throws WfException General workflow exception.
     * @return Reason for not running.
     */
    public List whyNotRunningType() throws WfException {
        String[] list = {"not_started", "suspended"};
        return Arrays.asList(list);
    }

    /** Getter for the runtime key
     * @throws WfException
     * @return Key of the runtime object
     */
    public String runtimeKey() throws WfException {
        return getRuntimeObject().getString("workEffortId");
    }

    /**
     * Getter for definition key
     * @throws WfException General workflow exception.
     * @return Key of the definition object.
     */
    public String key() throws WfException {
        if (activityId != null)
            return activityId;
        else
            return processId;
    }

    /**
     * Predicate to check if a 'member' is an element of the history.
     * @param member An element of the history.
     * @throws WfException General workflow exception.
     * @return true if the element of the history, false otherwise.
     */
    public boolean isMemberOfHistory(WfExecutionObject member) throws WfException {
        return false;
    }

    /**
     * Set the process context
     * @param newValue Set new process data.
     * @throws WfException General workflow exception.
     * @throws InvalidData The data is invalid.
     * @throws UpdateNotAllowed Update the context is not allowed.
     */
    public void setProcessContext(Map newValue) throws WfException, InvalidData,
            UpdateNotAllowed {
        setSerializedData(newValue);
    }

    /**
     * Set the process context (with previously stored data)
     * @param newValue RuntimeData entity key.
     * @throws WfException General workflow exception.
     * @throws InvalidData The data is invalid.
     * @throws UpdateNotAllowed Update the context is not allowed.
     */
    public void setProcessContext(String contextKey) throws WfException,
            InvalidData, UpdateNotAllowed {
        GenericValue dataObject = getRuntimeObject();
        try {
            dataObject.set("runtimeDataId", contextKey);
            dataObject.store();
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
    }

    /**
     * Get the Runtime Data key (context)
     * @return String primary key for the runtime (context) data
     * @throws WfException
     */
    public String contextKey() throws WfException {
        if (getRuntimeObject().get("runtimeDataId") == null)
            return null;
        else
            return getRuntimeObject().getString("runtimeDataId");
    }

    /**
     * Getter for attribute 'context'.
     * @throws WfException General workflow exception.
     * @return Process context.
     */
    public Map processContext() throws WfException {
        return getContext();
    }

    /**
     * @throws WfException General workflow exception.
     * @return Current state of this object.
     */
    public List workflowStateType() throws WfException {
        String[] list = {"open", "closed"};
        return Arrays.asList(list);
    }

    /**
     * Terminate this process or activity.
     * @throws WfException General workflow exception
     * @throws CannotStop
     * @throws NotRunning
     */
    public void terminate() throws WfException, CannotStop, NotRunning {
        String stateStr = "closed.terminated";
        if (!state().equals("open.running"))
            throw new NotRunning();
        if (!validStates().contains(stateStr))
            throw new CannotStop();
        changeState(stateStr);
    }

    /**
     * Setter for attribute 'description'.
     * @param newValue New value for attribute 'description'.
     * @throws WfException General workflow exception.
     */
    public void setDescription(String newValue) throws WfException {
        GenericValue valueObject = getDefinitionObject();
        try {
            valueObject.set("description", newValue);
            valueObject.store();
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
    }

    /**
     * Getter for attribute 'description'.
     * @throws WfException General workflow exception.
     * @return Description of this object.
     */
    public String description() throws WfException {
        return getDefinitionObject().getString("description");
    }

    /**
     * Getter for timestamp of last state change.
     * @throws WfException General workflow exception.
     * @return Timestamp of last state change.
     */
    public Timestamp lastStateTime() throws WfException {
        GenericValue dataObject = getRuntimeObject();
        if (dataObject == null || dataObject.get("lastStatusUpdate") == null)
            throw new WfException("No runtime object or status has never been set.");
        return dataObject.getTimestamp("lastStatusUpdate");
    }

    /**
     * Getter for history sequence.
     * @param maxNumber Maximum number of element in result list.
     * @throws WfException General workflow exception.
     * @throws HistoryNotAvailable
     * @return List of History objects.
     */
    public List getSequenceHistory(int maxNumber) throws WfException,
            HistoryNotAvailable {
        return history;
    }

    /**
     * Search in the history for specific elements.
     * @param query Search criteria.
     * @param namesInQuery elements to search.
     * @throws WfException General workflow exception
     * @throws HistoryNotAvailable
     * @return Found history elements that meet the search criteria.
     */
    public Iterator getIteratorHistory(String query,
                                       Map namesInQuery) throws WfException, HistoryNotAvailable {
        return history.iterator();
    }

    /**
     * Resume this process or activity.
     * @throws WfException General workflow exception.
     * @throws CannotResume
     * @throws NotRunning
     * @throws NotSuspended
     */
    public void resume() throws WfException, CannotResume, NotRunning, NotSuspended {
        if (state().startsWith("open.not_running")) {
            if (!state().equals("open.not_running.suspended"))
                throw new NotSuspended();
            else
                throw new NotRunning();
        }

        changeState("open.running");
    }

    /**
     * @throws WfException General workflow exception.
     * @return Termination art of this process ot activity.
     */
    public List howClosedType() throws WfException {
        String[] list = {"completed", "terminated", "aborted"};
        return Arrays.asList(list);
    }

    /**
     * Set new state for this process or activity.
     * @param newState New state value to be set.
     * @throws WfException General workflow exception.
     * @throws InvalidState The state is invalid.
     * @throws TransitionNotAllowed The transition is not allowed.
     */
    public void changeState(String newState) throws WfException, InvalidState,
            TransitionNotAllowed {
        // Test is transaction is allowed???
        GenericValue dataObject = getRuntimeObject();
        if (validStates().contains(newState)) {
            try {
                long now = (new Date()).getTime();
                dataObject.set("currentStatusId", getEntityStatus(newState));
                dataObject.set("lastStatusUpdate", new Timestamp(now));
                dataObject.store();
            } catch (GenericEntityException e) {
                throw new WfException(e.getMessage(), e);
            }
        } else {
            throw new InvalidState();
        }
    }

    /**
     * Suspend this process or activity.
     * @throws WfException General workflow exception.
     * @throws CannotSuspend
     * @throws NotRunning
     * @throws AlreadySuspended
     */
    public void suspend() throws WfException, CannotSuspend, NotRunning,
            AlreadySuspended {
        changeState("open.not_running.suspended");
    }

    /**
     * Returns the delegator being used by this workflow
     * @return GenericDelegator used for this workflow
     * @throws WfException
     */
    public GenericDelegator getDelegator() throws WfException {
        return delegator;
    }

    /**
     * Gets the GenericValue object of the definition.
     * @returns GenericValue object of the definition.
     */
    public GenericValue getDefinitionObject() throws WfException {
        String entityName = activityId != null ? "WorkflowActivity" : "WorkflowProcess";
        GenericValue value = null;
        Map fields = UtilMisc.toMap("packageId", packageId, "packageVersion", packageVersion, "processId", processId,
                "processVersion", processVersion);
        if (activityId != null)
            fields.put("activityId", activityId);
        try {
            value = getDelegator().findByPrimaryKey(entityName, fields);
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
        return value;
    }

    /**
     * Gets the GenericValue object of the runtime workeffort.
     * @returns GenericValue object of the runtime workeffort.
     */
    public GenericValue getRuntimeObject() throws WfException {
        GenericValue value = null;
        try {
            value = getDelegator().findByPrimaryKey("WorkEffort",
                    UtilMisc.toMap("workEffortId", workEffortId));
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
        return value;
    }

    /**
     * Returns the type of execution object
     * @return String name of this execution object type
     */
    public abstract String executionObjectType();

    /**
     * Updates the runtime data entity
     * @param field The field name of the entity (resultDataId,contextDataId)
     * @param value The value to serialize and set
     * @throws WfException
     */
    protected void setSerializedData(Map value) throws WfException, InvalidData {
        GenericValue runtimeData = null;
        GenericValue dataObject = getRuntimeObject();
        try {
            if (dataObject.get("runtimeDataId") == null) {
                String seqId = getDelegator().getNextSeqId("RuntimeData").toString();
                runtimeData = getDelegator().makeValue("RuntimeData",
                        UtilMisc.toMap("runtimeDataId", seqId));
                getDelegator().create(runtimeData);
                dataObject.set("runtimeDataId", seqId);
                dataObject.store();
            } else {
                runtimeData = dataObject.getRelatedOne("RuntimeData");
            }
            //String serialized = XmlSerializer.serialize(value);
            //System.out.println(serialized);

            runtimeData.set("runtimeInfo", XmlSerializer.serialize(value));
            runtimeData.store();
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        } catch (SerializeException e) {
            throw new InvalidData(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            throw new InvalidData(e.getMessage(), e);
        } catch (IOException e) {
            throw new InvalidData(e.getMessage(), e);
        }
    }

    /**
     * Get an instance of the service dispatcher
     * @return ServiceDispatcher instance for use with this workflow
     * @throws WfException
     */
    protected ServiceDispatcher getDispatcher() throws WfException {
        return ServiceDispatcher.getInstance(getServiceLoader(), getDelegator());
    }

    /**
     * Get an instance of the service dispatcher
     * @param The service loader name
     * @return ServiceDispatcher instance for use with this workflow
     * @throws WfException
     */
    protected ServiceDispatcher getDispatcher(String loader) throws WfException {
        return ServiceDispatcher.getInstance(loader, getDelegator());
    }

    /**
     * Gets the name of this workflow's service loader
     * @return String name of the loader
     * @throws WfException
     */
    protected String getServiceLoader() throws WfException {
        GenericValue dataObject = getRuntimeObject();
        if (dataObject.get("serviceLoaderName") == null)
            throw new WfException("No service loader name defined");
        return dataObject.getString("serviceLoaderName");
    }

    /**
     * Sets the name of the local dispatcher to be used with this workflow
     * @param loader The name of the loader
     * @throws WfException
     */
    public void setServiceLoader(String loader) throws WfException {
        GenericValue dataObject = getRuntimeObject();
        try {
            dataObject.set("serviceLoaderName", loader);
            dataObject.store();
            if (Debug.infoOn()) Debug.logInfo("------- EXECUTION OBJECT : Service loader set: " +
                    dataObject.getString("serviceLoaderName"), module);
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
    }

    protected String getEntityStatus(String state) {
        String statesArr[] = {"open.running", "open.not_running.not_started", "open.not_running.suspended",
                              "closed.completed", "closed.terminated", "closed.aborted"};
        String entityArr[] = {"WF_RUNNING", "WF_NOT_STARTED", "WF_SUSPENDED", "WF_COMPLETED",
                              "WF_TERMINATED", "WF_ABORTED"};

        for (int i = 0; i < statesArr.length; i++) {
            if (statesArr[i].equals(state))
                return entityArr[i];
        }
        return null;
    }

    protected String getOMGStatus(String state) {
        String statesArr[] = {"open.running", "open.not_running.not_started", "open.not_running.suspended",
                              "closed.completed", "closed.terminated", "closed.aborted"};
        String entityArr[] = {"WF_RUNNING", "WF_NOT_STARTED", "WF_SUSPENDED", "WF_COMPLETED",
                              "WF_TERMINATED", "WF_ABORTED"};

        for (int i = 0; i < entityArr.length; i++) {
            if (entityArr[i].equals(state))
                return statesArr[i];
        }
        return null;
    }

    private Map getContext() throws WfException {
        GenericValue dataObject = getRuntimeObject();
        String contextXML = null;
        Map context = null;
        if (dataObject.get("runtimeDataId") == null)
            return context;
        try {
            GenericValue runtimeData = dataObject.getRelatedOne("RuntimeData");
            contextXML = runtimeData.getString("runtimeInfo");
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
        // De-serialize the context
        if (contextXML != null) {
            try {
                context = (Map) XmlSerializer.deserialize(contextXML, getDelegator());
            } catch (SerializeException e) {
                throw new WfException(e.getMessage(), e);
            } catch (IOException e) {
                throw new WfException(e.getMessage(), e);
            } catch (Exception e) {
                throw new WfException(e.getMessage(), e);
            }
        }
        return context;
    }

    /**
     * Evaluate a condition or expression using the current context
     * @param expression The expression to evaluate
     * @return The result of the evaluation (True/False)
     * @throws WfException
     */
    protected boolean evalCondition(String expression) throws WfException {
        Map context = processContext();
        return evalCondition(expression, context);
    }

    /**
     * Evaluate a condition or expression
     * @param expression The expression to evaluate
     * @param context The context to use in evaluation
     * @return The result of the evaluation (True/False)
     * @throws WfException
     */
    protected boolean evalCondition(String expression, Map context) throws WfException {
        if (expression == null || expression.length() == 0) {
            Debug.logVerbose("Null or empty expression, returning true.", module);
            return true;
        }
        Object o = eval(expression, context);
        if (o == null)
            return false;
        else if (o instanceof Number)
            return (((Number) o).doubleValue() == 0) ? false : true;
        else
            return (!o.toString().equalsIgnoreCase("true")) ? false : true;
    }


    protected Object eval(String expression, Map context) throws WfException {
        Interpreter bsh = new Interpreter();
        Object o = null;
        if (expression == null || expression.equals(""))
            throw new WfException("Cannot evaluate empty or null expression");

        if (Debug.verboseOn()) Debug.logVerbose("Evaluating -- " + expression, module);
        if (Debug.verboseOn()) Debug.logVerbose("Using Context -- " + context, module);
        try {
            // Set the context for the condition
            Set keySet = context.keySet();
            Iterator i = keySet.iterator();
            while (i.hasNext()) {
                Object key = i.next();
                Object value = context.get(key);
                bsh.set((String) key, value);
            }
            // evaluate the expression
            o = bsh.eval(expression);
            if (Debug.verboseOn()) Debug.logVerbose("Evaluated to -- " + o, module);

            // read back the context info
            NameSpace ns = bsh.getNameSpace();
            String[] varNames = ns.getVariableNames();
            for (int x = 0; x < varNames.length; x++) {
                context.put(varNames[x], bsh.get(varNames[x]));
            }
        } catch (EvalError e) {
            Debug.logError(e, "BSH Evaluation error.", module);
        }
        return o;
    }

}

