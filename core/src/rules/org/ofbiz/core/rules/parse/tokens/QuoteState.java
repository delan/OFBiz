package org.ofbiz.core.rules.parse.tokens;

import java.io.*;

/**
 * <p><b>Title:</b> Quote State
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
 * A quoteState returns a quoted string token from a reader.
 * This state will collect characters until it sees a match
 * to the character that the tokenizer used to switch to
 * this state. For example, if a tokenizer uses a double-
 * quote character to enter this state, then <code>
 * nextToken()</code> will search for another double-quote
 * until it finds one or finds the end of the reader.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class QuoteState extends TokenizerState {
  protected char charbuf[] = new char[16];
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
   * Return a quoted string token from a reader. This method
   * will collect characters until it sees a match to the
   * character that the tokenizer used to switch to this
   * state.
   *
   * @return a quoted string token from a reader
   */
  public Token nextToken(
  PushbackReader r, int cin, Tokenizer t)
  throws IOException {
    
    int i = 0;
    charbuf[i++] = (char) cin;
    int c;
    do {
      c = r.read();
      if (c < 0) {
        c = cin;
      }
      checkBufLength(i);
      charbuf[i++] = (char) c;
    } while (c != cin);
    
    String sval = String.copyValueOf(charbuf, 0, i);
    return new Token(Token.TT_QUOTED, sval, 0);
  }
}
