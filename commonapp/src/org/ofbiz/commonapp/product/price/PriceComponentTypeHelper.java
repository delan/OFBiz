
package org.ofbiz.commonapp.product.price;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Price Component Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the PriceComponentType Entity EJB; acts as a proxy for the Home interface
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
public class PriceComponentTypeHelper
{

  /** A static variable to cache the Home object for the PriceComponentType EJB */
  private static PriceComponentTypeHome priceComponentTypeHome = null;

  /** Initializes the priceComponentTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The PriceComponentTypeHome instance for the default EJB server
   */
  public static PriceComponentTypeHome getPriceComponentTypeHome()
  {
    if(priceComponentTypeHome == null) //don't want to block here
    {
      synchronized(PriceComponentTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(priceComponentTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.price.PriceComponentTypeHome");
            priceComponentTypeHome = (PriceComponentTypeHome)MyNarrow.narrow(homeObject, PriceComponentTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("priceComponentType home obtained " + priceComponentTypeHome);
        }
      }
    }
    return priceComponentTypeHome;
  }




  /** Remove the PriceComponentType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    PriceComponentType priceComponentType = findByPrimaryKey(primaryKey);
    try
    {
      if(priceComponentType != null)
      {
        priceComponentType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a PriceComponentType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The PriceComponentType corresponding to the primaryKey
   */
  public static PriceComponentType findByPrimaryKey(java.lang.String primaryKey)
  {
    PriceComponentType priceComponentType = null;
    Debug.logInfo("PriceComponentTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      priceComponentType = (PriceComponentType)MyNarrow.narrow(getPriceComponentTypeHome().findByPrimaryKey(primaryKey), PriceComponentType.class);
      if(priceComponentType != null)
      {
        priceComponentType = priceComponentType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return priceComponentType;
  }

  /** Finds all PriceComponentType entities
   *@return    Collection containing all PriceComponentType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("PriceComponentTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getPriceComponentTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a PriceComponentType
   *@param  priceComponentTypeId                  Field of the PRICE_COMPONENT_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static PriceComponentType create(String priceComponentTypeId, String parentTypeId, String hasTable, String description)
  {
    PriceComponentType priceComponentType = null;
    Debug.logInfo("PriceComponentTypeHelper.create: priceComponentTypeId: " + priceComponentTypeId);
    if(priceComponentTypeId == null) { return null; }

    try { priceComponentType = (PriceComponentType)MyNarrow.narrow(getPriceComponentTypeHome().create(priceComponentTypeId, parentTypeId, hasTable, description), PriceComponentType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create priceComponentType with priceComponentTypeId: " + priceComponentTypeId);
      Debug.logError(ce);
      priceComponentType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return priceComponentType;
  }

  /** Updates the corresponding PriceComponentType
   *@param  priceComponentTypeId                  Field of the PRICE_COMPONENT_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static PriceComponentType update(String priceComponentTypeId, String parentTypeId, String hasTable, String description) throws java.rmi.RemoteException
  {
    if(priceComponentTypeId == null) { return null; }
    PriceComponentType priceComponentType = findByPrimaryKey(priceComponentTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PriceComponentType priceComponentTypeValue = new PriceComponentTypeValue();

    if(parentTypeId != null) { priceComponentTypeValue.setParentTypeId(parentTypeId); }
    if(hasTable != null) { priceComponentTypeValue.setHasTable(hasTable); }
    if(description != null) { priceComponentTypeValue.setDescription(description); }

    priceComponentType.setValueObject(priceComponentTypeValue);
    return priceComponentType;
  }

  /** Removes/deletes the specified  PriceComponentType
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   */
  public static void removeByParentTypeId(String parentTypeId)
  {
    if(parentTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByParentTypeId(parentTypeId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponentType priceComponentType = (PriceComponentType) iterator.next();
        Debug.logInfo("Removing priceComponentType with parentTypeId:" + parentTypeId);
        priceComponentType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponentType records by the following parameters:
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByParentTypeId(String parentTypeId)
  {
    Debug.logInfo("findByParentTypeId: parentTypeId:" + parentTypeId);

    Collection collection = null;
    if(parentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentTypeHome().findByParentTypeId(parentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponentType
   *@param  hasTable                  Field of the HAS_TABLE column.
   */
  public static void removeByHasTable(String hasTable)
  {
    if(hasTable == null) return;
    Iterator iterator = UtilMisc.toIterator(findByHasTable(hasTable));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponentType priceComponentType = (PriceComponentType) iterator.next();
        Debug.logInfo("Removing priceComponentType with hasTable:" + hasTable);
        priceComponentType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponentType records by the following parameters:
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByHasTable(String hasTable)
  {
    Debug.logInfo("findByHasTable: hasTable:" + hasTable);

    Collection collection = null;
    if(hasTable == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentTypeHome().findByHasTable(hasTable), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
