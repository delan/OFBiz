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
package org.ofbiz.core.minilang.method.envops;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.core.minilang.SimpleMethod;
import org.ofbiz.core.minilang.method.ContextAccessor;
import org.ofbiz.core.minilang.method.MethodContext;
import org.ofbiz.core.minilang.method.MethodOperation;
import org.ofbiz.core.util.Debug;
import org.w3c.dom.Element;

/**
 * Process sub-operations for each entry in the map
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class IterateMap extends MethodOperation {

    List subOps = new LinkedList();

    ContextAccessor keyAcsr;
    ContextAccessor valueAcsr;
    ContextAccessor mapAcsr;

    public IterateMap(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        this.keyAcsr = new ContextAccessor(element.getAttribute("key-name"));
        this.valueAcsr = new ContextAccessor(element.getAttribute("value-name"));
        this.mapAcsr = new ContextAccessor(element.getAttribute("map-name"));

        SimpleMethod.readOperations(element, subOps, simpleMethod);
    }

    public boolean exec(MethodContext methodContext) {
        Object fieldVal = null;

        if (mapAcsr.isEmpty()) {
            Debug.logWarning("No map-name specified in iterate tag, doing nothing");
            return true;
        }

        Object oldKey = keyAcsr.get(methodContext);
        Object oldValue = valueAcsr.get(methodContext);
        Map theMap = (Map) mapAcsr.get(methodContext);

        if (theMap == null) {
            if (Debug.infoOn()) Debug.logInfo("Map not found with name " + mapAcsr + ", doing nothing");
            return true;
        }
        if (theMap.size() == 0) {
            if (Debug.verboseOn()) Debug.logVerbose("Map with name " + mapAcsr + " has zero entries, doing nothing");
            return true;
        }

        Iterator theIterator = theMap.entrySet().iterator();
        while (theIterator.hasNext()) {
            Map.Entry theEntry = (Map.Entry) theIterator.next();
            keyAcsr.put(methodContext, theEntry.getKey());
            valueAcsr.put(methodContext, theEntry.getValue());

            if (!SimpleMethod.runSubOps(subOps, methodContext)) {
                // only return here if it returns false, otherwise just carry on
                return false;
            }
        }

        keyAcsr.put(methodContext, oldKey);
        valueAcsr.put(methodContext, oldValue);
        return true;
    }
}
