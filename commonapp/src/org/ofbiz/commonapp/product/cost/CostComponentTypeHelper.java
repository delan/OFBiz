
package org.ofbiz.commonapp.product.cost;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Cost Component Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the CostComponentType Entity EJB; acts as a proxy for the Home interface
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
public class CostComponentTypeHelper
{

  /** A static variable to cache the Home object for the CostComponentType EJB */
  private static CostComponentTypeHome costComponentTypeHome = null;

  /** Initializes the costComponentTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The CostComponentTypeHome instance for the default EJB server
   */
  public static CostComponentTypeHome getCostComponentTypeHome()
  {
    if(costComponentTypeHome == null) //don't want to block here
    {
      synchronized(CostComponentTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(costComponentTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.cost.CostComponentTypeHome");
            costComponentTypeHome = (CostComponentTypeHome)MyNarrow.narrow(homeObject, CostComponentTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("costComponentType home obtained " + costComponentTypeHome);
        }
      }
    }
    return costComponentTypeHome;
  }




  /** Remove the CostComponentType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    CostComponentType costComponentType = findByPrimaryKey(primaryKey);
    try
    {
      if(costComponentType != null)
      {
        costComponentType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a CostComponentType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The CostComponentType corresponding to the primaryKey
   */
  public static CostComponentType findByPrimaryKey(java.lang.String primaryKey)
  {
    CostComponentType costComponentType = null;
    Debug.logInfo("CostComponentTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      costComponentType = (CostComponentType)MyNarrow.narrow(getCostComponentTypeHome().findByPrimaryKey(primaryKey), CostComponentType.class);
      if(costComponentType != null)
      {
        costComponentType = costComponentType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return costComponentType;
  }

  /** Finds all CostComponentType entities
   *@return    Collection containing all CostComponentType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("CostComponentTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getCostComponentTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a CostComponentType
   *@param  costComponentTypeId                  Field of the COST_COMPONENT_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static CostComponentType create(String costComponentTypeId, String parentTypeId, String hasTable, String description)
  {
    CostComponentType costComponentType = null;
    Debug.logInfo("CostComponentTypeHelper.create: costComponentTypeId: " + costComponentTypeId);
    if(costComponentTypeId == null) { return null; }

    try { costComponentType = (CostComponentType)MyNarrow.narrow(getCostComponentTypeHome().create(costComponentTypeId, parentTypeId, hasTable, description), CostComponentType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create costComponentType with costComponentTypeId: " + costComponentTypeId);
      Debug.logError(ce);
      costComponentType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return costComponentType;
  }

  /** Updates the corresponding CostComponentType
   *@param  costComponentTypeId                  Field of the COST_COMPONENT_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static CostComponentType update(String costComponentTypeId, String parentTypeId, String hasTable, String description) throws java.rmi.RemoteException
  {
    if(costComponentTypeId == null) { return null; }
    CostComponentType costComponentType = findByPrimaryKey(costComponentTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    CostComponentType costComponentTypeValue = new CostComponentTypeValue();

    if(parentTypeId != null) { costComponentTypeValue.setParentTypeId(parentTypeId); }
    if(hasTable != null) { costComponentTypeValue.setHasTable(hasTable); }
    if(description != null) { costComponentTypeValue.setDescription(description); }

    costComponentType.setValueObject(costComponentTypeValue);
    return costComponentType;
  }

  /** Removes/deletes the specified  CostComponentType
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
        CostComponentType costComponentType = (CostComponentType) iterator.next();
        Debug.logInfo("Removing costComponentType with parentTypeId:" + parentTypeId);
        costComponentType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds CostComponentType records by the following parameters:
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByParentTypeId(String parentTypeId)
  {
    Debug.logInfo("findByParentTypeId: parentTypeId:" + parentTypeId);

    Collection collection = null;
    if(parentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getCostComponentTypeHome().findByParentTypeId(parentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  CostComponentType
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
        CostComponentType costComponentType = (CostComponentType) iterator.next();
        Debug.logInfo("Removing costComponentType with hasTable:" + hasTable);
        costComponentType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds CostComponentType records by the following parameters:
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByHasTable(String hasTable)
  {
    Debug.logInfo("findByHasTable: hasTable:" + hasTable);

    Collection collection = null;
    if(hasTable == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getCostComponentTypeHome().findByHasTable(hasTable), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
