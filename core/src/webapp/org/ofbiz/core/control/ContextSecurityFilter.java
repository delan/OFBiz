/*
 * $Id$
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
package org.ofbiz.core.control;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.StringUtil;

/**
 * ContextSecurityFilter - Security Filter to restrict access to raw files.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
 * @version    $Revision$
 * @since      2.0
 */
public class ContextSecurityFilter extends ControlServlet implements Filter {

    public static final String module = ContextSecurityFilter.class.getName();

    public FilterConfig config = null;

    public void init(FilterConfig config) {
        this.config = config;
        
        // this will speed up the initial sessionId generation
        new java.security.SecureRandom().nextLong();
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse) response);

        // set the ServletContext in the request for future use
        request.setAttribute("servletContext", config.getServletContext());
        
        // check if we are disabled
        String disableFilter = config.getInitParameter("disabled");
        if (disableFilter != null && "Y".equals(disableFilter)) {
            chain.doFilter(request, response);
            return;
        }
        
        // check if we are told to redirect everthing
        String redirectAllTo = config.getInitParameter("forceRedirectAll");                        
        if (redirectAllTo != null && redirectAllTo.length() > 0) {            
            // little trick here so we don't loop on ourself
            if (httpRequest.getSession().getAttribute("_FORCE_REDIRECT_") == null) {
                httpRequest.getSession().setAttribute("_FORCE_REDIRECT_", "true");
                Debug.logWarning("Redirecting user to: " + redirectAllTo, module);                  
                
                if (!redirectAllTo.toLowerCase().startsWith("http")) {
                    redirectAllTo = httpRequest.getContextPath() + redirectAllTo;
                }
                wrapper.sendRedirect(redirectAllTo);
                return;
            } else {                
                httpRequest.getSession().removeAttribute("_FORCE_REDIRECT_");
                chain.doFilter(request, response);
                return;
            }
        }

        // test to see if we have come through the control servlet already, if not do the processing
        if (request.getAttribute(SiteDefs.FORWARDED_FROM_CONTROL_SERVLET) == null) {
            String allowedPath = config.getInitParameter("allowedPaths");
            String redirectPath = config.getInitParameter("redirectPath");
            String errorCode = config.getInitParameter("errorCode");

            List allowList = StringUtil.split(allowedPath, ":");
            allowList.add("/");  // No path is allowed.
            allowList.add("");   // No path is allowed.

            if (Debug.verboseOn()) Debug.logVerbose("[Request]: " + httpRequest.getRequestURI(), module);

            String requestPath = httpRequest.getServletPath();
            if (requestPath == null) requestPath = "";
            if (requestPath.lastIndexOf("/") > 0) {
                if (requestPath.indexOf("/") == 0) {
                    requestPath = "/" + requestPath.substring(1, requestPath.indexOf("/", 1));
                } else {
                    requestPath = requestPath.substring(1, requestPath.indexOf("/"));
                }
            }

            String requestInfo = httpRequest.getServletPath();
            if (requestInfo == null) requestInfo = "";
            if (requestInfo.lastIndexOf("/") >= 0) {
                requestInfo = requestInfo.substring(0, requestInfo.lastIndexOf("/")) + "/*";
            }

            StringBuffer contextUriBuffer = new StringBuffer();
            if (httpRequest.getContextPath() != null) {
                contextUriBuffer.append(httpRequest.getContextPath());
            }
            if (httpRequest.getServletPath() != null) {
                contextUriBuffer.append(httpRequest.getServletPath());
            }
            if (httpRequest.getPathInfo() != null) {
                contextUriBuffer.append(httpRequest.getPathInfo());
            }
            String contextUri = contextUriBuffer.toString();

            // Verbose Debugging
            if (Debug.verboseOn()) {
                for (int i = 0; i < allowList.size(); i++) {
                    Debug.logVerbose("[Allow]: " + ((String) allowList.get(i)), module);
                }
                Debug.logVerbose("[Request path]: " + requestPath, module);
                Debug.logVerbose("[Request info]: " + requestInfo, module);
                Debug.logVerbose("[Servlet path]: " + httpRequest.getServletPath(), module);
            }

            // check to make sure the requested url is allowed
            if (!allowList.contains(requestPath) && !allowList.contains(requestInfo) && !allowList.contains(httpRequest.getServletPath())) {
                String filterMessage = "[Filtered request]: " + contextUri;

                if (redirectPath == null) {
                    int error = 404;
                    try {
                        error = Integer.parseInt(errorCode);
                    } catch (NumberFormatException nfe) {
                        Debug.logWarning(nfe, "Error code specified would not parse to Integer : " + errorCode, module);
                        error = 404;
                    }
                    filterMessage = filterMessage + " (" + error + ")";
                    wrapper.sendError(error, contextUri);
                } else {
                    filterMessage = filterMessage + " (" + redirectPath + ")";
                    if (!redirectPath.toLowerCase().startsWith("http")) {
                        redirectPath = httpRequest.getContextPath() + redirectPath;
                    }
                    wrapper.sendRedirect(redirectPath);                    
                }
                Debug.logWarning(filterMessage, module);
                return;
            }
        }

        // we're done checking; continue on
        chain.doFilter(request, response);
    }

    public void destroy() {
        config = null;
    }
}
