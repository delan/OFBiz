
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import java.math.*;

/**
 * <p><b>Title:</b> Product Association Entity
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

public interface ProductAssocHome extends EJBHome
{

  public ProductAssoc create(String productId, String productIdTo, String productAssocTypeId, java.util.Date fromDate, java.util.Date thruDate, String reason, Double quantity, String instruction) throws RemoteException, CreateException;
  public ProductAssoc create(String productId, String productIdTo, String productAssocTypeId) throws RemoteException, CreateException;
  public ProductAssoc findByPrimaryKey(org.ofbiz.commonapp.product.product.ProductAssocPK primaryKey) throws RemoteException, FinderException;
  public Collection findAll() throws RemoteException, FinderException;


  /**
   *  Finds ProductAssocs by the following fields:
   *

   *@param  productId                  Field for the PRODUCT_ID column.
   *@return      Collection containing the found ProductAssocs
   */
  public Collection findByProductId(String productId) throws RemoteException, FinderException;

  /**
   *  Finds ProductAssocs by the following fields:
   *

   *@param  productIdTo                  Field for the PRODUCT_ID_TO column.
   *@return      Collection containing the found ProductAssocs
   */
  public Collection findByProductIdTo(String productIdTo) throws RemoteException, FinderException;

  /**
   *  Finds ProductAssocs by the following fields:
   *

   *@param  productAssocTypeId                  Field for the PRODUCT_ASSOC_TYPE_ID column.
   *@return      Collection containing the found ProductAssocs
   */
  public Collection findByProductAssocTypeId(String productAssocTypeId) throws RemoteException, FinderException;

  /**
   *  Finds ProductAssocs by the following fields:
   *

   *@param  productId                  Field for the PRODUCT_ID column.
   *@param  productIdTo                  Field for the PRODUCT_ID_TO column.
   *@return      Collection containing the found ProductAssocs
   */
  public Collection findByProductIdAndProductIdTo(String productId, String productIdTo) throws RemoteException, FinderException;

  /**
   *  Finds ProductAssocs by the following fields:
   *

   *@param  productId                  Field for the PRODUCT_ID column.
   *@param  productAssocTypeId                  Field for the PRODUCT_ASSOC_TYPE_ID column.
   *@return      Collection containing the found ProductAssocs
   */
  public Collection findByProductIdAndProductAssocTypeId(String productId, String productAssocTypeId) throws RemoteException, FinderException;

  /**
   *  Finds ProductAssocs by the following fields:
   *

   *@param  productIdTo                  Field for the PRODUCT_ID_TO column.
   *@param  productAssocTypeId                  Field for the PRODUCT_ASSOC_TYPE_ID column.
   *@return      Collection containing the found ProductAssocs
   */
  public Collection findByProductIdToAndProductAssocTypeId(String productIdTo, String productAssocTypeId) throws RemoteException, FinderException;

}
