/*
 * $Id$
 * $Log$
 * Revision 1.3  2001/12/29 12:26:08  jonesde
 * Finished moving WorkEffort functionality to services, party assignment still needs to be done
 *
 * Revision 1.2  2001/12/23 06:29:42  jonesde
 * Replaced preStoreOther stuff with storeAll
 *
 * Revision 1.1  2001/12/22 10:40:21  jonesde
 * Early first pass at WorkEffortServices
 *
 *
 */
package org.ofbiz.commonapp.workeffort.workeffort;

import java.util.*;
import java.sql.Timestamp;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.service.*;

/**
 * <p><b>Title:</b> WorkEffortServices
 * <p><b>Description:</b> Services to handle form input and other data changes
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
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version 1.0
 * Created on November 7, 2001
 */
public class WorkEffortServices {
    /** Service that creates a WorkEffort entity
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map createWorkEffort(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        //where is userLogin?? TODO
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        String workEffortId = null;
        Long nextSeqId = delegator.getNextSeqId("WorkEffort");
        if (nextSeqId == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Could not get an Id for a new WorkEffort (NextSeqIdError)");
            return result;
        } else {
            workEffortId = nextSeqId.toString();
        }

        GenericValue newWorkEffort = null;
        newWorkEffort = delegator.makeValue("WorkEffort", null);
        Collection toBeStored = new LinkedList();
        toBeStored.add(newWorkEffort);
        Timestamp nowStamp = UtilDateTime.nowTimestamp();

        //create new status entry, and set lastStatusUpdate date
        toBeStored.add(delegator.makeValue("WorkEffortStatus",
                UtilMisc.toMap("workEffortId", workEffortId, "statusId", context.get("currentStatusId"), "statusDatetime", nowStamp, "setByPartyId", userLogin.get("partyId"))));
        newWorkEffort.set("currentStatusId", context.get("currentStatusId"));
        newWorkEffort.set("lastStatusUpdate", nowStamp);

        newWorkEffort.set("workEffortId", workEffortId);
        newWorkEffort.set("workEffortTypeId", context.get("workEffortTypeId"), false);
        newWorkEffort.set("universalId", context.get("universalId"), false);
        newWorkEffort.set("scopeEnumId", context.get("scopeEnumId"), false);
        newWorkEffort.set("priority", context.get("priority"), false);
        newWorkEffort.set("workEffortName", context.get("workEffortName"), false);
        newWorkEffort.set("description", context.get("description"), false);
        newWorkEffort.set("locationDesc", context.get("locationDesc"), false);
        newWorkEffort.set("estimatedStartDate", context.get("estimatedStartDate"), false);
        newWorkEffort.set("estimatedCompletionDate", context.get("estimatedCompletionDate"), false);
        newWorkEffort.set("actualStartDate", context.get("actualStartDate"), false);
        newWorkEffort.set("actualCompletionDate", context.get("actualCompletionDate"), false);
        newWorkEffort.set("estimatedMilliSeconds", context.get("estimatedMilliSeconds"), false);
        newWorkEffort.set("actualMilliSeconds", context.get("actualMilliSeconds"), false);
        newWorkEffort.set("totalMilliSecondsAllowed", context.get("totalMilliSecondsAllowed"), false);
        newWorkEffort.set("totalMoneyAllowed", context.get("totalMoneyAllowed"), false);
        newWorkEffort.set("moneyUomId", context.get("moneyUomId"), false);
        newWorkEffort.set("specialTerms", context.get("specialTerms"), false);
        newWorkEffort.set("timeTransparency", context.get("timeTransparency"), false);
        newWorkEffort.set("infoUrl", context.get("infoUrl"), false);

        newWorkEffort.set("lastModifiedDate", nowStamp);
        if (userLogin.get("partyId") != null)
            newWorkEffort.set("lastModifiedByPartyId", userLogin.get("partyId"));

        newWorkEffort.set("revisionNumber", new Long(1));
        newWorkEffort.set("createdDate", nowStamp);
        if (userLogin.get("partyId") != null) {
            newWorkEffort.set("createdByPartyId", userLogin.get("partyId"));
            //add a party assignment for the creator of the event
            toBeStored.add(delegator.makeValue("WorkEffortPartyAssignment",
                    UtilMisc.toMap("workEffortId", workEffortId, "partyId", userLogin.get("partyId"), "roleTypeId", "CAL_OWNER", "fromDate", nowStamp, "statusId",
                    "CAL_ASN_ACCEPTED")));
        }

        try {
            delegator.storeAll(toBeStored);
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Could not create new WorkEffort (write error)");
            return result;
        }

        result.put("workEffortId", workEffortId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);

        return result;
    }

    /** Service that updates a WorkEffort entity
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map updateWorkEffort(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        //where is userLogin?? TODO
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        GenericValue workEffort = null;
        
        //get, and validate, the primary keys
        List errorMessageList = new LinkedList();
        String workEffortId = (String) context.get("workEffortId");
        if (!UtilValidate.isNotEmpty(workEffortId))
            errorMessageList.add("Work Effort ID missing.");
        if (errorMessageList.size() > 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE_LIST, errorMessageList);
            return result;
        }

        //do a findByPrimary key to see if the entity exists, and other things later
        try {
            workEffort = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }

        if (workEffort == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Could not find Work Effort with ID" + workEffortId + ", workEffort not updated.");
            return result;
        }
        
        if (!hasWorkEffortSecurity(workEffortId, userLogin, "_UPDATE", delegator, security)) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "You cannot update this Work Effort, you must either be associated with it or have administration permission.");
            return result;
        }

        GenericValue newWorkEffort = null;
        newWorkEffort = (GenericValue) workEffort.clone();
        Collection toBeStored = new LinkedList();
        toBeStored.add(newWorkEffort);
        Timestamp nowStamp = UtilDateTime.nowTimestamp();

        //if necessary create new status entry, and set lastStatusUpdate date
        String currentStatusId = (String) context.get("currentStatusId");
        if (currentStatusId != null && !currentStatusId.equals(workEffort.getString("currentStatusId"))) {
            toBeStored.add(delegator.makeValue("WorkEffortStatus",
                    UtilMisc.toMap("workEffortId", workEffortId, "statusId", currentStatusId, "statusDatetime", nowStamp, "setByPartyId", userLogin.get("partyId"))));
            newWorkEffort.set("currentStatusId", currentStatusId);
            newWorkEffort.set("lastStatusUpdate", nowStamp);
        }

        newWorkEffort.set("workEffortId", workEffortId);
        newWorkEffort.set("workEffortTypeId", context.get("workEffortTypeId"), false);
        newWorkEffort.set("universalId", context.get("universalId"), false);
        newWorkEffort.set("scopeEnumId", context.get("scopeEnumId"), false);
        newWorkEffort.set("priority", context.get("priority"), false);
        newWorkEffort.set("workEffortName", context.get("workEffortName"), false);
        newWorkEffort.set("description", context.get("description"), false);
        newWorkEffort.set("locationDesc", context.get("locationDesc"), false);
        newWorkEffort.set("estimatedStartDate", context.get("estimatedStartDate"), false);
        newWorkEffort.set("estimatedCompletionDate", context.get("estimatedCompletionDate"), false);
        newWorkEffort.set("actualStartDate", context.get("actualStartDate"), false);
        newWorkEffort.set("actualCompletionDate", context.get("actualCompletionDate"), false);
        newWorkEffort.set("estimatedMilliSeconds", context.get("estimatedMilliSeconds"), false);
        newWorkEffort.set("actualMilliSeconds", context.get("actualMilliSeconds"), false);
        newWorkEffort.set("totalMilliSecondsAllowed", context.get("totalMilliSecondsAllowed"), false);
        newWorkEffort.set("totalMoneyAllowed", context.get("totalMoneyAllowed"), false);
        newWorkEffort.set("moneyUomId", context.get("moneyUomId"), false);
        newWorkEffort.set("specialTerms", context.get("specialTerms"), false);
        newWorkEffort.set("timeTransparency", context.get("timeTransparency"), false);
        newWorkEffort.set("infoUrl", context.get("infoUrl"), false);

        //if nothing has changed, return
        if (workEffort != null && newWorkEffort.equals(workEffort)) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            result.put(ModelService.SUCCESS_MESSAGE, "No changes made, not saving.");
            return result;
        }

        //only set lastModifiedDate after comparing new & old to see if anything has changed
        newWorkEffort.set("lastModifiedDate", nowStamp);
        if (userLogin.get("partyId") != null)
            newWorkEffort.set("lastModifiedByPartyId", userLogin.get("partyId"));

        Long currentRev = newWorkEffort.getLong("revisionNumber");
        if (currentRev != null)
            newWorkEffort.set("revisionNumber", new Long(currentRev.longValue() + 1));
        else
            newWorkEffort.set("revisionNumber", new Long(1));

        try {
            delegator.storeAll(toBeStored);
        } catch (GenericEntityException e) {
            Debug.logWarning("[WorkEffortEvents.updateWorkEffort] Could not update WorkEffort (write error); message: " + e.getMessage());
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Could not update WorkEffort (write error)");
            return result;
        }
        
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Service that deletes a WorkEffort entity
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map deleteWorkEffort(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        List errorMessageList = new LinkedList();
        //where is userLogin?? TODO
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        GenericValue workEffort = null;
        
        //do a findByPrimary key to see if the entity exists, and other things later
        try {
            workEffort = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", context.get("workEffortId")));
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }

        if (workEffort == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Could not find Work Effort with ID" + context.get("workEffortId") + ", workEffort not deleted.");
            return result;
        }
        
        if (!hasWorkEffortSecurity((String) context.get("workEffortId"), userLogin, "_DELETE", delegator, security)) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "You cannot delete this Work Effort, you must either be associated with it or have administration permission.");
            return result;
        }

        try {
            TransactionUtil.begin();

            //Remove associated/dependent entries from other tables here
            workEffort.removeRelated("WorkEffortAttribute");
            workEffort.removeRelated("WorkEffortCategoryMember");
            workEffort.removeRelated("WorkEffortPartyAssignment");
            workEffort.removeRelated("FromWorkEffortAssoc");
            workEffort.removeRelated("ToWorkEffortAssoc");
            workEffort.removeRelated("WorkEffortStatus");
            workEffort.removeRelated("ContextRuntimeData");
            workEffort.removeRelated("ResultRuntimeData");
            workEffort.removeRelated("NoteData");
            workEffort.removeRelated("RecurrenceInfo");

            //Delete actual main entity last, just in case database is set up to do a cascading delete, caches won't get cleared
            workEffort.remove();

            TransactionUtil.commit();
        } catch (GenericEntityException e) {
            errorMessageList.add("Could not delete WorkEffort: " + e.getMessage());

            try {
                TransactionUtil.rollback();
            } catch(GenericEntityException e2) {
                errorMessageList.add("Could not rollback delete of WorkEffort: " + e2.getMessage());
            }
        }
        
        if (errorMessageList.size() > 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE_LIST, errorMessageList);
        } else {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }

        return result;
    }
    
    public static boolean hasWorkEffortSecurity(String workEffortId, GenericValue userLogin, String operation, GenericDelegator delegator, Security security) {
        //get a collection of workEffortPartyAssignments, if empty then this user CANNOT view the event, unless they have permission to view all
        Collection workEffortPartyAssignments = null;
        if (userLogin != null && userLogin.get("partyId") != null && workEffortId != null) {
            try {
                workEffortPartyAssignments = delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", workEffortId, "partyId", userLogin.get("partyId")));
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }
        }

        //check permissions before moving on:
        // 1) if create, no permission necessary
        // 2) if update or delete logged in user must be associated OR have the corresponding UPDATE or DELETE permissions
        boolean associatedWith = (workEffortPartyAssignments != null && workEffortPartyAssignments.size() > 0) ? true : false;
        if (!associatedWith && !security.hasEntityPermission("WORKEFFORTMGR", operation, userLogin)) {
            return false;
        }
        return true;
    }
}
