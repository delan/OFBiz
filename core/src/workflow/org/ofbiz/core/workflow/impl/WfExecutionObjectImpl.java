/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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

import org.ofbiz.core.entity.*;
import org.ofbiz.core.serialize.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.workflow.*;

/**
 * WfExecutionObjectImpl - Workflow Execution Object implementation
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @author     David Ostrovsky (d.ostrovsky@gmx.de) 
 * @version    $Revision$
 * @since      2.0
 */
public abstract class WfExecutionObjectImpl implements WfExecutionObject {

    public static final String module = WfExecutionObjectImpl.class.getName();

    protected String packageId = null;
    protected String packageVersion = null;
    protected String processId = null;
    protected String processVersion = null;
    protected String activityId = null;
    protected String workEffortId = null;
    protected GenericDelegator delegator = null;
    protected List history = null;

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
        dataMap.put("estimatedStartDate", dataMap.get("createdDate"));
        dataMap.put("lastModifiedDate", dataMap.get("createdDate"));
        dataMap.put("priority", valueObject.getLong("objectPriority"));
        dataMap.put("currentStatusId", WfUtil.getOFBStatus("open.not_running.not_started"));
        if (activityId != null)
            dataMap.put("workflowActivityId", activityId);
        if (activityId != null && parentId != null) {
            GenericValue parentWorkEffort = getWorkEffort(parentId);
            if (parentWorkEffort != null && parentWorkEffort.get("sourceReferenceId") != null)
                dataMap.put("sourceReferenceId", parentWorkEffort.getString("sourceReferenceId"));
        }

        try {
            List lst = new ArrayList();
            dataObject = getDelegator().makeValue("WorkEffort", dataMap);
            lst.add(dataObject);
            getDelegator().storeAll(lst);
            
            String objectId = activityId != null ? activityId : processId;
            if (Debug.infoOn()) Debug.logInfo("Created new runtime object [" + objectId + "] (Workeffort: " + runtimeKey() + ")", module);
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#name()
     */   
    public String name() throws WfException {
        return getRuntimeObject().getString("workEffortName");
    }
   
    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#setName(java.lang.String)
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
     * @see org.ofbiz.core.workflow.WfExecutionObject#setPriority(long)
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
     * @see org.ofbiz.core.workflow.WfExecutionObject#priority()
     */
    public long priority() throws WfException {
        if (getRuntimeObject().get("priority") != null)
            return getRuntimeObject().getLong("priority").longValue();
        return 0; // change to default priority value
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#state()
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
     * @see org.ofbiz.core.workflow.WfExecutionObject#validStates()
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
     * @see org.ofbiz.core.workflow.WfExecutionObject#howManyHistory()
     */
    public int howManyHistory() throws WfException, HistoryNotAvailable {
        if (history.size() < 1)
            throw new HistoryNotAvailable();
        return history.size();
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#abort()
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
     * @see org.ofbiz.core.workflow.WfExecutionObject#whileOpenType()
     */
    public List whileOpenType() throws WfException {
        String[] list = {"running", "not_running"};

        return Arrays.asList(list);
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#whyNotRunningType()
     */
    public List whyNotRunningType() throws WfException {
        String[] list = {"not_started", "suspended"};

        return Arrays.asList(list);
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#runtimeKey()
     */
    public String runtimeKey() throws WfException {
        return getRuntimeObject().getString("workEffortId");
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#key()
     */
    public String key() throws WfException {
        if (activityId != null)
            return activityId;
        else
            return processId;
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#isMemberOfHistory(org.ofbiz.core.workflow.WfExecutionObject)
     */
    public boolean isMemberOfHistory(WfExecutionObject member) throws WfException {
        return false;
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#setProcessContext(java.util.Map)
     */
    public void setProcessContext(Map newValue) throws WfException, InvalidData, UpdateNotAllowed {            
        setSerializedData(newValue);
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#setProcessContext(java.lang.String)
     */
    public void setProcessContext(String contextKey) throws WfException, InvalidData, UpdateNotAllowed {            
        GenericValue dataObject = getRuntimeObject();

        try {
            dataObject.set("runtimeDataId", contextKey);
            dataObject.store();
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#contextKey()
     */
    public String contextKey() throws WfException {
        if (getRuntimeObject().get("runtimeDataId") == null)
            return null;
        else
            return getRuntimeObject().getString("runtimeDataId");
    }
 
    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#processContext()
     */
    public Map processContext() throws WfException {
        return getContext();
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#workflowStateType()
     */
    public List workflowStateType() throws WfException {
        String[] list = {"open", "closed"};
        return Arrays.asList(list);
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#terminate()
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
     * @see org.ofbiz.core.workflow.WfExecutionObject#setDescription(java.lang.String)
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
     * @see org.ofbiz.core.workflow.WfExecutionObject#description()
     */
    public String description() throws WfException {
        return getDefinitionObject().getString("description");
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#lastStateTime()
     */
    public Timestamp lastStateTime() throws WfException {
        GenericValue dataObject = getRuntimeObject();

        if (dataObject == null || dataObject.get("lastStatusUpdate") == null)
            throw new WfException("No runtime object or status has never been set.");
        return dataObject.getTimestamp("lastStatusUpdate");
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#getSequenceHistory(int)
     */
    public List getSequenceHistory(int maxNumber) throws WfException,
            HistoryNotAvailable {
        return history;
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#getIteratorHistory(java.lang.String, java.util.Map)
     */
    public Iterator getIteratorHistory(String query,
        Map namesInQuery) throws WfException, HistoryNotAvailable {
        return history.iterator();
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#resume()
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
     * @see org.ofbiz.core.workflow.WfExecutionObject#howClosedType()
     */
    public List howClosedType() throws WfException {
        String[] list = {"completed", "terminated", "aborted"};

        return Arrays.asList(list);
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#changeState(java.lang.String)
     */
    public void changeState(String newState) throws WfException, InvalidState, TransitionNotAllowed {            
        // Test is transaction is allowed???
        GenericValue dataObject = getRuntimeObject();

        if (validStates().contains(newState)) {
            try {
                long now = (new Date()).getTime();

                dataObject.set("currentStatusId", WfUtil.getOFBStatus(newState));
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
     * @see org.ofbiz.core.workflow.WfExecutionObject#suspend()
     */
    public void suspend() throws WfException, CannotSuspend, NotRunning, AlreadySuspended {            
        changeState("open.not_running.suspended");
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#getDelegator()
     */
    public GenericDelegator getDelegator() throws WfException {
        return delegator;
    }

    /**
     * @see org.ofbiz.core.workflow.WfExecutionObject#getDefinitionObject()
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
     * Getter for this type of execution object.
     * @return String
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
            // String serialized = XmlSerializer.serialize(value);
            // System.out.println(serialized);

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
     * @see org.ofbiz.core.workflow.WfExecutionObject#setServiceLoader(java.lang.String)
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
    
    private GenericValue getWorkEffort(String workEffortId) throws WfException {
        GenericValue we = null;
        try {
            we = getDelegator().findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId));
        } catch (GenericEntityException e) {
            throw new WfException("Problem getting WorkEffort entity (" + workEffortId + ")", e);
        }
        return we;
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
        
        Object o = null;
        try {
            o = BshUtil.eval(expression, context);
        } catch (bsh.EvalError e) {
            throw new WfException("Bsh evaluation error.", e);
        }

        if (o == null)
            return false;
        else if (o instanceof Number)
            return (((Number) o).doubleValue() == 0) ? false : true;
        else
            return (!o.toString().equalsIgnoreCase("true")) ? false : true;
    }
}

