/*
 * $Id: EntityOperator.java,v 1.3 2004/01/14 00:08:11 ajzeneski Exp $
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

import java.util.HashMap;

/**
 * Encapsulates operations between entities and entity fields. This is a immutable class.
 *
 *@author     <a href='mailto:chris_maurer@altavista.com'>Chris Maurer</a>
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 *@version    $Revision: 1.3 $
 *@since      2.0
 */
public class EntityOperator implements java.io.Serializable {

    public static final int ID_EQUALS = 1;
    public static final int ID_NOT_EQUAL = 2;
    public static final int ID_LESS_THAN = 3;
    public static final int ID_GREATER_THAN = 4;
    public static final int ID_LESS_THAN_EQUAL_TO = 5;
    public static final int ID_GREATER_THAN_EQUAL_TO = 6;
    public static final int ID_IN = 7;
    public static final int ID_BETWEEN = 8;
    public static final int ID_NOT = 9;
    public static final int ID_AND = 10;
    public static final int ID_OR = 11;
    public static final int ID_LIKE = 12;
    public static final int ID_NOT_IN = 13;
    
    private static HashMap registry = new HashMap();

    private static void register(String name, EntityOperator operator) {
        registry.put(name, operator);
    }

    public static EntityOperator lookup(String name) {
        return (EntityOperator)registry.get(name);
    }

    public static EntityComparisonOperator lookupComparison(String name) {
        EntityOperator operator = lookup(name);
        if ( !(operator instanceof EntityComparisonOperator ) )
            throw new IllegalArgumentException(name + " is not a comparison operator");
        return (EntityComparisonOperator)operator;
    }

    public static EntityJoinOperator lookupJoin(String name) {
        EntityOperator operator = lookup(name);
        if ( !(operator instanceof EntityJoinOperator ) )
            throw new IllegalArgumentException(name + " is not a join operator");
        return (EntityJoinOperator)operator;
    }

    public static final EntityComparisonOperator EQUALS = new EntityComparisonOperator(ID_EQUALS, "=") {
        public boolean compare(Object lhs, Object rhs) { return EntityComparisonOperator.compareEqual(lhs, rhs); }
    };
    static { register( "equals", EQUALS ); }
    public static final EntityComparisonOperator NOT_EQUAL = new EntityComparisonOperator(ID_NOT_EQUAL, "<>") {
        public boolean compare(Object lhs, Object rhs) { return EntityComparisonOperator.compareNotEqual(lhs, rhs); }
    };
    static { register( "notEqual", NOT_EQUAL ); }
    public static final EntityComparisonOperator LESS_THAN = new EntityComparisonOperator(ID_LESS_THAN, "<") {
        public boolean compare(Object lhs, Object rhs) { return EntityComparisonOperator.compareLessThan(lhs, rhs); }
    };
    static { register( "lessThan", LESS_THAN ); }
    public static final EntityComparisonOperator GREATER_THAN = new EntityComparisonOperator(ID_GREATER_THAN, ">") {
        public boolean compare(Object lhs, Object rhs) { return EntityComparisonOperator.compareGreaterThan(lhs, rhs); }
    };
    static { register( "greaterThan", GREATER_THAN ); }
    public static final EntityComparisonOperator LESS_THAN_EQUAL_TO = new EntityComparisonOperator(ID_LESS_THAN_EQUAL_TO, "<=") {
        public boolean compare(Object lhs, Object rhs) { return EntityComparisonOperator.compareLessThanEqualTo(lhs, rhs); }
    };
    static { register( "lessThanEqualTo", LESS_THAN_EQUAL_TO ); }
    public static final EntityComparisonOperator GREATER_THAN_EQUAL_TO = new EntityComparisonOperator(ID_GREATER_THAN_EQUAL_TO, ">=") {
        public boolean compare(Object lhs, Object rhs) { return EntityComparisonOperator.compareGreaterThanEqualTo(lhs, rhs); }
    };
    static { register( "greaterThanEqualTo", GREATER_THAN_EQUAL_TO ); }
    public static final EntityComparisonOperator IN = new EntityComparisonOperator(ID_IN, "IN") {
        public boolean compare(Object lhs, Object rhs) { return EntityComparisonOperator.compareIn(lhs, rhs); }
    };
    static { register( "in", IN ); }
    public static final EntityComparisonOperator BETWEEN = new EntityComparisonOperator(ID_BETWEEN, "BETWEEN");
    static { register( "between", BETWEEN ); }
    public static final EntityComparisonOperator NOT = new EntityComparisonOperator(ID_NOT, "NOT");
    static { register( "not", NOT ); }
    public static final EntityJoinOperator AND = new EntityJoinOperator(ID_AND, "AND", false);
    static { register( "and", AND ); }
    public static final EntityJoinOperator OR = new EntityJoinOperator(ID_OR, "OR", true);
    static { register( "or", OR ); }
    public static final EntityComparisonOperator LIKE = new EntityComparisonOperator(ID_LIKE, "LIKE") {
        public boolean compare(Object lhs, Object rhs) { return EntityComparisonOperator.compareLike(lhs, rhs); }
    };
    static { register( "like", LIKE ); }
    public static final EntityComparisonOperator NOT_IN = new EntityComparisonOperator(ID_NOT_IN, "NOT IN");
    static { register( "not-in", NOT_IN ); }

    protected int idInt;
    protected String codeString;

    public EntityOperator(int id, String code) {
        idInt = id;
        codeString = code;
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

    public String toString() {
        return codeString;
    }
    
    public int hashCode() {
        return this.codeString.hashCode();
    }

    public boolean equals(Object obj) {
        EntityOperator otherOper = (EntityOperator) obj;
        return this.idInt == otherOper.idInt;
    }

    public class MatchResult {
        public boolean shortCircuit = false;
        public boolean matches = false;
    }
}
