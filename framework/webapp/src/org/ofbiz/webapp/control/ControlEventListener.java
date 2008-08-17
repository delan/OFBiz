/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.webapp.control;

import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.serialize.XmlSerializer;
import org.ofbiz.entity.transaction.TransactionUtil;

/**
 * HttpSessionListener that gathers and tracks various information and statistics
 */
public class ControlEventListener implements HttpSessionListener {
    // Debug module name
    public static final String module = ControlEventListener.class.getName();

    protected static long totalActiveSessions = 0;
    protected static long totalPassiveSessions = 0;

    public ControlEventListener() {}

    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();

        // get/create the visit
        // NOTE: don't create the visit here, just let the control servlet do it; GenericValue visit = VisitHandler.getVisit(session);

        countCreateSession();

        // property setting flag for logging stats
        if (System.getProperty("org.ofbiz.log.session.stats") != null) {
            session.setAttribute("org.ofbiz.log.session.stats", "Y");
        }

        Debug.logInfo("Creating session: " + session.getId(), module);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        
        // Finalize the Visit
        boolean beganTransaction = false;
        try {
            beganTransaction = TransactionUtil.begin();
        
            // instead of using this message, get directly from session attribute so it won't create a new one: GenericValue visit = VisitHandler.getVisit(session);
            GenericValue visit = (GenericValue) session.getAttribute("visit");
            if (visit != null) {
                visit.set("thruDate", new Timestamp(session.getLastAccessedTime()));
                visit.store();
            } else {
                Debug.logWarning("Could not find visit value object in session [" + session.getId() + "] that is being destroyed", module);
            }

            // Store the UserLoginSession
            String userLoginSessionString = getUserLoginSession(session);
            GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
            if (userLogin != null && userLoginSessionString != null) {
                GenericValue userLoginSession = null;
                userLoginSession = userLogin.getRelatedOne("UserLoginSession");

                if (userLoginSession == null) {
                    userLoginSession = userLogin.getDelegator().makeValue("UserLoginSession", 
                            UtilMisc.toMap("userLoginId", userLogin.getString("userLoginId")));
                    userLogin.getDelegator().create(userLoginSession);
                }
                userLoginSession.set("savedDate", UtilDateTime.nowTimestamp());
                userLoginSession.set("sessionData", userLoginSessionString);
                userLoginSession.store();
            }

            countDestroySession();
            Debug.logInfo("Destroying session: " + session.getId(), module);
            this.logStats(session, visit);
        } catch (GenericEntityException e) {
            try {
                // only rollback the transaction if we started one...
                TransactionUtil.rollback(beganTransaction, "Error saving information about closed HttpSession", e);
            } catch (GenericEntityException e2) {
                Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
            }

            Debug.logError(e, "Error in session destuction information persistence", module);
        } finally {
            // only commit the transaction if we started one... this will throw an exception if it fails
            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Could not commit transaction for update visit for session destuction", module);
            }
        }
    }

    public void logStats(HttpSession session, GenericValue visit) {
        if (Debug.verboseOn() || session.getAttribute("org.ofbiz.log.session.stats") != null) {
            Debug.log("<===================================================================>", module);
            Debug.log("Session ID     : " + session.getId(), module);
            Debug.log("Created Time   : " + session.getCreationTime(), module);
            Debug.log("Last Access    : " + session.getLastAccessedTime(), module);
            Debug.log("Max Inactive   : " + session.getMaxInactiveInterval(), module);
            Debug.log("--------------------------------------------------------------------", module);
            Debug.log("Total Sessions : " + ControlEventListener.getTotalActiveSessions(), module);
            Debug.log("Total Active   : " + ControlEventListener.getTotalActiveSessions(), module);
            Debug.log("Total Passive  : " + ControlEventListener.getTotalPassiveSessions(),  module);
            Debug.log("** note : this session has been counted as destroyed.", module);
            Debug.log("--------------------------------------------------------------------", module);
            Debug.log("Visit ID       : " + visit.getString("visitId"), module);
            Debug.log("Party ID       : " + visit.getString("partyId"), module);
            Debug.log("Client IP      : " + visit.getString("clientIpAddress"), module);
            Debug.log("Client Host    : " + visit.getString("clientHostName"), module);
            Debug.log("Client User    : " + visit.getString("clientUser"), module);
            Debug.log("WebApp         : " + visit.getString("webappName"), module);
            Debug.log("Locale         : " + visit.getString("initialLocale"), module);
            Debug.log("UserAgent      : " + visit.getString("initialUserAgent"), module);
            Debug.log("Referrer       : " + visit.getString("initialReferrer"), module);
            Debug.log("Initial Req    : " + visit.getString("initialRequest"), module);
            Debug.log("Visit From     : " + visit.getString("fromDate"), module);
            Debug.log("Visit Thru     : " + visit.getString("thruDate"), module);
            Debug.log("--------------------------------------------------------------------", module);
            Debug.log("--- Start Session Attributes: ---", module);
            Enumeration sesNames = null;
            try {
                sesNames = session.getAttributeNames();
            } catch (IllegalStateException e) {
                Debug.log("Cannot get session attributes : " + e.getMessage(), module);
            }
            while (sesNames != null && sesNames.hasMoreElements()) {
                String attName = (String) sesNames.nextElement();
                Debug.log(attName + ":" + session.getAttribute(attName), module);
            }
            Debug.log("--- End Session Attributes ---", module);
            Debug.log("<===================================================================>", module);
        }
    }

    public static long getTotalActiveSessions() {
        return totalActiveSessions;
    }

    public static long getTotalPassiveSessions() {
        return totalPassiveSessions;
    }

    public static long getTotalSessions() {
        return totalActiveSessions + totalPassiveSessions;
    }

    public static void countCreateSession() {
        totalActiveSessions++;
    }

    public static void countDestroySession() {
        totalActiveSessions--;
    }

    public static void countPassivateSession() {
        totalActiveSessions--;
        totalPassiveSessions++;
    }

    public static void countActivateSession() {
        totalActiveSessions++;
        totalPassiveSessions--;
    }

    private String getUserLoginSession(HttpSession session) {
        Map userLoginSession = (Map) session.getAttribute("userLoginSession");

        String sessionData = null;
        if (UtilValidate.isNotEmpty(userLoginSession)) {
            try {
                sessionData = XmlSerializer.serialize(userLoginSession);
            } catch (Exception e) {
                Debug.logWarning(e, "Problems serializing UserLoginSession", module);
            }
        }
        return sessionData;
    }
}
