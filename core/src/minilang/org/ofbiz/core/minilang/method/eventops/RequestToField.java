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

package org.ofbiz.core.minilang.method.eventops;

import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;

/**
 * Copies a Servlet request attribute to a map field
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    February 20, 2002
 *@version    1.0
 */
public class RequestToField extends MethodOperation {
    String mapName;
    String fieldName;
    String requestName;

    public RequestToField(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        mapName = element.getAttribute("map-name");
        fieldName = element.getAttribute("field-name");
        requestName = element.getAttribute("request-name");

        if (requestName == null || requestName.length() == 0) {
            requestName = fieldName;
        }
    }

    public boolean exec(MethodContext methodContext) {
        //only run this if it is in an EVENT context
        if (methodContext.getMethodType() == MethodContext.EVENT) {
            Map fromMap = (Map) methodContext.getEnv(mapName);
            if (fromMap == null) {
                Debug.logWarning("Map not found with name " + mapName);
                return true;
            }

            Object fieldVal = methodContext.getRequest().getAttribute(requestName);
            if (fieldVal == null) {
                Debug.logWarning("Field value not found with name " + fieldName + " in Map with name " + mapName);
                return true;
            }

            fromMap.put(fieldName, fieldVal);
        }
        return true;
    }
}
