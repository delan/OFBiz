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

import java.io.*;
import java.util.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.model.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Generic Entity Value Object - Handles persisntence for any defined entity.
 * <p>Note that this class extends <code>Observable</code> to achieve change notification for
 * <code>Observer</code>s. Whenever a field changes the name of the field will be passed to
 * the <code>notifyObservers()</code> method, and through that to the <code>update()</code> method of each
 * <code>Observer</code>.
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    Wed Aug 08 2001
 *@version    1.0
 */
public class GenericEntity extends Observable implements Map, Serializable, Comparable, Cloneable {

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

    /** Contains the entityName of this entity, necessary for efficiency when creating EJBs */
    public String entityName = null;
    /** Contains the ModelEntity instance that represents the definition of this entity, not to be serialized */
    public transient ModelEntity modelEntity = null;
    /** Denotes whether or not this entity has been modified, or is known to be out of sync with the persistent record */
    public boolean modified = false;

    /** Creates new GenericEntity */
    public GenericEntity() {
        this.entityName = null;
        this.modelEntity = null;
        this.fields = new HashMap();
    }

    /** Creates new GenericEntity */
    public GenericEntity(ModelEntity modelEntity) {
        if (modelEntity == null) throw new IllegalArgumentException("Cannont create a GenericEntity with a null modelEntity parameter");
        this.modelEntity = modelEntity;
        this.entityName = modelEntity.getEntityName();
        this.fields = new HashMap();
    }

    /** Creates new GenericEntity from existing Map */
    public GenericEntity(ModelEntity modelEntity, Map fields) {
        if (modelEntity == null) throw new IllegalArgumentException("Cannont create a GenericEntity with a null modelEntity parameter");
        this.modelEntity = modelEntity;
        this.entityName = modelEntity.getEntityName();
        this.fields = new HashMap();
        setFields(fields);
    }

    /** Copy Constructor: Creates new GenericEntity from existing GenericEntity */
    public GenericEntity(GenericEntity value) {
        this.entityName = value.modelEntity.getEntityName();
        this.modelEntity = value.modelEntity;
        this.fields = (value.fields == null?new HashMap():new HashMap(value.fields));
        this.delegatorName = value.delegatorName;
        this.internalDelegator = value.internalDelegator;
    }

    public boolean isModified() {
        return modified;
    }

    public String getEntityName() {
        return entityName;
    }

    public ModelEntity getModelEntity() {
        if (modelEntity == null) {
            if (entityName != null) modelEntity = this.getDelegator().getModelEntity(entityName);
            if (modelEntity == null) {
                throw new IllegalStateException("[GenericEntity.getModelEntity] could not find modelEntity for entityName " + entityName);
            }
        }
        return modelEntity;
    }

    /** Get the GenericDelegator instance that created this value object and that is repsonsible for it.
     *@return GenericDelegator object
     */
    public GenericDelegator getDelegator() {
        if (internalDelegator == null) {
            if (delegatorName != null) internalDelegator = GenericDelegator.getGenericDelegator(delegatorName);
            if (internalDelegator == null) {
                throw new IllegalStateException("[GenericEntity.getDelegator] could not find delegator with name " + delegatorName);
            }
        }
        return internalDelegator;
    }

    /** Set the GenericDelegator instance that created this value object and that is repsonsible for it. */
    public void setDelegator(GenericDelegator internalDelegator) {
        if (internalDelegator == null) return;
        this.delegatorName = internalDelegator.getDelegatorName();
        this.internalDelegator = internalDelegator;
    }

    public Object get(String name) {
        if (getModelEntity().getField(name) == null) {
            throw new IllegalArgumentException("[GenericEntity.get] \"" + name + "\" is not a field of " + entityName);
            //Debug.logWarning("[GenericEntity.get] \"" + name + "\" is not a field of " + entityName + ", but getting anyway...");
        }
        return fields.get(name);
    }

    /** Returns true if the entity contains all of the primary key fields, but NO others. */
    public boolean isPrimaryKey() {
        TreeSet fieldKeys = new TreeSet(fields.keySet());
        for (int i = 0; i < getModelEntity().getPksSize(); i++) {
            if (!fieldKeys.contains(getModelEntity().getPk(i).getName())) return false;
            fieldKeys.remove(getModelEntity().getPk(i).getName());
        }
        if (!fieldKeys.isEmpty()) return false;
        return true;
    }

    /** Returns true if the entity contains all of the primary key fields. */
    public boolean containsPrimaryKey() {
        TreeSet fieldKeys = new TreeSet(fields.keySet());
        for (int i = 0; i < getModelEntity().getPksSize(); i++) {
            if (!fieldKeys.contains(getModelEntity().getPk(i).getName())) return false;
        }
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
    public synchronized Object set(String name, Object value, boolean setIfNull) {
        if (getModelEntity().getField(name) == null) {
            throw new IllegalArgumentException("[GenericEntity.set] \"" + name + "\" is not a field of " + entityName);
            //Debug.logWarning("[GenericEntity.set] \"" + name + "\" is not a field of " + entityName + ", but setting anyway...");
        }
        if (value != null || setIfNull) {
            if (value instanceof Boolean) {
                value = ((Boolean) value).booleanValue() ? "Y" : "N";
            }
            Object old = fields.put(name, value);
            modified = true;
            this.setChanged();
            this.notifyObservers(name);
            return old;
        } else {
            return fields.get(name);
        }
    }

    /** Sets the named field to the passed value, converting the value from a String to the corrent type using <code>Type.valueOf()</code>
     * @param name The field name to set
     * @param value The String value to convert and set
     */
    public void setString(String name, String value) {
        ModelField field = getModelEntity().getField(name);
        if (field == null) set(name, value); //this will get an error in the set() method...

        ModelFieldType type = null;
        try {
            type = getDelegator().getEntityFieldType(getModelEntity(), field.getType());
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
        if (type == null) throw new IllegalArgumentException("Type " + field.getType() + " not found");
        String fieldType = type.getJavaType();
        if (fieldType.equals("java.lang.String") || fieldType.equals("String"))
            set(name, value);
        else if (fieldType.equals("java.sql.Timestamp") || fieldType.equals("Timestamp"))
            set(name, java.sql.Timestamp.valueOf(value));
        else if (fieldType.equals("java.sql.Time") || fieldType.equals("Time"))
            set(name, java.sql.Time.valueOf(value));
        else if (fieldType.equals("java.sql.Date") || fieldType.equals("Date"))
            set(name, java.sql.Date.valueOf(value));
        else if (fieldType.equals("java.lang.Integer") || fieldType.equals("Integer"))
            set(name, Integer.valueOf(value));
        else if (fieldType.equals("java.lang.Long") || fieldType.equals("Long"))
            set(name, Long.valueOf(value));
        else if (fieldType.equals("java.lang.Float") || fieldType.equals("Float"))
            set(name, Float.valueOf(value));
        else if (fieldType.equals("java.lang.Double") || fieldType.equals("Double"))
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
        if (object == null) return null;
        if (object instanceof java.lang.String)
            return (String) object;
        else
            return object.toString();
    }

    public java.sql.Timestamp getTimestamp(String name) {
        return (java.sql.Timestamp) get(name);
    }

    public java.sql.Time getTime(String name) {
        return (java.sql.Time) get(name);
    }

    public java.sql.Date getDate(String name) {
        return (java.sql.Date) get(name);
    }

    public Integer getInteger(String name) {
        return (Integer) get(name);
    }

    public Long getLong(String name) {
        return (Long) get(name);
    }

    public Float getFloat(String name) {
        return (Float) get(name);
    }

    public Double getDouble(String name) {
        return (Double) get(name);
    }

    public GenericPK getPrimaryKey() {
        Collection pkNames = new LinkedList();
        Iterator iter = this.getModelEntity().getPksIterator();
        while (iter != null && iter.hasNext()) {
            ModelField curField = (ModelField) iter.next();
            pkNames.add(curField.getName());
        }
        return new GenericPK(getModelEntity(), this.getFields(pkNames));
    }

    /** go through the pks and for each one see if there is an entry in fields to set */
    public void setPKFields(Map fields) {
        Iterator iter = this.getModelEntity().getPksIterator();
        while (iter != null && iter.hasNext()) {
            ModelField curField = (ModelField) iter.next();
            if (fields.containsKey(curField.getName())) {
                this.set(curField.getName(), fields.get(curField.getName()));
            }
        }
    }

    /** go through the non-pks and for each one see if there is an entry in fields to set */
    public void setNonPKFields(Map fields) {
        this.setNonPKFields(fields, true);
    }
    
    /** go through the non-pks and for each one see if there is an entry in fields to set */
    public void setNonPKFields(Map fields, boolean setIfEmpty) {
        Iterator iter = this.getModelEntity().getNopksIterator();
        while (iter != null && iter.hasNext()) {
            ModelField curField = (ModelField) iter.next();
            if (fields.containsKey(curField.getName())) {
                Object field = fields.get(curField.getName());
                if (setIfEmpty) {
                    this.set(curField.getName(), field);
                } else {
                    //okay, only set if not empty...
                    if (field != null) {
                        //if it's a String then we need to check length, otherwise set it because it's not null
                        if (field instanceof String) {
                            String fieldStr = (String) field;
                            if (fieldStr.length() > 0) {
                                this.set(curField.getName(), field);
                            }
                        } else {
                            this.set(curField.getName(), field);
                        }
                    }
                }
            }
        }
    }

    /** Returns keys of entity fields
     * @return java.util.Collection
     */
    public Collection getAllKeys() {
        return fields.keySet();
    }

    /** Returns key/value pairs of entity fields
     * @return java.util.Map
     */
    public Map getAllFields() {
        return new HashMap(fields);
    }

    /** Used by clients to specify exactly the fields they are interested in
     * @param keysofFields the name of the fields the client is interested in
     * @return java.util.Map
     */
    public Map getFields(Collection keysofFields) {
        if (keysofFields == null) return null;
        Iterator keys = keysofFields.iterator();
        Object aKey = null;
        HashMap aMap = new HashMap();
        while (keys.hasNext()) {
            aKey = keys.next();
            aMap.put(aKey, this.fields.get(aKey));
        }
        return aMap;
    }

    /** Used by clients to update particular fields in the entity
     * @param keyValuePairs java.util.Map
     */
    public synchronized void setFields(Map keyValuePairs) {
        if (keyValuePairs == null) return;
        Iterator entries = keyValuePairs.entrySet().iterator();
        Map.Entry anEntry = null;
        //this could be implement with Map.putAll, but we'll leave it like this for the extra features it has
        while (entries.hasNext()) {
            anEntry = (Map.Entry) entries.next();
            this.set((String) anEntry.getKey(), anEntry.getValue());
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

    /** Used to indicate if locking is enabled for this entity
     * @return True if locking is enabled
     */
    public boolean lockEnabled() {
        return modelEntity.lock();
    }

    // ======= XML Related Methods ========
    public static Document makeXmlDocument(Collection values) {
        Document document = UtilXml.makeEmptyXmlDocument("entity-engine-xml");
        if (document == null) return null;

        addToXmlDocument(values, document);
        return document;
    }

    public static void addToXmlDocument(Collection values, Document document) {
        if (values == null) return;
        if (document == null) return;

        Element rootElement = document.getDocumentElement();

        Iterator iter = values.iterator();
        while (iter.hasNext()) {
            GenericValue value = (GenericValue) iter.next();
            Element valueElement = value.makeXmlElement(document);
            rootElement.appendChild(valueElement);
        }
    }

    /** Makes an XML Element object with an attribute for each field of the entity
     *@param document The XML Document that the new Element will be part of
     *@return org.w3c.dom.Element object representing this generic entity
     */
    public Element makeXmlElement(Document document) {
        return makeXmlElement(document, null);
    }

    /** Makes an XML Element object with an attribute for each field of the entity
     *@param document The XML Document that the new Element will be part of
     *@param prefix A prefix to put in front of the entity name in the tag name
     *@return org.w3c.dom.Element object representing this generic entity
     */
    public Element makeXmlElement(Document document, String prefix) {
        Element element = null;
        if (prefix == null) prefix = "";
        if (document != null) element = document.createElement(prefix + this.getEntityName());
        //else element = new ElementImpl(null, this.getEntityName());
        if (element == null) return null;

        ModelEntity modelEntity = this.getModelEntity();

        Iterator modelFields = modelEntity.getFieldsIterator();
        while (modelFields.hasNext()) {
            ModelField modelField = (ModelField) modelFields.next();
            String name = modelField.getName();
            String value = this.getString(name);
            if (value != null) {
                if (value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0) {
                    UtilXml.addChildElementCDATAValue(element, name, value, document);
                } else {
                    element.setAttribute(name, value);
                }
            } else {
                //do nothing will null values
            }
        }

        return element;
    }

    /** Determines the equality of two GenericEntity objects, overrides the default equals
     *@param  obj  The object (GenericEntity) to compare this two
     *@return      boolean stating if the two objects are equal
     */
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this.getClass().equals(obj.getClass())) {
            GenericEntity that = (GenericEntity) obj;
            if (this.getEntityName() != null && !this.getEntityName().equals(that.getEntityName())) {
                //Debug.logInfo("[GenericEntity.equals] Not equal: This entityName \"" + this.getEntityName() + "\" is not equal to that entityName \"" + that.getEntityName() + "\"");
                return false;
            }
            if (this.fields.equals(that.fields)) {
                return true;
            } else {
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
        return getEntityName().hashCode() >> 1 + fields.hashCode() >> 1;
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
        while (entries.hasNext()) {
            anEntry = (Map.Entry) entries.next();
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
        if (obj == null) return -1;

        //rather than doing an if instanceof, just cast it and let it throw an exception if
        // it fails, this will be faster for the expected case (that it IS a GenericEntity)
        //if not a GenericEntity throw ClassCastException, as the spec says
        GenericEntity that = (GenericEntity) obj;
        return this.compareTo(that);
    }

    /** Compares this GenericEntity to the passed GenericEntity
     *@param that GenericEntity to compare this to
     *@return int representing the result of the comparison (-1,0, or 1)
     */
    public int compareTo(GenericEntity that) {
        //if null, it will push to the beginning
        if (that == null) return -1;

        int tempResult = this.entityName.compareTo(that.entityName);
        //if they did not match, we know the order, otherwise compare the primary keys
        if (tempResult != 0) return tempResult;

        //both have same entityName, should be the same so let's compare PKs
        for (int i = 0; i < modelEntity.getPksSize(); i++) {
            ModelField curField = modelEntity.getPk(i);
            Comparable thisVal = (Comparable) this.get(curField.getName());
            Comparable thatVal = (Comparable) that.get(curField.getName());
            if (thisVal == null) {
                if (thatVal == null)
                    tempResult = 0;
                //if thisVal is null, but thatVal is not, return 1 to put this earlier in the list
                else
                    tempResult = 1;
            } else {
                //if thatVal is null, put the other earlier in the list
                if (thatVal == null)
                    tempResult = -1;
                else
                    tempResult = thisVal.compareTo(thatVal);
            }
            if (tempResult != 0) return tempResult;
        }

        //okay, if we got here it means the primaryKeys are exactly the SAME, so compare the rest of the fields
        for (int i = 0; i < modelEntity.getNopksSize(); i++) {
            ModelField curField = modelEntity.getNopk(i);
            Comparable thisVal = (Comparable) this.get(curField.getName());
            Comparable thatVal = (Comparable) that.get(curField.getName());
            if (thisVal == null) {
                if (thatVal == null)
                    tempResult = 0;
                //if thisVal is null, but thatVal is not, return 1 to put this earlier in the list
                else
                    tempResult = 1;
            } else {
                //if thatVal is null, put the other earlier in the list
                if (thatVal == null)
                    tempResult = -1;
                else
                    tempResult = thisVal.compareTo(thatVal);
            }
            if (tempResult != 0) return tempResult;
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
    
    // ---- Methods added to implement the Map interface: ----
    
    public Object remove(Object key) {
        return fields.remove(key);
    }
    
    public boolean containsKey(Object key) {
        return fields.containsKey(key);
    }
    
    public java.util.Set entrySet() {
        return fields.entrySet();
    }
    
    public Object put(Object key, Object value) {
        return this.set((String) key, value, true);
    }
    
    public void putAll(java.util.Map map) {
        this.setFields(map);
    }
    
    public void clear() {
        this.fields.clear();
    }
    
    public Object get(Object key) {
        return this.get((String) key);
    }
    
    public java.util.Set keySet() {
        return this.fields.keySet();
    }
    
    public boolean isEmpty() {
        return this.fields.isEmpty();
    }
    
    public java.util.Collection values() {
        return this.fields.values();
    }
    
    public boolean containsValue(Object value) {
        return this.fields.containsValue(value);
    }
    
    public int size() {
        return this.fields.size();
    }
}
