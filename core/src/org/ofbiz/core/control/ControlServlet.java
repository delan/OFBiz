/*
 * $Id$
 * $Log$
 * Revision 1.14  2001/09/06 03:30:27  azeneski
 * Removed control path debugging output.
 *
 * Revision 1.13  2001/08/28 02:21:45  azeneski
 * Moved helper and security to the request object rather then session. Added support for PoolMan connection pool.
 *
 * Revision 1.12  2001/08/25 17:29:11  azeneski
 * Started migrating Debug.log to Debug.logInfo and Debug.logError
 *
 * Revision 1.11  2001/08/25 01:42:01  azeneski
 * Seperated event processing, now is found totally in EventHandler.java
 * Updated all classes which deal with events to use to new handler.
 *
 * Revision 1.10  2001/08/24 17:14:34  azeneski
 * Removed plain text attribute and created a defination in SiteDefs.
 * NOTE: Need to update all pages/events which use the old name!
 *
 * Revision 1.9  2001/08/24 02:39:48  azeneski
 * Added logging of some initial client headers for application use.
 *
 * Revision 1.8  2001/08/22 14:07:43  jonesde
 * A few changes needed to get GenericWebEvent working
 *
 * Revision 1.7  2001/08/17 07:39:03  jonesde
 * Added initialization to ControlServlet, and put security and helper into the application scope (ServletContext). Other small changes to support this.
 *
 * Revision 1.6  2001/07/23 18:04:57  azeneski
 * Fixed runaway thread in the job scheduler.
 *
 * Revision 1.5  2001/07/19 20:50:22  azeneski
 * Added the job scheduler to 'core' module.
 *
 * Revision 1.4  2001/07/17 08:51:37  jonesde
 * Updated for auth implementation & small fixes.
 *
 * Revision 1.3  2001/07/17 03:01:51  azeneski
 * Fixed the double slash in CONTROL_PATH request attribute.
 *
 * Revision 1.2  2001/07/16 22:31:06  azeneski
 * Moved multi-site support to be handled by the webapp.
 *
 * Revision 1.1  2001/07/16 14:45:48  azeneski
 * Added the missing 'core' directory into the module.
 *
 * Revision 1.2  2001/07/15 23:27:36  azeneski
 * Removed old commented out references to SessionController from ControlServlet
 *
 * Revision 1.1  2001/07/15 16:36:42  azeneski
 * Initial Import
 *
 */

package org.ofbiz.core.control;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.scheduler.JobManager;
import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;

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
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on June 28, 2001, 10:12 PM
 */
public class ControlServlet extends HttpServlet {
    
    private JobManager jm = null;
    
    /** Creates new ControlServlet  */
    public ControlServlet() {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // setup the request handler
        getRequestHandler();
        //initialize the entity & security stuff
        String serverName = config.getServletContext().getInitParameter(SiteDefs.ENTITY_SERVER_NAME);
        if(serverName == null || serverName.length() <= 0) serverName = "default";
        GenericHelper helper = GenericHelperFactory.getDefaultHelper(serverName);
        if(helper == null)
            Debug.logError("[ControlServlet.init] ERROR: helper factory returned null for serverName \"" + serverName + "\"");
        Security security = new Security(helper);
        if(security == null)
            Debug.logError("[ControlServlet.init] ERROR: security create failed for serverName \"" + serverName + "\"");
        //add helper and security to the context
        getServletContext().setAttribute("helper", helper);
        getServletContext().setAttribute("security", security);
        // initialize the job scheduler
        jm = new JobManager(getServletContext(),helper);
        if ( jm == null )
            Debug.logError("[ControlServlet.init] ERROR: job scheduler init failed.");
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
        request.setAttribute(SiteDefs.JOB_MANAGER,jm);
        
        // Store some first hit client info for later.
        if ( session.isNew() ) {
            StringBuffer request_url = new StringBuffer();
            request_url.append(request.getScheme());
            request_url.append("://" + request.getServerName());
            if ( request.getServerPort() != 80 && request.getServerPort() != 443 )
                request_url.append(":" + request.getServerPort());
            session.setAttribute(SiteDefs.SERVER_ROOT_URL,request_url.toString());
            request_url.append(request.getRequestURI());
            if ( request.getQueryString() != null )
                request_url.append("?" + request.getQueryString());
            session.setAttribute(SiteDefs.CLIENT_LOCALE,request.getLocale());
            session.setAttribute(SiteDefs.CLIENT_REQUEST,request_url.toString());
            session.setAttribute(SiteDefs.CLIENT_USER_AGENT,request.getHeader("User-Agent"));
            session.setAttribute(SiteDefs.CLIENT_REFERER,(request.getHeader("Referer") != null ? request.getHeader("Referer") : "" ));
        }
        
        // for convenience, and necessity with event handlers, make security and helper available in the request:        
        GenericHelper helper = (GenericHelper)getServletContext().getAttribute("helper");
        if(helper == null) Debug.logError("[ControlServlet] ERROR: helper not found in ServletContext");
        request.setAttribute("helper", helper);
        
        Security security = (Security)getServletContext().getAttribute("security");
        if(security == null) Debug.logError("[ControlServlet] ERROR: security not found in ServletContext");
        request.setAttribute("security", security);        
        
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
    
    public void destroy() {
        if ( jm != null ) {
            jm.finalize();
            jm = null;
        }
    }
}

