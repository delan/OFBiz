package org.ofbiz.core.rules.parse.tokens;


import java.io.*;


/**
 * <p><b>Title:</b> Slash State
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
 * This state will either delegate to a comment-handling
 * state, or return a token with just a slash in it.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class SlashState extends TokenizerState {

    protected SlashStarState slashStarState =
        new SlashStarState();

    protected SlashSlashState slashSlashState =
        new SlashSlashState();

    /**
     * Either delegate to a comment-handling state, or return a
     * token with just a slash in it.
     *
     * @return   either just a slash token, or the results of
     *           delegating to a comment-handling state
     */
    public Token nextToken(
        PushbackReader r, int theSlash, Tokenizer t)
        throws IOException {

        int c = r.read();

        if (c == '*') {
            return slashStarState.nextToken(r, '*', t);
        }
        if (c == '/') {
            return slashSlashState.nextToken(r, '/', t);
        }
        if (c >= 0) {
            r.unread(c);
        }
        return new Token(Token.TT_SYMBOL, "/", 0);
    }
}
