
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import java.math.*;

/**
 * <p><b>Title:</b> Price Component Entity
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

public interface PriceComponentHome extends EJBHome
{

  public PriceComponent create(String priceComponentId, String priceComponentTypeId, String partyId, String partyTypeId, String productId, String productFeatureId, String productCategoryId, String agreementId, String agreementItemSeqId, String uomId, String geoId, String saleTypeId, String orderValueBreakId, String quantityBreakId, String utilizationUomId, Double utilizationQuantity, java.util.Date fromDate, java.util.Date thruDate, Double price, Double percent, String comment) throws RemoteException, CreateException;
  public PriceComponent create(String priceComponentId) throws RemoteException, CreateException;
  public PriceComponent findByPrimaryKey(java.lang.String primaryKey) throws RemoteException, FinderException;
  public Collection findAll() throws RemoteException, FinderException;


  /**
   *  Finds PriceComponents by the following fields:
   *

   *@param  priceComponentTypeId                  Field for the PRICE_COMPONENT_TYPE_ID column.
   *@return      Collection containing the found PriceComponents
   */
  public Collection findByPriceComponentTypeId(String priceComponentTypeId) throws RemoteException, FinderException;

  /**
   *  Finds PriceComponents by the following fields:
   *

   *@param  partyId                  Field for the PARTY_ID column.
   *@return      Collection containing the found PriceComponents
   */
  public Collection findByPartyId(String partyId) throws RemoteException, FinderException;

  /**
   *  Finds PriceComponents by the following fields:
   *

   *@param  partyTypeId                  Field for the PARTY_TYPE_ID column.
   *@return      Collection containing the found PriceComponents
   */
  public Collection findByPartyTypeId(String partyTypeId) throws RemoteException, FinderException;

  /**
   *  Finds PriceComponents by the following fields:
   *

   *@param  partyId                  Field for the PARTY_ID column.
   *@param  partyTypeId                  Field for the PARTY_TYPE_ID column.
   *@return      Collection containing the found PriceComponents
   */
  public Collection findByPartyIdAndPartyTypeId(String partyId, String partyTypeId) throws RemoteException, FinderException;

  /**
   *  Finds PriceComponents by the following fields:
   *

   *@param  productId                  Field for the PRODUCT_ID column.
   *@return      Collection containing the found PriceComponents
   */
  public Collection findByProductId(String productId) throws RemoteException, FinderException;

  /**
   *  Finds PriceComponents by the following fields:
   *

   *@param  productFeatureId                  Field for the PRODUCT_FEATURE_ID column.
   *@return      Collection containing the found PriceComponents
   */
  public Collection findByProductFeatureId(String productFeatureId) throws RemoteException, FinderException;

  /**
   *  Finds PriceComponents by the following fields:
   *

   *@param  productId                  Field for the PRODUCT_ID column.
   *@param  productFeatureId                  Field for the PRODUCT_FEATURE_ID column.
   *@return      Collection containing the found PriceComponents
   */
  public Collection findByProductIdAndProductFeatureId(String productId, String productFeatureId) throws RemoteException, FinderException;

  /**
   *  Finds PriceComponents by the following fields:
   *

   *@param  productCategoryId                  Field for the PRODUCT_CATEGORY_ID column.
   *@return      Collection containing the found PriceComponents
   */
  public Collection findByProductCategoryId(String productCategoryId) throws RemoteException, FinderException;

  /**
   *  Finds PriceComponents by the following fields:
   *

   *@param  productId                  Field for the PRODUCT_ID column.
   *@param  productCategoryId                  Field for the PRODUCT_CATEGORY_ID column.
   *@return      Collection containing the found PriceComponents
   */
  public Collection findByProductIdAndProductCategoryId(String productId, String productCategoryId) throws RemoteException, FinderException;

  /**
   *  Finds PriceComponents by the following fields:
   *

   *@param  agreementId                  Field for the AGREEMENT_ID column.
   *@return      Collection containing the found PriceComponents
   */
  public Collection findByAgreementId(String agreementId) throws RemoteException, FinderException;

  /**
   *  Finds PriceComponents by the following fields:
   *

   *@param  agreementId                  Field for the AGREEMENT_ID column.
   *@param  agreementItemSeqId                  Field for the AGREEMENT_ITEM_SEQ_ID column.
   *@return      Collection containing the found PriceComponents
   */
  public Collection findByAgreementIdAndAgreementItemSeqId(String agreementId, String agreementItemSeqId) throws RemoteException, FinderException;

  /**
   *  Finds PriceComponents by the following fields:
   *

   *@param  uomId                  Field for the UOM_ID column.
   *@return      Collection containing the found PriceComponents
   */
  public Collection findByUomId(String uomId) throws RemoteException, FinderException;

  /**
   *  Finds PriceComponents by the following fields:
   *

   *@param  geoId                  Field for the GEO_ID column.
   *@return      Collection containing the found PriceComponents
   */
  public Collection findByGeoId(String geoId) throws RemoteException, FinderException;

  /**
   *  Finds PriceComponents by the following fields:
   *

   *@param  saleTypeId                  Field for the SALE_TYPE_ID column.
   *@return      Collection containing the found PriceComponents
   */
  public Collection findBySaleTypeId(String saleTypeId) throws RemoteException, FinderException;

  /**
   *  Finds PriceComponents by the following fields:
   *

   *@param  orderValueBreakId                  Field for the ORDER_VALUE_BREAK_ID column.
   *@return      Collection containing the found PriceComponents
   */
  public Collection findByOrderValueBreakId(String orderValueBreakId) throws RemoteException, FinderException;

  /**
   *  Finds PriceComponents by the following fields:
   *

   *@param  quantityBreakId                  Field for the QUANTITY_BREAK_ID column.
   *@return      Collection containing the found PriceComponents
   */
  public Collection findByQuantityBreakId(String quantityBreakId) throws RemoteException, FinderException;

}
