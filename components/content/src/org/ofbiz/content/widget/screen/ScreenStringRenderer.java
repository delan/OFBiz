/*
 * $Id: ScreenStringRenderer.java,v 1.7 2004/07/27 20:29:40 byersa Exp $
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
package org.ofbiz.content.widget.screen;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;


/**
 * Widget Library - Screen String Renderer interface
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.7 $
 * @since      3.1
 */
public interface ScreenStringRenderer {
    public void renderSectionBegin(Writer writer, Map context, ModelScreenWidget.Section section) throws IOException;
    public void renderSectionEnd(Writer writer, Map context, ModelScreenWidget.Section section) throws IOException;
    public void renderContainerBegin(Writer writer, Map context, ModelScreenWidget.Container container) throws IOException;
    public void renderContainerEnd(Writer writer, Map context, ModelScreenWidget.Container container) throws IOException;

    public void renderLabel(Writer writer, Map context, ModelScreenWidget.Label label) throws IOException;
    public void renderLink(Writer writer, Map context, ModelScreenWidget.Link link) throws IOException;
    public void renderImage(Writer writer, Map context, ModelScreenWidget.Image image) throws IOException;
}

