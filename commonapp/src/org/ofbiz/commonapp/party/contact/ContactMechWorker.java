/*
 * $Id$
 * $Log$
 *
 */
package org.ofbiz.commonapp.party.contact;

import java.util.*;
import javax.servlet.*;
import javax.servlet.jsp.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Worker methods for Contact Mechanisms
 * <p><b>Description:</b> None
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
 *@author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@version 1.0
 *@created January 22, 2002
 */
public class ContactMechWorker {
    public static void getContactMechAndRelated(PageContext pageContext, String contactMechAttr, String contactMechIdAttr,
            String partyContactMechAttr, String partyContactMechPurposesAttr, String contactMechTypeIdAttr, String contactMechTypeAttr, String purposeTypesAttr,
            String postalAddressAttr, String telecomNumberAttr, String requestNameAttr, String tryEntityAttr) {

        ServletRequest request = pageContext.getRequest();
        GenericDelegator delegator = (GenericDelegator) pageContext.getServletContext().getAttribute("delegator");
        GenericValue userLogin = (GenericValue) pageContext.getSession().getAttribute(SiteDefs.USER_LOGIN);
        
        boolean tryEntity = true;
        if (request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;
        if ("true".equals(request.getParameter("tryEntity"))) tryEntity = true;

        String donePage = request.getParameter("DONE_PAGE");
        if (donePage == null || donePage.length() <= 0) donePage="viewprofile";

        String contactMechId = request.getParameter("contactMechId");
        if (request.getAttribute("contactMechId") != null)
            contactMechId = (String) request.getAttribute("contactMechId");
        if (contactMechId != null)
            pageContext.setAttribute(contactMechIdAttr, contactMechId);
        
        
        //try to find a PartyContactMech with a valid date range
        Collection partyContactMechs = null;
        try {
            partyContactMechs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", userLogin.get("partyId"), "contactMechId", contactMechId)));
        } catch (GenericEntityException e) {
            //not much we can do, log the error and move along
            Debug.logWarning(e);
        }
        
        GenericValue partyContactMech = EntityUtil.getFirst(partyContactMechs);
        if (partyContactMech != null) {
            pageContext.setAttribute(partyContactMechAttr, partyContactMech);

            Collection partyContactMechPurposes = null;
            try {
                partyContactMechPurposes = EntityUtil.filterByDate(partyContactMech.getRelated("PartyContactMechPurpose"));
            } catch (GenericEntityException e) {
                //not much we can do, log the error and move along
                Debug.logWarning(e);
            }
            if (partyContactMechPurposes != null && partyContactMechPurposes.size() > 0)
                pageContext.setAttribute(partyContactMechPurposesAttr, partyContactMechPurposes);
        }

        GenericValue contactMech = null;
        try {
            contactMech = delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", contactMechId));
        } catch (GenericEntityException e) {
            //not much we can do, log the error and move along
            Debug.logWarning(e);
        }

        String contactMechTypeId = null;
        if (contactMech != null) {
            pageContext.setAttribute(contactMechAttr, contactMech);
            contactMechTypeId = contactMech.getString("contactMechTypeId");
        }
        
        if (request.getParameter("preContactMechTypeId") != null) {
            contactMechTypeId = request.getParameter("preContactMechTypeId");
            tryEntity = false;
        }
        
        if (contactMechTypeId != null) {
            pageContext.setAttribute(contactMechTypeIdAttr, contactMechTypeId);
    
            try {
                GenericValue contactMechType = delegator.findByPrimaryKey("ContactMechType", UtilMisc.toMap("contactMechTypeId", contactMechTypeId));
                if (contactMechType != null)
                    pageContext.setAttribute(contactMechTypeAttr, contactMechType);
            } catch (GenericEntityException e) {
                //not much we can do, log the error and move along
                Debug.logWarning(e);
            }

            Collection purposeTypes = new LinkedList();
            Iterator typePurposes = null;
            try {
                typePurposes = UtilMisc.toIterator(delegator.findByAnd("ContactMechTypePurpose", UtilMisc.toMap("contactMechTypeId", contactMechTypeId)));
            } catch (GenericEntityException e) {
                //not much we can do, log the error and move along
                Debug.logWarning(e);
            }
            while (typePurposes != null && typePurposes.hasNext()) {
                GenericValue contactMechTypePurpose = (GenericValue) typePurposes.next();
                GenericValue contactMechPurposeType = null;
                try {
                    contactMechPurposeType = contactMechTypePurpose.getRelatedOne("ContactMechPurposeType");
                } catch (GenericEntityException e) {
                    //not much we can do, log the error and move along
                    Debug.logWarning(e);
                }
                if (contactMechPurposeType != null) {
                    purposeTypes.add(contactMechPurposeType);
                }
            }
            if (purposeTypes.size() > 0)
                pageContext.setAttribute(purposeTypesAttr, purposeTypes);
        }

        String requestName;
        if (contactMech == null) {
            //create
            if ("POSTAL_ADDRESS".equals(contactMechTypeId)) {
                requestName = "createPostalAddress";
            } else if ("TELECOM_NUMBER".equals(contactMechTypeId)) {
                requestName = "createTelecomNumber";
            } else if ("EMAIL_ADDRESS".equals(contactMechTypeId)) {
                requestName = "createEmailAddress";
            } else {
                requestName = "createContactMech";
            }
        } else {
            //update
            if ("POSTAL_ADDRESS".equals(contactMechTypeId)) {
                requestName = "updatePostalAddress";
            } else if ("TELECOM_NUMBER".equals(contactMechTypeId)) {
                requestName = "updateTelecomNumber";
            } else if ("EMAIL_ADDRESS".equals(contactMechTypeId)) {
                requestName = "updateEmailAddress";
            } else {
                requestName = "updateContactMech";
            }
        }
        pageContext.setAttribute(requestNameAttr, requestName);

        if ("POSTAL_ADDRESS".equals(contactMechTypeId)) {
            GenericValue postalAddress = null;
            try {
                if (contactMech != null) postalAddress = contactMech.getRelatedOne("PostalAddress");
            } catch (GenericEntityException e) {
                //not much we can do, log the error and move along
                Debug.logWarning(e);
            }
            if (postalAddress == null) tryEntity = false;
            if (postalAddress != null) pageContext.setAttribute(postalAddressAttr, postalAddress);
        } else if ("TELECOM_NUMBER".equals(contactMechTypeId)) {
            GenericValue telecomNumber = null;
            try {
                if (contactMech != null) telecomNumber = contactMech.getRelatedOne("TelecomNumber");
            } catch (GenericEntityException e) {
                //not much we can do, log the error and move along
                Debug.logWarning(e);
            }
            if (telecomNumber == null) tryEntity = false;
            if (telecomNumber != null) pageContext.setAttribute(telecomNumberAttr, telecomNumber);
        }

        pageContext.setAttribute(tryEntityAttr, new Boolean(tryEntity));
    }
}
