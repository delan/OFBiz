/*
 * $Id$
 *
 * Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.shark.expression;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;
import org.enhydra.shark.api.common.ExpressionBuilder;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilObject;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.shark.container.SharkContainer;

/**
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      Apr 17, 2005
 */
public abstract class BaseEntityCondExprBldr implements ExpressionBuilder, Serializable {

    public static final String module = BaseEntityCondExprBldr.class.getName();

    protected EntityCondition condition = null;
    protected Map entityNames = new LinkedMap();
    protected Map fieldNames = new LinkedMap();
    protected List autoFields = new ArrayList();
    protected List viewLinks = new ArrayList();

    protected boolean isOrSet = false;
    protected boolean isNotSet = false;
    protected boolean isComplete = true;

    // ExpressionBuilder Methods

    public boolean isComplete() {
        return this.isComplete;
    }

    public String toSQL() {
        return BaseEntityCondExprBldr.getHexString(this);
    }

    public String toScript() {
        return "";
    }

    public String toExpression() {
        StringBuffer ret = new StringBuffer();
        if (!this.isComplete()) {
            ret.append("/*FORCE*/\n");
        }

        ret.append(this.toScript());
        ret.append("\n/*sql ").append(this.toSQL()).append(" sql*/");

        return ret.toString();
    }

    // Special Purpose Methods

    public EntityCondition getCondition() {
        return this.condition;
    }

    public List runQuery() throws GenericEntityException {
        GenericDelegator delegator = this.getDelegator();
        DynamicViewEntity view = this.makeView();
        EntityListIterator eli = null;
        List result = null;
        try {
            eli = delegator.findListIteratorByCondition(view, this.getCondition(), null, null, null, null);
            result = eli.getCompleteList();
        } catch (GenericEntityException e) {
            throw e;
        } finally {
            eli.close();
        }
        return result;
    }

    protected void setNot(boolean not) {
        this.isNotSet = not;
    }

    protected void setOr(boolean or) {
        this.isOrSet = or;
    }

    protected void addCondition(EntityCondition current) {
        if (condition == null) {
            condition = current;
        } else {
            List condList = UtilMisc.toList(condition, current);
            if (this.isOrSet) {
                condition = new EntityConditionList(condList, EntityOperator.OR);
            } else {
                condition = new EntityConditionList(condList, EntityOperator.AND);
            }
        }
        // reset the NOT value
        this.setNot(false);
    }

    protected synchronized void addEntity(String alias, String entity) {
        entityNames.put(alias, entity);
    }

    protected synchronized void addField(String entityAlias, String fieldName, String fieldAlias) {
        Map fieldAliasMap = (Map) fieldNames.get(entityAlias);
        if (fieldAliasMap == null) {
            fieldAliasMap = new HashMap();
            fieldNames.put(entityAlias, fieldAliasMap);
        }
        fieldAliasMap.put(fieldName, fieldAlias);
    }

    protected synchronized void addAllFields(String entityAlias) {
        autoFields.add(entityAlias);
    }

    protected synchronized void addLink(String entityAlias, String relEntityAlias, boolean opt, List keyMap) {
        this.viewLinks.add(new ViewLink(entityAlias, relEntityAlias, opt, keyMap));
    }
    
    protected GenericDelegator getDelegator() {
        return SharkContainer.getDelegator();
    }

    private DynamicViewEntity makeView() {
        DynamicViewEntity view = new DynamicViewEntity();


        // create the members
        Iterator eni = this.entityNames.entrySet().iterator();
        while (eni.hasNext()) {
            Map.Entry entry = (Map.Entry) eni.next();
            view.addMemberEntity((String) entry.getKey(), (String) entry.getValue());
        }

        // set alias all fields
        Iterator aai = autoFields.iterator();
        while (aai.hasNext()) {
            String alias = (String) aai.next();
            view.addAliasAll(alias, "");
        }

        // create the other field aliases
        Iterator fni = fieldNames.keySet().iterator();
        while (fni.hasNext()) {
            String entityAlias = (String) fni.next();
            Map fieldMap = (Map) fieldNames.get(entityAlias);
            Iterator fmi = fieldMap.entrySet().iterator();
            while (fmi.hasNext()) {
                Map.Entry entry = (Map.Entry) fmi.next();
                view.addAlias(entityAlias, (String) entry.getValue(), (String) entry.getKey(), null, null, null, null);
            }
        }

        // add the view links
        Iterator vli = this.viewLinks.iterator();
        while (vli.hasNext()) {
            ViewLink vl = (ViewLink) vli.next();
            view.addViewLink(vl.entityAlias, vl.relEntityAlias, new Boolean(vl.relOptional), vl.keyMaps);
        }

        return view;
    }

    // Standard Static Serialization Methods

    public static String getHexString(BaseEntityCondExprBldr builder) {
        byte[] builderBytes = UtilObject.getBytes(builder);
        return StringUtil.toHexString(builderBytes);
    }

    public static BaseEntityCondExprBldr getBuilder(String hexString) {
        byte[] builderBytes = StringUtil.fromHexString(hexString);
        return (BaseEntityCondExprBldr) UtilObject.getObject(builderBytes);
    }

    class ViewLink implements Serializable {

        public String entityAlias;
        public String relEntityAlias;
        public boolean relOptional = false;
        public List keyMaps = new ArrayList();

        public ViewLink(String entityAlias, String relEntityAlias, boolean optional, List keyMaps) {
            this.entityAlias = entityAlias;
            this.relEntityAlias = relEntityAlias;
            this.relOptional = optional;
            this.keyMaps = keyMaps;
        }
    }
}
