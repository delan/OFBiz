package org.ofbiz.core.minilang;

import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.service.*;

/**
 * <p><b>Title:</b> SimpleEvent Mini Language
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    December 29, 2001
 *@version    1.0
 */
public class SimpleEvent {
    protected static UtilCache simpleEventsCache = new UtilCache("SimpleEvents", 0, 0);
    
    public static String runSimpleEvent(String xmlResource, String eventName, HttpServletRequest request) throws MiniLangException {
        return runSimpleEvent(xmlResource, eventName, request, null);
    }

    public static String runSimpleEvent(String xmlResource, String eventName, HttpServletRequest request, ClassLoader loader) throws MiniLangException {
        URL xmlURL = UtilURL.fromResource(xmlResource, loader);
        if (xmlURL == null) {
            throw new MiniLangException("Could not find SimpleEvent XML document in resource: " + xmlResource);
        }
                    
        return runSimpleEvent(xmlURL, eventName, request, loader);
    }

    public static String runSimpleEvent(URL xmlURL, String eventName, HttpServletRequest request, ClassLoader loader) throws MiniLangException {
        if (loader == null)
            loader = Thread.currentThread().getContextClassLoader();
        
        SimpleEvent simpleEvent = getSimpleEvent(xmlURL, eventName);
        return simpleEvent.exec(request, loader);
    }

    protected static SimpleEvent getSimpleEvent(URL xmlURL, String eventName) throws MiniLangException {
        Map simpleEvents = (Map) simpleEventsCache.get(xmlURL);
        if (simpleEvents == null) {
            synchronized (SimpleEvent.class) {
                simpleEvents = (Map) simpleEventsCache.get(xmlURL);
                if (simpleEvents == null) {
                    simpleEvents = new HashMap();
                    
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
                        throw new MiniLangException("Could not find SimpleEvent XML document: " + xmlURL.toString());
                    }
                    
                    Element rootElement = document.getDocumentElement();
                    List simpleEventElements = UtilXml.childElementList(rootElement, "simple-event");
                    
                    Iterator simpleEventIter = simpleEventElements.iterator();
                    while (simpleEventIter.hasNext()) {
                        Element simpleEventElement = (Element) simpleEventIter.next();
                        SimpleEvent simpleEvent = new SimpleEvent(simpleEventElement);
                        simpleEvents.put(simpleEvent.getEventName(), simpleEvent);
                    }
                    
                    //put it in the cache
                    simpleEventsCache.put(xmlURL, simpleEvents);
                }
            }
        }
        
        return (SimpleEvent) simpleEvents.get(eventName);
    }

    // Member fields begin here...
    List eventOperations = new LinkedList();
    String eventName;
    String shortDescription;
    String requestName;
    String responseCodeName;
    String errorMessageName;
    String eventMessageName;
    
    boolean loginRequired = true;

    public SimpleEvent(Element simpleEventElement) {
        eventName = simpleEventElement.getAttribute("event-name");
        shortDescription = simpleEventElement.getAttribute("short-description");

        requestName = simpleEventElement.getAttribute("request-name");
        if(requestName == null || requestName.length() == 0)
            requestName = "_request_";

        responseCodeName = simpleEventElement.getAttribute("response-code-name");
        if(responseCodeName == null || responseCodeName.length() == 0)
            responseCodeName = "_response_code_";
        
        errorMessageName = simpleEventElement.getAttribute("error-message-name");
        if(errorMessageName == null || errorMessageName.length() == 0)
            errorMessageName = "_error_message_";
        
        eventMessageName = simpleEventElement.getAttribute("event-message-name");
        if(eventMessageName == null || eventMessageName.length() == 0)
            eventMessageName = "_event_message_";
        
        loginRequired = !"false".equals(simpleEventElement.getAttribute("login-required"));
        
        readOperations(simpleEventElement);
    }

    public String getEventName() {
        return eventName;
    }

    public String exec(HttpServletRequest request, ClassLoader loader) {
        Map env = new HashMap();
        env.put(requestName, request);

        if (loginRequired) {
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);
            if (userLogin == null) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "You must be logged in to complete the " + shortDescription + " process.");
                return "error";
            }
        }

        Iterator eventOpsIter = eventOperations.iterator();
        while (eventOpsIter.hasNext()) {
            EventOperation eventOperation = (EventOperation) eventOpsIter.next();
            if (!eventOperation.exec(env, request, loader))
                break;
        }
        
        String errorMsg = (String) env.get(errorMessageName);
        if (errorMsg != null && errorMsg.length() > 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errorMsg);
        }
        
        String eventMsg = (String) env.get(eventMessageName);
        if (eventMsg != null && eventMsg.length() > 0) {
            request.setAttribute(SiteDefs.EVENT_MESSAGE, eventMsg);
        }

        String response = (String) env.get(responseCodeName);
        if (response == null || response.length() == 0) {
            Debug.logWarning("[SimpleEvent.exec] No response code string found, assuming success");
            response = "success";
        }
        return response;
    }

    void readOperations(Element simpleEventElement) {
        List operationElements = UtilXml.childElementList(simpleEventElement, null);
        if (operationElements != null && operationElements.size() > 0) {
            Iterator operElemIter = operationElements.iterator();
            while (operElemIter.hasNext()) {
                Element curOperElem = (Element) operElemIter.next();
                String nodeName = curOperElem.getNodeName();

                if ("parameter-map".equals(nodeName)) {
                    eventOperations.add(new SimpleEvent.ParameterMap(curOperElem, this));
                } else if ("simple-map-processor".equals(nodeName)) {
                    eventOperations.add(new SimpleEvent.SimpleMapProcessor(curOperElem, this));
                } else if ("check-errors".equals(nodeName)) {
                    eventOperations.add(new SimpleEvent.CheckErrors(curOperElem, this));
                } else if ("service".equals(nodeName)) {
                    eventOperations.add(new SimpleEvent.Service(curOperElem, this));
                } else {
                    Debug.logWarning("[SimpleEvent.StringProcess.readOperations] Operation element \"" + nodeName + "\" no recognized");
                }
            }
        }

    }
    
    /** A single string operation, does the specified operation on the given field */
    public static abstract class EventOperation {
        SimpleEvent simpleEvent;
        
        public EventOperation(Element element, SimpleEvent simpleEvent) {
            this.simpleEvent = simpleEvent;
        }
        
        /** Execute the operation; if false is returned then no further operations will be executed */
        public abstract boolean exec(Map env, HttpServletRequest request, ClassLoader loader);
    }
    
    /* ==================================================================== */
    /* All of the EventOperations...
    /* ==================================================================== */

    /** An event operation that creates a local map from the request parameters */
    public static class ParameterMap extends EventOperation {
        String mapName;
        
        public ParameterMap(Element element, SimpleEvent simpleEvent) {
            super(element, simpleEvent);
            mapName = element.getAttribute("map-name");
        }
        
        public boolean exec(Map env, HttpServletRequest request, ClassLoader loader) {
            env.put(mapName, UtilMisc.getParameterMap(request));
            return true;
        }
    }
    
    /** An event operation that calls a simple map processor minilang file */
    public static class SimpleMapProcessor extends EventOperation {
        String xmlResource;
        String processorName;
        String inMapName;
        String outMapName;
        String errorListName;
        
        public SimpleMapProcessor(Element element, SimpleEvent simpleEvent) {
            super(element, simpleEvent);
            xmlResource = element.getAttribute("xml-resource");
            processorName = element.getAttribute("processor-name");
            inMapName = element.getAttribute("in-map-name");
            outMapName = element.getAttribute("out-map-name");
            errorListName = element.getAttribute("error-list-name");
            if (errorListName == null || errorListName.length() == 0)
                errorListName = "_error_list_";
        }
        
        public boolean exec(Map env, HttpServletRequest request, ClassLoader loader) {
            List messages = (List) env.get(errorListName);
            if (messages == null) {
                messages = new LinkedList();
                env.put(errorListName, messages);
            }
            
            Map inMap = (Map) env.get(inMapName);
            if (inMap == null) {
                inMap = new HashMap();
                env.put(inMapName, inMap);
            }
            
            Map outMap = (Map) env.get(outMapName);
            if (outMap == null) {
                outMap = new HashMap();
                env.put(outMapName, outMap);
            }
            
            try {
                org.ofbiz.core.minilang.SimpleMapProcessor.runSimpleMapProcessor(xmlResource, processorName, inMap, outMap, messages, request.getLocale());
            } catch (MiniLangException e) {
                messages.add("Error running SimpleMapProcessor in XML file \"" + xmlResource + "\": " + e.toString());
            }
            
            return true;
        }
    }

    /** An event operation that checks a message list and may introduce a return code and stop the event */
    public static class CheckErrors extends EventOperation {
        String errorListName;
        String errorCode;
        
        FlexibleMessage errorPrefix;
        FlexibleMessage errorSuffix;
        FlexibleMessage messagePrefix;
        FlexibleMessage messageSuffix;
        
        public CheckErrors(Element element, SimpleEvent simpleEvent) {
            super(element, simpleEvent);
            errorCode = element.getAttribute("error-code");
            if (errorCode == null || errorCode.length() == 0)
                errorCode = "error";
            errorListName = element.getAttribute("error-list-name");
            if (errorListName == null || errorListName.length() == 0)
                errorListName = "_error_list_";

            errorPrefix = new FlexibleMessage(UtilXml.firstChildElement(element, "error-prefix"));
            errorSuffix = new FlexibleMessage(UtilXml.firstChildElement(element, "error-suffix"));
            messagePrefix = new FlexibleMessage(UtilXml.firstChildElement(element, "message-prefix"));
            messageSuffix = new FlexibleMessage(UtilXml.firstChildElement(element, "message-suffix"));
        }
        
        public boolean exec(Map env, HttpServletRequest request, ClassLoader loader) {
            List messages = (List) env.get(errorListName);
            if (messages != null && messages.size() > 0) {
                String errMsg = errorPrefix.getMessage(loader) + 
                        ServiceUtil.makeMessageList(messages, messagePrefix.getMessage(loader), messageSuffix.getMessage(loader)) + 
                        errorSuffix.getMessage(loader);
                env.put(simpleEvent.errorMessageName, errMsg);
                
                env.put(simpleEvent.responseCodeName, errorCode);
                return false;
            }
            
            return true;
        }
    }

    /** An event operation that creates a local map from the request parameters */
    public static class Service extends EventOperation {
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
        
        Map resultToRequest = new HashMap();
        Map resultToSession = new HashMap();
        
        public Service(Element element, SimpleEvent simpleEvent) {
            super(element, simpleEvent);
            serviceName = element.getAttribute("service-name");
            inMapName = element.getAttribute("in-map-name");
            includeUserLogin = !"false".equals(element.getAttribute("include-user-login"));
            errorCode = element.getAttribute("error-code");
            if (errorCode == null || errorCode.length() == 0)
                errorCode = "error";
            
            successCode = element.getAttribute("success-code");
            if (successCode == null || successCode.length() == 0)
                successCode = "success";

            errorPrefix = new FlexibleMessage(UtilXml.firstChildElement(element, "error-prefix"));
            errorSuffix = new FlexibleMessage(UtilXml.firstChildElement(element, "error-suffix"));
            successPrefix = new FlexibleMessage(UtilXml.firstChildElement(element, "success-prefix"));
            successSuffix = new FlexibleMessage(UtilXml.firstChildElement(element, "success-suffix"));
            messagePrefix = new FlexibleMessage(UtilXml.firstChildElement(element, "message-prefix"));
            messageSuffix = new FlexibleMessage(UtilXml.firstChildElement(element, "message-suffix"));
            defaultMessage = new FlexibleMessage(UtilXml.firstChildElement(element, "default-message"));
            
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
        }
        
        public boolean exec(Map env, HttpServletRequest request, ClassLoader loader) {
            LocalDispatcher dispatcher = (LocalDispatcher) request.getSession().getServletContext().getAttribute("dispatcher");
            
            Map inMap = (Map) env.get(inMapName);
            if (inMap == null) {
                inMap = new HashMap();
                env.put(inMapName, inMap);
            }
            
            // invoke the service
            Map result = null;
            if (includeUserLogin) {
                GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);
                if (userLogin != null)
                    inMap.put("userLogin", userLogin);
            }
            try {
                result = dispatcher.runSync(serviceName, inMap);
            } catch (GenericServiceException e) {
                Debug.logError(e);
                env.put(simpleEvent.errorMessageName, "ERROR: Could not complete " + simpleEvent.shortDescription + " process (problem invoking the " + serviceName + " service: " + e.getMessage() + ")");
                env.put(simpleEvent.responseCodeName, errorCode);
                return false;
            }

            if (resultToRequest.size() > 0) {
                Iterator iter = resultToRequest.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    request.setAttribute((String) entry.getKey(), result.get(entry.getValue()));
                }
            }
            
            if (resultToSession.size() > 0) {
                Iterator iter = resultToSession.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    request.getSession().setAttribute((String) entry.getKey(), result.get(entry.getValue()));
                }
            }
            
            String errorPrefixStr = errorPrefix.getMessage(loader);
            String errorSuffixStr = errorSuffix.getMessage(loader);
            String successPrefixStr = successPrefix.getMessage(loader);
            String successSuffixStr = successSuffix.getMessage(loader);
            String messagePrefixStr = messagePrefix.getMessage(loader);
            String messageSuffixStr = messageSuffix.getMessage(loader);

            String errorMessage = ServiceUtil.makeErrorMessage(result, messagePrefixStr, messageSuffixStr, errorPrefixStr, errorSuffixStr);
            if (UtilValidate.isNotEmpty(errorMessage))
                env.put(simpleEvent.errorMessageName, errorMessage);

            String successMessage = ServiceUtil.makeSuccessMessage(result, messagePrefixStr, messageSuffixStr, successPrefixStr, successSuffixStr);
            if (UtilValidate.isNotEmpty(successMessage))
                env.put(simpleEvent.eventMessageName, successMessage);

            String defaultMessageStr = defaultMessage.getMessage(loader);
            if (UtilValidate.isEmpty(errorMessage) && UtilValidate.isEmpty(successMessage) && UtilValidate.isNotEmpty(defaultMessageStr))
                env.put(simpleEvent.eventMessageName, defaultMessageStr);

            //TODO: what to do about moving stuff from the result to request attributes?
            //request.setAttribute("workEffortId", result.get("workEffortId"));

            // handle the result
            String responseCode = result.containsKey(ModelService.RESPONSE_MESSAGE) ? (String)result.get(ModelService.RESPONSE_MESSAGE) : successCode;
            env.put(simpleEvent.responseCodeName, responseCode);

            if (successCode.equals(responseCode))
                return true;
            else
                return false;
        }
    }
    
    /** Simple class to wrap messages that come either a straight string or a properties file */
    public static class FlexibleMessage {
        String message = null;
        String propertyResource = null;
        boolean isProperty = false;

        public FlexibleMessage(Element element) {
            String resAttr = element.getAttribute("resource");
            String propAttr = element.getAttribute("property");
            String elVal = UtilXml.elementValue(element);
            if (resAttr != null && resAttr.length() > 0) {
                propertyResource = resAttr;
                message = propAttr;
                isProperty = true;
            } else if (elVal != null && elVal.length() > 0) {
                message = elVal;
                isProperty = false;
            }
        }

        public String getMessage(ClassLoader loader) {
            //Debug.logInfo("[FlexibleMessage.getMessage] isProperty: " + isProperty + ", message: " + message + ", propertyResource: " + propertyResource);
            if (!isProperty && message != null) {
                //Debug.logInfo("[FlexibleMessage.getMessage] Adding message: " + message);
                return message;
            } else if (isProperty && propertyResource != null && message != null) {
                String propMsg = UtilProperties.getPropertyValue(UtilURL.fromResource(propertyResource, loader), message);
                //Debug.logInfo("[FlexibleMessage.getMessage] Adding property message: " + propMsg);
                if (propMsg == null || propMsg.length() == 0)
                    return "Simple Map Processing error occurred, but no message was found, sorry.";
                else
                    return propMsg;
            } else {
                return "";
                //Debug.logInfo("[FlexibleMessage.getMessage] No message found, returning empty string");
            }
        }
    }
}
