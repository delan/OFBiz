package org.ofbiz.core.entity;

import java.util.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.model.*;

/**
 * <p><b>Title:</b> Server Delegator Class
 * <p><b>Description:</b> None
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
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    Sep 17 2001
 *@version    1.0
 */
public class GenericDelegator {
  static UtilCache delegatorCache = new UtilCache("GenericDelegators", 0, 0);
  String delegatorName;
  ModelReader modelReader;
  ModelGroupReader modelGroupReader;
  
  UtilCache primaryKeyCache = null;
  UtilCache allCache = null;
  UtilCache andCache = null;
  
  SequenceUtil sequencer = null;
  
  public static GenericDelegator getGenericDelegator(String delegatorName) {
    GenericDelegator delegator = (GenericDelegator)delegatorCache.get(delegatorName);
    if(delegator == null) //don't want to block here
    {
      synchronized(GenericDelegator.class) {
        //must check if null again as one of the blocked threads can still enter
        delegator = (GenericDelegator)delegatorCache.get(delegatorName);
        if(delegator == null) {
          delegator = new GenericDelegator(delegatorName);
          delegatorCache.put(delegatorName, delegator);
        }
      }
    }
    return delegator;
  }
  
  public GenericDelegator(String delegatorName) {
    this.delegatorName = delegatorName;
    modelReader = ModelReader.getModelReader(delegatorName);
    modelGroupReader = ModelGroupReader.getModelGroupReader(delegatorName);

    primaryKeyCache = new UtilCache("FindByPrimaryKey-" + delegatorName);
    allCache = new UtilCache("FindAll-" + delegatorName);
    andCache = new UtilCache("FindByAnd-" + delegatorName);
    
    //initialize helpers by group
    Iterator groups = UtilMisc.toIterator(modelGroupReader.getGroupNames());
    while(groups != null && groups.hasNext()) {
      String groupName = (String)groups.next();
      String helperName = this.getGroupHelperName(groupName);
      Debug.logInfo("[GenericDelegator.GenericDelegator] Delegator \"" + delegatorName + "\" initializing helper \"" + helperName + "\" for entity group \"" + groupName + "\".");
      TreeSet helpersDone = new TreeSet();
      if(helperName != null && helperName.length() > 0) {
        //make sure each helper is only loaded once
        if(helpersDone.contains(helperName)) {
          Debug.logInfo("[GenericDelegator.GenericDelegator] Helper \"" + helperName + "\" alread initialized, not re-initializing.");
          continue;
        }
        helpersDone.add(helperName);
        //pre-load field type defs
        ModelFieldTypeReader.getModelFieldTypeReader(helperName);
        //get the helper and if configured, do the datasource check
        GenericHelper helper = GenericHelperFactory.getHelper(helperName);
        if(UtilProperties.propertyValueEqualsIgnoreCase("servers", helperName + ".datasource.check.on.start", "true"))
        {
          boolean addMissing = UtilProperties.propertyValueEqualsIgnoreCase("servers", helperName + ".datasource.add.missing.on.start", "true");
          Debug.logInfo("[GenericDelegator.GenericDelegator] Doing database check as requested in servers.properties with addMissing=" + addMissing);
          try { helper.checkDataSource(this.getModelEntityMapByGroup(groupName), null, addMissing); }
          catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); }
        }
      }
    }
  }
  
  /** Gets the name of the server configuration that corresponds to this delegator
   * @return server configuration name
   */
  public String getDelegatorName() { return this.delegatorName; }
  
  /** Gets the instance of ModelReader that corresponds to this delegator
   *@return ModelReader that corresponds to this delegator
   */
  public ModelReader getModelReader() { return this.modelReader; }
  
  /** Gets the instance of ModelGroupReader that corresponds to this delegator
   *@return ModelGroupReader that corresponds to this delegator
   */
  public ModelGroupReader getModelGroupReader() { return this.modelGroupReader; }
  
  /** Gets the instance of ModelEntity that corresponds to this delegator and the specified entityName
   *@param entityName The name of the entity to get
   *@return ModelEntity that corresponds to this delegator and the specified entityName
   */
  public ModelEntity getModelEntity(String entityName) { return modelReader.getModelEntity(entityName); }

  /** Gets a collection of entity models that are in a group corresponding to the specified group name
   *@param groupName The name of the group
   *@return Collection of ModelEntity instances
   */
  public Collection getModelEntitiesByGroup(String groupName) {
    Iterator enames = UtilMisc.toIterator(modelGroupReader.getEntityNamesByGroup(groupName));
    Collection entities = new LinkedList();
    if(enames == null || !enames.hasNext()) return entities;
    while(enames.hasNext()) {
      String ename = (String)enames.next();
      ModelEntity entity = this.getModelEntity(ename);
      if(entity != null) entities.add(entity);
    }
    return entities;
  }
  
  /** Gets a Map of entity name & entity model pairs that are in the named group
   *@param groupName The name of the group
   *@return Map of entityName String keys and ModelEntity instance values
   */
  public Map getModelEntityMapByGroup(String groupName) {
    Iterator enames = UtilMisc.toIterator(modelGroupReader.getEntityNamesByGroup(groupName));
    Map entities = new HashMap();
    if(enames == null || !enames.hasNext()) return entities;
    while(enames.hasNext()) {
      String ename = (String)enames.next();
      ModelEntity entity = this.getModelEntity(ename);
      if(entity != null) entities.put(entity.entityName, entity);
    }
    return entities;
  }
  
  /** Gets the helper name that corresponds to this delegator and the specified entityName
   *@param entityName The name of the entity to get the helper for
   *@return String with the helper name that corresponds to this delegator and the specified entityName
   */
  public String getEntityHelperName(String entityName) {
    String groupName = getModelGroupReader().getEntityGroupName(entityName);
    return this.getGroupHelperName(groupName);
  }
  
  /** Gets the helper name that corresponds to this delegator and the specified entityName
   *@param entityName The name of the entity to get the helper for
   *@return String with the helper name that corresponds to this delegator and the specified entityName
   */
  public String getGroupHelperName(String groupName) {
    return UtilProperties.getPropertyValue("servers", delegatorName + ".group." + groupName);
  }
  
  /** Gets the helper name that corresponds to this delegator and the specified entity
   *@param entity The entity to get the helper for
   *@return String with the helper name that corresponds to this delegator and the specified entity
   */
  public String getEntityHelperName(ModelEntity entity) {
    if(entity == null) return null;
    return getEntityHelperName(entity.entityName);
  }
  
  /** Gets the an instance of helper that corresponds to this delegator and the specified entityName
   *@param entityName The name of the entity to get the helper for
   *@return GenericHelper that corresponds to this delegator and the specified entityName
   */
  public GenericHelper getEntityHelper(String entityName) throws GenericEntityException {
    String helperName = getEntityHelperName(entityName);
    if(helperName != null && helperName.length() > 0) return GenericHelperFactory.getHelper(helperName);
    else throw new GenericEntityException("Helper name not found for entity " + entityName);
  }

  /** Gets the an instance of helper that corresponds to this delegator and the specified entity
   *@param entity The entity to get the helper for
   *@return GenericHelper that corresponds to this delegator and the specified entity
   */
  public GenericHelper getEntityHelper(ModelEntity entity) throws GenericEntityException {
    return getEntityHelper(entity.entityName);
  }

  /** Gets a field type instance by name from the helper that corresponds to the specified entity
   *@param entity The entity
   *@param type The name of the type
   *@return ModelFieldType instance for the named type from the helper that corresponds to the specified entity
   */
  public ModelFieldType getEntityFieldType(ModelEntity entity, String type) throws GenericEntityException {
    String helperName = getEntityHelperName(entity);
    if(helperName == null || helperName.length() <= 0) return null;
    ModelFieldTypeReader modelFieldTypeReader = ModelFieldTypeReader.getModelFieldTypeReader(helperName);
    if(modelFieldTypeReader == null) throw new GenericEntityException("ModelFieldTypeReader not found for entity " + entity.entityName + " with helper name " + helperName);
    return modelFieldTypeReader.getModelFieldType(type);
  }
  
  /** Gets field type names from the helper that corresponds to the specified entity
   *@param entity The entity
   *@return Collection of field type names from the helper that corresponds to the specified entity
   */
  public Collection getEntityFieldTypeNames(ModelEntity entity) throws GenericEntityException {
    String helperName = getEntityHelperName(entity);
    if(helperName == null || helperName.length() <= 0) return null;
    ModelFieldTypeReader modelFieldTypeReader = ModelFieldTypeReader.getModelFieldTypeReader(helperName);
    if(modelFieldTypeReader == null) throw new GenericEntityException("ModelFieldTypeReader not found for entity " + entity.entityName + " with helper name " + helperName);
    return modelFieldTypeReader.getFieldTypeNames();
  }
  
  /** Creates a Entity in the form of a GenericValue without persisting it */
  public GenericValue makeValue(String entityName, Map fields) {
    ModelEntity entity = modelReader.getModelEntity(entityName);
    if(entity == null) throw new IllegalArgumentException("[GenericDelegator.makeValue] could not find entity for entityName: " + entityName);
    GenericValue value = new GenericValue(entity, fields);
    value.delegator = this;
    return value;
  }
  
  /** Creates a Primary Key in the form of a GenericPK without persisting it */
  public GenericPK makePK(String entityName, Map fields) {
    ModelEntity entity = modelReader.getModelEntity(entityName);
    if(entity == null) throw new IllegalArgumentException("[GenericDelegator.makePK] could not find entity for entityName: " + entityName);
    GenericPK pk = new GenericPK(entity, fields);
    return pk;
  }
  
  /** Creates a Entity in the form of a GenericValue and write it to the database
   *@return GenericValue instance containing the new instance
   */
  public GenericValue create(String entityName, Map fields) throws GenericEntityException {
    if(entityName == null || fields == null) { return null; }
    GenericValue genericValue = new GenericValue(modelReader.getModelEntity(entityName), fields);
    return this.create(genericValue);
  }
  
  
  /** Creates a Entity in the form of a GenericValue and write it to the database
   * @return GenericValue instance containing the new instance
   */
  public GenericValue create(GenericValue value) throws GenericEntityException {
    GenericHelper helper = getEntityHelper(value.getModelEntity());
    value = helper.create(value);
    if(value != null) value.delegator = this;
    return value;
  }
  
  /** Creates a Entity in the form of a GenericValue and write it to the database
   * @return GenericValue instance containing the new instance
   */
  public GenericValue create(GenericPK primaryKey) throws GenericEntityException {
    GenericHelper helper = getEntityHelper(primaryKey.getModelEntity());
    GenericValue value = helper.create(primaryKey);
    if(value != null) value.delegator = this;
    return value;
  }
  
  /** Find a Generic Entity by its Primary Key
   * @param primaryKey The primary key to find by.
   * @return The GenericValue corresponding to the primaryKey
   */
  public GenericValue findByPrimaryKey(GenericPK primaryKey) throws GenericEntityException {
    GenericHelper helper = getEntityHelper(primaryKey.getModelEntity());
    GenericValue value = null;
    if(!primaryKey.isPrimaryKey()) throw new IllegalArgumentException("[GenericDelegator.findByPrimaryKey] Passed primary key is not a valid primary key: " + primaryKey);
    try { value = helper.findByPrimaryKey(primaryKey); }
    catch(GenericEntityNotFoundException e) { value = null; }
    if(value != null) value.delegator = this;
    return value;
  }
  
  /** Find a CACHED Generic Entity by its Primary Key
   *@param primaryKey The primary key to find by.
   *@return The GenericValue corresponding to the primaryKey
   */
  public GenericValue findByPrimaryKeyCache(GenericPK primaryKey) throws GenericEntityException {
    GenericValue value = (GenericValue)primaryKeyCache.get(primaryKey);
    if(value == null) {
      value = findByPrimaryKey(primaryKey);
      if(value != null) primaryKeyCache.put(primaryKey, value);
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
   *@param order The fields of the named entity to order the query by; optionall add a " ASC" for ascending or " DESC" for descending
   *@return    Collection containing all Generic entities
   */
  public Collection findAll(String entityName, List orderBy) throws GenericEntityException {
    return this.findByAnd(entityName, null, orderBy);
  }
  
  /** Finds all Generic entities, looking first in the cache; uses orderBy for lookup, but only keys results on the entityName and fields
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param order The fields of the named entity to order the query by; optionall add a " ASC" for ascending or " DESC" for descending
   *@return    Collection containing all Generic entities
   */
  public Collection findAllCache(String entityName, List orderBy) throws GenericEntityException {
    Collection col = (Collection)allCache.get(entityName);
    if(col == null) {
      col = findAll(entityName, orderBy);
      if(col != null) allCache.put(entityName, col);
    }
    return col;
  }
  
  /** Finds Generic Entity records by all of the specified fields (ie: combined using AND)
   * @param entityName The Name of the Entity as defined in the entity XML file
   * @param fields The fields of the named entity to query by with their corresponging values
   * @param order The fields of the named entity to order the query by;
   *      optionall add a " ASC" for ascending or " DESC" for descending
   * @return Collection of GenericValue instances that match the query
   */
  public Collection findByAnd(String entityName, Map fields, List orderBy) throws GenericEntityException {
    ModelEntity modelEntity = modelReader.getModelEntity(entityName);
    GenericHelper helper = getEntityHelper(modelEntity);

    Collection collection = null;
    collection = helper.findByAnd(modelEntity, fields, orderBy);
    absorbCollection(collection);
    return collection;
  }
  
  /** Finds Generic Entity records by all of the specified fields (ie: combined using AND), looking first in the cache; uses orderBy for lookup, but only keys results on the entityName and fields
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param fields The fields of the named entity to query by with their corresponging values
   *@param order The fields of the named entity to order the query by; optionall add a " ASC" for ascending or " DESC" for descending
   *@return Collection of GenericValue instances that match the query
   */
  public Collection findByAndCache(String entityName, Map fields, List orderBy) throws GenericEntityException {
    GenericPK tempPK = new GenericPK(modelReader.getModelEntity(entityName), fields);
    Collection col = (Collection)andCache.get(tempPK);
    if(col == null) {
      col = findByAnd(entityName, fields, orderBy);
      if(col != null) andCache.put(tempPK, col);
    }
    return col;
  }
  
  /** Removes/deletes Generic Entity records found by all of the specified fields (ie: combined using AND)
   * @param entityName The Name of the Entity as defined in the entity XML file
   * @param fields The fields of the named entity to query by with their corresponging values
   * @return Collection of GenericValue instances that match the query
   */
  public void removeByAnd(String entityName, Map fields) throws GenericEntityException {
    this.clearCacheLine(entityName, fields);
    ModelEntity modelEntity = modelReader.getModelEntity(entityName);
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
    ModelEntity modelEntity = value.getModelEntity();
    ModelRelation relation = modelEntity.getRelation(relationName);
    if(relation == null) throw new IllegalArgumentException("[GenericDelegator.selectRelated] could not find relation for relationName: " + relationName + " for value " + value);
    ModelEntity relatedEntity = modelReader.getModelEntity(relation.relEntityName);

    Map fields = new HashMap();
    for(int i=0; i<relation.keyMaps.size(); i++)
    {
      ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(i);
      fields.put(keyMap.relFieldName, value.get(keyMap.fieldName));
    }

    return this.findByAnd(relatedEntity.entityName, fields, null);
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
    if(relation == null) throw new GenericModelException("[GenericDelegator.selectRelated] could not find relation for relationName: " + relationName + " for value " + value);
    ModelEntity relatedEntity = modelReader.getModelEntity(relation.relEntityName);

    Map fields = new HashMap();
    for(int i=0; i<relation.keyMaps.size(); i++)
    {
      ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(i);
      fields.put(keyMap.relFieldName, value.get(keyMap.fieldName));
    }

    return this.findByAndCache(relatedEntity.entityName, fields, null);
  }

  /** Get related entity where relation is of type one, uses findByPrimaryKey
   * @throws IllegalArgumentException if the collection found has more than one item
   */
  public GenericValue getRelatedOne(String relationName, GenericValue value) throws GenericEntityException {
    ModelEntity modelEntity = value.getModelEntity();
    ModelRelation relation = value.getModelEntity().getRelation(relationName);
    if(relation == null) throw new GenericModelException("[GenericDelegator.getRelatedOne] could not find relation for relationName: " + relationName + " for value " + value);
    if(!"one".equals(relation.type)) throw new IllegalArgumentException("Relation is not a 'one' relation: " + relationName + " of entity " + value.getEntityName());
    ModelEntity relatedEntity = modelReader.getModelEntity(relation.relEntityName);
    
    Map fields = new HashMap();
    for(int i=0; i<relation.keyMaps.size(); i++)
    {
      ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(i);
      fields.put(keyMap.relFieldName, value.get(keyMap.fieldName));
    }

    return this.findByPrimaryKey(relatedEntity.entityName, fields);
  }
  
  /** Get related entity where relation is of type one, uses findByPrimaryKey, checking first in the cache to see if the desired value is there
   * @throws IllegalArgumentException if the collection found has more than one item
   */
  public GenericValue getRelatedOneCache(String relationName, GenericValue value) throws GenericEntityException {
    ModelEntity modelEntity = value.getModelEntity();
    ModelRelation relation = value.getModelEntity().getRelation(relationName);
    if(relation == null) throw new GenericModelException("[GenericDelegator.getRelatedOne] could not find relation for relationName: " + relationName + " for value " + value);
    if(!"one".equals(relation.type)) throw new IllegalArgumentException("Relation is not a 'one' relation: " + relationName + " of entity " + value.getEntityName());
    ModelEntity relatedEntity = modelReader.getModelEntity(relation.relEntityName);
    
    Map fields = new HashMap();
    for(int i=0; i<relation.keyMaps.size(); i++)
    {
      ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(i);
      fields.put(keyMap.relFieldName, value.get(keyMap.fieldName));
    }

    return this.findByPrimaryKeyCache(relatedEntity.entityName, fields);
  }
  
  /** Get the named Related Entity for the GenericValue from the persistent store
   * @param relationName String containing the relation name which is the
   *      combination of relation.title and relation.rel-entity-name as
   *      specified in the entity XML definition file
   * @param value GenericValue instance containing the entity
   * @return Collection of GenericValue instances as specified in the relation definition
   */
  public Collection getRelatedByAnd(String relationName, Map fields, GenericValue value) throws GenericEntityException {
    ModelEntity modelEntity = value.getModelEntity();
    ModelRelation relation = modelEntity.getRelation(relationName);
    if(relation == null) throw new IllegalArgumentException("[GenericDelegator.selectRelated] could not find relation for relationName: " + relationName + " for value " + value);
    ModelEntity relatedEntity = modelReader.getModelEntity(relation.relEntityName);

    //put the fields into the hash map first, 
    //they will be overridden by value's fields if over-specified this is important for security and cleanliness
    Map filterFields = new HashMap(fields);
    for(int i=0; i<relation.keyMaps.size(); i++)
    {
      ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(i);
      filterFields.put(keyMap.relFieldName, value.get(keyMap.fieldName));
    }

    return this.findByAnd(relatedEntity.entityName, filterFields, null);
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
    ModelEntity relatedEntity = modelReader.getModelEntity(relation.relEntityName);

    Map fields = new HashMap();
    for(int i=0; i<relation.keyMaps.size(); i++)
    {
      ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(i);
      fields.put(keyMap.relFieldName, value.get(keyMap.fieldName));
    }

    this.removeByAnd(relatedEntity.entityName, fields);
  }    
  
  /** Refresh the Entity for the GenericValue from the persistent store
   *@param value GenericValue instance containing the entity to refresh
   */
  public void refresh(GenericValue value) throws GenericEntityException {
    GenericPK pk = value.getPrimaryKey();
    clearCacheLine(pk);
    GenericValue newValue = findByPrimaryKey(pk);
    if(newValue == null) throw new IllegalArgumentException("[GenericDelegator.refresh] could not refresh value: " + value);
    value.fields = newValue.fields;
    value.delegator = this;
    value.modified = false;
  }
  
  /** Store the Entity from the GenericValue to the persistent store
   * @param value GenericValue instance containing the entity
   */
  public void store(GenericValue value) throws GenericEntityException {
    this.clearCacheLine(value.getPrimaryKey());
    GenericHelper helper = getEntityHelper(value.getModelEntity());
    helper.store(value);
  }
  
  /** Remove a CACHED Generic Entity (Collection) from the cache, either a PK, ByAnd, or All
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param fields The fields of the named entity to query by with their corresponging values
   *@return The GenericValue corresponding to the primaryKey
   */
  public void clearCacheLine(String entityName, Map fields) {
    ModelEntity entity = modelReader.getModelEntity(entityName);
    if(entity == null) throw new IllegalArgumentException("[GenericDelegator.clearCacheLine] could not find entity for entityName: " + entityName);
    
    if(fields == null || fields.size() <= 0) {
      //findAll
      if(allCache != null) {
        allCache.remove(entityName);
      }
    }
    else {
      GenericPK tempPK = new GenericPK(modelReader.getModelEntity(entityName), fields);
      
      //check to see if passed fields names exactly make the primary key...
      Collection pkNames = entity.getPkFieldNames();
      Collection passedNames = fields.keySet();
      if(pkNames.containsAll(passedNames) && passedNames.containsAll(pkNames)) {
        //findByPrimaryKey
        if(primaryKeyCache != null) {
          primaryKeyCache.remove(tempPK);
        }
      }
      else {
        //findByAnd
        if(andCache != null) {
          andCache.remove(tempPK);
        }
      }
    }
  }
  
  /** Remove a CACHED Generic Entity from the cache by its primary key
   *@param primaryKey The primary key to find by.
   *@return The GenericValue corresponding to the primaryKey
   */
  public void clearCacheLine(GenericPK primaryKey) {
    if(primaryKey != null && primaryKeyCache != null) {
      primaryKeyCache.remove(primaryKey);
    }
  }
  
  /** Get the next guaranteed unique seq id from the sequence with the given sequence name; if the named sequence doesn't exist, it will be created
   *@param seqName The name of the sequence to get the next seq id from
   *@return Long with the next seq id for the given sequence name
   */
  public Long getNextSeqId(String seqName) {
    if(sequencer == null) {
      synchronized(this) { 
        if(sequencer == null) {
          String helperName = this.getEntityHelperName("Sequence");
          sequencer = new SequenceUtil(helperName);
        }
      }
    }
    if(sequencer != null) return sequencer.getNextSeqId(seqName);
    else return null;
  }
  
  protected void absorbCollection(Collection col) {
    if(col == null) return;
    Iterator iter = col.iterator();
    while(iter.hasNext()) {
      GenericValue value = (GenericValue)iter.next();
      value.delegator = this;
    }
  }
}
