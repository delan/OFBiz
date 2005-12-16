package org.ofbiz.content;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
    
    public List getFilterNames(XMultiComponentFactory xmulticomponentfactory ) throws Exception {
    	XPropertySet xPropertySet = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, xmulticomponentfactory);        
    	Object oDefaultContext = xPropertySet.getPropertyValue("DefaultContext");
    	XComponentContext xComponentContext = (XComponentContext) UnoRuntime.queryInterface(XComponentContext.class, oDefaultContext);


    	Object filterFactory = xmulticomponentfactory.createInstanceWithContext("com.sun.star.document.FilterFactory", xComponentContext);
        XNameAccess xNameAccess = (XNameAccess)UnoRuntime.queryInterface(XNameAccess.class, filterFactory);
        String [] filterNames = xNameAccess.getElementNames();
        
        /*
        String [] serviceNames = filterFactory.getAvailableServiceNames();
        for (int i=0; i < serviceNames.length; i++) {
      	  String s = serviceNames[i];
      	  if (s.toLowerCase().indexOf("filter") >= 0) {
      		  Debug.logInfo("FILTER: " + s, module);
      	  }
      	  if (s.toLowerCase().indexOf("desktop") >= 0) {
      		  Debug.logInfo("DESKTOP: " + s, module);
      	  }
        }
        */
    	List filterNameList = UtilMisc.toListArray(filterNames);
    	return filterNameList;
    }
}
