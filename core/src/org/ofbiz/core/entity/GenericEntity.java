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
public class GenericEntity implements Serializable 
{
  /** Contains the fields for this entity */
  protected Map fields;
  /** Contains the entityName from the XML definition of this entity */
  protected String entityName;
  /** Denotes whether or not this entity has been modified, or is known to be out of sync with the persistent record */
  public boolean modified = false;

  /** Creates new GenericEntity */
  public GenericEntity() { this.entityName = null; this.fields = null; }
  /** Creates new GenericEntity */
  public GenericEntity(String entityName) { this.entityName = entityName; this.fields = new HashMap(); }
  /** Creates new GenericEntity from existing Map */
  public GenericEntity(String entityName, Map fields) { this.entityName = entityName; this.fields = new HashMap(fields); }
  /** Copy Constructor: Creates new GenericEntity from existing GenericEntity */
  public GenericEntity(GenericEntity value) { this.entityName = value.entityName; this.fields = new HashMap(value.fields); }
  
  public boolean isModified() { return modified; }
  
  public String getEntityName() { return entityName; }
  public ModelEntity getModelEntity() { return ModelReader.getModelEntity(entityName); }

  public Object get(String name) { return fields.get(name); }
  //would be nice to add valid field name checking for the given entityName
  public synchronized void set(String name, Object value) { fields.put(name, value); modified = true; }
  
  //might be nice to add some ClassCastException handling...
  public String getString(String name) { return (String)fields.get(name); }
  public java.sql.Timestamp getTimestamp(String name) { return (java.sql.Timestamp)fields.get(name); }
  public java.sql.Time getTime(String name) { return (java.sql.Time)fields.get(name); }
  public java.sql.Date getDate(String name) { return (java.sql.Date)fields.get(name); }
  public Integer getInteger(String name) { return (Integer)fields.get(name); }
  public Long getLong(String name) { return (Long)fields.get(name); }
  public Float getFloat(String name) { return (Float)fields.get(name); }
  public Double getDouble(String name) { return (Double)fields.get(name); }
  
  public GenericPK getPrimaryKey() 
  { 
    Collection pkNames = new LinkedList();
    Iterator iter = this.getModelEntity().pks.iterator();
    while(iter != null && iter.hasNext())
    {
      ModelField curField = (ModelField)iter.next();
      pkNames.add(curField.name);
    }
    return new GenericPK(entityName, this.getFields(pkNames));
  }
  public void setNonPKFields(Map fields)
  {
    //make a copy of the fields, remove the primary keys, set the rest
    Map keyValuePairs = new HashMap(fields);
    Iterator iter = this.getModelEntity().pks.iterator();
    while(iter != null && iter.hasNext())
    {
      ModelField curField = (ModelField)iter.next();
      keyValuePairs.remove(curField.name);
    }
    this.setFields(keyValuePairs);
  }
  
  /** Returns keys of entity fields
   * @return java.util.Collection
   */
  public Collection getAllKeys() { return fields.keySet(); }
  /** Returns key/value pairs of entity fields
   * @return java.util.Map
   */
  public Map getAllFields() { return new HashMap(fields); }
  /** Used by clients to specify exactly the fields they are interested in
   * @param keysofFields the name of the fields the client is interested in
   * @return java.util.Map
   */
  public Map getFields(Collection keysofFields)
  {
    if(keysofFields == null) return null;
    Iterator keys = keysofFields.iterator();
    Object aKey = null;
    HashMap aMap = new HashMap();
    while(keys.hasNext())
    {
      aKey = keys.next();
      aMap.put(aKey, this.fields.get(aKey));
    }
    return aMap;
  }
  /** Used by clients to update particular fields in the entity
   * @param keyValuePairs java.util.Map
   */
  public synchronized void setFields(Map keyValuePairs)
  {
    if(keyValuePairs == null) return;
    Iterator entries = keyValuePairs.entrySet().iterator();
    Map.Entry anEntry = null;
    //this could be implement with Map.putAll, but we'll leave it like this so we can add validation later
    while(entries.hasNext())
    {
      anEntry = (Map.Entry)entries.next();
      this.fields.put(anEntry.getKey(), anEntry.getValue());
      modified = true;
    }
  }

  /** Determines the equality of two GenericEntity objects, overrides the default equals
   *@param  obj  The object (GenericEntity) to compare this two
   *@return      boolean stating if the two objects are equal
   */
  public boolean equals(Object obj)
  {
    if(this.getClass().equals(obj.getClass()))
    {
      GenericEntity that = (GenericEntity)obj;
      if(this.entityName != null && !this.entityName.equals(that.entityName)) return false;
      return this.fields.equals(that.fields);
    }
    return false;
  }

  /** Creates a hashCode for the entity, using the default String hashCode and Map hashCode, overrides the default hashCode
   *@return    Hashcode corresponding to this entity
   */
  public int hashCode()
  {
    return entityName.hashCode() + fields.hashCode();
  }

  /** Creates a String for the entity, overrides the default toString
   *@return    String corresponding to this entity
   */
  public String toString()
  {
    StringBuffer theString = new StringBuffer();
    theString.append("[GenericEntity:");
    theString.append(entityName);
    theString.append("]");
    
    Iterator entries = fields.entrySet().iterator();
    Map.Entry anEntry = null;
    while(entries.hasNext())
    {
      anEntry = (Map.Entry)entries.next();
      theString.append("[");
      theString.append(anEntry.getKey().toString());
      theString.append(",");
      theString.append(anEntry.getValue().toString());
      theString.append("]");
    }
    return theString.toString();
  }
}
