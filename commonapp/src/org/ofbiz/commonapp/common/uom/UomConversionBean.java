
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
 *@created    Fri Jul 27 01:18:23 MDT 2001
 *@version    1.0
 */
public class UomConversionBean implements EntityBean
{
  /** The variable for the UOM_ID column of the UOM_CONVERSION table. */
  public String uomId;
  /** The variable for the UOM_ID_TO column of the UOM_CONVERSION table. */
  public String uomIdTo;
  /** The variable for the CONVERSION_FACTOR column of the UOM_CONVERSION table. */
  public Double conversionFactor;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the UomConversionBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key UOM_ID column of the UOM_CONVERSION table. */
  public String getUomId() { return uomId; }

  /** Get the primary key UOM_ID_TO column of the UOM_CONVERSION table. */
  public String getUomIdTo() { return uomIdTo; }

  /** Get the value of the CONVERSION_FACTOR column of the UOM_CONVERSION table. */
  public Double getConversionFactor() { return conversionFactor; }
  /** Set the value of the CONVERSION_FACTOR column of the UOM_CONVERSION table. */
  public void setConversionFactor(Double conversionFactor)
  {
    this.conversionFactor = conversionFactor;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the UomConversionBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(UomConversion valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getConversionFactor() != null)
      {
        this.conversionFactor = valueObject.getConversionFactor();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the UomConversionBean object
   *@return    The ValueObject value
   */
  public UomConversion getValueObject()
  {
    if(this.entityContext != null)
    {
      return new UomConversionValue((UomConversion)this.entityContext.getEJBObject(), uomId, uomIdTo, conversionFactor);
    }
    else { return null; }
  }


  /** Get the Main Uom entity corresponding to this entity. */
  public Uom getMainUom() { return UomHelper.findByPrimaryKey(uomId); }
  /** Remove the Main Uom entity corresponding to this entity. */
  public void removeMainUom() { UomHelper.removeByPrimaryKey(uomId); }

  /** Get the ConvTo Uom entity corresponding to this entity. */
  public Uom getConvToUom() { return UomHelper.findByPrimaryKey(uomIdTo); }
  /** Remove the ConvTo Uom entity corresponding to this entity. */
  public void removeConvToUom() { UomHelper.removeByPrimaryKey(uomIdTo); }


  /** Description of the Method
   *@param  uomId                  Field of the UOM_ID column.
   *@param  uomIdTo                  Field of the UOM_ID_TO column.
   *@param  conversionFactor                  Field of the CONVERSION_FACTOR column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.common.uom.UomConversionPK ejbCreate(String uomId, String uomIdTo, Double conversionFactor) throws CreateException
  {
    this.uomId = uomId;
    this.uomIdTo = uomIdTo;
    this.conversionFactor = conversionFactor;
    return null;
  }

  /** Description of the Method
   *@param  uomId                  Field of the UOM_ID column.
   *@param  uomIdTo                  Field of the UOM_ID_TO column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.common.uom.UomConversionPK ejbCreate(String uomId, String uomIdTo) throws CreateException
  {
    return ejbCreate(uomId, uomIdTo, null);
  }

  /** Description of the Method
   *@param  uomId                  Field of the UOM_ID column.
   *@param  uomIdTo                  Field of the UOM_ID_TO column.
   *@param  conversionFactor                  Field of the CONVERSION_FACTOR column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String uomId, String uomIdTo, Double conversionFactor) throws CreateException {}

  /** Description of the Method
   *@param  uomId                  Field of the UOM_ID column.
   *@param  uomIdTo                  Field of the UOM_ID_TO column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String uomId, String uomIdTo) throws CreateException
  {
    ejbPostCreate(uomId, uomIdTo, null);
  }

  /** Called when the entity bean is removed.
   *@exception  RemoveException  Description of Exception
   */
  public void ejbRemove() throws RemoveException {}

  /** Called when the entity bean is activated. */
  public void ejbActivate() {}

  /** Called when the entity bean is passivated. */
  public void ejbPassivate() {}

  /** Called when the entity bean is loaded. */
  public void ejbLoad() { ejbIsModified = false; }

  /** Called when the entity bean is stored. */
  public void ejbStore() { ejbIsModified = false; }

  /** Called to check if the entity bean needs to be stored. */
  public boolean isModified() { return ejbIsModified; }

  /** Unsets the EntityContext, ie sets it to null. */
  public void unsetEntityContext() { entityContext = null; }
}
