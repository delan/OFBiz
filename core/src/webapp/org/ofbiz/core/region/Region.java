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
import org.w3c.dom.*;

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
    public static final String regionsFileName = "/WEB-INF/regions.xml"; 
    
    private Map sections = new HashMap();
    
    public Region(String content) {
        this(content, null); // content is the name of a template
    }
    
    public Region(String content, Map sections) {
        super(content);
        
        if (sections != null)
            this.sections = new HashMap(sections);
    }
    
    public void put(Section section) {
        sections.put(section.getName(), section);
    }
    
    public Section get(String name) {
        return (Section) sections.get(name);
    }
    
    public Map getSections() {
        return sections;
    }
    
    public void render(PageContext pageContext) throws JspException {
        try {
            pageContext.include(content);
        } catch (Exception ex) {
            // IOException or ServletException
            throw new JspException(ex.getMessage());
        }
    }
    
    public String toString() {
        String s = "Region: " + content.toString() + "<br/>";
        int indent = 4;
        Iterator iter = sections.values().iterator();
        
        while (iter.hasNext()) {
            Section section = (Section) iter.next();
            for (int i=0; i < indent; ++i) {
                s += "&nbsp;";
            }
            s += section.toString() + "<br/>";
        }
        return s;
    }
}
