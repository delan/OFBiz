/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.commonapp.marketing.tracking;


import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.stats.*;
import org.ofbiz.core.util.*;


/**
 * Events used for maintaining TrackingCode related information
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    1.0
 * @created    1 October 2002
 */
public class TrackingCodeEvents {

    public static final String module = TrackingCodeEvents.class.getName();

    /** If TrackingCode monitoring is desired this event should be added to the list 
     * of events that run on every request. This event looks for the parameter 
     * <code>autoTrackingCode</code>.
     */
    public static String checkTrackingCodeUrlParam(HttpServletRequest request, HttpServletResponse response) {
        String trackingCodeId = request.getParameter("autoTrackingCode");
        if (UtilValidate.isNotEmpty(trackingCodeId)) {
            //tracking code is specified on the request, get the TrackingCode value and handle accordingly
            GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
            GenericValue trackingCode = null;
            try {
                trackingCode = delegator.findByPrimaryKeyCache("TrackingCode", UtilMisc.toMap("trackingCodeId", trackingCodeId));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error looking up TrackingCode with trackingCodeId [" + trackingCodeId + "], ignoring this trackingCodeId");
                return "error";
            }
            
            if (trackingCode == null) {
                Debug.logError("TrackingCode not found for trackingCodeId [" + trackingCodeId + "], ignoring this trackingCodeId.");
                //this return value will be ignored, but we'll designate this as an error anyway
                return "error";
            }

            //check effective dates
            java.sql.Timestamp nowStamp = UtilDateTime.nowTimestamp();
            if (trackingCode.get("fromDate") != null && nowStamp.before(trackingCode.getTimestamp("fromDate"))) {
                if (Debug.infoOn()) Debug.logInfo("The TrackingCode with ID [" + trackingCodeId + "] has not yet gone into effect, ignoring this trackingCodeId");
                return "success";
            }
            if (trackingCode.get("thruDate") != null && nowStamp.after(trackingCode.getTimestamp("thruDate"))) {
                if (Debug.infoOn()) Debug.logInfo("The TrackingCode with ID [" + trackingCodeId + "] has expired, ignoring this trackingCodeId");
                return "success";
            }
            
            //persist that info by associating with the current visit
            GenericValue visit = VisitHandler.getVisit(request.getSession());
            if (visit == null) {
                Debug.logWarning("Could not get visit, not associating trackingCode [" + trackingCodeId + "] with visit");
            } else {
                GenericValue trackingCodeVisit = delegator.makeValue("TrackingCodeVisit", 
                        UtilMisc.toMap("trackingCodeId", trackingCodeId, "visitId", visit.get("visitId"), 
                        "fromDate", UtilDateTime.nowTimestamp(), "sourceEnumId", "TKCDSRC_URL_PARAM"));
                try {
                    trackingCodeVisit.create();
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Error while saving TrackingCodeVisit");
                }
            }
            
            //write trackingCode cookies with the value set to the trackingCodeId
            //NOTE: just write these cookies and if others exist from other tracking codes they will be overwritten, ie only keep the newest
            
            //TODO: if trackingCode.billableLifetime not null and is > 0 write a billable cookie with name in the form: TKCDB_{trackingCode.trackingCodeTypeId} and timeout will be trackingCode.billableLifetime
            
            //TODO: if trackingCode.trackableLifetime not null and is > 0 write a trackable cookie with name in the form: TKCDT_{trackingCode.trackingCodeTypeId} and timeout will be trackingCode.trackableLifetime
            
            
            //if forward/redirect is needed, do a response.sendRedirect and return null to tell the control servlet to not do any other requests/views
            String redirectUrl = trackingCode.getString("redirectUrl");
            if (UtilValidate.isNotEmpty(redirectUrl)) {
                try {
                    response.sendRedirect(redirectUrl);
                } catch (java.io.IOException e) {
                    Debug.logError(e, "Could not redirect as requested in the trackingCode to: " + redirectUrl);
                }
                return null;
            }
        }
        
        return "success";
    }
    
    /** If attaching TrackingCode Cookies to the visit is desired this event should be added to the list 
     * of events that run on the first hit in a visit.
     */
    public static String checkTrackingCodeCookies(HttpServletRequest request, HttpServletResponse response) {
        //TODO: loop through cookies and look for ones with a name that starts with TKCDT_ for trackable cookies
        
        //TODO: for each trackingCodeId found in this way attach to the visit with the TKCDSRC_COOKIE sourceEnumId
        
        return "success";
    }
    
    /** Makes a list of TrackingCodeOrder entities to be attached to the current order; called by the createOrder event */
    public static List makeTrackingCodeOrders(HttpServletRequest request) {
        //TODO: go through the cookies and find any that start with TKCDB_ for billable tracking code cookies with isBillable=Y
        
        //TODO: next go through the TKCDT_ cookies and for each one that doesn't have a corresponding billable code add it to the list with isBillable=N
        
        return null;
    }
}
