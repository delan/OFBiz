/*
 * $Id: EntityConditionList.java,v 1.6 2004/07/07 00:15:24 doogie Exp $
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericModelException;
import org.ofbiz.entity.model.ModelEntity;

/**
 * Encapsulates a list of EntityConditions to be used as a single EntityCondition combined as specified
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.6 $
 * @since      2.0
 */
public class EntityConditionList extends EntityCondition {

    protected List conditionList;
    protected EntityJoinOperator operator;

    protected EntityConditionList() {}

    public EntityConditionList(List conditionList, EntityJoinOperator operator) {
        this.conditionList = conditionList;
        this.operator = operator;
    }

    public EntityOperator getOperator() {
        return this.operator;
    }

    public EntityCondition getCondition(int index) {
        return (EntityCondition) this.conditionList.get(index);
    }
    
    public int getConditionListSize() {
        return this.conditionList.size();
    }
    
    public Iterator getConditionIterator() {
        return this.conditionList.iterator();
    }
    
    public String makeWhereString(ModelEntity modelEntity, List entityConditionParams) {
        // if (Debug.verboseOn()) Debug.logVerbose("makeWhereString for entity " + modelEntity.getEntityName(), module);
        StringBuffer sql = new StringBuffer();
        operator.addSqlValue(sql, modelEntity, entityConditionParams, conditionList);
        return sql.toString();
    }

    public void checkCondition(ModelEntity modelEntity) throws GenericModelException {
        // if (Debug.verboseOn()) Debug.logVerbose("checkCondition for entity " + modelEntity.getEntityName(), module);
        operator.validateSql(modelEntity, conditionList);
    }

    public boolean mapMatches(GenericDelegator delegator, Map map) {
        return operator.mapMatches(delegator, map, conditionList);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof EntityConditionList)) return false;
        EntityConditionList other = (EntityConditionList) obj;
        return conditionList.equals(other.conditionList) && operator.equals(other.operator);
    }

    public int hashCode() {
        return conditionList.hashCode() & operator.hashCode();
    }
}
