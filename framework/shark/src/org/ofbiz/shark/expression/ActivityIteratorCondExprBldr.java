/*
 * $Id$
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
package org.ofbiz.shark.expression;

import java.sql.Timestamp;

import org.enhydra.shark.api.RootException;
import org.enhydra.shark.api.common.ActivityIteratorExpressionBuilder;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelKeyMap;

/**
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      3.3
 */
public class ActivityIteratorCondExprBldr extends BaseEntityCondExprBldr implements ActivityIteratorExpressionBuilder {

    protected boolean addedProcess = false;

    public ActivityIteratorCondExprBldr() {
        this.addEntity("WFAC", "WfActivity");
        this.addAllFields("WFAC");
    }

    public void addProcess(String field, String fieldAlias) {
        if (!addedProcess) {
            this.addEntity("WFPR", "WfProcess");
            this.addLink("WFAC", "WFAC", false, ModelKeyMap.makeKeyMapList("processId"));
        }
        this.addField("WFPR", field, fieldAlias);
    }

    public ActivityIteratorExpressionBuilder and() {
        this.setOr(false);
        return this;
    }

    public ActivityIteratorExpressionBuilder or() {
        this.setOr(true);
        return this;
    }

    public ActivityIteratorExpressionBuilder not() {
        this.setNot(true);
        return this;
    }

    // WfProcess conditions

    public ActivityIteratorExpressionBuilder addPackageIdEquals(String s) {
        this.addProcess("packageId", "packageId");
        this.addCondition(new EntityExpr("packageId", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, s));
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessDefIdEquals(String s) {
        this.addProcess("definitionId", "procDefId");
        this.addCondition(new EntityExpr("procDefId", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, s));
        return this;
    }

    public ActivityIteratorExpressionBuilder addMgrNameEquals(String s) {
        this.addProcess("mgrName", "mgrName");
        this.addCondition(new EntityExpr("mgrName", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, s));
        return this;
    }

    public ActivityIteratorExpressionBuilder addVersionEquals(String s) {
        this.addProcess("packageVer", "packageVer");
        this.addCondition(new EntityExpr("packageVer", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, s));
        return this;
    }

    public ActivityIteratorExpressionBuilder addIsEnabled() {
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessStateEquals(String s) {
        this.addProcess("packageVer", "packageVer");
        this.addCondition(new EntityExpr("packageVer", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, s));
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessStateStartsWith(String s) {
        this.addProcess("currentState", "procState");
        this.addCondition(new EntityExpr("procState", isNotSet ? EntityOperator.NOT_LIKE : EntityOperator.LIKE, s + "%"));
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessIdEquals(String s) {
        this.addCondition(new EntityExpr("processId", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, s));
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessNameEquals(String s) {
        this.addProcess("processName", "processName");
        this.addCondition(new EntityExpr("processName", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, s));
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessPriorityEquals(int i) {
        this.addProcess("priority", "procPriority");
        this.addCondition(new EntityExpr("procPriority", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, new Long(i)));
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessDescriptionEquals(String s) {
        this.addProcess("description", "procDesc");
        this.addCondition(new EntityExpr("procDesc", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, s));
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessDescriptionContains(String s) {
        this.addProcess("description", "procDesc");
        this.addCondition(new EntityExpr("procDesc", isNotSet ? EntityOperator.NOT_LIKE : EntityOperator.LIKE, "%" + s + "%"));
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessRequesterIdEquals(String s) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessStartTimeEquals(long l) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessStartTimeBefore(long l) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessStartTimeAfter(long l) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessLastStateTimeEquals(long l) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessLastStateTimeBefore(long l) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessLastStateTimeAfter(long l) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessVariableEquals(String s, Object o) throws RootException {
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessVariableEquals(String s, String s1) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessVariableEquals(String s, long l) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessVariableGreaterThan(String s, long l) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessVariableLessThan(String s, long l) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessVariableEquals(String s, double v) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessVariableGreaterThan(String s, double v) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addProcessVariableLessThan(String s, double v) {
        return this;
    }

    // WfActivity Conditions

    public ActivityIteratorExpressionBuilder addStateEquals(String s) {
        this.addCondition(new EntityExpr("currentState", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, s));
        return this;
    }

    public ActivityIteratorExpressionBuilder addStateStartsWith(String s) {
        this.addCondition(new EntityExpr("currentState", isNotSet ? EntityOperator.NOT_LIKE : EntityOperator.LIKE, s + "%"));
        return this;
    }

    public ActivityIteratorExpressionBuilder addIdEquals(String s) {
        this.addCondition(new EntityExpr("activityId", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, s));
        return this;
    }

    public ActivityIteratorExpressionBuilder addNameEquals(String s) {
        this.addCondition(new EntityExpr("activityName", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, s));
        return this;
    }

    public ActivityIteratorExpressionBuilder addPriorityEquals(int i) {
        this.addCondition(new EntityExpr("priority", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, new Long(i)));
        return this;
    }

    public ActivityIteratorExpressionBuilder addDescriptionEquals(String s) {
        this.addCondition(new EntityExpr("description", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, s));
        return this;
    }

    public ActivityIteratorExpressionBuilder addDescriptionContains(String s) {
        this.addCondition(new EntityExpr("description", isNotSet ? EntityOperator.NOT_LIKE : EntityOperator.LIKE, "%" + s + "%"));
        return this;
    }

    public ActivityIteratorExpressionBuilder addActivatedTimeEquals(long l) {
        this.addCondition(new EntityExpr("activatedTime", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, new Timestamp(l)));
        return this;
    }

    public ActivityIteratorExpressionBuilder addActivatedTimeBefore(long l) {
        this.addCondition(new EntityExpr("activatedTime", isNotSet ? EntityOperator.LESS_THAN : EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(l)));
        return this;
    }

    public ActivityIteratorExpressionBuilder addActivatedTimeAfter(long l) {
        this.addCondition(new EntityExpr("activatedTime", isNotSet ? EntityOperator.GREATER_THAN : EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(l)));
        return this;
    }

    public ActivityIteratorExpressionBuilder addLastStateTimeEquals(long l) {
        this.addCondition(new EntityExpr("lastStateTime", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, new Timestamp(l)));
        return this;
    }

    public ActivityIteratorExpressionBuilder addLastStateTimeBefore(long l) {
        this.addCondition(new EntityExpr("lastStateTime", isNotSet ? EntityOperator.LESS_THAN : EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(l)));
        return this;
    }

    public ActivityIteratorExpressionBuilder addLastStateTimeAfter(long l) {
        this.addCondition(new EntityExpr("lastStateTime", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, new Timestamp(l)));
        return this;
    }

    public ActivityIteratorExpressionBuilder addAcceptedTimeEquals(long l) {
        this.addCondition(new EntityExpr("acceptedTime", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, new Timestamp(l)));
        return this;
    }

    public ActivityIteratorExpressionBuilder addAcceptedTimeBefore(long l) {
        this.addCondition(new EntityExpr("acceptedTime", isNotSet ? EntityOperator.LESS_THAN : EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(l)));
        return this;
    }

    public ActivityIteratorExpressionBuilder addAcceptedTimeAfter(long l) {
        this.addCondition(new EntityExpr("acceptedTime", isNotSet ? EntityOperator.GREATER_THAN : EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(l)));
        return this;
    }

    public ActivityIteratorExpressionBuilder addVariableEquals(String s, Object o) throws RootException {
        if (o != null) {
            if (o instanceof String) {
                return addVariableEquals(s, (String) o);
            } else if (o instanceof Number) {
                if (o instanceof Double) {
                    return addVariableEquals(s, ((Double) o).doubleValue());
                } else {
                    return addVariableEquals(s, ((Long) o).longValue());
                }
            } else {
                throw new RootException("Unable to compare database blobs!");
            }
        }
        return this;
    }

    public ActivityIteratorExpressionBuilder addVariableEquals(String s, String s1) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addVariableEquals(String s, long l) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addVariableGreaterThan(String s, long l) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addVariableLessThan(String s, long l) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addVariableEquals(String s, double v) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addVariableGreaterThan(String s, double v) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addVariableLessThan(String s, double v) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addActivitySetDefId(String s) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addDefinitionId(String s) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addIsAccepted() {
        return this;
    }

    public ActivityIteratorExpressionBuilder addResourceUsername(String s) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addExpression(String s) {
        return this;
    }

    public ActivityIteratorExpressionBuilder addExpression(ActivityIteratorExpressionBuilder eieb) {
        return this;
    }
}
