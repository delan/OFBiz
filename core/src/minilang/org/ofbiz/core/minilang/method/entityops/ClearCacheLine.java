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
package org.ofbiz.core.minilang.method.entityops;

import java.util.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;

/**
 * Uses the delegator to clear elements from the cache; intelligently looks at
 *  the map passed to see if it is a byPrimaryKey, and byAnd, or an all.
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class ClearCacheLine extends MethodOperation {
    
    String entityName;
    String mapName;

    public ClearCacheLine(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        entityName = element.getAttribute("entity-name");
        mapName = element.getAttribute("map-name");
    }

    public boolean exec(MethodContext methodContext) {
        if (mapName == null || mapName.length() == 0) {
            methodContext.getDelegator().clearCacheLine(entityName, null);
        } else {
            Map theMap = (Map) methodContext.getEnv(mapName);

            if (theMap == null) {
                Debug.logWarning("In clear-cache-line could not find map with name " + mapName + ", not clearing any cache lines");
            } else {
                methodContext.getDelegator().clearCacheLine(entityName, theMap);
            }
        }
        return true;
    }
}
