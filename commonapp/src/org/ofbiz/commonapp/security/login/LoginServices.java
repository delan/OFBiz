/*
 * $Id$
 */

package org.ofbiz.commonapp.security.login;

import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.security.*;

/**
 * <p><b>Title:</b> Login Services
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
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    January 26, 2002
 *@version    1.0
 */
public class LoginServices {

    /** Login service to authenticate username and password
     * @return Map of results including (userLogin) GenericValue object
     */
    public static Map userLogin(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();

        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security", "password.encrypt"));
        
        String username = (String) context.get("login.username");
        if (username == null) username = (String) context.get("username");
        String password = (String) context.get("login.password");
        if (password == null) password = (String) context.get("password");

        String errMsg = "";
        if(username == null || username.length() <= 0) {
            errMsg = "Username missing.";
        } else if(password == null || password.length() <= 0) {
            errMsg = "Password missing";
        } else {
            String realPassword = useEncryption ? HashEncrypt.getHash(password) : password;
            
            GenericValue userLogin = null;
            try {
                userLogin = delegator.findByPrimaryKeyCache("UserLogin", UtilMisc.toMap("userLoginId", username));
            } catch(GenericEntityException e) {
                Debug.logWarning(e);
            }
            
            if(userLogin != null) {
                String ldmStr = UtilProperties.getPropertyValue("security", "login.disable.minutes");
                long loginDisableMinutes = 30;
                try {
                    loginDisableMinutes = Long.parseLong(ldmStr);
                } catch (Exception e) {
                    loginDisableMinutes = 30;
                    Debug.logWarning("Could not parse login.disable.minutes from security.properties, using default of 30");
                }

                Timestamp disabledDateTime = userLogin.getTimestamp("disabledDateTime");
                Timestamp reEnableTime = null;
                if (loginDisableMinutes > 0 && disabledDateTime != null) {
                    reEnableTime = new Timestamp(disabledDateTime.getTime() + loginDisableMinutes*60000);
                }
                
                boolean doStore = true;
                if (UtilValidate.isEmpty(userLogin.getString("enabled")) || "Y".equals(userLogin.getString("enabled")) ||
                        (reEnableTime != null && reEnableTime.before(UtilDateTime.nowTimestamp()))) {
                
                    String successfulLogin;
                    userLogin.set("enabled", "Y");
                    //if the password.accept.encrypted.and.plain property in security is set to true allow plain or encrypted passwords
                    if (userLogin.get("currentPassword") != null && 
                            (realPassword.equals(userLogin.getString("currentPassword")) || 
                             ("true".equals(UtilProperties.getPropertyValue("security", "password.accept.encrypted.and.plain")) && password.equals(userLogin.getString("currentPassword"))))) {
                        Debug.logVerbose("[LoginServices.userLogin] : Password Matched");
                        
                        //reset failed login count if necessry
                        Long currentFailedLogins = userLogin.getLong("successiveFailedLogins");
                        if (currentFailedLogins != null && currentFailedLogins.longValue() > 0) {
                            userLogin.set("successiveFailedLogins", new Long(0));
                        } else {
                            //successful login, no need to change anything, so don't do the store
                            doStore = false;
                        }
                        
                        successfulLogin = "Y";

                        result.put("userLogin", userLogin);
                        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
                    } else {
                        Debug.logInfo("[LoginServices.userLogin] : Password Incorrect");
                        // password invalid...
                        errMsg = "Password incorrect.";

                        //increment failed login count
                        Long currentFailedLogins = userLogin.getLong("successiveFailedLogins");
                        if (currentFailedLogins == null) {
                            currentFailedLogins = new Long(1);
                        } else {
                            currentFailedLogins = new Long(currentFailedLogins.longValue() + 1);
                        }
                        userLogin.set("successiveFailedLogins", currentFailedLogins);

                        //if failed logins over amount in properties file, disable account
                        String mflStr = UtilProperties.getPropertyValue("security", "max.failed.logins");
                        long maxFailedLogins = 3;
                        try {
                            maxFailedLogins = Long.parseLong(mflStr);
                        } catch (Exception e) {
                            maxFailedLogins = 3;
                            Debug.logWarning("Could not parse max.failed.logins from security.properties, using default of 3");
                        }

                        if (maxFailedLogins > 0 && currentFailedLogins.longValue() >= maxFailedLogins) {
                            userLogin.set("enabled", "N");
                            userLogin.set("disabledDateTime", UtilDateTime.nowTimestamp());
                        }

                        successfulLogin = "N";
                    }
                    
                    if (doStore) {
                        try {
                            userLogin.store();
                        } catch(GenericEntityException e) {
                            Debug.logWarning(e);
                        }
                    }

                    if ("true".equals(UtilProperties.getPropertyValue("security", "store.login.history"))) {
                        boolean createHistory = true;
                        if (context.get("isServiceAuth") != null && ((Boolean) context.get("isServiceAuth")).booleanValue()) {
                            if (!"true".equals(UtilProperties.getPropertyValue("security", "store.login.history.on.service.auth"))) {
                                createHistory = false;
                            }
                        }
                        
                        if (createHistory) {
                            try {
                                delegator.create("UserLoginHistory", UtilMisc.toMap("userLoginId", username,
                                    "fromDate", UtilDateTime.nowTimestamp(), "passwordUsed", password,
                                    "partyId", userLogin.get("partyId"), "referrerUrl", "NotYetImplemented", "successfulLogin", successfulLogin));
                            } catch(GenericEntityException e) {
                                Debug.logWarning(e);
                            }
                        }
                    }
                } else {
                    errMsg = "The account for user login id \"" + username + "\" has been disabled since " + disabledDateTime + ".";

                    if (loginDisableMinutes > 0 && reEnableTime != null) {
                        errMsg += " It will be re-enabled " + reEnableTime + ".";
                    }
                }
            } else {
                //userLogin record not found, user does not exist
                errMsg = "User not found.";
                Debug.logInfo("[LoginServices.userLogin] : Invalid User");
            }
        }

        if (errMsg.length() > 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, errMsg);
        }
        return result;
    }

    /** Creates a UserLogin
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map createUserLogin(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue loggedInUserLogin = (GenericValue) context.get("userLogin");
        List errorMessageList = new LinkedList();

        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security", "password.encrypt"));
        
        String userLoginId = (String) context.get("userLoginId");
        String partyId = (String) context.get("partyId");
        String currentPassword = (String) context.get("currentPassword");
        String currentPasswordVerify = (String) context.get("currentPasswordVerify");
        String passwordHint = (String) context.get("passwordHint");

        //security: don't create a user login if the specified partyId (if not empty) already exists
        // unless the logged in user has permission to do so (same partyId or PARTYMGR_CREATE)
        if (partyId != null || partyId.length() > 0) {
            GenericValue party = null;
            try {
                party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
            } catch (GenericEntityException e) {
                Debug.logWarning(e.toString());
            }

            if (party != null) {
                if (loggedInUserLogin != null) {
                    //<b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_CREATE permission
                    if (!partyId.equals(loggedInUserLogin.getString("partyId"))) {
                        if (!security.hasEntityPermission("PARTYMGR", "_CREATE", loggedInUserLogin)) {
                            errorMessageList.add("Party with specified party ID exists and you do not have permission to create a user login with this party ID");
                        }
                    }
                } else {
                    errorMessageList.add("You must be logged in and have permission to create a user login with a party ID for a party that already exists");
                }
            }
        }

        checkNewPassword(null, null, currentPassword, currentPasswordVerify, passwordHint, errorMessageList, true);

        GenericValue userLoginToCreate = delegator.makeValue("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        userLoginToCreate.set("passwordHint", passwordHint);
        userLoginToCreate.set("partyId", partyId);
        userLoginToCreate.set("currentPassword", useEncryption ? HashEncrypt.getHash(currentPassword) : currentPassword);

        try {
            if (delegator.findByPrimaryKey(userLoginToCreate.getPrimaryKey()) != null) {
                errorMessageList.add("Could not create login user: user with ID \"" + userLoginId + "\" already exists");
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
            errorMessageList.add("Could not create login user (read failure): " + e.getMessage());
        }

        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }

        try {
            userLoginToCreate.create();
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("Could create login user (write failure): " + e.getMessage());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Updates UserLogin Password info
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map updatePassword(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue loggedInUserLogin = (GenericValue) context.get("userLogin");

        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security", "password.encrypt"));
        boolean adminUser = false;

        String userLoginId = (String) context.get("userLoginId");
        if (userLoginId == null || userLoginId.length() == 0) {
            userLoginId = loggedInUserLogin.getString("userLoginId");
        }

        //<b>security check</b>: userLogin userLoginId must equal userLoginId, or must have PARTYMGR_UPDATE permission
        //NOTE: must check permission first so that admin users can set own password without specifying old password
        if (!security.hasEntityPermission("PARTYMGR", "_UPDATE", loggedInUserLogin)) {
            if (!userLoginId.equals(loggedInUserLogin.getString("userLoginId"))) {
                return ServiceUtil.returnError("You do not have permission to update the password for this user login");
            }
        } else {
            adminUser = true;
        }

        GenericValue userLoginToUpdate = null;
        try {
            userLoginToUpdate = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        } catch(GenericEntityException e) {
            return ServiceUtil.returnError("Could not change password (read failure): " + e.getMessage());
        }

        if (userLoginToUpdate == null) {
            return ServiceUtil.returnError("Could not change password, UserLogin with ID \"" + userLoginId + "\" does not exist");
        }

        String currentPassword = (String) context.get("currentPassword");
        String newPassword = (String) context.get("newPassword");
        String newPasswordVerify = (String) context.get("newPasswordVerify");
        String passwordHint = (String) context.get("passwordHint");

        List errorMessageList = new LinkedList();

        if (newPassword != null && newPassword.length() > 0) {
        checkNewPassword(userLoginToUpdate, currentPassword, newPassword, newPasswordVerify,
                passwordHint, errorMessageList, adminUser);
        }

        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }

        userLoginToUpdate.set("currentPassword", useEncryption ? HashEncrypt.getHash(newPassword) : newPassword, false);
        userLoginToUpdate.set("passwordHint", passwordHint, false);

        try {
            userLoginToUpdate.store();
        } catch(GenericEntityException e) {
            return ServiceUtil.returnError("Could not change password (write failure): " + e.getMessage());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Updates UserLogin Security info
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map updateUserLoginSecurity(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue loggedInUserLogin = (GenericValue) context.get("userLogin");

        String userLoginId = (String) context.get("userLoginId");
        if (userLoginId == null || userLoginId.length() == 0) {
            userLoginId = loggedInUserLogin.getString("userLoginId");
        }

        //<b>security check</b>: must have PARTYMGR_UPDATE permission
        if (!security.hasEntityPermission("PARTYMGR", "_UPDATE", loggedInUserLogin) && !security.hasEntityPermission("SECURITY", "_UPDATE", loggedInUserLogin)) {
            return ServiceUtil.returnError("You do not have permission to update the security info for this user login");
        }

        GenericValue userLoginToUpdate = null;
        try {
            userLoginToUpdate = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        } catch(GenericEntityException e) {
            return ServiceUtil.returnError("Could not change password (read failure): " + e.getMessage());
        }

        if (userLoginToUpdate == null) {
            return ServiceUtil.returnError("Could not change password, UserLogin with ID \"" + userLoginId + "\" does not exist");
        }

        boolean wasEnabled = !"N".equals((String) userLoginToUpdate.get("enabled"));
        userLoginToUpdate.set("enabled", context.get("enabled"), false);
        userLoginToUpdate.set("disabledDateTime", context.get("disabledDateTime"), false);
        userLoginToUpdate.set("successiveFailedLogins", context.get("successiveFailedLogins"), false);
        
        //if was enabled and we are disabling it, and no disabledDateTime was passed, set it to now
        if (wasEnabled && "N".equals((String) context.get("enabled")) && context.get("disabledDateTime") == null) {
            userLoginToUpdate.set("disabledDateTime", UtilDateTime.nowTimestamp());
        }

        try {
            userLoginToUpdate.store();
        } catch(GenericEntityException e) {
            return ServiceUtil.returnError("Could not change password (write failure): " + e.getMessage());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    public static void checkNewPassword(GenericValue userLogin, String currentPassword, String newPassword, String newPasswordVerify, String passwordHint, List errorMessageList, boolean ignoreCurrentPassword) {
        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security", "password.encrypt"));
        
        if (!ignoreCurrentPassword) {
            String realPassword = currentPassword;
            if (useEncryption && currentPassword != null) {
                realPassword = HashEncrypt.getHash(currentPassword);
            }
            //if the password.accept.encrypted.and.plain property in security is set to true allow plain or encrypted passwords
            boolean passwordMatches = currentPassword != null && (realPassword.equals(userLogin.getString("currentPassword")) ||
                    ("true".equals(UtilProperties.getPropertyValue("security", "password.accept.encrypted.and.plain")) && currentPassword.equals(userLogin.getString("currentPassword"))));

            if ((currentPassword == null) || (userLogin != null && currentPassword != null && !passwordMatches)) {
                errorMessageList.add("Old Password was not correct, please re-enter.");
            }
        }

        if (!UtilValidate.isNotEmpty(newPassword) || !UtilValidate.isNotEmpty(newPasswordVerify)) {
            errorMessageList.add("Password or verify password missing.");
        } else if (!newPassword.equals(newPasswordVerify)) {
            errorMessageList.add("Password did not match verify password");
        }

        int minPasswordLength = 0;
        try {
            minPasswordLength = Integer.parseInt(UtilProperties.getPropertyValue("security", "password.length.min", "0"));
        } catch (NumberFormatException nfe) {
            minPasswordLength = 0;
        }

        if (newPassword != null) {
            if (!(newPassword.length() >= minPasswordLength)) {
                errorMessageList.add("Password must be at least " + minPasswordLength + " characters long");
            }
            if (userLogin != null && newPassword.equalsIgnoreCase(userLogin.getString("userLoginId"))) {
                errorMessageList.add("Password may not equal the Username");
            }
            if (UtilValidate.isNotEmpty(passwordHint) && (passwordHint.toUpperCase().indexOf(newPassword.toUpperCase()) >= 0)) {
                errorMessageList.add("Password hint may not contain the password");
            }
        }
    }
}
