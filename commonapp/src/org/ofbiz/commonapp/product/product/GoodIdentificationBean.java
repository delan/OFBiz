
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Good Identification Entity
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
 *@created    Fri Jul 27 01:18:25 MDT 2001
 *@version    1.0
 */
public class GoodIdentificationBean implements EntityBean
{
  /** The variable for the GOOD_IDENTIFICATION_TYPE_ID column of the GOOD_IDENTIFICATION table. */
  public String goodIdentificationTypeId;
  /** The variable for the PRODUCT_ID column of the GOOD_IDENTIFICATION table. */
  public String productId;
  /** The variable for the ID_VALUE column of the GOOD_IDENTIFICATION table. */
  public String idValue;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the GoodIdentificationBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key GOOD_IDENTIFICATION_TYPE_ID column of the GOOD_IDENTIFICATION table. */
  public String getGoodIdentificationTypeId() { return goodIdentificationTypeId; }

  /** Get the primary key PRODUCT_ID column of the GOOD_IDENTIFICATION table. */
  public String getProductId() { return productId; }

  /** Get the value of the ID_VALUE column of the GOOD_IDENTIFICATION table. */
  public String getIdValue() { return idValue; }
  /** Set the value of the ID_VALUE column of the GOOD_IDENTIFICATION table. */
  public void setIdValue(String idValue)
  {
    this.idValue = idValue;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the GoodIdentificationBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(GoodIdentification valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getIdValue() != null)
      {
        this.idValue = valueObject.getIdValue();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the GoodIdentificationBean object
   *@return    The ValueObject value
   */
  public GoodIdentification getValueObject()
  {
    if(this.entityContext != null)
    {
      return new GoodIdentificationValue((GoodIdentification)this.entityContext.getEJBObject(), goodIdentificationTypeId, productId, idValue);
    }
    else { return null; }
  }


  /** Get the  GoodIdentificationType entity corresponding to this entity. */
  public GoodIdentificationType getGoodIdentificationType() { return GoodIdentificationTypeHelper.findByPrimaryKey(goodIdentificationTypeId); }
  /** Remove the  GoodIdentificationType entity corresponding to this entity. */
  public void removeGoodIdentificationType() { GoodIdentificationTypeHelper.removeByPrimaryKey(goodIdentificationTypeId); }

  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() { return ProductHelper.findByPrimaryKey(productId); }
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() { ProductHelper.removeByPrimaryKey(productId); }


  /** Description of the Method
   *@param  goodIdentificationTypeId                  Field of the GOOD_IDENTIFICATION_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  idValue                  Field of the ID_VALUE column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.product.GoodIdentificationPK ejbCreate(String goodIdentificationTypeId, String productId, String idValue) throws CreateException
  {
    this.goodIdentificationTypeId = goodIdentificationTypeId;
    this.productId = productId;
    this.idValue = idValue;
    return null;
  }

  /** Description of the Method
   *@param  goodIdentificationTypeId                  Field of the GOOD_IDENTIFICATION_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.product.GoodIdentificationPK ejbCreate(String goodIdentificationTypeId, String productId) throws CreateException
  {
    return ejbCreate(goodIdentificationTypeId, productId, null);
  }

  /** Description of the Method
   *@param  goodIdentificationTypeId                  Field of the GOOD_IDENTIFICATION_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  idValue                  Field of the ID_VALUE column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String goodIdentificationTypeId, String productId, String idValue) throws CreateException {}

  /** Description of the Method
   *@param  goodIdentificationTypeId                  Field of the GOOD_IDENTIFICATION_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String goodIdentificationTypeId, String productId) throws CreateException
  {
    ejbPostCreate(goodIdentificationTypeId, productId, null);
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
