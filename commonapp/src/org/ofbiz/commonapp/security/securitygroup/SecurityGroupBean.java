
package org.ofbiz.commonapp.security.securitygroup;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;

/**
 * <p><b>Title:</b> Security Component - Security Group Entity
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
 *@created    Wed May 23 02:35:27 MDT 2001
 *@version    1.0
 */

public class SecurityGroupBean implements EntityBean
{

  /**
   *  The variable for the GROUP_ID column of the SECURITY_GROUP table.
   */
  public String groupId;

  /**
   *  The variable for the DESCRIPTION column of the SECURITY_GROUP table.
   */
  public String description;


  EntityContext entityContext;

  /**
   *  Sets the EntityContext attribute of the SecurityGroupBean object
   *
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext)
  {
    this.entityContext = entityContext;
  }



  
  /**
   *  Get the primary key GROUP_ID column of the SECURITY_GROUP table.
   */
  public String getGroupId()
  {
    return groupId;
  }
  

  
  /**
   *  Get the value of the DESCRIPTION column of the SECURITY_GROUP table.
   */
  public String getDescription()
  {
    return description;
  }
  /**
   *  Set the value of the DESCRIPTION column of the SECURITY_GROUP table.
   */
  public void setDescription(String description)
  {
    this.description = description;
  }
  


  /**
   *  Sets the values from ValueObject attribute of the SecurityGroupBean object
   *
   *@param  valueObject  The new ValueObject value
   */
  public void setValueObject(SecurityGroup valueObject)
  {

    try
    {

  
      this.description = valueObject.getDescription();
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }

  }

  /**
   *  Gets the ValueObject attribute of the SecurityGroupBean object
   *
   *@return    The ValueObject value
   */
  public SecurityGroup getValueObject()
  {
    if(this.entityContext != null)
    {
      return new SecurityGroupValue((SecurityGroup)this.entityContext.getEJBObject(), groupId, description);
    }
    else
    {
      return null;
    }
  }

  /**
   *  Description of the Method
   *

   *@param  groupId                  Field of the GROUP_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String groupId, String description) throws CreateException
  {

    this.groupId = groupId;
    this.description = description;
    return null;
  }

  /**
   *  Description of the Method
   *

   *@param  groupId                  Field of the GROUP_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String groupId) throws CreateException
  {
    return ejbCreate(groupId, null);
  }

  /**
   *  Description of the Method
   *

   *@param  groupId                  Field of the GROUP_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String groupId, String description) throws CreateException
  {
  }

  /**
   *  Description of the Method
   *

   *@param  groupId                  Field of the GROUP_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String groupId) throws CreateException
  {
    ejbPostCreate(groupId, null);
  }

  /**
   *  Called when the entity bean is removed.
   *
   *@exception  RemoveException  Description of Exception
   */
  public void ejbRemove() throws RemoveException
  {
  }

  /**
   *  Called when the entity bean is activated.
   */
  public void ejbActivate()
  {
  }

  /**
   *  Called when the entity bean is passivated.
   */
  public void ejbPassivate()
  {
  }

  /**
   *  Called when the entity bean is loaded.
   */
  public void ejbLoad()
  {
  }

  /**
   *  Called when the entity bean is stored.
   */
  public void ejbStore()
  {
  }

  /**
   *  Unsets the EntityContext, ie sets it to null.
   */
  public void unsetEntityContext()
  {
    entityContext = null;
  }
}
