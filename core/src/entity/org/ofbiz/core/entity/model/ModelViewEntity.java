/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
import org.w3c.dom.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.jdbc.*;

/**
 * This class extends ModelEntity and provides additional information appropriate to view entities
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:peterm@miraculum.com">Peter Moon</a>    
 * @version    $Revision$
 * @since      2.0
 */
public class ModelViewEntity extends ModelEntity {
    public static final String module = ModelViewEntity.class.getName();

    /** Contains member-entity alias name definitions: key is alias, value is ModelMemberEntity */
    protected Map memberModelMemberEntities = new HashMap();

    /** A list of all ModelMemberEntity entries; this is mainly used to preserve the original order of member entities from the XML file */
    protected List allModelMemberEntities = new LinkedList();

    /** Contains member-entity ModelEntities: key is alias, value is ModelEntity; populated with fields */
    protected Map memberModelEntities = null;

    /** List of alias-alls which act as a shortcut for easily pulling over member entity fields */
    protected List aliasAlls = new ArrayList();

    /** List of aliases with information in addition to what is in the standard field list */
    protected List aliases = new ArrayList();

    /** List of view links to define how entities are connected (or "joined") */
    protected List viewLinks = new ArrayList();

    /** A List of the Field objects for the View Entity, one for each GROUP BY field */
    protected List groupBys = new ArrayList();

    public ModelViewEntity(ModelReader reader, Element entityElement, Element docElement, UtilTimer utilTimer, Hashtable docElementValues) {
        this.modelReader = reader;

        if (utilTimer != null) utilTimer.timerString("  createModelViewEntity: before general/basic info");
        this.populateBasicInfo(entityElement, docElement, docElementValues);

        if (utilTimer != null) utilTimer.timerString("  createModelViewEntity: before \"member-entity\"s");
        List memberEntityList = UtilXml.childElementList(entityElement, "member-entity");
        Iterator memberEntityIter = memberEntityList.iterator();
        while (memberEntityIter.hasNext()) {
            Element memberEntityElement = (Element) memberEntityIter.next();
            String alias = UtilXml.checkEmpty(memberEntityElement.getAttribute("entity-alias"));
            String name = UtilXml.checkEmpty(memberEntityElement.getAttribute("entity-name"));
            if (name.length() <= 0 || alias.length() <= 0) {
                Debug.logError("[new ModelViewEntity] entity-alias or entity-name missing on member-entity element of the view-entity " + this.entityName, module);
            } else {
                ModelMemberEntity modelMemberEntity = new ModelMemberEntity(alias, name);
                this.addMemberModelMemberEntity(modelMemberEntity);
            }
        }

        // when reading aliases and alias-alls, just read them into the alias list, there will be a pass
        // after loading all entities to go back and fill in all of the ModelField entries
        List aliasAllList = UtilXml.childElementList(entityElement, "alias-all");
        Iterator aliasAllIter = aliasAllList.iterator();
        while (aliasAllIter.hasNext()) {
            Element aliasElement = (Element) aliasAllIter.next();
            ModelViewEntity.ModelAliasAll aliasAll = new ModelAliasAll(aliasElement);
            this.aliasAlls.add(aliasAll);
        }

        if (utilTimer != null) utilTimer.timerString("  createModelViewEntity: before aliases");
        List aliasList = UtilXml.childElementList(entityElement, "alias");
        Iterator aliasIter = aliasList.iterator();
        while (aliasIter.hasNext()) {
            Element aliasElement = (Element) aliasIter.next();
            ModelViewEntity.ModelAlias alias = new ModelAlias(aliasElement);
            this.aliases.add(alias);
        }
        
        List viewLinkList = UtilXml.childElementList(entityElement, "view-link");
        Iterator viewLinkIter = viewLinkList.iterator();
        while (viewLinkIter.hasNext()) {
            Element viewLinkElement = (Element) viewLinkIter.next();
            ModelViewLink viewLink = new ModelViewLink(viewLinkElement);
            this.addViewLink(viewLink);
        }

        if (utilTimer != null) utilTimer.timerString("  createModelEntity: before relations");
        this.populateRelated(reader, entityElement);

        // before finishing, make sure the table name is null, this should help bring up errors early...
        this.tableName = null;
    }

    public Map getMemberModelMemberEntities() {
        return this.memberModelMemberEntities;
    }

    public List getAllModelMemberEntities() {
        return this.allModelMemberEntities;
    }

    public ModelMemberEntity getMemberModelMemberEntity(String alias) {
        return (ModelMemberEntity) this.memberModelMemberEntities.get(alias);
    }

    public ModelEntity getMemberModelEntity(String alias) {
        if (this.memberModelEntities == null) {
            this.memberModelEntities = new HashMap();
            populateFields(this.getModelReader().entityCache);
        }
        return (ModelEntity) this.memberModelEntities.get(alias);
    }

    public void addMemberModelMemberEntity(ModelMemberEntity modelMemberEntity) {
        this.memberModelMemberEntities.put(modelMemberEntity.getEntityAlias(), modelMemberEntity);
        this.allModelMemberEntities.add(modelMemberEntity);
    }

    public void removeMemberModelMemberEntity(String alias) {
        ModelMemberEntity modelMemberEntity = (ModelMemberEntity) this.memberModelMemberEntities.remove(alias);

        if (modelMemberEntity == null) return;
        this.allModelMemberEntities.remove(modelMemberEntity);
    }

    /** List of aliases with information in addition to what is in the standard field list */
    public ModelAlias getAlias(int index) {
        return (ModelAlias) this.aliases.get(index);
    }
    
    public ModelAlias getAlias(String name) {
        Iterator aliasIter = getAliasesIterator();
        while (aliasIter.hasNext()) {
            ModelAlias alias = (ModelAlias) aliasIter.next();
            if (alias.name.equals(name)) {
                return alias;
            }
        }
        return null;
    }

    public int getAliasesSize() {
        return this.aliases.size();
    }

    public Iterator getAliasesIterator() {
        return this.aliases.iterator();
    }

    public List getAliasesCopy() {
        return new ArrayList(this.aliases);
    }

    public List getGroupBysCopy() {
        return new ArrayList(this.groupBys);
    }

    /** List of view links to define how entities are connected (or "joined") */
    public ModelViewLink getViewLink(int index) {
        return (ModelViewLink) this.viewLinks.get(index);
    }

    public int getViewLinksSize() {
        return this.viewLinks.size();
    }

    public Iterator getViewLinksIterator() {
        return this.viewLinks.iterator();
    }

    public List getViewLinksCopy() {
        return new ArrayList(this.viewLinks);
    }

    public void addViewLink(ModelViewLink viewLink) {
        this.viewLinks.add(viewLink);
    }
    
    public String colNameString(List flds, String separator, String afterLast, boolean alias) {
        StringBuffer returnString = new StringBuffer();

        if (flds.size() < 1) {
            return "";
        }

        Iterator fldsIt = flds.iterator();
        while(fldsIt.hasNext()) {
            ModelField field = (ModelField) fldsIt.next();
            returnString.append(field.colName);
            if (alias) {
                ModelAlias modelAlias = this.getAlias(field.name);
                if (modelAlias != null) {
                    returnString.append(" AS " + modelAlias.colAlias);
                }
            }
            if (fldsIt.hasNext()) {
                returnString.append(separator);
            }
        }

        returnString.append(afterLast);
        return returnString.toString();
    }    

    public void populateFields(Map entityCache) {
        if (this.memberModelEntities == null) {
            this.memberModelEntities = new HashMap();
        }
        Iterator meIter = memberModelMemberEntities.entrySet().iterator();

        while (meIter.hasNext()) {
            Map.Entry entry = (Map.Entry) meIter.next();

            ModelMemberEntity modelMemberEntity = (ModelMemberEntity) entry.getValue();
            String aliasedEntityName = modelMemberEntity.getEntityName();
            ModelEntity aliasedEntity = (ModelEntity) entityCache.get(aliasedEntityName);

            if (aliasedEntity == null) {
                Debug.logError("[ModelViewEntity.populateFields] ERROR: could not find ModelEntity for entity name: " +
                    aliasedEntityName, module);
                continue;
            }
            memberModelEntities.put(entry.getKey(), aliasedEntity);
        }

        expandAllAliasAlls(entityCache);

        for (int i = 0; i < aliases.size(); i++) {
            ModelAlias alias = (ModelAlias) aliases.get(i);

            ModelMemberEntity modelMemberEntity = (ModelMemberEntity) memberModelMemberEntities.get(alias.entityAlias);

            if (modelMemberEntity == null) {
                Debug.logError("No member entity with alias " + alias.entityAlias + " found in view-entity " + this.getEntityName() + "; this view-entity will NOT be usable...", module);
            }
            String aliasedEntityName = modelMemberEntity.getEntityName();
            ModelEntity aliasedEntity = (ModelEntity) entityCache.get(aliasedEntityName);

            if (aliasedEntity == null) {
                Debug.logError("[ModelViewEntity.populateFields] ERROR: could not find ModelEntity for entity name: " + aliasedEntityName, module);
                continue;
            }

            ModelField aliasedField = aliasedEntity.getField(alias.field);

            if (aliasedField == null) {
                Debug.logError("[ModelViewEntity.populateFields] ERROR: could not find ModelField for field name \"" +
                    alias.field + "\" on entity with name: " + aliasedEntityName, module);
                continue;
            }

            ModelField field = new ModelField();

            field.name = alias.name;
            if (alias.isPk != null) {
                field.isPk = alias.isPk.booleanValue();
            } else {
                field.isPk = aliasedField.isPk;
            }

            this.fields.add(field);
            if (field.isPk) {
                this.pks.add(field);
            } else {
                this.nopks.add(field);
            }

            // if this is a groupBy field, add it to the groupBys list
            if (alias.groupBy) {
                this.groupBys.add(field);
            }

            // show a warning if function is specified and groupBy is true
            if (UtilValidate.isNotEmpty(alias.function) && alias.groupBy) {
                Debug.logWarning("The view-entity alias with name=" + alias.name + " has a function value and is specified as a group-by field; this may be an error, but is not necessarily.", module);
            }

            if ("count".equals(alias.function) || "count-distinct".equals(alias.function)) {
                // if we have a "count" function we have to change the type
                field.type = "numeric";
            } else {
                field.type = aliasedField.type;
            }

            if (UtilValidate.isNotEmpty(alias.function)) {
                if ("min".equals(alias.function)) {
                    field.colName = "MIN(" + alias.entityAlias + "." + SqlJdbcUtil.filterColName(aliasedField.colName) + ")";
                } else if ("max".equals(alias.function)) {
                    field.colName = "MAX(" + alias.entityAlias + "." + SqlJdbcUtil.filterColName(aliasedField.colName) + ")";
                } else if ("sum".equals(alias.function)) {
                    field.colName = "SUM(" + alias.entityAlias + "." + SqlJdbcUtil.filterColName(aliasedField.colName) + ")";
                } else if ("avg".equals(alias.function)) {
                    field.colName = "AVG(" + alias.entityAlias + "." + SqlJdbcUtil.filterColName(aliasedField.colName) + ")";
                } else if ("count".equals(alias.function)) {
                    field.colName = "COUNT(" + alias.entityAlias + "." + SqlJdbcUtil.filterColName(aliasedField.colName) + ")";
                } else if ("count-distinct".equals(alias.function)) {
                    field.colName = "COUNT(DISTINCT " + alias.entityAlias + "." + SqlJdbcUtil.filterColName(aliasedField.colName) + ")";
                } else if ("upper".equals(alias.function)) {
                    field.colName = "UPPER(" + alias.entityAlias + "." + SqlJdbcUtil.filterColName(aliasedField.colName) + ")";
                } else if ("lower".equals(alias.function)) {
                    field.colName = "LOWER(" + alias.entityAlias + "." + SqlJdbcUtil.filterColName(aliasedField.colName) + ")";
                } else {
                    Debug.logWarning("Specified alias function [" + alias.function + "] not valid; must be: min, max, sum, avg, count or count-distinct; using a column name with no function function", module);
                    field.colName = alias.entityAlias + "." + SqlJdbcUtil.filterColName(aliasedField.colName);
                }
            } else {
                field.colName = alias.entityAlias + "." + SqlJdbcUtil.filterColName(aliasedField.colName);
            }

            field.validators = aliasedField.validators;
        }
    }

    /**
     * Go through all aliasAlls and create an alias for each field of each member entity
     */
    private void expandAllAliasAlls(Map entityCache) {
        Iterator aliasAllIter = aliasAlls.iterator();
        while (aliasAllIter.hasNext()) {
            ModelAliasAll aliasAll = (ModelAliasAll) aliasAllIter.next();
            String prefix = aliasAll.getPrefix();

            ModelMemberEntity modelMemberEntity = (ModelMemberEntity) memberModelMemberEntities.get(aliasAll.getEntityAlias());
            if (modelMemberEntity == null) {
                Debug.logError("Member entity referred to in alias-all not found, ignoring: " + aliasAll.getEntityAlias(), module);
                continue;
            }

            String aliasedEntityName = modelMemberEntity.getEntityName();
            ModelEntity aliasedEntity = (ModelEntity) entityCache.get(aliasedEntityName);
            if (aliasedEntity == null) {
                Debug.logError("Entity referred to in member-entity " + aliasAll.getEntityAlias() + " not found, ignoring: " + aliasedEntityName, module);
                continue;
            }

            List entFieldList = aliasedEntity.getAllFieldNames();
            if (entFieldList == null) {
                Debug.logError("Entity referred to in member-entity " + aliasAll.getEntityAlias() + " has no fields, ignoring: " + aliasedEntityName, module);
                continue;
            }

            Iterator fieldnamesIterator = entFieldList.iterator();
            while (fieldnamesIterator.hasNext()) {
                // now merge the lists, leaving out any that duplicate an existing alias name
                String aliasName = (String) fieldnamesIterator.next();
                if (UtilValidate.isNotEmpty(prefix)) {
                    StringBuffer newAliasBuffer = new StringBuffer(prefix);
                    //make sure the first letter is uppercase to delineate the field name
                    newAliasBuffer.append(Character.toUpperCase(aliasName.charAt(0)));
                    newAliasBuffer.append(aliasName.substring(1));
                    aliasName = newAliasBuffer.toString();
                }
                
                ModelAlias existingAlias = this.getAlias(aliasName);
                if (existingAlias != null) {
                    //already exists, oh well... probably an override, but logInfo just in case
                    Debug.logInfo("Throwing out field alias in view entity " + this.getEntityName() + " because one already exists with the name: " + aliasName, module);
                    continue;
                }
                
                ModelAlias expandedAlias = new ModelAlias();
                expandedAlias.name = aliasName;
                expandedAlias.field = expandedAlias.name;
                expandedAlias.entityAlias = aliasAll.getEntityAlias();
                aliases.add(expandedAlias);
            }
        }
    }

    public static class ModelMemberEntity {
        protected String entityAlias = "";
        protected String entityName = "";

        public ModelMemberEntity(String entityAlias, String entityName) {
            this.entityAlias = entityAlias;
            this.entityName = entityName;
        }

        public String getEntityAlias() {
            return this.entityAlias;
        }

        public String getEntityName() {
            return this.entityName;
        }
    }

    public static class ModelAliasAll {
        protected String entityAlias = "";
        protected String prefix = "";

        protected ModelAliasAll() {}

        public ModelAliasAll(Element aliasAllElement) {
            this.entityAlias = UtilXml.checkEmpty(aliasAllElement.getAttribute("entity-alias"));
            this.prefix = UtilXml.checkEmpty(aliasAllElement.getAttribute("prefix"));
        }

        public String getEntityAlias() {
            return this.entityAlias;
        }

        public String getPrefix() {
            return this.prefix;
        }
    }

    public static class ModelAlias {
        protected String entityAlias = "";
        protected String name = "";
        protected String field = "";
        protected String colAlias = "";
        // this is a Boolean object for a tri-state: null, true or false
        protected Boolean isPk = null;
        protected boolean groupBy = false;
        // is specified this alias is a calculated value; can be: min, max, sum, avg, count, count-distinct
        protected String function = null;

        protected ModelAlias() {}

        public ModelAlias(Element aliasElement) {
            this.entityAlias = UtilXml.checkEmpty(aliasElement.getAttribute("entity-alias"));
            this.name = UtilXml.checkEmpty(aliasElement.getAttribute("name"));
            this.field = UtilXml.checkEmpty(aliasElement.getAttribute("field"), this.name);
            this.colAlias = UtilXml.checkEmpty(aliasElement.getAttribute("col-alias"), ModelUtil.javaNameToDbName(UtilXml.checkEmpty(this.name)));
            String primKeyValue = UtilXml.checkEmpty(aliasElement.getAttribute("prim-key"));

            if (UtilValidate.isNotEmpty(primKeyValue)) {
                this.isPk = new Boolean("true".equals(primKeyValue));
            } else {
                this.isPk = null;
            }
            this.groupBy = "true".equals(UtilXml.checkEmpty(aliasElement.getAttribute("group-by")));
            this.function = UtilXml.checkEmpty(aliasElement.getAttribute("function"));
        }

        public ModelAlias(String entityAlias, String name, String field, Boolean isPk, boolean groupBy, String function) {
            this.entityAlias = entityAlias;
            this.name = name;
            this.field = field;
            this.isPk = isPk;
            this.groupBy = groupBy;
            this.function = function;
        }

        public String getEntityAlias() {
            return this.entityAlias;
        }

        public String getName() {
            return this.name;
        }
        
        public String getColAlias() {
            return this.colAlias;
        }

        public String getField() {
            return this.field;
        }

        public Boolean getIsPk() {
            return this.isPk;
        }

        public boolean getGroupBy() {
            return this.groupBy;
        }

        public String getFunction() {
            return this.function;
        }
    }


    public static class ModelViewLink {
        protected String entityAlias = "";
        protected String relEntityAlias = "";
        protected boolean relOptional = false;
        protected List keyMaps = new ArrayList();

        protected ModelViewLink() {}

        public ModelViewLink(Element viewLinkElement) {
            this.entityAlias = UtilXml.checkEmpty(viewLinkElement.getAttribute("entity-alias"));
            this.relEntityAlias = UtilXml.checkEmpty(viewLinkElement.getAttribute("rel-entity-alias"));
            // if anything but true will be false; ie defaults to false
            this.relOptional = "true".equals(viewLinkElement.getAttribute("rel-optional"));

            NodeList keyMapList = viewLinkElement.getElementsByTagName("key-map");

            for (int j = 0; j < keyMapList.getLength(); j++) {
                Element keyMapElement = (Element) keyMapList.item(j);
                ModelKeyMap keyMap = new ModelKeyMap(keyMapElement);

                if (keyMap != null) keyMaps.add(keyMap);
            }
        }

        public ModelViewLink(String entityAlias, String relEntityAlias, List keyMaps) {
            this.entityAlias = entityAlias;
            this.relEntityAlias = relEntityAlias;
            this.keyMaps.addAll(keyMaps);
        }

        public String getEntityAlias() {
            return this.entityAlias;
        }

        public String getRelEntityAlias() {
            return this.relEntityAlias;
        }

        public boolean isRelOptional() {
            return this.relOptional;
        }

        public ModelKeyMap getKeyMap(int index) {
            return (ModelKeyMap) this.keyMaps.get(index);
        }

        public int getKeyMapsSize() {
            return this.keyMaps.size();
        }

        public Iterator getKeyMapsIterator() {
            return this.keyMaps.iterator();
        }

        public List getKeyMapsCopy() {
            return new ArrayList(this.keyMaps);
        }
    }
}
