package org.ofbiz.entity.cache;

import java.util.List;

import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.condition.EntityCondition;

public class Cache {

    protected EntityCache entityCache;
    protected EntityListCache entityListCache;
    protected EntityObjectCache entityObjectCache;

    protected String delegatorName;

    public Cache(String delegatorName) {
        this.delegatorName = delegatorName;
        entityCache = new EntityCache(delegatorName);
        entityListCache = new EntityListCache(delegatorName);
        entityObjectCache = new EntityObjectCache(delegatorName);
    }

    public void clear() {
        entityCache.clear();
        entityListCache.clear();
        entityObjectCache.clear();
    }

    public void remove(String entityName) {
        entityCache.remove(entityName);
        entityListCache.remove(entityName);
    }

    public GenericEntity get(GenericPK pk) {
        return entityCache.get(pk);
    }

    public List get(String entityName, EntityCondition condition, List orderBy) {
        return entityListCache.get(entityName, condition, orderBy);
    }

    public Object get(String entityName, EntityCondition condition, String name) {
        return entityObjectCache.get(entityName, condition, name);
    }

    public List put(String entityName, EntityCondition condition, List orderBy, List entities) {
        return entityListCache.put(entityName, condition, orderBy, entities);
    }

    public Object put(String entityName, EntityCondition condition, String name, Object value) {
        return entityObjectCache.put(entityName, condition, name, value);
    }

    public GenericEntity put(GenericEntity entity) {
        GenericEntity oldEntity = entityCache.put(entity.getPrimaryKey(), entity);
        if (entity.getModelEntity().getAutoClearCache()) {
            entityListCache.storeHook(entity);
            entityObjectCache.storeHook(entity);
        }
        return oldEntity;
    }
    
    public GenericEntity put(GenericPK pk, GenericEntity entity) {
        GenericEntity oldEntity = entityCache.put(pk, entity);
        if (pk.getModelEntity().getAutoClearCache()) {
            entityListCache.storeHook(pk, entity);
            entityObjectCache.storeHook(pk, entity);
        }
        return oldEntity;
    }

    public List remove(String entityName, EntityCondition condition, List orderBy) {
        entityCache.remove(entityName, condition);
        entityObjectCache.remove(entityName, condition);
        return entityListCache.remove(entityName, condition, orderBy);
    }

    public void remove(String entityName, EntityCondition condition) {
        entityCache.remove(entityName, condition);
        entityListCache.remove(entityName, condition);
        entityObjectCache.remove(entityName, condition);
    }

    public Object remove(String entityName, EntityCondition condition, String name) {
        return entityObjectCache.remove(entityName, condition, name);
    }

    public GenericEntity remove(GenericEntity entity) {
        GenericEntity oldEntity = entityCache.remove(entity.getPrimaryKey());
        entityListCache.storeHook(entity, null);
        entityObjectCache.storeHook(entity, null);
        return oldEntity;
    }

    public GenericEntity remove(GenericPK pk) {
        GenericEntity oldEntity = entityCache.remove(pk);
        entityListCache.storeHook(pk, null);
        entityObjectCache.storeHook(pk, null);
        return oldEntity;
    }
}
