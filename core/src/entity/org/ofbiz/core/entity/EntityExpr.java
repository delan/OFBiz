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

package org.ofbiz.core.entity;

import java.io.*;

/**
 * Encapsulates simple expressions used for specifying queries
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    Nov 13, 2001
 *@version    1.0
 */
public class EntityExpr implements Serializable {

    private Object lhs;
    private boolean lUpper;
    private EntityOperator operator;
    private Object rhs;
    private boolean rUpper;

    public EntityExpr(Object lhs, boolean lUpper, EntityOperator operator, Object rhs, boolean rUpper) {
        this(lhs, operator, rhs);
        setLUpper(lUpper);
        setRUpper(rUpper);
    }

    public EntityExpr(Object lhs, EntityOperator operator, Object rhs) {
        if (!(lhs instanceof String)) {
            throw new IllegalArgumentException("At the moment left hand side must be a String");
        }
        if (lhs instanceof EntityExpr || rhs instanceof EntityExpr) {
            throw new IllegalArgumentException("Nested expressions not yet supported");
        }

        this.lhs = lhs;
        this.operator = operator;
        this.rhs = rhs;
    }

    public void setLUpper(boolean upper) {
        lUpper = upper;
    }

    public boolean isLUpper() {
        return lUpper;
    }

    public boolean isRUpper() {
        return rUpper;
    }

    public void setRUpper(boolean upper) {
        rUpper = upper;
    }

    public Object getLhs() {
        return lhs;
    }

    public EntityOperator getOperator() {
        return operator;
    }

    public Object getRhs() {
        return rhs;
    }
}

