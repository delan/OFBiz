/*
 * $Id$
 */
package org.ofbiz.core.workflow.definition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.ofbiz.core.workflow.WfException;

/** 
 * <p>Represents an external tool or application
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Oliver Wieland (wieland.oliver@t-online.de)
 * @author <a href='mailto:jonesde@ofbiz.org'>David E. Jones</a>
 * @created Sun Aug 12 13:22:40 GMT+02:00 2001
 * @version 1.0
 */
public class Tool extends WfMetaObject implements Serializable  {
  // Attribute instance 'parameter'
  private String parameter;
  
  /** Empty constructor */
  Tool() { super(); }
  
  /** Constructor with all attributes
   * @param pParameter Initial value for attribute 'parameter'
   */
  Tool(String pParameter) { parameter = pParameter; }
  
  /** Getter for attribute 'parameter'
   * @return Value of attribute parameter
   */
  public String getParameter() { return parameter; }
  
  /** Setter for attribute 'parameter'
   * @param pParameter Neuer Wert des Attributes parameter
   */
  public void setParameter(String pParameter) {
    if (parameter == pParameter) return;
    if ( !notifyAttributeChangeParameter( pParameter ) ) return;
    parameter = pParameter;
  }
  
  /** This method is called, before the attribute 'Parameter' is set to a new value.
   * @param pParameter New Value for attribute 'Parameter'
   * @return true, if change accepted, otherwise false. Default is true
   */
  private boolean notifyAttributeChangeParameter(String pParameter) {
    return true;
  }
  
  /** String representation of Tool */
  public String toString() {
    StringBuffer lRet = new StringBuffer("Tool");
    return lRet.toString();
  }
}
