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
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.DispatchContext;
import org.w3c.dom.Element;

/**
 * EntityEcaRule
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      2.1
 */
public class EntityEcaRule {

    public static final String module = EntityEcaRule.class.getName();
    
    protected String entityName = null;
    protected String operationName = null;
    protected String eventName = null;
    protected boolean runOnError = false;
    protected List conditions = new LinkedList();
    protected List actions = new LinkedList();

    protected EntityEcaRule() {}

    public EntityEcaRule(Element eca) {
        this.entityName = eca.getAttribute("entity");
        this.operationName = eca.getAttribute("operation");
        this.eventName = eca.getAttribute("event");
        this.runOnError = "true".equals(eca.getAttribute("run-on-error"));

        List condList = UtilXml.childElementList(eca, "condition");
        Iterator ci = condList.iterator();
        while (ci.hasNext()) {
            conditions.add(new EntityEcaCondition((Element) ci.next(), true));
        }

        List condFList = UtilXml.childElementList(eca, "condition-field");
        Iterator cfi = condFList.iterator();
        while (cfi.hasNext()) {
            conditions.add(new EntityEcaCondition((Element) cfi.next(), false));
        }

        if (Debug.verboseOn()) Debug.logVerbose("Conditions: " + conditions, module);

        List actList = UtilXml.childElementList(eca, "action");
        Iterator ai = actList.iterator();
        while (ai.hasNext()) {
            actions.add(new EntityEcaAction((Element) ai.next()));
        }

        if (Debug.verboseOn()) Debug.logVerbose("Actions: " + actions, module);
    }

    public void eval(String currentOperation, DispatchContext dctx, GenericEntity value, boolean isError, Set actionsRun) throws GenericEntityException {
        //Debug.logInfo("eval eeca rule: operation=" + currentOperation + ", in event=" + this.eventName + ", on entity=" + this.entityName + ", for value=" + value, module);
        
        if (isError && !this.runOnError) {
            return;
        }
        
        if (!"any".equals(this.operationName) && this.operationName.indexOf(currentOperation) == -1) {
            return;
        }

        boolean allCondTrue = true;
        Iterator c = conditions.iterator();
        while (c.hasNext()) {
            EntityEcaCondition ec = (EntityEcaCondition) c.next();
            if (!ec.eval(dctx, value)) {
                allCondTrue = false;
                break;
            }
        }

        if (allCondTrue) {
            Iterator a = actions.iterator();
            while (a.hasNext()) {
                EntityEcaAction ea = (EntityEcaAction) a.next();
                // in order to enable OR logic without multiple calls to the given service, 
                //only execute a given service name once per service call phase 
                if (!actionsRun.contains(ea.serviceName)) {
                    ea.runAction(dctx, value);
                    actionsRun.add(ea.serviceName);
                }
            }
        }
    }
}
