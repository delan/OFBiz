/*
 * $Id$
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

package org.ofbiz.core.entity;

/**
 * Encapsulates operations between entities and entity fields. This is a immutable class.
 *
 *@author     <a href='mailto:chris_maurer@altavista.com'>Chris Maurer</a>
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 *@created    Nov 5, 2001
 *@version    1.0
 */
public class EntityOperator implements java.io.Serializable {
    
    public static final EntityOperator EQUALS = new EntityOperator("EQUALS", "=");
    public static final EntityOperator NOT_EQUAL = new EntityOperator("NOT_EQUAL", "<>");
    public static final EntityOperator LESS_THAN = new EntityOperator("LESS_THAN", "<");
    public static final EntityOperator GREATER_THAN = new EntityOperator("GREATER_THAN", ">");
    public static final EntityOperator LESS_THAN_EQUAL_TO = new EntityOperator("LESS_THAN_EQUAL_TO", "<=");
    public static final EntityOperator GREATER_THAN_EQUAL_TO = new EntityOperator("GREATER_THAN_EQUAL_TO", ">=");
    public static final EntityOperator IN = new EntityOperator("IN", "IN");
    public static final EntityOperator BETWEEN = new EntityOperator("BETWEEN", "BETWEEN");
    public static final EntityOperator NOT = new EntityOperator("NOT", "NOT");
    public static final EntityOperator AND = new EntityOperator("AND", "AND");
    public static final EntityOperator OR = new EntityOperator("OR", "OR");
    public static final EntityOperator LIKE = new EntityOperator("LIKE", "LIKE");
    
    private String nameString;
    private String codeString;
    
    public EntityOperator(String name, String code) {
        nameString = name;
        codeString = code;
    }
    
    public String getCode() {
        if (codeString==null)
            return "null";
        else
            return codeString;
    }
    
    public String getName() {
        return nameString;
    }
    
    public String toString() {
        return codeString;
    }
    
    public boolean equals(Object obj) {
        EntityOperator otherOper = (EntityOperator) obj;
        return this.getName().equals(otherOper.getName());
    }
}
