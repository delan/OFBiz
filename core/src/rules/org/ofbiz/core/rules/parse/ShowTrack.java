package org.ofbiz.core.rules.parse;


import org.ofbiz.core.rules.parse.tokens.*;


/**
 * <p><b>Title:</b> Show Track
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
 * Show some examples of using a <code>Track</code>.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class ShowTrack {

    /**
     * Return a parser that will recognize a list, for the
     * grammar:
     *
     *     list       = '(' contents ')';
     *     contents   = empty | actualList;
     *     actualList = Word (',' Word)*;
     */
    public static Parser list() {

        Parser empty, commaWord, actualList, contents, list;

        empty = new Empty();

        commaWord = new Track().add(new Symbol(',').discard()).add(new Word());

        actualList = new Sequence().add(new Word()).add(new Repetition(commaWord));

        contents = new Alternation().add(empty).add(actualList);

        list = new Track().add(new Symbol('(').discard()).add(contents).add(new Symbol(')').discard());

        return list;
    }

    /**
     * Show some examples of using a <code>Track</code>.
     */
    public static void main(String args[]) {

        Parser list = list();

        String test[] = new String[] {
                "()",
                "(pilfer)",
                "(pilfer, pinch)",
                "(pilfer, pinch, purloin)",
                "(pilfer, pinch,, purloin)",
                "(",
                "(pilfer",
                "(pilfer, ",
                "(, pinch, purloin)",
                "pilfer, pinch"};

        System.out.println("Using parser: " + list);
        for (int i = 0; i < test.length; i++) {
            System.out.println("---\ntesting: " + test[i]);
            TokenAssembly a = new TokenAssembly(test[i]);

            try {
                Assembly out = list.completeMatch(a);

                if (out == null) {
                    System.out.println(
                        "list.completeMatch() returns null");
                } else {
                    Object s = list.completeMatch(a).getStack();

                    System.out.println("Ok, stack is: " + s);
                }
            } catch (TrackException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
