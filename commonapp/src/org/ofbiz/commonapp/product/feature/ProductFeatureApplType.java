
package org.ofbiz.commonapp.product.feature;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Product Feature Applicability Type Entity
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

public interface ProductFeatureApplType extends EJBObject
{
  /** Get the primary key of the PRODUCT_FEATURE_APPL_TYPE_ID column of the PRODUCT_FEATURE_APPL_TYPE table. */
  public String getProductFeatureApplTypeId() throws RemoteException;
  
  /** Get the value of the PARENT_TYPE_ID column of the PRODUCT_FEATURE_APPL_TYPE table. */
  public String getParentTypeId() throws RemoteException;
  /** Set the value of the PARENT_TYPE_ID column of the PRODUCT_FEATURE_APPL_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException;
  
  /** Get the value of the HAS_TABLE column of the PRODUCT_FEATURE_APPL_TYPE table. */
  public String getHasTable() throws RemoteException;
  /** Set the value of the HAS_TABLE column of the PRODUCT_FEATURE_APPL_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the PRODUCT_FEATURE_APPL_TYPE table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the PRODUCT_FEATURE_APPL_TYPE table. */
  public void setDescription(String description) throws RemoteException;
  

  /** Get the value object of this ProductFeatureApplType class. */
  public ProductFeatureApplType getValueObject() throws RemoteException;
  /** Set the values in the value object of this ProductFeatureApplType class. */
  public void setValueObject(ProductFeatureApplType productFeatureApplTypeValue) throws RemoteException;


  /** Get the Parent ProductFeatureApplType entity corresponding to this entity. */
  public ProductFeatureApplType getParentProductFeatureApplType() throws RemoteException;
  /** Remove the Parent ProductFeatureApplType entity corresponding to this entity. */
  public void removeParentProductFeatureApplType() throws RemoteException;  

  /** Get a collection of Child ProductFeatureApplType related entities. */
  public Collection getChildProductFeatureApplTypes() throws RemoteException;
  /** Get the Child ProductFeatureApplType keyed by member(s) of this class, and other passed parameters. */
  public ProductFeatureApplType getChildProductFeatureApplType(String productFeatureApplTypeId) throws RemoteException;
  /** Remove Child ProductFeatureApplType related entities. */
  public void removeChildProductFeatureApplTypes() throws RemoteException;
  /** Remove the Child ProductFeatureApplType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildProductFeatureApplType(String productFeatureApplTypeId) throws RemoteException;

  /** Get a collection of  ProductFeatureAppl related entities. */
  public Collection getProductFeatureAppls() throws RemoteException;
  /** Get the  ProductFeatureAppl keyed by member(s) of this class, and other passed parameters. */
  public ProductFeatureAppl getProductFeatureAppl(String productId, String productFeatureId) throws RemoteException;
  /** Remove  ProductFeatureAppl related entities. */
  public void removeProductFeatureAppls() throws RemoteException;
  /** Remove the  ProductFeatureAppl keyed by member(s) of this class, and other passed parameters. */
  public void removeProductFeatureAppl(String productId, String productFeatureId) throws RemoteException;

}
