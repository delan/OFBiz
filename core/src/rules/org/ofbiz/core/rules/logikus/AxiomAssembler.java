package org.ofbiz.core.rules.logikus;


import java.util.*;
import org.ofbiz.core.rules.engine.*;
import org.ofbiz.core.rules.parse.*;


/**
 * <p><b>Title:</b> Axiom Assembler
 * <p><b>Description:</b> None
 * <p>Copyright (c) 1999 Steven J. Metsker.
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
 * <br>
 * <p>Pops the structures of a rule from an assembly's stack,
 * and constructs and pushes a rule.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class AxiomAssembler extends Assembler {

    /**
     * Pops all of the structures on the stack, builds a rule
     * from them, and pushes it.
     *
     * @param  Assembly  the assembly to work on
     */
    public void workOn(Assembly a) {
        Stack s = a.getStack();
        Structure[] sa = new Structure[s.size()];

        for (int i = 0; i < s.size(); i++) {
            sa[i] = (Structure) s.get(i);
        }
        s.removeAllElements();
        a.push(new Rule(sa));
    }
}
