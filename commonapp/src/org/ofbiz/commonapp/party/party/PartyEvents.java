/*
 * $Id$
 * $Log$ 
 */

package org.ofbiz.commonapp.party.party;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;

/**
 * <p><b>Title:</b> PartyEvents.java
 * <p><b>Description:</b> Events for Party/Person maintenance.
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
public class PartyEvents {
    
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
        String partyId = userLogin.getString("partyId");
        
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
            try { person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId)); }
            catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); person = null; }
            
            if(person == null) {
                person = delegator.makeValue("Person", UtilMisc.toMap("partyId", partyId));
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
            person.set("comments", comment, false);
            
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
      GenericValue person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
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
    
}
