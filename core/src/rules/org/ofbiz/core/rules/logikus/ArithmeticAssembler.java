package org.ofbiz.core.rules.logikus;

import org.ofbiz.core.rules.parse.*;
import org.ofbiz.core.rules.engine.*;

/**
 * <p><b>Title:</b> Arithmetic Assembler
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
 * <p>This assembler pops two arithmetic operands, builds an
 * ArithmeticOperator from them, and pushes it.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class ArithmeticAssembler extends Assembler {
  /**
   * the character which represents an arithmetic operator
   */
  protected char operator;
  /**
   * Constructs an assembler that will stack an
   * ArithmeticOperator with the specified operator.
   */
  public ArithmeticAssembler(char operator) {
    this.operator = operator;
  }
  /**
   * Pop two arithmetic operands, build an ArithmeticOperator
   * from them, and push it.
   *
   * @param  Assembly  the assembly to work on
   */
  public void workOn(Assembly a) {
    ArithmeticTerm operand1 = (ArithmeticTerm) a.pop();
    ArithmeticTerm operand0 = (ArithmeticTerm) a.pop();
    a.push(new ArithmeticOperator(
    operator, operand0, operand1));
  }
}
