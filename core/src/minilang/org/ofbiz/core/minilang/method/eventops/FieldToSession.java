/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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

import java.util.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;

/**
 * Copies a map field to a Servlet session attribute
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class FieldToSession extends MethodOperation {
    ContextAccessor mapAcsr;
    ContextAccessor fieldAcsr;
    String sessionName;

    public FieldToSession(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        mapAcsr = new ContextAccessor(element.getAttribute("map-name"));
        fieldAcsr = new ContextAccessor(element.getAttribute("field-name"));
        sessionName = element.getAttribute("session-name");

        if (sessionName == null || sessionName.length() == 0) {
            sessionName = element.getAttribute("field-name");
        }
    }

    public boolean exec(MethodContext methodContext) {
        // only run this if it is in an EVENT context
        if (methodContext.getMethodType() == MethodContext.EVENT) {
            Object fieldVal = null;
            if (!mapAcsr.isEmpty()) {
                Map fromMap = (Map) mapAcsr.get(methodContext);
                if (fromMap == null) {
                    Debug.logWarning("Map not found with name " + mapAcsr);
                    return true;
                }
                fieldVal = fieldAcsr.get(fromMap, methodContext);
            } else {
                // no map name, try the env
                fieldVal = fieldAcsr.get(methodContext);
            }

            if (fieldVal == null) {
                Debug.logWarning("Field value not found with name " + fieldAcsr + " in Map with name " + mapAcsr);
                return true;
            }

            methodContext.getRequest().getSession().setAttribute(sessionName, fieldVal);
        }
        return true;
    }
}
