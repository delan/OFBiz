package org.ofbiz.core.rules.engine;


/**
 * <p><b>Title:</b> Anonymous Variable
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
 * <p>An anonymous variable unifies successfully with any other
 * term, without binding to the term.
 * <p>
 * Anonymous variables are useful for screening out unwanted
 * terms. For example, if a program describes a marriage in
 * terms of an id, the husband, wife, and the beginning and
 * end dates of the marriage, its facts might look something
 * like:
 * <blockquote><pre>
 *     marriage(001, balthasar, grimelda, 14560512, 14880711);
 *     // ...
 *     marriage(257, kevin, karla, 19790623, present);
 * </pre></blockquote>
 * A rule that extracts just the husband from
 * <code>marriage</code> facts is:
 * <blockquote><pre>
 *     husband(Id, Hub) :- marriage(Id, Hub, _, _, _);
 * </pre></blockquote>
 * The underscores in this rule represent anonymous variables.
 * When the rule executes, it will unify its
 * <code>marriage</code> structure with <code>marriage</code>
 * facts, without regard to the last three terms of those
 * facts.
 * <p>
 * Without anonymous variables, the <code>husband</code> rule
 * would need three unused variables. Note that the following
 * approach would fail:
 * <blockquote><pre>
 *     husband(Id, Hub) :-
 *         marriage(Id, Hub, Anon, Anon, Anon); // wrong
 * </pre></blockquote>
 * This approach, while tempting, will not work because the
 * variable <code>Anon</code> will bind to the structures it
 * encounters. Issued against the example program,
 * <code>Anon</code> will first bind to <code>grimelda</code>.
 * Next, <code>Anon</code> will attempt to bind to
 * <code>14560512</code>. This will fail, since
 * <code>Anon</code> will already be bound to
 * <code>grimelda</code>.
 * <p>
 * The essential behavior anonymous variables provide is that
 * they unify successfully without binding.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */

public class Anonymous extends Variable {

    /**
     * Constructs an anonymous variable.
     */
    public Anonymous() {
        super("_");
    }

    /**
     * Returns this anonymous variable, which does not unify
     * with anything and thus does not need to copy itself.
     *
     * @return this anonymous variable
     *
     * @param AxiomSource ignored
     *
     * @param Scope ignored
     */
    public Term copyForProof(AxiomSource ignored, Scope ignored2) {
        return this;
    }

    /**
     * Return the value of this anonymous variable to use in
     * functions; this is meaningless in logic programming,
     * but the method returns the name of this variable.
     *
     * @return the name of the anonymous variable
     */
    public Object eval() {
        return name;
    }

    /**
     * Returns an empty unification.
     * <p>
     * The <code>unify</code> methods indicate failure by
     * returning <code>null</code>. Anonymous variables succeed
     * without binding, so they always return empty unifications.
     *
     * @param Structure ignored
     *
     * @return A successful, but empty, unification
     */
    public Unification unify(Structure ignored) {
        return Unification.empty;
    }

    /**
     * Returns an empty unification.
     *
     * @param Term ignored
     *
     * @return an empty unification
     */
    public Unification unify(Term ignored) {
        return Unification.empty;
    }

    /**
     * Returns an empty unification.
     *
     * @param Variable ignored
     *
     * @return an empty unification
     */
    public Unification unify(Variable ignored) {
        return Unification.empty;
    }

    /**
     * Returns an empty unification.
     *
     * @return   an empty unification
     */
    public Unification variables() {
        return Unification.empty;
    }
}
