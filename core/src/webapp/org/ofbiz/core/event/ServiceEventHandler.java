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
package org.ofbiz.core.event;

import java.util.*;
import javax.servlet.http.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * ServiceEventHandler - Service Event Handler
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class ServiceEventHandler implements EventHandler {

    public static final String module = ServiceEventHandler.class.getName();

    public static final String SYNC = "sync";
    public static final String ASYNC = "async";

    /** 
     * Invoke the web event
     *@param eventPath The mode of service invokation
     *@param eventMethod The service to invoke
     *@param request The servlet request object
     *@param response The servlet response object
     *@return String Result code
     *@throws EventHandlerException
     */
    public String invoke(String eventPath, String eventMethod, HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
        String mode = SYNC;
        String serviceName = null;

        if (eventPath == null || eventPath.length() == 0) {
            mode = SYNC;
        } else {
            mode = eventPath;
        }
        serviceName = eventMethod;
        if (Debug.verboseOn()) Debug.logVerbose("[Set mode/service]: " + mode + "/" + serviceName, module);

        HttpSession session = request.getSession();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        if (dispatcher == null) {
            throw new EventHandlerException("The local service dispatcher is null");
        }

        GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        Locale locale = UtilMisc.getLocale(request);
        
        DispatchContext dctx = dispatcher.getDispatchContext();
                
        if (dctx == null) {
            throw new EventHandlerException("Dispatch context cannot be found");
        }

        if (serviceName == null) {
            throw new EventHandlerException("Service name (eventMethod) cannot be null");
        }

        // get the service model to generate context
        ModelService model = null;

        try {
            model = dctx.getModelService(serviceName);
        } catch (GenericServiceException e) {
            throw new EventHandlerException("Problems getting the service model", e);
        }

        if (model == null) {
            throw new EventHandlerException("Problems getting the service model");
        }

        if (Debug.verboseOn()) Debug.logVerbose("[Processing]: SERVICE Event", module);
        if (Debug.verboseOn()) Debug.logVerbose("[Using delegator]: " + dispatcher.getDelegator().getDelegatorName(), module);

        // we have a service and the model; build the context
        Map serviceContext = new HashMap();
        Iterator ci = model.getInParamNames().iterator();

        while (ci.hasNext()) {
            String name = (String) ci.next();

            // don't include userLogin, that's taken care of below
            if ("userLogin".equals(name)) continue;
            
            Object value = request.getParameter(name);

            // if the parameter wasn't passed and no other value found, don't pass on the null
            if (value == null) {
                value = request.getAttribute(name);
            } 
            if (value == null) {
                value = request.getSession().getAttribute(name);
            }
            if (value == null) {
                //still null, give up for this one
                continue;
            }
            
            if (value instanceof String && ((String) value).length() == 0) {
                // interpreting empty fields as null values for each in back end handling...
                value = null;
            }

            // set even if null so that values will get nulled in the db later on
            serviceContext.put(name, value);
        }

        // get only the parameters for this service
        serviceContext = model.makeValid(serviceContext, ModelService.IN_PARAM);
        
        // include the UserLogin value object
        if (userLogin != null) 
            serviceContext.put("userLogin", userLogin);        
        
        // include the Locale object
        if (locale != null)
            serviceContext.put("locale", locale);

        // invoke the service
        Map result = null;

        try {
            if (ASYNC.equalsIgnoreCase(mode)) {
                dispatcher.runAsync(serviceName, serviceContext);
            } else {
                result = dispatcher.runSync(serviceName, serviceContext);
            }
        } catch (GenericServiceException e) {
            Debug.logError(e);
            throw new EventHandlerException("Service invocation error", e.getNested());
        }

        String responseString = null;

        if (result == null) {
            responseString = ModelService.RESPOND_SUCCESS;
        } else {

            if (!result.containsKey(ModelService.RESPONSE_MESSAGE))
                responseString = ModelService.RESPOND_SUCCESS;
            else
                responseString = (String) result.get(ModelService.RESPONSE_MESSAGE);

            // Get the messages:
            String errorPrefixStr = UtilProperties.getMessage("DefaultMessages", "service.error.prefix", locale);
            String errorSuffixStr = UtilProperties.getMessage("DefaultMessages", "service.error.suffix", locale);
            String successPrefixStr = UtilProperties.getMessage("DefaultMessages", "service.success.prefix", locale);
            String successSuffixStr = UtilProperties.getMessage("DefaultMessages", "service.success.suffix", locale);
            String messagePrefixStr = UtilProperties.getMessage("DefaultMessages", "service.message.prefix", locale);
            String messageSuffixStr = UtilProperties.getMessage("DefaultMessages", "service.message.suffix", locale);
            String defaultMessageStr = UtilProperties.getMessage("DefaultMessages", "service.default.message", locale);

            String errorMessage = ServiceUtil.makeErrorMessage(result, messagePrefixStr, messageSuffixStr, errorPrefixStr, errorSuffixStr);

            if (UtilValidate.isNotEmpty(errorMessage))
                request.setAttribute(SiteDefs.ERROR_MESSAGE, errorMessage);

            String successMessage = ServiceUtil.makeSuccessMessage(result, messagePrefixStr, messageSuffixStr, successPrefixStr, successSuffixStr);

            if (UtilValidate.isNotEmpty(successMessage))
                request.setAttribute(SiteDefs.EVENT_MESSAGE, successMessage);

            if (UtilValidate.isEmpty(errorMessage) && UtilValidate.isEmpty(successMessage) && UtilValidate.isNotEmpty(defaultMessageStr))
                request.setAttribute(SiteDefs.EVENT_MESSAGE, defaultMessageStr);

            // set the results in the request
            Iterator ri = result.keySet().iterator();

            while (ri.hasNext()) {
                String resultKey = (String) ri.next();
                Object resultValue = result.get((Object) resultKey);

                if (!resultKey.equals(ModelService.RESPONSE_MESSAGE) && !resultKey.equals(ModelService.ERROR_MESSAGE) &&
                        !resultKey.equals(ModelService.ERROR_MESSAGE_LIST) && !resultKey.equals(ModelService.ERROR_MESSAGE_MAP) &&
                        !resultKey.equals(ModelService.SUCCESS_MESSAGE) && !resultKey.equals(ModelService.SUCCESS_MESSAGE_LIST)) {
                    request.setAttribute(resultKey, resultValue);
                }
            }
        }

        if (Debug.verboseOn()) Debug.logVerbose("[Event Return]: " + responseString, module);
        return responseString;
    }
}
