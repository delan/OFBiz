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

package org.ofbiz.core.workflow;

import java.util.*;
import java.sql.Timestamp;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * Workflow Services - 'Services' and 'Workers' for interaction with Workflow API
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    December 5, 2001
 *@version    1.0
 */
public class WorkflowServices {

    // -------------------------------------------------------------------
    // Client 'Service' Methods
    // -------------------------------------------------------------------

    /** Cancel Workflow */
    public static Map cancelWorkflow(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        String workEffortId = (String) context.get("workEffortId");

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (!hasPermission(security, workEffortId, userLogin)) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "You do not have permission to access this workdlow");
            return result;
        }
        try {
            WfProcess process = WfFactory.getWfProcess(delegator, workEffortId);
            process.abort();
        } catch (WfException we) {
            we.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, we.getMessage());
        }
        return result;
    }

    /** Change the state of an activity */
    public static Map changeActivityState(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        String workEffortId = (String) context.get("workEffortId");
        String newState = (String) context.get("newState");

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (!hasPermission(security, workEffortId, userLogin)) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "You do not have permission to access this activity");
            return result;
        }
        try {
            WorkflowClient client = WfFactory.getClient(ctx);
            client.setState(workEffortId, newState);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (WfException we) {
            we.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, we.getMessage());
        }
        return result;
    }

    /** Check the state of an activity */
    public static Map checkActivityState(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        String workEffortId = (String) context.get("workEffortId");

        try {
            WorkflowClient client = WfFactory.getClient(ctx);
            result.put("activityState", client.getState(workEffortId));
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (WfException we) {
            we.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, we.getMessage());
        }
        return result;
    }

    /** Get the current activity context */
    public static Map getActivityContext(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        String workEffortId = (String) context.get("workEffortId");

        try {
            WorkflowClient client = WfFactory.getClient(ctx);
            result.put("activityContext", client.getContext(workEffortId));
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (WfException we) {
            we.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, we.getMessage());
        }
        return result;
    }

    /** Appends data to the activity context */
    public static Map appendActivityContext(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        String workEffortId = (String) context.get("workEffortId");
        Map appendContext = (Map) context.get("currentContext");

        if (appendContext == null || appendContext.size() == 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "The passed context is empty");
        }

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (!hasPermission(security, workEffortId, userLogin)) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "You do not have permission to access this activity");
            return result;
        }
        try {
            WorkflowClient client = WfFactory.getClient(ctx);
            client.appendContext(workEffortId, appendContext);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (WfException we) {
            we.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, we.getMessage());
        }
        return result;
    }

    /** Assign activity to a new or additional party */
    public static Map assignActivity(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        String workEffortId = (String) context.get("workEffortId");
        String partyId = (String) context.get("partyId");
        String roleType = (String) context.get("roleTypeId");
        boolean removeOldAssign = false;
        if (context.containsKey("removeOldAssignments")) {
            removeOldAssign = ((String) context.get("removeOldAssignments")).equals("true") ? true : false;
        }

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (!hasPermission(security, workEffortId, userLogin)) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "You do not have permission to access this activity");
            return result;
        }
        try {
            WorkflowClient client = WfFactory.getClient(ctx);
            client.assign(workEffortId, partyId, roleType, null, removeOldAssign ? false : true);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (WfException we) {
            we.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, we.getMessage());
        }
        return result;
    }

    /** Accept an assignment and attempt to start the activity */
    public static Map acceptAssignment(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        String workEffortId = (String) context.get("workEffortId");
        String partyId = (String) context.get("partyId");
        String roleType = (String) context.get("roleTypeId");
        Timestamp fromDate = (Timestamp) context.get("fromDate");

        try {
            WorkflowClient client = WfFactory.getClient(ctx);
            client.acceptAndStart(workEffortId, partyId, roleType, fromDate);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (WfException we) {
            we.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, we.getMessage());
        }
        return result;

    }

    /** Accept a role assignment and attempt to start the activity */
    public static Map acceptRoleAssignment(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        String workEffortId = (String) context.get("workEffortId");
        String partyId = (String) context.get("partyId");
        String roleType = (String) context.get("roleTypeId");
        Timestamp fromDate = (Timestamp) context.get("fromDate");

        try {
            WorkflowClient client = new WorkflowClient(ctx);
            client.delegateAndAccept(workEffortId, "_NA_", roleType, fromDate, partyId, roleType, fromDate, true);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (WfException we) {
            we.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, we.getMessage());
        }
        return result;
    }

    /** Complete an assignment */
    public static Map completeAssignment(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        String workEffortId = (String) context.get("workEffortId");
        String partyId = (String) context.get("partyId");
        String roleType = (String) context.get("roleTypeId");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Map actResults = (Map) context.get("result");

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (!hasPermission(security, workEffortId, userLogin)) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "You do not have permission to access this assignment");
            return result;
        }

        try {
            WorkflowClient client = WfFactory.getClient(ctx);
            client.complete(workEffortId, partyId, roleType, fromDate, actResults);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (WfException we) {
            we.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, we.getMessage());
        }
        return result;
    }

    public static Map limitInvoker(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        String workEffortId = (String) context.get("workEffortId");
        String limitService = (String) context.get("limitService");
        Map limitContext = (Map) context.get("limitContext");

        try {
            WorkflowClient client = WfFactory.getClient(ctx);
            String state = client.getState(workEffortId);
            if (state.startsWith("open")) {
                dispatcher.runSync(limitService, limitContext);
            }
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (WfException we) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, we.getMessage());
        } catch (GenericServiceException se) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, se.getMessage());
        }
        return result;
    }


    // -------------------------------------------------------------------
    // Service 'Worker' Methods
    // -------------------------------------------------------------------

    public static boolean hasPermission(Security security, String workEffortId, GenericValue userLogin) {
        if (userLogin == null || workEffortId == null) {
            Debug.logWarning("No UserLogin object or no Workeffort ID was passed.");
            return false;
        }
        if (security.hasPermission("WORKFLOW_MAINT", userLogin)) {
            return true;
        } else {
            String partyId = userLogin.getString("partyId");
            List expr = new ArrayList();
            expr.add(new EntityExpr("partyId", EntityOperator.EQUALS, partyId));
            expr.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_DECLINED"));
            expr.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_DELEGATED"));
            expr.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_COMPLETED"));
            expr.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_CANCELLED"));
            expr.add(new EntityExpr("workEffortId", EntityOperator.EQUALS, workEffortId));
            expr.add(new EntityExpr("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));

            Collection c = null;

            try {
                c = userLogin.getDelegator().findByAnd("WorkEffortAndPartyAssign", expr);
                Debug.logInfo("Found " + c.size() + " records.");
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
                return false;
            }
            if (c.size() == 0) {
                expr = new ArrayList();
                expr.add(new EntityExpr("partyId", EntityOperator.EQUALS, partyId));
                expr.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_DECLINED"));
                expr.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_DELEGATED"));
                expr.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_COMPLETED"));
                expr.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_CANCELLED"));
                expr.add(new EntityExpr("workEffortParentId", EntityOperator.EQUALS, workEffortId));
                expr.add(new EntityExpr("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
                try {
                    c = userLogin.getDelegator().findByAnd("WorkEffortAndPartyAssign", expr);
                    Debug.logInfo("Found " + c.size() + " records.");
                } catch (GenericEntityException e) {
                    Debug.logWarning(e);
                    return false;
                }
            }

            if (c.size() > 0) {
                return true;
            }
        }
        return false;
    }

}




