package org.ofbiz.core.entity;

import java.io.*;
import java.util.*;
import org.ofbiz.core.entity.model.*;

/**
 * <p><b>Title:</b> Generic Entity Value Object
 * <p><b>Description:</b> Handles persisntence for any defined entity.
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
 *@created    Wed Aug 08 2001
 *@version    1.0
 */
public class GenericValue extends GenericEntity
{
  /** Reference to an instance of GenericHelper used to do some basic operations on this entity value. If null various methods in this class will fail. This is automatically set by the GenericHelper implementations for all GenericValue objects instantiated through them. You may set this manually for objects you instantiate manually, but it is optional. */
  public transient GenericHelper helper = null;
  /** Hashtable to cache various related entity collections */
  public transient Map relatedCache = null;
  
  /** Creates new GenericValue */
  public GenericValue(String entityName) { super(entityName); }
  /** Creates new GenericValue from existing Map */
  public GenericValue(String entityName, Map fields) { super(entityName, fields); }
  /** Creates new GenericValue from existing GenericValue */
  public GenericValue(GenericValue value) { super(value); }
  /** Creates new GenericValue from existing GenericValue */
  public GenericValue(GenericPK primaryKey) { super(primaryKey); }
  /** Creates new GenericPK from Map based on parameters */
  public GenericValue(String entityName, String name1, Object value1) { super(entityName, name1, value1); }
  /** Creates new GenericPK from Map based on parameters */
  public GenericValue(String entityName, String name1, Object value1, String name2, Object value2) { super(entityName, name1, value1, name2, value2); }
  /** Creates new GenericPK from Map based on parameters */
  public GenericValue(String entityName, String name1, Object value1, String name2, Object value2, String name3, Object value3) { super(entityName, name1, value1, name2, value2, name3, value3); }
  /** Creates new GenericPK from Map based on parameters */
  public GenericValue(String entityName, String name1, Object value1, String name2, Object value2, String name3, Object value3, String name4, Object value4) { super(entityName, name1, value1, name2, value2, name3, value3, name4, value4); }
  
  public void store() { helper.store(this); }
  public void remove() { helper.removeByPrimaryKey(getPrimaryKey()); }
  
  /** Get the named Related Entity for the GenericValue from the persistent store
   *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
   *@return Collection of GenericValue instances as specified in the relation definition
   */
  public Collection getRelated(String relationName) { return helper.getRelated(relationName, this); }
  /** Get the named Related Entity for the GenericValue from the persistent store, looking first in a cache associated with this entity which is destroyed with this ValueObject when no longer used.
   *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
   *@return Collection of GenericValue instances as specified in the relation definition
   */
  public Collection getRelatedCache(String relationName) 
  { 
    if(relatedCache == null) relatedCache = new Hashtable();
    Collection col = (Collection)relatedCache.get(relationName);
    if(col == null)
    {
      col = getRelated(relationName);
      relatedCache.put(relationName, col);
    }
    return col;
  }

  /** Remove the named Related Entity for the GenericValue from the persistent store
   *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
   */
  public void removeRelated(String relationName) { helper.removeRelated(relationName, this); }
  /** PreStore the named Related Entity instances so that on the next create or update, these will be updated in the same transaction
   *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
   *@param entities Collection of GenericValue instances corresponding to the named relation that will be set or created if modified
   */
  public void preStoreRelated(String relationName, Collection entities) { relatedToStore.put(relationName, entities); }
}
