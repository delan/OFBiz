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

import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * I18nBundleTag - JSP tag that the MessageTags will use when retrieving keys
 * for this page.
 *
 * @author  <a href="mailto:k3ysss@yahoo.com">Jian He</a>
 * @version 1.0
 * @created April 14, 2002
 */
public class I18nBundleTag extends TagSupport {
    
    private String baseName = null;
    
    private ResourceBundle bundle = null;
    
    public void setBaseName (String baseName) {
        this.baseName = baseName;
    }
    
    public String getBaseName() {
        return this.baseName;
    }
    
    public void setBundle (ResourceBundle bundle) {
        this.bundle = bundle;
    }
    
    public ResourceBundle getBundle() {
        return this.bundle;
    }
    
    public int doStartTag() throws JspException {
        try {
            this.bundle = ResourceBundle.getBundle (this.baseName,
                pageContext.getRequest().getLocale());
            
            if (this.getId() != null) {
                pageContext.setAttribute(this.getId(), this.bundle);
            }
        } catch (Exception e) {
            throw new JspException (e.getMessage());
        }
        
        return EVAL_BODY_INCLUDE;
    }
    
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }
}
