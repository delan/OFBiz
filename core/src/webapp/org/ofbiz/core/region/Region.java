/*
 * $Id$
 *
 * Copyright (c) 2001 Sun Microsystems Inc., published in "Advanced Java Server Pages" by Prentice Hall PTR
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.region;

import java.net.*;
import java.util.*;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;
import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;

import org.ofbiz.core.util.*;

/**
 * A region is content that contains a set of sections that can render in a PageContext
 * <br>Implements abstract render(PageContext) from Content
 *
 *@author     David M. Geary in the book "Advanced Java Server Pages"
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    February 26, 2002
 *@version    1.0
 */
public class Region extends Content {

    private Map sections = new HashMap();
    protected String id;

    public Region(String id, String content) {
        this(id, content, null); // content is the name of a template
    }

    public Region(String id, String content, Map sections) {
        super(content);
        this.id = id;
        if (sections != null)
            this.sections.putAll(sections);
    }

    public String getId() {
        return this.id;
    }

    public void put(Section section) {
        sections.put(section.getName(), section);
    }

    public void putAll(Map newSections) {
        sections.putAll(newSections);
    }

    public Section get(String name) {
        return (Section) sections.get(name);
    }

    public Map getSections() {
        return sections;
    }

    public void render(PageContext pageContext) throws JspException {
        Debug.logVerbose("Rendering " + this.toString());

        try {
            pageContext.include(content);
        } catch (Exception ex) {
            // IOException or ServletException
            throw new JspException(ex.getMessage());
        }
    }

    public void render(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException, ServletException {
        Debug.logVerbose("Rendering " + this.toString());

        //this render method does not come from a page tag so some setup needs to happen here
        RegionStack.push(request, this);

        RequestDispatcher rd = request.getRequestDispatcher(content);
        if (rd == null)
            throw new IllegalArgumentException("Source returned a null dispatcher (" + content + ")");
        rd.include(request, response);
    }

    public String toString() {
        String s = "Region: " + content.toString() + "<br/>";
        int indent = 4;
        Iterator iter = sections.values().iterator();

        while (iter.hasNext()) {
            Section section = (Section) iter.next();
            for (int i = 0; i < indent; ++i) {
                s += "&nbsp;";
            }
            s += section.toString() + "<br/>";
        }
        return s;
    }
}
