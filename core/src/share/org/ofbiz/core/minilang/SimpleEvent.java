
package org.ofbiz.core.minilang;

import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.service.*;

import org.ofbiz.core.minilang.operation.*;

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
        if (simpleEvent == null) {
            throw new MiniLangException("Could not find SimpleEvent " + eventName + " in XML document in resource: " + xmlURL.toString());
        }
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
    public String shortDescription;
    String defaultErrorCode;
    String defaultSuccessCode;

    String parameterMapName;
    String requestName;
    public String responseCodeName;
    public String errorMessageName;
    public String eventMessageName;

    boolean loginRequired = true;
    boolean useTransaction = true;

    public SimpleEvent(Element simpleEventElement) {
        eventName = simpleEventElement.getAttribute("event-name");
        shortDescription = simpleEventElement.getAttribute("short-description");

        defaultErrorCode = simpleEventElement.getAttribute("default-error-code");
        if (defaultErrorCode == null || defaultErrorCode.length() == 0)
            defaultErrorCode = "error";
        defaultSuccessCode = simpleEventElement.getAttribute("default-success-code");
        if (defaultSuccessCode == null || defaultSuccessCode.length() == 0)
            defaultSuccessCode = "success";

        parameterMapName = simpleEventElement.getAttribute("parameter-map-name");
        if (parameterMapName == null || parameterMapName.length() == 0)
            parameterMapName = "parameters";

        requestName = simpleEventElement.getAttribute("request-name");
        if (requestName == null || requestName.length() == 0)
            requestName = "_request_";

        responseCodeName = simpleEventElement.getAttribute("response-code-name");
        if (responseCodeName == null || responseCodeName.length() == 0)
            responseCodeName = "_response_code_";

        errorMessageName = simpleEventElement.getAttribute("error-message-name");
        if (errorMessageName == null || errorMessageName.length() == 0)
            errorMessageName = "_error_message_";

        eventMessageName = simpleEventElement.getAttribute("event-message-name");
        if (eventMessageName == null || eventMessageName.length() == 0)
            eventMessageName = "_event_message_";

        loginRequired = !"false".equals(simpleEventElement.getAttribute("login-required"));
        useTransaction = !"false".equals(simpleEventElement.getAttribute("use-transaction"));

        readOperations(simpleEventElement);
    }

    public String getEventName() {
        return eventName;
    }

    public String exec(HttpServletRequest request, ClassLoader loader) {
        Map env = new HashMap();
        env.put(requestName, request);
        env.put(parameterMapName, UtilMisc.getParameterMap(request));

        if (loginRequired) {
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);
            if (userLogin == null) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "You must be logged in to complete the " + shortDescription + " process.");
                return defaultErrorCode;
            }
        }

        //if using transaction, try to start here
        boolean beganTransaction = false;
        if (useTransaction) {
            try {
                beganTransaction = TransactionUtil.begin();
            } catch (GenericTransactionException e) {
                String errMsg = "Error trying to begin transaction, could not process event: " + e.getMessage();
                Debug.logWarning("[SimpleEvent.exec] " + errMsg);
                Debug.logWarning(e);
                request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
                return defaultErrorCode;
            }
        }

        boolean finished = true;
        Iterator eventOpsIter = eventOperations.iterator();
        while (eventOpsIter.hasNext()) {
            EventOperation eventOperation = (EventOperation) eventOpsIter.next();
            if (!eventOperation.exec(env, request, loader)) {
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
                String errMsg = "Error trying to commit transaction, could not process event: " + e.getMessage();
                errorMsg += errMsg + "<br>";
                Debug.logWarning("[SimpleEvent.exec] " + errMsg);
                Debug.logWarning(e);
            }
        }

        //if beganTransaction and NOT finished rollback here
        if (beganTransaction && !finished) {
            try {
                TransactionUtil.rollback(beganTransaction);
            } catch (GenericTransactionException e) {
                String errMsg = "Error trying to rollback transaction, could not process event: " + e.getMessage();
                errorMsg += errMsg + "<br>";
                Debug.logWarning("[SimpleEvent.exec] " + errMsg);
                Debug.logWarning(e);
            }
        }

        String tempErrorMsg = (String) env.get(errorMessageName);
        if (tempErrorMsg != null && tempErrorMsg.length() > 0) {
            errorMsg += tempErrorMsg;
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errorMsg);
        }

        String eventMsg = (String) env.get(eventMessageName);
        if (eventMsg != null && eventMsg.length() > 0) {
            request.setAttribute(SiteDefs.EVENT_MESSAGE, eventMsg);
        }

        String response = (String) env.get(responseCodeName);
        if (response == null || response.length() == 0) {
            Debug.logWarning("[SimpleEvent.exec] No response code string found, assuming success");
            response = defaultSuccessCode;
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

                if ("simple-map-processor".equals(nodeName)) {
                    eventOperations.add(new CallSimpleMapProcessor(curOperElem, this));
                } else if ("check-errors".equals(nodeName)) {
                    eventOperations.add(new CheckErrors(curOperElem, this));
                } else if ("service".equals(nodeName)) {
                    eventOperations.add(new CallService(curOperElem, this));
                } else if ("field-to-request".equals(nodeName)) {
                    eventOperations.add(new FieldToRequest(curOperElem, this));
                } else if ("field-to-session".equals(nodeName)) {
                    eventOperations.add(new FieldToSession(curOperElem, this));
                } else {
                    Debug.logWarning("[SimpleEvent.StringProcess.readOperations] Operation element \"" + nodeName + "\" no recognized");
                }
            }
        }

    }
}
