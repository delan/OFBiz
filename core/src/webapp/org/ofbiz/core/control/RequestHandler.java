/*
 * $Id$
 *
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

import org.ofbiz.core.entity.*;
import org.ofbiz.core.event.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.stats.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.view.*;


/**
 * RequestHandler - Request Processor Object
 *
 *@author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     Dustin Caldwell
 *@created    June 28, 2001
 *@version    1.1
 */
public class RequestHandler implements Serializable {

    public static final String module = RequestHandler.class.getName();

    private ServletContext context;
    private RequestManager rm;

    public void init(ServletContext context) {
        this.context = context;
        Debug.logInfo("[RerquestHandler Loading...]", module);
        rm = new RequestManager(context);
    }

    public void doRequest(HttpServletRequest request, HttpServletResponse response, String chain,
                          GenericValue userLogin, GenericDelegator delegator) throws RequestHandlerException {

        String eventType = null;
        String eventPath = null;
        String eventMethod = null;

        String cname = request.getContextPath().substring(1);

        /* Grab data from request object to process. */
        String requestUri = RequestHandler.getRequestUri(request.getPathInfo());
        String nextView = RequestHandler.getNextPageUri(request.getPathInfo());

        /* Check for chained request. */
        if (chain != null) {
            requestUri = RequestHandler.getRequestUri(chain);
            nextView = RequestHandler.getNextPageUri(chain);
            Debug.logInfo("[RequestHandler]: Chain in place: requestUri=" + requestUri + " nextView=" + nextView, module);
        } else {
            // Check if we SHOULD be secure and are not. If we are posting let it pass to not lose data. (too late now anyway)
            if (!request.isSecure() && rm.requiresHttps(requestUri) && !request.getMethod().equalsIgnoreCase("POST")) {
                String port = UtilProperties.getPropertyValue("url.properties", "port.https", "443");
                if (UtilProperties.propertyValueEqualsIgnoreCase("url.properties", "port.https.enabled", "Y")) {
                    StringBuffer newUrl = new StringBuffer();
                    newUrl.append("https://");
                    String server = UtilProperties.getPropertyValue("url.properties", "force.http.host", request.getServerName());
                    newUrl.append(server);
                    if (!port.equals("443")) {
                        newUrl.append(":" + port);
                    }
                    newUrl.append((String) request.getAttribute(SiteDefs.CONTROL_PATH));
                    newUrl.append(request.getPathInfo());
                    if (request.getQueryString() != null)
                        newUrl.append("?" + request.getQueryString());

                    // if we are supposed to be secure, redirect secure.
                    callRedirect(newUrl.toString(), request, response);
                }
            }

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
                        EventHandler preEvent = EventFactory.getEventHandler(this, eType);
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
            Debug.logVerbose("[RequestHandler]: AuthRequired. Running security check.", module);
            String checkLoginType = rm.getEventType(SiteDefs.CHECK_LOGIN_REQUEST_URI);
            String checkLoginPath = rm.getEventPath(SiteDefs.CHECK_LOGIN_REQUEST_URI);
            String checkLoginMethod = rm.getEventMethod(SiteDefs.CHECK_LOGIN_REQUEST_URI);
            String checkLoginReturnString = null;
            try {
                EventHandler loginEvent = EventFactory.getEventHandler(this, checkLoginType);
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

        // Make sure we have a default 'success' view
        if (nextView == null) nextView = rm.getViewName(requestUri);
        Debug.logVerbose("[Current View]: " + nextView, module);

        // Invoke the defined event (unless login failed)
        if (eventReturnString == null) {
            eventType = rm.getEventType(requestUri);
            eventPath = rm.getEventPath(requestUri);
            eventMethod = rm.getEventMethod(requestUri);
            if (eventType != null && eventPath != null && eventMethod != null) {
                try {
                    long eventStartTime = System.currentTimeMillis();
                    EventHandler eh = EventFactory.getEventHandler(this, eventType);
                    eh.initialize(eventPath, eventMethod);
                    eventReturnString = eh.invoke(request, response);
                    ServerHitBin.countEvent(cname + "." + eventMethod, request.getSession().getId(), eventStartTime,
                            System.currentTimeMillis() - eventStartTime, userLogin, delegator);
                } catch (EventHandlerException e) {
                    //check to see if there is an "error" response, if so go there and make an request error message
                    String tryErrorMsg = rm.getRequestAttribute(requestUri, "error");
                    if (tryErrorMsg != null) {
                        eventReturnString = "error";
                        request.setAttribute(SiteDefs.ERROR_MESSAGE, "Error calling event: " + e.toString());
                    } else {
                        throw new RequestHandlerException("Error calling event and no error repsonse was specified", e);
                    }
                }
            }
        }

        // Process the eventReturn.
        String eventReturn = rm.getRequestAttribute(requestUri, eventReturnString);
        Debug.logVerbose("[Response Qualified]: " + eventReturn, module);

        // Set the next view if we aren't 'success'
        if (eventReturn != null && !"success".equalsIgnoreCase(eventReturnString)) nextView = eventReturn;
        Debug.logVerbose("[Event Response Mapping]: " + nextView, module);

        // get the previous request info
        String previousRequest = (String) request.getSession().getAttribute(SiteDefs.PREVIOUS_REQUEST);
        String loginPass = (String) request.getAttribute(SiteDefs.LOGIN_PASSED);
        Debug.logVerbose("[RequestHandler]: previousRequest - " + previousRequest + " (" + loginPass + ")", module);

        // check for a chain request.
        if (nextView != null && nextView.startsWith("request:")) {
            Debug.logVerbose("[RequestHandler.doRequest]: Response is a chained request.", module);
            nextView = nextView.substring(8);
            doRequest(request, response, nextView, userLogin, delegator);
        }

        // if previous request exists, and a login just succeeded, do that now.
        else if (previousRequest != null && loginPass != null && loginPass.equalsIgnoreCase("TRUE")) {
            request.getSession().removeAttribute(SiteDefs.PREVIOUS_REQUEST);
            Debug.logInfo("[Doing Previous Request]: " + previousRequest, module);
            doRequest(request, response, previousRequest, userLogin, delegator);
        }

        // check for a url for redirection
        else if (nextView != null && nextView.startsWith("url:")) {
            Debug.logVerbose("[RequestHandler.doRequest]: Response is a URL redirect.", module);
            nextView = nextView.substring(4);
            callRedirect(nextView, request, response);
        }

        // check for a View
        else if (nextView != null && nextView.startsWith("view:")) {
            Debug.logVerbose("[RequestHandler.doRequest]: Response is a view.", module);
            nextView = nextView.substring(5);
            renderView(nextView, rm.allowExtView(requestUri), request, response);
        }

        // check for a no dispatch return (meaning the return was processed by the event
        else if (nextView != null && nextView.startsWith("none:")) {
            Debug.logVerbose("[RequestHandler.doRequest]: Response is handled by the event.", module);
        }

        // a page request
        else if (nextView != null) {
            Debug.logVerbose("[RequestHandler.doRequest]: Response is a page.", module);
            renderView(nextView, rm.allowExtView(requestUri), request, response);
        }

        // unknow request
        else {
            throw new RequestHandlerException("Illegal request; handler could not process the request.");
        }
    }

    /** Returns the default error page for this request. */
    public String getDefaultErrorPage(HttpServletRequest request) {
        String requestUri = RequestHandler.getRequestUri(request.getPathInfo());
        return rm.getErrorPage(requestUri);
    }

    /** Returns the RequestManager Object. */
    public RequestManager getRequestManager() {
        return rm;
    }

    /** Returns the ServletContext Object. */
    public ServletContext getServletContext() {
        return context;
    }

    public static String getRequestUri(String path) {
        if (path.indexOf('/') == -1)
            return path;
        if (path.lastIndexOf('/') == 0)
            return path.substring(1);
        int nextIndex = path.indexOf('/', 1);
        return path.substring(1, nextIndex);
    }

    public static String getNextPageUri(String path) {
        if (path.indexOf('/') == -1 || path.lastIndexOf('/') == 0)
            return null;
        int nextIndex = path.indexOf('/', 1);
        return path.substring(nextIndex + 1);
    }

    private void callRedirect(String url, HttpServletRequest req, HttpServletResponse resp)
            throws RequestHandlerException {
        Debug.logInfo("[Sending redirect]: " + url, module);
        try {
            resp.sendRedirect(url);
        } catch (IOException ioe) {
            throw new RequestHandlerException(ioe.getMessage(), ioe);
        } catch (IllegalStateException ise) {
            throw new RequestHandlerException(ise.getMessage(), ise);
        }
    }

    private void renderView(String view, boolean allowExtView, HttpServletRequest req, HttpServletResponse resp)
            throws RequestHandlerException {
        GenericValue userLogin = (GenericValue) req.getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) req.getAttribute("delegator");
        String cname = req.getContextPath().substring(1);
        String oldView = view;

        if (view != null && view.length() > 0 && view.charAt(0) == '/') view = view.substring(1);
        Debug.logVerbose("[Getting View Map]: " + view, module);

        // before mapping the view, set a session attribute so we know where we are
        req.setAttribute(SiteDefs.CURRENT_VIEW, view);

        String viewType = rm.getViewType(view);
        String tempView = rm.getViewPage(view);
        String nextPage = null;
        if (tempView == null) {
            if (!allowExtView)
                throw new RequestHandlerException("No view to render.");
            else
                nextPage = "/" + oldView;
        } else {
            nextPage = tempView;
        }

        Debug.logVerbose("[Mapped To]: " + nextPage, module);

        long viewStartTime = System.currentTimeMillis();
        try {
            Debug.logVerbose("Rendering view [" + nextPage + "] of type [" + viewType + "]");
            ViewHandler vh = ViewFactory.getViewHandler(this, viewType);
            vh.render(nextPage, req, resp);
        } catch (ViewHandlerException e) {
            throw new RequestHandlerException("Error in view handler", e);
        }

        String vname = (String) req.getAttribute(SiteDefs.CURRENT_VIEW);
        if (vname != null) {
            ServerHitBin.countView(cname + "." + vname, req.getSession().getId(), viewStartTime,
                    System.currentTimeMillis() - viewStartTime, userLogin, delegator);
        }
    }
}
