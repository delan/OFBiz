package org.ofbiz.core.rules.parse;

import java.util.*;

/**
 * <p><b>Title:</b> Sequence
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
 * <p>A <code>Sequence</code> object is a collection of
 * parsers, all of which must in turn match against an
 * assembly for this parser to successfully match.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class Sequence extends CollectionParser {
  /**
   * Constructs a nameless sequence.
   */
  public Sequence() {
  }
  /**
   * Constructs a sequence with the given name.
   *
   * @param    name    a name to be known by
   */
  public Sequence(String name) {
    super(name);
  }
  /**
   * Accept a "visitor" and a collection of previously visited
   * parsers.
   *
   * @param   pv   the visitor to accept
   *
   * @param   visited   a collection of previously visited parsers
   */
  public void accept(ParserVisitor pv, List visited) {
    pv.visitSequence(this, visited);
  }
  /**
   * Given a set of assemblies, this method matches this
   * sequence against all of them, and returns a new set
   * of the assemblies that result from the matches.
   *
   * @return   a List of assemblies that result from
   *           matching against a beginning set of assemblies
   *
   * @param   in   a vector of assemblies to match against
   *
   */
  public List match(List in) {
    List out = in;
    Enumeration e = Collections.enumeration(subparsers);
    while (e.hasMoreElements()) {
      Parser p = (Parser) e.nextElement();
      out = p.matchAndAssemble(out);
      if (out.isEmpty()) {
        return out;
      }
    }
    return out;
  }
  /**
   * Create a random expansion for each parser in this
   * sequence and return a collection of all these expansions.
   */
  protected List randomExpansion(int maxDepth, int depth) {
    List v = new ArrayList();
    Enumeration e = Collections.enumeration(subparsers);
    while (e.hasMoreElements()) {
      Parser p = (Parser) e.nextElement();
      List w = p.randomExpansion(maxDepth, depth++);
      Enumeration f = Collections.enumeration(w);
      while (f.hasMoreElements()) {
        v.add(f.nextElement());
      }
    }
    return v;
  }
  /**
   * Returns the string to show between the parsers this
   * parser is a sequence of. This is an empty string,
   * since convention indicates sequence quietly. For
   * example, note that in the regular expression
   * <code>(a|b)c</code>, the lack of a delimiter between
   * the expression in parentheses and the 'c' indicates a
   * sequence of these expressions.
   */
  protected String toStringSeparator() {
    return "";
  }
}
