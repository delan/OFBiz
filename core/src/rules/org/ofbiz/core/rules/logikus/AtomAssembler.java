package org.ofbiz.core.rules.logikus;


import org.ofbiz.core.rules.parse.*;
import org.ofbiz.core.rules.parse.tokens.*;
import org.ofbiz.core.rules.engine.*;


/**
 * <p><b>Title:</b> Atom Assembler
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
 * <p>Exchanges a token on an assembly's stack with an atom
 * that has the token's value as its functor.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class AtomAssembler extends Assembler {

    /**
     * Exchanges a token on an assembly's stack with an atom
     * that has the token's value as its functor. In the case
     * of a quoted string, this assembler removes the quotes,
     * so that a string such as "Smith" becomes just Smith. In
     * the case of a number, this assembler pushes a NumberFact.
     *
     * @param  Assembly  the assembly to work on
     */
    public void workOn(Assembly a) {
        Token t = (Token) a.pop();

        // remove quotes from quoted string
        if (t.isQuotedString()) {
            String s = t.sval();
            String plain = s.substring(1, s.length() - 1);

            a.push(new Atom(plain));
        } else
        if (t.isNumber()) {
            a.push(new NumberFact(t.nval()));
        } else {
            a.push(new Atom(t.value()));
        }
    }
}
