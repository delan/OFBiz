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
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    Wed Aug 08 2001
 *@version    1.0
 */
public class GenericValue extends GenericEntity {
  /** Hashtable to cache various related entity collections */
  public transient Map relatedCache = null;
  /** Hashtable to cache various related cardinality one entity collections */
  public transient Map relatedOneCache = null;
  
  /** Creates new GenericValue */
  public GenericValue(ModelEntity modelEntity) { super(modelEntity); }
  /** Creates new GenericValue from existing Map */
  public GenericValue(ModelEntity modelEntity, Map fields) { super(modelEntity, fields); }
  /** Creates new GenericValue from existing GenericValue */
  public GenericValue(GenericValue value) { super(value); }
  /** Creates new GenericValue from existing GenericValue */
  public GenericValue(GenericPK primaryKey) { super(primaryKey); }
  
  public GenericValue create() throws GenericEntityException { return delegator.create(this); }
  public void store() throws GenericEntityException { delegator.store(this); }
  public void remove() throws GenericEntityException { delegator.removeByPrimaryKey(getPrimaryKey()); }
  public void refresh() throws GenericEntityException { delegator.refresh(this); }
  
  /** Get the named Related Entity for the GenericValue from the persistent store
   *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
   *@return Collection of GenericValue instances as specified in the relation definition
   */
  public Collection getRelated(String relationName) throws GenericEntityException { 
    return delegator.getRelated(relationName, this);
  }
  /** Get the named Related Entity for the GenericValue from the persistent
   *  store, looking first in the global generic cache (for the moment this isn't true, is same as EmbeddedCache variant)
   *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
   *@return Collection of GenericValue instances as specified in the relation definition
   */
  public Collection getRelatedCache(String relationName) throws GenericEntityException {
    return delegator.getRelatedCache(relationName, this);
  }
  /** Get the named Related Entity for the GenericValue from the persistent
   *  store, looking first in a cache associated with this entity which is
   *  destroyed with this ValueObject when no longer used.
   *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
   *@return Collection of GenericValue instances as specified in the relation definition
   */
  public Collection getRelatedEmbeddedCache(String relationName) throws GenericEntityException {
    if(relatedCache == null) relatedCache = new Hashtable();
    Collection col = (Collection)relatedCache.get(relationName);
    if(col == null) {
      col = getRelated(relationName);
      relatedCache.put(relationName, col);
    }
    return col;
  }
  
  /** Get the named Related Entity for the GenericValue from the persistent store
   *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
   *@return Collection of GenericValue instances as specified in the relation definition
   */
  public GenericValue getRelatedOne(String relationName) throws GenericEntityException {
    return delegator.getRelatedOne(relationName, this);
  }
  /** Get the named Related Entity for the GenericValue from the persistent
   *  store, looking first in the global generic cache (for the moment this isn't true, is same as EmbeddedCache variant)
   *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
   *@return Collection of GenericValue instances as specified in the relation definition
   */
  public GenericValue getRelatedOneCache(String relationName) throws GenericEntityException {
    return delegator.getRelatedOneCache(relationName, this);
  }
  /** Get the named Related Entity for the GenericValue from the persistent
   *  store, looking first in a cache associated with this entity which is
   *  destroyed with this ValueObject when no longer used.
   *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
   *@return Collection of GenericValue instances as specified in the relation definition
   */
  public GenericValue getRelatedOneEmbeddedCache(String relationName) throws GenericEntityException {
    if(relatedOneCache == null) relatedOneCache = new Hashtable();
    GenericValue value = (GenericValue)relatedOneCache.get(relationName);
    if(value == null) {
      value = getRelatedOne(relationName);
      if(value != null) relatedOneCache.put(relationName, value);
    }
    return value;
  }
  
  /** Get the named Related Entity for the GenericValue from the persistent store and filter it
   *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
   *@param fields the fields that must equal in order to keep
   *@return Collection of GenericValue instances as specified in the relation definition
   */
  public Collection getRelatedByAnd(String relationName, Map fields) throws GenericEntityException {
    return delegator.getRelatedByAnd(relationName, fields, this);
  }
  /** Get the named Related Entity for the GenericValue from the persistent
   *  store and filter it, looking first in the global generic cache (for the moment this isn't true, is same as EmbeddedCache variant)
   *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
   *@param fields the fields that must equal in order to keep
   *@return Collection of GenericValue instances as specified in the relation definition
   */
  public Collection getRelatedByAndCache(String relationName, Map fields) throws GenericEntityException {
    return EntityUtil.filterByAnd(delegator.getRelatedCache(relationName, this), fields);
  }
  /** Get the named Related Entity for the GenericValue from the persistent
   *  store and filter it, looking first in a cache associated with this entity which is
   *  destroyed with this ValueObject when no longer used.
   *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
   *@param fields the fields that must equal in order to keep
   *@return Collection of GenericValue instances as specified in the relation definition
   */
  public Collection getRelatedByAndEmbeddedCache(String relationName, Map fields) throws GenericEntityException {
    return EntityUtil.filterByAnd(getRelatedEmbeddedCache(relationName), fields);
  }
  
  /** Remove the named Related Entity for the GenericValue from the persistent store
   *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
   */
  public void removeRelated(String relationName) throws GenericEntityException {
    delegator.removeRelated(relationName, this); 
  }
  /** PreStore the Entity instance so that on the next create or update, this will be updated in the same transaction
   *@param entity GenericValue instance that will be set or created if modified
   */
  public void preStoreOther(GenericValue entity) { getOtherToStore().add(entity); }
  /** PreStore the Entity instances so that on the next create or update, these will be updated in the same transaction
   *@param entities Collection of GenericValue instances that will be set or created if modified
   */
  public void preStoreOthers(Collection entities) { getOtherToStore().addAll(entities); }
  protected Collection getOtherToStore() { 
    if(otherToStore == null) otherToStore = new LinkedList();
    return otherToStore;
  }
}
