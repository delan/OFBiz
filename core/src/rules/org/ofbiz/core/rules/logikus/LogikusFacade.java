package org.ofbiz.core.rules.logikus;

import org.ofbiz.core.rules.parse.*;
import org.ofbiz.core.rules.parse.tokens.*;
import org.ofbiz.core.rules.engine.*;

/**
 * <p><b>Title:</b> Logikus Facade
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
 * <p>This class provides utility methods that simplify the use
 * of the Logikus parser.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class LogikusFacade {
  /**
   * Translate one axiom string into an Axiom object.
   */
  public static Axiom axiom(String s) {
    return axiom(new TokenString(s));
  }
  /**
   * Translate the tokens for one axiom into an Axiom
   * object (either a Fact or a Rule);
   */
  protected static Axiom axiom(TokenString ts) {
    Parser p = new LogikusParser().axiom();
    Object o = parse(ts, p, "axiom");
    return (Axiom) o;
  }
  /**
   * Throws an informative runtime exception if the provided
   * string begins with an uppercase letter.
   */
  protected static void checkForUppercase(TokenString ts, String type) {
    
    if (ts.length() > 0) {
      Token t = ts.tokenAt(0);
      String s = t.sval();
      if (s.length() > 0 &&
      Character.isUpperCase(s.charAt(0))) {
        
        throw new LogikusException(
        "> Uppercase " + s +
        " indicates a variable and cannot begin a " +
        type + ".\n");
      }
    }
  }
  /**
   * Parse the given token string with the given parser,
   * throwing runtime exceptions if parsing fails
   * or is incomplete.
   */
  protected static Object parse(TokenString ts, Parser p, String type) {
    
    TokenAssembly ta = new TokenAssembly(ts);
    Assembly out = p.bestMatch(ta);
    if (out == null) {
      reportNoMatch(ts, type);
    }
    if (out.hasMoreElements()) {
      // allow an extra semicolon
      if (!out.remainder("").equals(";")) {
        reportLeftovers(out, type);
      }
    }
    return out.pop();
  }
  /**
   * Parse the text of a Logikus program and return a
   * <code>Program</code> object.
   *
   * @param   String   the text of the program
   *
   * @return a <code>Program</code> object
   *
   * @exception   RuntimeException   if parsing fails
   */
  public static Program program(String s) {
    Program p = new Program();
    TokenStringSource tss = new TokenStringSource(
    new Tokenizer(s), ";");
    while (true) {
      TokenString ts = tss.nextTokenString();
      if (ts == null) { // no more token strings
        break;
      }
      p.addAxiom(axiom(ts));
    }
    return p;
  }
  /**
   * Parse the text of a Logikus query and return a
   * <code>Query</code> object.
   *
   * @param   String   the text of the query
   *
   * @return a <code>Query</code> object
   *
   * @exception   RuntimeException   if parsing fails
   */
  public static Query query(String s, AxiomSource as) {
    Object o = parse(
    new TokenString(s), LogikusParser.query(), "query");
    if (o instanceof Fact) {
      Fact f = (Fact) o;
      return new Query(as, f);
    }
    return new Query(as, (Rule) o);
  }
  /**
   * Throws a runtime exception reporting an incomplete
   * parse.
   */
  protected static Object reportLeftovers(Assembly out, String type) {
    throw new LogikusException(
    "> Input for " + type +
    " appears complete after : \n> " + out.consumed(" ") +
    "\n");
  }
  /**
   * Throws a runtime exception reporting failed parse.
   */
  protected static void reportNoMatch(TokenString ts, String type) {
    checkForUppercase(ts, type);
    throw new LogikusException(
    "> Cannot parse " + type + " : " + ts + "\n");
  }
}
