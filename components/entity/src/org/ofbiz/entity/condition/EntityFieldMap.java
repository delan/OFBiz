/*
 * $Id: EntityFieldMap.java,v 1.11 2004/07/14 04:15:49 doogie Exp $
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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
 * @version    $Revision: 1.11 $
 * @since      2.0
 */
public class EntityFieldMap extends EntityConditionListBase {

    protected Map fieldMap;

    protected EntityFieldMap() {
        super();
    }

    public static List makeConditionList(Map fieldMap, EntityComparisonOperator op) {
        if (fieldMap == null) return new ArrayList();
        List list = new ArrayList(fieldMap.size());
        Iterator it = fieldMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String field = (String)entry.getKey();
            Object value = entry.getValue();
            list.add(new EntityExpr(field, op, value));
        }
        return list;
    }

    public EntityFieldMap(Map fieldMap, EntityJoinOperator operator) {
        super(makeConditionList(fieldMap, EntityOperator.EQUALS), operator);
        this.fieldMap = fieldMap;
        if (this.fieldMap == null) this.fieldMap = new LinkedHashMap();
        this.operator = operator;
    }

    public Object getField(String name) {
        return this.fieldMap.get(name);
    }
    
    public boolean containsField(String name) {
        return this.fieldMap.containsKey(name);
    }
    
    public Iterator getFieldKeyIterator() {
        return Collections.unmodifiableSet(this.fieldMap.keySet()).iterator();
    }
    
    public Iterator getFieldEntryIterator() {
        return Collections.unmodifiableSet(this.fieldMap.entrySet()).iterator();
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
