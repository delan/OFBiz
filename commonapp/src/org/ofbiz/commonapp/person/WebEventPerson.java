package org.ofbiz.commonapp.person;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.math.*;
import org.ofbiz.commonapp.security.*;
import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Person Component - Person WebEvent
 * <p><b>Description:</b> Class containing WebEvent handlers for the Person module.
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
 *@created    Sat May 19 19:06:23 MDT 2001
 *@version    1.0
 */

public class WebEventPerson
{
  /**
   *  An HTTP WebEvent handler that updates a Person entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static boolean updatePerson(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return true;
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return true;    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePerson: Update Mode was not specified, but is required.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updatePerson: Update Mode was not specified, but is required.");
      }
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("PERSON", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " Person (PERSON_" + updateMode + " or PERSON_ADMIN needed).");
      return true;
    }

    //get the primary key parameters...
  
    String username = request.getParameter("PERSON_USERNAME");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual Person last, just in case database is set up to do a cascading delete, caches won't get cleared
      PersonHelper.removeByPrimaryKey(username);
      return true;
    }

    //get the non-primary key parameters
  
    String password = request.getParameter("PERSON_PASSWORD");  
    String firstName = request.getParameter("PERSON_FIRST_NAME");  
    String middleName = request.getParameter("PERSON_MIDDLE_NAME");  
    String lastName = request.getParameter("PERSON_LAST_NAME");  
    String title = request.getParameter("PERSON_TITLE");  
    String suffix = request.getParameter("PERSON_SUFFIX");  
    String homePhone = request.getParameter("PERSON_HOME_PHONE");  
    String workPhone = request.getParameter("PERSON_WORK_PHONE");  
    String fax = request.getParameter("PERSON_FAX");  
    String email = request.getParameter("PERSON_EMAIL");  
    String homeStreet1 = request.getParameter("PERSON_HOME_STREET1");  
    String homeStreet2 = request.getParameter("PERSON_HOME_STREET2");  
    String homeCity = request.getParameter("PERSON_HOME_CITY");  
    String homeCounty = request.getParameter("PERSON_HOME_COUNTY");  
    String homeState = request.getParameter("PERSON_HOME_STATE");  
    String homeCountry = request.getParameter("PERSON_HOME_COUNTRY");  
    String homePostalCode = request.getParameter("PERSON_HOME_POSTAL_CODE");  

  

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(PersonHelper.findByPrimaryKey(username) != null) errMsg = errMsg + "<li>Person already exists with USERNAME:" + username + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(username)) errMsg = errMsg + "<li>USERNAME isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isNotEmpty(password)) errMsg = errMsg + "<li>PASSWORD isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isNotEmpty(firstName)) errMsg = errMsg + "<li>FIRST_NAME isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isNotEmpty(lastName)) errMsg = errMsg + "<li>LAST_NAME isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isInternationalPhoneNumber(homePhone)) errMsg = errMsg + "<li>HOME_PHONE isInternationalPhoneNumber failed: " + UtilValidate.isInternationalPhoneNumberMsg;
    if(!UtilValidate.isInternationalPhoneNumber(workPhone)) errMsg = errMsg + "<li>WORK_PHONE isInternationalPhoneNumber failed: " + UtilValidate.isInternationalPhoneNumberMsg;
    if(!UtilValidate.isInternationalPhoneNumber(fax)) errMsg = errMsg + "<li>FAX isInternationalPhoneNumber failed: " + UtilValidate.isInternationalPhoneNumberMsg;
    if(!UtilValidate.isEmail(email)) errMsg = errMsg + "<li>EMAIL isEmail failed: " + UtilValidate.isEmailMsg;
    if(!UtilValidate.isNotEmpty(email)) errMsg = errMsg + "<li>EMAIL isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      //note that it is much easier to do a RequestDispatcher.forward here instead of a respones.sendRedirect because the sendRedirent will not automatically keep the Parameters...
      RequestDispatcher rd;
      String onErrorPage = request.getParameter("ON_ERROR_PAGE");
      if(onErrorPage != null) rd = request.getRequestDispatcher(onErrorPage);
      else rd = request.getRequestDispatcher("/______InsertEditEntityPathNameHERE______/EditPerson.jsp");
      rd.forward(request, response);
      return false;
    }

    if(updateMode.equals("CREATE"))
    {
      Person person = PersonHelper.create(username, password, firstName, middleName, lastName, title, suffix, homePhone, workPhone, fax, email, homeStreet1, homeStreet2, homeCity, homeCounty, homeState, homeCountry, homePostalCode);
      if(person == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of Person failed. USERNAME: " + username);
        return true;
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      Person person = PersonHelper.update(username, password, firstName, middleName, lastName, title, suffix, homePhone, workPhone, fax, email, homeStreet1, homeStreet2, homeCity, homeCounty, homeState, homeCountry, homePostalCode);
      if(person == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of Person failed. USERNAME: " + username);
        return true;
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePerson: Update Mode specified (" + updateMode + ") was not valid.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updatePerson: Update Mode specified (" + updateMode + ") was not valid.");
      }
    }

    return true;
  }

  /**
   *  An HTTP WebEvent handler that updates a PersonAttribute entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static boolean updatePersonAttribute(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return true;
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return true;    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePersonAttribute: Update Mode was not specified, but is required.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updatePersonAttribute: Update Mode was not specified, but is required.");
      }
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("PERSON_ATTRIBUTE", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " PersonAttribute (PERSON_ATTRIBUTE_" + updateMode + " or PERSON_ATTRIBUTE_ADMIN needed).");
      return true;
    }

    //get the primary key parameters...
  
    String username = request.getParameter("PERSON_ATTRIBUTE_USERNAME");  
    String name = request.getParameter("PERSON_ATTRIBUTE_NAME");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual PersonAttribute last, just in case database is set up to do a cascading delete, caches won't get cleared
      PersonAttributeHelper.removeByPrimaryKey(username, name);
      return true;
    }

    //get the non-primary key parameters
  
    String value = request.getParameter("PERSON_ATTRIBUTE_VALUE");  

  

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(PersonAttributeHelper.findByPrimaryKey(username, name) != null) errMsg = errMsg + "<li>PersonAttribute already exists with USERNAME, NAME:" + username + ", " + name + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(username)) errMsg = errMsg + "<li>USERNAME isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isNotEmpty(name)) errMsg = errMsg + "<li>NAME isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      //note that it is much easier to do a RequestDispatcher.forward here instead of a respones.sendRedirect because the sendRedirent will not automatically keep the Parameters...
      RequestDispatcher rd;
      String onErrorPage = request.getParameter("ON_ERROR_PAGE");
      if(onErrorPage != null) rd = request.getRequestDispatcher(onErrorPage);
      else rd = request.getRequestDispatcher("/______InsertEditEntityPathNameHERE______/EditPersonAttribute.jsp");
      rd.forward(request, response);
      return false;
    }

    if(updateMode.equals("CREATE"))
    {
      PersonAttribute personAttribute = PersonAttributeHelper.create(username, name, value);
      if(personAttribute == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of PersonAttribute failed. USERNAME, NAME: " + username + ", " + name);
        return true;
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      PersonAttribute personAttribute = PersonAttributeHelper.update(username, name, value);
      if(personAttribute == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of PersonAttribute failed. USERNAME, NAME: " + username + ", " + name);
        return true;
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePersonAttribute: Update Mode specified (" + updateMode + ") was not valid.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updatePersonAttribute: Update Mode specified (" + updateMode + ") was not valid.");
      }
    }

    return true;
  }

  /**
   *  An HTTP WebEvent handler that updates a PersonType entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static boolean updatePersonType(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return true;
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return true;    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePersonType: Update Mode was not specified, but is required.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updatePersonType: Update Mode was not specified, but is required.");
      }
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("PERSON_TYPE", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " PersonType (PERSON_TYPE_" + updateMode + " or PERSON_TYPE_ADMIN needed).");
      return true;
    }

    //get the primary key parameters...
  
    String typeId = request.getParameter("PERSON_TYPE_TYPE_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual PersonType last, just in case database is set up to do a cascading delete, caches won't get cleared
      PersonTypeHelper.removeByPrimaryKey(typeId);
      return true;
    }

    //get the non-primary key parameters
  
    String description = request.getParameter("PERSON_TYPE_DESCRIPTION");  

  

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(PersonTypeHelper.findByPrimaryKey(typeId) != null) errMsg = errMsg + "<li>PersonType already exists with TYPE_ID:" + typeId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(typeId)) errMsg = errMsg + "<li>TYPE_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      //note that it is much easier to do a RequestDispatcher.forward here instead of a respones.sendRedirect because the sendRedirent will not automatically keep the Parameters...
      RequestDispatcher rd;
      String onErrorPage = request.getParameter("ON_ERROR_PAGE");
      if(onErrorPage != null) rd = request.getRequestDispatcher(onErrorPage);
      else rd = request.getRequestDispatcher("/______InsertEditEntityPathNameHERE______/EditPersonType.jsp");
      rd.forward(request, response);
      return false;
    }

    if(updateMode.equals("CREATE"))
    {
      PersonType personType = PersonTypeHelper.create(typeId, description);
      if(personType == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of PersonType failed. TYPE_ID: " + typeId);
        return true;
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      PersonType personType = PersonTypeHelper.update(typeId, description);
      if(personType == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of PersonType failed. TYPE_ID: " + typeId);
        return true;
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePersonType: Update Mode specified (" + updateMode + ") was not valid.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updatePersonType: Update Mode specified (" + updateMode + ") was not valid.");
      }
    }

    return true;
  }

  /**
   *  An HTTP WebEvent handler that updates a PersonTypeAttribute entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static boolean updatePersonTypeAttribute(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return true;
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return true;    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePersonTypeAttribute: Update Mode was not specified, but is required.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updatePersonTypeAttribute: Update Mode was not specified, but is required.");
      }
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("PERSON_TYPE_ATTRIBUTE", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " PersonTypeAttribute (PERSON_TYPE_ATTRIBUTE_" + updateMode + " or PERSON_TYPE_ATTRIBUTE_ADMIN needed).");
      return true;
    }

    //get the primary key parameters...
  
    String typeId = request.getParameter("PERSON_TYPE_ATTRIBUTE_TYPE_ID");  
    String name = request.getParameter("PERSON_TYPE_ATTRIBUTE_NAME");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual PersonTypeAttribute last, just in case database is set up to do a cascading delete, caches won't get cleared
      PersonTypeAttributeHelper.removeByPrimaryKey(typeId, name);
      return true;
    }

    //get the non-primary key parameters
  

  

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(PersonTypeAttributeHelper.findByPrimaryKey(typeId, name) != null) errMsg = errMsg + "<li>PersonTypeAttribute already exists with TYPE_ID, NAME:" + typeId + ", " + name + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(typeId)) errMsg = errMsg + "<li>TYPE_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isNotEmpty(name)) errMsg = errMsg + "<li>NAME isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      //note that it is much easier to do a RequestDispatcher.forward here instead of a respones.sendRedirect because the sendRedirent will not automatically keep the Parameters...
      RequestDispatcher rd;
      String onErrorPage = request.getParameter("ON_ERROR_PAGE");
      if(onErrorPage != null) rd = request.getRequestDispatcher(onErrorPage);
      else rd = request.getRequestDispatcher("/______InsertEditEntityPathNameHERE______/EditPersonTypeAttribute.jsp");
      rd.forward(request, response);
      return false;
    }

    if(updateMode.equals("CREATE"))
    {
      PersonTypeAttribute personTypeAttribute = PersonTypeAttributeHelper.create(typeId, name);
      if(personTypeAttribute == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of PersonTypeAttribute failed. TYPE_ID, NAME: " + typeId + ", " + name);
        return true;
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      PersonTypeAttribute personTypeAttribute = PersonTypeAttributeHelper.update(typeId, name);
      if(personTypeAttribute == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of PersonTypeAttribute failed. TYPE_ID, NAME: " + typeId + ", " + name);
        return true;
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePersonTypeAttribute: Update Mode specified (" + updateMode + ") was not valid.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updatePersonTypeAttribute: Update Mode specified (" + updateMode + ") was not valid.");
      }
    }

    return true;
  }

  /**
   *  An HTTP WebEvent handler that updates a PersonPersonType entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static boolean updatePersonPersonType(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return true;
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return true;    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePersonPersonType: Update Mode was not specified, but is required.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updatePersonPersonType: Update Mode was not specified, but is required.");
      }
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("PERSON_PERSON_TYPE", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " PersonPersonType (PERSON_PERSON_TYPE_" + updateMode + " or PERSON_PERSON_TYPE_ADMIN needed).");
      return true;
    }

    //get the primary key parameters...
  
    String username = request.getParameter("PERSON_PERSON_TYPE_USERNAME");  
    String typeId = request.getParameter("PERSON_PERSON_TYPE_TYPE_ID");  

  

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual PersonPersonType last, just in case database is set up to do a cascading delete, caches won't get cleared
      PersonPersonTypeHelper.removeByPrimaryKey(username, typeId);
      return true;
    }

    //get the non-primary key parameters
  

  

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(PersonPersonTypeHelper.findByPrimaryKey(username, typeId) != null) errMsg = errMsg + "<li>PersonPersonType already exists with USERNAME, TYPE_ID:" + username + ", " + typeId + "; please change.";

    //Validate parameters...
  
    if(!UtilValidate.isNotEmpty(username)) errMsg = errMsg + "<li>USERNAME isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;
    if(!UtilValidate.isNotEmpty(typeId)) errMsg = errMsg + "<li>TYPE_ID isNotEmpty failed: " + UtilValidate.isNotEmptyMsg;

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      //note that it is much easier to do a RequestDispatcher.forward here instead of a respones.sendRedirect because the sendRedirent will not automatically keep the Parameters...
      RequestDispatcher rd;
      String onErrorPage = request.getParameter("ON_ERROR_PAGE");
      if(onErrorPage != null) rd = request.getRequestDispatcher(onErrorPage);
      else rd = request.getRequestDispatcher("/______InsertEditEntityPathNameHERE______/EditPersonPersonType.jsp");
      rd.forward(request, response);
      return false;
    }

    if(updateMode.equals("CREATE"))
    {
      PersonPersonType personPersonType = PersonPersonTypeHelper.create(username, typeId);
      if(personPersonType == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of PersonPersonType failed. USERNAME, TYPE_ID: " + username + ", " + typeId);
        return true;
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      PersonPersonType personPersonType = PersonPersonTypeHelper.update(username, typeId);
      if(personPersonType == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of PersonPersonType failed. USERNAME, TYPE_ID: " + username + ", " + typeId);
        return true;
      }
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "updatePersonPersonType: Update Mode specified (" + updateMode + ") was not valid.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("updatePersonPersonType: Update Mode specified (" + updateMode + ") was not valid.");
      }
    }

    return true;
  }
}
