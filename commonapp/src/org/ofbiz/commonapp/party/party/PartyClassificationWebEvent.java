
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.math.*;
import org.ofbiz.commonapp.security.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Party Classification Entity
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
 *@created    Fri Jul 27 01:18:23 MDT 2001
 *@version    1.0
 */

public class PartyClassificationWebEvent
{
  /** An HTTP WebEvent handler that updates a PartyClassification entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Returns a String specifying the outcome state of the event. This is used to decide which event to run next or which view to display. If null no event is run nor view displayed, allowing the event to call a forward on a RequestDispatcher.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static String updatePartyClassification(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return "success";
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return "success";    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePartyClassification: Update Mode was not specified, but is required.");
      Debug.logWarning("updatePartyClassification: Update Mode was not specified, but is required.");
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("PARTY_CLASSIFICATION", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " PartyClassification (PARTY_CLASSIFICATION_" + updateMode + " or PARTY_CLASSIFICATION_ADMIN needed).");
      return "success";
    }

    //get the primary key parameters...
  
    String partyId = request.getParameter("PARTY_CLASSIFICATION_PARTY_ID");  
    String partyTypeId = request.getParameter("PARTY_CLASSIFICATION_PARTY_TYPE_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual PartyClassification last, just in case database is set up to do a cascading delete, caches won't get cleared
      PartyClassificationHelper.removeByPrimaryKey(partyId, partyTypeId);
      return "success";
    }

    //get the non-primary key parameters
  
    String partyClassificationTypeId = request.getParameter("PARTY_CLASSIFICATION_PARTY_CLASSIFICATION_TYPE_ID");  
    String fromDateDate = request.getParameter("PARTY_CLASSIFICATION_FROM_DATE_DATE");
    String fromDateTime = request.getParameter("PARTY_CLASSIFICATION_FROM_DATE_TIME");  
    String thruDateDate = request.getParameter("PARTY_CLASSIFICATION_THRU_DATE_DATE");
    String thruDateTime = request.getParameter("PARTY_CLASSIFICATION_THRU_DATE_TIME");  

  
    java.util.Date fromDate = UtilDateTime.toDate(fromDateDate, fromDateTime);
    if(!UtilValidate.isDate(fromDateDate)) errMsg = errMsg + "<li>FROM_DATE isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(fromDateTime)) errMsg = errMsg + "<li>FROM_DATE isTime failed: " + UtilValidate.isTimeMsg;
    java.util.Date thruDate = UtilDateTime.toDate(thruDateDate, thruDateTime);
    if(!UtilValidate.isDate(thruDateDate)) errMsg = errMsg + "<li>THRU_DATE isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(thruDateTime)) errMsg = errMsg + "<li>THRU_DATE isTime failed: " + UtilValidate.isTimeMsg;

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(PartyClassificationHelper.findByPrimaryKey(partyId, partyTypeId) != null) errMsg = errMsg + "<li>PartyClassification already exists with PARTY_ID, PARTY_TYPE_ID:" + partyId + ", " + partyTypeId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(partyId)) errMsg = errMsg + "<li>PARTY_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isNotEmpty(partyTypeId)) errMsg = errMsg + "<li>PARTY_TYPE_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }

    if(updateMode.equals("CREATE"))
    {
      PartyClassification partyClassification = PartyClassificationHelper.create(partyId, partyTypeId, partyClassificationTypeId, fromDate, thruDate);
      if(partyClassification == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of PartyClassification failed. PARTY_ID, PARTY_TYPE_ID: " + partyId + ", " + partyTypeId);
        return "success";
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      PartyClassification partyClassification = PartyClassificationHelper.update(partyId, partyTypeId, partyClassificationTypeId, fromDate, thruDate);
      if(partyClassification == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of PartyClassification failed. PARTY_ID, PARTY_TYPE_ID: " + partyId + ", " + partyTypeId);
        return "success";
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePartyClassification: Update Mode specified (" + updateMode + ") was not valid.");
      Debug.logWarning("updatePartyClassification: Update Mode specified (" + updateMode + ") was not valid.");
    }

    return "success";
  }
}
