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

package org.ofbiz.core.service.eca;

import java.util.*;
import java.net.*;
import java.io.*;
import org.w3c.dom.*;

import org.ofbiz.core.config.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * EventCondition
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @created    Jul 27, 2002
 * @version    1.0
 */
public class EventCondition {

    protected String lhsValueName, rhsValueName;
    protected String lhsMapName, rhsMapName;
    protected String rhsType;
    protected String operator;
    protected String compareType;
    protected String format;

    protected EventCondition() {
    }

    public EventCondition(Element condition, String rhsType) {
        this.lhsValueName = condition.getAttribute("field-name");
        this.lhsMapName = condition.getAttribute("map-name");

        this.rhsType = rhsType;
        if (rhsType.equals("constant")) {
            this.rhsValueName = condition.getAttribute("value");
            this.rhsMapName = null;
        } else {
            this.rhsValueName = condition.getAttribute("to-field-name");
            this.rhsMapName = condition.getAttribute("to-map-name");
        }

        this.operator = condition.getAttribute("operator");
        this.compareType = condition.getAttribute("type");
        this.format = condition.getAttribute("format");

        if (lhsValueName == null)
            lhsValueName = "";
        if (rhsValueName == null)
            rhsValueName = "";
    }

    public boolean eval(String serviceName, DispatchContext dctx, Map context) throws GenericServiceException {
        if (serviceName == null || dctx == null || context == null || dctx.getClassLoader() == null)
            throw new GenericServiceException("Cannot have null Service, Context or DispatchContext!");
        Object lhsValue, rhsValue;

        if (lhsMapName != null && lhsMapName.length() > 0) {
            try {
                if (context.containsKey(lhsMapName)) {
                    Map envMap = (Map) context.get(lhsMapName);
                    lhsValue = envMap.get(lhsValueName);
                } else {
                    throw new GenericServiceException("Map (" + lhsMapName + ") not found in context.");
                }
            } catch (ClassCastException e) {
                throw new GenericServiceException("Field (" + lhsMapName + ") is not a Map.", e);
            }
        } else {
            if (context.containsKey(lhsValueName))
                lhsValue = context.get(lhsValueName);
            else
                throw new GenericServiceException("Field (" + lhsValueName + ") is not found in context.");
        }

        if (rhsType.equals("constant")) {
            rhsValue = rhsValueName;
        } else if (rhsMapName != null && rhsMapName.length() > 0) {
            try {
                if (context.containsKey(rhsMapName)) {
                    Map envMap = (Map) context.get(rhsMapName);
                    rhsValue = envMap.get(rhsValueName);
                } else {
                    throw new GenericServiceException("Map (" + rhsMapName + ") not found in context.");
                }
            } catch (ClassCastException e) {
                throw new GenericServiceException("Field (" + rhsMapName + ") is not a Map.", e);
            }
        } else {
            if (context.containsKey(rhsValueName))
                rhsValue = context.get(rhsValueName);
            else
                throw new GenericServiceException("Field (" + rhsValueName + ") is not found in context.");
        }

        if (Debug.verboseOn())
            Debug.logVerbose("Comparing : " + lhsValue + " <> " + rhsValue);

        // evaluate the condition & invoke the action(s)
        List messages = new LinkedList();
        Boolean cond = ObjectType.doRealCompare(lhsValue, rhsValue, operator, compareType, null, messages, null, dctx.getClassLoader());

        // if any messages were returned send them out
        if (messages.size() > 0) {
            Iterator m = messages.iterator();
            while (m.hasNext())
                Debug.logWarning((String) m.next());
        }
        return cond.booleanValue();
    }

}
