/*
 * $Id: ServiceUtil.java,v 1.11 2004/05/14 23:37:45 jonesde Exp $
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.service;

import java.sql.Timestamp;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.security.Security;
import org.ofbiz.service.config.ServiceConfigUtil;

/**
 * Generic Service Utility Class
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.11 $
 * @since      2.0
 */
public class ServiceUtil {
    
    public static final String module = ServiceUtil.class.getName();
    
    /** A little short-cut method to check to see if a service returned an error */
    public static boolean isError(Map results) {
        return ModelService.RESPOND_ERROR.equals(results.get(ModelService.RESPONSE_MESSAGE));
    }

    /** A small routine used all over to improve code efficiency, make a result map with the message and the error response code */
    public static Map returnError(String errorMessage) {
        return returnError(errorMessage, null, null, null);
    }

    /** A small routine used all over to improve code efficiency, make a result map with the message and the error response code */
    public static Map returnError(List errorMessageList) {
        return returnError(null, errorMessageList, null, null);
    }

    /** A small routine used all over to improve code efficiency, make a result map with the message and the error response code, also forwards any error messages from the nestedResult */
    public static Map returnError(String errorMessage, List errorMessageList, Map errorMessageMap, Map nestedResult) {
        Map result = new HashMap();
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
        if (errorMessage != null) {
            result.put(ModelService.ERROR_MESSAGE, errorMessage);
        }

        List errorList = new LinkedList();
        if (errorMessageList != null) {
            errorList.addAll(errorMessageList);
        }
        
        Map errorMap = new HashMap();
        if (errorMessageMap != null) {
            errorMap.putAll(errorMessageMap);
        }
        
        if (nestedResult != null) {
            if (nestedResult.get(ModelService.ERROR_MESSAGE) != null) {
                errorList.add(nestedResult.get(ModelService.ERROR_MESSAGE));
            }
            if (nestedResult.get(ModelService.ERROR_MESSAGE_LIST) != null) {
                errorList.addAll((List) nestedResult.get(ModelService.ERROR_MESSAGE_LIST));
            }
            if (nestedResult.get(ModelService.ERROR_MESSAGE_MAP) != null) {
                errorMap.putAll((Map) nestedResult.get(ModelService.ERROR_MESSAGE_MAP));
            }
        }
        
        if (errorList.size() > 0) {
            result.put(ModelService.ERROR_MESSAGE_LIST, errorList);
        }
        if (errorMap.size() > 0) {
            result.put(ModelService.ERROR_MESSAGE_MAP, errorMap);
        }
        return result;
    }

    /** A small routine used all over to improve code efficiency, make a result map with the message and the success response code */
    public static Map returnSuccess(String successMessage) {
        return returnMessage(ModelService.RESPOND_SUCCESS, successMessage);
    }

    /** A small routine used all over to improve code efficiency, make a result map with the message and the success response code */
    public static Map returnSuccess() {
        return returnMessage(ModelService.RESPOND_SUCCESS, null);
    }

    /** A small routine to make a result map with the message and the response code
     * NOTE: This brings out some bad points to our message convention: we should be using a single message or message list
     *  and what type of message that is should be determined by the RESPONSE_MESSAGE (and there's another annoyance, it should be RESPONSE_CODE)
     */
    public static Map returnMessage(String code, String message) {
        Map result = new HashMap();
        if (code != null) result.put(ModelService.RESPONSE_MESSAGE, code);
        if (message != null) result.put(ModelService.SUCCESS_MESSAGE, message);
        return result;
    }

    /** A small routine used all over to improve code efficiency, get the partyId and does a security check
     *<b>security check</b>: userLogin partyId must equal partyId, or must have [secEntity][secOperation] permission
     */
    public static String getPartyIdCheckSecurity(GenericValue userLogin, Security security, Map context, Map result, String secEntity, String secOperation) {
        String partyId = (String) context.get("partyId");
        if (partyId == null || partyId.length() == 0) {
            partyId = userLogin.getString("partyId");
        }

        // partyId might be null, so check it
        if (partyId == null || partyId.length() == 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Party ID missing");
            return partyId;
        }

        // <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_CREATE permission
        if (!partyId.equals(userLogin.getString("partyId"))) {
            if (!security.hasEntityPermission(secEntity, secOperation, userLogin)) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "You do not have permission to perform this operation for this party");
                return partyId;
            }
        }
        return partyId;
    }

    public static void setMessages(HttpServletRequest request, String errorMessage, String eventMessage, String defaultMessage) {
        if (UtilValidate.isNotEmpty(errorMessage))
            request.setAttribute("_ERROR_MESSAGE_", errorMessage);

        if (UtilValidate.isNotEmpty(eventMessage))
            request.setAttribute("_EVENT_MESSAGE_", eventMessage);

        if (UtilValidate.isEmpty(errorMessage) && UtilValidate.isEmpty(eventMessage) && UtilValidate.isNotEmpty(defaultMessage))
            request.setAttribute("_EVENT_MESSAGE_", defaultMessage);

    }

    public static void getMessages(HttpServletRequest request, Map result, String defaultMessage,
            String msgPrefix, String msgSuffix, String errorPrefix, String errorSuffix, String successPrefix, String successSuffix) {
        String errorMessage = ServiceUtil.makeErrorMessage(result, msgPrefix, msgSuffix, errorPrefix, errorSuffix);
        String successMessage = ServiceUtil.makeSuccessMessage(result, msgPrefix, msgSuffix, successPrefix, successSuffix);
        setMessages(request, errorMessage, successMessage, defaultMessage);
    }

    public static String getErrorMessage(Map result) {
        return (String) result.get(ModelService.ERROR_MESSAGE);
    }

    public static String makeErrorMessage(Map result, String msgPrefix, String msgSuffix, String errorPrefix, String errorSuffix) {
        if (result == null) {
            Debug.logWarning("A null result map was passed", module);
            return null;
        }
        String errorMsg = (String) result.get(ModelService.ERROR_MESSAGE);
        List errorMsgList = (List) result.get(ModelService.ERROR_MESSAGE_LIST);
        Map errorMsgMap = (Map) result.get(ModelService.ERROR_MESSAGE_MAP);
        StringBuffer outMsg = new StringBuffer();

        if (errorMsg != null) {
            if (msgPrefix != null) outMsg.append(msgPrefix);
            outMsg.append(errorMsg);
            if (msgSuffix != null) outMsg.append(msgSuffix);
        }

        outMsg.append(makeMessageList(errorMsgList, msgPrefix, msgSuffix));

        if (errorMsgMap != null) {
            Iterator mapIter = errorMsgMap.entrySet().iterator();

            while (mapIter.hasNext()) {
                Map.Entry entry = (Map.Entry) mapIter.next();

                outMsg.append(msgPrefix);
                outMsg.append(entry.getKey());
                outMsg.append(": ");
                outMsg.append(entry.getValue());
                outMsg.append(msgSuffix);
            }
        }

        if (outMsg.length() > 0) {
            StringBuffer strBuf = new StringBuffer();

            if (errorPrefix != null) strBuf.append(errorPrefix);
            strBuf.append(outMsg.toString());
            if (errorSuffix != null) strBuf.append(errorSuffix);
            return strBuf.toString();
        } else {
            return null;
        }
    }

    public static String makeSuccessMessage(Map result, String msgPrefix, String msgSuffix, String successPrefix, String successSuffix) {
        if (result == null) {
            return "";
        }
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
            strBuf.append(outMsg.toString());
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
                String curMsg = iter.next().toString();
                if (msgPrefix != null) outMsg.append(msgPrefix);
                outMsg.append(curMsg);
                if (msgSuffix != null) outMsg.append(msgSuffix);
            }
        }
        return outMsg.toString();
    }
    
    public static Map purgeOldJobs(DispatchContext dctx, Map context) {
        String sendPool = ServiceConfigUtil.getSendPool();
        int daysToKeep = ServiceConfigUtil.getPurgeJobDays();
        GenericDelegator delegator = dctx.getDelegator();
        
        Timestamp now = UtilDateTime.nowTimestamp();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getTime());
        cal.add(Calendar.DAY_OF_YEAR, daysToKeep * -1);
        Timestamp purgeTime = new Timestamp(cal.getTimeInMillis());

        // create the conditions to query
        EntityCondition pool = new EntityExpr("poolId", EntityOperator.EQUALS, sendPool);

        List finExp = UtilMisc.toList(new EntityExpr("finishDateTime", EntityOperator.NOT_EQUAL, null));
        finExp.add(new EntityExpr("finishDateTime", EntityOperator.LESS_THAN, purgeTime));

        List canExp = UtilMisc.toList(new EntityExpr("cancelDateTime", EntityOperator.NOT_EQUAL, null));
        canExp.add(new EntityExpr("cancelDateTime", EntityOperator.LESS_THAN, purgeTime));

        EntityCondition cancelled = new EntityConditionList(canExp, EntityOperator.AND);
        EntityCondition finished = new EntityConditionList(finExp, EntityOperator.AND);

        EntityCondition done = new EntityConditionList(UtilMisc.toList(cancelled, finished), EntityOperator.OR);
        EntityCondition main = new EntityConditionList(UtilMisc.toList(done, pool), EntityOperator.AND);

        // lookup the jobs
        List foundJobs = null;
        try {
            foundJobs = delegator.findByCondition("JobSandbox", main, null, null);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get jobs to purge");
            return ServiceUtil.returnError(e.getMessage());
        }
        
        if (foundJobs != null && foundJobs.size() > 0) {
            Iterator i = foundJobs.iterator();
            while (i.hasNext()) {
                GenericValue job = (GenericValue) i.next();
                try {
                    job.remove();
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Unable to remove job : " + job, module);
                }                  
            }
        }
        
        return ServiceUtil.returnSuccess();
    }

    public static Map cancelJob(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (!security.hasPermission("SERVICE_INVOKE_ANY", userLogin)) {
            return ServiceUtil.returnError("You do not have permission to run this service");
        }

        String jobName = (String) context.get("jobName");
        Timestamp runTime = (Timestamp) context.get("runTime");
        Map fields = UtilMisc.toMap("jobName", jobName, "runTime", runTime);

        GenericValue job = null;
        try {
            job = delegator.findByPrimaryKey("JobSandbox", fields);
            if (job != null) {
                job.set("cancelDateTime", UtilDateTime.nowTimestamp());
                job.store();
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Unable to cancel job : " + fields);
        }

        Timestamp cancelDate = job.getTimestamp("cancelDateTime");
        if (cancelDate != null) {
            Map result = ServiceUtil.returnSuccess();
            result.put("cancelDateTime", cancelDate);
            return result;
        } else {
            return ServiceUtil.returnError("Unable to cancel job : " + job);
        }
    }
}
