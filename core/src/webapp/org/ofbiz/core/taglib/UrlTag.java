/*
 * $Id$
 *
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.taglib;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.ofbiz.core.control.*;
import org.ofbiz.core.util.*;

/**
 * UrlTag - Creates a URL string prepending the current control path.
 *
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @version    1.0
 * @created    August 4, 2001
 */
public class UrlTag extends BodyTagSupport {

    public static final String module = UrlTag.class.getName();
    public static String httpsPort = UtilProperties.getPropertyValue("url.properties", "port.https", "443");
    public static String httpsServer = UtilProperties.getPropertyValue("url.properties", "force.https.host");
    public static String httpPort = UtilProperties.getPropertyValue("url.properties", "port.http", "80");
    public static String httpServer = UtilProperties.getPropertyValue("url.properties", "force.http.host");
    
    public int doEndTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();

        String controlPath = (String) request.getAttribute(SiteDefs.CONTROL_PATH);
        ServletContext context = (ServletContext) request.getAttribute("servletContext");
        RequestHandler rh = (RequestHandler) context.getAttribute(SiteDefs.REQUEST_HANDLER);
        RequestManager rm = rh.getRequestManager();

        BodyContent body = getBodyContent();

        String baseURL = body.getString();
        String requestUri = RequestHandler.getRequestUri(baseURL);

        StringBuffer newURL = new StringBuffer();

        boolean useHttps = UtilProperties.propertyValueEqualsIgnoreCase("url.properties", "port.https.enabled", "Y");

        if (useHttps) {
            if (rm.requiresHttps(requestUri) && !request.isSecure()) {
                String server = httpsServer;
                if (server == null || server.length() == 0) {
                    server = request.getServerName();
                }
                newURL.append("https://");
                newURL.append(server);
                if (!httpsPort.equals("443")) {
                    newURL.append(":" + httpsPort);
                }
            } else if (!rm.requiresHttps(requestUri) && request.isSecure()) {
                String server = httpServer;
                if (server == null || server.length() == 0) {
                    server = request.getServerName();
                }
                newURL.append("http://");
                newURL.append(server);
                if (!httpPort.equals("80")) {
                    newURL.append(":" + httpPort);
                }
            }
        }

        Debug.logVerbose("UseHTTPS: " + useHttps + " -- URI: " + requestUri + " -> " + rm.requiresHttps(requestUri), module);
        newURL.append(controlPath);
        newURL.append(baseURL);

        body.clearBody();

        try {
            String encodedURL = newURL.toString();
            /* This doesn't work, leaving here as a painful reminder that each parameter must be encoded independently
            //encode for character escaping, etc with the URLEncoder.encode method
            int qmLoc = encodedURL.indexOf('?');
            if (qmLoc > 0) {
                String encodedQueryString = java.net.URLEncoder.encode(encodedURL.substring(qmLoc + 1));
                encodedURL = encodedURL.subSequence(0, qmLoc) + "?" + encodedQueryString;
            }
            */
            //encode for session maintenance with the response.encodeURL method
            encodedURL = response.encodeURL(encodedURL);
            getPreviousOut().print(encodedURL);
        } catch (IOException e) {
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }
}
