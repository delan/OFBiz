/*
 * $Id$
 */

package org.ofbiz.core.workflow;

import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Workflow Services
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
public class WorkflowServices {
    
    // -------------------------------------------------------------------
    // Client 'Service' Methods
    // -------------------------------------------------------------------
    
    /** Marks an activity as complete */
    public static Map completeActivity(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        String workEffortId = (String) context.get("workEffortId");
        Map actResults = (Map) context.get("results");
        
        try {
            WfActivity activity = WfFactory.getWfActivity(delegator, workEffortId);
            if ( actResults != null && actResults.size() > 0 )
                activity.setResult(actResults);
            activity.complete();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }
        catch ( WfException e ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,e.getMessage());
        }
        return result;
    }
    
    /** Change the state of an activity */
    public static Map changeActivityState(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        String workEffortId = (String) context.get("workEffortId");
        String newState = (String) context.get("newState");
        try {
            WfActivity activity = WfFactory.getWfActivity(delegator,workEffortId);
            activity.changeState(newState);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }
        catch ( WfException e ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,e.getMessage());
        }
        return result;
    }
    
    /** Check the state of an activity */
    public static Map checkActivityState(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        String workEffortId = (String) context.get("workEffortId");
        try {
            WfActivity activity = WfFactory.getWfActivity(delegator,workEffortId);
            result.put("activityState",activity.state());
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }
        catch ( WfException e ) {
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
            WfActivity activity = WfFactory.getWfActivity(delegator,workEffortId);
            activity.activate();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }
        catch ( WfException e ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,e.getMessage());
        }
        return result;
    }
    
    /** Assign activity to a new or additional party */
    public static Map assignActivity(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        String workEffortId = (String) context.get("workEffortId");
        String partyId = (String) context.get("partyId");
        String roleType = (String) context.get("roleTypeId");
        boolean removeOldAssign = false;
        if ( context.containsKey("removeOldAssignments") ) {
            removeOldAssign = ((String)context.get("removeOldAssignments")).equals("true") ? true : false;
        }
        try {
            WfActivity activity = WfFactory.getWfActivity(delegator,workEffortId);
            WfResource resource = WfFactory.getWfResource(delegator,null,null,partyId,roleType);
            activity.assign(resource,removeOldAssign ? false : true);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }
        catch ( WfException e ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,e.getMessage());
        }
        return result;
    }
    
    /** Accept an assignment and attempt to start the activity */
    public static Map acceptAssignment(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        
        String workEffortId = (String) context.get("workEffortId");
        String partyId = (String) context.get("partyId");
        String roleType = (String) context.get("roleTypeId");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        
        try {
            WfAssignment assign = WfFactory.getWfAssignment(delegator,workEffortId,partyId,roleType,fromDate);
            assign.accept();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }
        catch ( WfException we ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,we.getMessage());
        }
        return result;
        
    }
    
    /** Complete an assignment */
    public static Map completeAssignment(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        
        String workEffortId = (String) context.get("workEffortId");
        String partyId = (String) context.get("partyId");
        String roleType = (String) context.get("roleTypeId");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Map actResults = (Map) context.get("results");
        
        try {
            WfAssignment assign = WfFactory.getWfAssignment(delegator,workEffortId,partyId,roleType,fromDate);
            if ( actResults != null && actResults.size() > 0 )
                assign.setResult(actResults);
            assign.complete();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }
        catch ( WfException we ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,we.getMessage());
        }
        return result;
    }
    
    public static Map limitInvoker(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
                
        String workEffortId = (String) context.get("workEffortId");
        String limitService = (String) context.get("limitService");
        Map limitContext = (Map) context.get("limitContext");
                
        try {
            WfActivity activity = WfFactory.getWfActivity(delegator,workEffortId);
            if ( activity.state().startsWith("open") ) 
                dispatcher.runSync(limitService,limitContext);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }
        catch ( WfException we ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,we.getMessage());
        }
        catch ( GenericServiceException se ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,se.getMessage());
        }       
                        
        return result;
    }
    
     
    // -------------------------------------------------------------------
    // Client 'Worker' Methods
    // -------------------------------------------------------------------
    
}




