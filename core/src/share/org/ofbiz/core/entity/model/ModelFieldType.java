/*
 * $Id$
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

package org.ofbiz.core.entity.model;

import java.util.*;

/**
 * Generic Entity - FieldType model class
 *
 *@author     David E. Jones
 *@created    May 15, 2001
 *@version    1.0
 */

public class ModelFieldType {

    /** The type of the Field */
    public String type = null;
    /** The java-type of the Field */
    public String javaType = null;
    /** The sql-type of the Field */
    public String sqlType = null;
    /** validators to be called when an update is done */
    public Vector validators = new Vector();

    /** Default Constructor */
    public ModelFieldType() {
    }

    /** A simple function to derive the max length of a String created from the field value, based on the sql-type
     * @return max length of a String representing the Field value
     */
    public int stringLength() {
        if (sqlType.indexOf("VARCHAR") >= 0) {
            if (sqlType.indexOf("(") > 0 && sqlType.indexOf(")") > 0) {
                String length = sqlType.substring(sqlType.indexOf("(") + 1, sqlType.indexOf(")"));
                return Integer.parseInt(length);
            } else {
                return 255;
            }
        } else if (sqlType.indexOf("CHAR") >= 0) {
            if (sqlType.indexOf("(") > 0 && sqlType.indexOf(")") > 0) {
                String length = sqlType.substring(sqlType.indexOf("(") + 1, sqlType.indexOf(")"));
                return Integer.parseInt(length);
            } else {
                return 255;
            }
        } else if (sqlType.indexOf("TEXT") >= 0) {
            return 5000;
        }
        return 20;
    }
}
