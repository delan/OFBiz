package org.ofbiz.rules.logikus;


import java.util.*;
import org.ofbiz.rules.engine.*;
import org.ofbiz.rules.parse.*;
import org.ofbiz.rules.parse.tokens.*;


/**
 * <p><b>Title:</b> List Assembler
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
 * <p>Pops the terms of a list from an assembly's stack, builds
 * the list, and pushes it.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class ListAssembler extends Assembler {

    /**
     * Pops the terms of a list from an assembly's stack, builds
     * the list, and pushes it.
     * <p>
     * This method expects a series of terms to lie on top of a
     * stack, with an open bracket token lying beneath. If there
     * is no '[' marker, this class will throw an <code>
     * EmptistackException</code>.
     *
     * @param  Assembly  the assembly to work on
     */
    public void workOn(Assembly a) {
        Token fence = new Token('[');
        List termVector = elementsAbove(a, fence);
        Term[] termArray =
            StructureWithTermsAssembler.vectorReversedIntoTerms(
                termVector);

        if (termArray.length == 0) {
            a.push(Structure.emptyList);
        } else {
            a.push(Structure.list(termArray));
        }
    }
}
