
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import java.math.*;

/**
 * <p><b>Title:</b> Price Component Attribute Entity
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
 *@created    Fri Jul 27 01:18:30 MDT 2001
 *@version    1.0
 */

public interface PriceComponentAttributeHome extends EJBHome
{

  public PriceComponentAttribute create(String priceComponentId, String name, String value) throws RemoteException, CreateException;
  public PriceComponentAttribute create(String priceComponentId, String name) throws RemoteException, CreateException;
  public PriceComponentAttribute findByPrimaryKey(org.ofbiz.commonapp.product.price.PriceComponentAttributePK primaryKey) throws RemoteException, FinderException;
  public Collection findAll() throws RemoteException, FinderException;


  /**
   *  Finds PriceComponentAttributes by the following fields:
   *

   *@param  priceComponentId                  Field for the PRICE_COMPONENT_ID column.
   *@return      Collection containing the found PriceComponentAttributes
   */
  public Collection findByPriceComponentId(String priceComponentId) throws RemoteException, FinderException;

  /**
   *  Finds PriceComponentAttributes by the following fields:
   *

   *@param  name                  Field for the NAME column.
   *@return      Collection containing the found PriceComponentAttributes
   */
  public Collection findByName(String name) throws RemoteException, FinderException;

}
