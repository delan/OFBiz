/*
 * $Id$
 *
 * Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.securityext.thirdparty.truition;

import java.util.Collection;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.contact.ContactHelper;

/**
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.3
 */
public class TruitionCoReg {

    public static final String module =  TruitionCoReg.class.getName();
    public static final String logPrefix = "Truition Cookie Info: ";

    public static String truitionReg(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        StringBuffer cookieNameB = new StringBuffer();
        StringBuffer cookieValue = new StringBuffer();
        if (userLogin != null) {
            TruitionCoReg.makeTruitionCookie(userLogin, cookieNameB, cookieValue);
        }

        // locate the domain/cookie name setting
        String domainName = UtilProperties.getPropertyValue("truition.properties", "truition.domain.name");
        String cookieName = UtilProperties.getPropertyValue("truition.properties", "truition.cookie.name");
        if (UtilValidate.isEmpty(domainName)) {
            Debug.logError("Truition is not properly configured; domainName missing; see truition.properties", module);
            return "error";
        }
        if (UtilValidate.isEmpty(cookieName)) {
            Debug.logError("Truition is not properly configured; cookieName missing; see truition.properties", module);
            return "error";
        }

        // create the cookie
        Cookie tru = new Cookie(cookieName, cookieValue.toString());
        tru.setDomain(domainName);
        tru.setPath("/");
        tru.setMaxAge(-1); // session cookie (not persisted)
        resp.addCookie(tru);

        Debug.log("Set Truition Cookie [" + cookieName + "] - " + cookieValue.toString(), module);
        return "success";
    }

    public static String truitionLogoff(HttpServletRequest req, HttpServletResponse resp) {
        // locate the domain/cookie name setting
        String domainName = UtilProperties.getPropertyValue("truition.properties", "truition.domain.name");
        String cookieName = UtilProperties.getPropertyValue("truition.properties", "truition.cookie.name");
        if (UtilValidate.isEmpty(domainName)) {
            Debug.logError("Truition is not properly configured; domainName missing; see truition.properties", module);
            return "error";
        }
        if (UtilValidate.isEmpty(cookieName)) {
            Debug.logError("Truition is not properly configured; cookieName missing; see truition.properties", module);
            return "error";
        }

        Cookie[] cookies = req.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            if (cookieName.equals(cookies[i].getName())) {
                cookies[i].setMaxAge(0);
                resp.addCookie(cookies[i]);
            }
        }

        Debug.log("Set truition cookie [" + cookieName + " to expire now.", module);
        return "success";
    }

    public static String truitionRedirect(HttpServletRequest req, HttpServletResponse resp) {
        // redirect URL form field
        String redirectUrlName = UtilProperties.getPropertyValue("truition.properties", "truition.redirect.urlName");
        String redirectUrl = req.getParameter(redirectUrlName);
        Debug.log("Redirect to : " + redirectUrl, module);
        if (redirectUrl != null) {
            try {
                resp.sendRedirect(redirectUrl);
            } catch (IOException e) {
                Debug.logError(e, module);
                req.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                return "error";
            }

            Debug.log("Sending truition redirect - " + redirectUrl, module);
            return "redirect";
        }
        return "success";
    }

    public static void makeTruitionCookie(GenericValue userLogin, StringBuffer cookieName, StringBuffer cookieValue) {
        String siteId = UtilProperties.getPropertyValue("truition.properties", "truition.siteId");

        if (UtilValidate.isEmpty(siteId)) {
            Debug.logError("Truition is not properly configured; siteId missing; see truition.properties!", module);
            return;
        }

        // user login information
        String nickName = userLogin.getString("userLoginId");
        String password = userLogin.getString("currentPassword");
        String partyId = userLogin.getString("partyId");
        Debug.log(logPrefix + "nickName: " + nickName, module);
        Debug.log(logPrefix + "password: " + password, module);
        Debug.log(logPrefix + "partyId: " + partyId, module);

        GenericValue party = null;
        try {
            party = userLogin.getRelatedOne("Party");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }

        if (party != null) {
            String title = null;
            String firstName = null;
            String lastName = null;
            if ("PERSON".equals(party.getString("partyTypeId"))) {
                GenericValue person = null;
                try {
                    person = party.getRelatedOne("Person");
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }

                // first/last name
                if (person != null) {
                    title = person.getString("personalTitle");
                    firstName = person.getString("firstName");
                    lastName = person.getString("lastName");
                    if (title == null) {
                        title = "";
                    }
                }
                Debug.log(logPrefix + "title: " + title, module);
                Debug.log(logPrefix + "firstName: " + firstName, module);
                Debug.log(logPrefix + "lastName: " + lastName, module);

                // email address
                String emailAddress = null;
                Collection emCol = ContactHelper.getContactMech(party, "PRIMARY_EMAIL", "EMAIL_ADDRESS", false);
                if (emCol == null || emCol.size() == 0) {
                    emCol = ContactHelper.getContactMech(party, null, "EMAIL_ADDRESS", false);
                }
                if (emCol != null && emCol.size() > 0) {
                    GenericValue emVl = (GenericValue) emCol.iterator().next();
                    if (emVl != null) {
                        emailAddress = emVl.getString("infoString");
                    }
                } else {
                    emailAddress = "";
                }
                Debug.log(logPrefix + "emailAddress: " + emailAddress, module);

                // shipping address
                String address1 = null;
                String address2 = null;
                String city = null;
                String state = null;
                String zipCode = null;
                String country = null;
                Collection adCol = ContactHelper.getContactMech(party, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
                if (adCol == null || adCol.size() == 0) {
                    adCol = ContactHelper.getContactMech(party, null, "POSTAL_ADDRESS", false);
                }
                if (adCol != null && adCol.size() > 0) {
                    GenericValue adVl = (GenericValue) adCol.iterator().next();
                    if (adVl != null) {
                        GenericValue addr = null;
                        try {
                            addr = adVl.getDelegator().findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId",
                                    adVl.getString("contactMechId")));
                        } catch (GenericEntityException e) {
                            Debug.logError(e, module);
                        }
                        if (addr != null) {
                            address1 = addr.getString("address1");
                            address2 = addr.getString("address2");
                            city = addr.getString("city");
                            state = addr.getString("stateProvinceGeoId");
                            zipCode = addr.getString("postalCode");
                            country = addr.getString("countryGeoId");
                            if (address2 == null) {
                                address2 = "";
                            }
                        }
                    }
                }
                Debug.log(logPrefix + "address1: " + address1, module);
                Debug.log(logPrefix + "address2: " + address2, module);
                Debug.log(logPrefix + "city: " + city, module);
                Debug.log(logPrefix + "state: " + state, module);
                Debug.log(logPrefix + "zipCode: " + zipCode, module);
                Debug.log(logPrefix + "country: " + country, module);

                // phone number
                String phoneNumber = null;
                Collection phCol = ContactHelper.getContactMech(party, "PHONE_HOME", "TELECOM_NUMBER", false);
                if (phCol == null || phCol.size() == 0) {
                    phCol = ContactHelper.getContactMech(party, null, "TELECOM_NUMBER", false);
                }
                if (phCol != null && phCol.size() > 0) {
                    GenericValue phVl = (GenericValue) phCol.iterator().next();
                    if (phVl != null) {
                        GenericValue tele = null;
                        try {
                            tele = phVl.getDelegator().findByPrimaryKey("TelecomNumber", UtilMisc.toMap("contactMechId",
                                    phVl.getString("contactMechId")));
                        } catch (GenericEntityException e) {
                            Debug.logError(e, module);
                        }
                        if (tele != null) {
                            phoneNumber = ""; // reset the string
                            String cc = tele.getString("countryCode");
                            String ac = tele.getString("areaCode");
                            String nm = tele.getString("contactNumber");
                            if (UtilValidate.isNotEmpty(cc)) {
                                phoneNumber = phoneNumber + cc + "-";
                            }
                            if (UtilValidate.isNotEmpty(ac)) {
                                phoneNumber = phoneNumber + ac + "-";
                            }
                            phoneNumber = phoneNumber + nm;
                        } else {
                            phoneNumber = "";
                        }
                    }
                }
                Debug.log(logPrefix + "phoneNumber: " + phoneNumber, module);

                // create the cookie
                edeal.coreg.EdCoReg.ed_create_cookie_nvp(nickName, password, title, firstName, lastName, emailAddress, address1, address2, city, state, zipCode, country, phoneNumber, siteId, cookieName, cookieValue, "", "", "", partyId, "", "");
            } else {
                Debug.logError("Truition requires a Person to be logged in. First/Last name required!", module);
                return;
            }
        }
    }

    public static boolean truitionEnabled() {
        return "Y".equalsIgnoreCase(UtilProperties.getPropertyValue("truition.properties", "truition.enabled", "N"));
    }
}
