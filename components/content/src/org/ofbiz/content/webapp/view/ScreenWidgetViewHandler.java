/*
 * $Id$
 *
 * Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.content.webapp.view;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilJ2eeCompat;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.collections.MapStack;
import org.ofbiz.content.webapp.control.LoginWorker;
import org.ofbiz.content.widget.html.HtmlFormRenderer;
import org.ofbiz.content.widget.html.HtmlScreenRenderer;
import org.ofbiz.content.widget.screen.ModelScreen.ScreenRenderer;
import org.ofbiz.entity.GenericValue;
import org.xml.sax.SAXException;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;

/**
 * Handles view rendering for the Screen Widget
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      3.1
 */
public class ScreenWidgetViewHandler implements ViewHandler {

    public static final String module = ScreenWidgetViewHandler.class.getName();
    
    protected ServletContext servletContext = null;
    protected HtmlScreenRenderer htmlScreenRenderer = new HtmlScreenRenderer();

    /**
     * @see org.ofbiz.content.webapp.view.ViewHandler#init(javax.servlet.ServletContext)
     */
    public void init(ServletContext context) throws ViewHandlerException {
        this.servletContext = context;
    }

    /**
     * @see org.ofbiz.content.webapp.view.ViewHandler#render(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void render(String name, String page, String info, String contentType, String encoding, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {
        Writer writer = null;
        try {
        	// use UtilJ2eeCompat to get this setup properly
        	boolean useOutputStreamNotWriter = false;
        	if (this.servletContext != null) {
        		useOutputStreamNotWriter = UtilJ2eeCompat.useOutputStreamNotWriter(this.servletContext);
        	}
        	if (useOutputStreamNotWriter) {
        		ServletOutputStream ros = response.getOutputStream();
                writer = new OutputStreamWriter(ros, "UTF-8");
        	} else {
                writer = response.getWriter();
        	}

            HttpSession session = request.getSession();
            MapStack context = new MapStack();

            // ========== setup values that should always be in a screen context
            // make an object to more easily render screens
            ScreenRenderer screens = new ScreenRenderer(writer, context, htmlScreenRenderer);
            context.put("screens", screens);

            // make a reference for high level variables, a global context
            context.put("globalContext", context.standAloneStack());

            Map parameterMap = UtilHttp.getParameterMap(request);
            // go through all request attributes and for each name that is not already in the parameters Map add the attribute value
            Enumeration attrNames = request.getAttributeNames();
            while (attrNames.hasMoreElements()) {
                String attrName = (String) attrNames.nextElement();
                Object param = (String) parameterMap.get(attrName);
                if (param == null) {
                    parameterMap.put(attrName, request.getAttribute(attrName));
                } else if (param instanceof String && ((String) param).length() == 0) {
                    // also put the attribute value in if the parameter is empty
                    parameterMap.put(attrName, request.getAttribute(attrName));
                } else {
                    // do nothing, just log something
                    Debug.logInfo("Found request attribute that conflicts with parameter name, leaving request parameter in place for name: " + attrName, module);
                }
            }
            context.put("parameters", parameterMap);

            context.put("delegator", request.getAttribute("delegator"));
            context.put("dispatcher", request.getAttribute("dispatcher"));
            context.put("security", request.getAttribute("security"));
            
            GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
            context.put("userLogin", userLogin);
            context.put("autoUserLogin", session.getAttribute("autoUserLogin"));
            context.put("person", session.getAttribute("person"));
            context.put("partyGroup", session.getAttribute("partyGroup"));
            
            // some things also seem to require this, so here it is:
            request.setAttribute("userLogin", userLogin);
            
            // ========== setup values that are specific to OFBiz webapps
            context.put("request", request);
            context.put("response", response);
            context.put("session", session);
            context.put("application", servletContext);

            // these ones are FreeMarker specific and will only work in FTL templates, mainly here for backward compatibility
            BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
            context.put("sessionAttributes", new HttpSessionHashModel(session, wrapper));
            context.put("requestAttributes", new HttpRequestHashModel(request, wrapper));
            TaglibFactory JspTaglibs = new TaglibFactory(servletContext);
            context.put("JspTaglibs", JspTaglibs);
            context.put("requestParameters",  UtilHttp.getParameterMap(request));
            
            // this is a dummy object to stand-in for the JPublish page object for backward compatibility
            context.put("page", new HashMap());
            
            context.put("formStringRenderer", new HtmlFormRenderer(request, response));

            // make sure the locale is in the context
            context.put("locale", UtilHttp.getLocale(request));
            
            // get all locale information
            context.put("availableLocales", UtilMisc.availableLocales());

            // some information from/about the ControlServlet environment
            context.put("controlPath", request.getAttribute("_CONTROL_PATH_"));
            context.put("contextRoot", request.getAttribute("_CONTEXT_ROOT_"));
            context.put("serverRoot", request.getAttribute("_SERVER_ROOT_URL_"));
            context.put("checkLoginUrl", LoginWorker.makeLoginUrl(request, "checkLogin"));
            String externalLoginKey = LoginWorker.getExternalLoginKey(request);
            String externalKeyParam = externalLoginKey == null ? "" : "&externalLoginKey=" + externalLoginKey;
            context.put("externalLoginKey", externalLoginKey);
            context.put("externalKeyParam", externalKeyParam);
            
            // setup message lists
            List eventMessageList = (List) request.getAttribute("eventMessageList");
            if (eventMessageList == null) eventMessageList = new LinkedList();
            List errorMessageList = (List) request.getAttribute("errorMessageList");
            if (errorMessageList == null) errorMessageList = new LinkedList();

            if (request.getAttribute("_EVENT_MESSAGE_") != null) {
                eventMessageList.add(UtilFormatOut.replaceString((String) request.getAttribute("_EVENT_MESSAGE_"), "\n", "<br>"));
                request.removeAttribute("_EVENT_MESSAGE_");
            }
            if (request.getAttribute("_EVENT_MESSAGE_LIST_") != null) {
                eventMessageList.addAll((List) request.getAttribute("_EVENT_MESSAGE_LIST_"));
                request.removeAttribute("_EVENT_MESSAGE_LIST_");
            }
            if (request.getAttribute("_ERROR_MESSAGE_") != null) {
                errorMessageList.add(UtilFormatOut.replaceString((String) request.getAttribute("_ERROR_MESSAGE_"), "\n", "<br>"));
                request.removeAttribute("_ERROR_MESSAGE_");
            }
            if (session.getAttribute("_ERROR_MESSAGE_") != null) {
                errorMessageList.add(UtilFormatOut.replaceString((String) session.getAttribute("_ERROR_MESSAGE_"), "\n", "<br>"));
                session.removeAttribute("_ERROR_MESSAGE_");
            }
            if (request.getAttribute("_ERROR_MESSAGE_LIST_") != null) {
                errorMessageList.addAll((List) request.getAttribute("_ERROR_MESSAGE_LIST_"));
                request.removeAttribute("_ERROR_MESSAGE_LIST_");
            }
            context.put("eventMessageList", eventMessageList);
            context.put("errorMessageList", errorMessageList);
            
            // if there was an error message, this is an error
            if (errorMessageList.size() > 0) {
                context.put("isError", Boolean.TRUE);
            } else {
                context.put("isError", Boolean.FALSE);
            }
            // if a parameter was passed saying this is an error, it is an error
            if ("true".equals((String) parameterMap.get("isError"))) {
                context.put("isError", Boolean.TRUE);
            }

            // to preserve these values, push the MapStack
            context.push();
            
            screens.render(page);
        } catch (IOException e) {
            throw new ViewHandlerException("Error in the response writer/output stream: " + e.toString(), e);
        } catch (SAXException e) {
            throw new ViewHandlerException("XML Error rendering page: " + e.toString(), e);
        } catch (ParserConfigurationException e) {
            throw new ViewHandlerException("XML Error rendering page: " + e.toString(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    throw new ViewHandlerException("Error closing the writer/output stream: " + e.toString(), e);
                }
            }
        }
    }
}

