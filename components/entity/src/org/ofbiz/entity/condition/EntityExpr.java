/*
 * $Id: EntityExpr.java,v 1.4 2004/04/23 01:42:16 doogie Exp $
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

import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericModelException;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;

/**
 * Encapsulates simple expressions used for specifying queries
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.4 $
 * @since      2.0
 */
public class EntityExpr extends EntityCondition {

    private Object lhs;
    private boolean leftUpper = false;
    private EntityOperator operator;
    private Object rhs;
    private boolean rightUpper = false;

    protected EntityExpr() {}

    public EntityExpr(Object lhs, EntityComparisonOperator operator, Object rhs) {
        if (lhs == null) {
            throw new IllegalArgumentException("The field name cannot be null");
        }
        if (operator == null) {
            throw new IllegalArgumentException("The operator argument cannot be null");
        }

        this.lhs = lhs;
        this.operator = operator;
        this.rhs = rhs;
    }

    public EntityExpr(String lhs, boolean leftUpper, EntityComparisonOperator operator, Object rhs, boolean rightUpper) {
        this(lhs, operator, rhs);
        this.leftUpper = leftUpper;
        this.rightUpper = rightUpper;
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

    public void setLUpper(boolean upper) {
        leftUpper = upper;
    }

    public boolean isLUpper() {
        return leftUpper;
    }

    public boolean isRUpper() {
        return rightUpper;
    }

    public void setRUpper(boolean upper) {
        rightUpper = upper;
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
        StringBuffer whereStringBuffer = new StringBuffer();

        if (lhs instanceof String) {
            ModelField field = getField(modelEntity, (String) this.getLhs());
            String colName = getColName(field, (String) this.getLhs());
            if (colName != null) {
                if (this.getRhs() == null) {
                    whereStringBuffer.append(colName);
                    if (EntityOperator.NOT_EQUAL.equals(this.getOperator())) {
                        whereStringBuffer.append(" IS NOT NULL ");
                    } else {
                        whereStringBuffer.append(" IS NULL ");
                    }
                } else {
                    if (this.isLUpper()) {
                        whereStringBuffer.append("UPPER(" + colName + ")");
                    } else {
                        whereStringBuffer.append(colName);
                    }
                    whereStringBuffer.append(' ');
                    whereStringBuffer.append(this.getOperator().toString());
                    whereStringBuffer.append(' ');

                    // treat the IN operator as a special case, especially with a Collection rhs
                    if (EntityOperator.IN.equals(this.getOperator()) || EntityOperator.NOT_IN.equals(this.getOperator())) {
                        whereStringBuffer.append('(');

                        if (rhs instanceof Collection) {
                            Iterator rhsIter = ((Collection) rhs).iterator();

                            while (rhsIter.hasNext()) {
                                Object inObj = rhsIter.next();

                                addValue(whereStringBuffer, field, inObj, entityConditionParams);

                                if (rhsIter.hasNext()) {
                                    whereStringBuffer.append(", ");
                                }

                            }
                        } else {
                            addValue(whereStringBuffer, field, rhs, entityConditionParams);
                        }

                        whereStringBuffer.append(')');
                    } else {
                        addValue(whereStringBuffer, field, rhs, entityConditionParams);
                    }
                }
            } else {
                throw new IllegalArgumentException("ModelField with field name " + (String) this.getLhs() + " not found");
            }
        } else if (lhs instanceof EntityCondition) {
            whereStringBuffer.append('(');
            whereStringBuffer.append(((EntityCondition) lhs).makeWhereString(modelEntity, entityConditionParams));
            whereStringBuffer.append(") ");
            whereStringBuffer.append(this.getOperator().toString());
            whereStringBuffer.append(" (");
            if (rhs instanceof EntityCondition) {
                whereStringBuffer.append(((EntityCondition) rhs).makeWhereString(modelEntity, entityConditionParams));
            } else {
                addValue(whereStringBuffer, null, rhs, entityConditionParams);
            }
            whereStringBuffer.append(')');
        }
        return whereStringBuffer.toString();
    }

    public boolean entityMatches(GenericEntity entity) {
        ModelEntity modelEntity = entity.getModelEntity();
        if (this.operator instanceof EntityComparisonOperator) {
            EntityComparisonOperator comparer = (EntityComparisonOperator) this.operator;
            Object leftValue;
            if (lhs instanceof String) {
                leftValue = entity.get( (String) lhs );
                if (this.isLUpper() && leftValue instanceof String ) {
                    leftValue = ( (String) leftValue ).toUpperCase();
                }
            } else if (lhs instanceof EntityFunction) {
                EntityFunction func = (EntityFunction) lhs;
                leftValue = func.eval(entity);
            } else {
                leftValue = lhs;
            }
            Object rightValue;
            if (rhs instanceof EntityFunction) {
                EntityFunction func = (EntityFunction) rhs;
                rightValue = func.eval(entity);
            } else {
                rightValue = rhs;
            }
            if (this.isRUpper() && rightValue instanceof String ) {
                rightValue = ( (String) rightValue ).toUpperCase();
            }

            return comparer.compare(leftValue, rightValue);
        } else if (lhs instanceof EntityCondition && this.operator instanceof EntityJoinOperator) {
            EntityJoinOperator joiner = (EntityJoinOperator) this.operator;
            EntityOperator.MatchResult result = joiner.join((EntityCondition) lhs, entity);
            if (!result.shortCircuit) {
                result = joiner.join((EntityCondition) rhs, entity);
            }
            return result.matches;
        } else {
            return false;
        }
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
               equals(rhs, other.rhs) &&
               leftUpper == other.leftUpper &&
               rightUpper == other.rightUpper;
    }

    public int hashCode() {
        return hashCode(lhs) ^
               hashCode(operator) ^
               hashCode(rhs) ^
               (leftUpper ? 1 : 2) ^
               (rightUpper ? 4 : 8);
    }
}
