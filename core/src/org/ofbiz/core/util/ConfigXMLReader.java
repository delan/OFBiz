/*
 * $Id$
 * $Log$
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
    public static final String DATASOURCE = "datasource";
    public static final String DEFAULT_ERROR_PAGE = "errorpage";
    public static final String SITE_OWNER = "owner";
    public static final String SECURITY_CLASS = "security-class";
    
    /** URI Config Variables */
    public static final String REQUEST_MAPPING = "request-map";
    public static final String URI = "uri";
    public static final String EVENT_PATH = "event-path";
    public static final String EVENT_TYPE = "event-type";
    public static final String EVENT_METHOD = "event-invoke";
    public static final String NEXT_PAGE = "success";
    public static final String ERROR_PAGE = "error";
    public static final String REQ_HTTPS = "secure";
    public static final String REQ_AUTH = "auth";
    
    /** View Config Variables */
    public static final String VIEW_MAPPING = "view-map";
    public static final String VIEW = "view";
    public static final String MAPPED_PAGE = "mapped-page";
    
    /** Scheduler Config Variables */
    public static final String SCHEDULER_MAPPING = "schedule";
    public static final String SCHEDULER_JOB_NAME = "name";
    public static final String SCHEDULER_STARTDATE = "start-date";
    public static final String SCHEDULER_ENDDATE = "end-date";
    public static final String SCHEDULER_INTERVAL = "interval";
    public static final String SCHEDULER_INTERVAL_TYPE = "interval-type";
    public static final String SCHEDULER_EVENT_TYPE = "event-type";
    public static final String SCHEDULER_EVENT_PATH = "event-path";
    public static final String SCHEDULER_EVENT_METHOD = "event-invoke";
    public static final String SCHEDULER_REPEAT = "repeat";
    public static final String SCHEDULER_PARAMETERS = "parameter";
    public static final String SCHEDULER_HEADERS = "header";
    
    /** Loads the XML file and returns the root element */
    public static Element loadDocument(String location) {
        Document document = null;
        try {
            URL url = new URL(location);
            InputSource input = new InputSource(url.openStream());
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = parser.parse(input);
            Element rootElement = document.getDocumentElement();
            rootElement.normalize();
            Debug.logInfo("Loaded XML Config - " + location);
            return rootElement;
        }
        catch ( Exception e ) {
            Debug.logError(e,"ConfigXMLReader Error");
        }
        
        return null;
    }
    
    /** Gets a HashMap of request mappings. */
    public static HashMap getRequestMap(String xml) {
        HashMap map = new HashMap();
        Element root = loadDocument(xml);
        if ( root != null ) {
            // uri-map elements
            NodeList list = root.getElementsByTagName(REQUEST_MAPPING);
            for ( int rootCount = 0; rootCount < list.getLength(); rootCount++ ) {
                Node node = list.item(rootCount);
                if ( node instanceof Element ) {
                    Element element = (Element) node;
                    NodeList subList = element.getElementsByTagName("*");
                    HashMap uriMap = new HashMap();
                    String uri = null;
                    for ( int subCount = 0; subCount < subList.getLength(); subCount++ ) {
                        Node subNode = subList.item(subCount);
                        NodeList children = subNode.getChildNodes();
                        if ( children.getLength() > 0 ) {                            
                            Node childNode = children.item(0);
                            if ( childNode.getNodeValue() != null ) {
                                if ( subNode.getNodeName().equals(URI) )
                                    uri = childNode.getNodeValue();
                                else
                                    uriMap.put(subNode.getNodeName(),childNode.getNodeValue());
                            }
                        }
                    }
                    if ( uri != null )
                        map.put(uri,uriMap);
                }
            }
        }
        Debug.logInfo("RequestMap Created: (" + map.size() + ") records.");
        return map;
    }
    
    /** Gets a HashMap of site configuration variables. */
    public static HashMap getConfigMap(String xml) {
        HashMap map = new HashMap();
        Element root = loadDocument(xml);
        NodeList list = null;
        
        if ( root != null ) {
            // datasource element
            list = root.getElementsByTagName(DATASOURCE);
            if ( list.getLength() > 0 ) {
                Node node = list.item(0);
                NodeList children = node.getChildNodes();
                Node child = children.item(0);
                if ( child.getNodeValue() != null )
                    map.put(DATASOURCE,child.getNodeValue());
            }
            list = null;
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
        }
        Debug.logInfo("ConfigMap Created: (" + map.size() +") records.");
        return map;
    }
    
    /** Gets a HashMap of view mappings. */
    public static HashMap getViewMap(String xml) {
        HashMap map = new HashMap();
        Element root = loadDocument(xml);
        if ( root != null ) {
            // view-map elements
            NodeList list = root.getElementsByTagName(VIEW_MAPPING);
            for ( int rootCount = 0; rootCount < list.getLength(); rootCount++ ) {
                Node node = list.item(rootCount);
                if ( node instanceof Element ) {
                    Element element = (Element) node;
                    NodeList subList = element.getElementsByTagName("*");
                    HashMap uriMap = new HashMap();
                    String uri = null;
                    for ( int subCount = 0; subCount < subList.getLength(); subCount++ ) {
                        Node subNode = subList.item(subCount);
                        NodeList children = subNode.getChildNodes();
                        if ( children.getLength() > 0 ) {
                            Node childNode = children.item(0);
                            if ( childNode.getNodeValue() != null ) {
                                if ( subNode.getNodeName().equals(VIEW) )
                                    uri = childNode.getNodeValue();
                                else
                                    uriMap.put(subNode.getNodeName(),childNode.getNodeValue());
                            }
                        }
                    }
                    if ( uri != null )
                        map.put(uri,uriMap);
                }
            }
        }
        Debug.logInfo("ViewMap Created: (" + map.size() + ") records.");
        return map;
    }
    
    /** Gets a HashMap of scheduler mappings. */
    public static HashMap getSchedulerMap(String xml) {
        HashMap map = new HashMap();
        Element root = loadDocument(xml);        
        if ( root != null ) {
            // schedule elements
            NodeList list = root.getElementsByTagName(SCHEDULER_MAPPING);            
            for ( int rootCount = 0; rootCount < list.getLength(); rootCount++ ) {
                Node node = list.item(rootCount);
                if ( node instanceof Element ) {
                    Element element = (Element) node;
                    NodeList subList = element.getElementsByTagName("*");
                    HashMap mainMap = new HashMap();       // for the main attributes
                    HashMap paramMap = new HashMap();     // for the parameter list
                    HashMap headerMap = new HashMap();    // for the header list
                    String job = null;                    
                    for ( int subCount = 0; subCount < subList.getLength(); subCount++ ) {                        
                        Node subNode = subList.item(subCount);                        
                        if ( subNode.getNodeName().equals(SCHEDULER_PARAMETERS) ) {
                            Element thisElement = (Element) subNode;
                            paramMap.put(thisElement.getAttribute("name"),thisElement.getAttribute("value"));                            
                        }
                        else if ( subNode.getNodeName().equals(SCHEDULER_HEADERS) ) {
                            Element thisElement = (Element) subNode;
                            headerMap.put(thisElement.getAttribute("name"),thisElement.getAttribute("value"));                            
                        }
                        else {
                            NodeList children = subNode.getChildNodes();
                            if ( children.getLength() > 0 ) {
                                Node childNode = children.item(0);                           
                            
                                if ( childNode.getNodeValue() != null ) {                                    
                                    if ( subNode.getNodeName().equals(SCHEDULER_JOB_NAME) ) {
                                        job = childNode.getNodeValue();
                                    }
                                    else {
                                        mainMap.put(subNode.getNodeName(),childNode.getNodeValue());
                                    }
                                }
                            }
                        }
                    }
                    mainMap.put(SCHEDULER_PARAMETERS, paramMap);
                    mainMap.put(SCHEDULER_HEADERS, headerMap);
                    if ( job != null )
                        map.put(job,mainMap);
                }
            }
        }
        Debug.logInfo("SchedulerMap Created: (" + map.size() + ") records.");
        return map;
    }
    
    /** Gets the datasource associated with this config file. */
    public static String getDataSource(String xml) {
        String value = null;
        Element root = loadDocument(xml);
        NodeList nodeList = root.getElementsByTagName(DATASOURCE);
        Node node = nodeList.item(0);   // Only get the first one.
        return node.getNodeValue();
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
    
}
