/*
 * $Id$
 * $Log$
 * Revision 1.4  2001/07/17 22:17:21  jonesde
 * Updates for improved login: no extra redirect, and can login staying on same page
 *
 * Revision 1.3  2001/07/17 08:51:37  jonesde
 * Updated for auth implementation & small fixes.
 *
 * Revision 1.2  2001/07/16 22:31:06  azeneski
 * Moved multi-site support to be handled by the webapp.
 *
 * Revision 1.1  2001/07/16 14:45:48  azeneski
 * Added the missing 'core' directory into the module.
 *
 * Revision 1.1  2001/07/15 16:36:18  azeneski
 * Initial Import
 *
 */

package org.ofbiz.core.util;

/**
 * <p><b>Title:</b> SiteDefs.java
 * <p><b>Description:</b> Holds general site wide variables.
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
public final class SiteDefs {
    
    /** Default Site Config Variables */
    public static final String SITE_NAME = "Jaguar E-Business Suite";
    public static final String SITE_CONF = "/WEB-INF/xml/siteconf.properties";
    public static final String ERROR_PAGE = "/error/error.jsp";
    public static final String CHECK_LOGIN_REQUEST_URI = "checkLogin";
    public static final String LOGIN_REQUEST_URI = "login";
    public static final String LOGOUT_REQUEST_URI = "logout";
 
    /** Required context init parameter to locate site specific configuration */
    public static final String SITE_CONFIG = "siteConfigurationFile";
    
    /** Required context init parameter to locate scheduler configuration */
    public static final String SCHEDULER_CONFIG = "schedulerConfigurationFile";
    
    /** Used in session object to determine if a previous request is waiting */
    public static final String PREVIOUS_REQUEST = "_PREVIOUS_REQUEST_";
    /** Used in session object to store previous parameters */
    public static final String PREVIOUS_PARAMS = "_PREVIOUS_PARAMS_";
    
    /** Used in request object to pass around the web path */
    public static final String CONTROL_PATH = "_CONTROL_PATH_";
    /** Used in request object to pass around the final view of the current request */
    public static final String CURRENT_VIEW = "_CURRENT_VIEW_";
    
    /** Used in request/session object to denote error messages */
    public static final String ERROR_MESSAGE = "_ERROR_MESSAGE_";
    
    /** Used in servlet context to store the request handler */
    public static final String REQUEST_HANDLER = "_REQUEST_HANDLER_";   
}