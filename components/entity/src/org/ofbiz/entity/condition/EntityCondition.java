/*
 * $Id: EntityCondition.java,v 1.5 2004/04/23 01:42:15 doogie Exp $
 *
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.entity.condition;


import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import org.ofbiz.entity.GenericModelException;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.jdbc.SqlJdbcUtil;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;


/**
 * Represents the conditions to be used to constrain a query
 * <br>An EntityCondition can represent various type of constraints, including:
 * <ul>
 *  <li>EntityConditionList: a list of EntityConditions, combined with the operator specified
 *  <li>EntityExpr: for simple expressions or expressions that combine EntityConditions
 *  <li>EntityFieldMap: a map of fields where the field (key) equals the value, combined with the operator specified
 * </ul>
 * These can be used in various combinations using the EntityConditionList and EntityExpr objects.
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.5 $
 * @since      2.0
 */
public abstract class EntityCondition implements Serializable {
    protected ModelField getField(ModelEntity modelEntity, String fieldName) {
        ModelField modelField = null;
        if (modelEntity != null) {
            modelField = (ModelField) modelEntity.getField(fieldName);
        }
        return modelField;
    }

    protected String getColName(ModelField modelField, String fieldName) {
        String colName = null;
        if (modelField != null) {
            colName = modelField.getColName();
        } else {
            colName = (String) fieldName;
        }
        return colName;
    }

    protected void addValue(StringBuffer buffer, ModelField field, Object value, List params) {
        SqlJdbcUtil.addValue(buffer, params == null ? null : field, value, params);
    }

    public boolean equals(Object obj) {
        throw new UnsupportedOperationException("equals");
    }

    public int hashCode() {
        throw new UnsupportedOperationException("hashCode");
    }

    protected static boolean equals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    protected static int hashCode(Object o) {
        return o != null ? o.hashCode() : 0;
    }

    public String toString() {
        return makeWhereString(null, new ArrayList());
    }

    abstract public String makeWhereString(ModelEntity modelEntity, List entityConditionParams);

    abstract public void checkCondition(ModelEntity modelEntity) throws GenericModelException;

    abstract public boolean entityMatches(GenericEntity entity);
}
