
package org.ofbiz.commonapp.product.price;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Price Component Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the PriceComponent Entity EJB; acts as a proxy for the Home interface
 *
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
public class PriceComponentHelper
{

  /** A static variable to cache the Home object for the PriceComponent EJB */
  private static PriceComponentHome priceComponentHome = null;

  /** Initializes the priceComponentHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The PriceComponentHome instance for the default EJB server
   */
  public static PriceComponentHome getPriceComponentHome()
  {
    if(priceComponentHome == null) //don't want to block here
    {
      synchronized(PriceComponentHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(priceComponentHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.price.PriceComponentHome");
            priceComponentHome = (PriceComponentHome)MyNarrow.narrow(homeObject, PriceComponentHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("priceComponent home obtained " + priceComponentHome);
        }
      }
    }
    return priceComponentHome;
  }




  /** Remove the PriceComponent corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    PriceComponent priceComponent = findByPrimaryKey(primaryKey);
    try
    {
      if(priceComponent != null)
      {
        priceComponent.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a PriceComponent by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The PriceComponent corresponding to the primaryKey
   */
  public static PriceComponent findByPrimaryKey(java.lang.String primaryKey)
  {
    PriceComponent priceComponent = null;
    Debug.logInfo("PriceComponentHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      priceComponent = (PriceComponent)MyNarrow.narrow(getPriceComponentHome().findByPrimaryKey(primaryKey), PriceComponent.class);
      if(priceComponent != null)
      {
        priceComponent = priceComponent.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return priceComponent;
  }

  /** Finds all PriceComponent entities
   *@return    Collection containing all PriceComponent entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("PriceComponentHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getPriceComponentHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a PriceComponent
   *@param  priceComponentId                  Field of the PRICE_COMPONENT_ID column.
   *@param  priceComponentTypeId                  Field of the PRICE_COMPONENT_TYPE_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  agreementId                  Field of the AGREEMENT_ID column.
   *@param  agreementItemSeqId                  Field of the AGREEMENT_ITEM_SEQ_ID column.
   *@param  uomId                  Field of the UOM_ID column.
   *@param  geoId                  Field of the GEO_ID column.
   *@param  saleTypeId                  Field of the SALE_TYPE_ID column.
   *@param  orderValueBreakId                  Field of the ORDER_VALUE_BREAK_ID column.
   *@param  quantityBreakId                  Field of the QUANTITY_BREAK_ID column.
   *@param  utilizationUomId                  Field of the UTILIZATION_UOM_ID column.
   *@param  utilizationQuantity                  Field of the UTILIZATION_QUANTITY column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  price                  Field of the PRICE column.
   *@param  percent                  Field of the PERCENT column.
   *@param  comment                  Field of the COMMENT column.
   *@return                Description of the Returned Value
   */
  public static PriceComponent create(String priceComponentId, String priceComponentTypeId, String partyId, String partyTypeId, String productId, String productFeatureId, String productCategoryId, String agreementId, String agreementItemSeqId, String uomId, String geoId, String saleTypeId, String orderValueBreakId, String quantityBreakId, String utilizationUomId, Double utilizationQuantity, java.util.Date fromDate, java.util.Date thruDate, Double price, Double percent, String comment)
  {
    PriceComponent priceComponent = null;
    Debug.logInfo("PriceComponentHelper.create: priceComponentId: " + priceComponentId);
    if(priceComponentId == null) { return null; }

    try { priceComponent = (PriceComponent)MyNarrow.narrow(getPriceComponentHome().create(priceComponentId, priceComponentTypeId, partyId, partyTypeId, productId, productFeatureId, productCategoryId, agreementId, agreementItemSeqId, uomId, geoId, saleTypeId, orderValueBreakId, quantityBreakId, utilizationUomId, utilizationQuantity, fromDate, thruDate, price, percent, comment), PriceComponent.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create priceComponent with priceComponentId: " + priceComponentId);
      Debug.logError(ce);
      priceComponent = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return priceComponent;
  }

  /** Updates the corresponding PriceComponent
   *@param  priceComponentId                  Field of the PRICE_COMPONENT_ID column.
   *@param  priceComponentTypeId                  Field of the PRICE_COMPONENT_TYPE_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  agreementId                  Field of the AGREEMENT_ID column.
   *@param  agreementItemSeqId                  Field of the AGREEMENT_ITEM_SEQ_ID column.
   *@param  uomId                  Field of the UOM_ID column.
   *@param  geoId                  Field of the GEO_ID column.
   *@param  saleTypeId                  Field of the SALE_TYPE_ID column.
   *@param  orderValueBreakId                  Field of the ORDER_VALUE_BREAK_ID column.
   *@param  quantityBreakId                  Field of the QUANTITY_BREAK_ID column.
   *@param  utilizationUomId                  Field of the UTILIZATION_UOM_ID column.
   *@param  utilizationQuantity                  Field of the UTILIZATION_QUANTITY column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  price                  Field of the PRICE column.
   *@param  percent                  Field of the PERCENT column.
   *@param  comment                  Field of the COMMENT column.
   *@return                Description of the Returned Value
   */
  public static PriceComponent update(String priceComponentId, String priceComponentTypeId, String partyId, String partyTypeId, String productId, String productFeatureId, String productCategoryId, String agreementId, String agreementItemSeqId, String uomId, String geoId, String saleTypeId, String orderValueBreakId, String quantityBreakId, String utilizationUomId, Double utilizationQuantity, java.util.Date fromDate, java.util.Date thruDate, Double price, Double percent, String comment) throws java.rmi.RemoteException
  {
    if(priceComponentId == null) { return null; }
    PriceComponent priceComponent = findByPrimaryKey(priceComponentId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PriceComponent priceComponentValue = new PriceComponentValue();

    if(priceComponentTypeId != null) { priceComponentValue.setPriceComponentTypeId(priceComponentTypeId); }
    if(partyId != null) { priceComponentValue.setPartyId(partyId); }
    if(partyTypeId != null) { priceComponentValue.setPartyTypeId(partyTypeId); }
    if(productId != null) { priceComponentValue.setProductId(productId); }
    if(productFeatureId != null) { priceComponentValue.setProductFeatureId(productFeatureId); }
    if(productCategoryId != null) { priceComponentValue.setProductCategoryId(productCategoryId); }
    if(agreementId != null) { priceComponentValue.setAgreementId(agreementId); }
    if(agreementItemSeqId != null) { priceComponentValue.setAgreementItemSeqId(agreementItemSeqId); }
    if(uomId != null) { priceComponentValue.setUomId(uomId); }
    if(geoId != null) { priceComponentValue.setGeoId(geoId); }
    if(saleTypeId != null) { priceComponentValue.setSaleTypeId(saleTypeId); }
    if(orderValueBreakId != null) { priceComponentValue.setOrderValueBreakId(orderValueBreakId); }
    if(quantityBreakId != null) { priceComponentValue.setQuantityBreakId(quantityBreakId); }
    if(utilizationUomId != null) { priceComponentValue.setUtilizationUomId(utilizationUomId); }
    if(utilizationQuantity != null) { priceComponentValue.setUtilizationQuantity(utilizationQuantity); }
    if(fromDate != null) { priceComponentValue.setFromDate(fromDate); }
    if(thruDate != null) { priceComponentValue.setThruDate(thruDate); }
    if(price != null) { priceComponentValue.setPrice(price); }
    if(percent != null) { priceComponentValue.setPercent(percent); }
    if(comment != null) { priceComponentValue.setComment(comment); }

    priceComponent.setValueObject(priceComponentValue);
    return priceComponent;
  }

  /** Removes/deletes the specified  PriceComponent
   *@param  priceComponentTypeId                  Field of the PRICE_COMPONENT_TYPE_ID column.
   */
  public static void removeByPriceComponentTypeId(String priceComponentTypeId)
  {
    if(priceComponentTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByPriceComponentTypeId(priceComponentTypeId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponent priceComponent = (PriceComponent) iterator.next();
        Debug.logInfo("Removing priceComponent with priceComponentTypeId:" + priceComponentTypeId);
        priceComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponent records by the following parameters:
   *@param  priceComponentTypeId                  Field of the PRICE_COMPONENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPriceComponentTypeId(String priceComponentTypeId)
  {
    Debug.logInfo("findByPriceComponentTypeId: priceComponentTypeId:" + priceComponentTypeId);

    Collection collection = null;
    if(priceComponentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentHome().findByPriceComponentTypeId(priceComponentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponent
   *@param  partyId                  Field of the PARTY_ID column.
   */
  public static void removeByPartyId(String partyId)
  {
    if(partyId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByPartyId(partyId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponent priceComponent = (PriceComponent) iterator.next();
        Debug.logInfo("Removing priceComponent with partyId:" + partyId);
        priceComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponent records by the following parameters:
   *@param  partyId                  Field of the PARTY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyId(String partyId)
  {
    Debug.logInfo("findByPartyId: partyId:" + partyId);

    Collection collection = null;
    if(partyId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentHome().findByPartyId(partyId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponent
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   */
  public static void removeByPartyTypeId(String partyTypeId)
  {
    if(partyTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByPartyTypeId(partyTypeId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponent priceComponent = (PriceComponent) iterator.next();
        Debug.logInfo("Removing priceComponent with partyTypeId:" + partyTypeId);
        priceComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponent records by the following parameters:
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyTypeId(String partyTypeId)
  {
    Debug.logInfo("findByPartyTypeId: partyTypeId:" + partyTypeId);

    Collection collection = null;
    if(partyTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentHome().findByPartyTypeId(partyTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponent
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   */
  public static void removeByPartyIdAndPartyTypeId(String partyId, String partyTypeId)
  {
    if(partyId == null || partyTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByPartyIdAndPartyTypeId(partyId, partyTypeId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponent priceComponent = (PriceComponent) iterator.next();
        Debug.logInfo("Removing priceComponent with partyId, partyTypeId:" + partyId + ", " + partyTypeId);
        priceComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponent records by the following parameters:
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyIdAndPartyTypeId(String partyId, String partyTypeId)
  {
    Debug.logInfo("findByPartyIdAndPartyTypeId: partyId, partyTypeId:" + partyId + ", " + partyTypeId);

    Collection collection = null;
    if(partyId == null || partyTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentHome().findByPartyIdAndPartyTypeId(partyId, partyTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponent
   *@param  productId                  Field of the PRODUCT_ID column.
   */
  public static void removeByProductId(String productId)
  {
    if(productId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductId(productId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponent priceComponent = (PriceComponent) iterator.next();
        Debug.logInfo("Removing priceComponent with productId:" + productId);
        priceComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponent records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductId(String productId)
  {
    Debug.logInfo("findByProductId: productId:" + productId);

    Collection collection = null;
    if(productId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentHome().findByProductId(productId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponent
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   */
  public static void removeByProductFeatureId(String productFeatureId)
  {
    if(productFeatureId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductFeatureId(productFeatureId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponent priceComponent = (PriceComponent) iterator.next();
        Debug.logInfo("Removing priceComponent with productFeatureId:" + productFeatureId);
        priceComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponent records by the following parameters:
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductFeatureId(String productFeatureId)
  {
    Debug.logInfo("findByProductFeatureId: productFeatureId:" + productFeatureId);

    Collection collection = null;
    if(productFeatureId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentHome().findByProductFeatureId(productFeatureId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponent
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   */
  public static void removeByProductIdAndProductFeatureId(String productId, String productFeatureId)
  {
    if(productId == null || productFeatureId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductIdAndProductFeatureId(productId, productFeatureId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponent priceComponent = (PriceComponent) iterator.next();
        Debug.logInfo("Removing priceComponent with productId, productFeatureId:" + productId + ", " + productFeatureId);
        priceComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponent records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductIdAndProductFeatureId(String productId, String productFeatureId)
  {
    Debug.logInfo("findByProductIdAndProductFeatureId: productId, productFeatureId:" + productId + ", " + productFeatureId);

    Collection collection = null;
    if(productId == null || productFeatureId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentHome().findByProductIdAndProductFeatureId(productId, productFeatureId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponent
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   */
  public static void removeByProductCategoryId(String productCategoryId)
  {
    if(productCategoryId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductCategoryId(productCategoryId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponent priceComponent = (PriceComponent) iterator.next();
        Debug.logInfo("Removing priceComponent with productCategoryId:" + productCategoryId);
        priceComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponent records by the following parameters:
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductCategoryId(String productCategoryId)
  {
    Debug.logInfo("findByProductCategoryId: productCategoryId:" + productCategoryId);

    Collection collection = null;
    if(productCategoryId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentHome().findByProductCategoryId(productCategoryId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponent
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   */
  public static void removeByProductIdAndProductCategoryId(String productId, String productCategoryId)
  {
    if(productId == null || productCategoryId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductIdAndProductCategoryId(productId, productCategoryId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponent priceComponent = (PriceComponent) iterator.next();
        Debug.logInfo("Removing priceComponent with productId, productCategoryId:" + productId + ", " + productCategoryId);
        priceComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponent records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductIdAndProductCategoryId(String productId, String productCategoryId)
  {
    Debug.logInfo("findByProductIdAndProductCategoryId: productId, productCategoryId:" + productId + ", " + productCategoryId);

    Collection collection = null;
    if(productId == null || productCategoryId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentHome().findByProductIdAndProductCategoryId(productId, productCategoryId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponent
   *@param  agreementId                  Field of the AGREEMENT_ID column.
   */
  public static void removeByAgreementId(String agreementId)
  {
    if(agreementId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByAgreementId(agreementId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponent priceComponent = (PriceComponent) iterator.next();
        Debug.logInfo("Removing priceComponent with agreementId:" + agreementId);
        priceComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponent records by the following parameters:
   *@param  agreementId                  Field of the AGREEMENT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByAgreementId(String agreementId)
  {
    Debug.logInfo("findByAgreementId: agreementId:" + agreementId);

    Collection collection = null;
    if(agreementId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentHome().findByAgreementId(agreementId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponent
   *@param  agreementId                  Field of the AGREEMENT_ID column.
   *@param  agreementItemSeqId                  Field of the AGREEMENT_ITEM_SEQ_ID column.
   */
  public static void removeByAgreementIdAndAgreementItemSeqId(String agreementId, String agreementItemSeqId)
  {
    if(agreementId == null || agreementItemSeqId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByAgreementIdAndAgreementItemSeqId(agreementId, agreementItemSeqId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponent priceComponent = (PriceComponent) iterator.next();
        Debug.logInfo("Removing priceComponent with agreementId, agreementItemSeqId:" + agreementId + ", " + agreementItemSeqId);
        priceComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponent records by the following parameters:
   *@param  agreementId                  Field of the AGREEMENT_ID column.
   *@param  agreementItemSeqId                  Field of the AGREEMENT_ITEM_SEQ_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByAgreementIdAndAgreementItemSeqId(String agreementId, String agreementItemSeqId)
  {
    Debug.logInfo("findByAgreementIdAndAgreementItemSeqId: agreementId, agreementItemSeqId:" + agreementId + ", " + agreementItemSeqId);

    Collection collection = null;
    if(agreementId == null || agreementItemSeqId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentHome().findByAgreementIdAndAgreementItemSeqId(agreementId, agreementItemSeqId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponent
   *@param  uomId                  Field of the UOM_ID column.
   */
  public static void removeByUomId(String uomId)
  {
    if(uomId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByUomId(uomId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponent priceComponent = (PriceComponent) iterator.next();
        Debug.logInfo("Removing priceComponent with uomId:" + uomId);
        priceComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponent records by the following parameters:
   *@param  uomId                  Field of the UOM_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByUomId(String uomId)
  {
    Debug.logInfo("findByUomId: uomId:" + uomId);

    Collection collection = null;
    if(uomId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentHome().findByUomId(uomId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponent
   *@param  geoId                  Field of the GEO_ID column.
   */
  public static void removeByGeoId(String geoId)
  {
    if(geoId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByGeoId(geoId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponent priceComponent = (PriceComponent) iterator.next();
        Debug.logInfo("Removing priceComponent with geoId:" + geoId);
        priceComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponent records by the following parameters:
   *@param  geoId                  Field of the GEO_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByGeoId(String geoId)
  {
    Debug.logInfo("findByGeoId: geoId:" + geoId);

    Collection collection = null;
    if(geoId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentHome().findByGeoId(geoId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponent
   *@param  saleTypeId                  Field of the SALE_TYPE_ID column.
   */
  public static void removeBySaleTypeId(String saleTypeId)
  {
    if(saleTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findBySaleTypeId(saleTypeId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponent priceComponent = (PriceComponent) iterator.next();
        Debug.logInfo("Removing priceComponent with saleTypeId:" + saleTypeId);
        priceComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponent records by the following parameters:
   *@param  saleTypeId                  Field of the SALE_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findBySaleTypeId(String saleTypeId)
  {
    Debug.logInfo("findBySaleTypeId: saleTypeId:" + saleTypeId);

    Collection collection = null;
    if(saleTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentHome().findBySaleTypeId(saleTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponent
   *@param  orderValueBreakId                  Field of the ORDER_VALUE_BREAK_ID column.
   */
  public static void removeByOrderValueBreakId(String orderValueBreakId)
  {
    if(orderValueBreakId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByOrderValueBreakId(orderValueBreakId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponent priceComponent = (PriceComponent) iterator.next();
        Debug.logInfo("Removing priceComponent with orderValueBreakId:" + orderValueBreakId);
        priceComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponent records by the following parameters:
   *@param  orderValueBreakId                  Field of the ORDER_VALUE_BREAK_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByOrderValueBreakId(String orderValueBreakId)
  {
    Debug.logInfo("findByOrderValueBreakId: orderValueBreakId:" + orderValueBreakId);

    Collection collection = null;
    if(orderValueBreakId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentHome().findByOrderValueBreakId(orderValueBreakId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponent
   *@param  quantityBreakId                  Field of the QUANTITY_BREAK_ID column.
   */
  public static void removeByQuantityBreakId(String quantityBreakId)
  {
    if(quantityBreakId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByQuantityBreakId(quantityBreakId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponent priceComponent = (PriceComponent) iterator.next();
        Debug.logInfo("Removing priceComponent with quantityBreakId:" + quantityBreakId);
        priceComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponent records by the following parameters:
   *@param  quantityBreakId                  Field of the QUANTITY_BREAK_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByQuantityBreakId(String quantityBreakId)
  {
    Debug.logInfo("findByQuantityBreakId: quantityBreakId:" + quantityBreakId);

    Collection collection = null;
    if(quantityBreakId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentHome().findByQuantityBreakId(quantityBreakId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
