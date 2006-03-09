/*
 * $Id: PdfSurveyServices.java 5462 2005-08-05 18:35:48Z byersa $
 *
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.content.openoffice;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.sql.Timestamp;
import javax.servlet.ServletContext;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.data.DataResourceWorker;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.ByteWrapper;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.view.ViewHandlerException;

import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XDispatchHelper;
import com.sun.star.frame.XDispatchProvider;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.container.XNameAccess;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PRAcroForm;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfLister;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.PageSize;

/**
 * OpenOfficeServices Class
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Rev: 5462 $
 * @since 3.2
 * 
 *  
 */

public class OpenOfficeServices {

    public static final String module = OpenOfficeServices.class.getName();

    /**
     * Use OpenOffice to convert documents between types
     */
    public static Map convertDocument(DispatchContext dctx, Map context) {
    	
    	XMultiComponentFactory xmulticomponentfactory = null;
    	
        String stringUrl = "file:///" + context.get("filenameFrom");
        String stringConvertedFile = "file:///" + context.get("filenameTo");
        String filterName = "file:///" + context.get("filterName");
        String inputMimeType = (String)context.get("inputMimeType");
        String outputMimeType = (String)context.get("outputMimeType");
        String oooHost = (String)context.get("oooHost");
        if (UtilValidate.isEmpty(oooHost)) oooHost = "localhost";
        String oooPort = (String)context.get("oooPort");
        if (UtilValidate.isEmpty(oooPort)) oooPort = "8100";
        
	    try {	
	    	xmulticomponentfactory = OpenOfficeWorker.getRemoteServer(oooHost, oooPort);
	    	OpenOfficeWorker.convertOODocToFile(xmulticomponentfactory, stringUrl, stringConvertedFile, filterName);
	    } catch (IOException ioe) {
	 	   ServiceUtil.returnError(ioe.getMessage());
	    } catch( Exception e2 ) {
	 	   ServiceUtil.returnError(e2.getMessage());
	    }
    	System.out.println("xmulticomponentfactory: " + xmulticomponentfactory);
       
    	
        Map results = ServiceUtil.returnSuccess();
        return results;

    }

    /**
     * Use OpenOffice to convert documents between types
     */
    public static Map convertDocumentFileToFile(DispatchContext dctx, Map context) {
    	
    	XMultiComponentFactory xmulticomponentfactory = null;
    	
        String stringUrl = (String)context.get("filenameFrom");
        String stringConvertedFile = (String)context.get("filenameTo");
        String inputMimeType = (String)context.get("inputMimeType");
        String outputMimeType = (String)context.get("outputMimeType");
        String oooHost = (String)context.get("oooHost");
        if (UtilValidate.isEmpty(oooHost)) oooHost = "localhost";
        String oooPort = (String)context.get("oooPort");
        if (UtilValidate.isEmpty(oooPort)) oooPort = "8100";
        
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
	    } catch (IOException ioe) {
	    	ioe.printStackTrace();
	 	   ServiceUtil.returnError(ioe.getMessage());
	    } catch( Exception e2 ) {
	    	e2.printStackTrace();
	 	   ServiceUtil.returnError(e2.getMessage());
	    }
    	System.out.println("xmulticomponentfactory: " + xmulticomponentfactory);
    	
        Map results = ServiceUtil.returnSuccess();
        return results;

    }


    /**
     * Use OpenOffice to convert documents between types
     */
    public static Map convertDocumentStreamToStream(DispatchContext dctx, Map context) {
    	
    	XMultiComponentFactory xmulticomponentfactory = null;
    	
        String stringUrl = "file:///" + context.get("filenameFrom");
        String stringConvertedFile = "file:///" + context.get("filenameTo");
        String filterName = "file:///" + context.get("filterName");
        String inputMimeType = (String)context.get("inputMimeType");
        String outputMimeType = (String)context.get("outputMimeType");
        String oooHost = (String)context.get("oooHost");
        if (UtilValidate.isEmpty(oooHost)) oooHost = "localhost";
        String oooPort = (String)context.get("oooPort");
        if (UtilValidate.isEmpty(oooPort)) oooPort = "8100";
        
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
	    } catch (IOException ioe) {
	    	ioe.printStackTrace();
	 	   ServiceUtil.returnError(ioe.getMessage());
	    } catch( Exception e2 ) {
	    	e2.printStackTrace();
	    	ServiceUtil.returnError(e2.getMessage());
	    }
    	System.out.println("xmulticomponentfactory: " + xmulticomponentfactory);
       
    	
        Map results = ServiceUtil.returnSuccess();
        return results;

    }

    /**
     * Use OpenOffice to compare documents
     */
    public static Map compareDocuments(DispatchContext dctx, Map context) {
    	XMultiComponentFactory xmulticomponentfactory = null;
    	
        String stringUrl = "file:///" + context.get("filenameFrom");
        String stringOriginalFile = "file:///" + context.get("filenameOriginal");
        String stringOutFile = "file:///" + context.get("filenameOut");
        String oooHost = (String)context.get("oooHost");
        if (UtilValidate.isEmpty(oooHost)) oooHost = "localHost";
        String oooPort = (String)context.get("oooPort");
        if (UtilValidate.isEmpty(oooPort)) oooPort = "8100";
        
	    try {	
	    	xmulticomponentfactory = OpenOfficeWorker.getRemoteServer(oooHost, oooPort);
	    } catch (IOException ioe) {
	 	   ServiceUtil.returnError(ioe.getMessage());
	    } catch( Exception e2 ) {
	 	   ServiceUtil.returnError(e2.getMessage());
	    }
    	System.out.println("xmulticomponentfactory: " + xmulticomponentfactory);
       
        // Converting the document to the favoured type
        try {
          // Composing the URL
          
          
          // Query for the XPropertySet interface.
          XPropertySet xpropertysetMultiComponentFactory = ( XPropertySet ) UnoRuntime.queryInterface( XPropertySet.class,
        		                                             xmulticomponentfactory );
          
          // Get the default context from the office server.
          Object objectDefaultContext = xpropertysetMultiComponentFactory.getPropertyValue( "DefaultContext" );
          
          // Query for the interface XComponentContext.
          XComponentContext xcomponentcontext = ( XComponentContext ) UnoRuntime.queryInterface(XComponentContext.class, 
        		                                    objectDefaultContext );
          
          /* A desktop environment contains tasks with one or more
             frames in which components can be loaded. Desktop is the
             environment for components which can instanciate within
             frames. */
          
          Object desktopObj = xmulticomponentfactory.createInstanceWithContext("com.sun.star.frame.Desktop", xcomponentcontext );
          XDesktop desktop = ( XDesktop ) UnoRuntime.queryInterface( XDesktop.class, desktopObj);
          XComponentLoader xcomponentloader = ( XComponentLoader ) UnoRuntime.queryInterface( XComponentLoader.class, desktopObj);
         
          
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
          Object objectDocumentToStore = xcomponentloader.loadComponentFromURL( stringUrl, "_blank", 0, propertyvalue );
          
          // Getting an object that will offer a simple way to store a document to a URL.
          XStorable xstorable = ( XStorable ) UnoRuntime.queryInterface( XStorable.class, objectDocumentToStore );
          
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
          //XFrame frame = (XFrame) UnoRuntime.queryInterface( XFrame.class, desktop);
          Object dispatchHelperObj = xmulticomponentfactory.createInstanceWithContext("com.sun.star.frame.DispatchHelper", xcomponentcontext );
          XDispatchHelper dispatchHelper = ( XDispatchHelper ) UnoRuntime.queryInterface( XDispatchHelper.class, dispatchHelperObj);
          XDispatchProvider dispatchProvider = (XDispatchProvider) UnoRuntime.queryInterface( XDispatchProvider.class, frame);
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
          xstorable.storeToURL( stringOutFile, propertyvalue );
          
          // Getting the method dispose() for closing the document
          XComponent xcomponent = ( XComponent ) UnoRuntime.queryInterface( XComponent.class,
          xstorable );
          
          // Closing the converted document
          xcomponent.dispose();
        }
        catch( Exception exception ) {
          exception.printStackTrace();
          return ServiceUtil.returnError("Error converting document: " + exception.toString());
        }
        
        Map results = ServiceUtil.returnSuccess();
        return results;

    }
    
    /**
     * Use OpenOffice to convert documents between types
     */
    public static Map renderCompDocPdf(DispatchContext dctx, Map context) {
        
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map results = ServiceUtil.returnSuccess();
        ByteWrapper byteWrapper = null;
        
        Locale locale = (Locale)context.get("locale");
        String rootDir = (String)context.get("rootDir");
        String webSiteId = (String)context.get("webSiteId");
        String https = (String)context.get("https");
        
        XMultiComponentFactory xmulticomponentfactory = null;
        GenericDelegator delegator = dctx.getDelegator();
        
        String contentId = (String)context.get("contentId");
        String contentRevisionSeqId = (String)context.get("contentRevisionSeqId");
        String oooHost = (String)context.get("oooHost");
        if (UtilValidate.isEmpty(oooHost)) oooHost = "localhost";
        String oooPort = (String)context.get("oooPort");
        if (UtilValidate.isEmpty(oooPort)) oooPort = "8100";
        
        try {
                xmulticomponentfactory = OpenOfficeWorker.getRemoteServer(oooHost, oooPort);
            } catch (IOException ioe) {
                // just leave it null for cases where OO cannot be reached
            } catch( Exception e2 ) {
                // just leave it null for cases where OO cannot be reached
            }
        try {   
            Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
            List exprList = new ArrayList();
            EntityExpr entityExpr = null;
            entityExpr = new EntityExpr("contentIdTo", EntityOperator.EQUALS, contentId);
            exprList.add(entityExpr);
            entityExpr = new EntityExpr("rootRevisionContentId", EntityOperator.EQUALS, contentId);
            exprList.add(entityExpr);
            if (UtilValidate.isNotEmpty(contentRevisionSeqId)) {
                entityExpr = new EntityExpr("contentRevisionSeqId", EntityOperator.LESS_THAN_EQUAL_TO, contentRevisionSeqId);
                exprList.add(entityExpr);
            }
            entityExpr = new EntityExpr("contentAssocTypeId", EntityOperator.EQUALS, "COMPDOC_PART");
            exprList.add(entityExpr);
            entityExpr = new EntityExpr("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp);
            exprList.add(entityExpr);
            List thruList = new ArrayList();
            entityExpr = new EntityExpr("thruDate", EntityOperator.EQUALS, null);
            thruList.add(entityExpr);
            entityExpr = new EntityExpr("thruDate", EntityOperator.GREATER_THAN, nowTimestamp);
            thruList.add(entityExpr);
            EntityConditionList conditionThruList = new EntityConditionList(thruList, EntityOperator.OR);
            exprList.add(conditionThruList);
            EntityConditionList conditionList = new EntityConditionList(exprList, EntityOperator.AND);
            String [] fields = {"rootRevisionContentId", "itemContentId", "maxRevisionSeqId", "contentId", "dataResourceId", "contentIdTo", "contentAssocTypeId", "fromDate", "sequenceNum"};
            List selectFields = UtilMisc.toListArray(fields);
            List orderByFields = UtilMisc.toList("sequenceNum");
            List compDocParts = delegator.findByCondition("ContentAssocRevisionItemView", conditionList, selectFields, orderByFields);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            document.setPageSize(PageSize.LETTER);    
            Rectangle rect = document.getPageSize();
            //PdfWriter writer = PdfWriter.getInstance(document, baos);
            PdfCopy writer = new PdfCopy(document, baos);
            document.open();
           Iterator iter = compDocParts.iterator();
            int pgCnt =0;
            while (iter.hasNext()) {
                GenericValue contentAssocRevisionItemView = (GenericValue)iter.next();
                String thisContentId = contentAssocRevisionItemView.getString("contentId");
                String thisContentRevisionSeqId = contentAssocRevisionItemView.getString("maxRevisionSeqId");
                String thisDataResourceId = contentAssocRevisionItemView.getString("dataResourceId");
                GenericValue dataResource = delegator.findByPrimaryKey("DataResource", UtilMisc.toMap("dataResourceId", thisDataResourceId));
                String inputMimeType = null;
                if(dataResource != null) {
                    inputMimeType = (String)dataResource.getString("mimeTypeId");
                }
                byte [] inputByteArray = null;
                PdfReader reader = null;
                if (inputMimeType != null && inputMimeType.equals("application/pdf")) {
                    byteWrapper = DataResourceWorker.getContentAsByteWrapper(delegator, thisDataResourceId, https, webSiteId, locale, rootDir);
                    inputByteArray = byteWrapper.getBytes();
                    reader = new PdfReader(inputByteArray);
                } else if (inputMimeType != null && inputMimeType.equals("text/html")) {
                    byteWrapper = DataResourceWorker.getContentAsByteWrapper(delegator, thisDataResourceId, https, webSiteId, locale, rootDir);
                    inputByteArray = byteWrapper.getBytes();
                    String s = new String(inputByteArray);
                    Debug.logInfo("text/html string:" + s, module);
                    continue;
                } else if (inputMimeType != null && inputMimeType.equals("application/vnd.ofbiz.survey.response")) {
                    String surveyResponseId = dataResource.getString("relatedDetailId");
                    String surveyId = null;
                    String acroFormContentId = null;
                    GenericValue surveyResponse = null;
                    if (UtilValidate.isNotEmpty(surveyResponseId)) {
                        surveyResponse = delegator.findByPrimaryKey("SurveyResponse", UtilMisc.toMap("surveyResponseId", surveyResponseId));
                        if (surveyResponse != null) {
                            surveyId = surveyResponse.getString("surveyId");
                        }
                    }
                    if (UtilValidate.isNotEmpty(surveyId)) {
                        GenericValue survey = delegator.findByPrimaryKey("Survey", UtilMisc.toMap("surveyId", surveyId));
                        if (survey != null) {
                            acroFormContentId = survey.getString("acroFormContentId");
                            if (UtilValidate.isNotEmpty(acroFormContentId)) {
                            }
                        }
                    }
                    if (surveyResponse != null) {
                        if (UtilValidate.isEmpty(acroFormContentId)) {
                            // Create AcroForm PDF
                            Map survey2PdfResults = dispatcher.runSync("buildPdfFromSurveyResponse", UtilMisc.toMap("surveyResponseId", surveyId));
                            ByteWrapper outByteWrapper = (ByteWrapper)survey2PdfResults.get("outByteWrapper");
                            inputByteArray = outByteWrapper.getBytes();
                            reader = new PdfReader(inputByteArray);
                        } else {
                            // Fill in acroForm
                            Map survey2PdfResults = dispatcher.runSync("setAcroFieldsFromSurveyResponse", UtilMisc.toMap("surveyResponseId", surveyResponseId));
                            ByteWrapper outByteWrapper = (ByteWrapper)survey2PdfResults.get("outByteWrapper");
                            inputByteArray = outByteWrapper.getBytes();
                            reader = new PdfReader(inputByteArray);
                        }
                    }
                } else {
                    if (xmulticomponentfactory != null) {
                        byteWrapper = DataResourceWorker.getContentAsByteWrapper(delegator, thisDataResourceId, https, webSiteId, locale, rootDir);
                        OpenOfficeByteArrayInputStream oobais = new OpenOfficeByteArrayInputStream(byteWrapper.getBytes());
                        OpenOfficeByteArrayOutputStream oobaos = OpenOfficeWorker.convertOODocByteStreamToByteStream(xmulticomponentfactory, oobais, inputMimeType, "application/pdf");
                        inputByteArray = oobaos.toByteArray();
                        oobais.close();
                        oobaos.close();
                        reader = new PdfReader(inputByteArray);
                    } else {
                        //TODO: Write a PDF that says that OO is not available?
                    }
                }
                int n = reader.getNumberOfPages();
                for (int i=0; i < n; i++) {
                    PdfImportedPage pg = writer.getImportedPage(reader, i + 1);
                    //cb.addTemplate(pg, left, height * pgCnt);
                    writer.addPage(pg);
                    pgCnt++;
                }
            }
            document.close();
            ByteWrapper outByteWrapper = new ByteWrapper(baos.toByteArray());
            results.put("outByteWrapper", outByteWrapper);
        } catch (GenericEntityException e) {
           return ServiceUtil.returnError(e.getMessage());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return ServiceUtil.returnError(ioe.getMessage());
        } catch( Exception e2 ) {
            e2.printStackTrace();
            return ServiceUtil.returnError(e2.getMessage());
        }
        return results;

    }

    
    /**
     * Use OpenOffice to convert documents between types
     */
    public static Map renderContentPdf(DispatchContext dctx, Map context) {
        
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map results = ServiceUtil.returnSuccess();
        String dataResourceId = null;
        ByteWrapper byteWrapper = null;
        
        Locale locale = (Locale)context.get("locale");
        String rootDir = (String)context.get("rootDir");
        String webSiteId = (String)context.get("webSiteId");
        String https = (String)context.get("https");
        
        XMultiComponentFactory xmulticomponentfactory = null;
        GenericDelegator delegator = dctx.getDelegator();
        
        String contentId = (String)context.get("contentId");
        String contentRevisionSeqId = (String)context.get("contentRevisionSeqId");
        String oooHost = (String)context.get("oooHost");
        if (UtilValidate.isEmpty(oooHost)) oooHost = "localhost";
        String oooPort = (String)context.get("oooPort");
        if (UtilValidate.isEmpty(oooPort)) oooPort = "8100";
        
        try {   
            Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            document.setPageSize(PageSize.LETTER);    
            Rectangle rect = document.getPageSize();
            //PdfWriter writer = PdfWriter.getInstance(document, baos);
            PdfCopy writer = new PdfCopy(document, baos);
            document.open();

            GenericValue dataResource = null;
            if (UtilValidate.isEmpty(contentRevisionSeqId)) {
                GenericValue content = delegator.findByPrimaryKeyCache("Content", UtilMisc.toMap("contentId", contentId));
                dataResourceId = content.getString("dataResourceId");
                Debug.logInfo("SCVH(0b)- dataResourceId:" + dataResourceId, module);
                dataResource = delegator.findByPrimaryKey("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
             } else {
                GenericValue contentRevisionItem = delegator.findByPrimaryKeyCache("ContentRevisionItem", UtilMisc.toMap("contentId", contentId, "itemContentId", contentId, "contentRevisionSeqId", contentRevisionSeqId));
                if (contentRevisionItem == null) {
                    throw new ViewHandlerException("ContentRevisionItem record not found for contentId=" + contentId
                                                   + ", contentRevisionSeqId=" + contentRevisionSeqId + ", itemContentId=" + contentId);
                }
                Debug.logInfo("SCVH(1)- contentRevisionItem:" + contentRevisionItem, module);
                Debug.logInfo("SCVH(2)-contentId=" + contentId
                        + ", contentRevisionSeqId=" + contentRevisionSeqId + ", itemContentId=" + contentId, module);
                dataResourceId = contentRevisionItem.getString("newDataResourceId");
                Debug.logInfo("SCVH(3)- dataResourceId:" + dataResourceId, module);
                dataResource = delegator.findByPrimaryKey("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
            }
            String inputMimeType = null;
            if(dataResource != null) {
                inputMimeType = (String)dataResource.getString("mimeTypeId");
            }
            byte [] inputByteArray = null;
            PdfReader reader = null;
            if (inputMimeType != null && inputMimeType.equals("application/pdf")) {
                byteWrapper = DataResourceWorker.getContentAsByteWrapper(delegator, dataResourceId, https, webSiteId, locale, rootDir);
                inputByteArray = byteWrapper.getBytes();
                reader = new PdfReader(inputByteArray);
            } else if (inputMimeType != null && inputMimeType.equals("text/html")) {
                byteWrapper = DataResourceWorker.getContentAsByteWrapper(delegator, dataResourceId, https, webSiteId, locale, rootDir);
                inputByteArray = byteWrapper.getBytes();
                String s = new String(inputByteArray);
                Debug.logInfo("text/html string:" + s, module);
            } else if (inputMimeType != null && inputMimeType.equals("application/vnd.ofbiz.survey.response")) {
                String surveyResponseId = dataResource.getString("relatedDetailId");
                String surveyId = null;
                String acroFormContentId = null;
                GenericValue surveyResponse = null;
                if (UtilValidate.isNotEmpty(surveyResponseId)) {
                    surveyResponse = delegator.findByPrimaryKey("SurveyResponse", UtilMisc.toMap("surveyResponseId", surveyResponseId));
                    if (surveyResponse != null) {
                        surveyId = surveyResponse.getString("surveyId");
                    }
                }
                if (UtilValidate.isNotEmpty(surveyId)) {
                    GenericValue survey = delegator.findByPrimaryKey("Survey", UtilMisc.toMap("surveyId", surveyId));
                    if (survey != null) {
                        acroFormContentId = survey.getString("acroFormContentId");
                        if (UtilValidate.isNotEmpty(acroFormContentId)) {
                        }
                    }
                }
            
                if (surveyResponse != null) {
                    if (UtilValidate.isEmpty(acroFormContentId)) {
                        // Create AcroForm PDF
                        Map survey2PdfResults = dispatcher.runSync("buildPdfFromSurveyResponse", UtilMisc.toMap("surveyResponseId", surveyResponseId));
                        ByteWrapper outByteWrapper = (ByteWrapper)survey2PdfResults.get("outByteWrapper");
                        inputByteArray = outByteWrapper.getBytes();
                        //reader = new PdfReader(inputByteArray);
                    } else {
                        // Fill in acroForm
                        Map survey2PdfResults = dispatcher.runSync("setAcroFieldsFromSurveyResponse", UtilMisc.toMap("surveyResponseId", surveyResponseId));
                        ByteWrapper outByteWrapper = (ByteWrapper)survey2PdfResults.get("outByteWrapper");
                        inputByteArray = outByteWrapper.getBytes();
                        //reader = new PdfReader(inputByteArray);
                    }
                }
            } else {
                byteWrapper = DataResourceWorker.getContentAsByteWrapper(delegator, dataResourceId, https, webSiteId, locale, rootDir);
                xmulticomponentfactory = OpenOfficeWorker.getRemoteServer(oooHost, oooPort);
                OpenOfficeByteArrayInputStream oobais = new OpenOfficeByteArrayInputStream(byteWrapper.getBytes());
                OpenOfficeByteArrayOutputStream oobaos = OpenOfficeWorker.convertOODocByteStreamToByteStream(xmulticomponentfactory, oobais, inputMimeType, "application/pdf");
                inputByteArray = oobaos.toByteArray();
                oobais.close();
                oobaos.close();
                //reader = new PdfReader(inputByteArray);
            }
            
            ByteWrapper outByteWrapper = new ByteWrapper(inputByteArray);
            results.put("outByteWrapper", outByteWrapper);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(e.getMessage());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return ServiceUtil.returnError(ioe.getMessage());
        } catch( Exception e2 ) {
            e2.printStackTrace();
            return ServiceUtil.returnError(e2.getMessage());
        }
        System.out.println("xmulticomponentfactory: " + xmulticomponentfactory);
        return results;

    }

}
