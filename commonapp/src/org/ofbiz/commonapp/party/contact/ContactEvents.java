/*
 * $Id$
 * $Log$
 * Revision 1.2  2001/12/23 06:29:42  jonesde
 * Replaced preStoreOther stuff with storeAll
 *
 * Revision 1.1  2001/10/19 16:44:42  azeneski
 * Moved Party/ContactMech/Login events to more appropiate packages.
 * 
 */

package org.ofbiz.commonapp.party.contact;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;

/**
 * <p><b>Title:</b> ContactEvents.java
 * <p><b>Description:</b> Events for Contact Mechanism maintenance.
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
 * @author Andy Zeneski (jaz@zsolv.com)
 * @author David E. Jones (jonesde@ofbiz.org) 
 * @version 1.0
 * Created on October 19, 2001, 8:34 AM
 */
public class ContactEvents {
    
    /** Updates a ContactMech entity according to the parameters passed in the
     *  request object; will do a CREATE, UPDATE, or DELETE depending on the
     *  value of the UPDATE_MODE parameter. May modify the ContactMech entity
     *  corresponding to the CONTACT_MECH_ID in the request object and
     *  the PartyContactMech entity corresponding to the CONTACT_MECH_ID and the
     *  current UserLogin value object in the session.
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String updateContactMech(HttpServletRequest request, HttpServletResponse response) {
        String errMsg = "";
        GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        if(userLogin == null) { request.setAttribute("ERROR_MESSAGE", "<li>ERROR: User not logged in, cannot update contact info. Please contact customer service."); return "error"; }
        String partyId = userLogin.getString("partyId");
        
        String updateMode = request.getParameter("UPDATE_MODE");
        
        Timestamp now = UtilDateTime.nowTimestamp();
        if("CREATE".equals(updateMode)) {
            Collection toBeStored = new LinkedList();
            
            String contactMechTypeId = request.getParameter("CONTACT_MECH_TYPE_ID");
            if(contactMechTypeId == null) { errMsg = "<li>ERROR: Could not create new contact info, type not specified. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
            
            Long newCmId = delegator.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not create new contact info (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
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
                    request.setAttribute("ERROR_MESSAGE", errMsg);
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
                    request.setAttribute("ERROR_MESSAGE", errMsg);
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
                request.setAttribute("ERROR_MESSAGE", errMsg);
                return "error";
            }
            request.setAttribute("CONTACT_MECH_ID", newCmId.toString());
        } else if("DELETE".equals(updateMode)) {
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
                request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not delete contact info (read failure or not found: party-contact mech) . Please contact customer service.");
                return "error";
            }
            partyContactMech.set("thruDate", UtilDateTime.nowTimestamp());
            try {
                partyContactMech.store();
            } catch(GenericEntityException e) {
                request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not delete contact info (write failure) . Please contact customer service.");
                return "error";
            }
        } else if("UPDATE".equals(updateMode)) {
            Collection toBeStored = new LinkedList();
            
            boolean isModified = false;
            Long newCmId = delegator.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not change contact info (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
            
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
                request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not find specified contact info (read error). Please contact customer service.");
                return "error";
            }
            if(partyContactMech == null) {
                request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Logged in user cannot update specified contact info because it does not belong to the user. Please contact customer service.");
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
                    request.setAttribute("ERROR_MESSAGE", errMsg);
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
                    request.setAttribute("ERROR_MESSAGE", errMsg);
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
                    request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not change contact info (write failure) . Please contact customer service.");
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
                    request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not change contact info (write failure) . Please contact customer service.");
                    return "error";
                }
                request.setAttribute("CONTACT_MECH_ID", newCmId.toString());
            } else {
                String cmId = contactMech.getString("contactMechId");
                String newCmPurposeTypeId = request.getParameter("CM_NEW_PURPOSE_TYPE_ID");
                if(newCmPurposeTypeId != null && newCmPurposeTypeId.length() > 0) {
                    try {
                        if(delegator.create("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", cmId, "contactMechPurposeTypeId", newCmPurposeTypeId, "fromDate", now)) == null) {
                            request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not change contact info (write failure) . Please contact customer service.");
                            return "error";
                        }
                    }
                    catch(GenericEntityException e) {
                        Debug.logWarning(e.getMessage());
                        request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not change contact info (write failure) . Please contact customer service.");
                        return "error";
                    }
                }
                request.setAttribute("CONTACT_MECH_ID", cmId);
            }
            
        } else {
            errMsg = "<li>ERROR: Specified Update Mode (" + updateMode + ") is not valid. Please contact customer service.";
            request.setAttribute("ERROR_MESSAGE", errMsg);
            return "error";
        }
        
        request.setAttribute("EVENT_MESSAGE", "Contact Information Updated.");
        return "success";
    }
    
    /** Creates a PartyContactMechPurpose given the parameters in the request object
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String createPartyContactMechPurpose(HttpServletRequest request, HttpServletResponse response) {
        String errMsg = "";
        GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        if(userLogin == null) { errMsg = "<li>ERROR: User not logged in, cannot add purpose to contact mechanism. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
        String partyId = userLogin.getString("partyId");
        
        String contactMechId = request.getParameter("CONTACT_MECH_ID");
        String contactMechPurposeTypeId = request.getParameter("CONTACT_MECH_PURPOSE_TYPE_ID");
        if(contactMechPurposeTypeId == null || contactMechPurposeTypeId.length() <= 0) { errMsg = "<li>ERROR: Purpose type not specified, cannot add purpose to contact mechanism. Please try again."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
        
        
        GenericValue tempVal = null;
        try {
            Collection allPCMPs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId), null));
            tempVal = EntityUtil.getFirst(allPCMPs);
        } catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); tempVal = null; }
        
        if(tempVal != null) {
            //exists already with valid date, show warning
            request.setAttribute("ERROR_MESSAGE", "<li>Could not create new purpose, a purpose with that type already exists.");
            return "error";
        } else {
            //no entry with a valid date range exists, create new with open thruDate
            GenericValue newPartyContactMechPurpose = delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "fromDate", UtilDateTime.nowTimestamp()));
            try {
                if(delegator.create(newPartyContactMechPurpose) == null) {
                    errMsg = "<li>ERROR: Could not add purpose to contact mechanism (write failure). Please contact customer service.";
                    request.setAttribute("ERROR_MESSAGE", errMsg);
                    return "error";
                }
            } catch(GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not add purpose to contact mechanism (write failure). Please contact customer service.");
                return "error";
            }
        }
        return "success";
    }
    
    /** Deletes the PartyContactMechPurpose corresponding to the parameters in the request object
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String deletePartyContactMechPurpose(HttpServletRequest request, HttpServletResponse response) {
        String errMsg = "";
        GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        if(userLogin == null) { errMsg = "<li>ERROR: User not logged in, cannot delete contact mechanism purpose. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
        String partyId = userLogin.getString("partyId");
        
        String contactMechId = request.getParameter("CONTACT_MECH_ID");
        String contactMechPurposeTypeId = request.getParameter("CONTACT_MECH_PURPOSE_TYPE_ID");
        if(contactMechPurposeTypeId == null || contactMechPurposeTypeId.length() <= 0) { errMsg = "<li>ERROR: Purpose type not specified, cannot delete purpose from contact mechanism. Please try again."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
        
        String fromDateStr = request.getParameter("FROM_DATE");
        Timestamp fromDate = null;
        try {
            fromDate = Timestamp.valueOf(fromDateStr);
        } catch(Exception e) {
            request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not delete purpose from contact mechanism, from date \"" + fromDateStr + "\" was not valid. Please contact customer service.");
            return "error";
        }
        
        GenericValue pcmp = null;
        try {
            pcmp = delegator.findByPrimaryKey("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "fromDate", fromDate));
            if(pcmp == null) {
                errMsg = "<li>ERROR: Could not delete purpose from contact mechanism (record not found). Please contact customer service.";
                request.setAttribute("ERROR_MESSAGE", errMsg);
                return "error";
            }
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not delete purpose from contact mechanism (read failure). Please contact customer service.");
            return "error";
        }
        
        pcmp.set("thruDate", UtilDateTime.nowTimestamp());
        try {
            pcmp.store();
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not delete purpose from contact mechanism (write failure) . Please contact customer service.");
            return "error";
        }
        return "success";
    }
    
    /** Updates a CreditCardInfo entity according to the parameters passed in the
     *  request object; will do a CREATE, UPDATE, or DELETE depending on the
     *  value of the UPDATE_MODE parameter.
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String updateCreditCardInfo(HttpServletRequest request, HttpServletResponse response) {
        String errMsg = "";
        GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        if(userLogin == null) { errMsg = "<li>ERROR: User not logged in, cannot update credit card info. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
        String partyId = userLogin.getString("partyId");
        
        String updateMode = request.getParameter("UPDATE_MODE");
        
        Timestamp now = UtilDateTime.nowTimestamp();
        if("CREATE".equals(updateMode) || "UPDATE".equals(updateMode)) {
            Collection toBeStored = new LinkedList();
            boolean isModified = false;
            
            String nameOnCard = request.getParameter("CC_NAME_ON_CARD");
            String companyNameOnCard = request.getParameter("CC_COMPANY_NAME_ON_CARD");
            String cardType = request.getParameter("CC_CARD_TYPE");
            String cardNumber = request.getParameter("CC_CARD_NUMBER");
            String cardSecurityCode = request.getParameter("CC_CARD_SECURITY_CODE");
            String expMonth = request.getParameter("CC_EXPIRE_DATE_MONTH");
            String expYear = request.getParameter("CC_EXPIRE_DATE_YEAR");
            String expireDate = expMonth + "/" + expYear;
            String contactMechId = request.getParameter("CC_CONTACT_MECH_ID");
            
            if(!UtilValidate.isNotEmpty(nameOnCard)) errMsg += "<li>Name on Card missing.";
            if(!UtilValidate.isNotEmpty(cardType)) errMsg += "<li>Card Type missing.";
            if(!UtilValidate.isNotEmpty(cardNumber)) errMsg += "<li>Card Number missing.";
            if(!UtilValidate.isNotEmpty(expMonth)) errMsg += "<li>Expiration Month missing.";
            if(!UtilValidate.isNotEmpty(expYear)) errMsg += "<li>Expiration Year missing.";
            if(!UtilValidate.isAnyCard(cardNumber)) errMsg += "<li>" + UtilValidate.isAnyCardMsg;
            if(!UtilValidate.isCardMatch(cardType, cardNumber)) errMsg += "<li>" + cardNumber + UtilValidate.isCreditCardPrefixMsg + cardType + UtilValidate.isCreditCardSuffixMsg + " (It appears to be a " + UtilValidate.getCardType(cardNumber) + " credit card number)";
            if(!UtilValidate.isDateAfterToday(expireDate)) errMsg += "<li>The expiration date " + expireDate + " is before today.";
            if(errMsg.length() > 0) {
                errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
                request.setAttribute("ERROR_MESSAGE", errMsg);
                return "error";
            }
            
            GenericValue creditCardInfo = null;
            GenericValue newCc = null;
            if("UPDATE".equals(updateMode)) {
                String creditCardId = request.getParameter("CREDIT_CARD_ID");
                try { creditCardInfo = delegator.findByPrimaryKey("CreditCardInfo", UtilMisc.toMap("creditCardId", creditCardId)); }
                catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); creditCardInfo = null; }
            }
            if(creditCardInfo != null) newCc = new GenericValue(creditCardInfo);
            else newCc = delegator.makeValue("CreditCardInfo", null);
            toBeStored.add(newCc);
            
            Long newCcId = delegator.getNextSeqId("CreditCardInfo"); if(newCcId == null) { errMsg = "<li>ERROR: Could not create new contact info (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
            newCc.set("partyId", partyId);
            newCc.set("nameOnCard", nameOnCard);
            newCc.set("companyNameOnCard", companyNameOnCard);
            newCc.set("cardType", cardType);
            newCc.set("cardNumber", cardNumber);
            newCc.set("cardSecurityCode", cardSecurityCode);
            newCc.set("expireDate", expireDate);
            newCc.set("contactMechId", contactMechId);
            
            if("UPDATE".equals(updateMode)) {
                if(!newCc.equals(creditCardInfo)) {
                    newCc.set("creditCardId", newCcId.toString());
                    newCc.set("fromDate", now);
                    isModified = true;
                }
            } else {
                //is CREATE, set values
                newCc.set("creditCardId", newCcId.toString());
                newCc.set("fromDate", now);
                isModified = true;
            }
            
            GenericValue newPartyContactMechPurpose = null;
            if(contactMechId != null && contactMechId.length() > 0) {
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
                Debug.logInfo("yes, is modified");
                if(newPartyContactMechPurpose != null) toBeStored.add(newPartyContactMechPurpose);
                if("UPDATE".equals(updateMode)) {
                    //if it is an update, set thru date on old card
                    creditCardInfo.set("thruDate", now);
                    toBeStored.add(creditCardInfo);
                }
                
                try {
                    delegator.storeAll(toBeStored);
                } catch(GenericEntityException e) {
                    Debug.logWarning(e.getMessage());
                    request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not add credit card (write failure). Please contact customer service.");
                    return "error";
                }
            }
            request.setAttribute("CREDIT_CARD_ID", newCc.getString("creditCardId"));
            
        } else if("DELETE".equals(updateMode)) {
            //never delete a credit card, just put a to date on the link to the party
            String creditCardId = request.getParameter("CREDIT_CARD_ID");
            GenericValue creditCardInfo = null;
            try { creditCardInfo = delegator.findByPrimaryKey("CreditCardInfo", UtilMisc.toMap("creditCardId", creditCardId)); }
            catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); creditCardInfo = null; }
            
            if(creditCardInfo == null) {
                request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not find credit card info to delete (read failure). Please contact customer service.");
                return "error";
            }
            
            creditCardInfo.set("thruDate", now);
            try {
                creditCardInfo.store(); 
            } catch(GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not delete credit card info (write failure). Please contact customer service.");
                return "error";
            }
        } else {
            errMsg = "<li>ERROR: Specified Update Mode (" + updateMode + ") is not valid. Please contact customer service.";
            request.setAttribute("ERROR_MESSAGE", errMsg);
            return "error";
        }
        
        request.setAttribute("EVENT_MESSAGE", "Credit Card Information Updated.");
        return "success";
    }
}
