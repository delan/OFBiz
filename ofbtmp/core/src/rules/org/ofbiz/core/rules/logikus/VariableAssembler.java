/*
 * $Id$
 *
 * Copyright (c) 1999 Steven J. Metsker.
 * Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.rules.logikus;


import org.ofbiz.core.rules.engine.*;
import org.ofbiz.core.rules.parse.*;
import org.ofbiz.core.rules.parse.tokens.*;


/**
 * Pops a string like "X" or "Person" from an assembly's stack and pushes a variable with that name.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class VariableAssembler extends Assembler {

    /**
     * Pops a string like "X" or "Person" from an assembly's stack
     * and pushes a variable with that name.
     *
     * @param  Assembly  the assembly to work on
     */
    public void workOn(Assembly a) {
        Token t = (Token) a.pop();
        String name = t.sval();

        a.push(new Variable(name));
    }
}
