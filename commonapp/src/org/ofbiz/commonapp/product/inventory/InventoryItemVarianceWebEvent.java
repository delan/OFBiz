
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.math.*;
import org.ofbiz.commonapp.security.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Inventory Item Variance Entity
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
 *@created    Fri Jul 27 01:18:31 MDT 2001
 *@version    1.0
 */

public class InventoryItemVarianceWebEvent
{
  /** An HTTP WebEvent handler that updates a InventoryItemVariance entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Returns a String specifying the outcome state of the event. This is used to decide which event to run next or which view to display. If null no event is run nor view displayed, allowing the event to call a forward on a RequestDispatcher.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static String updateInventoryItemVariance(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return "success";
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return "success";    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateInventoryItemVariance: Update Mode was not specified, but is required.");
      Debug.logWarning("updateInventoryItemVariance: Update Mode was not specified, but is required.");
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " InventoryItemVariance (INVENTORY_ITEM_VARIANCE_" + updateMode + " or INVENTORY_ITEM_VARIANCE_ADMIN needed).");
      return "success";
    }

    //get the primary key parameters...
  
    String inventoryItemId = request.getParameter("INVENTORY_ITEM_VARIANCE_INVENTORY_ITEM_ID");  
    String physicalInventoryId = request.getParameter("INVENTORY_ITEM_VARIANCE_PHYSICAL_INVENTORY_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual InventoryItemVariance last, just in case database is set up to do a cascading delete, caches won't get cleared
      InventoryItemVarianceHelper.removeByPrimaryKey(inventoryItemId, physicalInventoryId);
      return "success";
    }

    //get the non-primary key parameters
  
    String varianceReasonId = request.getParameter("INVENTORY_ITEM_VARIANCE_VARIANCE_REASON_ID");  
    String quantityString = request.getParameter("INVENTORY_ITEM_VARIANCE_QUANTITY");  
    String comment = request.getParameter("INVENTORY_ITEM_VARIANCE_COMMENT");  

  
    Double quantity = null;
    try
    {
      if(quantityString != null && quantityString.length() > 0)
      { 
        quantity = Double.valueOf(quantityString);
      }
    }
    catch(Exception e)
    {
      errMsg = errMsg + "<li>QUANTITY conversion failed: \"" + quantityString + "\" is not a valid Double";
    }

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(InventoryItemVarianceHelper.findByPrimaryKey(inventoryItemId, physicalInventoryId) != null) errMsg = errMsg + "<li>InventoryItemVariance already exists with INVENTORY_ITEM_ID, PHYSICAL_INVENTORY_ID:" + inventoryItemId + ", " + physicalInventoryId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(inventoryItemId)) errMsg = errMsg + "<li>INVENTORY_ITEM_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isNotEmpty(physicalInventoryId)) errMsg = errMsg + "<li>PHYSICAL_INVENTORY_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isSignedDouble(quantityString)) errMsg = errMsg + "<li>QUANTITY isSignedDouble failed: " + UtilValidate.isSignedDoubleMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }

    if(updateMode.equals("CREATE"))
    {
      InventoryItemVariance inventoryItemVariance = InventoryItemVarianceHelper.create(inventoryItemId, physicalInventoryId, varianceReasonId, quantity, comment);
      if(inventoryItemVariance == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of InventoryItemVariance failed. INVENTORY_ITEM_ID, PHYSICAL_INVENTORY_ID: " + inventoryItemId + ", " + physicalInventoryId);
        return "success";
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      InventoryItemVariance inventoryItemVariance = InventoryItemVarianceHelper.update(inventoryItemId, physicalInventoryId, varianceReasonId, quantity, comment);
      if(inventoryItemVariance == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of InventoryItemVariance failed. INVENTORY_ITEM_ID, PHYSICAL_INVENTORY_ID: " + inventoryItemId + ", " + physicalInventoryId);
        return "success";
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateInventoryItemVariance: Update Mode specified (" + updateMode + ") was not valid.");
      Debug.logWarning("updateInventoryItemVariance: Update Mode specified (" + updateMode + ") was not valid.");
    }

    return "success";
  }
}
