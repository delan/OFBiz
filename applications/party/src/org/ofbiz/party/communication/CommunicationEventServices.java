/*
* 
*  Copyright (c) 2005 The Open For Business Project - www.ofbiz.org
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
* 
*  @author Si Chen (sichen@opensourcestrategies.com)
*/
package org.ofbiz.party.communication;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Locale;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class CommunicationEventServices {
    
    public static final String module = CommunicationEventServices.class.getName();
    public static final String resource = "PartyUiLabels";
    
    public static Map sendCommEventAsEmail(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map result = null;
                
        String communicationEventId = (String) context.get("communicationEventId");
        try {
            // find the communication event and make sure that it is actually an email
            GenericValue communicationEvent = delegator.findByPrimaryKey("CommunicationEvent", UtilMisc.toMap("communicationEventId", communicationEventId));
            if (communicationEvent == null) {
                String errMsg = UtilProperties.getMessage(resource,"commeventservices.communication_event_not_found_failure", locale);
                return ServiceUtil.returnError(errMsg + " " + communicationEventId);
            }
            if ((communicationEvent.getString("communicationEventTypeId") == null) ||
                !(communicationEvent.getString("communicationEventTypeId").equals("EMAIL_COMMUNICATION"))) {
                String errMsg = UtilProperties.getMessage(resource,"commeventservices.communication_event_must_be_email_for_email", locale);
                return ServiceUtil.returnError(errMsg + " " + communicationEventId);
            }
            
            // prepare the email
            Map sendMailParams = new HashMap();
            sendMailParams.put("sendFrom", communicationEvent.getString("contactMechIdFrom"));
            sendMailParams.put("subject", communicationEvent.getString("subject"));
            sendMailParams.put("body", communicationEvent.getString("content"));
            sendMailParams.put("communicationEventId", communicationEventId);
            
            sendMailParams.put("sendTo", communicationEvent.getString("contactMechIdTo"));
            sendMailParams.put("partyId", communicationEvent.getString("partyIdTo"));  // who it's going to
            sendMailParams.put("userLogin", userLogin);
            
            // send it
            Map tmpResult = dispatcher.runSync("sendMail", sendMailParams);
            if (ServiceUtil.isError(tmpResult)) {
                result = ServiceUtil.returnError(ServiceUtil.getErrorMessage(tmpResult));
            } else {
                result = ServiceUtil.returnSuccess();
            }
        } catch (GenericEntityException eex) {
            ServiceUtil.returnError(eex.getMessage());
        } catch (GenericServiceException esx) {
            ServiceUtil.returnError(esx.getMessage());
        }
        
        return result;
    }
}
