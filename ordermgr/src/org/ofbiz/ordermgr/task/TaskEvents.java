/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.ordermgr.task;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.control.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.event.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * Order Processing Task Events
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class TaskEvents {
    
    public static final String module = TaskEvents.class.getName();
    
    /** Complete assignment event */
    public static String completeAssignment(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        
        Map parameterMap = UtilMisc.getParameterMap(request);
        String workEffortId = (String) parameterMap.remove("workEffortId");
        String partyId = (String) parameterMap.remove("partyId");
        String roleTypeId = (String) parameterMap.remove("roleTypeId");
        String fromDateStr = (String) parameterMap.remove("fromDate");
        java.sql.Timestamp fromDate = null;
        try {       
            fromDate = (java.sql.Timestamp) ObjectType.simpleTypeConvert(fromDateStr, "java.sql.Timestamp", null, null);
        } catch (GeneralException e) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Invalid date format for fromDate");
            return "error";        
        }
        
        Map result = null;
        try {
            Map context = UtilMisc.toMap("workEffortId", workEffortId, "partyId", partyId, "roleTypeId", roleTypeId, 
                    "fromDate", fromDate, "result", parameterMap, "userLogin", userLogin);
            result = dispatcher.runSync("wfCompleteAssignment", context);
            if (result.containsKey(ModelService.RESPOND_ERROR)) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, (String) result.get(ModelService.ERROR_MESSAGE));
                return "error";
            }
        } catch (GenericServiceException e) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Problems invoking the complete assignment service");
            return "error";
        }

        return "success";              
    }    
    
    /** Accept role assignment event */
    public static String acceptRoleAssignment(HttpServletRequest request, HttpServletResponse response) { 
        ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
        RequestHandler rh = (RequestHandler) ctx.getAttribute(SiteDefs.REQUEST_HANDLER);
        
        if (addToOrderRole(request)) {
            try {
                EventHandler eh = EventFactory.getEventHandler(rh, "service");
                eh.invoke("", "wfAcceptRoleAssignment", request, response); 
            } catch (EventHandlerException e) {
                Debug.logError(e, "Invocation error", module);
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Failed to invoke the wfAcceptRoleAssignment service.");
                return "error";
            } 
            return "success";                         
        }                    
        return "error";
    }
    
    /** Delegate and accept assignment event */
    public static String delegateAndAcceptAssignment(HttpServletRequest request, HttpServletResponse response) {        
        ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
        RequestHandler rh = (RequestHandler) ctx.getAttribute(SiteDefs.REQUEST_HANDLER);
        
        if (addToOrderRole(request)) {
            try {
                EventHandler eh = EventFactory.getEventHandler(rh, "service");
                eh.invoke("", "wfDelegateAndAcceptAssignmet", request, response); 
            } catch (EventHandlerException e) {
                Debug.logError(e, "Invocation error", module);
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Failed to invoke the wfDelegateAndAcceptAssignmet service.");
                return "error";
            }    
            return "success";                       
        }            
        return "error";
    }
        
    private static boolean addToOrderRole(HttpServletRequest request) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String partyId = request.getParameter("partyId");
        String roleTypeId = request.getParameter("roleTypeId");
        String orderId = request.getParameter("order_id");
        Map context = UtilMisc.toMap("orderId", orderId, "partyId", partyId, "roleTypeId", roleTypeId);
        Map result = null;
        try {
            result = dispatcher.runSync("addOrderRole", context);  
            Debug.logInfo("Added user to order role " + result, module);          
        } catch (GenericServiceException gse) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, gse.getMessage());
            return false;
        }
        return true;
    }

}
