/*
 * $Id$
 * $Log$
 * Revision 1.9  2001/12/09 12:18:12  jonesde
 * Added some task/activity workers
 *
 * Revision 1.8  2001/11/14 13:51:37  jonesde
 * Fixed tryEntity bug on error
 *
 * Revision 1.7  2001/11/14 12:38:58  jonesde
 * Refactored upcoming work efforts worker to use UtilDateTime methods and use a period (day) based approach, much simpler
 *
 * Revision 1.6  2001/11/13 15:47:15  jonesde
 * Updated upcoming events, though one small todo left there
 *
 * Revision 1.5  2001/11/13 02:18:27  jonesde
 * Added some stuff, fixed problems
 *
 * Revision 1.4  2001/11/11 14:50:36  jonesde
 * Finished initial working versions of work effort workers and events
 *
 * Revision 1.3  2001/11/11 03:35:48  jonesde
 * Updated upcoming events worker to use view entity, left old code in as an example (of inefficiency...)
 *
 * Revision 1.2  2001/11/09 01:28:07  jonesde
 * More progress on event and workers, upcoming events worker mostly there
 *
 * Revision 1.1  2001/11/08 03:03:46  jonesde
 * Initial WorkEffort event and worker files, very little functionality in place so far
 *
 */
package org.ofbiz.commonapp.workeffort.workeffort;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.util.*;
import javax.servlet.jsp.*;
import java.sql.*;
import java.util.*;

/**
 * <p><b>Title:</b> WorkEffortWorker.java
 * <p><b>Description:</b> Worker class to reduce code in JSPs & make it more reusable
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
public class WorkEffortWorker {
    public static void getWorkEffort(PageContext pageContext, String workEffortIdAttrName, String workEffortAttrName, String partyAssignsAttrName,
            String canViewAttrName, String tryEntityAttrName, String currentStatusAttrName) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getServletContext().getAttribute("delegator");
        Security security = (Security) pageContext.getServletContext().getAttribute("security");
        GenericValue userLogin = (GenericValue) pageContext.getSession().getAttribute(SiteDefs.USER_LOGIN);

        String workEffortId = pageContext.getRequest().getParameter("WORK_EFFORT_ID");
        //if there was no parameter, check the request attribute, this may be a newly created entity
        if (workEffortId == null)
            workEffortId = (String) pageContext.getRequest().getAttribute("WORK_EFFORT_ID");

        GenericValue workEffort = null;
        try {
            workEffort = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }

        Boolean canView = null;
        Collection workEffortPartyAssignments = null;
        Boolean tryEntity = null;
        GenericValue currentStatus = null;
        if (workEffort == null) {
            tryEntity = new Boolean(false);
            canView = new Boolean(true);

            String statusId = pageContext.getRequest().getParameter("CURRENT_STATUS_ID");
            if (statusId != null && statusId.length() > 0) {
                try {
                    currentStatus = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", statusId));
                } catch (GenericEntityException e) {
                    Debug.logWarning(e);
                }
            }
        } else {
            //get a collection of workEffortPartyAssignments, if empty then this user CANNOT view the event, unless they have permission to view all
            if (userLogin != null && userLogin.get("partyId") != null && workEffortId != null) {
                try {
                    workEffortPartyAssignments =
                            delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", workEffortId, "partyId", userLogin.get("partyId")));
                } catch (GenericEntityException e) {
                    Debug.logWarning(e);
                }
            }
            canView = (workEffortPartyAssignments != null && workEffortPartyAssignments.size() > 0) ? new Boolean(true) : new Boolean(false);
            if (!canView.booleanValue() && security.hasEntityPermission("WORKEFFORTMGR", "_VIEW", pageContext.getSession())) {
                canView = new Boolean(true);
            }

            tryEntity = new Boolean(true);

            if (workEffort.get("currentStatusId") != null) {
                try {
                    currentStatus = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.get("currentStatusId")));
                } catch (GenericEntityException e) {
                    Debug.logWarning(e);
                }
            }
        }

        //if there was an error message, don't get values from entity
        if (pageContext.getRequest().getAttribute("ERROR_MESSAGE") != null || pageContext.getRequest().getAttribute(SiteDefs.ERROR_MESSAGE) != null) {
            tryEntity = new Boolean(false);
        }

        if (workEffortId != null)
            pageContext.setAttribute(workEffortIdAttrName, workEffortId);
        if (workEffort != null)
            pageContext.setAttribute(workEffortAttrName, workEffort);
        if (canView != null)
            pageContext.setAttribute(canViewAttrName, canView);
        if (workEffortPartyAssignments != null)
            pageContext.setAttribute(partyAssignsAttrName, workEffortPartyAssignments);
        if (tryEntity != null)
            pageContext.setAttribute(tryEntityAttrName, tryEntity);
        if (currentStatus != null)
            pageContext.setAttribute(currentStatusAttrName, currentStatus);
    }

    public static void getEventStatusItems(PageContext pageContext, String attributeName) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getServletContext().getAttribute("delegator");
        try {
            Collection statusItems = delegator.findByAndCache("StatusItem", UtilMisc.toMap("statusTypeId", "EVENT_STATUS"), UtilMisc.toList("sequenceId"));
            if (statusItems != null)
                pageContext.setAttribute(attributeName, statusItems);
        } catch (GenericEntityException e) {
            Debug.logError(e);
        }
    }

    public static void getTaskStatusItems(PageContext pageContext, String attributeName) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getServletContext().getAttribute("delegator");
        List statusItems = new LinkedList();
        try {
            Collection calItems = delegator.findByAndCache("StatusItem", UtilMisc.toMap("statusTypeId", "CALENDAR_STATUS"), UtilMisc.toList("sequenceId"));
            if (calItems != null)
                statusItems.addAll(calItems);
        } catch (GenericEntityException e) {
            Debug.logError(e);
        }
        try {
            Collection taskItems = delegator.findByAndCache("StatusItem", UtilMisc.toMap("statusTypeId", "TASK_STATUS"), UtilMisc.toList("sequenceId"));
            if (taskItems != null)
                statusItems.addAll(taskItems);
        } catch (GenericEntityException e) {
            Debug.logError(e);
        }
        
        pageContext.setAttribute(attributeName, statusItems);
    }

    public static void getActivityStatusItems(PageContext pageContext, String attributeName) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getServletContext().getAttribute("delegator");
        try {
            Collection statusItems = delegator.findByAndCache("StatusItem", UtilMisc.toMap("statusTypeId", "WORKFLOW_STATUS"), UtilMisc.toList("sequenceId"));
            if (statusItems != null)
                pageContext.setAttribute(attributeName, statusItems);
        } catch (GenericEntityException e) {
            Debug.logError(e);
        }
    }

    public static void getMonthWorkEffortEvents(PageContext pageContext, String attributeName) {
    }

    public static void getWorkEffortEventsByDays(PageContext pageContext, String daysAttrName, Timestamp startDay, int numDays) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getServletContext().getAttribute("delegator");
        GenericValue userLogin = (GenericValue) pageContext.getSession().getAttribute(SiteDefs.USER_LOGIN);

        //get a timestamp (date) for the beginning of today and for beginning of numDays+1 days from now
        Timestamp startStamp = UtilDateTime.getDayStart(startDay);
        Timestamp endStamp = UtilDateTime.getDayStart(startDay, numDays + 1);

        //Get the WorkEfforts
        List validWorkEfforts = null;
        if (false) {
            //The NON view entity approach:
            Collection workEffortPartyAssignments = null;
            if (userLogin != null && userLogin.get("partyId") != null) {
                try {
                    workEffortPartyAssignments = delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("partyId", userLogin.get("partyId")));
                } catch (GenericEntityException e) {
                    Debug.logWarning(e);
                }
            }
            //filter the work effort - this should really be done in a join/view entity
            validWorkEfforts = new Vector();
            Iterator iter = UtilMisc.toIterator(workEffortPartyAssignments);
            while (iter != null && iter.hasNext()) {
                GenericValue workEffortPartyAssignment = (GenericValue) iter.next();
                GenericValue workEffort = null;
                try {
                    workEffort = workEffortPartyAssignment.getRelatedOne("WorkEffort");
                } catch (GenericEntityException e) {
                    Debug.logWarning(e);
                }
                if (workEffort == null)
                    continue;

                Timestamp estimatedStartDate = workEffort.getTimestamp("estimatedStartDate");

                if (estimatedStartDate == null)
                    continue;
                if (estimatedStartDate.before(startStamp))
                    continue;
                if (estimatedStartDate.after(endStamp))
                    continue;
                if (!"EVENT".equals(workEffort.getString("workEffortTypeId")))
                    continue;

                validWorkEfforts.add(workEffort);
            }

            //order the filtered list by the start date
            validWorkEfforts = EntityUtil.orderBy(validWorkEfforts, UtilMisc.toList("estimatedStartDate"));
        }
        else {
            //Use the View Entity
            if (userLogin != null && userLogin.get("partyId") != null) {
                try {
                    validWorkEfforts = new Vector( delegator.findByAnd("WorkEffortAndPartyAssign",
                            UtilMisc.toList(new EntityExpr("partyId", EntityOperator.EQUALS, userLogin.get("partyId")),
                            new EntityExpr("estimatedCompletionDate", EntityOperator.GREATER_THAN_EQUAL_TO, startStamp),
                            new EntityExpr("estimatedStartDate", EntityOperator.LESS_THAN, endStamp),
                            new EntityExpr("workEffortTypeId", EntityOperator.EQUALS, "EVENT")), UtilMisc.toList("estimatedStartDate")));
                } catch (GenericEntityException e) {
                    Debug.logWarning(e);
                }
            }
        }
        if (validWorkEfforts == null || validWorkEfforts.size() <= 0)
            return;

        //Split the WorkEffort list into a list for each day
        List days = new Vector();

        //For each day in the set we check all work efforts to see if they fall within range
        for (int i = 0; i < numDays; i++) {
            Timestamp curDayStart = UtilDateTime.getDayStart(startStamp, i);
            Timestamp curDayEnd = UtilDateTime.getDayEnd(startStamp, i);
            List curWorkEfforts = new Vector();

            for (int j = 0; j < validWorkEfforts.size(); j++) {
                GenericValue workEffort = (GenericValue) validWorkEfforts.get(j);
                //Debug.log("Got workEffort: " + workEffort.toString());

                Timestamp estimatedStartDate = workEffort.getTimestamp("estimatedStartDate");
                Timestamp estimatedCompletionDate = workEffort.getTimestamp("estimatedCompletionDate");
                if (estimatedStartDate == null || estimatedCompletionDate == null)
                    continue;

                if (estimatedStartDate.before(curDayEnd) && estimatedCompletionDate.after(curDayStart)) {
                    curWorkEfforts.add(workEffort);
                }

                //if startDate is after dayEnd, continue to the next day, we haven't gotten to this one yet...
                if (estimatedStartDate.after(curDayEnd))
                    break;

                //if completionDate is before the dayEnd, remove from list, we are done with it
                if (estimatedCompletionDate.before(curDayEnd)) {
                    validWorkEfforts.remove(j);
                    j--;
                }
            }
            if (curWorkEfforts.size() > 0)
                days.add(curWorkEfforts);
        }

        pageContext.setAttribute(daysAttrName, days);
    }

    public static void getWorkEffortAssignedActivities(PageContext pageContext, String activitiesAttrName) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getServletContext().getAttribute("delegator");
        GenericValue userLogin = (GenericValue) pageContext.getSession().getAttribute(SiteDefs.USER_LOGIN);

        Collection validWorkEfforts = null;
        if (userLogin != null && userLogin.get("partyId") != null) {
            try {
                List constraints = new LinkedList();
                constraints.add(new EntityExpr("partyId", EntityOperator.EQUALS, userLogin.get("partyId")));
                constraints.add(new EntityExpr("workEffortTypeId", EntityOperator.EQUALS, "ACTIVITY"));
                constraints.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "WF_DECLINED"));
                constraints.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "WF_DELEGATED"));
                constraints.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "WF_COMPLETED"));
                constraints.add(new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "WF_COMPLETED"));
                constraints.add(new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "WF_TERMINATED"));
                constraints.add(new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "WF_ABORTED"));
                validWorkEfforts = delegator.findByAnd("WorkEffortAndPartyAssign", constraints, UtilMisc.toList("priority"));
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }
        }
        if (validWorkEfforts == null || validWorkEfforts.size() <= 0)
            return;

        pageContext.setAttribute(activitiesAttrName, validWorkEfforts);
    }

    public static void getWorkEffortAssignedTasks(PageContext pageContext, String tasksAttrName) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getServletContext().getAttribute("delegator");
        GenericValue userLogin = (GenericValue) pageContext.getSession().getAttribute(SiteDefs.USER_LOGIN);

        Collection validWorkEfforts = null;
        if (userLogin != null && userLogin.get("partyId") != null) {
            try {
                validWorkEfforts = delegator.findByAnd("WorkEffortAndPartyAssign", 
                        UtilMisc.toList(new EntityExpr("partyId", EntityOperator.EQUALS, userLogin.get("partyId")),
                        new EntityExpr("workEffortTypeId", EntityOperator.EQUALS, "TASK"),
                        new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "WF_DECLINED"),
                        new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "WF_DELEGATED"),
                        new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "WF_COMPLETED")),
                        UtilMisc.toList("priority"));
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }
        }
        if (validWorkEfforts == null || validWorkEfforts.size() <= 0)
            return;

        pageContext.setAttribute(tasksAttrName, validWorkEfforts);
    }
}

