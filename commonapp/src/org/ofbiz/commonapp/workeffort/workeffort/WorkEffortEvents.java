/*
 * $Id$
 * $Log$
 * Revision 1.7  2001/12/23 06:29:42  jonesde
 * Replaced preStoreOther stuff with storeAll
 *
 * Revision 1.6  2001/12/16 13:08:31  jonesde
 * Finished first pass of party assignment stuff
 *
 * Revision 1.5  2001/11/13 02:18:27  jonesde
 * Added some stuff, fixed problems
 *
 * Revision 1.4  2001/11/12 23:49:19  jonesde
 * Fixed small logic bug on checking to see if anything had changed
 *
 * Revision 1.3  2001/11/11 14:50:36  jonesde
 * Finished initial working versions of work effort workers and events
 *
 * Revision 1.2  2001/11/09 01:28:07  jonesde
 * More progress on event and workers, upcoming events worker mostly there
 *
 * Revision 1.1  2001/11/08 03:03:46  jonesde
 * Initial WorkEffort event and worker files, very little functionality in place so far
 *
 */
package org.ofbiz.commonapp.workeffort.workeffort;

import javax.servlet.http.*;
import javax.servlet.*;
import java.util.*;
import java.sql.Timestamp;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.service.*;

/**
 * <p><b>Title:</b> WorkEffortEvents.java
 * <p><b>Description:</b> Events to handle form input and other data changes
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
public class WorkEffortEvents {
    /** Updates WorkEffort information according to UPDATE_MODE parameter
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String updateWorkEffort(HttpServletRequest request, HttpServletResponse response) {
        String errMsg = "";
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");

        GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        if (userLogin == null) {
            request.setAttribute("ERROR_MESSAGE", "You must be logged in to update a Work Effort.");
            return "error";
        }

        String updateMode = request.getParameter("UPDATE_MODE");
        if (updateMode == null || updateMode.length() <= 0) {
            request.setAttribute("ERROR_MESSAGE", "Update Mode was not specified, but is required.");
            Debug.logWarning("[WorkEffortEvents.updateWorkEffort] Update Mode was not specified, but is required");
            return "error";
        }

        String workEffortId = null;
        GenericValue workEffort = null;
        if ("CREATE".equals(updateMode)) {
            Long nextSeqId = delegator.getNextSeqId("WorkEffort");
            if (nextSeqId == null) {
                request.setAttribute("ERROR_MESSAGE", "Could not get an Id for a new WorkEffort (NextSeqIdError)");
                return "error";
            } else {
                workEffortId = nextSeqId.toString();
            }
        } else {
            //get, and validate, the primary keys
            workEffortId = request.getParameter("WORK_EFFORT_ID");
            if (!UtilValidate.isNotEmpty(workEffortId))
                errMsg += "<li>Work Effort ID missing.";
            if (errMsg.length() > 0) {
                errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
                request.setAttribute("ERROR_MESSAGE", errMsg);
                return "error";
            }

            //do a findByPrimary key to see if the entity exists, and other things later
            try {
                workEffort = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId));
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }

            //get a collection of workEffortPartyAssignments, if empty then this user CANNOT view the event, unless they have permission to view all
            Collection workEffortPartyAssignments = null;
            if (userLogin != null && userLogin.get("partyId") != null && workEffortId != null) {
                try {
                    workEffortPartyAssignments =
                            delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", workEffortId, "partyId", userLogin.get("partyId")));
                } catch (GenericEntityException e) {
                    Debug.logWarning(e);
                }
            }

            //check permissions before moving on:
            // 1) if create, no permission necessary
            // 2) if update or delete logged in user must be associated OR have the corresponding UPDATE or DELETE permissions
            boolean associatedWith = (workEffortPartyAssignments != null && workEffortPartyAssignments.size() > 0) ? true : false;
            if (!associatedWith && !security.hasEntityPermission("WORKEFFORTMGR", "_" + updateMode, request.getSession())) {
                request.setAttribute("ERROR_MESSAGE", "You cannot update or delete this Work Effort, you must either be associated with it or have administration permission.");
                return "error";
            }
        }

        //if this is a delete, do that before getting all of the non-pk parameters and validating them
        if (updateMode.equals("DELETE")) {
            if (workEffort != null) {
                //NOTE: this is pretty weak for handling removal in clean way; what we really need
                // is the upcoming generic transaction token stuff in the Entity Engine

                //Remove associated/dependent entries from other tables here
                try {
                    workEffort.removeRelated("WorkEffortAttribute");
                } catch (GenericEntityException e) {
                    errMsg += e.getMessage();
                }
                try {
                    workEffort.removeRelated("WorkEffortCategoryMember");
                } catch (GenericEntityException e) {
                    errMsg += e.getMessage();
                }
                try {
                    workEffort.removeRelated("WorkEffortPartyAssignment");
                } catch (GenericEntityException e) {
                    errMsg += e.getMessage();
                }
                try {
                    workEffort.removeRelated("FromWorkEffortAssoc");
                } catch (GenericEntityException e) {
                    errMsg += e.getMessage();
                }
                try {
                    workEffort.removeRelated("ToWorkEffortAssoc");
                } catch (GenericEntityException e) {
                    errMsg += e.getMessage();
                }
                try {
                    workEffort.removeRelated("WorkEffortStatus");
                } catch (GenericEntityException e) {
                    errMsg += e.getMessage();
                }
                try {
                    workEffort.removeRelated("ContextRuntimeData");
                } catch (GenericEntityException e) {
                    errMsg += e.getMessage();
                }
                try {
                    workEffort.removeRelated("ResultRuntimeData");
                } catch (GenericEntityException e) {
                    errMsg += e.getMessage();
                }
                try {
                    workEffort.removeRelated("NoteData");
                } catch (GenericEntityException e) {
                    errMsg += e.getMessage();
                }
                try {
                    workEffort.removeRelated("RecurrenceInfo");
                } catch (GenericEntityException e) {
                    errMsg += e.getMessage();
                }
                //Delete actual main entity last, just in case database is set up to do a cascading delete, caches won't get cleared
                try {
                    workEffort.remove();
                } catch (GenericEntityException e) {
                    errMsg += e.getMessage();
                }
                if (errMsg.length() > 0) {
                    errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
                    request.setAttribute("ERROR_MESSAGE", errMsg);
                    return "error";
                } else {
                    return "success";
                }
            } else {
                request.setAttribute("ERROR_MESSAGE", "Could not find Work Effort with ID" + workEffortId + ", workEffort not deleted.");
                return "error";
            }
        }

        String workEffortTypeId = request.getParameter("WORK_EFFORT_TYPE_ID");
        String currentStatusId = request.getParameter("CURRENT_STATUS_ID");

        String universalId = request.getParameter("UNIVERSAL_ID");
        String scopeEnumId = request.getParameter("SCOPE_ENUM_ID");
        String priorityStr = request.getParameter("PRIORITY");
        Long priority = null;
        String workEffortName = request.getParameter("WORK_EFFORT_NAME");
        String description = request.getParameter("DESCRIPTION");
        String locationDesc = request.getParameter("LOCATION_DESC");

        String estimatedStartDateStr = request.getParameter("ESTIMATED_START_DATE");
        java.sql.Timestamp estimatedStartDate = null;
        String estimatedCompletionDateStr = request.getParameter("ESTIMATED_COMPLETION_DATE");
        java.sql.Timestamp estimatedCompletionDate = null;

        String actualStartDateStr = request.getParameter("ACTUAL_START_DATE");
        java.sql.Timestamp actualStartDate = null;
        String actualCompletionDateStr = request.getParameter("ACTUAL_COMPLETION_DATE");
        java.sql.Timestamp actualCompletionDate = null;

        String estimatedMilliSecondsStr = request.getParameter("ESTIMATED_MILLI_SECONDS");
        Double estimatedMilliSeconds = null;
        String actualMilliSecondsStr = request.getParameter("ACTUAL_MILLI_SECONDS");
        Double actualMilliSeconds = null;
        String totalMilliSecondsAllowedStr = request.getParameter("TOTAL_MILLI_SECONDS_ALLOWED");
        Double totalMilliSecondsAllowed = null;

        String totalMoneyAllowedStr = request.getParameter("TOTAL_MONEY_ALLOWED");
        Double totalMoneyAllowed = null;
        String moneyUomId = request.getParameter("MONEY_UOM_ID");

        String specialTerms = request.getParameter("SPECIAL_TERMS");
        String timeTransparencyStr = request.getParameter("TIME_TRANSPARENCY");
        Long timeTransparency = null;
        String infoUrl = request.getParameter("INFO_URL");


        if (UtilValidate.isNotEmpty(priorityStr)) {
            try {
                priority = Long.valueOf(priorityStr);
            } catch (Exception e) {
                errMsg += "<li>Priority is not a valid whole number.";
            }
        }

        if (UtilValidate.isNotEmpty(estimatedStartDateStr)) {
            try {
                estimatedStartDate = Timestamp.valueOf(estimatedStartDateStr);
            } catch (Exception e) {
                errMsg += "<li>Estimated Start Date is not a valid Date-Time.";
            }
        }
        if (UtilValidate.isNotEmpty(estimatedCompletionDateStr)) {
            try {
                estimatedCompletionDate = Timestamp.valueOf(estimatedCompletionDateStr);
            } catch (Exception e) {
                errMsg += "<li>Estimated Completion Date is not a valid Date-Time.";
            }
        }

        if (UtilValidate.isNotEmpty(actualStartDateStr)) {
            try {
                actualStartDate = Timestamp.valueOf(actualStartDateStr);
            } catch (Exception e) {
                errMsg += "<li>Actual Start Date is not a valid Date-Time.";
            }
        }
        if (UtilValidate.isNotEmpty(actualCompletionDateStr)) {
            try {
                actualCompletionDate = Timestamp.valueOf(actualCompletionDateStr);
            } catch (Exception e) {
                errMsg += "<li>Actual Completion Date is not a valid Date-Time.";
            }
        }

        if (UtilValidate.isNotEmpty(estimatedMilliSecondsStr)) {
            try {
                estimatedMilliSeconds = Double.valueOf(estimatedMilliSecondsStr);
            } catch (Exception e) {
                errMsg += "<li>Estimated Milli-seconds is not a valid number.";
            }
        }
        if (UtilValidate.isNotEmpty(actualMilliSecondsStr)) {
            try {
                actualMilliSeconds = Double.valueOf(actualMilliSecondsStr);
            } catch (Exception e) {
                errMsg += "<li>Actual Milli-seconds is not a valid number.";
            }
        }
        if (UtilValidate.isNotEmpty(totalMilliSecondsAllowedStr)) {
            try {
                totalMilliSecondsAllowed = Double.valueOf(totalMilliSecondsAllowedStr);
            } catch (Exception e) {
                errMsg += "<li>Total Milli-seconds Allows is not a valid number.";
            }
        }

        if (UtilValidate.isNotEmpty(totalMoneyAllowedStr)) {
            try {
                totalMoneyAllowed = Double.valueOf(totalMoneyAllowedStr);
            } catch (Exception e) {
                errMsg += "<li>Total Money Allowed is not a valid number.";
            }
        }
        if (UtilValidate.isNotEmpty(timeTransparencyStr)) {
            try {
                timeTransparency = Long.valueOf(timeTransparencyStr);
            } catch (Exception e) {
                errMsg += "<li>Time Transparency is not a valid whole number.";
            }
        }

        if (!UtilValidate.isNotEmpty(workEffortName))
            errMsg += "<li>Name is missing.";
        if (!UtilValidate.isNotEmpty(currentStatusId))
            errMsg += "<li>Status is missing.";
        if (estimatedStartDate != null && estimatedCompletionDate != null && estimatedStartDate.after(estimatedCompletionDate)) {
            errMsg += "<li>Start date/time cannot be after end date/time.";
        }
        if (errMsg.length() > 0) {
            errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
            request.setAttribute("ERROR_MESSAGE", errMsg);
            return "error";
        }

        //done validating, now go about setting values and storing them...
        GenericValue newWorkEffort = null;
        if (workEffort != null)
            newWorkEffort = (GenericValue) workEffort.clone();
        else
            newWorkEffort = delegator.makeValue("WorkEffort", null);
        Collection toBeStored = new LinkedList();
        toBeStored.add(newWorkEffort);
        Timestamp nowStamp = UtilDateTime.nowTimestamp();

        //if necessary create new status entry, and set lastStatusUpdate date
        if (workEffort == null || (currentStatusId != null && !currentStatusId.equals(workEffort.getString("currentStatusId")))) {
            toBeStored.add(delegator.makeValue("WorkEffortStatus",
                    UtilMisc.toMap("workEffortId", workEffortId, "statusId", currentStatusId, "statusDatetime", nowStamp, "setByPartyId", userLogin.get("partyId"))));
            newWorkEffort.set("currentStatusId", currentStatusId);
            newWorkEffort.set("lastStatusUpdate", nowStamp);
        }

        newWorkEffort.set("workEffortId", workEffortId);
        newWorkEffort.set("workEffortTypeId", workEffortTypeId, false);
        newWorkEffort.set("universalId", universalId, false);
        newWorkEffort.set("scopeEnumId", scopeEnumId, false);
        newWorkEffort.set("priority", priority, false);
        newWorkEffort.set("workEffortName", workEffortName, false);
        newWorkEffort.set("description", description, false);
        newWorkEffort.set("locationDesc", locationDesc, false);
        newWorkEffort.set("estimatedStartDate", estimatedStartDate, false);
        newWorkEffort.set("estimatedCompletionDate", estimatedCompletionDate, false);
        newWorkEffort.set("actualStartDate", actualStartDate, false);
        newWorkEffort.set("actualCompletionDate", actualCompletionDate, false);
        newWorkEffort.set("estimatedMilliSeconds", estimatedMilliSeconds, false);
        newWorkEffort.set("actualMilliSeconds", actualMilliSeconds, false);
        newWorkEffort.set("totalMilliSecondsAllowed", totalMilliSecondsAllowed, false);
        newWorkEffort.set("totalMoneyAllowed", totalMoneyAllowed, false);
        newWorkEffort.set("moneyUomId", moneyUomId, false);
        newWorkEffort.set("specialTerms", specialTerms, false);
        newWorkEffort.set("timeTransparency", timeTransparency, false);
        newWorkEffort.set("infoUrl", infoUrl, false);

        //if nothing has changed and we are updating, return
        if ("UPDATE".equals(updateMode) && workEffort != null && newWorkEffort.equals(workEffort)) {
            request.setAttribute("EVENT_MESSAGE", "No changes made, not saving.");
            return "success";
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

        if (updateMode.equals("CREATE")) {
            newWorkEffort.set("createdDate", nowStamp);
            if (userLogin.get("partyId") != null) {
                newWorkEffort.set("createdByPartyId", userLogin.get("partyId"));
                //add a party assignment for the creator of the event
                toBeStored.add(delegator.makeValue("WorkEffortPartyAssignment",
                        UtilMisc.toMap("workEffortId", workEffortId, "partyId", userLogin.get("partyId"), "roleTypeId", "CAL_OWNER", "fromDate", nowStamp, "statusId",
                        "CAL_ASN_ACCEPTED")));
            }

            GenericValue createWorkEffort = null;
            try {
                delegator.storeAll(toBeStored);
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
                request.setAttribute("ERROR_MESSAGE", "Could not create new WorkEffort (write error)");
                return "error";
            }
            request.setAttribute("WORK_EFFORT_ID", workEffortId);
        } else if (updateMode.equals("UPDATE")) {
            try {
                delegator.storeAll(toBeStored);
            } catch (GenericEntityException e) {
                request.setAttribute("ERROR_MESSAGE", "Could not update WorkEffort (write error)");
                Debug.logWarning("[WorkEffortEvents.updateWorkEffort] Could not update WorkEffort (write error); message: " + e.getMessage());
                return "error";
            }
        } else {
            request.setAttribute("ERROR_MESSAGE", "Specified update mode: \"" + updateMode + "\" is not supported.");
            return "error";
        }

        return "success";
    }

    /** Updates WorkEffortPartyAssignment information according to UPDATE_MODE parameter
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String updateWorkEffortPartyAssignment(HttpServletRequest request, HttpServletResponse response) {
        String errMsg = "";
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getSession().getServletContext().getAttribute("dispatcher");
        Timestamp nowStamp = UtilDateTime.nowTimestamp();

        GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        if (userLogin == null) {
            request.setAttribute("ERROR_MESSAGE", "You must be logged in to update a Work Effort.");
            return "error";
        }

        String updateMode = request.getParameter("UPDATE_MODE");
        if (updateMode == null || updateMode.length() <= 0) {
            request.setAttribute("ERROR_MESSAGE", "Update Mode was not specified, but is required.");
            Debug.logWarning("[WorkEffortEvents.updateWorkEffort] Update Mode was not specified, but is required");
            return "error";
        }

        String workEffortId = request.getParameter("workEffortId");
        String partyId = request.getParameter("partyId");
        String roleTypeId = request.getParameter("roleTypeId");
        String fromDateStr = request.getParameter("fromDate");
        Timestamp fromDate = null;
        GenericValue workEffortPartyAssignment = null;

        //get, and validate, the primary keys
        if (UtilValidate.isNotEmpty(fromDateStr)) {
            try {
                fromDate = Timestamp.valueOf(fromDateStr);
            } catch (Exception e) {
                errMsg += "<li>From Date is not a valid Date-Time.";
            }
        }

        if (!UtilValidate.isNotEmpty(workEffortId))
            errMsg += "<li>Work Effort ID missing.";
        if (!UtilValidate.isNotEmpty(partyId))
            errMsg += "<li>Party ID missing.";
        if (!UtilValidate.isNotEmpty(roleTypeId))
            errMsg += "<li>Role Type ID missing.";
            
        if ("CREATE".equals(updateMode)) {
            //if no fromDate specified, use nowStamp
            if (fromDate == null)
                fromDate = nowStamp;
        } else {
            if (!UtilValidate.isNotEmpty(fromDateStr))
                errMsg += "<li>From Date missing.";
        }
        
        if (errMsg.length() > 0) {
            errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
            request.setAttribute("ERROR_MESSAGE", errMsg);
            return "error";
        }

        //do a findByPrimary key to see if the entity exists, and other things later
        try {
            workEffortPartyAssignment = delegator.findByPrimaryKey("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", workEffortId, "partyId", partyId, "roleTypeId", roleTypeId, "fromDate", fromDate));
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }

        if ("CREATE".equals(updateMode)) {
            if (workEffortPartyAssignment != null) {
                request.setAttribute("ERROR_MESSAGE", "You cannot create this Work Effort Party Assignment, already exists.");
                return "error";
            }
        } else {
            if (workEffortPartyAssignment == null) {
                request.setAttribute("ERROR_MESSAGE", "You cannot update or delete this Work Effort Party Assignment, does not exist.");
                return "error";
            }
            
            //check permissions before moving on:
            // 1) if create, no permission necessary
            // 2) if update or delete logged in user must be associated OR have the corresponding UPDATE or DELETE permissions
            boolean associatedWith = (userLogin.getString("partyId") != null && userLogin.getString("partyId").equals(workEffortPartyAssignment.getString("partyId"))) ? true : false;
            if (!associatedWith && !security.hasEntityPermission("WORKEFFORTMGR", "_" + updateMode, request.getSession())) {
                request.setAttribute("ERROR_MESSAGE", "You cannot update or delete this Work Effort Party Assignment, you must either be associated with it or have administration permission.");
                return "error";
            }
        }

        //if this is a delete, do that before getting all of the non-pk parameters and validating them
        if (updateMode.equals("DELETE")) {
            //NOTE: this is pretty weak for handling removal in clean way; what we really need
            // is the upcoming generic transaction token stuff in the Entity Engine

            //Remove associated/dependent entries from other tables here

            //Delete actual main entity last, just in case database is set up to do a cascading delete, caches won't get cleared
            try {
                workEffortPartyAssignment.remove();
            } catch (GenericEntityException e) {
                errMsg += e.getMessage();
            }
            if (errMsg.length() > 0) {
                errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
                request.setAttribute("ERROR_MESSAGE", errMsg);
                return "error";
            } else {
                return "success";
            }
        }

        String thruDateStr = request.getParameter("thruDate");
        Timestamp thruDate = null;
        String facilityId = request.getParameter("facilityId");
        String statusId = request.getParameter("statusId");
        String comments = request.getParameter("comments");
        String mustRsvp = request.getParameter("mustRsvp");
        String expectationEnumId = request.getParameter("expectationEnumId");

        if (UtilValidate.isNotEmpty(thruDateStr)) {
            try {
                thruDate = Timestamp.valueOf(thruDateStr);
            } catch (Exception e) {
                errMsg += "<li>Thru Date is not a valid Date-Time.";
            }
        }
        
        if (errMsg.length() > 0) {
            errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
            request.setAttribute("ERROR_MESSAGE", errMsg);
            return "error";
        }

        //done validating, now go about setting values and storing them...
        GenericValue newWorkEffortPartyAssignment = null;
        if (workEffortPartyAssignment != null)
            newWorkEffortPartyAssignment = (GenericValue) workEffortPartyAssignment.clone();
        else
            newWorkEffortPartyAssignment = delegator.makeValue("WorkEffortPartyAssignment", null);

        //if necessary create new status entry, and set lastStatusUpdate date
        if (workEffortPartyAssignment == null || (statusId != null && !statusId.equals(workEffortPartyAssignment.getString("statusId")))) {
            //set the current status & timestamp
            newWorkEffortPartyAssignment.set("statusId", statusId);
            newWorkEffortPartyAssignment.set("statusDateTime", nowStamp);
            
            //if the WorkEffort is an ACTIVITY, check for accept or complete new status...
            GenericValue workEffort = null;
            try {
                workEffort = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId));
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }
            if (workEffort != null && "ACTIVITY".equals(workEffort.getString("workEffortTypeId"))) {
                //TODO: restrict status transitions
                
                Map context = UtilMisc.toMap("workEffortId", workEffortId, "partyId", partyId, "roleTypeId", roleTypeId, "fromDate", fromDate);
                if ("CAL_ACCEPTED".equals(statusId)) {
                    //accept the activity assignment
                    try {
                        Map results = dispatcher.runSync("acceptAssignment", context);
                    } catch (GenericServiceException e) {
                        Debug.logWarning(e);
                    }
                } else if ("CAL_COMPLETED".equals(statusId)) {
                    //complete the activity assignment
                    try {
                        Map results = dispatcher.runSync("completeAssignment", context);
                    } catch (GenericServiceException e) {
                        Debug.logWarning(e);
                    }
                } else if ("CAL_DECLINED".equals(statusId)) {
                    //decline the activity assignment
                    try {
                        Map results = dispatcher.runSync("declineAssignment", context);
                    } catch (GenericServiceException e) {
                        Debug.logWarning(e);
                    }
                } else {
                    //do nothing...
                }
            }
        }

        newWorkEffortPartyAssignment.set("workEffortId", workEffortId);
        newWorkEffortPartyAssignment.set("partyId", partyId);
        newWorkEffortPartyAssignment.set("roleTypeId", roleTypeId);
        newWorkEffortPartyAssignment.set("fromDate", fromDate);
        newWorkEffortPartyAssignment.set("thruDate", thruDate, false);
        newWorkEffortPartyAssignment.set("facilityId", facilityId, false);
        newWorkEffortPartyAssignment.set("comments", comments, false);
        newWorkEffortPartyAssignment.set("mustRsvp", mustRsvp, false);
        newWorkEffortPartyAssignment.set("expectationEnumId", expectationEnumId, false);
        
        if (updateMode.equals("CREATE")) {
            GenericValue createWorkEffortPartyAssignment = null;
            try {
                createWorkEffortPartyAssignment = delegator.create(newWorkEffortPartyAssignment);
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                createWorkEffortPartyAssignment = null;
            }
            if (createWorkEffortPartyAssignment == null) {
                request.setAttribute("ERROR_MESSAGE", "Could not create new WorkEffortPartyAssignment (write error)");
                return "error";
            }
            request.setAttribute("fromDate", fromDate);
        } else if (updateMode.equals("UPDATE")) {
            try {
                newWorkEffortPartyAssignment.store();
            } catch (GenericEntityException e) {
                request.setAttribute("ERROR_MESSAGE", "Could not update WorkEffortPartyAssignment (write error)");
                Debug.logWarning("[WorkEffortEvents.updateWorkEffortPartyAssignment] Could not update WorkEffortPartyAssignment (write error); message: " + e.getMessage());
                return "error";
            }
        } else {
            request.setAttribute("ERROR_MESSAGE", "Specified update mode: \"" + updateMode + "\" is not supported.");
            return "error";
        }
        
        return "success";
    }
}

