package org.ofbiz.core.rules.engine;


/**
 * <p><b>Title:</b> Arithmetic Operator
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
 * <p>An ArithmeticOperator represents an arithmetic operation
 * that will perform itself as part of a proof.
 * <p>
 * An ArithmeticOperator has an operator and two terms. The
 * operator must be '+', '-', '/', '*' or '%', or else the
 * eval() value of this object will always be 0. The
 * terms may be other arithmetic operators, variables, or
 * number structures.
 * <p>
 * For example, an ArithmeticOperator might be appear in a
 * comparison, as follows:
 * <blockquote><pre>
 *     >(+(X, 3), 42)
 * </pre></blockquote>
 * The arithmetic operator will have a valid value if X is
 * instantiated to a NumberStructure object. If X is
 * instantiated to, say, 40, then the arithmetic operator's
 * reply to eval() will be 47, and the comparison
 * will succeed.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */

public class ArithmeticOperator extends Structure implements ArithmeticTerm {
    protected char operator;
    protected ArithmeticTerm term0;
    protected ArithmeticTerm term1;

    /**
     * Constructs an arithmetic operator with the indicated operator and
     * terms.
     * <p>
     * The operator must be '+', '-', '/', '*' or '%', or else the
     * eval() value of this object will always be 0. The terms must be
     * other arithmetic operators, variables, or number structures. If either
     * term is invalid, this object will throw an EvaluationException during a
     * proof.
     *
     * @param  char the operator
     *
     * @param  ArithmeticTerm the first term
     *
     * @param  ArithmeticTerm the second term
     *
     */
    public ArithmeticOperator(char operator, ArithmeticTerm term0, ArithmeticTerm term1) {
        super(new Character(operator), new Term[] {term0, term1}
        );
        this.operator = operator;
        this.term0 = term0;
        this.term1 = term1;
    }

    /** Do the math. */
    protected Object arithmeticValue(double d0, double d1) {
        double result = 0;

        switch (operator) {
        case '+':
            result = d0 + d1;
            break;

        case '-':
            result = d0 - d1;
            break;

        case '*':
            result = d0 * d1;
            break;

        case '/':
            result = d0 / d1;
            break;

        case '%':
            result = d0 % d1;
            break;

        default:
            result = 0.0;
        }
        return new Double(result);
    }

    /**
     * Create a copy using the supplied scope for variables.
     *
     * @param AxiomSource ignored
     *
     * @param Scope the scope to use for variables
     *
     * @return a copy with variables from the supplied scope
     */
    public Term copyForProof(AxiomSource ignored, Scope scope) {
        return new ArithmeticOperator(
                operator,
                (ArithmeticTerm) term0.copyForProof(null, scope),
                (ArithmeticTerm) term1.copyForProof(null, scope));
    }

    /**
     * Returns the result of applying this object's operator
     * against the  arithmetic values of its two terms. For
     * example,
     *
     * <blockquote><pre>
     *     NumberStructure two = new NumberStructure(2);
     *     ArithmeticOperator x, y;
     *     x = new ArithmeticOperator('*', two, two);
     *     y = new ArithmeticOperator('+', x, two);
     *     System.out.println(y + " = " + y.eval());
     * </pre></blockquote>
     *
     * prints out:
     *
     * <blockquote><pre>
     *     +(*(2, 2), 2) = 6.0
     * </pre></blockquote>
     *
     * @return the result of applying this object's operator to
     *         the arithmetic value of its two terms
     *
     * @exception EvaluationException if either term is not a
     *                                valid arithmetic value
     */
    public Object eval() {

        double d0 = eval(term0);
        double d1 = eval(term1);

        return arithmeticValue(d0, d1);
    }

    /** get the "double" value of this term */
    protected double eval(ArithmeticTerm t) {
        Object o = t.eval();

        if (o == null) {
            throw new EvaluationException(
                    t + " is undefined in " + this);
        }
        if (!(o instanceof Number)) {
            throw new EvaluationException(
                    t + " is not a number in " + this);
        }
        Number n = (Number) o;

        return n.doubleValue();
    }
}
