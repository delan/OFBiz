/*
 * $Id: EntityFunction.java,v 1.2 2004/05/02 05:46:40 doogie Exp $
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

import java.util.List;

import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericModelException;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;

/**
 * Encapsulates operations between entities and entity fields. This is a immutable class.
 *
 *@author     <a href='mailto:chris_maurer@altavista.com'>Chris Maurer</a>
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 *@created    Nov 5, 2001
 *@version    1.0
 */
public abstract class EntityFunction extends EntityCondition {

    public static final int ID_LENGTH = 1;
    public static final int ID_TRIM = 2;
    public static final int ID_UPPER = 3;
    public static final int ID_LOWER = 4;

    public static class LENGTH extends EntityFunction {
        public LENGTH(EntityFunction nested) { super(ID_LENGTH, "LENGTH", nested); }
        public LENGTH(Object value, boolean asEntity) { super(ID_LENGTH, "LENGTH", value, asEntity); }
        public Object doEval(Object value) { return new Integer(value.toString().length()); }
    };
    public static class TRIM extends EntityFunction {
        public TRIM(EntityFunction nested) { super(ID_TRIM, "TRIM", nested); }
        public TRIM(Object value, boolean asEntity) { super(ID_TRIM, "TRIM", value, asEntity); }
        public Object doEval(Object value) { return value.toString().trim(); }
    };
    public static class UPPER extends EntityFunction {
        public UPPER(EntityFunction nested) { super(ID_UPPER, "UPPER", nested); }
        public UPPER(Object value, boolean asEntity) { super(ID_UPPER, "UPPER", value, asEntity); }
        public Object doEval(Object value) { return value.toString().toUpperCase(); }
    };
    public static class LOWER extends EntityFunction {
        public LOWER(EntityFunction nested) { super(ID_LOWER, "LOWER", nested); }
        public LOWER(Object value, boolean asEntity) { super(ID_LOWER, "LOWER", value, asEntity); }
        public Object doEval(Object value) { return value.toString().toLowerCase(); }
    };

    protected int idInt;
    protected String codeString;
    protected EntityFunction nested;
    protected Object value;
    protected boolean asEntity;

    protected EntityFunction(int id, String code, EntityFunction nested) {
        idInt = id;
        codeString = code;
        this.nested = nested;
    }

    protected EntityFunction(int id, String code, Object value, boolean asEntity) {
        idInt = id;
        codeString = code;
        this.value = value;
	this.asEntity = asEntity;
    }

    public String getCode() {
        if (codeString == null)
            return "null";
        else
            return codeString;
    }

    public int getId() {
        return idInt;
    }

    public boolean equals(Object obj) {
        EntityFunction otherFunc = (EntityFunction) obj;
        return
            this.idInt == otherFunc.idInt
            && ( this.nested != null ? nested.equals( otherFunc.nested ) : otherFunc.nested != null )
            && ( this.value != null ? value.equals( otherFunc.value ) : otherFunc.value != null )
            && this.asEntity == otherFunc.asEntity;
    }

    protected abstract Object doEval(Object value);

    public String makeWhereString(ModelEntity modelEntity, List entityConditionParams) {
        StringBuffer sb = new StringBuffer();
        sb.append(codeString).append('(');
        if (nested != null) {
            sb.append(nested.makeWhereString(modelEntity, entityConditionParams));
        } else {
            if (asEntity) {
                ModelField field = getField(modelEntity, (String) value);
                String colName = getColName(field, (String) value);
                sb.append(colName);
            } else {
                addValue(sb, null, value, entityConditionParams);
            }
        }
        sb.append(')');
        return sb.toString();
    }

    public void checkCondition(ModelEntity modelEntity) throws GenericModelException {
        if (nested != null) {
            nested.checkCondition(modelEntity);
        }
    }

    public Object eval(GenericEntity entity)
    {
        if (nested != null) {
            return doEval(nested.eval(entity));
        } else {
            if (asEntity) {
                return doEval(entity.get(value.toString()));
            } else {
                return doEval(value);
            }
        }
    }

    public Object eval(Object value)
    {
        if (nested != null) {
            return doEval(nested.eval(value));
        } else if (value != null) {
            return doEval(value);
        } else {
            return null;
        }
    }

    public boolean entityMatches(GenericEntity entity)
    {
        Object result = eval(entity);
        if (result == null ) return false;
        if (result instanceof Boolean ) return ( (Boolean) result).booleanValue();
        return result.toString().length() > 0;
    }
}
