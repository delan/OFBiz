package org.ofbiz.core.entity;

import java.util.*;
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
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    Tue Aug 07 01:10:32 MDT 2001
 *@version    1.0
 */
public interface GenericHelper
{
  /** Gets the name of the server configuration that corresponds to this helper
   *@return server configuration name
   */
  public String getServerName();

  /** Gets the instance of ModelReader that corresponds to this helper
   *@return ModelReader that corresponds to this helper
   */
  public ModelReader getModelReader();
  /** Gets the instance of ModelEntity that corresponds to this helper and the specified entityName
   *@param entityName The name of the entity to get
   *@return ModelEntity that corresponds to this helper and the specified entityName
   */
  public ModelEntity getModelEntity(String entityName);
  
  /** Creates a Entity in the form of a GenericValue without persisting it */
  public GenericValue makeValue(String entityName, Map fields);

  /** Creates a Primary Key in the form of a GenericPK without persisting it */
  public GenericPK makePK(String entityName, Map fields);

  /** Creates a Entity in the form of a GenericValue and write it to the database
   *@return GenericValue instance containing the new instance
   */
  public GenericValue create(String entityName, Map fields);
  /** Creates a Entity in the form of a GenericValue and write it to the database
   *@return GenericValue instance containing the new instance
   */
  public GenericValue create(GenericValue value);
  /** Creates a Entity in the form of a GenericValue and write it to the database
   *@return GenericValue instance containing the new instance
   */
  public GenericValue create(GenericPK primaryKey);

  /** Find a Generic Entity by its Primary Key
   *@param primaryKey The primary key to find by.
   *@return The GenericValue corresponding to the primaryKey
   */
  public GenericValue findByPrimaryKey(GenericPK primaryKey);
  /** Find a Generic Entity by its Primary Key
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param fields The fields of the named entity to query by with their corresponging values
   *@return The GenericValue corresponding to the primaryKey
   */
  public GenericValue findByPrimaryKey(String entityName, Map fields);
  /** Find a CACHED Generic Entity by its Primary Key
   *@param primaryKey The primary key to find by.
   *@return The GenericValue corresponding to the primaryKey
   */
  public GenericValue findByPrimaryKeyCache(GenericPK primaryKey);
  /** Find a CACHED Generic Entity by its Primary Key
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param fields The fields of the named entity to query by with their corresponging values
   *@return The GenericValue corresponding to the primaryKey
   */
  public GenericValue findByPrimaryKeyCache(String entityName, Map fields);

  /** Remove a Generic Entity corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public void removeByPrimaryKey(GenericPK primaryKey);

  /** Finds all Generic entities
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param order The fields of the named entity to order the query by; optionall add a " ASC" for ascending or " DESC" for descending
   *@return    Collection containing all Generic entities
   */
  public Collection findAll(String entityName, List orderBy);
  /** Finds all Generic entities, looking first in the cache; uses orderBy for lookup, but only keys results on the entityName and fields
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param order The fields of the named entity to order the query by; optionall add a " ASC" for ascending or " DESC" for descending
   *@return    Collection containing all Generic entities
   */
  public Collection findAllCache(String entityName, List orderBy);

  /** Finds Generic Entity records by all of the specified fields (ie: combined using AND)
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param fields The fields of the named entity to query by with their corresponging values
   *@param order The fields of the named entity to order the query by; 
   *       optionall add a " ASC" for ascending or " DESC" for descending
   *@return Collection of GenericValue instances that match the query
   */
  public Collection findByAnd(String entityName, Map fields, List orderBy);
  /** Finds Generic Entity records by all of the specified fields (ie: combined 
   *  using AND), looking first in the cache; uses orderBy for lookup, but only 
   *  keys results on the entityName and fields
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param fields The fields of the named entity to query by with their corresponging values
   *@param order The fields of the named entity to order the query by; 
   *       optionall add a " ASC" for ascending or " DESC" for descending
   *@return Collection of GenericValue instances that match the query
   */
  public Collection findByAndCache(String entityName, Map fields, List orderBy);
  
  /** Removes/deletes Generic Entity records found by all of the specified fields (ie: combined using AND)
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param fields The fields of the named entity to query by with their corresponging values
   *@return Collection of GenericValue instances that match the query
   */
  public void removeByAnd(String entityName, Map fields);
  
  /** Store the Entity from the GenericValue to the persistent store
   *@param value GenericValue instance containing the entity
   */
  public void store(GenericValue value);
  
  /** Get the named Related Entity for the GenericValue from the persistent store
   *@param relationName String containing the relation name which is the 
   *       combination of relation.title and relation.rel-entity-name as 
   *       specified in the entity XML definition file
   *@param value GenericValue instance containing the entity
   *@return Collection of GenericValue instances as specified in the relation definition
   */
  public Collection getRelated(String relationName, GenericValue value);

  /** Get the named Related Entity for the GenericValue from the persistent store
   *@param relationName String containing the relation name which is the 
   *       combination of relation.title and relation.rel-entity-name as 
   *       specified in the entity XML definition file
   *@param value GenericValue instance containing the entity
   *@return GenericValue instance as specified in the one-relation definition 
   *@throws IllegalArgumentException if the relation is a many-relation
   */
  public GenericValue getRelatedOne(String relationName, GenericValue value);

  /** Remove the named Related Entity for the GenericValue from the persistent store
   *@param relationName String containing the relation name which is the 
   *       combination of relation.title and relation.rel-entity-name as 
   *       specified in the entity XML definition file
   *@param value GenericValue instance containing the entity
   */
  public void removeRelated(String relationName, GenericValue value);

  /** Refresh the Entity for the GenericValue from the persistent store
   *@param value GenericValue instance containing the entity to refresh
   */
  public void refresh(GenericValue value);

  /** Get the next guaranteed unique seq id from the sequence with the given 
   *  sequence name; if the named sequence doesn't exist, it will be created
   *@param seqName The name of the sequence to get the next seq id from
   *@return Long with the next seq id for the given sequence name
   */
  public Long getNextSeqId(String seqName);

  /** Remove a CACHED Generic Entity (Collection) from the cache, either a PK, ByAnd, or All 
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param fields The fields of the named entity to query by with their corresponging values
   *@return The GenericValue corresponding to the primaryKey
   */
  public void clearCacheLine(String entityName, Map fields);

  /** Remove a CACHED Generic Entity from the cache by its primary key
   *@param primaryKey The primary key to find by.
   *@return The GenericValue corresponding to the primaryKey
   */
  public void clearCacheLine(GenericPK primaryKey);
}
