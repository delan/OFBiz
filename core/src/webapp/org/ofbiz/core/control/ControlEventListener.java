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
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    16 August 2002
 *@version    1.0
 */
public class ControlEventListener implements HttpSessionListener, HttpSessionActivationListener {
    //Debug module name
    public static final String module = ControlEventListener.class.getName();
    
    protected static long totalActiveSessions = 0;
    protected static long totalPassiveSessions = 0;
    
    protected GenericDelegator delegator = null;
    
    public ControlEventListener(GenericDelegator delegator) {
        this.delegator = delegator;
    }
    
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        if (session.getAttribute("delegator") == null) {
            session.setAttribute("delegator", delegator);
        }
        //get/create the visit
        //NOTE: don't create the visit here, just let the control servlet do it; GenericValue visit = VisitHandler.getVisit(session);
        
        totalActiveSessions++;
    }
    
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        GenericValue visit = VisitHandler.getVisit(session);
        if (visit != null) {
            visit.set("thruDate", new Timestamp(session.getLastAccessedTime()));
            try {
                visit.store();
            } catch (GenericEntityException e) {
                Debug.logError(e, "Could not update visit:", module);
            }
        }
        
        totalActiveSessions--;
    }
    
    public void sessionWillPassivate(HttpSessionEvent event) {
        totalActiveSessions--;
        totalPassiveSessions++;
    }
    
    public void sessionDidActivate(HttpSessionEvent event) {
        totalActiveSessions++;
        totalPassiveSessions--;
    }
}
