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

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;

/**
 * Copies an properties file property value to a field
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    February 19, 2002
 *@version    1.0
 */
public class PropertyToField extends MethodOperation {
    String resource;
    String property;
    String defaultVal;
    String mapName;
    String fieldName;

    public PropertyToField(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        resource = element.getAttribute("resource");
        property = element.getAttribute("property");
        defaultVal = element.getAttribute("default");
        mapName = element.getAttribute("map-name");
        fieldName = element.getAttribute("field-name");
    }

    public boolean exec(MethodContext methodContext) {
        String value = UtilProperties.getPropertyValue(resource, property);
        
        if (value == null || value.length() == 0) {
            value = defaultVal;
        }
        
        if (mapName != null && mapName.length() > 0) {
            Map toMap = (Map) methodContext.getEnv(mapName);
            if (toMap == null) {
                if (Debug.infoOn()) Debug.logInfo("Map not found with name " + mapName + ", creating new map");
                toMap = new HashMap();
                methodContext.putEnv(mapName, toMap);
            }
            toMap.put(fieldName, value);
        } else {
            methodContext.putEnv(fieldName, value);
        }

        return true;
    }
}
