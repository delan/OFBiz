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
package org.ofbiz.core.workflow.client;

import java.sql.*;
import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.service.job.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.workflow.*;

/**
 * Workflow Client - Client API to the Workflow Engine.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class WorkflowClient {

    public static final String module = WorkflowClient.class.getName();
    
    protected GenericDelegator delegator = null;
    protected LocalDispatcher dispatcher = null;
  
    protected WorkflowClient() {}
    
    /**
     * Get a new instance of the Workflow Client
     * @param delegator the GenericDelegator object which matchs the delegator used by the workflow engine.
     * @param dispatcher a LocalDispatcher object to invoke the workflow services.
     */  
    public WorkflowClient(GenericDelegator delegator, LocalDispatcher dispatcher) {
        if (delegator == null)
            throw new IllegalArgumentException("GenericDelegator cannot be null");
        if (dispatcher == null)
            throw new IllegalArgumentException("LocalDispatcher cannot be null");
        this.delegator = delegator;
        this.dispatcher = dispatcher;  
    }

    /**
     * Get a new instance of the Workflow Client
     * @param dctx A DispatchContext object.
     * *** Note the delegator from this object must match the delegator used by the workflow engine.
     */
    public WorkflowClient(DispatchContext context) {
        this(context.getDelegator(), context.getDispatcher());               
    }

    /**
     * Create an activity assignment.
     * @param workEffortId The WorkEffort entity ID for the activitiy.
     * @param partyId The assigned / to be assigned users party ID.
     * @param roleTypeId The assigned / to be assigned role type ID.
     * @param append Append this assignment to the list, if others exist.
     * @return The new assignment object.
     * @throws WfException
     */
    public WfAssignment assign(String workEffortId, String partyId, String roleTypeId, Timestamp fromDate, boolean append) throws WfException {            
        WfActivity activity = WfFactory.getWfActivity(delegator, workEffortId);
        WfResource resource = WfFactory.getWfResource(delegator, null, null, partyId, roleTypeId);

        if (!append) {
            Iterator i = activity.getIteratorAssignment();

            while (i.hasNext()) {
                WfAssignment a = (WfAssignment) i.next();
                a.remove();
            }
        }
        return WfFactory.getWfAssignment(activity, resource, fromDate, true);
    }

    /**
     * Accept an activity assignment.
     * @param workEffortId The WorkEffort entity ID for the activitiy.
     * @param partyId The assigned / to be assigned users party ID.
     * @param roleTypeId The assigned / to be assigned role type ID.
     * @param fromDate The assignment's from date.
     * @throws WfException
     */
    public void accept(String workEffortId, String partyId, String roleTypeId, Timestamp fromDate) throws WfException {
        WfAssignment assign = WfFactory.getWfAssignment(delegator, workEffortId, partyId, roleTypeId, fromDate);           
        assign.accept();
    }

    /**
     * Accept an activity assignment and begin processing.
     * @param workEffortId The WorkEffort entity ID for the activitiy.
     * @param partyId The assigned / to be assigned users party ID.
     * @param roleTypeId The assigned / to be assigned role type ID.
     * @param fromDate The assignment's from date.
     * @return GenericResultWaiter of the start job.
     * @throws WfException
     */
    public void acceptAndStart(String workEffortId, String partyId, String roleTypeId, Timestamp fromDate) throws WfException {        
        accept(workEffortId, partyId, roleTypeId, fromDate);
        start(workEffortId);
    }

    /**
     * Delegate an activity assignment.
     * @param workEffortId The WorkEffort entity ID for the activitiy.
     * @param fromPartyId The current assignment partyId.
     * @param fromRoleTypeId The current assignment roleTypeId.
     * @param fromFromDate The current assignment fromDate.
     * @param toPartyId The new delegated assignment partyId.
     * @param toRoleTypeId The new delegated assignment roleTypeId.
     * @param toFromDate The new delegated assignment fromDate.
     * @return The new assignment object.
     * @throws WfException
     */
    public WfAssignment delegate(String workEffortId, String fromPartyId, String fromRoleTypeId, Timestamp fromFromDate, String toPartyId, String toRoleTypeId, Timestamp toFromDate) throws WfException {                    
        WfActivity activity = WfFactory.getWfActivity(delegator, workEffortId);
        WfAssignment fromAssign = null;
        
        // check status and delegateAfterStart attribute
        if (activity.state().equals("open.running") && !activity.getDefinitionObject().getBoolean("delegateAfterStart").booleanValue())                 
            throw new WfException("This activity cannot be delegated once it has been started");
                      
        if (fromPartyId == null && fromRoleTypeId == null && fromFromDate == null) {            
            Iterator i = activity.getIteratorAssignment();
            fromAssign = (WfAssignment) i.next();
            if (i.hasNext())
                throw new WfException("Cannot locate the assignment to delegate from, there is more then one " +
                        "assignment for this activity.");
        }

        if (fromAssign == null)
            fromAssign = WfFactory.getWfAssignment(delegator, workEffortId, fromPartyId, fromRoleTypeId, fromFromDate);                    
        fromAssign.delegate();   
        
        // check for a restartOnDelegate
        WfActivity newActivity = null;
        if (activity.getDefinitionObject().getBoolean("restartOnDelegate").booleanValue()) {  
            // this only applies to running single assignment activities
            if (activity.state().equals("open.running") && activity.howManyAssignment() == 0) {
                try {
                    activity.abort();
                } catch (CannotStop cs) {
                    throw new WfException("Cannot stop the current activity");
                } catch (NotRunning nr) {
                    throw new WfException("Current activity is not running; cannot abort");
                }
                String parentProcessId = activity.container().runtimeKey();
                newActivity = WfFactory.getWfActivity(activity.getDefinitionObject(), parentProcessId);
            }         
        }    
        
        WfAssignment assign = null;
        if (newActivity != null) {
            assign = assign(newActivity.runtimeKey(), toPartyId, toRoleTypeId, toFromDate, true);
        } else {
            assign = assign(workEffortId, toPartyId, toRoleTypeId, toFromDate, true);
        }
        
        return assign;
    }

    /**
     * Delegate and accept an activity assignment.
     * @param workEffortId The WorkEffort entity ID for the activitiy.
     * @param partyId The assigned / to be assigned users party ID.
     * @param roleTypeId The assigned / to be assigned role type ID.
     * @param fromDate The assignment's from date.
     * @param start True to attempt to start the activity.
     * @return GenericResultWaiter of the start job.
     * @throws WfException
     */
    public void delegateAndAccept(String workEffortId, String fromPartyId, String fromRoleTypeId, Timestamp fromFromDate, String toPartyId, String toRoleTypeId, Timestamp toFromDate, boolean start) throws WfException {                                 
        WfAssignment assign = delegate(workEffortId, fromPartyId, fromRoleTypeId, fromFromDate, toPartyId, toRoleTypeId, toFromDate);
                      
        assign.accept();
        Debug.logVerbose("Delegated assignment.", module);
        if (start) {
            Debug.logVerbose("Starting activity.", module);
            if (!activityRunning(assign.activity()))
                start(assign.activity().runtimeKey());
            else
                Debug.logWarning("Activity already running; not starting.", module);
        } else {
            Debug.logVerbose("Not starting assignment.", module);
        }              
    }

    /**
     * Start the activity.
     * @param workEffortId The WorkEffort entity ID for the activitiy.
     * @return GenericResultWaiter of the start job.
     * @throws WfException
     */
    public void start(String workEffortId) throws WfException {        
        WfActivity activity = WfFactory.getWfActivity(delegator, workEffortId);

        if (Debug.verboseOn()) Debug.logVerbose("Starting activity: " + activity.name(), module);
        if (activityRunning(activity))
            throw new WfException("Activity is already running");
            
        Job job = new StartActivityJob(activity);

        if (Debug.verboseOn()) Debug.logVerbose("Job: " + job, module);
        try {
            dispatcher.getJobManager().runJob(job);
        } catch (JobManagerException e) {
            throw new WfException(e.getMessage(), e);
        }
               
    }

    /**
     * Complete an activity assignment and follow the next transition(s).
     * @param workEffortId The WorkEffort entity ID for the activity.
     * @param partyId The assigned / to be assigned users party ID.
     * @param roleTypeId The assigned / to be assigned role type ID.
     * @param fromDate The assignment's from date.
     * @return GenericResultWaiter for the complete job.
     * @throws WfException
     */
    public void complete(String workEffortId, String partyId, String roleTypeId, Timestamp fromDate, Map result) throws WfException {                    
        WfAssignment assign = WfFactory.getWfAssignment(delegator, workEffortId, partyId, roleTypeId, fromDate);
        if (result != null && result.size() > 0)
            assign.setResult(result);
        assign.complete();        
    }
    
    /**
     * Suspend an activity
     * @param workEffortId The WorkEffort entity key for the activity object
     * @throws WfException
     */
    public void suspend(String workEffortId) throws WfException {
        WfActivity activity = WfFactory.getWfActivity(delegator, workEffortId);
        
        if (Debug.verboseOn()) Debug.logVerbose("Suspending activity: " + activity.name(), module);
        if (!activityRunning(activity))
            throw new WfException("Activity is not running");
            
        activity.suspend();
    }        
       
    /**
     * Resume an activity
     * @param workEffortId The WorkEffort entity key for the activity object
     * @throws WfException
     */
    public void resume(String workEffortId) throws WfException {
        WfActivity activity = WfFactory.getWfActivity(delegator, workEffortId);

        if (Debug.verboseOn()) Debug.logVerbose("Resuming activity: " + activity.name(), module);
        if (activityRunning(activity))
            throw new WfException("Activity is already running");

        activity.resume();
    }
                
    /**
     * Append data to the execution object's process context.
     * @param workEffortId The WorkEffort entity key for the execution object.
     * @param append The data to append.
     * @throws WfException
     */
    public void appendContext(String workEffortId, Map append) throws WfException {
        WfExecutionObject obj = getExecutionObject(workEffortId);

        if (obj != null) {
            Map oCtx = obj.processContext();

            oCtx.putAll(append);
            obj.setProcessContext(oCtx);
            if (Debug.verboseOn()) Debug.logVerbose("ProcessContext (" + workEffortId + ") => " + obj.processContext(), module);
        }
    }

    /**
     * Returns the process context of the execution object.
     * @param workEffortId The WorkEffort entity key for the execution object.
     * @throws WfException
     */
    public Map getContext(String workEffortId) throws WfException {
        WfExecutionObject obj = getExecutionObject(workEffortId);

        if (obj == null) throw new WfException("Invalid Execution Object (null value)");
        if (Debug.verboseOn()) Debug.logVerbose("ProcessContext (" + workEffortId + ") => " + obj.processContext(), module);
        return obj.processContext();
    }

    /**
     * Gets the state of the execution object defined by the work effort key.
     * @param workEffortId The WorkEffort entity key for the execution object.
     * @throws WfException
     */
    public String getState(String workEffortId) throws WfException {
        WfExecutionObject obj = getExecutionObject(workEffortId);

        if (obj == null) throw new WfException("Invalid Execution Object (null value)");
        if (Debug.verboseOn()) Debug.logVerbose("Current State (" + workEffortId + ") => " + obj.state(), module);
        return obj.state();
    }

    /**
     * Set the state of the execution object defined by the work effort key.
     * @param workEffortId The WorkEffort entity key for the execution object.
     * @param state The new state of the execution object.
     * @return Current state of the execution object as a string.
     * @throws WfException If state change is not allowed.
     */
    public void setState(String workEffortId, String state) throws WfException {
        WfExecutionObject obj = getExecutionObject(workEffortId);

        if (obj == null) throw new WfException("Invalid Execution Object (null value)");
        obj.changeState(state);
        if (Debug.verboseOn()) Debug.logVerbose("Current State (" + workEffortId + ") => " + obj.state(), module);
    }

    /**
     * Gets the priority of the execution object defined by the work effort key.
     * @param workEffortId The WorkEffort entity key for the execution object.
     * @return Priority of the execution object as a long.
     * @throws WfException
     */
    public long getPriority(String workEffortId) throws WfException {
        WfExecutionObject obj = getExecutionObject(workEffortId);

        if (obj == null) throw new WfException("Invalid Execution Object (null value)");
        if (Debug.verboseOn()) Debug.logVerbose("Current Priority (" + workEffortId + ") => " + obj.priority(), module);
        return obj.priority();
    }

    /**
     * Set the priority of the execution object defined by the work effort key.
     * @param workEffortId The WorkEffort entity key for the execution object.
     * @param priority The new priority of the execution object.
     * @throws WfException If state change is not allowed.
     */
    public void setPriority(String workEffortId, long priority) throws WfException {
        WfExecutionObject obj = getExecutionObject(workEffortId);

        if (obj == null) throw new WfException("Invalid Execution Object (null value)");
        obj.setPriority(priority);
        if (Debug.verboseOn()) Debug.logVerbose("Current Priority (" + workEffortId + ") => " + obj.priority(), module);
    }

    // Get the execution object for the workeffort
    private WfExecutionObject getExecutionObject(String workEffortId) {
        WfExecutionObject obj = null;

        try {
            obj = (WfExecutionObject) WfFactory.getWfActivity(delegator, workEffortId);
        } catch (WfException e) {// ingore
        }
        if (obj == null) {
            try {
                obj = (WfExecutionObject) WfFactory.getWfProcess(delegator, workEffortId);
            } catch (WfException e) {// ignore
            }
        }
        return obj;
    }

    // Test an activity for running state.
    private boolean activityRunning(String workEffortId) throws WfException {
        return activityRunning(WfFactory.getWfActivity(delegator, workEffortId));
    }

    // Test an activity for running state.
    private boolean activityRunning(WfActivity activity) throws WfException {
        if (activity.state().equals("open.running"))
            return true;
        return false;
    }

}
