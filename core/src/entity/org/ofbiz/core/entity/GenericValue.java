/*
 * $Id$
 *
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.core.entity;


import java.util.*;

import org.ofbiz.core.entity.model.*;
import org.ofbiz.core.util.*;


/**
 * Generic Entity Value Object - Handles persisntence for any defined entity.
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     Eric Pabst
 *@created    Wed Aug 08 2001
 *@version    1.0
 */
public class GenericValue extends GenericEntity {

    /** Hashtable to cache various related entity collections */
    public transient Map relatedCache = null;

    /** Hashtable to cache various related cardinality one entity collections */
    public transient Map relatedOneCache = null;

    /** This Map will contain the original field values from the database iff
     * this GenericValue came from the database. If it was made manually it will
     * no have this Map, ie it will be null to not take up memory.
     */
    protected Map originalDbValues = null;

    /** Creates new GenericValue */
    public GenericValue(ModelEntity modelEntity) {
        super(modelEntity);
    }

    /** Creates new GenericValue from existing Map */
    public GenericValue(ModelEntity modelEntity, Map fields) {
        super(modelEntity, fields);
    }

    /** Creates new GenericValue from existing GenericValue */
    public GenericValue(GenericValue value) {
        super(value);
    }

    /** Creates new GenericValue from existing GenericValue */
    public GenericValue(GenericPK primaryKey) {
        super(primaryKey);
    }

    public GenericValue create() throws GenericEntityException {
        return this.getDelegator().create(this);
    }

    public void store() throws GenericEntityException {
        this.getDelegator().store(this);
    }

    public void remove() throws GenericEntityException {
        this.getDelegator().removeValue(this);
    }

    public void refresh() throws GenericEntityException {
        this.getDelegator().refresh(this);
    }

    public boolean originalDbValuesAvailable() {
        return this.originalDbValues != null ? true : false;
    }

    public Object getOriginalDbValue(String name) {
        if (getModelEntity().getField(name) == null) {
            throw new IllegalArgumentException("[GenericEntity.get] \"" + name + "\" is not a field of " + entityName);
        }
        return originalDbValues.get(name);
    }

    /** This should only be called by the Entity Engine once a GenericValue has 
     * been read from the database so that we have a copy of the original field 
     * values from the Db.
     */
    public void copyOriginalDbValues() {
        this.originalDbValues = new HashMap(this.fields);
    }

    /** Get the named Related Entity for the GenericValue from the persistent store
     *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
     *@return List of GenericValue instances as specified in the relation definition
     */
    public List getRelated(String relationName) throws GenericEntityException {
        return this.getDelegator().getRelated(relationName, this);
    }

    /** Get the named Related Entity for the GenericValue from the persistent store
     *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
     * @param byAndFields the fields that must equal in order to keep; may be null
     * @param orderBy The fields of the named entity to order the query by; may be null;
     *      optionally add a " ASC" for ascending or " DESC" for descending
     *@return List of GenericValue instances as specified in the relation definition
     */
    public List getRelated(String relationName, Map byAndFields, List orderBy) throws GenericEntityException {
        return this.getDelegator().getRelated(relationName, byAndFields, orderBy, this);
    }

    /** Get the named Related Entity for the GenericValue from the persistent
     *  store, looking first in the global generic cache (for the moment this isn't true, is same as EmbeddedCache variant)
     *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
     *@return List of GenericValue instances as specified in the relation definition
     */
    public List getRelatedCache(String relationName) throws GenericEntityException {
        return this.getDelegator().getRelatedCache(relationName, this);
    }

    public List getRelatedMulti(String relationNameOne, String relationNameTwo) throws GenericEntityException {
        return this.getDelegator().getMultiRelation(this, relationNameOne, relationNameTwo);
    }

    /** Get the named Related Entity for the GenericValue from the persistent
     *  store, looking first in the global generic cache (for the moment this isn't true, is same as EmbeddedCache variant)
     *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
     * @param byAndFields the fields that must equal in order to keep; may be null
     * @param orderBy The fields of the named entity to order the query by; may be null;
     *      optionally add a " ASC" for ascending or " DESC" for descending
     *@return List of GenericValue instances as specified in the relation definition
     */
    public List getRelatedCache(String relationName, Map byAndFields, List orderBy) throws GenericEntityException {
        List col = getRelatedCache(relationName);

        if (byAndFields != null) col = EntityUtil.filterByAnd(col, byAndFields);
        if (UtilValidate.isNotEmpty(orderBy)) col = EntityUtil.orderBy(col, orderBy);
        return col;
    }

    /** Get the named Related Entity for the GenericValue from the persistent
     *  store, looking first in a cache associated with this entity which is
     *  destroyed with this ValueObject when no longer used.
     *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
     *@return List of GenericValue instances as specified in the relation definition
     */
    public List getRelatedEmbeddedCache(String relationName) throws GenericEntityException {
        if (relatedCache == null) relatedCache = new Hashtable();
        List col = (List) relatedCache.get(relationName);

        if (col == null) {
            col = getRelated(relationName);
            relatedCache.put(relationName, col);
        }
        return col;
    }

    /** Get the named Related Entity for the GenericValue from the persistent
     *  store, looking first in a cache associated with this entity which is
     *  destroyed with this ValueObject when no longer used.
     *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
     * @param byAndFields the fields that must equal in order to keep; may be null
     * @param orderBy The fields of the named entity to order the query by; may be null;
     *      optionally add a " ASC" for ascending or " DESC" for descending
     *@return List of GenericValue instances as specified in the relation definition
     */
    public List getRelatedEmbeddedCache(String relationName, Map byAndFields, List orderBy) throws GenericEntityException {
        List col = getRelatedEmbeddedCache(relationName);

        if (byAndFields != null) col = EntityUtil.filterByAnd(col, byAndFields);
        if (UtilValidate.isNotEmpty(orderBy)) col = EntityUtil.orderBy(col, orderBy);
        return col;
    }

    /** Get the named Related Entity for the GenericValue from the persistent store
     *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
     *@return List of GenericValue instances as specified in the relation definition
     */
    public GenericValue getRelatedOne(String relationName) throws GenericEntityException {
        return this.getDelegator().getRelatedOne(relationName, this);
    }

    /** Get the named Related Entity for the GenericValue from the persistent
     *  store, looking first in the global generic cache (for the moment this isn't true, is same as EmbeddedCache variant)
     *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
     *@return List of GenericValue instances as specified in the relation definition
     */
    public GenericValue getRelatedOneCache(String relationName) throws GenericEntityException {
        return this.getDelegator().getRelatedOneCache(relationName, this);
    }

    /** Get the named Related Entity for the GenericValue from the persistent
     *  store, looking first in a cache associated with this entity which is
     *  destroyed with this ValueObject when no longer used.
     *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
     *@return List of GenericValue instances as specified in the relation definition
     */
    public GenericValue getRelatedOneEmbeddedCache(String relationName) throws GenericEntityException {
        if (relatedOneCache == null) relatedOneCache = new Hashtable();
        GenericValue value = (GenericValue) relatedOneCache.get(relationName);

        if (value == null) {
            value = getRelatedOne(relationName);
            if (value != null) relatedOneCache.put(relationName, value);
        }
        return value;
    }

    /** Get the named Related Entity for the GenericValue from the persistent store and filter it
     *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
     *@param fields the fields that must equal in order to keep
     *@return List of GenericValue instances as specified in the relation definition
     */
    public List getRelatedByAnd(String relationName, Map fields) throws GenericEntityException {
        return this.getDelegator().getRelatedByAnd(relationName, fields, this);
    }

    /** Get the named Related Entity for the GenericValue from the persistent
     *  store and filter it, looking first in the global generic cache (for the moment this isn't true, is same as EmbeddedCache variant)
     *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
     *@param fields the fields that must equal in order to keep
     *@return List of GenericValue instances as specified in the relation definition
     */
    public List getRelatedByAndCache(String relationName, Map fields) throws GenericEntityException {
        return EntityUtil.filterByAnd(this.getDelegator().getRelatedCache(relationName, this), fields);
    }

    /** Get the named Related Entity for the GenericValue from the persistent
     *  store and filter it, looking first in a cache associated with this entity which is
     *  destroyed with this ValueObject when no longer used.
     *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
     *@param fields the fields that must equal in order to keep
     *@return List of GenericValue instances as specified in the relation definition
     */
    public List getRelatedByAndEmbeddedCache(String relationName, Map fields) throws GenericEntityException {
        return EntityUtil.filterByAnd(getRelatedEmbeddedCache(relationName), fields);
    }

    /** Get the named Related Entity for the GenericValue from the persistent store and order it
     *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
     *@param orderBy the order that they should be returned
     *@return List of GenericValue instances as specified in the relation definition
     */
    public List getRelatedOrderBy(String relationName, List orderBy) throws GenericEntityException {
        return this.getDelegator().getRelatedOrderBy(relationName, orderBy, this);
    }

    /** Get the named Related Entity for the GenericValue from the persistent
     *  store and order it, looking first in the global generic cache (for the moment this isn't true, is same as EmbeddedCache variant)
     *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
     *@param orderBy the order that they should be returned
     *@return List of GenericValue instances as specified in the relation definition
     */
    public List getRelatedOrderByCache(String relationName, List orderBy) throws GenericEntityException {
        return EntityUtil.orderBy(this.getDelegator().getRelatedCache(relationName, this), orderBy);
    }

    /** Get the named Related Entity for the GenericValue from the persistent
     *  store and order it, looking first in a cache associated with this entity which is
     *  destroyed with this ValueObject when no longer used.
     *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
     *@param orderBy the order that they should be returned
     *@return List of GenericValue instances as specified in the relation definition
     */
    public List getRelatedOrderByEmbeddedCache(String relationName, List orderBy) throws GenericEntityException {
        return EntityUtil.orderBy(getRelatedEmbeddedCache(relationName), orderBy);
    }

    /** Remove the named Related Entity for the GenericValue from the persistent store
     *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
     */
    public void removeRelated(String relationName) throws GenericEntityException {
        this.getDelegator().removeRelated(relationName, this);
    }

    /** Get a dummy primary key for the named Related Entity for the GenericValue
     * @param relationName String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file
     * @return GenericPK containing a possibly incomplete PrimaryKey object representing the related entity or entities
     */
    public GenericPK getRelatedDummyPK(String relationName) throws GenericEntityException {
        return this.getDelegator().getRelatedDummyPK(relationName, null, this);
    }

    /** Get a dummy primary key for the named Related Entity for the GenericValue
     * @param relationName String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file
     * @param byAndFields the fields that must equal in order to keep; may be null
     * @return GenericPK containing a possibly incomplete PrimaryKey object representing the related entity or entities
     */
    public GenericPK getRelatedDummyPK(String relationName, Map byAndFields) throws GenericEntityException {
        return this.getDelegator().getRelatedDummyPK(relationName, byAndFields, this);
    }

    /** Clones this GenericValue, this is a shallow clone & uses the default shallow HashMap clone
     *@return Object that is a clone of this GenericValue
     */
    public Object clone() {
        GenericValue newEntity = new GenericValue(this);

        newEntity.setDelegator(internalDelegator);
        return newEntity;
    }
}
