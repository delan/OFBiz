package org.ofbiz.core.rules.parse.tokens;

import java.io.*;

/**
 * <p><b>Title:</b> Tokenizer State
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
 * A tokenizerState returns a token, given a reader, an
 * initial character read from the reader, and a tokenizer
 * that is conducting an overall tokenization of the reader.
 * The tokenizer will typically have a character state table
 * that decides which state to use, depending on an initial
 * character. If a single character is insufficient, a state
 * such as <code>SlashState</code> will read a second
 * character, and may delegate to another state, such as
 * <code>SlashStarState</code>. This prospect of delegation is
 * the reason that the <code>nextToken()</code> method has a
 * tokenizer argument.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public abstract class TokenizerState {
  /**
   * Return a token that represents a logical piece of a reader.
   *
   * @return  a token that represents a logical piece of the
   *          reader
   * @param   PushbackReader   a reader to read from
   * @param   c   the character that a tokenizer used to
   *              determine to use this state
   * @param   Tokenizer   the tokenizer conducting the overall
   *                      tokenization of the reader
   * @exception   IOException   if there is any problem reading
   */
  public abstract Token nextToken(PushbackReader r, int c, Tokenizer t) throws IOException;
}
