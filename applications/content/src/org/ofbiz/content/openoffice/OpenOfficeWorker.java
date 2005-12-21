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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.lang.Runtime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.ByteWrapper;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.container.XNameAccess;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;


/**
 * OpenOfficeWorker Class
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Rev: 5462 $
 * @since 3.2
 * 
 *  
 */
public class OpenOfficeWorker{

    public static final String module = OpenOfficeWorker.class.getName();

    /**
     * Use OpenOffice to convert documents between types
     */
    public static XMultiComponentFactory getRemoteServer(String host, String port) throws IOException, Exception {
    	
    	XMultiComponentFactory xmulticomponentfactory = null;
    	XComponentContext xcomponentcontext = null;
    	Object objectUrlResolver = null;
    	XUnoUrlResolver xurlresolver = null;
    	Object objectInitial = null;
        // Converting the document to the favoured type
        try {
          
          /* Bootstraps a component context with the jurt base components
             registered. Component context to be granted to a component for running.
             Arbitrary values can be retrieved from the context. */
          xcomponentcontext = com.sun.star.comp.helper.Bootstrap.createInitialComponentContext( null );
          
          /* Gets the service manager instance to be used (or null). This method has
             been added for convenience, because the service manager is a often used
             object. */
          xmulticomponentfactory = xcomponentcontext.getServiceManager();
          
          /* Creates an instance of the component UnoUrlResolver which
             supports the services specified by the factory. */
          objectUrlResolver = xmulticomponentfactory.createInstanceWithContext("com.sun.star.bridge.UnoUrlResolver", xcomponentcontext );
          
          // Create a new url resolver
          xurlresolver = ( XUnoUrlResolver ) UnoRuntime.queryInterface( XUnoUrlResolver.class, objectUrlResolver );
          
          // Resolves an object that is specified as follow:
          // uno:<connection description>;<protocol description>;<initial object name>
          objectInitial = xurlresolver.resolve( "uno:socket,host=" + host + ",port=" + port + ";urp;StarOffice.ServiceManager" );
          
          // Create a service manager from the initial object
          xmulticomponentfactory = ( XMultiComponentFactory ) UnoRuntime.queryInterface( XMultiComponentFactory.class, objectInitial );
          
        } catch( Exception exception ) {
            // TODO: None of this works. Need a programmable start solution.
           String ooxvfb = UtilProperties.getPropertyValue("openoffice", "oo.start.xvfb");
           String ooexport = UtilProperties.getPropertyValue("openoffice", "oo.start.export");
           String oosoffice = UtilProperties.getPropertyValue("openoffice", "oo.start.soffice");
        	   //Process procXvfb = Runtime.getRuntime().exec(ooxvfb);
        	   //Process procExport = Runtime.getRuntime().exec(ooexport);
        	   Process procSoffice = Runtime.getRuntime().exec(oosoffice);
        	   Thread.sleep(3000);
               objectInitial = xurlresolver.resolve( "uno:socket,host=" + host + ",port=" + port + ";urp;StarOffice.ServiceManager" );
               xmulticomponentfactory = ( XMultiComponentFactory ) UnoRuntime.queryInterface( XMultiComponentFactory.class, objectInitial );
       	   Debug.logInfo("soffice started. " + procSoffice, module);
        }
        
        return xmulticomponentfactory;

    }
 
    public static String listFilterNamesEvent(HttpServletRequest request, HttpServletResponse response) {
    	XMultiComponentFactory factory = null;
    	
    	try {
    		factory = getRemoteServer("localhost", "8100");
        	List filterList = getFilterNames(factory);
        	request.setAttribute("filterList", filterList);
    	} catch(IOException e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
    		return "error";
    	} catch(Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
    	}
    	return "success";
    }    
    
    public static List getFilterNames(XMultiComponentFactory xmulticomponentfactory ) throws Exception {
    	XPropertySet xPropertySet = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, xmulticomponentfactory);        
    	Object oDefaultContext = xPropertySet.getPropertyValue("DefaultContext");
    	XComponentContext xComponentContext = (XComponentContext) UnoRuntime.queryInterface(XComponentContext.class, oDefaultContext);


    	Object filterFactory = xmulticomponentfactory.createInstanceWithContext("com.sun.star.document.FilterFactory", xComponentContext);
        XNameAccess xNameAccess = (XNameAccess)UnoRuntime.queryInterface(XNameAccess.class, filterFactory);
        String [] filterNames = xNameAccess.getElementNames();
        
        //String [] serviceNames = filterFactory.getAvailableServiceNames();
        for (int i=0; i < filterNames.length; i++) {
      	  String s = filterNames[i];
  		  Debug.logInfo(s, module);
      	  /*
      	  if (s.toLowerCase().indexOf("filter") >= 0) {
      		  Debug.logInfo("FILTER: " + s, module);
      	  }
      	  if (s.toLowerCase().indexOf("desktop") >= 0) {
      		  Debug.logInfo("DESKTOP: " + s, module);
      	  }
      	  */
        }

    	List filterNameList = UtilMisc.toListArray(filterNames);
    	return filterNameList;
    }
    
    public static void convertOODocToFile(XMultiComponentFactory xmulticomponentfactory, 
    										String stringUrl, String stringConvertedFile, String convertFilterName ) 
                                         throws Exception {
        // Converting the document to the favoured type
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
          //XDesktop desktop = ( XDesktop ) UnoRuntime.queryInterface( XDesktop.class, desktopObj);
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
          
          // Preparing properties for converting the document
          propertyvalue = new PropertyValue[ 1 ];
          // Setting the flag for overwriting
          propertyvalue[ 0 ] = new PropertyValue();
          propertyvalue[ 0 ].Name = "Overwrite";
          propertyvalue[ 0 ].Value = new Boolean(true);
          // Setting the filter name
          //propertyvalue[ 1 ] = new PropertyValue();
          //propertyvalue[ 1 ].Name = "FilterName";
          //propertyvalue[ 1 ].Value = convertFilterName;         
          Debug.logInfo("stringConvertedFile: "+stringConvertedFile, module);
          // Storing and converting the document
          xstorable.storeToURL( stringConvertedFile, propertyvalue );
          
          // Getting the method dispose() for closing the document
          XComponent xcomponent = ( XComponent ) UnoRuntime.queryInterface( XComponent.class, xstorable );
          
          // Closing the converted document
          xcomponent.dispose();
          return;
    }
    
    public static OpenOfficeByteArrayOutputStream convertOODocByteStreamToByteStream(XMultiComponentFactory xmulticomponentfactory, 
    		                                OpenOfficeByteArrayInputStream is, String inputMimeType, String outputMimeType) 
    										throws Exception {
    	
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
          //XDesktop desktop = ( XDesktop ) UnoRuntime.queryInterface( XDesktop.class, desktopObj);
          XComponentLoader xcomponentloader = ( XComponentLoader ) UnoRuntime.queryInterface( XComponentLoader.class, desktopObj);
          
          // Preparing properties for loading the document
          PropertyValue propertyvalue[] = new PropertyValue[ 2 ];
          // Setting the flag for hidding the open document
          propertyvalue[ 0 ] = new PropertyValue();
          propertyvalue[ 0 ].Name = "Hidden";
          propertyvalue[ 0 ].Value = new Boolean(true);
          //
          propertyvalue[ 1 ] = new PropertyValue();
          propertyvalue[ 1 ].Name = "InputStream";
          propertyvalue[ 1 ].Value = is;
          
          // Loading the wanted document
          Object objectDocumentToStore = xcomponentloader.loadComponentFromURL( "private:stream", "_blank", 0, propertyvalue );
          
          // Getting an object that will offer a simple way to store a document to a URL.
          XStorable xstorable = ( XStorable ) UnoRuntime.queryInterface( XStorable.class, objectDocumentToStore );
          
          // Preparing properties for converting the document
          String filterName = "";
          if (UtilValidate.isEmpty(outputMimeType)) {
        	  filterName = "HTML";
          } else if (outputMimeType.equalsIgnoreCase("application/pdf")) {
        	  filterName = "writer_pdf_Export";
          } else if (outputMimeType.equalsIgnoreCase("application/msword")) {
        	  filterName = "MS Word 97";
          } else if (outputMimeType.equalsIgnoreCase("text/html")) {
        	  filterName = "HTML (StarWriter)";
          } else {
        	  filterName = "HTML";
          }
          propertyvalue = new PropertyValue[ 4 ];
          
          // Setting the flag for overwriting
          propertyvalue[ 3 ] = new PropertyValue();
          propertyvalue[ 3 ].Name = "Overwrite";
          propertyvalue[ 3 ].Value = new Boolean(true);
          // Setting the filter name
          propertyvalue[ 1 ] = new PropertyValue();
          propertyvalue[ 1 ].Name = "FilterName";
          propertyvalue[ 1 ].Value = filterName;
          // For PDFs
          propertyvalue[ 2 ] = new PropertyValue();
          propertyvalue[ 2 ].Name = "CompressionMode";
          propertyvalue[ 2 ].Value = "1";
          
          propertyvalue[ 0 ] = new PropertyValue();
          propertyvalue[ 0 ].Name = "OutputStream";
          OpenOfficeByteArrayOutputStream os = new OpenOfficeByteArrayOutputStream();
          propertyvalue[ 0 ].Value = os;
          
          xstorable.storeToURL( "private:stream", propertyvalue );
          //xstorable.storeToURL( "file:///home/byersa/testdoc1_file.pdf", propertyvalue );
          
          // Getting the method dispose() for closing the document
          XComponent xcomponent = ( XComponent ) UnoRuntime.queryInterface( XComponent.class,
          xstorable );
          
          // Closing the converted document
          xcomponent.dispose();
    	
          return os;
    }
}
