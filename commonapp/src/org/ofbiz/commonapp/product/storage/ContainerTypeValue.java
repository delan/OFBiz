
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class ContainerTypeValue implements ContainerType
{
  /** The variable of the CONTAINER_TYPE_ID column of the CONTAINER_TYPE table. */
  private String containerTypeId;
  /** The variable of the DESCRIPTION column of the CONTAINER_TYPE table. */
  private String description;

  private ContainerType containerType;

  public ContainerTypeValue()
  {
    this.containerTypeId = null;
    this.description = null;

    this.containerType = null;
  }

  public ContainerTypeValue(ContainerType containerType) throws RemoteException
  {
    if(containerType == null) return;
  
    this.containerTypeId = containerType.getContainerTypeId();
    this.description = containerType.getDescription();

    this.containerType = containerType;
  }

  public ContainerTypeValue(ContainerType containerType, String containerTypeId, String description)
  {
    if(containerType == null) return;
  
    this.containerTypeId = containerTypeId;
    this.description = description;

    this.containerType = containerType;
  }


  /** Get the primary key of the CONTAINER_TYPE_ID column of the CONTAINER_TYPE table. */
  public String getContainerTypeId()  throws RemoteException { return containerTypeId; }

  /** Get the value of the DESCRIPTION column of the CONTAINER_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the CONTAINER_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(containerType!=null) containerType.setDescription(description);
  }

  /** Get the value object of the ContainerType class. */
  public ContainerType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ContainerType class. */
  public void setValueObject(ContainerType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(containerType!=null) containerType.setValueObject(valueObject);

    if(containerTypeId == null) containerTypeId = valueObject.getContainerTypeId();
    description = valueObject.getDescription();
  }


  /** Get a collection of  Container related entities. */
  public Collection getContainers() { return ContainerHelper.findByContainerTypeId(containerTypeId); }
  /** Get the  Container keyed by member(s) of this class, and other passed parameters. */
  public Container getContainer(String containerId) { return ContainerHelper.findByPrimaryKey(containerId); }
  /** Remove  Container related entities. */
  public void removeContainers() { ContainerHelper.removeByContainerTypeId(containerTypeId); }
  /** Remove the  Container keyed by member(s) of this class, and other passed parameters. */
  public void removeContainer(String containerId) { ContainerHelper.removeByPrimaryKey(containerId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(containerType!=null) return containerType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(containerType!=null) return containerType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(containerType!=null) return containerType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(containerType!=null) return containerType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(containerType!=null) containerType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
