/*
 * $Id: ManufacLookupParams.java 3514 2004-09-23 08:30:48Z jacopo $
 *
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.accounting;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Fixed Asset Information Related Events
 *
 * @author     <a href="mailto:holivier@nereide.biz">Olivier Heintz</a>
 * @author		<a href="mailto:info@opentravelsystem.org>Hans Bakker</a>
 * @version    $Rev$
 * @since      3.0
 */
public class AccLookupParams {
    
    public static final String module = AccLookupParams.class.getName();
    
 
    /**
     * Set up parameters to be able to start the generic lockup to have LookupFixedAsset
     *
     * @param request  The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String lookupFixedAsset(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = ((HttpServletRequest) request).getSession();
        Map paramLookup = (Map) session.getAttribute("paramLookup");
        if (paramLookup == null || !"LookupFixedAsset".equals(paramLookup.get("lookupName"))) {
            if (paramLookup == null) {
                paramLookup = new HashMap();
                session.setAttribute("paramLookup", paramLookup);
            } else
                paramLookup.clear();
            paramLookup.put("lookupName", "LookupFixedAsset");
            paramLookup.put("titleProperty", "PageTitleLookupFixedAsset");
            paramLookup.put("formDefFile", "/lookup/FieldLookupForms.xml");
            paramLookup.put("singleFormName", "lookupFixedAsset");
            paramLookup.put("listFormName", "listLookupFixedAsset");
            paramLookup.put("entityName", "FixedAsset");
            //paramLookup.put("listName","entityList");
            paramLookup.put("viewSize", "10");
            paramLookup.put("permission", "ACCOUNTING");
            paramLookup.put("permissionType", "simple");
        }
        
        return "success";
    }
    }
