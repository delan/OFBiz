/*
 * $Id$
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.cache.UtilCache;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.model.ModelEntity;

public abstract class AbstractEntityConditionCache extends AbstractCache {

    protected AbstractEntityConditionCache(String delegatorName, String id) {
        super(delegatorName, id);
    }

    protected Object get(String entityName, EntityCondition condition, Object key) {
        Map conditionCache = getConditionCache(entityName, condition);
        if (conditionCache == null) return null;
        synchronized (conditionCache) {
            return conditionCache.get(key);
        }
    }

    protected Object put(String entityName, EntityCondition condition, Object key, Object value) {
        Map conditionCache = getOrCreateConditionCache(entityName, condition);
        synchronized (conditionCache) {
            return conditionCache.put(key, value);
        }
    }

    public void remove(String entityName, EntityCondition condition) {
        UtilCache cache = getCache(entityName);
        if (cache == null) return;
        cache.remove(condition);
    }

    protected Object remove(String entityName, EntityCondition condition, Object key) {
        Map conditionCache = getConditionCache(entityName, condition);
        if (conditionCache == null) return null;
        synchronized (conditionCache) {
            return conditionCache.remove(key);
         }
    }

    public static final EntityCondition getConditionKey(EntityCondition condition) {
        return condition != null ? condition : null;
    }

    public static final EntityCondition getFrozenConditionKey(EntityCondition condition) {
        return condition != null ? condition.freeze() : null;
    }

    protected Map getConditionCache(String entityName, EntityCondition condition) {
        UtilCache cache = getCache(entityName);
        if (cache == null) return null;
        return (Map) cache.get(getConditionKey(condition));
    }

    protected Map getOrCreateConditionCache(String entityName, EntityCondition condition) {
        UtilCache cache = getOrCreateCache(entityName);
        Object conditionKey = getConditionKey(condition);
        Map conditionCache = (Map) cache.get(conditionKey);
        if (conditionCache == null) {
            conditionCache = new HashMap();
            cache.put(conditionKey, conditionCache);
        }
        return conditionCache;
    }

    public void storeHook(GenericEntity newEntity) {
        storeHook(null, newEntity);
    }

    protected static final boolean isNull(Map value) {
        return value == null || value == GenericEntity.NULL_ENTITY || value == GenericValue.NULL_VALUE;
    }

    protected ModelEntity validateStoreHookArgs(GenericEntity oldEntity, GenericEntity newEntity) {
        ModelEntity model;
        String entityName;
        if (!isNull(newEntity)) {
            model = newEntity.getModelEntity();
            entityName = model.getEntityName();
            if (oldEntity != null && !entityName.equals(oldEntity.getEntityName()))
                throw new IllegalArgumentException("internal error: storeHook called with 2 different entities(old=" + oldEntity.getEntityName() + ", new=" + entityName + ")");
        } else {
            if (oldEntity != null)
                model = oldEntity.getModelEntity();
            else
                throw new IllegalArgumentException("internal error: storeHook called with 2 null arguments");
        }
        return model;
    }

    // if oldValue == null, then this is a new entity
    // if newValue == null, then 
    public void storeHook(GenericEntity oldEntity, GenericEntity newEntity) {
        storeHook(false, oldEntity, newEntity);
    }

    // if oldValue == null, then this is a new entity
    // if newValue == null, then 
    public void storeHook(GenericPK oldPK, GenericEntity newEntity) {
        storeHook(true, oldPK, newEntity);
    }


    protected List convert(boolean isPK, String targetEntityName, GenericEntity entity) {
        if (entity == null || entity == GenericEntity.NULL_ENTITY || entity == GenericValue.NULL_VALUE) return null;
        if (isPK)
            return entity.getModelEntity().convertToViewValues(targetEntityName, (GenericPK) entity);
        else
            return entity.getModelEntity().convertToViewValues(targetEntityName, (GenericEntity) entity);
    }

    public synchronized void storeHook(boolean isPK, GenericEntity oldEntity, GenericEntity newEntity) {
        ModelEntity model = validateStoreHookArgs(oldEntity, newEntity);
        String entityName = model.getEntityName();
        storeHook(entityName, isPK, UtilMisc.toList(oldEntity), UtilMisc.toList(newEntity));
        Iterator it = model.getViewConvertorsIterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String targetEntityName = (String) entry.getKey();
            storeHook(targetEntityName, isPK, convert(isPK, targetEntityName, oldEntity), convert(false, targetEntityName, newEntity));
        }
    }

    protected void storeHook(String entityName, boolean isPK, List oldValues, List newValues) {
        UtilCache entityCache;
        synchronized (UtilCache.utilCacheTable) {
            entityCache = (UtilCache) UtilCache.utilCacheTable.get(getCacheName(entityName));
        }
        if (entityCache == null) return;
        Iterator it = entityCache.getCacheLineKeys().iterator();
        while (it.hasNext()) {
            EntityCondition condition = (EntityCondition) it.next();
            boolean shouldRemove = false;
            if (condition == null) {
                shouldRemove = true;
            } else if (oldValues == null) {
                Iterator it2 = newValues.iterator();
                while (it2.hasNext() && !shouldRemove) {
                    Map newValue = (Map) it2.next();
                    shouldRemove |= condition.mapMatches(getDelegator(), newValue);
                }
            } else {
                boolean oldMatched = false;
                Iterator it2 = oldValues.iterator();
                while (it2.hasNext() && !shouldRemove) {
                    Map oldValue = (Map) it2.next();
                    if (condition.mapMatches(getDelegator(), oldValue)) {
                        oldMatched = true;
                        if (newValues != null) {
                            Iterator it3 = newValues.iterator();
                            while (it3.hasNext() && !shouldRemove) {
                                Map newValue = (Map) it3.next();
                                shouldRemove |= isNull(newValue) || condition.mapMatches(getDelegator(), newValue);
                            }
                        }
                    }
                }
                if (!oldMatched && isPK) shouldRemove = true;
            }
            if (shouldRemove) {
                it.remove();
            }
        }
    }
}
