
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Product Data Object Entity
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
 *@created    Fri Jul 27 01:18:26 MDT 2001
 *@version    1.0
 */
public class ProductDataObjectBean implements EntityBean
{
  /** The variable for the DATA_OBJECT_ID column of the PRODUCT_DATA_OBJECT table. */
  public String dataObjectId;
  /** The variable for the PRODUCT_ID column of the PRODUCT_DATA_OBJECT table. */
  public String productId;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the ProductDataObjectBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key DATA_OBJECT_ID column of the PRODUCT_DATA_OBJECT table. */
  public String getDataObjectId() { return dataObjectId; }

  /** Get the primary key PRODUCT_ID column of the PRODUCT_DATA_OBJECT table. */
  public String getProductId() { return productId; }

  /** Sets the values from ValueObject attribute of the ProductDataObjectBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(ProductDataObject valueObject)
  {
  }

  /** Gets the ValueObject attribute of the ProductDataObjectBean object
   *@return    The ValueObject value
   */
  public ProductDataObject getValueObject()
  {
    if(this.entityContext != null)
    {
      return new ProductDataObjectValue((ProductDataObject)this.entityContext.getEJBObject(), dataObjectId, productId);
    }
    else { return null; }
  }


  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() { return ProductHelper.findByPrimaryKey(productId); }
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() { ProductHelper.removeByPrimaryKey(productId); }


  /** Description of the Method
   *@param  dataObjectId                  Field of the DATA_OBJECT_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.product.ProductDataObjectPK ejbCreate(String dataObjectId, String productId) throws CreateException
  {
    this.dataObjectId = dataObjectId;
    this.productId = productId;
    return null;
  }

  /** Description of the Method
   *@param  dataObjectId                  Field of the DATA_OBJECT_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String dataObjectId, String productId) throws CreateException {}

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
