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

package org.ofbiz.core.minilang.method.callops;

import java.lang.reflect.*;

import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;

import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;


/**
 * Creates a Java object using the given fields as parameters
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    October 9th, 2002
 *@version    1.0
 */
public class CreateObject extends MethodOperation {

    String className;
    String fieldName;
    String mapName;

    /** A list of MethodObject objects to use as the method call parameters */
    List parameters;

    public CreateObject(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        className = element.getAttribute("class-name");
        fieldName = element.getAttribute("field-name");
        mapName = element.getAttribute("map-name");
        
        List parameterElements = UtilXml.childElementList(element, null);
        if (parameterElements.size() > 0) {
            parameters = new ArrayList(parameterElements.size());
            
            Iterator parameterIter = parameterElements.iterator();
            while (parameterIter.hasNext()) {
                Element parameterElement = (Element) parameterIter.next();
                MethodObject methodObject = null;
                if ("string".equals(parameterElement.getNodeName())) {
                    methodObject = new StringObject(parameterElement, simpleMethod); 
                } else if ("field".equals(parameterElement.getNodeName())) {
                    methodObject = new FieldObject(parameterElement, simpleMethod);
                } else {
                    //whoops, invalid tag here, print warning
                    Debug.logWarning("Found an unsupported tag under the call-object-method tag: " + parameterElement.getNodeName() + "; ignoring");
                }
                if (methodObject != null) {
                    parameters.add(methodObject);
                }
            }
        }
    }

    public boolean exec(MethodContext methodContext) {

        Class methodClass = null;
        try {
            methodClass = ObjectType.loadClass(className, methodContext.getLoader());
        } catch (ClassNotFoundException e) {
            Debug.logError(e, "Class to create not found with name " + className + " in create-object operation");

            String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [Class to create not found with name " + className + ": " + e.toString() + "]";
            methodContext.setErrorReturn(errMsg, simpleMethod);
            return false;
        }
        
        Object[] args = null;
        Class[] parameterTypes = null;

        if (parameters != null) {
            args = new Object[parameters.size()];
            parameterTypes = new Class[parameters.size()];

            Iterator parameterIter = parameters.iterator();
            int i = 0;
            while (parameterIter.hasNext()) {
                MethodObject methodObjectDef = (MethodObject) parameterIter.next();
                args[i] = methodObjectDef.getObject(methodContext);

                Class typeClass = methodObjectDef.getTypeClass(methodContext.getLoader());
                if (typeClass == null) {
                    String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [Parameter type not found with name " + methodObjectDef.getTypeName() + "]";
                    Debug.logError(errMsg);
                    methodContext.setErrorReturn(errMsg, simpleMethod);
                    return false;
                }

                parameterTypes[i] = typeClass;
                i++;
            }
        }
        
        try {
            Constructor constructor = methodClass.getConstructor(parameterTypes);
            try {
                Object newObject = constructor.newInstance(args);
                
                //if fieldName is empty, ignore return value
                if (UtilValidate.isNotEmpty(fieldName)) {
                    if (mapName != null && mapName.length() > 0) {
                        Map retMap = (Map) methodContext.getEnv(mapName);

                        if (retMap == null) {
                            retMap = new HashMap();
                            methodContext.putEnv(mapName, retMap);
                        }
                        retMap.put(fieldName, newObject);
                    } else {
                        // no map name, use the env
                        methodContext.putEnv(fieldName, newObject);
                    }
                }
            } catch (InstantiationException e) {
                Debug.logError(e, "Could not instantiate object in create-object operation");

                String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [Could not instantiate object: " + e.toString() + "]";
                methodContext.setErrorReturn(errMsg, simpleMethod);
                return false;
            } catch (IllegalAccessException e) {
                Debug.logError(e, "Illegal access constructing object in create-object operation");

                String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [Illegal access constructing object: " + e.toString() + "]";
                methodContext.setErrorReturn(errMsg, simpleMethod);
                return false;
            } catch (IllegalArgumentException e) {
                Debug.logError(e, "Illegal argument calling method in create-object operation");

                String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [Illegal argument calling constructor: " + e.toString() + "]";
                methodContext.setErrorReturn(errMsg, simpleMethod);
                return false;
            } catch (InvocationTargetException e) {
                Debug.logError(e.getTargetException(), "Constructor in create-object operation threw an exception");

                String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [Constructor in create-object threw an exception: " + e.getTargetException() + "]";
                methodContext.setErrorReturn(errMsg, simpleMethod);
                return false;
            }
        } catch (NoSuchMethodException e) {
            Debug.logError(e, "Could not find constructor to execute in simple-method create-object operation");
            
            String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [Could not find constructor to execute: " + e.toString() + "]";
            methodContext.setErrorReturn(errMsg, simpleMethod);
            return false;
        } catch (SecurityException e) {
            Debug.logError(e, "Security exception finding constructor to execute in simple-method create-object operation");
            
            String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [Security exception finding constructor to execute: " + e.toString() + "]";
            methodContext.setErrorReturn(errMsg, simpleMethod);
            return false;
        }
        
        return true;
    }
}
