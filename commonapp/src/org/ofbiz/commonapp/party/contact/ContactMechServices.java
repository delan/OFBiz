/*
 * $Id$
 * 
 *  Copyright (c) 2001 The Open For Business Project and repected authors.
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

package org.ofbiz.commonapp.party.contact;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.service.*;

/**
 * Services for Contact Mechanism maintenance
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version 1.0
 * Created on January 18, 2001
 */
public class ContactMechServices {
    /** Creates a ContactMech
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_CREATE permission
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map createContactMech(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp now = UtilDateTime.nowTimestamp();
        List toBeStored = new LinkedList();

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_CREATE");
        if (result.size() > 0)
            return result;
        
        String contactMechTypeId = (String) context.get("contactMechTypeId");

        Long newCmId = delegator.getNextSeqId("ContactMech"); 
        if(newCmId == null) {
            return ServiceUtil.returnError("ERROR: Could not create contact info (id generation failure)");
        }
        
        GenericValue tempContactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", contactMechTypeId));
        toBeStored.add(tempContactMech);

        toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", newCmId.toString(), 
                "fromDate", now, "roleTypeId", context.get("roleTypeId"), "allowSolicitation", context.get("allowSolicitation"), "extension", context.get("extension"))));

        if("POSTAL_ADDRESS".equals(contactMechTypeId)) {
            return ServiceUtil.returnError("This service (createContactMech) should not be used for POSTAL_ADDRESS type ContactMechs, use the createPostalAddress service");
        } else if("TELECOM_NUMBER".equals(contactMechTypeId)) {
            return ServiceUtil.returnError("This service (createContactMech) should not be used for TELECOM_NUMBER type ContactMechs, use the createTelecomNumber service");
        } else {
            tempContactMech.set("infoString", context.get("infoString"));
        }

        try { 
            delegator.storeAll(toBeStored);
        } catch(GenericEntityException e) {
            Debug.logWarning(e.toString());
            return ServiceUtil.returnError("Could not create contact info (write failure): " + e.getMessage());
        }

        result.put("contactMechId", newCmId.toString());
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Updates a ContactMech
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_UPDATE permission
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map updateContactMech(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp now = UtilDateTime.nowTimestamp();
        List toBeStored = new LinkedList();
        boolean isModified = false;
        
        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_UPDATE");
        if (result.size() > 0)
            return result;

        Long newCmId = delegator.getNextSeqId("ContactMech"); 
        if(newCmId == null) {
            return ServiceUtil.returnError("ERROR: Could not change contact info (id generation failure)");
        }

        String contactMechId = (String) context.get("contactMechId");
        GenericValue contactMech = null;
        GenericValue partyContactMech = null;
        try {
            contactMech = delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", contactMechId));
            //try to find a PartyContactMech with a valid date range
            List partyContactMechs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), UtilMisc.toList("fromDate")), true);
            partyContactMech = EntityUtil.getFirst(partyContactMechs);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            contactMech = null;
            partyContactMech = null;
        }
        if (contactMech == null) {
            return ServiceUtil.returnError("ERROR: Could not find specified contact info (read error)");
        }
        if (partyContactMech == null) {
            return ServiceUtil.returnError("ERROR: Cannot update specified contact info because it does not correspond to the specified party");
        }
        toBeStored.add(partyContactMech);

        String contactMechTypeId = contactMech.getString("contactMechTypeId");
        
        //never change a contact mech, just create a new one with the changes
        GenericValue newContactMech = new GenericValue(contactMech);
        GenericValue newPartyContactMech = new GenericValue(partyContactMech);
        GenericValue relatedEntityToSet = null;

        if("POSTAL_ADDRESS".equals(contactMechTypeId)) {
            return ServiceUtil.returnError("This service (updateContactMech) should not be used for POSTAL_ADDRESS type ContactMechs, use the updatePostalAddress service");
        } else if("TELECOM_NUMBER".equals(contactMechTypeId)) {
            return ServiceUtil.returnError("This service (updateContactMech) should not be used for TELECOM_NUMBER type ContactMechs, use the updateTelecomNumber service");
        } else {
            newContactMech.set("infoString", context.get("infoString"));
        }

        newPartyContactMech.set("roleTypeId", context.get("roleTypeId"));
        newPartyContactMech.set("allowSolicitation", context.get("allowSolicitation"));

        if(!newContactMech.equals(contactMech)) isModified = true;
        if(!newPartyContactMech.equals(partyContactMech)) isModified = true;

        toBeStored.add(newContactMech);
        toBeStored.add(newPartyContactMech);

        if(isModified) {
            if(relatedEntityToSet != null) toBeStored.add(relatedEntityToSet);

            newContactMech.set("contactMechId", newCmId.toString());
            newPartyContactMech.set("contactMechId", newCmId.toString());
            newPartyContactMech.set("fromDate", now);
            newPartyContactMech.set("thruDate", null);

            try {
                Iterator partyContactMechPurposes = UtilMisc.toIterator(partyContactMech.getRelated("PartyContactMechPurpose"));
                while(partyContactMechPurposes != null && partyContactMechPurposes.hasNext()) {
                    GenericValue tempVal = new GenericValue((GenericValue)partyContactMechPurposes.next());
                    tempVal.set("contactMechId", newCmId.toString());
                    toBeStored.add(tempVal);
                }
            } catch(GenericEntityException e) {
                Debug.logWarning(e.toString());
                return ServiceUtil.returnError("ERROR: Could not change contact info (read purpose failure): " + e.getMessage());
            }

            partyContactMech.set("thruDate", now);
            try { 
                delegator.storeAll(toBeStored);
            } catch(GenericEntityException e) {
                Debug.logWarning(e.toString());
                return ServiceUtil.returnError("ERROR: Could not change contact info (write failure): " + e.getMessage());
            }
        } else {
            result.put("newContactMechId", contactMechId);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            result.put(ModelService.SUCCESS_MESSAGE, "No changes made, not updating contact mechanism");
            return result;
        }

        result.put("newContactMechId", newCmId.toString());
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
    /** Deletes a ContactMech
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_DELETE permission
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map deleteContactMech(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp now = UtilDateTime.nowTimestamp();
        
        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_DELETE");
        if (result.size() > 0)
            return result;

        //never delete a contact mechanism, just put a to date on the link to the party
        String contactMechId = (String) context.get("contactMechId");
        GenericValue partyContactMech = null;
        try {
            //try to find a PartyContactMech with a valid date range
            List partyContactMechs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), UtilMisc.toList("fromDate")), true);
            partyContactMech = EntityUtil.getFirst(partyContactMechs);
        } catch(GenericEntityException e) {
            Debug.logWarning(e.toString());
            return ServiceUtil.returnError("Could not delete contact info (read failure): " + e.getMessage());
        }
        
        if(partyContactMech == null) {
            return ServiceUtil.returnError("Could not delete contact info (party contact mech not found)");
        }

        partyContactMech.set("thruDate", UtilDateTime.nowTimestamp());
        try {
            partyContactMech.store();
        } catch(GenericEntityException e) {
            Debug.logWarning(e.toString());
            return ServiceUtil.returnError("Could not delete contact info (write failure)");
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    
// ============================================================================
// ============================================================================
    
    /** Creates a PostalAddress
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_CREATE permission
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map createPostalAddress(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp now = UtilDateTime.nowTimestamp();
        List toBeStored = new LinkedList();
        
        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_CREATE");
        if (result.size() > 0)
            return result;

        String contactMechTypeId = "POSTAL_ADDRESS";

        Long newCmId = delegator.getNextSeqId("ContactMech"); 
        if(newCmId == null) {
            return ServiceUtil.returnError("ERROR: Could not create contact info (id generation failure)");
        }
        
        GenericValue tempContactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", contactMechTypeId));
        toBeStored.add(tempContactMech);

        toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", newCmId.toString(), 
                "fromDate", now, "roleTypeId", context.get("roleTypeId"), "allowSolicitation", context.get("allowSolicitation"), "extension", context.get("extension"))));

        GenericValue newAddr = delegator.makeValue("PostalAddress", null);
        newAddr.set("contactMechId", newCmId.toString());
        newAddr.set("toName", context.get("toName"));
        newAddr.set("attnName", context.get("attnName"));
        newAddr.set("address1", context.get("address1"));
        newAddr.set("address2", context.get("address2"));
        newAddr.set("directions", context.get("directions"));
        newAddr.set("city", context.get("city"));
        newAddr.set("postalCode", context.get("postalCode"));
        newAddr.set("stateProvinceGeoId", context.get("stateProvinceGeoId"));
        newAddr.set("countryGeoId", context.get("countryGeoId"));
        newAddr.set("postalCodeGeoId", context.get("postalCodeGeoId"));
        toBeStored.add(newAddr);

        try { 
            delegator.storeAll(toBeStored);
        } catch(GenericEntityException e) {
            Debug.logWarning(e.toString());
            return ServiceUtil.returnError("Could not create contact info (write failure): " + e.getMessage());
        }

        result.put("contactMechId", newCmId.toString());
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Updates a PostalAddress
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_UPDATE permission
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map updatePostalAddress(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp now = UtilDateTime.nowTimestamp();
        List toBeStored = new LinkedList();
        boolean isModified = false;
        
        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_UPDATE");
        if (result.size() > 0)
            return result;

        Long newCmId = delegator.getNextSeqId("ContactMech"); 
        if(newCmId == null) {
            return ServiceUtil.returnError("ERROR: Could not change contact info (id generation failure)");
        }

        String contactMechId = (String) context.get("contactMechId");
        GenericValue contactMech = null;
        GenericValue partyContactMech = null;
        try {
            contactMech = delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", contactMechId));
            //try to find a PartyContactMech with a valid date range
            List partyContactMechs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), UtilMisc.toList("fromDate")), true);
            partyContactMech = EntityUtil.getFirst(partyContactMechs);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            contactMech = null;
            partyContactMech = null;
        }
        if (contactMech == null) {
            return ServiceUtil.returnError("ERROR: Could not find specified contact info (read error)");
        }
        if (partyContactMech == null) {
            return ServiceUtil.returnError("ERROR: Cannot update specified contact info because it does not correspond to the specified party");
        }
        toBeStored.add(partyContactMech);

        //never change a contact mech, just create a new one with the changes
        GenericValue newContactMech = new GenericValue(contactMech);
        GenericValue newPartyContactMech = new GenericValue(partyContactMech);
        GenericValue relatedEntityToSet = null;

        if("POSTAL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))) {
            GenericValue addr = null;
            try {
                addr = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId));
            } catch(GenericEntityException e) {
                Debug.logWarning(e.toString());
                addr = null;
            }
            relatedEntityToSet = new GenericValue(addr);
            relatedEntityToSet.set("toName", context.get("toName"));
            relatedEntityToSet.set("attnName", context.get("attnName"));
            relatedEntityToSet.set("address1", context.get("address1"));
            relatedEntityToSet.set("address2", context.get("address2"));
            relatedEntityToSet.set("directions", context.get("directions"));
            relatedEntityToSet.set("city", context.get("city"));
            relatedEntityToSet.set("postalCode", context.get("postalCode"));
            relatedEntityToSet.set("stateProvinceGeoId", context.get("stateProvinceGeoId"));
            relatedEntityToSet.set("countryGeoId", context.get("countryGeoId"));
            relatedEntityToSet.set("postalCodeGeoId", context.get("postalCodeGeoId"));
            if(addr == null || !relatedEntityToSet.equals(addr)) {
                isModified = true;
            }
            relatedEntityToSet.set("contactMechId", newCmId.toString());
        } else {
            return ServiceUtil.returnError("Could not update this contact mech as a POSTAL_ADDRESS the specified contact mech is a " + contactMech.getString("contactMechTypeId"));
        }

        newPartyContactMech.set("roleTypeId", context.get("roleTypeId"));
        newPartyContactMech.set("allowSolicitation", context.get("allowSolicitation"));

        if(!newContactMech.equals(contactMech)) isModified = true;
        if(!newPartyContactMech.equals(partyContactMech)) isModified = true;

        toBeStored.add(newContactMech);
        toBeStored.add(newPartyContactMech);

        if(isModified) {
            if(relatedEntityToSet != null) toBeStored.add(relatedEntityToSet);

            newContactMech.set("contactMechId", newCmId.toString());
            newPartyContactMech.set("contactMechId", newCmId.toString());
            newPartyContactMech.set("fromDate", now);
            newPartyContactMech.set("thruDate", null);

            try {
                Iterator partyContactMechPurposes = UtilMisc.toIterator(partyContactMech.getRelated("PartyContactMechPurpose"));
                while(partyContactMechPurposes != null && partyContactMechPurposes.hasNext()) {
                    GenericValue tempVal = new GenericValue((GenericValue)partyContactMechPurposes.next());
                    tempVal.set("contactMechId", newCmId.toString());
                    toBeStored.add(tempVal);
                }
            } catch(GenericEntityException e) {
                Debug.logWarning(e.toString());
                return ServiceUtil.returnError("ERROR: Could not change contact info (read purpose failure): " + e.getMessage());
            }

            partyContactMech.set("thruDate", now);
            try { 
                delegator.storeAll(toBeStored);
            } catch(GenericEntityException e) {
                Debug.logWarning(e.toString());
                return ServiceUtil.returnError("ERROR: Could not change contact info (write failure): " + e.getMessage());
            }
        } else {
            result.put("newContactMechId", contactMechId);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            result.put(ModelService.SUCCESS_MESSAGE, "No changes made, not updating contact mechanism");
            return result;
        }

        result.put("newContactMechId", newCmId.toString());
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
// ============================================================================
// ============================================================================
    
    /** Creates a TelecomNumber
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_CREATE permission
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map createTelecomNumber(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp now = UtilDateTime.nowTimestamp();
        List toBeStored = new LinkedList();
        
        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_CREATE");
        if (result.size() > 0)
            return result;

        String contactMechTypeId = "TELECOM_NUMBER";

        Long newCmId = delegator.getNextSeqId("ContactMech"); 
        if(newCmId == null) {
            return ServiceUtil.returnError("ERROR: Could not create contact info (id generation failure)");
        }
        
        GenericValue tempContactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", contactMechTypeId));
        toBeStored.add(tempContactMech);

        toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", newCmId.toString(), 
                "fromDate", now, "roleTypeId", context.get("roleTypeId"), "allowSolicitation", context.get("allowSolicitation"), "extension", context.get("extension"))));

        toBeStored.add(delegator.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCmId.toString(), 
                "countryCode", context.get("countryCode"), "areaCode", context.get("areaCode"), "contactNumber", context.get("contactNumber"))));

        try { 
            delegator.storeAll(toBeStored);
        } catch(GenericEntityException e) {
            Debug.logWarning(e.toString());
            return ServiceUtil.returnError("Could not create contact info (write failure): " + e.getMessage());
        }

        result.put("contactMechId", newCmId.toString());
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Updates a TelecomNumber
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_UPDATE permission
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map updateTelecomNumber(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp now = UtilDateTime.nowTimestamp();
        List toBeStored = new LinkedList();
        boolean isModified = false;
        
        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_UPDATE");
        if (result.size() > 0)
            return result;

        Long newCmId = delegator.getNextSeqId("ContactMech"); 
        if(newCmId == null) {
            return ServiceUtil.returnError("ERROR: Could not change contact info (id generation failure)");
        }

        String contactMechId = (String) context.get("contactMechId");
        GenericValue contactMech = null;
        GenericValue partyContactMech = null;
        try {
            contactMech = delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", contactMechId));
            //try to find a PartyContactMech with a valid date range
            List partyContactMechs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), UtilMisc.toList("fromDate")), true);
            partyContactMech = EntityUtil.getFirst(partyContactMechs);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            contactMech = null;
            partyContactMech = null;
        }
        if (contactMech == null) {
            return ServiceUtil.returnError("ERROR: Could not find specified contact info (read error)");
        }
        if (partyContactMech == null) {
            return ServiceUtil.returnError("ERROR: Cannot update specified contact info because it does not correspond to the specified party");
        }
        toBeStored.add(partyContactMech);

        //never change a contact mech, just create a new one with the changes
        GenericValue newContactMech = new GenericValue(contactMech);
        GenericValue newPartyContactMech = new GenericValue(partyContactMech);
        GenericValue relatedEntityToSet = null;

        if("TELECOM_NUMBER".equals(contactMech.getString("contactMechTypeId"))) {
            GenericValue telNum = null;
            try { 
                telNum = delegator.findByPrimaryKey("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechId));
            } catch(GenericEntityException e) {
                Debug.logWarning(e.toString());
                telNum = null;
            }
            relatedEntityToSet = new GenericValue(telNum);
            relatedEntityToSet.set("countryCode", context.get("countryCode"));
            relatedEntityToSet.set("areaCode", context.get("areaCode"));
            relatedEntityToSet.set("contactNumber", context.get("contactNumber"));

            if(telNum == null || !relatedEntityToSet.equals(telNum)) {
                isModified = true;
            }
            relatedEntityToSet.set("contactMechId", newCmId.toString());
            newPartyContactMech.set("extension", context.get("extension"));
        } else {
            return ServiceUtil.returnError("Could not update this contact mech as a TELECOM_NUMBER the specified contact mech is a " + contactMech.getString("contactMechTypeId"));
        }

        newPartyContactMech.set("roleTypeId", context.get("roleTypeId"));
        newPartyContactMech.set("allowSolicitation", context.get("allowSolicitation"));

        if(!newContactMech.equals(contactMech)) isModified = true;
        if(!newPartyContactMech.equals(partyContactMech)) isModified = true;

        toBeStored.add(newContactMech);
        toBeStored.add(newPartyContactMech);

        if(isModified) {
            if(relatedEntityToSet != null) toBeStored.add(relatedEntityToSet);

            newContactMech.set("contactMechId", newCmId.toString());
            newPartyContactMech.set("contactMechId", newCmId.toString());
            newPartyContactMech.set("fromDate", now);
            newPartyContactMech.set("thruDate", null);

            try {
                Iterator partyContactMechPurposes = UtilMisc.toIterator(partyContactMech.getRelated("PartyContactMechPurpose"));
                while(partyContactMechPurposes != null && partyContactMechPurposes.hasNext()) {
                    GenericValue tempVal = new GenericValue((GenericValue)partyContactMechPurposes.next());
                    tempVal.set("contactMechId", newCmId.toString());
                    toBeStored.add(tempVal);
                }
            } catch(GenericEntityException e) {
                Debug.logWarning(e.toString());
                return ServiceUtil.returnError("ERROR: Could not change contact info (read purpose failure): " + e.getMessage());
            }

            partyContactMech.set("thruDate", now);
            try { 
                delegator.storeAll(toBeStored);
            } catch(GenericEntityException e) {
                Debug.logWarning(e.toString());
                return ServiceUtil.returnError("ERROR: Could not change contact info (write failure): " + e.getMessage());
            }
        } else {
            result.put("newContactMechId", contactMechId);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            result.put(ModelService.SUCCESS_MESSAGE, "No changes made, not updating contact mechanism");
            return result;
        }

        result.put("newContactMechId", newCmId.toString());
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
// ============================================================================
// ============================================================================
    
    /** Creates a EmailAddress
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_CREATE permission
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map createEmailAddress(DispatchContext ctx, Map context) {
        Map newContext = new HashMap(context);
        newContext.put("infoString", newContext.get("emailAddress"));
        newContext.remove("emailAddress");
        newContext.put("contactMechTypeId", "EMAIL_ADDRESS");
        
        return createContactMech(ctx, newContext);
    }

    /** Updates a EmailAddress
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_UPDATE permission
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map updateEmailAddress(DispatchContext ctx, Map context) {
        Map newContext = new HashMap(context);
        newContext.put("infoString", newContext.get("emailAddress"));
        newContext.remove("emailAddress");
        return updateContactMech(ctx, newContext);
    }
    
// ============================================================================
// ============================================================================
    
    /** Creates a PartyContactMechPurpose
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_CREATE permission
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map createPartyContactMechPurpose(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_CREATE");
        if (result.size() > 0)
            return result;
        
        //required parameters
        String contactMechId = (String) context.get("contactMechId");
        String contactMechPurposeTypeId = (String) context.get("contactMechPurposeTypeId");

        GenericValue tempVal = null;
        try {
            List allPCMPs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId), null), true);
            tempVal = EntityUtil.getFirst(allPCMPs);
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            tempVal = null;
        }
        
        Timestamp fromDate = UtilDateTime.nowTimestamp();
        if(tempVal != null) {
            //exists already with valid date, show warning
            return ServiceUtil.returnError("Could not create new purpose, a purpose with that type already exists");
        } else {
            //no entry with a valid date range exists, create new with open thruDate
            GenericValue newPartyContactMechPurpose = delegator.makeValue("PartyContactMechPurpose", 
                    UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId, 
                                   "fromDate", fromDate));
            try {
                delegator.create(newPartyContactMechPurpose);
            } catch(GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                return ServiceUtil.returnError("ERROR: Could not add purpose to contact mechanism (write failure): " + e.getMessage());
            }
        }

        result.put("fromDate", fromDate);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
    /** Deletes the PartyContactMechPurpose corresponding to the parameters in the context
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_DELETE permission
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map deletePartyContactMechPurpose(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_DELETE");
        if (result.size() > 0)
            return result;

        //required parameters
        String contactMechId = (String) context.get("contactMechId");
        String contactMechPurposeTypeId = (String) context.get("contactMechPurposeTypeId");
        Timestamp fromDate = (Timestamp) context.get("fromDate");

        GenericValue pcmp = null;
        try {
            pcmp = delegator.findByPrimaryKey("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "fromDate", fromDate));
            if(pcmp == null) {
                return ServiceUtil.returnError("Could not delete purpose from contact mechanism (record not found)");
            }
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("Could not delete purpose from contact mechanism (read failure): " + e.getMessage());
        }
        
        pcmp.set("thruDate", UtilDateTime.nowTimestamp());
        try {
            pcmp.store();
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("Could not delete purpose from contact mechanism (write failure): " + e.getMessage());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
}
