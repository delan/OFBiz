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

public class IfInstanceOf extends MethodOperation {

    public static final String module = IfInstanceOf.class.getName();

    protected String className = null;
    protected List<MethodOperation> elseSubOps = null;
    protected ContextAccessor<Object> fieldAcsr = null;
    protected ContextAccessor<Map<String, ? extends Object>> mapAcsr = null;
    protected List<MethodOperation> subOps;

    public IfInstanceOf(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        // the schema for this element now just has the "field" attribute, though the old "field-name" and "map-name" pair is still supported
        this.fieldAcsr = new ContextAccessor<Object>(element.getAttribute("field"), element.getAttribute("field-name"));
        this.mapAcsr = new ContextAccessor<Map<String, ? extends Object>>(element.getAttribute("map-name"));
        this.className = element.getAttribute("class");
        this.subOps = Collections.unmodifiableList(SimpleMethod.readOperations(element, simpleMethod));
        Element elseElement = UtilXml.firstChildElement(element, "else");
        if (elseElement != null) {
            this.elseSubOps = Collections.unmodifiableList(SimpleMethod.readOperations(elseElement, simpleMethod));
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        // only run subOps if element is instanceOf
        boolean runSubOps = false;
        Object fieldVal = null;
        if (!mapAcsr.isEmpty()) {
            Map<String, ? extends Object> fromMap = mapAcsr.get(methodContext);
            if (fromMap == null) {
                if (Debug.infoOn())
                    Debug.logInfo("Map not found with name " + mapAcsr + ", running operations", module);
            } else {
                fieldVal = fieldAcsr.get(fromMap, methodContext);
            }
        } else {
            // no map name, try the env
            fieldVal = fieldAcsr.get(methodContext);
        }
        runSubOps = ObjectType.instanceOf(fieldVal, className);
        if (runSubOps) {
            return SimpleMethod.runSubOps(subOps, methodContext);
        } else {
            if (elseSubOps != null) {
                return SimpleMethod.runSubOps(elseSubOps, methodContext);
            } else {
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
        return "<if-instance-of field-name=\"" + this.fieldAcsr + "\" map-name=\"" + this.mapAcsr + "\"/>";
    }

    public static final class IfInstanceOfFactory implements Factory<IfInstanceOf> {
        public IfInstanceOf createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new IfInstanceOf(element, simpleMethod);
        }

        public String getName() {
            return "if-instance-of";
        }
    }
}
