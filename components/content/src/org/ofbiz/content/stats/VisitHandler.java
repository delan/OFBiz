/*
 * $Id: VisitHandler.java,v 1.3 2004/02/11 06:04:20 jonesde Exp $
 *
 *  Copyright (c) 2001-2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.content.stats;

import java.net.InetAddress;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * Handles saving and maintaining visit information
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.3 $
 * @since      2.0
 */
public class VisitHandler {
    // Debug module name
    public static final String module = VisitHandler.class.getName();

    // this is not an event because it is required to run; as an event it could be disabled.
    public static void setInitialVisit(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String webappName = UtilHttp.getApplicationName(request);
        StringBuffer fullRequestUrl = UtilHttp.getFullRequestUrl(request);
        String initialLocale = request.getLocale() != null ? request.getLocale().toString() : "";
        String initialRequest = fullRequestUrl.toString();
        String initialReferrer = request.getHeader("Referer") != null ? request.getHeader("Referer") : "";
        String initialUserAgent = request.getHeader("User-Agent") != null ? request.getHeader("User-Agent") : "";

        session.setAttribute("_CLIENT_LOCALE_", request.getLocale());
        session.setAttribute("_CLIENT_REQUEST_", initialRequest);
        session.setAttribute("_CLIENT_USER_AGENT_", initialUserAgent);
        session.setAttribute("_CLIENT_REFERER_", initialUserAgent);
        VisitHandler.setInitials(request, session, initialLocale, initialRequest, initialReferrer, initialUserAgent, webappName);
    }

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
        // this defaults to true: ie if anything but "false" it will be true
        if (!UtilProperties.propertyValueEqualsIgnoreCase("serverstats", "stats.persist.visit", "false")) {
            GenericValue visit = (GenericValue) session.getAttribute("visit");

            if (visit == null) {
                GenericDelegator delegator = null;
                String delegatorName = (String) session.getAttribute("delegatorName");

                if (UtilValidate.isNotEmpty(delegatorName)) {
                    delegator = GenericDelegator.getGenericDelegator(delegatorName);
                }
                if (delegator == null) {
                    Debug.logError("Could not find delegator with delegatorName [" + delegatorName + "] in session, not creating Visit entity", module);
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
                        // get localhost ip address and hostname to store
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
                Debug.logWarning("Could not find or create the visit...", module);
            }
            return visit;
        } else {
            return null;
        }
    }
}
