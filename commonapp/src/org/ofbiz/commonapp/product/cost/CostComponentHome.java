
package org.ofbiz.commonapp.product.cost;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import java.math.*;

/**
 * <p><b>Title:</b> Cost Component Entity
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

public interface CostComponentHome extends EJBHome
{

  public CostComponent create(String costComponentId, String costComponentTypeId, String productId, String productFeatureId, String partyId, String geoId, java.util.Date fromDate, java.util.Date thruDate, Double cost) throws RemoteException, CreateException;
  public CostComponent create(String costComponentId) throws RemoteException, CreateException;
  public CostComponent findByPrimaryKey(java.lang.String primaryKey) throws RemoteException, FinderException;
  public Collection findAll() throws RemoteException, FinderException;


  /**
   *  Finds CostComponents by the following fields:
   *

   *@param  costComponentTypeId                  Field for the COST_COMPONENT_TYPE_ID column.
   *@return      Collection containing the found CostComponents
   */
  public Collection findByCostComponentTypeId(String costComponentTypeId) throws RemoteException, FinderException;

  /**
   *  Finds CostComponents by the following fields:
   *

   *@param  productId                  Field for the PRODUCT_ID column.
   *@return      Collection containing the found CostComponents
   */
  public Collection findByProductId(String productId) throws RemoteException, FinderException;

  /**
   *  Finds CostComponents by the following fields:
   *

   *@param  productFeatureId                  Field for the PRODUCT_FEATURE_ID column.
   *@return      Collection containing the found CostComponents
   */
  public Collection findByProductFeatureId(String productFeatureId) throws RemoteException, FinderException;

  /**
   *  Finds CostComponents by the following fields:
   *

   *@param  productId                  Field for the PRODUCT_ID column.
   *@param  productFeatureId                  Field for the PRODUCT_FEATURE_ID column.
   *@return      Collection containing the found CostComponents
   */
  public Collection findByProductIdAndProductFeatureId(String productId, String productFeatureId) throws RemoteException, FinderException;

  /**
   *  Finds CostComponents by the following fields:
   *

   *@param  partyId                  Field for the PARTY_ID column.
   *@return      Collection containing the found CostComponents
   */
  public Collection findByPartyId(String partyId) throws RemoteException, FinderException;

  /**
   *  Finds CostComponents by the following fields:
   *

   *@param  geoId                  Field for the GEO_ID column.
   *@return      Collection containing the found CostComponents
   */
  public Collection findByGeoId(String geoId) throws RemoteException, FinderException;

}
