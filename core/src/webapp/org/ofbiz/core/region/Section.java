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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import org.w3c.dom.*;

/**
 * A section is content with a name that implements Content.render. 
 * <p>That method renders content either by including
 * it or by printing it directly, depending upon the direct
 * value passed to the Section constructor.</p>
 *
 * <p>Note that a section's content can also be a region;if so,
 * Region.render is called from Section.Render().</p>
 *
 *@author     David M. Geary in the book "Advanced Java Server Pages"
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    February 26, 2002
 *@version    1.0
 */
public class Section extends Content {
    protected final String name;
    
    public Section(String name, String content, String direct) {
        super(content, direct);
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void render(PageContext pageContext) throws JspException {
        if(content != null) {
            // see if this section's content is a region
            Region region = (Region)pageContext.findAttribute(content);
            if (region != null) {
                // render the content as a region
                RegionStack.push(pageContext, region);
                region.render(pageContext);
                RegionStack.pop(pageContext);
            } else {
                if (isDirect()) {
                    try {
                        pageContext.getOut().print(content.toString());
                    } catch (java.io.IOException ex) {
                        throw new JspException(ex.getMessage());
                    }
                } else {
                    try {
                        pageContext.include(content.toString());
                    } catch (Exception ex) {
                        throw new JspException(ex.getMessage());
                    }
                }
            }
        }
    }
    
    public String toString() {
        return "Section: " + name + ", content= " + content.toString();
    }
}
