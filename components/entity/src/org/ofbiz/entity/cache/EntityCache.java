package org.ofbiz.entity.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilCache;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.model.ModelEntity;

public class EntityCache extends AbstractCache {
    public static final String module = EntityCache.class.getName();

    public EntityCache(String delegatorName) {
        super(delegatorName, "entity");
    }

    public GenericEntity get(GenericPK pk) {
        UtilCache entityCache = getCache(pk.getEntityName());
        if (entityCache == null) return null;
        return (GenericEntity) entityCache.get(pk);
    }

    public GenericEntity put(GenericEntity entity) {
        if (entity == null) return null;
        return put(entity.getPrimaryKey(), entity);
    }

    public GenericEntity put(GenericPK pk, GenericEntity entity) {
        if (pk.getModelEntity().getNeverCache()) {
            Debug.logWarning("Tried to put a value of the " + pk.getEntityName() + " entity in the BY PRIMARY KEY cache but this entity has never-cache set to true, not caching.", module);
            return null;
        }

        if (entity == null) {
            entity = GenericEntity.NULL_ENTITY;
        } else {
            // before going into the cache, make this value immutable
            entity.setImmutable();
        }
        UtilCache entityCache = getOrCreateCache(pk.getEntityName());
        return (GenericEntity)entityCache.put(pk, entity);
    }

    public void remove(String entityName, EntityCondition condition) {
        UtilCache entityCache = getCache(entityName);
        if (entityCache == null) return;
        Iterator it = entityCache.getCacheLineValues().iterator();
        while (it.hasNext()) {
            UtilCache.CacheLine line = (UtilCache.CacheLine) it.next();
            if (line.hasExpired()) continue;
            GenericEntity entity = (GenericEntity) line.getValue();
            if (entity == null) continue;
            if (condition.entityMatches(entity)) it.remove();
        }
    }

    public GenericEntity remove(GenericEntity entity) {
        return remove(entity.getPrimaryKey());
    }

    public GenericEntity remove(GenericPK pk) {
        UtilCache entityCache = getCache(pk.getEntityName());
        if (entityCache == null) return null;
        return (GenericEntity) entityCache.remove(pk);
    }
}
