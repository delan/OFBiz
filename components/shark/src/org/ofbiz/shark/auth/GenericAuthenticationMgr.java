/*
 * $Id$
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.shark.auth;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.shark.container.SharkContainer;
import org.ofbiz.base.util.UtilMisc;

import org.enhydra.shark.api.internal.authentication.AuthenticationManager;
import org.enhydra.shark.api.internal.working.CallbackUtilities;
import org.enhydra.shark.api.RootException;
import org.enhydra.shark.api.UserTransaction;

/**
 * Shark Generic Authentication Manager - Uses the Generic Entities
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      3.1
 */
public class GenericAuthenticationMgr implements AuthenticationManager {

    protected CallbackUtilities callBack = null;

    public void configure(CallbackUtilities callBack) throws RootException {
        this.callBack = callBack;
    }

    public boolean validateUser(UserTransaction userTransaction, String userName, String password) throws RootException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        GenericValue sharkUser = null;
        try {
            sharkUser = delegator.findByPrimaryKey("SharkUser", UtilMisc.toMap("userName", userName));
        } catch (GenericEntityException e) {
            throw new RootException(e);
        }

        if (sharkUser != null) {
            String registeredPwd = sharkUser.getString("passwd");
            if (password.equals(registeredPwd)) {
                return true;
            }
        }

        return false;
    }
}
