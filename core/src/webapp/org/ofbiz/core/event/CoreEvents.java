/*
 * $Id$
 *
 * Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.event;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.calendar.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.service.scheduler.*;
import org.ofbiz.core.util.*;

/**
 * CoreEvents - WebApp Events Related To CORE components
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    January 8, 2002
 *@version    1.0
 */

public class CoreEvents {

    /** Change delegator event. Changes the delegator for the current session
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Response code string
     */
    public static String changeDelegator(HttpServletRequest request, HttpServletResponse response) {
        String delegatorName = request.getParameter("delegator");
        Security security = (Security) request.getAttribute("security");
        if (!security.hasPermission("ENTITY_MAINT", request.getSession())) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>You are not authorized to use this function.");
            return "error";
        }
        if (delegatorName == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Required parameter 'delegator' not passed.");
            return "error";
        }

        GenericDelegator delegator = GenericDelegator.getGenericDelegator(delegatorName);
        if (delegator == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>No delegator defined by that name.");
            return "error";
        }

        // now change the dispatcher to use this delegator
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext dctx = dispatcher.getDispatchContext();
        String dispatcherName = dispatcher.getName();

        if (dispatcherName == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Dispatcher name is null.");
            return "error";
        }
        if (dctx == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Dispatch context is null.");
            return "error";
        }

        ServiceDispatcher sd = ServiceDispatcher.getInstance(dispatcherName, delegator);

        if (sd == null)
            dispatcher = new LocalDispatcher(dispatcherName, delegator, dctx);
        else
            dispatcher = sd.getLocalContext(dispatcherName).getDispatcher();

        request.getSession().setAttribute("delegator", delegator);
        request.getSession().setAttribute("dispatcher", dispatcher);

        return "success";
    }

    /** Change dispatcher event. Changes the dispatch for the current session
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Response code string
     */
    public static String changeDispatcher(HttpServletRequest request, HttpServletResponse response) {
        String dispatcherName = request.getParameter("dispatcher");
        Security security = (Security) request.getAttribute("security");
        if (!security.hasPermission("ENTITY_MAINT", request.getSession())) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>You are not authorized to use this function.");
            return "error";
        }
        if (dispatcherName == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Required parameter 'dispatcher' not passed.");
            return "error";
        }

        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        ServiceDispatcher sd = ServiceDispatcher.getInstance(dispatcherName, delegator);
        if (sd == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>No dispatcher with that name has been registered.");
            return "error";
        }
        LocalDispatcher dispatcher = sd.getLocalContext(dispatcherName).getDispatcher();
        request.getSession().setAttribute("dispatcher", dispatcher);
        return "success";
    }

    /** Schedule a service for a specific time or recurrence
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Response code string
     */
    public static String scheduleService(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map params = UtilMisc.getParameterMap(request);
        // get the schedule parameters
        String serviceName = (String) params.remove("SERVICE_NAME");
        String serviceTime = (String) params.remove("SERVICE_TIME");
        String serviceFreq = (String) params.remove("SERVICE_FREQ");
        String serviceIntr = (String) params.remove("SERVICE_INTERVAL");
        String serviceCnt  = (String) params.remove("SERVICE_COUNT");
        // the rest is the service context
        Map context = new HashMap(params);

        // some defaults
        long startTime = (new Date()).getTime();
        int count = 1;
        int interval = 1;
        int frequency = RecurrenceRule.DAILY;

        // make sure we passed a service
        if (serviceName == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>No service name was specified");
            return "error";
        }

        // some conversions
        if (serviceTime != null) {
        }

        // schedule service

        return "success";
    }

}


