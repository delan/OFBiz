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
package org.ofbiz.core.control;

import java.net.*;
import java.sql.*;
import java.util.*;
import javax.servlet.http.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.stats.*;

/**
 * HttpSessionListener that gathers and tracks various information and statistics
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
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

        Debug.logInfo("Creating session: " + event.getSession().toString(), module);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        GenericValue visit = VisitHandler.getVisit(session);

        if (visit != null) {
            visit.set("thruDate", new Timestamp(session.getLastAccessedTime()));
            try {
                visit.store();
            } catch (GenericEntityException e) {
                Debug.logError(e, "Could not update visit for session destuction: " + visit, module);
            }
        }

        countDestroySession();

        Debug.logInfo("Destroying session: " + event.getSession().toString(), module);
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
}
