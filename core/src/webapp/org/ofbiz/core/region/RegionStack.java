/*
 * $Id$
 *
 * Copyright (c) 2001 Sun Microsystems Inc., published in "Advanced Java Server Pages" by Prentice Hall PTR
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.region;

import javax.servlet.jsp.PageContext;
import java.util.Stack;

public class RegionStack {
    private RegionStack() { } // no instantiations
    
    public static Stack getStack(PageContext pc) {
        Stack s = (Stack) pc.getAttribute("region-stack", PageContext.APPLICATION_SCOPE);
        if(s == null) {
            s = new Stack();
            pc.setAttribute("region-stack", s, PageContext.APPLICATION_SCOPE);
        }
        return s;
    }
    
    public static Region peek(PageContext pc) {
        return (Region) getStack(pc).peek();
    }
    
    public static void push(PageContext pc, Region region) {
        getStack(pc).push(region);
    }
    
    public static Region pop(PageContext pc) {
        return (Region) getStack(pc).pop();
    }
}
