
package org.ofbiz.commonapp.product.feature;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Product Feature Interaction Type Entity
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

public interface ProductFeatureIactnType extends EJBObject
{
  /** Get the primary key of the PRODUCT_FEATURE_IACTN_TYPE_ID column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  public String getProductFeatureIactnTypeId() throws RemoteException;
  
  /** Get the value of the PARENT_TYPE_ID column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  public String getParentTypeId() throws RemoteException;
  /** Set the value of the PARENT_TYPE_ID column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException;
  
  /** Get the value of the HAS_TABLE column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  public String getHasTable() throws RemoteException;
  /** Set the value of the HAS_TABLE column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  public void setDescription(String description) throws RemoteException;
  

  /** Get the value object of this ProductFeatureIactnType class. */
  public ProductFeatureIactnType getValueObject() throws RemoteException;
  /** Set the values in the value object of this ProductFeatureIactnType class. */
  public void setValueObject(ProductFeatureIactnType productFeatureIactnTypeValue) throws RemoteException;


  /** Get the Parent ProductFeatureIactnType entity corresponding to this entity. */
  public ProductFeatureIactnType getParentProductFeatureIactnType() throws RemoteException;
  /** Remove the Parent ProductFeatureIactnType entity corresponding to this entity. */
  public void removeParentProductFeatureIactnType() throws RemoteException;  

  /** Get a collection of Child ProductFeatureIactnType related entities. */
  public Collection getChildProductFeatureIactnTypes() throws RemoteException;
  /** Get the Child ProductFeatureIactnType keyed by member(s) of this class, and other passed parameters. */
  public ProductFeatureIactnType getChildProductFeatureIactnType(String productFeatureIactnTypeId) throws RemoteException;
  /** Remove Child ProductFeatureIactnType related entities. */
  public void removeChildProductFeatureIactnTypes() throws RemoteException;
  /** Remove the Child ProductFeatureIactnType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildProductFeatureIactnType(String productFeatureIactnTypeId) throws RemoteException;

  /** Get a collection of  ProductFeatureIactn related entities. */
  public Collection getProductFeatureIactns() throws RemoteException;
  /** Get the  ProductFeatureIactn keyed by member(s) of this class, and other passed parameters. */
  public ProductFeatureIactn getProductFeatureIactn(String productFeatureId, String productFeatureIdTo) throws RemoteException;
  /** Remove  ProductFeatureIactn related entities. */
  public void removeProductFeatureIactns() throws RemoteException;
  /** Remove the  ProductFeatureIactn keyed by member(s) of this class, and other passed parameters. */
  public void removeProductFeatureIactn(String productFeatureId, String productFeatureIdTo) throws RemoteException;

}
