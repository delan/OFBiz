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

package org.ofbiz.core.minilang.method.envops;


import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;


/**
 * Copies a map field to a map field
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    February 19, 2002
 *@version    1.0
 */
public class FieldToField extends MethodOperation {
    String mapName;
    String fieldName;
    String toMapName;
    String toFieldName;

    public FieldToField(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        mapName = element.getAttribute("map-name");
        fieldName = element.getAttribute("field-name");
        toMapName = element.getAttribute("to-map-name");
        toFieldName = element.getAttribute("to-field-name");

        // set toMapName and toFieldName to their defualt values of mapName and fieldName if empty
        if (toMapName == null || toMapName.length() == 0) {
            toMapName = mapName;
        }
        if (toFieldName == null || toFieldName.length() == 0) {
            toFieldName = fieldName;
        }
    }

    public boolean exec(MethodContext methodContext) {
        Object fieldVal = null;

        if (mapName != null && mapName.length() > 0) {
            Map fromMap = (Map) methodContext.getEnv(mapName);

            if (fromMap == null) {
                if (Debug.infoOn()) Debug.logInfo("Map not found with name " + mapName + ", not copying from this map");
                return true;
            }

            fieldVal = fromMap.get(fieldName);
        } else {
            // no map name, try the env
            fieldVal = methodContext.getEnv(fieldName);
        }

        if (fieldVal == null) {
            if (Debug.verboseOn()) Debug.logVerbose("Field value not found with name " + fieldName + " in Map with name " + mapName + ", not copying field");
            return true;
        }

        // note that going to an env field will only work if it came from an env 
        // field because if not specified the to-map-name will be set to the map-name
        // to go from a map field to an env field, use the field-to-env operation
        Map toMap = null;

        if (toMapName != null && toMapName.length() > 0) {
            toMap = (Map) methodContext.getEnv(toMapName);
            if (toMap == null) {
                if (Debug.verboseOn()) Debug.logVerbose("Map not found with name " + toMapName + ", creating new map");
                toMap = new HashMap();
                methodContext.putEnv(toMapName, toMap);
            }
            toMap.put(toFieldName, fieldVal);
        } else {
            // no to-map, so put in env
            methodContext.putEnv(toFieldName, fieldVal);
        }

        return true;
    }
}
