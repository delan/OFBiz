/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project and repected authors.
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

package org.ofbiz.commonapp.party.party;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.service.*;

/**
 * <p><b>Title:</b> Services for Party Role maintenance
 * <p><b>Description:</b> None
 *
 * @author  <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version 1.0
 * @created January 26, 2002
 */
public class PartyRoleServices {
    /** Creates a PartyRole
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map createPartyRole(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_CREATE");
        if (result.size() > 0)
            return result;

        GenericValue partyRole = delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", context.get("roleTypeId")));
        try { 
            if (delegator.findByPrimaryKey(partyRole.getPrimaryKey()) != null) {
                return ServiceUtil.returnError("Could not create party role: already exists");
            }
        } catch(GenericEntityException e) {
            Debug.logWarning(e);
            return ServiceUtil.returnError("Could not create party role (read failure): " + e.getMessage());
        }

        try {
            partyRole.create();
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("Could create party role (write failure): " + e.getMessage());
        }
        
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Deletes a PartyRole
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map deletePartyRole(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_CREATE");
        if (result.size() > 0)
            return result;

        GenericValue partyRole = null;
        try { 
            partyRole = delegator.findByPrimaryKey("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", context.get("roleTypeId")));
        } catch(GenericEntityException e) {
            Debug.logWarning(e);
            return ServiceUtil.returnError("Could not delete party role (read failure): " + e.getMessage());
        }

        if(partyRole == null) {
            return ServiceUtil.returnError("Could not delete party role (partyRole not found)");
        }

        try {
            partyRole.remove();
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("Could delete party role (write failure): " + e.getMessage());
        }
        
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
}
