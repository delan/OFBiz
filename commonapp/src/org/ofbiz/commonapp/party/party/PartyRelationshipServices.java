/*
 * $Id$
 *
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
 * Services for Party Relationship maintenance
 *
 * @author  <a href="mailto:cworley@chris-n-april.com">Christopher Worley</a>
 * @author  <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version 1.0
 * @created March 13, 2002
 */
public class PartyRelationshipServices {

    /** Creates a PartyRelationship
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map createPartyRelationship(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_CREATE");

        if (result.size() > 0)
            return result;

        String partyIdFrom = (String) context.get("partyIdFrom");

        if (partyIdFrom == null) {
            partyIdFrom = (String) userLogin.getString("partyId");
        }

        String partyIdTo = (String) context.get("partyIdTo");

        if (partyIdTo == null) {
            return ServiceUtil.returnError("Cannot create party relationship, partyIdTo cannot be null.");
        }

        String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");

        if (roleTypeIdFrom == null) {
            roleTypeIdFrom = "_NA_";
        }

        String roleTypeIdTo = (String) context.get("roleTypeIdTo");

        if (roleTypeIdTo == null) {
            roleTypeIdTo = "_NA_";
        }

        Timestamp fromDate = (Timestamp) context.get("fromDate");

        if (fromDate == null) {
            fromDate = UtilDateTime.nowTimestamp();
        }

        GenericValue partyRelationship = delegator.makeValue("PartyRelationship", UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", roleTypeIdTo, "fromDate", fromDate));

        partyRelationship.set("thruDate", context.get("thruDate"), false);
        partyRelationship.set("priorityTypeId", context.get("priorityTypeId"), false);
        partyRelationship.set("comments", context.get("comments"), false);
        partyRelationship.set("partyRelationshipTypeId", context.get("partyRelationshipTypeId"), false);

        try {
            if (delegator.findByPrimaryKey(partyRelationship.getPrimaryKey()) != null) {
                return ServiceUtil.returnError("Could not create party relationship: already exists");
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
            return ServiceUtil.returnError("Could not create party role (read failure): " + e.getMessage());
        }

        try {
            partyRelationship.create();
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("Could not create party relationship (write failure): " + e.getMessage());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Updates a PartyRelationship
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map updatePartyRelationship(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_UPDATE");

        if (result.size() > 0)
            return result;

        String partyIdFrom = (String) context.get("partyIdFrom");

        if (partyIdFrom == null) {
            partyIdFrom = (String) userLogin.getString("partyId");
        }

        String partyIdTo = (String) context.get("partyIdTo");

        if (partyIdTo == null) {
            return ServiceUtil.returnError("Cannot create party relationship, partyIdTo cannot be null.");
        }

        String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");

        if (roleTypeIdFrom == null) {
            roleTypeIdFrom = "_NA_";
        }

        String roleTypeIdTo = (String) context.get("roleTypeIdTo");

        if (roleTypeIdTo == null) {
            roleTypeIdTo = "_NA_";
        }

        GenericValue partyRelationship = null;

        try {
            partyRelationship = delegator.findByPrimaryKey("PartyRelationship", UtilMisc.toMap("partyIdFrom", partyIdFrom,
                            "partyIdTo", partyIdTo, "roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", roleTypeIdTo, "fromDate", context.get("fromDate")));
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
            return ServiceUtil.returnError("Could not update party realtion (read failure): " + e.getMessage());
        }

        if (partyRelationship == null) {
            return ServiceUtil.returnError("Could not update party relationship (relationship not found)");
        }

        partyRelationship.set("thruDate", context.get("thruDate"), false);
        partyRelationship.set("statusId", context.get("statusId"), false);
        partyRelationship.set("priorityTypeId", context.get("priorityTypeId"), false);
        partyRelationship.set("partyRelationshipTypeId", context.get("partyRelationshipTypeId"), false);
        partyRelationship.set("comments", context.get("comments"), false);

        try {
            partyRelationship.store();
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("Could not update party relationship (write failure): " + e.getMessage());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Deletes a PartyRelationship
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map deletePartyRelationship(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_CREATE");

        if (result.size() > 0)
            return result;

        GenericValue partyRelationship = null;

        try {
            partyRelationship = delegator.findByPrimaryKey("PartyRelationship", UtilMisc.toMap("partyIdFrom", context.get("partyIdFrom"), "partyIdTo", context.get("partyIdTo"), "roleTypeIdFrom", context.get("roleTypeIdFrom"), "roleTypeIdTo", context.get("roleTypeIdTo"), "fromDate", context.get("fromDate")));
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
            return ServiceUtil.returnError("Could not delete party relationship (read failure): " + e.getMessage());
        }

        if (partyRelationship == null) {
            return ServiceUtil.returnError("Could not delete party relationship (partyRelationship not found)");
        }

        try {
            partyRelationship.remove();
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("Could delete party role (write failure): " + e.getMessage());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Creates a PartyRelationshipType
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map createPartyRelationshipType(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_CREATE");

        if (result.size() > 0)
            return result;

        GenericValue partyRelationshipType = delegator.makeValue("PartyRelationshipType", UtilMisc.toMap("partyRelationshipTypeId", context.get("partyRelationshipTypeId")));

        partyRelationshipType.set("parentTypeId", context.get("parentTypeId"), false);
        partyRelationshipType.set("hasTable", context.get("hasTable"), false);
        partyRelationshipType.set("roleTypeIdValidFrom", context.get("roleTypeIdValidFrom"), false);
        partyRelationshipType.set("roleTypeIdValidTo", context.get("roleTypeIdValidTo"), false);
        partyRelationshipType.set("description", context.get("description"), false);
        partyRelationshipType.set("partyRelationshipName", context.get("partyRelationshipName"), false);

        try {
            if (delegator.findByPrimaryKey(partyRelationshipType.getPrimaryKey()) != null) {
                return ServiceUtil.returnError("Could not create party relationship type: already exists");
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
            return ServiceUtil.returnError("Could not create party relationship type (read failure): " + e.getMessage());
        }

        try {
            partyRelationshipType.create();
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("Could not create party relationship type (write failure): " + e.getMessage());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

}
