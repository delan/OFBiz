/*
 * $Id$
 * $Log$
 * Revision 1.1  2002/01/26 11:21:32  jonesde
 * Changed old party events to new service and simple events, added event and services for party group
 *
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
 * <p><b>Title:</b> Services for Party/Person/Group maintenance
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
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
 * @author  <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author  <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @version 1.0
 * @created January 25, 2002
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
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp now = UtilDateTime.nowTimestamp();
        Collection toBeStored = new LinkedList();

        String partyId = (String) context.get("partyId");
        if (partyId == null || partyId.length() == 0) {
            partyId = userLogin.getString("partyId");
        }

        //if specified partyId starts with a number, return an error
        if (Character.isDigit(partyId.charAt(0))) {
            return ServiceUtil.returnError("Cannot create person, specified party ID cannot start with a digit, numeric IDs are reserved for auto-generated IDs");
        }
        
        //partyId might be empty, so check it and get next seq party id if empty
        if (partyId == null || partyId.length() == 0) {
            Long newId = delegator.getNextSeqId("Party"); 
            if(newId == null) {
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
                return ServiceUtil.returnError("Cannot create person, a party with the specified party ID already exists and is not a PERSON type party");
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
        } catch(GenericEntityException e) {
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
        } catch(GenericEntityException e) {
            Debug.logWarning(e);
            return ServiceUtil.returnError("Could not update person information (read failure): " + e.getMessage());
        }

        if(person == null) {
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
        } catch(GenericEntityException e) {
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
        if (partyId == null || partyId.length() == 0) {
            partyId = userLogin.getString("partyId");
        }

        //if specified partyId starts with a number, return an error
        if (Character.isDigit(partyId.charAt(0))) {
            return ServiceUtil.returnError("Cannot create party group, specified party ID cannot start with a digit, numeric IDs are reserved for auto-generated IDs");
        }
        
        //partyId might be empty, so check it and get next seq party id if empty
        if (partyId == null || partyId.length() == 0) {
            Long newId = delegator.getNextSeqId("Party"); 
            if(newId == null) {
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
                return ServiceUtil.returnError("Cannot create party group, a party with the specified party ID already exists and is not a PARTY_GROUP type party");
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
            return ServiceUtil.returnError("Cannot create party group, a party group with the specified party ID already exists");
        }

        partyGroup = delegator.makeValue("PartyGroup", UtilMisc.toMap("partyId", partyId));
        toBeStored.add(partyGroup);

        partyGroup.set("groupName", context.get("groupName"), false);
        partyGroup.set("federalTaxId", context.get("federalTaxId"), false);

        try {
            delegator.storeAll(toBeStored);
        } catch(GenericEntityException e) {
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
        } catch(GenericEntityException e) {
            Debug.logWarning(e);
            return ServiceUtil.returnError("Could not update party group information (read failure): " + e.getMessage());
        }

        if(partyGroup == null) {
            return ServiceUtil.returnError("Could not update party group information (partyGroup not found)");
        }

        partyGroup.set("groupName", context.get("groupName"), false);
        partyGroup.set("federalTaxId", context.get("federalTaxId"), false);

        try {
            partyGroup.store();
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("Could update party group information (write failure): " + e.getMessage());
        }
        
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
}
