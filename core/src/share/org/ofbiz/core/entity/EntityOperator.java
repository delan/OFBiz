
package org.ofbiz.core.entity;

/**
 * <p><b>Title:</b> EntityOperator
 * <p><b>Description:</b> Encapsulates operations between entities and entity fields. This is a immutable class.
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
 *
 *@author     <a href='mailto:chris_maurer@altavista.com'>Chris Maurer</a>
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    Mon Nov 5, 2001
 *@version    1.0
 */
public class EntityOperator {

    public static EntityOperator EQUALS = new EntityOperator(" = ");
    public static EntityOperator NOT_EQUAL = new EntityOperator(" <> ");
    public static EntityOperator LESS_THAN = new EntityOperator(" < ");
    public static EntityOperator GREATER_THAN = new EntityOperator(" > ");
    public static EntityOperator LESS_THAN_EQUAL_TO = new EntityOperator(" <= ");
    public static EntityOperator GREATER_THAN_EQUAL_TO = new EntityOperator(" >= ");
    public static EntityOperator IN = new EntityOperator(" IN ");
    public static EntityOperator BETWEEN = new EntityOperator(" BETWEEN ");
    public static EntityOperator NOT = new EntityOperator(" NOT ");
    public static EntityOperator AND = new EntityOperator(" AND ");
    public static EntityOperator OR = new EntityOperator(" OR ");

    private String codeString;

    private EntityOperator(String code) {
        codeString = code;
    }

    public String getCode() {
        return codeString;
    }
}
