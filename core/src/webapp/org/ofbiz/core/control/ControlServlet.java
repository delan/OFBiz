/*
 * $Id$
 */

package org.ofbiz.core.control;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.stats.*;
import org.ofbiz.core.config.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> ControlServlet.java
 * <p><b>Description:</b> Master servlet for the web application.
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
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    June 28, 2001
 *@version    1.0
 */
public class ControlServlet extends HttpServlet {

    //Debug module name
    public static final String module = ControlServlet.class.getName();

    /** Creates new ControlServlet  */
    public ControlServlet() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        Debug.logInfo("[ControlServlet.init] Loading Control Servlet mounted on path " +
                      config.getServletContext().getRealPath("/"), module);

        //clear the regions cache to avoid problems when reloading a webapp with a different classloader
        try {
            RegionCache.clearRegions(this.getServletContext().getResource(SiteDefs.REGIONS_CONFIG_LOCATION));
        } catch (java.net.MalformedURLException e) {
            Debug.logWarning(e, "Error clearing regions");
        }
        
        // initialize the delegator
        getDelegator();
        // initialize security
        getSecurity();
        // initialize the services dispatcher
        getDispatcher();
        // initialize the request handler
        getRequestHandler();

        // this will speed up the initial sessionId generation
        new java.security.SecureRandom().nextLong();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long requestStartTime = System.currentTimeMillis();
        String cname = request.getContextPath().substring(1);
        String rname = request.getPathInfo().substring(1);
        if (rname.indexOf('/') > 0) {
            rname = rname.substring(0, rname.indexOf('/'));
        }
        
        UtilTimer timer = null;
        if (Debug.timingOn()) {
            timer = new UtilTimer();
            timer.setLog(true);
            timer.timerString("[" + rname + "] Servlet Starting, doing setup", module);
        }

        if (Debug.verboseOn()) {
            Debug.logVerbose("--- Start Request Headers: ---", module);
            Enumeration headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = (String) headerNames.nextElement();
                Debug.logVerbose(headerName + ":" + request.getHeader(headerName), module);
            }
            Debug.logVerbose("--- End Request Headers: ---", module);
        }

        //NOTE: This is not within an if block for verboseOn because it fixes a problem
        //  with funny characters in the parameters that can seriously mess things up, weird but true...
        Debug.logVerbose("--- Start Request Parameters: ---", module);
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            Debug.logVerbose(paramName + ":" + request.getParameter(paramName), module);
        }
        Debug.logVerbose("--- End Request Parameters: ---", module);
        
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);

        HttpSession session = request.getSession();

        String charset = getServletContext().getInitParameter("charset");
        if (request.getCharacterEncoding() == null) {
            if (charset == null) {
                request.setCharacterEncoding("UTF-8");
            } else {
                request.setCharacterEncoding(charset);
            }
        }
        if (charset != null) {
            response.setContentType("text/html; charset=" + charset);
        }

        // Setup the CONTROL_PATH for JSP dispatching.
        request.setAttribute(SiteDefs.CONTROL_PATH, request.getContextPath() + request.getServletPath());
        // Debug.logInfo("Control Path: " + request.getAttribute(SiteDefs.CONTROL_PATH), module);

        StringBuffer request_url = new StringBuffer();
        request_url.append(request.getScheme());
        request_url.append("://" + request.getServerName());
        if (request.getServerPort() != 80 && request.getServerPort() != 443)
            request_url.append(":" + request.getServerPort());
        request.setAttribute(SiteDefs.SERVER_ROOT_URL, request_url.toString());

        // Store some first hit client info for later.
        if (session.isNew()) {
            request_url.append(request.getRequestURI());
            if (request.getQueryString() != null)
                request_url.append("?" + request.getQueryString());
            session.setAttribute(SiteDefs.CLIENT_LOCALE, request.getLocale());
            session.setAttribute(SiteDefs.CLIENT_REQUEST, request_url.toString());
            session.setAttribute(SiteDefs.CLIENT_USER_AGENT, request.getHeader("User-Agent"));
            session.setAttribute(SiteDefs.CLIENT_REFERER, (request.getHeader("Referer") != null ?
                                                           request.getHeader("Referer") : ""));
        }

        // for convenience, and necessity with event handlers, make security and delegator available in the request:
        //  try to get it from the session first so that we can have a delegator/dispatcher/security for a certain user if desired
        GenericDelegator delegator = (GenericDelegator) session.getAttribute("delegator");
        if (delegator == null) {
            delegator = (GenericDelegator) getServletContext().getAttribute("delegator");
        }
        if (delegator == null) {
            Debug.logError("[ControlServlet] ERROR: delegator not found in ServletContext", module);
        }
        request.setAttribute("delegator", delegator);
        
        LocalDispatcher dispatcher = (LocalDispatcher) session.getAttribute("dispatcher");
        if (dispatcher == null) {
            dispatcher = (LocalDispatcher) getServletContext().getAttribute("dispatcher");
        }
        if (dispatcher == null) {
            Debug.logError("[ControlServlet] ERROR: dispatcher not found in ServletContext", module);
        }
        request.setAttribute("dispatcher", dispatcher);

        Security security = (Security) session.getAttribute("security");
        if (security == null) {
            security = (Security) getServletContext().getAttribute("security");
        }
        if (security == null) {
            Debug.logError("[ControlServlet] ERROR: security not found in ServletContext", module);
        }
        request.setAttribute("security", security);

        // for use in Events the filesystem path of context root.
        request.setAttribute(SiteDefs.CONTEXT_ROOT, getServletContext().getRealPath("/"));

        // Because certain app servers are lame and don't support the HttpSession.getServletContext method,
        // we put it in the request here
        ServletContext servletContext = getServletContext();
        request.setAttribute("servletContext", servletContext);

        if (Debug.timingOn()) timer.timerString("[" + rname + "] Setup done, doing Event(s) and View(s)", module);

        String errorPage = null;
        try {
            //the ServerHitBin call for the event is done inside the doRequest method
            getRequestHandler().doRequest(request, response, null, userLogin, delegator);
        } catch (Exception e) {
            Debug.logError(e, "Error in request handler: ");
            request.setAttribute(SiteDefs.ERROR_MESSAGE, e.getMessage());
            errorPage = getRequestHandler().getDefaultErrorPage(request);
        }

        // Forward to the JSP
        // Debug.logInfo("[" + rname + "] Event done, rendering page: " + nextPage, module);
        // if (Debug.timingOn()) timer.timerString("[" + rname + "] Event done, rendering page: " + nextPage, module);

        if (errorPage != null) {
            //some containers call filters on EVERY request, even forwarded ones, so let it know that it came from the control servlet
            request.setAttribute(SiteDefs.FORWARDED_FROM_CONTROL_SERVLET, new Boolean(true));
            RequestDispatcher rd = request.getRequestDispatcher(errorPage);
            
            //use this request parameter to avoid infinite looping on errors in the error page...
            if (request.getAttribute("_ERROR_OCCURRED_") == null) {
                request.setAttribute("_ERROR_OCCURRED_", new Boolean(true));
                if (rd != null) rd.forward(request, response);
            } else {
                response.getWriter().print("ERROR in error page, but here is the text anyway: " + request.getAttribute(SiteDefs.ERROR_MESSAGE));
            }
        }

        if (Debug.timingOn()) timer.timerString("[" + rname + "] Done rendering page, Servlet Finished", module);

        ServerHitBin.countRequest(cname + "." + rname, session.getId(), requestStartTime, System.currentTimeMillis() - requestStartTime, userLogin, delegator);
    }

    private RequestHandler getRequestHandler() {
        RequestHandler rh = (RequestHandler) getServletContext().getAttribute(SiteDefs.REQUEST_HANDLER);
        if (rh == null) {
            rh = new RequestHandler();
            rh.init(getServletContext());
            getServletContext().setAttribute(SiteDefs.REQUEST_HANDLER, rh);
        }
        return rh;
    }

    private LocalDispatcher getDispatcher() {
        LocalDispatcher dispatcher = (LocalDispatcher) getServletContext().getAttribute("dispatcher");
        if (dispatcher == null) {
            GenericDelegator delegator = getDelegator();
            if (delegator == null) {
                Debug.logError("[ControlServlet.init] ERROR: delegator not defined.", module);
                return null;
            }
            Collection readers = null;
            String readerFiles = getServletContext().getInitParameter("serviceReaderUrls");
            if (readerFiles != null) {
                readers = new ArrayList();
                List readerList = StringUtil.split(readerFiles, ";");
                Iterator i = readerList.iterator();
                while (i.hasNext()) {
                    try {
                        String name = (String) i.next();
                        URL readerURL = getServletContext().getResource(name);
                        if (readerURL != null)
                            readers.add(readerURL);
                    } catch (NullPointerException npe) {
                        Debug.logInfo(npe, "[ControlServlet.init] ERROR: Null pointer exception thrown.", module);
                    } catch (MalformedURLException e) {
                        Debug.logError(e, "[ControlServlet.init] ERROR: cannot get URL from String.", module);
                    }
                }
            }
            // get the unique name of this dispatcher
            String dispatcherName = getServletContext().getInitParameter("localDispatcherName");
            if (dispatcherName == null)
                Debug.logError("No localDispatcherName specified in the web.xml file", module);
            dispatcher = new LocalDispatcher(dispatcherName, delegator, readers);
            getServletContext().setAttribute("dispatcher", dispatcher);
            if (dispatcher == null)
                Debug.logError("[ControlServlet.init] ERROR: dispatcher could not be initialized.", module);
        }
        return dispatcher;
    }

    private GenericDelegator getDelegator() {
        GenericDelegator delegator = (GenericDelegator) getServletContext().getAttribute("delegator");
        if (delegator == null) {
            String delegatorName = getServletContext().getInitParameter(SiteDefs.ENTITY_DELEGATOR_NAME);
            if (delegatorName == null || delegatorName.length() <= 0)
                delegatorName = "default";
            Debug.logInfo("[ControlServlet.init] Getting Entity Engine Delegator with delegator name " +
                          delegatorName, module);
            delegator = GenericDelegator.getGenericDelegator(delegatorName);
            getServletContext().setAttribute("delegator", delegator);
            if (delegator == null)
                Debug.logError("[ControlServlet.init] ERROR: delegator factory returned null for delegatorName \"" +
                               delegatorName + "\"", module);
        }
        return delegator;
    }

    private Security getSecurity() {
        Security security = (Security) getServletContext().getAttribute("security");
        if (security == null) {
            GenericDelegator delegator = (GenericDelegator) getServletContext().getAttribute("delegator");
            if (delegator != null)
                security = new Security(delegator);
            getServletContext().setAttribute("security", security);
            if (security == null)
                Debug.logError("[ControlServlet.init] ERROR: security create failed.", module);
        }
        return security;
    }
}
