/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.stats;

import java.net.*;
import java.sql.*;
import java.util.*;
import javax.servlet.http.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.util.*;


/**
 * Handles saving and maintaining visit information
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    16 August 2002
 *@version    1.0
 */
public class VisitHandler {
    //Debug module name
    public static final String module = VisitHandler.class.getName();
    
    public static void setInitials(HttpServletRequest request, HttpSession session, String initialLocale, String initialRequest, String initialReferrer, String initialUserAgent, String webappName) {
        GenericValue visit = getVisit(session);
        if (visit != null) {
            visit.set("initialLocale", initialLocale);
            if (initialRequest != null) visit.set("initialRequest", initialRequest.length() > 250 ? initialRequest.substring(0, 250) : initialRequest);
            if (initialReferrer != null) visit.set("initialReferrer", initialReferrer.length() > 250 ? initialReferrer.substring(0, 250) : initialReferrer);
            if (initialUserAgent != null) visit.set("initialUserAgent", initialUserAgent.length() > 250 ? initialUserAgent.substring(0, 250) : initialUserAgent);
            visit.set("webappName", webappName);
            visit.set("clientIpAddress", request.getRemoteAddr());
            visit.set("clientHostName", request.getRemoteHost());
            visit.set("clientUser", request.getRemoteUser());
            
            try {
                visit.store();
            } catch (GenericEntityException e) {
                Debug.logError(e, "Could not update visit:", module);
            }
        }
    }
    
    public static void setUserLogin(HttpSession session, GenericValue userLogin, boolean userCreated) {
        if (userLogin == null) return;
        GenericValue visit = getVisit(session);
        if (visit != null) {
            visit.set("userLoginId", userLogin.get("userLoginId"));
            visit.set("partyId", userLogin.get("partyId"));
            visit.set("userCreated", new Boolean(userCreated));
            
            try {
                visit.store();
            } catch (GenericEntityException e) {
                Debug.logError(e, "Could not update visit:", module);
            }
        }
    }
    
    public static String getVisitId(HttpSession session) {
        GenericValue visit = getVisit(session);
        if (visit != null) {
            return visit.getString("visitId");
        } else {
            return null;
        }
    }
    
    /** Get the visit from the session, or create if missing */
    public static GenericValue getVisit(HttpSession session) {
        //this defaults to true: ie if anything but "false" it will be true
        if (!UtilProperties.propertyValueEqualsIgnoreCase("serverstats", "stats.persist.visit", "false")) {
            GenericValue visit = (GenericValue) session.getAttribute("visit");
            if (visit == null) {
                GenericDelegator delegator = null;
                String delegatorName = (String) session.getAttribute("delegatorName");
                if (UtilValidate.isNotEmpty(delegatorName)) {
                    delegator = GenericDelegator.getGenericDelegator(delegatorName);
                }
                if (delegator == null) {
                    Debug.logError("Could not find delegator with delegatorName in session, not creating Visit entity", module);
                } else {
                    visit = delegator.makeValue("Visit", null);
                    Long nextId = delegator.getNextSeqId("Visit");
                    if (nextId == null) {
                        Debug.logError("Not persisting visit, could not get next seq id", module);
                        visit = null;
                    } else {
                        visit.set("visitId", nextId.toString());
                        visit.set("sessionId", session.getId());
                        visit.set("fromDate", new Timestamp(session.getCreationTime()));
                        //get localhost ip address and hostname to store
                        try {
                            InetAddress address = InetAddress.getLocalHost();
                            if (address != null) {
                                visit.set("serverIpAddress", address.getHostAddress());
                                visit.set("serverHostName", address.getHostName());
                            } else {
                                Debug.logError("Unable to get localhost internet address, was null", module);
                            }
                        } catch (java.net.UnknownHostException e) {
                            Debug.logError("Unable to get localhost internet address: " + e.toString(), module);
                        }
                        try {
                            visit.create();
                            session.setAttribute("visit", visit);
                        } catch (GenericEntityException e) {
                            Debug.logError(e, "Could not create new visit:", module);
                            visit = null;
                        }
                    }
                }
            }
            if (visit == null) {
                Debug.logWarning("Could not find or create the visit...");
            }
            return visit;
        } else {
            return null;
        }
    }
}
