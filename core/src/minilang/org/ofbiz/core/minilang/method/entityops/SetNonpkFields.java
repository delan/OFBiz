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

package org.ofbiz.core.minilang.method.entityops;

import java.net.*;
import java.text.*;
import java.util.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;
import org.ofbiz.core.entity.*;

/**
 * Looks for each non-PK field in the named map and if it exists there it will copy it into the named value object.
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    February 19, 2002
 *@version    1.0
 */
public class SetNonpkFields extends MethodOperation {
    String valueName;
    String mapName;
    boolean setIfNull = true;

    public SetNonpkFields(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        valueName = element.getAttribute("value-name");
        mapName = element.getAttribute("map-name");

        //if anything but false it will be true
        setIfNull = !"false".equals(element.getAttribute("set-if-null"));
    }

    public boolean exec(MethodContext methodContext) {
        GenericValue value = (GenericValue) methodContext.getEnv(valueName);
        
        if (value == null) {
            String errMsg = "In set-nonpk-fields a value was not found with the specified valueName: " + valueName + ", not setting fields";
            Debug.logWarning(errMsg);
            if (methodContext.getMethodType() == MethodContext.EVENT) {
                methodContext.putEnv(simpleMethod.getEventErrorMessageName(), errMsg);
                methodContext.putEnv(simpleMethod.getEventResponseCodeName(), simpleMethod.getDefaultErrorCode());
            } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
                methodContext.putEnv(simpleMethod.getServiceErrorMessageName(), errMsg);
                methodContext.putEnv(simpleMethod.getServiceResponseMessageName(), simpleMethod.getDefaultErrorCode());
            }
            return false;
        }

        Map theMap = (Map) methodContext.getEnv(mapName);
        if (theMap == null) {
            Debug.logWarning("In set-nonpk-fields could not find map with name " + mapName + ", not setting any fields");
        } else {
            value.setNonPKFields(theMap, setIfNull);
        }
        return true;
    }
}
