
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.math.*;
import org.ofbiz.commonapp.security.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Order Value Break Entity
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
 *@created    Fri Jul 27 01:18:30 MDT 2001
 *@version    1.0
 */

public class OrderValueBreakWebEvent
{
  /** An HTTP WebEvent handler that updates a OrderValueBreak entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Returns a String specifying the outcome state of the event. This is used to decide which event to run next or which view to display. If null no event is run nor view displayed, allowing the event to call a forward on a RequestDispatcher.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static String updateOrderValueBreak(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return "success";
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return "success";    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateOrderValueBreak: Update Mode was not specified, but is required.");
      Debug.logWarning("updateOrderValueBreak: Update Mode was not specified, but is required.");
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("ORDER_VALUE_BREAK", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " OrderValueBreak (ORDER_VALUE_BREAK_" + updateMode + " or ORDER_VALUE_BREAK_ADMIN needed).");
      return "success";
    }

    //get the primary key parameters...
  
    String orderValueBreakId = request.getParameter("ORDER_VALUE_BREAK_ORDER_VALUE_BREAK_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual OrderValueBreak last, just in case database is set up to do a cascading delete, caches won't get cleared
      OrderValueBreakHelper.removeByPrimaryKey(orderValueBreakId);
      return "success";
    }

    //get the non-primary key parameters
  
    String fromAmountString = request.getParameter("ORDER_VALUE_BREAK_FROM_AMOUNT");  
    String thruAmountString = request.getParameter("ORDER_VALUE_BREAK_THRU_AMOUNT");  

  
    Double fromAmount = null;
    try
    {
      if(fromAmountString != null && fromAmountString.length() > 0)
      { 
        fromAmount = Double.valueOf(fromAmountString);
      }
    }
    catch(Exception e)
    {
      errMsg = errMsg + "<li>FROM_AMOUNT conversion failed: \"" + fromAmountString + "\" is not a valid Double";
    }
    Double thruAmount = null;
    try
    {
      if(thruAmountString != null && thruAmountString.length() > 0)
      { 
        thruAmount = Double.valueOf(thruAmountString);
      }
    }
    catch(Exception e)
    {
      errMsg = errMsg + "<li>THRU_AMOUNT conversion failed: \"" + thruAmountString + "\" is not a valid Double";
    }

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(OrderValueBreakHelper.findByPrimaryKey(orderValueBreakId) != null) errMsg = errMsg + "<li>OrderValueBreak already exists with ORDER_VALUE_BREAK_ID:" + orderValueBreakId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(orderValueBreakId)) errMsg = errMsg + "<li>ORDER_VALUE_BREAK_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isSignedDouble(fromAmountString)) errMsg = errMsg + "<li>FROM_AMOUNT isSignedDouble failed: " + UtilValidate.isSignedDoubleMsg;
    if(!UtilValidate.isSignedDouble(thruAmountString)) errMsg = errMsg + "<li>THRU_AMOUNT isSignedDouble failed: " + UtilValidate.isSignedDoubleMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }

    if(updateMode.equals("CREATE"))
    {
      OrderValueBreak orderValueBreak = OrderValueBreakHelper.create(orderValueBreakId, fromAmount, thruAmount);
      if(orderValueBreak == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of OrderValueBreak failed. ORDER_VALUE_BREAK_ID: " + orderValueBreakId);
        return "success";
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      OrderValueBreak orderValueBreak = OrderValueBreakHelper.update(orderValueBreakId, fromAmount, thruAmount);
      if(orderValueBreak == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of OrderValueBreak failed. ORDER_VALUE_BREAK_ID: " + orderValueBreakId);
        return "success";
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateOrderValueBreak: Update Mode specified (" + updateMode + ") was not valid.");
      Debug.logWarning("updateOrderValueBreak: Update Mode specified (" + updateMode + ") was not valid.");
    }

    return "success";
  }
}
