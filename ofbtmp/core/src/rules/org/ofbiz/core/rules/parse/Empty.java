package org.ofbiz.core.rules.parse;


import java.util.*;


/**
 * <p><b>Title:</b> Empty
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
 * <p>An <code>Empty</code> parser matches any assembly once,
 * and applies its assembler that one time.
 * <p>
 * Language elements often contain empty parts. For example,
 * a language may at some point allow a list of parameters
 * in parentheses, and may allow an empty list. An empty
 * parser makes it easy to match, within the
 * parenthesis, either a list of parameters or "empty".
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class Empty extends Parser {

    /**
     * Accept a "visitor" and a collection of previously visited
     * parsers.
     *
     * @param   pv   the visitor to accept
     * @param   visited   a collection of previously visited parsers
     */
    public void accept(ParserVisitor pv, List visited) {
        pv.visitEmpty(this, visited);
    }

    /**
     * Given a set of assemblies, this method returns the set as
     * a successful match.
     *
     * @return   the input set of states
     * @param   in   a vector of assemblies to match against
     */
    public List match(List in) {
        return elementClone(in);
    }

    /**
     * There really is no way to expand an empty parser, so
     * return an empty vector.
     */
    protected List randomExpansion(int maxDepth, int depth) {
        return new ArrayList();
    }

    /**
     * Returns a textual description of this parser.
     */
    protected String unvisitedString(List visited) {
        return " empty ";
    }
}
