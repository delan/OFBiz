/*
 * $Id$
 * $Log$
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
    GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
    Security security = (Security)request.getAttribute("security");
    
    GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
    if(userLogin == null) {
      request.setAttribute("ERROR_MESSAGE", "You must be logged in to update a Work Effort.");
      return "error";
    }
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0) {
      request.setAttribute("ERROR_MESSAGE", "Update Mode was not specified, but is required.");
      Debug.logWarning("[WorkEffortEvents.updateWorkEffort] Update Mode was not specified, but is required");
      return "error";
    }
    
    String workEffortId = null;
    GenericValue workEffort = null;
    if("CREATE".equals(updateMode)) {
      Long nextSeqId = delegator.getNextSeqId("WorkEffort");
      if(nextSeqId == null) {
        request.setAttribute("ERROR_MESSAGE", "Could not get an Id for a new WorkEffort (NextSeqIdError)");
        return "error";
      }
      else workEffortId = nextSeqId.toString();
    }
    else {
      //get, and validate, the primary keys
      workEffortId = request.getParameter("WORK_EFFORT_ID");
      if(!UtilValidate.isNotEmpty(workEffortId)) errMsg += "<li>Work Effort ID missing.";
      if(errMsg.length() > 0) {
        errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
        request.setAttribute("ERROR_MESSAGE", errMsg);
        return "error";
      }

      //do a findByPrimary key to see if the entity exists, and other things later
      try { workEffort = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId)); }
      catch(GenericEntityException e) { Debug.logWarning(e); }

      //get a collection of workEffortPartyAssignments, if empty then this user CANNOT view the event, unless they have permission to view all
      Collection workEffortPartyAssignments = null;
      if(userLogin != null && userLogin.get("partyId") != null && workEffortId != null) {
        try { workEffortPartyAssignments = delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", workEffortId, "partyId", userLogin.get("partyId"))); }
        catch(GenericEntityException e) { Debug.logWarning(e); }
      }

      //check permissions before moving on:
      // 1) if create, no permission necessary
      // 2) if update or delete logged in user must be associated OR have the corresponding UPDATE or DELETE permissions
      boolean associatedWith = (workEffortPartyAssignments != null && workEffortPartyAssignments.size()>0)?true:false;
      if(!associatedWith && !security.hasEntityPermission("WORKEFFORTMGR", "_" + updateMode, request.getSession())) {
        request.setAttribute("ERROR_MESSAGE", "You cannot update or delete this Work Effort, you must either be associated with it or have administration permission.");
        return "error";
      }
    }
    
    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE")) {
      if(workEffort != null) {
        //NOTE: this is pretty weak for handling removal in clean way; what we really need
        // is the upcoming generic transaction token stuff in the Entity Engine
        
        //Remove associated/dependent entries from other tables here
        try { workEffort.removeRelated("WorkEffortAttribute"); }
        catch(GenericEntityException e) { errMsg += e.getMessage(); }
        try { workEffort.removeRelated("WorkEffortCategoryMember"); }
        catch(GenericEntityException e) { errMsg += e.getMessage(); }
        try { workEffort.removeRelated("WorkEffortPartyAssignment"); }
        catch(GenericEntityException e) { errMsg += e.getMessage(); }
        try { workEffort.removeRelated("FromWorkEffortAssoc"); }
        catch(GenericEntityException e) { errMsg += e.getMessage(); }
        try { workEffort.removeRelated("ToWorkEffortAssoc"); }
        catch(GenericEntityException e) { errMsg += e.getMessage(); }
        try { workEffort.removeRelated("WorkEffortStatus"); }
        catch(GenericEntityException e) { errMsg += e.getMessage(); }
        try { workEffort.removeRelated("ContextRuntimeData"); }
        catch(GenericEntityException e) { errMsg += e.getMessage(); }
        try { workEffort.removeRelated("ResultRuntimeData"); }
        catch(GenericEntityException e) { errMsg += e.getMessage(); }
        try { workEffort.removeRelated("NoteData"); }
        catch(GenericEntityException e) { errMsg += e.getMessage(); }
        try { workEffort.removeRelated("RecurrenceInfo"); }
        catch(GenericEntityException e) { errMsg += e.getMessage(); }
        //Delete actual main entity last, just in case database is set up to do a cascading delete, caches won't get cleared
        try { workEffort.remove(); }
        catch(GenericEntityException e) { errMsg += e.getMessage(); }
        if(errMsg.length() > 0) {
          errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
          request.setAttribute("ERROR_MESSAGE", errMsg);
          return "error";
        }
        else return "success";
      }
      else {
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
    
    
    if(UtilValidate.isNotEmpty(priorityStr)) {
      try { priority = Long.valueOf(priorityStr); }
      catch(Exception e) { errMsg += "<li>Priority is not a valid whole number."; }
    }

    if(UtilValidate.isNotEmpty(estimatedStartDateStr)) {
      try { estimatedStartDate = Timestamp.valueOf(estimatedStartDateStr); }
      catch(Exception e) { errMsg += "<li>Estimated Start Date is not a valid Date-Time."; }
    }
    if(UtilValidate.isNotEmpty(estimatedCompletionDateStr)) {
      try { estimatedCompletionDate = Timestamp.valueOf(estimatedCompletionDateStr); }
      catch(Exception e) { errMsg += "<li>Estimated Completion Date is not a valid Date-Time."; }
    }
    
    if(UtilValidate.isNotEmpty(actualStartDateStr)) {
      try { actualStartDate = Timestamp.valueOf(actualStartDateStr); }
      catch(Exception e) { errMsg += "<li>Actual Start Date is not a valid Date-Time."; }
    }
    if(UtilValidate.isNotEmpty(actualCompletionDateStr)) {
      try { actualCompletionDate = Timestamp.valueOf(actualCompletionDateStr); }
      catch(Exception e) { errMsg += "<li>Actual Completion Date is not a valid Date-Time."; }
    }
    
    if(UtilValidate.isNotEmpty(estimatedMilliSecondsStr)) {
      try { estimatedMilliSeconds = Double.valueOf(estimatedMilliSecondsStr); }
      catch(Exception e) { errMsg += "<li>Estimated Milli-seconds is not a valid number."; }
    }
    if(UtilValidate.isNotEmpty(actualMilliSecondsStr)) {
      try { actualMilliSeconds = Double.valueOf(actualMilliSecondsStr); }
      catch(Exception e) { errMsg += "<li>Actual Milli-seconds is not a valid number."; }
    }
    if(UtilValidate.isNotEmpty(totalMilliSecondsAllowedStr)) {
      try { totalMilliSecondsAllowed = Double.valueOf(totalMilliSecondsAllowedStr); }
      catch(Exception e) { errMsg += "<li>Total Milli-seconds Allows is not a valid number."; }
    }

    if(UtilValidate.isNotEmpty(totalMoneyAllowedStr)) {
      try { totalMoneyAllowed = Double.valueOf(totalMoneyAllowedStr); }
      catch(Exception e) { errMsg += "<li>Total Money Allowed is not a valid number."; }
    }
    if(UtilValidate.isNotEmpty(timeTransparencyStr)) {
      try { timeTransparency = Long.valueOf(timeTransparencyStr); }
      catch(Exception e) { errMsg += "<li>Time Transparency is not a valid whole number."; }
    }

    if(!UtilValidate.isNotEmpty(workEffortName)) errMsg += "<li>Name is missing.";
    if(!UtilValidate.isNotEmpty(currentStatusId)) errMsg += "<li>Status is missing.";
    if(estimatedStartDate != null && estimatedCompletionDate != null && estimatedStartDate.after(estimatedCompletionDate)) {
      errMsg += "<li>Start date/time cannot be after end date/time.";
    }
    if(errMsg.length() > 0) {
      errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }
    
    //done validating, now go about setting values and storing them...
    GenericValue newWorkEffort = null;
    if(workEffort != null) newWorkEffort = (GenericValue)workEffort.clone();
    else newWorkEffort = delegator.makeValue("WorkEffort", null);
    Timestamp nowStamp = UtilDateTime.nowTimestamp();
    
    //if necessary create new status entry, and set lastStatusUpdate date
    if(workEffort == null || (currentStatusId != null && !currentStatusId.equals(workEffort.getString("currentStatusId")))) {
      newWorkEffort.preStoreOther(delegator.makeValue("WorkEffortStatus", UtilMisc.toMap("workEffortId", workEffortId, "statusId", currentStatusId, "statusDatetime", nowStamp, "setByPartyId", userLogin.get("partyId"))));
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
    if("UPDATE".equals(updateMode) && workEffort != null && newWorkEffort.equals(workEffort)) {
      request.setAttribute("EVENT_MESSAGE", "No changes made, not saving.");
      return "success";
    }
    
    //only set lastModifiedDate after comparing new & old to see if anything has changed
    newWorkEffort.set("lastModifiedDate", nowStamp);
    if(userLogin.get("partyId") != null) newWorkEffort.set("lastModifiedByPartyId", userLogin.get("partyId"));

    Long currentRev = newWorkEffort.getLong("revisionNumber");
    if(currentRev != null) newWorkEffort.set("revisionNumber", new Long(currentRev.longValue() + 1));
    else newWorkEffort.set("revisionNumber", new Long(1));
    
    if(updateMode.equals("CREATE")) {
      newWorkEffort.set("createdDate", nowStamp);
      if(userLogin.get("partyId") != null) {
        newWorkEffort.set("createdByPartyId", userLogin.get("partyId"));
        //add a party assignment for the creator of the event
        newWorkEffort.preStoreOther(delegator.makeValue("WorkEffortPartyAssignment", 
          UtilMisc.toMap("workEffortId", workEffortId, "partyId", userLogin.get("partyId"), "roleTypeId", "CAL_OWNER", "fromDate", nowStamp, "statusId", "CAL_ASN_ACCEPTED")));
      }
      
      GenericValue createWorkEffort = null;
      try { createWorkEffort = delegator.create(newWorkEffort); }
      catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); createWorkEffort = null; }
      if(createWorkEffort == null) {
        request.setAttribute("ERROR_MESSAGE", "Could not create new WorkEffort (write error)");
        return "error";
      }
      request.setAttribute("WORK_EFFORT_ID", workEffortId);
    }
    else if(updateMode.equals("UPDATE")) {
      try { newWorkEffort.store(); }
      catch(GenericEntityException e) {
        request.setAttribute("ERROR_MESSAGE", "Could not update WorkEffort (write error)");
        Debug.logWarning("[WorkEffortEvents.updateWorkEffort] Could not update WorkEffort (write error); message: " + e.getMessage());
        return "error";
      }
    }
    else {
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
  /* --- Needs to be finished...
    String errMsg = "";
    GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
    Security security = (Security)request.getAttribute("security");
    Timestamp nowStamp = UtilDateTime.nowTimestamp();
    
    GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
    if(userLogin == null) {
      request.setAttribute("ERROR_MESSAGE", "You must be logged in to update a Work Effort.");
      return "error";
    }
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0) {
      request.setAttribute("ERROR_MESSAGE", "Update Mode was not specified, but is required.");
      Debug.logWarning("[WorkEffortEvents.updateWorkEffort] Update Mode was not specified, but is required");
      return "error";
    }
    
    String workEffortId = null;
    String partyId = null;
    String roleTypeId = null;
    Timestamp fromDate = null;
    GenericValue workEffortPartyAssignment = null;
    if("CREATE".equals(updateMode)) {
      fromDate = nowStamp;
    }
    else {
      //get, and validate, the primary keys
      workEffortId = request.getParameter("WORK_EFFORT_ID");
      if(!UtilValidate.isNotEmpty(workEffortId)) errMsg += "<li>Work Effort ID missing.";
      if(errMsg.length() > 0) {
        errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
        request.setAttribute("ERROR_MESSAGE", errMsg);
        return "error";
      }

      //do a findByPrimary key to see if the entity exists, and other things later
      try { workEffortPartyAssignment = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId)); }
      catch(GenericEntityException e) { Debug.logWarning(e); }

      //check permissions before moving on:
      // 1) if create, no permission necessary
      // 2) if update or delete logged in user must be associated OR have the corresponding UPDATE or DELETE permissions
      boolean associatedWith = (userLogin.getString("partyId").equals(workEffortPartyAssignment.getString("partyId")))?true:false;
      if(!associatedWith && !security.hasEntityPermission("WORKEFFORTMGR", "_" + updateMode, request.getSession())) {
        request.setAttribute("ERROR_MESSAGE", "You cannot update or delete this Work Effort, you must either be associated with it or have administration permission.");
        return "error";
      }
    }
    
    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE")) {
      if(workEffortPartyAssignment != null) {
        //NOTE: this is pretty weak for handling removal in clean way; what we really need
        // is the upcoming generic transaction token stuff in the Entity Engine
        
        //Remove associated/dependent entries from other tables here

        //Delete actual main entity last, just in case database is set up to do a cascading delete, caches won't get cleared
        try { workEffortPartyAssignment.remove(); }
        catch(GenericEntityException e) { errMsg += e.getMessage(); }
        if(errMsg.length() > 0) {
          errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
          request.setAttribute("ERROR_MESSAGE", errMsg);
          return "error";
        }
        else return "success";
      }
      else {
        request.setAttribute("ERROR_MESSAGE", "Could not find Work Effort with ID" + workEffortId + ", workEffortPartyAssignment not deleted.");
        return "error";
      }
    }
   */
    return "success";
  }
}
