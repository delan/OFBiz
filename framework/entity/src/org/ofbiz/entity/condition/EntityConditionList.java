/*
 * Copyright 2001-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.entity.condition;

import java.util.Iterator;
import java.util.List;

/**
 * Encapsulates a list of EntityConditions to be used as a single EntityCondition combined as specified
 *
 */
public class EntityConditionList extends EntityConditionListBase {
    public static final String module = EntityConditionList.class.getName();

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
    
    public void accept(EntityConditionVisitor visitor) {
        visitor.acceptEntityConditionList(this);
    }
}
