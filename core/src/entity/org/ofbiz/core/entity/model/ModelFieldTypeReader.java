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

import org.ofbiz.core.config.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.entity.config.*;


/**
 * Generic Entity - Field Type Definition Reader
 *
 * @author David E. Jones
 * @created May 15, 2001
 * @version 1.0
 */

public class ModelFieldTypeReader {

    public static final String module = ModelFieldTypeReader.class.getName();
    public static UtilCache readers = new UtilCache("entity.ModelFieldTypeReader", 0, 0);

    public Map fieldTypeCache = null;

    public int numEntities = 0;
    public int numFields = 0;
    public int numRelations = 0;

    public String modelName;
    public ResourceHandler fieldTypeResourceHandler;
    public String entityFileName;

    public static ModelFieldTypeReader getModelFieldTypeReader(String helperName) {
        EntityConfigUtil.DatasourceInfo datasourceInfo = EntityConfigUtil.getDatasourceInfo(helperName);
        String tempModelName = datasourceInfo.fieldTypeName;
        ModelFieldTypeReader reader = (ModelFieldTypeReader) readers.get(tempModelName);

        if (reader == null) //don't want to block here
        {
            synchronized (ModelFieldTypeReader.class) {
                //must check if null again as one of the blocked threads can still enter
                reader = (ModelFieldTypeReader) readers.get(tempModelName);
                if (reader == null) {
                    reader = new ModelFieldTypeReader(tempModelName);
                    readers.put(tempModelName, reader);
                }
            }
        }
        return reader;
    }

    public ModelFieldTypeReader(String modelName) {
        this.modelName = modelName;
        EntityConfigUtil.FieldTypeInfo fieldTypeInfo = EntityConfigUtil.getFieldTypeInfo(modelName);
        if (fieldTypeInfo == null) {
            throw new IllegalStateException("Could not find a field-type definition with name \"" + modelName + "\"");
        }
        fieldTypeResourceHandler = new ResourceHandler(EntityConfigUtil.ENTITY_ENGINE_XML_FILENAME, fieldTypeInfo.resourceElement);

        //preload caches...
        getFieldTypeCache();
    }

    public Map getFieldTypeCache() {
        if (fieldTypeCache == null) //don't want to block here
        {
            synchronized (ModelFieldTypeReader.class) {
                //must check if null again as one of the blocked threads can still enter
                if (fieldTypeCache == null) //now it's safe
                {
                    fieldTypeCache = new HashMap();

                    UtilTimer utilTimer = new UtilTimer();
                    //utilTimer.timerString("Before getDocument");

                    Document document = null;

                    try {
                        document = fieldTypeResourceHandler.getDocument();
                    } catch (GenericConfigException e) {
                        Debug.logError(e, "Error loading field type file");
                    }
                    if (document == null) {
                        fieldTypeCache = null;
                        return null;
                    }

                    //utilTimer.timerString("Before getDocumentElement");
                    Element docElement = document.getDocumentElement();

                    if (docElement == null) {
                        fieldTypeCache = null;
                        return null;
                    }
                    docElement.normalize();

                    Node curChild = docElement.getFirstChild();

                    int i = 0;

                    if (curChild != null) {
                        utilTimer.timerString("Before start of field type loop");
                        do {
                            if (curChild.getNodeType() == Node.ELEMENT_NODE && "field-type-def".equals(curChild.getNodeName())) {
                                i++;
                                //utilTimer.timerString("Start loop -- " + i + " --");
                                Element curFieldType = (Element) curChild;
                                String fieldTypeName = UtilXml.checkEmpty(curFieldType.getAttribute("type"), "[No type name]");
                                //utilTimer.timerString("  After fieldTypeName -- " + i + " --");
                                ModelFieldType fieldType = createModelFieldType(curFieldType, docElement, null);

                                //utilTimer.timerString("  After createModelFieldType -- " + i + " --");
                                if (fieldType != null) {
                                    fieldTypeCache.put(fieldTypeName, fieldType);
                                    //utilTimer.timerString("  After fieldTypeCache.put -- " + i + " --");
                                    Debug.logVerbose("-- getModelFieldType: #" + i + " Created fieldType: " + fieldTypeName, module);
                                } else {
                                    Debug.logWarning("-- -- ENTITYGEN ERROR:getModelFieldType: Could not create fieldType for fieldTypeName: " + fieldTypeName, module);
                                }

                            }
                        }
                        while ((curChild = curChild.getNextSibling()) != null);
                    } else
                        Debug.logWarning("No child nodes found.", module);
                    utilTimer.timerString("FINISHED - Total Field Types: " + i + " FINISHED");
                }
            }
        }
        return fieldTypeCache;
    }

    /** Creates a Collection with all of the ModelFieldType names
     * @return A Collection of ModelFieldType names
     */
    public Collection getFieldTypeNames() {
        Map ftc = getFieldTypeCache();

        return ftc.keySet();
    }

    /** Creates a Collection with all of the ModelFieldTypes
     * @return A Collection of ModelFieldTypes
     */
    public Collection getFieldTypes() {
        Map ftc = getFieldTypeCache();

        return ftc.values();
    }

    /** Gets an FieldType object based on a definition from the specified XML FieldType descriptor file.
     * @param fieldTypeName The fieldTypeName of the FieldType definition to use.
     * @return An FieldType object describing the specified fieldType of the specified descriptor file.
     */
    public ModelFieldType getModelFieldType(String fieldTypeName) {
        Map ftc = getFieldTypeCache();

        if (ftc != null)
            return (ModelFieldType) ftc.get(fieldTypeName);
        else
            return null;
    }

    ModelFieldType createModelFieldType(Element fieldTypeElement, Element docElement, UtilTimer utilTimer) {
        if (fieldTypeElement == null) return null;

        ModelFieldType field = new ModelFieldType(fieldTypeElement);

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
