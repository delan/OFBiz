
package org.ofbiz.commonapp.common.uom;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
 *@created    Fri Jul 27 01:18:23 MDT 2001
 *@version    1.0
 */
public class UomConversionValue implements UomConversion
{
  /** The variable of the UOM_ID column of the UOM_CONVERSION table. */
  private String uomId;
  /** The variable of the UOM_ID_TO column of the UOM_CONVERSION table. */
  private String uomIdTo;
  /** The variable of the CONVERSION_FACTOR column of the UOM_CONVERSION table. */
  private Double conversionFactor;

  private UomConversion uomConversion;

  public UomConversionValue()
  {
    this.uomId = null;
    this.uomIdTo = null;
    this.conversionFactor = null;

    this.uomConversion = null;
  }

  public UomConversionValue(UomConversion uomConversion) throws RemoteException
  {
    if(uomConversion == null) return;
  
    this.uomId = uomConversion.getUomId();
    this.uomIdTo = uomConversion.getUomIdTo();
    this.conversionFactor = uomConversion.getConversionFactor();

    this.uomConversion = uomConversion;
  }

  public UomConversionValue(UomConversion uomConversion, String uomId, String uomIdTo, Double conversionFactor)
  {
    if(uomConversion == null) return;
  
    this.uomId = uomId;
    this.uomIdTo = uomIdTo;
    this.conversionFactor = conversionFactor;

    this.uomConversion = uomConversion;
  }


  /** Get the primary key of the UOM_ID column of the UOM_CONVERSION table. */
  public String getUomId()  throws RemoteException { return uomId; }

  /** Get the primary key of the UOM_ID_TO column of the UOM_CONVERSION table. */
  public String getUomIdTo()  throws RemoteException { return uomIdTo; }

  /** Get the value of the CONVERSION_FACTOR column of the UOM_CONVERSION table. */
  public Double getConversionFactor() throws RemoteException { return conversionFactor; }
  /** Set the value of the CONVERSION_FACTOR column of the UOM_CONVERSION table. */
  public void setConversionFactor(Double conversionFactor) throws RemoteException
  {
    this.conversionFactor = conversionFactor;
    if(uomConversion!=null) uomConversion.setConversionFactor(conversionFactor);
  }

  /** Get the value object of the UomConversion class. */
  public UomConversion getValueObject() throws RemoteException { return this; }
  /** Set the value object of the UomConversion class. */
  public void setValueObject(UomConversion valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(uomConversion!=null) uomConversion.setValueObject(valueObject);

    if(uomId == null) uomId = valueObject.getUomId();
    if(uomIdTo == null) uomIdTo = valueObject.getUomIdTo();
    conversionFactor = valueObject.getConversionFactor();
  }


  /** Get the Main Uom entity corresponding to this entity. */
  public Uom getMainUom() { return UomHelper.findByPrimaryKey(uomId); }
  /** Remove the Main Uom entity corresponding to this entity. */
  public void removeMainUom() { UomHelper.removeByPrimaryKey(uomId); }

  /** Get the ConvTo Uom entity corresponding to this entity. */
  public Uom getConvToUom() { return UomHelper.findByPrimaryKey(uomIdTo); }
  /** Remove the ConvTo Uom entity corresponding to this entity. */
  public void removeConvToUom() { UomHelper.removeByPrimaryKey(uomIdTo); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(uomConversion!=null) return uomConversion.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(uomConversion!=null) return uomConversion.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(uomConversion!=null) return uomConversion.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(uomConversion!=null) return uomConversion.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(uomConversion!=null) uomConversion.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
