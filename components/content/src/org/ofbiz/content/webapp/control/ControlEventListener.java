/*
 * $Id: ControlEventListener.java,v 1.2 2003/09/14 05:36:47 jonesde Exp $
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
package org.ofbiz.content.webapp.control;

import java.sql.Timestamp;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.content.stats.VisitHandler;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.serialize.XmlSerializer;

/**
 * HttpSessionListener that gathers and tracks various information and statistics
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.2 $
 * @since      2.0
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

        Debug.logInfo("Creating session: " + session.getId(), module);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        
        // Finalize the Visit
        GenericValue visit = VisitHandler.getVisit(session);
        if (visit != null) {
            visit.set("thruDate", new Timestamp(session.getLastAccessedTime()));
            try {
                visit.store();
            } catch (GenericEntityException e) {
                Debug.logError(e, "Could not update visit for session destuction: " + visit, module);
            }
        }

        // Store the UserLoginSession
        String userLoginSessionString = getUserLoginSession(session);
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        if (userLogin != null && userLoginSessionString != null) {
            GenericValue userLoginSession = null;
            try {
                userLoginSession = userLogin.getRelatedOne("UserLoginSession");

                if (userLoginSession == null) {
                    userLoginSession = userLogin.getDelegator().makeValue("UserLoginSession", 
                            UtilMisc.toMap("userLoginId", userLogin.getString("userLoginId")));
                    userLogin.getDelegator().create(userLoginSession);
                }
                userLoginSession.set("savedDate", UtilDateTime.nowTimestamp());
                userLoginSession.set("sessionData", userLoginSessionString);
                userLoginSession.store();
            } catch (GenericEntityException e) {}
        }

        countDestroySession();

        Debug.logInfo("Destroying session: " + session.getId(), module);
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
        if (userLoginSession != null && userLoginSession.size() > 0) {
            try {
                sessionData = XmlSerializer.serialize(userLoginSession);
            } catch (Exception e) {
                Debug.logWarning(e, "Problems serializing UserLoginSession", module);
            }
        }
        return sessionData;
    }
}
