
package org.ofbiz.commonapp.product.supplier;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Supplier Product Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the SupplierProduct Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:33 MDT 2001
 *@version    1.0
 */
public class SupplierProductHelper
{

  /** A static variable to cache the Home object for the SupplierProduct EJB */
  private static SupplierProductHome supplierProductHome = null;

  /** Initializes the supplierProductHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The SupplierProductHome instance for the default EJB server
   */
  public static SupplierProductHome getSupplierProductHome()
  {
    if(supplierProductHome == null) //don't want to block here
    {
      synchronized(SupplierProductHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(supplierProductHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.supplier.SupplierProductHome");
            supplierProductHome = (SupplierProductHome)MyNarrow.narrow(homeObject, SupplierProductHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("supplierProduct home obtained " + supplierProductHome);
        }
      }
    }
    return supplierProductHome;
  }



  /** Remove the SupplierProduct corresponding to the primaryKey specified by fields
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   */
  public static void removeByPrimaryKey(String productId, String partyId)
  {
    if(productId == null || partyId == null)
    {
      return;
    }
    SupplierProductPK primaryKey = new SupplierProductPK(productId, partyId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the SupplierProduct corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.supplier.SupplierProductPK primaryKey)
  {
    if(primaryKey == null) return;
    SupplierProduct supplierProduct = findByPrimaryKey(primaryKey);
    try
    {
      if(supplierProduct != null)
      {
        supplierProduct.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a SupplierProduct by its Primary Key, specified by individual fields
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@return       The SupplierProduct corresponding to the primaryKey
   */
  public static SupplierProduct findByPrimaryKey(String productId, String partyId)
  {
    if(productId == null || partyId == null) return null;
    SupplierProductPK primaryKey = new SupplierProductPK(productId, partyId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a SupplierProduct by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The SupplierProduct corresponding to the primaryKey
   */
  public static SupplierProduct findByPrimaryKey(org.ofbiz.commonapp.product.supplier.SupplierProductPK primaryKey)
  {
    SupplierProduct supplierProduct = null;
    Debug.logInfo("SupplierProductHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      supplierProduct = (SupplierProduct)MyNarrow.narrow(getSupplierProductHome().findByPrimaryKey(primaryKey), SupplierProduct.class);
      if(supplierProduct != null)
      {
        supplierProduct = supplierProduct.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return supplierProduct;
  }

  /** Finds all SupplierProduct entities
   *@return    Collection containing all SupplierProduct entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("SupplierProductHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getSupplierProductHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a SupplierProduct
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  availableFromDate                  Field of the AVAILABLE_FROM_DATE column.
   *@param  availableThruDate                  Field of the AVAILABLE_THRU_DATE column.
   *@param  supplierPrefOrderId                  Field of the SUPPLIER_PREF_ORDER_ID column.
   *@param  supplierRatingTypeId                  Field of the SUPPLIER_RATING_TYPE_ID column.
   *@param  standardLeadTime                  Field of the STANDARD_LEAD_TIME column.
   *@param  comment                  Field of the COMMENT column.
   *@return                Description of the Returned Value
   */
  public static SupplierProduct create(String productId, String partyId, java.util.Date availableFromDate, java.util.Date availableThruDate, String supplierPrefOrderId, String supplierRatingTypeId, java.util.Date standardLeadTime, String comment)
  {
    SupplierProduct supplierProduct = null;
    Debug.logInfo("SupplierProductHelper.create: productId, partyId: " + productId + ", " + partyId);
    if(productId == null || partyId == null) { return null; }

    try { supplierProduct = (SupplierProduct)MyNarrow.narrow(getSupplierProductHome().create(productId, partyId, availableFromDate, availableThruDate, supplierPrefOrderId, supplierRatingTypeId, standardLeadTime, comment), SupplierProduct.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create supplierProduct with productId, partyId: " + productId + ", " + partyId);
      Debug.logError(ce);
      supplierProduct = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return supplierProduct;
  }

  /** Updates the corresponding SupplierProduct
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  availableFromDate                  Field of the AVAILABLE_FROM_DATE column.
   *@param  availableThruDate                  Field of the AVAILABLE_THRU_DATE column.
   *@param  supplierPrefOrderId                  Field of the SUPPLIER_PREF_ORDER_ID column.
   *@param  supplierRatingTypeId                  Field of the SUPPLIER_RATING_TYPE_ID column.
   *@param  standardLeadTime                  Field of the STANDARD_LEAD_TIME column.
   *@param  comment                  Field of the COMMENT column.
   *@return                Description of the Returned Value
   */
  public static SupplierProduct update(String productId, String partyId, java.util.Date availableFromDate, java.util.Date availableThruDate, String supplierPrefOrderId, String supplierRatingTypeId, java.util.Date standardLeadTime, String comment) throws java.rmi.RemoteException
  {
    if(productId == null || partyId == null) { return null; }
    SupplierProduct supplierProduct = findByPrimaryKey(productId, partyId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    SupplierProduct supplierProductValue = new SupplierProductValue();

    if(availableFromDate != null) { supplierProductValue.setAvailableFromDate(availableFromDate); }
    if(availableThruDate != null) { supplierProductValue.setAvailableThruDate(availableThruDate); }
    if(supplierPrefOrderId != null) { supplierProductValue.setSupplierPrefOrderId(supplierPrefOrderId); }
    if(supplierRatingTypeId != null) { supplierProductValue.setSupplierRatingTypeId(supplierRatingTypeId); }
    if(standardLeadTime != null) { supplierProductValue.setStandardLeadTime(standardLeadTime); }
    if(comment != null) { supplierProductValue.setComment(comment); }

    supplierProduct.setValueObject(supplierProductValue);
    return supplierProduct;
  }

  /** Removes/deletes the specified  SupplierProduct
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
        SupplierProduct supplierProduct = (SupplierProduct) iterator.next();
        Debug.logInfo("Removing supplierProduct with productId:" + productId);
        supplierProduct.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds SupplierProduct records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductId(String productId)
  {
    Debug.logInfo("findByProductId: productId:" + productId);

    Collection collection = null;
    if(productId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getSupplierProductHome().findByProductId(productId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  SupplierProduct
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
        SupplierProduct supplierProduct = (SupplierProduct) iterator.next();
        Debug.logInfo("Removing supplierProduct with partyId:" + partyId);
        supplierProduct.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds SupplierProduct records by the following parameters:
   *@param  partyId                  Field of the PARTY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyId(String partyId)
  {
    Debug.logInfo("findByPartyId: partyId:" + partyId);

    Collection collection = null;
    if(partyId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getSupplierProductHome().findByPartyId(partyId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  SupplierProduct
   *@param  supplierPrefOrderId                  Field of the SUPPLIER_PREF_ORDER_ID column.
   */
  public static void removeBySupplierPrefOrderId(String supplierPrefOrderId)
  {
    if(supplierPrefOrderId == null) return;
    Iterator iterator = UtilMisc.toIterator(findBySupplierPrefOrderId(supplierPrefOrderId));

    while(iterator.hasNext())
    {
      try
      {
        SupplierProduct supplierProduct = (SupplierProduct) iterator.next();
        Debug.logInfo("Removing supplierProduct with supplierPrefOrderId:" + supplierPrefOrderId);
        supplierProduct.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds SupplierProduct records by the following parameters:
   *@param  supplierPrefOrderId                  Field of the SUPPLIER_PREF_ORDER_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findBySupplierPrefOrderId(String supplierPrefOrderId)
  {
    Debug.logInfo("findBySupplierPrefOrderId: supplierPrefOrderId:" + supplierPrefOrderId);

    Collection collection = null;
    if(supplierPrefOrderId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getSupplierProductHome().findBySupplierPrefOrderId(supplierPrefOrderId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  SupplierProduct
   *@param  supplierRatingTypeId                  Field of the SUPPLIER_RATING_TYPE_ID column.
   */
  public static void removeBySupplierRatingTypeId(String supplierRatingTypeId)
  {
    if(supplierRatingTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findBySupplierRatingTypeId(supplierRatingTypeId));

    while(iterator.hasNext())
    {
      try
      {
        SupplierProduct supplierProduct = (SupplierProduct) iterator.next();
        Debug.logInfo("Removing supplierProduct with supplierRatingTypeId:" + supplierRatingTypeId);
        supplierProduct.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds SupplierProduct records by the following parameters:
   *@param  supplierRatingTypeId                  Field of the SUPPLIER_RATING_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findBySupplierRatingTypeId(String supplierRatingTypeId)
  {
    Debug.logInfo("findBySupplierRatingTypeId: supplierRatingTypeId:" + supplierRatingTypeId);

    Collection collection = null;
    if(supplierRatingTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getSupplierProductHome().findBySupplierRatingTypeId(supplierRatingTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
