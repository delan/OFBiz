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


/**
 * This class extends ModelEntity and provides additional information appropriate to view entities
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    November 9, 2001
 *@version    1.0
 */
public class ModelViewEntity extends ModelEntity {
    public static final String module = ModelViewEntity.class.getName();

    /** Contains member-entity alias name definitions: key is alias, value is entity-name */
    protected Map memberEntityNames = new HashMap();

    /** Contains member-entity ModelEntities: key is alias, value is ModelEntity; populated with fields */
    protected Map memberModelEntities = null;

    /** List of aliases with information in addition to what is in the standard field list */
    protected List aliases = new ArrayList();

    /** List of view links to define how entities are connected (or "joined") */
    protected List viewLinks = new ArrayList();

    public ModelViewEntity(ModelReader reader, Element entityElement, Element docElement, UtilTimer utilTimer, Hashtable docElementValues) {
        this.modelReader = reader;

        if (utilTimer != null) utilTimer.timerString("  createModelViewEntity: before general/basic info");
        this.populateBasicInfo(entityElement, docElement, docElementValues);

        if (utilTimer != null) utilTimer.timerString("  createModelViewEntity: before \"member-entity\"s");
        NodeList membEntList = entityElement.getElementsByTagName("member-entity");

        for (int i = 0; i < membEntList.getLength(); i++) {
            Element membEnt = (Element) membEntList.item(i);
            String alias = UtilXml.checkEmpty(membEnt.getAttribute("entity-alias"));
            String name = UtilXml.checkEmpty(membEnt.getAttribute("entity-name"));

            if (name.length() <= 0 || alias.length() <= 0) {
                Debug.logWarning("[ModelReader.createModelViewEntity] Warning: entity-alias or " +
                    "entity-name missing on member-entity element", module);
            } else {
                this.addMemberEntityName(alias, name);
            }
        }

        //when reading aliases, just read them into the alias list, there will be a pass
        // after loading all entities to go back and fill in all of the ModelField entries
        if (utilTimer != null) utilTimer.timerString("  createModelViewEntity: before aliases");
        NodeList aliasList = entityElement.getElementsByTagName("alias");

        for (int i = 0; i < aliasList.getLength(); i++) {
            ModelViewEntity.ModelAlias alias = this.makeModelAlias();
            Element aliasElement = (Element) aliasList.item(i);

            alias.entityAlias = UtilXml.checkEmpty(aliasElement.getAttribute("entity-alias"));
            alias.name = UtilXml.checkEmpty(aliasElement.getAttribute("name"));
            alias.field = UtilXml.checkEmpty(aliasElement.getAttribute("field"), alias.name);
            String primKeyValue = UtilXml.checkEmpty(aliasElement.getAttribute("prim-key"));
            if (UtilValidate.isNotEmpty(primKeyValue)) {
                alias.isPk = new Boolean("true".equals(primKeyValue));
            } else {
                alias.isPk = null;
            }
            this.aliases.add(alias);
        }

        NodeList viewLinkList = entityElement.getElementsByTagName("view-link");

        for (int i = 0; i < viewLinkList.getLength(); i++) {
            Element viewLinkElement = (Element) viewLinkList.item(i);
            ModelViewEntity.ModelViewLink viewLink = this.makeModelViewLink();

            viewLink.entityAlias = UtilXml.checkEmpty(viewLinkElement.getAttribute("entity-alias"));
            viewLink.relEntityAlias = UtilXml.checkEmpty(viewLinkElement.getAttribute("rel-entity-alias"));

            NodeList keyMapList = viewLinkElement.getElementsByTagName("key-map");

            for (int j = 0; j < keyMapList.getLength(); j++) {
                Element keyMapElement = (Element) keyMapList.item(j);
                ModelKeyMap keyMap = new ModelKeyMap(keyMapElement);

                if (keyMap != null) viewLink.keyMaps.add(keyMap);
            }
            this.viewLinks.add(viewLink);
        }

        if (utilTimer != null) utilTimer.timerString("  createModelEntity: before relations");
        this.populateRelated(reader, entityElement);

        //before finishing, make sure the table name is null, this should help bring up errors early...
        this.tableName = null;
    }

    public Map getMemberEntityNames() {
        return this.memberEntityNames;
    }

    public ModelEntity getMemberModelEntity(String alias) {
        if (this.memberModelEntities == null) {
            this.memberModelEntities = new HashMap();
            populateFields(this.getModelReader().entityCache);
        }
        return (ModelEntity) this.memberModelEntities.get(alias);
    }

    public void addMemberEntityName(String alias, String aliasedEntityName) {
        this.memberEntityNames.put(alias, aliasedEntityName);
    }

    /** List of aliases with information in addition to what is in the standard field list */
    public ModelAlias getAlias(int index) {
        return (ModelAlias) this.aliases.get(index);
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

    public void populateFields(Map entityCache) {
        if (this.memberModelEntities == null) {
            this.memberModelEntities = new HashMap();
        }
        Iterator meIter = memberEntityNames.entrySet().iterator();

        while (meIter.hasNext()) {
            Map.Entry entry = (Map.Entry) meIter.next();

            String aliasedEntityName = (String) entry.getValue();
            ModelEntity aliasedEntity = (ModelEntity) entityCache.get(aliasedEntityName);

            if (aliasedEntity == null) {
                Debug.logError("[ModelViewEntity.populateFields] ERROR: could not find ModelEntity for entity name: " +
                    aliasedEntityName);
                continue;
            }
            memberModelEntities.put(entry.getKey(), aliasedEntity);
        }

        for (int i = 0; i < aliases.size(); i++) {
            ModelAlias alias = (ModelAlias) aliases.get(i);

            String aliasedEntityName = (String) memberEntityNames.get(alias.entityAlias);
            ModelEntity aliasedEntity = (ModelEntity) entityCache.get(aliasedEntityName);
            if (aliasedEntity == null) {
                Debug.logError("[ModelViewEntity.populateFields] ERROR: could not find ModelEntity for entity name: " +
                    aliasedEntityName);
                continue;
            }

            ModelField aliasedField = aliasedEntity.getField(alias.field);
            if (aliasedField == null) {
                Debug.logError("[ModelViewEntity.populateFields] ERROR: could not find ModelField for field name \"" +
                    alias.field + "\" on entity with name: " + aliasedEntityName);
                continue;
            }
            
            ModelField field = new ModelField();

            field.name = alias.name;
            if (alias.isPk != null) {
                field.isPk = alias.isPk.booleanValue();
            } else {
                alias.isPk = new Boolean(aliasedField.isPk);
                field.isPk = aliasedField.isPk;
            }

            this.fields.add(field);
            if (field.isPk) {
                this.pks.add(field);
            } else {
                this.nopks.add(field);
            }

            field.type = aliasedField.type;
            field.colName = alias.entityAlias + "." + aliasedField.colName;
            field.validators = aliasedField.validators;
        }
    }

    public ModelAlias makeModelAlias() {
        return new ModelAlias();
    }

    public ModelViewLink makeModelViewLink() {
        return new ModelViewLink();
    }

    public class ModelAlias {
        protected String entityAlias = "";
        protected String name = "";
        protected String field = "";
        protected Boolean isPk = null;

        public ModelAlias() {}

        public String getEntityAlias() {
            return this.entityAlias;
        }

        public String getName() {
            return this.name;
        }

        public String getField() {
            return this.field;
        }

        public Boolean getIsPk() {
            return this.isPk;
        }
    }


    public class ModelViewLink {
        protected String entityAlias = "";
        protected String relEntityAlias = "";
        protected List keyMaps = new ArrayList();

        public ModelViewLink() {}

        public String getEntityAlias() {
            return this.entityAlias;
        }

        public String getRelEntityAlias() {
            return this.relEntityAlias;
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

