
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Product Association Type Entity
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

public interface ProductAssocType extends EJBObject
{
  /** Get the primary key of the PRODUCT_ASSOC_TYPE_ID column of the PRODUCT_ASSOC_TYPE table. */
  public String getProductAssocTypeId() throws RemoteException;
  
  /** Get the value of the PARENT_TYPE_ID column of the PRODUCT_ASSOC_TYPE table. */
  public String getParentTypeId() throws RemoteException;
  /** Set the value of the PARENT_TYPE_ID column of the PRODUCT_ASSOC_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException;
  
  /** Get the value of the HAS_TABLE column of the PRODUCT_ASSOC_TYPE table. */
  public String getHasTable() throws RemoteException;
  /** Set the value of the HAS_TABLE column of the PRODUCT_ASSOC_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the PRODUCT_ASSOC_TYPE table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the PRODUCT_ASSOC_TYPE table. */
  public void setDescription(String description) throws RemoteException;
  

  /** Get the value object of this ProductAssocType class. */
  public ProductAssocType getValueObject() throws RemoteException;
  /** Set the values in the value object of this ProductAssocType class. */
  public void setValueObject(ProductAssocType productAssocTypeValue) throws RemoteException;


  /** Get the Parent ProductAssocType entity corresponding to this entity. */
  public ProductAssocType getParentProductAssocType() throws RemoteException;
  /** Remove the Parent ProductAssocType entity corresponding to this entity. */
  public void removeParentProductAssocType() throws RemoteException;  

  /** Get a collection of Child ProductAssocType related entities. */
  public Collection getChildProductAssocTypes() throws RemoteException;
  /** Get the Child ProductAssocType keyed by member(s) of this class, and other passed parameters. */
  public ProductAssocType getChildProductAssocType(String productAssocTypeId) throws RemoteException;
  /** Remove Child ProductAssocType related entities. */
  public void removeChildProductAssocTypes() throws RemoteException;
  /** Remove the Child ProductAssocType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildProductAssocType(String productAssocTypeId) throws RemoteException;

  /** Get a collection of  ProductAssoc related entities. */
  public Collection getProductAssocs() throws RemoteException;
  /** Get the  ProductAssoc keyed by member(s) of this class, and other passed parameters. */
  public ProductAssoc getProductAssoc(String productId, String productIdTo) throws RemoteException;
  /** Remove  ProductAssoc related entities. */
  public void removeProductAssocs() throws RemoteException;
  /** Remove the  ProductAssoc keyed by member(s) of this class, and other passed parameters. */
  public void removeProductAssoc(String productId, String productIdTo) throws RemoteException;

}
