/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.core.entity;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.entity.jdbc.SqlJdbcUtil;
import org.ofbiz.core.entity.model.ModelEntity;
import org.ofbiz.core.entity.model.ModelRelation;
import org.ofbiz.core.entity.model.ModelField;
import org.ofbiz.core.entity.model.ModelFieldTypeReader;

import java.util.*;

/**
 * Partial GenericHelper implementation that is entirely memory-based,
 * to be used for simple unit testing (can't do anything beyond searches
 * for primary keys, findByOr and findByAnd).
 *
 * @author <a href="mailto:plightbo@.com">Pat Lightbody</a>
 */
public class MemoryHelper implements GenericHelper {
    private static HashMap cache = new HashMap();

    public static void clearCache() {
        cache = new HashMap();
    }

    private String helperName;

    private boolean addToCache(GenericValue value) {
        if (value == null) {
            return false;
        }

        if (!veryifyValue(value)) {
            return false;
        }

        value = (GenericValue) value.clone();
        HashMap entityCache = (HashMap) cache.get(value.getEntityName());
        if (entityCache == null) {
            entityCache = new HashMap();
            cache.put(value.getEntityName(), entityCache);
        }

        entityCache.put(value.getPrimaryKey(), value);
        return true;
    }

    private GenericValue findFromCache(GenericPK pk) {
        if (pk == null) {
            return null;
        }

        HashMap entityCache = (HashMap) cache.get(pk.getEntityName());
        if (entityCache == null) {
            return null;
        }

        GenericValue value = (GenericValue) entityCache.get(pk);
        if (value == null) {
            return null;
        } else {
            return (GenericValue) value.clone();
        }
    }

    private int removeFromCache(GenericPK pk) {
        if (pk == null) {
            return 0;
        }

        HashMap entityCache = (HashMap) cache.get(pk.getEntityName());
        if (entityCache == null) {
            return 0;
        }

        Object o = entityCache.remove(pk);
        if (o == null) {
            return 0;
        } else {
            return 1;
        }
    }

    private boolean isAndMatch(Map values, Map fields) {
        for (Iterator iterator = fields.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry mapEntry = (Map.Entry) iterator.next();
            if (mapEntry.getValue() == null) {
                if (values.get(mapEntry.getKey()) != null) {
                    return false;
                }
            } else {
                try {
                    if (!mapEntry.getValue().equals(values.get(mapEntry.getKey()))) {
                        return false;
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isOrMatch(Map values, Map fields) {
        for (Iterator iterator = fields.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry mapEntry = (Map.Entry) iterator.next();
            if (mapEntry.getValue() == null) {
                if (values.get(mapEntry.getKey()) == null) {
                    return true;
                }
            } else {
                try {
                    if (mapEntry.getValue().equals(values.get(mapEntry.getKey()))) {
                        return true;
                    }
                } catch (Exception e) {
                }
            }
        }

        return false;
    }

    private boolean veryifyValue(GenericValue value) {
        ModelEntity me = value.getModelEntity();

        // make sure the PKs exist
        for (Iterator iterator = me.getPksIterator(); iterator.hasNext();) {
            ModelField field = (ModelField) iterator.next();
            if (!value.fields.containsKey(field.getName())) {
                return false;
            }
        }

        // make sure the value doesn't have any extra (unknown) fields
        for (Iterator iterator = value.fields.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (me.getField((String) entry.getKey()) == null) {
                return false;
            }
        }

        // make sure all fields that are in the value are of the right type
        for (Iterator iterator = me.getFieldsIterator(); iterator.hasNext();) {
            ModelField field = (ModelField) iterator.next();
            Object o = value.get(field.getName());
            int typeValue = 0;
            try {
                typeValue = SqlJdbcUtil.getType(modelFieldTypeReader.getModelFieldType(field.getType()).getJavaType());
            } catch (GenericNotImplementedException e) {
                return false;
            }

            if (o != null) {
                switch (typeValue) {
                    case 1:
                        if (!(o instanceof String)) {
                            return false;
                        }
                        break;
                    case 2:
                        if (!(o instanceof java.sql.Timestamp)) {
                            return false;
                        }
                        break;

                    case 3:
                        if (!(o instanceof java.sql.Time)) {
                            return false;
                        }
                        break;

                    case 4:
                        if (!(o instanceof java.sql.Date)) {
                            return false;
                        }
                        break;

                    case 5:
                        if (!(o instanceof Integer)) {
                            return false;
                        }
                        break;

                    case 6:
                        if (!(o instanceof Long)) {
                            return false;
                        }
                        break;

                    case 7:
                        if (!(o instanceof Float)) {
                            return false;
                        }
                        break;

                    case 8:
                        if (!(o instanceof Double)) {
                            return false;
                        }
                        break;

                    case 9:
                        if (!(o instanceof Boolean)) {
                            return false;
                        }
                        break;
                }
            }
        }

        return true;
    }

    private ModelFieldTypeReader modelFieldTypeReader;

    public MemoryHelper(String helperName) {
        this.helperName = helperName;
        modelFieldTypeReader = ModelFieldTypeReader.getModelFieldTypeReader(helperName);
    }

    public String getHelperName() {
        return helperName;
    }

    public GenericValue create(GenericValue value) throws GenericEntityException {
        if (addToCache(value)) {
            return value;
        } else {
            return null;
        }
    }

    public GenericValue create(GenericPK primaryKey) throws GenericEntityException {
        return create(new GenericValue(primaryKey));
    }

    public GenericValue findByPrimaryKey(GenericPK primaryKey) throws GenericEntityException {
        return findFromCache(primaryKey);
    }

    public GenericValue findByPrimaryKeyPartial(GenericPK primaryKey, Set keys) throws GenericEntityException {
        GenericValue value = findFromCache(primaryKey);
        value.setFields(value.getFields(keys));
        return value;
    }

    public List findAllByPrimaryKeys(List primaryKeys) throws GenericEntityException {
        ArrayList result = new ArrayList(primaryKeys.size());
        for (Iterator iterator = primaryKeys.iterator(); iterator.hasNext();) {
            GenericPK pk = (GenericPK) iterator.next();
            result.add(this.findByPrimaryKey(pk));
        }

        return result;
    }

    public int removeByPrimaryKey(GenericPK primaryKey) throws GenericEntityException {
        return removeFromCache(primaryKey);
    }

    public List findByAnd(ModelEntity modelEntity, Map fields, List orderBy) throws GenericEntityException {
        HashMap entityCache = (HashMap) cache.get(modelEntity.getEntityName());
        if (entityCache == null) {
            return Collections.EMPTY_LIST;
        }

        ArrayList result = new ArrayList();
        for (Iterator iterator = entityCache.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry mapEntry = (Map.Entry) iterator.next();
            GenericValue value = (GenericValue) mapEntry.getValue();

            if (isAndMatch(value.fields, fields)) {
                result.add(value);
            }
        }

        return result;
    }

    public List findByAnd(ModelEntity modelEntity, List expressions, List orderBy) throws GenericEntityException {
        return null;
    }

    public List findByLike(ModelEntity modelEntity, Map fields, List orderBy) throws GenericEntityException {
        return null;
    }

    public List findByClause(ModelEntity modelEntity, List entityClauses, Map fields, List orderBy) throws GenericEntityException {
        return null;
    }

    public List findByOr(ModelEntity modelEntity, Map fields, List orderBy) throws GenericEntityException {
        HashMap entityCache = (HashMap) cache.get(modelEntity.getEntityName());
        if (entityCache == null) {
            return Collections.EMPTY_LIST;
        }

        ArrayList result = new ArrayList();
        for (Iterator iterator = entityCache.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry mapEntry = (Map.Entry) iterator.next();
            GenericValue value = (GenericValue) mapEntry.getValue();

            if (isOrMatch(value.fields, fields)) {
                result.add(value);
            }
        }

        return result;

    }

    public List findByOr(ModelEntity modelEntity, List expressions, List orderBy) throws GenericEntityException {
        return null;
    }

    public List findByCondition(ModelEntity modelEntity, EntityCondition entityCondition,
                                Collection fieldsToSelect, List orderBy) throws GenericEntityException {
        return null;
    }

    public List findByMultiRelation(GenericValue value, ModelRelation modelRelationOne, ModelEntity modelEntityOne,
                                    ModelRelation modelRelationTwo, ModelEntity modelEntityTwo) throws GenericEntityException {
        return null;
    }

    public EntityListIterator findListIteratorByCondition(ModelEntity modelEntity, EntityCondition whereEntityCondition,
                                                          EntityCondition havingEntityCondition, Collection fieldsToSelect, List orderBy, EntityFindOptions findOptions)
            throws GenericEntityException {
        return null;
    }

    public int removeByAnd(ModelEntity modelEntity, Map fields) throws GenericEntityException {
        HashMap entityCache = (HashMap) cache.get(modelEntity.getEntityName());
        if (entityCache == null) {
            return 0;
        }

        ArrayList removeList = new ArrayList();
        for (Iterator iterator = entityCache.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry mapEntry = (Map.Entry) iterator.next();
            GenericValue value = (GenericValue) mapEntry.getValue();
            if (isAndMatch(value.fields, fields)) {
                removeList.add(mapEntry.getKey());
            }
        }

        return removeAll(removeList);
    }

    public int store(GenericValue value) throws GenericEntityException {
        if (addToCache(value)) {
            return 1;
        } else {
            return 0;
        }
    }

    public int storeAll(List values) throws GenericEntityException {
        int count = 0;
        for (Iterator iterator = values.iterator(); iterator.hasNext();) {
            GenericValue gv = (GenericValue) iterator.next();
            if (addToCache(gv)) {
                count++;
            }
        }

        return count;
    }

    public int removeAll(List dummyPKs) throws GenericEntityException {
        int count = 0;
        for (Iterator iterator = dummyPKs.iterator(); iterator.hasNext();) {
            GenericPK pk = (GenericPK) iterator.next();
            count = count + removeFromCache(pk);
        }

        return count;
    }

    public void checkDataSource(Map modelEntities, Collection messages, boolean addMissing) throws GenericEntityException {
    }
}
