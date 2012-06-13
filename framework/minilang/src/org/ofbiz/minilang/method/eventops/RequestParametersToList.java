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
package org.ofbiz.minilang.method.eventops;

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.minilang.MiniLangException;
import org.ofbiz.minilang.MiniLangUtil;
import org.ofbiz.minilang.MiniLangValidate;
import org.ofbiz.minilang.SimpleMethod;
import org.ofbiz.minilang.method.MethodContext;
import org.ofbiz.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;request-parameters-to-list&gt; element.
 */
public final class RequestParametersToList extends MethodOperation {

    // This method is needed only during the v1 to v2 transition
    private static boolean autoCorrect(Element element) {
        // Correct deprecated list-name attribute
        String listAttr = element.getAttribute("list-name");
        if (listAttr.length() > 0) {
            element.setAttribute("list", listAttr);
            element.removeAttribute("list-name");
            return true;
        }
        return false;
    }

    private final FlexibleMapAccessor<List<String>> listFma;
    private final FlexibleStringExpander attributeNameFse;

    public RequestParametersToList(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.deprecatedAttribute(simpleMethod, element, "list-name", "replace with \"list\"");
            MiniLangValidate.attributeNames(simpleMethod, element, "list", "request-name");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "request-name");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "list");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        boolean elementModified = autoCorrect(element);
        if (elementModified && MiniLangUtil.autoCorrectOn()) {
            MiniLangUtil.flagDocumentAsCorrected(element);
        }
        this.attributeNameFse = FlexibleStringExpander.getInstance(element.getAttribute("request-name"));
        String attributeName = element.getAttribute("list");
        if (!attributeName.isEmpty()) {
            this.listFma = FlexibleMapAccessor.getInstance(attributeName);
        } else {
            this.listFma = FlexibleMapAccessor.getInstance(attributeNameFse.toString());
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        if (methodContext.getMethodType() == MethodContext.EVENT) {
            String attributeName = attributeNameFse.expandString(methodContext.getEnvMap());
            String[] parameterValues = methodContext.getRequest().getParameterValues(attributeName);
            if (parameterValues != null) {
                List<String> valueList = listFma.get(methodContext.getEnvMap());
                if (valueList == null) {
                    valueList = FastList.newInstance();
                    listFma.put(methodContext.getEnvMap(), valueList);
                }
                for (int i = 0; i < parameterValues.length; i++) {
                    valueList.add(parameterValues[i]);
                }
            }
        }
        return true;
    }

    @Override
    public String expandedString(MethodContext methodContext) {
        return FlexibleStringExpander.expandString(toString(), methodContext.getEnvMap());
    }

    @Override
    public String rawString() {
        return toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<request-parameters-to-list ");
        sb.append("request-name=\"").append(this.attributeNameFse).append("\" ");
        if (!this.listFma.isEmpty()) {
            sb.append("list=\"").append(this.listFma).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;request-parameters-to-list&gt; element.
     */
    public static final class RequestParametersToListFactory implements Factory<RequestParametersToList> {
        @Override
        public RequestParametersToList createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new RequestParametersToList(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "request-parameters-to-list";
        }
    }
}
