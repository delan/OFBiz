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
 * EventConditionAction
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @created    Jul 27, 2002
 * @version    1.0
 */
public class EventConditionAction {

    String serviceName;
    String eventName;
    boolean runOnError;
    List conditions = new LinkedList();
    List actions = new LinkedList();

    protected EventConditionAction() {}

    public EventConditionAction(Element eca) {
        this.serviceName = eca.getAttribute("service");
        this.eventName = eca.getAttribute("event");
        this.runOnError = "true".equals(eca.getAttribute("run-on-error"));

        List condList = UtilXml.childElementList(eca, "condition");
        Iterator ci = condList.iterator();

        while (ci.hasNext()) {
            conditions.add(new EventCondition((Element) ci.next(), true));
        }

        List condFList = UtilXml.childElementList(eca, "condition-field");
        Iterator cfi = condFList.iterator();

        while (cfi.hasNext()) {
            conditions.add(new EventCondition((Element) cfi.next(), false));
        }

        if (Debug.verboseOn()) Debug.logVerbose("Conditions: " + conditions);

        List actList = UtilXml.childElementList(eca, "action");
        Iterator ai = actList.iterator();

        while (ai.hasNext()) {
            actions.add(new EventAction((Element) ai.next()));
        }

        if (Debug.verboseOn()) Debug.logVerbose("Actions: " + actions);
    }

    public void eval(String serviceName, DispatchContext dctx, Map context, Map result, boolean isError) throws GenericServiceException {
        if (isError && !this.runOnError) {
            return;
        }

        boolean allCondTrue = true;
        Iterator c = conditions.iterator();

        while (c.hasNext()) {
            EventCondition ec = (EventCondition) c.next();

            if (!ec.eval(serviceName, dctx, context)) {
                allCondTrue = false;
                break;
            }
        }

        if (allCondTrue) {
            Iterator a = actions.iterator();

            while (a.hasNext()) {
                EventAction ea = (EventAction) a.next();

                ea.runAction(serviceName, dctx, context, result);
            }
        }
    }
}
