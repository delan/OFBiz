/*
 * $Id: HtmlTreeExpandCollapseRenderer.java,v 1.4 2004/08/09 23:52:21 jonesde Exp $
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
 * @version    $Revision: 1.4 $
 * @since      3.1
 */
public class HtmlTreeExpandCollapseRenderer extends HtmlTreeRenderer {

    protected List targetNodeTrail = null;
    protected List currentNodeTrail = null;
   
    public static final String module = HtmlTreeExpandCollapseRenderer.class.getName(); 

    public HtmlTreeExpandCollapseRenderer() {}

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
    
            ModelTree.ModelNode.Image expandCollapseImage = new ModelTree.ModelNode.Image();
            expandCollapseImage.setBorder("0");
            ModelTree.ModelNode.Link expandCollapseLink = new ModelTree.ModelNode.Link();
            String expandCollapseStyle = UtilFormatOut.checkEmpty(node.getExpandCollapseStyle(), "expandcollapse");
            expandCollapseLink.setStyle(expandCollapseStyle);
            expandCollapseLink.setImage(expandCollapseImage);
            //String currentNodeTrailCsv = (String)context.get("currentNodeTrailCsv");
            String currentNodeTrailCsv = null;
    
            if (targetContentId == null || !targetContentId.equals(contentId)) {
                context.put("processChildren", new Boolean(false));
                //expandCollapseLink.setText("&nbsp;+&nbsp;");
                currentNodeTrail.add(contentId);
                currentNodeTrailCsv = StringUtil.join(currentNodeTrail, ",");
                context.put("currentNodeTrailCsv", currentNodeTrailCsv);
                expandCollapseImage.setSrc("/images/expand.gif");
                expandCollapseLink.setTarget("/ViewOutline?contentId=${rootContentId}&targetNodeTrailCsv=${currentNodeTrailCsv}");
            } else {
                context.put("processChildren", new Boolean(true));
                //expandCollapseLink.setText("&nbsp;-&nbsp;");
                currentNodeTrailCsv = StringUtil.join(currentNodeTrail, ",");
                context.put("currentNodeTrailCsv", currentNodeTrailCsv);
                expandCollapseImage.setSrc("/images/collapse.gif");
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


    public void appendWhitespace(Writer writer) throws IOException {
        // appending line ends for now, but this could be replaced with a simple space or something
        writer.write("\r\n");
        //writer.write(' ');
    }
}
