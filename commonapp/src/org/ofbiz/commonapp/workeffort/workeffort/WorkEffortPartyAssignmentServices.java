/*
 * $Id$
 *
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.commonapp.workeffort.workeffort;

import java.util.*;
import java.sql.Timestamp;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.service.*;

/**
 * WorkEffortPartyAssignmentServices - Services to handle form input and other data changes.
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version 1.0
 * Created on December 29, 2001
 */
public class WorkEffortPartyAssignmentServices {

    /**
     * Service that creates a WorkEffortPartyAssignment entity
     * @param ctx The DispatchContext that this service is operating in
     * @param context Map containing the input parameters
     * @return Map with the result of the service, the output parameters
     */
    public static Map assignPartyToWorkEffort(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        //where is userLogin?? TODO
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        Timestamp nowStamp = UtilDateTime.nowTimestamp();
        Timestamp fromDate = nowStamp;

        GenericValue newWorkEffortPartyAssignment = null;
        newWorkEffortPartyAssignment = delegator.makeValue("WorkEffortPartyAssignment", null);
        List toBeStored = new LinkedList();
        toBeStored.add(newWorkEffortPartyAssignment);

        newWorkEffortPartyAssignment.set("workEffortId", context.get("workEffortId"));
        newWorkEffortPartyAssignment.set("partyId", context.get("partyId"));
        newWorkEffortPartyAssignment.set("roleTypeId", context.get("roleTypeId"));
        newWorkEffortPartyAssignment.set("fromDate", fromDate);
        newWorkEffortPartyAssignment.set("thruDate", context.get("thruDate"), false);
        newWorkEffortPartyAssignment.set("facilityId", context.get("facilityId"), false);
        newWorkEffortPartyAssignment.set("comments", context.get("comments"), false);
        newWorkEffortPartyAssignment.set("mustRsvp", context.get("mustRsvp"), false);
        newWorkEffortPartyAssignment.set("expectationEnumId", context.get("expectationEnumId"), false);

        //if necessary create new status entry, and set statusDateTime date
        String statusId = (String) context.get("statusId");
        if (statusId != null) {
            //set the current status & timestamp
            newWorkEffortPartyAssignment.set("statusId", statusId);
            newWorkEffortPartyAssignment.set("statusDateTime", nowStamp);
            updateWorkflowEngine(newWorkEffortPartyAssignment, userLogin, ctx.getDispatcher());
        }

        try {
            delegator.storeAll(toBeStored);
        } catch (GenericEntityException e) {
            Debug.logWarning("[WorkEffortPartyAssignmentEvents.updateWorkEffortPartyAssignment] Could not create WorkEffortPartyAssignment (write error)");
            Debug.logWarning(e);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Could not create new WorkEffortPartyAssignment (write error)");
            return result;
        }

        result.put("fromDate", fromDate);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);

        return result;
    }

    /**
     * Service that updates a WorkEffortPartyAssignment entity
     * @param ctx The DispatchContext that this service is operating in
     * @param context Map containing the input parameters
     * @return Map with the result of the service, the output parameters
     */
    public static Map updatePartyToWorkEffortAssignment(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        //where is userLogin?? TODO
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        //do a findByPrimary key to see if the entity exists, and other things later
        GenericValue workEffortPartyAssignment = null;
        try {
            workEffortPartyAssignment = delegator.findByPrimaryKey("WorkEffortPartyAssignment",
                                                                   UtilMisc.toMap("workEffortId", context.get("workEffortId"), "partyId", context.get("partyId"),
                                                                                  "roleTypeId", context.get("roleTypeId"), "fromDate", context.get("fromDate")));
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }

        if (workEffortPartyAssignment == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Could not update this Work Effort Party Assignment, does not exist.");
            return result;
        }

        //check permissions before moving on:
        // 1) if create, no permission necessary
        // 2) if update or delete logged in user must be associated OR have the corresponding UPDATE or DELETE permissions
        boolean associatedWith = (userLogin.getString("partyId") != null && userLogin.getString("partyId").equals(workEffortPartyAssignment.getString("partyId")));
        if (!associatedWith && !security.hasEntityPermission("WORKEFFORTMGR", "_UPDATE", userLogin)) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "You cannot update this Work Effort Party Assignment, you must either be associated with it or have administration permission.");
            return result;
        }

        GenericValue newWorkEffortPartyAssignment = null;
        newWorkEffortPartyAssignment = (GenericValue) workEffortPartyAssignment.clone();
        List toBeStored = new LinkedList();
        toBeStored.add(newWorkEffortPartyAssignment);
        Timestamp nowStamp = UtilDateTime.nowTimestamp();

        newWorkEffortPartyAssignment.set("workEffortId", context.get("workEffortId"));
        newWorkEffortPartyAssignment.set("partyId", context.get("partyId"));
        newWorkEffortPartyAssignment.set("roleTypeId", context.get("roleTypeId"));
        newWorkEffortPartyAssignment.set("fromDate", context.get("fromDate"));
        newWorkEffortPartyAssignment.set("thruDate", context.get("thruDate"), false);
        newWorkEffortPartyAssignment.set("facilityId", context.get("facilityId"), false);
        newWorkEffortPartyAssignment.set("comments", context.get("comments"), false);
        newWorkEffortPartyAssignment.set("mustRsvp", context.get("mustRsvp"), false);
        newWorkEffortPartyAssignment.set("expectationEnumId", context.get("expectationEnumId"), false);

        //if necessary create new status entry, and set statusDateTime date
        String statusId = (String) context.get("statusId");
        if (statusId != null && !statusId.equals(workEffortPartyAssignment.getString("statusId"))) {
            //set the current status & timestamp
            newWorkEffortPartyAssignment.set("statusId", statusId);
            newWorkEffortPartyAssignment.set("statusDateTime", nowStamp);
            updateWorkflowEngine(newWorkEffortPartyAssignment, userLogin, ctx.getDispatcher());
        }

        //if nothing has changed, return
        if (workEffortPartyAssignment != null && newWorkEffortPartyAssignment.equals(workEffortPartyAssignment)) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            result.put(ModelService.SUCCESS_MESSAGE, "No changes made, not saving.");
            return result;
        }

        try {
            delegator.storeAll(toBeStored);
        } catch (GenericEntityException e) {
            Debug.logWarning("[WorkEffortPartyAssignmentEvents.updateWorkEffortPartyAssignment] Could not update WorkEffortPartyAssignment (write error)");
            Debug.logWarning(e);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Could not update WorkEffortPartyAssignment (write error)");
            return result;
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /**
     * Service that deletes a WorkEffortPartyAssignment entity
     * @param ctx The DispatchContext that this service is operating in
     * @param context Map containing the input parameters
     * @return Map with the result of the service, the output parameters
     */
    public static Map unassignPartyFromWorkEffort(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        List errorMessageList = new LinkedList();
        //where is userLogin?? TODO
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        GenericValue workEffort = null;

        //do a findByPrimary key to see if the entity exists, and other things later
        GenericValue workEffortPartyAssignment = null;
        try {
            workEffortPartyAssignment = delegator.findByPrimaryKey("WorkEffortPartyAssignment",
                                                                   UtilMisc.toMap("workEffortId", context.get("workEffortId"), "partyId", context.get("partyId"),
                                                                                  "roleTypeId", context.get("roleTypeId"), "fromDate", context.get("fromDate")));
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }

        if (workEffortPartyAssignment == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Could not delete this Work Effort Party Assignment, does not exist.");
            return result;
        }

        //check permissions before moving on:
        // 1) if create, no permission necessary
        // 2) if update or delete logged in user must be associated OR have the corresponding UPDATE or DELETE permissions
        boolean associatedWith = (userLogin.getString("partyId") != null && userLogin.getString("partyId").equals(workEffortPartyAssignment.getString("partyId")));
        if (!associatedWith && !security.hasEntityPermission("WORKEFFORTMGR", "_UPDATE", userLogin)) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "You cannot delete this Work Effort Party Assignment, you must either be associated with it or have administration permission.");
            return result;
        }

        //TODO: if WorkEffortPartyAssignment deleted, let the Workflow engine know...

        try {
            TransactionUtil.begin();

            //Remove associated/dependent entries from other tables here

            //Delete actual main entity last, just in case database is set up to do a cascading delete, caches won't get cleared
            workEffortPartyAssignment.remove();

            TransactionUtil.commit();
        } catch (GenericEntityException e) {
            errorMessageList.add("Could not delete WorkEffortPartyAssignment: " + e.getMessage());

            try {
                TransactionUtil.rollback();
            } catch (GenericEntityException e2) {
                errorMessageList.add("Could not rollback delete of WorkEffortPartyAssignment: " + e2.getMessage());
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

    public static void updateWorkflowEngine(GenericValue wepa, GenericValue userLogin, LocalDispatcher dispatcher) {
        //if the WorkEffort is an ACTIVITY, check for accept or complete new status...
        GenericDelegator delegator = wepa.getDelegator();
        GenericValue workEffort = null;
        try {
            workEffort = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", wepa.get("workEffortId")));
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
        if (workEffort != null && "ACTIVITY".equals(workEffort.getString("workEffortTypeId"))) {
            //TODO: restrict status transitions

            String statusId = (String) wepa.get("statusId");
            Map context = UtilMisc.toMap("workEffortId", wepa.get("workEffortId"), "partyId", wepa.get("partyId"),
                                         "roleTypeId", wepa.get("roleTypeId"), "fromDate", wepa.get("fromDate"),
                                         "userLogin", userLogin);

            if ("CAL_ACCEPTED".equals(statusId)) {
                //accept the activity assignment
                try {
                    Map results = dispatcher.runSync("wfAcceptAssignment", context);
                    if (results != null && results.get(ModelService.ERROR_MESSAGE) != null)
                        Debug.logWarning((String) results.get(ModelService.ERROR_MESSAGE));
                } catch (GenericServiceException e) {
                    Debug.logWarning(e);
                }
            } else if ("CAL_COMPLETED".equals(statusId)) {
                //complete the activity assignment
                try {
                    Map results = dispatcher.runSync("wfCompleteAssignment", context);
                    if (results != null && results.get(ModelService.ERROR_MESSAGE) != null)
                        Debug.logWarning((String) results.get(ModelService.ERROR_MESSAGE));
                } catch (GenericServiceException e) {
                    Debug.logWarning(e);
                }
            } else if ("CAL_DECLINED".equals(statusId)) {
                //decline the activity assignment
                try {
                    Map results = dispatcher.runSync("wfDeclineAssignment", context);
                    if (results != null && results.get(ModelService.ERROR_MESSAGE) != null)
                        Debug.logWarning((String) results.get(ModelService.ERROR_MESSAGE));
                } catch (GenericServiceException e) {
                    Debug.logWarning(e);
                }
            } else {
                //do nothing...
            }
        }
    }
}
