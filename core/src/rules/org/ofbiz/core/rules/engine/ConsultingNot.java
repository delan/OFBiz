package org.ofbiz.core.rules.engine;


/**
 * <p><b>Title:</b> Consulting Not
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
 * <p>A ConsultingNot is a Not that has an axiom source to consult.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */

public class ConsultingNot extends Gateway {
    ConsultingStructure consultingStructure;

    /**
     * Contructs a ConsultingNot from the specified consulting
     * structure. This constructor is for use by Not.
     */
    protected ConsultingNot(ConsultingStructure consultingStructure) {

        super(consultingStructure.functor, consultingStructure.terms);
        this.consultingStructure = consultingStructure;
    }

    /**
     * Returns <code>false</code> if there is any way to prove this
     * structure.
     *
     * @return <code>false</code> if there is any way to prove
     *         this structure
     */
    public boolean canProveOnce() {
        return !(consultingStructure.canUnify() &&
                consultingStructure.resolvent.canEstablish());
    }

    /**
     * After succeeding once, unbind any variables bound during
     * the successful proof, and set the axioms to begin
     * again at the beginning.
     */
    protected void cleanup() {
        consultingStructure.unbind();
        consultingStructure.axioms = null;
    }

    /**
     * Returns a string description of this Not.
     *
     * @return a string description of this Not
     */
    public String toString() {
        return "not " + consultingStructure;
    }
}
