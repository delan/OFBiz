/*
 * $Id$
 * $Log$
 * Revision 1.4  2002/01/21 23:40:26  jonesde
 * Expanded if tag, no longer requires type (uses instanceof), supports Long, Float
 *
 * Revision 1.3  2001/11/11 14:48:51  jonesde
 * Added inputvalue tag
 *
 * Revision 1.2  2001/11/06 22:18:00  jonesde
 * The getSize method now returns a String; Eric Pabst reported this problem, hopefully this will fix it for him.
 *
 * Revision 1.1  2001/09/28 22:56:44  jonesde
 * Big update for fromDate PK use, organization stuff
 *
 * Revision 1.4  2001/09/21 19:38:50  jonesde
 * Updated settings to work with PoolMan & Tomcat 4, the current default config
 * Includes updated JNDIContextFactory and default datasource get through JNDI
 *
 * Revision 1.3  2001/09/21 11:15:17  jonesde
 * Updates related to Tomcat 4 update, bug fixes.
 *
 * Revision 1.2  2001/09/07 21:55:40  epabst
 * catch RuntimeException instead of Exception (better compile checking)
 * added size() checking using Reflection API
 *
 * Revision 1.1  2001/09/01 01:59:06  azeneski
 * Added two new JSP tags.
 *
 */

package org.ofbiz.core.taglib;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import java.util.Collection;
import java.io.IOException;
import java.lang.reflect.Method;

import org.ofbiz.core.util.Debug;

/**
 * <p><b>Title:</b> IfTag.java
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
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@version    1.0
 *@created    August 31, 2001, 7:57 PM
 */
public class IfTag extends BodyTagSupport {
    
    private String name = null;
    private String value = null;
    private String type = null;
    private Integer size = null;
    
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
        this.size = Integer.valueOf(size);
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
    
    public String getSize() {
        if (size == null)
            return null;
        return size.toString();
    }
    
    public int doStartTag() throws JspTagException {
        Object object = null;
        try {
            object = pageContext.findAttribute(name);
            if (object == null)
                return SKIP_BODY;
        } catch (RuntimeException e) {
            return SKIP_BODY;
        }
        //Debug.logInfo("Found object, and is not null");
        
        if (size != null) {
            int localSize = size.intValue();
            //make sure is reset since it is an optional attribute
            size = null;
        
            try {
                if (object instanceof Collection) {
                    // the object is a Collection so compare the size.
                    if (((Collection) object).size() > localSize)
                        return EVAL_BODY_AGAIN;
                } else if (object instanceof String) {
                    // the object is a Collection so compare the size.
                    if (((String) object).length() > localSize)
                        return EVAL_BODY_AGAIN;
                } else {
                    //use reflection to find a size() method
                    try {
                        Method sizeMethod = object.getClass().getMethod("size", null);
                        int objectSize = ((Integer) sizeMethod.invoke(object, null)).intValue();
                        if (objectSize > localSize)
                            return EVAL_BODY_AGAIN;
                    } catch (Exception e) {
                        return SKIP_BODY;
                    }
                }
            } catch (RuntimeException e) {
                return SKIP_BODY;
            }
        } else if (object instanceof String || "String".equalsIgnoreCase(type)) {
            // Assume the object is a string and compare to the String value of value.
            try {
                String s = (String) object;
                if (s.equals(value))
                    return EVAL_BODY_AGAIN;
            }
            catch ( RuntimeException e ) { return SKIP_BODY; }
        } else if (object instanceof Integer || "Integer".equalsIgnoreCase(type)) {
            // Assume the object is a Integer and compare to the Integer value of value.
            try {
                Integer i = (Integer) object;
                Integer v = Integer.valueOf(value);
                if (i.equals(v))
                    return EVAL_BODY_AGAIN;
            } catch (RuntimeException e) {
                return SKIP_BODY;
            }
        } else if (object instanceof Long || "Long".equalsIgnoreCase(type)) {
            // Assume the object is a Integer and compare to the Integer value of value.
            try {
                Long i = (Long) object;
                Long v = Long.valueOf(value);
                if (i.equals(v))
                    return EVAL_BODY_AGAIN;
            } catch (RuntimeException e) {
                return SKIP_BODY;
            }
        } else if (object instanceof Float || "Float".equalsIgnoreCase(type)) {
            // Assume the object is a Double and compare to the Double value of value.
            try {
                Float d = (Float) object;
                Float v = Float.valueOf(value);
                if (d.equals(v))
                    return EVAL_BODY_AGAIN;
            } catch (RuntimeException e) {
                return SKIP_BODY;
            }
        } else if (object instanceof Double || "Double".equalsIgnoreCase(type)) {
            // Assume the object is a Double and compare to the Double value of value.
            try {
                Double d = (Double) object;
                Double v = Double.valueOf(value);
                if (d.equals(v))
                    return EVAL_BODY_AGAIN;
            } catch (RuntimeException e) {
                return SKIP_BODY;
            }
        } else if (object instanceof Boolean || "Boolean".equalsIgnoreCase(type)) {
            // Assume the object is a Boolean and compare to the Boolean value of value.
            try {
                Boolean b = (Boolean) object;
                if(value != null) {
                    Boolean v = new Boolean(value);
                    if(b.equals(v)) return EVAL_BODY_AGAIN;
                } else {
                    if(b.booleanValue()) return EVAL_BODY_AGAIN;
                }
            } catch (RuntimeException e) {
                return SKIP_BODY;
            }
        } else if (value != null) {
            // Assume the object is an Object and compare to the Object named value.
            Object valueObject = null;
            try {
                valueObject = pageContext.findAttribute(value);
                if (valueObject != null && valueObject.equals(object))
                    return EVAL_BODY_AGAIN;
            } catch (RuntimeException e) {
                return SKIP_BODY;
            }
        } else {
            // basicly if no other comparisons available, just check to see if
            // the thing is null or not, and since we've already checked that,
            // treat as true here
            return EVAL_BODY_AGAIN;
        }
        
        return SKIP_BODY;
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
        } catch(IOException e) {
            Debug.logError(e,"IfTag Error.");
        }
        return EVAL_PAGE;
    }
}
