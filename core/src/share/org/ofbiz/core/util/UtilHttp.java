/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.core.util;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * HttpUtil - Misc TTP Utility Functions
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
 * @version    $Revision$
 * @since      2.1
 */
public class UtilHttp {

    /** 
     * Create a map from an HttpServletRequest object
     * @return The resulting Map
     */
    public static Map getParameterMap(HttpServletRequest request) {
        Map paramMap = new OrderedMap();        
        // first add in all path info parameters /~name1=value1/~name2=value2/
        // Note: this is called a lot, so not using split for performance reasons: List pathInfo = StringUtil.split(request.getPathInfo(), "/"); for (int i = 1; i < pathInfo.size(); i++) {
        String pathInfoStr = request.getPathInfo();
        
        // make sure string ends with a trailing '/' so we get all values
        if (!pathInfoStr.endsWith("/")) pathInfoStr += "/";
        
        int current = pathInfoStr.indexOf('/');
        int last = current;
        while ((current = pathInfoStr.indexOf('/', last + 1)) != -1) {
            String element = pathInfoStr.substring(last + 1, current);
            last = current;
            if (element.charAt(0) == '~' && element.indexOf('=') > -1) {
                String name = element.substring(1, element.indexOf('='));
                String value = element.substring(element.indexOf('=') + 1);
                paramMap.put(name, value);
            }
        }
        
        // now add all the actual parameters; these take priority
        java.util.Enumeration e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            paramMap.put(name, request.getParameter(name));
        }
        return paramMap;
    }

    /** 
     * Given a request, returns the application name or "root" if deployed on root 
     * @param request An HttpServletRequest to get the name info from
     * @return String
     */
    public static String getApplicationName(HttpServletRequest request) {
        String appName = "root";    
        if (request.getContextPath().length() > 1) {
            appName = request.getContextPath().substring(1);
        }
        return appName;
    }

    /**
     * Put request parameters in request object as attributes.
     * @param request
     */
    public static void parametersToAttributes(HttpServletRequest request) {
        java.util.Enumeration e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            request.setAttribute(name, request.getParameter(name));
        }
    }

    public static StringBuffer getServerRootUrl(HttpServletRequest request) {
        StringBuffer requestUrl = new StringBuffer();    
        requestUrl.append(request.getScheme());
        requestUrl.append("://" + request.getServerName());
        if (request.getServerPort() != 80 && request.getServerPort() != 443)
            requestUrl.append(":" + request.getServerPort());
        return requestUrl;
    }

    public static StringBuffer getFullRequestUrl(HttpServletRequest request) {
        StringBuffer requestUrl = UtilHttp.getServerRootUrl(request);    
        requestUrl.append(request.getRequestURI());
        if (request.getQueryString() != null) {
            requestUrl.append("?" + request.getQueryString());
        }
        return requestUrl;
    }

    private static Locale getLocale(HttpServletRequest request, HttpSession  session) {
        Object localeObject = session != null ? session.getAttribute("locale") : null;
        if (localeObject == null) localeObject = request != null ? request.getLocale() : null;
    
        if (localeObject != null && localeObject instanceof String) {
            localeObject = UtilMisc.parseLocale((String) localeObject);
        } 
        
        if (localeObject != null && localeObject instanceof Locale) {
            return (Locale) localeObject;
        } else {
            return Locale.getDefault();
        }                                
    }

    /**
     * Get the Locale object from a session variable; if not found use the browser's default
     * @param request HttpServletRequest object to use for lookup
     * @return Locale The current Locale to use
     */
    public static Locale getLocale(HttpServletRequest request) {
        if (request == null) return Locale.getDefault();
        return UtilHttp.getLocale(request, request.getSession());
    }

    /**
     * Get the Locale object from a session variable; if not found use the system's default.
     * NOTE: This method is not recommended because it ignores the Locale from the browser not having the request object.
     * @param session HttpSession object to use for lookup
     * @return Locale The current Locale to use
     */
    public static Locale getLocale(HttpSession  session) {
        if (session == null) return Locale.getDefault();
        return UtilHttp.getLocale(null, session);
    }

    public static void setLocale(HttpServletRequest request, String localeString) {
        UtilHttp.setLocale(request, UtilMisc.parseLocale(localeString));        
    }

    public static void setLocale(HttpServletRequest request, Locale locale) {
        request.getSession().setAttribute("locale", locale);        
    }
    
    /** URL Encodes a Map of arguements */
    public static String urlEncodeArgs(Map args) {
        StringBuffer buf = new StringBuffer();
        Set names = args.keySet();
        Iterator i = names.iterator();

        while (i.hasNext()) {
            String name = (String) i.next();
            Object value = args.get(name);
            String valueStr = null;
            if (value != null) {
                if (value instanceof String) {
                    valueStr = (String) value;
                } else {
                    valueStr = value.toString();
                }
            }
                        
            if (name != null) {
                if (buf.length() > 0) buf.append('&');
                buf.append(URLEncoder.encode(name));
                buf.append('=');
                if (valueStr != null) {
                    buf.append(URLEncoder.encode(valueStr));
                }
            }
        }
        return buf.toString();
    }    
}
