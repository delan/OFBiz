
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.math.*;
import org.ofbiz.commonapp.security.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Quantity Break Entity
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

public class QuantityBreakWebEvent
{
  /** An HTTP WebEvent handler that updates a QuantityBreak entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Returns a String specifying the outcome state of the event. This is used to decide which event to run next or which view to display. If null no event is run nor view displayed, allowing the event to call a forward on a RequestDispatcher.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static String updateQuantityBreak(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return "success";
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return "success";    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateQuantityBreak: Update Mode was not specified, but is required.");
      Debug.logWarning("updateQuantityBreak: Update Mode was not specified, but is required.");
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("QUANTITY_BREAK", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " QuantityBreak (QUANTITY_BREAK_" + updateMode + " or QUANTITY_BREAK_ADMIN needed).");
      return "success";
    }

    //get the primary key parameters...
  
    String quantityBreakId = request.getParameter("QUANTITY_BREAK_QUANTITY_BREAK_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual QuantityBreak last, just in case database is set up to do a cascading delete, caches won't get cleared
      QuantityBreakHelper.removeByPrimaryKey(quantityBreakId);
      return "success";
    }

    //get the non-primary key parameters
  
    String fromQuantityString = request.getParameter("QUANTITY_BREAK_FROM_QUANTITY");  
    String thruQuantityString = request.getParameter("QUANTITY_BREAK_THRU_QUANTITY");  

  
    Double fromQuantity = null;
    try
    {
      if(fromQuantityString != null && fromQuantityString.length() > 0)
      { 
        fromQuantity = Double.valueOf(fromQuantityString);
      }
    }
    catch(Exception e)
    {
      errMsg = errMsg + "<li>FROM_QUANTITY conversion failed: \"" + fromQuantityString + "\" is not a valid Double";
    }
    Double thruQuantity = null;
    try
    {
      if(thruQuantityString != null && thruQuantityString.length() > 0)
      { 
        thruQuantity = Double.valueOf(thruQuantityString);
      }
    }
    catch(Exception e)
    {
      errMsg = errMsg + "<li>THRU_QUANTITY conversion failed: \"" + thruQuantityString + "\" is not a valid Double";
    }

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(QuantityBreakHelper.findByPrimaryKey(quantityBreakId) != null) errMsg = errMsg + "<li>QuantityBreak already exists with QUANTITY_BREAK_ID:" + quantityBreakId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(quantityBreakId)) errMsg = errMsg + "<li>QUANTITY_BREAK_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isSignedDouble(fromQuantityString)) errMsg = errMsg + "<li>FROM_QUANTITY isSignedDouble failed: " + UtilValidate.isSignedDoubleMsg;
    if(!UtilValidate.isSignedDouble(thruQuantityString)) errMsg = errMsg + "<li>THRU_QUANTITY isSignedDouble failed: " + UtilValidate.isSignedDoubleMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }

    if(updateMode.equals("CREATE"))
    {
      QuantityBreak quantityBreak = QuantityBreakHelper.create(quantityBreakId, fromQuantity, thruQuantity);
      if(quantityBreak == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of QuantityBreak failed. QUANTITY_BREAK_ID: " + quantityBreakId);
        return "success";
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      QuantityBreak quantityBreak = QuantityBreakHelper.update(quantityBreakId, fromQuantity, thruQuantity);
      if(quantityBreak == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of QuantityBreak failed. QUANTITY_BREAK_ID: " + quantityBreakId);
        return "success";
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateQuantityBreak: Update Mode specified (" + updateMode + ") was not valid.");
      Debug.logWarning("updateQuantityBreak: Update Mode specified (" + updateMode + ") was not valid.");
    }

    return "success";
  }
}
