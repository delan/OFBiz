/*
 * $Id$
 * $Log$
 * Revision 1.1  2001/09/28 22:56:44  jonesde
 * Big update for fromDate PK use, organization stuff
 *
 * Revision 1.6  2001/08/25 17:29:11  azeneski
 * Started migrating Debug.log to Debug.logInfo and Debug.logError
 *
 * Revision 1.5  2001/07/19 14:15:58  azeneski
 * Moved org.ofbiz.core.control.RequestXMLReader to org.ofbiz.core.util.ConfigXMLReader
 * ConfigXMLReader is now used for all config files, not just the request mappings.
 * Updated RequestManager to use this new class.
 * Added getRequestManager() method to RequestHandler.
 *
 * Revision 1.4  2001/07/17 08:51:37  jonesde
 * Updated for auth implementation & small fixes.
 *
 * Revision 1.3  2001/07/17 03:45:09  azeneski
 * Changed request and view config to NOT use the leading '/'. All request and
 * view mappings should now leave be 'request' instead of '/request'.
 *
 * Revision 1.2  2001/07/16 22:31:06  azeneski
 * Moved multi-site support to be handled by the webapp.
 *
 * Revision 1.1  2001/07/16 14:45:48  azeneski
 * Added the missing 'core' directory into the module.
 *
 * Revision 1.1  2001/07/15 16:36:42  azeneski
 * Initial Import
 *
 */

package org.ofbiz.core.control;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import javax.servlet.ServletContext;

import org.ofbiz.core.util.ConfigXMLReader;
import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.Debug;

/**
 * <p><b>Title:</b> RequestManager.java
 * <p><b>Description:</b> Manages request, config and view mappings.
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on June 28, 2001, 10:12 PM
 */
public class RequestManager implements Serializable {
    
    HashMap configMap;
    HashMap requestMap;
    HashMap viewMap;
    
    public RequestManager(ServletContext context) {
        requestMap = new HashMap();
        configMap = new HashMap();
        viewMap = new HashMap();
        
        /** Loads the site configuration from servlet context parameter. */
        String configFileUrl = null;
        try {
            configFileUrl = context.getResource(context.getInitParameter(SiteDefs.SITE_CONFIG)).toString();
        }
        catch ( Exception e ) {
            Debug.logError(e,"Error Reading XML Config File: " + configFileUrl);
        }
        requestMap = ConfigXMLReader.getRequestMap(configFileUrl);
        configMap = ConfigXMLReader.getConfigMap(configFileUrl);
        viewMap = ConfigXMLReader.getViewMap(configFileUrl);
        
        /** Debugging */
        Debug.logInfo("----------------------------------");
        Debug.logInfo("Request Mappings:");
        Debug.logInfo("----------------------------------");
        HashMap debugMap =  requestMap;
        Set debugSet = debugMap.keySet();
        Iterator i = debugSet.iterator();
        while ( i.hasNext() ) {
            Object o = i.next();
            String request = (String) o;
            HashMap thisURI = (HashMap) debugMap.get(o);
            Debug.log(request);
            Iterator list = ((Set) thisURI.keySet()).iterator();
            while ( list.hasNext() ) {
                Object lo = list.next();
                String name = (String) lo;
                String value = (String) thisURI.get(lo);
                Debug.logInfo("\t" + name + " -> " + value);
            }
        }
        Debug.logInfo("----------------------------------");
        Debug.logInfo("End Request Mappings.");
        Debug.logInfo("----------------------------------");
        /** End Debugging */
    }
    
    public HashMap getRequestMap(String uriStr) {
        if ( requestMap != null && requestMap.containsKey(uriStr) )
            return (HashMap) requestMap.get(uriStr);
        return null;
    }
    
    public String getRequestAttribute(String uriStr, String attribute) {
        if ( requestMap != null && requestMap.containsKey(uriStr) ) {
            HashMap uri = (HashMap) requestMap.get(uriStr);
            if ( uri != null && uri.containsKey(attribute) )
                return (String) uri.get(attribute);
        }
        return null;
    }
    
    /** Gets the event class from the requestMap */
    public String getEventPath(String uriStr) {
        if ( requestMap != null && requestMap.containsKey(uriStr) ) {
            HashMap uri = (HashMap) requestMap.get(uriStr);
            if ( uri != null && uri.containsKey(ConfigXMLReader.EVENT_PATH) )
                return (String) uri.get(ConfigXMLReader.EVENT_PATH);
        }
        return null;
    }
    
    /** Gets the event type from the requestMap */
    public String getEventType(String uriStr) {
        if ( requestMap != null && requestMap.containsKey(uriStr) ) {
            HashMap uri = (HashMap) requestMap.get(uriStr);
            if ( uri != null && uri.containsKey(ConfigXMLReader.EVENT_TYPE) )
                return (String) uri.get(ConfigXMLReader.EVENT_TYPE);
        }
        return null;
    }
    
    /** Gets the event method from the requestMap */
    public String getEventMethod(String uriStr) {
        if ( requestMap != null && requestMap.containsKey(uriStr) ) {
            HashMap uri = (HashMap) requestMap.get(uriStr);
            if ( uri != null && uri.containsKey(ConfigXMLReader.EVENT_METHOD) )
                return (String) uri.get(ConfigXMLReader.EVENT_METHOD);
        }
        return null;
    }
    
    /** Gets the view name from the requestMap */
    public String getViewName(String uriStr) {
        if ( requestMap != null && requestMap.containsKey(uriStr) ) {
            HashMap uri = (HashMap) requestMap.get(uriStr);
            if ( uri != null && uri.containsKey(ConfigXMLReader.NEXT_PAGE) )
                return (String) uri.get(ConfigXMLReader.NEXT_PAGE);
        }
        return null;
    }
    
    /** Gets the next page (jsp) from the viewMap */
    public String getViewPage(String viewStr) {
        if ( viewStr != null && viewStr.startsWith("view:") )
            viewStr = viewStr.substring(viewStr.indexOf(':'));
        if ( viewMap != null && viewMap.containsKey(viewStr) ) {
            HashMap page = (HashMap) viewMap.get(viewStr);
            if ( page != null && page.containsKey(ConfigXMLReader.VIEW_PAGE) )
                return (String) page.get(ConfigXMLReader.VIEW_PAGE);
        }
        return null;
    }
    
    /** Gets the error page from the requestMap, if none uses the default */
    public String getErrorPage(String uriStr) {
        if ( requestMap != null && requestMap.containsKey(uriStr) ) {
            HashMap uri = (HashMap) requestMap.get(uriStr);
            if ( uri != null && uri.containsKey(ConfigXMLReader.ERROR_PAGE) ) {
                String returnPage = getViewPage((String) uri.get(ConfigXMLReader.ERROR_PAGE));
                if ( returnPage != null )
                    return returnPage;
            }
        }
        return getDefaultErrorPage();
    }
    
    /** Gets the default error page from the configMap or static site default */
    public String getDefaultErrorPage() {
        String errorPage = null;
        if ( configMap.containsKey(ConfigXMLReader.DEFAULT_ERROR_PAGE) )
            errorPage = (String) configMap.get(ConfigXMLReader.DEFAULT_ERROR_PAGE);
        if ( errorPage != null )
            return errorPage;
        return SiteDefs.ERROR_PAGE;
    }
    
    public boolean requiresAuth(String uriStr) {
        if ( requestMap != null && requestMap.containsKey(uriStr) ) {
            HashMap uri = (HashMap) requestMap.get(uriStr);
            if ( uri != null && uri.containsKey(ConfigXMLReader.SECURITY_AUTH) ) {
                String value = (String) uri.get(ConfigXMLReader.SECURITY_AUTH);
                if ( value.equalsIgnoreCase("true") )
                    return true;
            }
        }
        return false;
    }
    
    public boolean requiresHttps(String uriStr) {
        if ( requestMap != null && requestMap.containsKey(uriStr) ) {
            HashMap uri = (HashMap) requestMap.get(uriStr);
            if ( uri != null && uri.containsKey(ConfigXMLReader.SECURITY_HTTPS) ) {
                String value = (String) uri.get(ConfigXMLReader.SECURITY_HTTPS);
                if ( value.equalsIgnoreCase("true") )
                    return true;
            }
        }
        return false;
    }
    
    public Collection getPreProcessor() {
        if ( configMap != null && configMap.containsKey(ConfigXMLReader.PREPROCESSOR) ) {
            Collection c = (Collection) configMap.get(ConfigXMLReader.PREPROCESSOR);
            return c;
        }
        return null;
    }
    
    public Collection getPostProcessor() {
        if ( configMap != null && configMap.containsKey(ConfigXMLReader.POSTPROCESSOR) ) {
            Collection c = (Collection) configMap.get(ConfigXMLReader.POSTPROCESSOR);
            return c;
        }
        return null;
    }

        
}