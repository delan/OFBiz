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
     * @throws WfException
     */
    public void assign(String workEffortId, String partyId, String roleTypeId, boolean append) throws WfException {
        WfActivity activity = WfFactory.getWfActivity(context.getDelegator(), workEffortId);
        WfResource resource = WfFactory.getWfResource(context.getDelegator(), null, null, partyId, roleTypeId);
        activity.assign(resource, append);
    }

    /**
     * Accept an activity assignment and begin processing.
     * @param workEffortId The WorkEffort entity ID for the activitiy.
     * @param partyId The assigned / to be assigned users party ID.
     * @param roleTypeId The assigned / to be assigned role type ID.
     * @param fromDate The assignment's from date.
     * @throws WfException
     */
    public void accept(String workEffortId, String partyId, String roleTypeId, Timestamp fromDate) throws WfException {
        WfAssignment assign = WfFactory.getWfAssignment(context.getDelegator(), workEffortId, partyId, roleTypeId, fromDate);
        Job job = new AssignmentAcceptJob(assign);
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
    public void complete(String workEffortId, String partyId, String roleTypeId, Timestamp fromDate, Map result) throws WfException {
        WfAssignment assign = WfFactory.getWfAssignment(context.getDelegator(), workEffortId, partyId, roleTypeId, fromDate);
        Job job = new AssignmentCompleteJob(assign, result);
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
}
