
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import java.math.*;

/**
 * <p><b>Title:</b> Product Entity
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
 *@created    Fri Jul 27 01:18:24 MDT 2001
 *@version    1.0
 */

public interface ProductHome extends EJBHome
{

  public Product create(String productId, String primaryProductCategoryId, String manufacturerPartyId, String uomId, Double quantityIncluded, java.util.Date introductionDate, java.util.Date salesDiscontinuationDate, java.util.Date supportDiscontinuationDate, String name, String comment, String description, String longDescription, String smallImageUrl, String largeImageUrl, Double defaultPrice) throws RemoteException, CreateException;
  public Product create(String productId) throws RemoteException, CreateException;
  public Product findByPrimaryKey(java.lang.String primaryKey) throws RemoteException, FinderException;
  public Collection findAll() throws RemoteException, FinderException;


  /**
   *  Finds Products by the following fields:
   *

   *@param  primaryProductCategoryId                  Field for the PRIMARY_PRODUCT_CATEGORY_ID column.
   *@return      Collection containing the found Products
   */
  public Collection findByPrimaryProductCategoryId(String primaryProductCategoryId) throws RemoteException, FinderException;

  /**
   *  Finds Products by the following fields:
   *

   *@param  manufacturerPartyId                  Field for the MANUFACTURER_PARTY_ID column.
   *@return      Collection containing the found Products
   */
  public Collection findByManufacturerPartyId(String manufacturerPartyId) throws RemoteException, FinderException;

  /**
   *  Finds Products by the following fields:
   *

   *@param  uomId                  Field for the UOM_ID column.
   *@return      Collection containing the found Products
   */
  public Collection findByUomId(String uomId) throws RemoteException, FinderException;

}
