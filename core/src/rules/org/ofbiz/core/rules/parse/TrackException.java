package org.ofbiz.core.rules.parse;


/**
 * <p><b>Title:</b> Track Exception
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
 * Signals that a parser could not match text after
 * a specific point.
 */
public class TrackException extends RuntimeException {
    protected String after, expected, found;

    /**
     * Constructs a <code>TrackException</code> with the
     * specified reasons for the exception.
     *
     * @param   after   an indication of what text was parsed
     *
     * @param   expected   an indication of what kind of thing
     *                     was expected, such as a ')' token
     *
     * @param   found   the text the thrower actually found
     */
    public TrackException(
        String after, String expected, String found) {

        super("After   : " + after +
            "\nExpected: " + expected +
            "\nFound   : " + found);
        this.after = after;
        this.found = found;
        this.expected = expected;
    }

    /**
     * Returns some indication of what text was interpretable.
     *
     * @return   some indication of what text was interpretable
     */
    public String getAfter() {
        return after;
    }

    /**
     * Returns some indication of what kind of thing was
     * expected, such as a ')' token.
     *
     * @return   some indication of what kind of thing was
     *           expected, such as a ')' token
     */
    public String getExpected() {
        return expected;
    }

    /**
     * Returns the text element the thrower actually found when
     * it expected something else.
     *
     * @return   the text element the thrower actually found
     *           when it expected something else
     */
    public String getFound() {
        return found;
    }
}
