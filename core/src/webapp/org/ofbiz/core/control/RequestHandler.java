/*
 * $Id$
 *
 * Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.naming.*;

import org.ofbiz.core.service.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.event.*;
import org.ofbiz.core.stats.*;
import org.ofbiz.core.util.*;


/**
 * RequestHandler - Request Processor Object
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     Dustin Caldwell
 *@created    June 28, 2001
 *@version    1.0
 */
public class RequestHandler implements Serializable {

    public static final String module = RequestHandler.class.getName();

    private ServletContext context;
    private RequestManager rm;
    private VelocityViewHandler ve;

    public void init(ServletContext context) {
        this.context = context;
        Debug.logInfo("[RerquestHandler Loading...]", module);
        rm = new RequestManager(context);
        ve = null;
    }

    public String doRequest(HttpServletRequest request, HttpServletResponse response, String chain,
                            GenericValue userLogin, GenericDelegator delegator) throws RequestHandlerException {
        String requestUri = null;
        String eventType = null;
        String eventPath = null;
        String eventMethod = null;
        String nextView = null;
        String nextPage = null;
        boolean chainRequest = false;
        boolean noDispatch = false;
        boolean redirect = false;
        boolean velocity = false;

        String cname = request.getContextPath().substring(1);

        /* Grab data from request object to process. */
        requestUri = getRequestUri(request.getPathInfo());
        nextView = getNextPageUri(request.getPathInfo());

        /* Check for chained request. */
        if (chain != null) {
            requestUri = getRequestUri(chain);
            nextView = getNextPageUri(chain);
            Debug.logInfo("[RequestHandler]: Chain in place: requestUri=" + requestUri + " nextView=" + nextView, module);
        } else {
            // Invoke the pre-processor (but NOT in a chain)
            Collection preProcEvents = rm.getPreProcessor();
            if (preProcEvents != null) {
                Iterator i = preProcEvents.iterator();
                while (i.hasNext()) {
                    HashMap eventMap = (HashMap) i.next();
                    String eType = (String) eventMap.get(org.ofbiz.core.util.ConfigXMLReader.EVENT_TYPE);
                    String ePath = (String) eventMap.get(org.ofbiz.core.util.ConfigXMLReader.EVENT_PATH);
                    String eMeth = (String) eventMap.get(org.ofbiz.core.util.ConfigXMLReader.EVENT_METHOD);
                    try {
                        EventHandler preEvent = EventFactory.getEventHandler(rm, eType);
                        preEvent.initialize(ePath, eMeth);
                        String returnString = preEvent.invoke(request, response);
                        if (!returnString.equalsIgnoreCase("success"))
                            throw new EventHandlerException("Event did not return 'success'.");
                    } catch (EventHandlerException e) {
                        Debug.logError(e, module);
                    }
                }
            }
        }

        Debug.logInfo("[Processing Request]: " + requestUri, module);

        String eventReturnString = null;
        /* Perform security check. */
        if (rm.requiresAuth(requestUri)) {
            // Invoke the security handler
            // catch exceptions and throw RequestHandlerException if failed.
            String checkLoginType = rm.getEventType(SiteDefs.CHECK_LOGIN_REQUEST_URI);
            String checkLoginPath = rm.getEventPath(SiteDefs.CHECK_LOGIN_REQUEST_URI);
            String checkLoginMethod = rm.getEventMethod(SiteDefs.CHECK_LOGIN_REQUEST_URI);
            String checkLoginReturnString = null;
            try {
                EventHandler loginEvent = EventFactory.getEventHandler(rm, checkLoginType);
                loginEvent.initialize(checkLoginPath, checkLoginMethod);
                checkLoginReturnString = loginEvent.invoke(request, response);
            } catch (EventHandlerException e) {
                throw new RequestHandlerException(e.getMessage(), e);
            }
            if (!"success".equalsIgnoreCase(checkLoginReturnString)) {
                //previous URL already saved by event, so just do as the return says...
                eventReturnString = checkLoginReturnString;
                eventType = checkLoginType;
                eventPath = checkLoginPath;
                eventMethod = checkLoginMethod;
                requestUri = SiteDefs.CHECK_LOGIN_REQUEST_URI;
            }
        }

        if (nextView == null) nextView = rm.getViewName(requestUri);
        Debug.logVerbose("[Current View]: " + nextView, module);

        /* Invoke the event if defined, and if login not already done. */
        if (eventReturnString == null) {
            eventType = rm.getEventType(requestUri);
            eventPath = rm.getEventPath(requestUri);
            eventMethod = rm.getEventMethod(requestUri);
            if (eventType != null && eventPath != null && eventMethod != null) {
                try {
                    long eventStartTime = System.currentTimeMillis();
                    EventHandler eh = EventFactory.getEventHandler(rm, eventType);
                    eh.initialize(eventPath, eventMethod);
                    eventReturnString = eh.invoke(request, response);
                    ServerHitBin.countEvent(cname + "." + eventMethod, request.getSession().getId(), eventStartTime,
                                            System.currentTimeMillis() - eventStartTime, userLogin, delegator);
                } catch (EventHandlerException e) {
                    throw new RequestHandlerException(e.getMessage(), e);
                }
            }
        }

        /* Process the eventReturn. */
        String eventReturn = rm.getRequestAttribute(requestUri, eventReturnString);
        Debug.logVerbose("[Event Qualified]: " + eventReturn, module);

        if (eventReturn != null && !"success".equalsIgnoreCase(eventReturnString)) nextView = eventReturn;
        Debug.logVerbose("[Next View after eventReturn]: " + nextView, module);

        // check for a chain request.
        if (nextView != null && nextView.startsWith("request:")) {
            nextView = nextView.substring(8);
            chainRequest = true;
        }

        // check for a url for redirection
        if (nextView != null && nextView.startsWith("url:")) {
            nextView = nextView.substring(4);
            redirect = true;
        }

        // check for a JSP to dispatch to
        if (nextView != null && nextView.startsWith("view:")) {
            nextView = nextView.substring(5);
        }

        // check for a no dispatch return (meaning the return was processed by the event
        if (nextView != null && nextView.startsWith("none:")) {
            nextView = nextView.substring(5);  // *PLEASE NOTE* This is useless. View type NONE ignores the value of nextView
            noDispatch = true;
        }

        // get the next view.
        if (!chainRequest && !redirect && !noDispatch) {
            String tempView = nextView;
            if (tempView != null && tempView.length() > 0 && tempView.charAt(0) == '/') tempView = tempView.substring(1);
            Debug.logVerbose("[Getting View Map]: " + tempView, module);

            // before mapping the view, set a session attribute so we know where we are
            request.setAttribute(SiteDefs.CURRENT_VIEW, tempView);

            // check the type of view (for velocity)
            if (rm.getViewType(tempView).equals("velocity"))
                velocity = true;

            tempView = rm.getViewPage(tempView);
            nextPage = tempView != null ? tempView : "/" + nextView;
            Debug.logVerbose("[Mapped To]: " + nextPage, module);

        }

        // handle errors
        boolean normalReturn = true;
        if (chainRequest || redirect || noDispatch)
            normalReturn = false;

        if (eventPath == null && nextPage == null && eventReturn == null && normalReturn)
            throw new RequestHandlerException("RequestHandler: Unknown Request.");
        if (nextPage == null && eventReturn == null && normalReturn)
            throw new RequestHandlerException("RequestHandler: No Next Page To Display");

        // invoke chained requests
        if (chainRequest) {
            Debug.logInfo("[Running Chained Request]: " + nextView, module);
            nextPage = doRequest(request, response, nextView, userLogin, delegator);
        }

        // if previous request exists, and a login just succeeded, do that now...
        if (requestUri.equals(SiteDefs.LOGIN_REQUEST_URI) && "success".equalsIgnoreCase(eventReturnString)) {
            String previousRequest = (String) request.getSession().getAttribute(SiteDefs.PREVIOUS_REQUEST);
            if (previousRequest != null) {
                request.getSession().removeAttribute(SiteDefs.PREVIOUS_REQUEST);
                //here we need to display nothing, and do the previous request
                Debug.logInfo("[Doing Previous Request]: " + previousRequest, module);
                nextPage = doRequest(request, response, previousRequest, userLogin, delegator);
            }
        }

        // if noDispatch return null to the control servlet
        if (noDispatch)
            return null;

        // if redirect - redirect to the url and return null to the control servlet
        if (redirect) {
            Debug.logInfo("[Sending redirect]: " + nextView, module);
            try {
                response.sendRedirect(nextView);
            } catch (IOException ioe) {
                throw new RequestHandlerException(ioe.getMessage(), ioe);
            } catch (IllegalStateException ise) {
                throw new RequestHandlerException(ise.getMessage(), ise);
            }
            return null;
        }

        // if velocity - call the velocity view handler and return null to the control servlet
        if (velocity) {
            Debug.logInfo("[Calling Velocity Template]: " + nextPage, module);
            if (ve == null) {
                ve = new VelocityViewHandler();
                ve.init(context);
            }
            ve.eval(nextPage, request, response);
            return null;
        }

        return nextPage;
    }

    public String getDefaultErrorPage(HttpServletRequest request) {
        String requestUri = getRequestUri(request.getPathInfo());
        return rm.getErrorPage(requestUri);
    }

    /* Returns the RequestManager Object. */
    public RequestManager getRequestManager() {
        return rm;
    }

    /* Gets the mapped request URI from path_info */
    private String getRequestUri(String path) {
        if (path.indexOf('/') == -1)
            return path;
        if (path.lastIndexOf('/') == 0)
            return path.substring(1);
        int nextIndex = path.indexOf('/', 1);
        return path.substring(1, nextIndex);
    }

    /* Gets the next page to view from path_info */
    private String getNextPageUri(String path) {
        if (path.indexOf('/') == -1 || path.lastIndexOf('/') == 0)
            return null;
        int nextIndex = path.indexOf('/', 1);
        return path.substring(nextIndex + 1);
    }
}
