package org.ofbiz.core.rules.parse.tokens;

import java.util.*;
import org.ofbiz.core.rules.parse.*;

/**
 * <p><b>Title:</b> Quoted String
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
 * A QuotedString matches a quoted string, like "this one"
 * from a token assembly.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class QuotedString extends Terminal {
  /**
   * Returns true if an assembly's next element is a quoted
   * string.
   *
   * @param   object   an element from a assembly
   *
   * @return   true, if a assembly's next element is a quoted
   *           string, like "chubby cherubim".
   */
  protected boolean qualifies(Object o) {
    Token t = (Token) o;
    return t.isQuotedString();
  }
  /**
   * Create a set with one random quoted string (with 2 to
   * 6 characters).
   */
  public List randomExpansion(int maxDepth, int depth) {
    int n = (int) (5.0 * Math.random());
    
    char[] letters = new char[n + 2];
    letters[0] = '"';
    letters[n + 1] = '"';
    
    for (int i = 0; i < n; i++) {
      int c = (int) (26.0 * Math.random()) + 'a';
      letters[i + 1] = (char) c;
    }
    
    List v = new ArrayList();
    v.add(new String(letters));
    return v;
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
    return "QuotedString";
  }
}
