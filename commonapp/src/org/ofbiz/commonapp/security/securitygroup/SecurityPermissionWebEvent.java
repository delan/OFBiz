
package org.ofbiz.commonapp.security.securitygroup;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.math.*;
import org.ofbiz.commonapp.security.*;
import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Security Component - Security Permission Entity
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
 *@created    Fri Jun 29 12:50:48 MDT 2001
 *@version    1.0
 */

public class SecurityPermissionWebEvent
{
  /**
   *  An HTTP WebEvent handler that updates a SecurityPermission entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static boolean updateSecurityPermission(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return true;
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return true;    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateSecurityPermission: Update Mode was not specified, but is required.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updateSecurityPermission: Update Mode was not specified, but is required.");
      }
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("SECURITY_PERMISSION", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " SecurityPermission (SECURITY_PERMISSION_" + updateMode + " or SECURITY_PERMISSION_ADMIN needed).");
      return true;
    }

    //get the primary key parameters...
  
    String permissionId = request.getParameter("SECURITY_PERMISSION_PERMISSION_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual SecurityPermission last, just in case database is set up to do a cascading delete, caches won't get cleared
      SecurityPermissionHelper.removeByPrimaryKey(permissionId);
      return true;
    }

    //get the non-primary key parameters
  
    String description = request.getParameter("SECURITY_PERMISSION_DESCRIPTION");  

  

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(SecurityPermissionHelper.findByPrimaryKey(permissionId) != null) errMsg = errMsg + "<li>SecurityPermission already exists with PERMISSION_ID:" + permissionId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(permissionId)) errMsg = errMsg + "<li>PERMISSION_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      //note that it is much easier to do a RequestDispatcher.forward here instead of a respones.sendRedirect because the sendRedirent will not automatically keep the Parameters...
      RequestDispatcher rd;
      String onErrorPage = request.getParameter("ON_ERROR_PAGE");
      if(onErrorPage != null) rd = request.getRequestDispatcher(onErrorPage);
      else rd = request.getRequestDispatcher("/commonapp/security/securitygroup/EditSecurityPermission.jsp");
      rd.forward(request, response);
      return false;
    }

    if(updateMode.equals("CREATE"))
    {
      SecurityPermission securityPermission = SecurityPermissionHelper.create(permissionId, description);
      if(securityPermission == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of SecurityPermission failed. PERMISSION_ID: " + permissionId);
        return true;
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      SecurityPermission securityPermission = SecurityPermissionHelper.update(permissionId, description);
      if(securityPermission == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of SecurityPermission failed. PERMISSION_ID: " + permissionId);
        return true;
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateSecurityPermission: Update Mode specified (" + updateMode + ") was not valid.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updateSecurityPermission: Update Mode specified (" + updateMode + ") was not valid.");
      }
    }

    return true;
  }
}
