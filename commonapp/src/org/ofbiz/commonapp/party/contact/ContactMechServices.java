/*
 * $Id$
 * $Log$
 * Revision 1.2  2002/01/21 23:47:02  jonesde
 * Finished first pass for cm purpose and cc info
 *
 * Revision 1.1  2002/01/20 06:29:55  jonesde
 * Initial, incomplete pass at refactoring of party contact stuff with services, simple events, etc
 *
 * 
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
 * <p><b>Title:</b> Services for Contact Mechanism maintenance
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
        Collection toBeStored = new LinkedList();
        
        String partyId = (String) context.get("partyId");
        if (partyId == null || partyId.length() == 0) {
            partyId = userLogin.getString("partyId");
        }
        
        //partyId might be null, so check it
        if (partyId == null || partyId.length() == 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Party ID missing, cannot delete contact mechanism");
            return result;
        }
        
        //<b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_CREATE permission
        if (!partyId.equals(userLogin.getString("partyId"))) {
            if (!security.hasEntityPermission("PARTYMGR", "_CREATE", userLogin)) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "You do not have permission to delete contact mech for this partyId");
                return result;
            }
        }

        String contactMechTypeId = (String) context.get("contactMechTypeId");
        //String contactMechTypeId = "POSTAL_ADDRESS";
        //String contactMechTypeId = "TELECOM_NUMBER";
        //String contactMechTypeId = "EMAIL_ADDRESS";

        Long newCmId = delegator.getNextSeqId("ContactMech"); 
        if(newCmId == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not create contact info (id generation failure)");
            return result;
        }
        
        GenericValue tempContactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", contactMechTypeId));
        toBeStored.add(tempContactMech);

        toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", newCmId.toString(), 
                "fromDate", now, "roleTypeId", "CUSTOMER", "allowSolicitation", context.get("allowSolicitation"), "extension", context.get("extension"))));

        if("POSTAL_ADDRESS".equals(contactMechTypeId)) {
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
        } else if("TELECOM_NUMBER".equals(contactMechTypeId)) {
            toBeStored.add(delegator.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCmId.toString(), 
                    "countryCode", context.get("countryCode"), "areaCode", context.get("areaCode"), "contactNumber", context.get("contactNumber"))));
        } else if("EMAIL_ADDRESS".equals(contactMechTypeId)) {
            tempContactMech.set("infoString", context.get("emailAddress"));

            String cmPurposeTypeId;
            try {
                GenericValue party = userLogin.getRelatedOne("Party");
                if (UtilValidate.isEmpty(ContactHelper.getContactMechByPurpose(party, "PRIMARY_EMAIL", false))) {
                    cmPurposeTypeId = "PRIMARY_EMAIL";
                } else {
                    cmPurposeTypeId = "OTHER_EMAIL";
                }
                toBeStored.add(delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", newCmId.toString(), "contactMechPurposeTypeId", cmPurposeTypeId, "fromDate", now)));
            } catch(GenericEntityException e) {
                Debug.logWarning(e.getMessage());
            }
        } else {
            tempContactMech.set("infoString", context.get("infoString"));
        }

        try { 
            delegator.storeAll(toBeStored);
        } catch(GenericEntityException e) {
            Debug.logWarning(e.toString());
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Could not create contact info (write failure): " + e.getMessage());
            return result;
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
        Collection toBeStored = new LinkedList();
        boolean isModified = false;
        
        String partyId = (String) context.get("partyId");
        if (partyId == null || partyId.length() == 0) {
            partyId = userLogin.getString("partyId");
        }
        
        //partyId might be null, so check it
        if (partyId == null || partyId.length() == 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Party ID missing, cannot delete contact mechanism");
            return result;
        }
        
        //<b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_UPDATE permission
        if (!partyId.equals(userLogin.getString("partyId"))) {
            if (!security.hasEntityPermission("PARTYMGR", "_UPDATE", userLogin)) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "You do not have permission to delete contact mech for this partyId");
                return result;
            }
        }

        Long newCmId = delegator.getNextSeqId("ContactMech"); 
        if(newCmId == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not change contact info (id generation failure)");
            return result;
        }

        String contactMechId = (String) context.get("contactMechId");
        GenericValue contactMech = null;
        GenericValue partyContactMech = null;
        try {
            contactMech = delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", contactMechId));
            //try to find a PartyContactMech with a valid date range
            Collection partyContactMechs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), null));
            partyContactMech = EntityUtil.getOnly(partyContactMechs);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            contactMech = null;
            partyContactMech = null;
        }
        if (contactMech == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not find specified contact info (read error)");
            return result;
        }
        if (partyContactMech == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Cannot update specified contact info because it does not correspond to the specified party");
            return result;
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
        } else if("TELECOM_NUMBER".equals(contactMech.getString("contactMechTypeId"))) {
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
        } else if("EMAIL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))) {
            newContactMech.set("infoString", context.get("emailAddress"));
        } else {
            newContactMech.set("infoString", context.get("infoString"));
        }

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
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not change contact info (read purpose failure): " + e.getMessage());
                return result;
            }

            partyContactMech.set("thruDate", now);
            try { 
                delegator.storeAll(toBeStored);
            } catch(GenericEntityException e) {
                Debug.logWarning(e.toString());
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not change contact info (write failure): " + e.getMessage());
                return result;
            }
        } else {
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
        
        String partyId = (String) context.get("partyId");
        if (partyId == null || partyId.length() == 0) {
            partyId = userLogin.getString("partyId");
        }
        
        //partyId might be null, so check it
        if (partyId == null || partyId.length() == 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Party ID missing, cannot delete contact mechanism");
            return result;
        }
        
        //<b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_DELETE permission
        if (!partyId.equals(userLogin.getString("partyId"))) {
            if (!security.hasEntityPermission("PARTYMGR", "_DELETE", userLogin)) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "You do not have permission to delete contact mech for this partyId");
                return result;
            }
        }

        //never delete a contact mechanism, just put a to date on the link to the party
        String contactMechId = (String) context.get("contactMechId");
        GenericValue partyContactMech = null;
        try {
            //try to find a PartyContactMech with a valid date range
            Collection partyContactMechs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), null));
            partyContactMech = EntityUtil.getOnly(partyContactMechs);
        } catch(GenericEntityException e) {
            Debug.logWarning(e.toString());
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Could not delete contact info (read failure): " + e.getMessage());
            return result;
        }
        
        if(partyContactMech == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Could not delete contact info (party contact mech not found)");
            return result;
        }

        partyContactMech.set("thruDate", UtilDateTime.nowTimestamp());
        try {
            partyContactMech.store();
        } catch(GenericEntityException e) {
            Debug.logWarning(e.toString());
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Could not delete contact info (write failure)");
            return result;
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
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
        
        String partyId = (String) context.get("partyId");
        if (partyId == null || partyId.length() == 0) {
            partyId = userLogin.getString("partyId");
        }
        
        //required parameters
        String contactMechId = (String) context.get("contactMechId");
        String contactMechPurposeTypeId = (String) context.get("contactMechPurposeTypeId");

        //partyId might be null, so check it
        if (partyId == null || partyId.length() == 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Party ID missing, cannot add purpose to contact mechanism");
            return result;
        }
        
        //<b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_CREATE permission
        if (!partyId.equals(userLogin.getString("partyId"))) {
            if (!security.hasEntityPermission("PARTYMGR", "_CREATE", userLogin)) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "You do not have permission to create a contact mech purpose for this partyId");
                return result;
            }
        }
        
        GenericValue tempVal = null;
        try {
            Collection allPCMPs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId), null));
            tempVal = EntityUtil.getFirst(allPCMPs);
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            tempVal = null;
        }
        
        Timestamp fromDate = UtilDateTime.nowTimestamp();
        if(tempVal != null) {
            //exists already with valid date, show warning
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Could not create new purpose, a purpose with that type already exists");
            return result;
        } else {
            //no entry with a valid date range exists, create new with open thruDate
            GenericValue newPartyContactMechPurpose = delegator.makeValue("PartyContactMechPurpose", 
                    UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId, 
                                   "fromDate", fromDate));
            try {
                delegator.create(newPartyContactMechPurpose);
            } catch(GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not add purpose to contact mechanism (write failure): " + e.getMessage());
                return result;
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
        
        String partyId = (String) context.get("partyId");
        if (partyId == null || partyId.length() == 0) {
            partyId = userLogin.getString("partyId");
        }
        
        //required parameters
        String contactMechId = (String) context.get("contactMechId");
        String contactMechPurposeTypeId = (String) context.get("contactMechPurposeTypeId");
        Timestamp fromDate = (Timestamp) context.get("fromDate");

        //partyId might be null, so check it
        if (partyId == null || partyId.length() == 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Party ID missing, cannot add purpose to contact mechanism");
            return result;
        }
        
        //<b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_DELETE permission
        if (!partyId.equals(userLogin.getString("partyId"))) {
            if (!security.hasEntityPermission("PARTYMGR", "_DELETE", userLogin)) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "You do not have permission to delete a contact mech purpose for this partyId");
                return result;
            }
        }

        GenericValue pcmp = null;
        try {
            pcmp = delegator.findByPrimaryKey("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "fromDate", fromDate));
            if(pcmp == null) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "Could not delete purpose from contact mechanism (record not found)");
                return result;
            }
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Could not delete purpose from contact mechanism (read failure): " + e.getMessage());
            return result;
        }
        
        pcmp.set("thruDate", UtilDateTime.nowTimestamp());
        try {
            pcmp.store();
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Could not delete purpose from contact mechanism (write failure): " + e.getMessage());
            return result;
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
    /** Creates a CreditCardInfo entity according to the parameters passed in the context
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_CREATE permission
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map createCreditCardInfo(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Timestamp now = UtilDateTime.nowTimestamp();

        String partyId = (String) context.get("partyId");
        if (partyId == null || partyId.length() == 0) {
            partyId = userLogin.getString("partyId");
        }
        //partyId might be null, so check it
        if (partyId == null || partyId.length() == 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Party ID missing, cannot create credit card info");
            return result;
        }
        
        //<b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_CREATE permission
        if (!partyId.equals(userLogin.getString("partyId"))) {
            if (!security.hasEntityPermission("PARTYMGR", "_CREATE", userLogin)) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "You do not have permission to create credit card info for this partyId");
                return result;
            }
        }
        
        //do some more complicated/critical validation...
        List messages = new LinkedList();
        if (!UtilValidate.isCardMatch((String) context.get("cardType"), (String) context.get("cardNumber")))
            messages.add((String) context.get("cardNumber") + UtilValidate.isCreditCardPrefixMsg + 
                    (String) context.get("cardType") + UtilValidate.isCreditCardSuffixMsg + 
                    " (It appears to be a " + UtilValidate.getCardType((String) context.get("cardNumber")) + " credit card number)");
        if (!UtilValidate.isDateAfterToday((String) context.get("expireDate"))) 
            messages.add("The expiration date " + (String) context.get("expireDate") + " is before today.");
        if (messages.size() > 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE_LIST, messages);
            return result;
        }

        Collection toBeStored = new LinkedList();
        GenericValue newCc = delegator.makeValue("CreditCardInfo", null);
        toBeStored.add(newCc);

        Long newCcId = delegator.getNextSeqId("CreditCardInfo");
        if(newCcId == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not create credit card info (id generation failure)");
            return result;
        }
        newCc.set("partyId", partyId);
        newCc.set("nameOnCard", context.get("nameOnCard"));
        newCc.set("companyNameOnCard", context.get("companyNameOnCard"));
        newCc.set("cardType", context.get("cardType"));
        newCc.set("cardNumber", context.get("cardNumber"));
        newCc.set("cardSecurityCode", context.get("cardSecurityCode"));
        newCc.set("expireDate", context.get("expireDate"));
        newCc.set("contactMechId", context.get("contactMechId"));

        newCc.set("creditCardId", newCcId.toString());
        newCc.set("fromDate", now);

        GenericValue newPartyContactMechPurpose = null;
        String contactMechId = (String) context.get("contactMechId");
        if (contactMechId != null && contactMechId.length() > 0) {
            //add a PartyContactMechPurpose of BILLING_LOCATION if necessary
            String contactMechPurposeTypeId = "BILLING_LOCATION";

            GenericValue tempVal = null;
            try {
                Collection allPCMPs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId), null));
                tempVal = EntityUtil.getFirst(allPCMPs);
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                tempVal = null;
            }

            if (tempVal == null) {
                //no value found, create a new one
                newPartyContactMechPurpose = delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "fromDate", now));
            }
        }

        if (newPartyContactMechPurpose != null)
            toBeStored.add(newPartyContactMechPurpose);

        try {
            delegator.storeAll(toBeStored);
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not create credit card info (write failure): " + e.getMessage());
            return result;
        }

        result.put("creditCardId", newCc.getString("creditCardId"));
    
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
    /** Updates a CreditCardInfo entity according to the parameters passed in the context
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_UPDATE permission
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map updateCreditCardInfo(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Timestamp now = UtilDateTime.nowTimestamp();

        String partyId = (String) context.get("partyId");
        if (partyId == null || partyId.length() == 0) {
            partyId = userLogin.getString("partyId");
        }
        //partyId might be null, so check it
        if (partyId == null || partyId.length() == 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Party ID missing, cannot update credit card info");
            return result;
        }
        
        //<b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_UPDATE permission
        if (!partyId.equals(userLogin.getString("partyId"))) {
            if (!security.hasEntityPermission("PARTYMGR", "_UPDATE", userLogin)) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "You do not have permission to update credit card info for this partyId");
                return result;
            }
        }

        //do some more complicated/critical validation...
        List messages = new LinkedList();
        if (!UtilValidate.isCardMatch((String) context.get("cardType"), (String) context.get("cardNumber")))
            messages.add((String) context.get("cardNumber") + UtilValidate.isCreditCardPrefixMsg + 
                    (String) context.get("cardType") + UtilValidate.isCreditCardSuffixMsg + 
                    " (It appears to be a " + UtilValidate.getCardType((String) context.get("cardNumber")) + " credit card number)");
        if (!UtilValidate.isDateAfterToday((String) context.get("expireDate"))) 
            messages.add("The expiration date " + (String) context.get("expireDate") + " is before today.");
        if (messages.size() > 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE_LIST, messages);
            return result;
        }

        Collection toBeStored = new LinkedList();
        boolean isModified = false;

        GenericValue creditCardInfo = null;
        GenericValue newCc = null;
        String creditCardId = (String) context.get("creditCardId");
        try {
            creditCardInfo = delegator.findByPrimaryKey("CreditCardInfo", UtilMisc.toMap("creditCardId", creditCardId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not get credit card info to update (read error): " + e.getMessage());
            return result;
        }

        if (creditCardInfo == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not find credit card info to update with id " + creditCardId);
            return result;
        }
        
        newCc = new GenericValue(creditCardInfo);
        toBeStored.add(newCc);

        Long newCcId = delegator.getNextSeqId("CreditCardInfo");
        if(newCcId == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not update credit card info (id generation failure)");
            return result;
        }
        
        newCc.set("partyId", context.get("partyId"));
        newCc.set("nameOnCard", context.get("nameOnCard"));
        newCc.set("companyNameOnCard", context.get("companyNameOnCard"));
        newCc.set("cardType", context.get("cardType"));
        newCc.set("cardNumber", context.get("cardNumber"));
        newCc.set("cardSecurityCode", context.get("cardSecurityCode"));
        newCc.set("expireDate", context.get("expireDate"));
        newCc.set("contactMechId", context.get("contactMechId"));

        if (!newCc.equals(creditCardInfo)) {
            newCc.set("creditCardId", newCcId.toString());
            newCc.set("fromDate", now);
            isModified = true;
        }

        GenericValue newPartyContactMechPurpose = null;
        String contactMechId = (String) context.get("contactMechId");
        if (contactMechId != null && contactMechId.length() > 0) {
            //add a PartyContactMechPurpose of BILLING_LOCATION if necessary
            String contactMechPurposeTypeId = "BILLING_LOCATION";

            GenericValue tempVal = null;
            try {
                Collection allPCMPs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId), null));
                tempVal = EntityUtil.getFirst(allPCMPs);
            } catch(GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                tempVal = null;
            }

            if(tempVal == null) {
                //no value found, create a new one
                newPartyContactMechPurpose = delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "fromDate", now));
            }
        }

        if(isModified) {
            //Debug.logInfo("yes, is modified");
            if (newPartyContactMechPurpose != null) toBeStored.add(newPartyContactMechPurpose);

            //set thru date on old card
            creditCardInfo.set("thruDate", now);
            toBeStored.add(creditCardInfo);

            try {
                delegator.storeAll(toBeStored);
            } catch(GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not update credit card (write failure): " + e.getMessage());
                return result;
            }
        } else {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            result.put(ModelService.SUCCESS_MESSAGE, "No changes made, not updating credit card info");
            return result;
        }

        result.put("newCreditCardId", newCc.getString("creditCardId"));
    
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
    /** Deletes a CreditCardInfo entity according to the parameters passed in the context
     * <b>security check</b>: userLogin partyId must equal creditCardInfo partyId, or must have PARTYMGR_DELETE permission
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map deleteCreditCardInfo(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Timestamp now = UtilDateTime.nowTimestamp();
        
        //never delete a credit card, just put a to date on the link to the party
        String creditCardId = (String) context.get("creditCardId");
        GenericValue creditCardInfo = null;
        try {
            creditCardInfo = delegator.findByPrimaryKey("CreditCardInfo", UtilMisc.toMap("creditCardId", creditCardId));
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            creditCardInfo = null;
        }

        if(creditCardInfo == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not find credit card info to delete (read failure)");
            return result;
        }

        //<b>security check</b>: userLogin partyId must equal creditCardInfo partyId, or must have PARTYMGR_DELETE permission
        if (creditCardInfo.get("partyId") == null || !creditCardInfo.getString("partyId").equals(userLogin.getString("partyId"))) {
            if (!security.hasEntityPermission("PARTYMGR", "_DELETE", userLogin)) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "You do not have permission to delete credit card info for this partyId");
                return result;
            }
        }
        
        creditCardInfo.set("thruDate", now);
        try {
            creditCardInfo.store(); 
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not delete credit card info (write failure): " + e.getMessage());
            return result;
        }
        
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
}
