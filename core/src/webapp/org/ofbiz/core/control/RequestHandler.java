/*
 * $Id$ 
 */

package org.ofbiz.core.control;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.naming.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.event.*;
import org.ofbiz.core.util.*;


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
        Debug.logInfo("Loading RequestManager...");
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
            Debug.logInfo("Chain in place: requestUri=" + requestUri + " nextView=" + nextView);
        } 
        else {
            // Invoke the pre-processor (but NOT in a chain)
            Collection preProcEvents = rm.getPreProcessor();
            if ( preProcEvents != null ) {
                Iterator i = preProcEvents.iterator();
                while ( i.hasNext() ) {
                    HashMap eventMap = (HashMap) i.next();
                    String eType = (String) eventMap.get(org.ofbiz.core.util.ConfigXMLReader.EVENT_TYPE);
                    String ePath = (String) eventMap.get(org.ofbiz.core.util.ConfigXMLReader.EVENT_PATH);
                    String eMeth = (String) eventMap.get(org.ofbiz.core.util.ConfigXMLReader.EVENT_METHOD);
                    try {
                        EventHandler preEvent = EventFactory.getEventHandler(rm,eType);
                        preEvent.initialize(ePath,eMeth);
                        String returnString = preEvent.invoke(request,response);
                        if ( !returnString.equalsIgnoreCase("success") )
                            throw new EventHandlerException("Event did not return 'success'.");
                    }
                    catch ( EventHandlerException e ) {
                        Debug.logError(e);
                    }
                }
            }
        }
        
        Debug.logInfo("***Request: " + requestUri);
        
        String eventReturnString = null;
        /** Perform security check. */
        if(rm.requiresAuth(requestUri)) {
            // Invoke the security handler
            // catch exceptions and throw RequestHandlerException if failed.
            String checkLoginType = rm.getEventType(SiteDefs.CHECK_LOGIN_REQUEST_URI);
            String checkLoginPath = rm.getEventPath(SiteDefs.CHECK_LOGIN_REQUEST_URI);
            String checkLoginMethod = rm.getEventMethod(SiteDefs.CHECK_LOGIN_REQUEST_URI);
            String checkLoginReturnString = null;
            try {
                EventHandler loginEvent = EventFactory.getEventHandler(rm,checkLoginType);
                loginEvent.initialize(checkLoginPath,checkLoginMethod);                
                checkLoginReturnString = loginEvent.invoke(request,response);
            }
            catch ( EventHandlerException e ) {
                throw new RequestHandlerException(e.getMessage());
            }
            if(!"success".equalsIgnoreCase(checkLoginReturnString)) {
                //previous URL already saved by event, so just do as the return says...
                eventReturnString = checkLoginReturnString;
                eventType = checkLoginType;
                eventPath = checkLoginPath;
                eventMethod = checkLoginMethod;
                requestUri = SiteDefs.CHECK_LOGIN_REQUEST_URI;
            }
        }
        
        if ( nextView == null ) nextView = rm.getViewName(requestUri);
        Debug.logInfo("Current View: " + nextView);
        
        /** Invoke the event if defined, and if login not already done. */
        if(eventReturnString == null) {
            /** Get event info. */
            eventType = rm.getEventType(requestUri);
            eventPath = rm.getEventPath(requestUri);
            eventMethod = rm.getEventMethod(requestUri);
            if ( eventType != null && eventPath != null && eventMethod != null ) {
                try {
                    EventHandler eh = EventFactory.getEventHandler(rm,eventType);
                    eh.initialize(eventPath,eventMethod);                    
                    eventReturnString = eh.invoke(request,response);
                }
                catch ( EventHandlerException e ) {
                    throw new RequestHandlerException(e.getMessage());
                }
            }
        }
        
        /** Process the eventReturn. */
        String eventReturn = rm.getRequestAttribute(requestUri,eventReturnString);
        Debug.logInfo("Event Qualified: " + eventReturn);
        
        if(eventReturn != null && !"success".equalsIgnoreCase(eventReturnString)) nextView = eventReturn;
        Debug.logInfo("Next View after eventReturn: " + nextView);
        
        /** Check for a chain request. */
        if ( nextView != null && nextView.length() > 0 && nextView.indexOf(':') != -1 ) {
            String type = nextView.substring(0,nextView.indexOf(':'));
            String view = nextView.substring(nextView.indexOf(':') + 1);
            nextView = view;
            chainRequest = type.equalsIgnoreCase("request") ? true : false;
        }
        
        /** Get the next view. */
        if ( !chainRequest ) {
            String tempView = nextView;
            if(tempView != null && tempView.length() > 0 && tempView.charAt(0) == '/') tempView = tempView.substring(1);
            Debug.logInfo("Getting View Map: " + tempView);
            
            /* Before mapping the view, set a session attribute so we know where we are */
            request.setAttribute(SiteDefs.CURRENT_VIEW, tempView);
            
            tempView = rm.getViewPage(tempView);
            nextPage = tempView != null ? tempView : nextView;
            Debug.logInfo("Mapped To: " + nextPage);
        }
        
        /** Handle Errors. */
        if ( eventPath == null && nextPage == null && eventReturn == null && !chainRequest )
            throw new RequestHandlerException("RequestHandler: Unknown Request.");
        if ( nextPage == null && eventReturn == null && !chainRequest )
            throw new RequestHandlerException("RequestHandler: No Next Page To Display");
        
        /** Invoke chained requests. */
        if ( chainRequest ) {
            Debug.logInfo("Running Chained Request: " + nextView);
            nextPage = doRequest(request,response,nextView);
        }
        
        /** If previous request exists, and a login just succeeded, do that now... */
        if(requestUri.equals(SiteDefs.LOGIN_REQUEST_URI) && "success".equalsIgnoreCase(eventReturnString)) {
            String previousRequest = (String) request.getSession().getAttribute(SiteDefs.PREVIOUS_REQUEST);
            if ( previousRequest != null ) {
                request.getSession().removeAttribute(SiteDefs.PREVIOUS_REQUEST);
                //here we need to display nothing, and do the previous request
                Debug.logInfo("Doing Previous Request: " + previousRequest);
                nextPage = doRequest(request, response, previousRequest);
            }
        }
        
        return nextPage;
    }
    
    public String getDefaultErrorPage( HttpServletRequest request ) {
        String requestUri = getRequestUri(request.getPathInfo());
        return rm.getErrorPage(requestUri);
    }
    
    /** Returns the RequestManager Object. */
    public RequestManager getRequestManager() {
        return rm;
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


