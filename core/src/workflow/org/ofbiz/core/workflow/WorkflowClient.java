/*
 * $Id$
 *
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.workflow;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.service.scheduler.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.workflow.client.*;

/**
 * Workflow Client - Client API to the Workflow Engine.
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    March 5, 2001
 *@version    1.0
 */
public class WorkflowClient {

    public static final String module = WorkflowClient.class.getName();

    protected DispatchContext context;

    /**
     * Get a new instance of the Workflow Client
     * @param dctx A DispatchContext object.
     * *** Note the delegator from this object must match the delegator used by the workflow engine.
     */
    public WorkflowClient(DispatchContext context) {
        if (context == null)
            throw new IllegalArgumentException("DispatchContext cannot be null");
        this.context = context;
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
    public WfAssignment assign(String workEffortId, String partyId, String roleTypeId,
                               Timestamp fromDate, boolean append) throws WfException {

        WfActivity activity = WfFactory.getWfActivity(context.getDelegator(), workEffortId);
        WfResource resource = WfFactory.getWfResource(context.getDelegator(), null, null, partyId, roleTypeId);
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

        WfAssignment assign = WfFactory.getWfAssignment(context.getDelegator(), workEffortId, partyId,
                roleTypeId, fromDate);
        assign.accept();
    }

    /**
     * Accept an activity assignment and begin processing.
     * @param workEffortId The WorkEffort entity ID for the activitiy.
     * @param partyId The assigned / to be assigned users party ID.
     * @param roleTypeId The assigned / to be assigned role type ID.
     * @param fromDate The assignment's from date.
     * @throws WfException
     */
    public void acceptAndStart(String workEffortId, String partyId, String roleTypeId,
                               Timestamp fromDate) throws WfException {

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
    public WfAssignment delegate(String workEffortId, String fromPartyId, String fromRoleTypeId, Timestamp fromFromDate,
                                 String toPartyId, String toRoleTypeId, Timestamp toFromDate) throws WfException {

        WfAssignment fromAssign = null;

        if (fromPartyId == null && fromRoleTypeId == null && fromFromDate == null) {
            WfActivity activity = WfFactory.getWfActivity(context.getDelegator(), workEffortId);
            Iterator i = activity.getIteratorAssignment();
            fromAssign = (WfAssignment) i.next();
            if (i.hasNext())
                throw new WfException("Cannot locate the assignment to delegate from, there is more then one " +
                                      "assignment for this activity.");
        }

        fromAssign = WfFactory.getWfAssignment(context.getDelegator(), workEffortId, fromPartyId,
                                               fromRoleTypeId, fromFromDate);

        if (fromAssign.status().equals("CAL_DELEGATED"))
            throw new WfException("Assignment has already been delegated");

        fromAssign.changeStatus("CAL_DELEGATED");

        return assign(workEffortId, toPartyId, toRoleTypeId, toFromDate, true);
    }

    /**
     * Delegate and accept an activity assignment.
     * @param workEffortId The WorkEffort entity ID for the activitiy.
     * @param partyId The assigned / to be assigned users party ID.
     * @param roleTypeId The assigned / to be assigned role type ID.
     * @param fromDate The assignment's from date.
     * @param start True to attempt to start the activity.
     * @throws WfException
     */
    public void delegateAndAccept(String workEffortId, String fromPartyId, String fromRoleTypeId,
                                  Timestamp fromFromDate, String toPartyId, String toRoleTypeId,
                                  Timestamp toFromDate, boolean start) throws WfException {

        WfAssignment assign = delegate(workEffortId, fromPartyId, fromRoleTypeId, fromFromDate, toPartyId,
                                       toRoleTypeId, toFromDate);

        assign.accept();
        Debug.logVerbose("Delegated assignment.", module);
        if (start) {
            Debug.logVerbose("Starting activity.", module);
            if (!activityRunning(assign.activity()))
                start(workEffortId);
            else
                Debug.logWarning("Activity already running; not starting.", module);
        }
        else {
            Debug.logVerbose("Not starting assignment.", module);
        }
    }

    /**
     * Start the activity.
     * @param workEffortId The WorkEffort entity ID for the activitiy.
     * @throws WfException
     */
    public void start(String workEffortId) throws WfException {
        WfActivity activity = WfFactory.getWfActivity(context.getDelegator(), workEffortId);
        Debug.logVerbose("Starting activity: " + activity.name(), module);
        if (activityRunning(activity))
            throw new WfException("Activity is already running.");
        Job job = new StartActivityJob(activity);
        Debug.logVerbose("Job: " + job, module);
        try {
            context.getDispatcher().getJobManager().runJob(job);
        } catch (JobSchedulerException e) {
            throw new WfException(e.getMessage(), e);
        }
    }

    /**
     * Complete an activity assignment and follow the next transition(s).
     * @param workEffortId The WorkEffort entity ID for the activity.
     * @param partyId The assigned / to be assigned users party ID.
     * @param roleTypeId The assigned / to be assigned role type ID.
     * @param fromDate The assignment's from date.
     * @throws WfException
     */
    public void complete(String workEffortId, String partyId, String roleTypeId,
                         Timestamp fromDate, Map result) throws WfException {

        WfAssignment assign = WfFactory.getWfAssignment(context.getDelegator(), workEffortId, partyId,
                roleTypeId, fromDate);
        Job job = new CompleteAssignmentJob(assign, result);
        try {
            context.getDispatcher().getJobManager().runJob(job);
        } catch (JobSchedulerException e) {
            throw new WfException(e.getMessage(), e);
        }
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
        }
    }

    /**
     * Returns the process context of the execution object.
     * @param workEffortId The WorkEffort entity key for the execution object.
     * @throws WfException
     */
    public Map getContext(String workEffortId) throws WfException {
        WfExecutionObject obj = getExecutionObject(workEffortId);
        return obj.processContext();
    }

    /**
     * Gets the state of the execution object defined by the work effort key.
     * @param workEffortId The WorkEffort entity key for the execution object.
     * @throws WfException
     */
    public String getState(String workEffortId) throws WfException {
        WfExecutionObject obj = getExecutionObject(workEffortId);
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
        obj.changeState(state);
    }

    /**
     * Gets the priority of the execution object defined by the work effort key.
     * @param workEffortId The WorkEffort entity key for the execution object.
     * @return Priority of the execution object as a long.
     * @throws WfException
     */
    public long getPriority(String workEffortId) throws WfException {
        WfExecutionObject obj = getExecutionObject(workEffortId);
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
        obj.setPriority(priority);
    }

    private WfExecutionObject getExecutionObject(String workEffortId) {
        WfExecutionObject obj = null;
        try {
            obj = (WfExecutionObject) WfFactory.getWfActivity(context.getDelegator(), workEffortId);
        } catch (WfException e) {
            // ingore
        }
        if (obj == null) {
            try {
                obj = (WfExecutionObject) WfFactory.getWfProcess(context.getDelegator(), workEffortId);
            } catch (WfException e) {
                // ignore
            }
        }
        return obj;
    }

    // Test an activity for running state.
    private boolean activityRunning(String workEffortId) throws WfException {
        return activityRunning(WfFactory.getWfActivity(context.getDelegator(), workEffortId));
    }

    // Test an activity for running state.
    private boolean activityRunning(WfActivity activity) throws WfException {
        if (activity.state().equals("open.running"))
            return true;
        return false;
    }

}
