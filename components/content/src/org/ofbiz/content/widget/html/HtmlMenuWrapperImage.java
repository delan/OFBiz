/*
 * $Id: HtmlMenuWrapperImage.java,v 1.2 2004/04/11 08:28:18 jonesde Exp $
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
package org.ofbiz.content.widget.html;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.content.widget.menu.MenuStringRenderer;
import org.xml.sax.SAXException;

/**
 * Widget Library - HTML Menu Wrapper class - makes it easy to do the setup and render of a menu
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.2 $
 * @since      3.0
 */
public class HtmlMenuWrapperImage extends HtmlMenuWrapper {
    
    protected HtmlMenuWrapperImage() {}

    public HtmlMenuWrapperImage(String resourceName, String menuName, HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SAXException, ParserConfigurationException {
        super(resourceName, menuName, request, response);
    }

    public MenuStringRenderer getMenuRenderer() {
        return new HtmlMenuRendererImage(request, response);
    }
    
}
