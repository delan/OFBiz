/*
 * $Id: ServiceEventHandler.java,v 1.8 2004/07/10 04:56:09 jonesde Exp $
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

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelParam;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.ServiceValidationException;
import org.ofbiz.service.ServiceAuthException;

/**
 * ServiceEventHandler - Service Event Handler
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.8 $
 * @since      2.0
 */
public class ServiceEventHandler implements EventHandler {

    public static final String module = ServiceEventHandler.class.getName();

    public static final String SYNC = "sync";
    public static final String ASYNC = "async";

    /**
     * @see org.ofbiz.content.webapp.event.EventHandler#invoke(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public String invoke(String eventPath, String eventMethod, HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
        // make sure we have a valid reference to the Service Engine
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        if (dispatcher == null) {
            throw new EventHandlerException("The local service dispatcher is null");
        }
        DispatchContext dctx = dispatcher.getDispatchContext();
        if (dctx == null) {
            throw new EventHandlerException("Dispatch context cannot be found");
        }

        // get the details for the service(s) to call
        String mode = SYNC;
        String serviceName = null;

        if (eventPath == null || eventPath.length() == 0) {
            mode = SYNC;
        } else {
            mode = eventPath;
        }

        // nake sure we have a defined service to call
        serviceName = eventMethod;
        if (serviceName == null) {
            throw new EventHandlerException("Service name (eventMethod) cannot be null");
        }
        if (Debug.verboseOn()) Debug.logVerbose("[Set mode/service]: " + mode + "/" + serviceName, module);

        // some needed info for when running the service
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

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
        Iterator modelParmInIter = model.getInModelParamList().iterator();
        while (modelParmInIter.hasNext()) {
            ModelParam modelParam = (ModelParam) modelParmInIter.next();
            String name = modelParam.name;

            // don't include userLogin, that's taken care of below
            if ("userLogin".equals(name)) continue;
            // don't include locale, that is also taken care of below
            if ("locale".equals(name)) continue;

            Object value = null;
            if (modelParam.stringMapPrefix != null && modelParam.stringMapPrefix.length() > 0) {
                Map paramMap = UtilHttp.makeParamMapWithPrefix(request, modelParam.stringMapPrefix, null);
                value = paramMap;
                if (Debug.verboseOn()) Debug.log("Set [" + modelParam.name + "]: " + paramMap, module);
            } else if (modelParam.stringListSuffix != null && modelParam.stringListSuffix.length() > 0) {
                List paramList = UtilHttp.makeParamListWithSuffix(request, modelParam.stringListSuffix, null);
                value = paramList;
            } else {
                value = request.getParameter(name);

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
            }
            // set even if null so that values will get nulled in the db later on
            serviceContext.put(name, value);
        }

        // get only the parameters for this service - converted to proper type
        // TODO: pass in a list for error messages, like could not convert type or not a proper X, return immediately with messages if there are any
        List errorMessages = new LinkedList();
        serviceContext = model.makeValid(serviceContext, ModelService.IN_PARAM, true, errorMessages);
        if (errorMessages.size() > 0) {
            // uh-oh, had some problems...
            request.setAttribute("_ERROR_MESSAGE_LIST_", errorMessages);
            return "error";
        }

        // include the UserLogin value object
        if (userLogin != null) {
            serviceContext.put("userLogin", userLogin);
        }

        // include the Locale object
        if (locale != null) {
            serviceContext.put("locale", locale);
        }

        // invoke the service
        Map result = null;
        try {
            if (ASYNC.equalsIgnoreCase(mode)) {
                dispatcher.runAsync(serviceName, serviceContext);
            } else {
                result = dispatcher.runSync(serviceName, serviceContext);
            }
        } catch (ServiceAuthException e) {
            // not logging since the service engine already did
            request.setAttribute("_ERROR_MESSAGE_", e.getNonNestedMessage());
            return "error";
        } catch (ServiceValidationException e) {
            // not logging since the service engine already did
            request.setAttribute("serviceValidationException", e);
            request.setAttribute("_ERROR_MESSAGE_", e.getNonNestedMessage());
            return "error";
        } catch (GenericServiceException e) {
            Debug.logError(e, "Service invocation error", module);
            throw new EventHandlerException("Service invocation error", e.getNested());
        }

        String responseString = null;

        if (result == null) {
            responseString = ModelService.RESPOND_SUCCESS;
        } else {

            if (!result.containsKey(ModelService.RESPONSE_MESSAGE)) {
                responseString = ModelService.RESPOND_SUCCESS;
            } else {
                responseString = (String) result.get(ModelService.RESPONSE_MESSAGE);
            }

            // Get the messages:
            String errorPrefixStr = UtilProperties.getMessage("DefaultMessages", "service.error.prefix", locale);
            String errorSuffixStr = UtilProperties.getMessage("DefaultMessages", "service.error.suffix", locale);
            String successPrefixStr = UtilProperties.getMessage("DefaultMessages", "service.success.prefix", locale);
            String successSuffixStr = UtilProperties.getMessage("DefaultMessages", "service.success.suffix", locale);
            String messagePrefixStr = UtilProperties.getMessage("DefaultMessages", "service.message.prefix", locale);
            String messageSuffixStr = UtilProperties.getMessage("DefaultMessages", "service.message.suffix", locale);
            String defaultMessageStr = UtilProperties.getMessage("DefaultMessages", "service.default.message", locale);

            String errorMessage = ServiceUtil.makeErrorMessage(result, messagePrefixStr, messageSuffixStr, errorPrefixStr, errorSuffixStr);

            if (UtilValidate.isNotEmpty(errorMessage)) {
                request.setAttribute("_ERROR_MESSAGE_", errorMessage);
            }

            String successMessage = ServiceUtil.makeSuccessMessage(result, messagePrefixStr, messageSuffixStr, successPrefixStr, successSuffixStr);

            if (UtilValidate.isNotEmpty(successMessage)) {
                request.setAttribute("_EVENT_MESSAGE_", successMessage);
            }

            if (UtilValidate.isEmpty(errorMessage) && UtilValidate.isEmpty(successMessage) && UtilValidate.isNotEmpty(defaultMessageStr)) {
                request.setAttribute("_EVENT_MESSAGE_", defaultMessageStr);
            }

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
