package org.ofbiz.core.minilang;

import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;

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
    
    public static void runSimpleEvent(String xmlResource, String eventName, HttpServletRequest request) throws MiniLangException {
        runSimpleEvent(xmlResource, eventName, request, null);
    }

    public static void runSimpleEvent(String xmlResource, String eventName, HttpServletRequest request, ClassLoader loader) throws MiniLangException {
        URL xmlURL = UtilURL.fromResource(xmlResource, loader);
        if (xmlURL == null) {
            throw new MiniLangException("Could not find SimpleEvent XML document in resource: " + xmlResource);
        }
                    
        runSimpleEvent(xmlURL, eventName, request, loader);
    }

    public static void runSimpleEvent(URL xmlURL, String eventName, HttpServletRequest request, ClassLoader loader) throws MiniLangException {
        if (loader == null)
            loader = Thread.currentThread().getContextClassLoader();
        
        SimpleEvent simpleEvent = getSimpleEvent(xmlURL, eventName);
        simpleEvent.exec(request, loader);
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
    
    List eventOperations = new LinkedList();
    String eventName;
    String shortDescription;
    String requestName;
    String responseCodeName;
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
        
        this.loginRequired = !"false".equals(simpleEventElement.getAttribute("login-required"));
        
        readOperations(simpleEventElement);
    }

    public String getEventName() {
        return eventName;
    }

    public void exec(HttpServletRequest request, ClassLoader loader) {
        Map env = new HashMap();
        env.put(requestName, request);
        
        Iterator eventOpsIter = eventOperations.iterator();
        while (eventOpsIter.hasNext()) {
            EventOperation eventOperation = (EventOperation) eventOpsIter.next();
            if (!eventOperation.exec(env, request, loader))
                break;
        }
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
        String inMapName;
        String outMapName;
        String errorListName;
        
        public SimpleMapProcessor(Element element, SimpleEvent simpleEvent) {
            super(element, simpleEvent);
            xmlResource = element.getAttribute("xml-resource");
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
            
            
            return true;
        }
    }

    /** An event operation that checks a message list and may introduce a return code and stop the event */
    public static class CheckErrors extends EventOperation {
        String errorListName;
        String responseCode;
        
        public CheckErrors(Element element, SimpleEvent simpleEvent) {
            super(element, simpleEvent);
            responseCode = element.getAttribute("response-code");
            if (responseCode == null || responseCode.length() == 0)
                responseCode = "error";
            errorListName = element.getAttribute("error-list-name");
            if (errorListName == null || errorListName.length() == 0)
                errorListName = "_error_list_";
        }
        
        public boolean exec(Map env, HttpServletRequest request, ClassLoader loader) {
            List messages = (List) env.get(errorListName);
            if (messages != null && messages.size() > 0) {
                //String errMsg = "<b>The following errors occured:</b><br><ul>" + ServiceUtil.makeMessageList(messages, "<li>", "</li>") + "</ul>";
                //request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
                
                env.put(simpleEvent.responseCodeName, responseCode);
                return false;
            }
            
            return true;
        }
    }
    
    /** An event operation that creates a local map from the request parameters */
    public static class Service extends EventOperation {
        String mapName;
        
        public Service(Element element, SimpleEvent simpleEvent) {
            super(element, simpleEvent);
            this.mapName = element.getAttribute("map-name");
        }
        
        public boolean exec(Map env, HttpServletRequest request, ClassLoader loader) {
            env.put(mapName, UtilMisc.getParameterMap(request));
            return true;
        }
    }
    
    /** Simple class to wrap messages that come either a straight string or a properties file */
    public static class FlexibleMessage {
        String message = null;
        String propertyResource = null;
        boolean isProperty = false;

        public FlexibleMessage(Element element) {
            if (element.getAttribute("resource") != null) {
                this.propertyResource = element.getAttribute("resource");
                this.message = element.getAttribute("property");
                this.isProperty = true;
            } else if (UtilXml.elementValue(element) != null) {
                this.message = UtilXml.elementValue(element);
                this.isProperty = false;
            }
        }

        public String getMessage(ClassLoader loader) {
            if (!isProperty && message != null) {
                return message;
                //Debug.logInfo("[FlexibleMessage.getMessage] Adding message: " + message);
            } else if (isProperty && propertyResource != null && message != null) {
                String propMsg = UtilProperties.getPropertyValue(UtilURL.fromResource(propertyResource, loader), message);
                if (propMsg == null || propMsg.length() == 0)
                    return "Simple Map Processing error occurred, but no message was found, sorry.";
                else
                    return propMsg;
                //Debug.logInfo("[FlexibleMessage.getMessage] Adding property message: " + propMsg);
            } else {
                return "Simple Map Processing error occurred, but no message was found, sorry.";
                //Debug.logInfo("[FlexibleMessage.getMessage] ERROR: No message found");
            }
        }
    }
}
