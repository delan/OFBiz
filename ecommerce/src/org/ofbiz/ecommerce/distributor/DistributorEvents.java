/*
 * $Id$
 * $Log$
 * Revision 1.5  2001/10/27 20:22:27  jonesde
 * Now creates Party and PartyRole in additions to PartyRelationship for distributors
 *
 * Revision 1.4  2001/09/28 21:57:53  jonesde
 * Big update for fromDate PK use, organization stuff
 *
 * Revision 1.3  2001/09/27 15:53:31  epabst
 * refactored code to use getRelatedByAnd, filterByDate
 *
 * Revision 1.2  2001/09/26 18:41:44  epabst
 * renamed getActive to filterByDate()
 * renamed getContactMech to getContactMechByPurpose/ByType
 *
 * Revision 1.1  2001/09/26 15:09:53  epabst
 * track the distributorId now and store in PartyRelationship
 * It can be initially set via http://.../setdistributor?distributor_id=3433
 *
 * Revision 1.25  2001/09/25 20:26:09  epabst
 */
package org.ofbiz.ecommerce.distributor;

import javax.servlet.http.*;
import javax.servlet.*;
import java.util.*;
import java.sql.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.commonapp.party.contact.ContactHelper;

/**
 * <p><b>Title:</b> DistributorEvents.java
 * <p><b>Description:</b> Events for distributor customization and association.
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
public class DistributorEvents {
    private static final String DISTRIBUTOR_ID = "_DISTRIBUTOR_ID_";
    
    /** Save the distributorId specified in the request object into the session.
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String setDistributor(HttpServletRequest request, HttpServletResponse response) {
        String distributorId = request.getParameter("distributor_id");
        if(!UtilValidate.isNotEmpty(distributorId)) {
            //the distributorId was not given
            //Don't show this error to the user
            Debug.logWarning("setDistributor was called without 'distributor_id' being specified");
            return "error";
        }
        
        setDistributorId(request, distributorId);
        
        //ignore return value
        updateAssociatedDistributor(request, response);
        
        return "success";
    }
    
    /** Update the distributor association for the logged in user, if possible.
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String updateAssociatedDistributor(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
        
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        GenericValue party = null;
        try { party = userLogin == null ? null : userLogin.getRelatedOne("Party"); }
        catch(GenericEntityException gee) { Debug.logWarning(gee); }
        if(party != null) {
            //if a distributorId is already associated, it will be used instead
            String currentDistributorId = getDistributorId(party);
            if(UtilValidate.isEmpty(currentDistributorId)) {
                String distributorId = getDistributorId(request);
                if(UtilValidate.isNotEmpty(distributorId)) {
                    Collection toBeStored = new LinkedList();
                    
                    //create distributor Party
                    //create distributor PartyRole
                    //create PartyRelationship
                    GenericValue partyRelationship = delegator.makeValue("PartyRelationship", UtilMisc.toMap("partyIdFrom", party.getString("partyId"), "partyIdTo", distributorId, "roleTypeIdFrom", "CUSTOMER", "roleTypeIdTo", "DISTRIBUTOR"));
                    partyRelationship.set("fromDate", UtilDateTime.nowTimestamp());
                    partyRelationship.set("partyRelationshipTypeId", "DISTRIBUTION_CHANNEL_RELATIONSHIP");
                    toBeStored.add(partyRelationship);
                    
                    toBeStored.add(delegator.makeValue("Party", UtilMisc.toMap("partyId", distributorId)));
                    toBeStored.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", distributorId, "roleTypeId", "DISTRIBUTOR")));
                    try {
                        delegator.storeAll(toBeStored);
                        Debug.logInfo("distributor for user " + party.getString("partyId") + " set to " + distributorId);
                    }
                    catch (GenericEntityException gee) {
                        Debug.logWarning(gee);
                    }
                }
                else {
                    //no distributorId is available
                    Debug.log("no distributor in session or already associated with user " + userLogin.getString("partyId"));
                    return "error";
                }
            }
            else {
                setDistributorId(request, currentDistributorId);
            }
            
            return "success";
        }
        else {
            //not logged in
            Debug.log("can't associate distributor since not logged in yet");
            return "error";
        }
    }
    
    /** Get the distributorId for the active session.
     *@param request The HTTPRequest object for the current request
     *@return String the distributor id
     */
    public static String getDistributorId(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(DISTRIBUTOR_ID);
    }
    
    /** Set the distributorId for the active session.
     *@param request The HTTPRequest object for the current request
     *@param distributorId the distributor id
     */
    public static void setDistributorId(HttpServletRequest request, String distributorId) {
        request.getSession().setAttribute(DISTRIBUTOR_ID, distributorId);
        Debug.logInfo("set distributorId in session to " + distributorId);
    }
    
    private static GenericValue getDistributorPartyRelationship(GenericValue party) {
        try {
            return EntityUtil.getFirst(EntityUtil.filterByDate(party.getRelatedByAnd("FromPartyRelationship", UtilMisc.toMap("roleTypeIdTo", "DISTRIBUTOR"))));
        } catch (GenericEntityException gee) { Debug.logWarning(gee); }
        return null;
    }
    
    private static String getDistributorId(GenericValue party) {
        GenericValue partyRelationship = getDistributorPartyRelationship(party);
        return partyRelationship == null ? null : partyRelationship.getString("partyIdTo");
    }
}
