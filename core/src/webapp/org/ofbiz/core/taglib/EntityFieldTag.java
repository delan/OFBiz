/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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

import java.io.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.pseudotag.*;
import org.ofbiz.core.util.*;

/**
 * EntityFieldTag - Tag to Print Localized Entity Fields.
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class EntityFieldTag extends TagSupport {

    protected String field = null;
    protected String type = null;
    protected String attribute = null;
    protected Object defaultObj = "";
    protected String prefix = null;
    protected String suffix = null;

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Object getDefault() {
        return defaultObj;
    }

    public void setDefault(Object defaultObj) {
        this.defaultObj = defaultObj;
    }

    public int doStartTag() throws JspException {
        try {
            EntityField.run(attribute, field, prefix, suffix, defaultObj.toString(), type, pageContext);
        } catch (IOException e) {
            if (UtilJ2eeCompat.useNestedJspException(pageContext.getServletContext())) {
                throw new JspException(e.getMessage(), e);
            } else {
                Debug.logError(e, "Server does not support nested exceptions, here is the exception");
                throw new JspException(e.toString());
            }
        } catch (GenericEntityException e) {
            if (UtilJ2eeCompat.useNestedJspException(pageContext.getServletContext())) {
                throw new JspException("Entity Engine error: " + e.getMessage(), e);
            } else {
                Debug.logError(e, "Server does not support nested exceptions, here is the exception");
                throw new JspException("Entity Engine error: " + e.toString());
            }
        }

        return (SKIP_BODY);
    }
}
