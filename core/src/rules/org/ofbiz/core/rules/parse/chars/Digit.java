package org.ofbiz.core.rules.parse.chars;

import java.util.*;
import org.ofbiz.core.rules.parse.*;

/**
 * <p><b>Title:</b> Digit
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
 * <p>A Digit matches a digit from a character assembly.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class Digit extends Terminal {
  /**
   * Returns true if an assembly's next element is a digit.
   *
   * @param   object   an element from an assembly
   * @return   true, if an assembly's next element is a digit
   */
  public boolean qualifies(Object o) {
    Character c = (Character) o;
    return Character.isDigit(c.charValue());
  }
  /**
   * Create a set with one random digit.
   */
  public Vector randomExpansion(int maxDepth, int depth) {
    char c = (char) (10 * Math.random() + '0');
    Vector v = new Vector();
    v.addElement(new String(new char[]{c}));
    return v;
  }
  /**
   * Returns a textual description of this parser.
   *
   * @param   vector   a list of parsers already printed in
   *                   this description
   * @return   string   a textual description of this parser
   * @see Parser#toString()
   */
  public String unvisitedString(Vector visited) {
    return "D";
  }
}
