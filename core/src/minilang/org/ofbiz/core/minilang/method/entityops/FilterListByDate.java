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

package org.ofbiz.core.minilang.method.entityops;


import java.net.*;
import java.text.*;
import java.util.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;
import org.ofbiz.core.entity.*;


/**
 * Uses the delegator to find entity values by anding the map fields
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    June 5, 2002
 *@version    1.0
 */
public class FilterListByDate extends MethodOperation {
    String listName;
    String toListName;
    String validDateName;
    String fromFieldName;
    String thruFieldName;
    boolean allSame;

    public FilterListByDate(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        listName = element.getAttribute("list-name");
        toListName = element.getAttribute("to-list-name");
        if (UtilValidate.isEmpty(toListName)) toListName = listName;
        validDateName = element.getAttribute("valid-date-name");

        fromFieldName = element.getAttribute("from-field-name");
        if (UtilValidate.isEmpty(fromFieldName)) fromFieldName = "fromDate";
        thruFieldName = element.getAttribute("thru-field-name");
        if (UtilValidate.isEmpty(thruFieldName)) thruFieldName = "thruDate";

        allSame = !"false".equals(element.getAttribute("all-same"));
    }

    public boolean exec(MethodContext methodContext) {
        if (UtilValidate.isNotEmpty(validDateName)) {
            methodContext.putEnv(toListName, EntityUtil.filterByDate((List) methodContext.getEnv(listName), (java.sql.Timestamp) methodContext.getEnv(validDateName), fromFieldName, thruFieldName, true));
        } else {
            methodContext.putEnv(toListName, EntityUtil.filterByDate((List) methodContext.getEnv(listName), UtilDateTime.nowTimestamp(), fromFieldName, thruFieldName, true));
        }
        return true;
    }
}

