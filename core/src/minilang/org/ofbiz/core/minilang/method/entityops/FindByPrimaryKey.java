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
import org.ofbiz.core.entity.*;

/**
 * Uses the delegator to find an entity value by its primary key
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class FindByPrimaryKey extends MethodOperation {
    
    String valueName;
    String entityName;
    String mapName;
    String delegatorName;
    boolean useCache;

    public FindByPrimaryKey(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        valueName = element.getAttribute("value-name");
        entityName = element.getAttribute("entity-name");
        mapName = element.getAttribute("map-name");
        delegatorName = element.getAttribute("delegator-name");

        useCache = "true".equals(element.getAttribute("use-cache"));
    }

    public boolean exec(MethodContext methodContext) {
        GenericDelegator delegator = methodContext.getDelegator();
        if (delegatorName != null && delegatorName.length() > 0) {
            delegator = GenericDelegator.getGenericDelegator(delegatorName);
        }

        try {
            if (this.useCache) {
                methodContext.putEnv(valueName, delegator.findByPrimaryKeyCache(entityName, (Map) methodContext.getEnv(mapName)));
            } else {
                methodContext.putEnv(valueName, delegator.findByPrimaryKey(entityName, (Map) methodContext.getEnv(mapName)));
            }
        } catch (GenericEntityException e) {
            Debug.logError(e);
            String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [problem finding the " + entityName + " entity: " + e.getMessage() + "]";
            methodContext.setErrorReturn(errMsg, simpleMethod);
            return false;
        }
        return true;
    }
}
