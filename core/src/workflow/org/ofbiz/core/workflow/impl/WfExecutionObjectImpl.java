/*
 * $Id$
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
 * <p><b>Title:</b> WfExecutionObjectImpl
 * <p><b>Description:</b> Workflow Execution Object implementation
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@author     David Ostrovsky (d.ostrovsky@gmx.de)
 *@created    November 2, 2001
 *@version    1.0
 */
public abstract class WfExecutionObjectImpl implements WfExecutionObject {
    
    // The value objects for this execution object (wfprocess,wfactivity)
    protected GenericValue valueObject;
    protected GenericValue dataObject;
    
    // Runtime Attributes of this object
    protected Map context;
    protected List history;    
    protected String serviceLoader;    
    protected ServiceDispatcher dispatcher;
    
    /**
     * Creates a new WfExecutionObjectImpl
     * @param valueObject The GenericValue object for the definition entity.
     * @param dataObject The GenericValue object for the runtime entity.
     */
    public WfExecutionObjectImpl(GenericValue valueObject, GenericValue dataObject, String parentId) throws WfException {
        this.valueObject = valueObject;
        this.dataObject = dataObject;        
        this.context = new HashMap();
        this.dispatcher = null;
        this.serviceLoader = null;
        this.history = null;        
        loadRuntime(parentId);
    }
    
    // Loads or creates the stored runtime workeffort data.
    private void loadRuntime(String parentId) throws WfException {
        // If no dataObject create one
        if ( this.dataObject == null ) {
            // Create a new dataObject
            try {
                String weId = getDelegator().getNextSeqId("WorkEffort").toString();
                Map dataMap = new HashMap();
                String weType = valueObject.getEntityName().equals("WorkflowActivity") ? "ACTIVITY" : "WORK_FLOW";
                dataMap.put("workEffortId",weId);
                dataMap.put("workEffortTypeId",weType);
                dataMap.put("workEffortParentId",parentId);
                dataMap.put("workflowPackageId",valueObject.getString("packageId"));
                dataMap.put("workflowProcessId",valueObject.getString("processId"));
                dataMap.put("workEffortName",valueObject.getString("objectName"));
                dataMap.put("description",valueObject.getString("description"));
                dataMap.put("createdDate",new Timestamp((new Date()).getTime()));
                dataMap.put("actualStartDate",dataMap.get("createdDate"));
                dataMap.put("lastModifiedDate",dataMap.get("createdDate"));
                dataMap.put("priority",valueObject.getLong("objectPriority"));
                dataMap.put("currentStatusId",getEntityStatus("open.not_running.not_started"));
                if ( valueObject.getEntityName().equals("WorkflowActivity") )
                    dataMap.put("workflowActivityId",valueObject.getString("activityId"));
                dataObject = getDelegator().makeValue("WorkEffort",dataMap);
                if ( dataObject != null )
                    getDelegator().create(dataObject);
                Debug.logInfo("Created new runtime object (Workeffort)");
            }
            catch ( GenericEntityException e ) {
                throw new WfException(e.getMessage(),e);
            }                      
        }
        // we have a dataObject, load the context
        else {
            // Retreive the object context
            String contextXML = null;
            try {
                GenericValue runtimeData = dataObject.getRelatedOne("ContextRuntimeData");
                contextXML = runtimeData.getString("runtimeInfo");
            }
            catch ( GenericEntityException e ) {
                throw new WfException(e.getMessage(),e);
            }
            // De-serialize the context
            if ( contextXML != null ) {
                try {
                    context = (Map) XmlSerializer.deserialize(contextXML, getDelegator());
                }
                catch ( SerializeException e ) {
                    throw new WfException(e.getMessage(),e);
                }
                catch ( IOException e ) {
                    throw new WfException(e.getMessage(),e);
                }
                catch ( Exception e ) {
                    throw new WfException(e.getMessage(),e);
                }
            }
            // Get the dispatcher and local context name
            serviceLoader = dataObject.getString("serviceLoaderName");
            dispatcher = ServiceDispatcher.getInstance(serviceLoader,getDelegator());
        }
        
    }
    
    /**
     * Getter for attribute 'name'.
     * @throws WfException General workflow exception.
     * @return Name of the object.
     */
    public String name() throws WfException {
        return dataObject.getString("workEffortName");
    }
    
    /**
     * Setter for attribute 'name'
     * @param newValue Set the name of the object.
     * @throws WfException General workflow exception.
     */
    public void setName(String newValue) throws WfException {
        try {
            dataObject.set("workEffortName",newValue);
            dataObject.store();
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
    }
    
    /**
     * Setter for attribute 'priority'.
     * @param newValue
     * @throws WfException General workflow exception
     */
    public void setPriority(int newValue) throws WfException {
        try {
            dataObject.set("priority",new Integer(newValue));
            dataObject.store();
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
    }
    
    /**
     * Getter for attribute 'priority'.
     * @throws WfException General workflow exception.
     * @return Getter Priority of
     */
    public int priority() throws WfException {
        if ( dataObject.get("priority") != null )
            return dataObject.getInteger("priority").intValue();
        return 0;  // change to default priority value
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
            statusObj = dataObject.getRelatedOne("CurrentStatusItem");
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
        if ( statusObj != null )
            stateStr = statusObj.getString("statusCode");
        
        if ( stateStr == null )
            throw new WfException("Stored state is not a valid type.");
        return stateStr;
    }
    
    /**
     * Retrieve the list of all valid states.
     * @throws WfException General workflow exception.
     * @return List of valid states.
     */
    public List validStates() throws WfException {
        String statesArr[] = { "open.running",  "open.not_running.not_started",
        "open.not_running.suspended",  "closed.completed", "closed.terminated",
        "closed.aborted" };
        ArrayList possibleStates = new ArrayList(Arrays.asList(statesArr));
        String currentState = state();                    
        if ( currentState.startsWith("closed") )
            return new ArrayList();
        if ( !currentState.startsWith("open") )
            throw new WfException("Currently in an unknown state.");
        if ( currentState.equals("open.running") ) {
            possibleStates.remove("open.running");
            possibleStates.remove("open.not_running.not_started");
            return possibleStates;
        }
        if ( currentState.equals("open.not_running.not_started") ) {
            possibleStates.remove("open.not_running.not_started");
            possibleStates.remove("open.not_running.suspended");
            possibleStates.remove("closed.completed");
            possibleStates.remove("closed.terminated");
            possibleStates.remove("closed.aborted");
            return possibleStates;
        }
        if ( currentState.equals("open.not_running.suspended") ) {
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
        if ( history.size() < 1 )
            throw new HistoryNotAvailable();
        return history.size();
    }
    
    /**
     * Abort the execution of this process or activity.
     * @throws WfException General workflow exception.
     * @throws CannotStop The execution cannot be sopped.
     * @throws NotRunning The process or activity is not yet running.
     */
    public void abort() throws WfException, CannotStop, NotRunning {
    }
    
    /**
     * @throws WfException General workflow exception.
     * @return
     */
    public List whileOpenType() throws WfException {
        String[] list = { "running", "not_running" };
        return Arrays.asList(list);
    }
    
    /**
     * @throws WfException General workflow exception.
     * @return Reason for not running.
     */
    public List whyNotRunningType() throws WfException {
        String[] list = { "not_started", "suspended" };
        return Arrays.asList(list);
    }
    
    /** Getter for the runtime key 
     * @throws WfException
     * @return Key of the runtime object
     */
    public String runtimeKey() throws WfException {
        return dataObject.getString("workEffortId");
    }
    
    /**
     * Getter for definition key
     * @throws WfException General workflow exception.
     * @return Key of the definition object.
     */
    public String key() throws WfException {
        if ( valueObject.getEntityName().equals("WorkflowProcess") )
            return valueObject.getString("processId");
        else if ( valueObject.getEntityName().equals("WorkflowActivity") )
            return valueObject.getString("activityId");
        else
            throw new WfException("Value object is of an unknown type.");
    }
    
    /**
     * Predicate to check if a 'member' is an element of the history.
     * @param member An element of the history.
     * @throws WfException General workflow exception.
     * @return true if the element of the history, false otherwise.
     */
    public boolean isMemberOfHistory(WfExecutionObject member)
    throws WfException {
        return false;
    }
    
    /**
     * @param newValue Set new process data.
     * @throws WfException General workflow exception.
     * @throws InvalidData The data is invalid.
     * @throws UpdateNotAllowed Update the context is not allowed.
     */
    public void setProcessContext(Map newValue)
    throws WfException, InvalidData, UpdateNotAllowed {
        this.context = newValue;                
        setSerializedData(newValue);        
    }
    
    /**
     * Getter for attribute 'context'.
     * @throws WfException General workflow exception.
     * @return Process context.
     */
    public Map processContext() throws WfException {return context;}
    
    /**
     * @throws WfException General workflow exception.
     * @return Current state of this object.
     */
    public List workflowStateType() throws WfException {
        String[] list = { "open", "closed" };
        return Arrays.asList(list);
    }
    
    /**
     * Terminate this process or activity.
     * @throws WfException General workflow exception
     * @throws CannotStop
     * @throws NotRunning
     */
    public void terminate() throws WfException, CannotStop, NotRunning {
    }
    
    /**
     * Setter for attribute 'description'.
     * @param newValue New value for attribute 'description'.
     * @throws WfException General workflow exception.
     */
    public void setDescription(String newValue) throws WfException {
        try {
            valueObject.set("description",newValue);
            valueObject.store();
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
    }
    
    /**
     * Getter for attribute 'description'.
     * @throws WfException General workflow exception.
     * @return Description of this object.
     */
    public String description() throws WfException {
        return valueObject.getString("description");
    }
    
    /**
     * Getter for timestamp of last state change.
     * @throws WfException General workflow exception.
     * @return Timestamp of last state change.
     */
    public Timestamp lastStateTime() throws WfException {
        if ( dataObject == null || dataObject.get("lastStatusUpdate") == null )
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
    public List getSequenceHistory(int maxNumber)
    throws WfException, HistoryNotAvailable {
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
    public Iterator getIteratorHistory(String query, Map namesInQuery)
    throws WfException, HistoryNotAvailable {
        return history.iterator();
    }
    
    /**
     * Resume this process or activity.
     * @throws WfException General workflow exception.
     * @throws CannotResume
     * @throws NotRunning
     * @throws NotSuspended
     */
    public void resume() throws WfException, CannotResume,
    NotRunning, NotSuspended {
        if ( state().startsWith("open.not_running") ) {
            if ( !state().equals("open.not_running_suspended") )
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
        String[] list = { "completed", "terminated", "aborted" };
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
        if ( validStates().contains(newState) ) {
            try {
                long now = (new Date()).getTime();
                dataObject.set("currentStatusId",getEntityStatus(newState));
                dataObject.set("lastStatusUpdate",new Timestamp(now));
                dataObject.store();
            }
            catch ( GenericEntityException e ) {
                throw new WfException(e.getMessage(),e);
            }
        }
        else {
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
        return valueObject.getDelegator();
    }
    
    /**
     * Returns the workflow local dispatcher
     * @return LocalDispatcher for this workflow
     * @throws WfException
     */
    public ServiceDispatcher getDispatcher() throws WfException {
        if ( dispatcher == null )
            throw new WfException("No dispacher set");
        return dispatcher;
    }
    
    /**
     * Sets the LocalDispatcher for this workflow
     * @param dispatcher The ServiceDispatcher to be used with this workflow
     * @param loader The name of the LocalDispatcher to use for the DispatchContext.
     * @throws WfException
     */
    public void setDispatcher(ServiceDispatcher dispatcher, String loader) throws WfException {
        this.serviceLoader = loader;
        this.dispatcher = dispatcher;
        try {
            dataObject.set("serviceLoaderName",loader);
            dataObject.store();
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
    }
    
    /**
     * Gets the GenericValue object of the definition.
     * @returns GenericValue object of the definition.
     */
    public GenericValue getDefinitionObject() {
        return valueObject;
    }
    
    /**
     * Gets the GenericValue object of the runtime workeffort.
     * @returns GenericValue object of the runtime workeffort.
     */
    public GenericValue getRuntimeObject() {
        return dataObject;
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
        try {        
            if ( dataObject.get("runtimeDataId") == null ) {
                String seqId = getDelegator().getNextSeqId("RuntimeData").toString();
                runtimeData = getDelegator().makeValue("RuntimeData",UtilMisc.toMap("runtimeDataId",seqId));
                getDelegator().create(runtimeData);
                dataObject.set("runtimeDataId",seqId);
                dataObject.store();                
            }
            else {
                runtimeData = dataObject.getRelatedOne("RuntimeData");                
            }
            //String serialized = XmlSerializer.serialize(value);
            //System.out.println(serialized);
            
            runtimeData.set("runtimeInfo",XmlSerializer.serialize(value));
            runtimeData.store();            
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
        catch ( SerializeException e ) {
            throw new InvalidData(e.getMessage(),e);
        }
        catch ( FileNotFoundException e ) {
            throw new InvalidData(e.getMessage(),e);
        }
        catch ( IOException e ) {
            throw new InvalidData(e.getMessage(),e);
        }        
    }
    
    private String getEntityStatus(String state) {
        String statesArr[] = { "open.running",  "open.not_running.not_started",
        "open.not_running.suspended",  "closed.completed", "closed.terminated",
        "closed.aborted" };
        String entityArr[] = { "WF_RUNNING", "WF_NOT_STARTED", "WF_SUSPENDED",
        "WF_COMPLETED", "WF_TERMINATED", "WF_ABORTED" };
        
        for ( int i = 0; i < statesArr.length; i++ ) {
            if ( statesArr[i].equals(state) )
                return entityArr[i];
        }
        return null;
    }
}
