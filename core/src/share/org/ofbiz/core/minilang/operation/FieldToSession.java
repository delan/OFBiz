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

package org.ofbiz.core.minilang.operation;

import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.service.*;

import org.ofbiz.core.minilang.*;

/**
 * Copies a map field to a Servlet session attribute
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    December 29, 2001
 *@version    1.0
 */
public class FieldToSession extends EventOperation {
    String mapName;
    String fieldName;
    String sessionName;

    public FieldToSession(Element element, SimpleEvent simpleEvent) {
        super(element, simpleEvent);
        mapName = element.getAttribute("map-name");
        fieldName = element.getAttribute("field-name");
        sessionName = element.getAttribute("session-name");

        if (sessionName == null || sessionName.length() == 0) {
            sessionName = fieldName;
        }
    }

    public boolean exec(Map env, HttpServletRequest request, ClassLoader loader) {
        Map fromMap = (Map) env.get(mapName);
        if (fromMap == null) {
            Debug.logWarning("[SimpleEvent.FieldToSession.exec] Map not found with name " + mapName);
            return true;
        }

        Object fieldVal = fromMap.get(fieldName);
        if (fieldVal == null) {
            Debug.logWarning("[SimpleEvent.FieldToSession.exec] Field value not found with name " + fieldName + " in Map with name " + mapName);
            return true;
        }

        request.getSession().setAttribute(sessionName, fieldVal);
        return true;
    }
}
