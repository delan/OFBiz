package org.ofbiz.commonapp.person;

import java.rmi.*;
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
   * An HTTP WebEvent handler that updates a Person entity
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
    String updateMode = request.getParameter("UPDATE_MODE");


    String username = request.getParameter("PERSON_USERNAME");  
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


    if(updateMode.compareTo("CREATE") == 0)
    {
      if(!Security.hasEntityPermission("PERSON", "_CREATE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to create Person (PERSON_CREATE or PERSON_ADMIN needed).");
        return true;
      }

      Person person = PersonHelper.create(username, password, firstName, middleName, lastName, title, suffix, homePhone, workPhone, fax, email, homeStreet1, homeStreet2, homeCity, homeCounty, homeState, homeCountry, homePostalCode);
      if(person == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of Person failed. USERNAME: " + username);
        return true;
      }
    }
    else if(updateMode.compareTo("UPDATE") == 0)
    {
      if(!Security.hasEntityPermission("PERSON", "_UPDATE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to update Person (PERSON_UPDATE or PERSON_ADMIN needed).");
        return true;
      }

      Person person = PersonHelper.update(username, password, firstName, middleName, lastName, title, suffix, homePhone, workPhone, fax, email, homeStreet1, homeStreet2, homeCity, homeCounty, homeState, homeCountry, homePostalCode);
      if(person == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of Person failed. USERNAME: " + username);
        return true;
      }
    }
    else if(updateMode.compareTo("DELETE") == 0)
    {
      if(!Security.hasEntityPermission("PERSON", "_DELETE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to delete Person (PERSON_DELETE or PERSON_ADMIN needed).");
        return true;
      }

      //Remove associated/dependent entries from other tables here
      //Delete actual person last, just in case database is set up to do a cascading delete, caches won't get cleared
      PersonHelper.removeByPrimaryKey(username);
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
   * An HTTP WebEvent handler that updates a PersonAttribute entity
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
    String updateMode = request.getParameter("UPDATE_MODE");


    String username = request.getParameter("PERSON_ATTRIBUTE_USERNAME");  
    String name = request.getParameter("PERSON_ATTRIBUTE_NAME");  
    String value = request.getParameter("PERSON_ATTRIBUTE_VALUE");  


    if(updateMode.compareTo("CREATE") == 0)
    {
      if(!Security.hasEntityPermission("PERSON_ATTRIBUTE", "_CREATE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to create PersonAttribute (PERSON_ATTRIBUTE_CREATE or PERSON_ATTRIBUTE_ADMIN needed).");
        return true;
      }

      PersonAttribute personAttribute = PersonAttributeHelper.create(username, name, value);
      if(personAttribute == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of PersonAttribute failed. USERNAME, NAME: " + username + ", " + name);
        return true;
      }
    }
    else if(updateMode.compareTo("UPDATE") == 0)
    {
      if(!Security.hasEntityPermission("PERSON_ATTRIBUTE", "_UPDATE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to update PersonAttribute (PERSON_ATTRIBUTE_UPDATE or PERSON_ATTRIBUTE_ADMIN needed).");
        return true;
      }

      PersonAttribute personAttribute = PersonAttributeHelper.update(username, name, value);
      if(personAttribute == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of PersonAttribute failed. USERNAME, NAME: " + username + ", " + name);
        return true;
      }
    }
    else if(updateMode.compareTo("DELETE") == 0)
    {
      if(!Security.hasEntityPermission("PERSON_ATTRIBUTE", "_DELETE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to delete PersonAttribute (PERSON_ATTRIBUTE_DELETE or PERSON_ATTRIBUTE_ADMIN needed).");
        return true;
      }

      //Remove associated/dependent entries from other tables here
      //Delete actual personAttribute last, just in case database is set up to do a cascading delete, caches won't get cleared
      PersonAttributeHelper.removeByPrimaryKey(username, name);
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
   * An HTTP WebEvent handler that updates a PersonType entity
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
    String updateMode = request.getParameter("UPDATE_MODE");


    String typeId = request.getParameter("PERSON_TYPE_TYPE_ID");  
    String description = request.getParameter("PERSON_TYPE_DESCRIPTION");  


    if(updateMode.compareTo("CREATE") == 0)
    {
      if(!Security.hasEntityPermission("PERSON_TYPE", "_CREATE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to create PersonType (PERSON_TYPE_CREATE or PERSON_TYPE_ADMIN needed).");
        return true;
      }

      PersonType personType = PersonTypeHelper.create(typeId, description);
      if(personType == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of PersonType failed. TYPE_ID: " + typeId);
        return true;
      }
    }
    else if(updateMode.compareTo("UPDATE") == 0)
    {
      if(!Security.hasEntityPermission("PERSON_TYPE", "_UPDATE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to update PersonType (PERSON_TYPE_UPDATE or PERSON_TYPE_ADMIN needed).");
        return true;
      }

      PersonType personType = PersonTypeHelper.update(typeId, description);
      if(personType == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of PersonType failed. TYPE_ID: " + typeId);
        return true;
      }
    }
    else if(updateMode.compareTo("DELETE") == 0)
    {
      if(!Security.hasEntityPermission("PERSON_TYPE", "_DELETE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to delete PersonType (PERSON_TYPE_DELETE or PERSON_TYPE_ADMIN needed).");
        return true;
      }

      //Remove associated/dependent entries from other tables here
      //Delete actual personType last, just in case database is set up to do a cascading delete, caches won't get cleared
      PersonTypeHelper.removeByPrimaryKey(typeId);
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
   * An HTTP WebEvent handler that updates a PersonTypeAttribute entity
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
    String updateMode = request.getParameter("UPDATE_MODE");


    String typeId = request.getParameter("PERSON_TYPE_ATTRIBUTE_TYPE_ID");  
    String name = request.getParameter("PERSON_TYPE_ATTRIBUTE_NAME");  


    if(updateMode.compareTo("CREATE") == 0)
    {
      if(!Security.hasEntityPermission("PERSON_TYPE_ATTRIBUTE", "_CREATE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to create PersonTypeAttribute (PERSON_TYPE_ATTRIBUTE_CREATE or PERSON_TYPE_ATTRIBUTE_ADMIN needed).");
        return true;
      }

      PersonTypeAttribute personTypeAttribute = PersonTypeAttributeHelper.create(typeId, name);
      if(personTypeAttribute == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of PersonTypeAttribute failed. TYPE_ID, NAME: " + typeId + ", " + name);
        return true;
      }
    }
    else if(updateMode.compareTo("UPDATE") == 0)
    {
      if(!Security.hasEntityPermission("PERSON_TYPE_ATTRIBUTE", "_UPDATE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to update PersonTypeAttribute (PERSON_TYPE_ATTRIBUTE_UPDATE or PERSON_TYPE_ATTRIBUTE_ADMIN needed).");
        return true;
      }

      PersonTypeAttribute personTypeAttribute = PersonTypeAttributeHelper.update(typeId, name);
      if(personTypeAttribute == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of PersonTypeAttribute failed. TYPE_ID, NAME: " + typeId + ", " + name);
        return true;
      }
    }
    else if(updateMode.compareTo("DELETE") == 0)
    {
      if(!Security.hasEntityPermission("PERSON_TYPE_ATTRIBUTE", "_DELETE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to delete PersonTypeAttribute (PERSON_TYPE_ATTRIBUTE_DELETE or PERSON_TYPE_ATTRIBUTE_ADMIN needed).");
        return true;
      }

      //Remove associated/dependent entries from other tables here
      //Delete actual personTypeAttribute last, just in case database is set up to do a cascading delete, caches won't get cleared
      PersonTypeAttributeHelper.removeByPrimaryKey(typeId, name);
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
   * An HTTP WebEvent handler that updates a PersonPersonType entity
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
    String updateMode = request.getParameter("UPDATE_MODE");


    String username = request.getParameter("PERSON_PERSON_TYPE_USERNAME");  
    String typeId = request.getParameter("PERSON_PERSON_TYPE_TYPE_ID");  


    if(updateMode.compareTo("CREATE") == 0)
    {
      if(!Security.hasEntityPermission("PERSON_PERSON_TYPE", "_CREATE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to create PersonPersonType (PERSON_PERSON_TYPE_CREATE or PERSON_PERSON_TYPE_ADMIN needed).");
        return true;
      }

      PersonPersonType personPersonType = PersonPersonTypeHelper.create(username, typeId);
      if(personPersonType == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of PersonPersonType failed. USERNAME, TYPE_ID: " + username + ", " + typeId);
        return true;
      }
    }
    else if(updateMode.compareTo("UPDATE") == 0)
    {
      if(!Security.hasEntityPermission("PERSON_PERSON_TYPE", "_UPDATE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to update PersonPersonType (PERSON_PERSON_TYPE_UPDATE or PERSON_PERSON_TYPE_ADMIN needed).");
        return true;
      }

      PersonPersonType personPersonType = PersonPersonTypeHelper.update(username, typeId);
      if(personPersonType == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of PersonPersonType failed. USERNAME, TYPE_ID: " + username + ", " + typeId);
        return true;
      }
    }
    else if(updateMode.compareTo("DELETE") == 0)
    {
      if(!Security.hasEntityPermission("PERSON_PERSON_TYPE", "_DELETE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to delete PersonPersonType (PERSON_PERSON_TYPE_DELETE or PERSON_PERSON_TYPE_ADMIN needed).");
        return true;
      }

      //Remove associated/dependent entries from other tables here
      //Delete actual personPersonType last, just in case database is set up to do a cascading delete, caches won't get cleared
      PersonPersonTypeHelper.removeByPrimaryKey(username, typeId);
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
