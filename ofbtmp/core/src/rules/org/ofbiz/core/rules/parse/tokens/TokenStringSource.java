package org.ofbiz.core.rules.parse.tokens;


import java.io.*;
import java.util.*;


/**
 * <p><b>Title:</b> Token String Source
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
 * A TokenStringSource enumerates over a specified reader,
 * returning TokenStrings delimited by a specified delimiter.
 * <p>
 * For example,
 * <blockquote><pre>
 *
 *    String s = "I came; I saw; I left in peace;";
 *
 *    TokenStringSource tss =
 *        new TokenStringSource(new Tokenizer(s), ";");
 *
 *    while (tss.hasMoreTokenStrings()) {
 *        System.out.println(tss.nextTokenString());
 *    }
 *
 * </pre></blockquote>
 *
 * prints out:
 *
 * <blockquote><pre>
 *     I came
 *     I saw
 *     I left in peace
 * </pre></blockquote>
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class TokenStringSource {
    protected Tokenizer tokenizer;
    protected String delimiter;
    protected TokenString cachedTokenString = null;

    /**
     * Constructs a TokenStringSource that will read TokenStrings
     * using the specified tokenizer, delimited by the specified
     * delimiter.
     *
     * @param   tokenizer   a tokenizer to read tokens from
     *
     * @param   delimiter   the character that fences off where one
     *                      TokenString ends and the next begins
     *
     * @returns   a TokenStringSource that will read TokenStrings
     *            from the specified tokenizer, delimited by the
     *            specified delimiter
     */
    public TokenStringSource(
        Tokenizer tokenizer, String delimiter) {

        this.tokenizer = tokenizer;
        this.delimiter = delimiter;
    }

    /**
     * The design of <code>nextTokenString</code> is that is
     * always returns a cached value. This method will (at least
     * attempt to) load the cache if the cache is empty.
     */
    protected void ensureCacheIsLoaded() {
        if (cachedTokenString == null) {
            loadCache();
        }
    }

    /**
     * Returns true if the source has more TokenStrings.
     *
     * @return   true, if the source has more TokenStrings that
     *           have not yet been popped with <code>
     *           nextTokenString</code>.
     */
    public boolean hasMoreTokenStrings() {
        ensureCacheIsLoaded();
        return cachedTokenString != null;
    }

    /**
     * Loads the next TokenString into the cache, or sets the
     * cache to null if the source is out of tokens.
     */
    protected void loadCache() {
        List tokenVector = nextVector();

        if (tokenVector.isEmpty()) {
            cachedTokenString = null;
        } else {
            Token tokens[] = (Token[]) tokenVector.toArray(new Token[tokenVector.size()]);

            cachedTokenString = new TokenString(tokens);
        }
    }

    /**
     * Shows the example in the class comment.
     *
     * @param args ignored
     */
    public static void main(String args[]) {

        String s = "I came; I saw; I left in peace;";

        TokenStringSource tss =
            new TokenStringSource(new Tokenizer(s), ";");

        while (tss.hasMoreTokenStrings()) {
            System.out.println(tss.nextTokenString());
        }
    }

    /**
     * Returns the next TokenString from the source.
     *
     * @return   the next TokenString from the source
     */
    public TokenString nextTokenString() {
        ensureCacheIsLoaded();
        TokenString returnTokenString = cachedTokenString;

        cachedTokenString = null;
        return returnTokenString;
    }

    /**
     * Returns a List of the tokens in the source up to either
     * the delimiter or the end of the source.
     *
     * @return   a List of the tokens in the source up to either
     *           the delimiter or the end of the source.
     */
    protected List nextVector() {
        List v = new ArrayList();

        try {
            while (true) {
                Token tok = tokenizer.nextToken();

                if (tok.ttype() == Token.TT_EOF ||
                    tok.sval().equals(delimiter)) {

                    break;
                }
                v.add(tok);
            }
        } catch (IOException e) {
            throw new InternalError(
                    "Problem tokenizing string: " + e);
        }
        return v;
    }
}
