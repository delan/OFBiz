/*
 * $Id$
 * $Log$
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
            
    /** Creates new ControlServlet  */
    public ControlServlet() {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        getRequestHandler();
        getJobManager();
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        
        String nextPage  = null;

        /** Setup the CONTROL_PATH for JSP dispatching. */
        request.setAttribute(SiteDefs.CONTROL_PATH, request.getContextPath() + request.getServletPath());
        Debug.log("Control Path: " + request.getAttribute(SiteDefs.CONTROL_PATH));
        /** Setup the SERVLET_CONTEXT for events. */
        request.setAttribute(SiteDefs.SERVLET_CONTEXT,getServletContext());
        
        try {
            nextPage = getRequestHandler().doRequest(request,response, null);
        } catch( Exception e ) {
            e.printStackTrace();
            request.setAttribute(SiteDefs.ERROR_MESSAGE,e.getMessage());
            nextPage = getRequestHandler().getDefaultErrorPage(request);
        }
        
        // Forward to the JSP
        Debug.log("Dispatching to: " + nextPage);
        if(nextPage != null)
        {
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
    
    private JobManager getJobManager() {
        JobManager jm = (JobManager) getServletContext().getAttribute(SiteDefs.JOB_MANAGER);
        if ( jm == null ) {
            jm = new JobManager(getServletContext());
            getServletContext().setAttribute(SiteDefs.JOB_MANAGER,jm);
        }
        return jm;
    }
}

