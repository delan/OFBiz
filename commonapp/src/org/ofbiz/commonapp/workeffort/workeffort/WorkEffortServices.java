/*
 * $Id$
 * $Log$
 * Revision 1.1  2001/12/22 10:40:21  jonesde
 * Early first pass at WorkEffortServices
 *
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
        GenericValue userLogin = null;
        GenericValue workEffort = null;
        
        //get, and validate, the primary keys
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
        if (!associatedWith && !security.hasEntityPermission("WORKEFFORTMGR", "_DELETE", userLogin)) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "You cannot update or delete this Work Effort, you must either be associated with it or have administration permission.");
            return result;
        }

        if (workEffort != null) {
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
        } else {
            errorMessageList.add("Could not find Work Effort with ID" + workEffortId + ", workEffort not deleted.");
        }
        
        if (errorMessageList.size() > 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE_LIST, errorMessageList);
        } else {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }

        return result;
    }
}
