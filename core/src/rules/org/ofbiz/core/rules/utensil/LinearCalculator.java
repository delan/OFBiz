package org.ofbiz.core.rules.utensil;


/**
 * <p><b>Title:</b> Linear Calculator
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
 * <p>A LinearCalculator models two variables that vary
 * linearly with each other.
 *
 * For example, Fahrenheit and Celsius temperate scales vary
 * linearly. Fahrenheit temperature varies from 32 to 212 as
 * Celsius varies 0 to 100. A LinearCalculator can model the
 * whole scale, if it is created as:
 *
 * <blockquote><pre>
 *
 *     LinearCalculator lc =
 *         new LinearCalculator(32, 212, 0, 100);
 *     System.out.println(lc.calculateYforGivenX(68));
 *
 *  </pre></blockquote>
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class LinearCalculator {
    double xFrom;
    double xTo;
    double yFrom;
    double yTo;

    /**
     * Create a LinearCalculator from known points on two scales.
     *
     * @param double xFrom
     * @param double xTo
     * @param double yFrom
     * @param double yTo
     */
    public LinearCalculator(double xFrom, double xTo, double yFrom, double yTo) {
        this.xFrom = xFrom;
        this.xTo = xTo;
        this.yFrom = yFrom;
        this.yTo = yTo;
    }

    /**
     * Return the value on the first scale, corresponding to the given
     * value on the second scale.
     *
     * @return the value on the first scale, corresponding to the given
     *         value on the second scale
     */
    public double calculateXforGivenY(double y) {
        if (yTo == yFrom) {
            return (xFrom + xTo) / 2;
        }
        return (y - yFrom) / (yTo - yFrom) * (xTo - xFrom) + xFrom;
    }

    /**
     * Return the value on the second scale, corresponding to the given
     * value on the first scale.
     *
     * @return the value on the second scale, corresponding to the given
     *         value on the first scale
     */
    public double calculateYforGivenX(double x) {
        if (xTo == xFrom) {
            return (yFrom + yTo) / 2;
        }
        return (x - xFrom) / (xTo - xFrom) * (yTo - yFrom) + yFrom;
    }

    /**
     * Show the example in the class comment.
     *
     * @param  args  ignored.
     */
    public static void main(String args[]) {
        LinearCalculator lc = new LinearCalculator(32, 212, 0, 100);

        System.out.println(lc.calculateYforGivenX(68));
    }

    /**
     * Return a textual description of this object.
     *
     * @return a textual description of this object
     */
    public String toString() {
        return   "" + xFrom + ":" + xTo +
            "::" + yFrom + ":" + yTo;
    }
}
