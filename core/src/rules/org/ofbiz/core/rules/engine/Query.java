package org.ofbiz.core.rules.engine;


import java.util.*;


/**
 * <p><b>Title:</b> Query
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
 * <p>A Query is a dynamic rule that stands outside of a program
 * and proves itself by referring to a program.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class Query extends DynamicRule {

    /**
     * Create a query from the given structures, to prove itself
     * against the given axiom source.
     *
     * @param   AxiomSource   the source to prove against
     *
     * @param   Structures   the structures to prove
     */
    public Query(AxiomSource as, Structure[] structures) {
        this(as, new Scope(structures), structures);
    }

    /**
     * Create a query from the given rule's structures, to prove
     * itself against the given axiom source.
     *
     * @param   AxiomSource   the source to prove against
     *
     * @param   Rule   the rule that contains structures to prove
     */
    public Query(AxiomSource as, Rule rule) {
        this(as, rule.structures);
    }

    /**
     * This constructor ensures that the structures in the query
     * are all "provable", meaning that they are capable of
     * proving themselves. Structures cannot achieve this, but
     * they can produce consulting versions of themselves, given
     * an axiom source. Evalutations and comparisons are
     * provable in themselves, and will ignore the axiom
     * source.
     */
    protected Query(AxiomSource as, Scope scope, Structure[] structures) {
        super(as, scope, provableStructures(as, scope, structures));
    }

    /**
     * Create a query from the given structure, to prove itself
     * against the given axiom source.
     *
     * @param   AxiomSource   the source to prove against
     *
     * @param   Structures   the structure to prove
     */
    public Query(AxiomSource as, Structure structure) {
        this(as, new Structure[] {structure}
        );
    }

    /**
     * Create a query from the given structure, to prove itself
     * without any axiom source.
     *
     * For example new Query(new Comparison())
     *
     * @param   AxiomSource   the source to prove against
     *
     * @param   Structure   the structure to prove
     */
    public Query(Structure structure) {
        this(null, new Scope(), new Structure[] {structure}
        );
    }

    /**
     * Returns a string representation of this query.
     *
     * @return a string representation of this query.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < structures.length; i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(structures[i].toString());
        }
        return buf.toString();
    }
}
