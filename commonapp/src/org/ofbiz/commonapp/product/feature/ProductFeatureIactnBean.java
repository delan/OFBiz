
package org.ofbiz.commonapp.product.feature;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Product Feature Interaction Entity
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
 *@created    Fri Jul 27 01:18:28 MDT 2001
 *@version    1.0
 */
public class ProductFeatureIactnBean implements EntityBean
{
  /** The variable for the PRODUCT_FEATURE_ID column of the PRODUCT_FEATURE_IACTN table. */
  public String productFeatureId;
  /** The variable for the PRODUCT_FEATURE_ID_TO column of the PRODUCT_FEATURE_IACTN table. */
  public String productFeatureIdTo;
  /** The variable for the PRODUCT_FEATURE_IACTN_TYPE_ID column of the PRODUCT_FEATURE_IACTN table. */
  public String productFeatureIactnTypeId;
  /** The variable for the PRODUCT_ID column of the PRODUCT_FEATURE_IACTN table. */
  public String productId;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the ProductFeatureIactnBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key PRODUCT_FEATURE_ID column of the PRODUCT_FEATURE_IACTN table. */
  public String getProductFeatureId() { return productFeatureId; }

  /** Get the primary key PRODUCT_FEATURE_ID_TO column of the PRODUCT_FEATURE_IACTN table. */
  public String getProductFeatureIdTo() { return productFeatureIdTo; }

  /** Get the value of the PRODUCT_FEATURE_IACTN_TYPE_ID column of the PRODUCT_FEATURE_IACTN table. */
  public String getProductFeatureIactnTypeId() { return productFeatureIactnTypeId; }
  /** Set the value of the PRODUCT_FEATURE_IACTN_TYPE_ID column of the PRODUCT_FEATURE_IACTN table. */
  public void setProductFeatureIactnTypeId(String productFeatureIactnTypeId)
  {
    this.productFeatureIactnTypeId = productFeatureIactnTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the PRODUCT_ID column of the PRODUCT_FEATURE_IACTN table. */
  public String getProductId() { return productId; }
  /** Set the value of the PRODUCT_ID column of the PRODUCT_FEATURE_IACTN table. */
  public void setProductId(String productId)
  {
    this.productId = productId;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the ProductFeatureIactnBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(ProductFeatureIactn valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getProductFeatureIactnTypeId() != null)
      {
        this.productFeatureIactnTypeId = valueObject.getProductFeatureIactnTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getProductId() != null)
      {
        this.productId = valueObject.getProductId();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the ProductFeatureIactnBean object
   *@return    The ValueObject value
   */
  public ProductFeatureIactn getValueObject()
  {
    if(this.entityContext != null)
    {
      return new ProductFeatureIactnValue((ProductFeatureIactn)this.entityContext.getEJBObject(), productFeatureId, productFeatureIdTo, productFeatureIactnTypeId, productId);
    }
    else { return null; }
  }


  /** Get the  ProductFeatureIactnType entity corresponding to this entity. */
  public ProductFeatureIactnType getProductFeatureIactnType() { return ProductFeatureIactnTypeHelper.findByPrimaryKey(productFeatureIactnTypeId); }
  /** Remove the  ProductFeatureIactnType entity corresponding to this entity. */
  public void removeProductFeatureIactnType() { ProductFeatureIactnTypeHelper.removeByPrimaryKey(productFeatureIactnTypeId); }

  /** Get the Main ProductFeature entity corresponding to this entity. */
  public ProductFeature getMainProductFeature() { return ProductFeatureHelper.findByPrimaryKey(productFeatureId); }
  /** Remove the Main ProductFeature entity corresponding to this entity. */
  public void removeMainProductFeature() { ProductFeatureHelper.removeByPrimaryKey(productFeatureId); }

  /** Get the Assoc ProductFeature entity corresponding to this entity. */
  public ProductFeature getAssocProductFeature() { return ProductFeatureHelper.findByPrimaryKey(productFeatureIdTo); }
  /** Remove the Assoc ProductFeature entity corresponding to this entity. */
  public void removeAssocProductFeature() { ProductFeatureHelper.removeByPrimaryKey(productFeatureIdTo); }


  /** Description of the Method
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productFeatureIdTo                  Field of the PRODUCT_FEATURE_ID_TO column.
   *@param  productFeatureIactnTypeId                  Field of the PRODUCT_FEATURE_IACTN_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.feature.ProductFeatureIactnPK ejbCreate(String productFeatureId, String productFeatureIdTo, String productFeatureIactnTypeId, String productId) throws CreateException
  {
    this.productFeatureId = productFeatureId;
    this.productFeatureIdTo = productFeatureIdTo;
    this.productFeatureIactnTypeId = productFeatureIactnTypeId;
    this.productId = productId;
    return null;
  }

  /** Description of the Method
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productFeatureIdTo                  Field of the PRODUCT_FEATURE_ID_TO column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.feature.ProductFeatureIactnPK ejbCreate(String productFeatureId, String productFeatureIdTo) throws CreateException
  {
    return ejbCreate(productFeatureId, productFeatureIdTo, null, null);
  }

  /** Description of the Method
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productFeatureIdTo                  Field of the PRODUCT_FEATURE_ID_TO column.
   *@param  productFeatureIactnTypeId                  Field of the PRODUCT_FEATURE_IACTN_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productFeatureId, String productFeatureIdTo, String productFeatureIactnTypeId, String productId) throws CreateException {}

  /** Description of the Method
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productFeatureIdTo                  Field of the PRODUCT_FEATURE_ID_TO column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productFeatureId, String productFeatureIdTo) throws CreateException
  {
    ejbPostCreate(productFeatureId, productFeatureIdTo, null, null);
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
