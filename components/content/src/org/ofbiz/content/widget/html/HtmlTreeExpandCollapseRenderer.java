/*
 * $Id: HtmlTreeExpandCollapseRenderer.java,v 1.1 2004/07/24 23:01:20 byersa Exp $
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
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.content.widget.tree.ModelTree;
import org.ofbiz.content.widget.tree.TreeStringRenderer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.content.webapp.taglib.ContentUrlTag;
import org.ofbiz.content.webapp.control.RequestHandler;


/**
 * Widget Library - HTML Form Renderer implementation
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class HtmlTreeExpandCollapseRenderer implements TreeStringRenderer {

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected List targetNodeTrail = null;
    protected List currentNodeTrail = null;
   
    public static final String module = HtmlTreeExpandCollapseRenderer.class.getName(); 

    public HtmlTreeExpandCollapseRenderer() {}

    public HtmlTreeExpandCollapseRenderer(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public void renderNodeBegin(Writer writer, Map context, ModelTree.ModelNode node, int depth, boolean isLast) throws IOException {

        StringBuffer sb = new StringBuffer();
        for (int i=0; i<depth; i++)
            sb.append("-");
        writer.write(sb.toString());

        String contentId = (String)context.get("contentId");
        if (targetNodeTrail == null) {
            String targetNodeTrailCsv = (String)context.get("targetNodeTrailCsv");
            if (UtilValidate.isNotEmpty(targetNodeTrailCsv))
                targetNodeTrail = StringUtil.split(targetNodeTrailCsv, ",");
            else
                targetNodeTrail = new ArrayList();
    
            Debug.logInfo("HtmlTreeExpandCollapseRenderer, targetNodeTrail(2):" + targetNodeTrail, module);
            currentNodeTrail = new ArrayList();
        }
        boolean hasChildren = node.hasChildren(context);
            Debug.logInfo("HtmlTreeExpandCollapseRenderer, hasChildren(1):" + hasChildren, module);
        if (hasChildren) {
            Debug.logInfo("HtmlTreeExpandCollapseRenderer, targetNodeTrail(1):" + targetNodeTrail, module);
            Debug.logInfo("HtmlTreeExpandCollapseRenderer, currentNodeTrail(1):" + currentNodeTrail, module);
            Debug.logInfo("HtmlTreeExpandCollapseRenderer, contentId(1):" + contentId, module);
            String targetContentId = null;
            if (depth < targetNodeTrail.size()) {
                targetContentId = (String)targetNodeTrail.get(depth);
            }
            Debug.logInfo("HtmlTreeExpandCollapseRenderer, targetContentId(1):" + targetContentId, module);
            Debug.logInfo("HtmlTreeExpandCollapseRenderer, depth(1):" + depth, module);
    
            ModelTree.ModelNode.Link expandCollapseLink = new ModelTree.ModelNode.Link();
            expandCollapseLink.setStyle("");
            //String currentNodeTrailCsv = (String)context.get("currentNodeTrailCsv");
            String currentNodeTrailCsv = null;
    
            if (targetContentId == null || !targetContentId.equals(contentId)) {
                context.put("processChildren", new Boolean(false));
                expandCollapseLink.setText("+");
                currentNodeTrail.add(contentId);
                currentNodeTrailCsv = StringUtil.join(currentNodeTrail, ",");
                context.put("currentNodeTrailCsv", currentNodeTrailCsv);
                expandCollapseLink.setTarget("/ViewOutline?contentId=${rootContentId}&targetNodeTrailCsv=${currentNodeTrailCsv}");
            } else {
                context.put("processChildren", new Boolean(true));
                expandCollapseLink.setText("-");
                currentNodeTrailCsv = StringUtil.join(currentNodeTrail, ",");
                context.put("currentNodeTrailCsv", currentNodeTrailCsv);
                expandCollapseLink.setTarget("/ViewOutline?contentId=${rootContentId}&targetNodeTrailCsv=${currentNodeTrailCsv}");
                // add it so it can be remove in renderNodeEnd
                currentNodeTrail.add(contentId);
            }
            renderLink( writer, context, expandCollapseLink);
        } else {
                writer.write("&nbsp;");
                context.put("processChildren", new Boolean(false));
                currentNodeTrail.add(contentId);
        }

        return;
    }

    public void renderNodeEnd(Writer writer, Map context, ModelTree.ModelNode node) throws IOException {
        currentNodeTrail.remove(currentNodeTrail.size() - 1);
        return;
    }

    public void renderLabel(Writer writer, Map context, ModelTree.ModelNode.Label label) throws IOException {
        // open tag
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


    public void renderLink(Writer writer, Map context, ModelTree.ModelNode.Link link) throws IOException {
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
        String target = link.getTarget(context);
        if (UtilValidate.isNotEmpty(target)) {
            writer.write(" href=\"");
            String urlMode = link.getUrlMode();
            String prefix = link.getPrefix(context);
            boolean fullPath = link.getFullPath();
            boolean secure = link.getSecure();
            boolean encode = link.getEncode();
            if (urlMode != null && urlMode.equalsIgnoreCase("ofbiz")) {
                HttpServletResponse response = (HttpServletResponse) context.get("response");
                HttpServletRequest request = (HttpServletRequest) context.get("request");
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
                StringBuffer newURL = new StringBuffer();
                ContentUrlTag.appendContentPrefix(request, newURL);
                newURL.append(target);
                writer.write(newURL.toString());
            } else {
                writer.write(target);
            }

            writer.write("\"");
        }
        writer.write(">");
        
        // the text
        writer.write(link.getText(context));
        
        // close tag
        writer.write("</a>");
        
        appendWhitespace(writer);
    }

    public void appendWhitespace(Writer writer) throws IOException {
        // appending line ends for now, but this could be replaced with a simple space or something
        writer.write("\r\n");
        //writer.write(' ');
    }
}
