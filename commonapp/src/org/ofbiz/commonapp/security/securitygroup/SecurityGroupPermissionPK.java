
package org.ofbiz.commonapp.security.securitygroup;

import java.io.*;

/**
 * <p><b>Title:</b> Security Component - Security Group Permission Entity
 * <p><b>Description:</b> Defines a permission available to a security group
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
 *@created    Wed May 23 02:31:23 MDT 2001
 *@version    1.0
 */

public class SecurityGroupPermissionPK implements Serializable
{


  /**
   *  The variable of the GROUP_ID column of the SECURITY_GROUP_PERMISSION table.
   */
  public String groupId;

  /**
   *  The variable of the PERMISSION_ID column of the SECURITY_GROUP_PERMISSION table.
   */
  public String permissionId;


  /**
   *  Constructor for the SecurityGroupPermissionPK object
   */
  public SecurityGroupPermissionPK()
  {
  }

  /**
   *  Constructor for the SecurityGroupPermissionPK object
   *

   *@param  groupId                  Field of the GROUP_ID column.
   *@param  permissionId                  Field of the PERMISSION_ID column.
   */
  public SecurityGroupPermissionPK(String groupId, String permissionId)
  {

    this.groupId = groupId;
    this.permissionId = permissionId;
  }

  /**
   *  Description of the Method
   *
   *@param  obj  Description of Field
   *@return      Description of the Returned Value
   */
  public boolean equals(Object obj)
  {
    if(this.getClass().equals(obj.getClass()))
    {
      SecurityGroupPermissionPK that = (SecurityGroupPermissionPK)obj;
      return

            this.groupId.equals(that.groupId) &&
            this.permissionId.equals(that.permissionId) &&
            true; //This "true" is a dummy thing to take care of the last &&, just for laziness sake.
    }
    return false;
  }

  /**
   *  Description of the Method
   *
   *@return    Description of the Returned Value
   */
  public int hashCode()
  {
    return (groupId + permissionId).hashCode();
  }
}
