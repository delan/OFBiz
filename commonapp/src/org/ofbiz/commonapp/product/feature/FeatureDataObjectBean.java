
package org.ofbiz.commonapp.product.feature;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Feature Data Object Entity
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
public class FeatureDataObjectBean implements EntityBean
{
  /** The variable for the DATA_OBJECT_ID column of the FEATURE_DATA_OBJECT table. */
  public String dataObjectId;
  /** The variable for the PRODUCT_FEATURE_ID column of the FEATURE_DATA_OBJECT table. */
  public String productFeatureId;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the FeatureDataObjectBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key DATA_OBJECT_ID column of the FEATURE_DATA_OBJECT table. */
  public String getDataObjectId() { return dataObjectId; }

  /** Get the primary key PRODUCT_FEATURE_ID column of the FEATURE_DATA_OBJECT table. */
  public String getProductFeatureId() { return productFeatureId; }

  /** Sets the values from ValueObject attribute of the FeatureDataObjectBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(FeatureDataObject valueObject)
  {
  }

  /** Gets the ValueObject attribute of the FeatureDataObjectBean object
   *@return    The ValueObject value
   */
  public FeatureDataObject getValueObject()
  {
    if(this.entityContext != null)
    {
      return new FeatureDataObjectValue((FeatureDataObject)this.entityContext.getEJBObject(), dataObjectId, productFeatureId);
    }
    else { return null; }
  }


  /** Get the  ProductFeature entity corresponding to this entity. */
  public ProductFeature getProductFeature() { return ProductFeatureHelper.findByPrimaryKey(productFeatureId); }
  /** Remove the  ProductFeature entity corresponding to this entity. */
  public void removeProductFeature() { ProductFeatureHelper.removeByPrimaryKey(productFeatureId); }


  /** Description of the Method
   *@param  dataObjectId                  Field of the DATA_OBJECT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.feature.FeatureDataObjectPK ejbCreate(String dataObjectId, String productFeatureId) throws CreateException
  {
    this.dataObjectId = dataObjectId;
    this.productFeatureId = productFeatureId;
    return null;
  }

  /** Description of the Method
   *@param  dataObjectId                  Field of the DATA_OBJECT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String dataObjectId, String productFeatureId) throws CreateException {}

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
