/*
 * $Id$
 */

package org.ofbiz.commonapp.security.login;

import java.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

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
 *@created    Devember 20, 2001
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
        }
        catch ( GenericEntityException e ) {
            errors.add(e.getMessage());
        }
        if ( value != null ) {
            if( password.compareTo(value.getString("currentPassword")) == 0 ) {
                result.put("userLogin",value);
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
                Debug.logInfo("[LoginServices.userLogin] : Password Matched");
            }
            else {
                Debug.logInfo("[LoginServices.userLogin] : Password Incorrect");
                errors.add("Password incorrect.");
            }
        }
        else {
            errors.add("User not found.");
            Debug.logInfo("[LoginServices.userLogin] : Invalid User");
        }
        if ( errors.size() > 0 ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE_LIST,errors);
        }
        return result;
    }
    
}    
