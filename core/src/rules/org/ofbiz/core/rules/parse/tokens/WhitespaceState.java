package org.ofbiz.core.rules.parse.tokens;

import java.io.*;

/**
 * <p><b>Title:</b> Whitespace State
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
 * A whitespace state ignores whitespace (such as blanks
 * and tabs), and returns the tokenizer's next token. By
 * default, all characters from 0 to 32 are whitespace.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class WhitespaceState extends TokenizerState {
  protected boolean whitespaceChar[] = new boolean[256];
  /**
   * Constructs a whitespace state with a default idea of what
   * characters are, in fact, whitespace.
   *
   * @return   a state for ignoring whitespace
   */
  public WhitespaceState() {
    setWhitespaceChars(0, ' ', true);
  }
  /**
   * Ignore whitespace (such as blanks and tabs), and return
   * the tokenizer's next token.
   *
   * @return the tokenizer's next token
   */
  public Token nextToken(
  PushbackReader r, int aWhitespaceChar, Tokenizer t)
  throws IOException {
    
    int c;
    do {
      c = r.read();
    } while (
    c >= 0 &&
    c < whitespaceChar.length &&
    whitespaceChar[c]);
    
    if (c >= 0) {
      r.unread(c);
    }
    return t.nextToken();
  }
  /**
   * Establish the given characters as whitespace to ignore.
   *
   * @param   first   char
   *
   * @param   second   char
   *
   * @param   boolean   true, if this state should ignore
   *                    characters in the given range
   */
  public void setWhitespaceChars(int from, int to, boolean b) {
    for (int i = from; i <= to; i++) {
      if (i >= 0 && i < whitespaceChar.length) {
        whitespaceChar[i] = b;
      }
    }
  }
}
