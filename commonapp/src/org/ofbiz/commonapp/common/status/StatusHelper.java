
package org.ofbiz.commonapp.common.status;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Status Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the Status Entity EJB; acts as a proxy for the Home interface
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
public class StatusHelper
{

  /** A static variable to cache the Home object for the Status EJB */
  private static StatusHome statusHome = null;

  /** Initializes the statusHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The StatusHome instance for the default EJB server
   */
  public static StatusHome getStatusHome()
  {
    if(statusHome == null) //don't want to block here
    {
      synchronized(StatusHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(statusHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.common.status.StatusHome");
            statusHome = (StatusHome)MyNarrow.narrow(homeObject, StatusHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("status home obtained " + statusHome);
        }
      }
    }
    return statusHome;
  }




  /** Remove the Status corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    Status status = findByPrimaryKey(primaryKey);
    try
    {
      if(status != null)
      {
        status.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a Status by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The Status corresponding to the primaryKey
   */
  public static Status findByPrimaryKey(java.lang.String primaryKey)
  {
    Status status = null;
    Debug.logInfo("StatusHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      status = (Status)MyNarrow.narrow(getStatusHome().findByPrimaryKey(primaryKey), Status.class);
      if(status != null)
      {
        status = status.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return status;
  }

  /** Finds all Status entities
   *@return    Collection containing all Status entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("StatusHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getStatusHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a Status
   *@param  statusId                  Field of the STATUS_ID column.
   *@param  statusTypeId                  Field of the STATUS_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static Status create(String statusId, String statusTypeId, String description)
  {
    Status status = null;
    Debug.logInfo("StatusHelper.create: statusId: " + statusId);
    if(statusId == null) { return null; }

    try { status = (Status)MyNarrow.narrow(getStatusHome().create(statusId, statusTypeId, description), Status.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create status with statusId: " + statusId);
      Debug.logError(ce);
      status = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return status;
  }

  /** Updates the corresponding Status
   *@param  statusId                  Field of the STATUS_ID column.
   *@param  statusTypeId                  Field of the STATUS_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static Status update(String statusId, String statusTypeId, String description) throws java.rmi.RemoteException
  {
    if(statusId == null) { return null; }
    Status status = findByPrimaryKey(statusId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    Status statusValue = new StatusValue();

    if(statusTypeId != null) { statusValue.setStatusTypeId(statusTypeId); }
    if(description != null) { statusValue.setDescription(description); }

    status.setValueObject(statusValue);
    return status;
  }

  /** Removes/deletes the specified  Status
   *@param  statusTypeId                  Field of the STATUS_TYPE_ID column.
   */
  public static void removeByStatusTypeId(String statusTypeId)
  {
    if(statusTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByStatusTypeId(statusTypeId));

    while(iterator.hasNext())
    {
      try
      {
        Status status = (Status) iterator.next();
        Debug.logInfo("Removing status with statusTypeId:" + statusTypeId);
        status.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds Status records by the following parameters:
   *@param  statusTypeId                  Field of the STATUS_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByStatusTypeId(String statusTypeId)
  {
    Debug.logInfo("findByStatusTypeId: statusTypeId:" + statusTypeId);

    Collection collection = null;
    if(statusTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getStatusHome().findByStatusTypeId(statusTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
