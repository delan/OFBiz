/*
 * $Id: CoreEvents.java,v 1.6 2004/06/17 00:52:09 ajzeneski Exp $
 *
 * Copyright (c) 2001-2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.content.webapp.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.webapp.control.RequestHandler;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceDispatcher;
import org.ofbiz.service.WebAppDispatcher;
import org.ofbiz.service.calendar.RecurrenceRule;

/**
 * CoreEvents - WebApp Events Related To CORE components
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.6 $
 * @since      2.0
 */
public class CoreEvents {
    
    public static final String module = CoreEvents.class.getName();

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
     * Return null event. Used as a place holder for events.
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Response code string
     */
    public static String returnNull(HttpServletRequest request, HttpServletResponse response) {
        return null;
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
            request.setAttribute("_ERROR_MESSAGE_", "<li>You are not authorized to use this function.");
            return "error";
        }
        if (delegatorName == null) {
            request.setAttribute("_ERROR_MESSAGE_", "<li>Required parameter 'delegator' not passed.");
            return "error";
        }

        GenericDelegator delegator = GenericDelegator.getGenericDelegator(delegatorName);

        if (delegator == null) {
            request.setAttribute("_ERROR_MESSAGE_", "<li>No delegator defined by that name.");
            return "error";
        }

        // now change the dispatcher to use this delegator
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext dctx = dispatcher.getDispatchContext();
        String dispatcherName = dispatcher.getName();

        if (dispatcherName == null) {
            request.setAttribute("_ERROR_MESSAGE_", "<li>Dispatcher name is null.");
            return "error";
        }
        if (dctx == null) {
            request.setAttribute("_ERROR_MESSAGE_", "<li>Dispatch context is null.");
            return "error";
        }

        ServiceDispatcher sd = ServiceDispatcher.getInstance(dispatcherName, delegator);

        if (sd == null)
            dispatcher = new WebAppDispatcher(dctx, delegator);
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
            request.setAttribute("_ERROR_MESSAGE_", "<li>You are not authorized to use this function.");
            return "error";
        }
        if (dispatcherName == null) {
            request.setAttribute("_ERROR_MESSAGE_", "<li>Required parameter 'dispatcher' not passed.");
            return "error";
        }

        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        ServiceDispatcher sd = ServiceDispatcher.getInstance(dispatcherName, delegator);

        if (sd == null) {
            request.setAttribute("_ERROR_MESSAGE_", "<li>No dispatcher with that name has been registered.");
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
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Response code string
     */
    public static String scheduleService(HttpServletRequest request, HttpServletResponse response) {
        Security security = (Security) request.getAttribute("security");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        
        Map params = UtilHttp.getParameterMap(request);
        // get the schedule parameters
        String serviceName = (String) params.remove("SERVICE_NAME");
        String poolName = (String) params.remove("POOL_NAME");
        String serviceTime = (String) params.remove("SERVICE_TIME");
        String serviceEndTime = (String) params.remove("SERVICE_END_TIME");
        String serviceFreq = (String) params.remove("SERVICE_FREQUENCY");
        String serviceIntr = (String) params.remove("SERVICE_INTERVAL");
        String serviceCnt = (String) params.remove("SERVICE_COUNT");
        String retryCnt = (String) params.remove("SERVICE_MAXRETRY");

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
        long endTime = 0;
        int maxRetry = -1;
        int count = 1;
        int interval = 1;
        int frequency = RecurrenceRule.DAILY;

        StringBuffer errorBuf = new StringBuffer();

        // make sure we passed a service
        if (serviceName == null) {
            request.setAttribute("_ERROR_MESSAGE_", "<li>You must specify a 'SERVICE_NAME' (other parameters include: SERVICE_TIME, SERVICE_FREQUENCY, SERVICE_INTERVAL, SERVICE_COUNT).");
            return "error";
        }

        Timestamp ts = null;
        
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);

        // lookup the service definition to see if this service is externally available, if not require the SERVICE_INVOKE_ANY permission
        ModelService modelService = null;
        try {
            modelService = dispatcher.getDispatchContext().getModelService(serviceName);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Error looking up ModelService for serviceName [" + serviceName + "]", module);
            request.setAttribute("_ERROR_MESSAGE_", "<li>Error looking up ModelService for serviceName [" + serviceName + "]: " + e.toString());
            return "error";
        }
        if (modelService == null) {
            request.setAttribute("_ERROR_MESSAGE_", "<li>Could not find a service with the serviceName [" + serviceName + "]");
            return "error";
        }
        
        // make the context valid; using the makeValid method from ModelService
        Map serviceContext = new HashMap();
        Iterator ci = modelService.getInParamNames().iterator();
        while (ci.hasNext()) {
            String name = (String) ci.next();

            // don't include userLogin, that's taken care of below
            if ("userLogin".equals(name)) continue;
            // don't include locale, that is also taken care of below
            if ("locale".equals(name)) continue;
            
            Object value = request.getParameter(name);

            // if the parameter wasn't passed and no other value found, don't pass on the null
            if (value == null) {
                value = request.getAttribute(name);
            } 
            if (value == null) {
                value = request.getSession().getAttribute(name);
            }
            if (value == null) {
                // still null, give up for this one
                continue;
            }
            
            if (value instanceof String && ((String) value).length() == 0) {
                // interpreting empty fields as null values for each in back end handling...
                value = null;
            }

            // set even if null so that values will get nulled in the db later on
            serviceContext.put(name, value);
        }     
        serviceContext = modelService.makeValid(serviceContext, ModelService.IN_PARAM);
        
        if (userLogin != null) {
            serviceContext.put("userLogin", userLogin);                             
        }
        
        if (locale != null) {
            serviceContext.put("locale", locale);
        }
                
        if (!modelService.export && !security.hasPermission("SERVICE_INVOKE_ANY", request.getSession())) {
            request.setAttribute("_ERROR_MESSAGE_", "<li>You are not authorized to call this non-exported service, you must be logged in and have the SERVICE_INVOKE_ANY permission.");
            return "error";
        }
        
        // some conversions
        if (serviceTime != null && serviceTime.length() > 0) {            
            try {        
                Timestamp ts1 = Timestamp.valueOf(serviceTime);
                startTime = ts1.getTime();
            } catch (IllegalArgumentException e) {
                try {                        
                    startTime = Long.parseLong(serviceTime);                    
                } catch (NumberFormatException nfe) {
                    errorBuf.append("<li>Invalid format for SERVICE_TIME");
                }
            }
            if (startTime < (new Date()).getTime()) {
                errorBuf.append("<li>SERVICE_TIME has already passed");
            }
        }
        if (serviceEndTime != null && serviceEndTime.length() > 0) {            
            try {        
                Timestamp ts1 = Timestamp.valueOf(serviceEndTime);
                endTime = ts1.getTime();
            } catch (IllegalArgumentException e) {
                try {                        
                    endTime = Long.parseLong(serviceTime);
                } catch (NumberFormatException nfe) {
                    errorBuf.append("<li>Invalid format for SERVICE_TIME");
                }
            }
            if (endTime < (new Date()).getTime()) {
                errorBuf.append("<li>SERVICE_TIME has already passed");
            }
        }
        if (serviceIntr != null && serviceIntr.length() > 0) {
            try {
                interval = Integer.parseInt(serviceIntr);
            } catch (NumberFormatException nfe) {
                errorBuf.append("<li>Invalid format for SERVICE_INTERVAL");
            }
        }
        if (serviceCnt != null && serviceCnt.length() > 0) {
            try {
                count = Integer.parseInt(serviceCnt);
            } catch (NumberFormatException nfe) {
                errorBuf.append("<li>Invalid format for SERVICE_COUNT");
            }
        }
        if (serviceFreq != null && serviceFreq.length() > 0) {
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
                    frequency = ((Integer) freqMap.get(serviceFreq.toUpperCase())).intValue();
                }
            }
        }
        if (retryCnt != null && retryCnt.length() > 0) {
            int parsedValue = -2;

            try {
                parsedValue = Integer.parseInt(retryCnt);
            } catch (NumberFormatException e) {
                parsedValue = -2;
            }
            if (parsedValue > -2) {
                maxRetry = parsedValue;
            } else {
                maxRetry = modelService.maxRetry;
            }
        } else {
            maxRetry = modelService.maxRetry;
        }

        // return the errors
        if (errorBuf.length() > 0) {
            request.setAttribute("_ERROR_MESSAGE_", errorBuf.toString());
            return "error";
        }
                      
        // schedule service
        try {
            dispatcher.schedule(poolName, serviceName, serviceContext, startTime, frequency, interval, count, endTime, maxRetry);
        } catch (GenericServiceException e) {
            request.setAttribute("_ERROR_MESSAGE_", "<li>Service dispatcher threw an exception: " + e.getMessage());
            return "error";
        }

        request.setAttribute("_EVENT_MESSAGE_", "<li>Service has been scheduled");
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
    public static String runService(HttpServletRequest request, HttpServletResponse response) {
        // get the mode and service name 
        String serviceName = request.getParameter("serviceName");
        String mode = request.getParameter("mode");

        if (UtilValidate.isEmpty(serviceName)) {
            request.setAttribute("_ERROR_MESSAGE_", "<li>You must specify a 'serviceName', and optionally a 'mode' (sync or async, defaults to sync).");
            return "error";
        }

        if (UtilValidate.isEmpty(mode)) {
            mode = "sync";
        }
        
        // now do a security check
        
        Security security = (Security) request.getAttribute("security");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        
        //lookup the service definition to see if this service is externally available, if not require the SERVICE_INVOKE_ANY permission
        ModelService modelService = null;
        try {
            modelService = dispatcher.getDispatchContext().getModelService(serviceName);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Error looking up ModelService for serviceName [" + serviceName + "]", module);
            request.setAttribute("_ERROR_MESSAGE_", "<li>Error looking up ModelService for serviceName [" + serviceName + "]: " + e.toString());
            return "error";
        }
        if (modelService == null) {
            request.setAttribute("_ERROR_MESSAGE_", "<li>Could not find a service with the serviceName [" + serviceName + "]");
            return "error";
        }

        if (!modelService.export && !security.hasPermission("SERVICE_INVOKE_ANY", request.getSession())) {
            request.setAttribute("_ERROR_MESSAGE_", "<li>You are not authorized to call this non-exported service, you must be logged in and have the SERVICE_INVOKE_ANY permission.");
            return "error";
        }
        
        Debug.logInfo("Running service named [" + serviceName + "] from event with mode [" + mode + "]", module);
        
        // call the service via the ServiceEventHandler which 
        // adapts an event to a service.
        try {
            return seh.invoke(mode, serviceName, request, response);
        } catch (EventHandlerException e) {
            request.setAttribute("_ERROR_MESSAGE_", "<li>ServiceEventHandler threw an exception: " + e.getMessage());
            return "error";
        }
    }
    
    public static String streamFile(HttpServletRequest request, HttpServletResponse response) {
        RequestHandler rh = (RequestHandler) request.getAttribute("_REQUEST_HANDLER_");
        String filePath = RequestHandler.getNextPageUri(request.getPathInfo());
        String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
        
        // load the file
        File file = new File(filePath);
        if (file.exists()) {
            Long longLen = new Long(file.length());
            int length = longLen.intValue();
            try {
                FileInputStream fis = new FileInputStream(file);                                
                UtilHttp.streamContentToBrowser(response, fis, length, null);
                fis.close();                
            } catch (FileNotFoundException e) {
                Debug.logError(e, module);
                return "error";
            } catch (IOException e) {
                Debug.logError(e, module);
                return "error";
            }
        }                        
        return null;
    }
}
