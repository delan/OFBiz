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
 */
package org.ofbiz.core.control;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.security.Security;
import org.ofbiz.core.security.SecurityConfigurationException;
import org.ofbiz.core.security.SecurityFactory;
import org.ofbiz.core.service.LocalDispatcher;
import org.ofbiz.core.service.WebAppDispatcher;
import org.ofbiz.core.stats.ServerHitBin;
import org.ofbiz.core.util.CachedClassLoader;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.StringUtil;
import org.ofbiz.core.util.UtilHttp;
import org.ofbiz.core.util.UtilJ2eeCompat;
import org.ofbiz.core.util.UtilTimer;
import org.ofbiz.core.util.UtilValidate;
import org.ofbiz.core.view.JPublishWrapper;

import com.ibm.bsf.BSFManager;

/**
 * ControlServlet.java - Master servlet for the web application.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a> 
 * @version    $Revision$
 * @since      2.0
 */
public class ControlServlet extends HttpServlet {
    
    public static final String module = ControlServlet.class.getName();
          
    public ControlServlet() {
        super();
    }

    /**
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);        
        if (Debug.infoOn()) {
            Debug.logInfo("[ControlServlet.init] Loading Control Servlet mounted on path " + config.getServletContext().getRealPath("/"), module);
        }
                        
        // configure custom BSF engines
        configureBsf();
        // initialize the request handler
        getRequestHandler();
        // initialize the JPublish wrapper
        getJPublishWrapper();
    }

    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {                
        // setup DEFAULT chararcter encoding and content type, this will be overridden in the RequestHandler for view rendering
        String charset = getServletContext().getInitParameter("charset");
        if (charset == null || charset.length() == 0) charset = request.getCharacterEncoding();
        if (charset == null || charset.length() == 0) charset = "UTF-8";
        if (!"none".equals(charset)) {
            request.setCharacterEncoding(charset);
        }

        // setup content type
        String contentType = "text/html";
        if (charset.length() > 0 && !"none".equals(charset)) {
            response.setContentType(contentType + "; charset=" + charset);
        } else {
            response.setContentType(contentType);
        }

        long requestStartTime = System.currentTimeMillis();
        HttpSession session = request.getSession();        
        
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        // workaraound if we are in the root webapp
        String webappName = UtilHttp.getApplicationName(request);

        String rname = "";
        if (request.getPathInfo() != null) {
            rname = request.getPathInfo().substring(1);
        }
        if (rname.indexOf('/') > 0) {
            rname = rname.substring(0, rname.indexOf('/'));
        }

        UtilTimer timer = null;
        if (Debug.timingOn()) {
            timer = new UtilTimer();
            timer.setLog(true);
            timer.timerString("[" + rname + "] Servlet Starting, doing setup", module);
        }
        
        // Setup the CONTROL_PATH for JSP dispatching.
        request.setAttribute(SiteDefs.CONTROL_PATH, request.getContextPath() + request.getServletPath());
        if (Debug.verboseOn()) 
            Debug.logVerbose("Control Path: " + request.getAttribute(SiteDefs.CONTROL_PATH), module);

        // for convenience, and necessity with event handlers, make security and delegator available in the request:
        // try to get it from the session first so that we can have a delegator/dispatcher/security for a certain user if desired
        GenericDelegator delegator = null;
        String delegatorName = (String) session.getAttribute("delegatorName");
        if (UtilValidate.isNotEmpty(delegatorName)) {
            delegator = GenericDelegator.getGenericDelegator(delegatorName);
        }
        if (delegator == null) {
            delegator = (GenericDelegator) getServletContext().getAttribute("delegator");
        }
        if (delegator == null) {
            Debug.logError("[ControlServlet] ERROR: delegator not found in ServletContext", module);
        } else {
            request.setAttribute("delegator", delegator);
            // always put this in the session too so that session events can use the delegator
            session.setAttribute("delegatorName", delegator.getDelegatorName());
        }

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

        // display details on the servlet objects
        if (Debug.verboseOn()) {
            logRequestInfo(request);
        }

        if (Debug.timingOn()) timer.timerString("[" + rname + "] Setup done, doing Event(s) and View(s)", module);

        String errorPage = null;
        try {
            // the ServerHitBin call for the event is done inside the doRequest method
            getRequestHandler().doRequest(request, response, null, userLogin, delegator);
        } catch (RequestHandlerException e) {
            Throwable throwable = e.getNested() != null ? e.getNested() : e;

            Debug.logError(throwable, "Error in request handler: ", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, throwable.toString());
            errorPage = getRequestHandler().getDefaultErrorPage(request);
        } catch (Exception e) {
            Debug.logError(e, "Error in request handler: ", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, e.toString());
            errorPage = getRequestHandler().getDefaultErrorPage(request);
        }

        // Forward to the JSP
        // if (Debug.infoOn()) Debug.logInfo("[" + rname + "] Event done, rendering page: " + nextPage, module);
        // if (Debug.timingOn()) timer.timerString("[" + rname + "] Event done, rendering page: " + nextPage, module);

        if (errorPage != null) {
            // some containers call filters on EVERY request, even forwarded ones, so let it know that it came from the control servlet
            request.setAttribute(ContextFilter.FORWARDED_FROM_SERVLET, new Boolean(true));
            RequestDispatcher rd = request.getRequestDispatcher(errorPage);

            // use this request parameter to avoid infinite looping on errors in the error page...
            if (request.getAttribute("_ERROR_OCCURRED_") == null) {
                request.setAttribute("_ERROR_OCCURRED_", new Boolean(true));
                if (rd != null) rd.include(request, response);
            } else {
                String errorMessage = "ERROR in error page, avoiding infinite loop, but here is the text just in case it helps you: " + request.getAttribute(SiteDefs.ERROR_MESSAGE);

                if (UtilJ2eeCompat.useOutputStreamNotWriter(getServletContext())) {
                    response.getOutputStream().print(errorMessage);
                } else {
                    response.getWriter().print(errorMessage);
                }
            }
        }

        ServerHitBin.countRequest(webappName + "." + rname, request, requestStartTime, System.currentTimeMillis() - requestStartTime, userLogin, delegator);
        if (Debug.timingOn()) timer.timerString("[" + rname + "] Done rendering page, Servlet Finished", module);                
    }
    
    /**
     * @see javax.servlet.Servlet#destroy()
     */
    public void destroy() {
        super.destroy();                
    }    
    
    protected RequestHandler getRequestHandler() {
        RequestHandler rh = (RequestHandler) getServletContext().getAttribute(SiteDefs.REQUEST_HANDLER);
        if (rh == null) {
            rh = new RequestHandler();
            rh.init(getServletContext());
            getServletContext().setAttribute(SiteDefs.REQUEST_HANDLER, rh);
        }
        return rh;
    }

    protected JPublishWrapper getJPublishWrapper() {
        JPublishWrapper jp = (JPublishWrapper) getServletContext().getAttribute("jpublishWrapper");
        if ( jp == null) {
            jp = new JPublishWrapper(getServletContext());
            getServletContext().setAttribute("jpublishWrapper", jp);
        }
        return jp;
    }    
        
    protected void configureBsf() {
        String[] bshExtensions = {"bsh"};
        BSFManager.registerScriptingEngine("beanshell", "org.ofbiz.core.util.OfbizBshBsfEngine", bshExtensions);        

        String[] jsExtensions = {"js"};
        BSFManager.registerScriptingEngine("javascript", "org.ofbiz.core.util.OfbizJsBsfEngine", jsExtensions);
        
        String[] smExtensions = {"sm"};
        BSFManager.registerScriptingEngine("simplemethod", "org.ofbiz.core.minilang.SimpleMethodBsfEngine", smExtensions);
    }
    
    protected void logRequestInfo(HttpServletRequest request) {
        ServletContext servletContext = this.getServletContext();
        HttpSession session = request.getSession();
              
        Debug.logVerbose("--- Start Request Headers: ---", module);
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            Debug.logVerbose(headerName + ":" + request.getHeader(headerName), module);
        }
        Debug.logVerbose("--- End Request Headers: ---", module);        
       
        Debug.logVerbose("--- Start Request Parameters: ---", module);
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            Debug.logVerbose(paramName + ":" + request.getParameter(paramName), module);
        }
        Debug.logVerbose("--- End Request Parameters: ---", module);
                
        Debug.logVerbose("--- Start Request Attributes: ---", module);
        Enumeration reqNames = request.getAttributeNames();
        while (reqNames != null && reqNames.hasMoreElements()) {
            String attName = (String) reqNames.nextElement();
            Debug.logVerbose(attName + ":" + request.getAttribute(attName), module);
        }
        Debug.logVerbose("--- End Request Attributes ---", module);

        Debug.logVerbose("--- Start Session Attributes: ---", module);
        Enumeration sesNames = null;
        try {
            sesNames = session.getAttributeNames();                
        } catch (IllegalStateException e) {
            Debug.logVerbose("Cannot get session attributes : " + e.getMessage(), module);
        }        
        while (sesNames != null && sesNames.hasMoreElements()) {
            String attName = (String) sesNames.nextElement();
            Debug.logVerbose(attName + ":" + session.getAttribute(attName), module);
        }
        Debug.logVerbose("--- End Session Attributes ---", module);
        
        Enumeration appNames = servletContext.getAttributeNames();
        Debug.logVerbose("--- Start ServletContext Attributes: ---", module);
        while (appNames != null && appNames.hasMoreElements()) {
            String attName = (String) appNames.nextElement();
            Debug.logVerbose(attName + ":" + servletContext.getAttribute(attName), module);
        }
        Debug.logVerbose("--- End ServletContext Attributes ---", module);             
    }    
}
