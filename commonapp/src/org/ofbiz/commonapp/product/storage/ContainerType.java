
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

public interface ContainerType extends EJBObject
{
  /** Get the primary key of the CONTAINER_TYPE_ID column of the CONTAINER_TYPE table. */
  public String getContainerTypeId() throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the CONTAINER_TYPE table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the CONTAINER_TYPE table. */
  public void setDescription(String description) throws RemoteException;
  

  /** Get the value object of this ContainerType class. */
  public ContainerType getValueObject() throws RemoteException;
  /** Set the values in the value object of this ContainerType class. */
  public void setValueObject(ContainerType containerTypeValue) throws RemoteException;


  /** Get a collection of  Container related entities. */
  public Collection getContainers() throws RemoteException;
  /** Get the  Container keyed by member(s) of this class, and other passed parameters. */
  public Container getContainer(String containerId) throws RemoteException;
  /** Remove  Container related entities. */
  public void removeContainers() throws RemoteException;
  /** Remove the  Container keyed by member(s) of this class, and other passed parameters. */
  public void removeContainer(String containerId) throws RemoteException;

}
