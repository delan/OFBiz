
package org.ofbiz.commonapp.product.storage;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Container Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ContainerType Entity EJB; acts as a proxy for the Home interface
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
public class ContainerTypeHelper
{

  /** A static variable to cache the Home object for the ContainerType EJB */
  private static ContainerTypeHome containerTypeHome = null;

  /** Initializes the containerTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ContainerTypeHome instance for the default EJB server
   */
  public static ContainerTypeHome getContainerTypeHome()
  {
    if(containerTypeHome == null) //don't want to block here
    {
      synchronized(ContainerTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(containerTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.storage.ContainerTypeHome");
            containerTypeHome = (ContainerTypeHome)MyNarrow.narrow(homeObject, ContainerTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("containerType home obtained " + containerTypeHome);
        }
      }
    }
    return containerTypeHome;
  }




  /** Remove the ContainerType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    ContainerType containerType = findByPrimaryKey(primaryKey);
    try
    {
      if(containerType != null)
      {
        containerType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a ContainerType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ContainerType corresponding to the primaryKey
   */
  public static ContainerType findByPrimaryKey(java.lang.String primaryKey)
  {
    ContainerType containerType = null;
    Debug.logInfo("ContainerTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      containerType = (ContainerType)MyNarrow.narrow(getContainerTypeHome().findByPrimaryKey(primaryKey), ContainerType.class);
      if(containerType != null)
      {
        containerType = containerType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return containerType;
  }

  /** Finds all ContainerType entities
   *@return    Collection containing all ContainerType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ContainerTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getContainerTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ContainerType
   *@param  containerTypeId                  Field of the CONTAINER_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static ContainerType create(String containerTypeId, String description)
  {
    ContainerType containerType = null;
    Debug.logInfo("ContainerTypeHelper.create: containerTypeId: " + containerTypeId);
    if(containerTypeId == null) { return null; }

    try { containerType = (ContainerType)MyNarrow.narrow(getContainerTypeHome().create(containerTypeId, description), ContainerType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create containerType with containerTypeId: " + containerTypeId);
      Debug.logError(ce);
      containerType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return containerType;
  }

  /** Updates the corresponding ContainerType
   *@param  containerTypeId                  Field of the CONTAINER_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static ContainerType update(String containerTypeId, String description) throws java.rmi.RemoteException
  {
    if(containerTypeId == null) { return null; }
    ContainerType containerType = findByPrimaryKey(containerTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ContainerType containerTypeValue = new ContainerTypeValue();

    if(description != null) { containerTypeValue.setDescription(description); }

    containerType.setValueObject(containerTypeValue);
    return containerType;
  }


}
