/*
 * $Id$
 * $Log$
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
public class CustomerEvents
{
  public static String createCustomer(HttpServletRequest request, HttpServletResponse response)
  {
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

    if(username != null && username.length() > 0)
    {
      GenericValue userLogin = helper.findByPrimaryKey(helper.makePK("UserLogin", UtilMisc.toMap("userLoginId", username)));
      if(userLogin != null)
      {
        //UserLogin record found, user does exist: go back to new user page...
        errMsg += "<li>Username in use, please choose another.";
      }
    }

    if(errMsg.length() > 0)
    {
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
    
    Long newCMId = null;
    //make address
    newCMId = helper.getNextSeqId("ContactMech");
    if(newCMId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    tempUserLogin.preStoreOther(helper.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCMId.toString(), "contactMechTypeId", "POSTAL_ADDRESS")));
    Map addrFields = new HashMap();
    addrFields.put("contactMechId", newCMId.toString());
    addrFields.put("toName", firstName + " " + lastName);
    addrFields.put("address1", address1);
    addrFields.put("address2", address2);
    addrFields.put("directions", directions);
    addrFields.put("city", city);
    addrFields.put("postalCode", postalCode);
    addrFields.put("stateProvinceGeoId", state);
    addrFields.put("countryGeoId", country);
    //addrFields.put("postalCodeGeoId", postalCodeGeoId);
    tempUserLogin.preStoreOther(helper.makeValue("PostalAddress", addrFields));
    tempUserLogin.preStoreOther(helper.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCMId.toString(), "fromDate", UtilDateTime.nowTimestamp(), "roleTypeId", "CUSTOMER", "allowSolicitation", addressAllowSolicitation)));
    tempUserLogin.preStoreOther(helper.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", username, "contactMechId", newCMId.toString(), "contactMechPurposeTypeId", "SHIPPING_LOCATION", "fromDate", UtilDateTime.nowTimestamp())));

    //make home phone number
    if(UtilValidate.isNotEmpty(homeContactNumber))
    {
      newCMId = helper.getNextSeqId("ContactMech"); if(newCMId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      tempUserLogin.preStoreOther(helper.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCMId.toString(), "contactMechTypeId", "TELECOM_NUMBER")));
      tempUserLogin.preStoreOther(helper.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCMId.toString(), "countryCode", homeCountryCode, "areaCode", homeAreaCode, "contactNumber", homeContactNumber)));
      tempUserLogin.preStoreOther(helper.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCMId.toString(), "fromDate", UtilDateTime.nowTimestamp(), "roleTypeId", "CUSTOMER", "allowSolicitation", homeAllowSolicitation, "extension", homeExt)));
      tempUserLogin.preStoreOther(helper.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", username, "contactMechId", newCMId.toString(), "contactMechPurposeTypeId", "PHONE_HOME", "fromDate", UtilDateTime.nowTimestamp())));
    }
    
    //make work phone number
    if(UtilValidate.isNotEmpty(workContactNumber))
    {
      newCMId = helper.getNextSeqId("ContactMech"); if(newCMId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      tempUserLogin.preStoreOther(helper.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCMId.toString(), "contactMechTypeId", "TELECOM_NUMBER")));
      tempUserLogin.preStoreOther(helper.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCMId.toString(), "countryCode", workCountryCode, "areaCode", workAreaCode, "contactNumber", workContactNumber)));
      tempUserLogin.preStoreOther(helper.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCMId.toString(), "fromDate", UtilDateTime.nowTimestamp(), "roleTypeId", "CUSTOMER", "allowSolicitation", workAllowSolicitation, "extension", workExt)));
      tempUserLogin.preStoreOther(helper.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", username, "contactMechId", newCMId.toString(), "contactMechPurposeTypeId", "PHONE_WORK", "fromDate", UtilDateTime.nowTimestamp())));
    }
    
    //make fax number
    if(UtilValidate.isNotEmpty(faxContactNumber))
    {
      newCMId = helper.getNextSeqId("ContactMech"); if(newCMId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      tempUserLogin.preStoreOther(helper.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCMId.toString(), "contactMechTypeId", "TELECOM_NUMBER")));
      tempUserLogin.preStoreOther(helper.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCMId.toString(), "countryCode", faxCountryCode, "areaCode", faxAreaCode, "contactNumber", faxContactNumber)));
      tempUserLogin.preStoreOther(helper.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCMId.toString(), "fromDate", UtilDateTime.nowTimestamp(), "roleTypeId", "CUSTOMER", "allowSolicitation", faxAllowSolicitation)));
      tempUserLogin.preStoreOther(helper.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", username, "contactMechId", newCMId.toString(), "contactMechPurposeTypeId", "FAX_NUMBER", "fromDate", UtilDateTime.nowTimestamp())));
    }
 
    //make mobile phone number
    if(UtilValidate.isNotEmpty(mobileContactNumber))
    {
      newCMId = helper.getNextSeqId("ContactMech"); if(newCMId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      tempUserLogin.preStoreOther(helper.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCMId.toString(), "contactMechTypeId", "TELECOM_NUMBER")));
      tempUserLogin.preStoreOther(helper.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCMId.toString(), "countryCode", mobileCountryCode, "areaCode", mobileAreaCode, "contactNumber", mobileContactNumber)));
      tempUserLogin.preStoreOther(helper.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCMId.toString(), "fromDate", UtilDateTime.nowTimestamp(), "roleTypeId", "CUSTOMER", "allowSolicitation", mobileAllowSolicitation)));
      tempUserLogin.preStoreOther(helper.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", username, "contactMechId", newCMId.toString(), "contactMechPurposeTypeId", "PHONE_MOBILE", "fromDate", UtilDateTime.nowTimestamp())));
    }
      
    //make email
    newCMId = helper.getNextSeqId("ContactMech"); if(newCMId == null) { errMsg = "<li>ERROR: Could not create new account (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    tempUserLogin.preStoreOther(helper.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCMId.toString(), "contactMechTypeId", "EMAIL_ADDRESS", "infoString", email)));
    tempUserLogin.preStoreOther(helper.makeValue("PartyContactMech", UtilMisc.toMap("partyId", username, "contactMechId", newCMId.toString(), "fromDate", UtilDateTime.nowTimestamp(), "roleTypeId", "CUSTOMER", "allowSolicitation", emailAllowSolicitation)));
    
    newUserLogin = helper.create(tempUserLogin);
    if(newUserLogin == null) { errMsg = "<li>ERROR: Could not create new account (create failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }

    if(UtilProperties.propertyValueEqualsIgnoreCase("ecommerce", "create.allow.password", "true")) request.getSession().setAttribute(SiteDefs.USER_LOGIN, newUserLogin);
    return "success";
  }
  
  public static String updateContactMech(HttpServletRequest request, HttpServletResponse response)
  {
    String errMsg = "";
    GenericHelper helper = (GenericHelper)request.getAttribute("helper");
    GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
    if(userLogin == null) { errMsg = "<li>ERROR: User not logged in, cannot update contact info. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }

    String updateMode = request.getParameter("UPDATE_MODE");
    
    if("CREATE".equals(updateMode))
    {
      String contactMechTypeId = request.getParameter("CONTACT_MECH_TYPE_ID");
      if(contactMechTypeId == null) { errMsg = "<li>ERROR: Could not create new contact info, type not specified. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      
      Long newCMId = helper.getNextSeqId("ContactMech"); if(newCMId == null) { errMsg = "<li>ERROR: Could not create new contact info (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      GenericValue tempContactMech = helper.makeValue("ContactMech", UtilMisc.toMap("contactMechId", newCMId.toString(), "contactMechTypeId", contactMechTypeId));

      String allowSolicitation = request.getParameter("CM_ALLOW_SOL");
      String extension = request.getParameter("CM_EXTENSION");
      tempContactMech.preStoreOther(helper.makeValue("PartyContactMech", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", newCMId.toString(), "fromDate", UtilDateTime.nowTimestamp(), "roleTypeId", "CUSTOMER", "allowSolicitation", allowSolicitation, "extension", extension)));
      
      if("POSTAL_ADDRESS".equals(contactMechTypeId))
      {
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
        if(errMsg.length() > 0)
        {
          errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
          request.setAttribute("ERROR_MESSAGE", errMsg);
          return "error";
        }

        Map addrFields = new HashMap();
        addrFields.put("contactMechId", newCMId.toString());
        addrFields.put("toName", toName);
        addrFields.put("attnName", attnName);
        addrFields.put("address1", address1);
        addrFields.put("address2", address2);
        addrFields.put("directions", directions);
        addrFields.put("city", city);
        addrFields.put("postalCode", postalCode);
        addrFields.put("stateProvinceGeoId", state);
        addrFields.put("countryGeoId", country);
        //addrFields.put("postalCodeGeoId", postalCodeGeoId);
        tempContactMech.preStoreOther(helper.makeValue("PostalAddress", addrFields));
      }
      else if("TELECOM_NUMBER".equals(contactMechTypeId))
      {
        String countryCode = request.getParameter("CM_COUNTRY_CODE");
        String areaCode = request.getParameter("CM_AREA_CODE");
        String contactNumber = request.getParameter("CM_CONTACT_NUMBER");
        tempContactMech.preStoreOther(helper.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCMId.toString(), "countryCode", countryCode, "areaCode", areaCode, "contactNumber", contactNumber)));
      }
      else if("EMAIL_ADDRESS".equals(contactMechTypeId))
      {        
        String infoString = request.getParameter("CM_INFO_STRING");
        if(!UtilValidate.isEmail(infoString)) errMsg += "<li>" + UtilValidate.isEmailMsg;
        if(errMsg.length() > 0)
        {
          errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
          request.setAttribute("ERROR_MESSAGE", errMsg);
          return "error";
        }
        tempContactMech.set("infoString", infoString);
      }
      else
      {        
        String infoString = request.getParameter("CM_INFO_STRING");
        tempContactMech.set("infoString", infoString);
      }
            
      if(helper.create(tempContactMech) == null)
      { 
        errMsg = "<li>ERROR: Could not change contact info (write failure) . Please contact customer service.";
        request.setAttribute("ERROR_MESSAGE", errMsg);
        return "error"; 
      }
      request.setAttribute("CONTACT_MECH_ID", newCMId.toString());
    }
    else if("DELETE".equals(updateMode))
    {
      //never delete a contact mechanism, just put a to date on the link to the party
      String contactMechId = request.getParameter("CONTACT_MECH_ID");      
      GenericValue partyContactMech = helper.findByPrimaryKey("PartyContactMech", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", contactMechId));
      partyContactMech.set("thruDate", UtilDateTime.nowTimestamp());
      try { partyContactMech.store(); }
      catch(Exception e) { errMsg = "<li>ERROR: Could not delete contact info (write failure) . Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    }
    else if("UPDATE".equals(updateMode))
    {
      Long newCMId = helper.getNextSeqId("ContactMech"); if(newCMId == null) { errMsg = "<li>ERROR: Could not change contact info (id generation failure). Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }

      String contactMechId = request.getParameter("CONTACT_MECH_ID");      
      GenericValue contactMech = helper.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", contactMechId));
      GenericValue partyContactMech = helper.findByPrimaryKey("PartyContactMech", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", contactMechId));
      if(partyContactMech == null) { errMsg = "<li>ERROR: Logged in user is cannot update specified contact info because it does not belong to the user. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      
      //never change a contact mech, just create a new one with the changes
      GenericValue newContactMech = new GenericValue(contactMech);
      newContactMech.set("contactMechId", newCMId.toString());
      GenericValue newPartyContactMech = new GenericValue(partyContactMech);
      newPartyContactMech.set("contactMechId", newCMId.toString());
      newPartyContactMech.set("fromDate", UtilDateTime.nowTimestamp());

      if("POSTAL_ADDRESS".equals(contactMech.getString("contactMechTypeId")))
      {
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
        if(errMsg.length() > 0)
        {
          errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
          request.setAttribute("ERROR_MESSAGE", errMsg);
          return "error";
        }

        Map addrFields = new HashMap();
        addrFields.put("contactMechId", newCMId.toString());
        addrFields.put("toName", toName);
        addrFields.put("attnName", attnName);
        addrFields.put("address1", address1);
        addrFields.put("address2", address2);
        addrFields.put("directions", directions);
        addrFields.put("city", city);
        addrFields.put("postalCode", postalCode);
        addrFields.put("stateProvinceGeoId", state);
        addrFields.put("countryGeoId", country);
        //addrFields.put("postalCodeGeoId", postalCodeGeoId);
        partyContactMech.preStoreOther(helper.makeValue("PostalAddress", addrFields));
      }
      else if("TELECOM_NUMBER".equals(contactMech.getString("contactMechTypeId")))
      {
        String countryCode = request.getParameter("CM_COUNTRY_CODE");
        String areaCode = request.getParameter("CM_AREA_CODE");
        String contactNumber = request.getParameter("CM_CONTACT_NUMBER");
        String extension = request.getParameter("CM_EXTENSION");
        partyContactMech.preStoreOther(helper.makeValue("TelecomNumber", UtilMisc.toMap("contactMechId", newCMId.toString(), "countryCode", countryCode, "areaCode", areaCode, "contactNumber", contactNumber)));
        newPartyContactMech.set("extension", extension);
      }
      else if("EMAIL_ADDRESS".equals(contactMech.getString("contactMechTypeId")))
      {        
        String infoString = request.getParameter("CM_INFO_STRING");
        if(!UtilValidate.isEmail(infoString)) errMsg += "<li>" + UtilValidate.isEmailMsg;
        if(errMsg.length() > 0)
        {
          errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
          request.setAttribute("ERROR_MESSAGE", errMsg);
          return "error";
        }
        newContactMech.set("infoString", infoString);
      }
      else
      {        
        String infoString = request.getParameter("CM_INFO_STRING");
        newContactMech.set("infoString", infoString);
      }
      String allowSolicitation = request.getParameter("CM_ALLOW_SOL");
      newPartyContactMech.set("allowSolicitation", allowSolicitation);
      
      partyContactMech.preStoreOther(newContactMech);
      partyContactMech.preStoreOther(newPartyContactMech);
      
      Iterator partyContactMechPurposes = UtilMisc.toIterator(partyContactMech.getRelated("PartyContactMechPurpose"));
      while(partyContactMechPurposes != null && partyContactMechPurposes.hasNext())
      {
        GenericValue tempVal = new GenericValue((GenericValue)partyContactMechPurposes.next());
        tempVal.set("contactMechId", newCMId.toString());
        partyContactMech.preStoreOther(tempVal);
      }
      
      partyContactMech.set("thruDate", UtilDateTime.nowTimestamp());
      try { partyContactMech.store(); }
      catch(Exception e) { errMsg = "<li>ERROR: Could not change contact info (write failure) . Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    }
    else
    {
      errMsg = "<li>ERROR: Specified Update Mode (" + updateMode + ") is not valid. Please contact customer service."; 
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }
    
    return "success";
  }

  public static String createPartyContactMechPurpose(HttpServletRequest request, HttpServletResponse response)
  {
    String errMsg = "";
    GenericHelper helper = (GenericHelper)request.getAttribute("helper");
    GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
    if(userLogin == null) { errMsg = "<li>ERROR: User not logged in, cannot add purpose to contact mechanism. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }

    String contactMechId = request.getParameter("CONTACT_MECH_ID");    
    String contactMechPurposeTypeId = request.getParameter("CONTACT_MECH_PURPOSE_TYPE_ID");
    if(contactMechPurposeTypeId == null || contactMechPurposeTypeId.length() <= 0) { errMsg = "<li>ERROR: Purpose type not specified, cannot add purpose to contact mechanism. Please try again."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }

    
    GenericValue newVal = helper.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "fromDate", UtilDateTime.nowTimestamp()));
    GenericValue tempVal = helper.findByPrimaryKey(newVal.getPrimaryKey());
    if(tempVal != null)
    {
      //if exists already, and has a thruDate, reset it to "undelete"
      if(tempVal.get("thruDate") != null)
      {
        tempVal.set("fromDate", UtilDateTime.nowTimestamp());
        tempVal.set("thruDate", null);
        try { tempVal.store(); }
        catch(Exception e) { errMsg = "<li>ERROR: Could not undelete purpose of contact mechanism (write failure) . Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
      }
    }
    else if(helper.create(newVal) == null)
    {
      errMsg = "<li>ERROR: Could not add purpose to contact mechanism (write failure). Please contact customer service."; 
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }
    return "success";
  }

  public static String deletePartyContactMechPurpose(HttpServletRequest request, HttpServletResponse response)
  {
    String errMsg = "";
    GenericHelper helper = (GenericHelper)request.getAttribute("helper");
    GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
    if(userLogin == null) { errMsg = "<li>ERROR: User not logged in, cannot delete contact mechanism purpose. Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }

    String contactMechId = request.getParameter("CONTACT_MECH_ID");
    String contactMechPurposeTypeId = request.getParameter("CONTACT_MECH_PURPOSE_TYPE_ID");    
    if(contactMechPurposeTypeId == null || contactMechPurposeTypeId.length() <= 0) { errMsg = "<li>ERROR: Purpose type not specified, cannot delete purpose from contact mechanism. Please try again."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }

    GenericValue pcmp = helper.findByPrimaryKey("PartyContactMechPurpose", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId));
    if(pcmp == null)
    {
      errMsg = "<li>ERROR: Could not delete purpose from contact mechanism (record not found). Please contact customer service."; 
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }
    
    pcmp.set("thruDate", UtilDateTime.nowTimestamp());
    try { pcmp.store(); }
    catch(Exception e) { errMsg = "<li>ERROR: Could not delete purpose from contact mechanism (write failure) . Please contact customer service."; request.setAttribute("ERROR_MESSAGE", errMsg); return "error"; }
    return "success";
  }
  
/*  
  public static boolean changePassword(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException
  {
    String errorMsg = (String)request.getAttribute(HttpRequestConstants.ERROR_MESSAGE);
    if(errorMsg == null || errorMsg.length() <= 0)
    {
      Person person = null;
      person = (Person)request.getSession().getAttribute(HttpSessionConstants.LOGIN_PERSON);

      boolean hasPermission = Security.hasPermission(Security.USER_ADMIN, request.getSession());
      if(hasPermission)
      {
        Person tempPerson = (Person)request.getSession().getAttribute(HttpSessionConstants.ACTING_AS_PERSON);
        if(tempPerson != null)
        {
          person = tempPerson;
        }
      }

      String password = request.getParameter(HttpRequestConstants.LOGIN_PASSWORD);
      if(hasPermission)
      {
        password = person.getPassword();
      }

      String newPassword = request.getParameter(HttpRequestConstants.LOGIN_NEW_PASSWORD);
      String confirmPassword = request.getParameter(HttpRequestConstants.LOGIN_CONFIRM_PASSWORD);

      if(person == null)
      {
        //person not found, return error
        request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "Person not found: critical error.  Please login.");
        RequestDispatcher rd = request.getRequestDispatcher("/commerce/user/changepassword.jsp");
        rd.forward(request, response);
        return false;
      }

      if(password == null || password.length() <= 0 ||
        newPassword == null || newPassword.length() <= 0 ||
        confirmPassword == null || confirmPassword.length() <= 0)
      {
        //one or more of the passwords was incomplete
        request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "One or more of the passwords was empty, please re-enter.");
        RequestDispatcher rd = request.getRequestDispatcher("/commerce/user/changepassword.jsp");
        rd.forward(request, response);
        return false;
      }

      if(person.getPassword().compareTo(password) != 0)
      {
        //password was NOT correct, send back to changepassword page with an error
        request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "Old Password was not correct, please re-enter.");
        RequestDispatcher rd = request.getRequestDispatcher("/commerce/user/changepassword.jsp");
        rd.forward(request, response);
        return false;
      }

      //password was correct, check new password
      if(newPassword.compareTo(confirmPassword) != 0)
      {
        //passwords did not match
        request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "New Passwords did not match, please re-enter.");
        RequestDispatcher rd = request.getRequestDispatcher("/commerce/user/changepassword.jsp");
        rd.forward(request, response);
        return false;
      }

      //all is well, update password
      person.setPassword(newPassword);
    }
    return true;
  }

  public static boolean handleUpdateCustomer(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException
  {
    // a little check to avoid an endless loop in error cases...
    if(request.getAttribute("ERROR_MESSAGE) != null)
    {
      return true;
    }

    String errMsg = "";
    String customerId = request.getParameter("CUSTOMER_ID);
    Person person = null;
    Customer customer = null;
    person = (Person)request.getSession().getAttribute(HttpSessionConstants.LOGIN_PERSON);
    customer = (Customer)request.getSession().getAttribute(HttpSessionConstants.LOGIN_CUSTOMER);

    boolean hasPermission = Security.hasPermission(Security.USER_ADMIN, request.getSession());
    if(hasPermission)
    {
      Person tempPerson = (Person)request.getSession().getAttribute(HttpSessionConstants.ACTING_AS_PERSON);
      if(tempPerson != null)
      {
        person = tempPerson;
      }
      Customer tempCustomer = (Customer)request.getSession().getAttribute(HttpSessionConstants.ACTING_AS_CUSTOMER);
      if(tempCustomer != null)
      {
        customer = tempCustomer;
      }
    }

    if(customerId != null && customerId.length() > 0)
    {
      //if a customer Id is passed, edit that customer record

      //check to see if it belongs to person...
      CustomerPerson customerPerson = CustomerPersonHelper.findByPrimaryKey(customerId, person.getUsername());
      if(customerPerson != null)
      {
        Customer tempCustomer = CustomerHelper.findByPrimaryKey(customerId);
        if(tempCustomer != null)
        {
          customer = tempCustomer;
        }
      }
    }

    //Customer's primary address record
    Address address = AddressHelper.findByPrimaryKey(customer.getAddressId());

    String updateMode = request.getParameter("UPDATE_MODE);
    if(updateMode != null && updateMode.compareTo("UPDATE_DELETE) == 0)
    {
      //delete customer, with addresses, payments, etc.

    }

    //get all parameters:
    String companyName = request.getParameter(HttpRequestConstants.CUSTOMER_COMPANY_NAME);
    String address1 = request.getParameter(HttpRequestConstants.CUSTOMER_ADDRESS1);
    String address2 = request.getParameter(HttpRequestConstants.CUSTOMER_ADDRESS2);
    String city = request.getParameter(HttpRequestConstants.CUSTOMER_CITY);
    String county = "";
    //request.getParameter(HttpRequestConstants);
    String state = request.getParameter(HttpRequestConstants.CUSTOMER_STATE);
    String postalCode = request.getParameter(HttpRequestConstants.CUSTOMER_ZIPCODE);
    String directions = "";
    //request.getParameter(HttpRequestConstants);
    String geoCode = "";
    //request.getParameter(HttpRequestConstants);
    String mapUrl = "";
    //request.getParameter(HttpRequestConstants);

    String customer_email = request.getParameter(HttpRequestConstants.CUSTOMER_EMAIL);
    String orderEmail = request.getParameter(HttpRequestConstants.CUSTOMER_ORDER_EMAIL);
    String priceLevelString = request.getParameter(HttpRequestConstants.CUSTOMER_PRICE_LEVEL);
    String legacyId = request.getParameter(HttpRequestConstants.CUSTOMER_LEGACY_ID);
    String storeId = request.getParameter(HttpRequestConstants.CUSTOMER_STORE_ID);
    int priceLevel = 0;
    try
    {
      priceLevel = Integer.parseInt(priceLevelString);
    }
    catch(NumberFormatException nfe)
    {
      priceLevel = 0;
    }

    if(address1 == null || address1.length() <= 0)
    {
      errMsg += "<li>Address line 1 missing.";
    }
    if(city == null || city.length() <= 0)
    {
      errMsg += "<li>City missing.";
    }
    if(state == null || state.length() <= 0)
    {
      errMsg += "<li>State missing.";
    }
    if(postalCode == null || postalCode.length() <= 0)
    {
      errMsg += "<li>Zip/Postal Code missing.";
    }
    / *
     * if(email == null || email.length() <= 0)
     * {
     * errMsg += "<li>Email missing.";
     * }
     * else
     * {
     * validate email address format
     * int atindex = email.indexOf("@");
     * int dotindex = email.lastIndexOf(".");
     * int length = email.length();
     * if(atindex < 0)
     * {
     * errMsg += "<li>Email format invalid: \"@\" missing. Need full \"user@hostname.ext\".";
     * }
     * if(dotindex < 0 || atindex > dotindex)
     * {
     * errMsg += "<li>Email format invalid: \".\" missing in hostname. Need full \"user@hostname.ext\".";
     * }
     * if(length <= dotindex + 1)
     * {
     * errMsg += "<li>Email format invalid: no extension in hostname. Need full \"user@hostname.ext\".";
     * }
     * }
     * /
    if(errMsg.length() > 0)
    {
      errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, errMsg);
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/user/editcustomer.jsp");
      rd.forward(request, response);
      return false;
    }

    address.setaddress1(address1);
    address.setaddress2(address2);
    address.setCity(city);
    address.setCounty(county);
    address.setState(state);
    address.setPostalCode(postalCode);
    address.setDirections(directions);
    address.setGeoCode(geoCode);
    address.setMapUrl(mapUrl);

    customer.setCompanyName(companyName);
    customer.setEmail(customer_email);
    customer.setOrderEmail(orderEmail);
    if(hasPermission)
    {
      customer.setLegacyId(legacyId);
      customer.setStoreId(storeId);
      customer.setPriceLevel(new Integer(priceLevel));
    }

    return true;
  }

  / **
   *  Description of the Method
   *
   *@param  request                             Description of Parameter
   *@param  response                            Description of Parameter
   *@return                                     Description of the Returned
   *      Value
   *@exception  java.rmi.RemoteException        Description of Exception
   *@exception  java.io.IOException             Description of Exception
   *@exception  javax.servlet.ServletException  Description of Exception
   * /
  public static boolean handleUpdateProfile(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException
  {
    // a little check to avoid an endless loop in error cases...
    if(request.getAttribute(HttpRequestConstants.ERROR_MESSAGE) != null)
    {
      return true;
    }

    String errMsg = "";
    Person person = null;
    person = (Person)request.getSession().getAttribute(HttpSessionConstants.LOGIN_PERSON);

    boolean hasPermission = Security.hasPermission(Security.USER_ADMIN, request.getSession());
    if(hasPermission)
    {
      Person tempPerson = (Person)request.getSession().getAttribute(HttpSessionConstants.ACTING_AS_PERSON);
      if(tempPerson != null)
      {
        person = tempPerson;
      }
    }

    Address address = AddressHelper.findByPrimaryKey(person.getHomeAddressId());

    //get all parameters:
    String firstName = request.getParameter(HttpRequestConstants.USER_FIRST_NAME);
    String middleName = request.getParameter(HttpRequestConstants.USER_MIDDLE_NAME);
    String lastName = request.getParameter(HttpRequestConstants.USER_LAST_NAME);
    String salutation = "";
    String suffix = "";
    String homePhone = request.getParameter(HttpRequestConstants.CUSTOMER_HOME_PHONE);
    String workPhone = request.getParameter(HttpRequestConstants.CUSTOMER_WORK_PHONE);
    String faxPhone = request.getParameter(HttpRequestConstants.CUSTOMER_FAX_PHONE);
    String mobilePhone = request.getParameter(HttpRequestConstants.CUSTOMER_MOBILE_PHONE);
    String email = request.getParameter(HttpRequestConstants.USER_EMAIL);
    String companyPromoEmail = request.getParameter(HttpRequestConstants.COMPANY_PROMO_EMAIL);
    String partnerPromoEmail = request.getParameter(HttpRequestConstants.PARTNER_PROMO_EMAIL);

    String address1 = request.getParameter(HttpRequestConstants.USER_HOME_ADDRESS1);
    String address2 = request.getParameter(HttpRequestConstants.USER_HOME_ADDRESS2);
    String city = request.getParameter(HttpRequestConstants.USER_HOME_CITY);
    String county = "";
    //request.getParameter(HttpRequestConstants);
    String state = request.getParameter(HttpRequestConstants.USER_HOME_STATE);
    String postalCode = request.getParameter(HttpRequestConstants.USER_HOME_ZIPCODE);
    //Integer homeAddressId;

    String directions = "";
    //request.getParameter(HttpRequestConstants);
    String geoCode = "";
    //request.getParameter(HttpRequestConstants);
    String mapUrl = "";
    //request.getParameter(HttpRequestConstants);


    if(firstName == null || firstName.length() <= 0)
    {
      errMsg += "<li>First name missing.";
    }
    if(lastName == null || lastName.length() <= 0)
    {
      errMsg += "<li>Last name missing.";
    }
    if(email == null || email.length() <= 0)
    {
      errMsg += "<li>Email missing.";
    }
    else
    {
      //validate email address format
      int atindex = email.indexOf("@");
      int dotindex = email.lastIndexOf(".");
      int length = email.length();
      if(atindex < 0)
      {
        errMsg += "<li>Email format invalid: \"@\" missing. Need full \"user@hostname.ext\".";
      }
      if(dotindex < 0 || atindex > dotindex)
      {
        errMsg += "<li>Email format invalid: \".\" missing in hostname. Need full \"user@hostname.ext\".";
      }
      if(length <= dotindex + 1)
      {
        errMsg += "<li>Email format invalid: no extension in hostname. Need full \"user@hostname.ext\".";
      }
    }

    if(errMsg.length() > 0)
    {
      errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, errMsg);
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/user/editprofile.jsp");
      rd.forward(request, response);
      return false;
    }

    person.setFirstName(firstName);
    person.setMiddleName(middleName);
    person.setLastName(lastName);
    person.setSalutation(salutation);
    person.setSuffix(suffix);
    person.setHomePhone(homePhone);
    person.setWorkPhone(workPhone);
    person.setFaxPhone(faxPhone);
    person.setMobilePhone(mobilePhone);
    person.setEmail(email);
    person.setCompanyPromoEmail(companyPromoEmail);
    person.setPartnerPromoEmail(partnerPromoEmail);

    if(address != null)
    {
      address.setaddress1(address1);
      address.setaddress2(address2);
      address.setCity(city);
      address.setCounty(county);
      address.setState(state);
      address.setPostalCode(postalCode);
      address.setDirections(directions);
      address.setGeoCode(geoCode);
      address.setMapUrl(mapUrl);
    }
    else
    {
      address = AddressHelper.create(address1, address2, city, county, state, postalCode, directions, geoCode, mapUrl);
      if(address == null)
      {
        //uh oh, failed...
        request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "Could not create address.");
        RequestDispatcher rd = request.getRequestDispatcher("/commerce/user/editprofile.jsp");
        rd.forward(request, response);
        return false;
      }
      else
      {
        person.setHomeAddressId(address.getAddressId());
      }
    }

    return true;
  }

  public static boolean deletePostalAddress(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, javax.ejb.RemoveException, java.io.IOException, javax.servlet.ServletException
  {
    String addressIdString = request.getParameter(HttpRequestConstants.ADDRESS_KEY);
    int addressId = 0;

    Customer customer = (Customer)request.getSession().getAttribute(HttpSessionConstants.LOGIN_CUSTOMER);
    CustomerShipAddress customerShipAddress = null;

    if(addressIdString == null)
    {
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "The address id was not specified, cannot remove.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/generalerror.jsp");
      rd.forward(request, response);
      return false;
    }

    try
    {
      addressId = Integer.parseInt(addressIdString);
    }
    catch(java.lang.NumberFormatException nfe)
    {
      //do nothing if number is not properly formatted...
      addressId = 0;
    }

    if(addressId == 0)
    {
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "The address id specified was not valid, cannot remove.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/generalerror.jsp");
      rd.forward(request, response);
      return false;
    }

    customerShipAddress = CustomerShipAddressHelper.findByPrimaryKey(customer.getCustomerId(), new Integer(addressId));

    Address address = AddressHelper.findByPrimaryKey(new Integer(addressId));
    if(address == null)
    {
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "The address specified was not found, cannot remove.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/generalerror.jsp");
      rd.forward(request, response);
      return false;
    }

    if(!Security.hasPermission(Security.USER_ADMIN, request.getSession()) &&
      customer.getAddressId().intValue() != addressId &&
      customerShipAddress == null)
    {
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "The address specified does not belong to your account, you may not delete it.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/generalerror.jsp");
      rd.forward(request, response);
      return false;
    }

    address.remove();

    Iterator csaIterator = CustomerShipAddressHelper.findByAddressIdIterator(new Integer(addressId));
    while(csaIterator.hasNext())
    {
      customerShipAddress = (CustomerShipAddress)csaIterator.next();
      customerShipAddress.remove();
    }

    return true;
  }

  public static boolean handleSaveNewAddress(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException
  {
    // a little check to avoid an endless loop in error cases...
    if(request.getAttribute(HttpRequestConstants.ERROR_MESSAGE) != null)
    {
      return true;
    }

    String errMsg = "";
    Customer customer = null;
    customer = (Customer)request.getSession().getAttribute(HttpSessionConstants.LOGIN_CUSTOMER);

    boolean hasPermission = Security.hasPermission(Security.USER_ADMIN, request.getSession());
    if(hasPermission)
    {
      Customer tempCustomer = (Customer)request.getSession().getAttribute(HttpSessionConstants.ACTING_AS_CUSTOMER);
      if(tempCustomer != null)
      {
        customer = tempCustomer;
      }
    }

    String address1 = request.getParameter(HttpRequestConstants.CUSTOMER_SHIPPING_ADDRESS1);
    String address2 = request.getParameter(HttpRequestConstants.CUSTOMER_SHIPPING_ADDRESS2);
    String city = request.getParameter(HttpRequestConstants.CUSTOMER_SHIPPING_CITY);
    String county = "";
    //request.getParameter(HttpRequestConstants);
    String state = request.getParameter(HttpRequestConstants.CUSTOMER_SHIPPING_STATE);
    String postalCode = request.getParameter(HttpRequestConstants.CUSTOMER_SHIPPING_ZIPCODE);
    String directions = "";
    //request.getParameter(HttpRequestConstants);
    String geoCode = "";
    //request.getParameter(HttpRequestConstants);
    String mapUrl = "";
    //request.getParameter(HttpRequestConstants);

    if(address1 == null || address1.length() <= 0)
    {
      errMsg += "<li>Address line 1 missing.";
    }
    if(city == null || city.length() <= 0)
    {
      errMsg += "<li>City missing.";
    }
    if(state == null || state.length() <= 0)
    {
      errMsg += "<li>State missing.";
    }
    if(postalCode == null || postalCode.length() <= 0)
    {
      errMsg += "<li>Zip/Postal Code missing.";
    }

    if(errMsg.length() > 0)
    {
      errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, errMsg);
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/user/profilenewaddress.jsp");
      rd.forward(request, response);
      return false;
    }

    Address newShippingAddress = AddressHelper.create(address1, address2, city, county, state, postalCode, directions, geoCode, mapUrl);
    if(newShippingAddress == null)
    {
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "Could not create address record.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/user/profilenewaddress.jsp");
      rd.forward(request, response);
      return false;
    }
    if(CommonConstants.DEBUG_PRINT_INFO)
    {
      System.out.println("In handleSaveNewAddress, newShippingAddress.addressId=" + newShippingAddress.getAddressId().intValue());
    }

    CustomerShipAddress customerShipAddress = CustomerShipAddressHelper.create(customer.getCustomerId(), newShippingAddress.getAddressId());
    if(customerShipAddress == null)
    {
      try
      {
        newShippingAddress.remove();
      }
      catch(javax.ejb.RemoveException re)
      {
      }
      //this isn't good, but what to do? Probably not something the user can resolve...
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "Could not create customer shipping address record.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/user/profilenewaddress.jsp");
      rd.forward(request, response);
      return false;
    }
    return true;
  }

  public static boolean handleUpdateShippingAddress(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, javax.ejb.RemoveException, java.io.IOException, javax.servlet.ServletException
  {
    // a little check to avoid an endless loop in error cases...
    if(request.getAttribute(HttpRequestConstants.ERROR_MESSAGE) != null)
    {
      return true;
    }

    String errMsg = "";
    String addressIdString = request.getParameter(HttpRequestConstants.ADDRESS_KEY);
    int addressId = 0;

    Customer customer = (Customer)request.getSession().getAttribute(HttpSessionConstants.LOGIN_CUSTOMER);
    CustomerShipAddress customerShipAddress = null;

    if(addressIdString == null)
    {
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "The address id was not specified, cannot update.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/generalerror.jsp");
      rd.forward(request, response);
      return false;
    }

    try
    {
      addressId = Integer.parseInt(addressIdString);
    }
    catch(java.lang.NumberFormatException nfe)
    {
      //do nothing if number is not properly formatted...
      addressId = 0;
    }

    if(addressId == 0)
    {
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "The address id specified was not valid, cannot update.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/generalerror.jsp");
      rd.forward(request, response);
      return false;
    }

    customerShipAddress = CustomerShipAddressHelper.findByPrimaryKey(customer.getCustomerId(), new Integer(addressId));
    Address address = AddressHelper.findByPrimaryKey(new Integer(addressId));
    if(address == null)
    {
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "The address specified was not found, cannot change.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/generalerror.jsp");
      rd.forward(request, response);
      return false;
    }

    if(!Security.hasPermission(Security.USER_ADMIN, request.getSession()) &&
      customer.getAddressId().intValue() != addressId &&
      customerShipAddress == null)
    {
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "The address specified does not belong to your account, you may not change it.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/generalerror.jsp");
      rd.forward(request, response);
      return false;
    }

    String address1 = request.getParameter(HttpRequestConstants.CUSTOMER_SHIPPING_ADDRESS1);
    String address2 = request.getParameter(HttpRequestConstants.CUSTOMER_SHIPPING_ADDRESS2);
    String city = request.getParameter(HttpRequestConstants.CUSTOMER_SHIPPING_CITY);
    String county = "";
    //request.getParameter(HttpRequestConstants);
    String state = request.getParameter(HttpRequestConstants.CUSTOMER_SHIPPING_STATE);
    String postalCode = request.getParameter(HttpRequestConstants.CUSTOMER_SHIPPING_ZIPCODE);
    String directions = "";
    //request.getParameter(HttpRequestConstants);
    String geoCode = "";
    //request.getParameter(HttpRequestConstants);
    String mapUrl = "";
    //request.getParameter(HttpRequestConstants);

    if(address1 == null || address1.length() <= 0)
    {
      errMsg += "<li>Address line 1 missing.";
    }
    if(city == null || city.length() <= 0)
    {
      errMsg += "<li>City missing.";
    }
    if(state == null || state.length() <= 0)
    {
      errMsg += "<li>State missing.";
    }
    if(postalCode == null || postalCode.length() <= 0)
    {
      errMsg += "<li>Zip/Postal Code missing.";
    }

    if(errMsg.length() > 0)
    {
      errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, errMsg);
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/user/profileeditaddress.jsp");
      rd.forward(request, response);
      return false;
    }

    address.setaddress1(address1);
    address.setaddress2(address2);
    address.setCity(city);
    address.setCounty(county);
    address.setState(state);
    address.setPostalCode(postalCode);
    address.setDirections(directions);
    address.setGeoCode(geoCode);
    address.setMapUrl(mapUrl);

    return true;
  }

  / **
   *  Description of the Method
   *
   *@param  request                             Description of Parameter
   *@param  response                            Description of Parameter
   *@return                                     Description of the Returned
   *      Value
   *@exception  java.rmi.RemoteException        Description of Exception
   *@exception  java.io.IOException             Description of Exception
   *@exception  javax.servlet.ServletException  Description of Exception
   * /
  public static boolean handleCreateCustomerPayment(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException
  {
    // a little check to avoid an endless loop in error cases...
    if(request.getAttribute(HttpRequestConstants.ERROR_MESSAGE) != null)
    {
      return true;
    }

    String errMsg = "";
    Customer customer = null;
    customer = (Customer)request.getSession().getAttribute(HttpSessionConstants.LOGIN_CUSTOMER);

    boolean hasPermission = Security.hasPermission(Security.USER_ADMIN, request.getSession());
    if(hasPermission)
    {
      Customer tempCustomer = (Customer)request.getSession().getAttribute(HttpSessionConstants.ACTING_AS_CUSTOMER);
      if(tempCustomer != null)
      {
        customer = tempCustomer;
      }
    }

    String ccType = request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_TYPE);
    String ccHolder = request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_HOLDER);
    String ccCompany = "";
    String ccNumber = request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_NUMBER);
    String ccMonth = request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_MONTH);
    String ccYear = request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_YEAR);
    long longCCNumber = 0;

    String address1 = request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_ADDRESS1);
    String address2 = request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_ADDRESS2);
    String city = request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_CITY);
    String county = "";
    //request.getParameter(HttpRequestConstants);
    String state = request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_STATE);
    String postalCode = request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_ZIPCODE);
    String directions = "";
    //request.getParameter(HttpRequestConstants);
    String geoCode = "";
    //request.getParameter(HttpRequestConstants);
    String mapUrl = "";
    //request.getParameter(HttpRequestConstants);

    if(ccType == null || ccType.length() <= 0)
    {
      errMsg += "<li>Card type missing.";
    }
    if(ccHolder == null || ccHolder.length() <= 0)
    {
      errMsg += "<li>Name on card missing.";
    }
    if(ccNumber == null || ccNumber.length() <= 0)
    {
      errMsg += "<li>Card number missing.";
    }
    else
    {
      longCCNumber = CreditCard.parseDirtyLong(ccNumber);
      if(!CreditCard.isValid(longCCNumber))
      {
        //Credit Card number invalid
        errMsg += "<li>Credit card number " + longCCNumber + " was not valid.";
      }
      else
      {
        String recVendor = CreditCard.vendorToString(CreditCard.recognizeVendor(longCCNumber));
        if(ccType != null && recVendor.compareToIgnoreCase(ccType) != 0)
        {
          //Credit Card type (vendor) invalid
          errMsg += "<li>Credit card number " + longCCNumber + " was not valid for the vendor " + ccType + ", it appears to be a number from " + recVendor + ".";
        }
      }
    }
    if(ccMonth == null || ccMonth.length() <= 0)
    {
      errMsg += "<li>Expiration month missing.";
    }
    if(ccYear == null || ccYear.length() <= 0)
    {
      errMsg += "<li>Expiration year missing.";
    }
    else if(ccMonth != null && ccMonth.length() > 0)
    {
      java.util.Date dateNow = new java.util.Date();
      Calendar calendarNow = Calendar.getInstance();
      calendarNow.setTime(dateNow);
      Calendar calendarCC = Calendar.getInstance();
      try
      {
        calendarCC.set(Integer.parseInt(ccYear), Integer.parseInt(ccMonth), 28);
      }
      catch(Exception e)
      {
        errMsg += "<li>Specified month(" + ccMonth + ") or year(" + ccYear + ") were not valid numbers.";
      }
      finally
      {
        if(calendarNow.after(calendarCC))
        {
          //Expiration date specified is after the current date
          errMsg += "<li>Credit card expiration date " + calendarCC.getTime() + " is BEFORE todays date " + calendarNow.getTime() + ".";
        }
      }
    }

    if(address1 == null || address1.length() <= 0)
    {
      errMsg += "<li>Address line 1 missing.";
    }
    if(city == null || city.length() <= 0)
    {
      errMsg += "<li>City missing.";
    }
    if(state == null || state.length() <= 0)
    {
      errMsg += "<li>State missing.";
    }
    if(postalCode == null || postalCode.length() <= 0)
    {
      errMsg += "<li>Zip/Postal Code missing.";
    }

    if(errMsg.length() > 0)
    {
      errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, errMsg);
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/user/profilenewcc.jsp");
      rd.forward(request, response);
      return false;
    }

    Address newCCAddress = AddressHelper.create(address1, address2, city, county, state, postalCode, directions, geoCode, mapUrl);
    if(newCCAddress == null)
    {
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "Could not create address record.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/user/profilenewcc.jsp");
      rd.forward(request, response);
      return false;
    }
    System.out.println("In handleCreateCustomerPayment, newCCAddress.addressId=" + newCCAddress.getAddressId());

    CustomerPayment customerPayment = CustomerPaymentHelper.create(customer.getCustomerId(), ccType, ccNumber, ccMonth + "/" + ccYear, ccHolder, ccCompany, newCCAddress.getAddressId());
    if(customerPayment == null)
    {
      try
      {
        newCCAddress.remove();
      }
      catch(javax.ejb.RemoveException re)
      {
      }
      //this isn't good, but what to do? Probably not something the user can resolve...
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "Could not create payment record.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/user/profilenewcc.jsp");
      rd.forward(request, response);
      return false;
    }

    return true;
  }

  / **
   *  Description of the Method
   *
   *@param  request                             Description of Parameter
   *@param  response                            Description of Parameter
   *@return                                     Description of the Returned
   *      Value
   *@exception  java.rmi.RemoteException        Description of Exception
   *@exception  javax.ejb.RemoveException       Description of Exception
   *@exception  java.io.IOException             Description of Exception
   *@exception  javax.servlet.ServletException  Description of Exception
   * /
  public static boolean handleDeleteCustomerPayment(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, javax.ejb.RemoveException, java.io.IOException, javax.servlet.ServletException
  {
    Person person = (Person)request.getSession().getAttribute(HttpSessionConstants.LOGIN_PERSON);
    Customer customer = (Customer)request.getSession().getAttribute(HttpSessionConstants.LOGIN_CUSTOMER);

    String paymentIdString = (String)request.getParameter(HttpRequestConstants.CUSTOMER_PAYMENT_ID);
    Integer paymentId = null;

    if(paymentIdString == null)
    {
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "The payment id was not specified, cannot delete.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/generalerror.jsp");
      rd.forward(request, response);
      return false;
    }

    try
    {
      paymentId = Integer.valueOf(paymentIdString);
    }
    catch(Exception e)
    {
    }
    if(paymentId == null)
    {
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "The payment id specified (" + paymentIdString + ") was not a valid id, cannot delete.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/generalerror.jsp");
      rd.forward(request, response);
      return false;
    }

    CustomerPayment customerPayment = CustomerPaymentHelper.findByPaymentId(paymentId);
    if(customerPayment == null)
    {
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "The payment id specified (" + paymentId.intValue() + ") was not found, cannot delete.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/generalerror.jsp");
      rd.forward(request, response);
      return false;
    }

    if(customerPayment.getCustomerId().compareTo(customer.getCustomerId()) != 0 && !Security.hasPermission(Security.USER_ADMIN, request.getSession()))
    {
      //customer id's do not match, do not allow view or edit...
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "The credit card specified does not belong to your account, you may not delete it.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/generalerror.jsp");
      rd.forward(request, response);
      return false;
    }

    customerPayment.remove();
    return true;
  }

  / **
   *  Description of the Method
   *
   *@param  request                             Description of Parameter
   *@param  response                            Description of Parameter
   *@return                                     Description of the Returned
   *      Value
   *@exception  java.rmi.RemoteException        Description of Exception
   *@exception  java.io.IOException             Description of Exception
   *@exception  javax.servlet.ServletException  Description of Exception
   * /
  public static boolean handleUpdateCustomerPayment(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException
  {
    // a little check to avoid an endless loop in error cases...
    if(request.getAttribute(HttpRequestConstants.ERROR_MESSAGE) != null)
    {
      return true;
    }

    boolean hasPermission = Security.hasPermission(Security.USER_ADMIN, request.getSession());
    String errMsg = "";
    Person person = (Person)request.getSession().getAttribute(HttpSessionConstants.LOGIN_PERSON);
    Customer customer = (Customer)request.getSession().getAttribute(HttpSessionConstants.LOGIN_CUSTOMER);

    String paymentIdString = (String)request.getParameter(HttpRequestConstants.CUSTOMER_PAYMENT_ID);
    Integer paymentId = null;

    if(paymentIdString == null)
    {
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "The payment id was not specified, cannot update.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/generalerror.jsp");
      rd.forward(request, response);
      return false;
    }

    paymentId = Integer.valueOf(paymentIdString);
    if(paymentId == null)
    {
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "The payment id specified was not a valid id, cannot update.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/generalerror.jsp");
      rd.forward(request, response);
      return false;
    }

    CustomerPayment customerPayment = CustomerPaymentHelper.findByPaymentId(paymentId);
    if(customerPayment == null)
    {
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "The payment id specified was not found, cannot update.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/generalerror.jsp");
      rd.forward(request, response);
      return false;
    }

    if(customerPayment.getCustomerId().compareTo(customer.getCustomerId()) != 0 && !hasPermission)
    {
      //customer id's do not match, do not allow view or edit...
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "The credit card specified does not belong to your account, you may not change it.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/generalerror.jsp");
      rd.forward(request, response);
      return false;
    }

    String ccType = CommonUtil.checkNull(request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_TYPE));
    String ccHolder = CommonUtil.checkNull(request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_HOLDER));
    String ccCompany = "";
    String ccNumber = CommonUtil.checkNull(request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_NUMBER));
    String ccMonth = CommonUtil.checkNull(request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_MONTH));
    String ccYear = CommonUtil.checkNull(request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_YEAR));
    long longCCNumber = 0;

    String address1 = request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_ADDRESS1);
    String address2 = request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_ADDRESS2);
    String city = request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_CITY);
    String county = "";
    //request.getParameter(HttpRequestConstants);
    String state = request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_STATE);
    String postalCode = request.getParameter(HttpRequestConstants.CUSTOMER_CREDITCARD_ZIPCODE);
    String directions = "";
    //request.getParameter(HttpRequestConstants);
    String geoCode = "";
    //request.getParameter(HttpRequestConstants);
    String mapUrl = "";
    //request.getParameter(HttpRequestConstants);

    if(ccType == null || ccType.length() <= 0)
    {
      errMsg += "<li>Card type missing.";
    }
    if(ccHolder == null || ccHolder.length() <= 0)
    {
      errMsg += "<li>Name on card missing.";
    }
    if(ccNumber == null || ccNumber.length() <= 0)
    {
      errMsg += "<li>Card number missing.";
    }
    else
    {
      longCCNumber = CreditCard.parseDirtyLong(ccNumber);
      if(!CreditCard.isValid(longCCNumber))
      {
        //Credit Card number invalid
        errMsg += "<li>Credit card number " + longCCNumber + " was not valid.";
      }
      else
      {
        String recVendor = CreditCard.vendorToString(CreditCard.recognizeVendor(longCCNumber));
        if(ccType != null && recVendor.compareToIgnoreCase(ccType) != 0)
        {
          //Credit Card type (vendor) invalid
          errMsg += "<li>Credit card number " + longCCNumber + " was not valid for the vendor " + ccType + ", it appears to be a number from " + recVendor + ".";
        }
      }
    }
    if(ccMonth == null || ccMonth.length() <= 0)
    {
      errMsg += "<li>Expiration month missing.";
    }
    if(ccYear == null || ccYear.length() <= 0)
    {
      errMsg += "<li>Expiration year missing.";
    }
    else if(ccMonth != null && ccMonth.length() > 0)
    {
      java.util.Date dateNow = new java.util.Date();
      Calendar calendarNow = Calendar.getInstance();
      calendarNow.setTime(dateNow);
      Calendar calendarCC = Calendar.getInstance();
      try
      {
        calendarCC.set(Integer.parseInt(ccYear), Integer.parseInt(ccMonth), 28);
      }
      catch(Exception e)
      {
        errMsg += "<li>Specified month(" + ccMonth + ") or year(" + ccYear + ") were not valid numbers.";
      }
      finally
      {
        if(calendarNow.after(calendarCC))
        {
          //Expiration date specified is after the current date
          errMsg += "<li>Credit card expiration date " + calendarCC.getTime() + " is BEFORE todays date " + calendarNow.getTime() + ".";
        }
      }
    }

    if(address1 == null || address1.length() <= 0)
    {
      errMsg += "<li>Address line 1 missing.";
    }
    if(city == null || city.length() <= 0)
    {
      errMsg += "<li>City missing.";
    }
    if(state == null || state.length() <= 0)
    {
      errMsg += "<li>State missing.";
    }
    if(postalCode == null || postalCode.length() <= 0)
    {
      errMsg += "<li>Zip/Postal Code missing.";
    }

    if(errMsg.length() > 0)
    {
      errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, errMsg);
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/user/profileeditcc.jsp");
      rd.forward(request, response);
      return false;
    }

    if(hasPermission)
    {
      customerPayment.setCardType(ccType);
    }
    customerPayment.setNameOnCard(ccHolder);
    customerPayment.setCompany(ccCompany);
    if(hasPermission)
    {
      customerPayment.setCardNumber(ccNumber);
    }
    customerPayment.setExpireDate(ccMonth + "/" + ccYear);

    Address address = AddressHelper.findByPrimaryKey(customerPayment.getBillingAddress());
    if(address == null)
    {
      request.setAttribute(HttpRequestConstants.ERROR_MESSAGE, "The address record associated with the payment information could not be found and was not updated, but the rest of the information has been updated.");
      RequestDispatcher rd = request.getRequestDispatcher("/commerce/profileeditcc.jsp");
      rd.forward(request, response);
      return false;
    }

    address.setaddress1(address1);
    address.setaddress2(address2);
    address.setCity(city);
    address.setCounty(county);
    address.setState(state);
    address.setPostalCode(postalCode);
    address.setDirections(directions);
    address.setGeoCode(geoCode);
    address.setMapUrl(mapUrl);

    return true;
  }

  / **
   *  Description of the Method
   *
   *@param  request                             Description of Parameter
   *@param  response                            Description of Parameter
   *@return                                     Description of the Returned
   *      Value
   *@exception  java.rmi.RemoteException        Description of Exception
   *@exception  java.io.IOException             Description of Exception
   *@exception  javax.servlet.ServletException  Description of Exception
   * /
  public static boolean handleUpdateActingAs(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException, java.io.IOException, javax.servlet.ServletException
  {
    boolean hasUserPermission = Security.hasPermission(Security.USER_ADMIN, request.getSession());
    if(hasUserPermission)
    {
      String personId = request.getParameter(HttpRequestConstants.PERSON_ID);
      String customerId = request.getParameter(HttpRequestConstants.CUSTOMER_ID);
      Person person = null;
      Customer customer = null;

      if(personId != null && personId.length() > 0)
      {
        person = PersonHelper.findByPrimaryKey(personId);
      }
      if(customerId != null && customerId.length() > 0)
      {
        customer = CustomerHelper.findByPrimaryKey(customerId);
      }

      if(person != null)
      {
        request.getSession().setAttribute(HttpSessionConstants.ACTING_AS_PERSON, person);
      }
      else
      {
        request.getSession().removeAttribute(HttpSessionConstants.ACTING_AS_PERSON);
      }
      if(customer != null)
      {
        request.getSession().setAttribute(HttpSessionConstants.ACTING_AS_CUSTOMER, customer);
      }
      else
      {
        request.getSession().removeAttribute(HttpSessionConstants.ACTING_AS_CUSTOMER);
      }
    }

    return true;
  } */
}
