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
package org.ofbiz.minilang.method.entityops;

import java.util.Collection;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.minilang.MiniLangException;
import org.ofbiz.minilang.MiniLangRuntimeException;
import org.ofbiz.minilang.MiniLangValidate;
import org.ofbiz.minilang.SimpleMethod;
import org.ofbiz.minilang.artifact.ArtifactInfoContext;
import org.ofbiz.minilang.method.MethodContext;
import org.ofbiz.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;find-by-primary-key&gt; element.
 */
public final class FindByPrimaryKey extends MethodOperation {

    public static final String module = FindByPrimaryKey.class.getName();

    private final FlexibleStringExpander delegatorNameFse;
    private final FlexibleStringExpander entityNameFse;
    private final FlexibleMapAccessor<Collection<String>> fieldsToSelectListFma;
    private final FlexibleMapAccessor<Map<String, ? extends Object>> mapFma;
    private final FlexibleStringExpander useCacheFse;
    private final FlexibleMapAccessor<GenericValue> valueFma;

    public FindByPrimaryKey(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "entity-name", "use-cache", "fields-to-select-list", "map", "value-field", "delegator-name");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "value-field", "map");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "value-field", "map", "fields-to-select-list");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        valueFma = FlexibleMapAccessor.getInstance(element.getAttribute("value-field"));
        entityNameFse = FlexibleStringExpander.getInstance(element.getAttribute("entity-name"));
        mapFma = FlexibleMapAccessor.getInstance(element.getAttribute("map"));
        fieldsToSelectListFma = FlexibleMapAccessor.getInstance(element.getAttribute("fields-to-select-list"));
        delegatorNameFse = FlexibleStringExpander.getInstance(element.getAttribute("delegator-name"));
        useCacheFse = FlexibleStringExpander.getInstance(element.getAttribute("use-cache"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        String entityName = entityNameFse.expandString(methodContext.getEnvMap());
        boolean useCache = "true".equals(useCacheFse.expandString(methodContext.getEnvMap()));
        Delegator delegator = methodContext.getDelegator();
        String delegatorName = delegatorNameFse.expandString(methodContext.getEnvMap());
        if (!delegatorName.isEmpty()) {
            delegator = DelegatorFactory.getDelegator(delegatorName);
        }
        Map<String, ? extends Object> inMap = mapFma.get(methodContext.getEnvMap());
        if (inMap == null) {
            throw new MiniLangRuntimeException("Primary key map \"" + mapFma + "\" not found", this);
        }
        if (entityName.isEmpty() && inMap instanceof GenericEntity) {
            GenericEntity inEntity = (GenericEntity) inMap;
            entityName = inEntity.getEntityName();
        }
        if (entityName.isEmpty()) {
            throw new MiniLangRuntimeException("Entity name not found", this);
        }
        Collection<String> fieldsToSelectList = fieldsToSelectListFma.get(methodContext.getEnvMap());
        try {
            if (fieldsToSelectList != null) {
                valueFma.put(methodContext.getEnvMap(), delegator.findByPrimaryKeyPartial(delegator.makePK(entityName, inMap), UtilMisc.makeSetWritable(fieldsToSelectList)));
            } else {
                valueFma.put(methodContext.getEnvMap(), delegator.findOne(entityName, inMap, useCache));
            }
        } catch (GenericEntityException e) {
            String errMsg = "Exception thrown while performing entity find: " + e.getMessage();
            Debug.logWarning(e, errMsg, module);
            simpleMethod.addErrorMessage(methodContext, errMsg);
            return false;
        }
        return true;
    }

    @Override
    public String expandedString(MethodContext methodContext) {
        return FlexibleStringExpander.expandString(toString(), methodContext.getEnvMap());
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        aic.addEntityName(entityNameFse.toString());
    }

    @Override
    public String rawString() {
        return toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<find-by-primary-key ");
        sb.append("entity-name=\"").append(this.entityNameFse).append("\" ");
        sb.append("value-field=\"").append(this.valueFma).append("\" ");
        sb.append("map=\"").append(this.mapFma).append("\" ");
        if (!fieldsToSelectListFma.isEmpty()) {
            sb.append("fields-to-select-list=\"").append(this.fieldsToSelectListFma).append("\" ");
        }
        if (!useCacheFse.isEmpty()) {
            sb.append("use-cache=\"").append(this.useCacheFse).append("\" ");
        }
        if (!delegatorNameFse.isEmpty()) {
            sb.append("delegator-name=\"").append(this.delegatorNameFse).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;find-by-primary-key&gt; element.
     */
    public static final class FindByPrimaryKeyFactory implements Factory<FindByPrimaryKey> {
        @Override
        public FindByPrimaryKey createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new FindByPrimaryKey(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "find-by-primary-key";
        }
    }
}
