/*
 * $Id: RequestHandler.java,v 1.12 2004/06/10 20:43:32 ajzeneski Exp $
 *
 * Copyright (c) 2001-2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.content.webapp.control;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.content.stats.ServerHitBin;
import org.ofbiz.content.stats.VisitHandler;
import org.ofbiz.content.webapp.event.EventFactory;
import org.ofbiz.content.webapp.event.EventHandler;
import org.ofbiz.content.webapp.event.EventHandlerException;
import org.ofbiz.content.webapp.view.ViewFactory;
import org.ofbiz.content.webapp.view.ViewHandler;
import org.ofbiz.content.webapp.view.ViewHandlerException;
import org.ofbiz.content.website.WebSiteWorker;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * RequestHandler - Request Processor Object
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     Dustin Caldwell
 * @version    $Revision: 1.12 $
 * @since      2.0
 */
public class RequestHandler implements Serializable {

    public static final String module = RequestHandler.class.getName();
    
    private ServletContext context = null;
    private RequestManager requestManager = null;
    private ViewFactory viewFactory = null;
    private EventFactory eventFactory = null;

    public void init(ServletContext context) {
        Debug.logInfo("[RequestHandler Loading...]", module);
        this.context = context;        
        this.requestManager = new RequestManager(context);
        this.viewFactory = new ViewFactory(this);
        this.eventFactory = new EventFactory(this);
    }

    public void doRequest(HttpServletRequest request, HttpServletResponse response, String chain,
            GenericValue userLogin, GenericDelegator delegator) throws RequestHandlerException {

        String eventType = null;
        String eventPath = null;
        String eventMethod = null;

        // workaraound if we are in the root webapp
        String cname = UtilHttp.getApplicationName(request);

        // Grab data from request object to process
        String requestUri = RequestHandler.getRequestUri(request.getPathInfo());
        String nextView = RequestHandler.getNextPageUri(request.getPathInfo());
        if (request.getAttribute("targetRequestUri") == null) {
            if (request.getSession().getAttribute("_PREVIOUS_REQUEST_") != null) {
                request.setAttribute("targetRequestUri", request.getSession().getAttribute("_PREVIOUS_REQUEST_"));
            } else {
                request.setAttribute("targetRequestUri", "/" + requestUri);
            }
        }

        // Check for chained request.
        if (chain != null) {
            requestUri = RequestHandler.getRequestUri(chain);
            if (request.getAttribute("_POST_CHAIN_VIEW_") != null) {
                nextView = (String) request.getAttribute("_POST_CHAIN_VIEW_");
            } else {
                nextView = RequestHandler.getNextPageUri(chain);
            }
            if (Debug.infoOn()) Debug.logInfo("[RequestHandler]: Chain in place: requestUri=" + requestUri + " nextView=" + nextView, module);
        } else {
            // Check to make sure we are allowed to access this request directly. (Also checks if this request is defined.)
            if (!requestManager.allowDirectRequest(requestUri)) {
                throw new RequestHandlerException("Unknown request [" + requestUri + "]; this request does not exist or cannot be called directly.");
            }

            // Check if we SHOULD be secure and are not. If we are posting let it pass to not lose data. (too late now anyway)
            if (!request.isSecure() && requestManager.requiresHttps(requestUri) && !request.getMethod().equalsIgnoreCase("POST")) {
                String port = UtilProperties.getPropertyValue("url.properties", "port.https", "443");

                if (UtilProperties.propertyValueEqualsIgnoreCase("url.properties", "port.https.enabled", "Y")) {
                    StringBuffer newUrl = new StringBuffer();

                    newUrl.append("https://");
                    String server = UtilProperties.getPropertyValue("url.properties", "force.http.host", request.getServerName());

                    newUrl.append(server);
                    if (!port.equals("443")) {
                        newUrl.append(":" + port);
                    }
                    newUrl.append((String) request.getAttribute("_CONTROL_PATH_"));
                    newUrl.append(request.getPathInfo());
                    if (request.getQueryString() != null)
                        newUrl.append("?" + request.getQueryString());

                    // if we are supposed to be secure, redirect secure.
                    callRedirect(newUrl.toString(), response);
                }
            }

            // If its the first visit run the first visit events.
            HttpSession session = request.getSession();

            if (session.getAttribute("visit") == null) {
                // This isn't an event because it is required to run. We do not want to make it optional.
                VisitHandler.setInitialVisit(request);
                Collection events = requestManager.getFirstVisitEvents();

                if (events != null) {
                    Iterator i = events.iterator();

                    while (i.hasNext()) {
                        Map eventMap = (Map) i.next();
                        String eType = (String) eventMap.get(ConfigXMLReader.EVENT_TYPE);
                        String ePath = (String) eventMap.get(ConfigXMLReader.EVENT_PATH);
                        String eMeth = (String) eventMap.get(ConfigXMLReader.EVENT_METHOD);

                        try {
                            String returnString = this.runEvent(request, response, eType, ePath, eMeth);                            
                            if (returnString != null && !returnString.equalsIgnoreCase("success")) {
                                throw new EventHandlerException("First-Visit event did not return 'success'.");
                            } else if (returnString == null) {
                                nextView = "none:";
                            }
                        } catch (EventHandlerException e) {
                            Debug.logError(e, module);
                        }
                    }
                }
            }

            // Invoke the pre-processor (but NOT in a chain)
            Collection preProcEvents = requestManager.getPreProcessor();
            if (preProcEvents != null) {
                Iterator i = preProcEvents.iterator();

                while (i.hasNext()) {
                    Map eventMap = (HashMap) i.next();
                    String eType = (String) eventMap.get(ConfigXMLReader.EVENT_TYPE);
                    String ePath = (String) eventMap.get(ConfigXMLReader.EVENT_PATH);
                    String eMeth = (String) eventMap.get(ConfigXMLReader.EVENT_METHOD);
                    try {
                        String returnString = this.runEvent(request, response, eType, ePath, eMeth);                        
                        if (returnString != null && !returnString.equalsIgnoreCase("success")) {
                            throw new EventHandlerException("Pre-Processor event did not return 'success'.");
                        } else if (returnString == null) {
                            nextView = "none:";
                        }
                    } catch (EventHandlerException e) {
                        Debug.logError(e, module);
                    }
                }
            }
        }

        // Pre-Processor/First-Visit event(s) can interrupt the flow by returning null.
        // Warning: this could cause problems if more then one event attempts to return a response.
        if ("none:".equals(nextView)) {
            if (Debug.infoOn()) Debug.logInfo("[Pre-Processor Interrupted Request, not running: " + requestUri, module);
            return;
        }

        if (Debug.infoOn()) Debug.logInfo("[Processing Request]: " + requestUri, module);

        String eventReturnString = null;

        // Perform security check.
        if (requestManager.requiresAuth(requestUri)) {
            // Invoke the security handler
            // catch exceptions and throw RequestHandlerException if failed.
            Debug.logVerbose("[RequestHandler]: AuthRequired. Running security check.", module);
            String checkLoginType = requestManager.getEventType("checkLogin");
            String checkLoginPath = requestManager.getEventPath("checkLogin");
            String checkLoginMethod = requestManager.getEventMethod("checkLogin");
            String checkLoginReturnString = null;

            try {
                checkLoginReturnString = this.runEvent(request, response, checkLoginType, 
                        checkLoginPath, checkLoginMethod);                
            } catch (EventHandlerException e) {
                throw new RequestHandlerException(e.getMessage(), e);
            }
            if (!"success".equalsIgnoreCase(checkLoginReturnString)) {
                // previous URL already saved by event, so just do as the return says...
                eventReturnString = checkLoginReturnString;
                eventType = checkLoginType;
                eventPath = checkLoginPath;
                eventMethod = checkLoginMethod;
                requestUri = "checkLogin";
            }
        }

        // Invoke the defined event (unless login failed)
        if (eventReturnString == null) {
            eventType = requestManager.getEventType(requestUri);
            eventPath = requestManager.getEventPath(requestUri);
            eventMethod = requestManager.getEventMethod(requestUri);
            if (eventType != null && eventPath != null && eventMethod != null) {
                try {
                    long eventStartTime = System.currentTimeMillis();
                    eventReturnString = this.runEvent(request, response, eventType, eventPath, eventMethod);                    
                    ServerHitBin.countEvent(cname + "." + eventMethod, request, eventStartTime,
                        System.currentTimeMillis() - eventStartTime, userLogin, delegator);
                    if (eventReturnString == null)
                        nextView = "none:";
                } catch (EventHandlerException e) {
                    // check to see if there is an "error" response, if so go there and make an request error message
                    String tryErrorMsg = requestManager.getRequestAttribute(requestUri, "error");

                    if (tryErrorMsg != null) {
                        eventReturnString = "error";
                        request.setAttribute("_ERROR_MESSAGE_", "Error calling event: " + e.toString());
                    } else {
                        throw new RequestHandlerException("Error calling event and no error repsonse was specified", e);
                    }
                }
            }
        }

        // Process the eventReturn.
        String eventReturn = requestManager.getRequestAttribute(requestUri, eventReturnString);

        if (Debug.verboseOn()) Debug.logVerbose("[Response Qualified]: " + eventReturn, module);

        // Set the next view
        if (eventReturn != null && !"success".equals(eventReturnString)) nextView = eventReturn;
        if (Debug.verboseOn()) Debug.logVerbose("[Event Response Mapping]: " + nextView, module);

        // get the previous request info
        String previousRequest = (String) request.getSession().getAttribute("_PREVIOUS_REQUEST_");
        String loginPass = (String) request.getAttribute("_LOGIN_PASSED_");

        if (Debug.verboseOn()) Debug.logVerbose("[RequestHandler]: previousRequest - " + previousRequest + " (" + loginPass + ")", module);

        // if previous request exists, and a login just succeeded, do that now.
        if (previousRequest != null && loginPass != null && loginPass.equalsIgnoreCase("TRUE")) {
            request.getSession().removeAttribute("_PREVIOUS_REQUEST_");
            if (Debug.infoOn()) Debug.logInfo("[Doing Previous Request]: " + previousRequest, module);
            doRequest(request, response, previousRequest, userLogin, delegator);
            return; // this is needed or else we will run the view twice
        }

        String successView = requestManager.getViewName(requestUri);
        if ("success".equals(eventReturnString) && successView.startsWith("request:")) {
            // chains will override any url defined views; but we will save the view for the very end
            if (nextView != null) {
                request.setAttribute("_POST_CHAIN_VIEW_", nextView);
            }
            nextView = successView;
        }

        // Make sure we have some sort of response to go to
        if (nextView == null) nextView = successView;
        if (Debug.verboseOn()) Debug.logVerbose("[Current View]: " + nextView, module);

        // Handle the responses - chains/views
        if (nextView != null && nextView.startsWith("request:")) {
            // chained request
            Debug.log("[RequestHandler.doRequest]: Response is a chained request.", module);
            nextView = nextView.substring(8);
            doRequest(request, response, nextView, userLogin, delegator);
            return; // this just to be safe; not really needed

        }

        // handle views
        else {
            // first invoke the post-processor events.
            Collection postProcEvents = requestManager.getPostProcessor();
            if (postProcEvents != null) {
                Iterator i = postProcEvents.iterator();

                while (i.hasNext()) {
                    Map eventMap = (HashMap) i.next();
                    String eType = (String) eventMap.get(ConfigXMLReader.EVENT_TYPE);
                    String ePath = (String) eventMap.get(ConfigXMLReader.EVENT_PATH);
                    String eMeth = (String) eventMap.get(ConfigXMLReader.EVENT_METHOD);
                    try {
                        String returnString = this.runEvent(request, response, eType, ePath, eMeth);                        
                        if (returnString != null && !returnString.equalsIgnoreCase("success"))
                            throw new EventHandlerException("Post-Processor event did not return 'success'.");
                        else if (returnString == null)
                            nextView = "none:";
                    } catch (EventHandlerException e) {
                        Debug.logError(e, module);
                    }
                }
            }

            // check for a url for redirection
            if (nextView != null && nextView.startsWith("url:")) {
                Debug.log("[RequestHandler.doRequest]: Response is a URL redirect.", module);
                nextView = nextView.substring(4);
                callRedirect(nextView, response);
            }

            // check for a View
            else if (nextView != null && nextView.startsWith("view:")) {
                Debug.log("[RequestHandler.doRequest]: Response is a view.", module);
                nextView = nextView.substring(5);
                renderView(nextView, requestManager.allowExtView(requestUri), request, response);
            }

            // check for a no dispatch return (meaning the return was processed by the event
            else if (nextView != null && nextView.startsWith("none:")) {
                Debug.log("[RequestHandler.doRequest]: Response is handled by the event.", module);
            }

            // a page request
            else if (nextView != null) {
                Debug.log("[RequestHandler.doRequest]: Response is a page.", module);
                renderView(nextView, requestManager.allowExtView(requestUri), request, response);
            }

            // unknown request
            else {
                throw new RequestHandlerException("Illegal request; handler could not process the request.");
            }
        }
    }
    
    /** Find the event handler and invoke an event. */
    public String runEvent(HttpServletRequest request, HttpServletResponse response, String type, 
            String path, String method) throws EventHandlerException {
        EventHandler eventHandler = eventFactory.getEventHandler(type);
        return eventHandler.invoke(path, method, request, response);                   
    }    

    /** Returns the default error page for this request. */
    public String getDefaultErrorPage(HttpServletRequest request) {
        //String requestUri = RequestHandler.getRequestUri(request.getPathInfo());
        //return requestManager.getErrorPage(requestUri);
        return requestManager.getDefaultErrorPage();
    }

    /** Returns the RequestManager Object. */
    public RequestManager getRequestManager() {
        return requestManager;
    }

    /** Returns the ServletContext Object. */
    public ServletContext getServletContext() {
        return context;
    }
    
    /** Returns the ViewFactory Object. */
    public ViewFactory getViewFactory() {
        return viewFactory;
    }
    
    /** Returns the EventFactory Object. */
    public EventFactory getEventFactory() {
        return eventFactory;
    }

    public static String getRequestUri(String path) {
        List pathInfo = StringUtil.split(path, "/");
        if (((String)pathInfo.get(0)).indexOf('?') > -1) {        
            return ((String) pathInfo.get(0)).substring(0, ((String)pathInfo.get(0)).indexOf('?'));
        } else {               
            return (String) pathInfo.get(0);
        }
    }

    public static String getNextPageUri(String path) {
        List pathInfo = StringUtil.split(path, "/");
        String nextPage = null;
        for (int i = 1; i < pathInfo.size(); i++) {
            String element = (String) pathInfo.get(i);
            if (element.indexOf('~') != 0) {
                if (element.indexOf('?') > -1) {
                    element = element.substring(0, element.indexOf('?'));
                }
                if (i == 1) {                
                    nextPage = element;
                } else {
                    nextPage = nextPage + "/" + element;
                }
            }                                                 
        }
        return nextPage;
    }

    private void callRedirect(String url, HttpServletResponse resp) throws RequestHandlerException {
        if (Debug.infoOn()) Debug.logInfo("[Sending redirect]: " + url, module);
        try {
            resp.sendRedirect(url);
        } catch (IOException ioe) {
            throw new RequestHandlerException(ioe.getMessage(), ioe);
        } catch (IllegalStateException ise) {
            throw new RequestHandlerException(ise.getMessage(), ise);
        }
    }
    
    private void renderView(String view, boolean allowExtView, HttpServletRequest req, HttpServletResponse resp) throws RequestHandlerException {
        GenericValue userLogin = (GenericValue) req.getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) req.getAttribute("delegator");
        // workaraound if we are in the root webapp
        String cname = UtilHttp.getApplicationName(req);
        String oldView = view;

        if (view != null && view.length() > 0 && view.charAt(0) == '/') view = view.substring(1);

        // if the view name starts with the control servlet name and a /, then it was an 
        // attempt to override the default view with a call back into the control servlet,
        // so just get the target view name and use that
        String servletName = req.getServletPath().substring(1);

        Debug.logInfo("servletName=" + servletName + ", view=" + view, module);
        if (view.startsWith(servletName + "/")) {
            view = view.substring(servletName.length() + 1);
            Debug.logInfo("a manual control servlet request was received, removing control servlet path resulting in: view=" + view, module);
        }

        if (Debug.verboseOn()) Debug.logVerbose("[Getting View Map]: " + view, module);

        // before mapping the view, set a session attribute so we know where we are
        req.setAttribute("_CURRENT_VIEW_", view);

        String viewType = requestManager.getViewType(view);
        String tempView = requestManager.getViewPage(view);
        String nextPage = null;

        if (tempView == null) {
            if (!allowExtView) {
                throw new RequestHandlerException("No view to render.");
            } else {
                nextPage = "/" + oldView;
            }
        } else {
            nextPage = tempView;
        }

        if (Debug.verboseOn()) Debug.logVerbose("[Mapped To]: " + nextPage, module);

        long viewStartTime = System.currentTimeMillis();

        // setup chararcter encoding and content type
        String charset = getServletContext().getInitParameter("charset");

        if (charset == null || charset.length() == 0) charset = req.getCharacterEncoding();
        if (charset == null || charset.length() == 0) charset = "UTF-8";
        
        String viewCharset = requestManager.getViewEncoding(view);
        //NOTE: if the viewCharset is "none" then no charset will be used
        if (viewCharset != null && viewCharset.length() > 0) charset = viewCharset;

        if (!"none".equals(charset)) {
            try {
                req.setCharacterEncoding(charset);
            } catch (UnsupportedEncodingException e) {
                throw new RequestHandlerException("Could not set character encoding to " + charset, e);
            } catch (IllegalStateException e) {
                Debug.logInfo(e, "Could not set character encoding to " + charset + ", something has probably already committed the stream", module);
            }
        }

        // setup content type
        String contentType = "text/html";
        String viewContentType = requestManager.getViewContentType(view);
        if (viewContentType != null && viewContentType.length() > 0) contentType = viewContentType;
        
        if (charset.length() > 0 && !"none".equals(charset)) {
            resp.setContentType(contentType + "; charset=" + charset);
        } else {
            resp.setContentType(contentType);
        }

        if (Debug.verboseOn()) Debug.logVerbose("The ContentType for the " + view + " view is: " + contentType, module);
        
        try {
            if (Debug.verboseOn()) Debug.logVerbose("Rendering view [" + nextPage + "] of type [" + viewType + "]", module);
            ViewHandler vh = viewFactory.getViewHandler(viewType);
            //Debug.log("Obtained View Handler : " + vh, module);
            vh.render(view, nextPage, requestManager.getViewInfo(view), contentType, charset, req, resp);
            //Debug.log("Rendered View : " + view + " : " + vh, module);
        } catch (ViewHandlerException e) {
            Throwable throwable = e.getNested() != null ? e.getNested() : e;

            throw new RequestHandlerException(e.getNonNestedMessage(), throwable);
        }

        // before getting the view generation time flush the response output to get more consistent results
        try {
            resp.flushBuffer();
        } catch (java.io.IOException e) {
            throw new RequestHandlerException("Error flushing response buffer", e);
        }

        String vname = (String) req.getAttribute("_CURRENT_VIEW_");

        if (vname != null) {
            ServerHitBin.countView(cname + "." + vname, req, viewStartTime,
                System.currentTimeMillis() - viewStartTime, userLogin, delegator);
        }
    }
    
    public String makeLink(HttpServletRequest request, HttpServletResponse response, String url) {
        return makeLink(request, response, url, false, false, false);
    }
    
    public String makeLink(HttpServletRequest request, HttpServletResponse response, String url, boolean fullPath, boolean secure, boolean encode) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");        
        String webSiteId = WebSiteWorker.getWebSiteId(request);
        
        String httpsPort = null;
        String httpsServer = null;
        String httpPort = null;
        String httpServer = null;
        Boolean enableHttps = null;
        
        // load the properties from the website entity        
        GenericValue webSite = null;
        if (webSiteId != null) {
            try {
                webSite = delegator.findByPrimaryKeyCache("WebSite", UtilMisc.toMap("webSiteId", webSiteId));
                if (webSite != null) {
                    httpsPort = webSite.getString("httpsPort");
                    httpsServer = webSite.getString("httpsHost");
                    httpPort = webSite.getString("httpPort");
                    httpServer = webSite.getString("httpHost");
                    enableHttps = webSite.getBoolean("enableHttps");
                }
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "Problems with WebSite entity; using global defaults", module);
            }
        }
        
        // fill in any missing properties with fields from the global file
        if (httpsPort == null) 
            httpsPort = UtilProperties.getPropertyValue("url.properties", "port.https", "443");
        if (httpServer == null)
            httpsServer = UtilProperties.getPropertyValue("url.properties", "force.https.host");
        if (httpPort == null)
            httpPort = UtilProperties.getPropertyValue("url.properties", "port.http", "80");
        if (httpServer == null)
            httpServer = UtilProperties.getPropertyValue("url.properties", "force.http.host");
        if (enableHttps == null)
            enableHttps = new Boolean(UtilProperties.propertyValueEqualsIgnoreCase("url.properties", "port.https.enabled", "Y"));
        
        // create the path the the control servlet
        String controlPath = (String) request.getAttribute("_CONTROL_PATH_");              
        
        
        String requestUri = RequestHandler.getRequestUri(url);
        StringBuffer newURL = new StringBuffer();

        boolean useHttps = enableHttps.booleanValue();                
        if (useHttps || fullPath || secure) {
            if (secure || (useHttps && requestManager.requiresHttps(requestUri) && !request.isSecure())) {
                String server = httpsServer;

                if (server == null || server.length() == 0) {
                    server = request.getServerName();
                }
                newURL.append("https://");
                newURL.append(server);
                if (!httpsPort.equals("443")) {
                    newURL.append(":" + httpsPort);
                }
            } else if (fullPath || (useHttps && !requestManager.requiresHttps(requestUri) && request.isSecure())) {
                String server = httpServer;

                if (server == null || server.length() == 0) {
                    server = request.getServerName();
                }
                newURL.append("http://");
                newURL.append(server);
                if (!httpPort.equals("80")) {
                    newURL.append(":" + httpPort);
                }
            }
        }
                
        newURL.append(controlPath);
        newURL.append(url);
        String encodedUrl = null;
        if (response != null && !encode) {
            encodedUrl = response.encodeURL(newURL.toString());
        } else {            
            if (encode) {
                String sessionId = request.getSession().getId();
                newURL.append(";jsessionid=" + sessionId);
            }            
            encodedUrl = newURL.toString();
        }
        //if (encodedUrl.indexOf("null") > 0) {
            //Debug.logError("in makeLink, controlPath:" + controlPath + " url:" + url, "");
            //throw new RuntimeException("in makeLink, controlPath:" + controlPath + " url:" + url);
        //}
        
        return encodedUrl;              
    }

    public static String makeUrl(HttpServletRequest request, HttpServletResponse response, String url) {
        return makeUrl(request, response, url, false, false, false);
    }

    public static String makeUrl(HttpServletRequest request, HttpServletResponse response, String url, boolean fullPath, boolean secure, boolean encode) {
        ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
        RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");
        return rh.makeLink(request, response, url, fullPath, secure, encode);
    }

}
