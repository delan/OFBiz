/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.minilang;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.location.FlexibleLocation;
import org.ofbiz.base.util.Assert;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.cache.UtilCache;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.minilang.method.MethodContext;
import org.ofbiz.minilang.method.MethodOperation;
import org.ofbiz.minilang.method.MethodOperation.DeprecatedOperation;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Contains a block of Mini-language code.
 */
public final class SimpleMethod {

    public static final String module = SimpleMethod.class.getName();
    public static final String err_resource = "MiniLangErrorUiLabels";
    private static final Map<String, MethodOperation.Factory<MethodOperation>> methodOperationFactories;
    private static final UtilCache<String, Map<String, SimpleMethod>> simpleMethodsDirectCache = UtilCache.createUtilCache("minilang.SimpleMethodsDirect", 0, 0);
    private static final UtilCache<String, Map<String, SimpleMethod>> simpleMethodsResourceCache = UtilCache.createUtilCache("minilang.SimpleMethodsResource", 0, 0);
    private static final UtilCache<URL, Map<String, SimpleMethod>> simpleMethodsURLCache = UtilCache.createUtilCache("minilang.SimpleMethodsURL", 0, 0);

    static {
        Map<String, MethodOperation.Factory<MethodOperation>> mapFactories = new HashMap<String, MethodOperation.Factory<MethodOperation>>();
        Iterator<MethodOperation.Factory<MethodOperation>> it = UtilGenerics.cast(ServiceLoader.load(MethodOperation.Factory.class, SimpleMethod.class.getClassLoader()).iterator());
        while (it.hasNext()) {
            MethodOperation.Factory<MethodOperation> factory = it.next();
            mapFactories.put(factory.getName(), factory);
        }
        methodOperationFactories = Collections.unmodifiableMap(mapFactories);
    }

    private static void compileAllSimpleMethods(Element rootElement, Map<String, SimpleMethod> simpleMethods, String location) throws MiniLangException {
        for (Element simpleMethodElement : UtilXml.childElementList(rootElement, "simple-method")) {
            SimpleMethod simpleMethod = compileSimpleMethod(simpleMethodElement, simpleMethods, location);
            if (simpleMethods.containsKey(simpleMethod.getMethodName())) {
                MiniLangValidate.handleError("Duplicate method name found", simpleMethod, simpleMethodElement);
            }
            simpleMethods.put(simpleMethod.getMethodName(), simpleMethod);
        }
    }

    private static SimpleMethod compileSimpleMethod(Element simpleMethodElement, Map<String, SimpleMethod> simpleMethods, String location) throws MiniLangException {
        return new SimpleMethod(simpleMethodElement, simpleMethods, location);
    }

    private static Map<String, SimpleMethod> getAllDirectSimpleMethods(String name, String content, String fromLocation) throws MiniLangException {
        if (UtilValidate.isEmpty(fromLocation)) {
            fromLocation = "<location not known>";
        }
        Map<String, SimpleMethod> simpleMethods = FastMap.newInstance();
        Document document = null;
        try {
            document = UtilXml.readXmlDocument(content, true, true);
        } catch (Exception e) {
            throw new MiniLangException("Could not read SimpleMethod XML document [" + name + "]: ", e);
        }
        compileAllSimpleMethods(document.getDocumentElement(), simpleMethods, fromLocation);
        return simpleMethods;
    }

    private static Map<String, SimpleMethod> getAllSimpleMethods(URL xmlURL) throws MiniLangException {
        Map<String, SimpleMethod> simpleMethods = FastMap.newInstance();
        Document document = null;
        try {
            document = UtilXml.readXmlDocument(xmlURL, true, true);
        } catch (Exception e) {
            throw new MiniLangException("Could not read SimpleMethod XML document [" + xmlURL + "]: ", e);
        }
        compileAllSimpleMethods(document.getDocumentElement(), simpleMethods, xmlURL.toString());
        if (MiniLangUtil.isDocumentAutoCorrected(document)) {
            MiniLangUtil.writeMiniLangDocument(xmlURL, document);
        }
        return simpleMethods;
    }

    public static Map<String, SimpleMethod> getDirectSimpleMethods(String name, String content, String fromLocation) throws MiniLangException {
        Assert.notNull("name", name, "content", content);
        Map<String, SimpleMethod> simpleMethods = simpleMethodsDirectCache.get(name);
        if (simpleMethods == null) {
            simpleMethods = getAllDirectSimpleMethods(name, content, fromLocation);
            simpleMethodsDirectCache.putIfAbsent(name, simpleMethods);
            simpleMethods = simpleMethodsDirectCache.get(name);
        }
        return simpleMethods;
    }

    public static Map<String, SimpleMethod> getSimpleMethods(String xmlResource, ClassLoader loader) throws MiniLangException {
        Assert.notNull("xmlResource", xmlResource);
        Map<String, SimpleMethod> simpleMethods = simpleMethodsResourceCache.get(xmlResource);
        if (simpleMethods == null) {
            URL xmlURL = null;
            try {
                xmlURL = FlexibleLocation.resolveLocation(xmlResource, loader);
            } catch (MalformedURLException e) {
                throw new MiniLangException("Could not find SimpleMethod XML document in resource: " + xmlResource + "; error was: " + e.toString(), e);
            }
            if (xmlURL == null) {
                throw new MiniLangException("Could not find SimpleMethod XML document in resource: " + xmlResource);
            }
            simpleMethods = getAllSimpleMethods(xmlURL);
            simpleMethodsResourceCache.putIfAbsent(xmlResource, simpleMethods);
            simpleMethods = simpleMethodsResourceCache.get(xmlResource);
        }
        return simpleMethods;
    }

    public static Map<String, SimpleMethod> getSimpleMethods(URL xmlURL) throws MiniLangException {
        Assert.notNull("xmlURL", xmlURL);
        Map<String, SimpleMethod> simpleMethods = simpleMethodsURLCache.get(xmlURL);
        if (simpleMethods == null) {
            simpleMethods = getAllSimpleMethods(xmlURL);
            simpleMethodsURLCache.putIfAbsent(xmlURL, simpleMethods);
            simpleMethods = simpleMethodsURLCache.get(xmlURL);
        }
        return simpleMethods;
    }

    public static List<SimpleMethod> getSimpleMethodsList(String xmlResource, ClassLoader loader) throws MiniLangException {
        Assert.notNull("xmlResource", xmlResource);
        List<SimpleMethod> simpleMethods = FastList.newInstance();
        // Let the standard Map returning method take care of caching and compilation
        Map<String, SimpleMethod> simpleMethodMap = SimpleMethod.getSimpleMethods(xmlResource, loader);
        // Load and traverse the document again to get a correctly ordered list of methods
        URL xmlURL = null;
        try {
            xmlURL = FlexibleLocation.resolveLocation(xmlResource, loader);
        } catch (MalformedURLException e) {
            throw new MiniLangException("Could not find SimpleMethod XML document in resource: " + xmlResource + ": ", e);
        }
        Document document = null;
        try {
            document = UtilXml.readXmlDocument(xmlURL, true, true);
        } catch (Exception e) {
            throw new MiniLangException("Could not read SimpleMethod XML document [" + xmlURL + "]: ", e);
        }
        Element rootElement = document.getDocumentElement();
        for (Element simpleMethodElement : UtilXml.childElementList(rootElement, "simple-method")) {
            simpleMethods.add(simpleMethodMap.get(simpleMethodElement.getAttribute("method-name")));
        }
        return simpleMethods;
    }

    public static List<MethodOperation> readOperations(Element simpleMethodElement, SimpleMethod simpleMethod) throws MiniLangException {
        Assert.notNull("simpleMethodElement", simpleMethodElement, "simpleMethod", simpleMethod);
        List<? extends Element> operationElements = UtilXml.childElementList(simpleMethodElement);
        ArrayList<MethodOperation> methodOperations = new ArrayList<MethodOperation>(operationElements.size());
        if (UtilValidate.isNotEmpty(operationElements)) {
            for (Element curOperElem : operationElements) {
                String nodeName = curOperElem.getNodeName();
                MethodOperation methodOp = null;
                MethodOperation.Factory<MethodOperation> factory = methodOperationFactories.get(nodeName);
                if (factory != null) {
                    methodOp = factory.createMethodOperation(curOperElem, simpleMethod);
                } else if ("else".equals(nodeName)) {
                    // don't add anything, but don't complain either, this one is handled in the individual operations
                } else {
                    MiniLangValidate.handleError("Invalid element found", simpleMethod, curOperElem);
                }
                if (methodOp == null) {
                    continue;
                }
                methodOperations.add(methodOp);
                DeprecatedOperation depOp = methodOp.getClass().getAnnotation(DeprecatedOperation.class);
                if (depOp != null) {
                    MiniLangValidate.handleError("The " + nodeName + " operation has been deprecated in favor of the " + depOp.value() + " operation", simpleMethod, curOperElem);
                }
            }
        }
        methodOperations.trimToSize();
        return methodOperations;
    }

    public static String runSimpleEvent(String xmlResource, String methodName, HttpServletRequest request, HttpServletResponse response) throws MiniLangException {
        return runSimpleMethod(xmlResource, methodName, new MethodContext(request, response, null));
    }

    public static String runSimpleEvent(String xmlResource, String methodName, HttpServletRequest request, HttpServletResponse response, ClassLoader loader) throws MiniLangException {
        return runSimpleMethod(xmlResource, methodName, new MethodContext(request, response, loader));
    }

    public static String runSimpleEvent(URL xmlURL, String methodName, HttpServletRequest request, HttpServletResponse response, ClassLoader loader) throws MiniLangException {
        return runSimpleMethod(xmlURL, methodName, new MethodContext(request, response, loader));
    }

    public static String runSimpleMethod(String xmlResource, String methodName, MethodContext methodContext) throws MiniLangException {
        Assert.notNull("methodName", methodName, "methodContext", methodContext);
        Map<String, SimpleMethod> simpleMethods = getSimpleMethods(xmlResource, methodContext.getLoader());
        SimpleMethod simpleMethod = simpleMethods.get(methodName);
        if (simpleMethod == null) {
            throw new MiniLangException("Could not find SimpleMethod " + methodName + " in XML document in resource: " + xmlResource);
        }
        return simpleMethod.exec(methodContext);
    }

    public static String runSimpleMethod(URL xmlURL, String methodName, MethodContext methodContext) throws MiniLangException {
        Assert.notNull("methodName", methodName, "methodContext", methodContext);
        Map<String, SimpleMethod> simpleMethods = getSimpleMethods(xmlURL);
        SimpleMethod simpleMethod = simpleMethods.get(methodName);
        if (simpleMethod == null) {
            throw new MiniLangException("Could not find SimpleMethod " + methodName + " in XML document from URL: " + xmlURL.toString());
        }
        return simpleMethod.exec(methodContext);
    }

    public static Map<String, Object> runSimpleService(String xmlResource, String methodName, DispatchContext ctx, Map<String, ? extends Object> context) throws MiniLangException {
        MethodContext methodContext = new MethodContext(ctx, context, null);
        runSimpleMethod(xmlResource, methodName, methodContext);
        return methodContext.getResults();
    }

    public static Map<String, Object> runSimpleService(String xmlResource, String methodName, DispatchContext ctx, Map<String, ? extends Object> context, ClassLoader loader) throws MiniLangException {
        MethodContext methodContext = new MethodContext(ctx, context, loader);
        runSimpleMethod(xmlResource, methodName, methodContext);
        return methodContext.getResults();
    }

    public static Map<String, Object> runSimpleService(URL xmlURL, String methodName, DispatchContext ctx, Map<String, ? extends Object> context, ClassLoader loader) throws MiniLangException {
        MethodContext methodContext = new MethodContext(ctx, context, loader);
        runSimpleMethod(xmlURL, methodName, methodContext);
        return methodContext.getResults();
    }

    /**
     * Execs the given operations returning true if all return true, or returning false and stopping if any return false.
     * @throws MiniLangException 
     */
    public static boolean runSubOps(List<MethodOperation> methodOperations, MethodContext methodContext) throws MiniLangException {
        Assert.notNull("methodOperations", methodOperations, "methodContext", methodContext);
        for (MethodOperation methodOperation : methodOperations) {
            if (!methodOperation.exec(methodContext)) {
                return false;
            }
        }
        return true;
    }

    private final String defaultErrorCode;
    private final String defaultSuccessCode;
    private final String delegatorName;
    private final String dispatcherName;
    private final String eventErrorMessageListName;
    private final String eventErrorMessageName;
    private final String eventEventMessageListName;
    private final String eventEventMessageName;
    private final String eventRequestName;
    private final String eventResponseCodeName;
    private final String eventResponseName;
    private final String eventSessionName;
    private final String fromLocation;
    private final String localeName;
    private final boolean loginRequired;
    private final String methodName;
    private final List<MethodOperation> methodOperations;
    private final String parameterMapName;
    private final Map<String, SimpleMethod> parentSimpleMethodsMap;
    private final String securityName;
    private final String serviceErrorMessageListName;
    private final String serviceErrorMessageMapName;
    private final String serviceErrorMessageName;
    private final String serviceResponseMessageName;
    private final String serviceSuccessMessageListName;
    private final String serviceSuccessMessageName;
    private final String shortDescription;
    private final String userLoginName;
    private final boolean useTransaction;

    public SimpleMethod(Element simpleMethodElement, Map<String, SimpleMethod> parentSimpleMethodsMap, String fromLocation) throws MiniLangException {
        if (MiniLangValidate.validationOn()) {
            String locationMsg = " File = ".concat(fromLocation);
            if (simpleMethodElement.getAttribute("method-name").isEmpty()) {
                MiniLangValidate.handleError("Element must include the \"method-name\" attribute.".concat(locationMsg), null, simpleMethodElement);
            }
        }
        this.parentSimpleMethodsMap = parentSimpleMethodsMap;
        this.fromLocation = fromLocation;
        methodName = simpleMethodElement.getAttribute("method-name");
        shortDescription = simpleMethodElement.getAttribute("short-description");
        defaultErrorCode = UtilXml.elementAttribute(simpleMethodElement, "default-error-code", "error");
        defaultSuccessCode = UtilXml.elementAttribute(simpleMethodElement, "default-success-code", "success");
        parameterMapName = UtilXml.elementAttribute(simpleMethodElement, "parameter-map-name", "parameters");
        eventRequestName = UtilXml.elementAttribute(simpleMethodElement, "event-request-object-name", "request");
        eventSessionName = UtilXml.elementAttribute(simpleMethodElement, "event-session-object-name", "session");
        eventResponseName = UtilXml.elementAttribute(simpleMethodElement, "event-response-object-name", "response");
        eventResponseCodeName = UtilXml.elementAttribute(simpleMethodElement, "event-response-code-name", "_response_code_");
        eventErrorMessageName = UtilXml.elementAttribute(simpleMethodElement, "event-error-message-name", "_error_message_");
        eventErrorMessageListName = UtilXml.elementAttribute(simpleMethodElement, "event-error-message-list-name", "_error_message_list_");
        eventEventMessageName = UtilXml.elementAttribute(simpleMethodElement, "event-event-message-name", "_event_message_");
        eventEventMessageListName = UtilXml.elementAttribute(simpleMethodElement, "event-event-message-list-name", "_event_message_list_");
        serviceResponseMessageName = UtilXml.elementAttribute(simpleMethodElement, "service-response-message-name", "responseMessage");
        serviceErrorMessageName = UtilXml.elementAttribute(simpleMethodElement, "service-error-message-name", "errorMessage");
        serviceErrorMessageListName = UtilXml.elementAttribute(simpleMethodElement, "service-error-message-list-name", "errorMessageList");
        serviceErrorMessageMapName = UtilXml.elementAttribute(simpleMethodElement, "service-error-message-map-name", "errorMessageMap");
        serviceSuccessMessageName = UtilXml.elementAttribute(simpleMethodElement, "service-success-message-name", "successMessage");
        serviceSuccessMessageListName = UtilXml.elementAttribute(simpleMethodElement, "service-success-message-list-name", "successMessageList");
        loginRequired = !"false".equals(simpleMethodElement.getAttribute("login-required"));
        useTransaction = !"false".equals(simpleMethodElement.getAttribute("use-transaction"));
        localeName = UtilXml.elementAttribute(simpleMethodElement, "locale-name", "locale");
        delegatorName = UtilXml.elementAttribute(simpleMethodElement, "delegator-name", "delegator");
        securityName = UtilXml.elementAttribute(simpleMethodElement, "security-name", "security");
        dispatcherName = UtilXml.elementAttribute(simpleMethodElement, "dispatcher-name", "dispatcher");
        userLoginName = UtilXml.elementAttribute(simpleMethodElement, "user-login-name", "userLogin");
        methodOperations = Collections.unmodifiableList(readOperations(simpleMethodElement, this));
    }

    public void addErrorMessage(MethodContext methodContext, String message) {
        String messageListName = methodContext.getMethodType() == MethodContext.EVENT ? getEventErrorMessageListName() : getServiceErrorMessageListName();
        addMessage(methodContext, messageListName, message);
    }

    public void addMessage(MethodContext methodContext, String message) {
        String messageListName = methodContext.getMethodType() == MethodContext.EVENT ? getEventEventMessageListName() : getServiceSuccessMessageListName();
        addMessage(methodContext, messageListName, message);
    }

    private void addMessage(MethodContext methodContext, String messageListName, String message) {
        List<String> messages = methodContext.getEnv(messageListName);
        if (messages == null) {
            messages = FastList.newInstance();
            methodContext.putEnv(messageListName, messages);
        }
        messages.add(message);
    }

    /** Execute the Simple Method operations */
    public String exec(MethodContext methodContext) throws MiniLangException {
        // always put the null field object in as "null"
        methodContext.putEnv("null", GenericEntity.NULL_FIELD);
        methodContext.putEnv("nullField", GenericEntity.NULL_FIELD);
        methodContext.putEnv(delegatorName, methodContext.getDelegator());
        methodContext.putEnv(securityName, methodContext.getSecurity());
        methodContext.putEnv(dispatcherName, methodContext.getDispatcher());
        methodContext.putEnv(localeName, methodContext.getLocale());
        methodContext.putEnv(parameterMapName, methodContext.getParameters());
        if (methodContext.getMethodType() == MethodContext.EVENT) {
            methodContext.putEnv(eventRequestName, methodContext.getRequest());
            methodContext.putEnv(eventSessionName, methodContext.getRequest().getSession());
            methodContext.putEnv(eventResponseName, methodContext.getResponse());
        }
        methodContext.putEnv("methodName", this.getMethodName());
        methodContext.putEnv("methodShortDescription", this.getShortDescription());
        GenericValue userLogin = methodContext.getUserLogin();
        Locale locale = methodContext.getLocale();
        if (userLogin != null) {
            methodContext.putEnv(userLoginName, userLogin);
        }
        if (loginRequired) {
            if (userLogin == null) {
                Map<String, Object> messageMap = UtilMisc.<String, Object> toMap("shortDescription", shortDescription);
                String errMsg = UtilProperties.getMessage(SimpleMethod.err_resource, "simpleMethod.must_logged_process", messageMap, locale) + ".";
                return returnError(methodContext, errMsg);
            }
        }
        // if using transaction, try to start here
        boolean beganTransaction = false;
        if (useTransaction) {
            try {
                beganTransaction = TransactionUtil.begin();
            } catch (GenericTransactionException e) {
                String errMsg = UtilProperties.getMessage(SimpleMethod.err_resource, "simpleMethod.error_begin_transaction", locale) + ": " + e.getMessage();
                Debug.logWarning(e, errMsg, module);
                return returnError(methodContext, errMsg);
            }
        }
        // declare errorMsg here just in case transaction ops fail
        String errorMsg = "";
        boolean finished = false;
        try {
            finished = runSubOps(methodOperations, methodContext);
        } catch (Throwable t) {
            // make SURE nothing gets thrown through
            String errMsg = UtilProperties.getMessage(SimpleMethod.err_resource, "simpleMethod.error_running", locale) + ": " + t.getMessage();
            Debug.logWarning(t, errMsg, module);
            finished = false;
            errorMsg += errMsg;
        }
        String returnValue = null;
        String response = null;
        StringBuilder summaryErrorStringBuffer = new StringBuilder();
        if (methodContext.getMethodType() == MethodContext.EVENT) {
            boolean forceError = false;
            String tempErrorMsg = (String) methodContext.getEnv(eventErrorMessageName);
            if (errorMsg.length() > 0 || UtilValidate.isNotEmpty(tempErrorMsg)) {
                errorMsg += tempErrorMsg;
                methodContext.getRequest().setAttribute("_ERROR_MESSAGE_", errorMsg);
                forceError = true;
                summaryErrorStringBuffer.append(errorMsg);
            }
            List<Object> tempErrorMsgList = UtilGenerics.checkList(methodContext.getEnv(eventErrorMessageListName));
            if (UtilValidate.isNotEmpty(tempErrorMsgList)) {
                methodContext.getRequest().setAttribute("_ERROR_MESSAGE_LIST_", tempErrorMsgList);
                forceError = true;
                summaryErrorStringBuffer.append("; ");
                summaryErrorStringBuffer.append(tempErrorMsgList.toString());
            }
            String eventMsg = (String) methodContext.getEnv(eventEventMessageName);
            if (UtilValidate.isNotEmpty(eventMsg)) {
                methodContext.getRequest().setAttribute("_EVENT_MESSAGE_", eventMsg);
            }
            List<String> eventMsgList = UtilGenerics.checkList(methodContext.getEnv(eventEventMessageListName));
            if (UtilValidate.isNotEmpty(eventMsgList)) {
                methodContext.getRequest().setAttribute("_EVENT_MESSAGE_LIST_", eventMsgList);
            }
            response = (String) methodContext.getEnv(eventResponseCodeName);
            if (UtilValidate.isEmpty(response)) {
                if (forceError) {
                    // override response code, always use error code
                    Debug.logInfo("No response code string found, but error messages found so assuming error; returning code [" + defaultErrorCode + "]", module);
                    response = defaultErrorCode;
                } else {
                    Debug.logInfo("No response code string or errors found, assuming success; returning code [" + defaultSuccessCode + "]", module);
                    response = defaultSuccessCode;
                }
            } else if ("null".equalsIgnoreCase(response)) {
                response = null;
            }
            returnValue = response;
        } else {
            boolean forceError = false;
            String tempErrorMsg = (String) methodContext.getEnv(serviceErrorMessageName);
            if (errorMsg.length() > 0 || UtilValidate.isNotEmpty(tempErrorMsg)) {
                errorMsg += tempErrorMsg;
                methodContext.putResult(ModelService.ERROR_MESSAGE, errorMsg);
                forceError = true;
                summaryErrorStringBuffer.append(errorMsg);
            }
            List<Object> errorMsgList = UtilGenerics.checkList(methodContext.getEnv(serviceErrorMessageListName));
            if (UtilValidate.isNotEmpty(errorMsgList)) {
                methodContext.putResult(ModelService.ERROR_MESSAGE_LIST, errorMsgList);
                forceError = true;
                summaryErrorStringBuffer.append("; ");
                summaryErrorStringBuffer.append(errorMsgList.toString());
            }
            Map<String, Object> errorMsgMap = UtilGenerics.checkMap(methodContext.getEnv(serviceErrorMessageMapName));
            if (UtilValidate.isNotEmpty(errorMsgMap)) {
                methodContext.putResult(ModelService.ERROR_MESSAGE_MAP, errorMsgMap);
                forceError = true;
                summaryErrorStringBuffer.append("; ");
                summaryErrorStringBuffer.append(errorMsgMap.toString());
            }
            String successMsg = (String) methodContext.getEnv(serviceSuccessMessageName);
            if (UtilValidate.isNotEmpty(successMsg)) {
                methodContext.putResult(ModelService.SUCCESS_MESSAGE, successMsg);
            }
            List<Object> successMsgList = UtilGenerics.checkList(methodContext.getEnv(serviceSuccessMessageListName));
            if (UtilValidate.isNotEmpty(successMsgList)) {
                methodContext.putResult(ModelService.SUCCESS_MESSAGE_LIST, successMsgList);
            }
            response = (String) methodContext.getEnv(serviceResponseMessageName);
            if (UtilValidate.isEmpty(response)) {
                if (forceError) {
                    // override response code, always use error code
                    Debug.logVerbose("No response code string found, but error messages found so assuming error; returning code [" + defaultErrorCode + "]", module);
                    response = defaultErrorCode;
                } else {
                    Debug.logVerbose("No response code string or errors found, assuming success; returning code [" + defaultSuccessCode + "]", module);
                    response = defaultSuccessCode;
                }
            }
            methodContext.putResult(ModelService.RESPONSE_MESSAGE, response);
            returnValue = null;
        }
        // decide whether or not to commit based on the response message, ie only rollback if error is returned and not finished
        boolean doCommit = true;
        if (!finished && defaultErrorCode.equals(response)) {
            doCommit = false;
        }
        if (doCommit) {
            // commit here passing beganTransaction to perform it properly
            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericTransactionException e) {
                String errMsg = "Error trying to commit transaction, could not process method: " + e.getMessage();
                Debug.logWarning(e, errMsg, module);
                errorMsg += errMsg;
            }
        } else {
            // rollback here passing beganTransaction to either rollback, or set rollback only
            try {
                TransactionUtil.rollback(beganTransaction, "Error in simple-method [" + this.getShortDescription() + "]: " + summaryErrorStringBuffer, null);
            } catch (GenericTransactionException e) {
                String errMsg = "Error trying to rollback transaction, could not process method: " + e.getMessage();
                Debug.logWarning(e, errMsg, module);
                errorMsg += errMsg;
            }
        }
        return returnValue;
    }

    public Set<String> getAllEntityNamesUsed() throws MiniLangException {
        Set<String> allEntityNames = FastSet.newInstance();
        Set<String> simpleMethodsVisited = FastSet.newInstance();
        MiniLangUtil.findEntityNamesUsed(this.methodOperations, allEntityNames, simpleMethodsVisited);
        return allEntityNames;
    }

    public Set<String> getAllServiceNamesCalled() throws MiniLangException {
        Set<String> allServiceNames = FastSet.newInstance();
        Set<String> simpleMethodsVisited = FastSet.newInstance();
        MiniLangUtil.findServiceNamesCalled(this.methodOperations, allServiceNames, simpleMethodsVisited);
        return allServiceNames;
    }

    public String getDefaultErrorCode() {
        return this.defaultErrorCode;
    }

    public String getDefaultSuccessCode() {
        return this.defaultSuccessCode;
    }

    public String getDelegatorEnvName() {
        return this.delegatorName;
    }

    public String getDispatcherEnvName() {
        return this.dispatcherName;
    }

    public String getEventErrorMessageListName() {
        return this.eventErrorMessageListName;
    }

    public String getEventErrorMessageName() {
        return this.eventErrorMessageName;
    }

    public String getEventEventMessageListName() {
        return this.eventEventMessageListName;
    }

    public String getEventEventMessageName() {
        return this.eventEventMessageName;
    }

    // event fields
    public String getEventRequestName() {
        return this.eventRequestName;
    }

    public String getEventResponseCodeName() {
        return this.eventResponseCodeName;
    }

    public String getEventSessionName() {
        return this.eventSessionName;
    }

    public String getFromLocation() {
        return this.fromLocation;
    }

    public String getLocationAndName() {
        return this.fromLocation + "#" + this.methodName;
    }

    public boolean getLoginRequired() {
        return this.loginRequired;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public List<MethodOperation> getMethodOperations() {
        return this.methodOperations;
    }

    public String getParameterMapName() {
        return this.parameterMapName;
    }

    public String getSecurityEnvName() {
        return this.securityName;
    }

    public String getServiceErrorMessageListName() {
        return this.serviceErrorMessageListName;
    }

    public String getServiceErrorMessageMapName() {
        return this.serviceErrorMessageMapName;
    }

    public String getServiceErrorMessageName() {
        return this.serviceErrorMessageName;
    }

    public String getServiceResponseMessageName() {
        return this.serviceResponseMessageName;
    }

    public String getServiceSuccessMessageListName() {
        return this.serviceSuccessMessageListName;
    }

    public String getServiceSuccessMessageName() {
        return this.serviceSuccessMessageName;
    }

    public String getShortDescription() {
        return this.shortDescription + " [" + this.fromLocation + "#" + this.methodName + "]";
    }

    public SimpleMethod getSimpleMethodInSameFile(String simpleMethodName) {
        if (parentSimpleMethodsMap == null)
            return null;
        return parentSimpleMethodsMap.get(simpleMethodName);
    }

    public String getUserLoginEnvName() {
        return this.userLoginName;
    }

    public boolean getUseTransaction() {
        return this.useTransaction;
    }

    private String returnError(MethodContext methodContext, String errorMsg) {
        if (methodContext.getMethodType() == MethodContext.EVENT) {
            methodContext.getRequest().setAttribute("_ERROR_MESSAGE_", errorMsg);
        } else {
            methodContext.putResult(ModelService.ERROR_MESSAGE, errorMsg);
            methodContext.putResult(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
        }
        return defaultErrorCode;
    }
}
