/*
 * $Id$
 *
 * Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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
 * Services for Party/Person/Group maintenance
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    January 25, 2002
 *@version    1.0
 */
public class PartyServices {

    /** Deletes a Party
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map deleteParty(DispatchContext ctx, Map context) {
        /*
         * pretty serious operation, would delete:
         * - Party
         * - PartyRole
         * - PartyRelationship: from and to
         * - PartyDataObject
         * - Person or PartyGroup
         * - PartyContactMech, but not ContactMech itself
         * - PartyContactMechPurpose
         * - Order?
         *
         * We may want to not allow this, but rather have some sort of delete flag for it if it's REALLY that big of a deal...
         */
        return ServiceUtil.returnError("Cannot delete party, operation not yet implemented");
    }

    /** Creates a Person
     * If no partyId is specified a numeric partyId is retrieved from the Party sequence
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map createPerson(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        Timestamp now = UtilDateTime.nowTimestamp();
        Collection toBeStored = new LinkedList();

        String partyId = (String) context.get("partyId");

        //if specified partyId starts with a number, return an error
        if (partyId != null && Character.isDigit(partyId.charAt(0))) {
            return ServiceUtil.returnError("Cannot create person, specified party ID cannot start with a digit, " +
                                           "numeric IDs are reserved for auto-generated IDs");
        }

        //partyId might be empty, so check it and get next seq party id if empty
        if (partyId == null || partyId.length() == 0) {
            Long newId = delegator.getNextSeqId("Party");
            if (newId == null) {
                return ServiceUtil.returnError("ERROR: Could not create person (id generation failure)");
            } else {
                partyId = newId.toString();
            }
        }

        //check to see if party object exists, if so make sure it is PERSON type party
        GenericValue party = null;
        try {
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
        }

        if (party != null) {
            if (!"PERSON".equals(party.getString("partyTypeId"))) {
                return ServiceUtil.returnError("Cannot create person, a party with the specified party ID already " +
                                               "exists and is not a PERSON type party");
            }
        } else {
            //create a party if one doesn't already exist
            party = delegator.makeValue("Party", UtilMisc.toMap("partyId", partyId, "partyTypeId", "PERSON"));
            toBeStored.add(party);
        }

        GenericValue person = null;
        try {
            person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
        }

        if (person != null) {
            return ServiceUtil.returnError("Cannot create party, a person with the specified party ID already exists");
        }

        person = delegator.makeValue("Person", UtilMisc.toMap("partyId", partyId));
        toBeStored.add(person);

        person.set("firstName", context.get("firstName"), false);
        person.set("middleName", context.get("middleName"), false);
        person.set("lastName", context.get("lastName"), false);
        person.set("personalTitle", context.get("personalTitle"), false);
        person.set("suffix", context.get("suffix"), false);

        person.set("nickname", context.get("nickname"), false);
        person.set("gender", context.get("gender"), false);
        person.set("birthDate", context.get("birthDate"), false);
        person.set("height", context.get("height"), false);
        person.set("weight", context.get("weight"), false);
        person.set("mothersMaidenName", context.get("mothersMaidenName"), false);
        person.set("maritalStatus", context.get("maritalStatus"), false);
        person.set("socialSecurityNumber", context.get("socialSecurityNumber"), false);
        person.set("passportNumber", context.get("passportNumber"), false);
        person.set("passportExpireDate", context.get("passportExpireDate"), false);
        person.set("totalYearsWorkExperience", context.get("totalYearsWorkExperience"), false);
        person.set("comments", context.get("comments"), false);

        try {
            delegator.storeAll(toBeStored);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("Could not add person info (write failure): " + e.getMessage());
        }

        result.put("partyId", partyId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Updates a Person
     *<b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_UPDATE permission
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map updatePerson(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_UPDATE");
        if (result.size() > 0)
            return result;

        GenericValue person = null;
        try {
            person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
            return ServiceUtil.returnError("Could not update person information (read failure): " + e.getMessage());
        }

        if (person == null) {
            return ServiceUtil.returnError("Could not update person information (person not found)");
        }

        person.set("firstName", context.get("firstName"), false);
        person.set("middleName", context.get("middleName"), false);
        person.set("lastName", context.get("lastName"), false);
        person.set("personalTitle", context.get("personalTitle"), false);
        person.set("suffix", context.get("suffix"), false);

        person.set("nickname", context.get("nickname"), false);
        person.set("gender", context.get("gender"), false);
        person.set("birthDate", context.get("birthDate"), false);
        person.set("height", context.get("height"), false);
        person.set("weight", context.get("weight"), false);
        person.set("mothersMaidenName", context.get("mothersMaidenName"), false);
        person.set("maritalStatus", context.get("maritalStatus"), false);
        person.set("socialSecurityNumber", context.get("socialSecurityNumber"), false);
        person.set("passportNumber", context.get("passportNumber"), false);
        person.set("passportExpireDate", context.get("passportExpireDate"), false);
        person.set("totalYearsWorkExperience", context.get("totalYearsWorkExperience"), false);
        person.set("comments", context.get("comments"), false);

        try {
            person.store();
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("Could update personal information (write failure): " + e.getMessage());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Creates a PartyGroup
     * If no partyId is specified a numeric partyId is retrieved from the Party sequence
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map createPartyGroup(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp now = UtilDateTime.nowTimestamp();
        Collection toBeStored = new LinkedList();

        String partyId = (String) context.get("partyId");

        //if specified partyId starts with a number, return an error
        if (Character.isDigit(partyId.charAt(0))) {
            return ServiceUtil.returnError("Cannot create party group, specified party ID cannot start with a digit, " +
                                           "numeric IDs are reserved for auto-generated IDs");
        }

        //partyId might be empty, so check it and get next seq party id if empty
        if (partyId == null || partyId.length() == 0) {
            Long newId = delegator.getNextSeqId("Party");
            if (newId == null) {
                return ServiceUtil.returnError("ERROR: Could not create party group (id generation failure)");
            } else {
                partyId = newId.toString();
            }
        }

        //check to see if party object exists, if so make sure it is PARTY_GROUP type party
        GenericValue party = null;
        try {
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
        }

        if (party != null) {
            if (!"PARTY_GROUP".equals(party.getString("partyTypeId"))) {
                return ServiceUtil.returnError("Cannot create party group, a party with the specified party ID " +
                                               "already exists and is not a PARTY_GROUP type party");
            }
        } else {
            //create a party if one doesn't already exist
            party = delegator.makeValue("Party", UtilMisc.toMap("partyId", partyId, "partyTypeId", "PARTY_GROUP"));
            toBeStored.add(party);
        }

        GenericValue partyGroup = null;
        try {
            partyGroup = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
        }

        if (partyGroup != null) {
            return ServiceUtil.returnError("Cannot create party group, a party group with the specified " +
                                           "party ID already exists");
        }

        partyGroup = delegator.makeValue("PartyGroup", UtilMisc.toMap("partyId", partyId));
        toBeStored.add(partyGroup);

        partyGroup.set("groupName", context.get("groupName"), false);
        partyGroup.set("federalTaxId", context.get("federalTaxId"), false);

        try {
            delegator.storeAll(toBeStored);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("Could not add party group (write failure): " + e.getMessage());
        }

        result.put("partyId", partyId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Updates a PartyGroup
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map updatePartyGroup(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_UPDATE");
        if (result.size() > 0)
            return result;

        GenericValue partyGroup = null;
        try {
            partyGroup = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
            return ServiceUtil.returnError("Could not update party group information (read failure): " + e.getMessage());
        }

        if (partyGroup == null) {
            return ServiceUtil.returnError("Could not update party group information (partyGroup not found)");
        }

        partyGroup.set("groupName", context.get("groupName"), false);
        partyGroup.set("federalTaxId", context.get("federalTaxId"), false);

        try {
            partyGroup.store();
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("Could update party group information (write failure): " + e.getMessage());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Create an Affiliate entity
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map createAffiliate(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp now = UtilDateTime.nowTimestamp();

        String partyId = (String) context.get("partyId");
        if (partyId == null || partyId.length() == 0) {
            partyId = userLogin.getString("partyId");
        }

        //if specified partyId starts with a number, return an error
        if (Character.isDigit(partyId.charAt(0))) {
            return ServiceUtil.returnError("Cannot create affiliate, specified party ID cannot start with a digit, " +
                                           "numeric IDs are reserved for auto-generated IDs");
        }

        //partyId might be empty, so check it and get next seq party id if empty
        if (partyId == null || partyId.length() == 0) {
            Long newId = delegator.getNextSeqId("Party");
            if (newId == null) {
                return ServiceUtil.returnError("ERROR: Could not create affiliate (id generation failure)");
            } else {
                partyId = newId.toString();
            }
        }

        //check to see if party object exists, if so make sure it is AFFILIATE type party
        GenericValue party = null;
        try {
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
        }

        if (party == null) {
            return ServiceUtil.returnError("Cannot create affiliate; no party entity found.");
        }

        GenericValue affiliate = null;
        try {
            affiliate = delegator.findByPrimaryKey("Affiliate", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
        }

        if (affiliate != null) {
            return ServiceUtil.returnError("Cannot create, an affiliate with the specified party ID already exists");
        }

        affiliate = delegator.makeValue("Affiliate", UtilMisc.toMap("partyId", partyId));

        affiliate.set("affiliateName", context.get("affiliateName"), false);
        affiliate.set("affiliateDescription", context.get("affiliateDescription"), false);
        affiliate.set("yearEstablished", context.get("yearEstablished"), false);
        affiliate.set("siteType", context.get("siteType"), false);
        affiliate.set("sitePageViews", context.get("sitePageViews"), false);
        affiliate.set("siteVisitors", context.get("siteVisitors"), false);
        affiliate.set("dateTimeCreated", now, false);

        try {
            delegator.create(affiliate);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("Could not add affiliate info (write failure): " + e.getMessage());
        }

        result.put("partyId", partyId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Updates an Affiliate
     *<b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_UPDATE permission
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map updateAffiliate(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_UPDATE");
        if (result.size() > 0)
            return result;

        GenericValue affiliate = null;
        try {
            affiliate = delegator.findByPrimaryKey("Affiliate", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
            return ServiceUtil.returnError("Could not update affiliate information (read failure): " + e.getMessage());
        }

        if (affiliate == null) {
            return ServiceUtil.returnError("Could not update affiliate information (affiliate not found)");
        }

        affiliate.set("affiliateName", context.get("affiliateName"), false);
        affiliate.set("affiliateDescription", context.get("affiliateDescription"), false);
        affiliate.set("yearEstablished", context.get("yearEstablished"), false);
        affiliate.set("siteType", context.get("siteType"), false);
        affiliate.set("sitePageViews", context.get("sitePageViews"), false);
        affiliate.set("siteVisitors", context.get("siteVisitors"), false);

        try {
            affiliate.store();
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Could update affiliate information (write failure): " + e.getMessage());
        }
        return ServiceUtil.returnSuccess();
    }

    /** Create a party survey response record
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map createSurveyResp(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String partyId = (String) context.get("partyId");
        String surveyId = (String) context.get("surveyId");
        String responseId = (String) context.get("responseId");
        String response = (String) context.get("response");
        Timestamp now = UtilDateTime.nowTimestamp();
        if (partyId == null) {
            if (userLogin != null&& userLogin.get("partyId") != null)
                partyId = userLogin.getString("partyId");
        }
        if (partyId == null)
            return ServiceUtil.returnError("Could not create survey response (no partyId sent)");

        Map gFields = UtilMisc.toMap("partyId", partyId, "surveyId", surveyId, "surveyResponseId", responseId);
        Map sFields = UtilMisc.toMap("partyId", partyId, "surveyId", surveyId, "surveyResponseId", responseId,
                                    "surveyResponse", response, "responseDateTime", now);

        // look for existing record to update
        GenericValue getter = null;
        try {
            getter = delegator.findByPrimaryKey("PartySurveyResponse", gFields);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Cannot check survey response (read failure): " + e.getMessage());
        }
        if (getter != null) {
            getter.set("surveyResponse", response);
            getter.set("responseDateTime", now);
            try {
                getter.store();
            } catch (GenericEntityException ge) {
                return ServiceUtil.returnError("Cannot update survey response (write failure): " + ge.getMessage());
            }
        } else {
            try {
                GenericValue newValue = delegator.makeValue("PartySurveyResponse", sFields);
                delegator.create(newValue);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError("Cannot create survey response (write failure): " + e.getMessage());
            }
        }
        return ServiceUtil.returnSuccess();
    }

}
