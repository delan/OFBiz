/*
 * $Id: EntityDataServices.java,v 1.5 2003/12/20 20:26:21 ajzeneski Exp $
 *
 * Copyright (c) 2001-2003 The Open For Business Project - www.ofbiz.org
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
 *
 */
package org.ofbiz.entityext.data;

import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.security.Security;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilURL;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;

/**
 * Entity Data Import/Export Services
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.5 $
 * @since      2.1
 */
public class EntityDataServices {

    public static final String module = EntityDataServices.class.getName();

    public static Map exportDelimitedToDirectory(DispatchContext dctx, Map context) {
        return ServiceUtil.returnError("This service is not implemented yet.");
    }

    public static Map importDelimitedFromDirectory(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        Security security = dctx.getSecurity();

        // check permission
         GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (!security.hasPermission("ENTITY_MAINT", userLogin)) {
            return ServiceUtil.returnError("You do not have permission to run this service.");
        }

        // get the directory & delimiter
        String rootDirectory = (String) context.get("rootDirectory");
        URL rootDirectoryUrl = UtilURL.fromResource(rootDirectory);
        if (rootDirectoryUrl == null) {
            return ServiceUtil.returnError("Unable to locate root directory : " + rootDirectory);
        }

        File root = null;
        try {
            root = new File(new URI(rootDirectoryUrl.toExternalForm()));
        } catch (URISyntaxException e) {
            return ServiceUtil.returnError("Unable to get root directory URI");
        }

        if (!root.exists() || !root.isDirectory() || !root.canRead()) {
            return ServiceUtil.returnError("Root directory does not exist or is not readable.");
        }
        String delimiter = (String) context.get("delimiter");
        if (delimiter == null) {
            // default delimiter is tab
            delimiter = "\t";
        }

        // get the file list
        File[] files = root.listFiles();
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (!fileName.startsWith("_") && fileName.endsWith(".txt")) {
                    int records = 0;
                    try {
                        records = readEntityFile(files[i], delimiter, delegator);
                    } catch (GeneralException e) {
                        return ServiceUtil.returnError(e.getMessage());
                    } catch (FileNotFoundException e) {
                        return ServiceUtil.returnError("File not found : " + files[i].getName());
                    } catch (IOException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError("Problem reading file : " + files[i].getName());
                    }

                    Debug.logInfo("Imported/Updated [" + records + "] from : " + files[i].getAbsolutePath(), module);
                }
            }
        } else {
            return ServiceUtil.returnError("No files available for reading in this root directory : " + rootDirectory);
        }

        return ServiceUtil.returnSuccess();
    }

    private static String[] readEntityHeader(File file, String delimiter, BufferedReader dataReader) throws IOException {
        String filePath = file.getPath().replace('\\', '/');

        String[] header = null;
        File headerFile = new File(filePath.substring(0, filePath.lastIndexOf('/')), "_" + file.getName());

        boolean uniqueHeaderFile = true;
        BufferedReader reader = null;
        if (headerFile.exists()) {
            reader = new BufferedReader(new FileReader(headerFile));
        } else {
            uniqueHeaderFile = false;
            reader = dataReader;
        }

        // read one line from either the header file or the data file if no header file exists
        String firstLine = reader.readLine();
        if (firstLine != null) {
            header = firstLine.split(delimiter);
        }

        if (uniqueHeaderFile) {
            reader.close();
        }

        return header;
    }

    private static int readEntityFile(File file, String delimiter, GenericDelegator delegator) throws IOException, GeneralException {
        String entityName = file.getName().substring(0, file.getName().lastIndexOf('.'));
        if (entityName == null) {
            throw new GeneralException("Entity name cannot be null : [" + file.getName() + "]");
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String[] header = readEntityHeader(file, delimiter, reader);

        GeneralException exception = null;
        String line = null;
        int lineNumber = 1;
        while ((line = reader.readLine()) != null) {
            // process the record
            String fields[] = line.split(delimiter);

            if (fields.length < 1) {
                exception = new GeneralException("Illegal number of fields [" + file.getName() + " / " + lineNumber);
                break;
            }

            Map fieldMap = makeMap(header, fields);
            GenericValue newValue = delegator.makeValue(entityName, fieldMap);
            newValue = delegator.createOrStore(newValue);

            if (lineNumber % 500 == 0 || lineNumber == 1) {
                Debug.log("Records Stored [" + file.getName() + "]: " + lineNumber, module);
                //Debug.log("Last record : " + newValue, module);
            }

            lineNumber++;
        }
        reader.close();

        // now that we closed the reader; throw the exception
        if (exception != null) {
            throw exception;
        }

        return lineNumber;
    }

    private static Map makeMap(String[] header, String[] line) {
        Map newMap = new HashMap();
        for (int i = 0; i < header.length; i++) {
            String name = header[i].trim();

            String value = null;
            if (i < line.length) {
                value = line[i];
            }

            // check for null values
            if (value != null && value.length() > 0) {
                char first = value.charAt(0);
                if (first == 0x00) {
                    value = null;
                }

                // trim non-null values
                if (value != null) {
                    value = value.trim();
                }
            } else {
                value = null;
            }

            // insert into map
            newMap.put(name, value);
        }
        return newMap;
    }

    private String[] getEntityFieldNames(GenericDelegator delegator, String entityName) {
        ModelEntity entity = delegator.getModelEntity(entityName);
        if (entity == null) {
            return null;
        }
        List modelFields = entity.getFieldsCopy();
        if (modelFields == null) {
            return null;
        }

        String[] fieldNames = new String[modelFields.size()];
        for (int i = 0; i < modelFields.size(); i++) {
            ModelField field = (ModelField) modelFields.get(i);
            fieldNames[i] = field.getName();
        }

        return fieldNames;
    }

}
