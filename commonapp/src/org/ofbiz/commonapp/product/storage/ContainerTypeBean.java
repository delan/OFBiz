
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Container Type Entity
 * <p><b>Description:</b> None
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
public class ContainerTypeBean implements EntityBean
{
  /** The variable for the CONTAINER_TYPE_ID column of the CONTAINER_TYPE table. */
  public String containerTypeId;
  /** The variable for the DESCRIPTION column of the CONTAINER_TYPE table. */
  public String description;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the ContainerTypeBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key CONTAINER_TYPE_ID column of the CONTAINER_TYPE table. */
  public String getContainerTypeId() { return containerTypeId; }

  /** Get the value of the DESCRIPTION column of the CONTAINER_TYPE table. */
  public String getDescription() { return description; }
  /** Set the value of the DESCRIPTION column of the CONTAINER_TYPE table. */
  public void setDescription(String description)
  {
    this.description = description;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the ContainerTypeBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(ContainerType valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getDescription() != null)
      {
        this.description = valueObject.getDescription();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the ContainerTypeBean object
   *@return    The ValueObject value
   */
  public ContainerType getValueObject()
  {
    if(this.entityContext != null)
    {
      return new ContainerTypeValue((ContainerType)this.entityContext.getEJBObject(), containerTypeId, description);
    }
    else { return null; }
  }


  /** Get a collection of  Container related entities. */
  public Collection getContainers() { return ContainerHelper.findByContainerTypeId(containerTypeId); }
  /** Get the  Container keyed by member(s) of this class, and other passed parameters. */
  public Container getContainer(String containerId) { return ContainerHelper.findByPrimaryKey(containerId); }
  /** Remove  Container related entities. */
  public void removeContainers() { ContainerHelper.removeByContainerTypeId(containerTypeId); }
  /** Remove the  Container keyed by member(s) of this class, and other passed parameters. */
  public void removeContainer(String containerId) { ContainerHelper.removeByPrimaryKey(containerId); }


  /** Description of the Method
   *@param  containerTypeId                  Field of the CONTAINER_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String containerTypeId, String description) throws CreateException
  {
    this.containerTypeId = containerTypeId;
    this.description = description;
    return null;
  }

  /** Description of the Method
   *@param  containerTypeId                  Field of the CONTAINER_TYPE_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String containerTypeId) throws CreateException
  {
    return ejbCreate(containerTypeId, null);
  }

  /** Description of the Method
   *@param  containerTypeId                  Field of the CONTAINER_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String containerTypeId, String description) throws CreateException {}

  /** Description of the Method
   *@param  containerTypeId                  Field of the CONTAINER_TYPE_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String containerTypeId) throws CreateException
  {
    ejbPostCreate(containerTypeId, null);
  }

  /** Called when the entity bean is removed.
   *@exception  RemoveException  Description of Exception
   */
  public void ejbRemove() throws RemoveException {}

  /** Called when the entity bean is activated. */
  public void ejbActivate() {}

  /** Called when the entity bean is passivated. */
  public void ejbPassivate() {}

  /** Called when the entity bean is loaded. */
  public void ejbLoad() { ejbIsModified = false; }

  /** Called when the entity bean is stored. */
  public void ejbStore() { ejbIsModified = false; }

  /** Called to check if the entity bean needs to be stored. */
  public boolean isModified() { return ejbIsModified; }

  /** Unsets the EntityContext, ie sets it to null. */
  public void unsetEntityContext() { entityContext = null; }
}
