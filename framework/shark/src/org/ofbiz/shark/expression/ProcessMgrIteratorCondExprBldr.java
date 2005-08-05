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

import org.enhydra.shark.api.common.ProcessMgrIteratorExpressionBuilder;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;

/**
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.3
 */
public class ProcessMgrIteratorCondExprBldr extends BaseEntityCondExprBldr implements ProcessMgrIteratorExpressionBuilder {

    public ProcessMgrIteratorCondExprBldr() {
        this.addEntity("WFPM", "WfProcessMgr");
        this.addAllFields("WFPM");
    }

    public ProcessMgrIteratorExpressionBuilder and() {
        this.setOr(false);
        return this;
    }

    public ProcessMgrIteratorExpressionBuilder or() {
        this.setOr(true);
        return this;
    }

    public ProcessMgrIteratorExpressionBuilder not() {
        this.setNot(true);
        return this;
    }

    public ProcessMgrIteratorExpressionBuilder addPackageIdEquals(String s) {
        this.addCondition(new EntityExpr("packageId", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, s));
        return this;
    }

    public ProcessMgrIteratorExpressionBuilder addProcessDefIdEquals(String s) {
        this.addCondition(new EntityExpr("definitionId", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, s));
        return this;
    }

    public ProcessMgrIteratorExpressionBuilder addNameEquals(String s) {
        this.addCondition(new EntityExpr("mgrName", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, s));
        return this;
    }

    public ProcessMgrIteratorExpressionBuilder addVersionEquals(String s) {
        this.addCondition(new EntityExpr("packageVer", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, s));
        return this;
    }

    public ProcessMgrIteratorExpressionBuilder addIsEnabled() {
        this.addCondition(new EntityExpr("currentState", isNotSet ? EntityOperator.NOT_EQUAL : EntityOperator.EQUALS, new Long(0)));
        return this;
    }

    public ProcessMgrIteratorExpressionBuilder addExpression(String s) {
        ProcessMgrIteratorExpressionBuilder builder = (ProcessMgrIteratorExpressionBuilder) BaseEntityCondExprBldr.getBuilder(s);
        if (builder != null) {
            return this.addExpression(builder);
        } else {
            return this;
        }
    }

    public ProcessMgrIteratorExpressionBuilder addExpression(ProcessMgrIteratorExpressionBuilder builder) {
        if (!(builder instanceof BaseEntityCondExprBldr)) {
            throw new UnsupportedOperationException("Unsupported implementation");
        } else {
            this.addCondition(((BaseEntityCondExprBldr) builder).getCondition());
        }
        return this;
    }
}
