/*
 * $Id: CommonWorkers.java,v 1.1 2003/08/17 10:12:40 jonesde Exp $
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.common;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * Common Workers
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      2.0
 */
public class CommonWorkers {
    
    public final static String module = CommonWorkers.class.getName();

    public static String makeLoginUrl(PageContext pageContext) {
        return makeLoginUrl(pageContext, "checkLogin");
    }

    public static String makeLoginUrl(ServletRequest request) {
        return makeLoginUrl(request, "checkLogin");
    }
	
    public static String makeLoginUrl(PageContext pageContext, String requestName) {
        return makeLoginUrl(pageContext.getRequest(), requestName);
    }
    public static String makeLoginUrl(ServletRequest request, String requestName) {
        String queryString = null;

        Enumeration parameterNames = request.getParameterNames();

        while (parameterNames != null && parameterNames.hasMoreElements()) {
            String paramName = (String) parameterNames.nextElement();

            if (paramName != null) {
                if (queryString == null) queryString = paramName + "=" + request.getParameter(paramName);
                else queryString = queryString + "&" + paramName + "=" + request.getParameter(paramName);
            }
        }

        String loginUrl = "/" + requestName + "/" + UtilFormatOut.checkNull((String) request.getAttribute("_CURRENT_VIEW_"));

        if (queryString != null) loginUrl = loginUrl + "?" + UtilFormatOut.checkNull(queryString);

        return loginUrl;
    }
    
    public static List getCountryList(GenericDelegator delegator) {
        List geoList = new ArrayList();
        String defaultCountry = UtilProperties.getPropertyValue("general.properties", "country.uom.id.default");
        GenericValue defaultGeo = null;
        if (defaultCountry != null && defaultCountry.length() > 0) { 
            try {
                defaultGeo = delegator.findByPrimaryKeyCache("Geo", UtilMisc.toMap("geoId", defaultCountry));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot lookup Geo", module);
            }       
        }        
        Map findMap = UtilMisc.toMap("geoTypeId", "COUNTRY");
        List sortList = UtilMisc.toList("geoName");
        try {
            geoList = delegator.findByAndCache("Geo", findMap, sortList);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot lookup Geo", module);
        }        
        if (defaultGeo != null) {
            geoList.add(0, defaultGeo);
        }        
        return geoList;            
    }
    
    public static List getStateList(GenericDelegator delegator) {
        List geoList = new ArrayList();       
        Map findMap = UtilMisc.toMap("geoTypeId", "STATE");
        List sortList = UtilMisc.toList("geoName");
        try {
            geoList = delegator.findByAndCache("Geo", findMap, sortList);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot lookup Geo", module);
        }                        
        return geoList;            
    }    
}
