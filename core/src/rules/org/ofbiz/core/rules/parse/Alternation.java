package org.ofbiz.core.rules.parse;

import java.util.*;

/**
 * <p><b>Title:</b> Alternation
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
 * <p>An <code>Alternation</code> object is a collection of
 * parsers, any one of which can successfully match against
 * an assembly.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class Alternation extends CollectionParser {
  /**
   * Constructs a nameless alternation.
   */
  public Alternation() {
  }
  /**
   * Constructs an alternation with the given name.
   *
   * @param    name    a name to be known by
   */
  public Alternation(String name) {
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
    pv.visitAlternation(this, visited);
  }
  /**
   * Given a set of assemblies, this method matches this
   * alternation against all of them, and returns a new set
   * of the assemblies that result from the matches.
   *
   * @return   a List of assemblies that result from
   *           matching against a beginning set of assemblies
   *
   * @param   in   a vector of assemblies to match against
   *
   */
  public List match(List in) {
    List out = new ArrayList();
    Enumeration e = Collections.enumeration(subparsers);
    while (e.hasMoreElements()) {
      Parser p = (Parser) e.nextElement();
      add(out, p.matchAndAssemble(in));
    }
    return out;
  }
  /**
   * Create a random collection of elements that correspond to
   * this alternation.
   */
  protected List randomExpansion(int maxDepth, int depth) {
    if (depth >= maxDepth) {
      return randomSettle(maxDepth, depth);
    }
    double n = (double) subparsers.size();
    int i = (int) (n * Math.random());
    Parser j = (Parser) subparsers.get(i);
    return j.randomExpansion(maxDepth, depth++);
  }
  /**
   * This method is similar to randomExpansion, but it will
   * pick a terminal if one is available.
   */
  protected List randomSettle(int maxDepth, int depth) {
    
    // which alternatives are terminals?
    
    List terms = new ArrayList();
    Enumeration e = Collections.enumeration(subparsers);
    while (e.hasMoreElements()) {
      Parser j = (Parser) e.nextElement();
      if (j instanceof Terminal) {
        terms.add(j);
      }
    }
    
    // pick one of the terminals or, if there are no
    // terminals, pick any subparser
    
    List which = terms;
    if (terms.isEmpty()) {
      which = subparsers;
    }
    
    double n = (double) which.size();
    int i = (int) (n * Math.random());
    Parser p = (Parser) which.get(i);
    return p.randomExpansion(maxDepth, depth++);
  }
  /**
   * Returns the string to show between the parsers this
   * parser is an alternation of.
   */
  protected String toStringSeparator() {
    return "|";
  }
}
