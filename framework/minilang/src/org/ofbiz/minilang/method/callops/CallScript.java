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

import org.ofbiz.base.util.ScriptUtil;
import org.ofbiz.minilang.MiniLangException;
import org.ofbiz.minilang.SimpleMethod;
import org.ofbiz.minilang.method.ContextAccessor;
import org.ofbiz.minilang.method.MethodContext;
import org.ofbiz.minilang.method.MethodOperation;
import org.w3c.dom.Element;

public class CallScript extends MethodOperation {

    public static final String module = CallScript.class.getName();

    private static String getScriptLocation(String combinedName) {
        int pos = combinedName.lastIndexOf("#");
        if (pos == -1) {
            return combinedName;
        }
        return combinedName.substring(0, pos);
    }

    private static String getScriptMethodName(String combinedName) {
        int pos = combinedName.lastIndexOf("#");
        if (pos == -1) {
            return null;
        }
        return combinedName.substring(pos + 1);
    }

    private ContextAccessor<List<Object>> errorListAcsr;
    private String location;
    private String method;

    public CallScript(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        String scriptLocation = element.getAttribute("location");
        this.location = getScriptLocation(scriptLocation);
        this.method = getScriptMethodName(scriptLocation);
        this.errorListAcsr = new ContextAccessor<List<Object>>(element.getAttribute("error-list-name"), "error_list");
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        String location = methodContext.expandString(this.location);
        String method = methodContext.expandString(this.method);
        List<Object> messages = errorListAcsr.get(methodContext);
        if (messages == null) {
            messages = FastList.newInstance();
            errorListAcsr.put(methodContext, messages);
        }
        Map<String, Object> context = methodContext.getEnvMap();
        if (location.endsWith(".xml")) {
            try {
                SimpleMethod.runSimpleMethod(location, method, methodContext);
            } catch (MiniLangException e) {
                messages.add("Error running simple method at location [" + location + "]: " + e.getMessage());
            }
        } else {
            ScriptUtil.executeScript(this.location, this.method, context);
        }
        // update the method environment
        methodContext.putAllEnv(context);
        // always return true, error messages just go on the error list
        return true;
    }

    @Override
    public String expandedString(MethodContext methodContext) {
        return rawString();
    }

    @Override
    public String rawString() {
        return "<script/>";
    }

    public static final class CallScriptFactory implements Factory<CallScript> {
        public CallScript createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CallScript(element, simpleMethod);
        }

        public String getName() {
            return "script";
        }
    }
}
