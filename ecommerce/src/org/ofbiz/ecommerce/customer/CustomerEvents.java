/*
 * $Id$
 * $Log$
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
    GenericHelper helper = (GenericHelper)request.getAttribute("helper");
    
    String username = request.getParameter("USERNAME");
    String password = request.getParameter("PASSWORD");
    String confirm_password = request.getParameter("CONFIRM_PASSWORD");
    
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
    if(!UtilValidate.isNotEmpty(state)) errMsg += "<li>State missing.";
    if(!UtilValidate.isNotEmpty(postalCode)) errMsg += "<li>Zip/Postal Code missing.";
    if(!UtilValidate.isNotEmpty(email)) errMsg += "<li>Email missing.";
    if(!UtilValidate.isEmail(email)) errMsg += "<li>" + UtilValidate.isEmailMsg;
    
    if(!UtilValidate.isNotEmpty(username)) errMsg += "<li>Username missing.";
    if((!UtilValidate.isNotEmpty(password) || !UtilValidate.isNotEmpty(confirm_password))
    && UtilProperties.propertyValueEqualsIgnoreCase("ecommerce", "create.allow.password", "true"))
    { errMsg += "<li>Password(s) missing."; }
    else if(UtilProperties.propertyValueEqualsIgnoreCase("ecommerce", "create.allow.password", "true")
    && password.compareTo(confirm_password) != 0)
    { errMsg += "<li>Passwords did not match."; }
    
    if(username != null && username.length() > 0) {
      GenericValue userLogin = helper.findByPrimaryKey(helper.makePK("UserLogin", UtilMisc.toMap("userLoginId", username)));
      if(userLogin != null) {
        //UserLogin record found, user does exist: go back to new user page...
        errMsg += "<li>Username in use, please choose another.";
      }
    }
    
    if(errMsg.length() > 0) {
      errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }
    
    //UserLogin with username does not exist: create new user...
    if(!UtilProperties.propertyValueEqualsIgnoreCase("ecommerce", "create.allow.password", "true")) password = UtilProperties.getPropertyValue("ecommerce", "default.customer.password", "ungssblepswd");
    
    GenericValue tempUserLogin = helper.makeValue("UserLogin", UtilMisc.toMap("userLoginId", username, "currentPassword", password, "partyId", username));
    // create Party, PartyClass, Person, ContactMechs for Address, phones
    tempUserLogin.preStoreOther(helper.makeValue("Party", UtilMisc.toMap("partyId", username)));
    tempUserLogin.preStoreOther(helper.makeValue("PartyClassification", UtilMisc.toMap("partyId", username, "partyTypeId", "PERSON", "partyClassificationTypeId", "PERSON_CLASSIFICATION", "fromDate", UtilDateTime.nowTimestamp())));
    tempUserLogin.preStoreOther(helper.makeValue("PartyRole", UtilMisc.toMap("partyId", username, "roleTypeId", "CUSTOMER")));
    tempUserLogin.preStoreOther(helper.makeValue("Person", UtilMisc.toMap("partyId", username, "firstName", firstName, "middleName", middleName, "lastName", lastName, "personalTitle", personalTitle, "suffix", suffix)));
    
    Long newCmId = null;
    //make address
    newCmId = helper.getNextSeqId("ContactMech");
    if(newCmId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    tempUserLogin.preStoreOther(helper.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", "POSTAL_ADDRESS")));
    GenericValue newAddr = helper.makeValue("PostalAddress", null);
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
    tempUserLogin.preStoreOther(helper.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "fromDate", UtilDateTime.nowTimestamp(), "roleTypeId", "CUSTOMER", "allowSolicitation", addressAllowSolicitation)));
    tempUserLogin.preStoreOther(helper.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "contactMechPurposeTypeId", "SHIPPING_LOCATION", "fromDate", UtilDateTime.nowTimestamp())));
    
    //make home phone number
    if(UtilValidate.isNotEmpty(homeContactNumber)) {
      newCmId = helper.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      tempUserLogin.preStoreOther(helper.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", "TELECOM_NUMBER")));
      tempUserLogin.preStoreOther(helper.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCmId.toString(), "countryCode", homeCountryCode, "areaCode", homeAreaCode, "contactNumber", homeContactNumber)));
      tempUserLogin.preStoreOther(helper.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "fromDate", UtilDateTime.nowTimestamp(), "roleTypeId", "CUSTOMER", "allowSolicitation", homeAllowSolicitation, "extension", homeExt)));
      tempUserLogin.preStoreOther(helper.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "contactMechPurposeTypeId", "PHONE_HOME", "fromDate", UtilDateTime.nowTimestamp())));
    }
    
    //make work phone number
    if(UtilValidate.isNotEmpty(workContactNumber)) {
      newCmId = helper.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      tempUserLogin.preStoreOther(helper.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", "TELECOM_NUMBER")));
      tempUserLogin.preStoreOther(helper.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCmId.toString(), "countryCode", workCountryCode, "areaCode", workAreaCode, "contactNumber", workContactNumber)));
      tempUserLogin.preStoreOther(helper.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "fromDate", UtilDateTime.nowTimestamp(), "roleTypeId", "CUSTOMER", "allowSolicitation", workAllowSolicitation, "extension", workExt)));
      tempUserLogin.preStoreOther(helper.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "contactMechPurposeTypeId", "PHONE_WORK", "fromDate", UtilDateTime.nowTimestamp())));
    }
    
    //make fax number
    if(UtilValidate.isNotEmpty(faxContactNumber)) {
      newCmId = helper.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      tempUserLogin.preStoreOther(helper.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", "TELECOM_NUMBER")));
      tempUserLogin.preStoreOther(helper.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCmId.toString(), "countryCode", faxCountryCode, "areaCode", faxAreaCode, "contactNumber", faxContactNumber)));
      tempUserLogin.preStoreOther(helper.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "fromDate", UtilDateTime.nowTimestamp(), "roleTypeId", "CUSTOMER", "allowSolicitation", faxAllowSolicitation)));
      tempUserLogin.preStoreOther(helper.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "contactMechPurposeTypeId", "FAX_NUMBER", "fromDate", UtilDateTime.nowTimestamp())));
    }
    
    //make mobile phone number
    if(UtilValidate.isNotEmpty(mobileContactNumber)) {
      newCmId = helper.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      tempUserLogin.preStoreOther(helper.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", "TELECOM_NUMBER")));
      tempUserLogin.preStoreOther(helper.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCmId.toString(), "countryCode", mobileCountryCode, "areaCode", mobileAreaCode, "contactNumber", mobileContactNumber)));
      tempUserLogin.preStoreOther(helper.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "fromDate", UtilDateTime.nowTimestamp(), "roleTypeId", "CUSTOMER", "allowSolicitation", mobileAllowSolicitation)));
      tempUserLogin.preStoreOther(helper.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "contactMechPurposeTypeId", "PHONE_MOBILE", "fromDate", UtilDateTime.nowTimestamp())));
    }
    
    //make email
    newCmId = helper.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    tempUserLogin.preStoreOther(helper.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", "EMAIL_ADDRESS", "infoString", email)));
    tempUserLogin.preStoreOther(helper.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCmId.toString(), "fromDate", UtilDateTime.nowTimestamp(), "roleTypeId", "CUSTOMER", "allowSolicitation", emailAllowSolicitation)));
    
    newUserLogin = helper.create(tempUserLogin);
    if(newUserLogin == null) { errMsg = "<li>ERROR: Could not create new account (create failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    
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
    GenericHelper helper = (GenericHelper)request.getAttribute("helper");
    GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
    if(userLogin == null) { errMsg = "<li>ERROR: User not logged in, cannot update contact info. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    
    String updateMode = request.getParameter("UPDATE_MODE");
    
    if("CREATE".equals(updateMode)) {
      String contactMechTypeId = request.getParameter("CONTACT_MECH_TYPE_ID");
      if(contactMechTypeId == null) { errMsg = "<li>ERROR: Could not create new contact info, type not specified. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      
      Long newCmId = helper.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not create new contact info (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      GenericValue tempContactMech = helper.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCmId.toString(), "contactMechTypeId", contactMechTypeId));
      
      String allowSolicitation = request.getParameter("CM_ALLOW_SOL");
      String extension = request.getParameter("CM_EXTENSION");
      tempContactMech.preStoreOther(helper.makeValue("PartyContactMech", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", newCmId.toString(), "fromDate", UtilDateTime.nowTimestamp(), "roleTypeId", "CUSTOMER", "allowSolicitation", allowSolicitation, "extension", extension)));
      
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
        
        GenericValue newAddr = helper.makeValue("PostalAddress", null);
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
        tempContactMech.preStoreOther(helper.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCmId.toString(), "countryCode", countryCode, "areaCode", areaCode, "contactNumber", contactNumber)));
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
      }
      else {
        String infoString = request.getParameter("CM_INFO_STRING");
        tempContactMech.set("infoString", infoString);
      }
      
      if(helper.create(tempContactMech) == null) {
        errMsg = "<li>ERROR: Could not change contact info (write failure) . Please contact customer service.";
        request.setAttribute("ERROR_MESSAGE", errMsg);
        return "error";
      }
      request.setAttribute("CONTACT_MECH_ID", newCmId.toString());
    }
    else if("DELETE".equals(updateMode)) {
      //never delete a contact mechanism, just put a to date on the link to the party
      String contactMechId = request.getParameter("CONTACT_MECH_ID");
      GenericValue partyContactMech = helper.findByPrimaryKey("PartyContactMech", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", contactMechId));
      partyContactMech.set("thruDate", UtilDateTime.nowTimestamp());
      try { partyContactMech.store(); }
      catch(Exception e) { errMsg = "<li>ERROR: Could not delete contact info (write failure) . Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    }
    else if("UPDATE".equals(updateMode)) {
      Long newCmId = helper.getNextSeqId("ContactMech"); if(newCmId == null) { errMsg = "<li>ERROR: Could not change contact info (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      
      String contactMechId = request.getParameter("CONTACT_MECH_ID");
      GenericValue contactMech = helper.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", contactMechId));
      GenericValue partyContactMech = helper.findByPrimaryKey("PartyContactMech", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", contactMechId));
      if(partyContactMech == null) { errMsg = "<li>ERROR: Logged in user is cannot update specified contact info because it does not belong to the user. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      
      //never change a contact mech, just create a new one with the changes
      GenericValue newContactMech = new GenericValue(contactMech);
      newContactMech.set("contactMechId", newCmId.toString());
      GenericValue newPartyContactMech = new GenericValue(partyContactMech);
      newPartyContactMech.set("contactMechId", newCmId.toString());
      newPartyContactMech.set("fromDate", UtilDateTime.nowTimestamp());
      
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
        
        GenericValue newAddr = helper.makeValue("PostalAddress", null);
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
        partyContactMech.preStoreOther(newAddr);
      }
      else if("TELECOM_NUMBER".equals(contactMech.getString("contactMechTypeId"))) {
        String countryCode = request.getParameter("CM_COUNTRY_CODE");
        String areaCode = request.getParameter("CM_AREA_CODE");
        String contactNumber = request.getParameter("CM_CONTACT_NUMBER");
        String extension = request.getParameter("CM_EXTENSION");
        partyContactMech.preStoreOther(helper.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCmId.toString(), "countryCode", countryCode, "areaCode", areaCode, "contactNumber", contactNumber)));
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
      
      partyContactMech.preStoreOther(newContactMech);
      partyContactMech.preStoreOther(newPartyContactMech);
      
      Iterator partyContactMechPurposes = UtilMisc.toIterator(partyContactMech.getRelated("PartyContactMechPurpose"));
      while(partyContactMechPurposes != null && partyContactMechPurposes.hasNext()) {
        GenericValue tempVal = new GenericValue((GenericValue)partyContactMechPurposes.next());
        tempVal.set("contactMechId", newCmId.toString());
        partyContactMech.preStoreOther(tempVal);
      }
      
      partyContactMech.set("thruDate", UtilDateTime.nowTimestamp());
      try { partyContactMech.store(); }
      catch(Exception e) { errMsg = "<li>ERROR: Could not change contact info (write failure) . Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      
      request.setAttribute("CONTACT_MECH_ID", newCmId.toString());
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
    GenericHelper helper = (GenericHelper)request.getAttribute("helper");
    GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
    if(userLogin == null) { errMsg = "<li>ERROR: User not logged in, cannot add purpose to contact mechanism. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    
    String contactMechId = request.getParameter("CONTACT_MECH_ID");
    String contactMechPurposeTypeId = request.getParameter("CONTACT_MECH_PURPOSE_TYPE_ID");
    if(contactMechPurposeTypeId == null || contactMechPurposeTypeId.length() <= 0) { errMsg = "<li>ERROR: Purpose type not specified, cannot add purpose to contact mechanism. Please try again."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    
    
    GenericValue newVal = helper.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "fromDate", UtilDateTime.nowTimestamp()));
    GenericValue tempVal = helper.findByPrimaryKey(newVal.getPrimaryKey());
    if(tempVal != null) {
      //if exists already, and has a thruDate, reset it to "undelete"
      if(tempVal.get("thruDate") != null) {
        tempVal.set("fromDate", UtilDateTime.nowTimestamp());
        tempVal.set("thruDate", null);
        try { tempVal.store(); }
        catch(Exception e) { errMsg = "<li>ERROR: Could not undelete purpose of contact mechanism (write failure) . Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      }
    }
    else if(helper.create(newVal) == null) {
      errMsg = "<li>ERROR: Could not add purpose to contact mechanism (write failure). Please contact customer service.";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
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
    GenericHelper helper = (GenericHelper)request.getAttribute("helper");
    GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
    if(userLogin == null) { errMsg = "<li>ERROR: User not logged in, cannot delete contact mechanism purpose. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    
    String contactMechId = request.getParameter("CONTACT_MECH_ID");
    String contactMechPurposeTypeId = request.getParameter("CONTACT_MECH_PURPOSE_TYPE_ID");
    if(contactMechPurposeTypeId == null || contactMechPurposeTypeId.length() <= 0) { errMsg = "<li>ERROR: Purpose type not specified, cannot delete purpose from contact mechanism. Please try again."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    
    GenericValue pcmp = helper.findByPrimaryKey("PartyContactMechPurpose", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId));
    if(pcmp == null) {
      errMsg = "<li>ERROR: Could not delete purpose from contact mechanism (record not found). Please contact customer service.";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }
    
    pcmp.set("thruDate", UtilDateTime.nowTimestamp());
    try { pcmp.store(); }
    catch(Exception e) { errMsg = "<li>ERROR: Could not delete purpose from contact mechanism (write failure) . Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
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
    GenericHelper helper = (GenericHelper)request.getAttribute("helper");
    GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
    if(userLogin == null) { errMsg = "<li>ERROR: User not logged in, cannot update credit card info. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    
    String updateMode = request.getParameter("UPDATE_MODE");
    
    boolean doDelete = false;
    if("CREATE".equals(updateMode) || "UPDATE".equals(updateMode)) {
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
      
      Long newCcId = helper.getNextSeqId("CreditCardInfo"); if(newCcId == null) { errMsg = "<li>ERROR: Could not create new contact info (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      GenericValue newCc = helper.makeValue("CreditCardInfo", null);
      newCc.set("creditCardId", newCcId.toString());
      newCc.set("partyId", userLogin.get("partyId"));
      newCc.set("fromDate", UtilDateTime.nowTimestamp());
      newCc.set("nameOnCard", nameOnCard);
      newCc.set("companyNameOnCard", companyNameOnCard);
      newCc.set("cardType", cardType);
      newCc.set("cardNumber", cardNumber);
      newCc.set("cardSecurityCode", cardSecurityCode);
      newCc.set("expireDate", expireDate);
      newCc.set("contactMechId", contactMechId);
      if(helper.create(newCc) == null) {
        errMsg = "<li>ERROR: Could not add credit card (write failure). Please contact customer service.";
        request.setAttribute("ERROR_MESSAGE", errMsg);
        return "error";
      }
      request.setAttribute("CREDIT_CARD_ID", newCcId.toString());
      
      if("UPDATE".equals(updateMode)) {
        //if it is an update, set thru date on old card
        doDelete = true;
      }
    }
    else if("DELETE".equals(updateMode)) {
      doDelete = true;
    }
    else {
      errMsg = "<li>ERROR: Specified Update Mode (" + updateMode + ") is not valid. Please contact customer service.";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }
    
    if(doDelete) {
      //never delete a credit card, just put a to date on the link to the party
      String creditCardId = request.getParameter("CREDIT_CARD_ID");
      GenericValue creditCardInfo = helper.findByPrimaryKey("CreditCardInfo", UtilMisc.toMap("creditCardId", creditCardId));
      creditCardInfo.set("thruDate", UtilDateTime.nowTimestamp());
      try { creditCardInfo.store(); }
      catch(Exception e) { errMsg = "<li>ERROR: Could not delete credit card info (write failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
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
    GenericHelper helper = (GenericHelper)request.getAttribute("helper");
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
      
      if(UtilValidate.isNotEmpty(heightStr))
      {
        try { height = Double.valueOf(heightStr); }
        catch(Exception e) { errMsg += "<li>Height is not a valid number."; }
      }
      if(UtilValidate.isNotEmpty(weightStr))
      {
        try { weight = Double.valueOf(weightStr); }
        catch(Exception e) { errMsg += "<li>Weight is not a valid number."; }
      }
      if(UtilValidate.isNotEmpty(totalYearsWorkExperienceStr))
      {
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
      
      GenericValue person = helper.findByPrimaryKey("Person", UtilMisc.toMap("partyId", userLogin.get("partyId")));
      boolean doCreate = false;
      if(person == null)
      {
        person = helper.makeValue("Person", UtilMisc.toMap("partyId", userLogin.get("partyId")));
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

      if(doCreate)
      {
        if(helper.create(person) == null) {
          errMsg = "<li>ERROR: Could not add person info (write failure). Please contact customer service.";
          request.setAttribute("ERROR_MESSAGE", errMsg);
          return "error";
        }
      }
      else
      {
        try { person.store(); }
        catch(Exception e) { errMsg = "<li>ERROR: Could update personal information (write failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      }
    }
    else if("DELETE".equals(updateMode)) {
      /* Leave delete disabled for now...
      GenericValue person = helper.findByPrimaryKey("Person", UtilMisc.toMap("partyId", userLogin.get("partyId")));
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
    GenericHelper helper = (GenericHelper)request.getAttribute("helper");
    
    String password = request.getParameter("OLD_PASSWORD");
    String newPassword = request.getParameter("NEW_PASSWORD");
    String confirmPassword = request.getParameter("NEW_PASSWORD_CONFIRM");
    
    if(password == null || password.length() <= 0 ||
    newPassword == null || newPassword.length() <= 0 ||
    confirmPassword == null || confirmPassword.length() <= 0) {
      //one or more of the passwords was incomplete
      request.setAttribute("ERROR_MESSAGE", "<li>One or more of the passwords was empty, please re-enter.");
      return "error";
    }
    
    if(!password.equals(userLogin.getString("currentPassword"))) {
      //password was NOT correct, send back to changepassword page with an error
      request.setAttribute("ERROR_MESSAGE", "<li>Old Password was not correct, please re-enter.");
      return "error";
    }
    
    //password was correct, check new password
    if(!newPassword.equals(confirmPassword)) {
      //passwords did not match
      request.setAttribute("ERROR_MESSAGE", "<li>New Passwords did not match, please re-enter.");
      return "error";
    }
    
    //all is well, update password
    userLogin.set("currentPassword", newPassword);
    try { userLogin.store(); }
    catch(Exception e) { request.setAttribute("ERROR_MESSAGE", "<li>ERROR: Could not change password (write failure). Please contact customer service."); return "error"; }
    
    request.setAttribute("EVENT_MESSAGE", "Password Changed.");
    return "success";
  }
}
