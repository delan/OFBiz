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
package org.ofbiz.workeffort.workeffort;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * WorkEffortServices - WorkEffort related Services
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:johan@ibibi.com">Johan Isacsson</a>
 * @version    $Rev:$
 * @since      2.0
 */
public class WorkEffortServices {
    
    public static final String module = WorkEffortServices.class.getName();

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
                Debug.logWarning(e, module);
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
                Debug.logWarning(e, module);
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
                Debug.logWarning(e, module);
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
                Debug.logWarning(e, module);
                return ServiceUtil.returnError("Error finding desired WorkEffort records: " + e.toString());
            }
        }

        Map result = new HashMap();
        if (groupWorkEfforts == null) groupWorkEfforts = new LinkedList();
        result.put("groupActivities", groupWorkEfforts);
        return result;
    }
    
    public static Map getWorkEffort(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");    
        Security security = ctx.getSecurity();
        Map resultMap = new HashMap();
        
        String workEffortId = (String) context.get("workEffortId");    
        GenericValue workEffort = null;
        
        try {
            workEffort = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        
        Boolean canView = null;
        Collection workEffortPartyAssignments = null;
        Boolean tryEntity = null;
        GenericValue currentStatus = null;
        
        if (workEffort == null) {
            tryEntity = new Boolean(false);
            canView = new Boolean(true);
        
            String statusId = (String) context.get("currentStatusId");
        
            if (statusId != null && statusId.length() > 0) {
                try {
                    currentStatus = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", statusId));
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, module);
                }
            }
        } else {
            // get a collection of workEffortPartyAssignments, if empty then this user CANNOT view the event, unless they have permission to view all
            if (userLogin != null && userLogin.get("partyId") != null && workEffortId != null) {
                try {
                    workEffortPartyAssignments = delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", workEffortId, "partyId", userLogin.get("partyId")));
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, module);
                }
            }
            canView = (workEffortPartyAssignments != null && workEffortPartyAssignments.size() > 0) ? Boolean.TRUE : Boolean.FALSE;
            if (!canView.booleanValue() && security.hasEntityPermission("WORKEFFORTMGR", "_VIEW", userLogin)) {
                canView = new Boolean(true);
            }
        
            tryEntity = new Boolean(true);
        
            if (workEffort.get("currentStatusId") != null) {
                try {
                    currentStatus = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.get("currentStatusId")));
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, module);
                }
            }
        }
        
        if (workEffortId != null) resultMap.put("workEffortId", workEffortId);
        if (workEffort != null) resultMap.put("workEffort", workEffort);
        if (canView != null) resultMap.put("canView", canView);
        if (workEffortPartyAssignments != null) resultMap.put("partyAssigns", workEffortPartyAssignments);
        if (tryEntity != null) resultMap.put("tryEntity", tryEntity);
        if (currentStatus != null) resultMap.put("currentStatusItem", currentStatus);
        return resultMap;
    } 
        
    private static List getWorkEffortEvents(DispatchContext ctx, Timestamp startStamp, Timestamp endStamp, String partyId, String facilityId) {
        GenericDelegator delegator = ctx.getDelegator();
        List validWorkEfforts = new ArrayList();
        try {
            List entityExprList = UtilMisc.toList(
                    new EntityExpr("estimatedCompletionDate", EntityOperator.GREATER_THAN_EQUAL_TO, startStamp),
                    new EntityExpr("estimatedStartDate", EntityOperator.LESS_THAN, endStamp),
                    new EntityExpr("workEffortTypeId", EntityOperator.EQUALS, "EVENT"));
            if (UtilValidate.isNotEmpty(partyId)) {
                entityExprList.add(new EntityExpr("partyId", EntityOperator.EQUALS, partyId));
            }
            if (UtilValidate.isNotEmpty(facilityId)) {
                entityExprList.add(new EntityExpr("facilityId", EntityOperator.EQUALS, facilityId));
            }
            
            List tempWorkEfforts = delegator.findByAnd("WorkEffortAndPartyAssign", entityExprList, UtilMisc.toList("estimatedStartDate"));
            Set tempWeKeys = new HashSet();
            Iterator tempWorkEffortIter = tempWorkEfforts.iterator();
            while (tempWorkEffortIter.hasNext()) {
                GenericValue tempWorkEffort = (GenericValue) tempWorkEffortIter.next();
                String tempWorkEffortId = tempWorkEffort.getString("workEffortId");
                if (tempWeKeys.contains(tempWorkEffortId)) {
                    tempWorkEffortIter.remove();
                } else {
                    tempWeKeys.add(tempWorkEffortId);
                }
            }
            
            validWorkEfforts = new ArrayList(tempWorkEfforts);
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return validWorkEfforts;        
    }
    
    public static Map getWorkEffortEventsByDays(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");    
        Map resultMap = new HashMap();
        Timestamp startDay = (Timestamp) context.get("startDay");
        Integer numDaysInteger = (Integer) context.get("numDays");
        int numDays = 0;
        if(numDaysInteger != null) numDays = numDaysInteger.intValue();
        
        // get a timestamp (date) for the beginning of today and for beginning of numDays+1 days from now
        Timestamp startStamp = UtilDateTime.getDayStart(startDay);
        Timestamp endStamp = UtilDateTime.getDayStart(startDay, numDays + 1);
        
        // Get the WorkEfforts
        List validWorkEfforts = null;
        
        if (false) {
            // The NON view entity approach:
            Collection workEffortPartyAssignments = null;
            
            if (userLogin != null && userLogin.get("partyId") != null) {
                try {
                    workEffortPartyAssignments = delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("partyId", userLogin.get("partyId")));
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, module);
                }
            }
            // filter the work effort - this should really be done in a join/view entity
            validWorkEfforts = new ArrayList();
            Iterator iter = UtilMisc.toIterator(workEffortPartyAssignments);
            
            while (iter != null && iter.hasNext()) {
                GenericValue workEffortPartyAssignment = (GenericValue) iter.next();
                GenericValue workEffort = null;
            
                try {
                    workEffort = workEffortPartyAssignment.getRelatedOne("WorkEffort");
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, module);
                }
                if (workEffort == null) continue;
                
                Timestamp estimatedStartDate = workEffort.getTimestamp("estimatedStartDate");
                
                if (estimatedStartDate == null) continue;
                if (estimatedStartDate.before(startStamp)) continue;
                if (estimatedStartDate.after(endStamp)) continue;
                if (!"EVENT".equals(workEffort.getString("workEffortTypeId"))) continue;
            
                validWorkEfforts.add(workEffort);
            }
            
            // order the filtered list by the start date
            validWorkEfforts = EntityUtil.orderBy(validWorkEfforts, UtilMisc.toList("estimatedStartDate"));
        } else {
            // Use the View Entity
            if (userLogin != null && userLogin.get("partyId") != null) {
                validWorkEfforts = getWorkEffortEvents(ctx, startStamp, endStamp, userLogin.getString("partyId"), null);                
            }
        }
        
        // Split the WorkEffort list into a list for each day
        List days = new ArrayList();
        if (validWorkEfforts != null && validWorkEfforts.size() > 0) {
        
            // For each day in the set we check all work efforts to see if they fall within range
            for (int i = 0; i < numDays; i++) {
                Timestamp curDayStart = UtilDateTime.getDayStart(startStamp, i);
                Timestamp curDayEnd = UtilDateTime.getDayEnd(startStamp, i);
                List curWorkEfforts = new ArrayList();
            
                for (int j = 0; j < validWorkEfforts.size(); j++) {
                    GenericValue workEffort = (GenericValue) validWorkEfforts.get(j);
                    // Debug.log("Got workEffort: " + workEffort.toString(), module);
            
                    Timestamp estimatedStartDate = workEffort.getTimestamp("estimatedStartDate");
                    Timestamp estimatedCompletionDate = workEffort.getTimestamp("estimatedCompletionDate");
            
                    if (estimatedStartDate == null || estimatedCompletionDate == null) continue;
                    
                    if (estimatedStartDate.before(curDayEnd) && estimatedCompletionDate.after(curDayStart)) {
                        curWorkEfforts.add(workEffort);
                    }
        
                    // if startDate is after dayEnd, continue to the next day, we haven't gotten to this one yet...
                    if (estimatedStartDate.after(curDayEnd)) break;
                    
                    // if completionDate is before the dayEnd, remove from list, we are done with it
                    if (estimatedCompletionDate.before(curDayEnd)) {
                        validWorkEfforts.remove(j);
                        j--;
                    }
                }
                if (curWorkEfforts.size() > 0) days.add(curWorkEfforts);
            }
        }
        Map result = new HashMap();
        if (days == null) days = new ArrayList();
        result.put("days", days);
        return result;
    }
    
    public static Map getWorkEffortEventsByPeriod(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");    
        Map resultMap = new HashMap();
        
        Timestamp startDay = (Timestamp) context.get("start");
        Integer numPeriodsInteger = (Integer) context.get("numPeriods");
        Integer periodInteger = (Integer) context.get("periodSeconds");

        String partyId = (String) context.get("partyId");
        String facilityId = (String) context.get("facilityId");
        
        //To be returned, the max concurrent entries for a single period
        int maxConcurrentEntries = 0;
                
        long period = periodInteger.intValue()*1000;
        
        int numPeriods = 0;
        if(numPeriodsInteger != null) numPeriods = numPeriodsInteger.intValue();
        
        // get a timestamp (date) for the beginning of today and for beginning of numDays+1 days from now
        Timestamp startStamp = UtilDateTime.getDayStart(startDay);          
        Timestamp endStamp = new Timestamp(startStamp.getTime()+(period*(numPeriods+1)));
        
        startStamp.setNanos(0);
        // Get the WorkEfforts
        List validWorkEfforts = null;
        String partyIdToUse = null;
        
        if (UtilValidate.isNotEmpty(partyId)) {
            if (partyId.equals(userLogin.getString("partyId")) || security.hasEntityPermission("WORKEFFORTMGR", "_VIEW", userLogin)) {
                partyIdToUse = partyId;
            } else {
                return ServiceUtil.returnError("You do not have permission to view information for party with ID [" + partyId + "], you must be logged in as a user associated with this party, or have the WORKEFFORTMGR_VIEW or WORKEFFORTMGR_ADMIN permissions.");
            }
        } else {
            // if a facilityId is specified, don't set a default partyId...
            if (UtilValidate.isEmpty(facilityId)) {
                partyIdToUse = userLogin.getString("partyId");
            }
        }
                
        // Use the View Entity
        if (UtilValidate.isNotEmpty(partyIdToUse) || UtilValidate.isNotEmpty(facilityId)) {
            validWorkEfforts = getWorkEffortEvents(ctx, startStamp, endStamp, partyIdToUse, facilityId);                
        }
        
        // Split the WorkEffort list into a map with entries for each period, period start is the key
        List periods = new ArrayList();
        if (validWorkEfforts != null) {
        
            // For each day in the set we check all work efforts to see if they fall within range
            for (int i = 0; i < numPeriods; i++) {
                Timestamp curPeriodStart = new Timestamp(startStamp.getTime()+(i*period));
                Timestamp curPeriodEnd = new Timestamp(curPeriodStart.getTime()+period);
                List curWorkEfforts = new ArrayList();
                Map entry = new HashMap();
                for (int j = 0; j < validWorkEfforts.size(); j++) {
                    
                    GenericValue workEffort = (GenericValue) validWorkEfforts.get(j);
                    // Debug.log("Got workEffort: " + workEffort.toString(), module);
            
                    Timestamp estimatedStartDate = workEffort.getTimestamp("estimatedStartDate");
                    Timestamp estimatedCompletionDate = workEffort.getTimestamp("estimatedCompletionDate");
            
                    if (estimatedStartDate == null || estimatedCompletionDate == null) continue;
                    
                    if (estimatedStartDate.compareTo(curPeriodEnd) < 0 && estimatedCompletionDate.compareTo(curPeriodStart) > 0) {
                        //Debug.logInfo("Task start: "+estimatedStartDate+" Task end: "+estimatedCompletionDate+" Period start: "+curPeriodStart+" Period end: "+curPeriodEnd, module);
                       
                        Map calEntry = new HashMap();
                        calEntry.put("workEffort",workEffort);
                                               
                        long length = ((estimatedCompletionDate.after(endStamp) ? endStamp.getTime() : estimatedCompletionDate.getTime()) - (estimatedStartDate.before(startStamp) ? startStamp.getTime() : estimatedStartDate.getTime()));
                        int periodSpan = (int) Math.ceil((double) length / period);                                                
                        calEntry.put("periodSpan", new Integer(periodSpan));

                        if(i == 0) calEntry.put("startOfPeriod",new Boolean(true)); //If this is the first priod any valid entry is starting here
                        else {
                            boolean startOfPeriod = ((estimatedStartDate.getTime() - curPeriodStart.getTime()) >= 0);                            
                            calEntry.put("startOfPeriod", new Boolean(startOfPeriod));
                        }
                        curWorkEfforts.add(calEntry);
                    }
        
                    // if startDate is after hourEnd, continue to the next day, we haven't gotten to this one yet...
                    if (estimatedStartDate.after(curPeriodEnd)) break;
                    
                    // if completionDate is before the hourEnd, remove from list, we are done with it
                    if (estimatedCompletionDate.before(curPeriodEnd)) {
                        validWorkEfforts.remove(j);
                        j--;
                    }
                }
                //For calendar we want to include empty periods aswell
                //if (curWorkEfforts.size() > 0)  
                int numEntries = curWorkEfforts.size();
                if(numEntries > maxConcurrentEntries) maxConcurrentEntries = numEntries;
                entry.put("start",curPeriodStart);
                entry.put("end",curPeriodEnd);                
                entry.put("calendarEntries",curWorkEfforts);
                periods.add(entry);
            }
        }
        Map result = new HashMap();
        result.put("periods", periods);
        result.put("maxConcurrentEntries", new Integer(maxConcurrentEntries));
        return result;
    }    
}
