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
package org.ofbiz.commonapp.common;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.util.*;

/**
 * Common Services
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0.1
 */
public class CommonEvents {
    
    public static final String module = CommonEvents.class.getName();
    
    // cache for applet sessions; 10min duration and uses soft references
    public static UtilCache appletSessions = new UtilCache("AppletSessions", 0, 600000, true);
    
    public static String checkAppletRequest(HttpServletRequest request, HttpServletResponse response) { 
        Debug.logVerbose("Running checkAppletRequest", module);       
        String sessionId = request.getParameter("sessionId");   
        Debug.logVerbose("Got session: " + sessionId, module);
        Debug.logVerbose("Checking session now", module);
        
        String responseString = "";      
        if (appletSessions.containsKey(sessionId)) {            
            responseString = (String) appletSessions.remove(sessionId);   
            Debug.logVerbose("AppletSessions: " + appletSessions.size(), module);
        } else {
            Debug.logVerbose("Session not found in cache.", module);      
        }
        
        try {
            PrintWriter out = response.getWriter();
            response.setContentType("text/plain");
            out.println(responseString);
            out.close();
        } catch (IOException e) {
            Debug.logError(e, "Problems writing servlet output!", module);
        }
                                                
        return "success";
    }   
    
    public static String receiveAppletRequest(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");
        String userLoginId = request.getParameter("userLoginId");
        String sessionId = request.getParameter("sessionId");
        
        List visits = null;
        GenericValue userLogin = null;
        try {
            visits = delegator.findByAnd("Visit", UtilMisc.toMap("userLoginId", userLoginId, "sessionId", sessionId));
            if (visits != null && visits.size() > 0)
                userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));            
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get UserLogin from visit session.", module);
        }
                      
        String responseString = "";
        if (security.hasPermission("SERVICE_INVOKE_ANY", userLogin)) {                        
            String followers = (String) request.getParameter("followers");
            String currentPage = (String) request.getParameter("currentPage");
        
            List sessionList = StringUtil.split(followers, ",");
            Iterator si = sessionList.iterator();
            while (si.hasNext()) {                
                String toSession = (String) si.next();
                Debug.logVerbose("Adding session: " + toSession, module);
                appletSessions.put(toSession, currentPage);     
                Debug.logVerbose("AppletSessions: " + appletSessions.size(), module); 
            }
            responseString = "OK";
        } else {
            responseString = "ERROR";
        }
                
        try {
            PrintWriter out = response.getWriter();
            response.setContentType("text/plain");
            out.println(responseString);
            out.close();
        } catch (IOException e) {
            Debug.logError(e, "Problems writing servlet output!", module);
        }
                
        return "success";         
    }
    
}
