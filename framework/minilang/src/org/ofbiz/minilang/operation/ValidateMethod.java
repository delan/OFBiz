/*
 * Copyright 2001-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.minilang.operation;

import java.util.*;
import java.lang.reflect.*;

import org.w3c.dom.*;

import org.ofbiz.base.util.*;

/**
 * A string operation that calls a validation method
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      2.0
 */
public class ValidateMethod extends SimpleMapOperation {
    
    public static final String module = ValidateMethod.class.getName();
    
    String methodName;
    String className;

    public ValidateMethod(Element element, SimpleMapProcess simpleMapProcess) {
        super(element, simpleMapProcess);
        this.methodName = element.getAttribute("method");
        this.className = element.getAttribute("class");
    }

    public void exec(Map inMap, Map results, List messages, Locale locale, ClassLoader loader) {
        Object obj = inMap.get(fieldName);

        String fieldValue = null;

        try {
            fieldValue = (String) ObjectType.simpleTypeConvert(obj, "String", null, locale);
        } catch (GeneralException e) {
            messages.add("Could not convert field value for comparison: " + e.getMessage());
            return;
        }

        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }

        Class[] paramTypes = new Class[] {String.class};
        Object[] params = new Object[] {fieldValue};

        Class valClass;

        try {
            valClass = loader.loadClass(className);
        } catch (ClassNotFoundException cnfe) {
            String msg = "Could not find validation class: " + className;

            messages.add(msg);
            Debug.logError("[ValidateMethod.exec] " + msg, module);
            return;
        }

        Method valMethod;

        try {
            valMethod = valClass.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException cnfe) {
            String msg = "Could not find validation method: " + methodName + " of class " + className;

            messages.add(msg);
            Debug.logError("[ValidateMethod.exec] " + msg, module);
            return;
        }

        Boolean resultBool = Boolean.FALSE;

        try {
            resultBool = (Boolean) valMethod.invoke(null, params);
        } catch (Exception e) {
            String msg = "Error in validation method " + methodName + " of class " + className + ": " + e.getMessage();

            messages.add(msg);
            Debug.logError("[ValidateMethod.exec] " + msg, module);
            return;
        }

        if (!resultBool.booleanValue()) {
            addMessage(messages, loader, locale);
        }
    }
}
