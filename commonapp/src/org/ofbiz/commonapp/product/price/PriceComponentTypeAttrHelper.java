
package org.ofbiz.commonapp.product.price;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Price Component Type Attribute Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the PriceComponentTypeAttr Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:30 MDT 2001
 *@version    1.0
 */
public class PriceComponentTypeAttrHelper
{

  /** A static variable to cache the Home object for the PriceComponentTypeAttr EJB */
  private static PriceComponentTypeAttrHome priceComponentTypeAttrHome = null;

  /** Initializes the priceComponentTypeAttrHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The PriceComponentTypeAttrHome instance for the default EJB server
   */
  public static PriceComponentTypeAttrHome getPriceComponentTypeAttrHome()
  {
    if(priceComponentTypeAttrHome == null) //don't want to block here
    {
      synchronized(PriceComponentTypeAttrHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(priceComponentTypeAttrHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.price.PriceComponentTypeAttrHome");
            priceComponentTypeAttrHome = (PriceComponentTypeAttrHome)MyNarrow.narrow(homeObject, PriceComponentTypeAttrHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("priceComponentTypeAttr home obtained " + priceComponentTypeAttrHome);
        }
      }
    }
    return priceComponentTypeAttrHome;
  }



  /** Remove the PriceComponentTypeAttr corresponding to the primaryKey specified by fields
   *@param  priceComponentTypeId                  Field of the PRICE_COMPONENT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   */
  public static void removeByPrimaryKey(String priceComponentTypeId, String name)
  {
    if(priceComponentTypeId == null || name == null)
    {
      return;
    }
    PriceComponentTypeAttrPK primaryKey = new PriceComponentTypeAttrPK(priceComponentTypeId, name);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the PriceComponentTypeAttr corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.price.PriceComponentTypeAttrPK primaryKey)
  {
    if(primaryKey == null) return;
    PriceComponentTypeAttr priceComponentTypeAttr = findByPrimaryKey(primaryKey);
    try
    {
      if(priceComponentTypeAttr != null)
      {
        priceComponentTypeAttr.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a PriceComponentTypeAttr by its Primary Key, specified by individual fields
   *@param  priceComponentTypeId                  Field of the PRICE_COMPONENT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return       The PriceComponentTypeAttr corresponding to the primaryKey
   */
  public static PriceComponentTypeAttr findByPrimaryKey(String priceComponentTypeId, String name)
  {
    if(priceComponentTypeId == null || name == null) return null;
    PriceComponentTypeAttrPK primaryKey = new PriceComponentTypeAttrPK(priceComponentTypeId, name);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a PriceComponentTypeAttr by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The PriceComponentTypeAttr corresponding to the primaryKey
   */
  public static PriceComponentTypeAttr findByPrimaryKey(org.ofbiz.commonapp.product.price.PriceComponentTypeAttrPK primaryKey)
  {
    PriceComponentTypeAttr priceComponentTypeAttr = null;
    Debug.logInfo("PriceComponentTypeAttrHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      priceComponentTypeAttr = (PriceComponentTypeAttr)MyNarrow.narrow(getPriceComponentTypeAttrHome().findByPrimaryKey(primaryKey), PriceComponentTypeAttr.class);
      if(priceComponentTypeAttr != null)
      {
        priceComponentTypeAttr = priceComponentTypeAttr.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return priceComponentTypeAttr;
  }

  /** Finds all PriceComponentTypeAttr entities
   *@return    Collection containing all PriceComponentTypeAttr entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("PriceComponentTypeAttrHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getPriceComponentTypeAttrHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a PriceComponentTypeAttr
   *@param  priceComponentTypeId                  Field of the PRICE_COMPONENT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static PriceComponentTypeAttr create(String priceComponentTypeId, String name)
  {
    PriceComponentTypeAttr priceComponentTypeAttr = null;
    Debug.logInfo("PriceComponentTypeAttrHelper.create: priceComponentTypeId, name: " + priceComponentTypeId + ", " + name);
    if(priceComponentTypeId == null || name == null) { return null; }

    try { priceComponentTypeAttr = (PriceComponentTypeAttr)MyNarrow.narrow(getPriceComponentTypeAttrHome().create(priceComponentTypeId, name), PriceComponentTypeAttr.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create priceComponentTypeAttr with priceComponentTypeId, name: " + priceComponentTypeId + ", " + name);
      Debug.logError(ce);
      priceComponentTypeAttr = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return priceComponentTypeAttr;
  }

  /** Updates the corresponding PriceComponentTypeAttr
   *@param  priceComponentTypeId                  Field of the PRICE_COMPONENT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static PriceComponentTypeAttr update(String priceComponentTypeId, String name) throws java.rmi.RemoteException
  {
    if(priceComponentTypeId == null || name == null) { return null; }
    PriceComponentTypeAttr priceComponentTypeAttr = findByPrimaryKey(priceComponentTypeId, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PriceComponentTypeAttr priceComponentTypeAttrValue = new PriceComponentTypeAttrValue();


    priceComponentTypeAttr.setValueObject(priceComponentTypeAttrValue);
    return priceComponentTypeAttr;
  }

  /** Removes/deletes the specified  PriceComponentTypeAttr
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
        PriceComponentTypeAttr priceComponentTypeAttr = (PriceComponentTypeAttr) iterator.next();
        Debug.logInfo("Removing priceComponentTypeAttr with priceComponentTypeId:" + priceComponentTypeId);
        priceComponentTypeAttr.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponentTypeAttr records by the following parameters:
   *@param  priceComponentTypeId                  Field of the PRICE_COMPONENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPriceComponentTypeId(String priceComponentTypeId)
  {
    Debug.logInfo("findByPriceComponentTypeId: priceComponentTypeId:" + priceComponentTypeId);

    Collection collection = null;
    if(priceComponentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentTypeAttrHome().findByPriceComponentTypeId(priceComponentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponentTypeAttr
   *@param  name                  Field of the NAME column.
   */
  public static void removeByName(String name)
  {
    if(name == null) return;
    Iterator iterator = UtilMisc.toIterator(findByName(name));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponentTypeAttr priceComponentTypeAttr = (PriceComponentTypeAttr) iterator.next();
        Debug.logInfo("Removing priceComponentTypeAttr with name:" + name);
        priceComponentTypeAttr.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponentTypeAttr records by the following parameters:
   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByName(String name)
  {
    Debug.logInfo("findByName: name:" + name);

    Collection collection = null;
    if(name == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentTypeAttrHome().findByName(name), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
