package org.ofbiz.core.rules.engine;

import java.util.*;
/**
 * <p><b>Title:</b> Program
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
 * <p>A Program is a collection of rules and facts that together
 * form a logical model.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */

public class Program implements AxiomSource {
  protected Vector axioms = new Vector();
  /**
   * Create a new program with no axioms.
   */
  public Program() {
  }
  /**
   * Create a new program with the given axioms.
   */
  public Program(Axiom[] axioms) {
    for (int i = 0; i < axioms.length; i++) {
      addAxiom(axioms[i]);
    }
  }
  /**
   * Adds an axiom to this program.
   *
   * @param Axiom the axiom to add.
   */
  public void addAxiom(Axiom a) {
    axioms.addElement(a);
  }
  /**
   * Appends all the axioms of another source to this one.
   *
   * @param   program   the source of the new axioms
   */
  public void append(AxiomSource as) {
    AxiomEnumeration e = as.axioms();
    while (e.hasMoreAxioms()) {
      addAxiom(e.nextAxiom());
    }
  }
  /**
   * Returns an enumeration of the axioms in this program.
   *
   * @return an enumeration of the axioms in this program.
   */
  public AxiomEnumeration axioms() {
    return new ProgramEnumerator(this);
  }
  /**
   * Returns an enumeration of the axioms in this program.
   *
   * @return an enumeration of the axioms in this program.
   */
  public AxiomEnumeration axioms(Structure ignored) {
    return axioms();
  }
  /**
   * Returns a string representation of this program.
   *
   * @return a string representation of this program.
   */
  public String toString() {
    StringBuffer buf = new StringBuffer();
    boolean haveShownALine = false;
    Enumeration e = axioms.elements();
    while (e.hasMoreElements()) {
      if (haveShownALine) {
        buf.append("\n");
      }
      buf.append(e.nextElement().toString());
      buf.append(";");
      haveShownALine = true;
    }
    return buf.toString();
  }
}
