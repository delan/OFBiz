package org.ofbiz.core.rules.parse;

import java.util.*;

/**
 * <p><b>Title:</b> Repetition
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
 * <p>A <code>Repetition</code> matches its underlying parser
 * repeatedly against a assembly.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */

public class Repetition extends Parser {
  /**
   * the parser this parser is a repetition of
   */
  protected Parser subparser;
  
  /**
   * the width of a random expansion
   */
  protected static final int EXPWIDTH = 4;
  
  /**
   * an assembler to apply at the beginning of a match
   */
  protected Assembler preAssembler;
  /**
   * Constructs a repetition of the given parser.
   *
   * @param   parser   the parser to repeat
   *
   * @return   a repetiton that will match the given
   *           parser repeatedly in successive matches
   */
  public Repetition(Parser p) {
    this(p, null);
  }
  /**
   * Constructs a repetition of the given parser with the
   * given name.
   *
   * @param   Parser   the parser to repeat
   *
   * @param   String   a name to be known by
   *
   * @return   a repetiton that will match the given
   *           parser repeatedly in successive matches
   */
  public Repetition(Parser subparser, String name) {
    super(name);
    this.subparser = subparser;
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
    pv.visitRepetition(this, visited);
  }
  /**
   * Return this parser's subparser.
   *
   * @return   Parser   this parser's subparser
   */
  public Parser getSubparser() {
    return subparser;
  }
  /**
   * Given a set of assemblies, this method applies a preassembler
   * to all of them, matches its subparser repeatedly against each
   * of them, applies its post-assembler against each, and returns
   * a new set of the assemblies that result from the matches.
   * <p>
   * For example, matching the regular expression <code>a*
   * </code> against <code>{^aaab}</code> results in <code>
   * {^aaab, a^aab, aa^ab, aaa^b}</code>.
   *
   * @return   a List of assemblies that result from
   *           matching against a beginning set of assemblies
   *
   * @param   in   a vector of assemblies to match against
   *
   */
  public List match(List in) {
    if (preAssembler != null) {
      Enumeration e = Collections.enumeration(in);
      while (e.hasMoreElements()) {
        preAssembler.workOn((Assembly) e.nextElement());
      }
    }
    List out = elementClone(in);
    List s = in; // a working state
    while (!s.isEmpty()) {
      s = subparser.matchAndAssemble(s);
      add(out, s);
    }
    return out;
  }
  /**
   * Create a collection of random elements that correspond to
   * this repetition.
   */
  protected List randomExpansion(int maxDepth, int depth) {
    List v = new ArrayList();
    if (depth >= maxDepth) {
      return v;
    }
    
    int n = (int) (EXPWIDTH * Math.random());
    for (int j = 0; j < n; j++) {
      List w = subparser.randomExpansion(maxDepth, depth++);
      Enumeration e = Collections.enumeration(w);
      while (e.hasMoreElements()) {
        v.add(e.nextElement());
      }
    }
    return v;
  }
  /**
   * Sets the object that will work on every assembly before
   * matching against it.
   *
   * @param   Assembler   the assembler to apply
   *
   * @return   Parser   this
   */
  public Parser setPreAssembler(Assembler preAssembler) {
    this.preAssembler = preAssembler;
    return this;
  }
  /**
   * Returns a textual description of this parser.
   */
  protected String unvisitedString(List visited) {
    return subparser.toString(visited) + "*";
  }
}
