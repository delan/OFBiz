/*
 * $Id$
 * $Log$
 * Revision 1.9  2001/12/29 12:26:08  jonesde
 * Finished moving WorkEffort functionality to services, party assignment still needs to be done
 *
 * Revision 1.8  2001/12/23 13:47:12  jonesde
 * Fixed a bug stopping a new workeffort from being handled correctly, caused in the refactoring our preStoreOther earlier
 *
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
        LocalDispatcher dispatcher = (LocalDispatcher) request.getSession().getServletContext().getAttribute("dispatcher");
        //GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
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

        //if this is a delete, do that before getting all of the non-pk parameters and validating them
        if (updateMode.equals("DELETE")) {
            // invoke the service
            Map result = null;
            Map context = new HashMap();
            context.put("workEffortId", request.getParameter("WORK_EFFORT_ID"));
            context.put("userLogin", userLogin);
            try {
                result = dispatcher.runSync("deleteWorkEffort",context);
            } catch (GenericServiceException e) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE,"ERROR: Could not delete WorkEffort (problem invoking the service: " + e.getMessage() + ")");
                Debug.logError(e);
                return "error";
            }

            request.setAttribute(SiteDefs.EVENT_MESSAGE, "Work Effort successfully deleted.");
            //always do this last, may override defaults EVENT_MESSAGE
            ServiceUtil.getHtmlMessages(request, result);
            // return the result
            return result.containsKey(ModelService.RESPONSE_MESSAGE) ? (String)result.get(ModelService.RESPONSE_MESSAGE) : "success";
        }

        Long priority = null;

        java.sql.Timestamp estimatedStartDate = null;
        java.sql.Timestamp estimatedCompletionDate = null;

        java.sql.Timestamp actualStartDate = null;
        java.sql.Timestamp actualCompletionDate = null;

        Double estimatedMilliSeconds = null;
        Double actualMilliSeconds = null;

        Double totalMilliSecondsAllowed = null;
        Double totalMoneyAllowed = null;
        Long timeTransparency = null;


        if (UtilValidate.isNotEmpty(request.getParameter("PRIORITY"))) {
            try {
                priority = Long.valueOf(request.getParameter("PRIORITY"));
            } catch (Exception e) {
                errMsg += "<li>Priority is not a valid whole number.";
            }
        }

        if (UtilValidate.isNotEmpty(request.getParameter("ESTIMATED_START_DATE"))) {
            try {
                estimatedStartDate = Timestamp.valueOf(request.getParameter("ESTIMATED_START_DATE"));
            } catch (Exception e) {
                errMsg += "<li>Estimated Start Date is not a valid Date-Time.";
            }
        }
        if (UtilValidate.isNotEmpty(request.getParameter("ESTIMATED_COMPLETION_DATE"))) {
            try {
                estimatedCompletionDate = Timestamp.valueOf(request.getParameter("ESTIMATED_COMPLETION_DATE"));
            } catch (Exception e) {
                errMsg += "<li>Estimated Completion Date is not a valid Date-Time.";
            }
        }

        if (UtilValidate.isNotEmpty(request.getParameter("ACTUAL_START_DATE"))) {
            try {
                actualStartDate = Timestamp.valueOf(request.getParameter("ACTUAL_START_DATE"));
            } catch (Exception e) {
                errMsg += "<li>Actual Start Date is not a valid Date-Time.";
            }
        }
        if (UtilValidate.isNotEmpty(request.getParameter("ACTUAL_COMPLETION_DATE"))) {
            try {
                actualCompletionDate = Timestamp.valueOf(request.getParameter("ACTUAL_COMPLETION_DATE"));
            } catch (Exception e) {
                errMsg += "<li>Actual Completion Date is not a valid Date-Time.";
            }
        }

        if (UtilValidate.isNotEmpty(request.getParameter("ESTIMATED_MILLI_SECONDS"))) {
            try {
                estimatedMilliSeconds = Double.valueOf(request.getParameter("ESTIMATED_MILLI_SECONDS"));
            } catch (Exception e) {
                errMsg += "<li>Estimated Milli-seconds is not a valid number.";
            }
        }
        if (UtilValidate.isNotEmpty(request.getParameter("ACTUAL_MILLI_SECONDS"))) {
            try {
                actualMilliSeconds = Double.valueOf(request.getParameter("ACTUAL_MILLI_SECONDS"));
            } catch (Exception e) {
                errMsg += "<li>Actual Milli-seconds is not a valid number.";
            }
        }
        if (UtilValidate.isNotEmpty(request.getParameter("TOTAL_MILLI_SECONDS_ALLOWED"))) {
            try {
                totalMilliSecondsAllowed = Double.valueOf(request.getParameter("TOTAL_MILLI_SECONDS_ALLOWED"));
            } catch (Exception e) {
                errMsg += "<li>Total Milli-seconds Allows is not a valid number.";
            }
        }

        if (UtilValidate.isNotEmpty(request.getParameter("TOTAL_MONEY_ALLOWED"))) {
            try {
                totalMoneyAllowed = Double.valueOf(request.getParameter("TOTAL_MONEY_ALLOWED"));
            } catch (Exception e) {
                errMsg += "<li>Total Money Allowed is not a valid number.";
            }
        }
        if (UtilValidate.isNotEmpty(request.getParameter("TIME_TRANSPARENCY"))) {
            try {
                timeTransparency = Long.valueOf(request.getParameter("TIME_TRANSPARENCY"));
            } catch (Exception e) {
                errMsg += "<li>Time Transparency is not a valid whole number.";
            }
        }

        if (!UtilValidate.isNotEmpty(request.getParameter("WORK_EFFORT_NAME")))
            errMsg += "<li>Name is missing.";
        if (!UtilValidate.isNotEmpty(request.getParameter("CURRENT_STATUS_ID")))
            errMsg += "<li>Status is missing.";
        if (estimatedStartDate != null && estimatedCompletionDate != null && estimatedStartDate.after(estimatedCompletionDate)) {
            errMsg += "<li>Start date/time cannot be after end date/time.";
        }
        if (errMsg.length() > 0) {
            errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
            request.setAttribute("ERROR_MESSAGE", errMsg);
            return "error";
        }

        Map context = new HashMap();
        context.put("workEffortTypeId", request.getParameter("WORK_EFFORT_TYPE_ID"));
        context.put("currentStatusId", request.getParameter("CURRENT_STATUS_ID"));
        context.put("universalId", request.getParameter("UNIVERSAL_ID"));
        context.put("scopeEnumId", request.getParameter("SCOPE_ENUM_ID"));
        context.put("priority", priority);
        context.put("workEffortName", request.getParameter("WORK_EFFORT_NAME"));
        context.put("description", request.getParameter("DESCRIPTION"));
        context.put("locationDesc", request.getParameter("LOCATION_DESC"));
        context.put("estimatedStartDate", estimatedStartDate);
        context.put("estimatedCompletionDate", estimatedCompletionDate);
        context.put("actualStartDate", actualStartDate);
        context.put("actualCompletionDate", actualCompletionDate);
        context.put("estimatedMilliSeconds", estimatedMilliSeconds);
        context.put("actualMilliSeconds", actualMilliSeconds);
        context.put("totalMilliSecondsAllowed", totalMilliSecondsAllowed);
        context.put("totalMoneyAllowed", totalMoneyAllowed);
        context.put("moneyUomId", request.getParameter("MONEY_UOM_ID"));
        context.put("specialTerms", request.getParameter("SPECIAL_TERMS"));
        context.put("timeTransparency", timeTransparency);
        context.put("infoUrl", request.getParameter("INFO_URL"));

        context.put("userLogin", userLogin);

        if (updateMode.equals("CREATE")) {
            // invoke the service
            Map result = null;
            try {
                result = dispatcher.runSync("createWorkEffort",context);
            } catch (GenericServiceException e) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE,"ERROR: Could not delete WorkEffort (problem invoking the service: " + e.getMessage() + ")");
                Debug.logError(e);
                return "error";
            }

            request.setAttribute(SiteDefs.EVENT_MESSAGE, "Work Effort successfully created.");
            //always do this last, may override defaults EVENT_MESSAGE
            ServiceUtil.getHtmlMessages(request, result);
            
            request.setAttribute("WORK_EFFORT_ID", result.get("workEffortId"));

            // return the result
            return result.containsKey(ModelService.RESPONSE_MESSAGE) ? (String)result.get(ModelService.RESPONSE_MESSAGE) : "success";
        } else if (updateMode.equals("UPDATE")) {
            // invoke the service
            Map result = null;
            context.put("workEffortId", request.getParameter("WORK_EFFORT_ID"));
            try {
                result = dispatcher.runSync("updateWorkEffort",context);
            } catch (GenericServiceException e) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE,"ERROR: Could not delete WorkEffort (problem invoking the service: " + e.getMessage() + ")");
                Debug.logError(e);
                return "error";
            }

            request.setAttribute(SiteDefs.EVENT_MESSAGE, "Work Effort successfully updated.");
            //always do this last, may override defaults EVENT_MESSAGE
            ServiceUtil.getHtmlMessages(request, result);
            // return the result
            return result.containsKey(ModelService.RESPONSE_MESSAGE) ? (String)result.get(ModelService.RESPONSE_MESSAGE) : "success";
        } else {
            request.setAttribute("ERROR_MESSAGE", "Specified update mode: \"" + updateMode + "\" is not supported.");
            return "error";
        }
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

        Timestamp fromDate = null;

        //get, and validate, the primary keys
        if (UtilValidate.isNotEmpty(request.getParameter("fromDate"))) {
            try {
                fromDate = Timestamp.valueOf(request.getParameter("fromDate"));
            } catch (Exception e) {
                errMsg += "<li>From Date is not a valid Date-Time.";
            }
        }

        if (!UtilValidate.isNotEmpty(request.getParameter("workEffortId")))
            errMsg += "<li>Work Effort ID missing.";
        if (!UtilValidate.isNotEmpty(request.getParameter("partyId")))
            errMsg += "<li>Party ID missing.";
        if (!UtilValidate.isNotEmpty(request.getParameter("roleTypeId")))
            errMsg += "<li>Role Type ID missing.";
            
        if (!"CREATE".equals(updateMode)) {
            if (!UtilValidate.isNotEmpty(request.getParameter("fromDate")))
                errMsg += "<li>From Date missing.";
        }
        
        if (errMsg.length() > 0) {
            errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
            request.setAttribute("ERROR_MESSAGE", errMsg);
            return "error";
        }

        //if this is a delete, do that before getting all of the non-pk parameters and validating them
        if (updateMode.equals("DELETE")) {
            // invoke the service
            Map result = null;
            Map context = new HashMap();
            context.put("workEffortId", request.getParameter("workEffortId"));
            context.put("partyId", request.getParameter("partyId"));
            context.put("roleTypeId", request.getParameter("roleTypeId"));
            context.put("fromDate", fromDate);
            context.put("userLogin", userLogin);
            try {
                result = dispatcher.runSync("deleteWorkEffortPartyAssignment",context);
            } catch (GenericServiceException e) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE,"ERROR: Could not delete WorkEffortPartyAssignment (problem invoking the service: " + e.getMessage() + ")");
                Debug.logError(e);
                return "error";
            }

            request.setAttribute(SiteDefs.EVENT_MESSAGE, "Work Effort Party Assignment successfully deleted.");
            //always do this last, may override defaults EVENT_MESSAGE
            ServiceUtil.getHtmlMessages(request, result);
            // return the result
            return result.containsKey(ModelService.RESPONSE_MESSAGE) ? (String)result.get(ModelService.RESPONSE_MESSAGE) : "success";
        }

        Timestamp thruDate = null;
        if (UtilValidate.isNotEmpty(request.getParameter("thruDate"))) {
            try {
                thruDate = Timestamp.valueOf(request.getParameter("thruDate"));
            } catch (Exception e) {
                errMsg += "<li>Thru Date is not a valid Date-Time.";
            }
        }
        
        if (errMsg.length() > 0) {
            errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
            request.setAttribute("ERROR_MESSAGE", errMsg);
            return "error";
        }

        Map context = new HashMap();
        context.put("workEffortId", request.getParameter("workEffortId"));
        context.put("partyId", request.getParameter("partyId"));
        context.put("roleTypeId", request.getParameter("roleTypeId"));
        context.put("thruDate", thruDate);
        context.put("facilityId", request.getParameter("facilityId"));
        context.put("comments", request.getParameter("statusId"));
        context.put("mustRsvp", request.getParameter("mustRsvp"));
        context.put("expectationEnumId", request.getParameter("expectationEnumId"));
        
        if (updateMode.equals("CREATE")) {
            // invoke the service
            Map result = null;
            try {
                result = dispatcher.runSync("createWorkEffortPartyAssignment",context);
            } catch (GenericServiceException e) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE,"ERROR: Could not delete WorkEffortPartyAssignment (problem invoking the service: " + e.getMessage() + ")");
                Debug.logError(e);
                return "error";
            }

            request.setAttribute(SiteDefs.EVENT_MESSAGE, "Work Effort Party Assignment successfully created.");
            //always do this last, may override defaults EVENT_MESSAGE
            ServiceUtil.getHtmlMessages(request, result);

            request.setAttribute("fromDate", result.get("fromDate"));

            // return the result
            return result.containsKey(ModelService.RESPONSE_MESSAGE) ? (String)result.get(ModelService.RESPONSE_MESSAGE) : "success";
        } else if (updateMode.equals("UPDATE")) {
            // invoke the service
            Map result = null;
            context.put("fromDate", fromDate);
            try {
                result = dispatcher.runSync("updateWorkEffortPartyAssignment",context);
            } catch (GenericServiceException e) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE,"ERROR: Could not delete WorkEffortPartyAssignment (problem invoking the service: " + e.getMessage() + ")");
                Debug.logError(e);
                return "error";
            }

            request.setAttribute(SiteDefs.EVENT_MESSAGE, "Work Effort Party Assignment successfully updated.");
            //always do this last, may override defaults EVENT_MESSAGE
            ServiceUtil.getHtmlMessages(request, result);

            // return the result
            return result.containsKey(ModelService.RESPONSE_MESSAGE) ? (String)result.get(ModelService.RESPONSE_MESSAGE) : "success";
        } else {
            request.setAttribute("ERROR_MESSAGE", "Specified update mode: \"" + updateMode + "\" is not supported.");
            return "error";
        }
    }
}
