/*
 * $Id: RequestManager.java,v 1.1 2003/08/17 08:40:12 ajzeneski Exp $
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.content.webapp.control;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletContext;

import org.ofbiz.base.util.Debug;

/**
 * RequestManager - Manages request, config and view mappings.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      2.0
 */
public class RequestManager implements Serializable {

    public static final String module = RequestManager.class.getName();
    public static final int VIEW_HANDLER_KEY = 1;
    public static final int EVENT_HANDLER_KEY = 0;

    private URL configFileUrl;

    public RequestManager(ServletContext context) {

        /** Loads the site configuration from servlet context parameter. */
        try {
            configFileUrl = context.getResource("/WEB-INF/controller.xml");
        } catch (Exception e) {
            Debug.logError(e, "[RequestManager.constructor] Error Finding XML Config File: " +
                "/WEB-INF/controller.xml", module);
        }
        // do quick inits:
        ConfigXMLReader.getConfigMap(configFileUrl);
        ConfigXMLReader.getHandlerMap(configFileUrl);
        ConfigXMLReader.getRequestMap(configFileUrl);
        ConfigXMLReader.getViewMap(configFileUrl);
    }

    /** Gets the entire handler mapping */
    public Map getHandlerMap() {
        return (Map) ConfigXMLReader.getHandlerMap(configFileUrl);
    }

    /** Gets the class name of the named handler */
    public String getHandlerClass(String name, int type) {
        Map map = getHandlerMap();
        Map hMap = null;

        if (type == 1)
            hMap = (Map) map.get("view");
        else
            hMap = (Map) map.get("event");
        if (!hMap.containsKey(name))
            return null;
        else
            return (String) hMap.get(name);
    }

    public Map getRequestMapMap(String uriStr) {
        return (Map) ConfigXMLReader.getRequestMap(configFileUrl).get(uriStr);
    }

    public String getRequestAttribute(String uriStr, String attribute) {
        Map uri = getRequestMapMap(uriStr);

        if (uri != null)
            return (String) uri.get(attribute);
        else {
            Debug.logWarning("[RequestManager.getRequestAttribute] Value for attribute \"" + attribute +
                "\" of uri \"" + uriStr + "\" not found", module);
            return null;
        }
    }

    /** Gets the event class from the requestMap */
    public String getEventPath(String uriStr) {
        Map uri = getRequestMapMap(uriStr);

        if (uri != null)
            return (String) uri.get(ConfigXMLReader.EVENT_PATH);
        else {
            Debug.logWarning("[RequestManager.getEventPath] Path of event for request \"" + uriStr +
                "\" not found", module);
            return null;
        }
    }

    /** Gets the event type from the requestMap */
    public String getEventType(String uriStr) {
        Map uri = getRequestMapMap(uriStr);

        if (uri != null)
            return (String) uri.get(ConfigXMLReader.EVENT_TYPE);
        else {
            Debug.logWarning("[RequestManager.getEventType] Type of event for request \"" + uriStr +
                "\" not found", module);
            return null;
        }
    }

    /** Gets the event method from the requestMap */
    public String getEventMethod(String uriStr) {
        Map uri = getRequestMapMap(uriStr);

        if (uri != null) {
            return (String) uri.get(ConfigXMLReader.EVENT_METHOD);
        } else {
            Debug.logWarning("[RequestManager.getEventMethod] Method of event for request \"" +
                uriStr + "\" not found", module);
            return null;
        }
    }

    /** Gets the view name from the requestMap */
    public String getViewName(String uriStr) {
        Map uri = getRequestMapMap(uriStr);

        if (uri != null)
            return (String) uri.get(ConfigXMLReader.NEXT_PAGE);
        else {
            Debug.logWarning("[RequestManager.getViewName] View name for uri \"" + uriStr + "\" not found", module);
            return null;
        }
    }

    /** Gets the next page (jsp) from the viewMap */
    public String getViewPage(String viewStr) {
        if (viewStr != null && viewStr.startsWith("view:")) viewStr = viewStr.substring(viewStr.indexOf(':') + 1);
        Map page = (Map) ConfigXMLReader.getViewMap(configFileUrl).get(viewStr);

        if (page != null) {
            return (String) page.get(ConfigXMLReader.VIEW_PAGE);
        } else {
            Debug.logWarning("[RequestManager.getViewPage] View with name \"" + viewStr + "\" not found", module);
            return null;
        }
    }

    /** Gets the type of this view */
    public String getViewType(String viewStr) {
        Map view = (Map) ConfigXMLReader.getViewMap(configFileUrl).get(viewStr);

        if (view != null) {
            return (String) view.get(ConfigXMLReader.VIEW_TYPE);
        } else {
            Debug.logWarning("[RequestManager.getViewType] View with name \"" + viewStr + "\" not found", module);
            return null;
        }
    }

    /** Gets the info of this view */
    public String getViewInfo(String viewStr) {
        Map view = (Map) ConfigXMLReader.getViewMap(configFileUrl).get(viewStr);

        if (view != null) {
            return (String) view.get(ConfigXMLReader.VIEW_INFO);
        } else {
            Debug.logWarning("[RequestManager.getViewInfo] View with name \"" + viewStr + "\" not found", module);
            return null;
        }
    }
    
    /** Gets the content-type of this view */
    public String getViewContentType(String viewStr) {
        Map view = (Map) ConfigXMLReader.getViewMap(configFileUrl).get(viewStr);

        if (view != null) {
            return (String) view.get(ConfigXMLReader.VIEW_CONTENT_TYPE);
        } else {
            Debug.logWarning("[RequestManager.getViewInfo] View with name \"" + viewStr + "\" not found", module);
            return null;
        }
    }    
    
    /** Gets the content-type of this view */
    public String getViewEncoding(String viewStr) {
        Map view = (Map) ConfigXMLReader.getViewMap(configFileUrl).get(viewStr);

        if (view != null) {
            return (String) view.get(ConfigXMLReader.VIEW_ENCODING);
        } else {
            Debug.logWarning("[RequestManager.getViewInfo] View with name \"" + viewStr + "\" not found", module);
            return null;
        }
    }        

    /** Gets the error page from the requestMap, if none uses the default */
    public String getErrorPage(String uriStr) {
        Map uri = getRequestMapMap(uriStr);

        if (uri != null) {
            String returnPage = getViewPage((String) uri.get(ConfigXMLReader.ERROR_PAGE));

            if (returnPage != null)
                return returnPage;
            else
                return getDefaultErrorPage();
        } else
            return getDefaultErrorPage();
    }

    /** Gets the default error page from the configMap or static site default */
    public String getDefaultErrorPage() {
        String errorPage = null;

        errorPage = (String) ConfigXMLReader.getConfigMap(configFileUrl).get(ConfigXMLReader.DEFAULT_ERROR_PAGE);
        if (errorPage != null) return errorPage;
        return "/error/error.jsp";
    }

    public boolean requiresAuth(String uriStr) {
        Map uri = getRequestMapMap(uriStr);

        if (uri != null) {
            String value = (String) uri.get(ConfigXMLReader.SECURITY_AUTH);

            if (Debug.verboseOn()) Debug.logVerbose("Require Auth: " + value, module);
            if ("true".equalsIgnoreCase(value))
                return true;
            else
                return false;
        } else
            return false;
    }

    public boolean requiresHttps(String uriStr) {
        Map uri = getRequestMapMap(uriStr);

        if (uri != null) {
            String value = (String) uri.get(ConfigXMLReader.SECURITY_HTTPS);

            if (Debug.verboseOn()) Debug.logVerbose("Requires HTTPS: " + value, module);
            if ("true".equalsIgnoreCase(value))
                return true;
            else
                return false;
        } else
            return false;
    }

    public boolean allowExtView(String uriStr) {
        Map uri = getRequestMapMap(uriStr);

        if (uri != null) {
            String value = (String) uri.get(ConfigXMLReader.SECURITY_EXTVIEW);

            if (Debug.verboseOn()) Debug.logVerbose("Allow External View: " + value, module);
            if ("false".equalsIgnoreCase(value))
                return false;
            else
                return true;
        } else
            return true;
    }

    public boolean allowDirectRequest(String uriStr) {
        Map uri = getRequestMapMap(uriStr);

        if (uri != null) {
            String value = (String) uri.get(ConfigXMLReader.SECURITY_DIRECT);

            if (Debug.verboseOn()) Debug.logVerbose("Allow Direct Request: " + value, module);
            if ("false".equalsIgnoreCase(value))
                return false;
            else
                return true;
        } else
            return false;
    }

    public Collection getFirstVisitEvents() {
        Collection c = (Collection) ConfigXMLReader.getConfigMap(configFileUrl).get(ConfigXMLReader.FIRSTVISIT);
        return c;
    }

    public Collection getPreProcessor() {
        Collection c = (Collection) ConfigXMLReader.getConfigMap(configFileUrl).get(ConfigXMLReader.PREPROCESSOR);
        return c;
    }

    public Collection getPostProcessor() {
        Collection c = (Collection) ConfigXMLReader.getConfigMap(configFileUrl).get(ConfigXMLReader.POSTPROCESSOR);
        return c;
    }
}
