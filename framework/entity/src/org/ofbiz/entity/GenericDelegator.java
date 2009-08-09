/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.entity;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.entity.cache.Cache;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.datasource.GenericHelper;
import org.ofbiz.entity.eca.EntityEcaHandler;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelFieldType;
import org.ofbiz.entity.model.ModelFieldTypeReader;
import org.ofbiz.entity.model.ModelGroupReader;
import org.ofbiz.entity.model.ModelReader;
import org.ofbiz.entity.util.DistributedCacheClear;
import org.ofbiz.entity.util.EntityCrypto;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.SequenceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Delegator Interface
 */
public interface GenericDelegator {

    public void clearAllCacheLinesByDummyPK(Collection<GenericPK> dummyPKs);

    public void clearAllCacheLinesByValue(Collection<GenericValue> values);

    /** This method is a shortcut to completely clear all entity engine caches.
     * For performance reasons this should not be called very often.
     */
    public void clearAllCaches();

    public void clearAllCaches(boolean distribute);

    /** Remove a CACHED Generic Entity from the cache by its primary key, does NOT
     * check to see if the passed GenericPK is a complete primary key.
     * Also tries to clear the corresponding all cache entry.
     *@param primaryKey The primary key to clear by.
     */
    public void clearCacheLine(GenericPK primaryKey);

    public void clearCacheLine(GenericPK primaryKey, boolean distribute);

    /** Remove a CACHED GenericValue from as many caches as it can. Automatically
     * tries to remove entries from the all cache, the by primary key cache, and
     * the by and cache. This is the ONLY method that tries to clear automatically
     * from the by and cache.
     *@param value The GenericValue to clear by.
     */
    public void clearCacheLine(GenericValue value);

    public void clearCacheLine(GenericValue value, boolean distribute);

    /** Remove all CACHED Generic Entity (List) from the cache
     *@param entityName The Name of the Entity as defined in the entity XML file
     */
    public void clearCacheLine(String entityName);

    /** Remove a CACHED Generic Entity (List) from the cache, either a PK, ByAnd, or All
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponding values
     */
    public void clearCacheLine(String entityName, Map<String, ? extends Object> fields);

    /** Remove a CACHED Generic Entity (List) from the cache, either a PK, ByAnd, or All
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponding values
     */
    public void clearCacheLine(String entityName, Object... fields);

    public void clearCacheLineByCondition(String entityName, EntityCondition condition);

    public void clearCacheLineByCondition(String entityName, EntityCondition condition, boolean distribute);

    /** Remove a CACHED Generic Entity from the cache by its primary key.
     * Checks to see if the passed GenericPK is a complete primary key, if
     * it is then the cache line will be removed from the primaryKeyCache; if it
     * is NOT a complete primary key it will remove the cache line from the andCache.
     * If the fields map is empty, then the allCache for the entity will be cleared.
     *@param dummyPK The dummy primary key to clear by.
     */
    public void clearCacheLineFlexible(GenericEntity dummyPK);

    public void clearCacheLineFlexible(GenericEntity dummyPK, boolean distribute);

    public GenericDelegator cloneDelegator();

    public GenericDelegator cloneDelegator(String delegatorName);

    /** Creates a Entity in the form of a GenericValue and write it to the datasource
     *@param primaryKey The GenericPK to create a value in the datasource from
     *@return GenericValue instance containing the new instance
     */
    public GenericValue create(GenericPK primaryKey) throws GenericEntityException;

    /** Creates a Entity in the form of a GenericValue and write it to the datasource
     *@param primaryKey The GenericPK to create a value in the datasource from
     *@param doCacheClear boolean that specifies whether to clear related cache entries for this primaryKey to be created
     *@return GenericValue instance containing the new instance
     */
    public GenericValue create(GenericPK primaryKey, boolean doCacheClear) throws GenericEntityException;

    /** Creates a Entity in the form of a GenericValue and write it to the datasource
     *@param value The GenericValue to create a value in the datasource from
     *@return GenericValue instance containing the new instance
     */
    public GenericValue create(GenericValue value) throws GenericEntityException;

    /** Creates a Entity in the form of a GenericValue and write it to the datasource
     *@param value The GenericValue to create a value in the datasource from
     *@param doCacheClear boolean that specifies whether or not to automatically clear cache entries related to this operation
     *@return GenericValue instance containing the new instance
     */
    public GenericValue create(GenericValue value, boolean doCacheClear) throws GenericEntityException;

    /** Creates a Entity in the form of a GenericValue and write it to the database
     *@return GenericValue instance containing the new instance
     */
    public GenericValue create(String entityName, Map<String, ? extends Object> fields) throws GenericEntityException;

    /** Creates a Entity in the form of a GenericValue and write it to the database
     *@return GenericValue instance containing the new instance
     */
    public GenericValue create(String entityName, Object... fields) throws GenericEntityException;

    /** Creates or stores an Entity
     *@param value The GenericValue instance containing the new or existing instance
     *@return GenericValue instance containing the new or updated instance
     */
    public GenericValue createOrStore(GenericValue value) throws GenericEntityException;

    /** Creates or stores an Entity
     *@param value The GenericValue instance containing the new or existing instance
     *@param doCacheClear boolean that specifies whether or not to automatically clear cache entries related to this operation
     *@return GenericValue instance containing the new or updated instance
     */
    public GenericValue createOrStore(GenericValue value, boolean doCacheClear) throws GenericEntityException;

    /** Sets the sequenced ID (for entity with one primary key field ONLY), and then does a create in the database
     * as normal. The reason to do it this way is that it will retry and fix the sequence if somehow the sequencer
     * is in a bad state and returning a value that already exists.
     *@param value The GenericValue to create a value in the datasource from
     *@return GenericValue instance containing the new instance
     */
    public GenericValue createSetNextSeqId(GenericValue value) throws GenericEntityException;

    /** Creates a Entity in the form of a GenericValue and write it to the database
     *@return GenericValue instance containing the new instance
     */
    public GenericValue createSingle(String entityName, Object singlePkValue) throws GenericEntityException;

    public void decryptFields(GenericEntity entity) throws GenericEntityException;

    public void decryptFields(List<? extends GenericEntity> entities) throws GenericEntityException;

    public void encryptFields(GenericEntity entity) throws GenericEntityException;

    public void encryptFields(List<? extends GenericEntity> entities) throws GenericEntityException;

    public Object encryptFieldValue(String entityName, Object fieldValue) throws EntityCryptoException;

    /** Finds GenericValues by the conditions specified in the EntityCondition object, the the EntityCondition javadoc for more details.
     * NOTE 20080502: 3 references
     *@param entityName The name of the Entity as defined in the entity XML file
     *@param whereEntityCondition The EntityCondition object that specifies how to constrain this query before any groupings are done (if this is a view entity with group-by aliases)
     *@param havingEntityCondition The EntityCondition object that specifies how to constrain this query after any groupings are done (if this is a view entity with group-by aliases)
     *@param fieldsToSelect The fields of the named entity to get from the database; if empty or null all fields will be retreived
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@param findOptions An instance of EntityFindOptions that specifies advanced query options. See the EntityFindOptions JavaDoc for more details.
     *@return EntityListIterator representing the result of the query: NOTE THAT THIS MUST BE CLOSED (preferably in a finally block) WHEN YOU ARE
     *      DONE WITH IT, AND DON'T LEAVE IT OPEN TOO LONG BEACUSE IT WILL MAINTAIN A DATABASE CONNECTION.
     */
    public EntityListIterator find(String entityName, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, Set<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions) throws GenericEntityException;

    /** Finds all Generic entities
     * NOTE 20080502: 14 references; all changed to findList
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@return    List containing all Generic entities
     *@deprecated Use findList() instead
     */
    @Deprecated
    public List<GenericValue> findAll(String entityName) throws GenericEntityException;

    /** Finds all Generic entities
     * NOTE 20080502: 10 references; all changed to findList
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return    List containing all Generic entities
     *@deprecated Use findList() instead
     */
    @Deprecated
    public List<GenericValue> findAll(String entityName, List<String> orderBy) throws GenericEntityException;

    /** Finds all Generic entities
     * NOTE 20080502: 0 references
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return    List containing all Generic entities
     *@deprecated Use findList() instead
     */
    @Deprecated
    public List<GenericValue> findAll(String entityName, String... orderBy) throws GenericEntityException;

    /** Find a number of Generic Value objects by their Primary Keys, all at once
     * NOTE 20080502: 0 references
     *@param primaryKeys A Collection of primary keys to find by.
     *@return List of GenericValue objects corresponding to the passed primaryKey objects
     *@deprecated
     */
    @Deprecated
    public List<GenericValue> findAllByPrimaryKeys(Collection<GenericPK> primaryKeys) throws GenericEntityException;

    /** Find a number of Generic Value objects by their Primary Keys, all at once;
     *  this first looks in the local cache for each PK and if there then it puts it
     *  in the return list rather than putting it in the batch to send to
     *  a given helper.
     * NOTE 20080502: 0 references
     *@param primaryKeys A Collection of primary keys to find by.
     *@return List of GenericValue objects corresponding to the passed primaryKey objects
     *@deprecated
     */
    @Deprecated
    public List<GenericValue> findAllByPrimaryKeysCache(Collection<GenericPK> primaryKeys) throws GenericEntityException;

    /** Finds all Generic entities, looking first in the cache
     * NOTE 20080502: 4 references; all changed to findList
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@return    List containing all Generic entities
     *@deprecated Use findList() instead
     */
    @Deprecated
    public List<GenericValue> findAllCache(String entityName) throws GenericEntityException;

    /** Finds all Generic entities, looking first in the cache; uses orderBy for lookup, but only keys results on the entityName and fields
     * NOTE 20080502: 2 references; all changed to findList
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return    List containing all Generic entities
     *@deprecated Use findList() instead
     */
    @Deprecated
    public List<GenericValue> findAllCache(String entityName, List<String> orderBy) throws GenericEntityException;

    /** Finds all Generic entities, looking first in the cache; uses orderBy for lookup, but only keys results on the entityName and fields
     * NOTE 20080502: 0 references
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return    List containing all Generic entities
     *@deprecated Use findList() instead
     */
    @Deprecated
    public List<GenericValue> findAllCache(String entityName, String... orderBy) throws GenericEntityException;

    /** Finds Generic Entity records by all of the specified expressions (ie: combined using AND)
     * NOTE 20080502: 11 references; all changed to findList
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param expressions The expressions to use for the lookup, each consisting of at least a field name, an EntityOperator, and a value to compare to
     *@return List of GenericValue instances that match the query
     *@deprecated Use findList() instead
     */
    @Deprecated
    public <T extends EntityCondition> List<GenericValue> findByAnd(String entityName, List<T> expressions) throws GenericEntityException;

    /** Finds Generic Entity records by all of the specified expressions (ie: combined using AND)
     * NOTE 20080502: 24 references; all changed to findList
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param expressions The expressions to use for the lookup, each consisting of at least a field name, an EntityOperator, and a value to compare to
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return List of GenericValue instances that match the query
     *@deprecated Use findList() instead
     */
    @Deprecated
    public <T extends EntityCondition> List<GenericValue> findByAnd(String entityName, List<T> expressions, List<String> orderBy) throws GenericEntityException;

    /** Finds Generic Entity records by all of the specified fields (ie: combined using AND)
     * NOTE 20080502: 264 references
     * @param entityName The Name of the Entity as defined in the entity XML file
     * @param fields The fields of the named entity to query by with their corresponding values
     * @return List of GenericValue instances that match the query
     */
    public List<GenericValue> findByAnd(String entityName, Map<String, ? extends Object> fields) throws GenericEntityException;

    /** Finds Generic Entity records by all of the specified fields (ie: combined using AND)
     * NOTE 20080502: 72 references
     * @param entityName The Name of the Entity as defined in the entity XML file
     * @param fields The fields of the named entity to query by with their corresponding values
     * @param orderBy The fields of the named entity to order the query by;
     *      optionally add a " ASC" for ascending or " DESC" for descending
     * @return List of GenericValue instances that match the query
     */
    public List<GenericValue> findByAnd(String entityName, Map<String, ? extends Object> fields, List<String> orderBy) throws GenericEntityException;

    /** Finds Generic Entity records by all of the specified fields (ie: combined using AND)
     * NOTE 20080502: 1 references
     * @param entityName The Name of the Entity as defined in the entity XML file
     * @param fields The fields of the named entity to query by with their corresponding values
     * @return List of GenericValue instances that match the query
     */
    public List<GenericValue> findByAnd(String entityName, Object... fields) throws GenericEntityException;

    /** Finds Generic Entity records by all of the specified expressions (ie: combined using AND)
     * NOTE 20080502: 0 references
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param expressions The expressions to use for the lookup, each consisting of at least a field name, an EntityOperator, and a value to compare to
     *@return List of GenericValue instances that match the query
     *@deprecated Use findList() instead
     */
    @Deprecated
    public <T extends EntityCondition> List<GenericValue> findByAnd(String entityName, T... expressions) throws GenericEntityException;

    /** Finds Generic Entity records by all of the specified fields (ie: combined using AND), looking first in the cache; uses orderBy for lookup, but only keys results on the entityName and fields
     * NOTE 20080502: 91 references
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponding values
     *@return List of GenericValue instances that match the query
     */
    public List<GenericValue> findByAndCache(String entityName, Map<String, ? extends Object> fields) throws GenericEntityException;

    /** Finds Generic Entity records by all of the specified fields (ie: combined using AND), looking first in the cache; uses orderBy for lookup, but only keys results on the entityName and fields
     * NOTE 20080502: 56 references
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponding values
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return List of GenericValue instances that match the query
     */
    public List<GenericValue> findByAndCache(String entityName, Map<String, ? extends Object> fields, List<String> orderBy) throws GenericEntityException;

    /** Finds Generic Entity records by all of the specified fields (ie: combined using AND), looking first in the cache; uses orderBy for lookup, but only keys results on the entityName and fields
     * NOTE 20080502: 0 references
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponding values
     *@return List of GenericValue instances that match the query
     *@deprecated Use findList() instead
     */
    @Deprecated
    public List<GenericValue> findByAndCache(String entityName, Object... fields) throws GenericEntityException;

    /** Finds GenericValues by the conditions specified in the EntityCondition object, the the EntityCondition javadoc for more details.
     * NOTE 20080502: 64 references; all changed to findList
     *@param entityName The Name of the Entity as defined in the entity model XML file
     *@param entityCondition The EntityCondition object that specifies how to constrain this query
     *@param fieldsToSelect The fields of the named entity to get from the database; if empty or null all fields will be retreived
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return List of GenericValue objects representing the result
     *@deprecated Use findList() instead
     */
    @Deprecated
    public List<GenericValue> findByCondition(String entityName, EntityCondition entityCondition, Collection<String> fieldsToSelect, List<String> orderBy) throws GenericEntityException;

    /** Finds GenericValues by the conditions specified in the EntityCondition object, the the EntityCondition javadoc for more details.
     * NOTE 20080502: 6 references; all changed to findList
     *@param entityName The name of the Entity as defined in the entity XML file
     *@param whereEntityCondition The EntityCondition object that specifies how to constrain this query before any groupings are done (if this is a view entity with group-by aliases)
     *@param havingEntityCondition The EntityCondition object that specifies how to constrain this query after any groupings are done (if this is a view entity with group-by aliases)
     *@param fieldsToSelect The fields of the named entity to get from the database; if empty or null all fields will be retreived
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@param findOptions An instance of EntityFindOptions that specifies advanced query options. See the EntityFindOptions JavaDoc for more details.
     *@return List of GenericValue objects representing the result
     *@deprecated Use findList() instead
     */
    @Deprecated
    public List<GenericValue> findByCondition(String entityName, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, Collection<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions) throws GenericEntityException;

    /** Finds GenericValues by the conditions specified in the EntityCondition object, looking first in the cache, see the EntityCondition javadoc for more details.
     * NOTE 20080502: 17 references; all changed to findList
     *@param entityName The Name of the Entity as defined in the entity model XML file
     *@param entityCondition The EntityCondition object that specifies how to constrain this query
     *@param fieldsToSelect The fields of the named entity to get from the database; if empty or null all fields will be retreived
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return List of GenericValue objects representing the result
     *@deprecated Use findList() instead
     */
    @Deprecated
    public List<GenericValue> findByConditionCache(String entityName, EntityCondition entityCondition, Collection<String> fieldsToSelect, List<String> orderBy) throws GenericEntityException;

    /**
     * NOTE 20080502: 1 references; all changed to findList
     *@deprecated Use findList() instead
     */
    @Deprecated
    public List<GenericValue> findByLike(String entityName, Map<String, ? extends Object> fields) throws GenericEntityException;

    /**
     * NOTE 20080502: 1 references; all changed to findList
     *@deprecated Use findList() instead
     */
    @Deprecated
    public List<GenericValue> findByLike(String entityName, Map<String, ? extends Object> fields, List<String> orderBy) throws GenericEntityException;

    /**
     * NOTE 20080502: 0 references
     *@deprecated Use findList() instead
     */
    @Deprecated
    public List<GenericValue> findByLike(String entityName, Object... fields) throws GenericEntityException;

    /** Finds Generic Entity records by all of the specified expressions (ie: combined using OR)
     * NOTE 20080502: 2 references; all changed to findList
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param expressions The expressions to use for the lookup, each consisting of at least a field name, an EntityOperator, and a value to compare to
     *@return List of GenericValue instances that match the query
     *@deprecated Use findList() instead
     */
    @Deprecated
    public <T extends EntityCondition> List<GenericValue> findByOr(String entityName, List<T> expressions) throws GenericEntityException;

    /** Finds Generic Entity records by all of the specified expressions (ie: combined using OR)
     * NOTE 20080502: 0 references
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param expressions The expressions to use for the lookup, each consisting of at least a field name, an EntityOperator, and a value to compare to
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return List of GenericValue instances that match the query
     *@deprecated Use findList() instead
     */
    @Deprecated
    public <T extends EntityCondition> List<GenericValue> findByOr(String entityName, List<T> expressions, List<String> orderBy) throws GenericEntityException;

    /** Finds Generic Entity records by all of the specified fields (ie: combined using OR)
     * NOTE 20080502: 1 references; all changed to findList
     * @param entityName The Name of the Entity as defined in the entity XML file
     * @param fields The fields of the named entity to query by with their corresponding values
     * @return List of GenericValue instances that match the query
     *@deprecated Use findList() instead
     */
    @Deprecated
    public List<GenericValue> findByOr(String entityName, Map<String, ? extends Object> fields) throws GenericEntityException;

    /** Finds Generic Entity records by all of the specified fields (ie: combined using OR)
     * NOTE 20080502: 1 references; all changed to findList
     * @param entityName The Name of the Entity as defined in the entity XML file
     * @param fields The fields of the named entity to query by with their corresponding values
     * @param orderBy The fields of the named entity to order the query by;
     *      optionally add a " ASC" for ascending or " DESC" for descending
     * @return List of GenericValue instances that match the query
     *@deprecated Use findList() instead
     */
    @Deprecated
    public List<GenericValue> findByOr(String entityName, Map<String, ? extends Object> fields, List<String> orderBy) throws GenericEntityException;

    /** Finds Generic Entity records by all of the specified fields (ie: combined using OR)
     * NOTE 20080502: 0 references
     * @param entityName The Name of the Entity as defined in the entity XML file
     * @param fields The fields of the named entity to query by with their corresponding values
     * @return List of GenericValue instances that match the query
     *@deprecated Use findList() instead
     */
    @Deprecated
    public List<GenericValue> findByOr(String entityName, Object... fields) throws GenericEntityException;

    /** Finds Generic Entity records by all of the specified expressions (ie: combined using OR)
     * NOTE 20080502: 0 references
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param expressions The expressions to use for the lookup, each consisting of at least a field name, an EntityOperator, and a value to compare to
     *@return List of GenericValue instances that match the query
     *@deprecated Use findList() instead
     */
    @Deprecated
    public <T extends EntityCondition> List<GenericValue> findByOr(String entityName, T... expressions) throws GenericEntityException;

    /** Find a Generic Entity by its Primary Key
     * NOTE 20080502: 15 references; all changed to findOne
     *@param primaryKey The primary key to find by.
     *@return The GenericValue corresponding to the primaryKey
     *@deprecated Use findOne() instead
     */
    @Deprecated
    public GenericValue findByPrimaryKey(GenericPK primaryKey) throws GenericEntityException;

    /** Find a Generic Entity by its Primary Key
     * NOTE 20080502: 550 references (20080503 521 left); needs to be deprecated, should use findOne instead, but lots of stuff to replace!
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponding values
     *@return The GenericValue corresponding to the primaryKey
     */
    public GenericValue findByPrimaryKey(String entityName, Map<String, ? extends Object> fields) throws GenericEntityException;

    /** Find a Generic Entity by its Primary Key
     * NOTE 20080502: 21 references; all changed to findOne
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponding values
     *@return The GenericValue corresponding to the primaryKey
     *@deprecated Use findOne() instead
     */
    @Deprecated
    public GenericValue findByPrimaryKey(String entityName, Object... fields) throws GenericEntityException;

    /** Find a CACHED Generic Entity by its Primary Key
     * NOTE 20080502: 2 references; all changed to findOne
     *@param primaryKey The primary key to find by.
     *@return The GenericValue corresponding to the primaryKey
     *@deprecated Use findOne() instead
     */
    @Deprecated
    public GenericValue findByPrimaryKeyCache(GenericPK primaryKey) throws GenericEntityException;

    /** Find a CACHED Generic Entity by its Primary Key
     * NOTE 20080502: 218 references
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponding values
     *@return The GenericValue corresponding to the primaryKey
     */
    public GenericValue findByPrimaryKeyCache(String entityName, Map<String, ? extends Object> fields) throws GenericEntityException;

    /** Find a CACHED Generic Entity by its Primary Key
     * NOTE 20080502: 2 references
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponding values
     *@return The GenericValue corresponding to the primaryKey
     */
    public GenericValue findByPrimaryKeyCache(String entityName, Object... fields) throws GenericEntityException;

    /** Find a CACHED Generic Entity by its Primary Key
     * NOTE 20080502: 0 references
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param singlePkValue
     *@return The GenericValue corresponding to the primaryKey
     *@deprecated Use findOne() instead
     */
    @Deprecated
    public GenericValue findByPrimaryKeyCacheSingle(String entityName, Object singlePkValue) throws GenericEntityException;

    /** Find a Generic Entity by its Primary Key and only returns the values requested by the passed keys (names)
     * NOTE 20080502: 3 references
     *@param primaryKey The primary key to find by.
     *@param keys The keys, or names, of the values to retrieve; only these values will be retrieved
     *@return The GenericValue corresponding to the primaryKey
     */
    public GenericValue findByPrimaryKeyPartial(GenericPK primaryKey, Set<String> keys) throws GenericEntityException;

    /** Find a Generic Entity by its Primary Key and only returns the values requested by the passed keys (names)
     * NOTE 20080502: 0 references
     *@param primaryKey The primary key to find by.
     *@param keys The keys, or names, of the values to retrieve; only these values will be retrieved
     *@return The GenericValue corresponding to the primaryKey
     *@deprecated Use findByPrimaryKeyPartial(GenericPK, Set<String>) instead
     */
    @Deprecated
    public GenericValue findByPrimaryKeyPartial(GenericPK primaryKey, String... keys) throws GenericEntityException;

    /** Find a Generic Entity by its Primary Key
     * NOTE 20080502: 0 references
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param singlePkValue
     *@return The GenericValue corresponding to the primaryKey
     *@deprecated Use findOne() instead
     */
    @Deprecated
    public GenericValue findByPrimaryKeySingle(String entityName, Object singlePkValue) throws GenericEntityException;

    /**
     * NOTE 20080502: 3 references; all changed to findCoundByCondition
     *@deprecated Use findCountByCondition() instead
     */
    @Deprecated
    public long findCountByAnd(String entityName) throws GenericEntityException;

    /**
     * NOTE 20080502: 8 references; all changed to use findCountByCondition
     *@deprecated Use findCountByCondition() instead
     */
    @Deprecated
    public long findCountByAnd(String entityName, Map<String, ? extends Object> fields) throws GenericEntityException;

    /**
     * NOTE 20080502: 1 references; all changed to use findCountByCondition
     *@deprecated Use findCountByCondition() instead
     */
    @Deprecated
    public long findCountByAnd(String entityName, Object... fields) throws GenericEntityException;

    /**
     * NOTE 20080502: 17 references; all changed to use remaining findCountByCondition
     *@deprecated Use findCountByCondition() instead
     */
    @Deprecated
    public long findCountByCondition(String entityName, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition) throws GenericEntityException;

    /**
     * NOTE 20080502: 2 references
     */
    public long findCountByCondition(String entityName, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, EntityFindOptions findOptions) throws GenericEntityException;

    /** Finds GenericValues by the conditions specified in the EntityCondition object, the the EntityCondition javadoc for more details.
     * NOTE 20080502: 12 references
     *@param entityName The name of the Entity as defined in the entity XML file
     *@param entityCondition The EntityCondition object that specifies how to constrain this query before any groupings are done (if this is a view entity with group-by aliases)
     *@param fieldsToSelect The fields of the named entity to get from the database; if empty or null all fields will be retreived
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@param findOptions An instance of EntityFindOptions that specifies advanced query options. See the EntityFindOptions JavaDoc for more details.
     *@return List of GenericValue objects representing the result
     */
    public List<GenericValue> findList(String entityName, EntityCondition entityCondition, Set<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions, boolean useCache) throws GenericEntityException;

    /** Finds GenericValues by the conditions specified in the EntityCondition object, the the EntityCondition javadoc for more details.
     * NOTE 20080502: 9 references
     *@param dynamicViewEntity The DynamicViewEntity to use for the entity model for this query; generally created on the fly for limited use
     *@param whereEntityCondition The EntityCondition object that specifies how to constrain this query before any groupings are done (if this is a view entity with group-by aliases)
     *@param havingEntityCondition The EntityCondition object that specifies how to constrain this query after any groupings are done (if this is a view entity with group-by aliases)
     *@param fieldsToSelect The fields of the named entity to get from the database; if empty or null all fields will be retreived
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@param findOptions An instance of EntityFindOptions that specifies advanced query options. See the EntityFindOptions JavaDoc for more details.
     *@return EntityListIterator representing the result of the query: NOTE THAT THIS MUST BE CLOSED WHEN YOU ARE
     *      DONE WITH IT, AND DON'T LEAVE IT OPEN TOO LONG BEACUSE IT WILL MAINTAIN A DATABASE CONNECTION.
     */
    public EntityListIterator findListIteratorByCondition(DynamicViewEntity dynamicViewEntity, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, Collection<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions) throws GenericEntityException;

    /** Finds GenericValues by the conditions specified in the EntityCondition object, the the EntityCondition javadoc for more details.
     * NOTE 20080502: 26 references; all changed to find
     *@param entityName The Name of the Entity as defined in the entity model XML file
     *@param entityCondition The EntityCondition object that specifies how to constrain this query before any groupings are done (if this is a view entity with group-by aliases)
     *@param fieldsToSelect The fields of the named entity to get from the database; if empty or null all fields will be retreived
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return EntityListIterator representing the result of the query: NOTE THAT THIS MUST BE CLOSED WHEN YOU ARE
     *      DONE WITH IT, AND DON'T LEAVE IT OPEN TOO LONG BEACUSE IT WILL MAINTAIN A DATABASE CONNECTION.
     *@deprecated Use find() instead
     */
    @Deprecated
    public EntityListIterator findListIteratorByCondition(String entityName, EntityCondition entityCondition, Collection<String> fieldsToSelect, List<String> orderBy) throws GenericEntityException;

    /** Finds GenericValues by the conditions specified in the EntityCondition object, the the EntityCondition javadoc for more details.
     * NOTE 20080502: 12 references; all changed to find
     *@param entityName The name of the Entity as defined in the entity XML file
     *@param whereEntityCondition The EntityCondition object that specifies how to constrain this query before any groupings are done (if this is a view entity with group-by aliases)
     *@param havingEntityCondition The EntityCondition object that specifies how to constrain this query after any groupings are done (if this is a view entity with group-by aliases)
     *@param fieldsToSelect The fields of the named entity to get from the database; if empty or null all fields will be retreived
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@param findOptions An instance of EntityFindOptions that specifies advanced query options. See the EntityFindOptions JavaDoc for more details.
     *@return EntityListIterator representing the result of the query: NOTE THAT THIS MUST BE CLOSED WHEN YOU ARE
     *      DONE WITH IT, AND DON'T LEAVE IT OPEN TOO LONG BEACUSE IT WILL MAINTAIN A DATABASE CONNECTION.
     *@deprecated Use find() instead
     */
    @Deprecated
    public EntityListIterator findListIteratorByCondition(String entityName, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, Collection<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions) throws GenericEntityException;

    /** Find a Generic Entity by its Primary Key
     * NOTE 20080502: 6 references
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponding values
     *@return The GenericValue corresponding to the primaryKey
     */
    public GenericValue findOne(String entityName, boolean useCache, Object... fields) throws GenericEntityException;

    /** Find a Generic Entity by its Primary Key
     * NOTE 20080502: 6 references
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponding values
     *@return The GenericValue corresponding to the primaryKey
     */
    public GenericValue findOne(String entityName, Map<String, ? extends Object> fields, boolean useCache) throws GenericEntityException;

    public Cache getCache();

    public GenericDelegator getDelegator(String delegatorName);

    public String getDelegatorName();

    public EntityEcaHandler<?> getEntityEcaHandler();

    /** Gets a field type instance by name from the helper that corresponds to the specified entity
     *@param entity The entity
     *@param type The name of the type
     *@return ModelFieldType instance for the named type from the helper that corresponds to the specified entity
     */
    public ModelFieldType getEntityFieldType(ModelEntity entity, String type) throws GenericEntityException;

    /** Gets field type names from the helper that corresponds to the specified entity
     *@param entity The entity
     *@return Collection of field type names from the helper that corresponds to the specified entity
     */
    public Collection<String> getEntityFieldTypeNames(ModelEntity entity) throws GenericEntityException;

    /** Gets the helper name that corresponds to this delegator and the specified entityName
     *@param entityName The name of the entity to get the helper for
     *@return String with the helper name that corresponds to this delegator and the specified entityName
     */
    public String getEntityGroupName(String entityName);

    /** Gets the an instance of helper that corresponds to this delegator and the specified entity
     *@param entity The entity to get the helper for
     *@return GenericHelper that corresponds to this delegator and the specified entity
     */
    public GenericHelper getEntityHelper(ModelEntity entity) throws GenericEntityException;

    /** Gets the an instance of helper that corresponds to this delegator and the specified entityName
     *@param entityName The name of the entity to get the helper for
     *@return GenericHelper that corresponds to this delegator and the specified entityName
     */
    public GenericHelper getEntityHelper(String entityName) throws GenericEntityException;

    /** Gets the helper name that corresponds to this delegator and the specified entity
     *@param entity The entity to get the helper for
     *@return String with the helper name that corresponds to this delegator and the specified entity
     */
    public String getEntityHelperName(ModelEntity entity);

    /** Gets the helper name that corresponds to this delegator and the specified entityName
     *@param entityName The name of the entity to get the helper name for
     *@return String with the helper name that corresponds to this delegator and the specified entityName
     */
    public String getEntityHelperName(String entityName);

    public GenericValue getFromPrimaryKeyCache(GenericPK primaryKey);

    /** Gets the helper name that corresponds to this delegator and the specified entityName
     *@param groupName The name of the group to get the helper name for
     *@return String with the helper name that corresponds to this delegator and the specified entityName
     */
    public String getGroupHelperName(String groupName);

    public Locale getLocale();

    /** Gets the instance of ModelEntity that corresponds to this delegator and the specified entityName
     *@param entityName The name of the entity to get
     *@return ModelEntity that corresponds to this delegator and the specified entityName
     */
    public ModelEntity getModelEntity(String entityName);

    /** Gets a Map of entity name & entity model pairs that are in the named group
     *@param groupName The name of the group
     *@return Map of entityName String keys and ModelEntity instance values
     */
    public Map<String, ModelEntity> getModelEntityMapByGroup(String groupName) throws GenericEntityException;

    public ModelFieldTypeReader getModelFieldTypeReader(ModelEntity entity);

    /** Gets the instance of ModelGroupReader that corresponds to this delegator
     *@return ModelGroupReader that corresponds to this delegator
     */
    public ModelGroupReader getModelGroupReader();

    /** Gets the instance of ModelReader that corresponds to this delegator
     *@return ModelReader that corresponds to this delegator
     */
    public ModelReader getModelReader();

    /**
     * Get the named Related Entity for the GenericValue from the persistent store across another Relation.
     * Helps to get related Values in a multi-to-multi relationship.
     * NOTE 20080502: 0 references
     * @param relationNameOne String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file, for first relation
     * @param relationNameTwo String containing the relation name for second relation
     * @param value GenericValue instance containing the entity
     * @return List of GenericValue instances as specified in the relation definition
     *@deprecated Use getMultiRelation() instead
     */
    @Deprecated
    public List<GenericValue> getMultiRelation(GenericValue value, String relationNameOne, String relationNameTwo) throws GenericEntityException;

    /**
     * Get the named Related Entity for the GenericValue from the persistent store across another Relation.
     * Helps to get related Values in a multi-to-multi relationship.
     * NOTE 20080502: 3 references
     * @param relationNameOne String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file, for first relation
     * @param relationNameTwo String containing the relation name for second relation
     * @param value GenericValue instance containing the entity
     * @param orderBy The fields of the named entity to order the query by; may be null;
     *      optionally add a " ASC" for ascending or " DESC" for descending
     * @return List of GenericValue instances as specified in the relation definition
     */
    public List<GenericValue> getMultiRelation(GenericValue value, String relationNameOne, String relationNameTwo, List<String> orderBy) throws GenericEntityException;

    /** Get the next guaranteed unique seq id from the sequence with the given sequence name;
     * if the named sequence doesn't exist, it will be created
     *@param seqName The name of the sequence to get the next seq id from
     *@return String with the next sequenced id for the given sequence name
     */
    public String getNextSeqId(String seqName);

    /** Get the next guaranteed unique seq id from the sequence with the given sequence name;
     * if the named sequence doesn't exist, it will be created
     *@param seqName The name of the sequence to get the next seq id from
     *@param staggerMax The maximum amount to stagger the sequenced ID, if 1 the sequence will be incremented by 1, otherwise the current sequence ID will be incremented by a value between 1 and staggerMax
     *@return Long with the next seq id for the given sequence name
     */
    public String getNextSeqId(String seqName, long staggerMax);

    /** Get the next guaranteed unique seq id from the sequence with the given sequence name;
     * if the named sequence doesn't exist, it will be created
     *@param seqName The name of the sequence to get the next seq id from
     *@return Long with the next sequenced id for the given sequence name
     */
    public Long getNextSeqIdLong(String seqName);

    /** Get the next guaranteed unique seq id from the sequence with the given sequence name;
     * if the named sequence doesn't exist, it will be created
     *@param seqName The name of the sequence to get the next seq id from
     *@param staggerMax The maximum amount to stagger the sequenced ID, if 1 the sequence will be incremented by 1, otherwise the current sequence ID will be incremented by a value between 1 and staggerMax
     *@return Long with the next seq id for the given sequence name
     */
    public Long getNextSeqIdLong(String seqName, long staggerMax);

    /** Gets the name of the server configuration that corresponds to this delegator
     * @return server configuration name
     */
    public String getOriginalDelegatorName();

    /** Get the named Related Entity for the GenericValue from the persistent store
     * NOTE 20080502: 1 references; all changed to use remaining getRelated
     * @param relationName String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file
     * @param value GenericValue instance containing the entity
     * @return List of GenericValue instances as specified in the relation definition
     *@deprecated Use getRelated() instead
     */
    @Deprecated
    public List<GenericValue> getRelated(String relationName, GenericValue value) throws GenericEntityException;

    /** Get the named Related Entity for the GenericValue from the persistent store
     * NOTE 20080502: 5 references
     * @param relationName String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file
     * @param byAndFields the fields that must equal in order to keep; may be null
     * @param orderBy The fields of the named entity to order the query by; may be null;
     *      optionally add a " ASC" for ascending or " DESC" for descending
     * @param value GenericValue instance containing the entity
     * @return List of GenericValue instances as specified in the relation definition
     */
    public List<GenericValue> getRelated(String relationName, Map<String, ? extends Object> byAndFields, List<String> orderBy, GenericValue value) throws GenericEntityException;

    /** Get the named Related Entity for the GenericValue from the persistent store
     * NOTE 20080502: 1 references; all changed to use getRelated
     * @param relationName String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file
     * @param byAndFields the fields that must equal in order to keep; may be null
     * @param value GenericValue instance containing the entity
     * @return List of GenericValue instances as specified in the relation definition
     *@deprecated Use getRelated() instead
     */
    @Deprecated
    public List<GenericValue> getRelatedByAnd(String relationName, Map<String, ? extends Object> byAndFields, GenericValue value) throws GenericEntityException;

    /** Get the named Related Entity for the GenericValue from the persistent store, checking first in the cache to see if the desired value is there
     * NOTE 20080502: 4 references
     * @param relationName String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file
     * @param value GenericValue instance containing the entity
     * @return List of GenericValue instances as specified in the relation definition
     */
    public List<GenericValue> getRelatedCache(String relationName, GenericValue value) throws GenericEntityException;

    /** Get a dummy primary key for the named Related Entity for the GenericValue
     * NOTE 20080502: 2 references
     * @param relationName String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file
     * @param byAndFields the fields that must equal in order to keep; may be null
     * @param value GenericValue instance containing the entity
     * @return GenericPK containing a possibly incomplete PrimaryKey object representing the related entity or entities
     */
    public GenericPK getRelatedDummyPK(String relationName, Map<String, ? extends Object> byAndFields, GenericValue value) throws GenericEntityException;

    /** Get related entity where relation is of type one, uses findByPrimaryKey
     * NOTE 20080502: 7 references
     * @throws IllegalArgumentException if the list found has more than one item
     */
    public GenericValue getRelatedOne(String relationName, GenericValue value) throws GenericEntityException;

    /** Get related entity where relation is of type one, uses findByPrimaryKey, checking first in the cache to see if the desired value is there
     * NOTE 20080502: 1 references
     * @throws IllegalArgumentException if the list found has more than one item
     */
    public GenericValue getRelatedOneCache(String relationName, GenericValue value) throws GenericEntityException;

    /** Get the named Related Entity for the GenericValue from the persistent store
     * NOTE 20080502: 1 references; all changed to use getRelated
     * @param relationName String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file
     * @param orderBy The fields of the named entity to order the query by; may be null;
     *      optionally add a " ASC" for ascending or " DESC" for descending
     * @param value GenericValue instance containing the entity
     * @return List of GenericValue instances as specified in the relation definition
     *@deprecated Use getRelated() instead
     */
    @Deprecated
    public List<GenericValue> getRelatedOrderBy(String relationName, List<String> orderBy, GenericValue value) throws GenericEntityException;

    public GenericPK makePK(Element element);

    /** Creates a Primary Key in the form of a GenericPK without persisting it */
    public GenericPK makePK(String entityName);

    /** Creates a Primary Key in the form of a GenericPK without persisting it */
    public GenericPK makePK(String entityName, Map<String, ? extends Object> fields);

    /** Creates a Primary Key in the form of a GenericPK without persisting it */
    public GenericPK makePK(String entityName, Object... fields);

    /** Creates a Primary Key in the form of a GenericPK without persisting it */
    public GenericPK makePKSingle(String entityName, Object singlePkValue);

    public GenericDelegator makeTestDelegator(String delegatorName);

    /** Creates a Entity in the form of a GenericValue without persisting it; only valid fields will be pulled from the fields Map */
    public GenericValue makeValidValue(String entityName, Map<String, ? extends Object> fields);

    /** Creates a Entity in the form of a GenericValue without persisting it; only valid fields will be pulled from the fields Map */
    public GenericValue makeValidValue(String entityName, Object... fields);

    public GenericValue makeValue(Element element);

    /** Creates a Entity in the form of a GenericValue without persisting it */
    public GenericValue makeValue(String entityName);

    /** Creates a Entity in the form of a GenericValue without persisting it */
    public GenericValue makeValue(String entityName, Map<String, ? extends Object> fields);

    /** Creates a Entity in the form of a GenericValue without persisting it */
    public GenericValue makeValue(String entityName, Object... fields);

    public List<GenericValue> makeValues(Document document);

    /** Creates a Entity in the form of a GenericValue without persisting it */
    public GenericValue makeValueSingle(String entityName, Object singlePkValue);

    public void putAllInPrimaryKeyCache(List<GenericValue> values);

    public void putInPrimaryKeyCache(GenericPK primaryKey, GenericValue value);

    // ======= XML Related Methods ========
    public List<GenericValue> readXmlDocument(URL url) throws SAXException, ParserConfigurationException, java.io.IOException;

    /** Refresh the Entity for the GenericValue from the persistent store
     *@param value GenericValue instance containing the entity to refresh
     */
    public void refresh(GenericValue value) throws GenericEntityException;

    /** Refresh the Entity for the GenericValue from the persistent store
     *@param value GenericValue instance containing the entity to refresh
     *@param doCacheClear boolean that specifies whether or not to automatically clear cache entries related to this operation
     */
    public void refresh(GenericValue value, boolean doCacheClear) throws GenericEntityException;

    /** Refresh the Entity for the GenericValue from the cache
     *@param value GenericValue instance containing the entity to refresh
     */
    public void refreshFromCache(GenericValue value) throws GenericEntityException;

    /** Refreshes the ID sequencer clearing all cached bank values. */
    public void refreshSequencer();

    /** Remove the Entities from the List from the persistent store.
     *  <br/>The List contains GenericEntity objects, can be either GenericPK or GenericValue.
     *  <br/>If a certain entity contains a complete primary key, the entity in the datasource corresponding
     *  to that primary key will be removed, this is like a removeByPrimary Key.
     *  <br/>On the other hand, if a certain entity is an incomplete or non primary key,
     *  if will behave like the removeByAnd method.
     *  <br/>These updates all happen in one transaction, so they will either all succeed or all fail,
     *  if the data source supports transactions.
     *@param dummyPKs Collection of GenericEntity instances containing the entities or by and fields to remove
     *@return int representing number of rows effected by this operation
     */
    public int removeAll(List<? extends GenericEntity> dummyPKs) throws GenericEntityException;

    /** Remove the Entities from the List from the persistent store.
     *  <br/>The List contains GenericEntity objects, can be either GenericPK or GenericValue.
     *  <br/>If a certain entity contains a complete primary key, the entity in the datasource corresponding
     *  to that primary key will be removed, this is like a removeByPrimary Key.
     *  <br/>On the other hand, if a certain entity is an incomplete or non primary key,
     *  if will behave like the removeByAnd method.
     *  <br/>These updates all happen in one transaction, so they will either all succeed or all fail,
     *  if the data source supports transactions.
     *@param dummyPKs Collection of GenericEntity instances containing the entities or by and fields to remove
     *@param doCacheClear boolean that specifies whether or not to automatically clear cache entries related to this operation
     *@return int representing number of rows effected by this operation
     */
    public int removeAll(List<? extends GenericEntity> dummyPKs, boolean doCacheClear) throws GenericEntityException;

    public int removeAll(String entityName) throws GenericEntityException;

    /** Removes/deletes Generic Entity records found by all of the specified fields (ie: combined using AND)
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param doCacheClear boolean that specifies whether to clear cache entries for this value to be removed
     *@param fields The fields of the named entity to query by with their corresponding values
     *@return int representing number of rows effected by this operation
     */
    public int removeByAnd(String entityName, boolean doCacheClear, Object... fields) throws GenericEntityException;

    /** Removes/deletes Generic Entity records found by all of the specified fields (ie: combined using AND)
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponding values
     *@return int representing number of rows effected by this operation
     */
    public int removeByAnd(String entityName, Map<String, ? extends Object> fields) throws GenericEntityException;

    /** Removes/deletes Generic Entity records found by all of the specified fields (ie: combined using AND)
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponding values
     *@param doCacheClear boolean that specifies whether to clear cache entries for this value to be removed
     *@return int representing number of rows effected by this operation
     */
    public int removeByAnd(String entityName, Map<String, ? extends Object> fields, boolean doCacheClear) throws GenericEntityException;

    /** Removes/deletes Generic Entity records found by all of the specified fields (ie: combined using AND)
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponding values
     *@return int representing number of rows effected by this operation
     */
    public int removeByAnd(String entityName, Object... fields) throws GenericEntityException;

    /** Removes/deletes Generic Entity records found by the condition
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param condition The condition used to restrict the removing
     *@return int representing number of rows effected by this operation
     */
    public int removeByCondition(String entityName, EntityCondition condition) throws GenericEntityException;

    /** Removes/deletes Generic Entity records found by the condition
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param condition The condition used to restrict the removing
     *@param doCacheClear boolean that specifies whether to clear cache entries for this value to be removed
     *@return int representing number of rows effected by this operation
     */
    public int removeByCondition(String entityName, EntityCondition condition, boolean doCacheClear) throws GenericEntityException;

    /** Remove a Generic Entity corresponding to the primaryKey
     *@param primaryKey  The primary key of the entity to remove.
     *@return int representing number of rows effected by this operation
     */
    public int removeByPrimaryKey(GenericPK primaryKey) throws GenericEntityException;

    /** Remove a Generic Entity corresponding to the primaryKey
     *@param primaryKey  The primary key of the entity to remove.
     *@param doCacheClear boolean that specifies whether to clear cache entries for this primaryKey to be removed
     *@return int representing number of rows effected by this operation
     */
    public int removeByPrimaryKey(GenericPK primaryKey, boolean doCacheClear) throws GenericEntityException;

    /** Remove the named Related Entity for the GenericValue from the persistent store
     *@param relationName String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file
     *@param value GenericValue instance containing the entity
     *@return int representing number of rows effected by this operation
     */
    public int removeRelated(String relationName, GenericValue value) throws GenericEntityException;

    /** Remove the named Related Entity for the GenericValue from the persistent store
     *@param relationName String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file
     *@param value GenericValue instance containing the entity
     *@param doCacheClear boolean that specifies whether to clear cache entries for this value to be removed
     *@return int representing number of rows effected by this operation
     */
    public int removeRelated(String relationName, GenericValue value, boolean doCacheClear) throws GenericEntityException;

    /** Remove a Generic Value from the database
     *@param value The GenericValue object of the entity to remove.
     *@return int representing number of rows effected by this operation
     */
    public int removeValue(GenericValue value) throws GenericEntityException;

    /** Remove a Generic Value from the database
     *@param value The GenericValue object of the entity to remove.
     *@param doCacheClear boolean that specifies whether to clear cache entries for this value to be removed
     *@return int representing number of rows effected by this operation
     */
    public int removeValue(GenericValue value, boolean doCacheClear) throws GenericEntityException;

    public void rollback();

    public void setDistributedCacheClear(DistributedCacheClear distributedCacheClear);

    public void setEntityCrypto(EntityCrypto crypto);

    public void setEntityEcaHandler(EntityEcaHandler<?> entityEcaHandler);

    /** Look at existing values for a sub-entity with a sequenced secondary ID, and get the highest plus 1 */
    public void setNextSubSeqId(GenericValue value, String seqFieldName, int numericPadding, int incrementBy);

    /** Allows you to pass a SequenceUtil class (possibly one that overrides the getNextSeqId method);
     * if null is passed will effectively refresh the sequencer. */
    public void setSequencer(SequenceUtil sequencer);

    public void setLocale(Locale locale);

    public void setSessionIdentifier(String identifier);

    public void setUserIdentifier(String identifier);

    /** Store the Entity from the GenericValue to the persistent store
     *@param value GenericValue instance containing the entity
     *@return int representing number of rows effected by this operation
     */
    public int store(GenericValue value) throws GenericEntityException;

    /** Store the Entity from the GenericValue to the persistent store
     *@param value GenericValue instance containing the entity
     *@param doCacheClear boolean that specifies whether or not to automatically clear cache entries related to this operation
     *@return int representing number of rows effected by this operation
     */
    public int store(GenericValue value, boolean doCacheClear) throws GenericEntityException;

    /** Store the Entities from the List GenericValue instances to the persistent store.
     *  <br/>This is different than the normal store method in that the store method only does
     *  an update, while the storeAll method checks to see if each entity exists, then
     *  either does an insert or an update as appropriate.
     *  <br/>These updates all happen in one transaction, so they will either all succeed or all fail,
     *  if the data source supports transactions. This is just like to othersToStore feature
     *  of the GenericEntity on a create or store.
     *@param values List of GenericValue instances containing the entities to store
     *@return int representing number of rows effected by this operation
     */
    public int storeAll(List<GenericValue> values) throws GenericEntityException;

    /** Store the Entities from the List GenericValue instances to the persistent store.
     *  <br/>This is different than the normal store method in that the store method only does
     *  an update, while the storeAll method checks to see if each entity exists, then
     *  either does an insert or an update as appropriate.
     *  <br/>These updates all happen in one transaction, so they will either all succeed or all fail,
     *  if the data source supports transactions. This is just like to othersToStore feature
     *  of the GenericEntity on a create or store.
     *@param values List of GenericValue instances containing the entities to store
     *@param doCacheClear boolean that specifies whether or not to automatically clear cache entries related to this operation
     *@return int representing number of rows effected by this operation
     */
    public int storeAll(List<GenericValue> values, boolean doCacheClear) throws GenericEntityException;

    /** Store the Entities from the List GenericValue instances to the persistent store.
     *  <br/>This is different than the normal store method in that the store method only does
     *  an update, while the storeAll method checks to see if each entity exists, then
     *  either does an insert or an update as appropriate.
     *  <br/>These updates all happen in one transaction, so they will either all succeed or all fail,
     *  if the data source supports transactions. This is just like to othersToStore feature
     *  of the GenericEntity on a create or store.
     *@param values List of GenericValue instances containing the entities to store
     *@param doCacheClear boolean that specifies whether or not to automatically clear cache entries related to this operation
     *@param createDummyFks boolean that specifies whether or not to automatically create "dummy" place holder FKs
     *@return int representing number of rows effected by this operation
     */
    public int storeAll(List<GenericValue> values, boolean doCacheClear, boolean createDummyFks) throws GenericEntityException;

    /** Store a group of values
     *@param entityName The name of the Entity as defined in the entity XML file
     *@param fieldsToSet The fields of the named entity to set in the database
     *@param condition The condition that restricts the list of stored values
     *@return int representing number of rows effected by this operation
     *@throws GenericEntityException
     */
    public int storeByCondition(String entityName, Map<String, ? extends Object> fieldsToSet, EntityCondition condition) throws GenericEntityException;

    /** Store a group of values
     *@param entityName The name of the Entity as defined in the entity XML file
     *@param fieldsToSet The fields of the named entity to set in the database
     *@param condition The condition that restricts the list of stored values
     *@param doCacheClear boolean that specifies whether to clear cache entries for these values
     *@return int representing number of rows effected by this operation
     *@throws GenericEntityException
     */
    public int storeByCondition(String entityName, Map<String, ? extends Object> fieldsToSet, EntityCondition condition, boolean doCacheClear) throws GenericEntityException;

}
