package org.ofbiz.core.rules.parse.tokens;

import java.io.*;

/**
 * <p><b>Title:</b> Number State
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
 * A NumberState object returns a number from a reader. This
 * state's idea of a number allows an optional, initial
 * minus sign, followed by one or more digits. A decimal
 * point and another string of digits may follow these
 * digits.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class NumberState extends TokenizerState {
  protected int c;
  protected double value;
  protected boolean absorbedLeadingMinus;
  protected boolean absorbedDot;
  protected boolean gotAdigit;
  /**
   * Convert a stream of digits into a number, making this
   * number a fraction if the boolean parameter is true.
   */
  protected double absorbDigits(
  PushbackReader r, boolean fraction) throws IOException {
    
    int divideBy = 1;
    double v = 0;
    while ('0' <= c && c <= '9') {
      gotAdigit = true;
      v = v * 10 + (c - '0');
      c = r.read();
      if (fraction) {
        divideBy *= 10;
      }
    }
    if (fraction) {
      v = v / divideBy;
    }
    return v;
  }
  /**
   * Return a number token from a reader.
   *
   * @return a number token from a reader
   */
  public Token nextToken(
  PushbackReader r, int cin, Tokenizer t)
  throws IOException {
    
    reset(cin);
    parseLeft(r);
    parseRight(r);
    r.unread(c);
    return value(r, t);
  }
  /**
   * Parse up to a decimal point.
   */
  protected void parseLeft(PushbackReader r)
  throws IOException {
    
    if (c == '-') {
      c = r.read();
      absorbedLeadingMinus = true;
    }
    value = absorbDigits(r, false);
  }
  /**
   * Parse from a decimal point to the end of the number.
   */
  protected void parseRight(PushbackReader r)
  throws IOException {
    
    if (c == '.') {
      c = r.read();
      absorbedDot = true;
      value += absorbDigits(r, true);
    }
  }
  /**
   * Prepare to assemble a new number.
   */
  protected void reset(int cin) {
    c = cin;
    value = 0;
    absorbedLeadingMinus = false;
    absorbedDot = false;
    gotAdigit = false;
  }
  /**
   * Put together the pieces of a number.
   */
  protected Token value(PushbackReader r, Tokenizer t)
  throws IOException {
    
    if (!gotAdigit) {
      if (absorbedLeadingMinus && absorbedDot) {
        r.unread('.');
        return t.symbolState().nextToken(r, '-', t);
      }
      if (absorbedLeadingMinus) {
        return t.symbolState().nextToken(r, '-', t);
      }
      if (absorbedDot) {
        return t.symbolState().nextToken(r, '.', t);
      }
    }
    if (absorbedLeadingMinus) {
      value = -value;
    }
    return new Token(Token.TT_NUMBER, "", value);
  }
}
