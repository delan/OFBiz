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

import java.text.*;
import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * I18nMessageTag - JSP tag to use a resource bundle to internationalize
 * content in a web page.
 *
 * @author  <a href="mailto:k3ysss@yahoo.com">Jian He</a>
 * @version 1.0
 * @created April 13, 2002
 */
public class I18nMessageTag extends BodyTagSupport {
    
    private String key = null;
    
    private String value = null;
    
    private ResourceBundle bundle = null;
    
    private final List arguments = new ArrayList();
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setBundleId(String bundleId) {
        this.bundle = (ResourceBundle) pageContext.getAttribute(bundleId);
    }
    
    public void addArgument(Object argument) {
        this.arguments.add(argument);
    }
    
    public int doStartTag() throws JspException {
        try {
            if (this.bundle == null) {
                I18nBundleTag bundleTag = (I18nBundleTag) this.findAncestorWithClass(this, I18nBundleTag.class);
                
                if (bundleTag != null) {
                    this.bundle = bundleTag.getBundle();
                }
            }
            
            this.value = this.bundle.getString(this.key);
        } catch (Exception e) {
            throw new JspException(e.getMessage());
        }
        
        return EVAL_BODY_AGAIN;
    }
    
    public int doEndTag() throws JspException {
        try {
            if (arguments != null) {
                MessageFormat messageFormat = new MessageFormat(this.value);
                messageFormat.setLocale(this.bundle.getLocale());
                this.value = messageFormat.format(arguments.toArray());
            }
            
            this.pageContext.getOut().print(this.value);
        } catch (Exception e) {
            throw new JspException(e.getMessage());
        }
        
        return EVAL_PAGE;
    }
}
