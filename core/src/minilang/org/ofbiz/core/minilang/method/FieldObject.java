/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.minilang.method;


import java.net.*;
import java.text.*;
import java.util.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;


/**
 * A type of MethodObject that represents an Object value in a certain location
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    October 9, 2002
 *@version    1.0
 */
public class FieldObject extends MethodObject {
    String fieldName;
    String mapName;
    String type;

    public FieldObject(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        fieldName = element.getAttribute("field-name");
        mapName = element.getAttribute("map-name");
        type = element.getAttribute("type");
        if (UtilValidate.isEmpty(type)) {
            type = "String";
        }
    }

    /** Get the name for the type of the object */
    public String getTypeName() {
        return type;
    }
    
    public Class getTypeClass(ClassLoader loader) {
        try {
            return ObjectType.loadClass(type, loader);
        } catch (ClassNotFoundException e) {
            Debug.logError(e, "Could not find class for type: " + type);
            return null;
        }
    }
    
    public Object getObject(MethodContext methodContext) {
        Object fieldVal = null;

        if (mapName != null && mapName.length() > 0) {
            Map fromMap = (Map) methodContext.getEnv(mapName);

            if (fromMap == null) {
                Debug.logWarning("Map not found with name " + mapName + ", not getting Object value, returning null.");
                return null;
            }

            fieldVal = fromMap.get(fieldName);
        } else {
            // no map name, try the env
            fieldVal = methodContext.getEnv(fieldName);
        }

        if (fieldVal == null) {
            if (Debug.infoOn()) Debug.logInfo("Field value not found with name " + fieldName + " in Map with name " + mapName + ", not getting Object value, returning null.");
            return null;
        }
        
        return fieldVal;
    }
}
