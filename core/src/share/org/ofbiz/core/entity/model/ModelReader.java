
package org.ofbiz.core.entity.model;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Generic Entity - Entity Definition Reader
 * <p><b>Description:</b> Describes an Entity and acts as the base for all entity description data used in the code templates.
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @created May 15, 2001
 * @version 1.0
 */

public class ModelReader {

    public static UtilCache readers = new UtilCache("ModelReader", 0, 0);

    public Map entityCache = null;

    public int numEntities = 0;
    public int numViewEntities = 0;
    public int numFields = 0;
    public int numRelations = 0;

    public String modelName;

    //collection of filenames for entity definitions
    public Collection entityFileNames;

    //contains a collection of entity names for each filename, populated as they are loaded
    public Map fileNameEntities;
    //for each entity contains a map to the filename that the entity came from
    public Map entityFile;

    public static ModelReader getModelReader(String delegatorName) {
        String tempModelName = UtilProperties.getPropertyValue("entityengine", delegatorName + ".model.reader");
        ModelReader reader = (ModelReader) readers.get(tempModelName);
        if (reader == null) { //don't want to block here
            synchronized (ModelReader.class) {
                //must check if null again as one of the blocked threads can still enter
                reader = (ModelReader) readers.get(tempModelName);
                if (reader == null) {
                    reader = new ModelReader(tempModelName);
                    readers.put(tempModelName, reader);
                }
            }
        }
        return reader;
    }

    public ModelReader(String modelName) {
        this.modelName = modelName;
        entityFileNames = new LinkedList();
        fileNameEntities = new HashMap();
        entityFile = new HashMap();

        String wholeFileNamesStr = UtilProperties.getPropertyValue("entityengine", modelName + ".xml.entity");
        String fileNamesStr = wholeFileNamesStr;
        fileNamesStr.trim();

        while (fileNamesStr.indexOf(';') > 0) {
            String tempFileName = fileNamesStr.substring(0, fileNamesStr.indexOf(';'));
            tempFileName.trim();
            if (entityFileNames.contains(tempFileName)) {
                Debug.logWarning("WARNING: Entity filename " + tempFileName + " is listed more than once in the entity file list: " + wholeFileNamesStr);
            }
            entityFileNames.add(tempFileName);
            fileNamesStr = fileNamesStr.substring(fileNamesStr.indexOf(';') + 1);
            fileNamesStr.trim();
        }
        if (fileNamesStr.length() > 0) {
            entityFileNames.add(fileNamesStr);
        }

        //preload caches...
        getEntityCache();
    }

    public Map getEntityCache() {
        if (entityCache == null) { //don't want to block here
            synchronized (ModelReader.class) {
                //must check if null again as one of the blocked threads can still enter
                if (entityCache == null) { //now it's safe
                    numEntities = 0;
                    numViewEntities = 0;
                    numFields = 0;
                    numRelations = 0;

                    entityCache = new HashMap();
                    List tempViewEntityList = new LinkedList();

                    UtilTimer utilTimer = new UtilTimer();

                    Iterator fnIter = entityFileNames.iterator();
                    while (fnIter.hasNext()) {
                        String entityFileName = (String) fnIter.next();

                        //utilTimer.timerString("Before getDocument in file " + entityFileName);
                        Document document = getDocument(entityFileName);
                        if (document == null) {
                            entityCache = null;
                            return null;
                        }

                        Hashtable docElementValues = null;
                        docElementValues = new Hashtable();

                        //utilTimer.timerString("Before getDocumentElement in file " + entityFileName);
                        Element docElement = document.getDocumentElement();
                        if (docElement == null) {
                            entityCache = null;
                            return null;
                        }
                        docElement.normalize();
                        Node curChild = docElement.getFirstChild();

                        int i = 0;
                        if (curChild != null) {
                            utilTimer.timerString("Before start of entity loop in file " + entityFileName);
                            do {
                                boolean isEntity = "entity".equals(curChild.getNodeName());
                                boolean isViewEntity = "view-entity".equals(curChild.getNodeName());
                                if ((isEntity || isViewEntity) && curChild.getNodeType() == Node.ELEMENT_NODE) {
                                    i++;
                                    Element curEntity = (Element) curChild;
                                    String entityName = UtilXml.checkEmpty(curEntity.getAttribute("entity-name"));

                                    //add entityName to appropriate fileNameEntities collection
                                    Collection fileEntityNames = (Collection) fileNameEntities.get(entityFileName);
                                    if (fileEntityNames == null) {
                                        fileEntityNames = new LinkedList();
                                        fileNameEntities.put(entityFileName, fileEntityNames);
                                    }
                                    fileEntityNames.add(entityName);

                                    //check to see if entity with same name has already been read
                                    if (entityCache.containsKey(entityName)) {
                                        Debug.logWarning("WARNING: Entity " + entityName + " is defined more than once, most recent will over-write previous definition(s)");
                                        Debug.logWarning("WARNING: Entity " + entityName + " was found in file " + entityFileName + ", but was already defined in file " + (String) entityFile.get(entityName));
                                    }

                                    //add entityName, entityFileName pair to entityFile map
                                    entityFile.put(entityName, entityFileName);

                                    //utilTimer.timerString("  After entityEntityName -- " + i + " --");
                                    //ModelEntity entity = createModelEntity(curEntity, docElement, utilTimer, docElementValues);

                                    ModelEntity entity = null;
                                    if (isEntity) {
                                        entity = createModelEntity(curEntity, docElement, null, docElementValues);
                                    } else {
                                        entity = createModelViewEntity(curEntity, docElement, null, docElementValues);
                                        //put the view entity in a list to get ready for the second pass to populate fields...
                                        tempViewEntityList.add(entity);
                                    }

                                    //utilTimer.timerString("  After createModelEntity -- " + i + " --");
                                    if (entity != null) {
                                        entityCache.put(entityName, entity);
                                        //utilTimer.timerString("  After entityCache.put -- " + i + " --");
                                        if (isEntity) {
                                            Debug.logInfo("-- [Entity]: #" + i + ": " + entityName);
                                        } else {
                                            Debug.logInfo("-- [ViewEntity]: #" + i + ": " + entityName);
                                        }
                                    } else {
                                        Debug.logWarning("-- -- ENTITYGEN ERROR:getModelEntity: Could not create entity for entityName: " + entityName);
                                    }

                                }
                            } while ((curChild = curChild.getNextSibling()) != null);
                        } else {
                            Debug.logWarning("No child nodes found.");
                        }
                        utilTimer.timerString("Finished file " + entityFileName + " - Total Entities: " + i + " FINISHED");
                    }

                    //do a pass on all of the view entities now that all of the entities have
                    // loaded and populate the fields
                    for (int velInd = 0; velInd < tempViewEntityList.size(); velInd++) {
                        ModelViewEntity curViewEntity = (ModelViewEntity) tempViewEntityList.get(velInd);
                        curViewEntity.populateFields(entityCache);
                    }

                    Debug.log("FINISHED LOADING ENTITIES - ALL FILES; #Entities=" + numEntities + " #ViewEntities=" + numViewEntities + " #Fields=" + numFields + " #Relationships=" + numRelations);
                }
            }
        }
        return entityCache;
    }

    /** rebuilds the fileNameEntities Map of Collections based on the current
     *  entityFile Map, must be done whenever a manual change is made to the
     *  entityFile Map after the initial load to make them consistent again.
     */
    public void rebuildFileNameEntities() {
        fileNameEntities = new HashMap();
        Iterator entityFileIter = entityFile.entrySet().iterator();
        while (entityFileIter.hasNext()) {
            Map.Entry entry = (Map.Entry) entityFileIter.next();
            //add entityName to appropriate fileNameEntities collection
            Collection fileEntityNames = (Collection) fileNameEntities.get(entry.getValue());
            if (fileEntityNames == null) {
                fileEntityNames = new LinkedList();
                fileNameEntities.put(entry.getValue(), fileEntityNames);
            }
            fileEntityNames.add(entry.getKey());
        }
    }

    /** Gets an Entity object based on a definition from the specified XML Entity descriptor file.
     * @param entityName The entityName of the Entity definition to use.
     * @return An Entity object describing the specified entity of the specified descriptor file.
     */
    public ModelEntity getModelEntity(String entityName) {
        Map ec = getEntityCache();
        if (ec == null) {
            throw new java.lang.IllegalStateException("ERROR: Unable to load Entity Cache");
        }
        return (ModelEntity) ec.get(entityName);
    }

    /** Creates a Iterator with the entityName of each Entity defined in the specified XML Entity Descriptor file.
     * @return A Iterator of entityName Strings
     */
    public Iterator getEntityNamesIterator() {
        Collection collection = getEntityNames();
        if (collection != null) {
            return collection.iterator();
        } else {
            return null;
        }
    }

    /** Creates a Collection with the entityName of each Entity defined in the specified XML Entity Descriptor file.
     * @return A Collection of entityName Strings
     */
    public Collection getEntityNames() {
        Map ec = getEntityCache();
        if (ec == null) {
            throw new java.lang.IllegalStateException("ERROR: Unable to load Entity Cache");
        }
        return ec.keySet();
    }

    protected void populateBasicInfo(ModelEntity entity, Element entityElement, Element docElement, Hashtable docElementValues) {
        entity.entityName = UtilXml.checkEmpty(entityElement.getAttribute("entity-name"));
        entity.tableName = UtilXml.checkEmpty(entityElement.getAttribute("table-name"), ModelUtil.javaNameToDbName(entity.entityName));
        entity.packageName = UtilXml.checkEmpty(entityElement.getAttribute("package-name"));
        entity.dependentOn = UtilXml.checkEmpty(entityElement.getAttribute("dependent-on"));

        if (docElementValues == null) {
            entity.title = UtilXml.checkEmpty(entityElement.getAttribute("title"), UtilXml.childElementValue(docElement, "title"), "None");
            entity.description = UtilXml.checkEmpty(UtilXml.childElementValue(entityElement, "description"), UtilXml.childElementValue(docElement, "description"), "None");
            entity.copyright = UtilXml.checkEmpty(entityElement.getAttribute("copyright"), UtilXml.childElementValue(docElement, "copyright"), "Copyright (c) 2001 The Open For Business Project - www.ofbiz.org");
            entity.author = UtilXml.checkEmpty(entityElement.getAttribute("author"), UtilXml.childElementValue(docElement, "author"), "None");
            entity.version = UtilXml.checkEmpty(entityElement.getAttribute("version"), UtilXml.childElementValue(docElement, "version"), "1.0");
        } else {
            if (!docElementValues.containsKey("title")) docElementValues.put("title", UtilXml.childElementValue(docElement, "title"));
            if (!docElementValues.containsKey("description")) docElementValues.put("description", UtilXml.childElementValue(docElement, "description"));
            if (!docElementValues.containsKey("copyright")) docElementValues.put("copyright", UtilXml.childElementValue(docElement, "copyright"));
            if (!docElementValues.containsKey("author")) docElementValues.put("author", UtilXml.childElementValue(docElement, "author"));
            if (!docElementValues.containsKey("version")) docElementValues.put("version", UtilXml.childElementValue(docElement, "version"));
            entity.title = UtilXml.checkEmpty(entityElement.getAttribute("title"), (String) docElementValues.get("title"), "None");
            entity.description = UtilXml.checkEmpty(UtilXml.childElementValue(entityElement, "description"), (String) docElementValues.get("description"), "None");
            entity.copyright = UtilXml.checkEmpty(entityElement.getAttribute("copyright"), (String) docElementValues.get("copyright"), "Copyright (c) 2001 The Open For Business Project - www.ofbiz.org");
            entity.author = UtilXml.checkEmpty(entityElement.getAttribute("author"), (String) docElementValues.get("author"), "None");
            entity.version = UtilXml.checkEmpty(entityElement.getAttribute("version"), (String) docElementValues.get("version"), "1.0");
        }
    }

    protected void populateRelated(ModelEntity entity, Element entityElement) {
        NodeList relationList = entityElement.getElementsByTagName("relation");
        for (int i = 0; i < relationList.getLength(); i++) {
            Element relationElement = (Element) relationList.item(i);
            if (relationElement.getParentNode() == entityElement) {
                ModelRelation relation = createRelation(entity, relationElement);
                if (relation != null) entity.relations.add(relation);
            }
        }
    }

    ModelEntity createModelEntity(Element entityElement, Element docElement, UtilTimer utilTimer, Hashtable docElementValues) {
        if (entityElement == null) return null;
        numEntities++;
        ModelEntity entity = new ModelEntity();
        entity.modelReader = this;

        if (utilTimer != null) utilTimer.timerString("  createModelEntity: before general/basic info");
        this.populateBasicInfo(entity, entityElement, docElement, docElementValues);

        if (utilTimer != null) utilTimer.timerString("  createModelEntity: before fields");
        NodeList fieldList = entityElement.getElementsByTagName("field");
        for (int i = 0; i < fieldList.getLength(); i++) {
            ModelField field = createModelField((Element) fieldList.item(i), docElement, docElementValues);
            if (field != null) entity.fields.add(field);
        }

        if (utilTimer != null) utilTimer.timerString("  createModelEntity: before prim-keys");
        NodeList pkList = entityElement.getElementsByTagName("prim-key");
        for (int i = 0; i < pkList.getLength(); i++) {
            ModelField field = findModelField(entity, ((Element) pkList.item(i)).getAttribute("field"));
            if (field != null) {
                entity.pks.add(field);
                field.isPk = true;
            } else {
                Debug.logError("[ModelReader.createModelEntity] ERROR: Could not find field \"" + ((Element) pkList.item(i)).getAttribute("field") + "\" specified in a prim-key");
            }
        }

        //now that we have the pks and the fields, make the nopks vector
        entity.nopks = new Vector();
        for (int ind = 0; ind < entity.fields.size(); ind++) {
            ModelField field = (ModelField) entity.fields.elementAt(ind);
            if (!field.isPk) entity.nopks.add(field);
        }

        if (utilTimer != null) utilTimer.timerString("  createModelEntity: before relations");
        this.populateRelated(entity, entityElement);

        return entity;
    }

    ModelEntity createModelViewEntity(Element entityElement, Element docElement, UtilTimer utilTimer, Hashtable docElementValues) {
        if (entityElement == null) return null;
        numViewEntities++;
        ModelViewEntity entity = new ModelViewEntity();
        entity.modelReader = this;

        if (utilTimer != null) utilTimer.timerString("  createModelViewEntity: before general/basic info");
        this.populateBasicInfo(entity, entityElement, docElement, docElementValues);

        if (utilTimer != null) utilTimer.timerString("  createModelViewEntity: before \"member-entity\"s");
        NodeList membEntList = entityElement.getElementsByTagName("member-entity");
        for (int i = 0; i < membEntList.getLength(); i++) {
            Element membEnt = (Element) membEntList.item(i);
            String alias = UtilXml.checkEmpty(membEnt.getAttribute("entity-alias"));
            String name = UtilXml.checkEmpty(membEnt.getAttribute("entity-name"));
            if (name.length() <= 0 || alias.length() <= 0) {
                Debug.logWarning("[ModelReader.createModelViewEntity] Warning: entity-alias or entity-name missing on member-entity element");
            } else {
                entity.memberEntities.put(alias, name);
            }
        }

        //when reading aliases, just read them into the alias list, there will be a pass
        // after loading all entities to go back and fill in all of the ModelField entries
        if (utilTimer != null) utilTimer.timerString("  createModelViewEntity: before aliases");
        NodeList aliasList = entityElement.getElementsByTagName("alias");
        for (int i = 0; i < aliasList.getLength(); i++) {
            ModelViewEntity.ModelAlias alias = entity.makeModelAlias();
            Element aliasElement = (Element) aliasList.item(i);
            alias.entityAlias = UtilXml.checkEmpty(aliasElement.getAttribute("entity-alias"));
            alias.name = UtilXml.checkEmpty(aliasElement.getAttribute("name"));
            alias.field = UtilXml.checkEmpty(aliasElement.getAttribute("field"), alias.name);
            alias.isPk = "true".equals(aliasElement.getAttribute("prim-key"));
            entity.aliases.add(alias);
        }

        NodeList viewLinkList = entityElement.getElementsByTagName("view-link");
        for (int i = 0; i < viewLinkList.getLength(); i++) {
            Element viewLinkElement = (Element) viewLinkList.item(i);
            ModelViewEntity.ModelViewLink viewLink = entity.makeModelViewLink();
            viewLink.entityAlias = UtilXml.checkEmpty(viewLinkElement.getAttribute("entity-alias"));
            viewLink.relEntityAlias = UtilXml.checkEmpty(viewLinkElement.getAttribute("rel-entity-alias"));

            NodeList keyMapList = viewLinkElement.getElementsByTagName("key-map");
            for (int j = 0; j < keyMapList.getLength(); j++) {
                Element keyMapElement = (Element) keyMapList.item(j);
                ModelKeyMap keyMap = createKeyMap(keyMapElement);
                if (keyMap != null) viewLink.keyMaps.add(keyMap);
            }
            entity.viewLinks.add(viewLink);
        }

        if (utilTimer != null) utilTimer.timerString("  createModelEntity: before relations");
        this.populateRelated(entity, entityElement);

        //before finishing, make sure the table name is null, this should help bring up errors early...
        entity.tableName = null;

        return entity;
    }

    ModelRelation createRelation(ModelEntity entity, Element relationElement) {
        numRelations++;
        ModelRelation relation = new ModelRelation();
        relation.mainEntity = entity;

        relation.type = UtilXml.checkEmpty(relationElement.getAttribute("type"));
        relation.title = UtilXml.checkEmpty(relationElement.getAttribute("title"));
        relation.relEntityName = UtilXml.checkEmpty(relationElement.getAttribute("rel-entity-name"));

        NodeList keyMapList = relationElement.getElementsByTagName("key-map");
        for (int i = 0; i < keyMapList.getLength(); i++) {
            Element keyMapElement = (Element) keyMapList.item(i);
            if (keyMapElement.getParentNode() == relationElement) {
                ModelKeyMap keyMap = createKeyMap(keyMapElement);
                if (keyMap != null) {
                    relation.keyMaps.add(keyMap);
                }
            }
        }

        return relation;
    }

    ModelKeyMap createKeyMap(Element keyMapElement) {
        ModelKeyMap keyMap = new ModelKeyMap();

        keyMap.fieldName = UtilXml.checkEmpty(keyMapElement.getAttribute("field-name"));
        //if no relFieldName is specified, use the fieldName; this is convenient for when they are named the same, which is often the case
        keyMap.relFieldName = UtilXml.checkEmpty(keyMapElement.getAttribute("rel-field-name"), keyMap.fieldName);

        return keyMap;
    }

    ModelField findModelField(ModelEntity entity, String fieldName) {
        for (int i = 0; i < entity.fields.size(); i++) {
            ModelField field = (ModelField) entity.fields.elementAt(i);
            if (field.name.compareTo(fieldName) == 0) {
                return field;
            }
        }
        return null;
    }

    ModelField createModelField(Element fieldElement, Element docElement, Hashtable docElementValues) {
        if (fieldElement == null) {
            return null;
        }

        numFields++;
        ModelField field = new ModelField();

        field.type = UtilXml.checkEmpty(fieldElement.getAttribute("type"));
        field.name = UtilXml.checkEmpty(fieldElement.getAttribute("name"));
        field.colName = UtilXml.checkEmpty(fieldElement.getAttribute("col-name"), ModelUtil.javaNameToDbName(UtilXml.checkEmpty(field.name)));
        field.isPk = false; //is set elsewhere

        NodeList validateList = fieldElement.getElementsByTagName("validate");
        for (int i = 0; i < validateList.getLength(); i++) {
            Element element = (Element) validateList.item(i);
            field.validators.add(UtilXml.checkEmpty(element.getAttribute("name")));
        }

        return field;
    }

    protected Document getDocument(String filename) {
        if (filename == null) return null;
        Document document = null;
        try {
            document = UtilXml.readXmlDocument(UtilURL.fromFilename(filename));
        } catch (SAXException sxe) {
            // Error generated during parsing)
            Exception x = sxe;
            if (sxe.getException() != null) x = sxe.getException();
            x.printStackTrace();
        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return document;
    }
}
