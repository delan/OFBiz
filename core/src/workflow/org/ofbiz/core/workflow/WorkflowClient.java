/*
 * $Id$
 */

package org.ofbiz.core.workflow;

import java.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Workflow Client
 * <p><b>Description:</b> 'Services' and 'Workers' for interaction with Workflow API
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
 *@created    December 5, 2001
 *@version    1.0
 */
public class WorkflowClient {
    
    // -------------------------------------------------------------------
    // Client 'Services' Methods
    // -------------------------------------------------------------------
    
    /** Marks an activity as complete */
    public static Map completeActivity(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        
        String workEffortId = (String) context.get("workEffortId");
        try {
            WfActivity activity = getActivity(delegator,workEffortId);
            try {
                activity.complete();
            }
            catch ( WfException e) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE,e.getMessage());
            }
        }
        catch ( RuntimeException e ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,e.getMessage());
        }
        
        return result;
    }
    
    /** Change the state of an activity */
    public static Map changeState(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        
        String workEffortId = (String) context.get("workEffortId");
        try {
            WfActivity activity = getActivity(delegator,workEffortId);
            String newState = (String) context.get("newStatus");
            try {
                activity.changeState(newState);
            }
            catch ( WfException e ) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE,e.getMessage());
            }
        }
        catch ( RuntimeException e ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,e.getMessage());
        }
        
        
        return result;
    }
    
    /** Manually activate an activity */
    public static Map activateActivity(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        
        String workEffortId = (String) context.get("workEffortId");
        try {
            WfActivity activity = getActivity(delegator,workEffortId);
            try {
                activity.activate(true);
            }
            catch ( WfException e ) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE,e.getMessage());
            }
        }
        catch ( RuntimeException e ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,e.getMessage());
        }
        
        
        return result;
    }
    
    /** Assign activity to a new or additional party */
    public static Map assignTask(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        
        String workEffortId = (String) context.get("workEffortId");
        String partyId = (String) context.get("partyId");
        boolean removeOldAssign = context.get("removeOldAssignements").equals("true") ? true : false;
        
        try {
            WfActivity activity = getActivity(delegator,workEffortId);
            try {
                WfResource resource = WfFactory.getWfResource(delegator,null,null,partyId,null);
                activity.assign(resource,removeOldAssign ? false : true);
            }
            catch ( WfException e ) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE,e.getMessage());
            }
        }
        catch ( RuntimeException e ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,e.getMessage());
        }
        
        return result;
    }
    
    
    // -------------------------------------------------------------------
    // Client 'Worker' Methods
    // -------------------------------------------------------------------
    
    /**
     * Gets the GenericValue object for a workeffort id
     *@param delegator GenericDelegator used to look up this entity
     *@param workEffortId The ID of the record to find
     *@return GenericValue object of this entity
     */
    public static GenericValue getWorkEffort(GenericDelegator delegator, String workEffortId) {
        GenericValue value = null;
        try {
            Map fields = UtilMisc.toMap("workEffortId",workEffortId);
            Collection c = delegator.findByAnd("WorkEffort", fields);
            ArrayList l = new ArrayList(c);
            value = (GenericValue) l.get(0);
        }
        catch ( GenericEntityException e ) {
            throw new RuntimeException(e.getMessage());
        }
        
        if ( value == null )
            throw new RuntimeException("WorkEffort entity returned null");
        return value;
    }
    
    /**
     * Gets a WfProcessMgr object from using the package and process id's
     *@param delegator GenericDelegator used to find this process
     *@param pkg The PackageID
     *@param pid The ProcessID
     *@return WfProcessMgr associated with this package and process
     */
    public static WfProcessMgr getProcessManager(GenericDelegator delegator, String pkg, String pid) {
        WfProcessMgr pm = null;
        try {
            pm = WfFactory.getWfProcessMgr(delegator,pkg,pid);
        }
        catch ( WfException e ) {
            throw new RuntimeException(e.getMessage());
        }
        
        if ( pm == null )
            throw new RuntimeException("WfProcessMgr returned null");
        return pm;
    }
    
    /**
     * Gets a WfProcess object from the specified WfProcessMgr
     *@param mgr The WfProcessMgr containing this process
     *@param workEffortId The workeffort (runtime) id of this process
     *@return WfProcess associated with the workeffort id
     */
    public static WfProcess getProcess(WfProcessMgr mgr, String workEffortId) {
        WfProcess process = null;
        try {
            Iterator i = mgr.getIteratorProcess();
            while ( i.hasNext() && process == null ) {
                WfProcess p = (WfProcess) i.next();
                if ( p.runtimeKey().equals(workEffortId) )
                    process = p;
            }
        }
        catch ( WfException e ) {
            throw new RuntimeException(e.getMessage());
        }
        if ( process == null )
            throw new RuntimeException("Cannot get the WfProcess from the manager");
        return process;
    }
    
    /**
     * Gets a WfProcess object from a specific process workeffort id
     *@param delegator The GenericDelegator to use to locate this activity
     *@param workEffortId The workeffort id associated with this process
     *@return WfProcess associated with the defined workeffort id
     */
    public static WfProcess getProcess(GenericDelegator delegator, String workEffortId) {
        GenericValue workEffort = getWorkEffort(delegator,workEffortId);
        String packageId = workEffort.getString("workflowPackageId");
        String processId = workEffort.getString("workflowProcessId");
        
        WfProcessMgr mgr = getProcessManager(delegator,packageId,processId);
        WfProcess process = getProcess(mgr,workEffortId);
        return process;
    }
    
    /**
     * Gets a WfActivity object from a WfProcess
     *@param process The WfProcess containing the activity
     *@param workEffortId The workeffort (runtime) id of the activity
     *@return WfActivity associated with the defined workeffort id
     */
    public static WfActivity getActivity(WfProcess process, String workEffortId) {
        WfActivity activity = null;
        try {
            Iterator i = process.getIteratorStep();
            while ( i.hasNext() && activity == null ) {
                WfActivity a = (WfActivity) i.next();
                if ( a.runtimeKey().equals(workEffortId) )
                    activity = a;
            }
        }
        catch ( WfException e ) {
            throw new RuntimeException(e.getMessage());
        }
        if ( activity == null )
            throw new RuntimeException("Cannot get the WfActivity from the process");
        return activity;
    }
    
    /**
     * Gets a WfActivity object from a specific activity workeffort id
     *@param delegator The GenericDelegator to use to locate this activity
     *@param workEffortId The workeffort id associated with this activity
     *@return WfActivity associated with the defined workeffort id
     */
    public static WfActivity getActivity(GenericDelegator delegator, String workEffortId) {
        GenericValue workEffort = getWorkEffort(delegator,workEffortId);
        String processWorkEffortId = workEffort.getString("workEffortParentId");
        String packageId = workEffort.getString("workflowPackageId");
        String processId = workEffort.getString("workflowProcessId");
        
        WfProcessMgr mgr = getProcessManager(delegator,packageId,processId);
        WfProcess process = getProcess(mgr,processWorkEffortId);
        WfActivity activity = getActivity(process,workEffortId);
        return activity;
    }
    
    
}




