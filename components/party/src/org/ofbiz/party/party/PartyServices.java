/*
 * $Id: PartyServices.java,v 1.6 2004/07/03 19:54:23 jonesde Exp $
 *
 * Copyright (c) 2001, 2002, 2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.party.party;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * Services for Party/Person/Group maintenance
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.6 $
 * @since      2.0
 */
public class PartyServices {

    public static final String module = PartyServices.class.getName();
    public static final String resource = "PartyUiLabels";

    /**
     * Deletes a Party.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map deleteParty(DispatchContext ctx, Map context) {

        Locale locale = (Locale) context.get("locale");

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

        String errMsg = UtilProperties.getMessage(resource,"partyservices.cannot_delete_party_not_implemented", locale);
        return ServiceUtil.returnError(errMsg);
    }

    /**
     * Creates a Person.
     * If no partyId is specified a numeric partyId is retrieved from the Party sequence.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map createPerson(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        Timestamp now = UtilDateTime.nowTimestamp();
        List toBeStored = new LinkedList();
        Locale locale = (Locale) context.get("locale");
        // in most cases userLogin will be null, but get anyway so we can keep track of that info if it is available
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String partyId = (String) context.get("partyId");

        // if specified partyId starts with a number, return an error
        if (partyId != null && Character.isDigit(partyId.charAt(0))) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "party.id_is_digit", locale));
        }

        // partyId might be empty, so check it and get next seq party id if empty
        if (partyId == null || partyId.length() == 0) {
            try {
                partyId = delegator.getNextSeqId("Party");
            } catch (IllegalArgumentException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "party.id_generation_failure", locale));
            }
        }

        // check to see if party object exists, if so make sure it is PERSON type party
        GenericValue party = null;

        try {
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }

        if (party != null) {
            if (!"PERSON".equals(party.getString("partyTypeId"))) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "person.create.party_exists_not_person_type", locale));
            }
        } else {
            // create a party if one doesn't already exist
            Map newPartyMap = UtilMisc.toMap("partyId", partyId, "partyTypeId", "PERSON", "createdDate", now, "lastModifiedDate", now);
            if (userLogin != null) {
                newPartyMap.put("createdByUserLogin", userLogin.get("userLoginId"));
                newPartyMap.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
            }
            party = delegator.makeValue("Party", newPartyMap);
            toBeStored.add(party);
        }

        GenericValue person = null;

        try {
            person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }

        if (person != null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "person.create.person_exists", locale));
        }

        person = delegator.makeValue("Person", UtilMisc.toMap("partyId", partyId));
        person.setNonPKFields(context);
        toBeStored.add(person);

        try {
            delegator.storeAll(toBeStored);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "person.create.db_error", new Object[] { e.getMessage() }, locale));
        }

        result.put("partyId", partyId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /**
     * Updates a Person.
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_UPDATE permission.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map updatePerson(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_UPDATE");

        if (result.size() > 0)
            return result;

        GenericValue person = null;

        try {
            person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "person.update.read_failure", new Object[] { e.getMessage() }, locale));
        }

        if (person == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "person.update.not_found", locale));
        }

        person.setNonPKFields(context);

        try {
            person.store();
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "person.update.write_failure", new Object[] { e.getMessage() }, locale));
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resource, "person.update.success", locale));
        return result;
    }

    /**
     * Creates a PartyGroup.
     * If no partyId is specified a numeric partyId is retrieved from the Party sequence.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map createPartyGroup(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp now = UtilDateTime.nowTimestamp();

        String partyId = (String) context.get("partyId");
        Locale locale = (Locale) context.get("locale");
        String errMsg = null;

        // partyId might be empty, so check it and get next seq party id if empty
        if (partyId == null || partyId.length() == 0) {
            try {
                partyId = delegator.getNextSeqId("Party");
            } catch (IllegalArgumentException e) {
                errMsg = UtilProperties.getMessage(resource,"partyservices.could_not_create_party_group_generation_failure", locale);
                return ServiceUtil.returnError(errMsg);
            }
        } else {
            // if specified partyId starts with a number, return an error
            if (Character.isDigit(partyId.charAt(0))) {
                errMsg = UtilProperties.getMessage(resource,"partyservices.could_not_create_party_ID_digit", locale);
                return ServiceUtil.returnError(errMsg);
            }
        }

        try {
            // check to see if party object exists, if so make sure it is PARTY_GROUP type party
            GenericValue party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
            GenericValue partyGroupPartyType = delegator.findByPrimaryKeyCache("PartyType", UtilMisc.toMap("partyTypeId", "PARTY_GROUP"));

            if (partyGroupPartyType == null) {
                errMsg = UtilProperties.getMessage(resource,"partyservices.party_type_not_found_in_database_cannot_create_party_group", locale);
                return ServiceUtil.returnError(errMsg);
            }

            if (party != null) {
                GenericValue partyType = party.getRelatedOneCache("PartyType");

                if (!EntityTypeUtil.isType(partyType, partyGroupPartyType)) {
                    errMsg = UtilProperties.getMessage(resource,"partyservices.cannot_create_party_group_already_exists_not_PARTY_GROUP_type", locale);
                    return ServiceUtil.returnError(errMsg);
                }
            } else {
                // create a party if one doesn't already exist
                String partyTypeId = "PARTY_GROUP";

                if (UtilValidate.isNotEmpty(((String) context.get("partyTypeId")))) {
                    GenericValue desiredPartyType = delegator.findByPrimaryKeyCache("PartyType", UtilMisc.toMap("partyTypeId", context.get("partyTypeId")));
                    if (desiredPartyType != null && EntityTypeUtil.isType(desiredPartyType, partyGroupPartyType)) {
                        partyTypeId = desiredPartyType.getString("partyTypeId");
                    } else {
                        return ServiceUtil.returnError("The specified partyTypeId [" + context.get("partyTypeId") + "] could not be found or is not a sub-type of PARTY_GROUP");
                    }
                }

                Map newPartyMap = UtilMisc.toMap("partyId", partyId, "partyTypeId", partyTypeId, "createdDate", now, "lastModifiedDate", now);
                if (userLogin != null) {
                    newPartyMap.put("createdByUserLogin", userLogin.get("userLoginId"));
                    newPartyMap.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
                }
                party = delegator.makeValue("Party", newPartyMap);
                party.create();
            }

            GenericValue partyGroup = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", partyId));
            if (partyGroup != null) {
                errMsg = UtilProperties.getMessage(resource,"partyservices.cannot_create_party_group_already_exists", locale);
                return ServiceUtil.returnError(errMsg);
            }

            partyGroup = delegator.makeValue("PartyGroup", UtilMisc.toMap("partyId", partyId));
            partyGroup.setNonPKFields(context);
            partyGroup.create();
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            Map messageMap = UtilMisc.toMap("errMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(resource,"partyservices.data_source_error_adding_party_group", messageMap, locale);
            return ServiceUtil.returnError(errMsg);
        }

        result.put("partyId", partyId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /**
     * Updates a PartyGroup.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map updatePartyGroup(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_UPDATE");
        Locale locale = (Locale) context.get("locale");
        String errMsg = null;

        if (result.size() > 0)
            return result;

        GenericValue partyGroup = null;
        GenericValue party = null;

        try {
            partyGroup = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", partyId));
            party = partyGroup.getRelatedOne("Party");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            Map messageMap = UtilMisc.toMap("errMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(resource,"partyservices.could_not_update_party_information_read", messageMap, locale);
            return ServiceUtil.returnError(errMsg);
        }

        if (partyGroup == null || party == null) {
            errMsg = UtilProperties.getMessage(resource,"partyservices.could_not_update_party_information_not_found", locale);
            return ServiceUtil.returnError(errMsg);
        }

        partyGroup.setNonPKFields(context);
        party.setNonPKFields(context);

        try {
            partyGroup.store();
            party.store();
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            Map messageMap = UtilMisc.toMap("errMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(resource,"partyservices.could_not_update_party_information_write", messageMap, locale);
            return ServiceUtil.returnError(errMsg);
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /**
     * Create an Affiliate entity.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map createAffiliate(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp now = UtilDateTime.nowTimestamp();

        String partyId = (String) context.get("partyId");
        Locale locale = (Locale) context.get("locale");
        String errMsg = null;

        if (partyId == null || partyId.length() == 0) {
            partyId = userLogin.getString("partyId");
        }

        // if specified partyId starts with a number, return an error
        if (Character.isDigit(partyId.charAt(0))) {
            errMsg = UtilProperties.getMessage(resource,"partyservices.cannot_create_affiliate_digit", locale);
            return ServiceUtil.returnError(errMsg);
        }

        // partyId might be empty, so check it and get next seq party id if empty
        if (partyId == null || partyId.length() == 0) {
            try {
                partyId = delegator.getNextSeqId("Party");
            } catch (IllegalArgumentException e) {
                errMsg = UtilProperties.getMessage(resource,"partyservices.cannot_create_affiliate_generation_failure", locale);
                return ServiceUtil.returnError(errMsg);
            }
        }

        // check to see if party object exists, if so make sure it is AFFILIATE type party
        GenericValue party = null;

        try {
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }

        if (party == null) {
            errMsg = UtilProperties.getMessage(resource,"partyservices.cannot_create_affiliate_no_party_entity", locale);
            return ServiceUtil.returnError(errMsg);
        }

        GenericValue affiliate = null;

        try {
            affiliate = delegator.findByPrimaryKey("Affiliate", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }

        if (affiliate != null) {
            errMsg = UtilProperties.getMessage(resource,"partyservices.cannot_create_affiliate_ID_already_exists", locale);
            return ServiceUtil.returnError(errMsg);
        }

        affiliate = delegator.makeValue("Affiliate", UtilMisc.toMap("partyId", partyId));
        affiliate.setNonPKFields(context);
        affiliate.set("dateTimeCreated", now, false);

        try {
            delegator.create(affiliate);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            Map messageMap = UtilMisc.toMap("errMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(resource,"partyservices.could_not_add_affiliate_info_write", messageMap, locale);
            return ServiceUtil.returnError(errMsg);
        }

        result.put("partyId", partyId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /**
     * Updates an Affiliate.
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_UPDATE permission.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map updateAffiliate(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_UPDATE");
        Locale locale = (Locale) context.get("locale");
        String errMsg = null;

        if (result.size() > 0)
            return result;

        GenericValue affiliate = null;

        try {
            affiliate = delegator.findByPrimaryKey("Affiliate", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            Map messageMap = UtilMisc.toMap("errMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(resource,"partyservices.could_not_update_affiliate_information_read", messageMap, locale);
            return ServiceUtil.returnError(errMsg);
        }

        if (affiliate == null) {
            errMsg = UtilProperties.getMessage(resource,"partyservices.could_not_update_affiliate_information_not_found", locale);
            return ServiceUtil.returnError(errMsg);
        }

        affiliate.setNonPKFields(context);

        try {
            affiliate.store();
        } catch (GenericEntityException e) {
            Map messageMap = UtilMisc.toMap("errMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(resource,"partyservices.could_not_update_affiliate_information_write", messageMap, locale);
            return ServiceUtil.returnError(errMsg);
        }
        return ServiceUtil.returnSuccess();
    }

    /**
     * Add a PartyNote.
     * @param dctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map createPartyNote(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String noteString = (String) context.get("note");
        String partyId = (String) context.get("partyId");
        String errMsg = null;
        Locale locale = (Locale) context.get("locale");
        Map noteCtx = UtilMisc.toMap("note", noteString, "userLogin", userLogin);

        // Store the note.
        Map noteRes = org.ofbiz.common.CommonServices.createNote(dctx, noteCtx);

        if (noteRes.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR))
            return noteRes;

        String noteId = (String) noteRes.get("noteId");

        if (noteId == null || noteId.length() == 0) {
            errMsg = UtilProperties.getMessage(resource,"partyservices.problem_creating_note_no_noteId_returned", locale);
            return ServiceUtil.returnError(errMsg);
        }

        // Set the party info
        try {
            Map fields = UtilMisc.toMap("partyId", partyId, "noteId", noteId);
            GenericValue v = delegator.makeValue("PartyNote", fields);

            delegator.create(v);
        } catch (GenericEntityException ee) {
            Debug.logError(ee, module);
            Map messageMap = UtilMisc.toMap("errMessage", ee.getMessage());
            errMsg = UtilProperties.getMessage(resource,"partyservices.problem_associating_note_with_party", messageMap, locale);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, errMsg);
        }
        return result;
    }

    /**
     * Get the party object(s) from an e-mail address
     * @param dctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map getPartyFromEmail(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        Collection parties = new LinkedList();
        String email = (String) context.get("email");
        Locale locale = (Locale) context.get("locale");
        String errMsg = null;

        if (email.length() == 0){
            errMsg = UtilProperties.getMessage(resource,"partyservices.required_parameter_email_cannot_be_empty", locale);
            return ServiceUtil.returnError(errMsg);
        }

        try {
            List exprs = new LinkedList();

            exprs.add(new EntityExpr("infoString", true, EntityOperator.LIKE, "%" + email.toUpperCase() + "%", true));
            List c = EntityUtil.filterByDate(delegator.findByAnd("PartyAndContactMech", exprs, UtilMisc.toList("infoString")), true);

            if (Debug.verboseOn()) Debug.logVerbose("List: " + c, module);
            if (Debug.infoOn()) Debug.logInfo("PartyFromEmail number found: " + c.size(), module);
            if (c != null) {
                Iterator i = c.iterator();

                while (i.hasNext()) {
                    GenericValue pacm = (GenericValue) i.next();
                    GenericValue party = delegator.makeValue("Party", UtilMisc.toMap("partyId", pacm.get("partyId"), "partyTypeId", pacm.get("partyTypeId")));

                    parties.add(UtilMisc.toMap("party", party));
                }
            }
        } catch (GenericEntityException e) {
            Map messageMap = UtilMisc.toMap("errMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(resource,"partyservices.cannot_get_party_entities_read", messageMap, locale);
            return ServiceUtil.returnError(errMsg);
        }
        if (parties.size() > 0)
            result.put("parties", parties);
        return result;
    }

    /**
     * Get the party object(s) from a user login ID
     * @param dctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map getPartyFromUserLogin(DispatchContext dctx, Map context) {
        Debug.logWarning("Running the getPartyFromUserLogin Service...", module);
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        Collection parties = new LinkedList();
        String userLoginId = (String) context.get("userLoginId");
        Locale locale = (Locale) context.get("locale");

        if (userLoginId.length() == 0)
            return ServiceUtil.returnError("Required parameter 'userLoginId' cannot be empty.");

        try {
            List exprs = new LinkedList();

            exprs.add(new EntityExpr("userLoginId", true, EntityOperator.LIKE, "%" + userLoginId.toUpperCase() + "%", true));
            Collection ulc = delegator.findByAnd("PartyAndUserLogin", exprs, UtilMisc.toList("userloginId"));

            if (Debug.verboseOn()) Debug.logVerbose("Collection: " + ulc, module);
            if (Debug.infoOn()) Debug.logInfo("PartyFromUserLogin number found: " + ulc.size(), module);
            if (ulc != null) {
                Iterator i = ulc.iterator();

                while (i.hasNext()) {
                    GenericValue ul = (GenericValue) i.next();
                    GenericValue party = delegator.makeValue("Party", UtilMisc.toMap("partyId", ul.get("partyId"), "partyTypeId", ul.get("partyTypeId")));

                    parties.add(UtilMisc.toMap("party", party));
                }
            }
        } catch (GenericEntityException e) {
            Map messageMap = UtilMisc.toMap("errMessage", e.getMessage());
            String errMsg = UtilProperties.getMessage(resource,"partyservices.cannot_get_party_entities_read", messageMap, locale);
            return ServiceUtil.returnError(errMsg);
        }
        if (parties.size() > 0) {
            result.put("parties", parties);
        }
        return result;
    }

    /**
     * Get the party object(s) from person information
     * @param dctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map getPartyFromPerson(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        Collection parties = new LinkedList();
        String firstName = (String) context.get("firstName");
        String lastName = (String) context.get("lastName");
        Locale locale = (Locale) context.get("locale");

        if (firstName == null) {
            firstName = "";
        }
        if (lastName == null) {
            lastName = "";
        }
        if (firstName.length() == 0 && lastName.length() == 0){
            String errMsg = UtilProperties.getMessage(resource,"partyservices.both_names_cannot_be_empty", locale);
            return ServiceUtil.returnError(errMsg);
        }

        try {
            List exprs = new LinkedList();

            exprs.add(new EntityExpr("firstName", true, EntityOperator.LIKE, "%" + firstName.toUpperCase() + "%", true));
            exprs.add(new EntityExpr("lastName", true, EntityOperator.LIKE, "%" + lastName.toUpperCase() + "%", true));
            Collection pc = delegator.findByAnd("Person", exprs, UtilMisc.toList("lastName", "firstName", "partyId"));

            if (Debug.infoOn()) Debug.logInfo("PartyFromPerson number found: " + pc.size(), module);
            if (pc != null) {
                Iterator i = pc.iterator();

                while (i.hasNext()) {
                    GenericValue person = (GenericValue) i.next();
                    GenericValue party = delegator.makeValue("Party", UtilMisc.toMap("partyId", person.get("partyId"), "partyTypeId", "PERSON"));

                    parties.add(UtilMisc.toMap("person", person, "party", party));
                }
            }
        } catch (GenericEntityException e) {
            Map messageMap = UtilMisc.toMap("errMessage", e.getMessage());
            String errMsg = UtilProperties.getMessage(resource,"partyservices.cannot_get_party_entities_read", messageMap, locale);
            return ServiceUtil.returnError(errMsg);
        }
        if (parties.size() > 0) {
            result.put("parties", parties);
        }
        return result;
    }

    /**
     * Get the party object(s) from party group name.
     * @param dctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map getPartyFromPartyGroup(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        Collection parties = new LinkedList();
        String groupName = (String) context.get("groupName");
        Locale locale = (Locale) context.get("locale");

        if (groupName.length() == 0) {
            return ServiceUtil.returnError("Required parameter 'groupName' cannot be empty.");
        }

        try {
            List exprs = new LinkedList();

            exprs.add(new EntityExpr("groupName", true, EntityOperator.LIKE, "%" + groupName.toUpperCase() + "%", true));
            Collection pc = delegator.findByAnd("PartyGroup", exprs, UtilMisc.toList("groupName", "partyId"));

            if (Debug.infoOn()) Debug.logInfo("PartyFromGroup number found: " + pc.size(), module);
            if (pc != null) {
                Iterator i = pc.iterator();

                while (i.hasNext()) {
                    GenericValue group = (GenericValue) i.next();
                    GenericValue party = delegator.makeValue("Party", UtilMisc.toMap("partyId", group.get("partyId"), "partyTypeId", "PARTY_GROUP"));

                    parties.add(UtilMisc.toMap("partyGroup", group, "party", party));
                }
            }
        } catch (GenericEntityException e) {
            Map messageMap = UtilMisc.toMap("errMessage", e.getMessage());
            String errMsg = UtilProperties.getMessage(resource,"partyservices.cannot_get_party_entities_read", messageMap, locale);
            return ServiceUtil.returnError(errMsg);
        }
        if (parties.size() > 0) {
            result.put("parties", parties);
        }
        return result;
    }

    public static Map getPerson(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String partyId = (String) context.get("partyId");
        GenericValue person = null;

        try {
            person = delegator.findByPrimaryKeyCache("Person", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Cannot get person entity (read failure): " + e.getMessage());
        }
        if (person != null) {
            result.put("lookupPerson", person);
        }
        return result;
    }

    public static Map createRoleType(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        GenericValue roleType = null;

        try {
            roleType = delegator.makeValue("RoleType", null);
            roleType.setPKFields(context);
            roleType.setNonPKFields(context);
            roleType = delegator.create(roleType);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Cannot create role type entity (write failure): " + e.getMessage());
        }
        if (roleType != null) {
            result.put("roleType", roleType);
        }
        return result;
    }
}
