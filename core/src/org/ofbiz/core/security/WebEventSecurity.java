package org.ofbiz.core.security;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;

/**
 * <p><b>Title:</b> Security Component - Security WebEvent
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
  public static String checkLogin(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException 
  {
    GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);

    if(userLogin == null)
    {
      String queryString = null;
      Enumeration params = request.getParameterNames();
      while(params != null && params.hasMoreElements()) 
      {
        String paramName = (String) params.nextElement();
        if(paramName != null)
        {
          if (queryString == null) queryString = paramName + "=" + request.getParameter(paramName);
          else queryString = queryString + "&" + paramName + "=" + request.getParameter(paramName);
        }
      }

      //String requestURI = request.getRequestURI();
      //String servletPath = request.getServletPath();
      //String curPageURL = requestURI;
      //if(queryString != null) curPageURL = curPageURL + "?" + queryString;
      request.getSession().setAttribute(SiteDefs.PREVIOUS_REQUEST, request.getPathInfo());
      if(queryString != null) request.getSession().setAttribute(SiteDefs.PREVIOUS_PARAMS, queryString);

      Debug.logInfo("WebEventSecurity.checkLogin: queryString=" + queryString);
      Debug.logInfo("WebEventSecurity.checkLogin: PathInfo=" + request.getPathInfo());
      //Debug.logInfo("WebEventSecurity.check: requestURI=" + requestURI);
      //Debug.logInfo("WebEventSecurity.check: servletPath=" + servletPath);
      //Debug.logInfo("WebEventSecurity.check: curPageURL=" + curPageURL);

      return "error";
    }
    else { return "success"; }
  }

  /** An HTTP WebEvent handler that logs in a userLogin. This should run before the security check.
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */  
  public static String login(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException 
  {
    String errMsg = "";
    String username = request.getParameter("USERNAME");
    String password = request.getParameter("PASSWORD");

    GenericHelper helper = (GenericHelper)request.getAttribute("helper");

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
      GenericValue userLogin = helper.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", username));
      if(userLogin != null)
      {
        if(password.compareTo(userLogin.getString("currentPassword")) == 0)
        {
          request.getSession().setAttribute(SiteDefs.USER_LOGIN, userLogin);
          helper.create("UserLoginHistory", UtilMisc.toMap("userLoginId", username, 
                                                           "fromDate", UtilDateTime.nowTimestamp(), 
                                                           "password", password, 
                                                           "partyId", userLogin.get("partyId"), 
                                                           "referrerUrl", "NotYetImplemented"));
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
      return "error";
    }
    return "success";
  }
  
  /** An HTTP WebEvent handler that forces a login, even if it is not required for the requested page.
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */  
  //public static boolean forceLogin(HttpServletRequest request) throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException 
  //{
  //  return loggedIn(request);
  //}

  /** An HTTP WebEvent handler that logs out a userLogin by clearing the session.
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception java.io.IOException Standard IO Exception
   */  
  public static String logout(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException 
  {
    //invalidate the security group list cache
    GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);
    Security sec = (Security)request.getAttribute("security");
    if(sec != null && userLogin != null) sec.userLoginSecurityGroupByUserLoginId.remove(userLogin.getString("userLoginId"));
    
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
    
    return "success";
  }
}
