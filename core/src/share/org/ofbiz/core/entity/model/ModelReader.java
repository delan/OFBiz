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
 * Generic Entity - Entity Definition Reader
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    May 15, 2001
 *@version    1.0
 */

public class ModelReader {

    public static final String module = ModelReader.class.getName();
    public static UtilCache readers = new UtilCache("ModelReader", 0, 0);

    protected Map entityCache = null;

    protected int numEntities = 0;
    protected int numViewEntities = 0;
    protected int numFields = 0;
    protected int numRelations = 0;

    protected String modelName;

    /** collection of filenames for entity definitions */
    protected Collection entityFileNames;

    /** contains a collection of entity names for each filename, populated as they are loaded */
    protected Map fileNameEntities;
    /** for each entity contains a map to the filename that the entity came from */
    protected Map entityFile;

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
                Debug.logWarning("WARNING: Entity filename " + tempFileName + " is listed more than once in the " +
                                 "entity file list: " + wholeFileNamesStr, module);
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
                                        Debug.logWarning("WARNING: Entity " + entityName +
                                                         " is defined more than once, most recent will over-write " +
                                                         "previous definition(s)", module);
                                        Debug.logWarning("WARNING: Entity " + entityName + " was found in file " +
                                                         entityFileName + ", but was already defined in file " +
                                                         (String) entityFile.get(entityName), module);
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
                                            Debug.logVerbose("-- [Entity]: #" + i + ": " + entityName, module);
                                        } else {
                                            Debug.logVerbose("-- [ViewEntity]: #" + i + ": " + entityName, module);
                                        }
                                    } else {
                                        Debug.logWarning("-- -- ENTITYGEN ERROR:getModelEntity: Could not create " +
                                                         "entity for entityName: " + entityName, module);
                                    }

                                }
                            } while ((curChild = curChild.getNextSibling()) != null);
                        } else {
                            Debug.logWarning("No child nodes found.", module);
                        }
                        utilTimer.timerString("Finished file " + entityFileName + " - Total Entities: " + i + " FINISHED");
                    }

                    //do a pass on all of the view entities now that all of the entities have
                    // loaded and populate the fields
                    for (int velInd = 0; velInd < tempViewEntityList.size(); velInd++) {
                        ModelViewEntity curViewEntity = (ModelViewEntity) tempViewEntityList.get(velInd);
                        curViewEntity.populateFields(entityCache);
                    }

                    Debug.log("FINISHED LOADING ENTITIES - ALL FILES; #Entities=" + numEntities + " #ViewEntities=" +
                              numViewEntities + " #Fields=" + numFields + " #Relationships=" + numRelations, module);
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
    
    public Iterator getFileNameEntitiesKeyIterator() {
        if (fileNameEntities == null) return null;
        return fileNameEntities.keySet().iterator();
    }

    public Collection getFileNameEntities(String entityFileName) {
        if (fileNameEntities == null) return null;
        return (Collection) fileNameEntities.get(entityFileName);
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

    ModelEntity createModelEntity(Element entityElement, Element docElement, UtilTimer utilTimer, Hashtable docElementValues) {
        if (entityElement == null) return null;
        numEntities++;
        ModelEntity entity = new ModelEntity(this, entityElement, docElement, utilTimer, docElementValues);

        return entity;
    }

    ModelEntity createModelViewEntity(Element entityElement, Element docElement, UtilTimer utilTimer, Hashtable docElementValues) {
        if (entityElement == null) return null;
        numViewEntities++;
        ModelViewEntity entity = new ModelViewEntity(this, entityElement, docElement, utilTimer, docElementValues);

        return entity;
    }

    public ModelRelation createRelation(ModelEntity entity, Element relationElement) {
        numRelations++;
        ModelRelation relation = new ModelRelation(entity, relationElement);

        return relation;
    }

    public ModelField findModelField(ModelEntity entity, String fieldName) {
        for (int i = 0; i < entity.fields.size(); i++) {
            ModelField field = (ModelField) entity.fields.elementAt(i);
            if (field.name.compareTo(fieldName) == 0) {
                return field;
            }
        }
        return null;
    }

    public ModelField createModelField(Element fieldElement, Element docElement, Hashtable docElementValues) {
        if (fieldElement == null) {
            return null;
        }

        numFields++;
        ModelField field = new ModelField(fieldElement);

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
