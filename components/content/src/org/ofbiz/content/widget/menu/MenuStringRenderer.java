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
 */
package org.ofbiz.content.widget.menu;

import java.util.Map;


/**
 * Widget Library - Form String Renderer interface
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Rev$
 * @since      2.2
 */
public interface MenuStringRenderer {
    public void renderMenuItem(StringBuffer buffer, Map context, ModelMenuItem menuItem);
    public void renderMenuOpen(StringBuffer buffer, Map context, ModelMenu menu);
    public void renderMenuClose(StringBuffer buffer, Map context, ModelMenu menu);
    public void renderFormatSimpleWrapperOpen(StringBuffer buffer, Map context, ModelMenu menu);
    public void renderFormatSimpleWrapperClose(StringBuffer buffer, Map context, ModelMenu menu);
    public void renderFormatSimpleWrapperRows(StringBuffer buffer, Map context, Object menu);
    public void setUserLoginIdHasChanged(boolean b);
    public void renderLink(StringBuffer buffer, Map context, ModelMenuItem.Link link);
    public void renderImage(StringBuffer buffer, Map context, ModelMenuItem.Image image);
}
