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
 * Get the first entry from the list
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class FirstFromList extends MethodOperation {

    String entryName;
    String listName;

    public FirstFromList(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        this.entryName = element.getAttribute("entry-name");
        this.listName = element.getAttribute("list-name");
    }

    public boolean exec(MethodContext methodContext) {
        Object fieldVal = null;

        if (listName == null || listName.length() == 0) {
            Debug.logWarning("No list-name specified in iterate tag, doing nothing");
            return true;
        }

        List theList = (List) methodContext.getEnv(listName);

        if (theList == null) {
            if (Debug.infoOn()) Debug.logInfo("List not found with name " + listName + ", doing nothing");
            return true;
        }
        if (theList.size() == 0) {
            if (Debug.verboseOn()) Debug.logVerbose("List with name " + listName + " has zero entries, doing nothing");
            return true;
        }

        methodContext.putEnv(entryName, theList.get(0));
        return true;
    }
}
