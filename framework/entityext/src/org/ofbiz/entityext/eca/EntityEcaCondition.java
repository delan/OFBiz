/*
 * $Id$
 *
 * Copyright (c) 2002-2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.entityext.eca;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.DispatchContext;
import org.w3c.dom.Element;

/**
 * EntityEcaCondition
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      2.1
 */
public class EntityEcaCondition implements java.io.Serializable {
    
    public static final String module = EntityEcaCondition.class.getName();

    protected String lhsValueName, rhsValueName;
    protected String operator;
    protected String compareType;
    protected String format;
    protected boolean constant = false;

    protected EntityEcaCondition() {}

    public EntityEcaCondition(Element condition, boolean constant) {
        this.lhsValueName = condition.getAttribute("field-name");

        this.constant = constant;
        if (constant) {
            this.rhsValueName = condition.getAttribute("value");
        } else {
            this.rhsValueName = condition.getAttribute("to-field-name");
        }

        this.operator = condition.getAttribute("operator");
        this.compareType = condition.getAttribute("type");
        this.format = condition.getAttribute("format");

        if (lhsValueName == null)
            lhsValueName = "";
        if (rhsValueName == null)
            rhsValueName = "";
    }

    public boolean eval(DispatchContext dctx, GenericEntity value) throws GenericEntityException {
        if (dctx == null || value == null || dctx.getClassLoader() == null) {
            throw new GenericEntityException("Cannot have null Value or DispatchContext!");
        }

        if (Debug.verboseOn()) Debug.logVerbose(this.toString(), module);

        Object lhsValue = value.get(lhsValueName);

        Object rhsValue;
        if (constant) {
            rhsValue = rhsValueName;
        } else {
            rhsValue = value.get(rhsValueName);
        }

        if (Debug.verboseOn()) Debug.logVerbose("Comparing : " + lhsValue + " " + operator + " " + rhsValue, module);

        // evaluate the condition & invoke the action(s)
        List messages = new LinkedList();
        Boolean cond = ObjectType.doRealCompare(lhsValue, rhsValue, operator, compareType, format, messages, null, dctx.getClassLoader());

        // if any messages were returned send them out
        if (messages.size() > 0) {
            Iterator m = messages.iterator();
            while (m.hasNext()) {
                Debug.logWarning((String) m.next(), module);
            }
        }
        if (cond != null) {
            return cond.booleanValue();
        } else {
            return false;
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[" + lhsValueName + "]");
        buf.append("[" + operator + "]");
        buf.append("[" + rhsValueName + "]");
        buf.append("[" + constant + "]");
        buf.append("[" + compareType + "]");
        buf.append("[" + format + "]");
        return buf.toString();
    }
}
