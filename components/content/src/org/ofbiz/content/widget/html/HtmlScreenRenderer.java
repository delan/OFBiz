/*
 * $Id$
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
 */
package org.ofbiz.content.widget.html;

import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.content.webapp.control.RequestHandler;
import org.ofbiz.content.webapp.taglib.ContentUrlTag;
import org.ofbiz.content.widget.screen.ModelScreenWidget;
import org.ofbiz.content.widget.screen.ScreenStringRenderer;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * Widget Library - HTML Form Renderer implementation
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      3.1
 */
public class HtmlScreenRenderer implements ScreenStringRenderer {

    public HtmlScreenRenderer() {}
    public static final String module = HtmlScreenRenderer.class.getName();

    public void renderSectionBegin(Writer writer, Map context, ModelScreenWidget.Section section) throws IOException {
        // do nothing, this is just a place holder container for HTML
    }
    public void renderSectionEnd(Writer writer, Map context, ModelScreenWidget.Section section) throws IOException {
        // do nothing, this is just a place holder container for HTML
    }

    public void renderContainerBegin(Writer writer, Map context, ModelScreenWidget.Container container) throws IOException {
        writer.write("<div");

        String id = container.getId(context);
        if (UtilValidate.isNotEmpty(id)) {
            writer.write(" id=\"");
            writer.write(id);
            writer.write("\"");
        }
        
        String style = container.getStyle(context);
        if (UtilValidate.isNotEmpty(style)) {
            writer.write(" class=\"");
            writer.write(style);
            writer.write("\"");
        }
        
        writer.write(">");
        appendWhitespace(writer);
    }
    public void renderContainerEnd(Writer writer, Map context, ModelScreenWidget.Container container) throws IOException {
        writer.write("</div>");
        appendWhitespace(writer);
    }

    public void renderLabel(Writer writer, Map context, ModelScreenWidget.Label label) throws IOException {
        // open tag
                Debug.logInfo("renderLabel, depth:" + context.get("depth"), module);
        writer.write("<span");
        String id = label.getId(context);
        if (UtilValidate.isNotEmpty(id)) {
            writer.write(" id=\"");
            writer.write(id);
            writer.write("\"");
        }
        String style = label.getStyle(context);
        if (UtilValidate.isNotEmpty(style)) {
            writer.write(" class=\"");
            writer.write(style);
            writer.write("\"");
        }
        writer.write(">");
        
        // the text
        writer.write(label.getText(context));
        
        // close tag
        writer.write("</span>");
        
        appendWhitespace(writer);
    }

    public void renderLink(Writer writer, Map context, ModelScreenWidget.Link link) throws IOException {
        // open tag
        writer.write("<a");
        String id = link.getId(context);
        if (UtilValidate.isNotEmpty(id)) {
            writer.write(" id=\"");
            writer.write(id);
            writer.write("\"");
        }
        String style = link.getStyle(context);
        if (UtilValidate.isNotEmpty(style)) {
            writer.write(" class=\"");
            writer.write(style);
            writer.write("\"");
        }
        String targetWindow = link.getTargetWindow(context);
        if (UtilValidate.isNotEmpty(targetWindow)) {
            writer.write(" target=\"");
            writer.write(targetWindow);
            writer.write("\"");
        }
        String target = link.getTarget(context);
        if (UtilValidate.isNotEmpty(target)) {
            writer.write(" href=\"");
            String urlMode = link.getUrlMode();
            String prefix = link.getPrefix(context);
            boolean fullPath = link.getFullPath();
            boolean secure = link.getSecure();
            boolean encode = link.getEncode();
            HttpServletResponse response = (HttpServletResponse) context.get("response");
            HttpServletRequest request = (HttpServletRequest) context.get("request");
            if (urlMode != null && urlMode.equalsIgnoreCase("intra-app")) {
                if (request != null && response != null) {
                    ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
                    RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");
                    String urlString = rh.makeLink(request, response, target, fullPath, secure, encode);
                    writer.write(urlString);
                } else if (prefix != null) {
                    writer.write(prefix + target);
                } else {
                    writer.write(target);
                }
            } else  if (urlMode != null && urlMode.equalsIgnoreCase("content")) {
                if (request != null && response != null) {
                    StringBuffer newURL = new StringBuffer();
                    ContentUrlTag.appendContentPrefix(request, newURL);
                    newURL.append(target);
                    writer.write(newURL.toString());
                }
            } else {
                writer.write(target);
            }

            writer.write("\"");
        }
        writer.write(">");
        
        // the text
        ModelScreenWidget.Image img = link.getImage();
        if (img == null)
            writer.write(link.getText(context));
        else
            renderImage(writer, context, img);
        
        // close tag
        writer.write("</a>");
        
        appendWhitespace(writer);
    }

    public void renderImage(Writer writer, Map context, ModelScreenWidget.Image image) throws IOException {
        // open tag
        writer.write("<img ");
        String id = image.getId(context);
        if (UtilValidate.isNotEmpty(id)) {
            writer.write(" id=\"");
            writer.write(id);
            writer.write("\"");
        }
        String style = image.getStyle(context);
        if (UtilValidate.isNotEmpty(style)) {
            writer.write(" class=\"");
            writer.write(style);
            writer.write("\"");
        }
        String wid = image.getWidth(context);
        if (UtilValidate.isNotEmpty(wid)) {
            writer.write(" width=\"");
            writer.write(wid);
            writer.write("\"");
        }
        String hgt = image.getHeight(context);
        if (UtilValidate.isNotEmpty(hgt)) {
            writer.write(" height=\"");
            writer.write(hgt);
            writer.write("\"");
        }
        String border = image.getBorder(context);
        if (UtilValidate.isNotEmpty(border)) {
            writer.write(" border=\"");
            writer.write(border);
            writer.write("\"");
        }
        String src = image.getSrc(context);
        if (UtilValidate.isNotEmpty(src)) {
            writer.write(" src=\"");
            String urlMode = image.getUrlMode();
            boolean fullPath = false;
            boolean secure = false;
            boolean encode = false;
            HttpServletResponse response = (HttpServletResponse) context.get("response");
            HttpServletRequest request = (HttpServletRequest) context.get("request");
            if (urlMode != null && urlMode.equalsIgnoreCase("intra-app")) {
                if (request != null && response != null) {
                    ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
                    RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");
                    String urlString = rh.makeLink(request, response, src, fullPath, secure, encode);
                    writer.write(urlString);
                } else {
                    writer.write(src);
                }
            } else  if (urlMode != null && urlMode.equalsIgnoreCase("content")) {
                if (request != null && response != null) {
                    StringBuffer newURL = new StringBuffer();
                    ContentUrlTag.appendContentPrefix(request, newURL);
                    newURL.append(src);
                    writer.write(newURL.toString());
                }
            } else {
                writer.write(src);
            }

            writer.write("\"");
        }
        writer.write("/>");
        
        
        appendWhitespace(writer);
    }

    public void renderContentBegin(Writer writer, Map context, ModelScreenWidget.Content content) throws IOException {

        String editRequest = content.getEditRequest(context);
        Debug.logInfo("directEditRequest:" + editRequest, module);
        if (UtilValidate.isNotEmpty(editRequest)) {
            writer.write("<div");
    
            writer.write(" class=\"editWrapper\">");

            appendWhitespace(writer);
        }
    }

    public void renderContentBody(Writer writer, Map context, ModelScreenWidget.Content content) throws IOException {
            Locale locale = Locale.getDefault();
            Boolean nullThruDatesOnly = new Boolean(false);
            String mimeTypeId = "text/html";
            String expandedContentId = content.getContentId(context);
            String renderedContent = null;
            GenericDelegator delegator = (GenericDelegator) context.get("delegator");
                Debug.logInfo("expandedContentId:" + expandedContentId, module);
            try {
            	if (UtilValidate.isNotEmpty(expandedContentId)) {
                    renderedContent = ContentWorker.renderContentAsTextCache(delegator, expandedContentId, context, null, locale, mimeTypeId);
            	}
                if (UtilValidate.isEmpty(renderedContent)) {
                    String editRequest = content.getEditRequest(context);
                    if (UtilValidate.isNotEmpty(editRequest)) {
                        ContentWorker.renderContentAsTextCache(delegator, "NOCONTENTFOUND", writer, context, null, locale, mimeTypeId);
                    }
                } else {
                    writer.write(renderedContent);
                }

            } catch(GeneralException e) {
                String errMsg = "Error rendering included content with id [" + expandedContentId + "] : " + e.toString();
                Debug.logError(e, errMsg, module);
                //throw new RuntimeException(errMsg);
            } catch(IOException e2) {
                String errMsg = "Error rendering included content with id [" + expandedContentId + "] : " + e2.toString();
                Debug.logError(e2, errMsg, module);
                //throw new RuntimeException(errMsg);
            }
    }

    public void renderContentEnd(Writer writer, Map context, ModelScreenWidget.Content content) throws IOException {

                //Debug.logInfo("renderContentEnd, context:" + context, module);
        String editMode = "Edit";
        String editRequest = content.getEditRequest(context);
        if (editRequest != null && editRequest.toUpperCase().indexOf("IMAGE") > 0) {
            editMode += " Image";
        }
    	Map params = (Map)context.get("parameters");
        //String editRequestWithParams = editRequest + "?contentId=${currentValue.contentId}&drDataResourceId=${currentValue.drDataResourceId}&directEditRequest=${directEditRequest}&indirectEditRequest=${indirectEditRequest}&caContentIdTo=${currentValue.caContentIdTo}&caFromDate=${currentValue.caFromDate}&caContentAssocTypeId=${currentValue.caContentAssocTypeId}";
        	
        if (UtilValidate.isNotEmpty(editRequest)) {
            String contentId = content.getContentId(context);
            HttpServletResponse response = (HttpServletResponse) context.get("response");
            HttpServletRequest request = (HttpServletRequest) context.get("request");
            if (request != null && response != null) {
                ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
                RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");
                String urlString = rh.makeLink(request, response, editRequest, false, false, false);
                String linkString = "<a href=\"" + urlString + "\">" + editMode + "</a>";
                writer.write(linkString);
            }
            writer.write("</div>");
            appendWhitespace(writer);
        }
    }


    public void renderSubContentBegin(Writer writer, Map context, ModelScreenWidget.SubContent content) throws IOException {

        String editRequest = content.getEditRequest(context);
        if (UtilValidate.isNotEmpty(editRequest)) {
            writer.write("<div");
            writer.write(" class=\"editWrapper\">");
    
            writer.write(">");
            appendWhitespace(writer);
        }
    }

    public void renderSubContentBody(Writer writer, Map context, ModelScreenWidget.SubContent content) throws IOException {
            Locale locale = Locale.getDefault();
            Boolean nullThruDatesOnly = new Boolean(false);
            String mimeTypeId = "text/html";
            String expandedContentId = content.getContentId(context);
            String expandedAssocName = content.getAssocName(context);
            String renderedContent = null;
            GenericDelegator delegator = (GenericDelegator) context.get("delegator");
            Timestamp fromDate = UtilDateTime.nowTimestamp();
            HttpServletRequest request = (HttpServletRequest) context.get("request");
            GenericValue userLogin = null;
            if (request != null) {
                HttpSession session = request.getSession();
                userLogin = (GenericValue)session.getAttribute("userLogin");
            }
                Debug.logInfo("expandedContentId:" + expandedContentId, module);
            try {
                renderedContent = ContentWorker.renderSubContentAsTextCache(delegator, expandedContentId, expandedAssocName, null, context, locale, mimeTypeId, userLogin, fromDate);
                if (UtilValidate.isEmpty(renderedContent)) {
                    String editRequest = content.getEditRequest(context);
                    if (UtilValidate.isNotEmpty(editRequest)) {
                        ContentWorker.renderContentAsTextCache(delegator, "NOCONTENTFOUND", writer, context, null, locale, mimeTypeId);
                    }
                } else {
                    writer.write(renderedContent);
                }

            } catch(GeneralException e) {
                String errMsg = "Error rendering included content with id [" + expandedContentId + "] : " + e.toString();
                Debug.logError(e, errMsg, module);
                //throw new RuntimeException(errMsg);
            } catch(IOException e2) {
                String errMsg = "Error rendering included content with id [" + expandedContentId + "] : " + e2.toString();
                Debug.logError(e2, errMsg, module);
                //throw new RuntimeException(errMsg);
            }
    }

    public void renderSubContentEnd(Writer writer, Map context, ModelScreenWidget.SubContent content) throws IOException {

        String editMode = "Edit";
        String editRequest = content.getEditRequest(context);
    	Map params = (Map)context.get("parameters");
        if (editRequest != null && editRequest.toUpperCase().indexOf("IMAGE") > 0) {
            editMode += " Image";
        }
        if (UtilValidate.isNotEmpty(editRequest)) {
            HttpServletResponse response = (HttpServletResponse) context.get("response");
            HttpServletRequest request = (HttpServletRequest) context.get("request");
            if (request != null && response != null) {
                HttpSession session = request.getSession();
                GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
                GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
                String contentIdTo = content.getContentId(context);
                String mapKey = content.getAssocName(context);
                GenericValue view = null;
                try {
                    view = ContentWorker.getSubContentCache(delegator, contentIdTo, mapKey, userLogin, null, UtilDateTime.nowTimestamp(), new Boolean(false), null);
                } catch(GenericEntityException e) {
                    throw new IOException("Originally a GenericEntityException. " + e.getMessage());
                }
                ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
                RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");
                String urlString = rh.makeLink(request, response, editRequest, false, false, false);
                String linkString = "<a href=\"" + urlString + "\">" + editMode + "</a>";
                writer.write(linkString);
            }
            writer.write("</div>");
            appendWhitespace(writer);
        }
    }


    public void appendWhitespace(Writer writer) throws IOException {
        // appending line ends for now, but this could be replaced with a simple space or something
        writer.write("\r\n");
        //writer.write(' ');
    }
}
