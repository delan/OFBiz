/*
 * $Id$
 *
 * Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.core.entity.model;

import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;

/**
 * Generic Entity - Entity model class
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    May 15, 2001
 *@version    1.0
 */

public class ModelEntity {
    public static final String module = ModelEntity.class.getName();

    /** The name of the time stamp field for locking/syncronization */
    public static final String STAMP_FIELD = "lastUpdatedStamp";

    /** The ModelReader that created this Entity */
    protected ModelReader modelReader = null;

    /** The entity-name of the Entity */
    protected String entityName = "";
    /** The table-name of the Entity */
    protected String tableName = "";

    /** The package-name of the Entity */
    protected String packageName = "";

    /** The entity-name of the Entity that this Entity is dependent on, if empty then no dependency */
    protected String dependentOn = "";

    //Strings to go in the comment header.
    /** The title for documentation purposes */
    protected String title = "";
    /** The description for documentation purposes */
    protected String description = "";
    /** The copyright for documentation purposes */
    protected String copyright = "";
    /** The author for documentation purposes */
    protected String author = "";
    /** The version for documentation purposes */
    protected String version = "";

    /** A Vector of the Field objects for the Entity */
    protected Vector fields = new Vector();
    /** A Vector of the Field objects for the Entity, one for each Primary Key */
    protected Vector pks = new Vector();
    /** A Vector of the Field objects for the Entity, one for each NON Primary Key */
    protected Vector nopks = new Vector();
    /** relations defining relationships between this entity and other entities */
    protected Vector relations = new Vector();

    /** An indicator to specify if this entity requires locking for updates */
    protected boolean doLock = false;

    // ===== CONSTRUCTORS =====
    /** Default Constructor */
    public ModelEntity() {
    }
    
    /** XML Constructor */
    public ModelEntity(ModelReader reader, Element entityElement, Element docElement, UtilTimer utilTimer, Hashtable docElementValues) {
        this.modelReader = reader;

        if (utilTimer != null) utilTimer.timerString("  createModelEntity: before general/basic info");
        this.populateBasicInfo(entityElement, docElement, docElementValues);

        if (utilTimer != null) utilTimer.timerString("  createModelEntity: before fields");
        NodeList fieldList = entityElement.getElementsByTagName("field");
        for (int i = 0; i < fieldList.getLength(); i++) {
            ModelField field = reader.createModelField((Element) fieldList.item(i), docElement, docElementValues);
            if (field != null) this.fields.add(field);
        }

        if (utilTimer != null) utilTimer.timerString("  createModelEntity: before prim-keys");
        NodeList pkList = entityElement.getElementsByTagName("prim-key");
        for (int i = 0; i < pkList.getLength(); i++) {
            ModelField field = reader.findModelField(this, ((Element) pkList.item(i)).getAttribute("field"));
            if (field != null) {
                this.pks.add(field);
                field.isPk = true;
            } else {
                Debug.logError("[ModelReader.createModelEntity] ERROR: Could not find field \"" +
                               ((Element) pkList.item(i)).getAttribute("field") + "\" specified in a prim-key", module);
            }
        }

        //now that we have the pks and the fields, make the nopks vector
        this.nopks = new Vector();
        for (int ind = 0; ind < this.fields.size(); ind++) {
            ModelField field = (ModelField) this.fields.elementAt(ind);
            if (!field.isPk) this.nopks.add(field);
        }

        if (utilTimer != null) utilTimer.timerString("  createModelEntity: before relations");
        this.populateRelated(reader, entityElement);
    }

    /** DB Names Constructor */
    public ModelEntity(String tableName, Vector colList, ModelFieldTypeReader modelFieldTypeReader) {
        this.tableName = tableName.toUpperCase();
        this.entityName = ModelUtil.dbNameToClassName(this.tableName);
        Iterator columns = colList.iterator();
        while (columns.hasNext()) {
            GenericDAO.ColumnCheckInfo ccInfo = (GenericDAO.ColumnCheckInfo) columns.next();
            ModelField newField = new ModelField(ccInfo, modelFieldTypeReader);
            this.fields.add(newField);
        }
        this.updatePkLists();
    }
    
    protected void populateBasicInfo(Element entityElement, Element docElement, Hashtable docElementValues) {
        this.entityName = UtilXml.checkEmpty(entityElement.getAttribute("entity-name"));
        this.tableName = UtilXml.checkEmpty(entityElement.getAttribute("table-name"), ModelUtil.javaNameToDbName(this.entityName));
        this.packageName = UtilXml.checkEmpty(entityElement.getAttribute("package-name"));
        this.dependentOn = UtilXml.checkEmpty(entityElement.getAttribute("dependent-on"));
        this.doLock = UtilXml.checkBoolean(entityElement.getAttribute("enable-lock"));

        if (docElementValues == null) {
            this.title = UtilXml.checkEmpty(entityElement.getAttribute("title"), UtilXml.childElementValue(docElement, "title"), "None");
            this.description = UtilXml.checkEmpty(UtilXml.childElementValue(entityElement, "description"), UtilXml.childElementValue(docElement, "description"), "None");
            this.copyright = UtilXml.checkEmpty(entityElement.getAttribute("copyright"), UtilXml.childElementValue(docElement, "copyright"), "Copyright (c) 2001 The Open For Business Project - www.ofbiz.org");
            this.author = UtilXml.checkEmpty(entityElement.getAttribute("author"), UtilXml.childElementValue(docElement, "author"), "None");
            this.version = UtilXml.checkEmpty(entityElement.getAttribute("version"), UtilXml.childElementValue(docElement, "version"), "1.0");
        } else {
            if (!docElementValues.containsKey("title")) docElementValues.put("title", UtilXml.childElementValue(docElement, "title"));
            if (!docElementValues.containsKey("description")) docElementValues.put("description", UtilXml.childElementValue(docElement, "description"));
            if (!docElementValues.containsKey("copyright")) docElementValues.put("copyright", UtilXml.childElementValue(docElement, "copyright"));
            if (!docElementValues.containsKey("author")) docElementValues.put("author", UtilXml.childElementValue(docElement, "author"));
            if (!docElementValues.containsKey("version")) docElementValues.put("version", UtilXml.childElementValue(docElement, "version"));
            this.title = UtilXml.checkEmpty(entityElement.getAttribute("title"), (String) docElementValues.get("title"), "None");
            this.description = UtilXml.checkEmpty(UtilXml.childElementValue(entityElement, "description"), (String) docElementValues.get("description"), "None");
            this.copyright = UtilXml.checkEmpty(entityElement.getAttribute("copyright"), (String) docElementValues.get("copyright"), "Copyright (c) 2001 The Open For Business Project - www.ofbiz.org");
            this.author = UtilXml.checkEmpty(entityElement.getAttribute("author"), (String) docElementValues.get("author"), "None");
            this.version = UtilXml.checkEmpty(entityElement.getAttribute("version"), (String) docElementValues.get("version"), "1.0");
        }
    }

    protected void populateRelated(ModelReader reader, Element entityElement) {
        NodeList relationList = entityElement.getElementsByTagName("relation");
        for (int i = 0; i < relationList.getLength(); i++) {
            Element relationElement = (Element) relationList.item(i);
            if (relationElement.getParentNode() == entityElement) {
                ModelRelation relation = reader.createRelation(this, relationElement);
                if (relation != null) this.relations.add(relation);
            }
        }
    }

    // ===== GETTERS/SETTERS =====
    
    public ModelReader getModelReader() { return modelReader; }

    /** The entity-name of the Entity */
    public String getEntityName() { return this.entityName; }
    /** The table-name of the Entity */
    public String getTableName() { return this.tableName; }

    /** The package-name of the Entity */
    public String getPackageName() { return this.packageName; }

    /** The entity-name of the Entity that this Entity is dependent on, if empty then no dependency */
    public String getDependentOn() { return this.dependentOn; }

    //Strings to go in the comment header.
    /** The title for documentation purposes */
    public String getTitle() { return this.title; }
    /** The description for documentation purposes */
    public String getDescription() { return this.description; }
    /** The copyright for documentation purposes */
    public String getCopyright() { return this.copyright; }
    /** The author for documentation purposes */
    public String getAuthor() { return this.author; }
    /** The version for documentation purposes */
    public String getVersion() { return this.version; }

    /** A Vector of the Field objects for the Entity */
    public Vector getFields() { return this.fields; }
    /** A Vector of the Field objects for the Entity, one for each Primary Key */
    public Vector getPks() { return this.pks; }
    /** A Vector of the Field objects for the Entity, one for each NON Primary Key */
    public Vector getNopks() { return this.nopks; }
    /** relations defining relationships between this entity and other entities */
    public Vector getRelations() { return this.relations; }

    /** An indicator to specify if this entity requires locking for updates */
    public boolean getDoLock() { return this.doLock; }

    public boolean lock() {
        if (doLock && isField(STAMP_FIELD)) {
            return true;
        }
        else {
            doLock = false;
            return false;
        }
    }

    public void updatePkLists() {
        pks = new Vector();
        nopks = new Vector();
        for (int i = 0; i < fields.size(); i++) {
            ModelField field = (ModelField) fields.get(i);
            if (field.isPk)
                pks.add(field);
            else
                nopks.add(field);
        }
    }

    public boolean isField(String fieldName) {
        if (fieldName == null) return false;
        for (int i = 0; i < fields.size(); i++) {
            ModelField field = (ModelField) fields.get(i);
            if (field.name.equals(fieldName)) return true;
        }
        return false;
    }

    public boolean areFields(Collection fieldNames) {
        if (fieldNames == null) return false;
        Iterator iter = fieldNames.iterator();
        while (iter.hasNext()) {
            String fieldName = (String) iter.next();
            if (!isField(fieldName)) return false;
        }
        return true;
    }

    public ModelField getField(String fieldName) {
        if (fieldName == null) return null;
        for (int i = 0; i < fields.size(); i++) {
            ModelField field = (ModelField) fields.get(i);
            if (field.name.equals(fieldName)) return field;
        }
        return null;
    }

    public void removeField(String fieldName) {
        if (fieldName == null) return;
        for (int i = 0; i < fields.size(); i++) {
            ModelField field = (ModelField) fields.get(i);
            if (field.name.equals(fieldName)) {
                fields.removeElementAt(i);
                if (field.isPk)
                    pks.remove(field);
                else
                    nopks.remove(field);
            }
        }
        return;
    }

    public List getAllFieldNames() {
        return getFieldNamesFromFieldVector(fields);
    }

    public List getPkFieldNames() {
        return getFieldNamesFromFieldVector(pks);
    }

    public List getNoPkFieldNames() {
        return getFieldNamesFromFieldVector(nopks);
    }

    public List getFieldNamesFromFieldVector(Vector modelFields) {
        List nameList = new Vector(modelFields.size());
        if (modelFields == null || modelFields.size() <= 0) return nameList;
        for (int i = 0; i < modelFields.size(); i++) {
            ModelField field = (ModelField) modelFields.get(i);
            nameList.add(field.name);
        }
        return nameList;
    }

    public ModelRelation getRelation(String relationName) {
        if (relationName == null) return null;
        for (int i = 0; i < relations.size(); i++) {
            ModelRelation relation = (ModelRelation) relations.get(i);
            if (relationName.equals(relation.title + relation.relEntityName)) return relation;
        }
        return null;
    }

    public String nameString(Vector flds) {
        return nameString(flds, ", ", "");
    }

    public String nameString(Vector flds, String separator, String afterLast) {
        String returnString = "";
        if (flds.size() < 1) {
            return "";
        }

        int i = 0;
        for (; i < flds.size() - 1; i++) {
            returnString = returnString + ((ModelField) flds.elementAt(i)).name + separator;
        }
        returnString = returnString + ((ModelField) flds.elementAt(i)).name + afterLast;
        return returnString;
    }

    public String typeNameString(Vector flds) {
        String returnString = "";
        if (flds.size() < 1) {
            return "";
        }

        int i = 0;
        for (; i < flds.size() - 1; i++) {
            returnString = returnString + ((ModelField) flds.elementAt(i)).type + " " + ((ModelField) flds.elementAt(i)).name + ", ";
        }
        returnString = returnString + ((ModelField) flds.elementAt(i)).type + " " + ((ModelField) flds.elementAt(i)).name;
        return returnString;
    }

    public String fieldNameString() {
        return fieldNameString(", ", "");
    }

    public String fieldNameString(String separator, String afterLast) {
        return nameString(fields, separator, afterLast);
    }

    public String fieldTypeNameString() {
        return typeNameString(fields);
    }

    public String primKeyClassNameString() {
        return typeNameString(pks);
    }

    public String pkNameString() {
        return pkNameString(", ", "");
    }

    public String pkNameString(String separator, String afterLast) {
        return nameString(pks, separator, afterLast);
    }

    public String nonPkNullList() {
        return fieldsStringList(fields, "null", ", ", false, true);
    }

    public String fieldsStringList(Vector flds, String eachString, String separator) {
        return fieldsStringList(flds, eachString, separator, false, false);
    }

    public String fieldsStringList(Vector flds, String eachString, String separator, boolean appendIndex) {
        return fieldsStringList(flds, eachString, separator, appendIndex, false);
    }

    public String fieldsStringList(Vector flds, String eachString, String separator, boolean appendIndex, boolean onlyNonPK) {
        String returnString = "";
        if (flds.size() < 1) {
            return "";
        }

        int i = 0;
        for (; i < flds.size(); i++) {
            if (onlyNonPK && ((ModelField) flds.elementAt(i)).isPk) continue;
            returnString = returnString + eachString;
            if (appendIndex) returnString = returnString + (i + 1);
            if (i < flds.size() - 1) returnString = returnString + separator;
        }
        return returnString;
    }

    public String colNameString(Vector flds) {
        return colNameString(flds, ", ", "");
    }

    public String colNameString(Vector flds, String separator, String afterLast) {
        String returnString = "";
        if (flds.size() < 1) {
            return "";
        }

        int i = 0;
        for (; i < flds.size() - 1; i++) {
            returnString = returnString + ((ModelField) flds.elementAt(i)).colName + separator;
        }
        returnString = returnString + ((ModelField) flds.elementAt(i)).colName + afterLast;
        return returnString;
    }

    public String classNameString(Vector flds) {
        return classNameString(flds, ", ", "");
    }

    public String classNameString(Vector flds, String separator, String afterLast) {
        String returnString = "";
        if (flds.size() < 1) {
            return "";
        }

        int i = 0;
        for (; i < flds.size() - 1; i++) {
            returnString = returnString + ModelUtil.upperFirstChar(((ModelField) flds.elementAt(i)).name) + separator;
        }
        returnString = returnString + ModelUtil.upperFirstChar(((ModelField) flds.elementAt(i)).name) + afterLast;
        return returnString;
    }

    public String finderQueryString(Vector flds) {
        String returnString = "";
        if (flds.size() < 1) {
            return "";
        }
        int i = 0;
        for (; i < flds.size() - 1; i++) {
            returnString = returnString + ((ModelField) flds.elementAt(i)).colName + " like {" + i + "} AND ";
        }
        returnString = returnString + ((ModelField) flds.elementAt(i)).colName + " like {" + i + "}";
        return returnString;
    }

    public String httpArgList(Vector flds) {
        String returnString = "";
        if (flds.size() < 1) {
            return "";
        }
        int i = 0;
        for (; i < flds.size() - 1; i++) {
            returnString = returnString + "\"" + tableName + "_" + ((ModelField) flds.elementAt(i)).colName + "=\" + " + ((ModelField) flds.elementAt(i)).name + " + \"&\" + ";
        }
        returnString = returnString + "\"" + tableName + "_" + ((ModelField) flds.elementAt(i)).colName + "=\" + " + ((ModelField) flds.elementAt(i)).name;
        return returnString;
    }

    public String httpArgListFromClass(Vector flds) {
        String returnString = "";
        if (flds.size() < 1) {
            return "";
        }

        int i = 0;
        for (; i < flds.size() - 1; i++) {
            returnString = returnString + "\"" + tableName + "_" + ((ModelField) flds.elementAt(i)).colName + "=\" + " + ModelUtil.lowerFirstChar(entityName) + ".get" + ModelUtil.upperFirstChar(((ModelField) flds.elementAt(i)).name) + "() + \"&\" + ";
        }
        returnString = returnString + "\"" + tableName + "_" + ((ModelField) flds.elementAt(i)).colName + "=\" + " + ModelUtil.lowerFirstChar(entityName) + ".get" + ModelUtil.upperFirstChar(((ModelField) flds.elementAt(i)).name) + "()";
        return returnString;
    }

    public String httpArgListFromClass(Vector flds, String entityNameSuffix) {
        String returnString = "";
        if (flds.size() < 1) {
            return "";
        }

        int i = 0;
        for (; i < flds.size() - 1; i++) {
            returnString = returnString + "\"" + tableName + "_" + ((ModelField) flds.elementAt(i)).colName + "=\" + " + ModelUtil.lowerFirstChar(entityName) + entityNameSuffix + ".get" + ModelUtil.upperFirstChar(((ModelField) flds.elementAt(i)).name) + "() + \"&\" + ";
        }
        returnString = returnString + "\"" + tableName + "_" + ((ModelField) flds.elementAt(i)).colName + "=\" + " + ModelUtil.lowerFirstChar(entityName) + entityNameSuffix + ".get" + ModelUtil.upperFirstChar(((ModelField) flds.elementAt(i)).name) + "()";
        return returnString;
    }

    public String httpRelationArgList(Vector flds, ModelRelation relation) {
        String returnString = "";
        if (flds.size() < 1) {
            return "";
        }

        int i = 0;
        for (; i < flds.size() - 1; i++) {
            ModelKeyMap keyMap = relation.findKeyMapByRelated(((ModelField) flds.elementAt(i)).name);
            if (keyMap != null)
                returnString = returnString + "\"" + tableName + "_" + ((ModelField) flds.elementAt(i)).colName + "=\" + " + ModelUtil.lowerFirstChar(relation.mainEntity.entityName) + ".get" + ModelUtil.upperFirstChar(keyMap.fieldName) + "() + \"&\" + ";
            else
                Debug.logWarning("-- -- ENTITYGEN ERROR:httpRelationArgList: Related Key in Key Map not found for name: " + ((ModelField) flds.elementAt(i)).name + " related entity: " + relation.relEntityName + " main entity: " + relation.mainEntity.entityName + " type: " + relation.type);
        }
        ModelKeyMap keyMap = relation.findKeyMapByRelated(((ModelField) flds.elementAt(i)).name);
        if (keyMap != null)
            returnString = returnString + "\"" + tableName + "_" + ((ModelField) flds.elementAt(i)).colName + "=\" + " + ModelUtil.lowerFirstChar(relation.mainEntity.entityName) + ".get" + ModelUtil.upperFirstChar(keyMap.fieldName) + "()";
        else
            Debug.logWarning("-- -- ENTITYGEN ERROR:httpRelationArgList: Related Key in Key Map not found for name: " + ((ModelField) flds.elementAt(i)).name + " related entity: " + relation.relEntityName + " main entity: " + relation.mainEntity.entityName + " type: " + relation.type);
        return returnString;
    }
/*
  public String httpRelationArgList(ModelRelation relation) {
    String returnString = "";
    if(relation.keyMaps.size() < 1) { return ""; }

    int i = 0;
    for(; i < relation.keyMaps.size() - 1; i++) {
      ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.elementAt(i);
      if(keyMap != null)
        returnString = returnString + "\"" + tableName + "_" + keyMap.relColName + "=\" + " + ModelUtil.lowerFirstChar(relation.mainEntity.entityName) + ".get" + ModelUtil.upperFirstChar(keyMap.fieldName) + "() + \"&\" + ";
    }
    ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.elementAt(i);
    returnString = returnString + "\"" + tableName + "_" + keyMap.relColName + "=\" + " + ModelUtil.lowerFirstChar(relation.mainEntity.entityName) + ".get" + ModelUtil.upperFirstChar(keyMap.fieldName) + "()";
    return returnString;
  }
*/
    public String typeNameStringRelatedNoMapped(Vector flds, ModelRelation relation) {
        String returnString = "";
        if (flds.size() < 1) {
            return "";
        }

        int i = 0;
        if (relation.findKeyMapByRelated(((ModelField) flds.elementAt(i)).name) == null)
            returnString = returnString + ((ModelField) flds.elementAt(i)).type + " " + ((ModelField) flds.elementAt(i)).name;
        i++;
        for (; i < flds.size(); i++) {
            if (relation.findKeyMapByRelated(((ModelField) flds.elementAt(i)).name) == null) {
                if (returnString.length() > 0) returnString = returnString + ", ";
                returnString = returnString + ((ModelField) flds.elementAt(i)).type + " " + ((ModelField) flds.elementAt(i)).name;
            }
        }
        return returnString;
    }

    public String typeNameStringRelatedAndMain(Vector flds, ModelRelation relation) {
        String returnString = "";
        if (flds.size() < 1) {
            return "";
        }

        int i = 0;
        for (; i < flds.size() - 1; i++) {
            ModelKeyMap keyMap = relation.findKeyMapByRelated(((ModelField) flds.elementAt(i)).name);
            if (keyMap != null)
                returnString = returnString + keyMap.fieldName + ", ";
            else
                returnString = returnString + ((ModelField) flds.elementAt(i)).name + ", ";
        }
        ModelKeyMap keyMap = relation.findKeyMapByRelated(((ModelField) flds.elementAt(i)).name);
        if (keyMap != null)
            returnString = returnString + keyMap.fieldName;
        else
            returnString = returnString + ((ModelField) flds.elementAt(i)).name;
        return returnString;
    }
}

