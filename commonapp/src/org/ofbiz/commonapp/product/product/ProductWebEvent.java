
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.math.*;
import org.ofbiz.commonapp.security.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Entity
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

public class ProductWebEvent
{
  /** An HTTP WebEvent handler that updates a Product entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Returns a String specifying the outcome state of the event. This is used to decide which event to run next or which view to display. If null no event is run nor view displayed, allowing the event to call a forward on a RequestDispatcher.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static String updateProduct(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return "success";
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return "success";    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateProduct: Update Mode was not specified, but is required.");
      Debug.logWarning("updateProduct: Update Mode was not specified, but is required.");
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("PRODUCT", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " Product (PRODUCT_" + updateMode + " or PRODUCT_ADMIN needed).");
      return "success";
    }

    //get the primary key parameters...
  
    String productId = request.getParameter("PRODUCT_PRODUCT_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual Product last, just in case database is set up to do a cascading delete, caches won't get cleared
      ProductHelper.removeByPrimaryKey(productId);
      return "success";
    }

    //get the non-primary key parameters
  
    String primaryProductCategoryId = request.getParameter("PRODUCT_PRIMARY_PRODUCT_CATEGORY_ID");  
    String manufacturerPartyId = request.getParameter("PRODUCT_MANUFACTURER_PARTY_ID");  
    String uomId = request.getParameter("PRODUCT_UOM_ID");  
    String quantityIncludedString = request.getParameter("PRODUCT_QUANTITY_INCLUDED");  
    String introductionDateDate = request.getParameter("PRODUCT_INTRODUCTION_DATE_DATE");
    String introductionDateTime = request.getParameter("PRODUCT_INTRODUCTION_DATE_TIME");  
    String salesDiscontinuationDateDate = request.getParameter("PRODUCT_SALES_DISCONTINUATION_DATE_DATE");
    String salesDiscontinuationDateTime = request.getParameter("PRODUCT_SALES_DISCONTINUATION_DATE_TIME");  
    String supportDiscontinuationDateDate = request.getParameter("PRODUCT_SUPPORT_DISCONTINUATION_DATE_DATE");
    String supportDiscontinuationDateTime = request.getParameter("PRODUCT_SUPPORT_DISCONTINUATION_DATE_TIME");  
    String name = request.getParameter("PRODUCT_NAME");  
    String comment = request.getParameter("PRODUCT_COMMENT");  
    String description = request.getParameter("PRODUCT_DESCRIPTION");  
    String longDescription = request.getParameter("PRODUCT_LONG_DESCRIPTION");  
    String smallImageUrl = request.getParameter("PRODUCT_SMALL_IMAGE_URL");  
    String largeImageUrl = request.getParameter("PRODUCT_LARGE_IMAGE_URL");  
    String defaultPriceString = request.getParameter("PRODUCT_DEFAULT_PRICE");  

  
    Double quantityIncluded = null;
    try
    {
      if(quantityIncludedString != null && quantityIncludedString.length() > 0)
      { 
        quantityIncluded = Double.valueOf(quantityIncludedString);
      }
    }
    catch(Exception e)
    {
      errMsg = errMsg + "<li>QUANTITY_INCLUDED conversion failed: \"" + quantityIncludedString + "\" is not a valid Double";
    }
    java.util.Date introductionDate = UtilDateTime.toDate(introductionDateDate, introductionDateTime);
    if(!UtilValidate.isDate(introductionDateDate)) errMsg = errMsg + "<li>INTRODUCTION_DATE isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(introductionDateTime)) errMsg = errMsg + "<li>INTRODUCTION_DATE isTime failed: " + UtilValidate.isTimeMsg;
    java.util.Date salesDiscontinuationDate = UtilDateTime.toDate(salesDiscontinuationDateDate, salesDiscontinuationDateTime);
    if(!UtilValidate.isDate(salesDiscontinuationDateDate)) errMsg = errMsg + "<li>SALES_DISCONTINUATION_DATE isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(salesDiscontinuationDateTime)) errMsg = errMsg + "<li>SALES_DISCONTINUATION_DATE isTime failed: " + UtilValidate.isTimeMsg;
    java.util.Date supportDiscontinuationDate = UtilDateTime.toDate(supportDiscontinuationDateDate, supportDiscontinuationDateTime);
    if(!UtilValidate.isDate(supportDiscontinuationDateDate)) errMsg = errMsg + "<li>SUPPORT_DISCONTINUATION_DATE isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(supportDiscontinuationDateTime)) errMsg = errMsg + "<li>SUPPORT_DISCONTINUATION_DATE isTime failed: " + UtilValidate.isTimeMsg;
    Double defaultPrice = null;
    try
    {
      if(defaultPriceString != null && defaultPriceString.length() > 0)
      { 
        defaultPrice = Double.valueOf(defaultPriceString);
      }
    }
    catch(Exception e)
    {
      errMsg = errMsg + "<li>DEFAULT_PRICE conversion failed: \"" + defaultPriceString + "\" is not a valid Double";
    }

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(ProductHelper.findByPrimaryKey(productId) != null) errMsg = errMsg + "<li>Product already exists with PRODUCT_ID:" + productId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(productId)) errMsg = errMsg + "<li>PRODUCT_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isNotEmpty(primaryProductCategoryId)) errMsg = errMsg + "<li>PRIMARY_PRODUCT_CATEGORY_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isSignedDouble(quantityIncludedString)) errMsg = errMsg + "<li>QUANTITY_INCLUDED isSignedDouble failed: " + UtilValidate.isSignedDoubleMsg;
    if(!UtilValidate.isSignedDouble(defaultPriceString)) errMsg = errMsg + "<li>DEFAULT_PRICE isSignedDouble failed: " + UtilValidate.isSignedDoubleMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }

    if(updateMode.equals("CREATE"))
    {
      Product product = ProductHelper.create(productId, primaryProductCategoryId, manufacturerPartyId, uomId, quantityIncluded, introductionDate, salesDiscontinuationDate, supportDiscontinuationDate, name, comment, description, longDescription, smallImageUrl, largeImageUrl, defaultPrice);
      if(product == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of Product failed. PRODUCT_ID: " + productId);
        return "success";
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      Product product = ProductHelper.update(productId, primaryProductCategoryId, manufacturerPartyId, uomId, quantityIncluded, introductionDate, salesDiscontinuationDate, supportDiscontinuationDate, name, comment, description, longDescription, smallImageUrl, largeImageUrl, defaultPrice);
      if(product == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of Product failed. PRODUCT_ID: " + productId);
        return "success";
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateProduct: Update Mode specified (" + updateMode + ") was not valid.");
      Debug.logWarning("updateProduct: Update Mode specified (" + updateMode + ") was not valid.");
    }

    return "success";
  }
}
