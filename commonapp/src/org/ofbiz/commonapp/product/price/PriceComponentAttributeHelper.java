
package org.ofbiz.commonapp.product.price;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Price Component Attribute Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the PriceComponentAttribute Entity EJB; acts as a proxy for the Home interface
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
public class PriceComponentAttributeHelper
{

  /** A static variable to cache the Home object for the PriceComponentAttribute EJB */
  private static PriceComponentAttributeHome priceComponentAttributeHome = null;

  /** Initializes the priceComponentAttributeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The PriceComponentAttributeHome instance for the default EJB server
   */
  public static PriceComponentAttributeHome getPriceComponentAttributeHome()
  {
    if(priceComponentAttributeHome == null) //don't want to block here
    {
      synchronized(PriceComponentAttributeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(priceComponentAttributeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.price.PriceComponentAttributeHome");
            priceComponentAttributeHome = (PriceComponentAttributeHome)MyNarrow.narrow(homeObject, PriceComponentAttributeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("priceComponentAttribute home obtained " + priceComponentAttributeHome);
        }
      }
    }
    return priceComponentAttributeHome;
  }



  /** Remove the PriceComponentAttribute corresponding to the primaryKey specified by fields
   *@param  priceComponentId                  Field of the PRICE_COMPONENT_ID column.
   *@param  name                  Field of the NAME column.
   */
  public static void removeByPrimaryKey(String priceComponentId, String name)
  {
    if(priceComponentId == null || name == null)
    {
      return;
    }
    PriceComponentAttributePK primaryKey = new PriceComponentAttributePK(priceComponentId, name);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the PriceComponentAttribute corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.price.PriceComponentAttributePK primaryKey)
  {
    if(primaryKey == null) return;
    PriceComponentAttribute priceComponentAttribute = findByPrimaryKey(primaryKey);
    try
    {
      if(priceComponentAttribute != null)
      {
        priceComponentAttribute.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a PriceComponentAttribute by its Primary Key, specified by individual fields
   *@param  priceComponentId                  Field of the PRICE_COMPONENT_ID column.
   *@param  name                  Field of the NAME column.
   *@return       The PriceComponentAttribute corresponding to the primaryKey
   */
  public static PriceComponentAttribute findByPrimaryKey(String priceComponentId, String name)
  {
    if(priceComponentId == null || name == null) return null;
    PriceComponentAttributePK primaryKey = new PriceComponentAttributePK(priceComponentId, name);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a PriceComponentAttribute by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The PriceComponentAttribute corresponding to the primaryKey
   */
  public static PriceComponentAttribute findByPrimaryKey(org.ofbiz.commonapp.product.price.PriceComponentAttributePK primaryKey)
  {
    PriceComponentAttribute priceComponentAttribute = null;
    Debug.logInfo("PriceComponentAttributeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      priceComponentAttribute = (PriceComponentAttribute)MyNarrow.narrow(getPriceComponentAttributeHome().findByPrimaryKey(primaryKey), PriceComponentAttribute.class);
      if(priceComponentAttribute != null)
      {
        priceComponentAttribute = priceComponentAttribute.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return priceComponentAttribute;
  }

  /** Finds all PriceComponentAttribute entities
   *@return    Collection containing all PriceComponentAttribute entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("PriceComponentAttributeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getPriceComponentAttributeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a PriceComponentAttribute
   *@param  priceComponentId                  Field of the PRICE_COMPONENT_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                Description of the Returned Value
   */
  public static PriceComponentAttribute create(String priceComponentId, String name, String value)
  {
    PriceComponentAttribute priceComponentAttribute = null;
    Debug.logInfo("PriceComponentAttributeHelper.create: priceComponentId, name: " + priceComponentId + ", " + name);
    if(priceComponentId == null || name == null) { return null; }

    try { priceComponentAttribute = (PriceComponentAttribute)MyNarrow.narrow(getPriceComponentAttributeHome().create(priceComponentId, name, value), PriceComponentAttribute.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create priceComponentAttribute with priceComponentId, name: " + priceComponentId + ", " + name);
      Debug.logError(ce);
      priceComponentAttribute = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return priceComponentAttribute;
  }

  /** Updates the corresponding PriceComponentAttribute
   *@param  priceComponentId                  Field of the PRICE_COMPONENT_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                Description of the Returned Value
   */
  public static PriceComponentAttribute update(String priceComponentId, String name, String value) throws java.rmi.RemoteException
  {
    if(priceComponentId == null || name == null) { return null; }
    PriceComponentAttribute priceComponentAttribute = findByPrimaryKey(priceComponentId, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PriceComponentAttribute priceComponentAttributeValue = new PriceComponentAttributeValue();

    if(value != null) { priceComponentAttributeValue.setValue(value); }

    priceComponentAttribute.setValueObject(priceComponentAttributeValue);
    return priceComponentAttribute;
  }

  /** Removes/deletes the specified  PriceComponentAttribute
   *@param  priceComponentId                  Field of the PRICE_COMPONENT_ID column.
   */
  public static void removeByPriceComponentId(String priceComponentId)
  {
    if(priceComponentId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByPriceComponentId(priceComponentId));

    while(iterator.hasNext())
    {
      try
      {
        PriceComponentAttribute priceComponentAttribute = (PriceComponentAttribute) iterator.next();
        Debug.logInfo("Removing priceComponentAttribute with priceComponentId:" + priceComponentId);
        priceComponentAttribute.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponentAttribute records by the following parameters:
   *@param  priceComponentId                  Field of the PRICE_COMPONENT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPriceComponentId(String priceComponentId)
  {
    Debug.logInfo("findByPriceComponentId: priceComponentId:" + priceComponentId);

    Collection collection = null;
    if(priceComponentId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentAttributeHome().findByPriceComponentId(priceComponentId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PriceComponentAttribute
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
        PriceComponentAttribute priceComponentAttribute = (PriceComponentAttribute) iterator.next();
        Debug.logInfo("Removing priceComponentAttribute with name:" + name);
        priceComponentAttribute.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PriceComponentAttribute records by the following parameters:
   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByName(String name)
  {
    Debug.logInfo("findByName: name:" + name);

    Collection collection = null;
    if(name == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPriceComponentAttributeHome().findByName(name), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
