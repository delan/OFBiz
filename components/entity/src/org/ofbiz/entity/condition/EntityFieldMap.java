/*
 * $Id: EntityFieldMap.java,v 1.7 2004/07/07 00:15:24 doogie Exp $
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.entity.condition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericModelException;
import org.ofbiz.entity.jdbc.SqlJdbcUtil;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;

/**
 * Encapsulates simple expressions used for specifying queries
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.7 $
 * @since      2.0
 */
public class EntityFieldMap extends EntityCondition {

    protected Map fieldMap;
    protected EntityJoinOperator operator;

    protected EntityFieldMap() {}

    public EntityFieldMap(Map fieldMap, EntityJoinOperator operator) {
        this.fieldMap = fieldMap;
        this.operator = operator;
    }

    public EntityJoinOperator getOperator() {
        return this.operator;
    }

    public Object getField(String name) {
        return this.fieldMap.get(name);
    }
    
    public boolean containsField(String name) {
        return this.fieldMap.containsKey(name);
    }
    
    public Iterator getFieldKeyIterator() {
        return this.fieldMap.keySet().iterator();
    }
    
    public Iterator getFieldEntryIterator() {
        return this.fieldMap.entrySet().iterator();
    }
    
    public String makeWhereString(ModelEntity modelEntity, List entityConditionParams) {
        // if (Debug.verboseOn()) Debug.logVerbose("makeWhereString for entity " + modelEntity.getEntityName(), module);
        List whereFields = new ArrayList();

        if (fieldMap != null && fieldMap.size() > 0) {
            if (modelEntity == null) {
                Iterator iter = fieldMap.keySet().iterator();
                while (iter.hasNext()) {
                    String fieldName = (String) iter.next();
                    whereFields.add(fieldName);
                }
            } else {
                for (int fi = 0; fi < modelEntity.getFieldsSize(); fi++) {
                    ModelField curField = modelEntity.getField(fi);

                    if (fieldMap.containsKey(curField.getName())) {
                        whereFields.add(curField);
                    }
                }
            }
        }
        return SqlJdbcUtil.makeWhereStringFromFields(whereFields, fieldMap, operator.getCode(), entityConditionParams);
    }

    public boolean mapMatches(GenericDelegator delegator, Map map) {
        Iterator iter = fieldMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String fieldName = (String) entry.getKey();
            Object value = map.get( fieldName );
            if ( value == null ) {
                if ( entry.getValue() != null ) {
                    return false;
                }
            } else {
                if ( !value.equals( entry.getValue() ) ) {
                    return false;
                }
            }
        }
        return true;
    }

    public void checkCondition(ModelEntity modelEntity) throws GenericModelException {
        // if (Debug.verboseOn()) Debug.logVerbose("checkCondition for entity " + modelEntity.getEntityName(), module);
        // make sure that all fields in the Map are valid
        if (fieldMap != null && !modelEntity.areFields(fieldMap.keySet())) {
            throw new GenericModelException("At least one of the passed fields is not valid: " + fieldMap.keySet().toString());
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof EntityFieldMap)) return false;
        EntityFieldMap other = (EntityFieldMap) obj;
        return fieldMap.equals(other.fieldMap) && operator.equals(other.operator);
    }

    public int hashCode() {
        return (fieldMap != null ? fieldMap.hashCode() : 0) & operator.hashCode();
    }
}
