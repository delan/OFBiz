/*
 * $Id: EntityConditionList.java,v 1.8 2004/07/14 04:15:49 doogie Exp $
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
 * @version    $Revision: 1.8 $
 * @since      2.0
 */
public class EntityConditionList extends EntityConditionListBase {

    protected EntityConditionList() {
        super();
    }

    public EntityConditionList(List conditionList, EntityJoinOperator operator) {
        super(conditionList, operator);
    }

    public int getConditionListSize() {
        return super.getConditionListSize();
    }
    
    public Iterator getConditionIterator() {
        return super.getConditionIterator();
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
