/*
 * $Id$
 * $Log$
 * Revision 1.24  2001/09/25 15:02:14  epabst
 * when adding an EMAIL_ADDRESS, if no PRIMARY_EMAIL is currently
 * set, it is automatically set to be the PRIMARY_EMAIL address.  If it is set, the
 * EMAIL_ADDRESS is automatically set to be an OTHER_EMAIL address.
 *
 * Revision 1.23  2001/09/25 14:42:12  epabst
 * added password hint
 * added getCurrentPartyContactMechList helper method
 * created (via refactoring) setPassword method to validate and set it and the hint
 * fixed minor bugs
 * fixed minor formatting
 *
 * Revision 1.22  2001/09/19 08:42:08  jonesde
 * Initial checkin of refactored entity engine.
 *
 * Revision 1.21  2001/09/18 22:31:48  jonesde
 * Cleaned up messages, fixed a few small bugs.
 *
 * Revision 1.20  2001/09/13 03:38:58  jonesde
 * Cleanups, a few bugs fixed, added index.jsp which forwards to control/main
 *
 * Revision 1.19  2001/09/11 00:51:46  jonesde
 * A few changes to correspond with the moving of the KeywordSearch file, etc.
 *
 * Revision 1.18  2001/09/06 23:12:49  jonesde
 * Credit Cards and Contact Mechs no longer update if no info has changed, some other things cleaned up too.
 *
 * Revision 1.17  2001/09/05 21:52:34  jonesde
 * Added manual add of a partyconactmechpurpose on creation of a contactmech.
 *
 * Revision 1.16  2001/09/03 21:15:38  jonesde
 * Added select address from existing addresses in contact mech list; adds BILLING_LOCATION purpose if not already set.
 *
 * Revision 1.15  2001/09/03 16:37:18  jonesde
 * Finished person Date field inputs; Added some better null handling
 *
 * Revision 1.14  2001/09/03 07:32:46  jonesde
 * Small changes to take advantage of the new set semantics.
 *
 * Revision 1.13  2001/09/02 09:30:22  jonesde
 * Added initial edit person and enhanced validation for credit cards.
 *
 * Revision 1.12  2001/09/02 05:20:22  jonesde
 * Initial pass on create/edit credit card functionality.
 *
 * Revision 1.11  2001/09/02 01:04:44  jonesde
 * Changed flow so contact mech updates stay on same page; added event message to confirm changes, displayed by errormsg.jsp, even though it's not an error message.
 *
 * Revision 1.10  2001/09/02 00:35:20  jonesde
 * Added change password page
 *
 * Revision 1.9  2001/08/31 22:45:18  jonesde
 * Added final touches to contact mech maint, including some validators
 *
 * Revision 1.8  2001/08/31 19:07:33  jonesde
 * Added create & delete of party contact mech purpose
 *
 * Revision 1.7  2001/08/31 06:41:58  jonesde
 * Cleaned up the new contact info process
 *
 * Revision 1.6  2001/08/31 00:39:48  jonesde
 * First pass of editcontactmech and CustomerEvents.updateContactMech finished.
 *
 * Revision 1.5  2001/08/30 21:02:18  jonesde
 * Fixed little throws clause bug stopping it from compiling.
 *
 * Revision 1.4  2001/08/30 20:58:15  jonesde
 * Cleaned up some CSS stuff and some finishing touches on the newcustomer and viewprofile pieces, and added initial editcontactmech stuff.
 *
 * Revision 1.3  2001/08/30 16:20:55  jonesde
 * Greatly expanded new customer, also now uses the UtilValidate routines
 *
 * Revision 1.2  2001/08/30 07:23:40  jonesde
 * First mostly working revision of the new customer and view profile pages and new customer event.
 *
 * Revision 1.1  2001/08/30 00:27:49  jonesde
 * Added initial revision of customer information maintenance.
 *
 *
 */
package org.ofbiz.ecommerce.customer;

import javax.servlet.http.*;
import javax.servlet.*;
import java.util.*;
import java.sql.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.commonapp.party.contact.ContactHelper;

/**
 * <p><b>Title:</b> CustomerEvents.java
 * <p><b>Description:</b> Events for customer information maintenance.
 * <p>Copyright (c) 2001 The Open For Business Project (www.ofbiz.org) and repected authors.
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
 * Created on August 29, 2001
 */
public class CustomerEvents {
  /** Creates the necessary database entries for a new customer account based on
   *  the form inputs of the corresponding JSP customer/newcustomer.jsp.
   *@param request The HTTPRequest object for the current request
   *@param response The HTTPResponse object for the current request
   *@return String specifying the exit status of this event
   */
  public static String createCustomer(HttpServletRequest request, HttpServletResponse response) {
    GenericValue newUserLogin = null;
    
    String errMsg = "";
    GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
    
    String username = request.getParameter("USERNAME");
    String password = request.getParameter("PASSWORD");
    String confirmPassword = request.getParameter("CONFIRM_PASSWORD");
    String passwordHint = request.getParameter("PASSWORD_HINT");

    
    //get all parameters:
    String firstName = request.getParameter("USER_FIRST_NAME");
    String middleName = request.getParameter("USER_MIDDLE_NAME");
    String lastName = request.getParameter("USER_LAST_NAME");
    String personalTitle = request.getParameter("USER_TITLE");
    String suffix = request.getParameter("USER_SUFFIX");
    
    String homeCountryCode = request.getParameter("CUSTOMER_HOME_COUNTRY");
    String homeAreaCode = request.getParameter("CUSTOMER_HOME_AREA");
    String homeContactNumber = request.getParameter("CUSTOMER_HOME_CONTACT");
    String homeExt = request.getParameter("CUSTOMER_HOME_EXT");
    String homeAllowSolicitation = request.getParameter("CUSTOMER_HOME_ALLOW_SOL");
    
    String workCountryCode = request.getParameter("CUSTOMER_WORK_COUNTRY");
    String workAreaCode = request.getParameter("CUSTOMER_WORK_AREA");
    String workContactNumber = request.getParameter("CUSTOMER_WORK_CONTACT");
    String workExt = request.getParameter("CUSTOMER_WORK_EXT");
    String workAllowSolicitation = request.getParameter("CUSTOMER_WORK_ALLOW_SOL");
    
    String faxCountryCode = request.getParameter("CUSTOMER_FAX_COUNTRY");
    String faxAreaCode = request.getParameter("CUSTOMER_FAX_AREA");
    String faxContactNumber = request.getParameter("CUSTOMER_FAX_CONTACT");
    String faxAllowSolicitation = request.getParameter("CUSTOMER_FAX_ALLOW_SOL");
    
    String mobileCountryCode = request.getParameter("CUSTOMER_MOBILE_COUNTRY");
    String mobileAreaCode = request.getParameter("CUSTOMER_MOBILE_AREA");
    String mobileContactNumber = request.getParameter("CUSTOMER_MOBILE_CONTACT");
    String mobileAllowSolicitation = request.getParameter("CUSTOMER_MOBILE_ALLOW_SOL");
    
    String email = request.getParameter("CUSTOMER_EMAIL");
    String emailAllowSolicitation = request.getParameter("CUSTOMER_EMAIL_ALLOW_SOL");
    
    String address1 = request.getParameter("CUSTOMER_ADDRESS1");
    String address2 = request.getParameter("CUSTOMER_ADDRESS2");
    String city = request.getParameter("CUSTOMER_CITY");
    String state = request.getParameter("CUSTOMER_STATE");
    String postalCode = request.getParameter("CUSTOMER_POSTAL_CODE");
    String country = request.getParameter("CUSTOMER_COUNTRY");
    String directions = "";
    String addressAllowSolicitation = request.getParameter("CUSTOMER_ADDRESS_ALLOW_SOL");
    
    if(!UtilValidate.isNotEmpty(firstName)) errMsg += "<li>First name missing.";
    if(!UtilValidate.isNotEmpty(lastName)) errMsg += "<li>Last name missing.";
    if(!UtilValidate.isNotEmpty(address1)) errMsg += "<li>Address Line 1 missing.";
    if(!UtilValidate.isNotEmpty(city)) errMsg += "<li>City missing.";
    if(UtilValidate.isNotEmpty(country) && (country.equals("USA") || country.equals("CAN"))) {
      if(!UtilValidate.isNotEmpty(state)) errMsg += "<li>State missing.";
    }
    if(!UtilValidate.isNotEmpty(postalCode)) errMsg += "<li>Zip/Postal Code missing.";
    if(!UtilValidate.isNotEmpty(country)) errMsg += "<li>Country missing.";
    if(!UtilValidate.isNotEmpty(email)) errMsg += "<li>Email missing.";
    if(!UtilValidate.isEmail(email)) errMsg += "<li>" + UtilValidate.isEmailMsg;
    
    if(!UtilValidate.isNotEmpty(username)) errMsg += "<li>Username missing.";

    if(username != null && username.length() > 0) {
      GenericValue userLogin;
      try { userLogin = delegator.findByPrimaryKey(delegator.makePK("UserLogin", UtilMisc.toMap("userLoginId", username))); }
      catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); userLogin = null; }
      if(userLogin != null) {
        //UserLogin record found, user does exist: go back to new user page...
        errMsg += "<li>Username in use, please choose another.";
      }
    }
    
    GenericValue tempUserLogin = delegator.makeValue("UserLogin", UtilMisc.toMap("userLoginId", username, "partyId", username));
    if (UtilProperties.propertyValueEqualsIgnoreCase("ecommerce", "create.allow.password", "true")) {
        errMsg += setPassword(tempUserLogin, password, confirmPassword, passwordHint);
    }
 
    
    if(errMsg.length() > 0) {
      errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }
    
    //UserLogin with username does not exist: create new user...
    if(!UtilProperties.propertyValueEqualsIgnoreCase("ecommerce", "create.allow.password", "true")) password = UtilProperties.getPropertyValue("ecommerce", "default.customer.password", "ungssblepswd");
    
    Timestamp now = UtilDateTime.nowTimestamp();

    // create Party, PartyClass, Person, ContactMechs for Address, phones
    tempUserLogin.preStoreOther(delegator.makeValue("Party", UtilMisc.toMap("partyId", username)));
    tempUserLogin.preStoreOther(delegator.makeValue("PartyClassification", UtilMisc.toMap("partyId", username, "partyTypeId", "PERSON", "partyClassificationTypeId", "PERSON_CLASSIFICATION", "fromDate", now)));
    tempUserLogin.preStoreOther(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", username, "roleTypeId", "CUSTOMER")));
    tempUserLogin.preStoreOther(delegator.makeValue("Person", UtilMisc.toMap("partyId", username, "firstName", firstName, "middleName", middleName, "lastName", lastName, "personalTitle", personalTitle, "suffix", suffix)));
    
    Long newCmId = null;
    //make address
    newCmId = delegator.getNextSeqId("ContactMech");
    if(newCmId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    tempUserLogin.preStoreOther(delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", "POSTAL_ADDRESS")));
    GenericValue newAddr = delegator.makeValue("PostalAddress", null);
    newAddr.set("contactMechId", newCmId.toString());
    newAddr.set("toName", firstName + " " + lastName);
    newAddr.set("address1", address1);
    newAddr.set("address2", address2);
    newAddr.set("directions", directions);
    newAddr.set("city", city);
    newAddr.set("postalCode", postalCode);
    newAddr.set("stateProvinceGeoId", state);
    newAddr.set("countryGeoId", country);
    //newAddr.set("postalCodeGeoId", postalCodeGeoId);
    tempUserLogin.preStoreOther(newAddr);
    tempUserLogin.preStoreOther(delegator.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "fromDate", now, "roleTypeId", "CUSTOMER", "allowSolicitation", addressAllowSolicitation)));
    tempUserLogin.preStoreOther(delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "contactMechPurposeTypeId", "SHIPPING_LOCATION", "fromDate", now)));
    
    //make home phone number
    if(UtilValidate.isNotEmpty(homeContactNumber)) {
      newCmId = delegator.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      tempUserLogin.preStoreOther(delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", "TELECOM_NUMBER")));
      tempUserLogin.preStoreOther(delegator.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCmId.toString(), "countryCode", homeCountryCode, "areaCode", homeAreaCode, "contactNumber", homeContactNumber)));
      tempUserLogin.preStoreOther(delegator.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "fromDate", now, "roleTypeId", "CUSTOMER", "allowSolicitation", homeAllowSolicitation, "extension", homeExt)));
      tempUserLogin.preStoreOther(delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "contactMechPurposeTypeId", "PHONE_HOME", "fromDate", now)));
    }
    
    //make work phone number
    if(UtilValidate.isNotEmpty(workContactNumber)) {
      newCmId = delegator.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      tempUserLogin.preStoreOther(delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", "TELECOM_NUMBER")));
      tempUserLogin.preStoreOther(delegator.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCmId.toString(), "countryCode", workCountryCode, "areaCode", workAreaCode, "contactNumber", workContactNumber)));
      tempUserLogin.preStoreOther(delegator.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "fromDate", now, "roleTypeId", "CUSTOMER", "allowSolicitation", workAllowSolicitation, "extension", workExt)));
      tempUserLogin.preStoreOther(delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "contactMechPurposeTypeId", "PHONE_WORK", "fromDate", now)));
    }
    
    //make fax number
    if(UtilValidate.isNotEmpty(faxContactNumber)) {
      newCmId = delegator.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      tempUserLogin.preStoreOther(delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", "TELECOM_NUMBER")));
      tempUserLogin.preStoreOther(delegator.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCmId.toString(), "countryCode", faxCountryCode, "areaCode", faxAreaCode, "contactNumber", faxContactNumber)));
      tempUserLogin.preStoreOther(delegator.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "fromDate", now, "roleTypeId", "CUSTOMER", "allowSolicitation", faxAllowSolicitation)));
      tempUserLogin.preStoreOther(delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "contactMechPurposeTypeId", "FAX_NUMBER", "fromDate", now)));
    }
    
    //make mobile phone number
    if(UtilValidate.isNotEmpty(mobileContactNumber)) {
      newCmId = delegator.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      tempUserLogin.preStoreOther(delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", "TELECOM_NUMBER")));
      tempUserLogin.preStoreOther(delegator.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCmId.toString(), "countryCode", mobileCountryCode, "areaCode", mobileAreaCode, "contactNumber", mobileContactNumber)));
      tempUserLogin.preStoreOther(delegator.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "fromDate", now, "roleTypeId", "CUSTOMER", "allowSolicitation", mobileAllowSolicitation)));
      tempUserLogin.preStoreOther(delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "contactMechPurposeTypeId", "PHONE_MOBILE", "fromDate", now)));
    }
    
    //make email
    newCmId = delegator.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    tempUserLogin.preStoreOther(delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", "EMAIL_ADDRESS", "infoString", email)));
    tempUserLogin.preStoreOther(delegator.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "fromDate", now, "roleTypeId", "CUSTOMER", "allowSolicitation", emailAllowSolicitation)));
    tempUserLogin.preStoreOther(delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "contactMechPurposeTypeId", "PRIMARY_EMAIL", "fromDate", now)));
    
    try { newUserLogin = delegator.create(tempUserLogin); }
    catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); newUserLogin = null; }
    if(newUserLogin == null) {
      request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not create new account (create failure). Please contact customer service.");
      return "error";
    }
    
    if(UtilProperties.propertyValueEqualsIgnoreCase("ecommerce", "create.allow.password", "true")) request.getSession().setAttribute(SiteDefs.USER_LOGIN, newUserLogin);
    return "success";
  }
  
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
    if(userLogin == null) { errMsg = "<li>ERROR: User not logged in, cannot update contact info. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    
    String updateMode = request.getParameter("UPDATE_MODE");
    
    Timestamp now = UtilDateTime.nowTimestamp();
    if("CREATE".equals(updateMode)) {
      String contactMechTypeId = request.getParameter("CONTACT_MECH_TYPE_ID");
      if(contactMechTypeId == null) { errMsg = "<li>ERROR: Could not create new contact info, type not specified. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      
      Long newCmId = delegator.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not create new contact info (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      GenericValue tempContactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", contactMechTypeId));
      
      String allowSolicitation = request.getParameter("CM_ALLOW_SOL");
      String extension = request.getParameter("CM_EXTENSION");
      tempContactMech.preStoreOther(delegator.makeValue("PartyContactMech", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", newCmId.toString(), "fromDate", now, "roleTypeId", "CUSTOMER", "allowSolicitation", allowSolicitation, "extension", extension)));
      
      String newCmPurposeTypeId = request.getParameter("CM_NEW_PURPOSE_TYPE_ID");
      if(newCmPurposeTypeId != null && newCmPurposeTypeId.length() > 0)
        tempContactMech.preStoreOther(delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", newCmId.toString(), "contactMechPurposeTypeId", newCmPurposeTypeId, "fromDate", now)));
      
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
        tempContactMech.preStoreOther(newAddr);
      }
      else if("TELECOM_NUMBER".equals(contactMechTypeId)) {
        String countryCode = request.getParameter("CM_COUNTRY_CODE");
        String areaCode = request.getParameter("CM_AREA_CODE");
        String contactNumber = request.getParameter("CM_CONTACT_NUMBER");
        tempContactMech.preStoreOther(delegator.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCmId.toString(), "countryCode", countryCode, "areaCode", areaCode, "contactNumber", contactNumber)));
      }
      else if("EMAIL_ADDRESS".equals(contactMechTypeId)) {
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
            if (UtilValidate.isEmpty(ContactHelper.getContactMech(party, "PRIMARY_EMAIL", null, false))) {
                cmPurposeTypeId = "PRIMARY_EMAIL";
            } else {
                cmPurposeTypeId = "OTHER_EMAIL";
            }
            tempContactMech.preStoreOther(delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", newCmId.toString(), "contactMechPurposeTypeId", cmPurposeTypeId, "fromDate", now)));
        } catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); }
      }
      else {
        String infoString = request.getParameter("CM_INFO_STRING");
        tempContactMech.set("infoString", infoString);
      }
      
      GenericValue dummyValue = null;
      try { dummyValue = delegator.create(tempContactMech); }
      catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); dummyValue = null; }
      if(dummyValue == null) {
        errMsg = "<li>ERROR: Could not change contact info (write failure) . Please contact customer service.";
        request.setAttribute("ERROR_MESSAGE", errMsg);
        return "error";
      }
      request.setAttribute("CONTACT_MECH_ID", newCmId.toString());
    }
    else if("DELETE".equals(updateMode)) {
      //never delete a contact mechanism, just put a to date on the link to the party
      String contactMechId = request.getParameter("CONTACT_MECH_ID");
      GenericValue partyContactMech = null;
      try { partyContactMech = delegator.findByPrimaryKey("PartyContactMech", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", contactMechId)); }
      catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); partyContactMech = null; }
      if(partyContactMech == null) {
        request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not delete contact info (read failure or not found) . Please contact customer service.");
        return "error";
      }
      partyContactMech.set("thruDate", UtilDateTime.nowTimestamp());
      try { partyContactMech.store(); }
      catch(GenericEntityException e) {
        request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not delete contact info (write failure) . Please contact customer service.");
        return "error";
      }
    }
    else if("UPDATE".equals(updateMode)) {
      boolean isModified = false;
      Long newCmId = delegator.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not change contact info (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      
      String contactMechId = request.getParameter("CONTACT_MECH_ID");
      GenericValue contactMech = null;
      GenericValue partyContactMech = null;
      try {
        contactMech = delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", contactMechId));
        partyContactMech = delegator.findByPrimaryKey("PartyContactMech", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", contactMechId));
      }
      catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); contactMech = null; partyContactMech = null; }
      if(contactMech == null) {
        request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not find specified contact info (read error). Please contact customer service.");
        return "error";
      }
      if(partyContactMech == null) {
        request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Logged in user cannot update specified contact info because it does not belong to the user. Please contact customer service.");
        return "error";
      }
      
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
      }
      else if("TELECOM_NUMBER".equals(contactMech.getString("contactMechTypeId"))) {
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
      }
      else if("EMAIL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))) {
        String infoString = request.getParameter("CM_INFO_STRING");
        if(!UtilValidate.isEmail(infoString)) errMsg += "<li>" + UtilValidate.isEmailMsg;
        if(errMsg.length() > 0) {
          errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
          request.setAttribute("ERROR_MESSAGE", errMsg);
          return "error";
        }
        newContactMech.set("infoString", infoString);
      }
      else {
        String infoString = request.getParameter("CM_INFO_STRING");
        newContactMech.set("infoString", infoString);
      }
      
      String allowSolicitation = request.getParameter("CM_ALLOW_SOL");
      newPartyContactMech.set("allowSolicitation", allowSolicitation);
      
      if(!newContactMech.equals(contactMech)) isModified = true;
      if(!newPartyContactMech.equals(partyContactMech)) isModified = true;
      
      partyContactMech.preStoreOther(newContactMech);
      partyContactMech.preStoreOther(newPartyContactMech);
      
      if(isModified) {
        if(relatedEntityToSet != null) partyContactMech.preStoreOther(relatedEntityToSet);
        
        newContactMech.set("contactMechId", newCmId.toString());
        newPartyContactMech.set("contactMechId", newCmId.toString());
        newPartyContactMech.set("fromDate", now);
        newPartyContactMech.set("thruDate", null);
        
        try {
          Iterator partyContactMechPurposes = UtilMisc.toIterator(partyContactMech.getRelated("PartyContactMechPurpose"));
          while(partyContactMechPurposes != null && partyContactMechPurposes.hasNext()) {
            GenericValue tempVal = new GenericValue((GenericValue)partyContactMechPurposes.next());
            tempVal.set("contactMechId", newCmId.toString());
            partyContactMech.preStoreOther(tempVal);
          }
        }
        catch(GenericEntityException e) {
          Debug.logWarning(e.getMessage());
          request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not change contact info (write failure) . Please contact customer service.");
          return "error";
        }
        
        String newCmPurposeTypeId = request.getParameter("CM_NEW_PURPOSE_TYPE_ID");
        if(newCmPurposeTypeId != null && newCmPurposeTypeId.length() > 0) {
          partyContactMech.preStoreOther(delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", newCmId.toString(), "contactMechPurposeTypeId", newCmPurposeTypeId, "fromDate", now)));
        }
        
        partyContactMech.set("thruDate", now);
        try { partyContactMech.store(); }
        catch(GenericEntityException e) {
          Debug.logWarning(e.getMessage());
          request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not change contact info (write failure) . Please contact customer service.");
          return "error";
        }
        request.setAttribute("CONTACT_MECH_ID", newCmId.toString());
      }
      else {
        String cmId = contactMech.getString("contactMechId");
        String newCmPurposeTypeId = request.getParameter("CM_NEW_PURPOSE_TYPE_ID");
        if(newCmPurposeTypeId != null && newCmPurposeTypeId.length() > 0) {
          try {
            if(delegator.create("PartyContactMechPurpose", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", cmId, "contactMechPurposeTypeId", newCmPurposeTypeId, "fromDate", now)) == null) {
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
      
    }
    else {
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
    
    String contactMechId = request.getParameter("CONTACT_MECH_ID");
    String contactMechPurposeTypeId = request.getParameter("CONTACT_MECH_PURPOSE_TYPE_ID");
    if(contactMechPurposeTypeId == null || contactMechPurposeTypeId.length() <= 0) { errMsg = "<li>ERROR: Purpose type not specified, cannot add purpose to contact mechanism. Please try again."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    
    
    GenericValue newPartyContactMechPurpose = delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "fromDate", UtilDateTime.nowTimestamp()));
    GenericValue tempVal = null;
    try { tempVal = delegator.findByPrimaryKey(newPartyContactMechPurpose.getPrimaryKey()); }
    catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); tempVal = null; }
    if(tempVal != null) {
      //if exists already, and has a thruDate, reset it to "undelete"
      if(tempVal.get("thruDate") != null) {
        tempVal.set("fromDate", UtilDateTime.nowTimestamp());
        tempVal.set("thruDate", null);
        try { tempVal.store(); }
        catch(GenericEntityException e) {
          Debug.logWarning(e.getMessage());
          request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not undelete purpose of contact mechanism (write failure) . Please contact customer service.");
          return "error";
        }
      }
    }
    else {
      try {
        if(delegator.create(newPartyContactMechPurpose) == null) {
          errMsg = "<li>ERROR: Could not add purpose to contact mechanism (write failure). Please contact customer service.";
          request.setAttribute("ERROR_MESSAGE", errMsg);
          return "error";
        }
      }
      catch(GenericEntityException e) {
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
    
    String contactMechId = request.getParameter("CONTACT_MECH_ID");
    String contactMechPurposeTypeId = request.getParameter("CONTACT_MECH_PURPOSE_TYPE_ID");
    if(contactMechPurposeTypeId == null || contactMechPurposeTypeId.length() <= 0) { errMsg = "<li>ERROR: Purpose type not specified, cannot delete purpose from contact mechanism. Please try again."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    
    
    GenericValue pcmp = null;
    try {
      pcmp = delegator.findByPrimaryKey("PartyContactMechPurpose", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId));
      if(pcmp == null) {
        errMsg = "<li>ERROR: Could not delete purpose from contact mechanism (record not found). Please contact customer service.";
        request.setAttribute("ERROR_MESSAGE", errMsg);
        return "error";
      }
    }
    catch(GenericEntityException e) {
      Debug.logWarning(e.getMessage());
      request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not delete purpose from contact mechanism (read failure). Please contact customer service.");
      return "error";
    }
    
    pcmp.set("thruDate", UtilDateTime.nowTimestamp());
    try { pcmp.store(); }
    catch(GenericEntityException e) {
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
    
    String updateMode = request.getParameter("UPDATE_MODE");
    
    Timestamp now = UtilDateTime.nowTimestamp();
    if("CREATE".equals(updateMode) || "UPDATE".equals(updateMode)) {
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
      
      Long newCcId = delegator.getNextSeqId("CreditCardInfo"); if(newCcId == null) { errMsg = "<li>ERROR: Could not create new contact info (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      newCc.set("partyId", userLogin.get("partyId"));
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
      }
      else {
        //is CREATE, set values
        newCc.set("creditCardId", newCcId.toString());
        newCc.set("fromDate", now);
        isModified = true;
      }
      
      GenericValue newPartyContactMechPurpose = null;
      if(contactMechId != null && contactMechId.length() > 0) {
        if("CREATE".equals(updateMode) || (creditCardInfo != null && !contactMechId.equals(creditCardInfo.getString("contactMechId")))) {
          //add a PartyContactMechPurpose of BILLING_LOCATION if necessary
          String contactMechPurposeTypeId = "BILLING_LOCATION";
          newPartyContactMechPurpose = delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "fromDate", now));
          
          GenericValue tempVal = null;
          try { tempVal = delegator.findByPrimaryKey(newPartyContactMechPurpose.getPrimaryKey()); }
          catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); tempVal = null; }
          
          if(tempVal != null) {
            //if exists already, and has a thruDate, reset or "undelete" it
            if(tempVal.get("thruDate") != null) {
              tempVal.set("fromDate", now);
              tempVal.set("thruDate", null);
              newPartyContactMechPurpose = tempVal;
            }
            else {
              newPartyContactMechPurpose = null;
            }
          }
        }
      }
      
      if(isModified) {
        Debug.logInfo("yes, is modified");
        if(newPartyContactMechPurpose != null) newCc.preStoreOther(newPartyContactMechPurpose);
        if("UPDATE".equals(updateMode)) {
          //if it is an update, set thru date on old card
          creditCardInfo.set("thruDate", now);
          newCc.preStoreOther(creditCardInfo);
        }
        
        try {
          if(delegator.create(newCc) == null) {
            request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not add credit card (write failure). Please contact customer service.");
            return "error";
          }
        }
        catch(GenericEntityException e) {
          Debug.logWarning(e.getMessage());
          request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not add credit card (write failure). Please contact customer service.");
          return "error";
        }
      }
      else {
        request.setAttribute("CREDIT_CARD_ID", newCc.getString("creditCardId"));
      }
      
    }
    else if("DELETE".equals(updateMode)) {
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
      try { creditCardInfo.store(); }
      catch(GenericEntityException e) {
        Debug.logWarning(e.getMessage());
        request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not delete credit card info (write failure). Please contact customer service.");
        return "error";
      }
    }
    else {
      errMsg = "<li>ERROR: Specified Update Mode (" + updateMode + ") is not valid. Please contact customer service.";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }
    
    request.setAttribute("EVENT_MESSAGE", "Credit Card Information Updated.");
    return "success";
  }
  
  /** Updates a Person entity according to the parameters passed in the
   *  request object; will do a CREATE, UPDATE, or DELETE depending on the
   *  value of the UPDATE_MODE parameter. When doing an UPDATE the actual
   *  row in the database is updated rather than creating a new one. For this
   *  reason parameters can be left out of the request without resulting in
   *  the corresponding fields values being set to null in the datasource.
   *@param request The HTTPRequest object for the current request
   *@param response The HTTPResponse object for the current request
   *@return String specifying the exit status of this event
   */
  public static String updatePerson(HttpServletRequest request, HttpServletResponse response) {
    String errMsg = "";
    GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
    GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
    if(userLogin == null) { errMsg = "<li>ERROR: User not logged in, cannot update credit card info. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    
    String updateMode = request.getParameter("UPDATE_MODE");
    
    if("CREATE".equals(updateMode) || "UPDATE".equals(updateMode)) {
      String firstName = request.getParameter("PERSON_FIRST_NAME");
      String middleName = request.getParameter("PERSON_MIDDLE_NAME");
      String lastName = request.getParameter("PERSON_LAST_NAME");
      String personalTitle = request.getParameter("PERSON_TITLE");
      String suffix = request.getParameter("PERSON_SUFFIX");
      
      String nickname = request.getParameter("PERSON_NICKNAME");
      String gender = request.getParameter("PERSON_GENDER");
      String birthDateStr = request.getParameter("PERSON_BIRTH_DATE");
      String heightStr = request.getParameter("PERSON_HEIGHT");
      String weightStr = request.getParameter("PERSON_WEIGHT");
      String mothersMaidenName = request.getParameter("PERSON_MOTHERS_MAIDEN_NAME");
      String maritalStatus = request.getParameter("PERSON_MARITAL_STATUS");
      String socialSecurityNumber = request.getParameter("PERSON_SOCIAL_SECURITY_NUMBER");
      String passportNumber = request.getParameter("PERSON_PASSPORT_NUMBER");
      String passportExpireDateStr = request.getParameter("PERSON_PASSPORT_EXPIRE_DATE");
      String totalYearsWorkExperienceStr = request.getParameter("PERSON_TOTAL_YEARS_WORK_EXPERIENCE");
      String comment = request.getParameter("PERSON_COMMENT");
      
      java.sql.Date birthDate = null;
      java.sql.Date passportExpireDate = null;
      Double height = null;
      Double weight = null;
      Double totalYearsWorkExperience = null;
      
      if(UtilValidate.isNotEmpty(birthDateStr)) {
        try { birthDate = UtilDateTime.toSqlDate(birthDateStr); }
        catch(Exception e) { errMsg += "<li>Birth Date is not a valid Date."; }
      }
      if(UtilValidate.isNotEmpty(passportExpireDateStr)) {
        try { passportExpireDate = UtilDateTime.toSqlDate(passportExpireDateStr); }
        catch(Exception e) { errMsg += "<li>Passport Expire Date is not a valid Date."; }
      }
      
      if(UtilValidate.isNotEmpty(heightStr)) {
        try { height = Double.valueOf(heightStr); }
        catch(Exception e) { errMsg += "<li>Height is not a valid number."; }
      }
      if(UtilValidate.isNotEmpty(weightStr)) {
        try { weight = Double.valueOf(weightStr); }
        catch(Exception e) { errMsg += "<li>Weight is not a valid number."; }
      }
      if(UtilValidate.isNotEmpty(totalYearsWorkExperienceStr)) {
        try { totalYearsWorkExperience = Double.valueOf(totalYearsWorkExperienceStr); }
        catch(Exception e) { errMsg += "<li>Total Years Work Experience is not a valid number."; }
      }
      
      if(!UtilValidate.isNotEmpty(firstName)) errMsg += "<li>First Name missing.";
      if(!UtilValidate.isNotEmpty(lastName)) errMsg += "<li>Last Name missing.";
      if(errMsg.length() > 0) {
        errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
        request.setAttribute("ERROR_MESSAGE", errMsg);
        return "error";
      }
      
      boolean doCreate = false;
      GenericValue person = null;
      try { person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", userLogin.get("partyId"))); }
      catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); person = null; }
      
      if(person == null) {
        person = delegator.makeValue("Person", UtilMisc.toMap("partyId", userLogin.get("partyId")));
        doCreate = true;
      }
      
      person.set("firstName", firstName, false);
      person.set("middleName", middleName, false);
      person.set("lastName", lastName, false);
      person.set("personalTitle", personalTitle, false);
      person.set("suffix", suffix, false);
      
      person.set("nickname", nickname, false);
      person.set("gender", gender, false);
      person.set("birthDate", birthDate, false);
      person.set("height", height, false);
      person.set("weight", weight, false);
      person.set("mothersMaidenName", mothersMaidenName, false);
      person.set("maritalStatus", maritalStatus, false);
      person.set("socialSecurityNumber", socialSecurityNumber, false);
      person.set("passportNumber", passportNumber, false);
      person.set("passportExpireDate", passportExpireDate, false);
      person.set("totalYearsWorkExperience", totalYearsWorkExperience, false);
      person.set("comment", comment, false);
      
      if(doCreate) {
        try {
          if(delegator.create(person) == null) {
            request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not add person info (write failure). Please contact customer service.");
            return "error";
          }
        }
        catch(GenericEntityException e) {
          Debug.logWarning(e.getMessage());
          request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not add person info (write failure). Please contact customer service.");
          return "error";
        }
      }
      else {
        try { person.store(); }
        catch(GenericEntityException e) {
          Debug.logWarning(e.getMessage());
          request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could update personal information (write failure). Please contact customer service.");
          return "error";
        }
      }
    }
    else if("DELETE".equals(updateMode)) {
      /* Leave delete disabled for now...
      GenericValue person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", userLogin.get("partyId")));
      if(person != null)
      {
        try { person.remove(); }
        catch(Exception e) { errMsg = "<li>ERROR: Could not delete personal information (write failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      }
       */
      request.setAttribute("ERROR_MESSAGE", "ERROR: Deletion of person object not allowed.");
      return "error";
    }
    else {
      errMsg = "<li>ERROR: Specified Update Mode (" + updateMode + ") is not valid. Please contact customer service.";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }
    
    request.setAttribute("EVENT_MESSAGE", "Personal Information Updated.");
    return "success";
  }
  
  /** Change the password for the current UserLogin in the session to the
   *  password specified in the request object.
   *@param request The HTTPRequest object for the current request
   *@param response The HTTPResponse object for the current request
   *@return String specifying the exit status of this event
   */
  public static String changePassword(HttpServletRequest request, HttpServletResponse response) {
    GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
    GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
    
    String password = request.getParameter("OLD_PASSWORD");
    String newPassword = request.getParameter("NEW_PASSWORD");
    String confirmPassword = request.getParameter("NEW_PASSWORD_CONFIRM");
    String passwordHint = request.getParameter("PASSWORD_HINT");
    
    if(!UtilValidate.isNotEmpty(password)) {
      //the password was incomplete
      request.setAttribute("ERROR_MESSAGE", "<li>The password was empty, please re-enter.");
      return "error";
    }
    
    if(!password.equals(userLogin.getString("currentPassword"))) {
      //password was NOT correct, send back to changepassword page with an error
      request.setAttribute("ERROR_MESSAGE", "<li>Old Password was not correct, please re-enter.");
      return "error";
    }
    
    String errMsg = setPassword(userLogin, newPassword, confirmPassword, passwordHint);
    if (UtilValidate.isNotEmpty(errMsg)) {
        request.setAttribute("ERROR_MESSAGE", errMsg);
        return "error";
    }
    
    try { userLogin.store(); }
    catch(Exception e) { request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not change password (write failure). Please contact customer service."); return "error"; }
    
    request.setAttribute("EVENT_MESSAGE", "Password Changed.");
    return "success";
  }
  
  /** The user forgot his/her password.  This will either call showPasswordHint or emailPassword.
   *@param request The HTTPRequest object for the current request
   *@param response The HTTPResponse object for the current request
   *@return String specifying the exit status of this event
   */
  public static String forgotPassword(HttpServletRequest request, HttpServletResponse response) {
    if (UtilValidate.isNotEmpty(request.getParameter("GET_PASSWORD_HINT"))) {
        return showPasswordHint(request, response);
    } else {
        return emailPassword(request, response);
    }
  }
  
  /** Show the password hint for the userLoginId specified in the request object.
   *@param request The HTTPRequest object for the current request
   *@param response The HTTPResponse object for the current request
   *@return String specifying the exit status of this event
   */
  public static String showPasswordHint(HttpServletRequest request, HttpServletResponse response) {
    GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
    
    String userLoginId = request.getParameter("USERNAME");
    
    if(!UtilValidate.isNotEmpty(userLoginId)) {
      //the password was incomplete
      request.setAttribute("ERROR_MESSAGE", "<li>The Username was empty, please re-enter.");
      return "error";
    }
    
    GenericValue supposedUserLogin = null;
    try {
        supposedUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
    } catch (GenericEntityException gee) { Debug.logWarning(gee); }
    if (supposedUserLogin == null) {
        //the Username was not found
        request.setAttribute("ERROR_MESSAGE", "<li>The Username was not found, please re-enter.");
        return "error";
    }

    String passwordHint = supposedUserLogin .getString("passwordHint");
    if (!UtilValidate.isNotEmpty(passwordHint)) {
        //the Username was not found
        request.setAttribute("ERROR_MESSAGE", "<li>No password hint was specified, try having the password emailed instead.");
        return "error";
    }

    request.setAttribute("EVENT_MESSAGE", "The Password Hint is: " + passwordHint);
    return "success";
  }
  
  /** Email the password for the userLoginId specified in the request object.
   *@param request The HTTPRequest object for the current request
   *@param response The HTTPResponse object for the current request
   *@return String specifying the exit status of this event
   */
  public static String emailPassword(HttpServletRequest request, HttpServletResponse response) {
    GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
    
    String userLoginId = request.getParameter("USERNAME");
    
    if(!UtilValidate.isNotEmpty(userLoginId)) {
      //the password was incomplete
      request.setAttribute("ERROR_MESSAGE", "<li>The Username was empty, please re-enter.");
      return "error";
    }
    
    GenericValue supposedUserLogin = null;
    try {
        supposedUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
    } catch (GenericEntityException gee) { Debug.logWarning(gee); }
    if (supposedUserLogin == null) {
        //the Username was not found
        request.setAttribute("ERROR_MESSAGE", "<li>The Username was not found, please re-enter.");
        return "error";
    }

    StringBuffer emails = new StringBuffer();
    GenericValue party = null;
    try { party = supposedUserLogin.getRelatedOne("Party"); }
    catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); party = null; }
    if(party != null) {
        Iterator emailIter = UtilMisc.toIterator(ContactHelper.getContactMech(party, "PRIMARY_EMAIL", "EMAIL_ADDRESS", false));
        while(emailIter != null && emailIter.hasNext()) {
            GenericValue email = (GenericValue) emailIter.next();
            emails.append(emails.length() > 0 ? "," : "").append(email.getString("infoString"));
        }
    }

    if (!UtilValidate.isNotEmpty(emails.toString())) {
        //the Username was not found
        request.setAttribute("ERROR_MESSAGE", "<li>No Primary Email Address has been set, please contact ustomer service.");
        return "error";
    }
    
    final String SMTP_SERVER = UtilProperties.getPropertyValue("ecommerce", "smtp.relay.host");
    final String LOCAL_MACHINE = UtilProperties.getPropertyValue("ecommerce", "smtp.local.machine");
    final String PASSWORD_SENDER_EMAIL = UtilProperties.getPropertyValue("ecommerce", "password.send.email");

    String content = "Username: " + userLoginId + "\nPassword: " + UtilFormatOut.checkNull(supposedUserLogin.getString("currentPassword"));
    try {
        SendMailSMTP mail = new SendMailSMTP(SMTP_SERVER, PASSWORD_SENDER_EMAIL, emails.toString(), content);
        mail.setLocalMachine(LOCAL_MACHINE);
        mail.setSubject(SiteDefs.SITE_NAME + " Password Reminder");
        //mail.setExtraHeader("MIME-Version: 1.0\nContent-type: text/html; charset=us-ascii\n");
        mail.setMessage(content);
        mail.send();
    } catch (java.io.IOException e) {
        Debug.logWarning(e);
        request.setAttribute(SiteDefs.ERROR_MESSAGE, "error occurred: unable to email password.  Please try again later or contact customer service.");
        return "error";
    }
    
    request.setAttribute("EVENT_MESSAGE", "Your password has been sent to you.  Please check your Email.");
    return "success";
  }

  /**
   * Will not persist the password - just set the attribute
   *
   * @return empty String if success or the error message
   */
  private static String setPassword(GenericValue userLogin, String password, String confirmPassword, String passwordHint) {
    String errMsg = "";
    if (UtilValidate.isEmpty(passwordHint)) passwordHint = null;

    if(!UtilValidate.isNotEmpty(password) || !UtilValidate.isNotEmpty(confirmPassword)) { 
        errMsg += "<li>Password(s) missing."; 
    } else if(!password.equals(confirmPassword)) { 
        errMsg += "<li>Password confirmation did not match."; 
    } else {
        int minPasswordLength;
        try { minPasswordLength = Integer.parseInt(UtilProperties.getPropertyValue("security", "password.length.min", "0"));
        } catch (NumberFormatException nfe) { minPasswordLength = 0; };
        if(!(password.length() >= minPasswordLength)) { 
            errMsg += "<li>Password must be at least " + minPasswordLength + " characters long.";
        } else if (password.equalsIgnoreCase(userLogin.getString("userLoginId"))) {
            errMsg += "<li>Password may not equal the Username.";
        } else if (UtilValidate.isNotEmpty(passwordHint) 
                && (passwordHint.toUpperCase().indexOf(password.toUpperCase()) >= 0)) {
            errMsg += "<li>Password hint may not contain the password.";
        }
    }
    
    if (errMsg.length() == 0) {
        //all is well, update password
        userLogin.set("currentPassword", password);
        userLogin.set("passwordHint", passwordHint);
    }
    
    return errMsg;
  }
}
