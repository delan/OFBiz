
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

public interface ProductFeatureIactn extends EJBObject
{
  /** Get the primary key of the PRODUCT_FEATURE_ID column of the PRODUCT_FEATURE_IACTN table. */
  public String getProductFeatureId() throws RemoteException;
  
  /** Get the primary key of the PRODUCT_FEATURE_ID_TO column of the PRODUCT_FEATURE_IACTN table. */
  public String getProductFeatureIdTo() throws RemoteException;
  
  /** Get the value of the PRODUCT_FEATURE_IACTN_TYPE_ID column of the PRODUCT_FEATURE_IACTN table. */
  public String getProductFeatureIactnTypeId() throws RemoteException;
  /** Set the value of the PRODUCT_FEATURE_IACTN_TYPE_ID column of the PRODUCT_FEATURE_IACTN table. */
  public void setProductFeatureIactnTypeId(String productFeatureIactnTypeId) throws RemoteException;
  
  /** Get the value of the PRODUCT_ID column of the PRODUCT_FEATURE_IACTN table. */
  public String getProductId() throws RemoteException;
  /** Set the value of the PRODUCT_ID column of the PRODUCT_FEATURE_IACTN table. */
  public void setProductId(String productId) throws RemoteException;
  

  /** Get the value object of this ProductFeatureIactn class. */
  public ProductFeatureIactn getValueObject() throws RemoteException;
  /** Set the values in the value object of this ProductFeatureIactn class. */
  public void setValueObject(ProductFeatureIactn productFeatureIactnValue) throws RemoteException;


  /** Get the  ProductFeatureIactnType entity corresponding to this entity. */
  public ProductFeatureIactnType getProductFeatureIactnType() throws RemoteException;
  /** Remove the  ProductFeatureIactnType entity corresponding to this entity. */
  public void removeProductFeatureIactnType() throws RemoteException;  

  /** Get the Main ProductFeature entity corresponding to this entity. */
  public ProductFeature getMainProductFeature() throws RemoteException;
  /** Remove the Main ProductFeature entity corresponding to this entity. */
  public void removeMainProductFeature() throws RemoteException;  

  /** Get the Assoc ProductFeature entity corresponding to this entity. */
  public ProductFeature getAssocProductFeature() throws RemoteException;
  /** Remove the Assoc ProductFeature entity corresponding to this entity. */
  public void removeAssocProductFeature() throws RemoteException;  

}
