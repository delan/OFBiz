/*
 * $Id$
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
package org.ofbiz.manufacturing;

import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;

/**
 * Product Information Related Events
 *
 * @author     <a href="mailto:holivier@nereide.biz">Olivier Heintz</a>
 * @version    $Rev$
 * @since      3.0
 */
public class ManufacLookupParams {
    
    public static final String module = ManufacLookupParams.class.getName();
    
    /**
     * Set up parameters to be able to start the generic lockup to have LookupRoutingTask
     *
     * @param request  The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String lookupRouting(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = ((HttpServletRequest) request).getSession();
        Map paramLookup = (Map) session.getAttribute("paramLookup");
        if (paramLookup == null || !"LookupRouting".equals(paramLookup.get("lookupName"))) {
            if (paramLookup == null) {
                paramLookup = new HashMap();
                session.setAttribute("paramLookup", paramLookup);
            } else
                paramLookup.clear();
            paramLookup.put("lookupName", "LookupRouting");
            paramLookup.put("titleProperty", "PageTitleLookupRouting");
            paramLookup.put("formDefFile", "/lookup/FieldLookupForms.xml");
            paramLookup.put("singleFormName", "lookupRouting");
            paramLookup.put("listFormName", "listLookupRouting");
            paramLookup.put("entityName", "WorkEffort");
            //paramLookup.put("listName","entityList");
            paramLookup.put("viewSize", "10");
            paramLookup.put("permission", "MANUFACTURING");
            paramLookup.put("permissionType", "simple");
            paramLookup.put("initialConstraint", UtilMisc.toMap("workEffortTypeId", "ROUTING", "fixedAssetId_op", "equals"));
        }
        return "success";
    }
    
    /**
     * Set up parameters to be able to start the generic lockup to have LookupRoutingTask
     *
     * @param request  The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String lookupRoutingTask(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = ((HttpServletRequest) request).getSession();
        Map paramLookup = (Map) session.getAttribute("paramLookup");
        if (paramLookup == null || !"LookupRoutingTask".equals(paramLookup.get("lookupName"))) {
            if (paramLookup == null) {
                paramLookup = new HashMap();
                session.setAttribute("paramLookup", paramLookup);
            } else
                paramLookup.clear();
            paramLookup.put("lookupName", "LookupRoutingTask");
            paramLookup.put("titleProperty", "PageTitleLookupRoutingTask");
            paramLookup.put("formDefFile", "/lookup/FieldLookupForms.xml");
            paramLookup.put("singleFormName", "lookupRoutingTask");
            paramLookup.put("listFormName", "listLookupRoutingTask");
            paramLookup.put("entityName", "WorkEffort");
            //paramLookup.put("listName","entityList");
            paramLookup.put("viewSize", "10");
            paramLookup.put("permission", "MANUFACTURING");
            paramLookup.put("permissionType", "simple");
            paramLookup.put("initialConstraint", UtilMisc.toMap("workEffortTypeId", "ROU_TASK", "fixedAssetId_op", "equals"));
        }
        return "success";
    }
    
    /**
     * Set up parameters to be able to start the generic lockup to have LookupProduct
     *
     * @param request  The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String lookupProduct(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = ((HttpServletRequest) request).getSession();
        Map paramLookup = (Map) session.getAttribute("paramLookup");
        if (paramLookup == null || !"LookupProduct".equals(paramLookup.get("lookupName"))) {
            if (paramLookup == null) {
                paramLookup = new HashMap();
                session.setAttribute("paramLookup", paramLookup);
            } else
                paramLookup.clear();
            paramLookup.put("lookupName", "LookupProduct");
            paramLookup.put("titleProperty", "PageTitleLookupProduct");
            paramLookup.put("formDefFile", "/lookup/FieldLookupForms.xml");
            paramLookup.put("singleFormName", "lookupProduct");
            paramLookup.put("listFormName", "listLookupProduct");
            paramLookup.put("entityName", "Product");
            //paramLookup.put("listName","entityList");
            paramLookup.put("viewSize", "10");
            paramLookup.put("permission", "MANUFACTURING");
            paramLookup.put("permissionType", "simple");
        }
        
        return "success";
    }
    
    /**
     * Set up parameters to be able to start the generic lockup to have LookupVirtualProduct
     *
     * @param request  The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String lookupVirtualProduct(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = ((HttpServletRequest) request).getSession();
        Map paramLookup = (Map) session.getAttribute("paramLookup");
        if (paramLookup == null || !"LookupVirtualProduct".equals(paramLookup.get("lookupName"))) {
            if (paramLookup == null) {
                paramLookup = new HashMap();
                session.setAttribute("paramLookup", paramLookup);
            } else
                paramLookup.clear();
            paramLookup.put("lookupName", "LookupVirtualProduct");
            paramLookup.put("titleProperty", "PageTitleLookupVirtualProducts");
            paramLookup.put("formDefFile", "/bom/BomForms.xml");
            paramLookup.put("singleFormName", "lookupVirtualProduct");
            paramLookup.put("listFormName", "listLookupVirtualProduct");
            paramLookup.put("entityName", "Product");
            //paramLookup.put("listName","entityList");
            paramLookup.put("viewSize", "10");
            paramLookup.put("permission", "MANUFACTURING");
            paramLookup.put("permissionType", "simple");
            paramLookup.put("initialConstraint", UtilMisc.toMap("isVirtual", "Y", "isVirtual_op", "equals"));
        }
        
        return "success";
    }
}
