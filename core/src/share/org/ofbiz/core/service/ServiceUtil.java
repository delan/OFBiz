/*
 * $Id$
 */

package org.ofbiz.core.service;

import java.util.*;
import javax.servlet.http.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;

/**
 * <p><b>Title:</b> Generic Service Utility Class
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
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
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    October 20, 2001
 *@version    1.0
 */
public class ServiceUtil {
    /** A small routine used all over to improve code efficiency, make a result map with the message and the error response code */
    public static Map returnError(String errorMessage) {
        Map result = new HashMap();
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
        if (errorMessage != null) result.put(ModelService.ERROR_MESSAGE, errorMessage);
        return result;
    }
    
    /** A small routine used all over to improve code efficiency, make a result map with the message and the success response code */
    public static Map returnSuccess(String successMessage) {
        return returnMessage(ModelService.RESPOND_SUCCESS, successMessage);
    }
    
    /** A small routine to make a result map with the message and the response code */
    public static Map returnMessage(String code, String message) {
        Map result = new HashMap();
        if (code != null) result.put(ModelService.RESPONSE_MESSAGE, code);
        if (message != null) result.put(ModelService.SUCCESS_MESSAGE, message);
        return result;
    }
    
    /** A small routine used all over to improve code efficiency, get the partyId and does a security check
     *<b>security check</b>: userLogin partyId must equal partyId, or must have <secEntity><secOperation> permission
     */
    public static String getPartyIdCheckSecurity(GenericValue userLogin, Security security, Map context, Map result, String secEntity, String secOperation) {
        String partyId = (String) context.get("partyId");
        if (partyId == null || partyId.length() == 0) {
            partyId = userLogin.getString("partyId");
        }
        
        //partyId might be null, so check it
        if (partyId == null || partyId.length() == 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Party ID missing");
            return partyId;
        }
        
        //<b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_CREATE permission
        if (!partyId.equals(userLogin.getString("partyId"))) {
            if (!security.hasEntityPermission(secEntity, secOperation, userLogin)) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "You do not have permission to perform this operation for this party");
                return partyId;
            }
        }
        return partyId;
    }
    
    public static void getMessages(HttpServletRequest request, Map result, String defaultMessage, 
            String msgPrefix, String msgSuffix, String errorPrefix, String errorSuffix, String successPrefix, String successSuffix) {
        String errorMessage = ServiceUtil.makeErrorMessage(result, msgPrefix, msgSuffix, errorPrefix, errorSuffix);
        if (UtilValidate.isNotEmpty(errorMessage))
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errorMessage);
            
        String successMessage = ServiceUtil.makeSuccessMessage(result, msgPrefix, msgSuffix, successPrefix, successSuffix);
        if (UtilValidate.isNotEmpty(successMessage))
            request.setAttribute(SiteDefs.EVENT_MESSAGE, successMessage);

        if (UtilValidate.isEmpty(errorMessage) && UtilValidate.isEmpty(successMessage) && UtilValidate.isNotEmpty(defaultMessage))
            request.setAttribute(SiteDefs.EVENT_MESSAGE, defaultMessage);
    }
    
    public static String makeErrorMessage(Map result, String msgPrefix, String msgSuffix, String errorPrefix, String errorSuffix) {
        String errorMsg = (String) result.get(ModelService.ERROR_MESSAGE);
        List errorMsgList = (List) result.get(ModelService.ERROR_MESSAGE_LIST);
        StringBuffer outMsg = new StringBuffer();
        
        outMsg.append(makeMessageList(errorMsgList, msgPrefix, msgSuffix));
        
        if (errorMsg != null) {
            if (msgPrefix != null) outMsg.append(msgPrefix);
            outMsg.append(errorMsg);
            if (msgSuffix != null) outMsg.append(msgSuffix);
        }
        
        if (outMsg.length() > 0) {
            StringBuffer strBuf = new StringBuffer();
            if (errorPrefix != null) strBuf.append(errorPrefix);
            strBuf.append(outMsg);
            if (errorSuffix != null) strBuf.append(errorSuffix);
            return strBuf.toString();
        } else {
            return null;
        }
    }

    public static String makeSuccessMessage(Map result, String msgPrefix, String msgSuffix, String successPrefix, String successSuffix) {
        String successMsg = (String) result.get(ModelService.SUCCESS_MESSAGE);
        List successMsgList = (List) result.get(ModelService.SUCCESS_MESSAGE_LIST);
        StringBuffer outMsg = new StringBuffer();
        
        outMsg.append(makeMessageList(successMsgList, msgPrefix, msgSuffix));
        
        if (successMsg != null) {
            if (msgPrefix != null) outMsg.append(msgPrefix);
            outMsg.append(successMsg);
            if (msgSuffix != null) outMsg.append(msgSuffix);
        }
        
        if (outMsg.length() > 0) {
            StringBuffer strBuf = new StringBuffer();
            if (successPrefix != null) strBuf.append(successPrefix);
            strBuf.append(outMsg);
            if (successSuffix != null) strBuf.append(successSuffix);
            return strBuf.toString();
        } else {
            return null;
        }
    }

    public static String makeMessageList(List msgList, String msgPrefix, String msgSuffix) {
        StringBuffer outMsg = new StringBuffer();
        if (msgList != null && msgList.size() > 0) {
            Iterator iter = msgList.iterator();
            while (iter.hasNext()) {
                String curMsg = (String) iter.next();
                if (msgPrefix != null) outMsg.append(msgPrefix);
                outMsg.append(curMsg);
                if (msgSuffix != null) outMsg.append(msgSuffix);
            }
        }
        return outMsg.toString();
    }
}
