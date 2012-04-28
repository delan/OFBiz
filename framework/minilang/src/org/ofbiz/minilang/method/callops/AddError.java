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

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.minilang.MiniLangException;
import org.ofbiz.minilang.MiniLangUtil;
import org.ofbiz.minilang.MiniLangValidate;
import org.ofbiz.minilang.SimpleMethod;
import org.ofbiz.minilang.method.MethodContext;
import org.ofbiz.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Adds an error message to the error message list.
 */
public final class AddError extends MethodOperation {

    // This method is needed only during the v1 to v2 transition
    private static boolean autoCorrect(Element element) {
        String errorListAttr = element.getAttribute("error-list-name");
        if (errorListAttr.length() > 0) {
            element.removeAttribute("error-list-name");
            return true;
        }
        return false;
    }
    
    private final FlexibleStringExpander messageFse;
    private final String propertykey;
    private final String propertyResource;

    public AddError(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.childElements(simpleMethod, element, "fail-message", "fail-property");
            MiniLangValidate.requireAnyChildElement(simpleMethod, element, "fail-message", "fail-property");
        }
        boolean elementModified = autoCorrect(element);
        if (elementModified && MiniLangUtil.autoCorrectOn()) {
            MiniLangUtil.flagDocumentAsCorrected(element);
        }
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
            List<String> messages = null;
            if (methodContext.getMethodType() == MethodContext.EVENT) {
                messages = methodContext.getEnv(this.simpleMethod.getEventErrorMessageListName());
                if (messages == null) {
                    messages = FastList.newInstance();
                    methodContext.putEnv(this.simpleMethod.getEventErrorMessageListName(), messages);
                }
            } else {
                messages = methodContext.getEnv(this.simpleMethod.getServiceErrorMessageListName());
                if (messages == null) {
                    messages = FastList.newInstance();
                    methodContext.putEnv(this.simpleMethod.getServiceErrorMessageListName(), messages);
                }
            }
            messages.add(message);
            // TODO: Remove this line after tests are fixed
            Debug.logInfo("<add-error> message = " + message + ", location = " + this.simpleMethod.getLocationAndName() + ", line = " + this.getLineNumber(), this.getClass().getName());
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
        return "<add-error/>";
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
