
package org.ofbiz.commonapp.product.supplier;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import java.math.*;

/**
 * <p><b>Title:</b> Reorder Guideline Entity
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
 *@created    Fri Jul 27 01:18:33 MDT 2001
 *@version    1.0
 */

public interface ReorderGuidelineHome extends EJBHome
{

  public ReorderGuideline create(String reorderGuidelineId, String productId, String partyId, String roleTypeId, String facilityId, String geoId, java.util.Date fromDate, java.util.Date thruDate, Double reorderQuantity, Double reorderLevel) throws RemoteException, CreateException;
  public ReorderGuideline create(String reorderGuidelineId) throws RemoteException, CreateException;
  public ReorderGuideline findByPrimaryKey(java.lang.String primaryKey) throws RemoteException, FinderException;
  public Collection findAll() throws RemoteException, FinderException;


  /**
   *  Finds ReorderGuidelines by the following fields:
   *

   *@param  productId                  Field for the PRODUCT_ID column.
   *@return      Collection containing the found ReorderGuidelines
   */
  public Collection findByProductId(String productId) throws RemoteException, FinderException;

  /**
   *  Finds ReorderGuidelines by the following fields:
   *

   *@param  partyId                  Field for the PARTY_ID column.
   *@return      Collection containing the found ReorderGuidelines
   */
  public Collection findByPartyId(String partyId) throws RemoteException, FinderException;

  /**
   *  Finds ReorderGuidelines by the following fields:
   *

   *@param  roleTypeId                  Field for the ROLE_TYPE_ID column.
   *@return      Collection containing the found ReorderGuidelines
   */
  public Collection findByRoleTypeId(String roleTypeId) throws RemoteException, FinderException;

  /**
   *  Finds ReorderGuidelines by the following fields:
   *

   *@param  partyId                  Field for the PARTY_ID column.
   *@param  roleTypeId                  Field for the ROLE_TYPE_ID column.
   *@return      Collection containing the found ReorderGuidelines
   */
  public Collection findByPartyIdAndRoleTypeId(String partyId, String roleTypeId) throws RemoteException, FinderException;

  /**
   *  Finds ReorderGuidelines by the following fields:
   *

   *@param  facilityId                  Field for the FACILITY_ID column.
   *@return      Collection containing the found ReorderGuidelines
   */
  public Collection findByFacilityId(String facilityId) throws RemoteException, FinderException;

  /**
   *  Finds ReorderGuidelines by the following fields:
   *

   *@param  geoId                  Field for the GEO_ID column.
   *@return      Collection containing the found ReorderGuidelines
   */
  public Collection findByGeoId(String geoId) throws RemoteException, FinderException;

}
