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

import java.util.*;
import java.net.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.model.*;
import org.ofbiz.core.entity.config.*;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Generic Data Source Delegator Class
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     <a href="mailto:chris_maurer@altavista.com">Chris Maurer</a>
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    Sep 17 2001
 *@version    1.0
 */
public class GenericDelegator {

    public static final String module = GenericDelegator.class.getName();

    /** the delegatorCache will now be a HashMap, allowing reload of definitions,
     * but the delegator will always be the same object for the given name */
    protected static Map delegatorCache = new HashMap();
    protected String delegatorName;

    /** set this to true for better performance; set to false to be able to reload definitions at runtime throught the cache manager */
    public static final boolean keepLocalReaders = true;
    ModelReader modelReader = null;
    ModelGroupReader modelGroupReader = null;

    UtilCache primaryKeyCache = null;
    UtilCache allCache = null;
    UtilCache andCache = null;

    SequenceUtil sequencer = null;

    public static GenericDelegator getGenericDelegator(String delegatorName) {
        GenericDelegator delegator = (GenericDelegator) delegatorCache.get(delegatorName);
        if (delegator == null)//don't want to block here
        {
            synchronized (GenericDelegator.class) {
                //must check if null again as one of the blocked threads can still enter
                delegator = (GenericDelegator) delegatorCache.get(delegatorName);
                if (delegator == null) {
                    try {
                        delegator = new GenericDelegator(delegatorName);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Error creating delegator");
                    }
                    if (delegator != null) {
                        delegatorCache.put(delegatorName, delegator);
                    }
                }
            }
        }
        return delegator;
    }

    /** Only allow creation through the factory method */
    protected GenericDelegator() {}
    
    /** Only allow creation through the factory method */
    protected GenericDelegator(String delegatorName) throws GenericEntityException {
        Debug.logInfo("Creating new Delegator with name \"" + delegatorName + "\".", module);

        this.delegatorName = delegatorName;
        if (keepLocalReaders) {
            modelReader = ModelReader.getModelReader(delegatorName);
            modelGroupReader = ModelGroupReader.getModelGroupReader(delegatorName);
        }

        primaryKeyCache = new UtilCache("FindByPrimaryKey-" + delegatorName);
        allCache = new UtilCache("FindAll-" + delegatorName);
        andCache = new UtilCache("FindByAnd-" + delegatorName);

        //initialize helpers by group
        Iterator groups = UtilMisc.toIterator(getModelGroupReader().getGroupNames());
        while (groups != null && groups.hasNext()) {
            String groupName = (String) groups.next();
            String helperName = this.getGroupHelperName(groupName);
            Debug.logInfo("Delegator \"" + delegatorName + "\" initializing helper \"" + 
                    helperName + "\" for entity group \"" + groupName + "\".", module);
            TreeSet helpersDone = new TreeSet();
            if (helperName != null && helperName.length() > 0) {
                //make sure each helper is only loaded once
                if (helpersDone.contains(helperName)) {
                    Debug.logInfo("Helper \"" + helperName + "\" already initialized, not re-initializing.", module);
                    continue;
                }
                helpersDone.add(helperName);
                //pre-load field type defs, the return value is ignored
                ModelFieldTypeReader.getModelFieldTypeReader(helperName);
                //get the helper and if configured, do the datasource check
                GenericHelper helper = GenericHelperFactory.getHelper(helperName);
                
                Element rootElement = EntityConfigUtil.getXmlRootElement();
                Element datasourceElement = UtilXml.firstChildElement(rootElement, "datasource", "name", helperName);
                boolean checkOnStart = true;
                boolean addMissing = false;
                if (datasourceElement == null) {
                    Debug.logWarning("datasource def not found with name " + helperName + ", using defaults for check-on-start (true) and add-missing-on-start (false)");
                } else {
                    checkOnStart = !"false".equals(datasourceElement.getAttribute("check-on-start"));
                    addMissing = "true".equals(datasourceElement.getAttribute("add-missing-on-start"));
                }

                if (checkOnStart) {
                    Debug.logInfo("Doing database check as requested in entityengine.xml with addMissing=" + addMissing, module);
                    try {
                        helper.checkDataSource(this.getModelEntityMapByGroup(groupName), null, addMissing);
                    } catch (GenericEntityException e) {
                        Debug.logWarning(e.getMessage(), module);
                    }
                }
            }
        }
    }

    /** Gets the name of the server configuration that corresponds to this delegator
     * @return server configuration name
     */
    public String getDelegatorName() {
        return this.delegatorName;
    }

    /** Gets the instance of ModelReader that corresponds to this delegator
     *@return ModelReader that corresponds to this delegator
     */
    public ModelReader getModelReader() {
        if (keepLocalReaders) {
            return this.modelReader;
        } else {
            try {
                return ModelReader.getModelReader(delegatorName);
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
                return null;
            }
        }
    }

    /** Gets the instance of ModelGroupReader that corresponds to this delegator
     *@return ModelGroupReader that corresponds to this delegator
     */
    public ModelGroupReader getModelGroupReader() {
        if (keepLocalReaders)
            return this.modelGroupReader;
        else
            return ModelGroupReader.getModelGroupReader(delegatorName);
    }

    /** Gets the instance of ModelEntity that corresponds to this delegator and the specified entityName
     *@param entityName The name of the entity to get
     *@return ModelEntity that corresponds to this delegator and the specified entityName
     */
    public ModelEntity getModelEntity(String entityName) {
        try {
            return getModelReader().getModelEntity(entityName);
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
            return null;
        }
    }

    /** Gets the helper name that corresponds to this delegator and the specified entityName
     *@param entityName The name of the entity to get the helper for
     *@return String with the helper name that corresponds to this delegator and the specified entityName
     */
    public String getEntityGroupName(String entityName) {
        String groupName = getModelGroupReader().getEntityGroupName(entityName);
        return groupName;
    }

    /** Gets a collection of entity models that are in a group corresponding to the specified group name
     *@param groupName The name of the group
     *@return Collection of ModelEntity instances
     */
    public Collection getModelEntitiesByGroup(String groupName) {
        Iterator enames = UtilMisc.toIterator(getModelGroupReader().getEntityNamesByGroup(groupName));
        Collection entities = new LinkedList();
        if (enames == null || !enames.hasNext())
            return entities;
        while (enames.hasNext()) {
            String ename = (String) enames.next();
            ModelEntity entity = this.getModelEntity(ename);
            if (entity != null)
                entities.add(entity);
        }
        return entities;
    }

    /** Gets a Map of entity name & entity model pairs that are in the named group
     *@param groupName The name of the group
     *@return Map of entityName String keys and ModelEntity instance values
     */
    public Map getModelEntityMapByGroup(String groupName) {
        Iterator enames = UtilMisc.toIterator(getModelGroupReader().getEntityNamesByGroup(groupName));
        Map entities = new HashMap();
        if (enames == null || !enames.hasNext())
            return entities;
        while (enames.hasNext()) {
            String ename = (String) enames.next();
            ModelEntity entity = this.getModelEntity(ename);
            if (entity != null)
                entities.put(entity.getEntityName(), entity);
        }
        return entities;
    }

    /** Gets the helper name that corresponds to this delegator and the specified entityName
     *@param groupName The name of the group to get the helper name for
     *@return String with the helper name that corresponds to this delegator and the specified entityName
     */
    public String getGroupHelperName(String groupName) {
        Element rootElement = null;
        try {
            rootElement = EntityConfigUtil.getXmlRootElement();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error getting group helper name");
            return null;
        }

        Element delegatorElement = UtilXml.firstChildElement(rootElement, "delegator", "name", delegatorName);
        if (delegatorElement == null) {
            Debug.logWarning("Delegator def not found with name " + delegatorName);
            return null;
        }
        Element goupMapElement = UtilXml.firstChildElement(delegatorElement, "group-map", "group-name", groupName);
        if (goupMapElement == null) {
            Debug.logWarning("No entity group definition found with name " + groupName + " in delegator with name " + delegatorName);
            return null;
        }

        return goupMapElement.getAttribute("datasource-name");
    }

    /** Gets the helper name that corresponds to this delegator and the specified entityName
     *@param entityName The name of the entity to get the helper name for
     *@return String with the helper name that corresponds to this delegator and the specified entityName
     */
    public String getEntityHelperName(String entityName) {
        String groupName = getModelGroupReader().getEntityGroupName(entityName);
        return this.getGroupHelperName(groupName);
    }

    /** Gets the helper name that corresponds to this delegator and the specified entity
     *@param entity The entity to get the helper for
     *@return String with the helper name that corresponds to this delegator and the specified entity
     */
    public String getEntityHelperName(ModelEntity entity) {
        if (entity == null)
            return null;
        return getEntityHelperName(entity.getEntityName());
    }

    /** Gets the an instance of helper that corresponds to this delegator and the specified entityName
     *@param entityName The name of the entity to get the helper for
     *@return GenericHelper that corresponds to this delegator and the specified entityName
     */
    public GenericHelper getEntityHelper(String entityName) throws GenericEntityException {
        String helperName = getEntityHelperName(entityName);
        if (helperName != null && helperName.length() > 0)
            return GenericHelperFactory.getHelper(helperName);
        else
            throw new GenericEntityException("Helper name not found for entity " + entityName);
    }

    /** Gets the an instance of helper that corresponds to this delegator and the specified entity
     *@param entity The entity to get the helper for
     *@return GenericHelper that corresponds to this delegator and the specified entity
     */
    public GenericHelper getEntityHelper(ModelEntity entity) throws GenericEntityException {
        return getEntityHelper(entity.getEntityName());
    }

    /** Gets a field type instance by name from the helper that corresponds to the specified entity
     *@param entity The entity
     *@param type The name of the type
     *@return ModelFieldType instance for the named type from the helper that corresponds to the specified entity
     */
    public ModelFieldType getEntityFieldType(ModelEntity entity, String type) throws GenericEntityException {
        String helperName = getEntityHelperName(entity);
        if (helperName == null || helperName.length() <= 0)
            return null;
        ModelFieldTypeReader modelFieldTypeReader = ModelFieldTypeReader.getModelFieldTypeReader(helperName);
        if (modelFieldTypeReader == null)
            throw new GenericEntityException("ModelFieldTypeReader not found for entity " + entity.getEntityName() + " with helper name " + helperName);
        return modelFieldTypeReader.getModelFieldType(type);
    }

    /** Gets field type names from the helper that corresponds to the specified entity
     *@param entity The entity
     *@return Collection of field type names from the helper that corresponds to the specified entity
     */
    public Collection getEntityFieldTypeNames(ModelEntity entity) throws GenericEntityException {
        String helperName = getEntityHelperName(entity);
        if (helperName == null || helperName.length() <= 0)
            return null;
        ModelFieldTypeReader modelFieldTypeReader = ModelFieldTypeReader.getModelFieldTypeReader(helperName);
        if (modelFieldTypeReader == null)
            throw new GenericEntityException("ModelFieldTypeReader not found for entity " + entity.getEntityName() + " with helper name " + helperName);
        return modelFieldTypeReader.getFieldTypeNames();
    }

    /** Creates a Entity in the form of a GenericValue without persisting it */
    public GenericValue makeValue(String entityName, Map fields) {
        ModelEntity entity = null;
        try {
            entity = getModelReader().getModelEntity(entityName);
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
        if (entity == null)
            throw new IllegalArgumentException("[GenericDelegator.makeValue] could not find entity for entityName: " + entityName);
        GenericValue value = new GenericValue(entity, fields);
        value.setDelegator(this);
        return value;
    }

    /** Creates a Primary Key in the form of a GenericPK without persisting it */
    public GenericPK makePK(String entityName, Map fields) {
        ModelEntity entity = null;
        try {
            entity = getModelReader().getModelEntity(entityName);
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
        if (entity == null)
            throw new IllegalArgumentException("[GenericDelegator.makePK] could not find entity for entityName: " + entityName);
        GenericPK pk = new GenericPK(entity, fields);
        pk.setDelegator(this);
        return pk;
    }

    /** Creates a Entity in the form of a GenericValue and write it to the database
     *@return GenericValue instance containing the new instance
     */
    public GenericValue create(String entityName, Map fields) throws GenericEntityException {
        if (entityName == null || fields == null) {
            return null;
        }
        ModelEntity entity = null;
        try {
            entity = getModelReader().getModelEntity(entityName);
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
        GenericValue genericValue = new GenericValue(entity, fields);
        return this.create(genericValue);
    }


    /** Creates a Entity in the form of a GenericValue and write it to the database
     * @return GenericValue instance containing the new instance
     */
    public GenericValue create(GenericValue value) throws GenericEntityException {
        GenericHelper helper = getEntityHelper(value.getModelEntity());
        value = helper.create(value);
        if (value != null)
            value.setDelegator(this);
        if (value.lockEnabled())
            refresh(value);
        return value;
    }

    /** Creates a Entity in the form of a GenericValue and write it to the database
     * @return GenericValue instance containing the new instance
     */
    public GenericValue create(GenericPK primaryKey) throws GenericEntityException {
        GenericHelper helper = getEntityHelper(primaryKey.getModelEntity());
        GenericValue value = helper.create(primaryKey);
        if (value != null)
            value.setDelegator(this);
        return value;
    }

    /** Find a Generic Entity by its Primary Key
     * @param primaryKey The primary key to find by.
     * @return The GenericValue corresponding to the primaryKey
     */
    public GenericValue findByPrimaryKey(GenericPK primaryKey) throws GenericEntityException {
        GenericHelper helper = getEntityHelper(primaryKey.getEntityName());
        GenericValue value = null;
        if (!primaryKey.isPrimaryKey())
            throw new IllegalArgumentException("[GenericDelegator.findByPrimaryKey] Passed primary key is not a valid primary key: " + primaryKey);
        try {
            value = helper.findByPrimaryKey(primaryKey);
        } catch (GenericEntityNotFoundException e) {
            value = null;
        }
        if (value != null)
            value.setDelegator(this);
        return value;
    }

    /** Find a CACHED Generic Entity by its Primary Key
     *@param primaryKey The primary key to find by.
     *@return The GenericValue corresponding to the primaryKey
     */
    public GenericValue findByPrimaryKeyCache(GenericPK primaryKey) throws GenericEntityException {
        GenericValue value = this.getFromPrimaryKeyCache(primaryKey);
        if (value == null) {
            value = findByPrimaryKey(primaryKey);
            if (value != null)
                this.putInPrimaryKeyCache(primaryKey, value);
        }
        return value;
    }

    /** Find a Generic Entity by its Primary Key
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponging values
     *@return The GenericValue corresponding to the primaryKey
     */
    public GenericValue findByPrimaryKey(String entityName, Map fields) throws GenericEntityException {
        return findByPrimaryKey(makePK(entityName, fields));
    }

    /** Find a CACHED Generic Entity by its Primary Key
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponging values
     *@return The GenericValue corresponding to the primaryKey
     */
    public GenericValue findByPrimaryKeyCache(String entityName, Map fields) throws GenericEntityException {
        return findByPrimaryKeyCache(makePK(entityName, fields));
    }

    /** Find a Generic Entity by its Primary Key and only returns the values requested by the passed keys (names)
     *@param primaryKey The primary key to find by.
     *@param keys The keys, or names, of the values to retrieve; only these values will be retrieved
     *@return The GenericValue corresponding to the primaryKey
     */
    public GenericValue findByPrimaryKeyPartial(GenericPK primaryKey, Set keys) throws GenericEntityException {
        GenericHelper helper = getEntityHelper(primaryKey.getEntityName());
        GenericValue value = null;
        if (!primaryKey.isPrimaryKey())
            throw new IllegalArgumentException("[GenericDelegator.findByPrimaryKey] Passed primary key is not a valid primary key: " + primaryKey);
        try {
            value = helper.findByPrimaryKeyPartial(primaryKey, keys);
        } catch (GenericEntityNotFoundException e) {
            value = null;
        }
        if (value != null)
            value.setDelegator(this);
        return value;
    }

    /** Find a number of Generic Value objects by their Primary Keys, all at once
     *@param primaryKeys A Collection of primary keys to find by.
     *@return Collection of GenericValue objects corresponding to the passed primaryKey objects
     */
    public Collection findAllByPrimaryKeys(Collection primaryKeys) throws GenericEntityException {
        if (primaryKeys == null)
            return null;
        Collection results = new LinkedList();

        //from the delegator level this is complicated because different GenericPK
        // objects in the collection may correspond to different helpers
        HashMap pksPerHelper = new HashMap();
        Iterator pkiter = primaryKeys.iterator();
        while (pkiter.hasNext()) {
            GenericPK curPK = (GenericPK) pkiter.next();
            String helperName = this.getEntityHelperName(curPK.getEntityName());
            Collection pks = (Collection) pksPerHelper.get(helperName);
            if (pks == null) {
                pks = new LinkedList();
                pksPerHelper.put(helperName, pks);
            }
            pks.add(curPK);
        }

        Iterator helperIter = pksPerHelper.entrySet().iterator();
        while (helperIter.hasNext()) {
            Map.Entry curEntry = (Map.Entry) helperIter.next();
            String helperName = (String) curEntry.getKey();
            GenericHelper helper = GenericHelperFactory.getHelper(helperName);
            Collection values = helper.findAllByPrimaryKeys((Collection) curEntry.getValue());
            results.addAll(values);
        }
        return results;
    }

    /** Find a number of Generic Value objects by their Primary Keys, all at once;
     *  this first looks in the local cache for each PK and if there then it puts it
     *  in the return collection rather than putting it in the batch to send to
     *  a given helper.
     *@param primaryKeys A Collection of primary keys to find by.
     *@return Collection of GenericValue objects corresponding to the passed primaryKey objects
     */
    public Collection findAllByPrimaryKeysCache(Collection primaryKeys) throws GenericEntityException {
        if (primaryKeys == null)
            return null;
        Collection results = new LinkedList();

        //from the delegator level this is complicated because different GenericPK
        // objects in the collection may correspond to different helpers
        HashMap pksPerHelper = new HashMap();
        Iterator pkiter = primaryKeys.iterator();
        while (pkiter.hasNext()) {
            GenericPK curPK = (GenericPK) pkiter.next();

            GenericValue value = this.getFromPrimaryKeyCache(curPK);
            if (value != null) {
                //it is in the cache, so just put the cached value in the results
                results.add(value);
            } else {
                //is not in the cache, so put in a collection for a call to the helper
                String helperName = this.getEntityHelperName(curPK.getEntityName());
                Collection pks = (Collection) pksPerHelper.get(helperName);
                if (pks == null) {
                    pks = new LinkedList();
                    pksPerHelper.put(helperName, pks);
                }
                pks.add(curPK);
            }
        }

        Iterator helperIter = pksPerHelper.entrySet().iterator();
        while (helperIter.hasNext()) {
            Map.Entry curEntry = (Map.Entry) helperIter.next();
            String helperName = (String) curEntry.getKey();
            GenericHelper helper = GenericHelperFactory.getHelper(helperName);
            Collection values = helper.findAllByPrimaryKeys((Collection) curEntry.getValue());
            this.putAllInPrimaryKeyCache(values);
            results.addAll(values);
        }
        return results;
    }

    /** Remove a Generic Entity corresponding to the primaryKey
     * @param  primaryKey  The primary key of the entity to remove.
     */
    public void removeByPrimaryKey(GenericPK primaryKey) throws GenericEntityException {
        GenericHelper helper = getEntityHelper(primaryKey.getModelEntity());
        this.clearCacheLine(primaryKey);
        helper.removeByPrimaryKey(primaryKey);
    }

    /** Finds all Generic entities
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@return    Collection containing all Generic entities
     */
    public Collection findAll(String entityName) throws GenericEntityException {
        return this.findByAnd(entityName, new HashMap(), null);
    }

    /** Finds all Generic entities
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return    Collection containing all Generic entities
     */
    public Collection findAll(String entityName, List orderBy) throws GenericEntityException {
        return this.findByAnd(entityName, new HashMap(), orderBy);
    }

    /** Finds all Generic entities, looking first in the cache
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@return    Collection containing all Generic entities
     */
    public Collection findAllCache(String entityName) throws GenericEntityException {
        return this.findAllCache(entityName, null);
    }

    /** Finds all Generic entities, looking first in the cache; uses orderBy for lookup, but only keys results on the entityName and fields
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return    Collection containing all Generic entities
     */
    public Collection findAllCache(String entityName, List orderBy) throws GenericEntityException {
        Collection col = this.getFromAllCache(entityName);
        if (col == null) {
            col = findAll(entityName, orderBy);
            if (col != null)
                this.putInAllCache(entityName, col);
        }
        return col;
    }

    /** Finds Generic Entity records by all of the specified fields (ie: combined using AND)
     * @param entityName The Name of the Entity as defined in the entity XML file
     * @param fields The fields of the named entity to query by with their corresponging values
     * @return Collection of GenericValue instances that match the query
     */
    public Collection findByAnd(String entityName, Map fields) throws GenericEntityException {
        return this.findByAnd(entityName, fields, null);
    }

    /** Finds Generic Entity records by all of the specified fields (ie: combined using OR)
     * @param entityName The Name of the Entity as defined in the entity XML file
     * @param fields The fields of the named entity to query by with their corresponging values
     * @return Collection of GenericValue instances that match the query
     */
    public Collection findByOr(String entityName, Map fields) throws GenericEntityException {
        return this.findByOr(entityName, fields, null);
    }

    /** Finds Generic Entity records by all of the specified fields (ie: combined using AND)
     * @param entityName The Name of the Entity as defined in the entity XML file
     * @param fields The fields of the named entity to query by with their corresponging values
     * @param orderBy The fields of the named entity to order the query by;
     *      optionally add a " ASC" for ascending or " DESC" for descending
     * @return Collection of GenericValue instances that match the query
     */
    public Collection findByAnd(String entityName, Map fields, List orderBy) throws GenericEntityException {
        ModelEntity modelEntity = getModelReader().getModelEntity(entityName);
        if (modelEntity == null) {
            throw new GenericModelException("Could not find definition for entity name " + entityName);
        }
        GenericHelper helper = getEntityHelper(modelEntity);

        if (fields != null && !modelEntity.areFields(fields.keySet()))
            throw new GenericModelException("At least one of the passed fields is not valid: " + fields.keySet().toString());

        Collection collection = null;
        collection = helper.findByAnd(modelEntity, fields, orderBy);
        absorbCollection(collection);
        return collection;
    }

    /** Finds Generic Entity records by all of the specified fields (ie: combined using OR)
     * @param entityName The Name of the Entity as defined in the entity XML file
     * @param fields The fields of the named entity to query by with their corresponging values
     * @param order The fields of the named entity to order the query by;
     *      optionally add a " ASC" for ascending or " DESC" for descending
     * @return Collection of GenericValue instances that match the query
     */
    public Collection findByOr(String entityName, Map fields, List orderBy) throws GenericEntityException {
        ModelEntity modelEntity = getModelReader().getModelEntity(entityName);
        GenericHelper helper = getEntityHelper(modelEntity);

        if (fields != null && !modelEntity.areFields(fields.keySet()))
            throw new IllegalArgumentException("[GenericDelegator.findByOr] At least of the passed fields is not valid: " + fields.keySet().toString());

        Collection collection = null;
        collection = helper.findByOr(modelEntity, fields, orderBy);
        absorbCollection(collection);
        return collection;
    }

    /** Finds Generic Entity records by all of the specified fields (ie: combined using AND), looking first in the cache; uses orderBy for lookup, but only keys results on the entityName and fields
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponging values
     *@return Collection of GenericValue instances that match the query
     */
    public Collection findByAndCache(String entityName, Map fields) throws GenericEntityException {
        return this.findByAndCache(entityName, fields, null);
    }

    /** Finds Generic Entity records by all of the specified fields (ie: combined using AND), looking first in the cache; uses orderBy for lookup, but only keys results on the entityName and fields
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponging values
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return Collection of GenericValue instances that match the query
     */
    public Collection findByAndCache(String entityName, Map fields, List orderBy) throws GenericEntityException {
        Collection col = this.getFromAndCache(entityName, fields);
        if (col == null) {
            col = findByAnd(entityName, fields, orderBy);
            if (col != null)
                this.putInAndCache(entityName, fields, col);
        }
        return col;
    }

    /** Finds Generic Entity records by all of the specified expressions (ie: combined using AND)
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param expressions The expressions to use for the lookup, each consisting of at least a field name, an EntityOperator, and a value to compare to
     *@return Collection of GenericValue instances that match the query
     */
    public Collection findByAnd(String entityName, List expressions) throws GenericEntityException {
        return findByAnd(entityName, expressions, null);
    }

    /** Finds Generic Entity records by all of the specified expressions (ie: combined using OR)
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param expressions The expressions to use for the lookup, each consisting of at least a field name, an EntityOperator, and a value to compare to
     *@return Collection of GenericValue instances that match the query
     */
    public Collection findByOr(String entityName, List expressions) throws GenericEntityException {
        return findByOr(entityName, expressions, null);
    }

    /** Finds Generic Entity records by all of the specified expressions (ie: combined using AND)
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param expressions The expressions to use for the lookup, each consisting of at least a field name, an EntityOperator, and a value to compare to
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return Collection of GenericValue instances that match the query
     */
    public Collection findByAnd(String entityName, List expressions, List orderBy) throws GenericEntityException {
        ModelEntity modelEntity = getModelReader().getModelEntity(entityName);
        GenericHelper helper = getEntityHelper(modelEntity);

        Collection collection = null;
        collection = helper.findByAnd(modelEntity, expressions, orderBy);
        absorbCollection(collection);
        return collection;
    }

    /** Finds Generic Entity records by all of the specified expressions (ie: combined using OR)
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param expressions The expressions to use for the lookup, each consisting of at least a field name, an EntityOperator, and a value to compare to
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return Collection of GenericValue instances that match the query
     */
    public Collection findByOr(String entityName, List expressions, List orderBy) throws GenericEntityException {
        ModelEntity modelEntity = getModelReader().getModelEntity(entityName);
        GenericHelper helper = getEntityHelper(modelEntity);

        Collection collection = null;
        collection = helper.findByOr(modelEntity, expressions, orderBy);
        absorbCollection(collection);
        return collection;
    }

    public Collection findByLike(String entityName, Map fields) throws GenericEntityException {
        return findByLike(entityName, fields, null);
    }

    public Collection findByLike(String entityName, Map fields, List orderBy) throws GenericEntityException {
        ModelEntity modelEntity = getModelReader().getModelEntity(entityName);
        GenericHelper helper = getEntityHelper(modelEntity);

        Collection collection = null;
        collection = helper.findByLike(modelEntity, fields, orderBy);
        absorbCollection(collection);
        return collection;
    }

    public Collection findByClause(String entityName, List entityClauses, Map fields) throws GenericEntityException {
        return findByClause(entityName, entityClauses, fields, null);
    }

    public Collection findByClause(String entityName, List entityClauses, Map fields, List orderBy) throws GenericEntityException {
        if (entityClauses == null)
            return null;
        ModelEntity modelEntity = getModelReader().getModelEntity(entityName);
        GenericHelper helper = getEntityHelper(modelEntity);

        for (int i = 0; i < entityClauses.size(); i++) {
            EntityClause genEntityClause = (EntityClause) entityClauses.get(i);
            genEntityClause.setModelEntities(getModelReader());
        }
        Collection collection = null;
        collection = helper.findByClause(modelEntity, entityClauses, fields, orderBy);
        absorbCollection(collection);
        return collection;
    }

    /** Removes/deletes Generic Entity records found by all of the specified fields (ie: combined using AND)
     * @param entityName The Name of the Entity as defined in the entity XML file
     * @param fields The fields of the named entity to query by with their corresponging values
     */
    public void removeByAnd(String entityName, Map fields) throws GenericEntityException {
        this.clearCacheLine(entityName, fields);
        ModelEntity modelEntity = getModelReader().getModelEntity(entityName);
        GenericHelper helper = getEntityHelper(modelEntity);
        helper.removeByAnd(modelEntity, fields);
    }

    /** Get the named Related Entity for the GenericValue from the persistent store
     * @param relationName String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file
     * @param value GenericValue instance containing the entity
     * @return Collection of GenericValue instances as specified in the relation definition
     */
    public Collection getRelated(String relationName, GenericValue value) throws GenericEntityException {
        return getRelated(relationName, null, null, value);
    }

    /** Get the named Related Entity for the GenericValue from the persistent store
     * @param relationName String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file
     * @param byAndFields the fields that must equal in order to keep; may be null
     * @param value GenericValue instance containing the entity
     * @return Collection of GenericValue instances as specified in the relation definition
     */
    public Collection getRelatedByAnd(String relationName, Map byAndFields, GenericValue value) throws GenericEntityException {
        return this.getRelated(relationName, byAndFields, null, value);
    }

    /** Get the named Related Entity for the GenericValue from the persistent store
     * @param relationName String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file
     * @param orderBy The fields of the named entity to order the query by; may be null;
     *      optionally add a " ASC" for ascending or " DESC" for descending
     * @param value GenericValue instance containing the entity
     * @return Collection of GenericValue instances as specified in the relation definition
     */
    public Collection getRelatedOrderBy(String relationName, List orderBy, GenericValue value) throws GenericEntityException {
        return this.getRelated(relationName, null, orderBy, value);
    }

    /** Get the named Related Entity for the GenericValue from the persistent store
     * @param relationName String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file
     * @param byAndFields the fields that must equal in order to keep; may be null
     * @param orderBy The fields of the named entity to order the query by; may be null;
     *      optionally add a " ASC" for ascending or " DESC" for descending
     * @param value GenericValue instance containing the entity
     * @return Collection of GenericValue instances as specified in the relation definition
     */
    public Collection getRelated(String relationName, Map byAndFields, List orderBy, GenericValue value) throws GenericEntityException {
        ModelEntity modelEntity = value.getModelEntity();
        ModelRelation relation = modelEntity.getRelation(relationName);
        if (relation == null)
            throw new GenericModelException("Could not find relation for relationName: " + relationName + " for value " + value);

        //put the byAndFields (if not null) into the hash map first,
        //they will be overridden by value's fields if over-specified this is important for security and cleanliness
        Map fields = byAndFields == null ? new HashMap() : new HashMap(byAndFields);
        for (int i = 0; i < relation.getKeyMapsSize(); i++) {
            ModelKeyMap keyMap = relation.getKeyMap(i);
            fields.put(keyMap.getRelFieldName(), value.get(keyMap.getFieldName()));
        }

        return this.findByAnd(relation.getRelEntityName(), fields, orderBy);
    }

    /** Get a dummy primary key for the named Related Entity for the GenericValue
     * @param relationName String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file
     * @param byAndFields the fields that must equal in order to keep; may be null
     * @param value GenericValue instance containing the entity
     * @return GenericPK containing a possibly incomplete PrimaryKey object representing the related entity or entities
     */
    public GenericPK getRelatedDummyPK(String relationName, Map byAndFields, GenericValue value) throws GenericEntityException {
        ModelEntity modelEntity = value.getModelEntity();
        ModelRelation relation = modelEntity.getRelation(relationName);
        if (relation == null)
            throw new GenericModelException("Could not find relation for relationName: " + relationName + " for value " + value);
        ModelEntity relatedEntity = getModelReader().getModelEntity(relation.getRelEntityName());

        //put the byAndFields (if not null) into the hash map first,
        //they will be overridden by value's fields if over-specified this is important for security and cleanliness
        Map fields = byAndFields == null ? new HashMap() : new HashMap(byAndFields);
        for (int i = 0; i < relation.getKeyMapsSize(); i++) {
            ModelKeyMap keyMap = relation.getKeyMap(i);
            fields.put(keyMap.getRelFieldName(), value.get(keyMap.getFieldName()));
        }

        GenericPK dummyPK = new GenericPK(relatedEntity, fields);
        dummyPK.setDelegator(this);
        return dummyPK;
    }

    /** Get the named Related Entity for the GenericValue from the persistent store, checking first in the cache to see if the desired value is there
     * @param relationName String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file
     * @param value GenericValue instance containing the entity
     * @return Collection of GenericValue instances as specified in the relation definition
     */
    public Collection getRelatedCache(String relationName, GenericValue value) throws GenericEntityException {
        ModelEntity modelEntity = value.getModelEntity();
        ModelRelation relation = modelEntity.getRelation(relationName);
        if (relation == null)
            throw new GenericModelException("Could not find relation for relationName: " + relationName + " for value " + value);

        Map fields = new HashMap();
        for (int i = 0; i < relation.getKeyMapsSize(); i++) {
            ModelKeyMap keyMap = relation.getKeyMap(i);
            fields.put(keyMap.getRelFieldName(), value.get(keyMap.getFieldName()));
        }

        return this.findByAndCache(relation.getRelEntityName(), fields, null);
    }

    /** Get related entity where relation is of type one, uses findByPrimaryKey
     * @throws IllegalArgumentException if the collection found has more than one item
     */
    public GenericValue getRelatedOne(String relationName, GenericValue value) throws GenericEntityException {
        ModelEntity modelEntity = value.getModelEntity();
        ModelRelation relation = value.getModelEntity().getRelation(relationName);
        if (relation == null) {
            throw new GenericModelException("Could not find relation for relationName: " + relationName + " for value " + value);
        }
        if (!"one".equals(relation.getType()) && !"one-nofk".equals(relation.getType())) {
            throw new IllegalArgumentException("Relation is not a 'one' or a 'one-nofk' relation: " + relationName + " of entity " + value.getEntityName());
        }

        Map fields = new HashMap();
        for (int i = 0; i < relation.getKeyMapsSize(); i++) {
            ModelKeyMap keyMap = relation.getKeyMap(i);
            fields.put(keyMap.getRelFieldName(), value.get(keyMap.getFieldName()));
        }

        return this.findByPrimaryKey(relation.getRelEntityName(), fields);
    }

    /** Get related entity where relation is of type one, uses findByPrimaryKey, checking first in the cache to see if the desired value is there
     * @throws IllegalArgumentException if the collection found has more than one item
     */
    public GenericValue getRelatedOneCache(String relationName, GenericValue value) throws GenericEntityException {
        ModelEntity modelEntity = value.getModelEntity();
        ModelRelation relation = modelEntity.getRelation(relationName);
        if (relation == null) {
            throw new GenericModelException("Could not find relation for relationName: " + relationName + " for value " + value);
        }
        if (!"one".equals(relation.getType()) && !"one-nofk".equals(relation.getType())) {
            throw new IllegalArgumentException("Relation is not a 'one' or a 'one-nofk' relation: " + relationName + " of entity " + value.getEntityName());
        }

        Map fields = new HashMap();
        for (int i = 0; i < relation.getKeyMapsSize(); i++) {
            ModelKeyMap keyMap = relation.getKeyMap(i);
            fields.put(keyMap.getRelFieldName(), value.get(keyMap.getFieldName()));
        }

        return this.findByPrimaryKeyCache(relation.getRelEntityName(), fields);
    }

    /** Remove the named Related Entity for the GenericValue from the persistent store
     * @param relationName String containing the relation name which is the
     *      combination of relation.title and relation.rel-entity-name as
     *      specified in the entity XML definition file
     * @param value GenericValue instance containing the entity
     */
    public void removeRelated(String relationName, GenericValue value) throws GenericEntityException {
        ModelEntity modelEntity = value.getModelEntity();
        ModelRelation relation = modelEntity.getRelation(relationName);
        if (relation == null) {
            throw new GenericModelException("Could not find relation for relationName: " + relationName + " for value " + value);
        }

        Map fields = new HashMap();
        for (int i = 0; i < relation.getKeyMapsSize(); i++) {
            ModelKeyMap keyMap = relation.getKeyMap(i);
            fields.put(keyMap.getRelFieldName(), value.get(keyMap.getFieldName()));
        }

        this.removeByAnd(relation.getRelEntityName(), fields);
    }

    /** Refresh the Entity for the GenericValue from the persistent store
     *@param value GenericValue instance containing the entity to refresh
     */
    public void refresh(GenericValue value) throws GenericEntityException {
        GenericPK pk = value.getPrimaryKey();
        clearCacheLine(pk);
        GenericValue newValue = findByPrimaryKey(pk);
        if (newValue == null)
            throw new IllegalArgumentException("[GenericDelegator.refresh] could not refresh value: " + value);
        value.fields = newValue.fields;
        value.setDelegator(this);
        value.modified = false;
    }

    /** Store the Entity from the GenericValue to the persistent store
     * @param value GenericValue instance containing the entity
     */
    public void store(GenericValue value) throws GenericEntityException {
        this.clearCacheLine(value.getPrimaryKey());
        GenericHelper helper = getEntityHelper(value.getModelEntity());
        helper.store(value);
        // refresh the valueObject to get the new version
        if (value.lockEnabled())
            refresh(value);
    }

    /** Store the Entities from the Collection GenericValue instances to the persistent store.
     *  <br>This is different than the normal store method in that the store method only does
     *  an update, while the storeAll method checks to see if each entity exists, then
     *  either does an insert or an update as appropriate.
     *  <br>These updates all happen in one transaction, so they will either all succeed or all fail,
     *  if the data source supports transactions. This is just like to othersToStore feature
     *  of the GenericEntity on a create or store.
     *@param values Collection of GenericValue instances containing the entities to store
     */
    public void storeAll(Collection values) throws GenericEntityException {
        if (values == null)
            return;

        //from the delegator level this is complicated because different GenericValue
        // objects in the collection may correspond to different helpers
        HashMap valuesPerHelper = new HashMap();
        Iterator viter = values.iterator();
        while (viter.hasNext()) {
            GenericValue value = (GenericValue) viter.next();
            String helperName = this.getEntityHelperName(value.getEntityName());
            Collection helperValues = (Collection) valuesPerHelper.get(helperName);
            if (helperValues == null) {
                helperValues = new LinkedList();
                valuesPerHelper.put(helperName, helperValues);
            }
            helperValues.add(value);
        }

        boolean beganTransaction = false;
        try {
            //if there are multiple helpers and no transaction is active, begin one
            if (valuesPerHelper.size() > 1) {
                beganTransaction = TransactionUtil.begin();
            }

            Iterator helperIter = valuesPerHelper.entrySet().iterator();
            while (helperIter.hasNext()) {
                Map.Entry curEntry = (Map.Entry) helperIter.next();
                String helperName = (String) curEntry.getKey();
                GenericHelper helper = GenericHelperFactory.getHelper(helperName);
                this.clearAllCacheLinesByValue((Collection) curEntry.getValue());
                helper.storeAll((Collection) curEntry.getValue());
            }

            //only commit the transaction if we started one...
            TransactionUtil.commit(beganTransaction);
        } catch (GenericEntityException e) {
            try {
                //only rollback the transaction if we started one...
                TransactionUtil.rollback(beganTransaction);
            } catch (GenericEntityException e2) {
                Debug.logError("[GenericDelegator.removeAll] Could not rollback transaction: ", module);
                Debug.logError(e2, module);
            }
            //after rolling back, rethrow the exception
            throw e;
        }

        // Refresh the valueObjects to get the new version
        viter = values.iterator();
        while (viter.hasNext()) {
            GenericValue value = (GenericValue) viter.next();
            if (value.lockEnabled())
                refresh(value);
        }
    }

    /** Remove the Entities from the Collection from the persistent store.
     *  <br>The Collection contains GenericEntity objects, can be either GenericPK or GenericValue.
     *  <br>If a certain entity contains a complete primary key, the entity in the datasource corresponding
     *  to that primary key will be removed, this is like a removeByPrimary Key.
     *  <br>On the other hand, if a certain entity is an incomplete or non primary key,
     *  if will behave like the removeByAnd method.
     *  <br>These updates all happen in one transaction, so they will either all succeed or all fail,
     *  if the data source supports transactions.
     *@param dummyPKs Collection of GenericEntity instances containing the entities or by and fields to remove
     */
    public void removeAll(Collection dummyPKs) throws GenericEntityException {
        if (dummyPKs == null)
            return;

        //from the delegator level this is complicated because different GenericValue
        // objects in the collection may correspond to different helpers
        HashMap valuesPerHelper = new HashMap();
        Iterator viter = dummyPKs.iterator();
        while (viter.hasNext()) {
            GenericEntity entity = (GenericEntity) viter.next();
            String helperName = this.getEntityHelperName(entity.getEntityName());
            Collection helperValues = (Collection) valuesPerHelper.get(helperName);
            if (helperValues == null) {
                helperValues = new LinkedList();
                valuesPerHelper.put(helperName, helperValues);
            }
            helperValues.add(entity);
        }

        boolean beganTransaction = false;
        try {
            //if there are multiple helpers and no transaction is active, begin one
            if (valuesPerHelper.size() > 1) {
                beganTransaction = TransactionUtil.begin();
            }

            Iterator helperIter = valuesPerHelper.entrySet().iterator();
            while (helperIter.hasNext()) {
                Map.Entry curEntry = (Map.Entry) helperIter.next();
                String helperName = (String) curEntry.getKey();
                GenericHelper helper = GenericHelperFactory.getHelper(helperName);
                this.clearAllCacheLines((Collection) curEntry.getValue());
                helper.removeAll((Collection) curEntry.getValue());
            }

            //only commit the transaction if we started one...
            if (beganTransaction)
                TransactionUtil.commit();
        } catch (GenericEntityException e) {
            try {
                //only rollback the transaction if we started one...
                if (beganTransaction)
                    TransactionUtil.rollback();
            } catch (GenericEntityException e2) {
                Debug.logError("[GenericDelegator.removeAll] Could not rollback transaction: ", module);
                Debug.logError(e2, module);
            }
            //after rolling back, rethrow the exception
            throw e;
        }
    }

    // ======================================
    // ======= Cache Related Methods ========

    /** Remove a CACHED Generic Entity (Collection) from the cache, either a PK, ByAnd, or All
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponging values
     */
    public void clearCacheLine(String entityName, Map fields) {
        ModelEntity entity = null;
        try {
            entity = getModelReader().getModelEntity(entityName);
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
        if (entity == null) {
            throw new IllegalArgumentException("[GenericDelegator.clearCacheLine] could not find entity for entityName: " + entityName);
        }

        GenericPK dummyPK = new GenericPK(entity, fields);
        this.clearCacheLineFlexible(dummyPK);
    }

    /** Remove a CACHED Generic Entity from the cache by its primary key.
     * Checks to see if the passed GenericPK is a complete primary key, if
     * it is then the cache line will be removed from the primaryKeyCache; if it
     * is NOT a complete primary key it will remove the cache line from the andCache.
     * If the fields map is empty, then the allCache for the entity will be cleared.
     *@param dummyPK The dummy primary key to clear by.
     */
    public void clearCacheLineFlexible(GenericEntity dummyPK) {
        if (dummyPK != null) {
            //check to see if passed fields names exactly make the primary key...
            if (dummyPK.isPrimaryKey()) {
                //findByPrimaryKey
                if (primaryKeyCache != null) {
                    primaryKeyCache.remove(dummyPK);
                }
            } else {
                if (dummyPK.size() == 0) {
                    //findAll
                    if (allCache != null) {
                        allCache.remove(dummyPK.getEntityName());
                    }
                } else {
                    //findByAnd
                    if (andCache != null) {
                        andCache.remove(dummyPK);
                    }
                }
            }
        }
    }

    /** Remove a CACHED Generic Entity from the cache by its primary key, does NOT
     * check to see if the passed GenericPK is a complete primary key.
     *@param primaryKey The primary key to clear by.
     */
    public void clearCacheLine(GenericPK primaryKey) {
        if (primaryKey != null && primaryKeyCache != null) {
            primaryKeyCache.remove(primaryKey);
        }
    }

    public void clearAllCacheLines(Collection dummyPKs) {
        if (dummyPKs == null)
            return;
        Iterator iter = dummyPKs.iterator();
        while (iter.hasNext()) {
            GenericEntity entity = (GenericEntity) iter.next();
            this.clearCacheLineFlexible(entity);
        }
    }

    public void clearAllCacheLinesByValue(Collection values) {
        if (values == null)
            return;
        Iterator iter = values.iterator();
        while (iter.hasNext()) {
            GenericValue value = (GenericValue) iter.next();
            this.clearCacheLine(value.getPrimaryKey());
        }
    }

    public GenericValue getFromPrimaryKeyCache(GenericPK primaryKey) {
        if (primaryKey == null)
            return null;
        return (GenericValue) primaryKeyCache.get(primaryKey);
    }

    public Collection getFromAllCache(String entityName) {
        if (entityName == null)
            return null;
        return (Collection) allCache.get(entityName);
    }

    public Collection getFromAndCache(String entityName, Map fields) {
        if (entityName == null || fields == null)
            return null;
        ModelEntity entity = null;
        try {
            entity = getModelReader().getModelEntity(entityName);
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
        GenericPK tempPK = new GenericPK(entity, fields);
        if (tempPK == null)
            return null;
        return (Collection) andCache.get(tempPK);
    }

    public void putInPrimaryKeyCache(GenericPK primaryKey, GenericValue value) {
        if (primaryKey == null || value == null)
            return;
        primaryKeyCache.put(primaryKey, value);
    }

    public void putAllInPrimaryKeyCache(Collection values) {
        if (values == null)
            return;
        Iterator iter = values.iterator();
        while (iter.hasNext()) {
            GenericValue value = (GenericValue) iter.next();
            this.putInPrimaryKeyCache(value.getPrimaryKey(), value);
        }
    }

    public void putInAllCache(String entityName, Collection values) {
        if (entityName == null || values == null)
            return;
        allCache.put(entityName, values);
    }

    public void putInAndCache(String entityName, Map fields, Collection values) {
        if (entityName == null || fields == null || values == null)
            return;
        ModelEntity entity = null;
        try {
            entity = getModelReader().getModelEntity(entityName);
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
        GenericPK tempPK = new GenericPK(entity, fields);
        if (tempPK == null)
            return;
        andCache.put(tempPK, values);
    }


    // ======= XML Related Methods ========
    public Collection readXmlDocument(URL url) throws SAXException, ParserConfigurationException, java.io.IOException {
        if (url == null)
            return null;
        return this.makeValues(UtilXml.readXmlDocument(url, false));
    }

    public Collection makeValues(Document document) {
        if (document == null)
            return null;
        Collection values = new LinkedList();

        Element docElement = document.getDocumentElement();
        if (docElement == null)
            return null;
        if (!"entity-engine-xml".equals(docElement.getTagName())) {
            Debug.logError("[GenericDelegator.makeValues] Root node was not <entity-engine-xml>", module);
            throw new java.lang.IllegalArgumentException("Root node was not <entity-engine-xml>");
        }
        docElement.normalize();
        Node curChild = docElement.getFirstChild();

        if (curChild != null) {
            do {
                if (curChild.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) curChild;
                    GenericValue value = this.makeValue(element);
                    if (value != null)
                        values.add(value);
                }
            } while ((curChild = curChild.getNextSibling()) != null);
        } else
            Debug.logWarning("[GenericDelegator.makeValues] No child nodes found in document.", module);

        return values;
    }

    public GenericPK makePK(Element element) {
        GenericValue value = makeValue(element);
        return value.getPrimaryKey();
    }

    public GenericValue makeValue(Element element) {
        if (element == null)
            return null;
        String entityName = element.getTagName();
        //if a dash or colon is in the tag name, grab what is after it
        if (entityName.indexOf('-') > 0)
            entityName = entityName.substring(entityName.indexOf('-') + 1);
        if (entityName.indexOf(':') > 0)
            entityName = entityName.substring(entityName.indexOf(':') + 1);
        GenericValue value = this.makeValue(entityName, null);

        ModelEntity modelEntity = value.getModelEntity();

        Iterator modelFields = modelEntity.getFieldsIterator();
        while (modelFields.hasNext()) {
            ModelField modelField = (ModelField) modelFields.next();
            String name = modelField.getName();
            String attr = element.getAttribute(name);
            if (attr != null) {
                if (attr.length() > 0) {
                    value.setString(name, attr);
                }
            } else {
                //if no attribute try a subelement
                Element subElement = UtilXml.firstChildElement(element, name);
                if (subElement != null) {
                    value.setString(name, UtilXml.elementValue(subElement));
                }
            }
        }

        return value;
    }


    // ======= Misc Methods ========

    /** Get the next guaranteed unique seq id from the sequence with the given sequence name; if the named sequence doesn't exist, it will be created
     *@param seqName The name of the sequence to get the next seq id from
     *@return Long with the next seq id for the given sequence name
     */
    public Long getNextSeqId(String seqName) {
        if (sequencer == null) {
            synchronized (this) {
                if (sequencer == null) {
                    String helperName = this.getEntityHelperName("SequenceValueItem");
                    sequencer = new SequenceUtil(helperName);
                }
            }
        }
        if (sequencer != null)
            return sequencer.getNextSeqId(seqName);
        else
            return null;
    }

    protected void absorbCollection(Collection col) {
        if (col == null)
            return;
        Iterator iter = col.iterator();
        while (iter.hasNext()) {
            GenericValue value = (GenericValue) iter.next();
            value.setDelegator(this);
        }
    }
}
