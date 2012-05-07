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
package org.ofbiz.minilang.method.envops;

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.minilang.MiniLangException;
import org.ofbiz.minilang.MiniLangValidate;
import org.ofbiz.minilang.SimpleMethod;
import org.ofbiz.minilang.method.MethodContext;
import org.ofbiz.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Adds an error message to an error message list.
 */
public final class AddError extends MethodOperation {

    private final FlexibleMapAccessor<List<String>> errorListFma;
    private final FlexibleStringExpander messageFse;
    private final String propertykey;
    private final String propertyResource;

    public AddError(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "error-list-name");
            MiniLangValidate.constantAttributes(simpleMethod, element, "error-list-name");
            MiniLangValidate.childElements(simpleMethod, element, "fail-message", "fail-property");
            MiniLangValidate.requireAnyChildElement(simpleMethod, element, "fail-message", "fail-property");
        }
        errorListFma = FlexibleMapAccessor.getInstance(MiniLangValidate.checkAttribute(element.getAttribute("error-list-name"), "error_list"));
        Element childElement = UtilXml.firstChildElement(element, "fail-message");
        if (childElement != null) {
            if (MiniLangValidate.validationOn()) {
                MiniLangValidate.attributeNames(simpleMethod, childElement, "message");
                MiniLangValidate.requiredAttributes(simpleMethod, childElement, "message");
                MiniLangValidate.constantPlusExpressionAttributes(simpleMethod, childElement, "message");
            }
            this.messageFse = FlexibleStringExpander.getInstance(childElement.getAttribute("message"));
            this.propertykey = null;
            this.propertyResource = null;
        } else {
            childElement = UtilXml.firstChildElement(element, "fail-property");
            if (childElement != null) {
                if (MiniLangValidate.validationOn()) {
                    MiniLangValidate.attributeNames(simpleMethod, childElement, "property", "resource");
                    MiniLangValidate.requiredAttributes(simpleMethod, childElement, "property", "resource");
                    MiniLangValidate.constantAttributes(simpleMethod, childElement, "property", "resource");
                }
                this.messageFse = FlexibleStringExpander.getInstance(null);
                this.propertykey = childElement.getAttribute("property");
                this.propertyResource = childElement.getAttribute("resource");
            } else {
                this.messageFse = FlexibleStringExpander.getInstance(null);
                this.propertykey = null;
                this.propertyResource = null;
            }
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        String message = null;
        if (!this.messageFse.isEmpty()) {
            message = this.messageFse.expandString(methodContext.getEnvMap());
        } else if (this.propertyResource != null) {
            message = UtilProperties.getMessage(this.propertyResource, this.propertykey, methodContext.getEnvMap(), methodContext.getLocale());
        }
        if (message != null) {
            List<String> messages = errorListFma.get(methodContext.getEnvMap());
            if (messages == null) {
                messages = FastList.newInstance();
            }
            errorListFma.put(methodContext.getEnvMap(), messages);
            messages.add(message);
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
        StringBuilder sb = new StringBuilder("<add-error ");
        if (!"error_list".equals(this.errorListFma.getOriginalName())) {
            sb.append("error-list-name=\"").append(this.errorListFma).append("\"");
        }
        sb.append(">");
        if (!this.messageFse.isEmpty()) {
            sb.append("<fail-message message=\"").append(this.messageFse).append("\" />");
        }
        if (this.propertykey != null) {
            sb.append("<fail-property property=\"").append(this.propertykey).append(" resource=\"").append(this.propertyResource).append("\" />");
        }
        sb.append("</add-error>");
        return sb.toString();
    }

    public static final class AddErrorFactory implements Factory<AddError> {
        public AddError createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new AddError(element, simpleMethod);
        }

        public String getName() {
            return "add-error";
        }
    }
}
