/*
 * $Id$
 * $Log$
 * Revision 1.2  2001/10/01 17:12:37  azeneski
 * Updated ConfigXMLReader w/ simplified XML files.
 *
 * Revision 1.1  2001/09/28 22:56:44  jonesde
 * Big update for fromDate PK use, organization stuff
 *
 * Revision 1.3  2001/08/25 17:29:11  azeneski
 * Started migrating Debug.log to Debug.logInfo and Debug.logError
 *
 * Revision 1.2  2001/07/23 21:20:57  azeneski
 * Added support for HTTP GET/POST events in job scheduler.
 * Fixed a bug in the XML parser which caused the parser to die
 * when a empty element was found.
 *
 * Revision 1.1  2001/07/19 14:15:59  azeneski
 * Moved org.ofbiz.core.control.RequestXMLReader to org.ofbiz.core.util.ConfigXMLReader
 * ConfigXMLReader is now used for all config files, not just the request mappings.
 * Updated RequestManager to use this new class.
 * Added getRequestManager() method to RequestHandler.
 *
 */

package org.ofbiz.core.util;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.*;
import org.w3c.dom.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.net.URL;

import org.ofbiz.core.util.Debug;

/**
 * <p><b>Title:</b> ConfigXMLReader.java
 * <p><b>Description:</b> Reads and parses the XML site config files.
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
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
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on June 29, 2001, 6:18 PM
 */
public class ConfigXMLReader {
    
    /** Site Config Variables */
    public static final String DEFAULT_ERROR_PAGE = "errorpage";
    public static final String SITE_OWNER = "owner";
    public static final String SECURITY_CLASS = "security-class";
    public static final String PREPROCESSOR = "preprocessor";
    public static final String POSTPROCESSOR = "postprocessor";
    
    /** URI Config Variables */
    public static final String REQUEST_MAPPING = "request-map";
    public static final String REQUEST_URI = "uri";
    public static final String REQUEST_EDIT = "edit";
    
    public static final String REQUEST_DESCRIPTION = "description";
    public static final String ERROR_PAGE = "error";
    public static final String NEXT_PAGE = "success";
    
    public static final String SECURITY = "security";
    public static final String SECURITY_HTTPS = "https";
    public static final String SECURITY_AUTH = "auth";
    
    public static final String EVENT = "event";
    public static final String EVENT_PATH = "path";
    public static final String EVENT_TYPE = "type";
    public static final String EVENT_METHOD = "invoke";
    
    public static final String RESPONSE = "response";
    public static final String RESPONSE_NAME = "name";
    public static final String RESPONSE_TYPE = "type";
    public static final String RESPONSE_VALUE = "value";
    
    /** View Config Variables */
    public static final String VIEW_MAPPING = "view-map";
    public static final String VIEW_NAME = "name";
    public static final String VIEW_PAGE = "page";
    public static final String VIEW_DESCRIPTION = "description";
    
    /** Scheduler Config Variables */
    public static final String SCHEDULER_MAPPING = "schedule";
    public static final String SCHEDULER_JOB_NAME = "name";
    
    public static final String SCHEDULER_DURATION = "duration";
    public static final String SCHEDULER_DURATION_START = "start";
    public static final String SCHEDULER_DURATION_END = "end";
    
    public static final String SCHEDULER_INTERVAL = "interval";
    public static final String SCHEDULER_INTERVAL_TYPE = "type";
    public static final String SCHEDULER_INTERVAL_VALUE = "value";
    public static final String SCHEDULER_INTERVAL_REPEAT = "repeat";
    
    public static final String SCHEDULER_PARAMETERS = "parameter";
    public static final String SCHEDULER_HEADERS = "header";
    
    /** Loads the XML file and returns the root element */
    public static Element loadDocument(String location) {
        Document document = null;
        try {
            URL url = new URL(location);
            InputSource input = new InputSource(url.openStream());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            DocumentBuilder parser = factory.newDocumentBuilder();
            document = parser.parse(input);
            Element rootElement = document.getDocumentElement();
            //rootElement.normalize();
            //Debug.logInfo("Loaded XML Config - " + location);
            return rootElement;
        }
        catch ( Exception e ) {
            e.printStackTrace();
            //Debug.logError(e,"ConfigXMLReader Error");
        }
        
        return null;
    }
    
    /** Gets a HashMap of request mappings. */
    public static HashMap getRequestMap(String xml) {
        HashMap map = new HashMap();
        Element root = loadDocument(xml);
        if ( root == null )
            return map;
        
        NodeList list = root.getElementsByTagName(REQUEST_MAPPING);
        for ( int rootCount = 0; rootCount < list.getLength(); rootCount++ ) {
            // Create a URI-MAP for each element found.
            HashMap uriMap = new HashMap();
            // Get the node.
            Node node = list.item(rootCount);
            // Make sure we are an element.
            if ( node instanceof Element ) {
                // Get the URI info.
                Element mapping = (Element) node;
                String uri = mapping.getAttribute(REQUEST_URI);
                String edit = mapping.getAttribute(REQUEST_EDIT);
                if ( edit == null || edit.equals(""))
                    edit = "true";
                if ( uri != null ) {
                    uriMap.put(REQUEST_URI, uri);
                    uriMap.put(REQUEST_EDIT, edit);
                }
                
                // Check for security.
                NodeList securityList = mapping.getElementsByTagName(SECURITY);
                if ( securityList.getLength() > 0 ) {
                    Node securityNode = securityList.item(0);  // There should be only one.
                    if ( securityNode instanceof Element ) {       // We must be an element.
                        Element security = (Element) securityNode;
                        String securityHttps = security.getAttribute(SECURITY_HTTPS);
                        String securityAuth =  security.getAttribute(SECURITY_AUTH);
                        uriMap.put(SECURITY_HTTPS, securityHttps);
                        uriMap.put(SECURITY_AUTH, securityAuth);
                    }
                }
                
                // Check for an event.
                NodeList eventList = mapping.getElementsByTagName(EVENT);
                if ( eventList.getLength() > 0 ) {
                    Node eventNode = eventList.item(0);  // There should be only one.
                    if ( eventNode instanceof Element ) {   // We must be an element.
                        Element event = (Element) eventNode;
                        String type = event.getAttribute(EVENT_TYPE);
                        String path = event.getAttribute(EVENT_PATH);
                        String invoke = event.getAttribute(EVENT_METHOD);
                        uriMap.put(EVENT_TYPE, type);
                        uriMap.put(EVENT_PATH, path);
                        uriMap.put(EVENT_METHOD, invoke);
                    }
                }
                
                // Check for a description.
                NodeList descList = mapping.getElementsByTagName(REQUEST_DESCRIPTION);
                if ( descList.getLength() > 0 ) {
                    Node descNode = descList.item(0);   // There should be only one.
                    if ( descNode instanceof Element ) {   // We must be an element.
                        NodeList children = descNode.getChildNodes();
                        if ( children.getLength() > 0 ) {
                            Node cdata = children.item(0);  // Just get the first one.
                            String description = cdata.getNodeValue();
                            if ( description != null )
                                description = description.trim();
                            else
                                description = "";
                            uriMap.put(REQUEST_DESCRIPTION, description);
                        }
                    }
                }
                else {
                    uriMap.put(REQUEST_DESCRIPTION,"");
                }
                
                // Get the response(s).
                NodeList respList = mapping.getElementsByTagName(RESPONSE);
                for ( int respCount = 0; respCount < respList.getLength(); respCount++ ) {
                    Node responseNode = respList.item(respCount);
                    if ( responseNode instanceof Element ) {
                        Element response = (Element) responseNode;
                        String name = response.getAttribute(RESPONSE_NAME);
                        String type = response.getAttribute(RESPONSE_TYPE);
                        String value = response.getAttribute(RESPONSE_VALUE);
                        uriMap.put(name, type + ":" + value);
                    }
                }
                
                if ( uri != null )
                    map.put(uri, uriMap);
            }
            
        }
        Debug.logInfo("RequestMap Created: (" + map.size() + ") records.");
        return map;
    }
    
    /** Gets a HashMap of view mappings. */
    public static HashMap getViewMap(String xml) {
        HashMap map = new HashMap();
        Element root = loadDocument(xml);
        if ( root == null )
            return map;
        
        NodeList list = root.getElementsByTagName(VIEW_MAPPING);
        for ( int rootCount = 0; rootCount < list.getLength(); rootCount++ ) {
            // Create a URI-MAP for each element found.
            HashMap uriMap = new HashMap();
            // Get the node.
            Node node = list.item(rootCount);
            // Make sure we are an element.
            if ( node instanceof Element ) {
                // Get the view info.
                Element mapping = (Element) node;
                String name = mapping.getAttribute(VIEW_NAME);
                String page = mapping.getAttribute(VIEW_PAGE);
                uriMap.put(VIEW_NAME, name);
                uriMap.put(VIEW_PAGE, page);
                
                // Check for a description.
                NodeList descList = mapping.getElementsByTagName(VIEW_DESCRIPTION);
                if ( descList.getLength() > 0 ) {
                    Node descNode = descList.item(0);   // There should be only one.
                    if ( descNode instanceof Element ) {   // We must be an element.
                        NodeList children = descNode.getChildNodes();
                        if ( children.getLength() > 0 ) {
                            Node cdata = children.item(0);  // Just get the first one.
                            String description = cdata.getNodeValue();
                            if ( description != null )
                                description = description.trim();
                            else
                                description = "";
                            uriMap.put(VIEW_DESCRIPTION, description);
                        }
                    }
                }
                else {
                    uriMap.put(VIEW_DESCRIPTION,"");
                }
                
                if ( name != null )
                    map.put(name, uriMap);
            }
        }
        Debug.logInfo("ViewMap Created: (" + map.size() + ") records.");
        return map;
    }
    
    /** Gets a HashMap of site configuration variables. */
    public static HashMap getConfigMap(String xml) {
        HashMap map = new HashMap();
        Element root = loadDocument(xml);
        NodeList list = null;
        
        if ( root != null ) {
            // default error page
            list = root.getElementsByTagName(DEFAULT_ERROR_PAGE);
            if ( list.getLength() > 0 ) {
                Node node = list.item(0);
                NodeList children = node.getChildNodes();
                Node child = children.item(0);
                if ( child.getNodeName() != null )
                    map.put(DEFAULT_ERROR_PAGE,child.getNodeValue());
            }
            list = null;
            // site owner
            list = root.getElementsByTagName(SITE_OWNER);
            if ( list.getLength() > 0 ) {
                Node node = list.item(0);
                NodeList children = node.getChildNodes();
                Node child = children.item(0);
                if ( child.getNodeName() != null )
                    map.put(SITE_OWNER,child.getNodeValue());
            }
            list = null;
            // security class
            list = root.getElementsByTagName(SECURITY_CLASS);
            if ( list.getLength() > 0 ) {
                Node node = list.item(0);
                NodeList children = node.getChildNodes();
                Node child = children.item(0);
                if ( child.getNodeName() != null )
                    map.put(SECURITY_CLASS,child.getNodeValue());
            }
            list = null;
            // preprocessor events
            list = root.getElementsByTagName(PREPROCESSOR);
            if ( list.getLength() > 0 ) {
                ArrayList eventList = new ArrayList();
                Node node = list.item(0);
                if ( node instanceof Element ) {
                    Element nodeElement = (Element) node;
                    NodeList procEvents = nodeElement.getElementsByTagName(EVENT);
                    for ( int procCount = 0; procCount < procEvents.getLength(); procCount++ ) {
                        Node eventNode = procEvents.item(procCount);
                        if ( eventNode instanceof Element ) {
                            Element event = (Element) eventNode;
                            String type = event.getAttribute(EVENT_TYPE);
                            String path = event.getAttribute(EVENT_PATH);
                            String invoke = event.getAttribute(EVENT_METHOD);
                            
                            HashMap eventMap = new HashMap();
                            eventMap.put(EVENT_TYPE, type);
                            eventMap.put(EVENT_PATH, path);
                            eventMap.put(EVENT_METHOD, invoke);
                            eventList.add(eventMap);
                        }
                    }
                }
                map.put(PREPROCESSOR,eventList);
            }
            list = null;
            // postprocessor events
            list = root.getElementsByTagName(POSTPROCESSOR);
            if ( list.getLength() > 0 ) {
                ArrayList eventList = new ArrayList();
                Node node = list.item(0);
                if ( node instanceof Element ) {
                    Element nodeElement = (Element) node;
                    NodeList procEvents = nodeElement.getElementsByTagName(EVENT);
                    for ( int procCount = 0; procCount < procEvents.getLength(); procCount++ ) {
                        Node eventNode = procEvents.item(procCount);
                        if ( eventNode instanceof Element ) {
                            Element event = (Element) eventNode;
                            String type = event.getAttribute(EVENT_TYPE);
                            String path = event.getAttribute(EVENT_PATH);
                            String invoke = event.getAttribute(EVENT_METHOD);
                            
                            HashMap eventMap = new HashMap();
                            eventMap.put(EVENT_TYPE, type);
                            eventMap.put(EVENT_PATH, path);
                            eventMap.put(EVENT_METHOD, invoke);
                            eventList.add(eventMap);
                        }
                    }
                }
                map.put(POSTPROCESSOR,eventList);
            }
            list = null;
        }
        Debug.logInfo("ConfigMap Created: (" + map.size() +") records.");
        return map;
    }
    
    /** Gets a HashMap of scheduler mappings. */
    public static HashMap getSchedulerMap(String xml) {
        HashMap map = new HashMap();
        Element root = loadDocument(xml);
        if ( root != null ) {
            // Schedule elements.
            NodeList list = root.getElementsByTagName(SCHEDULER_MAPPING);
            for ( int rootCount = 0; rootCount < list.getLength(); rootCount++ ) {
                HashMap mainMap = new HashMap();
                Node node = list.item(rootCount);
                if ( node instanceof Element ) {
                    Element element = (Element) node;
                    String jobName = element.getAttribute(SCHEDULER_JOB_NAME);
                    mainMap.put(SCHEDULER_JOB_NAME,jobName);
                    // Get duration.
                    NodeList durationList = element.getElementsByTagName(SCHEDULER_DURATION);
                    if ( durationList.getLength() > 0 ) {
                        Node durationNode = durationList.item(0);
                        if ( durationNode instanceof Element ) {
                            Element duration = (Element) durationNode;
                            String startTime = duration.getAttribute(SCHEDULER_DURATION_START);
                            String endTime = duration.getAttribute(SCHEDULER_DURATION_END);
                            mainMap.put(SCHEDULER_DURATION_START,startTime);
                            mainMap.put(SCHEDULER_DURATION_END,endTime);
                        }
                    }
                    // Get interval.
                    NodeList intervalList = element.getElementsByTagName(SCHEDULER_INTERVAL);
                    if ( intervalList.getLength() > 0 ) {
                        Node intervalNode = intervalList.item(0);
                        if ( intervalNode instanceof Element ) {
                            Element interval = (Element) intervalNode;
                            String intervalType = interval.getAttribute(SCHEDULER_INTERVAL_TYPE);
                            String intervalValue = interval.getAttribute(SCHEDULER_INTERVAL_VALUE);
                            String intervalRepeat = interval.getAttribute(SCHEDULER_INTERVAL_REPEAT);
                            mainMap.put("i"+SCHEDULER_INTERVAL_TYPE,intervalType);
                            mainMap.put(SCHEDULER_INTERVAL_VALUE,intervalValue);
                            mainMap.put(SCHEDULER_INTERVAL_REPEAT,intervalRepeat);
                        }
                    }
                    // Get event.
                    NodeList eventList = element.getElementsByTagName(EVENT);
                    if ( eventList.getLength() > 0 ) {
                        Node eventNode = eventList.item(0);
                        if ( eventNode instanceof Element ) {
                            Element event = (Element) eventNode;
                            String eventType = event.getAttribute(EVENT_TYPE);
                            String eventPath = event.getAttribute(EVENT_PATH);
                            String eventInvoke = event.getAttribute(EVENT_METHOD);
                            mainMap.put(EVENT_TYPE,eventType);
                            mainMap.put(EVENT_PATH,eventPath);
                            mainMap.put(EVENT_METHOD,eventInvoke);
                        }
                    }
                    // Get parameter(s).
                    HashMap paramMap = new HashMap();
                    NodeList paramList = element.getElementsByTagName(SCHEDULER_PARAMETERS);
                    for ( int paramCount = 0; paramCount < paramList.getLength(); paramCount++ ) {
                        Node paramNode = paramList.item(paramCount);
                        if ( paramNode instanceof Element ) {
                            Element param = (Element) paramNode;
                            String name = param.getAttribute("name");
                            String value = param.getAttribute("value");
                            paramMap.put(name,value);
                        }
                    }
                    mainMap.put(SCHEDULER_PARAMETERS,paramMap);
                    // Get header(s).
                    HashMap headerMap = new HashMap();
                    NodeList headerList = element.getElementsByTagName(SCHEDULER_HEADERS);
                    for ( int headerCount = 0; headerCount < headerList.getLength(); headerCount++ ) {
                        Node headerNode = headerList.item(headerCount);
                        if ( headerNode instanceof Element ) {
                            Element header = (Element) headerNode;
                            String name = header.getAttribute("name");
                            String value = header.getAttribute("value");
                            headerMap.put(name,value);
                        }
                    }
                    mainMap.put(SCHEDULER_HEADERS,headerMap);
                    
                    if ( jobName != null )
                        map.put(jobName,mainMap);
                }
            }
        }
        Debug.logInfo("SchedulerMap Created: (" + map.size() + ") records.");
        return map;
        
    }
    
    /** Not used right now */
    public static String getSubTagValue(Node node, String subTagName) {
        String returnString = "";
        if ( node != null ) {
            NodeList  children = node.getChildNodes();
            for ( int innerLoop = 0; innerLoop < children.getLength(); innerLoop++ ) {
                Node  child = children.item(innerLoop);
                if ( (child != null ) && ( child.getNodeName() != null ) && child.getNodeName().equals(subTagName ) ) {
                    Node grandChild = child.getFirstChild();
                    if ( grandChild.getNodeValue() != null )
                        return grandChild.getNodeValue();
                }
            }
        }
        return returnString;
    }
    
    public static void main(String args[]) throws Exception {
        /** Debugging */
        if ( args[0] == null ) {
            System.out.println("Please give a path to the config file you wish to test.");
            return;
        }
        System.out.println("----------------------------------");
        System.out.println("Request Mappings:");
        System.out.println("----------------------------------");
        java.util.HashMap debugMap =  getRequestMap(args[0]);
        java.util.Set debugSet = debugMap.keySet();
        java.util.Iterator i = debugSet.iterator();
        while ( i.hasNext() ) {
            Object o = i.next();
            String request = (String) o;
            HashMap thisURI = (java.util.HashMap) debugMap.get(o);
            System.out.println(request);
            java.util.Iterator list = ((java.util.Set) thisURI.keySet()).iterator();
            while ( list.hasNext() ) {
                Object lo = list.next();
                String name = (String) lo;
                String value = (String) thisURI.get(lo);
                System.out.println("\t" + name + " -> " + value);
            }
        }
        System.out.println("----------------------------------");
        System.out.println("End Request Mappings.");
        System.out.println("----------------------------------");
        /** End Debugging */
    }
    
}
