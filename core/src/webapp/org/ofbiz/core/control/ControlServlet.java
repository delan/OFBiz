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
    
    /** Creates new ControlServlet  */
    public ControlServlet() {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        Debug.logInfo("[ControlServlet.init] Loading Control Servlet mounted on path " + config.getServletContext().getRealPath("/"));
                        
        // if exists, start PoolMan
        try {
            Class poolManClass = Class.forName("com.codestudio.sql.PoolMan");
            Method startMethod = poolManClass.getMethod("start", null);
            startMethod.invoke(null, null);
            //Debug.logInfo("Found PoolMan Driver...");
        }
        catch(Exception ex) {
            Debug.logWarning("[ControlServlet.init] WARNING: PoolMan not found");
        }
        
        // initialize the request handler
        getRequestHandler();
        // initialize the delegator
        getDelegator();
        // initialize security
        getSecurity();
        // initialize the services dispatcher
        getDispatcher();        
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        
        String nextPage  = null;
        
        // Setup the CONTROL_PATH for JSP dispatching.
        request.setAttribute(SiteDefs.CONTROL_PATH, request.getContextPath() + request.getServletPath());
        // Debug.logInfo("Control Path: " + request.getAttribute(SiteDefs.CONTROL_PATH));
        
        StringBuffer request_url = new StringBuffer();
        request_url.append(request.getScheme());
        request_url.append("://" + request.getServerName());
        if ( request.getServerPort() != 80 && request.getServerPort() != 443 )
            request_url.append(":" + request.getServerPort());
        request.setAttribute(SiteDefs.SERVER_ROOT_URL,request_url.toString());
        
        // Store some first hit client info for later.
        if ( session.isNew() ) {
            request_url.append(request.getRequestURI());
            if ( request.getQueryString() != null )
                request_url.append("?" + request.getQueryString());
            session.setAttribute(SiteDefs.CLIENT_LOCALE,request.getLocale());
            session.setAttribute(SiteDefs.CLIENT_REQUEST,request_url.toString());
            session.setAttribute(SiteDefs.CLIENT_USER_AGENT,request.getHeader("User-Agent"));
            session.setAttribute(SiteDefs.CLIENT_REFERER,(request.getHeader("Referer") != null ? request.getHeader("Referer") : "" ));
        }
        
        // for convenience, and necessity with event handlers, make security and delegator available in the request:
        GenericDelegator delegator = (GenericDelegator)getServletContext().getAttribute("delegator");
        if(delegator == null) Debug.logError("[ControlServlet] ERROR: delegator not found in ServletContext");
        request.setAttribute("delegator", delegator);
        
        Security security = (Security)getServletContext().getAttribute("security");
        if(security == null) Debug.logError("[ControlServlet] ERROR: security not found in ServletContext");
        request.setAttribute("security", security);
        
        // for use in Events the filesystem path of context root.
        request.setAttribute(SiteDefs.CONTEXT_ROOT,getServletContext().getRealPath("/"));
        
        try {
            nextPage = getRequestHandler().doRequest(request,response, null);
        } catch( Exception e ) {
            e.printStackTrace();
            request.setAttribute(SiteDefs.ERROR_MESSAGE,e.getMessage());
            nextPage = getRequestHandler().getDefaultErrorPage(request);
        }
        
        // Forward to the JSP
        Debug.logInfo("Dispatching to: " + nextPage);
        if(nextPage != null) {
            RequestDispatcher rd = request.getRequestDispatcher(nextPage);
            if(rd != null) rd.forward(request,response);
        }
    }
    
    private RequestHandler getRequestHandler() {
        RequestHandler rh = (RequestHandler) getServletContext().getAttribute(SiteDefs.REQUEST_HANDLER);
        if ( rh == null ) {
            rh = new RequestHandler();
            rh.init(getServletContext());
            getServletContext().setAttribute(SiteDefs.REQUEST_HANDLER,rh);
        }
        return rh;
    }
    
    private LocalDispatcher getDispatcher() {
        LocalDispatcher dispatcher = (LocalDispatcher) getServletContext().getAttribute("dispatcher");
        if ( dispatcher == null ) {
            GenericDelegator delegator = getDelegator();
            if ( delegator == null ) {
                Debug.logError("[ControlServlet.init] ERROR: delegator not defined.");
                return null;
            }
            Collection readers = null; 
            String readerFiles = getServletContext().getInitParameter("serviceReaderUrls");            
            if ( readerFiles != null ) {                
                readers = new ArrayList();
                List readerList = StringUtil.split(readerFiles,";");                
                Iterator i = readerList.iterator();
                while ( i.hasNext() ) {                    
                    try {
                        String name = (String) i.next();                        
                        URL readerURL = getServletContext().getResource(name);
                        if ( readerURL != null )
                            readers.add(readerURL);                
                    }
                    catch ( NullPointerException npe ) {
                        Debug.logInfo("[ControlServlet.init] ERROR: Null pointer exception thrown.");
                    }
                    catch ( MalformedURLException e ) {
                        Debug.logError(e,"[ControlServlet.init] ERROR: cannot get URL from String.");
                    }                    
                }
            }            
            String rootPath = getServletContext().getRealPath("/");
            dispatcher = new LocalDispatcher(getServletContext().getServletContextName(),rootPath,delegator,readers);                  
            getServletContext().setAttribute("dispatcher",dispatcher);
            if ( dispatcher == null )
                Debug.logError("[ControlServlet.init] ERROR: dispatcher could not be initialized.");                         
        }
        return dispatcher;
    }
                
    private GenericDelegator getDelegator() {
        GenericDelegator delegator = (GenericDelegator) getServletContext().getAttribute("delegator");
        if ( delegator == null ) {
            String delegatorName = getServletContext().getInitParameter(SiteDefs.ENTITY_DELEGATOR_NAME);
            if(delegatorName == null || delegatorName.length() <= 0)
                delegatorName = "default";
            Debug.logInfo("[ControlServlet.init] Getting Entity Engine Delegator with delegator name " + delegatorName);
            delegator = GenericDelegator.getGenericDelegator(delegatorName);
            getServletContext().setAttribute("delegator",delegator);
            if(delegator == null)
                Debug.logError("[ControlServlet.init] ERROR: delegator factory returned null for delegatorName \"" + delegatorName + "\"");
        }
        return delegator;
    }
    
    private Security getSecurity() {
        Security security = (Security) getServletContext().getAttribute("security");
        if ( security == null ) {
            GenericDelegator delegator = (GenericDelegator) getServletContext().getAttribute("delegator");
            if ( delegator != null )
                security = new Security(delegator);
            getServletContext().setAttribute("security",security);
            if ( security == null )
                Debug.logError("[ControlServlet.init] ERROR: security create failed.");
        }
        return security;
    }
}
