/*
 * $Id: UtilHttp.java,v 1.15 2004/07/09 18:14:40 ajzeneski Exp $
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
package org.ofbiz.base.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.collections.OrderedMap;

/**
 * HttpUtil - Misc TTP Utility Functions
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.15 $
 * @since      2.1
 */
public class UtilHttp {

    public static final String module = UtilHttp.class.getName();

    /**
     * Create a map from an HttpServletRequest object
     * @return The resulting Map
     */
    public static Map getParameterMap(HttpServletRequest request) {
        Map paramMap = new OrderedMap();
        // first add in all path info parameters /~name1=value1/~name2=value2/
        String pathInfoStr = request.getPathInfo();

        if (pathInfoStr != null && pathInfoStr.length() > 0) {
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
        }

        // now add all the actual parameters; these take priority
        java.util.Enumeration e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            paramMap.put(name, request.getParameter(name));
        }
        return paramMap;
    }

    public static Map makeParamMapWithPrefix(HttpServletRequest request, String prefix, String suffix) {
        Map paramMap = new HashMap();
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = (String) parameterNames.nextElement();
            if (parameterName.startsWith(prefix)) {
                if (suffix != null && suffix.length() > 0) {
                    if (parameterName.endsWith(suffix)) {
                        String key = parameterName.substring(prefix.length(), parameterName.length() - (suffix.length() - 1));
                        String value = request.getParameter(parameterName);
                        paramMap.put(key, value);
                    }
                } else {
                    String key = parameterName.substring(prefix.length());
                    String value = request.getParameter(parameterName);
                    paramMap.put(key, value);
                }
            }
        }
        return paramMap;
    }

    public static List makeParamListWithSuffix(HttpServletRequest request, String suffix, String prefix) {
        List paramList = new ArrayList();
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = (String) parameterNames.nextElement();
            if (parameterName.endsWith(suffix)) {
                if (prefix != null && prefix.length() > 0) {
                    if (parameterName.startsWith(prefix)) {
                        String value = request.getParameter(parameterName);
                        paramList.add(value);
                    }
                } else {
                    String value = request.getParameter(parameterName);
                    paramList.add(value);
                }
            }
        }
        return paramList;
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

    private static Locale getLocale(HttpServletRequest request, HttpSession session) {
        Map userLogin = (Map) session.getAttribute("userLogin");
        if (userLogin == null) {
            userLogin = (Map) session.getAttribute("autoUserLogin");
        }
        
        Object localeObject = null;

        // check userLogin first
        if (userLogin != null) {
            localeObject = userLogin.get("lastLocale");
        }

        // session second
        if (localeObject == null) {
            localeObject = session != null ? session.getAttribute("locale") : null;
        }

        // finally request (w/ a fall back to default)
        if (localeObject == null) {
            localeObject = request != null ? request.getLocale() : null;
        }

        return UtilMisc.ensureLocale(localeObject);
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
    public static Locale getLocale(HttpSession session) {
        if (session == null) return Locale.getDefault();
        return UtilHttp.getLocale(null, session);
    }

    public static void setLocale(HttpServletRequest request, String localeString) {
        UtilHttp.setLocale(request, UtilMisc.parseLocale(localeString));
    }

    public static void setLocale(HttpServletRequest request, Locale locale) {
        request.getSession().setAttribute("locale", locale);
    }

    /**
     * Get the currency string from the session.
     * @param session HttpSession object to use for lookup
     * @return String The ISO currency code
     */
    public static String getCurrencyUom(HttpSession session) {
        Map userLogin = (Map) session.getAttribute("userLogin");
        if (userLogin == null) {
            userLogin = (Map) session.getAttribute("autoUserLogin");
        }

        String iso = null;

        // check userLogin first
        if (userLogin != null) {
            iso = (String) userLogin.get("lastCurrencyUom");
        }

        // session next
        if (iso == null) {
            iso = (String) session.getAttribute("currencyUom");
        }

        // if none is set we will use the configured default
        if (iso == null) {

            try {
                iso = UtilProperties.getPropertyValue("general", "currency.uom.id.default", "USD");
            } catch (Exception e) {
                Debug.logWarning("Error getting the general:currency.uom.id.default value: " + e.toString(), module);
            }
        }


        // if still none we will use the default for whatever locale we can get...
        if (iso == null) {
            Currency cur = Currency.getInstance(getLocale(session));
            iso = cur.getCurrencyCode();
        }
        
        return iso;
    }

    /**
     * Get the currency string from the session.
     * @param request HttpServletRequest object to use for lookup
     * @return String The ISO currency code
     */
    public static String getCurrencyUom(HttpServletRequest request) {
        return getCurrencyUom(request.getSession());
    }

    /** Simple event to set the users per-session currency uom value */
    public static void setCurrencyUom(HttpServletRequest request, String currencyUom) {
        request.getSession().setAttribute("currencyUom", currencyUom);
    }

    /** URL Encodes a Map of arguements */
    public static String urlEncodeArgs(Map args) {
        StringBuffer buf = new StringBuffer();
        if (args != null) {
            Iterator i = args.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                String name = (String) entry.getKey();
                Object value = entry.getValue();
                String valueStr = null;
                if (name != null && value != null) {
                    if (value instanceof String) {
                        valueStr = (String) value;
                    } else {
                        valueStr = value.toString();
                    }

                    if (valueStr != null && valueStr.length() > 0) {
                        if (buf.length() > 0) buf.append('&');
                        try {
                            buf.append(URLEncoder.encode(name, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            Debug.logError(e, module);
                        }
                        buf.append('=');
                        try {
                            buf.append(URLEncoder.encode(valueStr, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            Debug.logError(e, module);
                        }
                    }
                }
            }
        }
        return buf.toString();
    }

    public static String setResponseBrowserProxyNoCache(HttpServletRequest request, HttpServletResponse response) {
        setResponseBrowserProxyNoCache(response);
        return "success";
    }

    public static void setResponseBrowserProxyNoCache(HttpServletResponse response) {
        long nowMillis = System.currentTimeMillis();
        response.setDateHeader("Expires", nowMillis);
        response.setDateHeader("Last-Modified", nowMillis); // always modified
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate"); // HTTP/1.1
        response.addHeader("Cache-Control", "post-check=0, pre-check=0, false");
        response.setHeader("Pragma", "no-cache"); // HTTP/1.0
    }

    public static String getContentTypeByFileName(String fileName) {
        FileNameMap mime = URLConnection.getFileNameMap();
        return mime.getContentTypeFor(fileName);
    }

    /**
     * Stream an array of bytes to the browser
     * This method will close the ServletOutputStream when finished
     *
     * @param response HttpServletResponse object to get OutputStream from
     * @param bytes Byte array of content to stream
     * @param contentType The content type to pass to the browser
     * @throws IOException
     */
    public static void streamContentToBrowser(HttpServletResponse response, byte[] bytes, String contentType) throws IOException {
        // tell the browser not the cache
        setResponseBrowserProxyNoCache(response);

        // set the response info
        response.setContentLength(bytes.length);
        if (contentType != null) {
            response.setContentType(contentType);
        }

        // create the streams
        OutputStream out = response.getOutputStream();
        InputStream in = new ByteArrayInputStream(bytes);

        // stream the content
        try {
            streamContent(out, in, bytes.length);
        } catch (IOException e) {
            in.close();
            out.close(); // should we close the ServletOutputStream on error??
            throw e;
        }

        // close the input stream
        in.close();

        // close the servlet output stream
        out.flush();
        out.close();
    }

    /**
     * Streams content from InputStream to the ServletOutputStream
     * This method will close the ServletOutputStream when finished
     * This method does not close the InputSteam passed
     *
     * @param response HttpServletResponse object to get OutputStream from
     * @param in InputStream of the actual content
     * @param length Size (in bytes) of the content
     * @param contentType The content type to pass to the browser
     * @throws IOException
     */
    public static void streamContentToBrowser(HttpServletResponse response, InputStream in, int length, String contentType) throws IOException {
        // tell the browser not the cache
        setResponseBrowserProxyNoCache(response);

        // set the response info
        response.setContentLength(length);
        if (contentType != null) {
            response.setContentType(contentType);
        }

        // stream the content
        OutputStream out = response.getOutputStream();
        try {
            streamContent(out, in, length);
        } catch (IOException e) {
            out.close();
            throw e;
        }

        // close the servlet output stream
        out.flush();
        out.close();
    }

    /**
     * Stream binary content from InputStream to OutputStream
     * This method does not close the streams passed
     *
     * @param out OutputStream content should go to
     * @param in InputStream of the actual content
     * @param length Size (in bytes) of the content
     * @throws IOException
     */
    public static void streamContent(OutputStream out, InputStream in, int length) throws IOException {
        int bufferSize = 512; // same as the default buffer size; change as needed

        // make sure we have something to write to
        if (out == null) {
            throw new IOException("Attempt to write to null output stream");
        }

        // make sure we have something to read from
        if (in == null) {
            throw new IOException("Attempt to read from null input stream");
        }

        // make sure we have some content
        if (length == 0) {
            throw new IOException("Attempt to write 0 bytes of content to output stream");
        }

        // initialize the buffered streams
        BufferedOutputStream bos = new BufferedOutputStream(out, bufferSize);
        BufferedInputStream bis = new BufferedInputStream(in, bufferSize);

        byte[] buffer = new byte[length];
        int read = 0;
        try {
            while ((read = bis.read(buffer, 0, buffer.length)) != -1) {
                bos.write(buffer, 0, read);
            }
        } catch (IOException e) {
            Debug.logError(e, "Problem reading/writing buffers", module);
            bis.close();
            bos.close();
            throw e;
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (bos != null) {
                bos.flush();
                bos.close();
            }
        }
    }
}
