
package org.ofbiz.commonapp.security.login;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.math.*;
import org.ofbiz.commonapp.security.*;
import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Login Account History Entity
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
 *@created    Tue Jul 03 01:11:48 MDT 2001
 *@version    1.0
 */

public class LoginAccountHistoryWebEvent
{
  /**
   *  An HTTP WebEvent handler that updates a LoginAccountHistory entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static boolean updateLoginAccountHistory(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return true;
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return true;    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateLoginAccountHistory: Update Mode was not specified, but is required.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updateLoginAccountHistory: Update Mode was not specified, but is required.");
      }
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " LoginAccountHistory (LOGIN_ACCOUNT_HISTORY_" + updateMode + " or LOGIN_ACCOUNT_HISTORY_ADMIN needed).");
      return true;
    }

    //get the primary key parameters...
  
    String userLoginId = request.getParameter("LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID");  
    String userLoginSeqId = request.getParameter("LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual LoginAccountHistory last, just in case database is set up to do a cascading delete, caches won't get cleared
      LoginAccountHistoryHelper.removeByPrimaryKey(userLoginId, userLoginSeqId);
      return true;
    }

    //get the non-primary key parameters
  
    String fromDateDate = request.getParameter("LOGIN_ACCOUNT_HISTORY_FROM_DATE_DATE");
    String fromDateTime = request.getParameter("LOGIN_ACCOUNT_HISTORY_FROM_DATE_TIME");  
    String thruDateDate = request.getParameter("LOGIN_ACCOUNT_HISTORY_THRU_DATE_DATE");
    String thruDateTime = request.getParameter("LOGIN_ACCOUNT_HISTORY_THRU_DATE_TIME");  
    String partyId = request.getParameter("LOGIN_ACCOUNT_HISTORY_PARTY_ID");  
    String userId = request.getParameter("LOGIN_ACCOUNT_HISTORY_USER_ID");  
    String password = request.getParameter("LOGIN_ACCOUNT_HISTORY_PASSWORD");  

  
    java.util.Date fromDate = UtilDateTime.toDate(fromDateDate, fromDateTime);
    if(!UtilValidate.isDate(fromDateDate)) errMsg = errMsg + "<li>FROM_DATE isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(fromDateTime)) errMsg = errMsg + "<li>FROM_DATE isTime failed: " + UtilValidate.isTimeMsg;
    java.util.Date thruDate = UtilDateTime.toDate(thruDateDate, thruDateTime);
    if(!UtilValidate.isDate(thruDateDate)) errMsg = errMsg + "<li>THRU_DATE isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(thruDateTime)) errMsg = errMsg + "<li>THRU_DATE isTime failed: " + UtilValidate.isTimeMsg;

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(LoginAccountHistoryHelper.findByPrimaryKey(userLoginId, userLoginSeqId) != null) errMsg = errMsg + "<li>LoginAccountHistory already exists with USER_LOGIN_ID, USER_LOGIN_SEQ_ID:" + userLoginId + ", " + userLoginSeqId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(userLoginId)) errMsg = errMsg + "<li>USER_LOGIN_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isNotEmpty(userLoginSeqId)) errMsg = errMsg + "<li>USER_LOGIN_SEQ_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      //note that it is much easier to do a RequestDispatcher.forward here instead of a respones.sendRedirect because the sendRedirent will not automatically keep the Parameters...
      RequestDispatcher rd;
      String onErrorPage = request.getParameter("ON_ERROR_PAGE");
      if(onErrorPage != null) rd = request.getRequestDispatcher(onErrorPage);
      else rd = request.getRequestDispatcher("/commonapp/security/login/EditLoginAccountHistory.jsp");
      rd.forward(request, response);
      return false;
    }

    if(updateMode.equals("CREATE"))
    {
      LoginAccountHistory loginAccountHistory = LoginAccountHistoryHelper.create(userLoginId, userLoginSeqId, fromDate, thruDate, partyId, userId, password);
      if(loginAccountHistory == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of LoginAccountHistory failed. USER_LOGIN_ID, USER_LOGIN_SEQ_ID: " + userLoginId + ", " + userLoginSeqId);
        return true;
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      LoginAccountHistory loginAccountHistory = LoginAccountHistoryHelper.update(userLoginId, userLoginSeqId, fromDate, thruDate, partyId, userId, password);
      if(loginAccountHistory == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of LoginAccountHistory failed. USER_LOGIN_ID, USER_LOGIN_SEQ_ID: " + userLoginId + ", " + userLoginSeqId);
        return true;
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateLoginAccountHistory: Update Mode specified (" + updateMode + ") was not valid.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updateLoginAccountHistory: Update Mode specified (" + updateMode + ") was not valid.");
      }
    }

    return true;
  }
}
