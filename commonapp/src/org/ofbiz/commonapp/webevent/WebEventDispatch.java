package org.ofbiz.commonapp.webevent;

import javax.servlet.http.*;
import javax.servlet.*;
import java.util.*;
import java.lang.reflect.*;

import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Class to handle web event dispatching
 * <p><b>Description:</b> The dispatcher for the WebEvent component.
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
 *@author     David Jones
 *@created    May 21, 2001
 *@version    1.0
 */
public class WebEventDispatch
{
  /** The main WebEvent Dispatcher method.
   * Looks at the parameters on an HTTP request query string and calls any webevents specified with the WEBEVENT or WEBPREEVENT parameters.
   * Controls the security by running a security check before running normal webevents, and after running webpreevents.
   * @param request The HttpServletRequest for the current HttpSession
   * @param response The HttpServletResponse for the current HttpSession
   * @param loginRequired Specifies whether or not a logged in person is required; determines whether or not the security check should be done.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   */  
  public static boolean dispatch(HttpServletRequest request, HttpServletResponse response, boolean loginRequired)
  {
    String eventName, eventValue;

    eventName = request.getParameter("WEBPREEVENT");    
    if(eventName != null && eventName.length() > 0)
    {
      //in order to be executed before the security check, the event name must be
      // in the WEBPREEVENT parameter, and the properties file entry must be
      // prefixed with a "security."
      try
      {
        eventValue = UtilProperties.getPropertyValue("webevent", "security." + eventName);
        if(eventValue != null && eventValue.length() > 0)
          if(!doEvent(eventValue, request, response)) return false;
      }
      catch(Exception exc) 
      {
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          exc.printStackTrace();
        }
      }
    }
    
    eventName = request.getParameter("WEBEVENT");

    //If requested, make sure the user is logged in
    if(loginRequired)
    {
      try
      {
        eventValue = UtilProperties.getPropertyValue("webevent", "security.check");
        if(eventValue != null && eventValue.length() > 0)
        {
          if(!doEvent(eventValue, request, response)) return false;
        }
        else 
        {
          //no security check found, always send to login.jsp
          RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
          rd.forward(request, response);
          return false;
        }
      }
      catch(Exception exc) 
      {
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          exc.printStackTrace();
        }
      }
    }

    try
    {
      if(eventName == null || eventName.length() <= 0) return true;
      eventValue = UtilProperties.getPropertyValue("webevent", eventName);
      if(eventValue == null || eventValue.length() <= 0) return true;
      return doEvent(eventValue, request, response);
    }
    catch(Exception exc) 
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
      {
        exc.printStackTrace();
      }
    }
    return true;
  }

  /** Takes the event string from the properties file and executes it as specified.
   * Supports multiple languages for handling events including Java and the various languages of the IBM Bean Scripting Framework (BSF) like JavaScript, JPython, and NetRexx.
   * @param eventValue The value of the requested webevent from the webevent.properties file
   * @param request The HttpServletRequest for the current HttpSession
   * @param response The HttpServletResponse for the current HttpSession
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   */  
  protected static boolean doEvent(String eventValue, HttpServletRequest request, HttpServletResponse response)
  {
    if(eventValue == null || eventValue.length() <= 0) return true;
    String eventType = eventValue.substring(0,eventValue.indexOf(":"));

    if(eventType.compareToIgnoreCase("java") == 0)
    {
      String className = eventValue.substring(eventValue.indexOf(":")+1,eventValue.lastIndexOf("."));
      String methodName = eventValue.substring(eventValue.lastIndexOf(".")+1);

      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true")) System.out.println("doEvent:java: className=" + className);
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true")) System.out.println("doEvent:java: methodName=" + methodName);
      
      try
      {
        Class[] paramTypes = new Class[] {HttpServletRequest.class, HttpServletResponse.class};
        Object[] params = new Object[] {request, response};

        Class eventClass = Class.forName(className);
        Method eventMethod = eventClass.getMethod(methodName, paramTypes);

        Boolean retValue = (Boolean)eventMethod.invoke(null, params);
        return retValue.booleanValue();
      }
      catch(ClassNotFoundException cnfe)
      {
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
        {
          System.out.println("doEvent ERROR: ClassNotFoundException: " + className);
          cnfe.printStackTrace();
        }
        return true;
      }
      catch(NoSuchMethodException nsme)
      {
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
        {
          System.out.println("doEvent ERROR: NoSuchMethodException: " + methodName);
          nsme.printStackTrace();
        }
        return true;
      }
      catch(InvocationTargetException ite)
      {
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
        {
          System.out.println("doEvent ERROR: InvocationTargetException: " + eventValue);
          ite.printStackTrace();
        }

        return true;
      }
      catch(IllegalAccessException iae)
      {
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
        {
          System.out.println("doEvent ERROR: IllegalAccessException: " + eventValue);
          iae.printStackTrace();
        }
        return true;
      }
    }
    else if(eventType.compareToIgnoreCase("javascript") == 0)
    {
      //to be implemented later through IBM BSF
    }
    else if(eventType.compareToIgnoreCase("jpython") == 0)
    {
      //to be implemented later through IBM BSF
    }
    else if(eventType.compareToIgnoreCase("netrexx") == 0)
    {
      //to be implemented later through IBM BSF
    }
    
    return true;
  }
}

