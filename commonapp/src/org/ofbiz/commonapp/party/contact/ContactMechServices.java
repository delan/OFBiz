/*
 * $Id$
 * $Log$
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
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map createContactMech(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        String partyId = (String) context.get("partyId");
        if (partyId == null || partyId.length() == 0) {
            partyId = userLogin.getString("partyId");
        }
        
        Timestamp now = UtilDateTime.nowTimestamp();
      /*
        Collection toBeStored = new LinkedList();

        String contactMechTypeId = request.getParameter("CONTACT_MECH_TYPE_ID");
        if(contactMechTypeId == null) { errMsg = "<li>ERROR: Could not create new contact info, type not specified. Please contact customer service."; request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg); return "error"; }

        Long newCmId = delegator.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not create new contact info (id generation failure). Please contact customer service."; request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg); return "error"; }
        GenericValue tempContactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", contactMechTypeId));
        toBeStored.add(tempContactMech);

        String allowSolicitation = request.getParameter("CM_ALLOW_SOL");
        String extension = request.getParameter("CM_EXTENSION");
        toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", newCmId.toString(), "fromDate", now, "roleTypeId", "CUSTOMER", "allowSolicitation", allowSolicitation, "extension", extension)));

        String newCmPurposeTypeId = request.getParameter("CM_NEW_PURPOSE_TYPE_ID");
        if(newCmPurposeTypeId != null && newCmPurposeTypeId.length() > 0)
            toBeStored.add(delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", newCmId.toString(), "contactMechPurposeTypeId", newCmPurposeTypeId, "fromDate", now)));

        if("POSTAL_ADDRESS".equals(contactMechTypeId)) {
            String toName = request.getParameter("CM_TO_NAME");
            String attnName = request.getParameter("CM_ATTN_NAME");
            String address1 = request.getParameter("CM_ADDRESS1");
            String address2 = request.getParameter("CM_ADDRESS2");
            String city = request.getParameter("CM_CITY");
            String state = request.getParameter("CM_STATE");
            String postalCode = request.getParameter("CM_POSTAL_CODE");
            String country = request.getParameter("CM_COUNTRY");
            String directions = "";

            if(!UtilValidate.isNotEmpty(address1)) errMsg += "<li>Address Line 1 missing.";
            if(!UtilValidate.isNotEmpty(city)) errMsg += "<li>City missing.";
            if(!UtilValidate.isNotEmpty(state)) errMsg += "<li>State missing.";
            if(!UtilValidate.isNotEmpty(postalCode)) errMsg += "<li>Zip/Postal Code missing.";
            if(errMsg.length() > 0) {
                errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
                request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
                return "error";
            }

            GenericValue newAddr = delegator.makeValue("PostalAddress", null);
            newAddr.set("contactMechId", newCmId.toString());
            newAddr.set("toName", toName);
            newAddr.set("attnName", attnName);
            newAddr.set("address1", address1);
            newAddr.set("address2", address2);
            newAddr.set("directions", directions);
            newAddr.set("city", city);
            newAddr.set("postalCode", postalCode);
            newAddr.set("stateProvinceGeoId", state);
            newAddr.set("countryGeoId", country);
            //newAddr.set("postalCodeGeoId", postalCodeGeoId);
            toBeStored.add(newAddr);
        } else if("TELECOM_NUMBER".equals(contactMechTypeId)) {
            String countryCode = request.getParameter("CM_COUNTRY_CODE");
            String areaCode = request.getParameter("CM_AREA_CODE");
            String contactNumber = request.getParameter("CM_CONTACT_NUMBER");
            toBeStored.add(delegator.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCmId.toString(), "countryCode", countryCode, "areaCode", areaCode, "contactNumber", contactNumber)));
        } else if("EMAIL_ADDRESS".equals(contactMechTypeId)) {
            String infoString = request.getParameter("CM_INFO_STRING");
            if(!UtilValidate.isEmail(infoString)) errMsg += "<li>" + UtilValidate.isEmailMsg;
            if(errMsg.length() > 0) {
                errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
                request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
                return "error";
            }
            tempContactMech.set("infoString", infoString);

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
            String infoString = request.getParameter("CM_INFO_STRING");
            tempContactMech.set("infoString", infoString);
        }

        try { 
            delegator.storeAll(toBeStored);
        } catch(GenericEntityException e) {
            errMsg = "<li>ERROR: Could not change contact info (write failure) . Please contact customer service.";
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
            return "error";
        }
        request.setAttribute("CONTACT_MECH_ID", newCmId.toString());
        return "success";
       */
        return null;
    }

    /** Updates a ContactMech
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map updateContactMech(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        String partyId = (String) context.get("partyId");
        if (partyId == null || partyId.length() == 0) {
            partyId = userLogin.getString("partyId");
        }
        
        Timestamp now = UtilDateTime.nowTimestamp();
      /*
        Collection toBeStored = new LinkedList();

        boolean isModified = false;
        Long newCmId = delegator.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not change contact info (id generation failure). Please contact customer service."; request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg); return "error"; }

        String contactMechId = request.getParameter("CONTACT_MECH_ID");
        GenericValue contactMech = null;
        GenericValue partyContactMech = null;
        try {
            contactMech = delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", contactMechId));
            //try to find a PartyContactMech with a valid date range
            Collection partyContactMechs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), null));
            partyContactMech = EntityUtil.getOnly(partyContactMechs);
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage()); contactMech = null; partyContactMech = null;
        } catch(IllegalArgumentException e) {
            Debug.logWarning(e.getMessage()); partyContactMech = null;
        }
        if(contactMech == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>ERROR: Could not find specified contact info (read error). Please contact customer service.");
            return "error";
        }
        if(partyContactMech == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>ERROR: Logged in user cannot update specified contact info because it does not belong to the user. Please contact customer service.");
            return "error";
        }
        toBeStored.add(partyContactMech);

        //never change a contact mech, just create a new one with the changes
        GenericValue newContactMech = new GenericValue(contactMech);
        GenericValue newPartyContactMech = new GenericValue(partyContactMech);
        GenericValue relatedEntityToSet = null;

        if("POSTAL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))) {
            String toName = request.getParameter("CM_TO_NAME");
            String attnName = request.getParameter("CM_ATTN_NAME");
            String address1 = request.getParameter("CM_ADDRESS1");
            String address2 = request.getParameter("CM_ADDRESS2");
            String city = request.getParameter("CM_CITY");
            String state = request.getParameter("CM_STATE");
            String postalCode = request.getParameter("CM_POSTAL_CODE");
            String country = request.getParameter("CM_COUNTRY");
            String directions = "";

            if(!UtilValidate.isNotEmpty(address1)) errMsg += "<li>Address Line 1 missing.";
            if(!UtilValidate.isNotEmpty(city)) errMsg += "<li>City missing.";
            if(!UtilValidate.isNotEmpty(state)) errMsg += "<li>State missing.";
            if(!UtilValidate.isNotEmpty(postalCode)) errMsg += "<li>Zip/Postal Code missing.";
            if(errMsg.length() > 0) {
                errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
                request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
                return "error";
            }

            GenericValue addr = null;
            try { addr = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId)); }
            catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); addr = null; }
            relatedEntityToSet = new GenericValue(addr);
            relatedEntityToSet.set("toName", toName);
            relatedEntityToSet.set("attnName", attnName);
            relatedEntityToSet.set("address1", address1);
            relatedEntityToSet.set("address2", address2);
            relatedEntityToSet.set("directions", directions);
            relatedEntityToSet.set("city", city);
            relatedEntityToSet.set("postalCode", postalCode);
            relatedEntityToSet.set("stateProvinceGeoId", state);
            relatedEntityToSet.set("countryGeoId", country);
            //relatedEntityToSet.set("postalCodeGeoId", postalCodeGeoId);
            if(addr == null || !relatedEntityToSet.equals(addr)) {
                isModified = true;
            }
            relatedEntityToSet.set("contactMechId", newCmId.toString());
        } else if("TELECOM_NUMBER".equals(contactMech.getString("contactMechTypeId"))) {
            String countryCode = request.getParameter("CM_COUNTRY_CODE");
            String areaCode = request.getParameter("CM_AREA_CODE");
            String contactNumber = request.getParameter("CM_CONTACT_NUMBER");
            String extension = request.getParameter("CM_EXTENSION");

            GenericValue telNum = null;
            try { telNum = delegator.findByPrimaryKey("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechId)); }
            catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); telNum = null; }
            relatedEntityToSet = new GenericValue(telNum);
            relatedEntityToSet.set("countryCode", countryCode);
            relatedEntityToSet.set("areaCode", areaCode);
            relatedEntityToSet.set("contactNumber", contactNumber);

            if(telNum == null || !relatedEntityToSet.equals(telNum)) {
                isModified = true;
            }
            relatedEntityToSet.set("contactMechId", newCmId.toString());
            newPartyContactMech.set("extension", extension);
        }  else if("EMAIL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))) {
            String infoString = request.getParameter("CM_INFO_STRING");
            if(!UtilValidate.isEmail(infoString)) errMsg += "<li>" + UtilValidate.isEmailMsg;
            if(errMsg.length() > 0) {
                errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
                request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
                return "error";
            }
            newContactMech.set("infoString", infoString);
        } else {
            String infoString = request.getParameter("CM_INFO_STRING");
            newContactMech.set("infoString", infoString);
        }

        String allowSolicitation = request.getParameter("CM_ALLOW_SOL");
        newPartyContactMech.set("allowSolicitation", allowSolicitation);

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
                Debug.logWarning(e.getMessage());
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>ERROR: Could not change contact info (write failure) . Please contact customer service.");
                return "error";
            }

            String newCmPurposeTypeId = request.getParameter("CM_NEW_PURPOSE_TYPE_ID");
            if(newCmPurposeTypeId != null && newCmPurposeTypeId.length() > 0) {
                toBeStored.add(delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", newCmId.toString(), "contactMechPurposeTypeId", newCmPurposeTypeId, "fromDate", now)));
            }

            partyContactMech.set("thruDate", now);
            try { 
                delegator.storeAll(toBeStored);
            } catch(GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>ERROR: Could not change contact info (write failure) . Please contact customer service.");
                return "error";
            }
            request.setAttribute("CONTACT_MECH_ID", newCmId.toString());
        } else {
            String cmId = contactMech.getString("contactMechId");
            String newCmPurposeTypeId = request.getParameter("CM_NEW_PURPOSE_TYPE_ID");
            if(newCmPurposeTypeId != null && newCmPurposeTypeId.length() > 0) {
                try {
                    if(delegator.create("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", cmId, "contactMechPurposeTypeId", newCmPurposeTypeId, "fromDate", now)) == null) {
                        request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>ERROR: Could not change contact info (write failure) . Please contact customer service.");
                        return "error";
                    }
                }
                catch(GenericEntityException e) {
                    Debug.logWarning(e.getMessage());
                    request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>ERROR: Could not change contact info (write failure) . Please contact customer service.");
                    return "error";
                }
            }
            request.setAttribute("CONTACT_MECH_ID", cmId);
        }

        return "success";
       */
        return null;
    }
    
    /** Deletes a ContactMech
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map deleteContactMech(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        String partyId = (String) context.get("partyId");
        if (partyId == null || partyId.length() == 0) {
            partyId = userLogin.getString("partyId");
        }
        
        Timestamp now = UtilDateTime.nowTimestamp();
      /*
        //never delete a contact mechanism, just put a to date on the link to the party
        String contactMechId = request.getParameter("CONTACT_MECH_ID");
        GenericValue partyContactMech = null;
        try {
            //try to find a PartyContactMech with a valid date range
            Collection partyContactMechs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), null));
            partyContactMech = EntityUtil.getOnly(partyContactMechs);
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage()); partyContactMech = null;
        } catch(IllegalArgumentException e) {
            Debug.logWarning(e.getMessage()); partyContactMech = null;
        }
        if(partyContactMech == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>ERROR: Could not delete contact info (read failure or not found: party-contact mech) . Please contact customer service.");
            return "error";
        }
        partyContactMech.set("thruDate", UtilDateTime.nowTimestamp());
        try {
            partyContactMech.store();
        } catch(GenericEntityException e) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>ERROR: Could not delete contact info (write failure) . Please contact customer service.");
            return "error";
        }

        return "success";
       */
        return null;
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

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        result.put("fromDate", fromDate);
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
