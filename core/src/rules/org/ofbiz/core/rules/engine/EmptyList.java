package org.ofbiz.core.rules.engine;


/**
 * <p><b>Title:</b> Empty List
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
 * <p>The EmptyList is a list with no terms.
 * <p>
 * All lists except this one contain a head, which may be any
 * term, and a tail, which is another list. This recursive
 * defintion terminates with this singleton, the empty list.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class EmptyList extends Fact {

    /**
     * Constructs the empty list singleton.
     */
    protected EmptyList() {
        super(".");
    }

    /**
     * Return true, since an empty list is a list.
     *
     * @return true
     */
    public boolean isList() {
        return true;
    }

    /**
     * Returns a string representation of this list as a part of
     * another list. When the empty list represents itself as part
     * of another list, it just returns "".
     *
     * @return an empty string
     */
    public String listTailString() {
        return "";
    }

    /**
     * Returns a string representation of the empty list.
     *
     * @return   a string representation of the empty list
     */
    public String toString() {
        return "[]";
    }
}
