
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.math.*;
import org.ofbiz.commonapp.security.*;
import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Party Entity
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
 *@created    Tue Jul 03 01:11:42 MDT 2001
 *@version    1.0
 */

public class PartyWebEvent
{
  /**
   *  An HTTP WebEvent handler that updates a Party entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static boolean updateParty(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return true;
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return true;    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateParty: Update Mode was not specified, but is required.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updateParty: Update Mode was not specified, but is required.");
      }
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("PARTY", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " Party (PARTY_" + updateMode + " or PARTY_ADMIN needed).");
      return true;
    }

    //get the primary key parameters...
  
    String partyId = request.getParameter("PARTY_PARTY_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual Party last, just in case database is set up to do a cascading delete, caches won't get cleared
      PartyHelper.removeByPrimaryKey(partyId);
      return true;
    }

    //get the non-primary key parameters
  

  

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(PartyHelper.findByPrimaryKey(partyId) != null) errMsg = errMsg + "<li>Party already exists with PARTY_ID:" + partyId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(partyId)) errMsg = errMsg + "<li>PARTY_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      //note that it is much easier to do a RequestDispatcher.forward here instead of a respones.sendRedirect because the sendRedirent will not automatically keep the Parameters...
      RequestDispatcher rd;
      String onErrorPage = request.getParameter("ON_ERROR_PAGE");
      if(onErrorPage != null) rd = request.getRequestDispatcher(onErrorPage);
      else rd = request.getRequestDispatcher("/commonapp/party/party/EditParty.jsp");
      rd.forward(request, response);
      return false;
    }

    if(updateMode.equals("CREATE"))
    {
      Party party = PartyHelper.create(partyId);
      if(party == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of Party failed. PARTY_ID: " + partyId);
        return true;
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      Party party = PartyHelper.update(partyId);
      if(party == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of Party failed. PARTY_ID: " + partyId);
        return true;
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updateParty: Update Mode specified (" + updateMode + ") was not valid.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updateParty: Update Mode specified (" + updateMode + ") was not valid.");
      }
    }

    return true;
  }
}
