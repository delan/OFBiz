/*
 * $Id: EntityWhereString.java,v 1.8 2004/07/14 04:03:54 doogie Exp $
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

import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericModelException;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.model.ModelEntity;

/**
 * <p>Encapsulates SQL expressions used for where clause snippets.
 *  NOTE: This is UNSAFE and BREAKS the idea behind the Entity Engine where
 *  you avoid directly specifying SQL. So, KEEP IT MINIMAL and preferrably replace
 *  it when the feature you are getting at is implemented in a more automatic way for you.</p>
 *
 * <p>By minimal I mean use this in conjunction with other EntityConditions like the
 *  EntityExpr, EntityConditionList and EntityFieldMap objects which more cleanly
 *  encapsulate where conditions and don't require you to directly write SQL.</p>
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.8 $
 * @since      2.0
 */
public class EntityWhereString extends EntityCondition {

    protected String sqlString;

    protected EntityWhereString() {}

    public EntityWhereString(String sqlString) {
        this.sqlString = sqlString;
    }

    public String makeWhereString(ModelEntity modelEntity, List entityConditionParams) {
        return sqlString;
    }

    public void checkCondition(ModelEntity modelEntity) throws GenericModelException {// no nothing, this is always assumed to be fine... could do funky SQL syntax checking, but hey this is a HACK anyway
    }

	public boolean entityMatches(GenericEntity entity) {
		throw new UnsupportedOperationException("Cannot do entityMatches on a WhereString, ie no SQL evaluation in EE; Where String is: " + sqlString);
	}

    public boolean mapMatches(GenericDelegator delegator, Map map) {
        throw new UnsupportedOperationException("Cannot do mapMatches on a WhereString, ie no SQL evaluation in EE; Where String is: " + sqlString);
    }

    public String getWhereString() {
        return sqlString;
    }

    public EntityCondition freeze() {
        return this;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof EntityWhereString)) return false;
        EntityWhereString other = (EntityWhereString) obj;
        return equals(sqlString, other.sqlString);
    }

    public int hashCode() {
        return hashCode(sqlString);
    }
}
