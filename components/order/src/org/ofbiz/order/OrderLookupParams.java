/*
 * $Id: OrderLookupParams.java,v 1.1 2004/07/30 16:01:45 ajzeneski Exp $
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.order;

import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Party Information Related Events
 *
 * @author     <a href="mailto:peter.goron@nereide.biz">Peter Goron</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class OrderLookupParams {

    public static final String module = OrderLookupParams.class.getName();

    /**
     * Set up parameters to be able to start the generic lockup to have LookupPerson
     *
     * @param request  The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String lookupPerson(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Map paramLookup = (Map) session.getAttribute("paramLookup");
        if (paramLookup == null || !"LookupPerson".equals(paramLookup.get("lookupName"))) {
            if (paramLookup == null) {
                paramLookup = new HashMap();
                session.setAttribute("paramLookup", paramLookup);
            } else {
                paramLookup.clear();
            }

            paramLookup.put("lookupName", "LookupPerson");
            paramLookup.put("titleProperty", "PageTitleLookupPerson");
            paramLookup.put("formDefFile", "/lookup/FieldLookupForms.xml");
            paramLookup.put("singleFormName", "lookupPersonForm");
            paramLookup.put("listFormName", "lookupPersonListForm");
            paramLookup.put("entityName", "Person");
            paramLookup.put("viewSize", "10");
            paramLookup.put("permission", "PARTYMGR");
            paramLookup.put("permissionType", "_VIEW");
        }

        return "success";
    }

    /**
     * Set up parameters to be able to start the generic lockup to have LookupPartyGroup
     *
     * @param request  The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String lookupPartyGroup(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Map paramLookup = (Map) session.getAttribute("paramLookup");
        if (paramLookup == null || !"LookupPartyGroup".equals(paramLookup.get("lookupName"))) {
            if (paramLookup == null) {
                paramLookup = new HashMap();
                session.setAttribute("paramLookup", paramLookup);
            } else {
                paramLookup.clear();
            }

            paramLookup.put("lookupName", "LookupGroup");
            paramLookup.put("titleProperty", "PageTitleLookupGroup");
            paramLookup.put("formDefFile", "/lookup/FieldLookupForms.xml");
            paramLookup.put("singleFormName", "lookupPartyGroupForm");
            paramLookup.put("listFormName", "lookupPartyGroupListForm");
            paramLookup.put("entityName", "PartyGroup");
            paramLookup.put("viewSize", "10");
            paramLookup.put("permission", "PARTYMGR");
            paramLookup.put("permissionType", "_VIEW");
        }

        return "success";
    }

}
