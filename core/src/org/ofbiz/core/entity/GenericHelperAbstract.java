package org.ofbiz.core.entity;

import java.util.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.model.*;

/**
 * <p><b>Title:</b> Generic Entity Helper Class
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
 *@author     David E. Jones
 *@created    Tue Aug 07 01:10:32 MDT 2001
 *@version    1.0
 */
public abstract class GenericHelperAbstract implements GenericHelper {
  ModelReader modelReader;
  
  UtilCache primaryKeyCache = null;
  UtilCache allCache = null;
  UtilCache andCache = null;
  
  String serverName;
  
  SequenceUtil sequencer = null;
  
  /** Gets the name of the server configuration that corresponds to this helper
   *@return server configuration name
   */
  public String getServerName() { return serverName; }
  
  /** Gets the instance of ModelReader that corresponds to this helper
   *@return ModelReader that corresponds to this helper
   */
  public ModelReader getModelReader() { return modelReader; }
  
  /** Gets the instance of ModelEntity that corresponds to this helper and the specified entityName
   *@param entityName The name of the entity to get
   *@return ModelEntity that corresponds to this helper and the specified entityName
   */
  public ModelEntity getModelEntity(String entityName) { return modelReader.getModelEntity(entityName); }
  
  /** Creates a Entity in the form of a GenericValue without persisting it */
  public GenericValue makeValue(String entityName, Map fields) {
    ModelEntity entity = modelReader.getModelEntity(entityName);
    if(entity == null) throw new IllegalArgumentException("[GenericHelperAbstract.makeValue] could not find entity for entityName: " + entityName);
    GenericValue value = new GenericValue(entity, fields);
    value.helper = this;
    return value;
  }
  
  /** Creates a Primary Key in the form of a GenericPK without persisting it */
  public GenericPK makePK(String entityName, Map fields) {
    ModelEntity entity = modelReader.getModelEntity(entityName);
    if(entity == null) throw new IllegalArgumentException("[GenericHelperAbstract.makePK] could not find entity for entityName: " + entityName);
    GenericPK pk = new GenericPK(entity, fields);
    return pk;
  }
  
  /** Find a Generic Entity by its Primary Key
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param fields The fields of the named entity to query by with their corresponging values
   *@return The GenericValue corresponding to the primaryKey
   */
  public GenericValue findByPrimaryKey(String entityName, Map fields) {
    return findByPrimaryKey(makePK(entityName, fields));
  }
  
  /** Find a CACHED Generic Entity by its Primary Key
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param fields The fields of the named entity to query by with their corresponging values
   *@return The GenericValue corresponding to the primaryKey
   */
  public GenericValue findByPrimaryKeyCache(String entityName, Map fields) {
    return findByPrimaryKeyCache(makePK(entityName, fields));
  }
  
  /** Find a CACHED Generic Entity by its Primary Key
   *@param primaryKey The primary key to find by.
   *@return The GenericValue corresponding to the primaryKey
   */
  public GenericValue findByPrimaryKeyCache(GenericPK primaryKey) {
    GenericValue value = (GenericValue)primaryKeyCache.get(primaryKey);
    if(value == null) {
      value = findByPrimaryKey(primaryKey);
      if(value != null) primaryKeyCache.put(primaryKey, value);
    }
    return value;
  }
  
  /** Finds all Generic entities
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param order The fields of the named entity to order the query by; optionall add a " ASC" for ascending or " DESC" for descending
   *@return    Collection containing all Generic entities
   */
  public Collection findAll(String entityName, List orderBy)
  {
    Collection collection = null;
    collection = findByAnd(entityName, null, orderBy);
    absorbCollection(collection);
    return collection;
  }

  /** Finds all Generic entities, looking first in the cache; uses orderBy for lookup, but only keys results on the entityName and fields
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param order The fields of the named entity to order the query by; optionall add a " ASC" for ascending or " DESC" for descending
   *@return    Collection containing all Generic entities
   */
  public Collection findAllCache(String entityName, List orderBy) {
    Collection col = (Collection)allCache.get(entityName);
    if(col == null) {
      col = findAll(entityName, orderBy);
      if(col != null) allCache.put(entityName, col);
    }
    return col;
  }
  
  /** Finds Generic Entity records by all of the specified fields (ie: combined using AND), looking first in the cache; uses orderBy for lookup, but only keys results on the entityName and fields
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param fields The fields of the named entity to query by with their corresponging values
   *@param order The fields of the named entity to order the query by; optionall add a " ASC" for ascending or " DESC" for descending
   *@return Collection of GenericValue instances that match the query
   */
  public Collection findByAndCache(String entityName, Map fields, List orderBy) {
    GenericPK tempPK = new GenericPK(modelReader.getModelEntity(entityName), fields);
    Collection col = (Collection)andCache.get(tempPK);
    if(col == null) {
      col = findByAnd(entityName, fields, orderBy);
      if(col != null) andCache.put(tempPK, col);
    }
    return col;
  }
  
  /**
   * simple implementation that uses {@link getRelated(String, GenericValue)}.
   * @throws IllegalArgumentException if the collection found has more than one item
   */
  public GenericValue getRelatedOne(String relationName, GenericValue value) {
    ModelRelation relation = value.getModelEntity().getRelation(relationName);
    if(relation == null) throw new IllegalArgumentException("[GenericHelperAbstract.getRelatedOne] could not find relation for relationName: " + relationName + " for value " + value);
    
    Collection col = getRelated(relationName, value);
    if ((col == null) || col.size() == 0) { return null; }
    else if (col.size() == 1) { return (GenericValue) col.iterator().next(); }
    else { throw new IllegalArgumentException("[GenericHelperAbstract.getRelatedOne] got multiple results for relationName: " + relationName + " for value " + value); }
  }
  
  /** Refresh the Entity for the GenericValue from the persistent store
   *@param value GenericValue instance containing the entity to refresh
   */
  public void refresh(GenericValue value) {
    GenericPK pk = value.getPrimaryKey();
    clearCacheLine(pk);
    GenericValue newValue = findByPrimaryKey(pk);
    if(newValue == null) throw new IllegalArgumentException("[GenericHelperAbstract.refresh] could not refresh value: " + value);
    value.fields = newValue.fields;
    value.helper = this;
    value.modified = false;
  }
  
  /** Get the next guaranteed unique seq id from the sequence with the given sequence name; if the named sequence doesn't exist, it will be created
   *@param seqName The name of the sequence to get the next seq id from
   *@return Long with the next seq id for the given sequence name
   */
  public Long getNextSeqId(String seqName) {
    if(sequencer == null)
      synchronized(this) { if(sequencer == null) sequencer = new SequenceUtil(serverName); }
      return sequencer.getNextSeqId(seqName);
  }

  /** Remove a CACHED Generic Entity (Collection) from the cache, either a PK, ByAnd, or All 
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param fields The fields of the named entity to query by with their corresponging values
   *@return The GenericValue corresponding to the primaryKey
   */
  public void clearCacheLine(String entityName, Map fields) {
    ModelEntity entity = modelReader.getModelEntity(entityName);
    if(entity == null) throw new IllegalArgumentException("[GenericHelperAbstract.clearCacheLine] could not find entity for entityName: " + entityName);
    
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

  protected void absorbCollection(Collection col)
  {
    if(col == null) return;
    Iterator iter = col.iterator();
    while(iter.hasNext())
    {
      GenericValue value = (GenericValue)iter.next();
      value.helper = this;
    }
  }
}
