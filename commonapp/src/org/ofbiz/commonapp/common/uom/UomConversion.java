
package org.ofbiz.commonapp.common.uom;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Unit Of Measure Conversion Type Entity
 * <p><b>Description:</b> None
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
 *@author     David E. Jones
 *@created    Fri Jul 27 01:18:22 MDT 2001
 *@version    1.0
 */

public interface UomConversion extends EJBObject
{
  /** Get the primary key of the UOM_ID column of the UOM_CONVERSION table. */
  public String getUomId() throws RemoteException;
  
  /** Get the primary key of the UOM_ID_TO column of the UOM_CONVERSION table. */
  public String getUomIdTo() throws RemoteException;
  
  /** Get the value of the CONVERSION_FACTOR column of the UOM_CONVERSION table. */
  public Double getConversionFactor() throws RemoteException;
  /** Set the value of the CONVERSION_FACTOR column of the UOM_CONVERSION table. */
  public void setConversionFactor(Double conversionFactor) throws RemoteException;
  

  /** Get the value object of this UomConversion class. */
  public UomConversion getValueObject() throws RemoteException;
  /** Set the values in the value object of this UomConversion class. */
  public void setValueObject(UomConversion uomConversionValue) throws RemoteException;


  /** Get the Main Uom entity corresponding to this entity. */
  public Uom getMainUom() throws RemoteException;
  /** Remove the Main Uom entity corresponding to this entity. */
  public void removeMainUom() throws RemoteException;  

  /** Get the ConvTo Uom entity corresponding to this entity. */
  public Uom getConvToUom() throws RemoteException;
  /** Remove the ConvTo Uom entity corresponding to this entity. */
  public void removeConvToUom() throws RemoteException;  

}
