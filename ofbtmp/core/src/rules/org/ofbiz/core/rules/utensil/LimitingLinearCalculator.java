package org.ofbiz.core.rules.utensil;


/**
 * <p><b>Title:</b> Limiting Linear Calculator
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
 * <p>A LimitingLinearCalculator is a LinearCalculator where the
 * data points given in the constructor limit the extrapoltation.
 *
 * The X value of a LimitingLinearCalculator will never be
 * below the minimum of xFrom and xTo, and will never be above
 * the maximum of these two.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class LimitingLinearCalculator extends LinearCalculator {

    /**
     * Create a LimitingLinearCalculator from known points on two scales.
     *
     * @param double xFrom
     * @param double xTo
     * @param double yFrom
     * @param double yTo
     */
    public LimitingLinearCalculator(double xFrom, double xTo, double yFrom, double yTo) {
        super(xFrom, xTo, yFrom, yTo);
    }

    /**
     * Return the value on the first scale, corresponding to the given
     * value on the second scale. Limit the X value to be between xFrom
     * and xTo.
     *
     * @return the value on the first scale, corresponding to the given
     *         value on the second scale
     */
    public double calculateXforGivenY(double y) {
        if (y <= yTo && y <= yFrom) {
            return yFrom <= yTo ? xFrom : xTo;
        }
        if (y >= yTo && y >= yFrom) {
            return yFrom >= yTo ? xFrom : xTo;
        }
        return super.calculateXforGivenY(y);
    }

    /**
     * Return the value on the second scale, corresponding to the given
     * value on the first scale. Limit the Y value to be between yFrom
     * and yTo.
     *
     * @return the value on the second scale, corresponding to the given
     *         value on the first scale
     */
    public double calculateYforGivenX(double x) {
        if (x <= xTo && x <= xFrom) {
            return xFrom <= xTo ? yFrom : yTo;
        }
        if (x >= xTo && x >= xFrom) {
            return xFrom >= xTo ? yFrom : yTo;
        }
        return super.calculateYforGivenX(x);
    }
}
