package org.ofbiz.core.rules.parse.chars;

import org.ofbiz.core.rules.parse.*;

/**
 * <p><b>Title:</b> Character Assembly
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
 * <p>A CharacterAssembly is an Assembly whose elements are
 * characters.
 *
 * @author Steven J. Metsker
 * @version 1.0
 * @see Assembly
 */

public class CharacterAssembly extends Assembly {
  /**
   * the string to consume
   */
  protected String string;
  
  /**
   * Constructs a CharacterAssembly from the given String.
   *
   * @param   String   the String to consume
   *
   * @return   a CharacterAssembly that will consume the
   *           supplied String
   */
  public CharacterAssembly(String string) {
    this.string = string;
  }
  /**
   * Returns a textual representation of the amount of this
   * characterAssembly that has been consumed.
   *
   * @param   delimiter   the mark to show between consumed
   *                      elements
   *
   * @return   a textual description of the amount of this
   *           assembly that has been consumed
   */
  public String consumed(String delimiter) {
    if (delimiter.equals("")) {
      return string.substring(0, elementsConsumed());
    }
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < elementsConsumed(); i++) {
      if (i > 0) {
        buf.append(delimiter);
      }
      buf.append(string.charAt(i));
    }
    return buf.toString();
  }
  /**
   * Returns the default string to show between elements
   * consumed or remaining.
   *
   * @return   the default string to show between elements
   *           consumed or remaining
   */
  public String defaultDelimiter() {
    return "";
  }
  /**
   * Returns the number of elements in this assembly.
   *
   * @return   the number of elements in this assembly
   */
  public int length() {
    return string.length();
  }
  /**
   * Returns the next character.
   *
   * @return   the next character from the associated token
   *           string
   *
   * @exception  ArrayIndexOutOfBoundsException  if there are
   *             no more characters in this assembly's string
   */
  public Object nextElement() {
    return new Character(string.charAt(index++));
  }
  /**
   * Shows the next object in the assembly, without removing it
   *
   * @return   the next object
   */
  public Object peek() {
    if (index < length()) {
      return new Character(string.charAt(index));
    } else {
      return null;
    }
  }
  /**
   * Returns a textual representation of the amount of this
   * characterAssembly that remains to be consumed.
   *
   * @param   delimiter   the mark to show between consumed
   *                      elements
   *
   * @return   a textual description of the amount of this
   *           assembly that remains to be consumed
   */
  public String remainder(String delimiter) {
    if (delimiter.equals("")) {
      return string.substring(elementsConsumed());
    }
    StringBuffer buf = new StringBuffer();
    for (int i = elementsConsumed();
    i < string.length();
    i++) {
      
      if (i > elementsConsumed()) {
        buf.append(delimiter);
      }
      buf.append(string.charAt(i));
    }
    return buf.toString();
  }
}
