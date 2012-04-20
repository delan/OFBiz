/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/

package org.ofbiz.minilang.method.callops;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.minilang.MiniLangException;
import org.ofbiz.minilang.SimpleMethod;
import org.ofbiz.minilang.method.ContextAccessor;
import org.ofbiz.minilang.method.FieldObject;
import org.ofbiz.minilang.method.MethodContext;
import org.ofbiz.minilang.method.MethodObject;
import org.ofbiz.minilang.method.MethodOperation;
import org.ofbiz.minilang.method.StringObject;
import org.w3c.dom.Element;

/**
 * Calls a Java class method using the given fields as parameters
 */
public class CallClassMethod extends MethodOperation {

    public static final String module = CallClassMethod.class.getName();

    String className;
    String methodName;
    /** A list of MethodObject objects to use as the method call parameters */
    List<MethodObject<?>> parameters;
    ContextAccessor<Object> retFieldAcsr;
    ContextAccessor<Map<String, Object>> retMapAcsr;

    public CallClassMethod(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        className = element.getAttribute("class-name");
        methodName = element.getAttribute("method-name");
        // the schema for this element now just has the "ret-field" attribute, though the
        // old "ret-field-name" and "ret-map-name" pair is still supported
        retFieldAcsr = new ContextAccessor<Object>(element.getAttribute("ret-field"), element.getAttribute("ret-field-name"));
        retMapAcsr = new ContextAccessor<Map<String, Object>>(element.getAttribute("ret-map-name"));
        List<? extends Element> parameterElements = UtilXml.childElementList(element);
        if (parameterElements.size() > 0) {
            parameters = FastList.newInstance();
            for (Element parameterElement : parameterElements) {
                MethodObject<?> methodObject = null;
                if ("string".equals(parameterElement.getNodeName())) {
                    methodObject = new StringObject(parameterElement, simpleMethod);
                } else if ("field".equals(parameterElement.getNodeName())) {
                    methodObject = new FieldObject<Object>(parameterElement, simpleMethod);
                } else {
                    // whoops, invalid tag here, print warning
                    Debug.logWarning("Found an unsupported tag under the call-object-method tag: " + parameterElement.getNodeName() + "; ignoring", module);
                }
                if (methodObject != null) {
                    parameters.add(methodObject);
                }
            }
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        String className = methodContext.expandString(this.className);
        String methodName = methodContext.expandString(this.methodName);
        Class<?> methodClass = null;
        try {
            methodClass = ObjectType.loadClass(className, methodContext.getLoader());
        } catch (ClassNotFoundException e) {
            Debug.logError(e, "Class to create not found with name " + className + " in create-object operation", module);

            String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [Class to create not found with name " + className + ": " + e.toString() + "]";
            methodContext.setErrorReturn(errMsg, simpleMethod);
            return false;
        }
        return CallObjectMethod.callMethod(simpleMethod, methodContext, parameters, methodClass, null, methodName, retFieldAcsr, retMapAcsr);
    }

    @Override
    public String expandedString(MethodContext methodContext) {
        // TODO: something more than a stub/dummy
        return this.rawString();
    }

    @Override
    public String rawString() {
        // TODO: something more than the empty tag
        return "<call-class-method/>";
    }

    public static final class CallClassMethodFactory implements Factory<CallClassMethod> {
        public CallClassMethod createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CallClassMethod(element, simpleMethod);
        }

        public String getName() {
            return "call-class-method";
        }
    }
}
