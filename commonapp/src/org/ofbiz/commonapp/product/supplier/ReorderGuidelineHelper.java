
package org.ofbiz.commonapp.product.supplier;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Reorder Guideline Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ReorderGuideline Entity EJB; acts as a proxy for the Home interface
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
public class ReorderGuidelineHelper
{

  /** A static variable to cache the Home object for the ReorderGuideline EJB */
  private static ReorderGuidelineHome reorderGuidelineHome = null;

  /** Initializes the reorderGuidelineHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ReorderGuidelineHome instance for the default EJB server
   */
  public static ReorderGuidelineHome getReorderGuidelineHome()
  {
    if(reorderGuidelineHome == null) //don't want to block here
    {
      synchronized(ReorderGuidelineHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(reorderGuidelineHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.supplier.ReorderGuidelineHome");
            reorderGuidelineHome = (ReorderGuidelineHome)MyNarrow.narrow(homeObject, ReorderGuidelineHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("reorderGuideline home obtained " + reorderGuidelineHome);
        }
      }
    }
    return reorderGuidelineHome;
  }




  /** Remove the ReorderGuideline corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    ReorderGuideline reorderGuideline = findByPrimaryKey(primaryKey);
    try
    {
      if(reorderGuideline != null)
      {
        reorderGuideline.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a ReorderGuideline by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ReorderGuideline corresponding to the primaryKey
   */
  public static ReorderGuideline findByPrimaryKey(java.lang.String primaryKey)
  {
    ReorderGuideline reorderGuideline = null;
    Debug.logInfo("ReorderGuidelineHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      reorderGuideline = (ReorderGuideline)MyNarrow.narrow(getReorderGuidelineHome().findByPrimaryKey(primaryKey), ReorderGuideline.class);
      if(reorderGuideline != null)
      {
        reorderGuideline = reorderGuideline.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return reorderGuideline;
  }

  /** Finds all ReorderGuideline entities
   *@return    Collection containing all ReorderGuideline entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ReorderGuidelineHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getReorderGuidelineHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ReorderGuideline
   *@param  reorderGuidelineId                  Field of the REORDER_GUIDELINE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  geoId                  Field of the GEO_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  reorderQuantity                  Field of the REORDER_QUANTITY column.
   *@param  reorderLevel                  Field of the REORDER_LEVEL column.
   *@return                Description of the Returned Value
   */
  public static ReorderGuideline create(String reorderGuidelineId, String productId, String partyId, String roleTypeId, String facilityId, String geoId, java.util.Date fromDate, java.util.Date thruDate, Double reorderQuantity, Double reorderLevel)
  {
    ReorderGuideline reorderGuideline = null;
    Debug.logInfo("ReorderGuidelineHelper.create: reorderGuidelineId: " + reorderGuidelineId);
    if(reorderGuidelineId == null) { return null; }

    try { reorderGuideline = (ReorderGuideline)MyNarrow.narrow(getReorderGuidelineHome().create(reorderGuidelineId, productId, partyId, roleTypeId, facilityId, geoId, fromDate, thruDate, reorderQuantity, reorderLevel), ReorderGuideline.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create reorderGuideline with reorderGuidelineId: " + reorderGuidelineId);
      Debug.logError(ce);
      reorderGuideline = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return reorderGuideline;
  }

  /** Updates the corresponding ReorderGuideline
   *@param  reorderGuidelineId                  Field of the REORDER_GUIDELINE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  geoId                  Field of the GEO_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  reorderQuantity                  Field of the REORDER_QUANTITY column.
   *@param  reorderLevel                  Field of the REORDER_LEVEL column.
   *@return                Description of the Returned Value
   */
  public static ReorderGuideline update(String reorderGuidelineId, String productId, String partyId, String roleTypeId, String facilityId, String geoId, java.util.Date fromDate, java.util.Date thruDate, Double reorderQuantity, Double reorderLevel) throws java.rmi.RemoteException
  {
    if(reorderGuidelineId == null) { return null; }
    ReorderGuideline reorderGuideline = findByPrimaryKey(reorderGuidelineId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ReorderGuideline reorderGuidelineValue = new ReorderGuidelineValue();

    if(productId != null) { reorderGuidelineValue.setProductId(productId); }
    if(partyId != null) { reorderGuidelineValue.setPartyId(partyId); }
    if(roleTypeId != null) { reorderGuidelineValue.setRoleTypeId(roleTypeId); }
    if(facilityId != null) { reorderGuidelineValue.setFacilityId(facilityId); }
    if(geoId != null) { reorderGuidelineValue.setGeoId(geoId); }
    if(fromDate != null) { reorderGuidelineValue.setFromDate(fromDate); }
    if(thruDate != null) { reorderGuidelineValue.setThruDate(thruDate); }
    if(reorderQuantity != null) { reorderGuidelineValue.setReorderQuantity(reorderQuantity); }
    if(reorderLevel != null) { reorderGuidelineValue.setReorderLevel(reorderLevel); }

    reorderGuideline.setValueObject(reorderGuidelineValue);
    return reorderGuideline;
  }

  /** Removes/deletes the specified  ReorderGuideline
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
        ReorderGuideline reorderGuideline = (ReorderGuideline) iterator.next();
        Debug.logInfo("Removing reorderGuideline with productId:" + productId);
        reorderGuideline.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ReorderGuideline records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductId(String productId)
  {
    Debug.logInfo("findByProductId: productId:" + productId);

    Collection collection = null;
    if(productId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getReorderGuidelineHome().findByProductId(productId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ReorderGuideline
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
        ReorderGuideline reorderGuideline = (ReorderGuideline) iterator.next();
        Debug.logInfo("Removing reorderGuideline with partyId:" + partyId);
        reorderGuideline.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ReorderGuideline records by the following parameters:
   *@param  partyId                  Field of the PARTY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyId(String partyId)
  {
    Debug.logInfo("findByPartyId: partyId:" + partyId);

    Collection collection = null;
    if(partyId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getReorderGuidelineHome().findByPartyId(partyId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ReorderGuideline
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   */
  public static void removeByRoleTypeId(String roleTypeId)
  {
    if(roleTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByRoleTypeId(roleTypeId));

    while(iterator.hasNext())
    {
      try
      {
        ReorderGuideline reorderGuideline = (ReorderGuideline) iterator.next();
        Debug.logInfo("Removing reorderGuideline with roleTypeId:" + roleTypeId);
        reorderGuideline.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ReorderGuideline records by the following parameters:
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByRoleTypeId(String roleTypeId)
  {
    Debug.logInfo("findByRoleTypeId: roleTypeId:" + roleTypeId);

    Collection collection = null;
    if(roleTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getReorderGuidelineHome().findByRoleTypeId(roleTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ReorderGuideline
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   */
  public static void removeByPartyIdAndRoleTypeId(String partyId, String roleTypeId)
  {
    if(partyId == null || roleTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByPartyIdAndRoleTypeId(partyId, roleTypeId));

    while(iterator.hasNext())
    {
      try
      {
        ReorderGuideline reorderGuideline = (ReorderGuideline) iterator.next();
        Debug.logInfo("Removing reorderGuideline with partyId, roleTypeId:" + partyId + ", " + roleTypeId);
        reorderGuideline.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ReorderGuideline records by the following parameters:
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyIdAndRoleTypeId(String partyId, String roleTypeId)
  {
    Debug.logInfo("findByPartyIdAndRoleTypeId: partyId, roleTypeId:" + partyId + ", " + roleTypeId);

    Collection collection = null;
    if(partyId == null || roleTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getReorderGuidelineHome().findByPartyIdAndRoleTypeId(partyId, roleTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ReorderGuideline
   *@param  facilityId                  Field of the FACILITY_ID column.
   */
  public static void removeByFacilityId(String facilityId)
  {
    if(facilityId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByFacilityId(facilityId));

    while(iterator.hasNext())
    {
      try
      {
        ReorderGuideline reorderGuideline = (ReorderGuideline) iterator.next();
        Debug.logInfo("Removing reorderGuideline with facilityId:" + facilityId);
        reorderGuideline.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ReorderGuideline records by the following parameters:
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByFacilityId(String facilityId)
  {
    Debug.logInfo("findByFacilityId: facilityId:" + facilityId);

    Collection collection = null;
    if(facilityId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getReorderGuidelineHome().findByFacilityId(facilityId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ReorderGuideline
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
        ReorderGuideline reorderGuideline = (ReorderGuideline) iterator.next();
        Debug.logInfo("Removing reorderGuideline with geoId:" + geoId);
        reorderGuideline.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ReorderGuideline records by the following parameters:
   *@param  geoId                  Field of the GEO_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByGeoId(String geoId)
  {
    Debug.logInfo("findByGeoId: geoId:" + geoId);

    Collection collection = null;
    if(geoId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getReorderGuidelineHome().findByGeoId(geoId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
