package org.ofbiz.core.rules.parse;

import java.util.*;

/**
 * <p><b>Title:</b> Collection Parser
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
 * <p>This class abstracts the behavior common to parsers
 * that consist of a series of other parsers.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public abstract class CollectionParser extends Parser {
  /**
   * the parsers this parser is a collection of
   */
  protected Vector subparsers = new Vector();
  /**
   * Supports subclass constructors with no arguments.
   */
  public CollectionParser() {
  }
  /**
   * Supports subclass constructors with a name argument
   *
   * @param   string   the name of this parser
   */
  public CollectionParser(String name) {
    super(name);
  }
  /**
   * Adds a parser to the collection.
   *
   * @param   Parser   the parser to add
   *
   * @return   this
   */
  public CollectionParser add(Parser e) {
    subparsers.addElement(e);
    return this;
  }
  /**
   * Return this parser's subparsers.
   *
   * @return   Vector   this parser's subparsers
   */
  public Vector getSubparsers() {
    return subparsers;
  }
  /**
   * Helps to textually describe this CollectionParser.
   *
   * @returns   the string to place between parsers in
   *            the collection
   */
  protected abstract String toStringSeparator();
  /**
   * Returns a textual description of this parser.
   */
  protected String unvisitedString(Vector visited) {
    StringBuffer buf = new StringBuffer("<");
    boolean needSeparator = false;
    Enumeration e = subparsers.elements();
    while (e.hasMoreElements()) {
      if (needSeparator) {
        buf.append(toStringSeparator());
      }
      Parser next = (Parser) e.nextElement();
      buf.append(next.toString(visited));
      needSeparator = true;
    }
    buf.append(">");
    return buf.toString();
  }
}
