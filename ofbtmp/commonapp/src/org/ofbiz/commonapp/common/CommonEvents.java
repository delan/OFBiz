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
 * @since      2.1
 */
public class CommonEvents {
    
    public static final String module = CommonEvents.class.getName();
           
    public static UtilCache appletSessions = new UtilCache("AppletSessions", 0, 600000, true);            
    
    public static String checkAppletRequest(HttpServletRequest request, HttpServletResponse response) { 
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");                
        String sessionId = request.getParameter("sessionId");
        String visitId = request.getParameter("visitId");
        sessionId = sessionId.trim();
        visitId = visitId.trim();
        
        String responseString = "";
        
        GenericValue visit = null;
        try {
            visit = delegator.findByPrimaryKey("Visit", UtilMisc.toMap("visitId", visitId));                             
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot Visit Object", module);
        }
       
        if (visit != null && visit.getString("sessionId").equals(sessionId) && appletSessions.containsKey(sessionId)) {            
            Map sessionMap = (Map) appletSessions.get(sessionId);
            if (sessionMap != null && sessionMap.containsKey("followPage"))
                responseString = (String) sessionMap.remove("followPage");                                                             
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
        String sessionId = request.getParameter("sessionId");
        String visitId = request.getParameter("visitId");
        sessionId = sessionId.trim();
        visitId = visitId.trim();
                
        String responseString = "ERROR";
        
        GenericValue visit = null;
        try {
            visit = delegator.findByPrimaryKey("Visit", UtilMisc.toMap("visitId", visitId));                             
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot Visit Object", module);
        }        
        
        if (visit.getString("sessionId").equals(sessionId)) {                                                                             
            String currentPage = (String) request.getParameter("currentPage");
            if (appletSessions.containsKey(sessionId)) {              
                Map sessionMap = (Map) appletSessions.get(sessionId);
                String followers = (String) sessionMap.get("followers");
                List folList = StringUtil.split(followers, ",");
                Iterator i = folList.iterator();
                while (i.hasNext()) {
                    String follower = (String) i.next();
                    Map folSesMap = UtilMisc.toMap("followPage", currentPage);
                    appletSessions.put(follower, folSesMap);
                }
            }           
            responseString = "OK";
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
    
    public static String setAppletFollower(HttpServletRequest request, HttpServletResponse response) {
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String visitId = request.getParameter("visitId");
        if (visitId != null) request.setAttribute("visitId", visitId);
        if (security.hasPermission("SEND_CONTROL_APPLET", userLogin)) { 
            String followerSessionId = request.getParameter("followerSid");
            String followSessionId = request.getParameter("followSid");
            Map follow = (Map) appletSessions.get(followSessionId);
            if (follow == null) follow = new HashMap();
            String followerListStr = (String) follow.get("followers");
            if (followerListStr == null) {
                followerListStr = followerSessionId;
            } else {
                followerListStr = followerListStr + "," + followerSessionId;
            }
            appletSessions.put(followSessionId, follow);
            appletSessions.put(followerSessionId, null);            
        }
        return "success";                
    }

    public static String setFollowerPage(HttpServletRequest request, HttpServletResponse response) {
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String visitId = request.getParameter("visitId");
        if (visitId != null) request.setAttribute("visitId", visitId);
        if (security.hasPermission("SEND_CONTROL_APPLET", userLogin)) { 
            String followerSessionId = request.getParameter("followerSid");
            String pageUrl = request.getParameter("pageUrl");
            Map follow = (Map) appletSessions.get(followerSessionId);
            if (follow == null) follow = new HashMap();
            follow.put("followPage", pageUrl);            
            appletSessions.put(followerSessionId, follow);            
        }
        return "success";                
    }    
    
    
}
