/*
 * $Id$
 * $Log$
 * Revision 1.15  2001/09/26 01:35:49  azeneski
 * Modification to set context root in request object.
 *
 * Revision 1.14  2001/09/19 08:32:02  jonesde
 * Initial checkin of refactored entity engine.
 *
 * Revision 1.13  2001/09/14 20:04:57  epabst
 * updated Site name
 *
 * Revision 1.12  2001/09/14 19:06:05  epabst
 * created new session attribute called SiteDefs.SERVER_ROOT_URL that contains something like:
 * "http://myserver.com:1234"
 *
 * Revision 1.11  2001/08/25 16:57:14  azeneski
 * *** empty log message ***
 *
 * Revision 1.10  2001/08/24 17:14:34  azeneski
 * Removed plain text attribute and created a defination in SiteDefs.
 * NOTE: Need to update all pages/events which use the old name!
 *
 * Revision 1.9  2001/08/17 07:39:03  jonesde
 * Added initialization to ControlServlet, and put security and helper into the application scope (ServletContext). Other small changes to support this.
 *
 * Revision 1.8  2001/07/29 01:57:31  azeneski
 * *** empty log message ***
 *
 * Revision 1.7  2001/07/23 18:05:00  azeneski
 * Fixed runaway thread in the job scheduler.
 *
 * Revision 1.6  2001/07/19 20:50:22  azeneski
 * Added the job scheduler to 'core' module.
 *
 * Revision 1.5  2001/07/19 14:19:31  azeneski
 * Added scheduler config variable to SiteDefs.
 *
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
    public static final String ERROR_PAGE = "/error/error.jsp";
    public static final String CHECK_LOGIN_REQUEST_URI = "checkLogin";
    public static final String LOGIN_REQUEST_URI = "login";
    public static final String LOGOUT_REQUEST_URI = "logout";
 
    /** Required context init parameter to specify the entity delegator name as defined in servers.properties */
    public static final String ENTITY_DELEGATOR_NAME = "entityDelegatorName";

    /** Required context init parameter to locate site specific configuration */
    public static final String SITE_CONFIG = "siteConfigurationFile";
    
    /** Required context init parameter to locate scheduler configuration */
    public static final String SCHEDULER_CONFIG = "schedulerConfigurationFile";
    /** Required to parse the date strings in the scheduler config file. */
    public static final String SCHEDULER_DATE_FORMAT = "yyyy-MM-dd hh:mm";
    
    /** Used in session object to determine if a previous request is waiting */
    public static final String PREVIOUS_REQUEST = "_PREVIOUS_REQUEST_";
    /** Used in session object to store previous parameters */
    public static final String PREVIOUS_PARAMS = "_PREVIOUS_PARAMS_";
    
    /** Used in session object to pass around the protocol, server name, and port */
    public static final String SERVER_ROOT_URL = "_SERVER_ROOT_URL_";
    
    /** Used in request object to pass around the web path */
    public static final String CONTROL_PATH = "_CONTROL_PATH_";
    
    /** Used in request object to store the ServletConfig object */
    public static final String SERVLET_CONTEXT = "_SERVLET_CONTEXT_";    
    
    /** Used in request object to pass around the final view of the current request */
    public static final String CURRENT_VIEW = "_CURRENT_VIEW_";
    
    /** Used in request/session object to denote error messages */
    public static final String ERROR_MESSAGE = "_ERROR_MESSAGE_";
    
    /** Used in servlet context to store the request handler */
    public static final String REQUEST_HANDLER = "_REQUEST_HANDLER_";   
    
    /** Used in servlet request to store the job manager */
    public static final String JOB_MANAGER = "_JOB_MANAGER_";
    
    /** Used session object to locate the shopping cart */
    public static final String SHOPPING_CART = "_SHOPPING_CART_";

    /** Session attribute name for UserLogin entity of current logged in user, if one is logged in */
    public static final String USER_LOGIN = "_USER_LOGIN_";
        
    /** Session attributes for the client's initial connect variables */
    public static final String CLIENT_REFERER = "_CLIENT_REFERER_";
    public static final String CLIENT_USER_AGENT = "_CLIENT_USER_AGENT_";
    public static final String CLIENT_REQUEST = "_CLIENT_REQUEST_";
    public static final String CLIENT_LOCALE = "_CLIENT_LOCALE_";
    
    /** Request attribute used to store the filesystem path of the Context Root. */
    public static final String CONTEXT_ROOT = "_CONTEXT_ROOT_";
    
}