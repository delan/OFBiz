
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.math.*;
import org.ofbiz.commonapp.security.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Classification Entity
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
 *@created    Fri Jul 27 01:18:24 MDT 2001
 *@version    1.0
 */

public class ProductClassWebEvent
{
  /** An HTTP WebEvent handler that updates a ProductClass entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Returns a String specifying the outcome state of the event. This is used to decide which event to run next or which view to display. If null no event is run nor view displayed, allowing the event to call a forward on a RequestDispatcher.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static String updateProductClass(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return "success";
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return "success";    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateProductClass: Update Mode was not specified, but is required.");
      Debug.logWarning("updateProductClass: Update Mode was not specified, but is required.");
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("PRODUCT_CLASS", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " ProductClass (PRODUCT_CLASS_" + updateMode + " or PRODUCT_CLASS_ADMIN needed).");
      return "success";
    }

    //get the primary key parameters...
  
    String productId = request.getParameter("PRODUCT_CLASS_PRODUCT_ID");  
    String productTypeId = request.getParameter("PRODUCT_CLASS_PRODUCT_TYPE_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual ProductClass last, just in case database is set up to do a cascading delete, caches won't get cleared
      ProductClassHelper.removeByPrimaryKey(productId, productTypeId);
      return "success";
    }

    //get the non-primary key parameters
  
    String fromDateDate = request.getParameter("PRODUCT_CLASS_FROM_DATE_DATE");
    String fromDateTime = request.getParameter("PRODUCT_CLASS_FROM_DATE_TIME");  
    String thruDateDate = request.getParameter("PRODUCT_CLASS_THRU_DATE_DATE");
    String thruDateTime = request.getParameter("PRODUCT_CLASS_THRU_DATE_TIME");  

  
    java.util.Date fromDate = UtilDateTime.toDate(fromDateDate, fromDateTime);
    if(!UtilValidate.isDate(fromDateDate)) errMsg = errMsg + "<li>FROM_DATE isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(fromDateTime)) errMsg = errMsg + "<li>FROM_DATE isTime failed: " + UtilValidate.isTimeMsg;
    java.util.Date thruDate = UtilDateTime.toDate(thruDateDate, thruDateTime);
    if(!UtilValidate.isDate(thruDateDate)) errMsg = errMsg + "<li>THRU_DATE isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(thruDateTime)) errMsg = errMsg + "<li>THRU_DATE isTime failed: " + UtilValidate.isTimeMsg;

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(ProductClassHelper.findByPrimaryKey(productId, productTypeId) != null) errMsg = errMsg + "<li>ProductClass already exists with PRODUCT_ID, PRODUCT_TYPE_ID:" + productId + ", " + productTypeId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(productId)) errMsg = errMsg + "<li>PRODUCT_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isNotEmpty(productTypeId)) errMsg = errMsg + "<li>PRODUCT_TYPE_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }

    if(updateMode.equals("CREATE"))
    {
      ProductClass productClass = ProductClassHelper.create(productId, productTypeId, fromDate, thruDate);
      if(productClass == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of ProductClass failed. PRODUCT_ID, PRODUCT_TYPE_ID: " + productId + ", " + productTypeId);
        return "success";
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      ProductClass productClass = ProductClassHelper.update(productId, productTypeId, fromDate, thruDate);
      if(productClass == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of ProductClass failed. PRODUCT_ID, PRODUCT_TYPE_ID: " + productId + ", " + productTypeId);
        return "success";
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateProductClass: Update Mode specified (" + updateMode + ") was not valid.");
      Debug.logWarning("updateProductClass: Update Mode specified (" + updateMode + ") was not valid.");
    }

    return "success";
  }
}
