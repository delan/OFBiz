/*
 * $Id$
 */

package org.ofbiz.core.datafile;

import java.io.*;
import java.util.*;
import java.net.*;
import javax.xml.parsers.*;

import org.xml.sax.*;
import org.w3c.dom.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Flat File definition reader
 * <p><b>Description:</b> None
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
 * @author <a href='mailto:jonesde@ofbiz.org'>David E. Jones</a>
 * @created Nov 14, 2001
 * @version 1.0
 */

public class ModelDataFileReader {

    public static UtilCache readers = new UtilCache("ModelDataFile", 0, 0);

    public URL readerURL = null;
    public Map modelDataFiles = null;

    public static ModelDataFileReader getModelDataFileReader(URL readerURL) {
        ModelDataFileReader reader = null;
        reader = (ModelDataFileReader) readers.get(readerURL);
        if (reader == null) { //don't want to block here
            synchronized (ModelDataFileReader.class) {
                //must check if null again as one of the blocked threads can still enter
                reader = (ModelDataFileReader) readers.get(readerURL);
                if (reader == null) {
                    if (Debug.infoOn()) Debug.logVerbose("[ModelDataFileReader.getModelDataFileReader] : creating reader.");
                    reader = new ModelDataFileReader(readerURL);
                    readers.put(readerURL, reader);
                }
            }
        }
        if (reader != null && (reader.modelDataFiles == null || reader.modelDataFiles.size() == 0)) {
            readers.remove(readerURL);
            return null;
        }
        if (Debug.infoOn()) Debug.logVerbose("[ModelDataFileReader.getModelDataFileReader] : returning reader.");
        return reader;
    }

    public ModelDataFileReader(URL readerURL) {
        this.readerURL = readerURL;

        //preload models...
        getModelDataFiles();
    }

    public Map getModelDataFiles() {
        if (modelDataFiles == null) { //don't want to block here
            synchronized (ModelDataFileReader.class) {
                //must check if null again as one of the blocked threads can still enter
                if (modelDataFiles == null) { //now it's safe
                    modelDataFiles = new HashMap();

                    UtilTimer utilTimer = new UtilTimer();

                    utilTimer.timerString("Before getDocument in file " + readerURL);
                    Document document = getDocument(readerURL);
                    if (document == null) {
                        modelDataFiles = null;
                        return null;
                    }

                    utilTimer.timerString("Before getDocumentElement in file " + readerURL);
                    Element docElement = document.getDocumentElement();
                    if (docElement == null) {
                        modelDataFiles = null;
                        return null;
                    }
                    docElement.normalize();
                    Node curChild = docElement.getFirstChild();

                    int i = 0;
                    if (curChild != null) {
                        utilTimer.timerString("Before start of dataFile loop in file " + readerURL);
                        do {
                            if (curChild.getNodeType() == Node.ELEMENT_NODE && "data-file".equals(curChild.getNodeName())) {
                                i++;
                                Element curDataFile = (Element) curChild;
                                String dataFileName = UtilXml.checkEmpty(curDataFile.getAttribute("name"));

                                //check to see if dataFile with same name has already been read
                                if (modelDataFiles.containsKey(dataFileName)) {
                                    Debug.logWarning("WARNING: DataFile " + dataFileName +
                                                     " is defined more than once, most recent will over-write previous definition(s)");
                                }

                                //utilTimer.timerString("  After dataFileName -- " + i + " --");
                                ModelDataFile dataFile = createModelDataFile(curDataFile);
                                //utilTimer.timerString("  After createModelDataFile -- " + i + " --");
                                if (dataFile != null) {
                                    modelDataFiles.put(dataFileName, dataFile);
                                    //utilTimer.timerString("  After modelDataFiles.put -- " + i + " --");
                                    if (Debug.infoOn()) Debug.logVerbose("-- getModelDataFile: #" + i + " Loaded dataFile: " + dataFileName);
                                } else
                                    Debug.logWarning("-- -- SERVICE ERROR:getModelDataFile: Could not create dataFile for dataFileName: " + dataFileName);

                            }
                        } while ((curChild = curChild.getNextSibling()) != null)
                                ;
                    } else
                        Debug.logWarning("No child nodes found.");
                    utilTimer.timerString("Finished file " + readerURL + " - Total Flat File Defs: " + i + " FINISHED");
                }
            }
        }
        return modelDataFiles;
    }

    /** Gets an DataFile object based on a definition from the specified XML DataFile descriptor file.
     * @param dataFileName The dataFileName of the DataFile definition to use.
     * @return An DataFile object describing the specified dataFile of the specified descriptor file.
     */
    public ModelDataFile getModelDataFile(String dataFileName) {
        Map ec = getModelDataFiles();
        if (ec != null)
            return (ModelDataFile) ec.get(dataFileName);
        else
            return null;
    }

    /** Creates a Iterator with the dataFileName of each DataFile defined in the specified XML DataFile Descriptor file.
     * @return A Iterator of dataFileName Strings
     */
    public Iterator getDataFileNamesIterator() {
        Collection collection = getDataFileNames();
        if (collection != null)
            return collection.iterator();
        else
            return null;
    }

    /** Creates a Collection with the dataFileName of each DataFile defined in the specified XML DataFile Descriptor file.
     * @return A Collection of dataFileName Strings
     */
    public Collection getDataFileNames() {
        Map ec = getModelDataFiles();
        return ec.keySet();
    }

    protected ModelDataFile createModelDataFile(Element dataFileElement) {
        ModelDataFile dataFile = new ModelDataFile();
        String tempStr;

        dataFile.name = UtilXml.checkEmpty(dataFileElement.getAttribute("name"));
        dataFile.typeCode = UtilXml.checkEmpty(dataFileElement.getAttribute("type-code"));
        dataFile.sender = UtilXml.checkEmpty(dataFileElement.getAttribute("sender"));
        dataFile.receiver = UtilXml.checkEmpty(dataFileElement.getAttribute("receiver"));

        tempStr = UtilXml.checkEmpty(dataFileElement.getAttribute("record-length"));
        if (tempStr != null && tempStr.length() > 0)
            dataFile.recordLength = Integer.parseInt(tempStr);
        tempStr = UtilXml.checkEmpty(dataFileElement.getAttribute("delimiter"));
        if (tempStr != null && tempStr.length() == 1)
            dataFile.delimiter = tempStr.charAt(0);

        dataFile.separatorStyle = UtilXml.checkEmpty(dataFileElement.getAttribute("separator-style"));
        dataFile.description = UtilXml.checkEmpty(dataFileElement.getAttribute("description"));

        NodeList rList = dataFileElement.getElementsByTagName("record");
        for (int i = 0; i < rList.getLength(); i++) {
            Element recordElement = (Element) rList.item(i);
            ModelRecord modelRecord = createModelRecord(recordElement);
            if (modelRecord != null)
                dataFile.records.add(modelRecord);
            else
                Debug.logWarning("[ModelDataFileReader.createModelDataFile] Weird, modelRecord was null");
        }

        for (int i = 0; i < dataFile.records.size(); i++) {
            ModelRecord modelRecord = (ModelRecord) dataFile.records.get(i);
            if (modelRecord.parentName.length() > 0) {
                ModelRecord parentRecord = dataFile.getModelRecord(modelRecord.parentName);
                if (parentRecord != null) {
                    parentRecord.childRecords.add(modelRecord);
                    modelRecord.parentRecord = parentRecord;
                } else {
                    Debug.logError("[ModelDataFileReader.createModelDataFile] ERROR: Could not find parentRecord with name " + modelRecord.parentName);
                }
            }
        }

        return dataFile;
    }

    protected ModelRecord createModelRecord(Element recordElement) {
        ModelRecord record = new ModelRecord();
        String tempStr;

        record.name = UtilXml.checkEmpty(recordElement.getAttribute("name"));
        record.typeCode = UtilXml.checkEmpty(recordElement.getAttribute("type-code"));

        record.tcMin = UtilXml.checkEmpty(recordElement.getAttribute("tc-min"));
        if (record.tcMin.length() > 0) record.tcMinNum = Long.parseLong(record.tcMin);
        record.tcMax = UtilXml.checkEmpty(recordElement.getAttribute("tc-max"));
        if (record.tcMax.length() > 0) record.tcMaxNum = Long.parseLong(record.tcMax);

        tempStr = UtilXml.checkEmpty(recordElement.getAttribute("tc-isnum"));
        if (tempStr != null && tempStr.length() > 0)
            record.tcIsNum = Boolean.valueOf(tempStr).booleanValue();

        tempStr = UtilXml.checkEmpty(recordElement.getAttribute("tc-position"));
        if (tempStr != null && tempStr.length() > 0)
            record.tcPosition = Integer.parseInt(tempStr);
        tempStr = UtilXml.checkEmpty(recordElement.getAttribute("tc-length"));
        if (tempStr != null && tempStr.length() > 0)
            record.tcLength = Integer.parseInt(tempStr);

        record.description = UtilXml.checkEmpty(recordElement.getAttribute("description"));
        record.parentName = UtilXml.checkEmpty(recordElement.getAttribute("parent-name"));
        record.limit = UtilXml.checkEmpty(recordElement.getAttribute("limit"));

        NodeList fList = recordElement.getElementsByTagName("field");
        int priorEnd = -1;
        for (int i = 0; i < fList.getLength(); i++) {
            Element fieldElement = (Element) fList.item(i);
            ModelField modelField = createModelField(fieldElement);

            // if the position is not specified, assume the start position based on last entry
            if ((i > 0) && (modelField.position == -1)) {
                modelField.position = priorEnd;
            }
            priorEnd = modelField.position + modelField.length;

            if (modelField != null)
                record.fields.add(modelField);
            else
                Debug.logWarning("[ModelDataFileReader.createModelRecord] Weird, modelField was null");
        }

        return record;
    }

    protected ModelField createModelField(Element fieldElement) {
        ModelField field = new ModelField();
        String tempStr;

        field.name = UtilXml.checkEmpty(fieldElement.getAttribute("name"));

        tempStr = UtilXml.checkEmpty(fieldElement.getAttribute("position"));
        if (tempStr != null && tempStr.length() > 0)
            field.position = Integer.parseInt(tempStr);
        tempStr = UtilXml.checkEmpty(fieldElement.getAttribute("length"));
        if (tempStr != null && tempStr.length() > 0)
            field.length = Integer.parseInt(tempStr);

        field.type = UtilXml.checkEmpty(fieldElement.getAttribute("type"));
        field.format = UtilXml.checkEmpty(fieldElement.getAttribute("format"));
        field.validExp = UtilXml.checkEmpty(fieldElement.getAttribute("valid-exp"));
        field.description = UtilXml.checkEmpty(fieldElement.getAttribute("description"));

        tempStr = UtilXml.checkEmpty(fieldElement.getAttribute("prim-key"));
        if (tempStr != null && tempStr.length() == 1)
            field.isPk = Boolean.valueOf(tempStr).booleanValue();

        return field;
    }

    protected Document getDocument(URL url) {
        if (url == null)
            return null;
        Document document = null;
        try {
            document = UtilXml.readXmlDocument(url);
        } catch (SAXException sxe) {
            // Error generated during parsing)
            Exception x = sxe;
            if (sxe.getException() != null)
                x = sxe.getException();
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

