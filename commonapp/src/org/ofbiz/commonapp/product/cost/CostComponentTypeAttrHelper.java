
package org.ofbiz.commonapp.product.cost;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Cost Component Type Attribute Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the CostComponentTypeAttr Entity EJB; acts as a proxy for the Home interface
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
public class CostComponentTypeAttrHelper
{

  /** A static variable to cache the Home object for the CostComponentTypeAttr EJB */
  private static CostComponentTypeAttrHome costComponentTypeAttrHome = null;

  /** Initializes the costComponentTypeAttrHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The CostComponentTypeAttrHome instance for the default EJB server
   */
  public static CostComponentTypeAttrHome getCostComponentTypeAttrHome()
  {
    if(costComponentTypeAttrHome == null) //don't want to block here
    {
      synchronized(CostComponentTypeAttrHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(costComponentTypeAttrHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.cost.CostComponentTypeAttrHome");
            costComponentTypeAttrHome = (CostComponentTypeAttrHome)MyNarrow.narrow(homeObject, CostComponentTypeAttrHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("costComponentTypeAttr home obtained " + costComponentTypeAttrHome);
        }
      }
    }
    return costComponentTypeAttrHome;
  }



  /** Remove the CostComponentTypeAttr corresponding to the primaryKey specified by fields
   *@param  costComponentTypeId                  Field of the COST_COMPONENT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   */
  public static void removeByPrimaryKey(String costComponentTypeId, String name)
  {
    if(costComponentTypeId == null || name == null)
    {
      return;
    }
    CostComponentTypeAttrPK primaryKey = new CostComponentTypeAttrPK(costComponentTypeId, name);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the CostComponentTypeAttr corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.cost.CostComponentTypeAttrPK primaryKey)
  {
    if(primaryKey == null) return;
    CostComponentTypeAttr costComponentTypeAttr = findByPrimaryKey(primaryKey);
    try
    {
      if(costComponentTypeAttr != null)
      {
        costComponentTypeAttr.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a CostComponentTypeAttr by its Primary Key, specified by individual fields
   *@param  costComponentTypeId                  Field of the COST_COMPONENT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return       The CostComponentTypeAttr corresponding to the primaryKey
   */
  public static CostComponentTypeAttr findByPrimaryKey(String costComponentTypeId, String name)
  {
    if(costComponentTypeId == null || name == null) return null;
    CostComponentTypeAttrPK primaryKey = new CostComponentTypeAttrPK(costComponentTypeId, name);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a CostComponentTypeAttr by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The CostComponentTypeAttr corresponding to the primaryKey
   */
  public static CostComponentTypeAttr findByPrimaryKey(org.ofbiz.commonapp.product.cost.CostComponentTypeAttrPK primaryKey)
  {
    CostComponentTypeAttr costComponentTypeAttr = null;
    Debug.logInfo("CostComponentTypeAttrHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      costComponentTypeAttr = (CostComponentTypeAttr)MyNarrow.narrow(getCostComponentTypeAttrHome().findByPrimaryKey(primaryKey), CostComponentTypeAttr.class);
      if(costComponentTypeAttr != null)
      {
        costComponentTypeAttr = costComponentTypeAttr.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return costComponentTypeAttr;
  }

  /** Finds all CostComponentTypeAttr entities
   *@return    Collection containing all CostComponentTypeAttr entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("CostComponentTypeAttrHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getCostComponentTypeAttrHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a CostComponentTypeAttr
   *@param  costComponentTypeId                  Field of the COST_COMPONENT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static CostComponentTypeAttr create(String costComponentTypeId, String name)
  {
    CostComponentTypeAttr costComponentTypeAttr = null;
    Debug.logInfo("CostComponentTypeAttrHelper.create: costComponentTypeId, name: " + costComponentTypeId + ", " + name);
    if(costComponentTypeId == null || name == null) { return null; }

    try { costComponentTypeAttr = (CostComponentTypeAttr)MyNarrow.narrow(getCostComponentTypeAttrHome().create(costComponentTypeId, name), CostComponentTypeAttr.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create costComponentTypeAttr with costComponentTypeId, name: " + costComponentTypeId + ", " + name);
      Debug.logError(ce);
      costComponentTypeAttr = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return costComponentTypeAttr;
  }

  /** Updates the corresponding CostComponentTypeAttr
   *@param  costComponentTypeId                  Field of the COST_COMPONENT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static CostComponentTypeAttr update(String costComponentTypeId, String name) throws java.rmi.RemoteException
  {
    if(costComponentTypeId == null || name == null) { return null; }
    CostComponentTypeAttr costComponentTypeAttr = findByPrimaryKey(costComponentTypeId, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    CostComponentTypeAttr costComponentTypeAttrValue = new CostComponentTypeAttrValue();


    costComponentTypeAttr.setValueObject(costComponentTypeAttrValue);
    return costComponentTypeAttr;
  }

  /** Removes/deletes the specified  CostComponentTypeAttr
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
        CostComponentTypeAttr costComponentTypeAttr = (CostComponentTypeAttr) iterator.next();
        Debug.logInfo("Removing costComponentTypeAttr with costComponentTypeId:" + costComponentTypeId);
        costComponentTypeAttr.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds CostComponentTypeAttr records by the following parameters:
   *@param  costComponentTypeId                  Field of the COST_COMPONENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByCostComponentTypeId(String costComponentTypeId)
  {
    Debug.logInfo("findByCostComponentTypeId: costComponentTypeId:" + costComponentTypeId);

    Collection collection = null;
    if(costComponentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getCostComponentTypeAttrHome().findByCostComponentTypeId(costComponentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  CostComponentTypeAttr
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
        CostComponentTypeAttr costComponentTypeAttr = (CostComponentTypeAttr) iterator.next();
        Debug.logInfo("Removing costComponentTypeAttr with name:" + name);
        costComponentTypeAttr.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds CostComponentTypeAttr records by the following parameters:
   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByName(String name)
  {
    Debug.logInfo("findByName: name:" + name);

    Collection collection = null;
    if(name == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getCostComponentTypeAttrHome().findByName(name), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
