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
 * <p>Describes a process split
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
public class Split extends WfMetaObject implements Serializable  {
  //Activity Types - see WfMC XPDL Spec 0.03a pages 28
  public static final int INVALID = -1;
  public static final int AND = 1;
  public static final int XOR = 2;
  
  // Attribute instance 'splitType'
  private int splitType;
  
  /** Empty constructor */
  Split() { super(); }
  
  /** Constructor with all attributes
   * @param pSpltType Initial value for attribute 'splitType'
   */
  Split(int splitType) { this.splitType = splitType; }
  
  /** Getter for attribute 'splitType'
   * @return Value of attribute splitType
   */
  public int getSpltType() { return splitType; }
  
  /** Setter for attribute 'splitType'
   * @param pSpltType Neuer Wert des Attributes splitType
   */
  public void setSpltType(int splitType)  {
    if (this.splitType == splitType) return;
    if (!notifyAttributeChangeSpltType(splitType)) return;
    this.splitType = splitType;
  }
  
  /** This method is called, before the attribute 'SpltType' is set to a new value.
   * @param pSpltType New Value for attribute 'SpltType'
   * @return true, if change accepted, otherwise false. Default is true
   */
  private boolean notifyAttributeChangeSpltType(int splitType) {
    return true;
  }
  
  
  /** String representation of Split */
  public String toString() {
    StringBuffer lRet = new StringBuffer("Split");
    return lRet.toString();
  }
}








