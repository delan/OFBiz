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

package org.ofbiz.core.minilang;

import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;

import org.ofbiz.core.minilang.operation.*;

/**
 * SimpleMethod Mini Language Core Object
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    December 29, 2001
 *@version    1.0
 */
public class SimpleMethod {

    protected static UtilCache simpleMethodsResourceCache = new UtilCache("SimpleMethodsResource", 0, 0);
    protected static UtilCache simpleMethodsURLCache = new UtilCache("SimpleMethodsURL", 0, 0);

    // ----- Event Context Invokers -----
    
    public static String runSimpleEvent(String xmlResource, String methodName, HttpServletRequest request) throws MiniLangException {
        return runSimpleMethod(xmlResource, methodName, new MethodContext(request, null));
    }
    public static String runSimpleEvent(String xmlResource, String methodName, HttpServletRequest request, ClassLoader loader) throws MiniLangException {
        return runSimpleMethod(xmlResource, methodName, new MethodContext(request, loader));
    }
    public static String runSimpleEvent(URL xmlURL, String methodName, HttpServletRequest request, ClassLoader loader) throws MiniLangException {
        return runSimpleMethod(xmlURL, methodName, new MethodContext(request, loader));
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

    protected static Map getSimpleMethods(String xmlResource, String methodName, ClassLoader loader) throws MiniLangException {
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

                    //put it in the cache
                    simpleMethodsResourceCache.put(xmlResource, simpleMethods);
                }
            }
        }

        return simpleMethods;
    }

    protected static Map getSimpleMethods(URL xmlURL, String methodName) throws MiniLangException {
        Map simpleMethods = (Map) simpleMethodsURLCache.get(xmlURL);
        if (simpleMethods == null) {
            synchronized (SimpleMethod.class) {
                simpleMethods = (Map) simpleMethodsURLCache.get(xmlURL);
                if (simpleMethods == null) {
                    simpleMethods = getAllSimpleMethods(xmlURL);

                    //put it in the cache
                    simpleMethodsURLCache.put(xmlURL, simpleMethods);
                }
            }
        }

        return simpleMethods;
    }

    protected static Map getAllSimpleMethods(URL xmlURL) throws MiniLangException {
        Map simpleMethods = new HashMap();

        //read in the file
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
    String eventResponseCodeName;
    String eventErrorMessageName;
    String eventEventMessageName;

    // service fields
    String serviceResponseMessageName;
    String serviceErrorMessageName;
    String serviceErrorMessageListName;
    String serviceSuccessMessageName;
    String serviceSuccessMessageListName;

    boolean loginRequired = true;
    boolean useTransaction = true;

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
            eventRequestName = "_request_";
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
        serviceSuccessMessageName = simpleMethodElement.getAttribute("service-success-message-name");
        if (serviceSuccessMessageName == null || serviceSuccessMessageName.length() == 0)
            serviceSuccessMessageName = "successMessage";
        serviceSuccessMessageListName = simpleMethodElement.getAttribute("service-success-message-list-name");
        if (serviceSuccessMessageListName == null || serviceSuccessMessageListName.length() == 0)
            serviceSuccessMessageListName = "successMessageList";

        loginRequired = !"false".equals(simpleMethodElement.getAttribute("login-required"));
        useTransaction = !"false".equals(simpleMethodElement.getAttribute("use-transaction"));

        readOperations(simpleMethodElement);
    }

    public String getMethodName() { return this.methodName; }
    public String getShortDescription() { return this.shortDescription; }
    public String getDefaultErrorCode() { return this.defaultErrorCode; }
    public String getDefaultSuccessCode() { return this.defaultSuccessCode; }

    public String getParameterMapName() { return this.parameterMapName; }

    // event fields
    public String getEventRequestName() { return this.eventRequestName; }
    public String getEventResponseCodeName() { return this.eventResponseCodeName; }
    public String getEventErrorMessageName() { return this.eventErrorMessageName; }
    public String getEventEventMessageName() { return this.eventEventMessageName; }

    // service fields
    public String getServiceResponseMessageName() { return this.serviceResponseMessageName; }
    public String getServiceErrorMessageName() { return this.serviceErrorMessageName; }
    public String getServiceErrorMessageListName() { return this.serviceErrorMessageListName; }
    public String getServiceSuccessMessageName() { return this.serviceSuccessMessageName; }
    public String getServiceSuccessMessageListName() { return this.serviceSuccessMessageListName; }

    public boolean getLoginRequired() { return this.loginRequired; }
    public boolean getUseTransaction() { return this.useTransaction; }

    /** Execute the Simple Method operations */
    public String exec(MethodContext methodContext) {
        methodContext.putEnv(parameterMapName, methodContext.getParameters());

        if (methodContext.getMethodType() == MethodContext.EVENT) {
            methodContext.putEnv(eventRequestName, methodContext.getRequest());
        }

        if (loginRequired) {
            GenericValue userLogin = null;
            if (methodContext.getMethodType() == MethodContext.EVENT) {
                userLogin = (GenericValue) methodContext.getRequest().getSession().getAttribute(SiteDefs.USER_LOGIN);
            } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
                userLogin = (GenericValue) methodContext.getParameter("userLogin");
            }
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

        //if using transaction, try to start here
        boolean beganTransaction = false;
        if (useTransaction) {
            try {
                beganTransaction = TransactionUtil.begin();
            } catch (GenericTransactionException e) {
                String errMsg = "Error trying to begin transaction, could not process method: " + e.getMessage();
                Debug.logWarning(errMsg);
                Debug.logWarning(e);
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

        boolean finished = true;
        Iterator methodOpsIter = methodOperations.iterator();
        while (methodOpsIter.hasNext()) {
            MethodOperation methodOperation = (MethodOperation) methodOpsIter.next();
            if (!methodOperation.exec(methodContext)) {
                finished = false;
                break;
            }
        }

        //declare errorMsg here just in case transaction ops fail
        String errorMsg = "";

        //if beganTransaction and finished commit here
        if (beganTransaction && finished) {
            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericTransactionException e) {
                String errMsg = "Error trying to commit transaction, could not process method: " + e.getMessage();
                errorMsg += errMsg + "<br>";
                Debug.logWarning(errMsg);
                Debug.logWarning(e);
            }
        }

        //if beganTransaction and NOT finished rollback here
        if (beganTransaction && !finished) {
            try {
                TransactionUtil.rollback(beganTransaction);
            } catch (GenericTransactionException e) {
                String errMsg = "Error trying to rollback transaction, could not process method: " + e.getMessage();
                errorMsg += errMsg + "<br>";
                Debug.logWarning(errMsg);
                Debug.logWarning(e);
            }
        }

        if (methodContext.getMethodType() == MethodContext.EVENT) {
            String tempErrorMsg = (String) methodContext.getEnv(eventErrorMessageName);
            if (tempErrorMsg != null && tempErrorMsg.length() > 0) {
                errorMsg += tempErrorMsg;
                methodContext.getRequest().setAttribute(SiteDefs.ERROR_MESSAGE, errorMsg);
            }

            String eventMsg = (String) methodContext.getEnv(eventEventMessageName);
            if (eventMsg != null && eventMsg.length() > 0) {
                methodContext.getRequest().setAttribute(SiteDefs.EVENT_MESSAGE, eventMsg);
            }

            String response = (String) methodContext.getEnv(eventResponseCodeName);
            if (response == null || response.length() == 0) {
                Debug.logWarning("No response code string found, assuming success");
                response = defaultSuccessCode;
            }
            return response;
        } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
            String tempErrorMsg = (String) methodContext.getEnv(serviceErrorMessageName);
            if (tempErrorMsg != null && tempErrorMsg.length() > 0) {
                errorMsg += tempErrorMsg;
                methodContext.putResult(ModelService.ERROR_MESSAGE, errorMsg);
            }

            List errorMsgList = (List) methodContext.getEnv(serviceErrorMessageListName);
            if (errorMsgList != null && errorMsgList.size() > 0) {
                methodContext.putResult(ModelService.ERROR_MESSAGE_LIST, errorMsgList);
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
                Debug.logWarning("No response code string found, assuming success");
                response = defaultSuccessCode;
            }
            methodContext.putResult(ModelService.RESPONSE_MESSAGE, response);
            return null;
        } else {
            return defaultSuccessCode;
        }
    }

    void readOperations(Element simpleMethodElement) {
        List operationElements = UtilXml.childElementList(simpleMethodElement, null);
        if (operationElements != null && operationElements.size() > 0) {
            Iterator operElemIter = operationElements.iterator();
            while (operElemIter.hasNext()) {
                Element curOperElem = (Element) operElemIter.next();
                String nodeName = curOperElem.getNodeName();

                if ("call-map-processor".equals(nodeName)) {
                    methodOperations.add(new CallSimpleMapProcessor(curOperElem, this));
                } else if ("check-errors".equals(nodeName)) {
                    methodOperations.add(new CheckErrors(curOperElem, this));
                } else if ("call-service".equals(nodeName)) {
                    methodOperations.add(new CallService(curOperElem, this));
                } else if ("field-to-request".equals(nodeName)) {
                    methodOperations.add(new FieldToRequest(curOperElem, this));
                } else if ("field-to-session".equals(nodeName)) {
                    methodOperations.add(new FieldToSession(curOperElem, this));
                } else {
                    Debug.logWarning("Operation element \"" + nodeName + "\" no recognized");
                }
            }
        }
    }
}
