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
package org.ofbiz.core.minilang.method.callops;

import java.util.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;

import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;

/**
 * Calls a service using the given parameters
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a> 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class CallService extends MethodOperation {
    
    public static final String module = CallService.class.getName();
    
    String serviceName;
    ContextAccessor inMapAcsr;
    String includeUserLoginStr;
    String breakOnErrorStr;
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
        inMapAcsr = new ContextAccessor(element.getAttribute("in-map-name"));
        includeUserLoginStr = element.getAttribute("include-user-login");
        breakOnErrorStr = element.getAttribute("break-on-error");
        errorCode = element.getAttribute("error-code");
        if (errorCode == null || errorCode.length() == 0) errorCode = "error";

        successCode = element.getAttribute("success-code");
        if (successCode == null || successCode.length() == 0) successCode = "success";

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
                rtfDef.mapAcsr = new ContextAccessor(resultToFieldElement.getAttribute("map-name"));
                rtfDef.fieldAcsr = new ContextAccessor(resultToFieldElement.getAttribute("field-name"), rtfDef.resultName);

                resultToField.add(rtfDef);
            }
        }

        // get result-to-request and result-to-session sub-ops
        List resultToRequestElements = UtilXml.childElementList(element, "result-to-request");
        if (resultToRequestElements != null && resultToRequestElements.size() > 0) {
            Iterator iter = resultToRequestElements.iterator();
            while (iter.hasNext()) {
                Element resultToRequestElement = (Element) iter.next();
                FlexibleServletAccessor reqAcsr = new FlexibleServletAccessor(resultToRequestElement.getAttribute("request-name"), resultToRequestElement.getAttribute("result-name"));
                ContextAccessor resultAcsr = new ContextAccessor(resultToRequestElement.getAttribute("result-name"));
                resultToRequest.put(reqAcsr, resultAcsr);
            }
        }

        List resultToSessionElements = UtilXml.childElementList(element, "result-to-session");
        if (resultToSessionElements != null && resultToSessionElements.size() > 0) {
            Iterator iter = resultToSessionElements.iterator();
            while (iter.hasNext()) {
                Element resultToSessionElement = (Element) iter.next();
                FlexibleServletAccessor sesAcsr = new FlexibleServletAccessor(resultToSessionElement.getAttribute("session-name"), resultToSessionElement.getAttribute("result-name"));
                ContextAccessor resultAcsr = new ContextAccessor(resultToSessionElement.getAttribute("result-name"));
                resultToSession.put(sesAcsr, resultAcsr);
            }
        }

        List resultToResultElements = UtilXml.childElementList(element, "result-to-result");
        if (resultToResultElements != null && resultToResultElements.size() > 0) {
            Iterator iter = resultToResultElements.iterator();
            while (iter.hasNext()) {
                Element resultToResultElement = (Element) iter.next();
                ContextAccessor serResAcsr = new ContextAccessor(resultToResultElement.getAttribute("service-result-name"), resultToResultElement.getAttribute("result-name"));
                ContextAccessor resultAcsr = new ContextAccessor(resultToResultElement.getAttribute("result-name"));
                resultToResult.put(serResAcsr, resultAcsr);
            }
        }
    }

    public boolean exec(MethodContext methodContext) {
        boolean includeUserLogin = !"false".equals(methodContext.expandString(includeUserLoginStr));
        boolean breakOnError = !"false".equals(methodContext.expandString(breakOnErrorStr));

        String serviceName = methodContext.expandString(this.serviceName);
        String errorCode = methodContext.expandString(this.errorCode);
        String successCode = methodContext.expandString(this.successCode);

        Map inMap = null;
        if (inMapAcsr.isEmpty()) {
            inMap = new HashMap();
        } else {
            inMap = (Map) inMapAcsr.get(methodContext);
            if (inMap == null) {
                inMap = new HashMap();
                inMapAcsr.put(methodContext, inMap);
            }
        }

        // before invoking the service, clear messages
        if (methodContext.getMethodType() == MethodContext.EVENT) {
            methodContext.removeEnv(simpleMethod.getEventErrorMessageName());
            methodContext.removeEnv(simpleMethod.getEventEventMessageName());
            methodContext.removeEnv(simpleMethod.getEventResponseCodeName());
        } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
            methodContext.removeEnv(simpleMethod.getServiceErrorMessageName());
            methodContext.removeEnv(simpleMethod.getServiceSuccessMessageName());
            methodContext.removeEnv(simpleMethod.getServiceResponseMessageName());
        }

        // invoke the service
        Map result = null;

        // add UserLogin to context if expected
        if (includeUserLogin) {
            GenericValue userLogin = methodContext.getUserLogin();

            if (userLogin != null) {
                inMap.put("userLogin", userLogin);
            }
        }
        
        // always add Locale to context unless null
        Locale locale = methodContext.getLocale();
        if (locale != null) {
            inMap.put("locale", locale);
        }
        
        try {
            result = methodContext.getDispatcher().runSync(serviceName, inMap);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [problem invoking the " + serviceName + " service: " + e.getMessage() + "]";
            if (methodContext.getMethodType() == MethodContext.EVENT) {
                methodContext.putEnv(simpleMethod.getEventErrorMessageName(), errMsg);
                methodContext.putEnv(simpleMethod.getEventResponseCodeName(), errorCode);
            } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
                methodContext.putEnv(simpleMethod.getServiceErrorMessageName(), errMsg);
                methodContext.putEnv(simpleMethod.getServiceResponseMessageName(), errorCode);
            }
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
                if (!rtfDef.mapAcsr.isEmpty()) {
                    Map tempMap = (Map) rtfDef.mapAcsr.get(methodContext);
                    if (tempMap == null) {
                        tempMap = new HashMap();
                        rtfDef.mapAcsr.put(methodContext, tempMap);
                    }
                    rtfDef.fieldAcsr.put(tempMap, result.get(rtfDef.resultName), methodContext);
                } else {
                    rtfDef.fieldAcsr.put(methodContext, result.get(rtfDef.resultName));
                }
            }
        }

        // only run this if it is in an EVENT context
        if (methodContext.getMethodType() == MethodContext.EVENT) {
            if (resultToRequest.size() > 0) {
                Iterator iter = resultToRequest.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    FlexibleServletAccessor requestAcsr = (FlexibleServletAccessor) entry.getKey();
                    ContextAccessor resultAcsr = (ContextAccessor) entry.getValue();
                    requestAcsr.put(methodContext.getRequest(), resultAcsr.get(result, methodContext), methodContext.getEnvMap());
                }
            }

            if (resultToSession.size() > 0) {
                Iterator iter = resultToSession.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    FlexibleServletAccessor sessionAcsr = (FlexibleServletAccessor) entry.getKey();
                    ContextAccessor resultAcsr = (ContextAccessor) entry.getValue();
                    sessionAcsr.put(methodContext.getRequest().getSession(), resultAcsr.get(result, methodContext), methodContext.getEnvMap());
                }
            }
        }

        // only run this if it is in an SERVICE context
        if (methodContext.getMethodType() == MethodContext.SERVICE) {
            if (resultToResult.size() > 0) {
                Iterator iter = resultToResult.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    ContextAccessor fromAcsr = (ContextAccessor) entry.getKey();
                    ContextAccessor resultAcsr = (ContextAccessor) entry.getValue();
                    fromAcsr.put(methodContext.getResults(), resultAcsr.get(result, methodContext), methodContext);
                }
            }
        }

        String errorPrefixStr = errorPrefix.getMessage(methodContext.getLoader(), methodContext);
        String errorSuffixStr = errorSuffix.getMessage(methodContext.getLoader(), methodContext);
        String successPrefixStr = successPrefix.getMessage(methodContext.getLoader(), methodContext);
        String successSuffixStr = successSuffix.getMessage(methodContext.getLoader(), methodContext);
        String messagePrefixStr = messagePrefix.getMessage(methodContext.getLoader(), methodContext);
        String messageSuffixStr = messageSuffix.getMessage(methodContext.getLoader(), methodContext);

        String errorMessage = ServiceUtil.makeErrorMessage(result, messagePrefixStr, messageSuffixStr, errorPrefixStr, errorSuffixStr);
        if (UtilValidate.isNotEmpty(errorMessage)) {
            errorMessage += " calling service " + serviceName + " in " + simpleMethod.getMethodName();
            if (methodContext.getMethodType() == MethodContext.EVENT) {
                methodContext.putEnv(simpleMethod.getEventErrorMessageName(), errorMessage);
            } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
                methodContext.putEnv(simpleMethod.getServiceErrorMessageName(), errorMessage);
            }
        }

        String successMessage = ServiceUtil.makeSuccessMessage(result, messagePrefixStr, messageSuffixStr, successPrefixStr, successSuffixStr);
        if (UtilValidate.isNotEmpty(successMessage)) {
            if (methodContext.getMethodType() == MethodContext.EVENT) {
                methodContext.putEnv(simpleMethod.getEventEventMessageName(), successMessage);
            } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
                methodContext.putEnv(simpleMethod.getServiceSuccessMessageName(), successMessage);
            }
        }

        String defaultMessageStr = defaultMessage.getMessage(methodContext.getLoader(), methodContext);
        if (UtilValidate.isEmpty(errorMessage) && UtilValidate.isEmpty(successMessage) && UtilValidate.isNotEmpty(defaultMessageStr)) {
            if (methodContext.getMethodType() == MethodContext.EVENT) {
                methodContext.putEnv(simpleMethod.getEventEventMessageName(), defaultMessageStr);
            } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
                methodContext.putEnv(simpleMethod.getServiceSuccessMessageName(), defaultMessageStr);
            }
        }

        // handle the result
        String responseCode = result.containsKey(ModelService.RESPONSE_MESSAGE) ? (String) result.get(ModelService.RESPONSE_MESSAGE) : successCode;
        if (methodContext.getMethodType() == MethodContext.EVENT) {
            methodContext.putEnv(simpleMethod.getEventResponseCodeName(), responseCode);
        } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
            methodContext.putEnv(simpleMethod.getServiceResponseMessageName(), responseCode);
        }

        if (errorCode.equals(responseCode) && breakOnError) {
            return false;
        } else {
            return true;
        }
    }

    public static class ResultToFieldDef {
        public String resultName;
        public ContextAccessor mapAcsr;
        public ContextAccessor fieldAcsr;
    }
}
