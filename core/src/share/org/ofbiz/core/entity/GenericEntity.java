package org.ofbiz.core.entity;

import java.io.*;
import java.net.*;
import java.util.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.model.*;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p><b>Title:</b> Generic Entity Value Object
 * <p><b>Description:</b> Handles persisntence for any defined entity.
 * Note that this class extends <code>Observable</code> to achieve change notification for
 * <code>Observer</code>s. Whenever a field changes the name of the field will be passed to
 * the <code>notifyObservers()</code> method, and through that to the <code>update()</code> method of each
 * <code>Observer</code>.
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
 *@author <a href='mailto:jonesde@ofbiz.org'>David E. Jones</a>
 *@created    Wed Aug 08 2001
 *@version    1.0
 */
public class GenericEntity extends Observable implements Serializable, Comparable, Cloneable {
  /** Name of the GenericDelegator, used to reget the GenericDelegator when deserialized */
  public String delegatorName = null;
  /** Reference to an instance of GenericDelegator used to do some basic operations on this entity value. If null various methods in this class will fail. This is automatically set by the GenericDelegator for all GenericValue objects instantiated through it. You may set this manually for objects you instantiate manually, but it is optional. */
  public transient GenericDelegator internalDelegator = null;
  
  /** Contains the fields for this entity. Note that this should always be a
   *  HashMap to allow for two things: non-synchronized reads (synchronized
   *  writes are done through synchronized setters) and being able to store
   *  null values. Null values are important because with them we can distinguish
   *  between desiring to set a value to null and desiring to not modify the
   *  current value on an update.
   */
  protected Map fields;
  /** Map to store related entities that will be updated if modified when this entity is stored; populated with preStoreRelated(String, Collection). This is here so that it can be implicitly stored in the same transaction context. */
  public Collection otherToStore = null;
  
  /** Contains the entityName of this entity, necessary for efficiency when creating EJBs */
  public String entityName = null;
  /** Contains the ModelEntity instance that represents the definition of this entity, not to be serialized */
  public transient ModelEntity modelEntity = null;
  /** Denotes whether or not this entity has been modified, or is known to be out of sync with the persistent record */
  public boolean modified = false;
  
  /** Creates new GenericEntity */
  public GenericEntity() { this.entityName = null; this.modelEntity = null; this.fields = new HashMap(); }
  /** Creates new GenericEntity */
  public GenericEntity(ModelEntity modelEntity) {
    if(modelEntity == null) throw new IllegalArgumentException("Cannont create a GenericEntity with a null modelEntity parameter");
    this.modelEntity = modelEntity;
    this.entityName = modelEntity.entityName;
    this.fields = new HashMap();
  }
  /** Creates new GenericEntity from existing Map */
  public GenericEntity(ModelEntity modelEntity, Map fields) {
    if(modelEntity == null) throw new IllegalArgumentException("Cannont create a GenericEntity with a null modelEntity parameter");
    this.modelEntity = modelEntity;
    this.entityName = modelEntity.entityName;
    this.fields = (fields==null?new HashMap():new HashMap(fields));
  }
  /** Copy Constructor: Creates new GenericEntity from existing GenericEntity */
  public GenericEntity(GenericEntity value) { this.entityName = value.modelEntity.entityName; this.modelEntity = value.modelEntity; this.fields = (value.fields==null?new HashMap():new HashMap(value.fields)); }
  
  public boolean isModified() { return modified; }
  
  public String getEntityName() { return entityName; }
  public ModelEntity getModelEntity() {
    if(modelEntity == null) {
      if(entityName != null) modelEntity = this.getDelegator().getModelEntity(entityName);
      if(modelEntity == null) {
        throw new IllegalStateException("[GenericEntity.getModelEntity] could not find modelEntity for entityName " + entityName);
      }
    }
    return modelEntity; 
  }
  
  /** Get the GenericDelegator implementation instance that created this value object and that is repsonsible for it.
   *@return GenericDelegator object
   */
  public GenericDelegator getDelegator() {
    if(internalDelegator == null) {
      if(delegatorName != null) internalDelegator = GenericDelegator.getGenericDelegator(delegatorName);
      if(internalDelegator == null) {
        throw new IllegalStateException("[GenericEntity.getDelegator] could not find delegator with name " + delegatorName);
      }
    }
    return internalDelegator;
  }
  /** Get the GenericDelegator implementation instance that created this value object and that is repsonsible for it.
   *@return GenericDelegator object
   */
  public void setDelegator(GenericDelegator internalDelegator) {
    if(internalDelegator == null) return;
    this.delegatorName = internalDelegator.getDelegatorName();
    this.internalDelegator = internalDelegator;
  }

  public Object get(String name) {
    if(getModelEntity().getField(name) == null) {
      throw new IllegalArgumentException("[GenericEntity.get] \"" + name + "\" is not a field of " + entityName);
      //Debug.logWarning("[GenericEntity.get] \"" + name + "\" is not a field of " + entityName + ", but getting anyway...");
    }
    return fields.get(name);
  }
  
  public boolean isPrimaryKey() {
    Vector pks = getModelEntity().pks;
    TreeSet fieldKeys = new TreeSet(fields.keySet());
    for(int i=0; i<pks.size(); i++) {
      if(!fieldKeys.contains(((ModelField)pks.elementAt(i)).name)) return false;
      fieldKeys.remove(((ModelField)pks.elementAt(i)).name);
    }
    if(!fieldKeys.isEmpty()) return false;
    return true;
  }
  
  /** Sets the named field to the passed value, even if the value is null
   * @param name The field name to set
   * @param value The value to set
   */
  public void set(String name, Object value) {
    set(name, value, true);
  }
  /** Sets the named field to the passed value. If value is null, it is only
   *  set if the setIfNull parameter is true. This is useful because an update
   *  will only set values that are included in the HashMap and will store null
   *  values in the HashMap to the datastore. If a value is not in the HashMap,
   *  it will be left unmodified in the datastore.
   * @param name The field name to set
   * @param value The value to set
   * @param setIfNull Specifies whether or not to set the value if it is null
   */
  public synchronized void set(String name, Object value, boolean setIfNull) {
    if(getModelEntity().getField(name) == null) {
      throw new IllegalArgumentException("[GenericEntity.set] \"" + name + "\" is not a field of " + entityName);
      //Debug.logWarning("[GenericEntity.set] \"" + name + "\" is not a field of " + entityName + ", but setting anyway...");
    }
    if(value != null || setIfNull) {
      if (value instanceof Boolean) {
        value = ((Boolean) value).booleanValue() ? "Y" : "N";
      }
      fields.put(name, value);
      modified = true;
      this.setChanged();
      this.notifyObservers(name);
    }
  }
  
  /** Sets the named field to the passed value, converting the value from a String to the corrent type using <code>Type.valueOf()</code>
   * @param name The field name to set
   * @param value The String value to convert and set
   */
  public void setString(String name, String value) {
    ModelField field = getModelEntity().getField(name);
    if(field == null) set(name, value); //this will get an error in the set() method...
    
    ModelFieldType type = null;
    try { type = getDelegator().getEntityFieldType(getModelEntity(), field.type); }
    catch(GenericEntityException e) { Debug.logWarning(e); }
    if(type == null) throw new IllegalArgumentException("Type " + field.type + " not found");
    String fieldType = type.javaType;
    if(fieldType.equals("java.lang.String") || fieldType.equals("String"))
      set(name, value);
    else if(fieldType.equals("java.sql.Timestamp") || fieldType.equals("Timestamp"))
      set(name, java.sql.Timestamp.valueOf(value));
    else if(fieldType.equals("java.sql.Time") || fieldType.equals("Time"))
      set(name, java.sql.Time.valueOf(value));
    else if(fieldType.equals("java.sql.Date") || fieldType.equals("Date"))
      set(name, java.sql.Date.valueOf(value));
    else if(fieldType.equals("java.lang.Integer") || fieldType.equals("Integer"))
      set(name, Integer.valueOf(value));
    else if(fieldType.equals("java.lang.Long") || fieldType.equals("Long"))
      set(name, Long.valueOf(value));
    else if(fieldType.equals("java.lang.Float") || fieldType.equals("Float"))
      set(name, Float.valueOf(value));
    else if(fieldType.equals("java.lang.Double") || fieldType.equals("Double"))
      set(name, Double.valueOf(value));
    else {
      throw new IllegalArgumentException("Java type " + fieldType + " not currently supported. Sorry.");
    }
  }
  
  public Boolean getBoolean(String name) {
    String value = getString(name);
    Boolean result;
    if (value == null) {
      result = null;
    } else if ("Y".equals(value)) {
      result = Boolean.TRUE;
    } else if ("N".equals(value)) {
      result = Boolean.FALSE;
    } else {
      throw new IllegalArgumentException("getBoolean could not map '" + value + "' to Boolean type");
    }
    return result;
  }
  
  //might be nice to add some ClassCastException handling... and auto conversion? hmmm...
  public String getString(String name) { 
    Object object = get(name);
    if(object == null) return null;
    if(object instanceof java.lang.String) return (String)object;
    else return object.toString();
  }
  public java.sql.Timestamp getTimestamp(String name) { return (java.sql.Timestamp)get(name); }
  public java.sql.Time getTime(String name) { return (java.sql.Time)get(name); }
  public java.sql.Date getDate(String name) { return (java.sql.Date)get(name); }
  public Integer getInteger(String name) { return (Integer)get(name); }
  public Long getLong(String name) { return (Long)get(name); }
  public Float getFloat(String name) { return (Float)get(name); }
  public Double getDouble(String name) { return (Double)get(name); }
  
  public GenericPK getPrimaryKey() {
    Collection pkNames = new LinkedList();
    Iterator iter = this.getModelEntity().pks.iterator();
    while(iter != null && iter.hasNext()) {
      ModelField curField = (ModelField)iter.next();
      pkNames.add(curField.name);
    }
    return new GenericPK(getModelEntity(), this.getFields(pkNames));
  }
  public void setNonPKFields(Map fields) {
    //make a copy of the fields, remove the primary keys, set the rest
    Map keyValuePairs = new HashMap(fields);
    Iterator iter = this.getModelEntity().pks.iterator();
    while(iter != null && iter.hasNext()) {
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
  public Map getFields(Collection keysofFields) {
    if(keysofFields == null) return null;
    Iterator keys = keysofFields.iterator();
    Object aKey = null;
    HashMap aMap = new HashMap();
    while(keys.hasNext()) {
      aKey = keys.next();
      aMap.put(aKey, this.fields.get(aKey));
    }
    return aMap;
  }
  /** Used by clients to update particular fields in the entity
   * @param keyValuePairs java.util.Map
   */
  public synchronized void setFields(Map keyValuePairs) {
    if(keyValuePairs == null) return;
    Iterator entries = keyValuePairs.entrySet().iterator();
    Map.Entry anEntry = null;
    //this could be implement with Map.putAll, but we'll leave it like this for the extra features it has
    while(entries.hasNext()) {
      anEntry = (Map.Entry)entries.next();
      this.set((String)anEntry.getKey(), anEntry.getValue());
    }
  }
  
  public boolean matchesFields(Map keyValuePairs) {
    if (fields == null) return true;
    Iterator keyIter = keyValuePairs.keySet().iterator();
    while (keyIter.hasNext()) {
      String key = (String) keyIter.next();
      if (!UtilValidate.areEqual(keyValuePairs.get(key), this.fields.get(key))) {
        return false;
      }
    }
    return true;
  }

// ======= XML Related Methods ========  
  public static Document makeXmlDocument(Collection values) {
    Document document = UtilXml.makeEmptyXmlDocument();
    if(document == null) return null;
    
    Element rootElement = document.createElement("entity-engine-xml");
    document.appendChild(rootElement);
    
    addToXmlDocument(values, document);
    return document;
  }

  public static void addToXmlDocument(Collection values, Document document) {
    if(values == null) return;
    if(document == null) return;

    Element rootElement = document.getDocumentElement();

    Iterator iter = values.iterator();
    while(iter.hasNext()) {
      GenericValue value = (GenericValue)iter.next();
      Element valueElement = value.makeXmlElement(document);
      rootElement.appendChild(valueElement);
    }
  }

  /** Makes an XML Element object with an attribute for each field of the entity
   *@param document The XML Document that the new Element will be part of
   *@return org.w3c.dom.Element object representing this generic entity
   */
  public Element makeXmlElement(Document document) {
    Element element = null;
    if(document != null) element = document.createElement(this.getEntityName());
    //else element = new ElementImpl(null, this.getEntityName());
    if(element == null) return null;
    
    ModelEntity modelEntity = this.getModelEntity();
    
    Iterator modelFields = modelEntity.fields.iterator();
    while(modelFields.hasNext()) {
      ModelField modelField = (ModelField)modelFields.next();
      String name = modelField.name;
      String value = this.getString(name);
      if(value == null) value = "";
      element.setAttribute(name, value);
    }
    
    return element;
  }
  
  /** Determines the equality of two GenericEntity objects, overrides the default equals
   *@param  obj  The object (GenericEntity) to compare this two
   *@return      boolean stating if the two objects are equal
   */
  public boolean equals(Object obj) {
    if(this.getClass().equals(obj.getClass())) {
      GenericEntity that = (GenericEntity)obj;
      if(this.getEntityName() != null && !this.getEntityName().equals(that.getEntityName())) {
        //Debug.logInfo("[GenericEntity.equals] Not equal: This entityName \"" + this.getEntityName() + "\" is not equal to that entityName \"" + that.getEntityName() + "\"");
        return false;
      }
      if(this.fields.equals(that.fields)) {
        return true;
      }
      else {
        //Debug.logInfo("[GenericEntity.equals] Not equal: Fields of this entity: \n" + this.toString() + "\n are not equal to fields of that entity:\n" + that.toString());
      }
    }
    return false;
  }
  
  /** Creates a hashCode for the entity, using the default String hashCode and Map hashCode, overrides the default hashCode
   *@return    Hashcode corresponding to this entity
   */
  public int hashCode() {
    //divide both by two (shift to right one bit) to maintain scale and add together
    return getEntityName().hashCode()>>1 + fields.hashCode()>>1;
  }
  
  /** Creates a String for the entity, overrides the default toString
   *@return    String corresponding to this entity
   */
  public String toString() {
    StringBuffer theString = new StringBuffer();
    theString.append("[GenericEntity:");
    theString.append(getEntityName());
    theString.append("]");
    
    Iterator entries = fields.entrySet().iterator();
    Map.Entry anEntry = null;
    while(entries.hasNext()) {
      anEntry = (Map.Entry)entries.next();
      theString.append("[");
      theString.append(anEntry.getKey());
      theString.append(",");
      theString.append(anEntry.getValue());
      theString.append("]");
    }
    return theString.toString();
  }
  
  /** Compares this GenericEntity to the passed object
   *@param obj Object to compare this to
   *@return int representing the result of the comparison (-1,0, or 1)
   */
  public int compareTo(Object obj) {
    //if null, it will push to the beginning
    if(obj == null) return -1;
    
    //rather than doing an if instanceof, just cast it and let it throw an exception if
    // it fails, this will be faster for the expected case (that it IS a GenericEntity)
    //if not a GenericEntity throw ClassCastException, as the spec says
    GenericEntity that = (GenericEntity)obj;
    return this.compareTo(that);
  }
  
  /** Compares this GenericEntity to the passed object
   *@param obj Object to compare this to
   *@return int representing the result of the comparison (-1,0, or 1)
   */
  public int compareTo(GenericEntity that) {
    //if null, it will push to the beginning
    if(that == null) return -1;
    
    int tempResult = this.entityName.compareTo(that.entityName);
    //if they did not match, we know the order, otherwise compare the primary keys
    if(tempResult != 0) return tempResult;
    
    //both have same entityName, should be the same so let's compare PKs
    for(int i=0; i<modelEntity.pks.size(); i++) {
      ModelField curField = (ModelField)modelEntity.pks.get(i);
      Comparable thisVal = (Comparable)this.get(curField.name);
      Comparable thatVal = (Comparable)that.get(curField.name);
      if(thisVal == null) {
        if(thatVal == null) tempResult = 0;
        //if thisVal is null, but thatVal is not, return 1 to put this earlier in the list
        else tempResult = 1;
      }
      else {
        //if thatVal is null, put the other earlier in the list
        if(thatVal == null) tempResult = -1;
        else tempResult = thisVal.compareTo(thatVal);
      }
      if(tempResult != 0) return tempResult;
    }
    
    //okay, if we got here it means the primaryKeys are exactly the SAME, so compare the rest of the fields
    for(int i=0; i<modelEntity.nopks.size(); i++) {
      ModelField curField = (ModelField)modelEntity.nopks.get(i);
      Comparable thisVal = (Comparable)this.get(curField.name);
      Comparable thatVal = (Comparable)that.get(curField.name);
      if(thisVal == null) {
        if(thatVal == null) tempResult = 0;
        //if thisVal is null, but thatVal is not, return 1 to put this earlier in the list
        else tempResult = 1;
      }
      else {
        //if thatVal is null, put the other earlier in the list
        if(thatVal == null) tempResult = -1;
        else tempResult = thisVal.compareTo(thatVal);
      }
      if(tempResult != 0) return tempResult;
    }
    
    //if we got here it means the two are exactly the same, so return tempResult, which should be 0
    return tempResult;
  }
  
  /** Clones this GenericEntity, this is a shallow clone & uses the default shallow HashMap clone
   *@return Object that is a clone of this GenericEntity
   */
  public Object clone() {
    GenericEntity newEntity = new GenericEntity(this);
    newEntity.setDelegator(internalDelegator);
    return newEntity;
  }
}
