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
    protected String lhsType, rhsType;
    protected String operator;
    protected String compareType;

    protected List actions = new LinkedList();

    protected EventCondition() {
    }

    public EventCondition(Element condition) {
        this.lhsValueName = condition.getAttribute("lhs-value");
        this.rhsValueName = condition.getAttribute("rhs-value");
        this.lhsMapName = condition.getAttribute("lhs-map");
        this.rhsMapName = condition.getAttribute("rhs-map");
        this.lhsType = condition.getAttribute("lhs-type");
        this.rhsType = condition.getAttribute("rhs-type");
        this.operator = condition.getAttribute("operator");
        this.compareType = condition.getAttribute("compare-type");

        List actionList = UtilXml.childElementList(condition, "action");
        if (actionList != null) {
            Iterator i = actionList.iterator();
            while (i.hasNext()) {
                Element action = (Element) i.next();
                actions.add(new EventAction(action));
            }
        }
    }

    public void eval(DispatchContext dctx, Map context, Map result) throws GenericServiceException {
        Object lhsValue, rhsValue;
        if (lhsType.equals("constant")) {
            lhsValue = (Object) lhsValueName;
        } else if (lhsMapName != null) {
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
            rhsValue = (Object) rhsValueName;
        } else if (rhsMapName != null) {
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

        // evaluate the condition & invoke the action(s)
        List messages = new LinkedList();
        if (ObjectType.doRealCompare(lhsValue, rhsValue, operator, compareType, null, messages, null, dctx.getClassLoader()).booleanValue()) {
            Iterator a = actions.iterator();
            while (a.hasNext()) {
                EventAction action = (EventAction) a.next();
                action.runAction(dctx, context, result);
            }
        }

        // if any messages were returned send them out
        if (messages.size() > 0) {
            Iterator m = messages.iterator();
            while (m.hasNext())
                Debug.logWarning((String) m.next());
        }
    }

}
