/*
 * $Id$
 * $Log$
 */

package org.ofbiz.core.control;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

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
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        // SessionController sessionController = getSessionController(session);
        
        String nextPage  = null;
        String siteId = null;
        
        siteId = request.getParameter(SiteDefs.SITE_PARAM);
        if ( siteId == null ) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE,"No Site Parameter Specified.");
            nextPage = getRequestHandler().getDefaultErrorPage(request);
            Debug.log("Request Error: No Site Parameter Specified.");
        }
        else {
            try {
                nextPage = getRequestHandler().doRequest(request,response);
            } catch( Exception e ) {
                e.printStackTrace();
                request.setAttribute(SiteDefs.ERROR_MESSAGE,e.getMessage());
                nextPage = getRequestHandler().getDefaultErrorPage(request);
            }
        }
        
        // Forward to the JSP
        Debug.log("Dispatching to: " + nextPage);
        getServletConfig().getServletContext().getRequestDispatcher(nextPage).forward(request,response);
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
    
    /*
    private SessionController getSessionController(HttpSession session) {
        SessionController sc = (SessionController) session.getAttribute("_SESSIONCONTROL_");
        if ( sc == null ) {
            sc = new SessionController();
            sc.init(getServletContext(),session);
            session.setAttribute("_SESSIONCONTROL_");
        }
        return sc;
    }
     */
    
}

