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
package org.ofbiz.core.workflow;

import java.sql.Timestamp;
import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.workflow.impl.*;

/**
 * WfFactory - Workflow Factory Class
 *
 *@author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 *@created    October 31, 2001
 *@version    1.0
 */
public class WfFactory {

    // a cache of loaded objects
    private static Map manager = new HashMap();
    private static Map process = new HashMap();
    private static Map activity = new HashMap();
    private static Map assign = new HashMap();
    private static Map resource = new HashMap();
    private static Map client = new HashMap();

    /**
     * Creates a new {@link WfActivity} instance.
     * @param value GenericValue object defining this activity.
     * @param process The WorkEffort key of the parent process
     * @return An instance of the WfActivify Interface
     * @throws WfException
     */
    public static WfActivity getWfActivity(GenericValue value, String process) throws WfException {
        if (value == null) throw new WfException("Activity definition value object cannot be null");
        if (process == null) throw new WfException("Parent process WorkEffort key cannot be null");
        WfActivity act = new WfActivityImpl(value, process);
        String mapKey = value.getDelegator().getDelegatorName() + ":" + act.runtimeKey();
        activity.put(mapKey, act);
        return act;
    }

    public static WfActivity getWfActivity(GenericDelegator delegator, String workEffortId) throws WfException {
        if (delegator == null) throw new WfException("The delegator object cannot be null");
        if (workEffortId == null) throw new WfException("The WorkEffort key cannot be null");
        String mapKey = delegator.getDelegatorName() + ":" + workEffortId;

        if (!activity.containsKey(mapKey)) {
            synchronized (WfFactory.class) {
                if (!activity.containsKey(mapKey))
                    activity.put(mapKey, new WfActivityImpl(delegator, workEffortId));
            }
        }
        return (WfActivity) activity.get(mapKey);
    }

    /**
     * Creates a new {@link WfAssignment} instance.
     * @return An instance of the WfAssignment Interface
     * @throws WfException
     */
    public static WfAssignment getWfAssignment(WfActivity activity, WfResource resource,
        Timestamp fromDate, boolean create) throws WfException {
        if (activity == null) throw new WfException("WfActivity cannot be null");
        if (resource == null) throw new WfException("WfResource cannot be null");
        if (fromDate == null) fromDate = new Timestamp(new Date().getTime());
        String mapKey = activity.runtimeKey() + ":" + resource.resourcePartyId() + ":" +
                resource.resourceRoleId() + ":" + fromDate.getTime();

        if (!assign.containsKey(mapKey)) {
            synchronized (WfFactory.class) {
                if (!assign.containsKey(mapKey))
                    assign.put(mapKey, new WfAssignmentImpl(activity, resource, fromDate, create));
            }
        }
        return (WfAssignment) assign.get(mapKey);
    }

    public static WfAssignment getWfAssignment(GenericDelegator delegator, String work, String party, String role,
        Timestamp from) throws WfException {
        WfActivity act = getWfActivity(delegator, work);
        WfResource res = getWfResource(delegator, null, null, party, role);
        return getWfAssignment(act, res, from, false);
    }

    /** 
     * Creates a new {@link WfProcess} instance.
     * @param value The GenericValue object for the process definition.
     * @param mgr The WfProcessMgr which is managing this process.
     * @return An instance of the WfProcess Interface.
     * @throws WfException
     */
    public static WfProcess getWfProcess(GenericValue value, WfProcessMgr mgr) throws WfException {
        if (value == null) throw new WfException("Process definition value object cannot be null");
        if (mgr == null) throw new WfException("WfProcessMgr cannot be null");
        WfProcess proc = new WfProcessImpl(value, mgr);
        String mapKey = value.getDelegator().getDelegatorName() + ":" + proc.runtimeKey();
        process.put(mapKey, proc);
        return proc;
    }

    public static WfProcess getWfProcess(GenericDelegator delegator, String workEffortId) throws WfException {
        if (delegator == null) throw new WfException("The delegator object cannot be null");
        if (workEffortId == null) throw new WfException("The WorkEffort key cannot be null");
        String mapKey = delegator.getDelegatorName() + ":" + workEffortId;
        if (!process.containsKey(mapKey)) {
            synchronized (WfFactory.class) {
                if (!process.containsKey(mapKey))
                    process.put(mapKey, new WfProcessImpl(delegator, workEffortId));
            }
        }
        return (WfProcess) process.get(mapKey);
    }

    /** 
     * Creates a new {@link WfProcessMgr} instance.
     * @param del The GenericDelegator to use for this manager.
     * @param pkg The Workflow Package ID.
     * @param pkver The Workflow Package Version.
     * @param pid The Workflow Process ID.
     * @param pver The Workflow Process Version.
     * @return An instance of the WfProcessMgr Interface.
     * @throws WfException
     */
    public static WfProcessMgr getWfProcessMgr(GenericDelegator del, String pkg, String pkver, String pid, String pver) throws WfException {
        if (del == null) throw new WfException("Delegator cannot be null");
        if (pkg == null) throw new WfException("Workflow package id cannot be null.");
        if (pid == null) throw new WfException("Workflow process id cannot be null");

        String mapKey = del.getDelegatorName() + ":" + pkg + ":" + pid;
        WfProcessMgr mgr = null;

        if (!manager.containsKey(mapKey)) {
            synchronized (WfFactory.class) {
                if (!manager.containsKey(mapKey))
                    manager.put(mapKey, new WfProcessMgrImpl(del, pkg, pkver, pid, pver));
            }
        }
        return (WfProcessMgr) manager.get(mapKey);
    }

    /** 
     * Creates a new {@link WfRequester} instance.
     * @return An instance of the WfRequester Interface.
     * @throws WfException
     */
    public static WfRequester getWfRequester() throws WfException {
        return new WfRequesterImpl();
    }

    /** 
     * Creates a new {@link WfResource} instance.
     * @param value The GenericValue object of the WorkflowParticipant
     * @throws WfException
     * @return An instance of the WfResource Interface.
     */
    public static WfResource getWfResource(GenericValue value) throws WfException {
        if (value == null) throw new WfException("Value object for WfResource definition cannot be null");
        WfResource res = new WfResourceImpl(value);
        String mapKey = value.getDelegator().getDelegatorName() + ":" + res.resourceKey() + ":" + res.resourceName() +
                ":" + res.resourcePartyId() + ":" + res.resourceRoleId();

        resource.put(mapKey, res);
        return res;
    }

    /** 
     * Creates a new {@link WfResource} instance.
     * @param delegator The GenericDelegator for this instance
     * @param key The key for the resource
     * @param name The name of the resource
     * @param party The partyId of the resource
     * @param role The roleTypeId of the resource
     * @return An instance of the WfResource Interface.
     * @throws WfException
     */
    public static WfResource getWfResource(GenericDelegator delegator, String key, String name, String party, String role) throws WfException {        
        if (party == null) party = "_NA_";
        if (role == null) role = "_NA_";
        String mapKey = delegator.getDelegatorName() + ":" + key + ":" + name + ":" + party + ":" + role;

        if (!resource.containsKey(mapKey)) {
            synchronized (WfFactory.class) {
                if (!resource.containsKey(mapKey))
                    resource.put(mapKey, new WfResourceImpl(delegator, key, name, party, role));
            }
        }
        return (WfResource) resource.get(mapKey);
    }

    /** 
     * Creates a new {@link WfEventAudit} instance.
     * @return An instance of the WfEventAudit Interface.
     * @throws WfException
     */
    public static WfEventAudit getWfEventAudit(WfExecutionObject object, String type) throws WfException {
        return new WfEventAuditImpl(object, type);
    }

    public static WorkflowClient getClient(DispatchContext dctx) {
        if (!client.containsKey(dctx)) {
            synchronized (WfFactory.class) {
                if (!client.containsKey(dctx))
                    resource.put(dctx, new WorkflowClient(dctx));
            }
        }
        return (WorkflowClient) resource.get(dctx);
    }

}
