package org.ofbiz.core.rules.engine;


/**
 * <p><b>Title:</b> Dynamic Axiom
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
 * <p>A DynamicAxiom is an axiom (that is, either a fact
 * or a rule) that a structure can consult to prove itself.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public interface DynamicAxiom {

    /**
     * Return the first structure of this dynamic axiom.
     *
     * @return the first structure of this dynamic axiom
     */
    Structure head();

    /**
     * Return the tail of this dynamic axiom.
     *
     * @return the tail of this dynamic axiom. This tail
     *         is the part of the dynamic that still needs to
     *         prove itself.
     */
    DynamicRule resolvent();
}
