/*
 * $Id$
 * $Log$
 * Revision 1.1  2001/09/28 22:56:44  jonesde
 * Big update for fromDate PK use, organization stuff
 *
 * Revision 1.4  2001/09/21 11:15:17  jonesde
 * Updates related to Tomcat 4 update, bug fixes.
 *
 * Revision 1.3  2001/08/16 01:24:19  azeneski
 * Updated property tags to use the EntityValue by default.
 *
 * Revision 1.2  2001/08/06 00:45:09  azeneski
 * minor adjustments to tag files. added new format tag.
 *
 * Revision 1.1  2001/08/05 00:48:47  azeneski
 * Added new core JSP tag library. Non-application specific taglibs.
 *
 */

package org.ofbiz.core.taglib;

import java.util.*;
import java.io.IOException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;

/**
 * <p><b>Title:</b> Custom JSP Tag to get the next element of the IteratorTag.
 * <p><b>Description:</b> None
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
 *@author <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version 1.0
 * Created on August 4, 2001, 8:21 PM
 */
public class IterateNextTag extends BodyTagSupport {
    
    protected String name = null;
    protected Class type = null;
    protected Object element = null;
    protected boolean expandMap = false;
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setType(String type) throws ClassNotFoundException {
        this.type = Class.forName(type);
    }
    
    public void setExpandMap(String expMap) {
        //defaults to false, so if anything but true will be false:
        expandMap = "true".equals(expMap);
    }
    
    public String getName() {
        return name;
    }
    
    public String getExpandMap() {
        return expandMap?"true":"false";
    }
    
    public Object getElement() {
        return element;
    }
    
    public int doStartTag() throws JspTagException {
        IteratorTag iteratorTag = (IteratorTag) findAncestorWithClass(this, IteratorTag.class);
        
        if ( iteratorTag == null )
            throw new JspTagException("IterateNextTag not inside IteratorTag.");
        
        Iterator iterator = iteratorTag.getIterator();
        
        if (iterator == null || !iterator.hasNext())
            return SKIP_BODY;
        
        if ( name == null )
            name = "next";
        
        // get the next element from the iterator
        Object element = iterator.next();
        pageContext.setAttribute(name,element);

        //expand a map element here if requested
        if (expandMap) {
            Map tempMap = (Map) element;
            Iterator mapEntries = tempMap.entrySet().iterator();
            while (mapEntries.hasNext()) {
                Map.Entry entry = (Map.Entry) mapEntries.next();
                pageContext.setAttribute((String) entry.getKey(), entry.getValue());
            }
        }
        
        // give the updated iterator back.
        iteratorTag.setIterator(iterator);
        
        return EVAL_BODY_AGAIN;
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
            System.out.println("IterateNext Tag error: " + e);
        }
        return EVAL_PAGE;
    }
}
