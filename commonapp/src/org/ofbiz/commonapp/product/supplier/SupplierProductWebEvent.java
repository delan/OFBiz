
package org.ofbiz.commonapp.product.supplier;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.math.*;
import org.ofbiz.commonapp.security.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Supplier Product Entity
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
 *@created    Fri Jul 27 01:18:33 MDT 2001
 *@version    1.0
 */

public class SupplierProductWebEvent
{
  /** An HTTP WebEvent handler that updates a SupplierProduct entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Returns a String specifying the outcome state of the event. This is used to decide which event to run next or which view to display. If null no event is run nor view displayed, allowing the event to call a forward on a RequestDispatcher.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static String updateSupplierProduct(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return "success";
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return "success";    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateSupplierProduct: Update Mode was not specified, but is required.");
      Debug.logWarning("updateSupplierProduct: Update Mode was not specified, but is required.");
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("SUPPLIER_PRODUCT", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " SupplierProduct (SUPPLIER_PRODUCT_" + updateMode + " or SUPPLIER_PRODUCT_ADMIN needed).");
      return "success";
    }

    //get the primary key parameters...
  
    String productId = request.getParameter("SUPPLIER_PRODUCT_PRODUCT_ID");  
    String partyId = request.getParameter("SUPPLIER_PRODUCT_PARTY_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual SupplierProduct last, just in case database is set up to do a cascading delete, caches won't get cleared
      SupplierProductHelper.removeByPrimaryKey(productId, partyId);
      return "success";
    }

    //get the non-primary key parameters
  
    String availableFromDateDate = request.getParameter("SUPPLIER_PRODUCT_AVAILABLE_FROM_DATE_DATE");
    String availableFromDateTime = request.getParameter("SUPPLIER_PRODUCT_AVAILABLE_FROM_DATE_TIME");  
    String availableThruDateDate = request.getParameter("SUPPLIER_PRODUCT_AVAILABLE_THRU_DATE_DATE");
    String availableThruDateTime = request.getParameter("SUPPLIER_PRODUCT_AVAILABLE_THRU_DATE_TIME");  
    String supplierPrefOrderId = request.getParameter("SUPPLIER_PRODUCT_SUPPLIER_PREF_ORDER_ID");  
    String supplierRatingTypeId = request.getParameter("SUPPLIER_PRODUCT_SUPPLIER_RATING_TYPE_ID");  
    String standardLeadTimeDate = request.getParameter("SUPPLIER_PRODUCT_STANDARD_LEAD_TIME_DATE");
    String standardLeadTimeTime = request.getParameter("SUPPLIER_PRODUCT_STANDARD_LEAD_TIME_TIME");  
    String comment = request.getParameter("SUPPLIER_PRODUCT_COMMENT");  

  
    java.util.Date availableFromDate = UtilDateTime.toDate(availableFromDateDate, availableFromDateTime);
    if(!UtilValidate.isDate(availableFromDateDate)) errMsg = errMsg + "<li>AVAILABLE_FROM_DATE isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(availableFromDateTime)) errMsg = errMsg + "<li>AVAILABLE_FROM_DATE isTime failed: " + UtilValidate.isTimeMsg;
    java.util.Date availableThruDate = UtilDateTime.toDate(availableThruDateDate, availableThruDateTime);
    if(!UtilValidate.isDate(availableThruDateDate)) errMsg = errMsg + "<li>AVAILABLE_THRU_DATE isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(availableThruDateTime)) errMsg = errMsg + "<li>AVAILABLE_THRU_DATE isTime failed: " + UtilValidate.isTimeMsg;
    java.util.Date standardLeadTime = UtilDateTime.toDate(standardLeadTimeDate, standardLeadTimeTime);
    if(!UtilValidate.isDate(standardLeadTimeDate)) errMsg = errMsg + "<li>STANDARD_LEAD_TIME isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(standardLeadTimeTime)) errMsg = errMsg + "<li>STANDARD_LEAD_TIME isTime failed: " + UtilValidate.isTimeMsg;

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(SupplierProductHelper.findByPrimaryKey(productId, partyId) != null) errMsg = errMsg + "<li>SupplierProduct already exists with PRODUCT_ID, PARTY_ID:" + productId + ", " + partyId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(productId)) errMsg = errMsg + "<li>PRODUCT_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isNotEmpty(partyId)) errMsg = errMsg + "<li>PARTY_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }

    if(updateMode.equals("CREATE"))
    {
      SupplierProduct supplierProduct = SupplierProductHelper.create(productId, partyId, availableFromDate, availableThruDate, supplierPrefOrderId, supplierRatingTypeId, standardLeadTime, comment);
      if(supplierProduct == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of SupplierProduct failed. PRODUCT_ID, PARTY_ID: " + productId + ", " + partyId);
        return "success";
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      SupplierProduct supplierProduct = SupplierProductHelper.update(productId, partyId, availableFromDate, availableThruDate, supplierPrefOrderId, supplierRatingTypeId, standardLeadTime, comment);
      if(supplierProduct == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of SupplierProduct failed. PRODUCT_ID, PARTY_ID: " + productId + ", " + partyId);
        return "success";
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateSupplierProduct: Update Mode specified (" + updateMode + ") was not valid.");
      Debug.logWarning("updateSupplierProduct: Update Mode specified (" + updateMode + ") was not valid.");
    }

    return "success";
  }
}
