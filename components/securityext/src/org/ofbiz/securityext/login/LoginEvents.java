/*
 * $Id: LoginEvents.java,v 1.16 2004/07/04 14:50:25 ajzeneski Exp $
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
package org.ofbiz.securityext.login;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.content.stats.VisitHandler;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

/**
 * LoginEvents - Events for UserLogin and Security handling.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="">Dustin Caldwell</a>
 * @author     <a href="mailto:therrick@yahoo.com">Tom Herrick</a>
 * @version    $Revision: 1.16 $
 * @since      2.0
 */
public class LoginEvents {

    public static final String module = LoginEvents.class.getName();
    public static final String resource = "SecurityextUiLabels";

    public static final String EXTERNAL_LOGIN_KEY_ATTR = "externalLoginKey";

    /**
     * Save USERNAME and PASSWORD for use by auth pages even if we start in non-auth pages.
     *
     * @param request The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @return
     */
    public static String saveEntryParams(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
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
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        HttpSession session = request.getSession();

        // anonymous shoppers are not logged in
        if (userLogin != null && "anonymous".equals(userLogin.getString("userLoginId"))) {
            userLogin = null;
        }

        // user is logged in; check to see if there is an entry in the loggedInSessions Map, if not log out this user
        // also check if they have permission for this login attempt; if not log them out as well.
        if (userLogin != null) {
            boolean loggedInSession = PersistedLoginContainer.isLoggedInSession(userLogin, request);
            boolean hasBasePermission = hasBasePermission(userLogin, request);
            if (!loggedInSession || !hasBasePermission) {
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
                Debug.logInfo("reqParams Map: " + reqParams, module);
                Debug.logInfo("queryString: " + queryString, module);

                session.setAttribute("_PREVIOUS_REQUEST_", request.getPathInfo());
                if (queryString != null && queryString.length() > 0) {
                    session.setAttribute("_PREVIOUS_PARAMS_", queryString);
                }

                if (Debug.infoOn()) Debug.logInfo("checkLogin: queryString=" + queryString, module);
                if (Debug.infoOn()) Debug.logInfo("checkLogin: PathInfo=" + request.getPathInfo(), module);

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
     */
    public static String login(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        String username = request.getParameter("USERNAME");
        String password = request.getParameter("PASSWORD");
        String errMsg = null;

        if (username == null) username = (String) session.getAttribute("USERNAME");
        if (password == null) password = (String) session.getAttribute("PASSWORD");

        if ((username != null) && ("true".equalsIgnoreCase(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
            username = username.toLowerCase();
        }
        if ((password != null) && ("true".equalsIgnoreCase(UtilProperties.getPropertyValue("security.properties", "password.lowercase")))) {
            password = password.toLowerCase();
        }

        if ("true".equalsIgnoreCase(UtilProperties.getPropertyValue("security.properties", "login.lock.active"))) {
            boolean userIdLoggedIn = PersistedLoginContainer.isLoggedInSession(username, request, false);
            boolean thisUserLoggedIn = PersistedLoginContainer.isLoggedInSession(username, request, true);
            if (userIdLoggedIn && !thisUserLoggedIn) {
                errMsg = UtilProperties.getMessage(resource, "loginevents.user_already_logged_in", UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", "<b>" + errMsg + "</b>");
                return "error";
            }
        }

        // get the visit id to pass to the userLogin for history
        String visitId = VisitHandler.getVisitId(session);

        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map result = null;

        try {
            result = dispatcher.runSync("userLogin", UtilMisc.toMap("login.username", username, "login.password", password, "visitId", visitId, "locale", UtilHttp.getLocale(request)));
        } catch (GenericServiceException e) {
            Debug.logError(e, "Error calling userLogin service", module);
            Map messageMap = UtilMisc.toMap("errorMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(resource, "loginevents.following_error_occurred_during_login", messageMap, UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg );
            return "error";
        }

        if (ModelService.RESPOND_SUCCESS.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
            GenericValue userLogin = (GenericValue) result.get("userLogin");
            Map userLoginSession = (Map) result.get("userLoginSession");

            if (userLogin != null && hasBasePermission(userLogin, request)) {
                doBasicLogin(userLogin, request);
            } else {
                errMsg = UtilProperties.getMessage(resource,"loginevents.unable_to_login_this_application", UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg );
                return "error";
            }

            if (userLoginSession != null) {
                session.setAttribute("userLoginSession", userLoginSession);
            }
        } else {
            Map messageMap = UtilMisc.toMap("errorMessage", (String) result.get(ModelService.ERROR_MESSAGE));
            errMsg = UtilProperties.getMessage(resource,"loginevents.following_error_occurred_during_login", messageMap, UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        request.setAttribute("_LOGIN_PASSED_", "TRUE");
        // make sure the autoUserLogin is set to the same and that the client cookie has the correct userLoginId
        return autoLoginSet(request, response);
    }

    public static void doBasicLogin(GenericValue userLogin, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("userLogin", userLogin);
        // let the visit know who the user is
        VisitHandler.setUserLogin(session, userLogin, false);
        PersistedLoginContainer.loginToSession(userLogin, request);
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
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

        // log out from all other sessions too; do this here so that it is only done when a user explicitly logs out
        PersistedLoginContainer.logoutFromAllSessions(userLogin);

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
        // also save the shopping cart if we have one
        // DON'T save the cart, causes too many problems: security issues with things done in cart to easy to miss, especially bad on public systems; was put in here because of the "not me" link for auto-login stuff, but that is a small problem compared to what it causes
        //ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute("shoppingCart");

        session.invalidate();
        session = request.getSession(true);

        if (currCatalog != null) session.setAttribute("CURRENT_CATALOG_ID", currCatalog);
        if (delegatorName != null) session.setAttribute("delegatorName", delegatorName);
        // DON'T save the cart, causes too many problems: if (shoppingCart != null) session.setAttribute("shoppingCart", new WebShoppingCart(shoppingCart, session));
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
        String errMsg = null;

        if ((userLoginId != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
            userLoginId = userLoginId.toLowerCase();
        }

        if (!UtilValidate.isNotEmpty(userLoginId)) {
            // the password was incomplete
            errMsg = UtilProperties.getMessage(resource,"loginevents.username_was_empty_reenter", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", "<li>" + errMsg);
            return "error";
        }

        GenericValue supposedUserLogin = null;

        try {
            supposedUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        } catch (GenericEntityException gee) {
            Debug.logWarning(gee, "", module);
        }
        if (supposedUserLogin == null) {
            // the Username was not found
            errMsg = UtilProperties.getMessage(resource,"loginevents.username_not_found_reenter", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", "<li>" + errMsg);
            return "error";
        }

        String passwordHint = supposedUserLogin.getString("passwordHint");

        if (!UtilValidate.isNotEmpty(passwordHint)) {
            // the Username was not found
            errMsg = UtilProperties.getMessage(resource,"loginevents.no_password_hint_specified_try_password_emailed", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", "<li>" + errMsg);
            return "error";
        }

        Map messageMap = UtilMisc.toMap("passwordHint", passwordHint);
        errMsg = UtilProperties.getMessage(resource,"loginevents.password_hint_is", messageMap, UtilHttp.getLocale(request));
        request.setAttribute("_ERROR_MESSAGE_", errMsg);
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
        String productStoreId = ProductStoreWorker.getProductStoreId(request);
        String errMsg = null;

        Map subjectData = new HashMap();
        subjectData.put("productStoreId", productStoreId);

        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));

        String userLoginId = request.getParameter("USERNAME");
        subjectData.put("userLoginId", userLoginId);

        if ((userLoginId != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
            userLoginId = userLoginId.toLowerCase();
        }

        if (!UtilValidate.isNotEmpty(userLoginId)) {
            // the password was incomplete
            errMsg = UtilProperties.getMessage(resource,"loginevents.username_was_empty_reenter", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", "<li>" + errMsg);
            return "error";
        }

        GenericValue supposedUserLogin = null;
        String passwordToSend = null;

        try {
            supposedUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
            if (supposedUserLogin == null) {
                // the Username was not found
                errMsg = UtilProperties.getMessage(resource,"loginevents.username_not_found_reenter", UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", "<li>" + errMsg);
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
            Debug.logWarning(e, "", module);
            Map messageMap = UtilMisc.toMap("errorMessage", e.toString());
            errMsg = UtilProperties.getMessage(resource,"loginevents.error_accessing_password", messageMap, UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        if (supposedUserLogin == null) {
            // the Username was not found
            Map messageMap = UtilMisc.toMap("userLoginId", userLoginId);
            errMsg = UtilProperties.getMessage(resource,"loginevents.user_with_the_username_not_found", messageMap, UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        StringBuffer emails = new StringBuffer();
        GenericValue party = null;

        try {
            party = supposedUserLogin.getRelatedOne("Party");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", module);
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
            errMsg = UtilProperties.getMessage(resource,"loginevents.no_primary_email_address_set_contact_customer_service", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        // get the ProductStore email settings
        GenericValue productStoreEmail = null;
        try {
            productStoreEmail = delegator.findByPrimaryKey("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId", productStoreId, "emailType", "PRDS_PWD_RETRIEVE"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting ProductStoreEmailSetting", module);
        }

        if (productStoreEmail == null) {
            errMsg = UtilProperties.getMessage(resource,"loginevents.problems_with_configuration_contact_customer_service", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        // need OFBIZ_HOME for processing
        String ofbizHome = System.getProperty("ofbiz.home");

        // set the needed variables in new context
        Map templateData = new HashMap();
        templateData.put("useEncryption", new Boolean(useEncryption));
        templateData.put("password", UtilFormatOut.checkNull(passwordToSend));

        // prepare the parsed subject
        String subjectString = productStoreEmail.getString("subject");
        subjectString = FlexibleStringExpander.expandString(subjectString, subjectData);

        Map serviceContext = new HashMap();
        serviceContext.put("templateName", ofbizHome + productStoreEmail.get("templatePath"));
        serviceContext.put("templateData", templateData);
        serviceContext.put("subject", subjectString);
        serviceContext.put("sendFrom", productStoreEmail.get("fromAddress"));
        serviceContext.put("sendCc", productStoreEmail.get("ccAddress"));
        serviceContext.put("sendBcc", productStoreEmail.get("bccAddress"));
        serviceContext.put("contentType", productStoreEmail.get("contentType"));
        serviceContext.put("sendTo", emails.toString());

        try {
            Map result = dispatcher.runSync("sendGenericNotificationEmail", serviceContext);

            if (ModelService.RESPOND_ERROR.equals((String) result.get(ModelService.RESPONSE_MESSAGE))) {
                Map messageMap = UtilMisc.toMap("errorMessage", result.get(ModelService.ERROR_MESSAGE));
                errMsg = UtilProperties.getMessage(resource,"loginevents.error_unable_email_password_contact_customer_service_errorwas", messageMap, UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
        } catch (GenericServiceException e) {
            Debug.logWarning(e, "", module);
            errMsg = UtilProperties.getMessage(resource,"loginevents.error_unable_email_password_contact_customer_service", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        // don't save password until after it has been sent
        if (useEncryption) {
            try {
                supposedUserLogin.store();
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "", module);
                Map messageMap = UtilMisc.toMap("errorMessage", e.toString());
                errMsg = UtilProperties.getMessage(resource,"loginevents.error_saving_new_password_email_not_correct_password", messageMap, UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
        }

        if (useEncryption) {
            errMsg = UtilProperties.getMessage(resource,"loginevents.new_password_createdandsent_check_email", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
        } else {
            errMsg = UtilProperties.getMessage(resource,"loginevents.new_password_sent_check_email", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
        }
        return "success";
    }

    protected static String getAutoLoginCookieName(HttpServletRequest request) {
        return UtilHttp.getApplicationName(request) + ".autoUserLoginId";
    }

    public static String getAutoUserLoginId(HttpServletRequest request) {
        String autoUserLoginId = null;
        Cookie[] cookies = request.getCookies();
        if (Debug.verboseOn()) Debug.logVerbose("Cookies:" + cookies, module);
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
            Debug.logInfo("Running autoLogin check.", module);
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
                Debug.logError(e, "Cannot get autoUserLogin information: " + e.getMessage(), module);
            }
        }
        return "success";
    }

    public static String autoLoginSet(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
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
        //Debug.logInfo("Running getExternalLoginKey, externalLoginKeys.size=" + externalLoginKeys.size(), module);
        GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");

        String externalKey = (String) request.getAttribute(EXTERNAL_LOGIN_KEY_ATTR);
        if (externalKey != null) return externalKey;

        HttpSession session = request.getSession();
        synchronized (session) {
            // if the session has a previous key in place, remove it from the master list
            String sesExtKey = (String) session.getAttribute(EXTERNAL_LOGIN_KEY_ATTR);
            if (sesExtKey != null) {
                PersistedLoginContainer.removeExternalLoginKey(sesExtKey);
            }

            //check the userLogin here, after the old session setting is set so that it will always be cleared
            if (userLogin == null) return "";

            externalKey = PersistedLoginContainer.createExternalLoginKey();

            request.setAttribute(EXTERNAL_LOGIN_KEY_ATTR, externalKey);
            session.setAttribute(EXTERNAL_LOGIN_KEY_ATTR, externalKey);
            PersistedLoginContainer.setExternalLoginKey(externalKey, userLogin);
            return externalKey;
        }
    }

    public static String checkExternalLoginKey(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        String externalKey = request.getParameter(EXTERNAL_LOGIN_KEY_ATTR);
        if (externalKey == null) return "success";

        GenericValue userLogin = PersistedLoginContainer.getExternalLoginKeyValue(externalKey);
        if (userLogin != null) {
            // found userLogin, do the external login...

            // if the user is already logged in and the login is different, logout the other user
            GenericValue currentUserLogin = (GenericValue) session.getAttribute("userLogin");
            if (currentUserLogin != null) {
                if (currentUserLogin.getString("userLoginId").equals(userLogin.getString("userLoginId"))) {
                    // is the same user, just carry on...
                    return "success";
                }

                // logout the current user and login the new user...
                String logoutRetVal = logout(request, response);
                // ignore the return value; even if the operation failed we want to set the new UserLogin
            }

            if ("true".equalsIgnoreCase(UtilProperties.getPropertyValue("security.properties", "login.lock.active"))) {
                String username = userLogin.getString("userLoginId");
                boolean userIdLoggedIn = PersistedLoginContainer.isLoggedInSession(username, request, false);
                boolean thisUserLoggedIn = PersistedLoginContainer.isLoggedInSession(username, request, true);
                if (userIdLoggedIn && !thisUserLoggedIn) {
                    request.setAttribute("_ERROR_MESSAGE_", "<b>This user is already logged in.</b><br>");
                    return "error";
                }
            }

            doBasicLogin(userLogin, request);
        } else {
            Debug.logWarning("Could not find userLogin for external login key: " + externalKey, module);
        }

        return "success";
    }

    protected static boolean hasBasePermission(GenericValue userLogin, HttpServletRequest request) {
        ServletContext context = (ServletContext) request.getAttribute("servletContext");
        Security security = (Security) request.getAttribute("security");
        HttpSession session = request.getSession();

        String serverId = (String) context.getAttribute("_serverId");
        String contextPath = request.getContextPath();

        ComponentConfig.WebappInfo info = ComponentConfig.getWebAppInfo(serverId, contextPath);
        if (security != null) {
            if (info != null) {
                String permission = info.getBasePermission();
                if (!"NONE".equals(permission) && !security.hasEntityPermission(permission, "_VIEW", userLogin)) {
                    return false;
                }
            } else {
                Debug.logInfo("No webapp configuration found for : " + serverId + " / " + contextPath, module);
            }
        } else {
            Debug.logWarning("Received a null Security object from HttpServletRequest", module);
        }

        return true;

    }
}
