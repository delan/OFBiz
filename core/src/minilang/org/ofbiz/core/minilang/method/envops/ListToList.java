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

import java.util.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;

/**
 * Copies an environment field to a list
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class ListToList extends MethodOperation {
    
    ContextAccessor listAcsr;
    ContextAccessor toListAcsr;

    public ListToList(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        listAcsr = new ContextAccessor(element.getAttribute("list-name"));
        toListAcsr = new ContextAccessor(element.getAttribute("to-list-name"));
    }

    public boolean exec(MethodContext methodContext) {
        Object fieldVal = null;

        List fromList = (List) listAcsr.get(methodContext);
        List toList = (List) toListAcsr.get(methodContext);

        if (fromList == null) {
            if (Debug.infoOn()) Debug.logInfo("List not found with name " + listAcsr + ", not copying list");
            return true;
        }

        if (toList == null) {
            if (Debug.verboseOn()) Debug.logVerbose("List not found with name " + toListAcsr + ", creating new list");
            toList = new LinkedList();
            listAcsr.put(methodContext, toList);
        }

        toList.addAll(fromList);
        return true;
    }
}
