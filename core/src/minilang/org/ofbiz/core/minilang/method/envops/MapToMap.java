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
 *@created    October 10, 2002
 *@version    1.0
 */
public class MapToMap extends MethodOperation {
    String mapName;
    String toMapName;

    public MapToMap(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        mapName = element.getAttribute("map-name");
        toMapName = element.getAttribute("to-map-name");
    }

    public boolean exec(MethodContext methodContext) {
        Map fromMap = null;
        if (mapName != null && mapName.length() > 0) {
            fromMap = (Map) methodContext.getEnv(mapName);

            if (fromMap == null) {
                if (Debug.infoOn()) Debug.logInfo("Map not found with name " + mapName + ", not copying from this map");
                return true;
            }
        }

        if (toMapName != null && toMapName.length() > 0) {
            Map toMap = (Map) methodContext.getEnv(toMapName);
            if (toMap == null) {
                if (Debug.verboseOn()) Debug.logVerbose("Map not found with name " + toMapName + ", creating new map");
                toMap = new HashMap();
                methodContext.putEnv(toMapName, toMap);
            }

            toMap.putAll(fromMap);
        } else {
            methodContext.putAllEnv(fromMap);
        }
        return true;
    }
}
