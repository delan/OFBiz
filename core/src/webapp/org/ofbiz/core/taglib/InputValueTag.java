package org.ofbiz.core.taglib;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.DateFormat;
import java.util.*;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.entity.model.*;
import org.ofbiz.core.util.Debug;

/**
 * <p><b>Title:</b> InputValueTag
 * <p><b>Description:</b> Outputs a string for an input box from either an entity field or a request parameter.
 *   Decides which to use by checking to see if the entityattr exist and using the specified field if it does.
 *   If the Boolean object referred to by the tryentityattr attribute is false, always tries to use the request parameter and ignores the entity field.
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
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
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    Mon Nov 5, 2001
 *@version    1.0
 */
public class InputValueTag extends TagSupport {
    private String field = null;
    private String param = null;
    private String entityAttr = null;
    private String tryEntityAttr = null;
    private String defaultStr = "";

    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }

    public String getParam() {
        return param;
    }
    public void setParam(String param) {
        this.param = param;
    }

    public String getEntityAttr() {
        return entityAttr;
    }
    public void setEntityAttr(String entityAttr) {
        this.entityAttr = entityAttr;
    }

    public String getTryEntityAttr() {
        return tryEntityAttr;
    }
    public void setTryEntityAttr(String tryEntityAttr) {
        this.tryEntityAttr = tryEntityAttr;
    }

    public String getDefault() {
        return defaultStr;
    }
    public void setDefault(String defaultStr) {
        this.defaultStr = defaultStr;
    }

    public int doStartTag() throws JspTagException {
        String inputValue = null;
        String paramValue = null;
        boolean tryEntity = true;

        Boolean tempBool = null;
        if (tryEntityAttr != null) 
            tempBool = (Boolean) pageContext.getAttribute(tryEntityAttr);
        if (tempBool != null)
            tryEntity = tempBool.booleanValue();
        if (tryEntity) {
            Object entTemp = pageContext.getAttribute(entityAttr);
            if (entTemp != null) {
                if (entTemp instanceof GenericValue) {
                    GenericValue entity = (GenericValue) entTemp;
                    Object fieldVal = entity.get(field);
                    if (fieldVal != null)
                        inputValue = fieldVal.toString();
                } else if (entTemp instanceof Map) {
                    Map map = (Map) entTemp;
                    Object fieldVal = map.get(field);
                    if (fieldVal != null)
                        inputValue = fieldVal.toString();
                }
            }
        }
        //if nothing found in entity, or if not checked, try a parameter
        if (inputValue == null) {
            inputValue = pageContext.getRequest().getParameter(param);
        }
        if (inputValue == null || inputValue.length() == 0)
            inputValue = defaultStr;
        
        //reset optionals
        defaultStr = "";
        tryEntityAttr = null;

        try {
            pageContext.getOut().print(inputValue);
        } catch (IOException e) {
            throw new JspTagException(e.getMessage());
        }

        return (SKIP_BODY);
    }
}

