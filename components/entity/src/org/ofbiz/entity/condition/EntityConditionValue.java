/*
 * $Id: EntityConditionValue.java,v 1.3 2004/07/07 17:37:40 ajzeneski Exp $
 *
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.entity.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericModelException;
import org.ofbiz.entity.config.EntityConfigUtil;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;

/**
 * Encapsulates operations between entities and entity fields. This is a immutable class.
 *
 *@author     <a href='mailto:chris_maurer@altavista.com'>Chris Maurer</a>
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 *@since      1.0
 *@version    $Revision: 1.3 $
 */
public abstract class EntityConditionValue extends EntityConditionBase {

    public abstract ModelField getModelField(ModelEntity modelEntity);

    public void addSqlValue(StringBuffer sql, ModelEntity modelEntity, List entityConditionParams, boolean includeTableNamePrefix,
            EntityConfigUtil.DatasourceInfo datasourceinfo) {
        addSqlValue(sql, emptyMap, modelEntity, entityConditionParams, includeTableNamePrefix, datasourceinfo);
    }

    public abstract void addSqlValue(StringBuffer sql, Map tableAliases, ModelEntity modelEntity, List entityConditionParams,
            boolean includeTableNamePrefix, EntityConfigUtil.DatasourceInfo datasourceinfo);

    public abstract void validateSql(ModelEntity modelEntity) throws GenericModelException;

    public Object getValue(GenericEntity entity) {
        return getValue((Map) entity);
    }

    public abstract Object getValue(Map map);

    public abstract EntityConditionValue freeze();

    public String toString() {
        StringBuffer sql = new StringBuffer();
        addSqlValue(sql, null, new ArrayList(), false, null);
        return sql.toString();
    }
}


