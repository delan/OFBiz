
package org.ofbiz.commonapp.common.status;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Status Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the StatusType Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:22 MDT 2001
 *@version    1.0
 */
public class StatusTypeHelper
{

  /** A static variable to cache the Home object for the StatusType EJB */
  private static StatusTypeHome statusTypeHome = null;

  /** Initializes the statusTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The StatusTypeHome instance for the default EJB server
   */
  public static StatusTypeHome getStatusTypeHome()
  {
    if(statusTypeHome == null) //don't want to block here
    {
      synchronized(StatusTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(statusTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.common.status.StatusTypeHome");
            statusTypeHome = (StatusTypeHome)MyNarrow.narrow(homeObject, StatusTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("statusType home obtained " + statusTypeHome);
        }
      }
    }
    return statusTypeHome;
  }




  /** Remove the StatusType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    StatusType statusType = findByPrimaryKey(primaryKey);
    try
    {
      if(statusType != null)
      {
        statusType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a StatusType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The StatusType corresponding to the primaryKey
   */
  public static StatusType findByPrimaryKey(java.lang.String primaryKey)
  {
    StatusType statusType = null;
    Debug.logInfo("StatusTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      statusType = (StatusType)MyNarrow.narrow(getStatusTypeHome().findByPrimaryKey(primaryKey), StatusType.class);
      if(statusType != null)
      {
        statusType = statusType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return statusType;
  }

  /** Finds all StatusType entities
   *@return    Collection containing all StatusType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("StatusTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getStatusTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a StatusType
   *@param  statusTypeId                  Field of the STATUS_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static StatusType create(String statusTypeId, String parentTypeId, String hasTable, String description)
  {
    StatusType statusType = null;
    Debug.logInfo("StatusTypeHelper.create: statusTypeId: " + statusTypeId);
    if(statusTypeId == null) { return null; }

    try { statusType = (StatusType)MyNarrow.narrow(getStatusTypeHome().create(statusTypeId, parentTypeId, hasTable, description), StatusType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create statusType with statusTypeId: " + statusTypeId);
      Debug.logError(ce);
      statusType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return statusType;
  }

  /** Updates the corresponding StatusType
   *@param  statusTypeId                  Field of the STATUS_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static StatusType update(String statusTypeId, String parentTypeId, String hasTable, String description) throws java.rmi.RemoteException
  {
    if(statusTypeId == null) { return null; }
    StatusType statusType = findByPrimaryKey(statusTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    StatusType statusTypeValue = new StatusTypeValue();

    if(parentTypeId != null) { statusTypeValue.setParentTypeId(parentTypeId); }
    if(hasTable != null) { statusTypeValue.setHasTable(hasTable); }
    if(description != null) { statusTypeValue.setDescription(description); }

    statusType.setValueObject(statusTypeValue);
    return statusType;
  }

  /** Removes/deletes the specified  StatusType
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
        StatusType statusType = (StatusType) iterator.next();
        Debug.logInfo("Removing statusType with parentTypeId:" + parentTypeId);
        statusType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds StatusType records by the following parameters:
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByParentTypeId(String parentTypeId)
  {
    Debug.logInfo("findByParentTypeId: parentTypeId:" + parentTypeId);

    Collection collection = null;
    if(parentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getStatusTypeHome().findByParentTypeId(parentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  StatusType
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
        StatusType statusType = (StatusType) iterator.next();
        Debug.logInfo("Removing statusType with hasTable:" + hasTable);
        statusType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds StatusType records by the following parameters:
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByHasTable(String hasTable)
  {
    Debug.logInfo("findByHasTable: hasTable:" + hasTable);

    Collection collection = null;
    if(hasTable == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getStatusTypeHome().findByHasTable(hasTable), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
