/*
 * $Id: EntityExpr.java,v 1.9 2004/07/07 00:15:24 doogie Exp $
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
package org.ofbiz.entity.condition;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericModelException;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;

/**
 * Encapsulates simple expressions used for specifying queries
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.9 $
 * @since      2.0
 */
public class EntityExpr extends EntityCondition {

    private Object lhs;
    private EntityOperator operator;
    private Object rhs;

    protected EntityExpr() {}

    public EntityExpr(Object lhs, EntityComparisonOperator operator, Object rhs) {
        if (lhs == null) {
            throw new IllegalArgumentException("The field value cannot be null");
        }
        if (operator == null) {
            throw new IllegalArgumentException("The operator argument cannot be null");
        }

        if (rhs == null) {
            if (!EntityOperator.NOT_EQUAL.equals(operator) && !EntityOperator.EQUALS.equals(operator)) {
                throw new IllegalArgumentException("Operator must be EQUALS or NOT_EQUAL when right/rhs argument is NULL ");
            }
        }

        if (EntityOperator.BETWEEN.equals(operator)) {
            if (!(rhs instanceof Collection) || (((Collection) rhs).size() != 2)) {
                throw new IllegalArgumentException("BETWEEN Operator requires a Collection with 2 elements for the right/rhs argument");
            }
        }
        
        this.lhs = lhs;
        this.operator = operator;
        this.rhs = rhs;
    }

    /** @deprecated */
    public EntityExpr(String lhs, boolean leftUpper, EntityComparisonOperator operator, Object rhs, boolean rightUpper) {
        if (lhs == null) {
            throw new IllegalArgumentException("The field value cannot be null");
        }
        if (operator == null) {
            throw new IllegalArgumentException("The operator argument cannot be null");
        }
        this.lhs = new EntityFieldValue(lhs);
        if (leftUpper) this.lhs = new EntityFunction.UPPER(this.lhs);
        this.operator = operator;
        if (rhs instanceof EntityConditionValue) {
            if (rightUpper) rhs = new EntityFunction.UPPER((EntityConditionValue) rhs);
            this.rhs = rhs;
        } else {
            if (rightUpper) rhs = new EntityFunction.UPPER(rhs);
            this.rhs = rhs;
        }
    }

    public EntityExpr(EntityCondition lhs, EntityJoinOperator operator, EntityCondition rhs) {
        if (lhs == null) {
            throw new IllegalArgumentException("The left EntityCondition argument cannot be null");
        }
        if (rhs == null) {
            throw new IllegalArgumentException("The right EntityCondition argument cannot be null");
        }
        if (operator == null) {
            throw new IllegalArgumentException("The operator argument cannot be null");
        }

        this.lhs = lhs;
        this.operator = operator;
        this.rhs = rhs;
    }

    /** @deprecated */
    public void setLUpper(boolean upper) {
    }

    /** @deprecated */
    public boolean isLUpper() {
        return lhs instanceof EntityFunction.UPPER;
    }

    /** @deprecated */
    public boolean isRUpper() {
        return rhs instanceof EntityFunction.UPPER;
    }

    /** @deprecated */
    public void setRUpper(boolean upper) {
    }

    public Object getLhs() {
        return lhs;
    }

    public EntityOperator getOperator() {
        return operator;
    }

    public Object getRhs() {
        return rhs;
    }

    public String makeWhereString(ModelEntity modelEntity, List entityConditionParams) {
        // if (Debug.verboseOn()) Debug.logVerbose("makeWhereString for entity " + modelEntity.getEntityName(), module);
        StringBuffer sql = new StringBuffer();
        operator.addSqlValue(sql, modelEntity, entityConditionParams, lhs, rhs);
        return sql.toString();
    }

    public boolean mapMatches(GenericDelegator delegator, Map map) {
        return operator.mapMatches(delegator, map, lhs, rhs);
    }

    public void checkCondition(ModelEntity modelEntity) throws GenericModelException {
        // if (Debug.verboseOn()) Debug.logVerbose("checkCondition for entity " + modelEntity.getEntityName(), module);
        if (lhs instanceof String) {
            if (modelEntity.getField((String) lhs) == null) {
                throw new GenericModelException("Field with name " + lhs + " not found in the " + modelEntity.getEntityName() + " Entity");
            }
        } else if (lhs instanceof EntityCondition) {
            ((EntityCondition) lhs).checkCondition(modelEntity);
            ((EntityCondition) rhs).checkCondition(modelEntity);
        }
    }

	protected void addValue(StringBuffer buffer, ModelField field, Object value, List params) {
		if (this.isRUpper()) {
			if (value instanceof String) {
				value = ((String) value).toUpperCase();
			}
		}
		super.addValue(buffer, field, value, params);
	}

    public boolean equals(Object obj) {
        if (!(obj instanceof EntityExpr)) return false;
        EntityExpr other = (EntityExpr) obj;
        return equals(lhs, other.lhs) &&
               equals(operator, other.operator) &&
               equals(rhs, other.rhs);
    }

    public int hashCode() {
        return hashCode(lhs) ^
               hashCode(operator) ^
               hashCode(rhs);
    }
}
