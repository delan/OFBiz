/*
 * $Id: EntityDataServices.java,v 1.2 2003/12/12 04:02:04 ajzeneski Exp $
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
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilURL;

import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;

/**
 * Entity Data Import/Export Services
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.2 $
 * @since      2.1
 */
public class EntityDataServices {

    public static final String module = EntityDataServices.class.getName();

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
                if (files[i].getName().toLowerCase().endsWith(".txt")) {
                    int records = 0;
                    try {
                        records = readEntityFile(files[i], delimiter, delegator);
                    } catch (GeneralException e) {
                        return ServiceUtil.returnError(e.getMessage());
                    } catch (FileNotFoundException e) {
                        return ServiceUtil.returnError("File not found : " + files[i].getName());
                    } catch (IOException e) {
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

    private static int readEntityFile(File file, String delimiter, GenericDelegator delegator) throws IOException, GeneralException {
        String entityName = file.getName().substring(0, file.getName().lastIndexOf('.'));
        if (entityName == null) {
            throw new GeneralException("Entity name cannot be null : [" + file.getName() + "]");
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String[] header = null;
        boolean first = true;

        String line = null;
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            if (first) {
                // this is the header line
                header = line.split(delimiter);
                first = false;
            } else {
                // process the record
                String fields[] = line.split(delimiter);
                if (fields.length != header.length) {
                    throw new GeneralException("Number of fields on line #" + lineNumber + " from file " + file.getName() + " does not match the header.");
                }

                Map fieldMap = makeMap(header, fields);
                GenericValue newValue = delegator.makeValue(entityName, fieldMap);
                newValue = delegator.createOrStore(newValue);
                //Debug.log("Stored record : " + newValue, module);
            }
            lineNumber++;
        }
        return lineNumber;
    }

    private static Map makeMap(String[] header, String[] line) {
        Map newMap = new HashMap();
        for (int i = 0; i < header.length; i++) {
            // strip off all whitespace
            newMap.put(header[i].replaceAll("\\s", ""), line[i].replaceAll("\\s", ""));
        }
        return newMap;
    }
}
