
package org.ofbiz.commonapp.product.storage;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Container Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the Container Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:32 MDT 2001
 *@version    1.0
 */
public class ContainerHelper
{

  /** A static variable to cache the Home object for the Container EJB */
  private static ContainerHome containerHome = null;

  /** Initializes the containerHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ContainerHome instance for the default EJB server
   */
  public static ContainerHome getContainerHome()
  {
    if(containerHome == null) //don't want to block here
    {
      synchronized(ContainerHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(containerHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.storage.ContainerHome");
            containerHome = (ContainerHome)MyNarrow.narrow(homeObject, ContainerHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("container home obtained " + containerHome);
        }
      }
    }
    return containerHome;
  }




  /** Remove the Container corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    Container container = findByPrimaryKey(primaryKey);
    try
    {
      if(container != null)
      {
        container.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a Container by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The Container corresponding to the primaryKey
   */
  public static Container findByPrimaryKey(java.lang.String primaryKey)
  {
    Container container = null;
    Debug.logInfo("ContainerHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      container = (Container)MyNarrow.narrow(getContainerHome().findByPrimaryKey(primaryKey), Container.class);
      if(container != null)
      {
        container = container.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return container;
  }

  /** Finds all Container entities
   *@return    Collection containing all Container entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ContainerHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getContainerHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a Container
   *@param  containerId                  Field of the CONTAINER_ID column.
   *@param  containerTypeId                  Field of the CONTAINER_TYPE_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@return                Description of the Returned Value
   */
  public static Container create(String containerId, String containerTypeId, String facilityId)
  {
    Container container = null;
    Debug.logInfo("ContainerHelper.create: containerId: " + containerId);
    if(containerId == null) { return null; }

    try { container = (Container)MyNarrow.narrow(getContainerHome().create(containerId, containerTypeId, facilityId), Container.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create container with containerId: " + containerId);
      Debug.logError(ce);
      container = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return container;
  }

  /** Updates the corresponding Container
   *@param  containerId                  Field of the CONTAINER_ID column.
   *@param  containerTypeId                  Field of the CONTAINER_TYPE_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@return                Description of the Returned Value
   */
  public static Container update(String containerId, String containerTypeId, String facilityId) throws java.rmi.RemoteException
  {
    if(containerId == null) { return null; }
    Container container = findByPrimaryKey(containerId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    Container containerValue = new ContainerValue();

    if(containerTypeId != null) { containerValue.setContainerTypeId(containerTypeId); }
    if(facilityId != null) { containerValue.setFacilityId(facilityId); }

    container.setValueObject(containerValue);
    return container;
  }

  /** Removes/deletes the specified  Container
   *@param  containerTypeId                  Field of the CONTAINER_TYPE_ID column.
   */
  public static void removeByContainerTypeId(String containerTypeId)
  {
    if(containerTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByContainerTypeId(containerTypeId));

    while(iterator.hasNext())
    {
      try
      {
        Container container = (Container) iterator.next();
        Debug.logInfo("Removing container with containerTypeId:" + containerTypeId);
        container.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds Container records by the following parameters:
   *@param  containerTypeId                  Field of the CONTAINER_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByContainerTypeId(String containerTypeId)
  {
    Debug.logInfo("findByContainerTypeId: containerTypeId:" + containerTypeId);

    Collection collection = null;
    if(containerTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getContainerHome().findByContainerTypeId(containerTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  Container
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
        Container container = (Container) iterator.next();
        Debug.logInfo("Removing container with facilityId:" + facilityId);
        container.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds Container records by the following parameters:
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByFacilityId(String facilityId)
  {
    Debug.logInfo("findByFacilityId: facilityId:" + facilityId);

    Collection collection = null;
    if(facilityId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getContainerHome().findByFacilityId(facilityId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
