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

import java.text.*;
import java.util.*;

import org.w3c.dom.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;

/**
 * Appends the specified String to a List
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class StringToList extends MethodOperation {
    
    String string;
    ContextAccessor listAcsr;
    ContextAccessor argListAcsr;

    public StringToList(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        string = element.getAttribute("string");
        listAcsr = new ContextAccessor(element.getAttribute("list-name"));
        argListAcsr = new ContextAccessor(element.getAttribute("arg-list-name"));
    }

    public boolean exec(MethodContext methodContext) {
        String value = methodContext.expandString(string);
        
        if (!argListAcsr.isEmpty()) {
            List argList = (List) argListAcsr.get(methodContext);
            if (argList != null && argList.size() > 0) {
                value = MessageFormat.format(value, argList.toArray());
            }
        }

        List toList = (List) listAcsr.get(methodContext);

        if (toList == null) {
            if (Debug.verboseOn()) Debug.logVerbose("List not found with name " + listAcsr + ", creating new List");
            toList = new LinkedList();
            listAcsr.put(methodContext, toList);
        }
        toList.add(value);

        return true;
    }
}
