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

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * WorkEffortServices - WorkEffort related Services
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class WorkEffortServices {

    public static Map getWorkEffortAssignedTasks(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        List validWorkEfforts = null;

        if (userLogin != null && userLogin.get("partyId") != null) {
            try {
                validWorkEfforts = delegator.findByAnd("WorkEffortAndPartyAssign",
                            UtilMisc.toList(new EntityExpr("partyId", EntityOperator.EQUALS, userLogin.get("partyId")),
                                new EntityExpr("workEffortTypeId", EntityOperator.EQUALS, "TASK"),
                                new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "CAL_DECLINED"),
                                new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "CAL_DELEGATED"),
                                new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "CAL_COMPLETED"),
                                new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "CAL_CANCELLED")),
                            UtilMisc.toList("priority"));
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
                return ServiceUtil.returnError("Error finding desired WorkEffort records: " + e.toString());
            }
        }

        Map result = new HashMap();
        if (validWorkEfforts == null) validWorkEfforts = new LinkedList();
        result.put("tasks", validWorkEfforts);
        return result;
    }

    public static Map getWorkEffortAssignedActivities(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        List validWorkEfforts = null;

        if (userLogin != null && userLogin.get("partyId") != null) {
            try {
                List constraints = new LinkedList();

                constraints.add(new EntityExpr("partyId", EntityOperator.EQUALS, userLogin.get("partyId")));
                constraints.add(new EntityExpr("workEffortTypeId", EntityOperator.EQUALS, "ACTIVITY"));
                constraints.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_DECLINED"));
                constraints.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_DELEGATED"));
                constraints.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_COMPLETED"));
                constraints.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_CANCELLED"));
                constraints.add(new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "WF_COMPLETED"));
                constraints.add(new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "WF_TERMINATED"));
                constraints.add(new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "WF_ABORTED"));
                validWorkEfforts = delegator.findByAnd("WorkEffortAndPartyAssign", constraints, UtilMisc.toList("priority"));
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
                return ServiceUtil.returnError("Error finding desired WorkEffort records: " + e.toString());
            }
        }

        Map result = new HashMap();
        if (validWorkEfforts == null) validWorkEfforts = new LinkedList();
        result.put("activities", validWorkEfforts);
        return result;
    }

    public static Map getWorkEffortAssignedActivitiesByRole(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        List roleWorkEfforts = null;

        if (userLogin != null && userLogin.get("partyId") != null) {
            try {
                List constraints = new LinkedList();

                constraints.add(new EntityExpr("partyId", EntityOperator.EQUALS, userLogin.get("partyId")));
                constraints.add(new EntityExpr("workEffortTypeId", EntityOperator.EQUALS, "ACTIVITY"));
                constraints.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_DECLINED"));
                constraints.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_DELEGATED"));
                constraints.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_COMPLETED"));
                constraints.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_CANCELLED"));
                constraints.add(new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "WF_COMPLETED"));
                constraints.add(new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "WF_TERMINATED"));
                constraints.add(new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "WF_ABORTED"));
                roleWorkEfforts = delegator.findByAnd("WorkEffortPartyAssignByRole", constraints, UtilMisc.toList("priority"));
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
                return ServiceUtil.returnError("Error finding desired WorkEffort records: " + e.toString());
            }
        }

        Map result = new HashMap();
        if (roleWorkEfforts == null) roleWorkEfforts = new LinkedList();
        result.put("roleActivities", roleWorkEfforts);
        return result;
    }

    public static Map getWorkEffortAssignedActivitiesByGroup(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        List groupWorkEfforts = null;

        if (userLogin != null && userLogin.get("partyId") != null) {
            try {
                List constraints = new LinkedList();

                constraints.add(new EntityExpr("partyId", EntityOperator.EQUALS, userLogin.get("partyId")));
                constraints.add(new EntityExpr("workEffortTypeId", EntityOperator.EQUALS, "ACTIVITY"));
                constraints.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_DECLINED"));
                constraints.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_DELEGATED"));
                constraints.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_COMPLETED"));
                constraints.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_CANCELLED"));
                constraints.add(new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "WF_COMPLETED"));
                constraints.add(new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "WF_TERMINATED"));
                constraints.add(new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "WF_ABORTED"));
                groupWorkEfforts = delegator.findByAnd("WorkEffortPartyAssignByGroup", constraints, UtilMisc.toList("priority"));
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
                return ServiceUtil.returnError("Error finding desired WorkEffort records: " + e.toString());
            }
        }

        Map result = new HashMap();
        if (groupWorkEfforts == null) groupWorkEfforts = new LinkedList();
        result.put("groupActivities", groupWorkEfforts);
        return result;
    }
}
