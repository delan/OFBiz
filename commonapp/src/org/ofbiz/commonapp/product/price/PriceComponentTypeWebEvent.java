
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.math.*;
import org.ofbiz.commonapp.security.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Price Component Type Entity
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

public class PriceComponentTypeWebEvent
{
  /** An HTTP WebEvent handler that updates a PriceComponentType entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Returns a String specifying the outcome state of the event. This is used to decide which event to run next or which view to display. If null no event is run nor view displayed, allowing the event to call a forward on a RequestDispatcher.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static String updatePriceComponentType(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return "success";
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return "success";    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePriceComponentType: Update Mode was not specified, but is required.");
      Debug.logWarning("updatePriceComponentType: Update Mode was not specified, but is required.");
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("PRICE_COMPONENT_TYPE", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " PriceComponentType (PRICE_COMPONENT_TYPE_" + updateMode + " or PRICE_COMPONENT_TYPE_ADMIN needed).");
      return "success";
    }

    //get the primary key parameters...
  
    String priceComponentTypeId = request.getParameter("PRICE_COMPONENT_TYPE_PRICE_COMPONENT_TYPE_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual PriceComponentType last, just in case database is set up to do a cascading delete, caches won't get cleared
      PriceComponentTypeHelper.removeByPrimaryKey(priceComponentTypeId);
      return "success";
    }

    //get the non-primary key parameters
  
    String parentTypeId = request.getParameter("PRICE_COMPONENT_TYPE_PARENT_TYPE_ID");  
    String hasTable = request.getParameter("PRICE_COMPONENT_TYPE_HAS_TABLE");  
    String description = request.getParameter("PRICE_COMPONENT_TYPE_DESCRIPTION");  

  

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(PriceComponentTypeHelper.findByPrimaryKey(priceComponentTypeId) != null) errMsg = errMsg + "<li>PriceComponentType already exists with PRICE_COMPONENT_TYPE_ID:" + priceComponentTypeId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(priceComponentTypeId)) errMsg = errMsg + "<li>PRICE_COMPONENT_TYPE_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isNotEmpty(parentTypeId)) errMsg = errMsg + "<li>PARENT_TYPE_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }

    if(updateMode.equals("CREATE"))
    {
      PriceComponentType priceComponentType = PriceComponentTypeHelper.create(priceComponentTypeId, parentTypeId, hasTable, description);
      if(priceComponentType == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of PriceComponentType failed. PRICE_COMPONENT_TYPE_ID: " + priceComponentTypeId);
        return "success";
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      PriceComponentType priceComponentType = PriceComponentTypeHelper.update(priceComponentTypeId, parentTypeId, hasTable, description);
      if(priceComponentType == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of PriceComponentType failed. PRICE_COMPONENT_TYPE_ID: " + priceComponentTypeId);
        return "success";
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePriceComponentType: Update Mode specified (" + updateMode + ") was not valid.");
      Debug.logWarning("updatePriceComponentType: Update Mode specified (" + updateMode + ") was not valid.");
    }

    return "success";
  }
}
