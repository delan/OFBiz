/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.commonapp.party.contact.ContactHelper;
import org.ofbiz.commonapp.product.catalog.CatalogWorker;
import org.ofbiz.core.control.JPublishWrapper;
import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.security.Security;
import org.ofbiz.core.service.GenericServiceException;
import org.ofbiz.core.service.LocalDispatcher;
import org.ofbiz.core.service.ModelService;
import org.ofbiz.core.stats.VisitHandler;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.FlexibleStringExpander;
import org.ofbiz.core.util.GeneralException;
import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.UtilFormatOut;
import org.ofbiz.core.util.UtilHttp;
import org.ofbiz.core.util.UtilMisc;
import org.ofbiz.core.util.UtilProperties;
import org.ofbiz.core.util.UtilValidate;

/**
 * LoginEvents - Events for UserLogin and Security handling.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="">Dustin Caldwell</a>
 * @author     <a href="mailto:therrick@yahoo.com">Tom Herrick</a>
 * @version    $Revision$
 * @since      2.0
 */
public class LoginEvents {
        
    public static final String module = LoginEvents.class.getName();

    public static final String EXTERNAL_LOGIN_KEY_ATTR = "externalLoginKey";

    /** This Map is keyed by the randomly generated externalLoginKey and the value is a UserLogin GenericValue object */
    public static Map externalLoginKeys = new HashMap();

    /** This Map is keyed by userLoginId and the value is another Map keyed by the webappName and the value is the sessionId.
     * When a user logs in an entry in this Map will be populated for the given user, webapp and session. 
     * When checking security this Map will be checked if the user is logged in to see if we should log them out automatically; this implements the universal logout.
     * When a user logs out this Map will be cleared so the user will be logged out automatically on subsequent requests. 
     */
    public static Map loggedInSessions = new HashMap();
    
    /**
     * Save USERNAME and PASSWORD for use by auth pages even if we start in non-auth pages.
     *
     * @param request The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @return
     */
    public static String saveEntryParams(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        HttpSession session = request.getSession();

        // save entry login parameters if we don't have a valid login object
        if (userLogin == null) {

            String username = request.getParameter("USERNAME");
            String password = request.getParameter("PASSWORD");

            if ((username != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
                username = username.toLowerCase();
            }
            if ((password != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "password.lowercase")))) {
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
     * @return
     */
    public static String checkLogin(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        HttpSession session = request.getSession();

        // user is logged in; check to see if there is an entry in the loggedInSessions Map, if not log out this user 
        if (userLogin != null) {
            boolean loggedInSession = isLoggedInSession(userLogin, request);
            if (!loggedInSession) {
                doBasicLogout(userLogin, request);
                userLogin = null;
                // have to reget this because the old session object will be invalid
                session = request.getSession();
            }
        }

        String username = null;
        String password = null;

        if (userLogin == null) {
            // check parameters
            if (username == null) username = request.getParameter("USERNAME");
            if (password == null) password = request.getParameter("PASSWORD");
            // check session attributes
            if (username == null) username = (String) session.getAttribute("USERNAME");
            if (password == null) password = (String) session.getAttribute("PASSWORD");

            if ((username != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
                username = username.toLowerCase();
            }
            if ((password != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "password.lowercase")))) {
                password = password.toLowerCase();
            }

            // in this condition log them in if not already; if not logged in or can't log in, save parameters and return error
            if ((username == null) || (password == null) || ("error".equals(login(request, response)))) {
                Map reqParams = UtilHttp.getParameterMap(request);
                String queryString = UtilHttp.urlEncodeArgs(reqParams);

                session.setAttribute(SiteDefs.PREVIOUS_REQUEST, request.getPathInfo());
                if (queryString != null && queryString.length() > 0) {
                    session.setAttribute(SiteDefs.PREVIOUS_PARAMS, queryString);
                }

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
    public static String login(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        String username = request.getParameter("USERNAME");
        String password = request.getParameter("PASSWORD");

        if (username == null) username = (String) session.getAttribute("USERNAME");
        if (password == null) password = (String) session.getAttribute("PASSWORD");

        if ((username != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
            username = username.toLowerCase();
        }
        if ((password != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "password.lowercase")))) {
            password = password.toLowerCase();
        }

        // get the visit id to pass to the userLogin for history
        String visitId = VisitHandler.getVisitId(session);
        
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map result = null;

        try {
            result = dispatcher.runSync("userLogin", UtilMisc.toMap("login.username", username, "login.password", password, "visitId", visitId));
        } catch (GenericServiceException e) {
            Debug.logError(e, "Error calling userLogin service");
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<b>The following error occurred during login:</b><br>" + e.getMessage());
            return "error";
        }

        if (ModelService.RESPOND_SUCCESS.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
            GenericValue userLogin = (GenericValue) result.get("userLogin");
            Map userLoginSession = (Map) result.get("userLoginSession");

            if (userLogin != null) {
                doBasicLogin(userLogin, request);
            }
            
            if (userLoginSession != null) {
            	session.setAttribute("userLoginSession", userLoginSession);
            }
        } else {
            String errMsg = (String) result.get(ModelService.ERROR_MESSAGE);

            errMsg = "<b>The following error occurred during login:</b><br>" + errMsg;
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
            return "error";
        }
                
        request.setAttribute(SiteDefs.LOGIN_PASSED, "TRUE");
        // make sure the autoUserLogin is set to the same and that the client cookie has the correct userLoginId
        return autoLoginSet(request, response);
    }
    
    public static void doBasicLogin(GenericValue userLogin, HttpServletRequest request) {
        HttpSession session = request.getSession(); 
        session.setAttribute(SiteDefs.USER_LOGIN, userLogin);
        // let the visit know who the user is
        VisitHandler.setUserLogin(session, userLogin, false);
        loginToSession(userLogin, request);
    }

    /**
     * An HTTP WebEvent handler that logs out a userLogin by clearing the session.
     *
     * @param request The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @return Return a boolean which specifies whether or not the calling Servlet or
     *        JSP should generate its own content. This allows an event to override the default content.
     */
    public static String logout(HttpServletRequest request, HttpServletResponse response) {
        // invalidate the security group list cache
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);

        // log out from all other sessions too; do this here so that it is only done when a user explicitly logs out
        logoutFromAllSessions(userLogin);
        
        doBasicLogout(userLogin, request);

        if (request.getAttribute("_AUTO_LOGIN_LOGOUT_") == null) {
            return autoLoginCheck(request, response);
        }
        return "success";
    }
    
    public static void doBasicLogout(GenericValue userLogin, HttpServletRequest request) {
        HttpSession session = request.getSession();

        Security security = (Security) request.getAttribute("security");

        if (security != null && userLogin != null) {
            Security.userLoginSecurityGroupByUserLoginId.remove(userLogin.getString("userLoginId"));
        }
        
        // this is a setting we don't want to lose, although it would be good to have a more general solution here...
        String currCatalog = (String) session.getAttribute("CURRENT_CATALOG_ID");
        // also make sure the delegatorName is preserved, especially so that a new Visit can be created
        String delegatorName = (String) session.getAttribute("delegatorName");

        session.invalidate();
        session = request.getSession(true);

        if (currCatalog != null) session.setAttribute("CURRENT_CATALOG_ID", currCatalog);
        if (delegatorName != null) session.setAttribute("delegatorName", delegatorName);
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

        if ((userLoginId != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
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
        String webSiteId = CatalogWorker.getWebSiteId(request);
        
        Map subjectData = new HashMap();
        subjectData.put("webSiteId", webSiteId);
      
        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));

        String userLoginId = request.getParameter("USERNAME");
        subjectData.put("userLoginId", userLoginId);

        if ((userLoginId != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
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
        
        // get the WebSite settings
        GenericValue webSiteEmail = null;
        try {
            webSiteEmail = delegator.findByPrimaryKey("WebSiteEmailSetting", UtilMisc.toMap("webSiteId", webSiteId, "emailType", "WES_PWD_RETRIEVE"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting WebSiteEmailSetting", module);
        }
        
        if (webSiteEmail == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems with configuration; please contact customer service.");
            return "error";
        }
        
        // need OFBIZ_HOME for processing
        String ofbizHome = System.getProperty("ofbiz.home");
        
        // set the needed variables in new context
        Map templateData = new HashMap();
        templateData.put("useEncryption", new Boolean(useEncryption));
        templateData.put("password", UtilFormatOut.checkNull(passwordToSend));
        
        // prepare the parsed subject        
        String subjectString = webSiteEmail.getString("subject");
        subjectString = FlexibleStringExpander.expandString(subjectString, subjectData);
                
        Map serviceContext = new HashMap();                        
        serviceContext.put("templateName", ofbizHome + webSiteEmail.get("templatePath"));
        serviceContext.put("templateData", templateData);
        serviceContext.put("subject", subjectString);
        serviceContext.put("sendFrom", webSiteEmail.get("fromAddress"));        
        serviceContext.put("sendCc", webSiteEmail.get("ccAddress"));
        serviceContext.put("sendBcc", webSiteEmail.get("ccAddress"));
        serviceContext.put("sendTo", emails.toString());              
                                              
        try {
            Map result = dispatcher.runSync("sendGenericNotificationEmail", serviceContext);

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
        return UtilHttp.getApplicationName(request) + ".autoUserLoginId";
    }

    public static String getAutoUserLoginId(HttpServletRequest request) {
        String autoUserLoginId = null;
        Cookie[] cookies = request.getCookies();
        Debug.logInfo("Cookies:" + cookies, module);
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
        autoLoginCookie.setPath("/");
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
            autoLoginCookie.setPath("/");
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

    /**
     * Gets (and creates if necessary) a key to be used for an external login parameter
     */
    public static String getExternalLoginKey(HttpServletRequest request) {
        Debug.logInfo("Running getExternalLoginKey, externalLoginKeys.size=" + externalLoginKeys.size());
        GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");
        
        String externalKey = (String) request.getAttribute(EXTERNAL_LOGIN_KEY_ATTR);
        if (externalKey != null) return externalKey;

        HttpSession session = request.getSession();
        synchronized (session) {
            // if the session has a previous key in place, remove it from the master list
            String sesExtKey = (String) session.getAttribute(EXTERNAL_LOGIN_KEY_ATTR);
            if (sesExtKey != null) {
                externalLoginKeys.remove(sesExtKey);
            }

            //check the userLogin here, after the old session setting is set so that it will always be cleared
            if (userLogin == null) return "";
            
            //no key made yet for this request, create one
            while (externalKey == null || externalLoginKeys.containsKey(externalKey)) {
                externalKey = "EL" + Long.toString(Math.round(Math.random() * 1000000)) + Long.toString(Math.round(Math.random() * 1000000));
            }

            request.setAttribute(EXTERNAL_LOGIN_KEY_ATTR, externalKey);
            session.setAttribute(EXTERNAL_LOGIN_KEY_ATTR, externalKey);
            externalLoginKeys.put(externalKey, userLogin);
            return externalKey;
        }
    }

    public static String checkExternalLoginKey(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        
        String externalKey = request.getParameter(EXTERNAL_LOGIN_KEY_ATTR);
        if (externalKey == null) return "success";
        
        GenericValue userLogin = (GenericValue) externalLoginKeys.get(externalKey);
        if (userLogin != null) {
            // found userLogin, do the external login...

            // if the user is already logged in and the login is different, logout the other user
            GenericValue currentUserLogin = (GenericValue) session.getAttribute(SiteDefs.USER_LOGIN);
            if (currentUserLogin != null) {
                if (currentUserLogin.getString("userLoginId").equals(userLogin.getString("userLoginId"))) {
                    // is the same user, just carry on...
                    return "success";
                }
                
                // logout the current user and login the new user...
                String logoutRetVal = logout(request, response);
                // ignore the return value; even if the operation failed we want to set the new UserLogin
            }

            doBasicLogin(userLogin, request);
        } else {
            Debug.logWarning("Could not find userLogin for external login key: " + externalKey);
        }
        
        return "success";
    }
    
    public static void cleanupExternalLoginKey(HttpSession session) {
        String sesExtKey = (String) session.getAttribute(EXTERNAL_LOGIN_KEY_ATTR);
        if (sesExtKey != null) {
            externalLoginKeys.remove(sesExtKey);
        }
    }
    
    public static boolean isLoggedInSession(GenericValue userLogin, HttpServletRequest request) {
        if (userLogin != null) {
            Map webappMap = (Map) loggedInSessions.get(userLogin.get("userLoginId"));
            if (webappMap == null) {
                return false;
            } else {
                String sessionId = (String) webappMap.get(UtilHttp.getApplicationName(request));
                if (sessionId == null || !sessionId.equals(request.getSession().getId())) { 
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
    
    public static void loginToSession(GenericValue userLogin, HttpServletRequest request) {
        if (userLogin != null) {
            Map webappMap = (Map) loggedInSessions.get(userLogin.get("userLoginId"));
            if (webappMap == null) {
                webappMap = new HashMap();
                loggedInSessions.put(userLogin.get("userLoginId"), webappMap);
            } 
    
            String webappName = UtilHttp.getApplicationName(request);
            webappMap.put(webappName, request.getSession().getId());
        }
    }
    
    public static void logoutFromAllSessions(GenericValue userLogin) {
        if (userLogin != null) {
            loggedInSessions.remove(userLogin.get("userLoginId"));
        }
    }
}
