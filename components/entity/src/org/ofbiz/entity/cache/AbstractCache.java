package org.ofbiz.entity.cache;

import java.util.Arrays;
import org.ofbiz.base.util.UtilCache;
import org.ofbiz.entity.GenericDelegator;

public abstract class AbstractCache {

    protected String delegatorName, id;

    protected AbstractCache(String delegatorName, String id) {
        this.delegatorName = delegatorName;
        this.id = id;
    }

    public GenericDelegator getDelegator() {
        return GenericDelegator.getGenericDelegator(delegatorName);
    }

    public void remove(String entityName) {
        UtilCache.clearCache(getCacheName(entityName));
    }

    public void clear() {
        UtilCache.clearCachesThatStartWith(getCacheNamePrefix());
    }

    public String getCacheNamePrefix() {
        return "Cache." + id + "." + delegatorName + ".";
    }

    public String[] getCacheNamePrefixes() {
        return new String[] {
            "Cache." + id + ".${delegator-name}.",
            "Cache." + id + "." + delegatorName + "."
        };
    }

    public String getCacheName(String entityName) {
        return getCacheNamePrefix() + entityName;
    }

    public String[] getCacheNames(String entityName) {
        String[] prefixes = getCacheNamePrefixes();
        String[] names = new String[prefixes.length * 2];
        for (int i = 0; i < prefixes.length; i++) {
            names[i] = prefixes[i] + "${entity-name}";
        }
        for (int i = prefixes.length, j = 0; j < prefixes.length; i++, j++) {
            names[i] = prefixes[j] + entityName;
        }
        return names;
    }

    protected UtilCache getCache(String entityName) {
        synchronized (UtilCache.utilCacheTable) {
            return (UtilCache) UtilCache.utilCacheTable.get(getCacheName(entityName));
        }
    }

    protected UtilCache getOrCreateCache(String entityName) {
        synchronized (UtilCache.utilCacheTable) {
            String name = getCacheName(entityName);
            UtilCache cache = (UtilCache) UtilCache.utilCacheTable.get(name);
            if (cache == null) {
                cache = new UtilCache(name);
                String[] names = getCacheNames(entityName);
                cache.setPropertiesParams(names);
            }
            return cache;
        }
    }
}
