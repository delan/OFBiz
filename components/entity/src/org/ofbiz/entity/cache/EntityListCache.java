/*
 * $Id: EntityListCache.java,v 1.2 2004/07/13 11:29:46 jonesde Exp $
 *
 * Copyright (c) 2001-2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.entity.cache;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

public class EntityListCache extends AbstractEntityConditionCache {

    public EntityListCache(String delegatorName) {
        super(delegatorName, "entity-list");
    }

    public List get(String entityName, EntityCondition condition) {
        return get(entityName, condition, null);
    }

    public List get(String entityName, EntityCondition condition, List orderBy) {
        Map conditionCache = getConditionCache(entityName, condition);
        if (conditionCache == null) return null;
        synchronized (conditionCache) {
            Object orderByKey = getOrderByKey(orderBy);
            List list = (List) conditionCache.get(orderByKey);
            if (list == null) {
                Iterator it = conditionCache.values().iterator();
                if (it.hasNext()) list = (List) it.next();
                if (list != null) {
                    list = EntityUtil.orderBy(list, orderBy);
                    conditionCache.put(orderByKey, list);
                }
            }
            return list;
        }
    }

    public void put(String entityName, EntityCondition condition, List entities) {
        put(entityName, condition, null, entities);
    }

    public List put(String entityName, EntityCondition condition, List orderBy, List entities) {
        return (List) super.put(entityName, getFrozenConditionKey(condition), getOrderByKey(orderBy), entities);
    }

    public List remove(String entityName, EntityCondition condition, List orderBy) {
        return (List) super.remove(entityName, condition, getOrderByKey(orderBy));
    }

    public static final Object getOrderByKey(List orderBy) {
        return orderBy != null ? (Object) orderBy : "{null}";
    }
}
