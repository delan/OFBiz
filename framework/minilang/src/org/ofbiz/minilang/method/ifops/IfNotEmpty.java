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
package org.ofbiz.minilang.method.ifops;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.minilang.MiniLangException;
import org.ofbiz.minilang.SimpleMethod;
import org.ofbiz.minilang.method.ContextAccessor;
import org.ofbiz.minilang.method.MethodContext;
import org.ofbiz.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * If the specified field is not empty process sub-operations
 */
public class IfNotEmpty extends MethodOperation {

    public static final String module = IfNotEmpty.class.getName();

    protected List<MethodOperation> elseSubOps = null;
    protected ContextAccessor<Object> fieldAcsr;
    protected ContextAccessor<Map<String, ? extends Object>> mapAcsr;
    protected List<MethodOperation> subOps;

    public IfNotEmpty(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        // NOTE: this is still supported, but is deprecated
        this.mapAcsr = new ContextAccessor<Map<String, ? extends Object>>(element.getAttribute("map-name"));
        this.fieldAcsr = new ContextAccessor<Object>(element.getAttribute("field"));
        if (this.fieldAcsr.isEmpty()) {
            // NOTE: this is still supported, but is deprecated
            this.fieldAcsr = new ContextAccessor<Object>(element.getAttribute("field-name"));
        }
        this.subOps = Collections.unmodifiableList(SimpleMethod.readOperations(element, simpleMethod));
        Element elseElement = UtilXml.firstChildElement(element, "else");
        if (elseElement != null) {
            this.elseSubOps = Collections.unmodifiableList(SimpleMethod.readOperations(elseElement, simpleMethod));
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        // if conditions fails, always return true; if a sub-op returns false
        // return false and stop, otherwise return true
        // return true;
        Object fieldVal = null;
        if (!mapAcsr.isEmpty()) {
            Map<String, ? extends Object> fromMap = mapAcsr.get(methodContext);
            if (fromMap == null) {
                if (Debug.verboseOn())
                    Debug.logVerbose("Map not found with name " + mapAcsr + ", not running operations", module);
            } else {
                fieldVal = fieldAcsr.get(fromMap, methodContext);
            }
        } else {
            // no map name, try the env
            fieldVal = fieldAcsr.get(methodContext);
        }
        if (fieldVal == null) {
            if (Debug.verboseOn())
                Debug.logVerbose("Field value not found with name " + fieldAcsr + " in Map with name " + mapAcsr + ", not running operations", module);
        }
        // only run subOps if element is not empty/null
        boolean runSubOps = !ObjectType.isEmpty(fieldVal);
        if (runSubOps) {
            // if (Debug.verboseOn()) Debug.logVerbose("IfNotEmpty: Running if operations mapAcsr=" + mapAcsr + " fieldAcsr=" + fieldAcsr, module);
            return SimpleMethod.runSubOps(subOps, methodContext);
        } else {
            if (elseSubOps != null) {
                // if (Debug.verboseOn()) Debug.logVerbose("IfNotEmpty: Running else operations mapAcsr=" + mapAcsr + " fieldAcsr=" + fieldAcsr, module);
                return SimpleMethod.runSubOps(elseSubOps, methodContext);
            } else {
                // if (Debug.verboseOn()) Debug.logVerbose("IfNotEmpty: Not Running any operations mapAcsr=" + mapAcsr + " fieldAcsr=" + fieldAcsr, module);
                return true;
            }
        }
    }

    @Override
    public String expandedString(MethodContext methodContext) {
        // TODO: something more than a stub/dummy
        return this.rawString();
    }

    public List<MethodOperation> getAllSubOps() {
        List<MethodOperation> allSubOps = FastList.newInstance();
        allSubOps.addAll(this.subOps);
        if (this.elseSubOps != null)
            allSubOps.addAll(this.elseSubOps);
        return allSubOps;
    }

    @Override
    public String rawString() {
        // TODO: add all attributes and other info
        return "<if-not-empty field-name=\"" + this.fieldAcsr + "\" map-name=\"" + this.mapAcsr + "\"/>";
    }

    public static final class IfNotEmptyFactory implements Factory<IfNotEmpty> {
        public IfNotEmpty createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new IfNotEmpty(element, simpleMethod);
        }

        public String getName() {
            return "if-not-empty";
        }
    }
}
