/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.util;

/**
 * Holds general site wide variables.
 *
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @version    1.0
 * @created    June 28, 2001
 */
public final class SiteDefs {

    /* Default Site Config Variables */
    public static final String ERROR_PAGE = "/error/error.jsp";
    public static final String CHECK_LOGIN_REQUEST_URI = "checkLogin";
    public static final String LOGIN_REQUEST_URI = "login";
    public static final String LOGOUT_REQUEST_URI = "logout";

    /** The name of the flag in the request attributes to denote that the control servlet was passed through */
    public static final String FORWARDED_FROM_CONTROL_SERVLET = "_FORWARDED_FROM_CONTROL_SERVLET_";
    
    /** Required context init parameter to specify the entity delegator name as defined in entityengine.xml */
    public static final String ENTITY_DELEGATOR_NAME = "entityDelegatorName";

    /** Location of controller configuration XML file */
    public static final String CONTROLLER_CONFIG_LOCATION = "/WEB-INF/controller.xml";
    /** Location of regions configuration XML file */
    public static final String REGIONS_CONFIG_LOCATION = "/WEB-INF/regions.xml";

    /** Used in session object to determine if a previous request is waiting */
    public static final String PREVIOUS_REQUEST = "_PREVIOUS_REQUEST_";
    /** Used in session object to store previous parameters */
    public static final String PREVIOUS_PARAMS = "_PREVIOUS_PARAMS_";
    /** Used in request object to note that a login has passed */
    public static final String LOGIN_PASSED = "_LOGIN_PASSED_";

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

    /** Used in request/session object to denote event non error messages */
    public static final String EVENT_MESSAGE = "_EVENT_MESSAGE_";

    /** Used in servlet context to store the request handler */
    public static final String REQUEST_HANDLER = "_REQUEST_HANDLER_";

    /** Used session object to locate the shopping cart */
    public static final String SHOPPING_CART = "_SHOPPING_CART_";

    /** Session attribute name for UserLogin entity of current logged in user, if one is logged in */
    public static final String USER_LOGIN = "userLogin";
    /** Session attribute name for Person entity of current logged in user, if one is logged in and it is a person */
    public static final String PERSON = "_PERSON_";

    /** Session attributes for the client's initial connect variables */
    public static final String CLIENT_REFERER = "_CLIENT_REFERER_";
    public static final String CLIENT_USER_AGENT = "_CLIENT_USER_AGENT_";
    public static final String CLIENT_REQUEST = "_CLIENT_REQUEST_";
    public static final String CLIENT_LOCALE = "_CLIENT_LOCALE_";

    /** Request attribute used to store the filesystem path of the Context Root. */
    public static final String CONTEXT_ROOT = "_CONTEXT_ROOT_";

}
