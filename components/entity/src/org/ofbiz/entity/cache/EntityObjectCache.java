package org.ofbiz.entity.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilCache;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityUtil;

public class EntityObjectCache extends AbstractEntityConditionCache {

    public EntityObjectCache(String delegatorName) {
        super(delegatorName, "object-list");
    }

    public Object get(String entityName, EntityCondition condition, String name) {
        return super.get(entityName, condition, name);
    }

    public Object put(String entityName, EntityCondition condition, String name, Object value) {
        return super.put(entityName, getFrozenConditionKey(condition), name, value);
    }

    public Object remove(String entityName, EntityCondition condition, String name) {
        return super.remove(entityName, condition, name);
    }
}
