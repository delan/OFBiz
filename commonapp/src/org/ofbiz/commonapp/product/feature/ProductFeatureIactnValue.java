
package org.ofbiz.commonapp.product.feature;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class ProductFeatureIactnValue implements ProductFeatureIactn
{
  /** The variable of the PRODUCT_FEATURE_ID column of the PRODUCT_FEATURE_IACTN table. */
  private String productFeatureId;
  /** The variable of the PRODUCT_FEATURE_ID_TO column of the PRODUCT_FEATURE_IACTN table. */
  private String productFeatureIdTo;
  /** The variable of the PRODUCT_FEATURE_IACTN_TYPE_ID column of the PRODUCT_FEATURE_IACTN table. */
  private String productFeatureIactnTypeId;
  /** The variable of the PRODUCT_ID column of the PRODUCT_FEATURE_IACTN table. */
  private String productId;

  private ProductFeatureIactn productFeatureIactn;

  public ProductFeatureIactnValue()
  {
    this.productFeatureId = null;
    this.productFeatureIdTo = null;
    this.productFeatureIactnTypeId = null;
    this.productId = null;

    this.productFeatureIactn = null;
  }

  public ProductFeatureIactnValue(ProductFeatureIactn productFeatureIactn) throws RemoteException
  {
    if(productFeatureIactn == null) return;
  
    this.productFeatureId = productFeatureIactn.getProductFeatureId();
    this.productFeatureIdTo = productFeatureIactn.getProductFeatureIdTo();
    this.productFeatureIactnTypeId = productFeatureIactn.getProductFeatureIactnTypeId();
    this.productId = productFeatureIactn.getProductId();

    this.productFeatureIactn = productFeatureIactn;
  }

  public ProductFeatureIactnValue(ProductFeatureIactn productFeatureIactn, String productFeatureId, String productFeatureIdTo, String productFeatureIactnTypeId, String productId)
  {
    if(productFeatureIactn == null) return;
  
    this.productFeatureId = productFeatureId;
    this.productFeatureIdTo = productFeatureIdTo;
    this.productFeatureIactnTypeId = productFeatureIactnTypeId;
    this.productId = productId;

    this.productFeatureIactn = productFeatureIactn;
  }


  /** Get the primary key of the PRODUCT_FEATURE_ID column of the PRODUCT_FEATURE_IACTN table. */
  public String getProductFeatureId()  throws RemoteException { return productFeatureId; }

  /** Get the primary key of the PRODUCT_FEATURE_ID_TO column of the PRODUCT_FEATURE_IACTN table. */
  public String getProductFeatureIdTo()  throws RemoteException { return productFeatureIdTo; }

  /** Get the value of the PRODUCT_FEATURE_IACTN_TYPE_ID column of the PRODUCT_FEATURE_IACTN table. */
  public String getProductFeatureIactnTypeId() throws RemoteException { return productFeatureIactnTypeId; }
  /** Set the value of the PRODUCT_FEATURE_IACTN_TYPE_ID column of the PRODUCT_FEATURE_IACTN table. */
  public void setProductFeatureIactnTypeId(String productFeatureIactnTypeId) throws RemoteException
  {
    this.productFeatureIactnTypeId = productFeatureIactnTypeId;
    if(productFeatureIactn!=null) productFeatureIactn.setProductFeatureIactnTypeId(productFeatureIactnTypeId);
  }

  /** Get the value of the PRODUCT_ID column of the PRODUCT_FEATURE_IACTN table. */
  public String getProductId() throws RemoteException { return productId; }
  /** Set the value of the PRODUCT_ID column of the PRODUCT_FEATURE_IACTN table. */
  public void setProductId(String productId) throws RemoteException
  {
    this.productId = productId;
    if(productFeatureIactn!=null) productFeatureIactn.setProductId(productId);
  }

  /** Get the value object of the ProductFeatureIactn class. */
  public ProductFeatureIactn getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductFeatureIactn class. */
  public void setValueObject(ProductFeatureIactn valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productFeatureIactn!=null) productFeatureIactn.setValueObject(valueObject);

    if(productFeatureId == null) productFeatureId = valueObject.getProductFeatureId();
    if(productFeatureIdTo == null) productFeatureIdTo = valueObject.getProductFeatureIdTo();
    productFeatureIactnTypeId = valueObject.getProductFeatureIactnTypeId();
    productId = valueObject.getProductId();
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


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productFeatureIactn!=null) return productFeatureIactn.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productFeatureIactn!=null) return productFeatureIactn.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productFeatureIactn!=null) return productFeatureIactn.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productFeatureIactn!=null) return productFeatureIactn.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productFeatureIactn!=null) productFeatureIactn.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
