package org.ofbiz.core.rules.parse.tokens;


import java.util.*;
import org.ofbiz.core.rules.parse.*;


/**
 * <p><b>Title:</b> Upper Case Word
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
 * This class shows the how to introduce a new type of
 * terminal, specifically for recognizing uppercase words.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class UppercaseWord extends Word {

    /**
     * Returns true if an assembly's next element is an upper
     * case word.
     *
     * @param   object   an element from a assembly
     *
     * @return   true, if a assembly's next element is an upper
     *           case word
     */
    protected boolean qualifies(Object o) {
        Token t = (Token) o;

        if (!t.isWord()) {
            return false;
        }
        String word = t.sval();

        return word.length() > 0 &&
            Character.isUpperCase(word.charAt(0));
    }

    /**
     * Create a set with one random uppercase word (with 3
     * to 7 characters).
     */
    public List randomExpansion(int maxDepth, int depth) {
        int n = (int) (5.0 * Math.random()) + 3;

        char[] letters = new char[n];

        for (int i = 0; i < n; i++) {
            int c = (int) (26.0 * Math.random()) + 'A';

            letters[i] = (char) c;
        }

        List v = new ArrayList();

        v.add(new String(letters));
        return v;
    }

    /**
     * Returns a textual description of this production.
     *
     * @param   vector   a list of productions already printed
     *                   in this description
     *
     * @return   string   a textual description of this production
     *
     * @see ProductionRule#toString()
     */
    public String unvisitedString(List visited) {
        return "Word";
    }
}
