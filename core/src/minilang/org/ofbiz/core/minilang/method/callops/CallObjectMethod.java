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
 * Calls a Java object method using the given fields as parameters
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    October 9th, 2002
 *@version    1.0
 */
public class CallObjectMethod extends MethodOperation {
    
    public static final String module = CallClassMethod.class.getName();

    String objFieldName;
    String objMapName;
    String methodName;
    String retFieldName;
    String retMapName;

    /** A list of MethodObject objects to use as the method call parameters */
    List parameters;

    public CallObjectMethod(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        objFieldName = element.getAttribute("obj-field-name");
        objMapName = element.getAttribute("obj-map-name");
        methodName = element.getAttribute("method-name");
        retFieldName = element.getAttribute("ret-field-name");
        retMapName = element.getAttribute("ret-map-name");
        
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
                    Debug.logWarning("Found an unsupported tag under the call-object-method tag: " + parameterElement.getNodeName() + "; ignoring", module);
                }
                if (methodObject != null) {
                    parameters.add(methodObject);
                }
            }
        }
    }

    public boolean exec(MethodContext methodContext) {

        Object methodObject = null;

        if (objMapName != null && objMapName.length() > 0) {
            Map fromMap = (Map) methodContext.getEnv(objMapName);

            if (fromMap == null) {
                Debug.logWarning("Map not found with name " + objMapName + ", which should contain the object to execute a method on; not executing method, rerturning error.", module);
                
                String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [Map not found with name " + objMapName + ", which should contain the object to execute a method on]";
                methodContext.setErrorReturn(errMsg, simpleMethod);
                return false;
            }

            methodObject = fromMap.get(objFieldName);
        } else {
            // no map name, try the env
            methodObject = methodContext.getEnv(objFieldName);
        }

        if (methodObject == null) {
            if (Debug.infoOn()) Debug.logInfo("Object not found to execute method on with name " + objFieldName + " in Map with name " + objMapName + ", not executing method, rerturning error.", module);
            
            String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [Object not found to execute method on with name " + objFieldName + " in Map with name " + objMapName + "]";
            methodContext.setErrorReturn(errMsg, simpleMethod);
            return false;
        }

        Class methodClass = methodObject.getClass();
        return CallObjectMethod.callMethod(simpleMethod, methodContext, parameters, methodClass, methodObject, methodName, retFieldName, retMapName);
    }
    
    public static boolean callMethod(SimpleMethod simpleMethod, MethodContext methodContext, List parameters, Class methodClass, Object methodObject, String methodName, String retFieldName, String retMapName) {
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
                    Debug.logError(errMsg, module);
                    methodContext.setErrorReturn(errMsg, simpleMethod);
                    return false;
                }

                parameterTypes[i] = typeClass;
                i++;
            }
        }
        
        try {
            Method method = methodClass.getMethod(methodName, parameterTypes);
            try {
                Object retValue = method.invoke(methodObject, args);
                
                //if retFieldName is empty, ignore return value
                if (UtilValidate.isNotEmpty(retFieldName)) {
                    if (retMapName != null && retMapName.length() > 0) {
                        Map retMap = (Map) methodContext.getEnv(retMapName);

                        if (retMap == null) {
                            retMap = new HashMap();
                            methodContext.putEnv(retMapName, retMap);
                        }
                        retMap.put(retFieldName, retValue);
                    } else {
                        // no map name, use the env
                        methodContext.putEnv(retFieldName, retValue);
                    }
                }
                
            } catch (IllegalAccessException e) {
                Debug.logError(e, "Could not access method in call method operation", module);
                String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [Could not access method to execute named " + methodName + ": " + e.toString() + "]";
                methodContext.setErrorReturn(errMsg, simpleMethod);
                return false;
            } catch (IllegalArgumentException e) {
                Debug.logError(e, "Illegal argument calling method in call method operation", module);
                String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [Illegal argument calling method to execute named " + methodName + ": " + e.toString() + "]";
                methodContext.setErrorReturn(errMsg, simpleMethod);
                return false;
            } catch (InvocationTargetException e) {
                Debug.logError(e.getTargetException(), "Method in call method operation threw an exception", module);
                String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [Method to execute named " + methodName + " threw an exception: " + e.getTargetException() + "]";
                methodContext.setErrorReturn(errMsg, simpleMethod);
                return false;
            }
        } catch (NoSuchMethodException e) {
            Debug.logError(e, "Could not find method to execute in simple-method call method operation", module);            
            String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [Could not find method to execute named " + methodName + ": " + e.toString() + "]";
            methodContext.setErrorReturn(errMsg, simpleMethod);
            return false;
        } catch (SecurityException e) {
            Debug.logError(e, "Security exception finding method to execute in simple-method call method operation", module);            
            String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [Security exception finding method to execute named " + methodName + ": " + e.toString() + "]";
            methodContext.setErrorReturn(errMsg, simpleMethod);
            return false;
        }
        
        return true;
    }
}
