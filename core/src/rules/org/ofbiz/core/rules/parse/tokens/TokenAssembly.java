package org.ofbiz.core.rules.parse.tokens;


import java.util.*;
import org.ofbiz.core.rules.utensil.*;
import org.ofbiz.core.rules.parse.*;


/**
 * <p><b>Title:</b> Token Assembly
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
 * A TokenAssembly is an Assembly whose elements are Tokens.
 * Tokens are, roughly, the chunks of text that a <code>
 * Tokenizer</code> returns.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class TokenAssembly extends Assembly {

    /**
     * the "string" of tokens this assembly will consume
     */
    protected TokenString tokenString;

    /**
     * Constructs a TokenAssembly on a TokenString constructed
     * from the given String.
     *
     * @param   string   the string to consume
     *
     * @return   a TokenAssembly that will consume a tokenized
     *           version of the supplied String
     */
    public TokenAssembly(String s) {
        this(new TokenString(s));
    }

    /**
     * Constructs a TokenAssembly on a TokenString constructed
     * from the given Tokenizer.
     *
     * @param   Tokenizer   the tokenizer to consume tokens
     *                      from
     *
     * @return   a TokenAssembly that will consume a tokenized
     *           version of the supplied Tokenizer
     */
    public TokenAssembly(Tokenizer t) {
        this(new TokenString(t));
    }

    /**
     * Constructs a TokenAssembly from the given TokenString.
     *
     * @param   tokenString   the tokenString to consume
     *
     * @return   a TokenAssembly that will consume the supplied
     *           TokenString
     */
    public TokenAssembly(TokenString tokenString) {
        this.tokenString = tokenString;
    }

    /**
     * Returns a textual representation of the amount of this
     * tokenAssembly that has been consumed.
     *
     * @param   delimiter   the mark to show between consumed
     *                      elements
     *
     * @return   a textual description of the amount of this
     *           assembly that has been consumed
     */
    public String consumed(String delimiter) {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < elementsConsumed(); i++) {
            if (i > 0) {
                buf.append(delimiter);
            }
            buf.append(tokenString.tokenAt(i));
        }
        return buf.toString();
    }

    /**
     * Returns the default string to show between elements
     * consumed or remaining.
     *
     * @return   the default string to show between elements
     *           consumed or remaining
     */
    public String defaultDelimiter() {
        return "/";
    }

    /**
     * Returns the number of elements in this assembly.
     *
     * @return   the number of elements in this assembly
     */
    public int length() {
        return tokenString.length();
    }

    /**
     * Returns the next token.
     *
     * @return   the next token from the associated token string.
     *
     * @exception  ArrayIndexOutOfBoundsException  if there are no
     *             more tokens in this tokenizer's string.
     */
    public Object nextElement() {
        return tokenString.tokenAt(index++);
    }

    /**
     * Shows the next object in the assembly, without removing it
     *
     * @return   the next object
     *
     */
    public Object peek() {
        if (index < length()) {
            return tokenString.tokenAt(index);
        } else {
            return null;
        }
    }

    /**
     * Returns a textual representation of the amount of this
     * tokenAssembly that remains to be consumed.
     *
     * @param   delimiter   the mark to show between consumed
     *                      elements
     *
     * @return   a textual description of the amount of this
     *           assembly that remains to be consumed
     */
    public String remainder(String delimiter) {
        StringBuffer buf = new StringBuffer();

        for (int i = elementsConsumed();
            i < tokenString.length();
            i++) {

            if (i > elementsConsumed()) {
                buf.append(delimiter);
            }
            buf.append(tokenString.tokenAt(i));
        }
        return buf.toString();
    }
}
