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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.widget.tree.ModelTree;


/**
 * Widget Library - HTML Form Renderer implementation
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Rev$
 * @since      3.1
 */
public class HtmlTreeExpandCollapseRenderer extends HtmlTreeRenderer {

    //protected List targetNodeTrail = null;
    //protected List currentNodeTrail = null;
   
    public static final String module = HtmlTreeExpandCollapseRenderer.class.getName(); 

    public HtmlTreeExpandCollapseRenderer() {}

    public void renderNodeBegin(Writer writer, Map context, ModelTree.ModelNode node, int depth, boolean isLast) throws IOException {

        context.put("depth", Integer.toString(depth));
        /*
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<depth; i++)
            sb.append("-");
        writer.write(sb.toString());
        */
        writer.write("<div");
        String style = node.getWrapStyle(context);
        if (UtilValidate.isNotEmpty(style)) {
            writer.write(" class=\"");
            writer.write(style);
            writer.write("\"");
        }
        writer.write(">");

        String contentId = (String)context.get("contentId");
        /*
        if (targetNodeTrail == null) {
            targetNodeTrail = node.getModelTree().getTrailList();
    
            Debug.logInfo("HtmlTreeExpandCollapseRenderer, targetNodeTrail(2):" + targetNodeTrail, module);
            currentNodeTrail = new ArrayList();
        }
        */
        boolean hasChildren = node.hasChildren(context);
            Debug.logInfo("HtmlTreeExpandCollapseRenderer, hasChildren(1):" + hasChildren, module);

        // check to see if this node needs to be expanded.
        if (hasChildren) {
            String targetContentId = null;
            List targetNodeTrail = node.getModelTree().getTrailList();
            if (depth < targetNodeTrail.size()) {
                targetContentId = (String)targetNodeTrail.get(depth);
            }
            Debug.logInfo("HtmlTreeExpandCollapseRenderer, targetContentId(1):" + targetContentId, module);
            Debug.logInfo("HtmlTreeExpandCollapseRenderer, depth(1):" + depth, module);
    
            ModelTree.ModelNode.Image expandCollapseImage = new ModelTree.ModelNode.Image();
            expandCollapseImage.setBorder("0");
            ModelTree.ModelNode.Link expandCollapseLink = new ModelTree.ModelNode.Link();
            String expandCollapseStyle = UtilFormatOut.checkEmpty(node.getExpandCollapseStyle(), "expandcollapse");
            expandCollapseLink.setStyle(expandCollapseStyle);
            expandCollapseLink.setImage(expandCollapseImage);
            //String currentNodeTrailCsv = (String)context.get("currentNodeTrailCsv");
            String currentNodeTrailPiped = null;
            List currentNodeTrail = node.getModelTree().getCurrentNodeTrail();
    
            if (targetContentId == null || !targetContentId.equals(contentId)) {
                context.put("processChildren", new Boolean(false));
                //expandCollapseLink.setText("&nbsp;+&nbsp;");
                currentNodeTrailPiped = StringUtil.join(currentNodeTrail, "|");
                context.put("currentNodeTrailPiped", currentNodeTrailPiped);
                //context.put("currentNodeTrailCsv", currentNodeTrailCsv);
                expandCollapseImage.setSrc("/images/expand.gif");
                String target = node.getModelTree().getExpandCollapseRequest(context);
                String trailName = node.getModelTree().getTrailName(context);
                target += "?" + trailName + "=" + currentNodeTrailPiped;
                //expandCollapseLink.setTarget("/ViewOutline?docRootContentId=${docRootContentId}&targetNodeTrailCsv=${currentNodeTrailCsv}");
                expandCollapseLink.setTarget(target);
            } else {
                context.put("processChildren", new Boolean(true));
                //expandCollapseLink.setText("&nbsp;-&nbsp;");
                String lastContentId = (String)currentNodeTrail.remove(currentNodeTrail.size() - 1);
                currentNodeTrailPiped = StringUtil.join(currentNodeTrail, "|");
                context.put("currentNodeTrailPiped", currentNodeTrailPiped);
                //context.put("currentNodeTrailCsv", currentNodeTrailCsv);
                expandCollapseImage.setSrc("/images/collapse.gif");
                String target = node.getModelTree().getExpandCollapseRequest(context);
                String trailName = node.getModelTree().getTrailName(context);
                target += "?" + trailName + "=" + currentNodeTrailPiped;
                expandCollapseLink.setTarget(target);
                // add it so it can be remove in renderNodeEnd
                currentNodeTrail.add(lastContentId);
            }
            renderLink( writer, context, expandCollapseLink);
        } else {
                writer.write("&nbsp;");
                context.put("processChildren", new Boolean(false));
                //currentNodeTrail.add(contentId);
        }

        return;
    }

    public void renderNodeEnd(Writer writer, Map context, ModelTree.ModelNode node) throws IOException {
        //currentNodeTrail.remove(currentNodeTrail.size() - 1);
        writer.write("</div>");
        return;
    }


    public void appendWhitespace(Writer writer) throws IOException {
        // appending line ends for now, but this could be replaced with a simple space or something
        writer.write("\r\n");
        //writer.write(' ');
    }
}
