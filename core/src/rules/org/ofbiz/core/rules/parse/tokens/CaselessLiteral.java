package org.ofbiz.core.rules.parse.tokens;

/**
 * <p><b>Title:</b> Caseless Literal
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
 * <p>A CaselessLiteral matches a specified String from an
 * assembly, disregarding case.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class CaselessLiteral extends Literal {
  /**
   * Constructs a literal that will match the specified string,
   * given mellowness about case.
   *
   * @param   string   the string to match as a token
   *
   * @return   a literal that will match the specified string,
   *           disregarding case
   */
  public CaselessLiteral(String literal) {
    super(literal);
  }
  /**
   * Returns true if the literal this object equals an
   * assembly's next element, disregarding case.
   *
   * @param   object   an element from an assembly
   *
   * @return   true, if the specified literal equals the next
   *           token from an assembly, disregarding case
   */
  protected boolean qualifies(Object o) {
    return literal.equalsIgnoreCase((Token) o);
  }
}
