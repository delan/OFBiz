/*
 * $Id$
 * $Log$
 * Revision 1.4  2001/07/17 08:51:37  jonesde
 * Updated for auth implementation & small fixes.
 *
 * Revision 1.3  2001/07/17 03:45:09  azeneski
 * Changed request and view config to NOT use the leading '/'. All request and
 * view mappings should now leave be 'request' instead of '/request'.
 *
 * Revision 1.2  2001/07/16 22:31:06  azeneski
 * Moved multi-site support to be handled by the webapp.
 *
 * Revision 1.1  2001/07/16 14:45:48  azeneski
 * Added the missing 'core' directory into the module.
 *
 * Revision 1.1  2001/07/15 16:36:42  azeneski
 * Initial Import
 *
 */

package org.ofbiz.core.control;

import java.io.Serializable;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.naming.InitialContext;
import java.lang.reflect.*;

import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.Debug;

/**
 * <p><b>Title:</b> RequestHandler.java
 * <p><b>Description:</b> Request Processor Object
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
 * @author David E. Jones
 * @version 1.0
 * Created on June 28, 2001, 10:12 PM
 */
public class RequestHandler implements Serializable {
    
    private ServletContext context;
    private RequestManager rm;
    
    public RequestHandler() {}
    
    public void init( ServletContext context ) {
        this.context = context;
        Debug.log("Loading RequestManager...");
        rm = new RequestManager(context);
    }
    
    public String doRequest( HttpServletRequest request, HttpServletResponse response, String chain ) throws RequestHandlerException {
        String requestUri = null;
        String eventType = null;
        String eventPath = null;
        String eventMethod = null;
        String nextView = null;
        String nextPage = null;
        boolean chainRequest = false;
        
        /** Grab data from request object to process. */        
        requestUri = getRequestUri(request.getPathInfo());
        nextView = getNextPageUri(request.getPathInfo());
        
        /** Check for chained request. */
        if ( chain != null ) {
            requestUri = getRequestUri(chain);
            nextView = getNextPageUri(chain);
        }
        
        Debug.log("***Request: " + requestUri);
                
        String eventReturnString = null;
        /** Perform security check. */
        if(rm.requiresAuth(requestUri))
        {
          // Invoke the security handler
          // catch exceptions and throw RequestHandlerException if failed.
          String checkLoginType = rm.getEventType(SiteDefs.CHECK_LOGIN_REQUEST_URI);
          String checkLoginPath = rm.getEventPath(SiteDefs.CHECK_LOGIN_REQUEST_URI);
          String checkLoginMethod = rm.getEventMethod(SiteDefs.CHECK_LOGIN_REQUEST_URI);
          String checkLoginReturnString = doEvent(request, response, checkLoginType, checkLoginPath, checkLoginMethod);
          if(!"success".equalsIgnoreCase(checkLoginReturnString))
          {
            //previous URL already saved by event, so just do as the return says...
            eventReturnString = checkLoginReturnString;
            /* Set event info. */
            eventType = checkLoginType;
            eventPath = checkLoginPath;
            eventMethod = checkLoginMethod;
            requestUri = SiteDefs.CHECK_LOGIN_REQUEST_URI;
          }
        }
        
        if ( nextView == null ) nextView = rm.getViewName(requestUri);
        Debug.log("Current View: " + nextView);
        
        /** Invoke the event if defined, and if login not already done. */
        if(eventReturnString == null) 
        {
          /** Get event info. */
          eventType = rm.getEventType(requestUri);
          eventPath = rm.getEventPath(requestUri);
          eventMethod = rm.getEventMethod(requestUri);

          eventReturnString = doEvent(request, response, eventType, eventPath, eventMethod);
        }
        
        /** Process the eventReturn. */        
        String eventReturn = rm.getRequestAttribute(requestUri,eventReturnString);
        Debug.log("Event Qualified: " + eventReturn);
        
        if(eventReturn != null && !"success".equalsIgnoreCase(eventReturnString)) nextView = eventReturn;
        Debug.log("Next View after eventReturn: " + nextView);
        
        /** Check for a chain request. */
        if ( nextView != null && nextView.indexOf(':') != -1 ) {
            String type = nextView.substring(0,nextView.indexOf(':'));
            String view = nextView.substring(nextView.indexOf(':') + 1);            
            nextView = view;
            chainRequest = type.equalsIgnoreCase("request") ? true : false;
        }
                    
        /** Get the next view. */
        if ( !chainRequest ) {          
            String tempView = nextView;
            if(tempView != null && tempView.charAt(0) == '/') tempView = tempView.substring(1);
            Debug.log("Getting View Map: " + tempView);
            
            /* Before mapping the view, set a session attribute so we know where we are */
            request.getSession().setAttribute(SiteDefs.CURRENT_VIEW, tempView);
            
            tempView = rm.getViewPage(tempView);
            nextPage = tempView != null ? tempView : nextView;
            Debug.log("Mapped To: " + nextPage);
        }
        
        /** Handle Errors. */
        if ( eventPath == null && nextPage == null && eventReturn == null )
            throw new RequestHandlerException("RequestHandler: Unknown Request.");
        if ( nextPage == null && eventReturn == null )
            throw new RequestHandlerException("RequestHandler: No Next Page To Display");
        
        /** Invoke chained requests. */
        if ( chainRequest ) {
            Debug.log("Running Chained Request: " + nextView);
            nextPage = doRequest(request,response,nextView);
        }

        /** If previous request exists, and a login just succeeded, do that now... */
        if(requestUri.equals(SiteDefs.LOGIN_REQUEST_URI) && "success".equalsIgnoreCase(eventReturnString))
        {
          String previousRequest = (String) request.getSession().getAttribute(SiteDefs.PREVIOUS_REQUEST);
          if ( previousRequest != null ) 
          {
            request.getSession().removeAttribute(SiteDefs.PREVIOUS_REQUEST);
            //here we need to display nothing, and do the previous request
            Debug.log("Doing Previous Request: " + previousRequest);
            nextPage = doRequest(request, response, previousRequest);
          }
        }
            
        return nextPage;
    }
    
    public String doEvent( HttpServletRequest request, HttpServletResponse response, String eventType, String eventPath, String eventMethod) throws RequestHandlerException {
        String eventReturnString = null;

        if ( eventType != null && eventPath != null && eventMethod != null ) {
            if ( eventType.compareToIgnoreCase("java") == 0 ) {
                try {
                    Class[] paramTypes = new Class[] {HttpServletRequest.class, HttpServletResponse.class};
                    Object[] params = new Object[] {request,response};
                    Class c = Class.forName(eventPath);
                    Method m = c.getMethod(eventMethod, paramTypes);
                    eventReturnString = (String) m.invoke(null,params);
                    Debug.log("Event Return: " + eventReturnString);
                }
                catch ( Exception e ) {
                    Debug.log(e,"RequestHandler - Problems Processing Event.");
                    throw new RequestHandlerException("RequestHandler: Problems Processing Event - " + e.getMessage());
                }
            }
            else if ( eventType.compareToIgnoreCase("javascript") == 0 ) {
                //to be implemented later through IBM BSF
            }
            else if ( eventType.compareToIgnoreCase("jpython") == 0 ) {
                //to be implemented later through IBM BSF
            }
            else if ( eventType.compareToIgnoreCase("netrexx") == 0 ) {
                //to be implemented later through IBM BSF
            }
            else if ( eventType != null ) {
                throw new RequestHandlerException("RequestHandler: Unknown Event Type.");
            }
        }
        return eventReturnString;
    }

    public String getDefaultErrorPage( HttpServletRequest request ) {        
        String requestUri = getRequestUri(request.getPathInfo());
        return rm.getErrorPage(requestUri);
    }
    
    /** Gets the mapped request URI from path_info */
    private String getRequestUri(String path) {
        if ( path.indexOf('/') == -1 )
            return path;
        if ( path.lastIndexOf('/') == 0 )
            return path.substring(1);
        int nextIndex = path.indexOf('/',1);
        return path.substring(1,nextIndex);
    }
    
    /** Gets the next page to view from path_info */
    private String getNextPageUri(String path) {
        if ( path.indexOf('/') == -1 || path.lastIndexOf('/') == 0 )
            return null;
        int nextIndex = path.indexOf('/',1);
        return path.substring(nextIndex + 1);
    }
}


