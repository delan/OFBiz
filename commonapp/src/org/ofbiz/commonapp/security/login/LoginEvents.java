/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.commonapp.security.login;


import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.net.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.stats.*;
import org.ofbiz.commonapp.party.contact.ContactHelper;


/**
 * LoginEvents - Events for UserLogin and Security handling.
 *
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     Dustin Caldwell
 * @author     <a href="mailto:therrick@yahoo.com">Tom Herrick</a>
 * @version    1.0
 * @created    Oct 19, 2001
 */
public class LoginEvents {

    /**
     * Save USERNAME and PASSWORD for use by auth pages even if we start in non-auth pages.
     *
     * @param request The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @throws RemoteException
     * @throws IOException
     * @throws ServletException
     * @return
     */
    public static String saveEntryParams(HttpServletRequest request, HttpServletResponse response)
        throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        HttpSession session = request.getSession();

        // save entry login parameters if we don't have a valid login object
        if (userLogin == null) {

            String username = request.getParameter("USERNAME");
            String password = request.getParameter("PASSWORD");

            if ((username != null) && ("true".equals(UtilProperties.getPropertyValue("security", "username.lowercase")))) {
                username = username.toLowerCase();
            }
            if ((password != null) && ("true".equals(UtilProperties.getPropertyValue("security", "password.lowercase")))) {
                password = password.toLowerCase();
            }
            
            // save parameters into the session - so they can be used later, if needed
            if (username != null) session.setAttribute("USERNAME", username);
            if (password != null) session.setAttribute("PASSWORD", password);

        } else {
            // if the login object is valid, remove attributes
            session.removeAttribute("USERNAME");
            session.removeAttribute("PASSWORD");
        }

        return "success";
    }

    /**
     * An HTTP WebEvent handler that checks to see is a userLogin is logged in.
     * If not, the user is forwarded to the /login.jsp page.
     *
     * @param request The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @throws RemoteException
     * @throws IOException
     * @throws ServletException
     * @return
     */
    public static String checkLogin(HttpServletRequest request, HttpServletResponse response)
        throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        HttpSession session = request.getSession();

        String username = null;
        String password = null;

        if (userLogin == null) {
            // check parameters
            if (username == null) username = request.getParameter("USERNAME");
            if (password == null) password = request.getParameter("PASSWORD");
            // check session attributes
            if (username == null) username = (String) session.getAttribute("USERNAME");
            if (password == null) password = (String) session.getAttribute("PASSWORD");

            if ((username != null) && ("true".equals(UtilProperties.getPropertyValue("security", "username.lowercase")))) {
                username = username.toLowerCase();
            }
            if ((password != null) && ("true".equals(UtilProperties.getPropertyValue("security", "password.lowercase")))) {
                password = password.toLowerCase();
            }

            if ((username == null) || (password == null) || ("error".equals(login(request, response)))) {
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

                session.setAttribute(SiteDefs.PREVIOUS_REQUEST, request.getPathInfo());
                if (queryString != null)
                    session.setAttribute(SiteDefs.PREVIOUS_PARAMS, queryString);

                if (Debug.infoOn()) Debug.logInfo("SecurityEvents.checkLogin: queryString=" + queryString);
                if (Debug.infoOn()) Debug.logInfo("SecurityEvents.checkLogin: PathInfo=" + request.getPathInfo());

                return "error";
            }
        }

        return "success";
    }

    /**
     * An HTTP WebEvent handler that logs in a userLogin. This should run before the security check.
     *
     * @param request The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @return Return a boolean which specifies whether or not the calling Servlet or
     *         JSP should generate its own content. This allows an event to override the default content.
     * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
     * @exception java.rmi.RemoteException Standard RMI Remote Exception
     * @exception java.io.IOException Standard IO Exception
     */
    public static String login(HttpServletRequest request, HttpServletResponse response)
        throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException {
        HttpSession session = request.getSession();

        String username = request.getParameter("USERNAME");
        String password = request.getParameter("PASSWORD");

        if (username == null) username = (String) session.getAttribute("USERNAME");
        if (password == null) password = (String) session.getAttribute("PASSWORD");

        if ((username != null) && ("true".equals(UtilProperties.getPropertyValue("security", "username.lowercase")))) {
            username = username.toLowerCase();
        }
        if ((password != null) && ("true".equals(UtilProperties.getPropertyValue("security", "password.lowercase")))) {
            password = password.toLowerCase();
        }

        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map result = null;

        try {
            result = dispatcher.runSync("userLogin", UtilMisc.toMap("login.username", username, "login.password", password));
        } catch (GenericServiceException e) {
            Debug.logError(e, "Error calling userLogin service");
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<b>The following error occured during login:</b><br>" + e.getMessage());
            return "error";
        }

        if (ModelService.RESPOND_SUCCESS.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
            GenericValue userLogin = (GenericValue) result.get("userLogin");

            if (userLogin != null) {
                session.setAttribute(SiteDefs.USER_LOGIN, userLogin);
                // let the visit know who the user is
                VisitHandler.setUserLogin(session, userLogin, false);
            }
        } else {
            String errMsg = (String) result.get(ModelService.ERROR_MESSAGE);

            errMsg = "<b>The following error occured during login:</b><br>" + errMsg;
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
            return "error";
        }

        request.setAttribute(SiteDefs.LOGIN_PASSED, "TRUE");
        // make sure the autoUserLogin is set to the same and that the client cookie has the correct userLoginId
        return autoLoginSet(request, response);
    }

    /**
     * An HTTP WebEvent handler that logs out a userLogin by clearing the session.
     *
     * @param request The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @return Return a boolean which specifies whether or not the calling Servlet or
     *        JSP should generate its own content. This allows an event to override the default content.
     * @exception java.io.IOException Standard IO Exception
     */
    public static String logout(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
        // invalidate the security group list cache
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        Security security = (Security) request.getAttribute("security");

        if (security != null && userLogin != null) {
            security.userLoginSecurityGroupByUserLoginId.remove(userLogin.getString("userLoginId"));
        }

        HttpSession session = request.getSession();

        // this is a setting we don't want to lose, although it would be good to have a more general solution here...
        String currCatalog = (String) session.getAttribute("CURRENT_CATALOG_ID");
        // also make sure the delegatorName is preserved, especially so that a new Visit can be created
        String delegatorName = (String) session.getAttribute("delegatorName");

        session.invalidate();
        session = request.getSession(true);

        if (currCatalog != null) session.setAttribute("CURRENT_CATALOG_ID", currCatalog);
        if (delegatorName != null) session.setAttribute("delegatorName", delegatorName);

        if (request.getAttribute("_AUTO_LOGIN_LOGOUT_") == null) {
            return autoLoginCheck(request, response);
        }
        return "success";
    }

    /**
     * The user forgot his/her password.  This will either call showPasswordHint or emailPassword.
     *
     * @param request The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String forgotPassword(HttpServletRequest request, HttpServletResponse response) {
        if ((UtilValidate.isNotEmpty(request.getParameter("GET_PASSWORD_HINT"))) || (UtilValidate.isNotEmpty(request.getParameter("GET_PASSWORD_HINT.x")))) {
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
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        String userLoginId = request.getParameter("USERNAME");

        if ((userLoginId != null) && ("true".equals(UtilProperties.getPropertyValue("security", "username.lowercase")))) {
            userLoginId = userLoginId.toLowerCase();
        }

        if (!UtilValidate.isNotEmpty(userLoginId)) {
            // the password was incomplete
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
            // the Username was not found
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>The Username was not found, please re-enter.");
            return "error";
        }

        String passwordHint = supposedUserLogin.getString("passwordHint");

        if (!UtilValidate.isNotEmpty(passwordHint)) {
            // the Username was not found
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>No password hint was specified, try having the password emailed instead.");
            return "error";
        }

        request.setAttribute(SiteDefs.EVENT_MESSAGE, "The Password Hint is: " + passwordHint);
        return "success";
    }

    /**
     *  Email the password for the userLoginId specified in the request object.
     *
     * @param request The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String emailPassword(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String contextRoot = (String) request.getAttribute(SiteDefs.CONTEXT_ROOT);
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        URL ecommercePropertiesUrl = null;

        try {
            ecommercePropertiesUrl = application.getResource("/WEB-INF/ecommerce.properties");
        } catch (java.net.MalformedURLException e) {
            Debug.logWarning(e);
        }

        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security", "password.encrypt"));

        String userLoginId = request.getParameter("USERNAME");

        if ((userLoginId != null) && ("true".equals(UtilProperties.getPropertyValue("security", "username.lowercase")))) {
            userLoginId = userLoginId.toLowerCase();
        }

        if (!UtilValidate.isNotEmpty(userLoginId)) {
            // the password was incomplete
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>The Username was empty, please re-enter.");
            return "error";
        }

        GenericValue supposedUserLogin = null;
        String passwordToSend = null;

        try {
            supposedUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
            if (supposedUserLogin == null) {
                // the Username was not found
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>The Username was not found, please re-enter.");
                return "error";
            }
            if (useEncryption) {
                // password encrypted, can't send, generate new password and email to user
                double randNum = Math.random();

                // multiply by 100,000 to usually make a 5 digit number
                passwordToSend = "auto" + ((long) (randNum * 100000));
                supposedUserLogin.set("currentPassword", HashEncrypt.getHash(passwordToSend));
                supposedUserLogin.set("passwordHint", "Auto-Generated Password");
            } else {
                passwordToSend = supposedUserLogin.getString("currentPassword");
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Error accessing password: " + e.toString());
            return "error";
        }
        if (supposedUserLogin == null) {
            // the Username was not found
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>A user with the username \"" + userLoginId + "\" was not found, please re-enter.");
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
            // Iterator emailIter = UtilMisc.toIterator(ContactHelper.getContactMech(party, "PRIMARY_EMAIL", "EMAIL_ADDRESS", false));
            Iterator emailIter = UtilMisc.toIterator(ContactHelper.getContactMechByPurpose(party, "PRIMARY_EMAIL", false));

            while (emailIter != null && emailIter.hasNext()) {
                GenericValue email = (GenericValue) emailIter.next();

                emails.append(emails.length() > 0 ? "," : "").append(email.getString("infoString"));
            }
        }

        if (!UtilValidate.isNotEmpty(emails.toString())) {
            // the Username was not found
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>No Primary Email Address has been set, please contact customer service.");
            return "error";
        }

        String SMTP_SERVER = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "smtp.relay.host");
        // String LOCAL_MACHINE = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "smtp.local.machine");
        String PASSWORD_SENDER_EMAIL = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "password.send.email");

        String controlPath = (String) request.getAttribute(SiteDefs.CONTROL_PATH);

        if (controlPath == null) {
            Debug.logError("[CheckOutEvents.renderConfirmOrder] CONTROL_PATH is null.");
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Error generating order confirmation, but it was recorded and will be processed.");
            return "error";
        }
        // build the server root string
        StringBuffer serverRoot = new StringBuffer();
        String server = UtilProperties.getPropertyValue("url.properties", "force.http.host", request.getServerName());
        String port = UtilProperties.getPropertyValue("url.properties", "port.http", "80");

        serverRoot.append("http://");
        serverRoot.append(server);
        if (!port.equals("80")) {
            serverRoot.append(":" + port);
        }
        String bodyUrl = serverRoot + controlPath + "/passwordemail";

        Map context = new HashMap();

        if (useEncryption) {
            context.put("subject", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "company.name", "") + " New Password");
        } else {
            context.put("subject", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "company.name", "") + " Password Reminder");
        }
        context.put("bodyUrl", bodyUrl);
        context.put("sendTo", emails.toString());
        context.put("sendFrom", PASSWORD_SENDER_EMAIL);
        context.put("sendVia", SMTP_SERVER);

        Map parameters = new HashMap();

        parameters.put("password", UtilFormatOut.checkNull(passwordToSend));
        parameters.put("useEncryption", new Boolean(useEncryption).toString());
        context.put("bodyUrlParameters", parameters);

        // String content = "Username: " + userLoginId + "\n" + (useEncryption ? "New Password: " : "Current Password: ") + ;
        try {
            Map result = dispatcher.runSync("sendMailFromUrl", context);

            if (ModelService.RESPOND_ERROR.equals((String) result.get(ModelService.RESPONSE_MESSAGE))) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Error occurred: unable to email password.  Please try again later or contact customer service. (error was: " + result.get(ModelService.ERROR_MESSAGE) + ")");
                return "error";
            }
        } catch (GenericServiceException e) {
            Debug.logWarning(e);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Error occurred: unable to email password.  Please try again later or contact customer service.");
            return "error";
        }

        // don't save password until after it has been sent
        if (useEncryption) {
            try {
                supposedUserLogin.store();
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Error saving new password, the email that you receive will not have the correct password in it, your old password is still being used: " + e.toString());
                return "error";
            }
        }

        if (useEncryption) {
            request.setAttribute(SiteDefs.EVENT_MESSAGE, "A new password has been created and sent to you.  Please check your Email.");
        } else {
            request.setAttribute(SiteDefs.EVENT_MESSAGE, "Your password has been sent to you.  Please check your Email.");
        }
        return "success";
    }

    protected static String getAutoLoginCookieName(HttpServletRequest request) {
        return UtilMisc.getApplicationName(request) + ".autoUserLoginId";
    }

    public static String getAutoUserLoginId(HttpServletRequest request) {
        String autoUserLoginId = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(getAutoLoginCookieName(request))) {
                    autoUserLoginId = cookies[i].getValue();
                    break;
                }
            }
        }
        return autoUserLoginId;
    }

    public static String autoLoginCheck(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();

        return autoLoginCheck(delegator, session, getAutoUserLoginId(request));
    }

    private static String autoLoginCheck(GenericDelegator delegator, HttpSession session, String autoUserLoginId) {
        if (autoUserLoginId != null) {
            Debug.logInfo("Running autoLogin check.");
            try {
                GenericValue autoUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", autoUserLoginId));
                GenericValue person = null;
                GenericValue group = null;
                if (autoUserLogin != null) {
                    person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", autoUserLogin.getString("partyId")));
                    group = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", autoUserLogin.getString("partyId")));
                    session.setAttribute("autoUserLogin", autoUserLogin);
                }
                if (person != null) {
                    session.setAttribute("autoName", person.getString("firstName") + " " + person.getString("lastName"));
                } else if (group != null) {
                    session.setAttribute("autoName", group.getString("groupName"));
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot get autoUserLogin information: " + e.getMessage());
            }
        }
        return "success";
    }

    public static String autoLoginSet(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute(SiteDefs.USER_LOGIN);
        Cookie autoLoginCookie = new Cookie(getAutoLoginCookieName(request), userLogin.getString("userLoginId"));

        autoLoginCookie.setMaxAge(60 * 60 * 24 * 365);
        response.addCookie(autoLoginCookie);
        return autoLoginCheck(delegator, session, userLogin.getString("userLoginId"));
    }

    public static String autoLoginRemove(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("autoUserLogin");

        // remove the cookie
        if (userLogin != null) {
            Cookie autoLoginCookie = new Cookie(getAutoLoginCookieName(request), userLogin.getString("userLoginId"));

            autoLoginCookie.setMaxAge(0);
            response.addCookie(autoLoginCookie);
        }
        // remove the session attributes
        session.removeAttribute("autoUserLogin");
        session.removeAttribute("autoName");
        // logout the user if logged in.
        if (session.getAttribute("userLogin") != null) {
            request.setAttribute("_AUTO_LOGIN_LOGOUT_", new Boolean(true));
            return logout(request, response);
        }
        return "success";
    }
}
