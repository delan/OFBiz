/*
 * $Id$
 */

package org.ofbiz.core.control;

import java.io.*;
import java.util.*;
import java.net.*;
import javax.servlet.*;

import org.ofbiz.core.util.*


/**
 * <p><b>Title:</b> RequestManager.java
 * <p><b>Description:</b> Manages request, config and view mappings.
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
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    June 28, 2001
 *@version    1.0
 */
public class RequestManager implements Serializable {

    private URL configFileUrl;

    public RequestManager(ServletContext context) {
        /** Loads the site configuration from servlet context parameter. */
        try {
            configFileUrl = context.getResource(context.getInitParameter(SiteDefs.SITE_CONFIG));
        } catch (Exception e) {
            Debug.logError(e, "[RequestManager.constructor] Error Finding XML Config File: " +
                              context.getInitParameter(SiteDefs.SITE_CONFIG));
        }
        //do quick inits:
        ConfigXMLReader.getHandlerMap(configFileUrl);
        ConfigXMLReader.getRequestMap(configFileUrl);
        ConfigXMLReader.getViewMap(configFileUrl);
        ConfigXMLReader.getConfigMap(configFileUrl);
    }

    /** Gets the entire handler mapping */
    public HashMap getHandlerMap() {
        return (HashMap) ConfigXMLReader.getHandlerMap(configFileUrl);
    }

    /** Gets the class name of the named handler */
    public String getHandlerClass(String name) {
        HashMap map = getHandlerMap();
        if (!map.containsKey(name))
            return null;
        else
            return (String) map.get(name);
    }

    public HashMap getRequestMapMap(String uriStr) {
        return (HashMap) ConfigXMLReader.getRequestMap(configFileUrl).get(uriStr);
    }

    public String getRequestAttribute(String uriStr, String attribute) {
        HashMap uri = getRequestMapMap(uriStr);
        if (uri != null)
            return (String) uri.get(attribute);
        else {
            Debug.logWarning("[RequestManager.getRequestAttribute] Value for attribute \"" + attribute +
                             "\" of uri \"" + uriStr + "\" not found");
            return null;
        }
    }

    /** Gets the event class from the requestMap */
    public String getEventPath(String uriStr) {
        HashMap uri = getRequestMapMap(uriStr);
        if (uri != null)
            return (String) uri.get(ConfigXMLReader.EVENT_PATH);
        else {
            Debug.logWarning("[RequestManager.getEventPath] Path of event for request \"" + uriStr + "\" not found");
            return null;
        }
    }

    /** Gets the event type from the requestMap */
    public String getEventType(String uriStr) {
        HashMap uri = getRequestMapMap(uriStr);
        if (uri != null)
            return (String) uri.get(ConfigXMLReader.EVENT_TYPE);
        else {
            Debug.logWarning("[RequestManager.getEventType] Type of event for request \"" + uriStr + "\" not found");
            return null;
        }
    }

    /** Gets the event method from the requestMap */
    public String getEventMethod(String uriStr) {
        HashMap uri = getRequestMapMap(uriStr);
        if (uri != null) {
            return (String) uri.get(ConfigXMLReader.EVENT_METHOD);
        } else {
            Debug.logWarning("[RequestManager.getEventMethod] Method of event for request \"" +
                             uriStr + "\" not found");
            return null;
        }
    }

    /** Gets the view name from the requestMap */
    public String getViewName(String uriStr) {
        HashMap uri = getRequestMapMap(uriStr);
        if (uri != null)
            return (String) uri.get(ConfigXMLReader.NEXT_PAGE);
        else {
            Debug.logWarning("[RequestManager.getViewName] View name for uri \"" + uriStr + "\" not found");
            return null;
        }
    }

    /** Gets the next page (jsp) from the viewMap */
    public String getViewPage(String viewStr) {
        if (viewStr != null && viewStr.startsWith("view:")) viewStr = viewStr.substring(viewStr.indexOf(':') + 1);
        HashMap page = (HashMap) ConfigXMLReader.getViewMap(configFileUrl).get(viewStr);
        if (page != null) {
            return (String) page.get(ConfigXMLReader.VIEW_PAGE);
        } else {
            Debug.logWarning("[RequestManager.getViewPage] View with name \"" + viewStr + "\" not found");
            return null;
        }
    }

    /** Gets the error page from the requestMap, if none uses the default */
    public String getErrorPage(String uriStr) {
        HashMap uri = getRequestMapMap(uriStr);
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
        return SiteDefs.ERROR_PAGE;
    }

    public boolean requiresAuth(String uriStr) {
        HashMap uri = getRequestMapMap(uriStr);
        if (uri != null) {
            String value = (String) uri.get(ConfigXMLReader.SECURITY_AUTH);
            if ("true".equalsIgnoreCase(value))
                return true;
            else
                return false;
        } else
            return false;
    }

    public boolean requiresHttps(String uriStr) {
        HashMap uri = getRequestMapMap(uriStr);
        if (uri != null) {
            String value = (String) uri.get(ConfigXMLReader.SECURITY_HTTPS);
            if ("true".equalsIgnoreCase(value))
                return true;
            else
                return false;
        } else
            return false;
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