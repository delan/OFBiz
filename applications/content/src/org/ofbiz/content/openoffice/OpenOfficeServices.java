/*
 * $Id: $
 *
 * Copyright 2001-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.content.openoffice;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.ByteWrapper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XDispatchHelper;
import com.sun.star.frame.XDispatchProvider;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

/**
 * OpenOfficeServices Class
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 */
public class OpenOfficeServices {
    public static final String module = OpenOfficeServices.class.getName();

    /**
     * Use OpenOffice to convert documents between types
     */
    public static Map convertDocumentByteWrapper(DispatchContext dctx, Map context) {
        XMultiComponentFactory xmulticomponentfactory = null;
        
        ByteWrapper inByteWrapper = (ByteWrapper) context.get("inByteWrapper");
        String inputMimeType = (String) context.get("inputMimeType");
        String outputMimeType = (String) context.get("outputMimeType");

        // if these are empty don't worry, the OpenOfficeWorker down below will take care of it
        String oooHost = (String) context.get("oooHost");
        String oooPort = (String) context.get("oooPort");
        
        try {   
            xmulticomponentfactory = OpenOfficeWorker.getRemoteServer(oooHost, oooPort);
            byte[] inByteArray = inByteWrapper.getBytes();
            OpenOfficeByteArrayInputStream oobais = new OpenOfficeByteArrayInputStream(inByteArray);
            Debug.logInfo("Doing convertDocumentByteWrapper, inBytes size is [" + inByteArray.length + "]", module);
            OpenOfficeByteArrayOutputStream oobaos = OpenOfficeWorker.convertOODocByteStreamToByteStream(xmulticomponentfactory, oobais, inputMimeType, outputMimeType);
            
            Map results = ServiceUtil.returnSuccess();
            results.put("outByteWrapper", new ByteWrapper(oobaos.toByteArray()));
            oobais.close();
            oobaos.close();

            return results;
        } catch (IOException e) {
            Debug.logError(e, "Error in OpenOffice operation: ", module);
            return ServiceUtil.returnError(e.toString());
        } catch(Exception e) {
            Debug.logError(e, "Error in OpenOffice operation: ", module);
            return ServiceUtil.returnError(e.toString());
        }
    }

    /**
     * Use OpenOffice to convert documents between types
     */
    public static Map convertDocument(DispatchContext dctx, Map context) {
        XMultiComponentFactory xmulticomponentfactory = null;
        
        String stringUrl = "file:///" + context.get("filenameFrom");
        String stringConvertedFile = "file:///" + context.get("filenameTo");
        String filterName = "file:///" + context.get("filterName");

        // if these are empty don't worry, the OpenOfficeWorker down below will take care of it
        String oooHost = (String) context.get("oooHost");
        String oooPort = (String) context.get("oooPort");
        
        try {    
            xmulticomponentfactory = OpenOfficeWorker.getRemoteServer(oooHost, oooPort);
            OpenOfficeWorker.convertOODocToFile(xmulticomponentfactory, stringUrl, stringConvertedFile, filterName);
            
            Map results = ServiceUtil.returnSuccess();
            return results;
        } catch (IOException e) {
            Debug.logError(e, "Error in OpenOffice operation: ", module);
            return ServiceUtil.returnError(e.toString());
        } catch(Exception e) {
            Debug.logError(e, "Error in OpenOffice operation: ", module);
            return ServiceUtil.returnError(e.toString());
        }
    }

    /**
     * Use OpenOffice to convert documents between types
     */
    public static Map convertDocumentFileToFile(DispatchContext dctx, Map context) {
        XMultiComponentFactory xmulticomponentfactory = null;
        
        String stringUrl = (String) context.get("filenameFrom");
        String stringConvertedFile = (String) context.get("filenameTo");
        String inputMimeType = (String) context.get("inputMimeType");
        String outputMimeType = (String) context.get("outputMimeType");

        // if these are empty don't worry, the OpenOfficeWorker down below will take care of it
        String oooHost = (String) context.get("oooHost");
        String oooPort = (String) context.get("oooPort");
        
        try {    
            xmulticomponentfactory = OpenOfficeWorker.getRemoteServer(oooHost, oooPort);
            File inputFile = new File(stringUrl);
            long fileSize = inputFile.length();
            FileInputStream fis = new FileInputStream(inputFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream((int)fileSize);
            int c;
            while ((c = fis.read()) != -1) {
                baos.write(c);
            }
            OpenOfficeByteArrayInputStream oobais = new OpenOfficeByteArrayInputStream(baos.toByteArray());
            OpenOfficeByteArrayOutputStream oobaos = OpenOfficeWorker.convertOODocByteStreamToByteStream(xmulticomponentfactory, oobais, inputMimeType, outputMimeType);
            FileOutputStream fos = new FileOutputStream(stringConvertedFile);
            fos.write(oobaos.toByteArray());
            fos.close();
            fis.close();
            oobais.close();
            oobaos.close();
            
            Map results = ServiceUtil.returnSuccess();
            return results;
        } catch (IOException e) {
            Debug.logError(e, "Error in OpenOffice operation: ", module);
            return ServiceUtil.returnError(e.toString());
        } catch(Exception e) {
            Debug.logError(e, "Error in OpenOffice operation: ", module);
            return ServiceUtil.returnError(e.toString());
        }
    }

    /**
     * Use OpenOffice to convert documents between types
     */
    public static Map convertDocumentStreamToStream(DispatchContext dctx, Map context) {
        XMultiComponentFactory xmulticomponentfactory = null;
        
        String stringUrl = "file:///" + context.get("filenameFrom");
        String stringConvertedFile = "file:///" + context.get("filenameTo");
        String inputMimeType = (String) context.get("inputMimeType");
        String outputMimeType = (String) context.get("outputMimeType");

        // if these are empty don't worry, the OpenOfficeWorker down below will take care of it
        String oooHost = (String) context.get("oooHost");
        String oooPort = (String) context.get("oooPort");
        
        try {    
            xmulticomponentfactory = OpenOfficeWorker.getRemoteServer(oooHost, oooPort);
            File inputFile = new File(stringUrl);
            long fileSize = inputFile.length();
            FileInputStream fis = new FileInputStream(inputFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream((int)fileSize);
            int c;
            while ((c = fis.read()) != -1) {
                baos.write(c);
            }
            OpenOfficeByteArrayInputStream oobais = new OpenOfficeByteArrayInputStream(baos.toByteArray());
            OpenOfficeByteArrayOutputStream oobaos = OpenOfficeWorker.convertOODocByteStreamToByteStream(xmulticomponentfactory, oobais, inputMimeType, outputMimeType);
            FileOutputStream fos = new FileOutputStream(stringConvertedFile);
            fos.write(oobaos.toByteArray());
            fos.close();
            fis.close();
            oobais.close();
            oobaos.close();
            
            Map results = ServiceUtil.returnSuccess();
            return results;
        } catch (IOException e) {
            Debug.logError(e, "Error in OpenOffice operation: ", module);
            return ServiceUtil.returnError(e.toString());
        } catch(Exception e) {
            Debug.logError(e, "Error in OpenOffice operation: ", module);
            return ServiceUtil.returnError(e.toString());
        }
    }

    /**
     * Use OpenOffice to compare documents
     */
    public static Map compareDocuments(DispatchContext dctx, Map context) {
        XMultiComponentFactory xmulticomponentfactory = null;
        
        String stringUrl = "file:///" + context.get("filenameFrom");
        String stringOriginalFile = "file:///" + context.get("filenameOriginal");
        String stringOutFile = "file:///" + context.get("filenameOut");

        // if these are empty don't worry, the OpenOfficeWorker down below will take care of it
        String oooHost = (String)context.get("oooHost");
        String oooPort = (String)context.get("oooPort");
        
        try {    
            xmulticomponentfactory = OpenOfficeWorker.getRemoteServer(oooHost, oooPort);
        } catch (IOException e) {
            Debug.logError(e, "Error in OpenOffice operation: ", module);
            return ServiceUtil.returnError(e.toString());
        } catch(Exception e) {
            Debug.logError(e, "Error in OpenOffice operation: ", module);
            return ServiceUtil.returnError(e.toString());
        }
        //System.out.println("xmulticomponentfactory: " + xmulticomponentfactory);
       
        // Converting the document to the favoured type
        try {
            // Composing the URL
            
            
            // Query for the XPropertySet interface.
            XPropertySet xpropertysetMultiComponentFactory = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xmulticomponentfactory);
            
            // Get the default context from the office server.
            Object objectDefaultContext = xpropertysetMultiComponentFactory.getPropertyValue("DefaultContext");
            
            // Query for the interface XComponentContext.
            XComponentContext xcomponentcontext = (XComponentContext) UnoRuntime.queryInterface(XComponentContext.class, objectDefaultContext);
            
            /* A desktop environment contains tasks with one or more
               frames in which components can be loaded. Desktop is the
               environment for components which can instanciate within
               frames. */
            
            Object desktopObj = xmulticomponentfactory.createInstanceWithContext("com.sun.star.frame.Desktop", xcomponentcontext);
            XDesktop desktop = (XDesktop) UnoRuntime.queryInterface(XDesktop.class, desktopObj);
            XComponentLoader xcomponentloader = (XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class, desktopObj);
           
            
            // Preparing properties for loading the document
            PropertyValue propertyvalue[] = new PropertyValue[ 1 ];
            // Setting the flag for hidding the open document
            propertyvalue[ 0 ] = new PropertyValue();
            propertyvalue[ 0 ].Name = "Hidden";
            propertyvalue[ 0 ].Value = new Boolean(true);
            //TODO: Hardcoding opening word documents -- this will need to change.
            //propertyvalue[ 1 ] = new PropertyValue();
            //propertyvalue[ 1 ].Name = "FilterName";
            //propertyvalue[ 1 ].Value = "HTML (StarWriter)";
            
            // Loading the wanted document
            Object objectDocumentToStore = xcomponentloader.loadComponentFromURL(stringUrl, "_blank", 0, propertyvalue);
            
            // Getting an object that will offer a simple way to store a document to a URL.
            XStorable xstorable = (XStorable) UnoRuntime.queryInterface(XStorable.class, objectDocumentToStore);
            
            // Preparing properties for comparing the document
            propertyvalue = new PropertyValue[ 1 ];
            // Setting the flag for overwriting
            propertyvalue[ 0 ] = new PropertyValue();
            propertyvalue[ 0 ].Name = "URL";
            propertyvalue[ 0 ].Value = stringOriginalFile;
            // Setting the filter name
            //propertyvalue[ 1 ] = new PropertyValue();
            //propertyvalue[ 1 ].Name = "FilterName";
            //propertyvalue[ 1 ].Value = context.get("convertFilterName");
            XFrame frame = desktop.getCurrentFrame();
            //XFrame frame = (XFrame) UnoRuntime.queryInterface(XFrame.class, desktop);
            Object dispatchHelperObj = xmulticomponentfactory.createInstanceWithContext("com.sun.star.frame.DispatchHelper", xcomponentcontext);
            XDispatchHelper dispatchHelper = (XDispatchHelper) UnoRuntime.queryInterface(XDispatchHelper.class, dispatchHelperObj);
            XDispatchProvider dispatchProvider = (XDispatchProvider) UnoRuntime.queryInterface(XDispatchProvider.class, frame);
            dispatchHelper.executeDispatch(dispatchProvider, ".uno:CompareDocuments", "", 0, propertyvalue);       
            
            // Preparing properties for storing the document
            propertyvalue = new PropertyValue[ 1 ];
            // Setting the flag for overwriting
            propertyvalue[ 0 ] = new PropertyValue();
            propertyvalue[ 0 ].Name = "Overwrite";
            propertyvalue[ 0 ].Value = new Boolean(true);
            // Setting the filter name
            //propertyvalue[ 1 ] = new PropertyValue();
            //propertyvalue[ 1 ].Name = "FilterName";
            //propertyvalue[ 1 ].Value = context.get("convertFilterName");
            
            Debug.logInfo("stringOutFile: "+stringOutFile, module);
            // Storing and converting the document
            xstorable.storeToURL(stringOutFile, propertyvalue);
            
            // Getting the method dispose() for closing the document
            XComponent xcomponent = (XComponent) UnoRuntime.queryInterface(XComponent.class,
            xstorable);
            
            // Closing the converted document
            xcomponent.dispose();
            
            Map results = ServiceUtil.returnSuccess();
            return results;
        } catch (Exception e) {
            Debug.logError(e, "Error in OpenOffice operation: ", module);
            return ServiceUtil.returnError("Error converting document: " + e.toString());
        }
    }
}
