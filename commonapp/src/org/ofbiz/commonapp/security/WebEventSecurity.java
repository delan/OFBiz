package org.ofbiz.commonapp.security;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import org.ofbiz.commonapp.common.*;
import org.ofbiz.commonapp.person.*;
import org.ofbiz.commonapp.security.securitygroup.*;
import org.ofbiz.commonapp.security.person.*;

/**
 * <p><b>Title:</b> Person Component - Person WebEvent
 * <p><b>Description:</b> This class contains the WebEvent handlers for the Security module.
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
 *@created    May 22 2001
 *@version    1.0
 */
public class WebEventSecurity
{
  /** An HTTP WebEvent handler that checks to see is a person is logged in. If not, the user is forwarded to the /login.jsp page.
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @throws RemoteException
   * @throws IOException
   * @throws ServletException
   * @return
   */  
  public static boolean check(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException 
  {
    Person person = (Person) request.getSession().getAttribute("PERSON");

    if(person == null)
    {
      String queryString = null;
      Enumeration params = request.getParameterNames();
      while(params != null && params.hasMoreElements()) 
      {
        String paramName = (String) params.nextElement();
        if(paramName != null && paramName.compareTo("WEBPREEVENT") != 0)
        {
          if (queryString == null) queryString = paramName + "=" + request.getParameter(paramName);
          else queryString = queryString + "&" + paramName + "=" + request.getParameter(paramName);
        }
      }

      String requestURI = request.getRequestURI();
      String servletPath = request.getServletPath();
      String curPageURL = requestURI;
      if(queryString != null) curPageURL = curPageURL + "?" + queryString;
      request.getSession().setAttribute("NEXT_PAGE_URL", curPageURL);

      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
      {
        System.out.println("WebEventSecurity.check: queryString=" + queryString);
        System.out.println("WebEventSecurity.check: requestURI=" + requestURI);
        System.out.println("WebEventSecurity.check: servletPath=" + servletPath);
        System.out.println("WebEventSecurity.check: curPageURL=" + curPageURL);
      }

      RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
      rd.forward(request, response);
      return false;
    }
    else { return true; }
  }

  /** An HTTP WebEvent handler that logs in a person. This should run before the security check.
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */  
  public static boolean login(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException 
  {
    String errMsg = "";
    String username = request.getParameter("USERNAME");
    String password = request.getParameter("PASSWORD");

    if(username == null || username.length() <= 0)
    {
      errMsg = "Username missing.";
    }
    else if(password == null || password.length() <= 0)
    {
      errMsg = "Password missing";
    }
    else
    {
      Person person = PersonHelper.findByPrimaryKey(username);
      if(person != null)
      {
        if(person.getPassword().compareTo(password) == 0)
        {
          request.getSession().setAttribute("PERSON", person);
        }
        else
        {
          // password invalid, just go to badlogin page...
          errMsg = "Password incorrect.";
        }
      }
      else
      {
        //person record not found, user does not exist
        errMsg = "User not found.";
      }
    }
    
    if(errMsg.length() > 0)
    {
      errMsg = "<b>The following error occured:</b><br>" + errMsg;
      request.getSession().setAttribute("ERROR_MESSAGE", errMsg);
      RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
      rd.forward(request, response);
      return false;
    }
    return true;
  }
  
  /** An HTTP WebEvent handler that forces a login, even if it is not required for the requested page.
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */  
  public static boolean forceLogin(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException 
  {
    return check(request, response);
  }

  /** An HTTP WebEvent handler that logs out a person by clearing the session.
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception java.io.IOException Standard IO Exception
   */  
  public static boolean logout(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException 
  {
    //invalidate the security group list cache
    Person person = (Person) request.getSession().getAttribute("PERSON");
    if(person != null) Security.personSecurityGroupByUsername.remove(person.getUsername());
    
    //invalidate doesn't work because it requires a new request to rebuild for some reason
      //request.getSession().invalidate();
      //response.sendRedirect(request.getContextPath());
      //return false;
    Enumeration enum = request.getSession().getAttributeNames();
    while(enum != null && enum.hasMoreElements())
    {
      String attr = (String)enum.nextElement();
      request.getSession().removeAttribute(attr);
    }
    
    return true;
  }

  /**
   *  An HTTP WebEvent handler that updates a SecurityGroup entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static boolean updateSecurityGroup(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return true;
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return true;    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateSecurityGroup: Update Mode was not specified, but is required.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updateSecurityGroup: Update Mode was not specified, but is required.");
      }
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("SECURITY_GROUP", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " SecurityGroup (SECURITY_GROUP_" + updateMode + " or SECURITY_GROUP_ADMIN needed).");
      return true;
    }

    //get the primary key parameters...
  
    String groupId = request.getParameter("SECURITY_GROUP_GROUP_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual SecurityGroup last, just in case database is set up to do a cascading delete, caches won't get cleared
      SecurityGroupHelper.removeByPrimaryKey(groupId);
      return true;
    }

    //get the non-primary key parameters
  
    String description = request.getParameter("SECURITY_GROUP_DESCRIPTION");  

  

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(SecurityGroupHelper.findByPrimaryKey(groupId) != null) errMsg = errMsg + "<li>SecurityGroup already exists with GROUP_ID:" + groupId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(groupId)) errMsg = errMsg + "<li>GROUP_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      //note that it is much easier to do a RequestDispatcher.forward here instead of a respones.sendRedirect because the sendRedirent will not automatically keep the Parameters...
      RequestDispatcher rd;
      String onErrorPage = request.getParameter("ON_ERROR_PAGE");
      if(onErrorPage != null) rd = request.getRequestDispatcher(onErrorPage);
      else rd = request.getRequestDispatcher("/______InsertEditEntityPathNameHERE______/EditSecurityGroup.jsp");
      rd.forward(request, response);
      return false;
    }

    if(updateMode.equals("CREATE"))
    {
      SecurityGroup securityGroup = SecurityGroupHelper.create(groupId, description);
      if(securityGroup == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of SecurityGroup failed. GROUP_ID: " + groupId);
        return true;
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      SecurityGroup securityGroup = SecurityGroupHelper.update(groupId, description);
      if(securityGroup == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of SecurityGroup failed. GROUP_ID: " + groupId);
        return true;
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateSecurityGroup: Update Mode specified (" + updateMode + ") was not valid.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updateSecurityGroup: Update Mode specified (" + updateMode + ") was not valid.");
      }
    }

    return true;
  }

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
      else rd = request.getRequestDispatcher("/______InsertEditEntityPathNameHERE______/EditSecurityPermission.jsp");
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

  /**
   *  An HTTP WebEvent handler that updates a SecurityGroupPermission entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static boolean updateSecurityGroupPermission(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return true;
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return true;    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateSecurityGroupPermission: Update Mode was not specified, but is required.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updateSecurityGroupPermission: Update Mode was not specified, but is required.");
      }
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " SecurityGroupPermission (SECURITY_GROUP_PERMISSION_" + updateMode + " or SECURITY_GROUP_PERMISSION_ADMIN needed).");
      return true;
    }

    //get the primary key parameters...
  
    String groupId = request.getParameter("SECURITY_GROUP_PERMISSION_GROUP_ID");  
    String permissionId = request.getParameter("SECURITY_GROUP_PERMISSION_PERMISSION_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual SecurityGroupPermission last, just in case database is set up to do a cascading delete, caches won't get cleared
      SecurityGroupPermissionHelper.removeByPrimaryKey(groupId, permissionId);
      return true;
    }

    //get the non-primary key parameters
  

  

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(SecurityGroupPermissionHelper.findByPrimaryKey(groupId, permissionId) != null) errMsg = errMsg + "<li>SecurityGroupPermission already exists with GROUP_ID, PERMISSION_ID:" + groupId + ", " + permissionId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(groupId)) errMsg = errMsg + "<li>GROUP_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isNotEmpty(permissionId)) errMsg = errMsg + "<li>PERMISSION_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      //note that it is much easier to do a RequestDispatcher.forward here instead of a respones.sendRedirect because the sendRedirent will not automatically keep the Parameters...
      RequestDispatcher rd;
      String onErrorPage = request.getParameter("ON_ERROR_PAGE");
      if(onErrorPage != null) rd = request.getRequestDispatcher(onErrorPage);
      else rd = request.getRequestDispatcher("/______InsertEditEntityPathNameHERE______/EditSecurityGroupPermission.jsp");
      rd.forward(request, response);
      return false;
    }

    if(updateMode.equals("CREATE"))
    {
      SecurityGroupPermission securityGroupPermission = SecurityGroupPermissionHelper.create(groupId, permissionId);
      if(securityGroupPermission == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of SecurityGroupPermission failed. GROUP_ID, PERMISSION_ID: " + groupId + ", " + permissionId);
        return true;
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      SecurityGroupPermission securityGroupPermission = SecurityGroupPermissionHelper.update(groupId, permissionId);
      if(securityGroupPermission == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of SecurityGroupPermission failed. GROUP_ID, PERMISSION_ID: " + groupId + ", " + permissionId);
        return true;
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateSecurityGroupPermission: Update Mode specified (" + updateMode + ") was not valid.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updateSecurityGroupPermission: Update Mode specified (" + updateMode + ") was not valid.");
      }
    }

    return true;
  }

  /**
   *  An HTTP WebEvent handler that updates a PersonSecurityGroup entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static boolean updatePersonSecurityGroup(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return true;
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return true;    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePersonSecurityGroup: Update Mode was not specified, but is required.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updatePersonSecurityGroup: Update Mode was not specified, but is required.");
      }
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("PERSON_SECURITY_GROUP", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " PersonSecurityGroup (PERSON_SECURITY_GROUP_" + updateMode + " or PERSON_SECURITY_GROUP_ADMIN needed).");
      return true;
    }

    //get the primary key parameters...
  
    String username = request.getParameter("PERSON_SECURITY_GROUP_USERNAME");  
    String groupId = request.getParameter("PERSON_SECURITY_GROUP_GROUP_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual PersonSecurityGroup last, just in case database is set up to do a cascading delete, caches won't get cleared
      PersonSecurityGroupHelper.removeByPrimaryKey(username, groupId);
      return true;
    }

    //get the non-primary key parameters
  

  

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(PersonSecurityGroupHelper.findByPrimaryKey(username, groupId) != null) errMsg = errMsg + "<li>PersonSecurityGroup already exists with USERNAME, GROUP_ID:" + username + ", " + groupId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(username)) errMsg = errMsg + "<li>USERNAME isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isNotEmpty(groupId)) errMsg = errMsg + "<li>GROUP_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      //note that it is much easier to do a RequestDispatcher.forward here instead of a respones.sendRedirect because the sendRedirent will not automatically keep the Parameters...
      RequestDispatcher rd;
      String onErrorPage = request.getParameter("ON_ERROR_PAGE");
      if(onErrorPage != null) rd = request.getRequestDispatcher(onErrorPage);
      else rd = request.getRequestDispatcher("/______InsertEditEntityPathNameHERE______/EditPersonSecurityGroup.jsp");
      rd.forward(request, response);
      return false;
    }

    if(updateMode.equals("CREATE"))
    {
      PersonSecurityGroup personSecurityGroup = PersonSecurityGroupHelper.create(username, groupId);
      if(personSecurityGroup == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of PersonSecurityGroup failed. USERNAME, GROUP_ID: " + username + ", " + groupId);
        return true;
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      PersonSecurityGroup personSecurityGroup = PersonSecurityGroupHelper.update(username, groupId);
      if(personSecurityGroup == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of PersonSecurityGroup failed. USERNAME, GROUP_ID: " + username + ", " + groupId);
        return true;
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePersonSecurityGroup: Update Mode specified (" + updateMode + ") was not valid.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updatePersonSecurityGroup: Update Mode specified (" + updateMode + ") was not valid.");
      }
    }

    return true;
  }
}
