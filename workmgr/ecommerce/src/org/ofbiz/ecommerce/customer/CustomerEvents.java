/*
 * $Id$
 * $Log$
 * Revision 1.35  2001/10/19 16:45:32  azeneski
 * Moved party, contact and login related events out.
 *
 * Revision 1.34  2001/10/14 09:59:42  jonesde
 * Updated field names for round of resolving reserved word collisions
 */
package org.ofbiz.ecommerce.customer;

import javax.servlet.*;
import javax.servlet.http.*;
import java.net.*;
import java.util.*;
import java.sql.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.commonapp.party.contact.ContactHelper;
import org.ofbiz.commonapp.security.login.LoginEvents;

/**
 * <p><b>Title:</b> CustomerEvents.java
 * <p><b>Description:</b> Events for customer information maintenance.
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
public class CustomerEvents {
    /** Creates the necessary database entries for a new customer account based on
     *  the form inputs of the corresponding JSP customer/newcustomer.jsp.
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String createCustomer(HttpServletRequest request, HttpServletResponse response) {
        GenericValue newUserLogin = null;
        String contextRoot=(String)request.getAttribute(SiteDefs.CONTEXT_ROOT);
        //getServletContext appears to be new on the session object for Servlet 2.3
        ServletContext application = request.getSession().getServletContext();
        URL ecommercePropertiesUrl = null;
        try { ecommercePropertiesUrl = application.getResource("/WEB-INF/ecommerce.properties"); }
        catch(java.net.MalformedURLException e) { Debug.logWarning(e); }
        
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
        if(UtilProperties.propertyValueEqualsIgnoreCase(ecommercePropertiesUrl, "create.allow.password", "true")) {
            errMsg += LoginEvents.setPassword(tempUserLogin, password, confirmPassword, passwordHint);
        }
        
        
        if(errMsg.length() > 0) {
            errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
            request.setAttribute("ERROR_MESSAGE", errMsg);
            return "error";
        }
        
        //UserLogin with username does not exist: create new user...
        if(!UtilProperties.propertyValueEqualsIgnoreCase(ecommercePropertiesUrl, "create.allow.password", "true")) password = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "default.customer.password", "ungssblepswd");
        
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
        
        if(UtilProperties.propertyValueEqualsIgnoreCase(ecommercePropertiesUrl, "create.allow.password", "true")) request.getSession().setAttribute(SiteDefs.USER_LOGIN, newUserLogin);
        return "success";
    }
        
}
