package org.ofbiz.core.rules.parse.tokens;


import java.io.*;


/**
 * <p><b>Title:</b> Word State
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
 * A wordState returns a word from a reader. Like other
 * states, a tokenizer transfers the job of reading to this
 * state, depending on an initial character. Thus, the
 * tokenizer decides which characters may begin a word, and
 * this state determines which characters may appear as a
 * second or later character in a word. These are typically
 * different sets of characters; in particular, it is typical
 * for digits to appear as parts of a word, but not as the
 * initial character of a word.
 *
 * <p>
 * By default, the following characters may appear in a word.
 * The method <code>setWordChars()</code> allows customizing
 * this.
 *
 * <blockquote><pre>
 *     From    To
 *      'a', 'z'
 *      'A', 'Z'
 *      '0', '9'
 *
 *     as well as: minus sign, underscore, and apostrophe.
 *
 * </pre></blockquote>
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class WordState extends TokenizerState {
    protected char charbuf[] = new char[16];
    protected boolean wordChar[] = new boolean[256];

    /**
     * Constructs a word state with a default idea of what
     * characters are admissible inside a word (as described in
     * the class comment).
     *
     * @return   a state for recognizing a word
     */
    public WordState() {
        setWordChars('a', 'z', true);
        setWordChars('A', 'Z', true);
        setWordChars('0', '9', true);
        setWordChars('-', '-', true);
        setWordChars('_', '_', true);
        setWordChars('\'', '\'', true);
        setWordChars(0xc0, 0xff, true);
    }

    /**
     * Fatten up charbuf as necessary.
     */
    protected void checkBufLength(int i) {
        if (i >= charbuf.length) {
            char nb[] = new char[charbuf.length * 2];

            System.arraycopy(charbuf, 0, nb, 0, charbuf.length);
            charbuf = nb;
        }
    }

    /**
     * Return a word token from a reader.
     *
     * @return a word token from a reader
     */
    public Token nextToken(PushbackReader r, int c, Tokenizer t)
        throws IOException {

        int i = 0;

        do {
            checkBufLength(i);
            charbuf[i++] = (char) c;
            c = r.read();
        } while (wordChar(c));

        if (c >= 0) {
            r.unread(c);
        }
        String sval = String.copyValueOf(charbuf, 0, i);

        return new Token(Token.TT_WORD, sval, 0);
    }

    /**
     * Establish characters in the given range as valid
     * characters for part of a word after the first character.
     * Note that the tokenizer must determine which characters
     * are valid as the beginning character of a word.
     *
     * @param   from   char
     *
     * @param   to   char
     *
     * @param   boolean   true, if this state should allow
     *                    characters in the given range as part
     *                    of a word
     */
    public void setWordChars(int from, int to, boolean b) {
        for (int i = from; i <= to; i++) {
            if (i >= 0 && i < wordChar.length) {
                wordChar[i] = b;
            }
        }
    }

    /**
     * Just a test of the wordChar array.
     */
    protected boolean wordChar(int c) {
        if (c >= 0 && c < wordChar.length) {
            return wordChar[c];
        }
        return false;
    }
}
