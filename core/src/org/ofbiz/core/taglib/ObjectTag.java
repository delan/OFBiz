/*
 * $Id$
 * $Log$
 * Revision 1.2  2001/08/06 00:45:09  azeneski
 * minor adjustments to tag files. added new format tag.
 *
 * Revision 1.1  2001/08/05 00:48:47  azeneski
 * Added new core JSP tag library. Non-application specific taglibs.
 *
 */

package org.ofbiz.core.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.ofbiz.core.util.Debug;

/**
 * <p><b>Title:</b> ObjectTag.java
 * <p><b>Description:</b> Custom JSP Tag to give page context to a stored object.
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on August 4, 2001, 8:21 PM
 */
public  class ObjectTag extends TagSupport {
    
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
        this.type = Class.forName(type);
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
        //Debug.logInfo("Starting Object Tag...");
        element = pageContext.findAttribute(property);        
        if ( element != null ) {
            //Debug.logInfo("Got element from property: " + property);
            pageContext.setAttribute(name,element);
        }
        else {
            Debug.logWarning("Did not find element in property. ("+property+")");
        }
        return EVAL_BODY_INCLUDE;
    }
    
    public int doEndTag() {
        //Debug.logInfo("ObjectTag done.");
        return EVAL_PAGE;
    }
}



