/*
 * $Id: TreeStringRenderer.java,v 1.3 2004/07/29 04:42:37 byersa Exp $
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
 */
package org.ofbiz.content.widget.tree;

import java.util.Map;
import java.io.Writer;
import java.io.IOException;

import org.ofbiz.content.widget.screen.ScreenStringRenderer;

/**
 * Widget Library - Tree String Renderer interface
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.3 $
 * @since      2.2
 */
public interface TreeStringRenderer {

    public void renderNodeBegin(Writer writer, Map context, ModelTree.ModelNode node, int depth, boolean isLast) throws IOException;
    public void renderNodeEnd(Writer writer, Map context, ModelTree.ModelNode node) throws IOException;
    public void renderLabel(Writer writer, Map context, ModelTree.ModelNode.Label label) throws IOException;
    public void renderLink(Writer writer, Map context, ModelTree.ModelNode.Link link) throws IOException;
    public void renderImage(Writer writer, Map context, ModelTree.ModelNode.Image image) throws IOException;
    public ScreenStringRenderer getScreenStringRenderer( Map context);
}
