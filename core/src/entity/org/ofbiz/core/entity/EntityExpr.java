/*
 * $Id$
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
package org.ofbiz.core.entity;

import java.util.*;

import org.ofbiz.core.entity.model.*;

/**
 * Encapsulates simple expressions used for specifying queries
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class EntityExpr extends EntityCondition {

    private Object lhs;
    private boolean leftUpper = false;
    private EntityOperator operator;
    private Object rhs;
    private boolean rightUpper = false;

    protected EntityExpr() {}

    public EntityExpr(String lhs, EntityOperator operator, Object rhs) {
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

    public EntityExpr(String lhs, boolean leftUpper, EntityOperator operator, Object rhs, boolean rightUpper) {
        this(lhs, operator, rhs);
        this.leftUpper = leftUpper;
        this.rightUpper = rightUpper;
    }

    public EntityExpr(EntityCondition lhs, EntityOperator operator, EntityCondition rhs) {
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
        // if (Debug.verboseOn()) Debug.logVerbose("makeWhereString for entity " + modelEntity.getEntityName());
        StringBuffer whereStringBuffer = new StringBuffer();

        if (lhs instanceof String) {
            ModelField field = (ModelField) modelEntity.getField((String) this.getLhs());

            if (field != null) {
                if (this.getRhs() == null) {
                    whereStringBuffer.append(field.getColName());
                    if (EntityOperator.NOT_EQUAL.equals(this.getOperator())) {
                        whereStringBuffer.append(" IS NOT NULL ");
                    } else {
                        whereStringBuffer.append(" IS NULL ");
                    }
                } else {
                    if (this.isLUpper()) {
                        whereStringBuffer.append("UPPER(" + field.getColName() + ")");
                    } else {
                        whereStringBuffer.append(field.getColName());
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

                                whereStringBuffer.append('?');
                                if (rhsIter.hasNext()) {
                                    whereStringBuffer.append(", ");
                                }

                                if (this.isRUpper()) {
                                    if (inObj instanceof String) {
                                        inObj = ((String) inObj).toUpperCase();
                                    }
                                }
                                entityConditionParams.add(new EntityConditionParam(field, inObj));
                            }
                        } else {
                            whereStringBuffer.append(" ? ");

                            if (this.isRUpper()) {
                                if (rhs instanceof String) {
                                    rhs = ((String) rhs).toUpperCase();
                                }
                            }
                            entityConditionParams.add(new EntityConditionParam(field, rhs));
                        }

                        whereStringBuffer.append(") ");
                    } else {
                        whereStringBuffer.append(" ? ");

                        if (this.isRUpper()) {
                            if (rhs instanceof String) {
                                rhs = ((String) rhs).toUpperCase();
                            }
                        }
                        entityConditionParams.add(new EntityConditionParam(field, rhs));
                    }
                }
            } else {
                throw new IllegalArgumentException("ModelField with field name " + (String) this.getLhs() + " not found");
            }
        } else if (lhs instanceof EntityCondition) {
            // then rhs MUST also be an EntityCondition
            whereStringBuffer.append('(');
            whereStringBuffer.append(((EntityCondition) lhs).makeWhereString(modelEntity, entityConditionParams));
            whereStringBuffer.append(") ");
            whereStringBuffer.append(this.getOperator().toString());
            whereStringBuffer.append(" (");
            whereStringBuffer.append(((EntityCondition) rhs).makeWhereString(modelEntity, entityConditionParams));
            whereStringBuffer.append(')');
        }
        return whereStringBuffer.toString();
    }

    public void checkCondition(ModelEntity modelEntity) throws GenericModelException {
        // if (Debug.verboseOn()) Debug.logVerbose("checkCondition for entity " + modelEntity.getEntityName());
        if (lhs instanceof String) {
            if (modelEntity.getField((String) lhs) == null) {
                throw new GenericModelException("Field with name " + lhs + " not found in the " + modelEntity.getEntityName() + " Entity");
            }
        } else if (lhs instanceof EntityCondition) {
            ((EntityCondition) lhs).checkCondition(modelEntity);
            ((EntityCondition) rhs).checkCondition(modelEntity);
        }
    }

    public String toString() {
        return "[Expr::" + lhs + "::" + operator + "::" + rhs + "]";
    }
}
