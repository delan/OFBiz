package org.ofbiz.commonapp.security;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import org.ofbiz.commonapp.common.*;
import org.ofbiz.commonapp.security.securitygroup.*;
import org.ofbiz.commonapp.security.login.*;

/**
 * <p><b>Title:</b> UserLogin Component - UserLogin WebEvent
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
  /** An HTTP WebEvent handler that checks to see is a userLogin is logged in. If not, the user is forwarded to the /login.jsp page.
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @throws RemoteException
   * @throws IOException
   * @throws ServletException
   * @return
   */  
  public static boolean check(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException 
  {
    UserLogin userLogin = (UserLogin) request.getSession().getAttribute("USER_LOGIN");

    if(userLogin == null)
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

  /** An HTTP WebEvent handler that logs in a userLogin. This should run before the security check.
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
      UserLogin userLogin = UserLoginHelper.findByPrimaryKey(username);
      if(userLogin != null)
      {
        if(userLogin.getCurrentPassword().compareTo(password) == 0)
        {
          request.getSession().setAttribute("USER_LOGIN", userLogin);
        }
        else
        {
          // password invalid, just go to badlogin page...
          errMsg = "Password incorrect.";
        }
      }
      else
      {
        //userLogin record not found, user does not exist
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

  /** An HTTP WebEvent handler that logs out a userLogin by clearing the session.
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception java.io.IOException Standard IO Exception
   */  
  public static boolean logout(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException 
  {
    //invalidate the security group list cache
    UserLogin userLogin = (UserLogin) request.getSession().getAttribute("USER_LOGIN");
    if(userLogin != null) Security.userLoginSecurityGroupByUserLoginId.remove(userLogin.getUserLoginId());
    
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
}
