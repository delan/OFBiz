/*
 * $Id$
 * $Log$
 */

package org.ofbiz.core.taglib;

import java.util.Iterator;
import java.io.IOException;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * <p><b>Title:</b> IterateNextTag.java
 * <p><b>Description:</b> Custom JSP Tag to get the next element of the IteratorTag.
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
public class IterateNextTag extends BodyTagSupport {
            
    protected String name = null;
    protected Class type = null;
    protected Object element = null;
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setType(String type) throws ClassNotFoundException {
        this.type = Class.forName(type);
    }
    
    public String getName() {
        return name;
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
        
        if ( type == null ) {
            try {
                setType(iteratorTag.getType());
                setValue("type",type);
            }
            catch ( ClassNotFoundException e ) {
                throw new JspTagException(e.getMessage());
            }            
        }
                
        // get the next element from the iterator
        Object element = iterator.next();
        pageContext.setAttribute(name,element);
        
        // give the updated iterator back.
        iteratorTag.setIterator(iterator);
        
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
        catch(IOException e) {
            System.out.println("IterateNext Tag error: " + e);
        }
        return EVAL_PAGE;
    }   
}



