
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import java.math.*;

/**
 * <p><b>Title:</b> Price Component Type Entity
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
 *@created    Fri Jul 27 01:18:29 MDT 2001
 *@version    1.0
 */

public interface PriceComponentTypeHome extends EJBHome
{

  public PriceComponentType create(String priceComponentTypeId, String parentTypeId, String hasTable, String description) throws RemoteException, CreateException;
  public PriceComponentType create(String priceComponentTypeId) throws RemoteException, CreateException;
  public PriceComponentType findByPrimaryKey(java.lang.String primaryKey) throws RemoteException, FinderException;
  public Collection findAll() throws RemoteException, FinderException;


  /**
   *  Finds PriceComponentTypes by the following fields:
   *

   *@param  parentTypeId                  Field for the PARENT_TYPE_ID column.
   *@return      Collection containing the found PriceComponentTypes
   */
  public Collection findByParentTypeId(String parentTypeId) throws RemoteException, FinderException;

  /**
   *  Finds PriceComponentTypes by the following fields:
   *

   *@param  hasTable                  Field for the HAS_TABLE column.
   *@return      Collection containing the found PriceComponentTypes
   */
  public Collection findByHasTable(String hasTable) throws RemoteException, FinderException;

}
