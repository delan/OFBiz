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

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> ServiceEventHandler - Service Event Handler
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    December 7, 2001
 *@version    1.0
 */
public class ServiceEventHandler implements EventHandler {

    public static final String module = ServiceEventHandler.class.getName();

    public static final String SYNC = "sync";
    public static final String ASYNC = "async";

    private String mode = SYNC;
    private String serviceName = null;

    /** Initialize the required parameters
     *@param eventPath The mode of service invokation
     *@param eventMethod The service to invoke
     */
    public void initialize(String eventPath, String eventMethod) {
        if (eventPath == null || eventPath.length() == 0) {
          this.mode = SYNC;
        }
        else {
          this.mode = eventPath;
        }
        this.serviceName = eventMethod;
        Debug.logVerbose("[Set mode/service]: " +
                                mode + "/" + serviceName, module);
    }

    /** Invoke the web event
     *@param request The servlet request object
     *@param response The servlet response object
     *@return String Result code
     *@throws EventHandlerException
     */
    public String invoke(HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
        HttpSession session = request.getSession();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        if (dispatcher == null)
            throw new EventHandlerException("The local service dispatcher is null");

        GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        DispatchContext dctx = dispatcher.getDispatchContext();
        if (dctx == null)
            throw new EventHandlerException("Dispatch context cannot be found");

        if (serviceName == null)
            throw new EventHandlerException("Service name (eventMethod) cannot be null");

        // get the service model to generate context
        ModelService model = null;
        try {
            model = dctx.getModelService(serviceName);
        } catch (GenericServiceException e) {
            throw new EventHandlerException("Problems getting the service model",e);
        }

        if (model == null)
            throw new EventHandlerException("Problems getting the service model");

        Debug.logVerbose("[Processing]: SERVICE Event", module);
        Debug.logVerbose("[Using delegator]: " + dispatcher.getDelegator().getDelegatorName(), module);

        // we have a service and the model; build the context
        Map serviceContext = new HashMap();
        Iterator ci = model.getInParamNames().iterator();
        while (ci.hasNext()) {
            String name = (String) ci.next();
            Object value = request.getParameter(name);
            if (value == null)
                value = request.getAttribute(name);
            if (value == null)
                value = request.getSession().getAttribute(name);
            if ( value != null )
                serviceContext.put(name,value);
        }

        // get only the parameters for this service
        serviceContext = model.makeValid(serviceContext,ModelService.IN_PARAM);
        if (userLogin != null)
            serviceContext.put("userLogin", userLogin);

        // invoke the service
        Map result = null;
        try {
            if (ASYNC.equalsIgnoreCase(mode)) {
              dispatcher.runAsync(serviceName,serviceContext);
            }
            else {
              result = dispatcher.runSync(serviceName,serviceContext);
            }
        } catch (GenericServiceException e) {
            throw new EventHandlerException("Service invocation error", e);
        }

        String responseString = null;
        if (result == null) {
            responseString = ModelService.RESPOND_SUCCESS;
        }
        else {

            if (!result.containsKey(ModelService.RESPONSE_MESSAGE))
                responseString = ModelService.RESPOND_SUCCESS;
            else
                responseString = (String) result.get(ModelService.RESPONSE_MESSAGE);

            //Get the messages:

            String errorPrefixStr = UtilProperties.getPropertyValue("DefaultMessages", "service.error.prefix");
            String errorSuffixStr = UtilProperties.getPropertyValue("DefaultMessages", "service.error.suffix");
            String successPrefixStr = UtilProperties.getPropertyValue("DefaultMessages", "service.success.prefix");
            String successSuffixStr = UtilProperties.getPropertyValue("DefaultMessages", "service.success.suffix");
            String messagePrefixStr = UtilProperties.getPropertyValue("DefaultMessages", "service.message.prefix");
            String messageSuffixStr = UtilProperties.getPropertyValue("DefaultMessages", "service.message.suffix");
            String defaultMessageStr = UtilProperties.getPropertyValue("DefaultMessages", "service.default.message");

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
                Object resultValue = result.get((Object)resultKey);
                if (!resultKey.equals(ModelService.RESPONSE_MESSAGE) && !resultKey.equals(ModelService.ERROR_MESSAGE) &&
                        !resultKey.equals(ModelService.ERROR_MESSAGE_LIST) && !resultKey.equals(ModelService.SUCCESS_MESSAGE) && !resultKey.equals(ModelService.SUCCESS_MESSAGE_LIST)) {
                    request.setAttribute(resultKey,resultValue);
                }
            }
        }

        Debug.logVerbose("[Event Return]: " + responseString, module);

        return responseString;

    }
}
