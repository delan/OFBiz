package org.ofbiz.core.rules.engine;

import java.util.*;

/**
 * <p><b>Title:</b> Program Enumerator
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
 * <p>A ProgramEnumerator returns the axioms of a program, one at a time.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class ProgramEnumerator implements AxiomEnumeration {
  protected Enumeration e;
  /**
   * Construct an enumeration of the given program.
   *
   * @param Program the program to enumerate over
   *
   */
  public ProgramEnumerator(Program p) {
    e = Collections.enumeration(p.axioms);
  }
  /**
   * Tests if this enumeration contains more axioms.
   *
   * @return  <code>true</code> if the program this enumeration
   *          is constructed for contains more axioms, and
   *          <code>false</code> otherwise.
   */
  public boolean hasMoreAxioms() {
    return e.hasMoreElements();
  }
  /**
   * Returns the next axiom of this enumeration.
   *
   * @return the next axiom of this enumeration.
   */
  public Axiom nextAxiom() {
    return (Axiom) e.nextElement();
  }
}
