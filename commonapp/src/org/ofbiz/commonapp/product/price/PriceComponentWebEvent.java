
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.math.*;
import org.ofbiz.commonapp.security.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Price Component Entity
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
 *@created    Fri Jul 27 01:18:29 MDT 2001
 *@version    1.0
 */

public class PriceComponentWebEvent
{
  /** An HTTP WebEvent handler that updates a PriceComponent entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Returns a String specifying the outcome state of the event. This is used to decide which event to run next or which view to display. If null no event is run nor view displayed, allowing the event to call a forward on a RequestDispatcher.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static String updatePriceComponent(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return "success";
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return "success";    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePriceComponent: Update Mode was not specified, but is required.");
      Debug.logWarning("updatePriceComponent: Update Mode was not specified, but is required.");
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("PRICE_COMPONENT", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " PriceComponent (PRICE_COMPONENT_" + updateMode + " or PRICE_COMPONENT_ADMIN needed).");
      return "success";
    }

    //get the primary key parameters...
  
    String priceComponentId = request.getParameter("PRICE_COMPONENT_PRICE_COMPONENT_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual PriceComponent last, just in case database is set up to do a cascading delete, caches won't get cleared
      PriceComponentHelper.removeByPrimaryKey(priceComponentId);
      return "success";
    }

    //get the non-primary key parameters
  
    String priceComponentTypeId = request.getParameter("PRICE_COMPONENT_PRICE_COMPONENT_TYPE_ID");  
    String partyId = request.getParameter("PRICE_COMPONENT_PARTY_ID");  
    String partyTypeId = request.getParameter("PRICE_COMPONENT_PARTY_TYPE_ID");  
    String productId = request.getParameter("PRICE_COMPONENT_PRODUCT_ID");  
    String productFeatureId = request.getParameter("PRICE_COMPONENT_PRODUCT_FEATURE_ID");  
    String productCategoryId = request.getParameter("PRICE_COMPONENT_PRODUCT_CATEGORY_ID");  
    String agreementId = request.getParameter("PRICE_COMPONENT_AGREEMENT_ID");  
    String agreementItemSeqId = request.getParameter("PRICE_COMPONENT_AGREEMENT_ITEM_SEQ_ID");  
    String uomId = request.getParameter("PRICE_COMPONENT_UOM_ID");  
    String geoId = request.getParameter("PRICE_COMPONENT_GEO_ID");  
    String saleTypeId = request.getParameter("PRICE_COMPONENT_SALE_TYPE_ID");  
    String orderValueBreakId = request.getParameter("PRICE_COMPONENT_ORDER_VALUE_BREAK_ID");  
    String quantityBreakId = request.getParameter("PRICE_COMPONENT_QUANTITY_BREAK_ID");  
    String utilizationUomId = request.getParameter("PRICE_COMPONENT_UTILIZATION_UOM_ID");  
    String utilizationQuantityString = request.getParameter("PRICE_COMPONENT_UTILIZATION_QUANTITY");  
    String fromDateDate = request.getParameter("PRICE_COMPONENT_FROM_DATE_DATE");
    String fromDateTime = request.getParameter("PRICE_COMPONENT_FROM_DATE_TIME");  
    String thruDateDate = request.getParameter("PRICE_COMPONENT_THRU_DATE_DATE");
    String thruDateTime = request.getParameter("PRICE_COMPONENT_THRU_DATE_TIME");  
    String priceString = request.getParameter("PRICE_COMPONENT_PRICE");  
    String percentString = request.getParameter("PRICE_COMPONENT_PERCENT");  
    String comment = request.getParameter("PRICE_COMPONENT_COMMENT");  

  
    Double utilizationQuantity = null;
    try
    {
      if(utilizationQuantityString != null && utilizationQuantityString.length() > 0)
      { 
        utilizationQuantity = Double.valueOf(utilizationQuantityString);
      }
    }
    catch(Exception e)
    {
      errMsg = errMsg + "<li>UTILIZATION_QUANTITY conversion failed: \"" + utilizationQuantityString + "\" is not a valid Double";
    }
    java.util.Date fromDate = UtilDateTime.toDate(fromDateDate, fromDateTime);
    if(!UtilValidate.isDate(fromDateDate)) errMsg = errMsg + "<li>FROM_DATE isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(fromDateTime)) errMsg = errMsg + "<li>FROM_DATE isTime failed: " + UtilValidate.isTimeMsg;
    java.util.Date thruDate = UtilDateTime.toDate(thruDateDate, thruDateTime);
    if(!UtilValidate.isDate(thruDateDate)) errMsg = errMsg + "<li>THRU_DATE isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(thruDateTime)) errMsg = errMsg + "<li>THRU_DATE isTime failed: " + UtilValidate.isTimeMsg;
    Double price = null;
    try
    {
      if(priceString != null && priceString.length() > 0)
      { 
        price = Double.valueOf(priceString);
      }
    }
    catch(Exception e)
    {
      errMsg = errMsg + "<li>PRICE conversion failed: \"" + priceString + "\" is not a valid Double";
    }
    Double percent = null;
    try
    {
      if(percentString != null && percentString.length() > 0)
      { 
        percent = Double.valueOf(percentString);
      }
    }
    catch(Exception e)
    {
      errMsg = errMsg + "<li>PERCENT conversion failed: \"" + percentString + "\" is not a valid Double";
    }

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(PriceComponentHelper.findByPrimaryKey(priceComponentId) != null) errMsg = errMsg + "<li>PriceComponent already exists with PRICE_COMPONENT_ID:" + priceComponentId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(priceComponentId)) errMsg = errMsg + "<li>PRICE_COMPONENT_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isSignedDouble(utilizationQuantityString)) errMsg = errMsg + "<li>UTILIZATION_QUANTITY isSignedDouble failed: " + UtilValidate.isSignedDoubleMsg;
    if(!UtilValidate.isSignedDouble(priceString)) errMsg = errMsg + "<li>PRICE isSignedDouble failed: " + UtilValidate.isSignedDoubleMsg;
    if(!UtilValidate.isSignedDouble(percentString)) errMsg = errMsg + "<li>PERCENT isSignedDouble failed: " + UtilValidate.isSignedDoubleMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }

    if(updateMode.equals("CREATE"))
    {
      PriceComponent priceComponent = PriceComponentHelper.create(priceComponentId, priceComponentTypeId, partyId, partyTypeId, productId, productFeatureId, productCategoryId, agreementId, agreementItemSeqId, uomId, geoId, saleTypeId, orderValueBreakId, quantityBreakId, utilizationUomId, utilizationQuantity, fromDate, thruDate, price, percent, comment);
      if(priceComponent == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of PriceComponent failed. PRICE_COMPONENT_ID: " + priceComponentId);
        return "success";
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      PriceComponent priceComponent = PriceComponentHelper.update(priceComponentId, priceComponentTypeId, partyId, partyTypeId, productId, productFeatureId, productCategoryId, agreementId, agreementItemSeqId, uomId, geoId, saleTypeId, orderValueBreakId, quantityBreakId, utilizationUomId, utilizationQuantity, fromDate, thruDate, price, percent, comment);
      if(priceComponent == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of PriceComponent failed. PRICE_COMPONENT_ID: " + priceComponentId);
        return "success";
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePriceComponent: Update Mode specified (" + updateMode + ") was not valid.");
      Debug.logWarning("updatePriceComponent: Update Mode specified (" + updateMode + ") was not valid.");
    }

    return "success";
  }
}
