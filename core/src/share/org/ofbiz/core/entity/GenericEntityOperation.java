package org.ofbiz.core.entity;

/**
 * <p><b>Title:</b> GenericEntityOperation
 * <p><b>Description:</b> Encapsulates operations between entities and entity fields.
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
 *@author     Chris Maurer
 *@created    Mon Nov 5, 2001
 *@version    1.0
 */
public class GenericEntityOperation {
  
  public static String EQUALS = " = ";
  public static String NOT_EQUAL = " <> ";
  public static String LESS_THAN = " < ";
  public static String GREATER_THAN = " > ";
  public static String LESS_THAN_EQUAL_TO = " <= ";
  public static String GREATER_THAN_EQUAL_TO = " >= ";
  public static String IN = " IN ";
  public static String BETWEEN = " BETWEEN ";
  public static String NOT = " NOT ";
  public static String AND = " AND ";
  public static String OR = " OR ";
  
  public String code = "";
  
  public GenericEntityOperation(String code) {
    setCode(code);
  }
  
  public void setCode(String code) {
    if(code.equals(EQUALS)){ this.code = code;  return; }
    if(code.equals(NOT_EQUAL)){ this.code = code; return; }
    if(code.equals(LESS_THAN)){ this.code = code; return; }
    if(code.equals(GREATER_THAN)){ this.code = code; return; }
    if(code.equals(LESS_THAN_EQUAL_TO)){ this.code = code; return; }
    if(code.equals(GREATER_THAN_EQUAL_TO)){ this.code = code; return; }
    if(code.equals(IN)){ this.code = code; return; }
    if(code.equals(BETWEEN)){ this.code = code; return; }
    if(code.equals(NOT)){ this.code = code; return; }
    if(code.equals(AND)){ this.code = code; return; }
    if(code.equals(OR)){ this.code = code; return; }
    throw new IllegalArgumentException("Code " + code + " not found.");
  }
  
  public String getCode(){
    return code;
  }
  
}
