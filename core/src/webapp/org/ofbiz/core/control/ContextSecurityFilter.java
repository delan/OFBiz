/*
 * $Id$
 */

package org.ofbiz.core.control;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> ContextSecurityFilter.java
 * <p><b>Description:</b> Security Filter to restrict access to raw JSP pages.
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
 *@created    September 23, 2001
 *@version    1.0
 */
public class ContextSecurityFilter implements Filter {

    public FilterConfig config;

    public void init(FilterConfig config) {
        this.config = config;
    }

    public void setFilterConfig(FilterConfig config) {
        this.config = config;
    }

    public FilterConfig getFilterConfig() {
        return config;
    }
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse) response);

        //test to see if we have come through the control servlet already, if not do the processing
        if (request.getAttribute(SiteDefs.FORWARDED_FROM_CONTROL_SERVLET) == null) {
            String allowedPath = config.getInitParameter("allowedPaths");
            String redirectPath = config.getInitParameter("redirectPath");
            String errorCode = config.getInitParameter("errorCode");

            List allowList = StringUtil.split(allowedPath, ":");
            allowList.add("/");    // No path is allowed.
            allowList.add("");      // No path is allowed.

            //Debug.logInfo("[ContextSecurityFilter.debug] : " + httpRequest.getRequestURI());

            String requestPath = httpRequest.getServletPath();
            if (requestPath == null) requestPath = "";

            if (requestPath.lastIndexOf("/") > 0) {
                if (requestPath.indexOf("/") == 0)
                    requestPath = "/" + requestPath.substring(1, requestPath.indexOf("/", 1));
                else
                    requestPath = requestPath.substring(1, requestPath.indexOf("/"));
            }

            String requestInfo = httpRequest.getServletPath();
            if (requestInfo == null) requestInfo = "";

            if (requestInfo.lastIndexOf("/") >= 0) {
                requestInfo = requestInfo.substring(0, requestInfo.lastIndexOf("/")) + "/*";
            }

            StringBuffer contextUriBuffer = new StringBuffer();
            if (httpRequest.getContextPath() != null)
                contextUriBuffer.append(httpRequest.getContextPath());
            if (httpRequest.getServletPath() != null)
                contextUriBuffer.append(httpRequest.getServletPath());
            if (httpRequest.getPathInfo() != null)
                contextUriBuffer.append(httpRequest.getPathInfo());
            String contextUri = contextUriBuffer.toString();

            /* Debugging
            for ( int i = 0; i < allowList.size(); i++ ) {
                Debug.logInfo("[ContextSecurityFilter.debug] : allow - " + ((String)allowList.get(i)));
            }
            Debug.logInfo("[ContextSecurityFilter.debug] : request path - " + requestPath);
            Debug.logInfo("[ContextSecurityFilter.debug] : request info - " + requestInfo);
            Debug.logInfo("[ContextSecurityFilter.debug] : servlet path - " + httpRequest.getServletPath());
            */

            if (!allowList.contains(requestPath) && !allowList.contains(requestInfo) &&
                    !allowList.contains(httpRequest.getServletPath())) {
                String filterMessage = "[ContextSecurityFilter] : Filtered request - " + contextUri;
                if (redirectPath == null) {
                    int error;
                    try {
                        error = Integer.parseInt(errorCode);
                    } catch (NumberFormatException nfe) {
                        error = 404;
                    }
                    filterMessage = filterMessage + " (" + error + ")";
                    wrapper.sendError(error, contextUri);
                } else {
                    filterMessage = filterMessage + " (" + redirectPath + ")";
                    wrapper.sendRedirect(httpRequest.getContextPath() + redirectPath);
                    //request.getRequestDispatcher(redirectPath).forward(request, response);
                }
                Debug.logInfo(filterMessage);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    public void destroy() {
        config = null;
    }
}
