package org.ofbiz.rules.engine;


/**
 * <p><b>Title:</b> Rule
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
 * <p>A Rule represents a logic statement that a structure is true
 * if a following series of other structures are true.
 * <p>
 * For example,
 * <blockquote><pre>
 *     bachelor(X) :- male(X), unmarried(X);
 * </pre></blockquote>
 * <p>
 * is a logical rule.
 * <p>
 * A rule can make a provable version of itself, a DynamicRule,
 * that is a essentially a copy of the structures in the rule,
 * with a new scope and with a program to consult for other
 * rules.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class Rule implements Axiom {
    protected Structure[] structures;

    /**
     * Construct rule from the given structures.
     *
     * @param Structure[] the structures that make up this rule.
     *
     */
    public Rule(Structure[] structures) {
        this.structures = structures;
    }

    /**
     * Construct a one-structure rule from the given structure.
     *
     * @param Structure the structure that makes up this rule.
     *
     */
    public Rule(Structure s) {
        this(new Structure[] {s}
        );
    }

    /**
     * Return a provable version of this rule.
     *
     * @return a provable version of this rule
     */
    public DynamicAxiom dynamicAxiom(AxiomSource as) {
        return new DynamicRule(as, new Scope(), this);
    }

    /**
     * Returns true if the supplied object is an equivalent
     * rule.
     *
     * @param   object   the object to compare
     *
     * @return   true, if the supplied object's structures equal
     *           this rule's structures
     */
    public boolean equals(Object o) {
        if (!(o instanceof Rule))
            return false;
        Rule r = (Rule) o;

        if (!(structures.length == r.structures.length)) {
            return false;
        }
        for (int i = 0; i < structures.length; i++) {
            if (!(structures[i].equals(r.structures[i]))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return the first structure in this rule.
     *
     * @return the first structure in this rule
     */
    public Structure head() {
        return structures[0];
    }

    /**
     * Returns a string representation of this rule.
     *
     * @return a string representation of this rule.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < structures.length; i++) {
            if (i == 1) {
                buf.append(" :- ");
            }
            if (i > 1) {
                buf.append(", ");
            }
            buf.append(structures[i].toString());
        }
        return buf.toString();
    }
}
