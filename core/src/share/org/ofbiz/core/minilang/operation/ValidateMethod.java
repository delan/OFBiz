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

package org.ofbiz.core.minilang.operation;

import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;
import java.lang.reflect.*;

import org.w3c.dom.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.service.*;

import org.ofbiz.core.minilang.*;

/**
 * A string operation that calls a validation method
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    December 29, 2001
 *@version    1.0
 */
public class ValidateMethod extends SimpleMapOperation {
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

        Class[] paramTypes = new Class[]{String.class};
        Object[] params = new Object[]{fieldValue};

        Class valClass;
        try {
            valClass = loader.loadClass(className);
        } catch (ClassNotFoundException cnfe) {
            String msg = "Could not find validation class: " + className;
            messages.add(msg);
            Debug.logError("[ValidateMethod.exec] " + msg);
            return;
        }

        Method valMethod;
        try {
            valMethod = valClass.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException cnfe) {
            String msg = "Could not find validation method: " + methodName + " of class " + className;
            messages.add(msg);
            Debug.logError("[ValidateMethod.exec] " + msg);
            return;
        }

        Boolean resultBool = Boolean.FALSE;
        try {
            resultBool = (Boolean) valMethod.invoke(null, params);
        } catch (Exception e) {
            String msg = "Error in validation method " + methodName + " of class " + className + ": " + e.getMessage();
            messages.add(msg);
            Debug.logError("[ValidateMethod.exec] " + msg);
            return;
        }

        if (!resultBool.booleanValue()) {
            addMessage(messages, loader);
        }
    }
}
