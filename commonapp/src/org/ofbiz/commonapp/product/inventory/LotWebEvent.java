
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.math.*;
import org.ofbiz.commonapp.security.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Lot Entity
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
 *@created    Fri Jul 27 01:18:32 MDT 2001
 *@version    1.0
 */

public class LotWebEvent
{
  /** An HTTP WebEvent handler that updates a Lot entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Returns a String specifying the outcome state of the event. This is used to decide which event to run next or which view to display. If null no event is run nor view displayed, allowing the event to call a forward on a RequestDispatcher.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static String updateLot(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return "success";
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return "success";    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateLot: Update Mode was not specified, but is required.");
      Debug.logWarning("updateLot: Update Mode was not specified, but is required.");
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("LOT", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " Lot (LOT_" + updateMode + " or LOT_ADMIN needed).");
      return "success";
    }

    //get the primary key parameters...
  
    String lotId = request.getParameter("LOT_LOT_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual Lot last, just in case database is set up to do a cascading delete, caches won't get cleared
      LotHelper.removeByPrimaryKey(lotId);
      return "success";
    }

    //get the non-primary key parameters
  
    String creationDateDate = request.getParameter("LOT_CREATION_DATE_DATE");
    String creationDateTime = request.getParameter("LOT_CREATION_DATE_TIME");  
    String quantityString = request.getParameter("LOT_QUANTITY");  
    String expirationDateDate = request.getParameter("LOT_EXPIRATION_DATE_DATE");
    String expirationDateTime = request.getParameter("LOT_EXPIRATION_DATE_TIME");  

  
    java.util.Date creationDate = UtilDateTime.toDate(creationDateDate, creationDateTime);
    if(!UtilValidate.isDate(creationDateDate)) errMsg = errMsg + "<li>CREATION_DATE isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(creationDateTime)) errMsg = errMsg + "<li>CREATION_DATE isTime failed: " + UtilValidate.isTimeMsg;
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
    java.util.Date expirationDate = UtilDateTime.toDate(expirationDateDate, expirationDateTime);
    if(!UtilValidate.isDate(expirationDateDate)) errMsg = errMsg + "<li>EXPIRATION_DATE isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(expirationDateTime)) errMsg = errMsg + "<li>EXPIRATION_DATE isTime failed: " + UtilValidate.isTimeMsg;

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(LotHelper.findByPrimaryKey(lotId) != null) errMsg = errMsg + "<li>Lot already exists with LOT_ID:" + lotId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(lotId)) errMsg = errMsg + "<li>LOT_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isSignedDouble(quantityString)) errMsg = errMsg + "<li>QUANTITY isSignedDouble failed: " + UtilValidate.isSignedDoubleMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }

    if(updateMode.equals("CREATE"))
    {
      Lot lot = LotHelper.create(lotId, creationDate, quantity, expirationDate);
      if(lot == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of Lot failed. LOT_ID: " + lotId);
        return "success";
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      Lot lot = LotHelper.update(lotId, creationDate, quantity, expirationDate);
      if(lot == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of Lot failed. LOT_ID: " + lotId);
        return "success";
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateLot: Update Mode specified (" + updateMode + ") was not valid.");
      Debug.logWarning("updateLot: Update Mode specified (" + updateMode + ") was not valid.");
    }

    return "success";
  }
}
