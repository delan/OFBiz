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
public abstract class GenericHelperAbstract implements GenericHelper
{
  ModelReader modelReader;

  UtilCache primaryKeyCache = null;
  UtilCache allCache = null;
  UtilCache andCache = null;
  
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
  public GenericValue makeValue(String entityName, Map fields)
  {
    GenericValue value = new GenericValue(modelReader.getModelEntity(entityName), fields);
    value.helper = this;
    return value;
  }

  /** Creates a Primary Key in the form of a GenericPK without persisting it */
  public GenericPK makePK(String entityName, Map fields)
  {
    GenericPK pk = new GenericPK(modelReader.getModelEntity(entityName), fields);
    return pk;
  }

  /** Find a Generic Entity by its Primary Key
   *@param primaryKey The primary key to find by.
   *@return The GenericValue corresponding to the primaryKey
   */
  public GenericValue findByPrimaryKey(String entityName, Map fields)
  {
    return findByPrimaryKey(makePK(entityName, fields));
  }
  
  /** Find a CACHED Generic Entity by its Primary Key
   *@param primaryKey The primary key to find by.
   *@return The GenericValue corresponding to the primaryKey
   */
  public GenericValue findByPrimaryKeyCache(String entityName, Map fields)
  {
    return findByPrimaryKeyCache(makePK(entityName, fields));
  }

  /** Find a CACHED Generic Entity by its Primary Key
   *@param primaryKey The primary key to find by.
   *@return The GenericValue corresponding to the primaryKey
   */
  public GenericValue findByPrimaryKeyCache(GenericPK primaryKey)
  {
    GenericValue value = (GenericValue)primaryKeyCache.get(primaryKey);
    if(value == null)
    {
      value = findByPrimaryKey(primaryKey);
      primaryKeyCache.put(primaryKey, value);
    }
    return value;
  }

  /** Finds all Generic entities, looking first in the cache; uses orderBy for lookup, but only keys results on the entityName and fields
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param order The fields of the named entity to order the query by; optionall add a " ASC" for ascending or " DESC" for descending
   *@return    Collection containing all Generic entities
   */
  public Collection findAllCache(String entityName, List orderBy)
  {
    Collection col = (Collection)allCache.get(entityName);
    if(col == null)
    {
      col = findAll(entityName, orderBy);
      allCache.put(entityName, col);
    }
    return col;
  }
    
  /** Finds Generic Entity records by all of the specified fields (ie: combined using AND), looking first in the cache; uses orderBy for lookup, but only keys results on the entityName and fields
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param fields The fields of the named entity to query by with their corresponging values
   *@param order The fields of the named entity to order the query by; optionall add a " ASC" for ascending or " DESC" for descending
   *@return Collection of GenericValue instances that match the query
   */
  public Collection findByAndCache(String entityName, Map fields, List orderBy)
  {
    GenericPK tempPK = new GenericPK(modelReader.getModelEntity(entityName), fields);
    Collection col = (Collection)andCache.get(tempPK);
    if(col == null)
    {
      col = findByAnd(entityName, fields, orderBy);
      andCache.put(tempPK, col);
    }
    return col;
  }
}
