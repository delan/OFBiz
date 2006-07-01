/*
 * $Id: ServiceEcaRule.java 5462 2005-08-05 18:35:48Z jonesde $
 *
 * Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.service.eca;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Element;

/**
 * ServiceEcaRule
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      2.0
 */
public class ServiceEcaRule implements java.io.Serializable {

    public static final String module = ServiceEcaRule.class.getName();

    protected String serviceName = null;
    protected String eventName = null;
    protected boolean runOnFailure = false;
    protected boolean runOnError = false;
    protected List conditions = new LinkedList();
    protected List actions = new LinkedList();
    protected boolean enabled = true;

    protected ServiceEcaRule() {}

    public ServiceEcaRule(Element eca) {
        this.serviceName = eca.getAttribute("service");
        this.eventName = eca.getAttribute("event");
        this.runOnFailure = "true".equals(eca.getAttribute("run-on-failure"));
        this.runOnError = "true".equals(eca.getAttribute("run-on-error"));

        List condList = UtilXml.childElementList(eca, "condition");
        Iterator ci = condList.iterator();

        while (ci.hasNext()) {
            conditions.add(new ServiceEcaCondition((Element) ci.next(), true, false));
        }

        List condFList = UtilXml.childElementList(eca, "condition-field");
        Iterator cfi = condFList.iterator();

        while (cfi.hasNext()) {
            conditions.add(new ServiceEcaCondition((Element) cfi.next(), false, false));
        }

        List condSList = UtilXml.childElementList(eca, "condition-service");
        Iterator sfi = condSList.iterator();

        while (sfi.hasNext()) {
            conditions.add(new ServiceEcaCondition((Element) sfi.next(), false, true));
        }

        if (Debug.verboseOn()) Debug.logVerbose("Conditions: " + conditions, module);

        List actList = UtilXml.childElementList(eca, "action");
        Iterator ai = actList.iterator();

        while (ai.hasNext()) {
            Element actionElement = (Element) ai.next();
            actions.add(new ServiceEcaAction(actionElement, eventName));
        }

        if (Debug.verboseOn()) Debug.logVerbose("Actions: " + actions, module);
    }

    public void eval(String serviceName, DispatchContext dctx, Map context, Map result, boolean isError, boolean isFailure, Set actionsRun) throws GenericServiceException {
        if (!enabled) {
            Debug.logInfo("Service ECA [" + this.serviceName + "] on [" + this.eventName + "] is disabled; not running.", module);
            return;
        }
        if (isFailure && !this.runOnFailure) {
            return;
        }
        if (isError && !this.runOnError) {
            return;
        }

        boolean allCondTrue = true;
        Iterator c = conditions.iterator();

        while (c.hasNext()) {
            ServiceEcaCondition ec = (ServiceEcaCondition) c.next();
            if (!ec.eval(serviceName, dctx, context)) {
                if (Debug.infoOn()) Debug.logInfo("Got false for condition: " + ec, module);
                allCondTrue = false;
                break;
            } else {
                if (Debug.verboseOn()) Debug.logVerbose("Got true for condition: " + ec, module);
            }
        }

        if (allCondTrue) {
            Iterator a = actions.iterator();
            boolean allOkay = true;
            while (a.hasNext() && allOkay) {
                ServiceEcaAction ea = (ServiceEcaAction) a.next();
                // in order to enable OR logic without multiple calls to the given service,
                // only execute a given service name once per service call phase
                if (!actionsRun.contains(ea.serviceName)) {
                    if (Debug.infoOn()) Debug.logInfo("Running Service ECA Service: " + ea.serviceName + ", triggered by rule on Service: " + serviceName, module);
                    if (ea.runAction(serviceName, dctx, context, result)) {
                        actionsRun.add(ea.serviceName);
                    } else {
                        allOkay = false;
                    }
                }
            }
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
