/*
 * $Id: ValidateMethodCondition.java 5462 2005-08-05 18:35:48Z jonesde $
 *
 *  Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.minilang.method.conditional;

import java.util.*;
import java.lang.reflect.*;
import org.w3c.dom.*;
import org.ofbiz.base.util.*;
import org.ofbiz.minilang.method.*;

/**
 * Implements validate method condition.
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      2.1
 */
public class ValidateMethodCondition implements Conditional {
    
    public static final String module = ValidateMethodCondition.class.getName();
    
    ContextAccessor mapAcsr;
    ContextAccessor fieldAcsr;
    String methodName;
    String className;
    
    public ValidateMethodCondition(Element element) {
        this.mapAcsr = new ContextAccessor(element.getAttribute("map-name"));
        this.fieldAcsr = new ContextAccessor(element.getAttribute("field-name"));
        this.methodName = element.getAttribute("method");
        this.className = element.getAttribute("class");
    }

    public boolean checkCondition(MethodContext methodContext) {
        String methodName = methodContext.expandString(this.methodName);
        String className = methodContext.expandString(this.className);

        String fieldString = getFieldString(methodContext);

        Class[] paramTypes = new Class[] {String.class};
        Object[] params = new Object[] {fieldString};

        Class valClass;
        try {
            valClass = methodContext.getLoader().loadClass(className);
        } catch (ClassNotFoundException cnfe) {
            Debug.logError("Could not find validation class: " + className, module);
            return false;
        }

        Method valMethod;
        try {
            valMethod = valClass.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException cnfe) {
            Debug.logError("Could not find validation method: " + methodName + " of class " + className, module);
            return false;
        }

        Boolean resultBool = Boolean.FALSE;
        try {
            resultBool = (Boolean) valMethod.invoke(null, params);
        } catch (Exception e) {
            Debug.logError(e, "Error in IfValidationMethod " + methodName + " of class " + className + ", not processing sub-ops ", module);
        }
        
        if (resultBool != null) return resultBool.booleanValue();
        
        return false;
    }
    
    protected String getFieldString(MethodContext methodContext) {
        String fieldString = null;
        Object fieldVal = null;

        if (!mapAcsr.isEmpty()) {
            Map fromMap = (Map) mapAcsr.get(methodContext);
            if (fromMap == null) {
                if (Debug.infoOn()) Debug.logInfo("Map not found with name " + mapAcsr + ", using empty string for comparison", module);
            } else {
                fieldVal = fieldAcsr.get(fromMap, methodContext);
            }
        } else {
            // no map name, try the env
            fieldVal = fieldAcsr.get(methodContext);
        }

        if (fieldVal != null) {
            try {
                fieldString = (String) ObjectType.simpleTypeConvert(fieldVal, "String", null, null);
            } catch (GeneralException e) {
                Debug.logError(e, "Could not convert object to String, using empty String", module);
            }
        }

        // always use an empty string by default
        if (fieldString == null) fieldString = "";

        return fieldString;
    }

    public void prettyPrint(StringBuffer messageBuffer, MethodContext methodContext) {
        // allow methodContext to be null
        String methodName = methodContext == null ? this.methodName : methodContext.expandString(this.methodName);
        String className = methodContext == null ? this.className : methodContext.expandString(this.className);

        messageBuffer.append("validate-method[");
        messageBuffer.append(className);
        messageBuffer.append(".");
        messageBuffer.append(methodName);
        messageBuffer.append("(");
        if (!this.mapAcsr.isEmpty()) {
            messageBuffer.append(this.mapAcsr);
            messageBuffer.append(".");
        }
        messageBuffer.append(this.fieldAcsr);
        if (methodContext != null) {
            messageBuffer.append("=");
            messageBuffer.append(getFieldString(methodContext));
        }
        messageBuffer.append(")]");
    }
}
