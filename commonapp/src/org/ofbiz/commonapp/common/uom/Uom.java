
package org.ofbiz.commonapp.common.uom;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Unit Of Measure Entity
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

public interface Uom extends EJBObject
{
  /** Get the primary key of the UOM_ID column of the UOM table. */
  public String getUomId() throws RemoteException;
  
  /** Get the value of the UOM_TYPE_ID column of the UOM table. */
  public String getUomTypeId() throws RemoteException;
  /** Set the value of the UOM_TYPE_ID column of the UOM table. */
  public void setUomTypeId(String uomTypeId) throws RemoteException;
  
  /** Get the value of the ABBREVIATION column of the UOM table. */
  public String getAbbreviation() throws RemoteException;
  /** Set the value of the ABBREVIATION column of the UOM table. */
  public void setAbbreviation(String abbreviation) throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the UOM table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the UOM table. */
  public void setDescription(String description) throws RemoteException;
  

  /** Get the value object of this Uom class. */
  public Uom getValueObject() throws RemoteException;
  /** Set the values in the value object of this Uom class. */
  public void setValueObject(Uom uomValue) throws RemoteException;


  /** Get the  UomType entity corresponding to this entity. */
  public UomType getUomType() throws RemoteException;
  /** Remove the  UomType entity corresponding to this entity. */
  public void removeUomType() throws RemoteException;  

  /** Get a collection of Main UomConversion related entities. */
  public Collection getMainUomConversions() throws RemoteException;
  /** Get the Main UomConversion keyed by member(s) of this class, and other passed parameters. */
  public UomConversion getMainUomConversion(String uomIdTo) throws RemoteException;
  /** Remove Main UomConversion related entities. */
  public void removeMainUomConversions() throws RemoteException;
  /** Remove the Main UomConversion keyed by member(s) of this class, and other passed parameters. */
  public void removeMainUomConversion(String uomIdTo) throws RemoteException;

  /** Get a collection of ConvTo UomConversion related entities. */
  public Collection getConvToUomConversions() throws RemoteException;
  /** Get the ConvTo UomConversion keyed by member(s) of this class, and other passed parameters. */
  public UomConversion getConvToUomConversion(String uomId) throws RemoteException;
  /** Remove ConvTo UomConversion related entities. */
  public void removeConvToUomConversions() throws RemoteException;
  /** Remove the ConvTo UomConversion keyed by member(s) of this class, and other passed parameters. */
  public void removeConvToUomConversion(String uomId) throws RemoteException;

}
