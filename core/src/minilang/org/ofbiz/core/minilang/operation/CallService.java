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

package org.ofbiz.core.minilang.operation;

import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;

import org.ofbiz.core.minilang.*;

/**
 * Calls a service using the given parameters
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    December 29, 2001
 *@version    1.0
 */
public class CallService extends MethodOperation {
    String serviceName;
    String inMapName;
    boolean includeUserLogin = true;
    String errorCode;
    String successCode;

    FlexibleMessage errorPrefix;
    FlexibleMessage errorSuffix;
    FlexibleMessage successPrefix;
    FlexibleMessage successSuffix;
    FlexibleMessage messagePrefix;
    FlexibleMessage messageSuffix;
    FlexibleMessage defaultMessage;

    /** A list of strings with names of new maps to create */
    List resultsToMap = new LinkedList();
    /** A list of ResultToFieldDef objects */
    List resultToField = new LinkedList();
    /** the key is the request attribute name, the value is the result name to get */
    Map resultToRequest = new HashMap();
    /** the key is the session attribute name, the value is the result name to get */
    Map resultToSession = new HashMap();
    /** the key is the result entry name, the value is the result name to get */
    Map resultToResult = new HashMap();

    public CallService(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        serviceName = element.getAttribute("service-name");
        inMapName = element.getAttribute("in-map-name");
        includeUserLogin = !"false".equals(element.getAttribute("include-user-login"));
        errorCode = element.getAttribute("error-code");
        if (errorCode == null || errorCode.length() == 0)
            errorCode = "error";

        successCode = element.getAttribute("success-code");
        if (successCode == null || successCode.length() == 0)
            successCode = "success";

        errorPrefix = new FlexibleMessage(UtilXml.firstChildElement(element, "error-prefix"), "service.error.prefix");
        errorSuffix = new FlexibleMessage(UtilXml.firstChildElement(element, "error-suffix"), "service.error.suffix");
        successPrefix = new FlexibleMessage(UtilXml.firstChildElement(element, "success-prefix"), "service.success.prefix");
        successSuffix = new FlexibleMessage(UtilXml.firstChildElement(element, "success-suffix"), "service.success.suffix");
        messagePrefix = new FlexibleMessage(UtilXml.firstChildElement(element, "message-prefix"), "service.message.prefix");
        messageSuffix = new FlexibleMessage(UtilXml.firstChildElement(element, "message-suffix"), "service.message.suffix");
        defaultMessage = new FlexibleMessage(UtilXml.firstChildElement(element, "default-message"), "service.default.message");

        List resultsToMapElements = UtilXml.childElementList(element, "results-to-map");
        if (resultsToMapElements != null && resultsToMapElements.size() > 0) {
            Iterator iter = resultsToMapElements.iterator();
            while (iter.hasNext()) {
                Element resultsToMapElement = (Element) iter.next();
                resultsToMap.add(resultsToMapElement.getAttribute("map-name"));
            }
        }

        List resultToFieldElements = UtilXml.childElementList(element, "result-to-field");
        if (resultToFieldElements != null && resultToFieldElements.size() > 0) {
            Iterator iter = resultToFieldElements.iterator();
            while (iter.hasNext()) {
                Element resultToFieldElement = (Element) iter.next();
                ResultToFieldDef rtfDef = new ResultToFieldDef();
                rtfDef.resultName = resultToFieldElement.getAttribute("result-name");
                rtfDef.mapName = resultToFieldElement.getAttribute("map-name");
                rtfDef.fieldName = resultToFieldElement.getAttribute("field-name");

                if (rtfDef.fieldName == null || rtfDef.fieldName.length() == 0)
                    rtfDef.fieldName = rtfDef.resultName;

                resultToField.add(rtfDef);
            }
        }

        //get result-to-request and result-to-session sub-ops
        List resultToRequestElements = UtilXml.childElementList(element, "result-to-request");
        if (resultToRequestElements != null && resultToRequestElements.size() > 0) {
            Iterator iter = resultToRequestElements.iterator();
            while (iter.hasNext()) {
                Element resultToRequestElement = (Element) iter.next();
                String reqName = resultToRequestElement.getAttribute("request-name");
                if (reqName == null || reqName.length() == 0)
                    reqName = resultToRequestElement.getAttribute("result-name");
                resultToRequest.put(reqName, resultToRequestElement.getAttribute("result-name"));
            }
        }

        List resultToSessionElements = UtilXml.childElementList(element, "result-to-session");
        if (resultToSessionElements != null && resultToSessionElements.size() > 0) {
            Iterator iter = resultToSessionElements.iterator();
            while (iter.hasNext()) {
                Element resultToSessionElement = (Element) iter.next();
                String sesName = resultToSessionElement.getAttribute("session-name");
                if (sesName == null || sesName.length() == 0)
                    sesName = resultToSessionElement.getAttribute("result-name");
                resultToSession.put(sesName, resultToSessionElement.getAttribute("result-name"));
            }
        }

        List resultToResultElements = UtilXml.childElementList(element, "result-to-result");
        if (resultToResultElements != null && resultToResultElements.size() > 0) {
            Iterator iter = resultToResultElements.iterator();
            while (iter.hasNext()) {
                Element resultToResultElement = (Element) iter.next();
                String serResName = resultToResultElement.getAttribute("service-result-name");
                if (serResName == null || serResName.length() == 0)
                    serResName = resultToResultElement.getAttribute("result-name");
                resultToSession.put(serResName, resultToResultElement.getAttribute("result-name"));
            }
        }
    }

    public boolean exec(MethodContext methodContext) {
        Map inMap = (Map) methodContext.getEnv(inMapName);
        if (inMap == null) {
            inMap = new HashMap();
            methodContext.putEnv(inMapName, inMap);
        }

        // invoke the service
        Map result = null;
        if (includeUserLogin) {
            GenericValue userLogin = null;
            if (methodContext.getMethodType() == MethodContext.EVENT) {
                userLogin = (GenericValue) methodContext.getRequest().getSession().getAttribute(SiteDefs.USER_LOGIN);
            } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
                userLogin = (GenericValue) methodContext.getParameter("userLogin");
            }
            if (userLogin != null)
                inMap.put("userLogin", userLogin);
        }
        try {
            result = methodContext.getDispatcher().runSync(serviceName, inMap);
        } catch (GenericServiceException e) {
            Debug.logError(e);
            methodContext.putEnv(simpleMethod.eventErrorMessageName, "ERROR: Could not complete " + simpleMethod.shortDescription + " process (problem invoking the " + serviceName + " service: " + e.getMessage() + ")");
            methodContext.putEnv(simpleMethod.eventResponseCodeName, errorCode);
            return false;
        }

        if (resultsToMap.size() > 0) {
            Iterator iter = resultsToMap.iterator();
            while (iter.hasNext()) {
                String mapName = (String) iter.next();
                methodContext.putEnv(mapName, new HashMap(result));
            }
        }

        if (resultToField.size() > 0) {
            Iterator iter = resultToField.iterator();
            while (iter.hasNext()) {
                ResultToFieldDef rtfDef = (ResultToFieldDef) iter.next();
                Map tempMap = (Map) methodContext.getEnv(rtfDef.mapName);
                if (tempMap == null) {
                    tempMap = new HashMap();
                    methodContext.putEnv(rtfDef.mapName, tempMap);
                }
                tempMap.put(rtfDef.fieldName, result.get(rtfDef.resultName));
            }
        }

        //only run this if it is in an EVENT context
        if (methodContext.getMethodType() == MethodContext.EVENT) {
            if (resultToRequest.size() > 0) {
                Iterator iter = resultToRequest.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    methodContext.getRequest().setAttribute((String) entry.getKey(), result.get(entry.getValue()));
                }
            }
        }

        //only run this if it is in an EVENT context
        if (methodContext.getMethodType() == MethodContext.EVENT) {
            if (resultToSession.size() > 0) {
                Iterator iter = resultToSession.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    methodContext.getRequest().getSession().setAttribute((String) entry.getKey(), result.get(entry.getValue()));
                }
            }
        }

        //only run this if it is in an SERVICE context
        if (methodContext.getMethodType() == MethodContext.SERVICE) {
            if (resultToResult.size() > 0) {
                Iterator iter = resultToResult.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    methodContext.putResult((String) entry.getKey(), result.get(entry.getValue()));
                }
            }
        }

        String errorPrefixStr = errorPrefix.getMessage(methodContext.getLoader());
        String errorSuffixStr = errorSuffix.getMessage(methodContext.getLoader());
        String successPrefixStr = successPrefix.getMessage(methodContext.getLoader());
        String successSuffixStr = successSuffix.getMessage(methodContext.getLoader());
        String messagePrefixStr = messagePrefix.getMessage(methodContext.getLoader());
        String messageSuffixStr = messageSuffix.getMessage(methodContext.getLoader());

        String errorMessage = ServiceUtil.makeErrorMessage(result, messagePrefixStr, messageSuffixStr, errorPrefixStr, errorSuffixStr);
        if (UtilValidate.isNotEmpty(errorMessage))
            methodContext.putEnv(simpleMethod.eventErrorMessageName, errorMessage);

        String successMessage = ServiceUtil.makeSuccessMessage(result, messagePrefixStr, messageSuffixStr, successPrefixStr, successSuffixStr);
        if (UtilValidate.isNotEmpty(successMessage))
            methodContext.putEnv(simpleMethod.eventEventMessageName, successMessage);

        String defaultMessageStr = defaultMessage.getMessage(methodContext.getLoader());
        if (UtilValidate.isEmpty(errorMessage) && UtilValidate.isEmpty(successMessage) && UtilValidate.isNotEmpty(defaultMessageStr))
            methodContext.putEnv(simpleMethod.eventEventMessageName, defaultMessageStr);

        // handle the result
        String responseCode = result.containsKey(ModelService.RESPONSE_MESSAGE) ? (String) result.get(ModelService.RESPONSE_MESSAGE) : successCode;
        methodContext.putEnv(simpleMethod.eventResponseCodeName, responseCode);

        if (successCode.equals(responseCode)) {
            return true;
        } else {
            return false;
        }
    }

    public static class ResultToFieldDef {
        public String resultName;
        public String mapName;
        public String fieldName;
    }
}
