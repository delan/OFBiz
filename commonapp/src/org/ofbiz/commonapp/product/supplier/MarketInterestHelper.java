
package org.ofbiz.commonapp.product.supplier;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Market Interest Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the MarketInterest Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:34 MDT 2001
 *@version    1.0
 */
public class MarketInterestHelper
{

  /** A static variable to cache the Home object for the MarketInterest EJB */
  private static MarketInterestHome marketInterestHome = null;

  /** Initializes the marketInterestHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The MarketInterestHome instance for the default EJB server
   */
  public static MarketInterestHome getMarketInterestHome()
  {
    if(marketInterestHome == null) //don't want to block here
    {
      synchronized(MarketInterestHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(marketInterestHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.supplier.MarketInterestHome");
            marketInterestHome = (MarketInterestHome)MyNarrow.narrow(homeObject, MarketInterestHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("marketInterest home obtained " + marketInterestHome);
        }
      }
    }
    return marketInterestHome;
  }



  /** Remove the MarketInterest corresponding to the primaryKey specified by fields
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   */
  public static void removeByPrimaryKey(String productCategoryId, String partyTypeId)
  {
    if(productCategoryId == null || partyTypeId == null)
    {
      return;
    }
    MarketInterestPK primaryKey = new MarketInterestPK(productCategoryId, partyTypeId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the MarketInterest corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.supplier.MarketInterestPK primaryKey)
  {
    if(primaryKey == null) return;
    MarketInterest marketInterest = findByPrimaryKey(primaryKey);
    try
    {
      if(marketInterest != null)
      {
        marketInterest.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a MarketInterest by its Primary Key, specified by individual fields
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@return       The MarketInterest corresponding to the primaryKey
   */
  public static MarketInterest findByPrimaryKey(String productCategoryId, String partyTypeId)
  {
    if(productCategoryId == null || partyTypeId == null) return null;
    MarketInterestPK primaryKey = new MarketInterestPK(productCategoryId, partyTypeId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a MarketInterest by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The MarketInterest corresponding to the primaryKey
   */
  public static MarketInterest findByPrimaryKey(org.ofbiz.commonapp.product.supplier.MarketInterestPK primaryKey)
  {
    MarketInterest marketInterest = null;
    Debug.logInfo("MarketInterestHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      marketInterest = (MarketInterest)MyNarrow.narrow(getMarketInterestHome().findByPrimaryKey(primaryKey), MarketInterest.class);
      if(marketInterest != null)
      {
        marketInterest = marketInterest.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return marketInterest;
  }

  /** Finds all MarketInterest entities
   *@return    Collection containing all MarketInterest entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("MarketInterestHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getMarketInterestHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a MarketInterest
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@return                Description of the Returned Value
   */
  public static MarketInterest create(String productCategoryId, String partyTypeId, java.util.Date fromDate, java.util.Date thruDate)
  {
    MarketInterest marketInterest = null;
    Debug.logInfo("MarketInterestHelper.create: productCategoryId, partyTypeId: " + productCategoryId + ", " + partyTypeId);
    if(productCategoryId == null || partyTypeId == null) { return null; }

    try { marketInterest = (MarketInterest)MyNarrow.narrow(getMarketInterestHome().create(productCategoryId, partyTypeId, fromDate, thruDate), MarketInterest.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create marketInterest with productCategoryId, partyTypeId: " + productCategoryId + ", " + partyTypeId);
      Debug.logError(ce);
      marketInterest = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return marketInterest;
  }

  /** Updates the corresponding MarketInterest
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@return                Description of the Returned Value
   */
  public static MarketInterest update(String productCategoryId, String partyTypeId, java.util.Date fromDate, java.util.Date thruDate) throws java.rmi.RemoteException
  {
    if(productCategoryId == null || partyTypeId == null) { return null; }
    MarketInterest marketInterest = findByPrimaryKey(productCategoryId, partyTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    MarketInterest marketInterestValue = new MarketInterestValue();

    if(fromDate != null) { marketInterestValue.setFromDate(fromDate); }
    if(thruDate != null) { marketInterestValue.setThruDate(thruDate); }

    marketInterest.setValueObject(marketInterestValue);
    return marketInterest;
  }

  /** Removes/deletes the specified  MarketInterest
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
        MarketInterest marketInterest = (MarketInterest) iterator.next();
        Debug.logInfo("Removing marketInterest with productCategoryId:" + productCategoryId);
        marketInterest.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds MarketInterest records by the following parameters:
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductCategoryId(String productCategoryId)
  {
    Debug.logInfo("findByProductCategoryId: productCategoryId:" + productCategoryId);

    Collection collection = null;
    if(productCategoryId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getMarketInterestHome().findByProductCategoryId(productCategoryId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  MarketInterest
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
        MarketInterest marketInterest = (MarketInterest) iterator.next();
        Debug.logInfo("Removing marketInterest with partyTypeId:" + partyTypeId);
        marketInterest.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds MarketInterest records by the following parameters:
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyTypeId(String partyTypeId)
  {
    Debug.logInfo("findByPartyTypeId: partyTypeId:" + partyTypeId);

    Collection collection = null;
    if(partyTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getMarketInterestHome().findByPartyTypeId(partyTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
