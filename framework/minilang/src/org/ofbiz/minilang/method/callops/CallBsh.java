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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.minilang.MiniLangException;
import org.ofbiz.minilang.SimpleMethod;
import org.ofbiz.minilang.method.ContextAccessor;
import org.ofbiz.minilang.method.MethodContext;
import org.ofbiz.minilang.method.MethodOperation;
import org.w3c.dom.Element;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Simple class to wrap messages that come either from a straight string or a properties file
 */
public class CallBsh extends MethodOperation {

    public static final String module = CallBsh.class.getName();
    public static final int bufferLength = 4096;

    ContextAccessor<List<Object>> errorListAcsr;
    String inline = null;
    String resource = null;

    public CallBsh(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        inline = UtilXml.elementValue(element);
        resource = element.getAttribute("resource");
        errorListAcsr = new ContextAccessor<List<Object>>(element.getAttribute("error-list-name"), "error_list");
        if (UtilValidate.isNotEmpty(inline)) {
            // pre-parse/compile inlined bsh, only accessed here
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        List<Object> messages = errorListAcsr.get(methodContext);
        if (messages == null) {
            messages = FastList.newInstance();
            errorListAcsr.put(methodContext, messages);
        }
        Interpreter bsh = new Interpreter();
        bsh.setClassLoader(methodContext.getLoader());
        try {
            // setup environment
            for (Map.Entry<String, Object> entry : methodContext) {
                bsh.set(entry.getKey(), entry.getValue());
            }
            // run external, from resource, first if resource specified
            if (UtilValidate.isNotEmpty(resource)) {
                String resource = methodContext.expandString(this.resource);
                InputStream is = methodContext.getLoader().getResourceAsStream(resource);

                if (is == null) {
                    messages.add("Could not find bsh resource: " + resource);
                } else {
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new InputStreamReader(is));
                        StringBuilder outSb = new StringBuilder();
                        String tempStr = null;
                        while ((tempStr = reader.readLine()) != null) {
                            outSb.append(tempStr);
                            outSb.append('\n');
                        }
                        Object resourceResult = bsh.eval(outSb.toString());
                        // if map is returned, copy values into env
                        if ((resourceResult != null) && (resourceResult instanceof Map<?, ?>)) {
                            methodContext.putAllEnv(UtilGenerics.<String, Object> checkMap(resourceResult));
                        }
                    } catch (IOException e) {
                        messages.add("IO error loading bsh resource: " + e.getMessage());
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                messages.add("IO error closing BufferedReader: " + e.getMessage());
                            }
                        }
                    }
                }
            }
            if (Debug.verboseOn())
                Debug.logVerbose("Running inline BSH script: " + inline, module);
            // run inlined second to it can override the one from the property
            Object inlineResult = bsh.eval(inline);
            if (Debug.verboseOn())
                Debug.logVerbose("Result of inline BSH script: " + inlineResult, module);
            // if map is returned, copy values into env
            if ((inlineResult != null) && (inlineResult instanceof Map<?, ?>)) {
                methodContext.putAllEnv(UtilGenerics.<String, Object> checkMap(inlineResult));
            }
        } catch (EvalError e) {
            Debug.logError(e, "BeanShell execution caused an error", module);
            messages.add("BeanShell execution caused an error: " + e.getMessage());
        }
        // always return true, error messages just go on the error list
        return true;
    }

    @Override
    public String expandedString(MethodContext methodContext) {
        // TODO: something more than a stub/dummy
        return this.rawString();
    }

    @Override
    public String rawString() {
        // TODO: something more than the empty tag
        return "<call-bsh/>";
    }

    public static final class CallBshFactory implements Factory<CallBsh> {
        public CallBsh createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CallBsh(element, simpleMethod);
        }

        public String getName() {
            return "call-bsh";
        }
    }
}
