/*
 * $Id$
 * $Log$
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
  public static void getWorkEffort(PageContext pageContext, String workEffortIdAttrName, String workEffortAttrName, String partyAssignsAttrName, String canViewAttrName, String tryEntityAttrName, String currentStatusAttrName) {
    GenericDelegator delegator = (GenericDelegator)pageContext.getServletContext().getAttribute("delegator");
    Security security = (Security)pageContext.getServletContext().getAttribute("security");
    GenericValue userLogin = (GenericValue)pageContext.getSession().getAttribute(SiteDefs.USER_LOGIN);
    
    String workEffortId = pageContext.getRequest().getParameter("WORK_EFFORT_ID");
    //if there was no parameter, check the request attribute, this may be a newly created entity
    if(workEffortId == null) workEffortId = (String)pageContext.getRequest().getAttribute("WORK_EFFORT_ID");
    
    GenericValue workEffort = null;
    try { workEffort = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId)); }
    catch(GenericEntityException e) { Debug.logWarning(e); }

    Boolean canView = null;
    Collection workEffortPartyAssignments = null;
    Boolean tryEntity = null;
    GenericValue currentStatus = null;
    if(workEffort == null) {
      tryEntity = new Boolean(false);
      canView = new Boolean(true);

      String statusId = pageContext.getRequest().getParameter("CURRENT_STATUS_ID");
      if(statusId != null && statusId.length() > 0) {
        try { currentStatus = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", statusId)); }
        catch(GenericEntityException e) { Debug.logWarning(e); }
      }
    }
    else {
      //get a collection of workEffortPartyAssignments, if empty then this user CANNOT view the event, unless they have permission to view all
      if(userLogin != null && userLogin.get("partyId") != null && workEffortId != null) {
        try { workEffortPartyAssignments = delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", workEffortId, "partyId", userLogin.get("partyId"))); }
        catch(GenericEntityException e) { Debug.logWarning(e); }
      }
      canView = (workEffortPartyAssignments != null && workEffortPartyAssignments.size()>0)?new Boolean(true):new Boolean(false);
      if(!canView.booleanValue() && security.hasEntityPermission("WORKEFFORTMGR", "_VIEW", pageContext.getSession())) {
        canView = new Boolean(true);
      }
      
      tryEntity = new Boolean(true);

      if(workEffort.get("currentStatusId") != null) {
        try { currentStatus = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.get("currentStatusId"))); }
        catch(GenericEntityException e) { Debug.logWarning(e); }
      }
    }
    
    //if there was an error message, don't get values from entity
    if(pageContext.getRequest().getAttribute(SiteDefs.ERROR_MESSAGE) != null) {
      tryEntity = new Boolean(false);
    }
    Debug.logInfo("[WorkEffortWorker.getWorkEffort] tryEntity = " + tryEntity);
    
    if(workEffortId != null) pageContext.setAttribute(workEffortIdAttrName, workEffortId);
    if(workEffort != null) pageContext.setAttribute(workEffortAttrName, workEffort);
    if(canView != null) pageContext.setAttribute(canViewAttrName, canView);
    if(workEffortPartyAssignments != null) pageContext.setAttribute(partyAssignsAttrName, workEffortPartyAssignments);
    if(tryEntity != null) pageContext.setAttribute(tryEntityAttrName, tryEntity);
    if(currentStatus != null) pageContext.setAttribute(currentStatusAttrName, currentStatus);
    
    pageContext.setAttribute(tryEntityAttrName, new Boolean(workEffort==null?false:true));
  }
    
  public static void getEventStatusItems(PageContext pageContext, String attributeName) {
    GenericDelegator delegator = (GenericDelegator)pageContext.getServletContext().getAttribute("delegator");
    try {
      Collection statusItems = delegator.findByAndCache("StatusItem", UtilMisc.toMap("statusTypeId", "EVENT_STATUS"), UtilMisc.toList("sequenceId"));
      if(statusItems != null) pageContext.setAttribute(attributeName, statusItems);
    }
    catch(GenericEntityException e) { Debug.logError(e); }
  }

  public static void getMonthWorkEfforts(PageContext pageContext, String attributeName) {
  }

  public static void getUpcomingWorkEfforts(PageContext pageContext, String daysAttrName) {
    int numDays = 7;
    GenericDelegator delegator = (GenericDelegator)pageContext.getServletContext().getAttribute("delegator");
    GenericValue userLogin = (GenericValue)pageContext.getSession().getAttribute(SiteDefs.USER_LOGIN);

    //get a timestamp (date) for the beginning of today and for beginning of numDays+1 days from now
    Calendar tempCal = Calendar.getInstance();
    tempCal.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    java.sql.Timestamp startStamp = new Timestamp(tempCal.getTime().getTime());
    tempCal.add(Calendar.DAY_OF_WEEK, numDays + 1);
    java.sql.Timestamp endStamp = new Timestamp(tempCal.getTime().getTime());

    //Get the WorkEfforts
    List validWorkEfforts = null;
    if(false) {
      //The NON view entity approach:
      Collection workEffortPartyAssignments = null;
      if(userLogin != null && userLogin.get("partyId") != null) {
        try { workEffortPartyAssignments = delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("partyId", userLogin.get("partyId"))); }
        catch(GenericEntityException e) { Debug.logWarning(e); }
      }
      //filter the work effort - this should really be done in a join/view entity
      validWorkEfforts = new Vector();
      Iterator iter = UtilMisc.toIterator(workEffortPartyAssignments);
      while(iter != null && iter.hasNext()) {
        GenericValue workEffortPartyAssignment = (GenericValue)iter.next();
        GenericValue workEffort = null;
        try { workEffort = workEffortPartyAssignment.getRelatedOne("WorkEffort"); }
        catch(GenericEntityException e) { Debug.logWarning(e); }
        if(workEffort == null) continue;

        Timestamp estimatedStartDate = workEffort.getTimestamp("estimatedStartDate");

        if(estimatedStartDate == null) continue;
        if(estimatedStartDate.before(startStamp)) continue;
        if(estimatedStartDate.after(endStamp)) continue;
        if(!"EVENT".equals(workEffort.getString("workEffortTypeId"))) continue;

        validWorkEfforts.add(workEffort);
      }

      //order the filtered list by the start date
      validWorkEfforts = EntityUtil.orderBy(validWorkEfforts, UtilMisc.toList("estimatedStartDate"));
    }
    else {
      //Use the View Entity
      if(userLogin != null && userLogin.get("partyId") != null) {
        try { validWorkEfforts = new Vector(delegator.findByAnd("WorkEffortAndPartyAssign", 
                UtilMisc.toList("partyId", "estimatedStartDate", "estimatedStartDate", "workEffortTypeId"),
                UtilMisc.toList(EntityOperator.EQUALS, EntityOperator.GREATER_THAN_EQUAL_TO, EntityOperator.LESS_THAN, EntityOperator.EQUALS),
                UtilMisc.toList(userLogin.get("partyId"), startStamp, endStamp, "EVENT"),
                UtilMisc.toList("estimatedStartDate")));
        }
        catch(GenericEntityException e) { Debug.logWarning(e); }
      }
    }
    
    //Split the WorkEffort list into a list for each day
    List days = new Vector();
    List curWorkEfforts = null;
    //Timestamp curDayStart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
    Timestamp curDayEnd = null;
    Iterator wfiter = null;
    if(validWorkEfforts != null) wfiter = validWorkEfforts.iterator();
    while(wfiter != null && wfiter.hasNext()) {
      GenericValue workEffort = (GenericValue)wfiter.next();
      //Debug.log("Got workEffort: " + workEffort.toString());      
      
      Timestamp estimatedStartDate = workEffort.getTimestamp("estimatedStartDate");
      Timestamp estimatedCompletionDate = workEffort.getTimestamp("estimatedCompletionDate");
      if(estimatedStartDate == null || estimatedCompletionDate == null) continue;
      
      if(curDayEnd == null || estimatedStartDate.after(curDayEnd)) {
        curWorkEfforts = new Vector();
        days.add(curWorkEfforts);
        curDayEnd = UtilDateTime.getDayEnd(estimatedStartDate);
      }
      //this should never be null at this point...
      if(curWorkEfforts != null) {
        curWorkEfforts.add(workEffort);
      }
    }

    pageContext.setAttribute(daysAttrName, days);
  }
}
