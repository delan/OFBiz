/*
 * $Id$
 * $Log$ 
 */

package org.ofbiz.core.taglib;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import java.util.Collection;
import java.io.IOException;

import org.ofbiz.core.util.Debug;

/**
 * <p><b>Title:</b> UnlessTag.java
 * <p><b>Description:</b> Custom JSP tag to test page context attributes.
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
 * Created on August 31, 2001, 7:57 PM
 */
public class UnlessTag extends BodyTagSupport {
    
    private String name = null;
    private String value = null;
    private String type = null;
    private int size = -1;
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setSize(String size) throws NumberFormatException {
        this.size = Integer.parseInt(size);
    }
    
    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getType() {
        return type;
    }
    
    public int getSize() {
        return size;
    }
    
    public int doStartTag() throws JspTagException {
        
        Object object = null;
        try {
            object = pageContext.findAttribute(name);
            if ( object == null )
                return EVAL_BODY_TAG;
        }
        catch ( Exception e ) { return EVAL_BODY_TAG; }
        Debug.logInfo("Found object, and is not null");
        
        if ( size == -1 && value == null && type == null )
            return SKIP_BODY;
        
        if (  size > -1 ) {
            // Assume the object is a Collection and compare the size.
            try {
                Collection c = (Collection) object;
                if ( c.size() > size )
                    return SKIP_BODY;
            }
            catch ( Exception e ) { return EVAL_BODY_TAG; }
        }
        
        else if ( type.equalsIgnoreCase("String") ) {
            // Assume the object is a string and compare to the String value of value.
            try {
                String s = (String) object;
                if ( s.equals(value) )
                    return SKIP_BODY;
            }
            catch ( Exception e ) { return EVAL_BODY_TAG; }
        }
        
        else if ( type.equalsIgnoreCase("Integer") ) {
            // Assume the object is a Integer and compare to the Integer value of value.
            try {
                Integer i = (Integer) object;
                Integer v = new Integer(value);
                if ( i == v )
                    return SKIP_BODY;
            }
            catch ( Exception e ) { return EVAL_BODY_TAG; }
        }
        
        else if ( type.equalsIgnoreCase("Double") ) {
            // Assume the object is a Double and compare to the Double value of value.
            try {
                Double d = (Double) object;
                Double v = new Double(value);
                if ( d == v )
                    return SKIP_BODY;
            }
            catch ( Exception e ) { return EVAL_BODY_TAG; }
        }
        
        else if ( type.equalsIgnoreCase("Boolean") ) {
            // Assume the object is a Boolean and compare to the Boolean value of value.
            try {
                Boolean b = (Boolean) object;
                Boolean v = new Boolean(value);
                if ( b.equals(v) )
                    return SKIP_BODY;
            }
            catch ( Exception e ) { return EVAL_BODY_TAG; }
        }
        
        else {
            // Assume the object is an Object and compare to the Object named value.
            Object valueObject = null;
            try {
                valueObject = pageContext.findAttribute(value);
                if ( valueObject != null && valueObject.equals(object) )
                    return SKIP_BODY;
            }
            catch ( Exception e ) { return EVAL_BODY_TAG; }
        }
        
        return EVAL_BODY_TAG;        
    }
    
    public int doAfterBody() {
        return SKIP_BODY;
    }
    
    public int doEndTag() {
        try {
            BodyContent body = getBodyContent();
            if (body != null) {
                JspWriter out = body.getEnclosingWriter();
                out.print(body.getString());
            }
        }
        catch(IOException e) { Debug.logError(e,"IfTag Error."); }
        return EVAL_PAGE;
    }
}
