
package org.ofbiz.commonapp.product.product;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the Product Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:24 MDT 2001
 *@version    1.0
 */
public class ProductHelper
{

  /** A static variable to cache the Home object for the Product EJB */
  private static ProductHome productHome = null;

  /** Initializes the productHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductHome instance for the default EJB server
   */
  public static ProductHome getProductHome()
  {
    if(productHome == null) //don't want to block here
    {
      synchronized(ProductHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.product.ProductHome");
            productHome = (ProductHome)MyNarrow.narrow(homeObject, ProductHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("product home obtained " + productHome);
        }
      }
    }
    return productHome;
  }




  /** Remove the Product corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    Product product = findByPrimaryKey(primaryKey);
    try
    {
      if(product != null)
      {
        product.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a Product by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The Product corresponding to the primaryKey
   */
  public static Product findByPrimaryKey(java.lang.String primaryKey)
  {
    Product product = null;
    Debug.logInfo("ProductHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      product = (Product)MyNarrow.narrow(getProductHome().findByPrimaryKey(primaryKey), Product.class);
      if(product != null)
      {
        product = product.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return product;
  }

  /** Finds all Product entities
   *@return    Collection containing all Product entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a Product
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  primaryProductCategoryId                  Field of the PRIMARY_PRODUCT_CATEGORY_ID column.
   *@param  manufacturerPartyId                  Field of the MANUFACTURER_PARTY_ID column.
   *@param  uomId                  Field of the UOM_ID column.
   *@param  quantityIncluded                  Field of the QUANTITY_INCLUDED column.
   *@param  introductionDate                  Field of the INTRODUCTION_DATE column.
   *@param  salesDiscontinuationDate                  Field of the SALES_DISCONTINUATION_DATE column.
   *@param  supportDiscontinuationDate                  Field of the SUPPORT_DISCONTINUATION_DATE column.
   *@param  name                  Field of the NAME column.
   *@param  comment                  Field of the COMMENT column.
   *@param  description                  Field of the DESCRIPTION column.
   *@param  longDescription                  Field of the LONG_DESCRIPTION column.
   *@param  smallImageUrl                  Field of the SMALL_IMAGE_URL column.
   *@param  largeImageUrl                  Field of the LARGE_IMAGE_URL column.
   *@param  defaultPrice                  Field of the DEFAULT_PRICE column.
   *@return                Description of the Returned Value
   */
  public static Product create(String productId, String primaryProductCategoryId, String manufacturerPartyId, String uomId, Double quantityIncluded, java.util.Date introductionDate, java.util.Date salesDiscontinuationDate, java.util.Date supportDiscontinuationDate, String name, String comment, String description, String longDescription, String smallImageUrl, String largeImageUrl, Double defaultPrice)
  {
    Product product = null;
    Debug.logInfo("ProductHelper.create: productId: " + productId);
    if(productId == null) { return null; }

    try { product = (Product)MyNarrow.narrow(getProductHome().create(productId, primaryProductCategoryId, manufacturerPartyId, uomId, quantityIncluded, introductionDate, salesDiscontinuationDate, supportDiscontinuationDate, name, comment, description, longDescription, smallImageUrl, largeImageUrl, defaultPrice), Product.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create product with productId: " + productId);
      Debug.logError(ce);
      product = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return product;
  }

  /** Updates the corresponding Product
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  primaryProductCategoryId                  Field of the PRIMARY_PRODUCT_CATEGORY_ID column.
   *@param  manufacturerPartyId                  Field of the MANUFACTURER_PARTY_ID column.
   *@param  uomId                  Field of the UOM_ID column.
   *@param  quantityIncluded                  Field of the QUANTITY_INCLUDED column.
   *@param  introductionDate                  Field of the INTRODUCTION_DATE column.
   *@param  salesDiscontinuationDate                  Field of the SALES_DISCONTINUATION_DATE column.
   *@param  supportDiscontinuationDate                  Field of the SUPPORT_DISCONTINUATION_DATE column.
   *@param  name                  Field of the NAME column.
   *@param  comment                  Field of the COMMENT column.
   *@param  description                  Field of the DESCRIPTION column.
   *@param  longDescription                  Field of the LONG_DESCRIPTION column.
   *@param  smallImageUrl                  Field of the SMALL_IMAGE_URL column.
   *@param  largeImageUrl                  Field of the LARGE_IMAGE_URL column.
   *@param  defaultPrice                  Field of the DEFAULT_PRICE column.
   *@return                Description of the Returned Value
   */
  public static Product update(String productId, String primaryProductCategoryId, String manufacturerPartyId, String uomId, Double quantityIncluded, java.util.Date introductionDate, java.util.Date salesDiscontinuationDate, java.util.Date supportDiscontinuationDate, String name, String comment, String description, String longDescription, String smallImageUrl, String largeImageUrl, Double defaultPrice) throws java.rmi.RemoteException
  {
    if(productId == null) { return null; }
    Product product = findByPrimaryKey(productId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    Product productValue = new ProductValue();

    if(primaryProductCategoryId != null) { productValue.setPrimaryProductCategoryId(primaryProductCategoryId); }
    if(manufacturerPartyId != null) { productValue.setManufacturerPartyId(manufacturerPartyId); }
    if(uomId != null) { productValue.setUomId(uomId); }
    if(quantityIncluded != null) { productValue.setQuantityIncluded(quantityIncluded); }
    if(introductionDate != null) { productValue.setIntroductionDate(introductionDate); }
    if(salesDiscontinuationDate != null) { productValue.setSalesDiscontinuationDate(salesDiscontinuationDate); }
    if(supportDiscontinuationDate != null) { productValue.setSupportDiscontinuationDate(supportDiscontinuationDate); }
    if(name != null) { productValue.setName(name); }
    if(comment != null) { productValue.setComment(comment); }
    if(description != null) { productValue.setDescription(description); }
    if(longDescription != null) { productValue.setLongDescription(longDescription); }
    if(smallImageUrl != null) { productValue.setSmallImageUrl(smallImageUrl); }
    if(largeImageUrl != null) { productValue.setLargeImageUrl(largeImageUrl); }
    if(defaultPrice != null) { productValue.setDefaultPrice(defaultPrice); }

    product.setValueObject(productValue);
    return product;
  }

  /** Removes/deletes the specified  Product
   *@param  primaryProductCategoryId                  Field of the PRIMARY_PRODUCT_CATEGORY_ID column.
   */
  public static void removeByPrimaryProductCategoryId(String primaryProductCategoryId)
  {
    if(primaryProductCategoryId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByPrimaryProductCategoryId(primaryProductCategoryId));

    while(iterator.hasNext())
    {
      try
      {
        Product product = (Product) iterator.next();
        Debug.logInfo("Removing product with primaryProductCategoryId:" + primaryProductCategoryId);
        product.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds Product records by the following parameters:
   *@param  primaryProductCategoryId                  Field of the PRIMARY_PRODUCT_CATEGORY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPrimaryProductCategoryId(String primaryProductCategoryId)
  {
    Debug.logInfo("findByPrimaryProductCategoryId: primaryProductCategoryId:" + primaryProductCategoryId);

    Collection collection = null;
    if(primaryProductCategoryId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductHome().findByPrimaryProductCategoryId(primaryProductCategoryId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  Product
   *@param  manufacturerPartyId                  Field of the MANUFACTURER_PARTY_ID column.
   */
  public static void removeByManufacturerPartyId(String manufacturerPartyId)
  {
    if(manufacturerPartyId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByManufacturerPartyId(manufacturerPartyId));

    while(iterator.hasNext())
    {
      try
      {
        Product product = (Product) iterator.next();
        Debug.logInfo("Removing product with manufacturerPartyId:" + manufacturerPartyId);
        product.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds Product records by the following parameters:
   *@param  manufacturerPartyId                  Field of the MANUFACTURER_PARTY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByManufacturerPartyId(String manufacturerPartyId)
  {
    Debug.logInfo("findByManufacturerPartyId: manufacturerPartyId:" + manufacturerPartyId);

    Collection collection = null;
    if(manufacturerPartyId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductHome().findByManufacturerPartyId(manufacturerPartyId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  Product
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
        Product product = (Product) iterator.next();
        Debug.logInfo("Removing product with uomId:" + uomId);
        product.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds Product records by the following parameters:
   *@param  uomId                  Field of the UOM_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByUomId(String uomId)
  {
    Debug.logInfo("findByUomId: uomId:" + uomId);

    Collection collection = null;
    if(uomId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductHome().findByUomId(uomId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
