/*
 * $Id$
 *
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
import org.ofbiz.core.service.job.*;
import org.ofbiz.core.util.*;

/**
 * CoreEvents - WebApp Events Related To CORE components
 *
 *@author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 *@created    January 8, 2002
 *@version    1.0
 */

public class CoreEvents {

    /**
     * Return success event. Used as a place holder for events.
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Response code string
     */
    public static String returnSuccess(HttpServletRequest request, HttpServletResponse response) {
        return "success";
    }

    /**
     * Return error event. Used as a place holder for events.
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Response code string
     */
    public static String returnError(HttpServletRequest request, HttpServletResponse response) {
        return "error";
    }

    /**
     * Change delegator event. Changes the delegator for the current session
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
            dispatcher = new LocalDispatcher(dctx, delegator);
        else
            dispatcher = sd.getLocalContext(dispatcherName).getDispatcher();

        request.getSession().setAttribute("delegator", delegator);
        request.getSession().setAttribute("dispatcher", dispatcher);

        return "success";
    }

    /**
     * Change dispatcher event. Changes the dispatch for the current session
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

    /**
     * Schedule a service for a specific time or recurrence
     *  Request Parameters which are used for this service:
     *
     *  SERVICE_NAME      - Name of the service to invoke
     *  SERVICE_TIME      - First time the service will occur
     *  SERVICE_FREQUENCY - The type of recurrence (SECONDLY,MINUTELY,DAILY,etc)
     *  SERVICE_INTERVAL  - The interval of the frequency (every 5 minutes, etc)
     *  SERVICE_COUNT     - The number of time the service should run
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Response code string
     */
    public static String scheduleService(HttpServletRequest request, HttpServletResponse response) {
        // first do a security check
        Security security = (Security) request.getAttribute("security");
        if (!security.hasPermission("ENTITY_MAINT", request.getSession())) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>You are not authorized to use this function.");
            return "error";
        }

        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map params = UtilMisc.getParameterMap(request);
        // get the schedule parameters
        String serviceName = (String) params.remove("SERVICE_NAME");
        String serviceTime = (String) params.remove("SERVICE_TIME");
        String serviceFreq = (String) params.remove("SERVICE_FREQUENCY");
        String serviceIntr = (String) params.remove("SERVICE_INTERVAL");
        String serviceCnt  = (String) params.remove("SERVICE_COUNT");
        // the rest is the service context
        Map context = new HashMap(params);

        // the frequency map
        Map freqMap = new HashMap();
        freqMap.put("SECONDLY", new Integer(1));
        freqMap.put("MINUTELY", new Integer(2));
        freqMap.put("HOURLY", new Integer(3));
        freqMap.put("DAILY", new Integer(4));
        freqMap.put("WEEKLY", new Integer(5));
        freqMap.put("MONTHLY", new Integer(6));
        freqMap.put("YEARLY", new Integer(7));

        // some defaults
        long startTime = (new Date()).getTime();
        int count = 1;
        int interval = 1;
        int frequency = RecurrenceRule.DAILY;

        StringBuffer errorBuf = new StringBuffer();

        // make sure we passed a service
        if (serviceName == null) {
            errorBuf.append("<li>No service name was specified");
        }

        // some conversions
        if (serviceTime != null) {
            try {
                startTime = Long.parseLong(serviceTime);
            }
            catch (NumberFormatException nfe) {
                errorBuf.append("<li>Invalid format for SERVICE_TIME");
            }
            if (startTime < (new Date()).getTime()) {
                errorBuf.append("<li>SERVICE_TIME has already passed");
            }
        }
        if (serviceIntr != null) {
            try {
                interval = Integer.parseInt(serviceIntr);
            } catch (NumberFormatException nfe) {
                errorBuf.append("<li>Invalid format for SERVICE_INTERVAL");
            }
        }
        if (serviceCnt != null) {
            try {
                count = Integer.parseInt(serviceCnt);
            } catch (NumberFormatException nfe) {
                errorBuf.append("<li>Invalid format for SERVICE_COUNT");
            }
        }
        if (serviceFreq != null) {
            int parsedValue = 0;
            try {
                parsedValue = Integer.parseInt(serviceFreq);
                if (parsedValue > 0 && parsedValue < 8)
                    frequency = parsedValue;
            } catch (NumberFormatException nfe) {
                parsedValue = 0;
            }
            if (parsedValue == 0) {
                if (!freqMap.containsKey(serviceFreq.toUpperCase())) {
                    errorBuf.append("<li>Invalid format for SERIVCE_FREQUENCY");
                } else {
                    frequency = ((Integer)freqMap.get(serviceFreq.toUpperCase())).intValue();
                }
            }
        }

        // return the errors
        if (errorBuf.length() > 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errorBuf.toString());
            return "error";
        }

        // schedule service
        try {
            dispatcher.schedule(serviceName, context, startTime, frequency, interval, count);
        } catch (GenericServiceException e) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Service dispatcher threw an exception: " + e.getMessage());
            return "error";
        }

        request.setAttribute(SiteDefs.EVENT_MESSAGE, "<li>Service has been scheduled");
        return "success";
    }

    public static ServiceEventHandler seh = new ServiceEventHandler();
    /**
     * Run a service.
     *  Request Parameters which are used for this event:
     *  SERVICE_NAME      - Name of the service to invoke
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Response code string
     */
    public static String runService(HttpServletRequest request, HttpServletResponse response)  {
        // first do a security check
        Security security = (Security) request.getAttribute("security");
        if (!security.hasPermission("SERVICE_INVOKE_ANY", request.getSession())) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>You are not authorized to use this function, you must have the SERVICE_INVOKE_ANY permission.");
            return "error";
        }

        // get the mode and service name 
        String serviceName = request.getParameter("SERVICE_NAME");
        String mode = request.getParameter("SERVICE_MODE");

        // call the service via the ServiceEventHandler which 
        // adapts an event to a service.
        try {
            return seh.invoke(mode, serviceName, request, response);
        } catch (EventHandlerException e) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>ServiceEventHandler threw an exception: " + e.getMessage());
            return "error";
        }
    }
}


