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
package org.ofbiz.core.minilang;

import java.net.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;

import org.ofbiz.core.minilang.method.*;

/**
 * SimpleMethod Mini Language Core Object
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a> 
 * @version    $Revision$
 * @since      2.0
 */
public class SimpleMethod {
    
    public static final String module = SimpleMethod.class.getName();

    protected static UtilCache simpleMethodsResourceCache = new UtilCache("minilang.SimpleMethodsResource", 0, 0);
    protected static UtilCache simpleMethodsURLCache = new UtilCache("minilang.SimpleMethodsURL", 0, 0);

    // ----- Event Context Invokers -----

    public static String runSimpleEvent(String xmlResource, String methodName, HttpServletRequest request, HttpServletResponse response) throws MiniLangException {
        return runSimpleMethod(xmlResource, methodName, new MethodContext(request, response, null));
    }

    public static String runSimpleEvent(String xmlResource, String methodName, HttpServletRequest request, HttpServletResponse response, ClassLoader loader) throws MiniLangException {
        return runSimpleMethod(xmlResource, methodName, new MethodContext(request, response, loader));
    }

    public static String runSimpleEvent(URL xmlURL, String methodName, HttpServletRequest request, HttpServletResponse response, ClassLoader loader) throws MiniLangException {
        return runSimpleMethod(xmlURL, methodName, new MethodContext(request, response, loader));
    }

    // ----- Service Context Invokers -----

    public static Map runSimpleService(String xmlResource, String methodName, DispatchContext ctx, Map context) throws MiniLangException {
        MethodContext methodContext = new MethodContext(ctx, context, null);

        runSimpleMethod(xmlResource, methodName, methodContext);
        return methodContext.getResults();
    }

    public static Map runSimpleService(String xmlResource, String methodName, DispatchContext ctx, Map context, ClassLoader loader) throws MiniLangException {
        MethodContext methodContext = new MethodContext(ctx, context, loader);

        runSimpleMethod(xmlResource, methodName, methodContext);
        return methodContext.getResults();
    }

    public static Map runSimpleService(URL xmlURL, String methodName, DispatchContext ctx, Map context, ClassLoader loader) throws MiniLangException {
        MethodContext methodContext = new MethodContext(ctx, context, loader);

        runSimpleMethod(xmlURL, methodName, methodContext);
        return methodContext.getResults();
    }

    // ----- General Method Invokers -----

    public static String runSimpleMethod(String xmlResource, String methodName, MethodContext methodContext) throws MiniLangException {
        Map simpleMethods = getSimpleMethods(xmlResource, methodName, methodContext.getLoader());
        SimpleMethod simpleMethod = (SimpleMethod) simpleMethods.get(methodName);

        if (simpleMethod == null) {
            throw new MiniLangException("Could not find SimpleMethod " + methodName + " in XML document in resource: " + xmlResource);
        }
        return simpleMethod.exec(methodContext);
    }

    public static String runSimpleMethod(URL xmlURL, String methodName, MethodContext methodContext) throws MiniLangException {
        Map simpleMethods = getSimpleMethods(xmlURL, methodName);
        SimpleMethod simpleMethod = (SimpleMethod) simpleMethods.get(methodName);

        if (simpleMethod == null) {
            throw new MiniLangException("Could not find SimpleMethod " + methodName + " in XML document from URL: " + xmlURL.toString());
        }
        return simpleMethod.exec(methodContext);
    }

    public static Map getSimpleMethods(String xmlResource, String methodName, ClassLoader loader) throws MiniLangException {
        Map simpleMethods = (Map) simpleMethodsResourceCache.get(xmlResource);

        if (simpleMethods == null) {
            synchronized (SimpleMethod.class) {
                simpleMethods = (Map) simpleMethodsResourceCache.get(xmlResource);
                if (simpleMethods == null) {
                    URL xmlURL = UtilURL.fromResource(xmlResource, loader);

                    if (xmlURL == null) {
                        throw new MiniLangException("Could not find SimpleMethod XML document in resource: " + xmlResource);
                    }
                    simpleMethods = getAllSimpleMethods(xmlURL);

                    // put it in the cache
                    simpleMethodsResourceCache.put(xmlResource, simpleMethods);
                }
            }
        }

        return simpleMethods;
    }

    public static Map getSimpleMethods(URL xmlURL, String methodName) throws MiniLangException {
        Map simpleMethods = (Map) simpleMethodsURLCache.get(xmlURL);

        if (simpleMethods == null) {
            synchronized (SimpleMethod.class) {
                simpleMethods = (Map) simpleMethodsURLCache.get(xmlURL);
                if (simpleMethods == null) {
                    simpleMethods = getAllSimpleMethods(xmlURL);

                    // put it in the cache
                    simpleMethodsURLCache.put(xmlURL, simpleMethods);
                }
            }
        }

        return simpleMethods;
    }

    protected static Map getAllSimpleMethods(URL xmlURL) throws MiniLangException {
        Map simpleMethods = new HashMap();

        // read in the file
        Document document = null;

        try {
            document = UtilXml.readXmlDocument(xmlURL, true);
        } catch (java.io.IOException e) {
            throw new MiniLangException("Could not read XML file", e);
        } catch (org.xml.sax.SAXException e) {
            throw new MiniLangException("Could not parse XML file", e);
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            throw new MiniLangException("XML parser not setup correctly", e);
        }

        if (document == null) {
            throw new MiniLangException("Could not find SimpleMethod XML document: " + xmlURL.toString());
        }

        Element rootElement = document.getDocumentElement();
        List simpleMethodElements = UtilXml.childElementList(rootElement, "simple-method");

        Iterator simpleMethodIter = simpleMethodElements.iterator();

        while (simpleMethodIter.hasNext()) {
            Element simpleMethodElement = (Element) simpleMethodIter.next();
            SimpleMethod simpleMethod = new SimpleMethod(simpleMethodElement);

            simpleMethods.put(simpleMethod.getMethodName(), simpleMethod);
        }

        return simpleMethods;
    }

    // Member fields begin here...
    List methodOperations = new LinkedList();
    String methodName;
    String shortDescription;
    String defaultErrorCode;
    String defaultSuccessCode;

    String parameterMapName;

    // event fields
    String eventRequestName;
    String eventResponseName;
    String eventResponseCodeName;
    String eventErrorMessageName;
    String eventEventMessageName;

    // service fields
    String serviceResponseMessageName;
    String serviceErrorMessageName;
    String serviceErrorMessageListName;
    String serviceErrorMessageMapName;
    String serviceSuccessMessageName;
    String serviceSuccessMessageListName;

    boolean loginRequired = true;
    boolean useTransaction = true;

    String localeName;
    String delegatorName;
    String securityName;
    String dispatcherName;
    String userLoginName;

    public SimpleMethod(Element simpleMethodElement) {
        methodName = simpleMethodElement.getAttribute("method-name");
        shortDescription = simpleMethodElement.getAttribute("short-description");

        defaultErrorCode = simpleMethodElement.getAttribute("default-error-code");
        if (defaultErrorCode == null || defaultErrorCode.length() == 0)
            defaultErrorCode = "error";
        defaultSuccessCode = simpleMethodElement.getAttribute("default-success-code");
        if (defaultSuccessCode == null || defaultSuccessCode.length() == 0)
            defaultSuccessCode = "success";

        parameterMapName = simpleMethodElement.getAttribute("parameter-map-name");
        if (parameterMapName == null || parameterMapName.length() == 0)
            parameterMapName = "parameters";

        eventRequestName = simpleMethodElement.getAttribute("event-request-object-name");
        if (eventRequestName == null || eventRequestName.length() == 0)
            eventRequestName = "request";
        eventResponseName = simpleMethodElement.getAttribute("event-response-object-name");
        if (eventResponseName == null || eventResponseName.length() == 0)
            eventResponseName = "response";
        eventResponseCodeName = simpleMethodElement.getAttribute("event-response-code-name");
        if (eventResponseCodeName == null || eventResponseCodeName.length() == 0)
            eventResponseCodeName = "_response_code_";
        eventErrorMessageName = simpleMethodElement.getAttribute("event-error-message-name");
        if (eventErrorMessageName == null || eventErrorMessageName.length() == 0)
            eventErrorMessageName = "_error_message_";
        eventEventMessageName = simpleMethodElement.getAttribute("event-event-message-name");
        if (eventEventMessageName == null || eventEventMessageName.length() == 0)
            eventEventMessageName = "_event_message_";

        serviceResponseMessageName = simpleMethodElement.getAttribute("service-response-message-name");
        if (serviceResponseMessageName == null || serviceResponseMessageName.length() == 0)
            serviceResponseMessageName = "responseMessage";
        serviceErrorMessageName = simpleMethodElement.getAttribute("service-error-message-name");
        if (serviceErrorMessageName == null || serviceErrorMessageName.length() == 0)
            serviceErrorMessageName = "errorMessage";
        serviceErrorMessageListName = simpleMethodElement.getAttribute("service-error-message-list-name");
        if (serviceErrorMessageListName == null || serviceErrorMessageListName.length() == 0)
            serviceErrorMessageListName = "errorMessageList";
        serviceErrorMessageMapName = simpleMethodElement.getAttribute("service-error-message-map-name");
        if (serviceErrorMessageMapName == null || serviceErrorMessageMapName.length() == 0)
            serviceErrorMessageMapName = "errorMessageMap";

        serviceSuccessMessageName = simpleMethodElement.getAttribute("service-success-message-name");
        if (serviceSuccessMessageName == null || serviceSuccessMessageName.length() == 0)
            serviceSuccessMessageName = "successMessage";
        serviceSuccessMessageListName = simpleMethodElement.getAttribute("service-success-message-list-name");
        if (serviceSuccessMessageListName == null || serviceSuccessMessageListName.length() == 0)
            serviceSuccessMessageListName = "successMessageList";

        loginRequired = !"false".equals(simpleMethodElement.getAttribute("login-required"));
        useTransaction = !"false".equals(simpleMethodElement.getAttribute("use-transaction"));

        localeName = simpleMethodElement.getAttribute("locale-name");
        if (localeName == null || localeName.length() == 0) {
            localeName = "locale";
        }
        delegatorName = simpleMethodElement.getAttribute("delegator-name");
        if (delegatorName == null || delegatorName.length() == 0) {
            delegatorName = "delegator";
        }
        securityName = simpleMethodElement.getAttribute("security-name");
        if (securityName == null || securityName.length() == 0) {
            securityName = "security";
        }
        dispatcherName = simpleMethodElement.getAttribute("dispatcher-name");
        if (dispatcherName == null || dispatcherName.length() == 0) {
            dispatcherName = "dispatcher";
        }
        userLoginName = simpleMethodElement.getAttribute("user-login-name");
        if (userLoginName == null || userLoginName.length() == 0) {
            userLoginName = "userLogin";
        }

        readOperations(simpleMethodElement, this.methodOperations, this);
    }

    public String getMethodName() {
        return this.methodName;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public String getDefaultErrorCode() {
        return this.defaultErrorCode;
    }

    public String getDefaultSuccessCode() {
        return this.defaultSuccessCode;
    }

    public String getParameterMapName() {
        return this.parameterMapName;
    }

    // event fields
    public String getEventRequestName() {
        return this.eventRequestName;
    }

    public String getEventResponseCodeName() {
        return this.eventResponseCodeName;
    }

    public String getEventErrorMessageName() {
        return this.eventErrorMessageName;
    }

    public String getEventEventMessageName() {
        return this.eventEventMessageName;
    }

    // service fields
    public String getServiceResponseMessageName() {
        return this.serviceResponseMessageName;
    }

    public String getServiceErrorMessageName() {
        return this.serviceErrorMessageName;
    }

    public String getServiceErrorMessageListName() {
        return this.serviceErrorMessageListName;
    }

    public String getServiceSuccessMessageName() {
        return this.serviceSuccessMessageName;
    }

    public String getServiceSuccessMessageListName() {
        return this.serviceSuccessMessageListName;
    }

    public boolean getLoginRequired() {
        return this.loginRequired;
    }

    public boolean getUseTransaction() {
        return this.useTransaction;
    }

    public String getDelegatorEnvName() {
        return this.delegatorName;
    }

    public String getSecurityEnvName() {
        return this.securityName;
    }

    public String getDispatcherEnvName() {
        return this.dispatcherName;
    }

    /** Execute the Simple Method operations */
    public String exec(MethodContext methodContext) {
        methodContext.putEnv(delegatorName, methodContext.getDelegator());
        methodContext.putEnv(securityName, methodContext.getSecurity());
        methodContext.putEnv(dispatcherName, methodContext.getDispatcher());
        methodContext.putEnv(localeName, methodContext.getLocale());
        methodContext.putEnv(parameterMapName, methodContext.getParameters());

        if (methodContext.getMethodType() == MethodContext.EVENT) {
            methodContext.putEnv(eventRequestName, methodContext.getRequest());
            methodContext.putEnv(eventResponseName, methodContext.getResponse());
        }

        GenericValue userLogin = methodContext.getUserLogin();

        if (userLogin != null) {
            methodContext.putEnv(userLoginName, userLogin);
        }
        if (loginRequired) {
            if (userLogin == null) {
                String errMsg = "You must be logged in to complete the " + shortDescription + " process.";

                if (methodContext.getMethodType() == MethodContext.EVENT) {
                    methodContext.getRequest().setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
                    return defaultErrorCode;
                } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
                    methodContext.putResult(ModelService.ERROR_MESSAGE, errMsg);
                    methodContext.putResult(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    return null;
                }
            }
        }

        // if using transaction, try to start here
        boolean beganTransaction = false;

        if (useTransaction) {
            try {
                beganTransaction = TransactionUtil.begin();
            } catch (GenericTransactionException e) {
                String errMsg = "Error trying to begin transaction, could not process method: " + e.getMessage();

                Debug.logWarning(errMsg, module);
                Debug.logWarning(e, module);
                if (methodContext.getMethodType() == MethodContext.EVENT) {
                    methodContext.getRequest().setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
                    return defaultErrorCode;
                } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
                    methodContext.putResult(ModelService.ERROR_MESSAGE, errMsg);
                    methodContext.putResult(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    return null;
                }
            }
        }

        boolean finished = runSubOps(methodOperations, methodContext);

        // declare errorMsg here just in case transaction ops fail
        String errorMsg = "";

        if (finished) {
            // if finished commit here passing beganTransaction to perform it properly
            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericTransactionException e) {
                String errMsg = "Error trying to commit transaction, could not process method: " + e.getMessage();

                errorMsg += errMsg + "<br>";
                Debug.logWarning(errMsg, module);
                Debug.logWarning(e, module);
            }
        } else {
            // if NOT finished rollback here passing beganTransaction to either rollback, or set rollback only
            try {
                TransactionUtil.rollback(beganTransaction);
            } catch (GenericTransactionException e) {
                String errMsg = "Error trying to rollback transaction, could not process method: " + e.getMessage();

                errorMsg += errMsg + "<br>";
                Debug.logWarning(errMsg, module);
                Debug.logWarning(e, module);
            }
        }

        if (methodContext.getMethodType() == MethodContext.EVENT) {
            boolean forceError = false;
            
            String tempErrorMsg = (String) methodContext.getEnv(eventErrorMessageName);
            if (errorMsg.length() > 0 || (tempErrorMsg != null && tempErrorMsg.length() > 0)) {
                errorMsg += tempErrorMsg;
                methodContext.getRequest().setAttribute(SiteDefs.ERROR_MESSAGE, errorMsg);
                forceError = true;
            }

            String eventMsg = (String) methodContext.getEnv(eventEventMessageName);
            if (eventMsg != null && eventMsg.length() > 0) {
                methodContext.getRequest().setAttribute(SiteDefs.EVENT_MESSAGE, eventMsg);
            }

            String response = (String) methodContext.getEnv(eventResponseCodeName);
            if (response == null || response.length() == 0) {
                if (forceError) {
                    //override response code, always use error code
                    Debug.logInfo("No response code string found, but error messages found so assuming error; returning code [" + defaultErrorCode + "]", module);
                    response = defaultErrorCode;
                } else {
                    Debug.logInfo("No response code string or errors found, assuming success; returning code [" + defaultSuccessCode + "]", module);
                    response = defaultSuccessCode;
                }
            }
            return response;
        } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
            boolean forceError = false;
            
            String tempErrorMsg = (String) methodContext.getEnv(serviceErrorMessageName);
            if (errorMsg.length() > 0 || (tempErrorMsg != null && tempErrorMsg.length() > 0)) {
                errorMsg += tempErrorMsg;
                methodContext.putResult(ModelService.ERROR_MESSAGE, errorMsg);
                forceError = true;
            }

            List errorMsgList = (List) methodContext.getEnv(serviceErrorMessageListName);
            if (errorMsgList != null && errorMsgList.size() > 0) {
                methodContext.putResult(ModelService.ERROR_MESSAGE_LIST, errorMsgList);
                forceError = true;
            }

            Map errorMsgMap = (Map) methodContext.getEnv(serviceErrorMessageMapName);
            if (errorMsgMap != null && errorMsgMap.size() > 0) {
                methodContext.putResult(ModelService.ERROR_MESSAGE_MAP, errorMsgMap);
                forceError = true;
            }

            String successMsg = (String) methodContext.getEnv(serviceSuccessMessageName);
            if (successMsg != null && successMsg.length() > 0) {
                methodContext.putResult(ModelService.SUCCESS_MESSAGE, successMsg);
            }

            List successMsgList = (List) methodContext.getEnv(serviceSuccessMessageListName);
            if (successMsgList != null && successMsgList.size() > 0) {
                methodContext.putResult(ModelService.SUCCESS_MESSAGE_LIST, successMsgList);
            }

            String response = (String) methodContext.getEnv(serviceResponseMessageName);
            if (response == null || response.length() == 0) {
                if (forceError) {
                    //override response code, always use error code
                    Debug.logInfo("No response code string found, but error messages found so assuming error; returning code [" + defaultErrorCode + "]", module);
                    response = defaultErrorCode;
                } else {
                    Debug.logInfo("No response code string or errors found, assuming success; returning code [" + defaultSuccessCode + "]", module);
                    response = defaultSuccessCode;
                }
            }
            methodContext.putResult(ModelService.RESPONSE_MESSAGE, response);
            return null;
        } else {
            return defaultSuccessCode;
        }
    }

    public static void readOperations(Element simpleMethodElement, List methodOperations, SimpleMethod simpleMethod) {
        List operationElements = UtilXml.childElementList(simpleMethodElement, null);

        if (operationElements != null && operationElements.size() > 0) {
            Iterator operElemIter = operationElements.iterator();

            while (operElemIter.hasNext()) {
                Element curOperElem = (Element) operElemIter.next();
                String nodeName = curOperElem.getNodeName();

                if ("call-map-processor".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.callops.CallSimpleMapProcessor(curOperElem, simpleMethod));
                } else if ("check-errors".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.callops.CheckErrors(curOperElem, simpleMethod));
                } else if ("add-error".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.callops.AddError(curOperElem, simpleMethod));
                } else if ("return".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.callops.Return(curOperElem, simpleMethod));
                } else if ("call-service".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.callops.CallService(curOperElem, simpleMethod));
                } else if ("call-service-asynch".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.callops.CallServiceAsynch(curOperElem, simpleMethod));
                } else if ("call-bsh".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.callops.CallBsh(curOperElem, simpleMethod));
                } else if ("call-simple-method".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.callops.CallSimpleMethod(curOperElem, simpleMethod));

                } else if ("call-object-method".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.callops.CallObjectMethod(curOperElem, simpleMethod));
                } else if ("call-class-method".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.callops.CallClassMethod(curOperElem, simpleMethod));
                } else if ("create-object".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.callops.CreateObject(curOperElem, simpleMethod));
                    
                } else if ("field-to-request".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.eventops.FieldToRequest(curOperElem, simpleMethod));
                } else if ("field-to-session".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.eventops.FieldToSession(curOperElem, simpleMethod));
                } else if ("request-to-field".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.eventops.RequestToField(curOperElem, simpleMethod));
                } else if ("session-to-field".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.eventops.SessionToField(curOperElem, simpleMethod));
                } else if ("webapp-property-to-field".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.eventops.WebappPropertyToField(curOperElem, simpleMethod));

                } else if ("field-to-result".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.serviceops.FieldToResult(curOperElem, simpleMethod));

                } else if ("map-to-map".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.envops.MapToMap(curOperElem, simpleMethod));
                } else if ("field-to-field".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.envops.FieldToField(curOperElem, simpleMethod));
                } else if ("field-to-list".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.envops.FieldToList(curOperElem, simpleMethod));
                } else if ("env-to-field".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.envops.EnvToField(curOperElem, simpleMethod));
                } else if ("field-to-env".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.envops.FieldToEnv(curOperElem, simpleMethod));
                } else if ("string-to-field".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.envops.StringToField(curOperElem, simpleMethod));
                } else if ("to-string".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.envops.ToString(curOperElem, simpleMethod));
                } else if ("clear-field".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.envops.ClearField(curOperElem, simpleMethod));
                } else if ("iterate".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.envops.Iterate(curOperElem, simpleMethod));
                } else if ("first-from-list".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.envops.FirstFromList(curOperElem, simpleMethod));

                } else if ("transaction-begin".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.TransactionBegin(curOperElem, simpleMethod));
                } else if ("transaction-commit".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.TransactionCommit(curOperElem, simpleMethod));
                } else if ("transaction-rollback".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.TransactionRollback(curOperElem, simpleMethod));
                    
                } else if ("now-timestamp-to-env".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.NowTimestampToEnv(curOperElem, simpleMethod));
                } else if ("now-date-to-env".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.NowDateToEnv(curOperElem, simpleMethod));
                } else if ("sequenced-id-to-env".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.SequencedIdToEnv(curOperElem, simpleMethod));
                } else if ("set-current-user-login".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.SetCurrentUserLogin(curOperElem, simpleMethod));

                } else if ("find-by-primary-key".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.FindByPrimaryKey(curOperElem, simpleMethod));
                } else if ("find-by-and".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.FindByAnd(curOperElem, simpleMethod));
                } else if ("filter-list-by-and".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.FilterListByAnd(curOperElem, simpleMethod));
                } else if ("filter-list-by-date".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.FilterListByDate(curOperElem, simpleMethod));

                } else if ("make-value".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.MakeValue(curOperElem, simpleMethod));
                } else if ("clone-value".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.CloneValue(curOperElem, simpleMethod));
                } else if ("create-value".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.CreateValue(curOperElem, simpleMethod));
                } else if ("store-value".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.StoreValue(curOperElem, simpleMethod));
                } else if ("remove-value".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.RemoveValue(curOperElem, simpleMethod));
                } else if ("remove-related".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.RemoveRelated(curOperElem, simpleMethod));
                } else if ("remove-by-and".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.RemoveByAnd(curOperElem, simpleMethod));
                } else if ("clear-cache-line".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.ClearCacheLine(curOperElem, simpleMethod));
                } else if ("clear-entity-caches".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.ClearEntityCaches(curOperElem, simpleMethod));
                } else if ("set-pk-fields".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.SetPkFields(curOperElem, simpleMethod));
                } else if ("set-nonpk-fields".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.SetNonpkFields(curOperElem, simpleMethod));

                } else if ("store-list".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.StoreList(curOperElem, simpleMethod));
                } else if ("remove-list".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.entityops.RemoveList(curOperElem, simpleMethod));

                } else if ("if-validate-method".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.ifops.IfValidateMethod(curOperElem, simpleMethod));
                } else if ("if-compare".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.ifops.IfCompare(curOperElem, simpleMethod));
                } else if ("if-compare-field".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.ifops.IfCompareField(curOperElem, simpleMethod));
                } else if ("if-regexp".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.ifops.IfRegexp(curOperElem, simpleMethod));
                } else if ("if-empty".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.ifops.IfEmpty(curOperElem, simpleMethod));
                } else if ("if-not-empty".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.ifops.IfNotEmpty(curOperElem, simpleMethod));
                } else if ("if-has-permission".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.ifops.IfHasPermission(curOperElem, simpleMethod));
                } else if ("check-permission".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.ifops.CheckPermission(curOperElem, simpleMethod));
                } else if ("check-id".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.ifops.CheckId(curOperElem, simpleMethod));
                } else if ("else".equals(nodeName)) {// don't add anything, but don't complain either, this one is handled in the individual operations
                    
                } else if ("property-to-field".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.otherops.PropertyToField(curOperElem, simpleMethod));
                } else if ("calculate".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.otherops.Calculate(curOperElem, simpleMethod));
                } else if ("log".equals(nodeName)) {
                    methodOperations.add(new org.ofbiz.core.minilang.method.otherops.Log(curOperElem, simpleMethod));
                    
                } else {
                    Debug.logWarning("Operation element \"" + nodeName + "\" no recognized", module);
                }
            }
        }
    }

    /** Execs the given operations returning true if all return true, or returning 
     *  false and stopping if any return false.
     */
    public static boolean runSubOps(List methodOperations, MethodContext methodContext) {
        Iterator methodOpsIter = methodOperations.iterator();

        while (methodOpsIter.hasNext()) {
            MethodOperation methodOperation = (MethodOperation) methodOpsIter.next();

            if (!methodOperation.exec(methodContext)) {
                return false;
            }
        }

        return true;
    }
}
