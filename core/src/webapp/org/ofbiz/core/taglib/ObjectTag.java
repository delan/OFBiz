/*
 * $Id$
 *
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

package org.ofbiz.core.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.ofbiz.core.util.*;

/**
 * ObjectTag - Loads an object from the PageContext.
 *
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @version    1.0
 * @created    August 4, 2001
 */
public class ObjectTag extends TagSupport {

    protected Object element = null;
    protected String name = null;
    protected String property = null;
    protected Class type = null;

    public void setName(String name) {
        this.name = name;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setType(String type) throws ClassNotFoundException {
        this.type = ObjectType.loadClass(type);
    }

    public String getName() {
        return name;
    }

    public String getProperty() {
        return property;
    }

    public Object getObject() {
        return element;
    }

    public String getType() {
        return type.getName();
    }

    public int doStartTag() throws JspTagException {
        element = pageContext.findAttribute(property);
        if (element != null) {
            pageContext.setAttribute(name, element);
        } else {
            Debug.logWarning("Did not find element in property. (" + property + ")");
        }
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() {
        return EVAL_PAGE;
    }
}




