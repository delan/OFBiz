
package org.ofbiz.commonapp.product.cost;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Cost Component Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the CostComponent Entity EJB; acts as a proxy for the Home interface
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
public class CostComponentHelper
{

  /** A static variable to cache the Home object for the CostComponent EJB */
  private static CostComponentHome costComponentHome = null;

  /** Initializes the costComponentHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The CostComponentHome instance for the default EJB server
   */
  public static CostComponentHome getCostComponentHome()
  {
    if(costComponentHome == null) //don't want to block here
    {
      synchronized(CostComponentHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(costComponentHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.cost.CostComponentHome");
            costComponentHome = (CostComponentHome)MyNarrow.narrow(homeObject, CostComponentHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("costComponent home obtained " + costComponentHome);
        }
      }
    }
    return costComponentHome;
  }




  /** Remove the CostComponent corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    CostComponent costComponent = findByPrimaryKey(primaryKey);
    try
    {
      if(costComponent != null)
      {
        costComponent.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a CostComponent by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The CostComponent corresponding to the primaryKey
   */
  public static CostComponent findByPrimaryKey(java.lang.String primaryKey)
  {
    CostComponent costComponent = null;
    Debug.logInfo("CostComponentHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      costComponent = (CostComponent)MyNarrow.narrow(getCostComponentHome().findByPrimaryKey(primaryKey), CostComponent.class);
      if(costComponent != null)
      {
        costComponent = costComponent.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return costComponent;
  }

  /** Finds all CostComponent entities
   *@return    Collection containing all CostComponent entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("CostComponentHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getCostComponentHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a CostComponent
   *@param  costComponentId                  Field of the COST_COMPONENT_ID column.
   *@param  costComponentTypeId                  Field of the COST_COMPONENT_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  geoId                  Field of the GEO_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  cost                  Field of the COST column.
   *@return                Description of the Returned Value
   */
  public static CostComponent create(String costComponentId, String costComponentTypeId, String productId, String productFeatureId, String partyId, String geoId, java.util.Date fromDate, java.util.Date thruDate, Double cost)
  {
    CostComponent costComponent = null;
    Debug.logInfo("CostComponentHelper.create: costComponentId: " + costComponentId);
    if(costComponentId == null) { return null; }

    try { costComponent = (CostComponent)MyNarrow.narrow(getCostComponentHome().create(costComponentId, costComponentTypeId, productId, productFeatureId, partyId, geoId, fromDate, thruDate, cost), CostComponent.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create costComponent with costComponentId: " + costComponentId);
      Debug.logError(ce);
      costComponent = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return costComponent;
  }

  /** Updates the corresponding CostComponent
   *@param  costComponentId                  Field of the COST_COMPONENT_ID column.
   *@param  costComponentTypeId                  Field of the COST_COMPONENT_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  geoId                  Field of the GEO_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  cost                  Field of the COST column.
   *@return                Description of the Returned Value
   */
  public static CostComponent update(String costComponentId, String costComponentTypeId, String productId, String productFeatureId, String partyId, String geoId, java.util.Date fromDate, java.util.Date thruDate, Double cost) throws java.rmi.RemoteException
  {
    if(costComponentId == null) { return null; }
    CostComponent costComponent = findByPrimaryKey(costComponentId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    CostComponent costComponentValue = new CostComponentValue();

    if(costComponentTypeId != null) { costComponentValue.setCostComponentTypeId(costComponentTypeId); }
    if(productId != null) { costComponentValue.setProductId(productId); }
    if(productFeatureId != null) { costComponentValue.setProductFeatureId(productFeatureId); }
    if(partyId != null) { costComponentValue.setPartyId(partyId); }
    if(geoId != null) { costComponentValue.setGeoId(geoId); }
    if(fromDate != null) { costComponentValue.setFromDate(fromDate); }
    if(thruDate != null) { costComponentValue.setThruDate(thruDate); }
    if(cost != null) { costComponentValue.setCost(cost); }

    costComponent.setValueObject(costComponentValue);
    return costComponent;
  }

  /** Removes/deletes the specified  CostComponent
   *@param  costComponentTypeId                  Field of the COST_COMPONENT_TYPE_ID column.
   */
  public static void removeByCostComponentTypeId(String costComponentTypeId)
  {
    if(costComponentTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByCostComponentTypeId(costComponentTypeId));

    while(iterator.hasNext())
    {
      try
      {
        CostComponent costComponent = (CostComponent) iterator.next();
        Debug.logInfo("Removing costComponent with costComponentTypeId:" + costComponentTypeId);
        costComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds CostComponent records by the following parameters:
   *@param  costComponentTypeId                  Field of the COST_COMPONENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByCostComponentTypeId(String costComponentTypeId)
  {
    Debug.logInfo("findByCostComponentTypeId: costComponentTypeId:" + costComponentTypeId);

    Collection collection = null;
    if(costComponentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getCostComponentHome().findByCostComponentTypeId(costComponentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  CostComponent
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
        CostComponent costComponent = (CostComponent) iterator.next();
        Debug.logInfo("Removing costComponent with productId:" + productId);
        costComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds CostComponent records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductId(String productId)
  {
    Debug.logInfo("findByProductId: productId:" + productId);

    Collection collection = null;
    if(productId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getCostComponentHome().findByProductId(productId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  CostComponent
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
        CostComponent costComponent = (CostComponent) iterator.next();
        Debug.logInfo("Removing costComponent with productFeatureId:" + productFeatureId);
        costComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds CostComponent records by the following parameters:
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductFeatureId(String productFeatureId)
  {
    Debug.logInfo("findByProductFeatureId: productFeatureId:" + productFeatureId);

    Collection collection = null;
    if(productFeatureId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getCostComponentHome().findByProductFeatureId(productFeatureId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  CostComponent
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
        CostComponent costComponent = (CostComponent) iterator.next();
        Debug.logInfo("Removing costComponent with productId, productFeatureId:" + productId + ", " + productFeatureId);
        costComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds CostComponent records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductIdAndProductFeatureId(String productId, String productFeatureId)
  {
    Debug.logInfo("findByProductIdAndProductFeatureId: productId, productFeatureId:" + productId + ", " + productFeatureId);

    Collection collection = null;
    if(productId == null || productFeatureId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getCostComponentHome().findByProductIdAndProductFeatureId(productId, productFeatureId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  CostComponent
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
        CostComponent costComponent = (CostComponent) iterator.next();
        Debug.logInfo("Removing costComponent with partyId:" + partyId);
        costComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds CostComponent records by the following parameters:
   *@param  partyId                  Field of the PARTY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyId(String partyId)
  {
    Debug.logInfo("findByPartyId: partyId:" + partyId);

    Collection collection = null;
    if(partyId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getCostComponentHome().findByPartyId(partyId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  CostComponent
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
        CostComponent costComponent = (CostComponent) iterator.next();
        Debug.logInfo("Removing costComponent with geoId:" + geoId);
        costComponent.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds CostComponent records by the following parameters:
   *@param  geoId                  Field of the GEO_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByGeoId(String geoId)
  {
    Debug.logInfo("findByGeoId: geoId:" + geoId);

    Collection collection = null;
    if(geoId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getCostComponentHome().findByGeoId(geoId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
