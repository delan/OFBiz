
package org.ofbiz.commonapp.product.cost;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Cost Component Attribute Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the CostComponentAttribute Entity EJB; acts as a proxy for the Home interface
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
public class CostComponentAttributeHelper
{

  /** A static variable to cache the Home object for the CostComponentAttribute EJB */
  private static CostComponentAttributeHome costComponentAttributeHome = null;

  /** Initializes the costComponentAttributeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The CostComponentAttributeHome instance for the default EJB server
   */
  public static CostComponentAttributeHome getCostComponentAttributeHome()
  {
    if(costComponentAttributeHome == null) //don't want to block here
    {
      synchronized(CostComponentAttributeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(costComponentAttributeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.cost.CostComponentAttributeHome");
            costComponentAttributeHome = (CostComponentAttributeHome)MyNarrow.narrow(homeObject, CostComponentAttributeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("costComponentAttribute home obtained " + costComponentAttributeHome);
        }
      }
    }
    return costComponentAttributeHome;
  }



  /** Remove the CostComponentAttribute corresponding to the primaryKey specified by fields
   *@param  costComponentId                  Field of the COST_COMPONENT_ID column.
   *@param  name                  Field of the NAME column.
   */
  public static void removeByPrimaryKey(String costComponentId, String name)
  {
    if(costComponentId == null || name == null)
    {
      return;
    }
    CostComponentAttributePK primaryKey = new CostComponentAttributePK(costComponentId, name);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the CostComponentAttribute corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.cost.CostComponentAttributePK primaryKey)
  {
    if(primaryKey == null) return;
    CostComponentAttribute costComponentAttribute = findByPrimaryKey(primaryKey);
    try
    {
      if(costComponentAttribute != null)
      {
        costComponentAttribute.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a CostComponentAttribute by its Primary Key, specified by individual fields
   *@param  costComponentId                  Field of the COST_COMPONENT_ID column.
   *@param  name                  Field of the NAME column.
   *@return       The CostComponentAttribute corresponding to the primaryKey
   */
  public static CostComponentAttribute findByPrimaryKey(String costComponentId, String name)
  {
    if(costComponentId == null || name == null) return null;
    CostComponentAttributePK primaryKey = new CostComponentAttributePK(costComponentId, name);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a CostComponentAttribute by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The CostComponentAttribute corresponding to the primaryKey
   */
  public static CostComponentAttribute findByPrimaryKey(org.ofbiz.commonapp.product.cost.CostComponentAttributePK primaryKey)
  {
    CostComponentAttribute costComponentAttribute = null;
    Debug.logInfo("CostComponentAttributeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      costComponentAttribute = (CostComponentAttribute)MyNarrow.narrow(getCostComponentAttributeHome().findByPrimaryKey(primaryKey), CostComponentAttribute.class);
      if(costComponentAttribute != null)
      {
        costComponentAttribute = costComponentAttribute.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return costComponentAttribute;
  }

  /** Finds all CostComponentAttribute entities
   *@return    Collection containing all CostComponentAttribute entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("CostComponentAttributeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getCostComponentAttributeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a CostComponentAttribute
   *@param  costComponentId                  Field of the COST_COMPONENT_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                Description of the Returned Value
   */
  public static CostComponentAttribute create(String costComponentId, String name, String value)
  {
    CostComponentAttribute costComponentAttribute = null;
    Debug.logInfo("CostComponentAttributeHelper.create: costComponentId, name: " + costComponentId + ", " + name);
    if(costComponentId == null || name == null) { return null; }

    try { costComponentAttribute = (CostComponentAttribute)MyNarrow.narrow(getCostComponentAttributeHome().create(costComponentId, name, value), CostComponentAttribute.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create costComponentAttribute with costComponentId, name: " + costComponentId + ", " + name);
      Debug.logError(ce);
      costComponentAttribute = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return costComponentAttribute;
  }

  /** Updates the corresponding CostComponentAttribute
   *@param  costComponentId                  Field of the COST_COMPONENT_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                Description of the Returned Value
   */
  public static CostComponentAttribute update(String costComponentId, String name, String value) throws java.rmi.RemoteException
  {
    if(costComponentId == null || name == null) { return null; }
    CostComponentAttribute costComponentAttribute = findByPrimaryKey(costComponentId, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    CostComponentAttribute costComponentAttributeValue = new CostComponentAttributeValue();

    if(value != null) { costComponentAttributeValue.setValue(value); }

    costComponentAttribute.setValueObject(costComponentAttributeValue);
    return costComponentAttribute;
  }

  /** Removes/deletes the specified  CostComponentAttribute
   *@param  costComponentId                  Field of the COST_COMPONENT_ID column.
   */
  public static void removeByCostComponentId(String costComponentId)
  {
    if(costComponentId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByCostComponentId(costComponentId));

    while(iterator.hasNext())
    {
      try
      {
        CostComponentAttribute costComponentAttribute = (CostComponentAttribute) iterator.next();
        Debug.logInfo("Removing costComponentAttribute with costComponentId:" + costComponentId);
        costComponentAttribute.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds CostComponentAttribute records by the following parameters:
   *@param  costComponentId                  Field of the COST_COMPONENT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByCostComponentId(String costComponentId)
  {
    Debug.logInfo("findByCostComponentId: costComponentId:" + costComponentId);

    Collection collection = null;
    if(costComponentId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getCostComponentAttributeHome().findByCostComponentId(costComponentId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  CostComponentAttribute
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
        CostComponentAttribute costComponentAttribute = (CostComponentAttribute) iterator.next();
        Debug.logInfo("Removing costComponentAttribute with name:" + name);
        costComponentAttribute.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds CostComponentAttribute records by the following parameters:
   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByName(String name)
  {
    Debug.logInfo("findByName: name:" + name);

    Collection collection = null;
    if(name == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getCostComponentAttributeHome().findByName(name), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
