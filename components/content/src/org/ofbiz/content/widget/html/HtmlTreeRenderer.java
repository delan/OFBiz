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
import java.util.Map;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.content.webapp.control.RequestHandler;
import org.ofbiz.content.webapp.taglib.ContentUrlTag;
import org.ofbiz.content.widget.screen.ScreenRenderer;
import org.ofbiz.content.widget.screen.ScreenStringRenderer;
import org.ofbiz.content.widget.tree.ModelTree;
import org.ofbiz.content.widget.tree.TreeStringRenderer;


/**
 * Widget Library - HTML Form Renderer implementation
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Rev$
 * @since      3.1
 */
public class HtmlTreeRenderer implements TreeStringRenderer {

    ScreenStringRenderer screenStringRenderer = null;
    public static final String module = HtmlTreeRenderer.class.getName(); 

    public HtmlTreeRenderer() {}
    
    public static String buildPathString(ModelTree modelTree, int depth) {
    	StringBuffer buf = new StringBuffer();
        for (int i=1; i <= depth; i++) {
            int idx = modelTree.getNodeIndexAtDepth(i);
       		buf.append(".");
        	buf.append(Integer.toString(idx + 1));
        }
        return buf.toString();
    }

    public void renderNodeBegin(Writer writer, Map context, ModelTree.ModelNode node, int depth, boolean isLast, List subNodeValues) throws IOException {

        String pathString = buildPathString(node.getModelTree(), depth);
        String currentNodeTrailPiped = null;
        List currentNodeTrail = node.getModelTree().getCurrentNodeTrail();
        String staticNodeTrailPiped = StringUtil.join(currentNodeTrail, "|");
        context.put("staticNodeTrailPiped", staticNodeTrailPiped);
        context.put("nodePathString", pathString);
        //int idx = node.getModelTree().getNodeIndexAtDepth(depth);
        context.put("depth", Integer.toString(depth));
        /*
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<depth; i++)
            sb.append("&nbsp;&nbsp;");
        writer.write(sb.toString());
        */
        String style = node.getWrapStyle(context);
        if (UtilValidate.isNotEmpty(style)) {
        	writer.write("<div");
            writer.write(" class=\"");
            writer.write(style);
            writer.write("\"");
            writer.write(">");
        }

        String pkName = node.getModelTree().getPkName();
        String entityId = (String)context.get(pkName);
        /*
        if (targetNodeTrail == null) {
            targetNodeTrail = node.getModelTree().getTrailList();
    
            Debug.logInfo("HtmlTreeExpandCollapseRenderer, targetNodeTrail(2):" + targetNodeTrail, module);
            currentNodeTrail = new ArrayList();
        }
        */
        boolean hasChildren = node.hasChildren(context, subNodeValues);
            //Debug.logInfo("HtmlTreeExpandCollapseRenderer, hasChildren(1):" + hasChildren, module);

        // check to see if this node needs to be expanded.
        if (hasChildren && node.isExpandCollapse()) {
            String targetEntityId = null;
            List targetNodeTrail = node.getModelTree().getTrailList();
            if (depth < targetNodeTrail.size()) {
                targetEntityId = (String)targetNodeTrail.get(depth);
            }
            //Debug.logInfo("HtmlTreeExpandCollapseRenderer, targetEntityId(1):" + targetEntityId, module);
            //Debug.logInfo("HtmlTreeExpandCollapseRenderer, depth(1):" + depth, module);
    
            ModelTree.ModelNode.Image expandCollapseImage = new ModelTree.ModelNode.Image();
            expandCollapseImage.setBorder("0");
            ModelTree.ModelNode.Link expandCollapseLink = new ModelTree.ModelNode.Link();
            String expandCollapseStyle = UtilFormatOut.checkEmpty(node.getExpandCollapseStyle(), "expandcollapse");
            expandCollapseLink.setStyle(expandCollapseStyle);
            expandCollapseLink.setImage(expandCollapseImage);
            //String currentNodeTrailCsv = (String)context.get("currentNodeTrailCsv");
    
            int openDepth = node.getModelTree().getOpenDepth();
            if (targetEntityId == null || !targetEntityId.equals(entityId)) {
                if( node.showPeers(depth)) {
                	context.put("processChildren", new Boolean(false));
                	//expandCollapseLink.setText("&nbsp;+&nbsp;");
                	currentNodeTrailPiped = StringUtil.join(currentNodeTrail, "|");
                	context.put("currentNodeTrailPiped", currentNodeTrailPiped);
                	//context.put("currentNodeTrailCsv", currentNodeTrailCsv);
                	expandCollapseImage.setSrc("/images/expand.gif");
                	String target = node.getModelTree().getExpandCollapseRequest(context);
                	String trailName = node.getModelTree().getTrailName(context);
                	target += "?" + trailName + "=" + currentNodeTrailPiped;
                    target += "#" + staticNodeTrailPiped;
                	//expandCollapseLink.setTarget("/ViewOutline?docRootContentId=${docRootContentId}&targetNodeTrailCsv=${currentNodeTrailCsv}");
                	expandCollapseLink.setTarget(target);
                }
            } else {
                context.put("processChildren", new Boolean(true));
                //expandCollapseLink.setText("&nbsp;-&nbsp;");
                String lastContentId = (String)currentNodeTrail.remove(currentNodeTrail.size() - 1);
                currentNodeTrailPiped = StringUtil.join(currentNodeTrail, "|");
                if (currentNodeTrailPiped == null)
                    currentNodeTrailPiped = "";
                context.put("currentNodeTrailPiped", currentNodeTrailPiped);
                //context.put("currentNodeTrailCsv", currentNodeTrailCsv);
                expandCollapseImage.setSrc("/images/collapse.gif");
                String target = node.getModelTree().getExpandCollapseRequest(context);
                String trailName = node.getModelTree().getTrailName(context);
                target += "?" + trailName + "=" + currentNodeTrailPiped;
                target += "#" + staticNodeTrailPiped;
                expandCollapseLink.setTarget(target);
                // add it so it can be remove in renderNodeEnd
                currentNodeTrail.add(lastContentId);
                currentNodeTrailPiped = StringUtil.join(currentNodeTrail, "|");
                if (currentNodeTrailPiped == null)
                    currentNodeTrailPiped = "";
                context.put("currentNodeTrailPiped", currentNodeTrailPiped);
            }
            renderLink( writer, context, expandCollapseLink);
        } else if (!hasChildren){
                writer.write(" ");
                context.put("processChildren", new Boolean(false));
                //currentNodeTrail.add(contentId);
        }
        return;
    }

    public void renderNodeEnd(Writer writer, Map context, ModelTree.ModelNode node) throws IOException {
        String style = node.getWrapStyle(context);
        if (UtilValidate.isNotEmpty(style)) {
        writer.write("</div>");
        }
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
        String name = link.getName(context);
        if (UtilValidate.isNotEmpty(name)) {
            writer.write(" name=\"");
            writer.write(name);
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
            HttpServletResponse res = (HttpServletResponse) context.get("response");
            HttpServletRequest req = (HttpServletRequest) context.get("request");
            if (urlMode != null && urlMode.equalsIgnoreCase("intra-app")) {
                if (req != null && res != null) {
                    ServletContext ctx = (ServletContext) req.getAttribute("servletContext");
                    RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");
                    String urlString = rh.makeLink(req, res, target, fullPath, secure, encode);
                    writer.write(urlString);
                } else if (prefix != null) {
                    writer.write(prefix + target);
                } else {
                    writer.write(target);
                }
            } else  if (urlMode != null && urlMode.equalsIgnoreCase("content")) {
                StringBuffer newURL = new StringBuffer();
                ContentUrlTag.appendContentPrefix(req, newURL);
                newURL.append(target);
                writer.write(newURL.toString());
            } else {
                writer.write(target);
            }

            writer.write("\"");
        }
        writer.write(">");
        
        // the text
        ModelTree.ModelNode.Image img = link.getImage();
        if (img == null)
            writer.write(link.getText(context));
        else
            renderImage(writer, context, img);
        
        // close tag
        writer.write("</a>");
        
        appendWhitespace(writer);
    }

    public void renderImage(Writer writer, Map context, ModelTree.ModelNode.Image image) throws IOException {
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
        
    }

    public void appendWhitespace(Writer writer) throws IOException {
        // appending line ends for now, but this could be replaced with a simple space or something
        writer.write("\r\n");
        //writer.write(' ');
    }

    public ScreenStringRenderer getScreenStringRenderer(Map context) {

        ScreenRenderer screenRenderer = (ScreenRenderer)context.get("screens"); 
        if (screenRenderer != null) {
            screenStringRenderer = screenRenderer.getScreenStringRenderer();
        } else {
            if (screenStringRenderer == null) {
                screenStringRenderer = new HtmlScreenRenderer();
            }
        }
        return screenStringRenderer;
    }
}
