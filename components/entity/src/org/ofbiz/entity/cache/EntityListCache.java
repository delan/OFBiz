package org.ofbiz.entity.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.model.ModelEntity;
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
