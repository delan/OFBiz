/*
 * $Id$
 */

package org.ofbiz.commonapp.security.login;

import java.util.*;
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
        List errors = new ArrayList();
        GenericDelegator delegator = ctx.getDelegator();
        
        String username = (String) context.get("login.username");
        String password = (String) context.get("login.password");
        GenericValue value = null;
        
        try {
            value = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", username));
        } catch ( GenericEntityException e ) {
            errors.add(e.getMessage());
        }
        if (value != null) {
            if( password.compareTo(value.getString("currentPassword")) == 0 ) {
                result.put("userLogin",value);
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
                Debug.logInfo("[LoginServices.userLogin] : Password Matched");
            } else {
                Debug.logInfo("[LoginServices.userLogin] : Password Incorrect");
                errors.add("Password incorrect.");
            }
        } else {
            errors.add("User not found.");
            Debug.logInfo("[LoginServices.userLogin] : Invalid User");
        }
        if (errors.size() > 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE_LIST,errors);
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

        String userLoginId = (String) context.get("userLoginId");
        String partyId = (String) context.get("partyId");
        String newPassword = (String) context.get("currentPassword");
        String newPasswordVerify = (String) context.get("currentPasswordVerify");
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
        
        checkNewPassword(null, null, newPassword, newPasswordVerify, passwordHint, errorMessageList);

        GenericValue userLoginToCreate = delegator.makeValue("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        userLoginToCreate.set("passwordHint", passwordHint);
        userLoginToCreate.set("partyId", partyId);
        userLoginToCreate.set("currentPassword", newPassword);
        
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
        
        String userLoginId = (String) context.get("userLoginId");
        if (userLoginId == null || userLoginId.length() == 0) {
            userLoginId = loggedInUserLogin.getString("userLoginId");
        }
        
        //<b>security check</b>: userLogin userLoginId must equal userLoginId, or must have PARTYMGR_UPDATE permission
        if (!userLoginId.equals(loggedInUserLogin.getString("userLoginId"))) {
            if (!security.hasEntityPermission("PARTYMGR", "_UPDATE", loggedInUserLogin)) {
                return ServiceUtil.returnError("You do not have permission to update the password for this party");
            }
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
        checkNewPassword(userLoginToUpdate, currentPassword, newPassword, newPasswordVerify, passwordHint, errorMessageList);
        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }

        userLoginToUpdate.set("currentPassword", newPassword);
        userLoginToUpdate.set("passwordHint", passwordHint);
        
        try {
            userLoginToUpdate.store();
        } catch(GenericEntityException e) {
            return ServiceUtil.returnError("Could not change password (write failure): " + e.getMessage());
        }
        
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
    public static void checkNewPassword(GenericValue userLogin, String currentPassword, String newPassword, String newPasswordVerify, String passwordHint, List errorMessageList) {
        if (userLogin != null && currentPassword != null && !currentPassword.equals(userLogin.getString("currentPassword"))) {
            //password was NOT correct, send back to changepassword page with an error
            errorMessageList.add("Old Password was not correct, please re-enter.");
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
