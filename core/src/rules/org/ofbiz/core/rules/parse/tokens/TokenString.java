package org.ofbiz.core.rules.parse.tokens;

import java.io.*;
import java.util.*;

/**
 * <p><b>Title:</b> Token String
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
 * A TokenString is like a String, but it is a series of
 * Tokens rather than a series of chars. Once a TokenString is
 * created, it is "immutable", meaning it cannot change. This
 * lets you freely copy TokenStrings without worrying about
 * their state.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class TokenString {
  /**
   * the tokens in this tokenString
   */
  protected Token tokens[];
  
  /**
   * Constructs a tokenString from the supplied tokens.
   *
   * @param   tokens   the tokens to use
   *
   * @return    a tokenString constructed from the supplied
   *            tokens
   */
  public TokenString(Token[] tokens) {
    this.tokens = tokens;
  }
  /**
   * Constructs a tokenString from the supplied string.
   *
   * @param   string   the string to tokenize
   *
   * @return    a tokenString constructed from tokens read from
   *            the supplied string
   */
  public TokenString(String s) {
    this(new Tokenizer(s));
  }
  /**
   * Constructs a tokenString from the supplied reader and
   * tokenizer.
   *
   * @param   Tokenizer   the tokenizer that will produces the
   *                      tokens
   *
   * @return    a tokenString constructed from the tokenizer's
   *            tokens
   */
  public TokenString(Tokenizer t) {
    Vector v = new Vector();
    try {
      while (true) {
        Token tok = t.nextToken();
        if (tok.ttype() == Token.TT_EOF) {
          break;
        }
        v.addElement(tok);
      };
    } catch (IOException e) {
      throw new InternalError(
      "Problem tokenizing string: " + e);
    }
    tokens = new Token[v.size()];
    v.copyInto(tokens);
  }
  /**
   * Returns the number of tokens in this tokenString.
   *
   * @return   the number of tokens in this tokenString
   */
  public int length() {
    return tokens.length;
  }
  /**
   * Returns the token at the specified index.
   *
   * @param    index   the index of the desired token
   *
   * @return   token   the token at the specified index
   */
  public Token tokenAt(int i) {
    return tokens[i];
  }
  /**
   * Returns a string representation of this tokenString.
   *
   * @return   a string representation of this tokenString
   */
  public String toString() {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < tokens.length; i++) {
      if (i > 0) {
        buf.append(" ");
      }
      buf.append(tokens[i]);
    }
    return buf.toString();
  }
}
