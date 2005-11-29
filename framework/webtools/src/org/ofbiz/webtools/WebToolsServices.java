/*
 *  Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.webtools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.MalformedURLException;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntitySaxReader;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import org.xml.sax.InputSource;
import org.w3c.dom.*;

import freemarker.template.*;
import freemarker.ext.dom.NodeModel;
import freemarker.ext.beans.BeansWrapper;

/**
 * WebTools Services
 *
 * @author     <a href="mailto:tiz@sastau.it">Jacopo Cappellato</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 */

public class WebToolsServices {

    public static final String module = WebToolsServices.class.getName();

    public static Map entityImport(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        List messages = new ArrayList();

        String filename = (String)context.get("filename");
        String fmfilename = (String)context.get("fmfilename");
        String fulltext = (String)context.get("fulltext");
        boolean isUrl = (String)context.get("isUrl") != null;
        String mostlyInserts = (String)context.get("mostlyInserts");
        String maintainTimeStamps = (String)context.get("maintainTimeStamps");
        String createDummyFks = (String)context.get("createDummyFks");

        Integer txTimeout = (Integer)context.get("txTimeout");

        if (txTimeout == null) {
            txTimeout = new Integer(7200);
        }
        InputSource ins = null;
        URL url = null;

        // #############################
        // The filename to parse is prepared
        // #############################
        if (filename != null && filename.length() > 0) {
            try {
                url = isUrl?new URL(filename):UtilURL.fromFilename(filename);
            } catch(MalformedURLException e) {
//                errorMessageList.add("ERROR: " + e.getMessage());
            }
            InputStream is = null;
            try {
                is = url.openStream();
            } catch(IOException e) {
//                errorMessageList.add("ERROR: " + e.getMessage());
            }
            ins = new InputSource(is);
        }

        // #############################
        // The text to parse is prepared
        // #############################
        if (fulltext != null && fulltext.length() > 0) {
            StringReader sr = new StringReader(fulltext);
            ins = new InputSource(sr);
        }

        // #############################
        // FM Template
        // #############################
        String s = null;
        if (UtilValidate.isNotEmpty(fmfilename) && ins != null) {
            FileReader templateReader = null;
            try {
                templateReader = new FileReader(fmfilename);
            } catch(FileNotFoundException e) {
//                errorMessageList.add("ERROR: " + e.getMessage());
            }

            StringWriter outWriter = new StringWriter();

            Template template = null;
            try {
                Configuration conf = org.ofbiz.base.util.template.FreeMarkerWorker.makeDefaultOfbizConfig();
                template = new Template("FMImportFilter", templateReader, conf);
                Map fmcontext = new HashMap();

                NodeModel nodeModel = NodeModel.parse(ins);
                fmcontext.put("doc", nodeModel);
                BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
                TemplateHashModel staticModels = wrapper.getStaticModels();
                fmcontext.put("Static", staticModels);

                template.process(fmcontext, outWriter);
                s = outWriter.toString();
            } catch(Exception ex) {
//                errorMessageList.add("ERROR: " + ex.getMessage());
            }
        }

        // #############################
        // The parsing takes place
        // #############################
        if (s != null || fulltext != null || url != null) {
            try{
                Map inputMap = UtilMisc.toMap("mostlyInserts", mostlyInserts, 
                                              "createDummyFks", createDummyFks,
                                              "maintainTimeStamps", maintainTimeStamps,
                                              "txTimeout", txTimeout,
                                              "userLogin", userLogin);
                if (s != null) {
                    inputMap.put("xmltext", s);
                } else {
                    if (fulltext != null) {
                        inputMap.put("xmltext", fulltext);
                    } else {
                        inputMap.put("url", url);
                    }
                }
                Map outputMap = dispatcher.runSync("parseEntityXmlFile", inputMap);
                Long numberRead = (Long)outputMap.get("rowProcessed");

                messages.add("Got " + numberRead.longValue() + " entities to write to the datasource.");
            } catch (Exception ex){
//                errorMessageList.add("ERROR: " + exc.getMessage());
            }
        } else {
            messages.add("No filename/URL or complete XML document specified, doing nothing.");
        }

        // send the notification
        Map resp = UtilMisc.toMap("messages", messages);
        return resp;
    }

    public static Map entityImportDir(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        List messages = new ArrayList();

        String path = (String)context.get("path");
        String mostlyInserts = (String)context.get("mostlyInserts");
        String maintainTimeStamps = (String)context.get("maintainTimeStamps");
        String createDummyFks = (String)context.get("createDummyFks");
        boolean deleteFiles = (String)context.get("deleteFiles") != null;

        Integer txTimeout = (Integer)context.get("txTimeout");
        Long filePause = (Long)context.get("filePause");

        if (txTimeout == null) {
            txTimeout = new Integer(7200);
        }
        if (filePause == null) {
            filePause = new Long(0);
        }

        if (path != null && path.length() > 0) {
            long pauseLong = filePause != null ? filePause.longValue() : 0;
            File baseDir = new File(path);

            if (baseDir.isDirectory() && baseDir.canRead()) {
                File[] fileArray = baseDir.listFiles();
                ArrayList files = new ArrayList(fileArray.length);
                for (int a=0; a<fileArray.length; a++){
                    if (fileArray[a].getName().toUpperCase().endsWith("XML")) {
                        files.add(fileArray[a]);
                    }
                }
                boolean importedOne = false;
                int fileListMarkedSize = files.size();
                int passes = 0;
                for (int a=0; a<files.size(); a++){
                    // Infinite loop defense
                    if (a == fileListMarkedSize) {
                        passes++;
                        fileListMarkedSize = files.size();
                        messages.add("Pass " + passes + " complete");
                        // This means we've done a pass
                        if ( false == importedOne ) {
                            // We've failed to make any imports
                            messages.add("Dropping out as we failed to make any imports on the last pass");
                            a = files.size();
                            continue;
                        }
                        importedOne = false;
                    }
                    File curFile = (File)files.get(a);
                    try{
                        URL url = curFile.toURL();
                        Map inputMap = UtilMisc.toMap("url", url,
                                                      "mostlyInserts", mostlyInserts, 
                                                      "createDummyFks", createDummyFks,
                                                      "maintainTimeStamps", maintainTimeStamps,
                                                      "txTimeout", txTimeout,
                                                      "userLogin", userLogin);
                        Map outputMap = dispatcher.runSync("parseEntityXmlFile", inputMap);
                        Long numberRead = (Long)outputMap.get("rowProcessed");

                        messages.add("Got " + numberRead.longValue() + " entities from " + curFile);

                        importedOne = true;
                        if (deleteFiles) {
                            curFile.delete();
                        }
                    } catch (Exception ex){
                        messages.add("Error trying to read from " + curFile + ": " + ex);
                        if (ex.toString().indexOf("referential integrity violation") > -1 ||
                                ex.toString().indexOf("Integrity constraint violation") > -1){
                            //It didn't work because object it depends on are still
                            //missing from the DB. Retry later.
                            //
                            //FIXME: Of course this is a potential infinite loop.
                            messages.add("Looks like referential integrity violation, will retry");
                            files.add(curFile);
                        }
                    }
                    // pause in between files
                    if (pauseLong > 0) {
                        Debug.log("Pausing for [" + pauseLong + "] seconds - " + UtilDateTime.nowTimestamp());
                        try {
                            Thread.sleep((pauseLong * 1000));
                        } catch(InterruptedException ie) {
                            Debug.log("Pause finished - " + UtilDateTime.nowTimestamp());
                        }
                    }
                }
            } else {
                messages.add("path not found or can't be read");
            }
        } else {
            messages.add("No path specified, doing nothing.");
        }
        // send the notification
        Map resp = UtilMisc.toMap("messages", messages);
        return resp;
    }

    public static Map parseEntityXmlFile(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        URL url = (URL)context.get("url");
        String xmltext = (String)context.get("xmltext");

        if (url == null && xmltext == null) {
            return ServiceUtil.returnError("No entity xml file or text specified");
        }
        boolean mostlyInserts = (String)context.get("mostlyInserts") != null;
        boolean maintainTimeStamps = (String)context.get("maintainTimeStamps") != null;
        boolean createDummyFks = (String)context.get("createDummyFks") != null;
        Integer txTimeout = (Integer)context.get("txTimeout");

        if (txTimeout == null) {
            txTimeout = new Integer(7200);
        }

        Long rowProcessed = new Long(0);
        try {
            EntitySaxReader reader = new EntitySaxReader(delegator);
            reader.setUseTryInsertMethod(mostlyInserts);
            reader.setMaintainTxStamps(maintainTimeStamps);
            reader.setTransactionTimeout(txTimeout.intValue());
            reader.setCreateDummyFks(createDummyFks);

            long numberRead = (url != null? reader.parse(url): reader.parse(xmltext));
            rowProcessed = new Long(numberRead);
        } catch (Exception ex){
            return ServiceUtil.returnError("Error parsing entity xml file: " + ex.getMessage());
        }
        // send the notification
        Map resp = UtilMisc.toMap("rowProcessed", rowProcessed);
        return resp;
    }

}
