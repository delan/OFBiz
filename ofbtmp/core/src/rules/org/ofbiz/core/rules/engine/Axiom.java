package org.ofbiz.core.rules.engine;


/**
 * <p><b>Title:</b> Axiom
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
 * <p>In practice, an Axiom is either a fact or a rule, the two
 * types of objects that can appear in a program. More
 * precisely, an Axiom has a head structure with which a
 * consulting structure can unify, and an Axiom can produce
 * a ProvableAxiom.
 * <p>
 * Facts are simply true, and return themselves as
 * DynamicAxioms. To prove itself, a Rule needs to
 * create a DynamicAxiom that can attempt to prove
 * itself against an axiom source.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public interface Axiom {

    /**
     * Return an axiom that a consulting structure can use
     * to prove itself.
     *
     * @return an axiom that a consulting structure can use
     *         to prove itself.
     */
    DynamicAxiom dynamicAxiom(AxiomSource as);

    /**
     * Return the first structure of this axiom.
     *
     * @return the first structure of this axiom
     */
    Structure head();
}
