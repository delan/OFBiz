package org.ofbiz.core.rules.parse;

import java.util.*;

/**
 * <p><b>Title:</b> Parser Visitor
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
 * <p>This class provides a "visitor" hierarchy in support of
 * the Visitor pattern -- see the book, "Design Patterns" for
 * an explanation of this pattern.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public abstract class ParserVisitor {
  /**
   * Visit an alternation.
   *
   * @param   a   the parser to visit
   * @param   visited   a collection of previously visited parsers
   */
  public abstract void visitAlternation(Alternation a, List visited);
  /**
   * Visit an empty parser.
   *
   * @param   e   the parser to visit
   * @param   visited   a collection of previously visited parsers
   */
  public abstract void visitEmpty(Empty e, List visited);
  /**
   * Visit a repetition.
   *
   * @param   r   the parser to visit
   * @param   visited   a collection of previously visited parsers
   */
  public abstract void visitRepetition(Repetition r, List visited);
  /**
   * Visit a sequence.
   *
   * @param   s   the parser to visit
   * @param   visited   a collection of previously visited parsers
   */
  public abstract void visitSequence(Sequence s, List visited);
  /**
   * Visit a terminal.
   *
   * @param   t   the parser to visit
   * @param   visited   a collection of previously visited parsers
   */
  public abstract void visitTerminal(Terminal t, List visited);
}
