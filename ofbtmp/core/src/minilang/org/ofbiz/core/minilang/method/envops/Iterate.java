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
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;

/**
 * Process sub-operations for each entry in the list
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class Iterate extends MethodOperation {
    
    public static final String module = Iterate.class.getName();

    List subOps = new LinkedList();

    ContextAccessor entryAcsr;
    ContextAccessor listAcsr;

    public Iterate(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        this.entryAcsr = new ContextAccessor(element.getAttribute("entry-name"));
        this.listAcsr = new ContextAccessor(element.getAttribute("list-name"));

        SimpleMethod.readOperations(element, subOps, simpleMethod);
    }

    public boolean exec(MethodContext methodContext) {
        Object fieldVal = null;

        if (listAcsr.isEmpty()) {
            Debug.logWarning("No list-name specified in iterate tag, doing nothing", module);
            return true;
        }

        Object oldEntryValue = entryAcsr.get(methodContext);
        Object objList = listAcsr.get(methodContext);
        if (objList instanceof EntityListIterator) {
            EntityListIterator eli = (EntityListIterator) objList;

            GenericValue theEntry;
            while ((theEntry = (GenericValue) eli.next()) != null) {
                entryAcsr.put(methodContext, theEntry);

                if (!SimpleMethod.runSubOps(subOps, methodContext)) {
                    // only return here if it returns false, otherwise just carry on
                    return false;
                }
            }

            // close the iterator
            try {
                eli.close();
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                String errMsg = "ERROR: Error closing entityListIterator in " + simpleMethod.getShortDescription() + " [" + e.getMessage() + "]";

                if (methodContext.getMethodType() == MethodContext.EVENT) {
                    methodContext.putEnv(simpleMethod.getEventErrorMessageName(), errMsg);
                    methodContext.putEnv(simpleMethod.getEventResponseCodeName(), simpleMethod.getDefaultErrorCode());
                } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
                    methodContext.putEnv(simpleMethod.getServiceErrorMessageName(), errMsg);
                    methodContext.putEnv(simpleMethod.getServiceResponseMessageName(), simpleMethod.getDefaultErrorCode());
                }
                return false;
            }
        } else {
            Collection theList = (Collection) objList;

            if (theList == null) {
                if (Debug.infoOn()) Debug.logInfo("List not found with name " + listAcsr + ", doing nothing", module);
                return true;
            }
            if (theList.size() == 0) {
                if (Debug.verboseOn()) Debug.logVerbose("List with name " + listAcsr + " has zero entries, doing nothing", module);
                return true;
            }

            Iterator theIterator = theList.iterator();

            while (theIterator.hasNext()) {
                Object theEntry = theIterator.next();
                entryAcsr.put(methodContext, theEntry);

                if (!SimpleMethod.runSubOps(subOps, methodContext)) {
                    // only return here if it returns false, otherwise just carry on
                    return false;
                }
            }
        }
        entryAcsr.put(methodContext, oldEntryValue);
        return true;
    }
}
