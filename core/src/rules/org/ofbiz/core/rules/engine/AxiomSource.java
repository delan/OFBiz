package org.ofbiz.core.rules.engine;

/**
 * <p><b>Title:</b> Axiom Source
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
 * <p>An AxiomSource is a provider of axioms.
 * <p>
 * Within the package sjm.engine, the only provider of
 * axioms is Program. The AxiomSource interface
 * allows other types of object to provide axioms,
 * specifically objects that act as databases and
 * provide lots of facts.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public interface AxiomSource {
  /**
   * Returns all the axioms from a source.
   *
   * @return all the axioms from a source
   */
  AxiomEnumeration axioms();
  /**
   * Returns an enumeration of axioms. The parameter
   * specifies the structure that is trying to prove itself.
   * The implementor of this method can ignore this, or
   * use it as an index.
   * <p>
   * An axiom has a chance of serving to prove the
   * structure only if the axiom begins with a structure
   * that matches the input structure with regard to its
   * functor and its number or terms (or "arity"). An implementor
   * can put this point to good purpose, only returning
   * axioms that have some chance of providing a proof.
   *
   * @param Structure the structure that is trying to prove itself
   *
   * @return a collection of axioms
   */
  AxiomEnumeration axioms(Structure s);
}
