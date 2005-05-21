/*
 * $Id$
 *
 *  Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.webapp.control;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.cache.UtilCache;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * ConfigXMLReader.java - Reads and parses the XML site config files.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      2.0
 */
public class ConfigXMLReader {

    public static final String module = ConfigXMLReader.class.getName();

    public static ControllerConfig getControllerConfig(URL url) {
        ControllerConfig controllerConfig = (ControllerConfig) controllerCache.get(url);
        if (controllerConfig == null) { // don't want to block here
            synchronized (ConfigXMLReader.class) {
                // must check if null again as one of the blocked threads can still enter
                controllerConfig = (ControllerConfig) controllerCache.get(url);
                if (controllerConfig == null) {
                    controllerConfig = new ControllerConfig(url);
                    controllerCache.put(url, controllerConfig);
                }
            }
        }
        return controllerConfig;
    }
    
    public static UtilCache controllerCache = new UtilCache("webapp.ControllerConfig");
    
    public static class ControllerConfig {
        public URL url;
        
        public Map configMap = FastMap.newInstance();
        public Map handlerMap = FastMap.newInstance();
        public Map requestMap = FastMap.newInstance();
        public Map viewMap = FastMap.newInstance();

        public ControllerConfig(URL url) {
            this.url = url;
            
            Element rootElement = loadDocument(url);
            if (rootElement != null) {
                this.configMap = loadConfigMap(rootElement, url);
                this.handlerMap = loadHandlerMap(rootElement, url);
                this.requestMap = loadRequestMap(rootElement, url);
                this.viewMap = loadViewMap(rootElement, url);
            }
        }
    }

    /** Site Config Variables */
    public static final String DEFAULT_ERROR_PAGE = "errorpage";
    public static final String SITE_OWNER = "owner";
    public static final String SECURITY_CLASS = "security-class";
    public static final String FIRSTVISIT = "firstvisit";
    public static final String PREPROCESSOR = "preprocessor";
    public static final String POSTPROCESSOR = "postprocessor";

    /** URI Config Variables */
    public static final String INCLUDE = "include";
    public static final String INCLUDE_FILE = "file";
    public static final String INCLUDE_URL = "url";

    public static final String REQUEST_MAPPING = "request-map";
    public static final String REQUEST_URI = "uri";
    public static final String REQUEST_EDIT = "edit";

    public static final String REQUEST_DESCRIPTION = "description";
    public static final String ERROR_PAGE = "error";
    public static final String NEXT_PAGE = "success";

    public static final String SECURITY = "security";
    public static final String SECURITY_HTTPS = "https";
    public static final String SECURITY_AUTH = "auth";
    public static final String SECURITY_EXTVIEW = "external-view";
    public static final String SECURITY_DIRECT = "direct-request";

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
    public static final String VIEW_TYPE = "type";
    public static final String VIEW_INFO = "info";
    public static final String VIEW_CONTENT_TYPE = "content-type";
    public static final String VIEW_ENCODING = "encoding";
    public static final String VIEW_DESCRIPTION = "description";

    /** Handler Config Variables */
    public static final String HANDLER = "handler";
    public static final String HANDLER_NAME = "name";
    public static final String HANDLER_TYPE = "type";
    public static final String HANDLER_CLASS = "class";

    /** Loads the XML file and returns the root element */
    public static Element loadDocument(URL location) {
        Document document = null;
        try {
            document = UtilXml.readXmlDocument(location, true);
            Element rootElement = document.getDocumentElement();
            // rootElement.normalize();
            if (Debug.verboseOn()) Debug.logVerbose("Loaded XML Config - " + location, module);
            return rootElement;
        } catch (Exception e) {
            Debug.logError(e, module);
        }
        return null;
    }

    /** Gets a Map of request mappings. */
    public static Map getRequestMap(URL xml) {
        ControllerConfig controllerConfig = getControllerConfig(xml);
        return controllerConfig != null ? controllerConfig.requestMap : null;
    }

    /** Gets a FastMap of request mappings. */
    public static Map loadRequestMap(Element root, URL xml) {
        long startTime = System.currentTimeMillis();
        FastMap map = FastMap.newInstance();
        if (root == null) {
            root = loadDocument(xml);
        }

        if (root == null) return map;

        List includeElementList = UtilXml.childElementList(root, INCLUDE);
        Iterator includeElementIter = includeElementList.iterator();
        while (includeElementIter.hasNext()) {
            Element includeElement = (Element) includeElementIter.next();
            String includeFile = includeElement.getAttribute(INCLUDE_FILE);

            if ((includeFile != null) && (includeFile.length() > 0)) {
                File oldFile = new File(xml.getFile());
                File newFile = new java.io.File("" + oldFile.getParent() + java.io.File.separator + includeFile);

                try {
                    Map subMap = loadRequestMap(null, newFile.toURL());

                    map.putAll(subMap);
                } catch (MalformedURLException mue) {
                    mue.printStackTrace();
                }
            }

            String includeURL = includeElement.getAttribute(INCLUDE_URL);
            if ((includeURL != null) && (includeURL.length() > 0)) {
                try {
                    Map subMap = loadRequestMap(null, new URL(includeURL));
                    map.putAll(subMap);
                } catch (MalformedURLException mue) {
                    mue.printStackTrace();
                }
            }
        }

        List requestMapElementList = UtilXml.childElementList(root, REQUEST_MAPPING);
        Iterator requestMapElementIter = requestMapElementList.iterator();
        while (requestMapElementIter.hasNext()) {
            Element requestMapElement = (Element) requestMapElementIter.next();
            
            // Create a URI-MAP for each element found.
            FastMap uriMap = FastMap.newInstance();

            // Get the URI info.
            String uri = requestMapElement.getAttribute(REQUEST_URI);
            String edit = requestMapElement.getAttribute(REQUEST_EDIT);

            if (edit == null || edit.equals(""))
                edit = "true";
            if (uri != null) {
                uriMap.put(REQUEST_URI, uri);
                uriMap.put(REQUEST_EDIT, edit);
            }

            // Check for security.
            Element securityElement = UtilXml.firstChildElement(requestMapElement, SECURITY);
            if (securityElement != null) {
                String securityHttps = securityElement.getAttribute(SECURITY_HTTPS);
                String securityAuth = securityElement.getAttribute(SECURITY_AUTH);
                String securityExtView = securityElement.getAttribute(SECURITY_EXTVIEW);
                String securityDirectRequest = securityElement.getAttribute(SECURITY_DIRECT);
                uriMap.put(SECURITY_HTTPS, securityHttps);
                uriMap.put(SECURITY_AUTH, securityAuth);
                uriMap.put(SECURITY_EXTVIEW, securityExtView);
                uriMap.put(SECURITY_DIRECT, securityDirectRequest);
            }

            // Check for an event.
            Element eventElement = UtilXml.firstChildElement(requestMapElement, EVENT);
            if (eventElement != null) {
                String type = eventElement.getAttribute(EVENT_TYPE);
                String path = eventElement.getAttribute(EVENT_PATH);
                String invoke = eventElement.getAttribute(EVENT_METHOD);

                uriMap.put(EVENT_TYPE, type);
                uriMap.put(EVENT_PATH, path);
                uriMap.put(EVENT_METHOD, invoke);
            }

            // Check for a description.
            String description = UtilXml.childElementValue(requestMapElement, REQUEST_DESCRIPTION);
            uriMap.put(REQUEST_DESCRIPTION, UtilValidate.isNotEmpty(description) ? description : "");

            // Get the response(s).
            List responseElementList = UtilXml.childElementList(requestMapElement, RESPONSE);
            Iterator responseElementIter = responseElementList.iterator();
            while (responseElementIter.hasNext()) {
                Element responseElement = (Element) responseElementIter.next();
                String name = responseElement.getAttribute(RESPONSE_NAME);
                String type = responseElement.getAttribute(RESPONSE_TYPE);
                String value = responseElement.getAttribute(RESPONSE_VALUE);
                uriMap.put(name, type + ":" + value);
            }

            if (uri != null) {
                map.put(uri, uriMap);
            }
        }

        /* Debugging */
        if (Debug.verboseOn()) {
            Debug.logVerbose("-------- Request Mappings --------", module);
            FastMap debugMap = map;
            Set debugSet = debugMap.keySet();
            Iterator i = debugSet.iterator();

            while (i.hasNext()) {
                Object o = i.next();
                String request = (String) o;
                FastMap thisURI = (FastMap) debugMap.get(o);


                StringBuffer verboseMessageBuffer = verboseMessageBuffer = new StringBuffer();

                Iterator debugIter = ((Set) thisURI.keySet()).iterator();
                while (debugIter.hasNext()) {
                    Object lo = debugIter.next();
                    String name = (String) lo;
                    String value = (String) thisURI.get(lo);

                    verboseMessageBuffer.append("[" + name + "=>" + value + "]");
                }
                Debug.logVerbose(request + " :: " + verboseMessageBuffer.toString(), module);
            }
            Debug.logVerbose("------ End Request Mappings ------", module);
        }
        /* End Debugging */

        double totalSeconds = (System.currentTimeMillis() - startTime)/1000.0;
        if (Debug.infoOn()) Debug.logInfo("RequestMap Created: (" + map.size() + ") records in " + totalSeconds + "s", module);
        return map;
    }

    /** Gets a FastMap of view mappings. */
    public static Map getViewMap(URL xml) {
        ControllerConfig controllerConfig = getControllerConfig(xml);
        return controllerConfig != null ? controllerConfig.viewMap : null;
    }

    /** Gets a FastMap of view mappings. */
    public static Map loadViewMap(Element root, URL xml) {
        long startTime = System.currentTimeMillis();
        FastMap map = FastMap.newInstance();
        if (root == null) {
            root = loadDocument(xml);
        }

        if (root == null) {
            return map;
        }

        List includeElementList = UtilXml.childElementList(root, INCLUDE);
        Iterator includeElementIter = includeElementList.iterator();
        while (includeElementIter.hasNext()) {
            Element includeElement = (Element) includeElementIter.next();
            String includeFile = includeElement.getAttribute(INCLUDE_FILE);

            if ((includeFile != null) && (includeFile.length() > 0)) {
                File oldFile = new File(xml.getFile());
                File newFile = new java.io.File("" + oldFile.getParent() + java.io.File.separator + includeFile);

                try {
                    Map subMap = loadRequestMap(null, newFile.toURL());

                    map.putAll(subMap);
                } catch (MalformedURLException mue) {
                    mue.printStackTrace();
                }
            }

            String includeURL = includeElement.getAttribute(INCLUDE_URL);
            if ((includeURL != null) && (includeURL.length() > 0)) {
                try {
                    Map subMap = loadRequestMap(null, new URL(includeURL));
                    map.putAll(subMap);
                } catch (MalformedURLException mue) {
                    mue.printStackTrace();
                }
            }
        }

        List viewMapElementList = UtilXml.childElementList(root, VIEW_MAPPING);
        Iterator viewMapElementIter = viewMapElementList.iterator();
        while (viewMapElementIter.hasNext()) {
            Element viewMapElement = (Element) viewMapElementIter.next();
            // Create a URI-MAP for each element found.
            FastMap uriMap = FastMap.newInstance();

            // Get the view info.
            String name = viewMapElement.getAttribute(VIEW_NAME);
            String page = viewMapElement.getAttribute(VIEW_PAGE);
            if (page == null || page.length() == 0) {
                page = name;
            }

            uriMap.put(VIEW_NAME, name);
            uriMap.put(VIEW_PAGE, page);
            uriMap.put(VIEW_TYPE, viewMapElement.getAttribute(VIEW_TYPE));
            uriMap.put(VIEW_INFO, viewMapElement.getAttribute(VIEW_INFO));
            uriMap.put(VIEW_CONTENT_TYPE, viewMapElement.getAttribute(VIEW_CONTENT_TYPE));
            uriMap.put(VIEW_ENCODING, viewMapElement.getAttribute(VIEW_ENCODING));

            // Check for a description.
            String description = UtilXml.childElementValue(viewMapElement, VIEW_DESCRIPTION);
            uriMap.put(VIEW_DESCRIPTION, UtilValidate.isNotEmpty(description) ? description : "");

            if (name != null) map.put(name, uriMap);
        }

        /* Debugging */
        if (Debug.verboseOn()) {
            Debug.logVerbose("-------- View Mappings --------", module);
            FastMap debugMap = map;
            Set debugSet = debugMap.keySet();
            Iterator i = debugSet.iterator();
    
            while (i.hasNext()) {
                Object o = i.next();
                String request = (String) o;
                FastMap thisURI = (FastMap) debugMap.get(o);
    
                StringBuffer verboseMessageBuffer = verboseMessageBuffer = new StringBuffer();
    
                Iterator debugIter = ((Set) thisURI.keySet()).iterator();
                while (debugIter.hasNext()) {
                    Object lo = debugIter.next();
                    String name = (String) lo;
                    String value = (String) thisURI.get(lo);
    
                    verboseMessageBuffer.append("[" + name + "=>" + value + "]");
                }
                Debug.logVerbose(request + " :: " + verboseMessageBuffer.toString(), module);
            }
            Debug.logVerbose("------ End View Mappings ------", module);
        }
        /* End Debugging */

        double totalSeconds = (System.currentTimeMillis() - startTime)/1000.0;
        if (Debug.infoOn()) Debug.logInfo("ViewMap Created: (" + map.size() + ") records in " + totalSeconds + "s", module);
        return map;
    }

    /** Gets a FastMap of site configuration variables. */
    public static Map getConfigMap(URL xml) {
        ControllerConfig controllerConfig = getControllerConfig(xml);
        return controllerConfig != null ? controllerConfig.configMap : null;
    }

    /** Gets a FastMap of site configuration variables. */
    public static Map loadConfigMap(Element root, URL xml) {
        long startTime = System.currentTimeMillis();
        FastMap map = FastMap.newInstance();
        if (root == null) {
            root = loadDocument(xml);
        }

        if (root != null) {
            // default error page
            String errorpage = UtilXml.childElementValue(root, DEFAULT_ERROR_PAGE);
            if (UtilValidate.isNotEmpty(errorpage)) map.put(DEFAULT_ERROR_PAGE, errorpage);

            // site owner
            String owner = UtilXml.childElementValue(root, SITE_OWNER);
            if (UtilValidate.isNotEmpty(owner)) map.put(SITE_OWNER, owner);

            // security class
            String securityClass = UtilXml.childElementValue(root, SECURITY_CLASS);
            if (UtilValidate.isNotEmpty(securityClass)) map.put(SECURITY_CLASS, securityClass);

            // first visit event
            Element firstvisitElement = UtilXml.firstChildElement(root, FIRSTVISIT);
            if (firstvisitElement != null) {
                List eventList = FastList.newInstance();
                List eventElementList = UtilXml.childElementList(firstvisitElement, EVENT);
                Iterator eventElementIter = eventElementList.iterator();
                while (eventElementIter.hasNext()) {
                    Element eventElement = (Element) eventElementIter.next();
                    FastMap eventMap = FastMap.newInstance();
                    eventMap.put(EVENT_TYPE, eventElement.getAttribute(EVENT_TYPE));
                    eventMap.put(EVENT_PATH, eventElement.getAttribute(EVENT_PATH));
                    eventMap.put(EVENT_METHOD, eventElement.getAttribute(EVENT_METHOD));
                    eventList.add(eventMap);
                }
                map.put(FIRSTVISIT, eventList);
            }

            // preprocessor events
            Element preprocessorElement = UtilXml.firstChildElement(root, PREPROCESSOR);
            if (preprocessorElement != null) {
                List eventList = FastList.newInstance();
                List eventElementList = UtilXml.childElementList(preprocessorElement, EVENT);
                Iterator eventElementIter = eventElementList.iterator();
                while (eventElementIter.hasNext()) {
                    Element eventElement = (Element) eventElementIter.next();
                    FastMap eventMap = FastMap.newInstance();
                    eventMap.put(EVENT_TYPE, eventElement.getAttribute(EVENT_TYPE));
                    eventMap.put(EVENT_PATH, eventElement.getAttribute(EVENT_PATH));
                    eventMap.put(EVENT_METHOD, eventElement.getAttribute(EVENT_METHOD));
                    eventList.add(eventMap);
                }
                map.put(PREPROCESSOR, eventList);
            }

            // postprocessor events
            Element postprocessorElement = UtilXml.firstChildElement(root, POSTPROCESSOR);
            if (postprocessorElement != null) {
                List eventList = FastList.newInstance();
                List eventElementList = UtilXml.childElementList(postprocessorElement, EVENT);
                Iterator eventElementIter = eventElementList.iterator();
                while (eventElementIter.hasNext()) {
                    Element eventElement = (Element) eventElementIter.next();
                    FastMap eventMap = FastMap.newInstance();
                    eventMap.put(EVENT_TYPE, eventElement.getAttribute(EVENT_TYPE));
                    eventMap.put(EVENT_PATH, eventElement.getAttribute(EVENT_PATH));
                    eventMap.put(EVENT_METHOD, eventElement.getAttribute(EVENT_METHOD));
                    eventList.add(eventMap);
                }
                map.put(POSTPROCESSOR, eventList);
            }

            // after-login events
            Element afterLoginElement = UtilXml.firstChildElement(root, "after-login");
            if (afterLoginElement != null) {
                List eventList = FastList.newInstance();
                List eventElementList = UtilXml.childElementList(afterLoginElement, EVENT);
                Iterator eventElementIter = eventElementList.iterator();
                while (eventElementIter.hasNext()) {
                    Element eventElement = (Element) eventElementIter.next();
                    FastMap eventMap = FastMap.newInstance();
                    eventMap.put(EVENT_TYPE, eventElement.getAttribute(EVENT_TYPE));
                    eventMap.put(EVENT_PATH, eventElement.getAttribute(EVENT_PATH));
                    eventMap.put(EVENT_METHOD, eventElement.getAttribute(EVENT_METHOD));
                    eventList.add(eventMap);
                }
                map.put("after-login", eventList);
            }

            // before-logout events
            Element beforeLogoutElement = UtilXml.firstChildElement(root, "before-logout");
            if (beforeLogoutElement != null) {
                List eventList = FastList.newInstance();
                List eventElementList = UtilXml.childElementList(beforeLogoutElement, EVENT);
                Iterator eventElementIter = eventElementList.iterator();
                while (eventElementIter.hasNext()) {
                    Element eventElement = (Element) eventElementIter.next();
                    FastMap eventMap = FastMap.newInstance();
                    eventMap.put(EVENT_TYPE, eventElement.getAttribute(EVENT_TYPE));
                    eventMap.put(EVENT_PATH, eventElement.getAttribute(EVENT_PATH));
                    eventMap.put(EVENT_METHOD, eventElement.getAttribute(EVENT_METHOD));
                    eventList.add(eventMap);
                }
                map.put("before-logout", eventList);
            }
        }

        /* Debugging */
        /*
         Debug.logVerbose("-------- Config Mappings --------", module);
         FastMap debugMap = map;
         Set debugSet = debugMap.keySet();
         Iterator i = debugSet.iterator();
         while (i.hasNext()) {
         Object o = i.next();
         String request = (String) o;
         FastMap thisURI = (FastMap) debugMap.get(o);
         Debug.logVerbose(request, module);
         Iterator debugIter = ((Set) thisURI.keySet()).iterator();
         while (debugIter.hasNext()) {
         Object lo = debugIter.next();
         String name = (String) lo;
         String value = (String) thisURI.get(lo);
         if (Debug.verboseOn()) Debug.logVerbose("\t" + name + " -> " + value, module);
         }
         }
         Debug.logVerbose("------ End Config Mappings ------", module);
         */
        /* End Debugging */

        double totalSeconds = (System.currentTimeMillis() - startTime)/1000.0;
        if (Debug.infoOn()) Debug.logInfo("ConfigMap Created: (" + map.size() + ") records in " + totalSeconds + "s", module);
        return map;
    }

    /** Gets a FastMap of handler mappings. */
    public static Map getHandlerMap(URL xml) {
        ControllerConfig controllerConfig = getControllerConfig(xml);
        return controllerConfig != null ? controllerConfig.handlerMap : null;
    }

    public static Map loadHandlerMap(Element root, URL xml) {
        long startTime = System.currentTimeMillis();
        FastMap map = FastMap.newInstance();
        if (root == null) {
            root = loadDocument(xml);
        }

        if (root != null) {
            Map rMap = FastMap.newInstance();
            Map vMap = FastMap.newInstance();

            List handlerElementList = UtilXml.childElementList(root, HANDLER);
            Iterator handlerElementIter = handlerElementList.iterator();
            while (handlerElementIter.hasNext()) {
                Element handlerElement = (Element) handlerElementIter.next();
                String hName = checkEmpty(handlerElement.getAttribute(HANDLER_NAME));
                String hClass = checkEmpty(handlerElement.getAttribute(HANDLER_CLASS));
                String hType = checkEmpty(handlerElement.getAttribute(HANDLER_TYPE));
                if (hType.equals("view")) {
                    vMap.put(hName, hClass);
                } else {
                    rMap.put(hName, hClass);
                }
            }
            map.put("view", vMap);
            map.put("event", rMap);
        }

        /* Debugging */
        if (Debug.verboseOn()) {
            Debug.logVerbose("-------- Handler Mappings --------", module);
            Map debugMap = (Map) map.get("event");

            if (debugMap != null && debugMap.size() > 0) {
                Debug.logVerbose("-------------- EVENT -------------", module);
                Set debugSet = debugMap.keySet();
                Iterator i = debugSet.iterator();
                while (i.hasNext()) {
                    Object o = i.next();
                    String handlerName = (String) o;
                    String className = (String) debugMap.get(o);
                    Debug.logVerbose("[EH] : " + handlerName + " => " + className, module);
                }
            }
            debugMap = (Map) map.get("view");
            if (debugMap != null && debugMap.size() > 0) {
                Debug.logVerbose("-------------- VIEW --------------", module);
                Set debugSet = debugMap.keySet();
                Iterator i = debugSet.iterator();
                while (i.hasNext()) {
                    Object o = i.next();
                    String handlerName = (String) o;
                    String className = (String) debugMap.get(o);
                    Debug.logVerbose("[VH] : " + handlerName + " => " + className, module);
                }
            }
            Debug.logVerbose("------ End Handler Mappings ------", module);
        }

        double totalSeconds = (System.currentTimeMillis() - startTime)/1000.0;
        if (Debug.infoOn()) Debug.logInfo("HandlerMap Created: (" + map.size() + ") records in " + totalSeconds + "s", module);
        return map;
    }

    private static String checkEmpty(String string) {
        if (string != null && string.length() > 0)
            return string;
        else
            return "";
    }

    public static void main(String args[]) throws Exception {
        /** Debugging */
        if (args[0] == null) {
            System.out.println("Please give a path to the config file you wish to test.");
            return;
        }
        System.out.println("----------------------------------");
        System.out.println("Request Mappings:");
        System.out.println("----------------------------------");
        Map debugMap = getRequestMap(new URL(args[0]));
        Set debugSet = debugMap.keySet();
        Iterator i = debugSet.iterator();
        while (i.hasNext()) {
            Object o = i.next();
            String request = (String) o;
            FastMap thisURI = (FastMap) debugMap.get(o);

            System.out.println(request);
            Iterator list = ((java.util.Set) thisURI.keySet()).iterator();
            while (list.hasNext()) {
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
