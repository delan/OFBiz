
package org.ofbiz.commonapp.product.supplier;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.math.*;
import org.ofbiz.commonapp.security.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Reorder Guideline Entity
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

public class ReorderGuidelineWebEvent
{
  /** An HTTP WebEvent handler that updates a ReorderGuideline entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Returns a String specifying the outcome state of the event. This is used to decide which event to run next or which view to display. If null no event is run nor view displayed, allowing the event to call a forward on a RequestDispatcher.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static String updateReorderGuideline(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return "success";
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return "success";    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateReorderGuideline: Update Mode was not specified, but is required.");
      Debug.logWarning("updateReorderGuideline: Update Mode was not specified, but is required.");
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("REORDER_GUIDELINE", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " ReorderGuideline (REORDER_GUIDELINE_" + updateMode + " or REORDER_GUIDELINE_ADMIN needed).");
      return "success";
    }

    //get the primary key parameters...
  
    String reorderGuidelineId = request.getParameter("REORDER_GUIDELINE_REORDER_GUIDELINE_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual ReorderGuideline last, just in case database is set up to do a cascading delete, caches won't get cleared
      ReorderGuidelineHelper.removeByPrimaryKey(reorderGuidelineId);
      return "success";
    }

    //get the non-primary key parameters
  
    String productId = request.getParameter("REORDER_GUIDELINE_PRODUCT_ID");  
    String partyId = request.getParameter("REORDER_GUIDELINE_PARTY_ID");  
    String roleTypeId = request.getParameter("REORDER_GUIDELINE_ROLE_TYPE_ID");  
    String facilityId = request.getParameter("REORDER_GUIDELINE_FACILITY_ID");  
    String geoId = request.getParameter("REORDER_GUIDELINE_GEO_ID");  
    String fromDateDate = request.getParameter("REORDER_GUIDELINE_FROM_DATE_DATE");
    String fromDateTime = request.getParameter("REORDER_GUIDELINE_FROM_DATE_TIME");  
    String thruDateDate = request.getParameter("REORDER_GUIDELINE_THRU_DATE_DATE");
    String thruDateTime = request.getParameter("REORDER_GUIDELINE_THRU_DATE_TIME");  
    String reorderQuantityString = request.getParameter("REORDER_GUIDELINE_REORDER_QUANTITY");  
    String reorderLevelString = request.getParameter("REORDER_GUIDELINE_REORDER_LEVEL");  

  
    java.util.Date fromDate = UtilDateTime.toDate(fromDateDate, fromDateTime);
    if(!UtilValidate.isDate(fromDateDate)) errMsg = errMsg + "<li>FROM_DATE isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(fromDateTime)) errMsg = errMsg + "<li>FROM_DATE isTime failed: " + UtilValidate.isTimeMsg;
    java.util.Date thruDate = UtilDateTime.toDate(thruDateDate, thruDateTime);
    if(!UtilValidate.isDate(thruDateDate)) errMsg = errMsg + "<li>THRU_DATE isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(thruDateTime)) errMsg = errMsg + "<li>THRU_DATE isTime failed: " + UtilValidate.isTimeMsg;
    Double reorderQuantity = null;
    try
    {
      if(reorderQuantityString != null && reorderQuantityString.length() > 0)
      { 
        reorderQuantity = Double.valueOf(reorderQuantityString);
      }
    }
    catch(Exception e)
    {
      errMsg = errMsg + "<li>REORDER_QUANTITY conversion failed: \"" + reorderQuantityString + "\" is not a valid Double";
    }
    Double reorderLevel = null;
    try
    {
      if(reorderLevelString != null && reorderLevelString.length() > 0)
      { 
        reorderLevel = Double.valueOf(reorderLevelString);
      }
    }
    catch(Exception e)
    {
      errMsg = errMsg + "<li>REORDER_LEVEL conversion failed: \"" + reorderLevelString + "\" is not a valid Double";
    }

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(ReorderGuidelineHelper.findByPrimaryKey(reorderGuidelineId) != null) errMsg = errMsg + "<li>ReorderGuideline already exists with REORDER_GUIDELINE_ID:" + reorderGuidelineId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(reorderGuidelineId)) errMsg = errMsg + "<li>REORDER_GUIDELINE_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isNotEmpty(productId)) errMsg = errMsg + "<li>PRODUCT_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isSignedDouble(reorderQuantityString)) errMsg = errMsg + "<li>REORDER_QUANTITY isSignedDouble failed: " + UtilValidate.isSignedDoubleMsg;
    if(!UtilValidate.isSignedDouble(reorderLevelString)) errMsg = errMsg + "<li>REORDER_LEVEL isSignedDouble failed: " + UtilValidate.isSignedDoubleMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }

    if(updateMode.equals("CREATE"))
    {
      ReorderGuideline reorderGuideline = ReorderGuidelineHelper.create(reorderGuidelineId, productId, partyId, roleTypeId, facilityId, geoId, fromDate, thruDate, reorderQuantity, reorderLevel);
      if(reorderGuideline == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of ReorderGuideline failed. REORDER_GUIDELINE_ID: " + reorderGuidelineId);
        return "success";
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      ReorderGuideline reorderGuideline = ReorderGuidelineHelper.update(reorderGuidelineId, productId, partyId, roleTypeId, facilityId, geoId, fromDate, thruDate, reorderQuantity, reorderLevel);
      if(reorderGuideline == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of ReorderGuideline failed. REORDER_GUIDELINE_ID: " + reorderGuidelineId);
        return "success";
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateReorderGuideline: Update Mode specified (" + updateMode + ") was not valid.");
      Debug.logWarning("updateReorderGuideline: Update Mode specified (" + updateMode + ") was not valid.");
    }

    return "success";
  }
}
