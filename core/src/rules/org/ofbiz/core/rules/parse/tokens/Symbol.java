package org.ofbiz.core.rules.parse.tokens;


import java.util.*;
import org.ofbiz.core.rules.parse.*;


/**
 * <p><b>Title:</b> Symbol
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
 * A Symbol matches a specific sequence, such as
 * <code><</code>, or <code><=</code> that a tokenizer
 * returns as a symbol.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class Symbol extends Terminal {

    /**
     * the literal to match
     */
    protected Token symbol;

    /**
     * Constructs a symbol that will match the specified char.
     *
     * @param   char   the character to match. The char must be
     *                 one that the tokenizer will return as a
     *                 symbol token. This typically includes most
     *                 characters except letters and digits.
     *
     * @return   a symbol that will match the specified char
     */
    public Symbol(char c) {
        this(String.valueOf(c));
    }

    /**
     * Constructs a symbol that will match the specified sequence
     * of characters.
     *
     * @param   String   the characters to match. The characters
     *                   must be a sequence that the tokenizer will
     *                   return as a symbol token, such as
     *                   <code><=</code>.
     *
     * @return   a Symbol that will match the specified sequence
     *           of characters
     */
    public Symbol(String s) {
        symbol = new Token(Token.TT_SYMBOL, s, 0);
    }

    /**
     * Returns true if the symbol this object represents equals an
     * assembly's next element.
     *
     * @param   object   an element from an assembly
     *
     * @return   true, if the specified symbol equals the next
     *           token from an assembly
     */
    protected boolean qualifies(Object o) {
        return symbol.equals((Token) o);
    }

    /**
     * Returns a textual description of this parser.
     *
     * @param   vector   a list of parsers already printed in
     *                   this description
     *
     * @return   string   a textual description of this parser
     *
     * @see Parser#toString()
     */
    public String unvisitedString(List visited) {
        return symbol.toString();
    }
}
