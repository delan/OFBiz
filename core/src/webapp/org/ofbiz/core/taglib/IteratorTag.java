/*
 * $Id$
 */

package org.ofbiz.core.taglib;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Custom JSP Tag to iterate over a collection.
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2002 The Open For Business Project and repected authors.
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
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    1.0
 * @created    August 4, 2001
 */
public class IteratorTag extends BodyTagSupport {

    protected Iterator iterator = null;
    protected String name = null;
    protected String property = null;
    protected Object element = null;
    protected Class type = null;
    protected boolean expandMap = false;

    public void setName(String name) {
        this.name = name;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setType(String type) throws ClassNotFoundException {
        this.type = Class.forName(type);
    }

    public void setExpandMap(String expMap) {
        //defaults to false, so if anything but true will be false:
        expandMap = "true".equals(expMap);
    }

    public void setIterator(Iterator iterator) {
        this.iterator = iterator;
    }

    public String getName() {
        return name;
    }

    public String getProperty() {
        return property;
    }

    public Object getElement() {
        return element;
    }

    public Iterator getIterator() {
        return this.iterator;
    }

    public String getType() {
        return type.getName();
    }

    public String getExpandMap() {
        return expandMap ? "true":"false";
    }

    public int doStartTag() throws JspTagException {
        //Debug.logInfo("Starting Iterator Tag...");

        if (!defineIterator())
            return SKIP_BODY;

        //Debug.logInfo("We now have an iterator.");

        if (defineElement())
            return EVAL_BODY_AGAIN;
        else
            return SKIP_BODY;
    }

    public int doAfterBody() {
        if (defineElement())
            return EVAL_BODY_AGAIN;
        else
            return SKIP_BODY;
    }

    public int doEndTag() {
        try {
            BodyContent body = getBodyContent();
            if (body != null) {
                JspWriter out = body.getEnclosingWriter();
                out.print(body.getString());
            }
        } catch (IOException e) {
            Debug.logInfo("IteratorTag IO Error");
            Debug.logInfo(e);
        }
        return EVAL_PAGE;
    }

    private boolean defineIterator() {
        //clear the iterator, after this it may be set directly
        Iterator newIterator = null;
        Collection thisCollection = null;
        if (property != null) {
            Debug.logInfo("Getting iterator from property: " + property);
            Object propertyObject = pageContext.findAttribute(property);
            if (propertyObject instanceof Iterator) {
                newIterator = (Iterator) propertyObject;
            } else {
                //if ClassCastException, it should indicate looking for a Collection
                thisCollection = (Collection) propertyObject;
            }
        } else {
            //Debug.logInfo("No property, check for Object Tag.");
            ObjectTag objectTag =
                    (ObjectTag) findAncestorWithClass(this, ObjectTag.class);
            if (objectTag == null)
                return false;
            if (objectTag.getType().equals("java.util.Collection")) {
                thisCollection = (Collection) objectTag.getObject();
            } else {
                try {
                    Method[] m =
                            Class.forName(objectTag.getType()).getDeclaredMethods();
                    for (int i = 0; i < m.length; i++) {
                        if (m[i].getName().equals("iterator")) {
                            //Debug.logInfo("Found iterator method. Using it.");
                            newIterator = (Iterator) m[i].invoke(
                                    objectTag.getObject(), null);
                            break;
                        }
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }

        if (newIterator == null) {
            if (thisCollection == null || thisCollection.size() < 1)
                return false;

            newIterator = thisCollection.iterator();
            //Debug.logInfo("Got iterator.");
        } else {//already set
            Debug.logInfo("iterator already set.");
        }
        this.iterator = newIterator;
        return true;
    }

    private boolean defineElement() {
        element = null;
        pageContext.removeAttribute(name);
        if (this.iterator.hasNext()) {
            element = this.iterator.next();
            //Debug.logInfo("iterator has another object: " + element);
        } else {
            //Debug.logInfo("iterator has no more objects");
        }
        if (element != null) {
            //Debug.logInfo("set attribute " + name + " to be " + element + " as next value from iterator");
            pageContext.setAttribute(name, element);

            //expand a map element here if requested
            if (expandMap) {
                Map tempMap = (Map) element;
                Iterator mapEntries = tempMap.entrySet().iterator();
                while (mapEntries.hasNext()) {
                    Map.Entry entry = (Map.Entry) mapEntries.next();
                    Object value = entry.getValue();
                    if (value == null) value = new String();
                    pageContext.setAttribute((String) entry.getKey(), value);
                }
            }

            return true;
        }
        //Debug.logInfo("no more iterations; element = " + element);
        //Debug.logInfo("no more iterations");
        return false;
    }
}




