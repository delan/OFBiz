/*
 * $Id$
 * $Log$
 * Revision 1.5  2002/01/23 11:28:12  jonesde
 * Slimmed down a little with some useful new functions in ServiceUtil
 *
 * Revision 1.4  2002/01/23 01:20:00  jonesde
 * Added postal address, telecom number and email address specific service implementations
 *
 * Revision 1.3  2002/01/22 07:47:04  jonesde
 * A bunch more stuff for contact mech services
 *
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
                "fromDate", now, "roleTypeId", "CUSTOMER", "allowSolicitation", context.get("allowSolicitation"), "extension", context.get("extension"))));

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
        Collection toBeStored = new LinkedList();
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
            Collection partyContactMechs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), null));
            partyContactMech = EntityUtil.getOnly(partyContactMechs);
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
            Collection partyContactMechs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), null));
            partyContactMech = EntityUtil.getOnly(partyContactMechs);
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
        Collection toBeStored = new LinkedList();
        
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
                "fromDate", now, "roleTypeId", "CUSTOMER", "allowSolicitation", context.get("allowSolicitation"), "extension", context.get("extension"))));

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
        Collection toBeStored = new LinkedList();
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
            Collection partyContactMechs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), null));
            partyContactMech = EntityUtil.getOnly(partyContactMechs);
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
        Collection toBeStored = new LinkedList();
        
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
                "fromDate", now, "roleTypeId", "CUSTOMER", "allowSolicitation", context.get("allowSolicitation"), "extension", context.get("extension"))));

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
        Collection toBeStored = new LinkedList();
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
            Collection partyContactMechs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), null));
            partyContactMech = EntityUtil.getOnly(partyContactMechs);
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
        newContext.put("contactMechTypeId", newContext.get("EMAIL_ADDRESS"));
        
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
            Collection allPCMPs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId), null));
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

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_CREATE");
        if (result.size() > 0)
            return result;
        
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
            return ServiceUtil.returnError("ERROR: Could not create credit card info (id generation failure)");
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
            return ServiceUtil.returnError("ERROR: Could not create credit card info (write failure): " + e.getMessage());
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

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_UPDATE");
        if (result.size() > 0)
            return result;

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
            return ServiceUtil.returnError("ERROR: Could not get credit card info to update (read error): " + e.getMessage());
        }

        if (creditCardInfo == null) {
            return ServiceUtil.returnError("ERROR: Could not find credit card info to update with id " + creditCardId);
        }
        
        newCc = new GenericValue(creditCardInfo);
        toBeStored.add(newCc);

        Long newCcId = delegator.getNextSeqId("CreditCardInfo");
        if(newCcId == null) {
            return ServiceUtil.returnError("ERROR: Could not update credit card info (id generation failure)");
        }
        
        newCc.set("partyId", partyId);
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
                return ServiceUtil.returnError("ERROR: Could not update credit card (write failure): " + e.getMessage());
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
            return ServiceUtil.returnError("ERROR: Could not find credit card info to delete (read failure)");
        }

        //<b>security check</b>: userLogin partyId must equal creditCardInfo partyId, or must have PARTYMGR_DELETE permission
        if (creditCardInfo.get("partyId") == null || !creditCardInfo.getString("partyId").equals(userLogin.getString("partyId"))) {
            if (!security.hasEntityPermission("PARTYMGR", "_DELETE", userLogin)) {
                return ServiceUtil.returnError("You do not have permission to delete credit card info for this partyId");
            }
        }
        
        creditCardInfo.set("thruDate", now);
        try {
            creditCardInfo.store(); 
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("ERROR: Could not delete credit card info (write failure): " + e.getMessage());
        }
        
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
}
