/*
 * $Id$
 * $Log$
 * Revision 1.4  2002/01/27 08:33:13  jonesde
 * Changed update password to use new service, also added createUserLogin service
 *
 * Revision 1.3  2002/01/25 06:59:06  jonesde
 * A small addition for ecommerce to maintain the current catalog setting
 *
 *
 * Revision 1.1  2001/10/19 16:44:42  azeneski
 * Moved Party/ContactMech/Login events to more appropiate packages.
 *
 */

package org.ofbiz.commonapp.security.login;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.net.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.commonapp.party.contact.ContactHelper;

/**
 * <p><b>Title:</b> LoginEvents.java
 * <p><b>Description:</b> Events for UserLogin and Security handling.
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
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
 * @author <a href="mailto:jaz@zsolv.com">Andy Zeneski</a> 
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author Dustin Caldwell
 * @version 1.0
 * Created on October 19, 2001, 8:34 AM
 */
public class LoginEvents {
    
    /** An HTTP WebEvent handler that checks to see is a userLogin is logged in. If not, the user is forwarded to the /login.jsp page.
     *@param request The HTTP request object for the current JSP or Servlet request.
     *@param response The HTTP response object for the current JSP or Servlet request.
     *@throws RemoteException
     *@throws IOException
     *@throws ServletException
     *@return
     */
    public static String checkLogin(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException {
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        
        if (userLogin == null) {
            String queryString = null;
            Enumeration params = request.getParameterNames();
            while (params != null && params.hasMoreElements()) {
                String paramName = (String) params.nextElement();
                if (paramName != null) {
                    if (queryString == null) {
                        queryString = paramName + "=" + request.getParameter(paramName);
                    } else {
                        queryString = queryString + "&" + paramName + "=" + request.getParameter(paramName);
                    }
                }
            }
            
            request.getSession().setAttribute(SiteDefs.PREVIOUS_REQUEST, request.getPathInfo());
            if (queryString != null)
                request.getSession().setAttribute(SiteDefs.PREVIOUS_PARAMS, queryString);
            
            Debug.logInfo("SecurityEvents.checkLogin: queryString=" + queryString);
            Debug.logInfo("SecurityEvents.checkLogin: PathInfo=" + request.getPathInfo());
            
            return "error";
        } else {
            return "success";
        }
    }
    
    /** An HTTP WebEvent handler that logs in a userLogin. This should run before the security check.
     *@param request The HTTP request object for the current JSP or Servlet request.
     *@param response The HTTP response object for the current JSP or Servlet request.
     *@return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
     *@exception javax.servlet.ServletException Standard J2EE Servlet Exception
     *@exception java.rmi.RemoteException Standard RMI Remote Exception
     *@exception java.io.IOException Standard IO Exception
     */
    public static String login(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException {
        String errMsg = "";
        String username = request.getParameter("USERNAME");
        String password = request.getParameter("PASSWORD");
        
        GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
        
        if(username == null || username.length() <= 0) {
            errMsg = "Username missing.";
        } else if(password == null || password.length() <= 0) {
            errMsg = "Password missing";
        } else {
            GenericValue userLogin = null;
            try {
                userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", username));
            } catch(GenericEntityException e) {
                Debug.logWarning(e);
            }
            if(userLogin != null) {
                if(userLogin.get("currentPassword") != null && password.equals(userLogin.getString("currentPassword"))) {
                    request.getSession().setAttribute(SiteDefs.USER_LOGIN, userLogin);
                    try {
                        delegator.create("UserLoginHistory", UtilMisc.toMap("userLoginId", username,
                            "fromDate", UtilDateTime.nowTimestamp(), "passwordUsed", password,
                            "partyId", userLogin.get("partyId"), "referrerUrl", "NotYetImplemented"));
                    } catch(GenericEntityException e) {
                        Debug.logWarning(e);
                    }
                } else {
                    // password invalid, just go to badlogin page...
                    errMsg = "Password incorrect.";
                }
            } else {
                //userLogin record not found, user does not exist
                errMsg = "User not found.";
            }
        }
        
        if(errMsg.length() > 0) {
            errMsg = "<b>The following error occured:</b><br>" + errMsg;
            request.getSession().setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
            return "error";
        }
        return "success";
    }
    
    /** An HTTP WebEvent handler that logs out a userLogin by clearing the session.
     *@param request The HTTP request object for the current JSP or Servlet request.
     *@param response The HTTP response object for the current JSP or Servlet request.
     *@return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
     *@exception java.io.IOException Standard IO Exception
     */
    public static String logout(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
        //invalidate the security group list cache
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        Security sec = (Security)request.getAttribute("security");
        
        if(sec != null && userLogin != null)
            sec.userLoginSecurityGroupByUserLoginId.remove(userLogin.getString("userLoginId"));
        
        //this is a setting we don't want to lose, although it would be good to have a more general solution here...
        String currCatalog = (String) request.getSession().getAttribute("CURRENT_CATALOG_ID");
        request.getSession().invalidate();
        request.getSession(true);
        if(currCatalog != null) {
            request.getSession().setAttribute("CURRENT_CATALOG_ID", currCatalog);
        }
        
        return "success";
    }
    
    /** The user forgot his/her password.  This will either call showPasswordHint or emailPassword.
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String forgotPassword(HttpServletRequest request, HttpServletResponse response) {
        if (UtilValidate.isNotEmpty(request.getParameter("GET_PASSWORD_HINT"))) {
            return showPasswordHint(request, response);
        } else {
            return emailPassword(request, response);
        }
    }
    
    /** Show the password hint for the userLoginId specified in the request object.
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String showPasswordHint(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
        
        String userLoginId = request.getParameter("USERNAME");
        
        if(!UtilValidate.isNotEmpty(userLoginId)) {
            //the password was incomplete
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>The Username was empty, please re-enter.");
            return "error";
        }
        
        GenericValue supposedUserLogin = null;
        try {
            supposedUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        } catch (GenericEntityException gee) {
            Debug.logWarning(gee);
        }
        if (supposedUserLogin == null) {
            //the Username was not found
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>The Username was not found, please re-enter.");
            return "error";
        }
        
        String passwordHint = supposedUserLogin .getString("passwordHint");
        if (!UtilValidate.isNotEmpty(passwordHint)) {
            //the Username was not found
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>No password hint was specified, try having the password emailed instead.");
            return "error";
        }
        
        request.setAttribute(SiteDefs.EVENT_MESSAGE, "The Password Hint is: " + passwordHint);
        return "success";
    }
    
    /** Email the password for the userLoginId specified in the request object.
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String emailPassword(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
        String contextRoot=(String)request.getAttribute(SiteDefs.CONTEXT_ROOT);
        //getServletContext appears to be new on the session object for Servlet 2.3
        ServletContext application = request.getSession().getServletContext();
        URL ecommercePropertiesUrl = null;
        try {
            ecommercePropertiesUrl = application.getResource("/WEB-INF/ecommerce.properties");
        } catch (java.net.MalformedURLException e) {
            Debug.logWarning(e);
        }
        
        String userLoginId = request.getParameter("USERNAME");
        
        if (!UtilValidate.isNotEmpty(userLoginId)) {
            //the password was incomplete
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>The Username was empty, please re-enter.");
            return "error";
        }
        
        GenericValue supposedUserLogin = null;
        try {
            supposedUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        } catch (GenericEntityException gee) {
            Debug.logWarning(gee);
        }
        if (supposedUserLogin == null) {
            //the Username was not found
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>The Username was not found, please re-enter.");
            return "error";
        }
        
        StringBuffer emails = new StringBuffer();
        GenericValue party = null;
        try {
            party = supposedUserLogin.getRelatedOne("Party");
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            party = null;
        }
        if (party != null) {
            //Iterator emailIter = UtilMisc.toIterator(ContactHelper.getContactMech(party, "PRIMARY_EMAIL", "EMAIL_ADDRESS", false));
            Iterator emailIter = UtilMisc.toIterator(ContactHelper.getContactMechByPurpose(party, "PRIMARY_EMAIL", false));
            while (emailIter != null && emailIter.hasNext()) {
                GenericValue email = (GenericValue) emailIter.next();
                emails.append(emails.length() > 0 ? "," : "").append(email.getString("infoString"));
            }
        }
        
        if (!UtilValidate.isNotEmpty(emails.toString())) {
            //the Username was not found
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>No Primary Email Address has been set, please contact ustomer service.");
            return "error";
        }
        
        String SMTP_SERVER = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "smtp.relay.host");
        String LOCAL_MACHINE = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "smtp.local.machine");
        String PASSWORD_SENDER_EMAIL = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "password.send.email");
        
        String content = "Username: " + userLoginId + "\nPassword: " + UtilFormatOut.checkNull(supposedUserLogin.getString("currentPassword"));
        try {
            SendMailSMTP mail = new SendMailSMTP(SMTP_SERVER, PASSWORD_SENDER_EMAIL, emails.toString(), content);
            mail.setLocalMachine(LOCAL_MACHINE);
            mail.setSubject(UtilProperties.getPropertyValue(ecommercePropertiesUrl, "company.name", "") + " Password Reminder");
            //mail.setExtraHeader("MIME-Version: 1.0\nContent-type: text/html; charset=us-ascii\n");
            mail.setMessage(content);
            mail.send();
        } catch (java.io.IOException e) {
            Debug.logWarning(e);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "error occurred: unable to email password.  Please try again later or contact customer service.");
            return "error";
        }
        
        request.setAttribute(SiteDefs.EVENT_MESSAGE, "Your password has been sent to you.  Please check your Email.");
        return "success";
    }
}
