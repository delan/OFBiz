/*
 * $Id$
 */

package org.ofbiz.core.workflow;

import java.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Workflow Client Services
 * <p><b>Description:</b> Services for client interaction with workflow API
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
public class WorkflowClientServices {
    
    // -------------------------------------------------------------------
    // Service methods
    // -------------------------------------------------------------------
    
    public static Map completeActivity(Map context) {
        Map result = new HashMap();
        result.put("response","");
        DispatchContext ctx = (DispatchContext) context.get("DISPATCHCONTEXT");
        GenericDelegator delegator = ctx.getDelegator();
        
        String workEffortId = (String) context.get("workEffortId");
        GenericValue workEffort = getWorkEffort(delegator,workEffortId);
        
        String processWorkEffortId = workEffort.getString("workEffortParentId");
        String packageId = workEffort.getString("workflowPackageId");
        String processId = workEffort.getString("workflowProcessId");
        
        WfProcessMgr mgr = getProcessManager(delegator,packageId,processId);
        WfProcess process = getProcess(mgr,processWorkEffortId);
        WfActivity activity = getActivity(process,workEffortId);
        
        String newState = (String) context.get("newStatus");
        try {
            activity.complete();
        }
        catch ( WfException e) {
            result.put("response",e.getMessage());
        }
        
        return result;
    }
    
    // -------------------------------------------------------------------
    // Helper methods for geting objects
    // -------------------------------------------------------------------
    
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
    
    public static WfProcessMgr getProcessManager(GenericDelegator delegator, String pkg, String pid) {
        WfProcessMgr pm = null;
        try {
            pm = WfFactory.newWfProcessMgr(delegator,pkg,pid);
        }
        catch ( WfException e ) {
            throw new RuntimeException(e.getMessage());
        }
        
        if ( pm == null )
            throw new RuntimeException("WfProcessMgr returned null");
        return pm;
    }
    
    public static WfProcess getProcess(WfProcessMgr mgr, String workEffortId) {
        WfProcess process = null;
        try {
            Iterator i = mgr.getIteratorProcess();
            while ( i.hasNext() && process == null ) {
                WfProcess p = (WfProcess) i.next();
                if ( p.getRuntimeObject().getString("workEffortId").equals(workEffortId) )
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
    
    public static WfActivity getActivity(WfProcess process, String workEffortId) {
        WfActivity activity = null;
        try {
            Iterator i = process.getIteratorStep();
            while ( i.hasNext() && activity == null ) {
                WfActivity a = (WfActivity) i.next();
                if ( a.getRuntimeObject().getString("workEffortId").equals(workEffortId) )
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
    
}




