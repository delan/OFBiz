/*
 * $Id$
 *
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.core.minilang.method;

import org.ofbiz.core.util.*;

/**
 * Used to flexibly access Map values, supporting the "." (dot) syntax for
 * accessing sub-map values and the "[]" (square bracket) syntax for accessing
 * list elements. See individual Map operations for more information.
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.1
 */
public class ContextAccessor {

    protected String name;
    protected FlexibleMapAccessor fma;
    protected boolean needsExpand;

    public ContextAccessor(String name) {
        this.name = name;
        int openPos = name.indexOf("${");
        if (openPos != -1 && name.indexOf("}", openPos) != -1) {
            fma = null;
            needsExpand = true;
        } else {
            fma = new FlexibleMapAccessor(name);
            needsExpand = false;
        }
    }
    
    /** Based on name get from Map or from List in Map */
    public Object get(MethodContext context) {
        if (this.needsExpand) {
            return context.getEnv(name);
        } else {
            return context.getEnv(fma);
        }
    }
    
    /** Based on name put in Map or from List in Map;
     * If the brackets for a list are empty the value will be appended to the list,
     * otherwise the value will be set in the position of the number in the brackets.
     * If a "+" (plus sign) is included inside the square brackets before the index 
     * number the value will inserted/added at that point instead of set at the point.
     */
    public void put(MethodContext context, Object value) {
        if (this.needsExpand) {
            context.putEnv(name, value);
        } else {
            context.putEnv(fma, value);
        }
    }
    
    /** Based on name remove from Map or from List in Map */
    public Object remove(MethodContext context) {
        if (this.needsExpand) {
            return context.removeEnv(name);
        } else {
            return context.removeEnv(fma);
        }
    }
}
