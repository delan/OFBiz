package org.ofbiz.core.rules.logikus;

import java.util.*;
import org.ofbiz.core.rules.engine.*;
import org.ofbiz.core.rules.parse.*;
import org.ofbiz.core.rules.parse.tokens.*;

/**
 * <p><b>Title:</b> Logikus Exception
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
 * <p>Pops the terms and functor of a structure from an assembly's
 * stack, builds a structure, and pushes it.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class StructureWithTermsAssembler extends Assembler {
  /**
   * Reverse a vector into an array of terms.
   *
   * @param   Vector   the vector to reverse
   * @return   Term[]   the vector, reversed
   */
  public static Term[] vectorReversedIntoTerms(Vector v) {
    int size = v.size();
    Term[] terms = new Term[size];
    for (int i = 0; i < size; i++) {
      terms[size - 1 - i] = (Term) v.elementAt(i);
    }
    return terms;
  }
  /**
   * Pops the terms and functor of a structure from an assembly's
   * stack, builds a structure, and pushes it.
   * <p>
   * This method expects a series of terms to lie on top of a
   * stack, with an open paren token lying underneath. If there
   * is no '(' marker, this class will throw an <code>
   * EmptyStackException</code>.
   * <p>
   * Beneath the terms of the structure, this method expects to
   * find a token whose value is the functor of the structure.
   *
   * @param  Assembly  the assembly to work on
   */
  public void workOn(Assembly a) {
    Vector termVector = elementsAbove(a, new Token('('));
    Term[] termArray = vectorReversedIntoTerms(termVector);
    Token t = (Token) a.pop();
    a.push(new Structure(t.value(), termArray));
  }
}
