package org.ofbiz.core.rules.engine;


import java.util.*;


/**
 * <p><b>Title:</b> Unification
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
 * <p>A Unification is a collection of variables.
 *
 * Structures and variables use unifications to keep track of the
 * variable assignments that make a proof work. The unification
 * class itself provides behavior for adding and accessing
 * variables.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */

public class Unification {
    public static final Unification empty = new Unification();
    List vector;

    /**
     * Creates an empty unification.
     */
    public Unification() {}

    /**
     * Creates a unification that starts off including a single
     * variable.
     *
     * @param Variable the variable with which the unification
     *        begins
     */
    public Unification(Variable v) {
        addVariable(v);
    }

    /**
     * Adds a variable to this unification.
     *
     * @param   Variable  the variable to add to this unification
     *
     * @return this unification
     */
    public Unification addVariable(Variable v) {
        if (!vector().contains(v)) {
            vector.add(v);
        }
        return this;
    }

    /**
     * Adds all the variables of another unification to this one.
     *
     * @param Unification the unification to append
     *
     * @return this unification
     */
    public Unification append(Unification u) {
        for (int i = 0; i < u.size(); i++) {
            addVariable(u.variableAt(i));
        }
        return this;
    }

    /**
     * Return the variables in this unification.
     *
     * @return the variables in this unification.
     */
    public Enumeration elements() {
        return Collections.enumeration(vector());
    }

    /**
     * Returns the number of variables in this unification.
     *
     * @return   int   the number of variables in this unification
     */
    public int size() {
        if (vector == null) {
            return 0;
        }
        return vector.size();
    }

    /**
     * Returns a string representation of this unification.
     *
     * @return   a string representation of this unification
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < size(); i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(variableAt(i).definitionString());
        }
        return buf.toString();
    }

    /**
     * Returns a string representation of this unification,
     * without printing variable names.
     *
     * @return   a string representation of this unification,
     *           without printing variable names
     */
    public String toStringQuiet() {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < size(); i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(variableAt(i));
        }
        return buf.toString();
    }

    /**
     * Asks all the contained variables to unbind.
     */
    public void unbind() {
        for (int i = 0; i < size(); i++) {
            variableAt(i).unbind();
        }
    }

    /**
     * Returns the variable at the indicated index.
     *
     * @param   int   the index of the variable to return
     *
     * @return   variable   the variable at the indicated index
     */
    protected Variable variableAt(int i) {
        return (Variable) vector().get(i);
    }

    /**
     * lazy-initialize this unification's vector
     */
    protected List vector() {
        if (vector == null) {
            vector = new ArrayList();
        }
        return vector;
    }
}
