/*
 * $Id$
 * $Log$
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
    public static final String ERROR_PAGE = "/jsp/default/error.jsp";
    public static final String SECURITY_CLASS = "org.ofbiz.commonapp.security.Security.class";
 
    /** Required parameter to locate site specific configuration */
    public static final String SITE_PARAM = "site_id";
    
    /** Used in session object to determine if a previous request is waiting */
    public static final String PREVIOUS_REQUEST = "_PREVIOUS_REQUEST_";
    
    /** Used in request/session object to denote error messages */
    public static final String ERROR_MESSAGE = "_ERROR_MESSAGE_";
    
    /** Used in servlet context to store the request handler */
    public static final String REQUEST_HANDLER = "_REQUEST_HANDLER_";
    
}