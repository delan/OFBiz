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

package org.ofbiz.core.minilang.method.ifops;

import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;
import java.lang.reflect.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;

/**
 * Iff the validate method returns true with the specified field process sub-operations
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    February 19, 2002
 *@version    1.0
 */
public class IfValidateMethod extends MethodOperation {
    
    List subOps = new LinkedList();
    List elseSubOps = null;
    
    String mapName;
    String fieldName;
    String methodName;
    String className;

    public IfValidateMethod(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        this.mapName = element.getAttribute("map-name");
        this.fieldName = element.getAttribute("field-name");
        this.methodName = element.getAttribute("method");
        this.className = element.getAttribute("class");
        
        SimpleMethod.readOperations(element, subOps, simpleMethod);
        
        Element elseElement = UtilXml.firstChildElement(element, "else");
        if (elseElement != null) {
            elseSubOps = new LinkedList();
            SimpleMethod.readOperations(elseElement, elseSubOps, simpleMethod);
        }
    }

    public boolean exec(MethodContext methodContext) {
        //if conditions fails, always return true; if a sub-op returns false 
        // return false and stop, otherwise return true

        String fieldString = null;
        Object fieldVal = null;
        if (mapName != null && mapName.length() > 0) {
            Map fromMap = (Map) methodContext.getEnv(mapName);
            if (fromMap == null) {
                Debug.logInfo("Map not found with name " + mapName + ", using empty string for comparison");
            } else {
                fieldVal = fromMap.get(fieldName);
            }
        } else {
            //no map name, try the env
            fieldVal = methodContext.getEnv(fieldName);
        }
        
        if (fieldVal != null) {
            try {
                fieldString = (String) ObjectType.simpleTypeConvert(fieldVal, "String", null, null);
            } catch (GeneralException e) {
                Debug.logError(e, "Could not convert object to String, using empty String");
            }
        }

        //always use an empty string by default
        if (fieldString == null)
            fieldString = "";
        
        Class[] paramTypes = new Class[]{String.class};
        Object[] params = new Object[]{fieldString};

        Class valClass;
        try {
            valClass = methodContext.getLoader().loadClass(className);
        } catch (ClassNotFoundException cnfe) {
            Debug.logError("Could not find validation class: " + className);
            return false;
        }

        Method valMethod;
        try {
            valMethod = valClass.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException cnfe) {
            Debug.logError("Could not find validation method: " + methodName + " of class " + className);
            return false;
        }

        Boolean resultBool = Boolean.FALSE;
        try {
            resultBool = (Boolean) valMethod.invoke(null, params);
        } catch (Exception e) {
            Debug.logError(e, "Error in IfValidationMethod " + methodName + " of class " + className + ", not processing sub-ops ");
        }

        if (resultBool.booleanValue()) {
            return SimpleMethod.runSubOps(subOps, methodContext);
        } else {
            if (elseSubOps != null) {
                return SimpleMethod.runSubOps(elseSubOps, methodContext);
            } else {
                return true;
            }
        }
    }
}
