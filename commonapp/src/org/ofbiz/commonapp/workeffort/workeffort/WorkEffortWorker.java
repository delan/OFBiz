/*
 * $Id$
 * $Log$
 */
package org.ofbiz.commonapp.workeffort.workeffort;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.util.*;
import javax.servlet.jsp.*;
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
  public static void getWorkEffort(PageContext pageContext, String workEffortIdAttrName, String workEffortAttrName, String partyAssignsAttrName, String canViewAttrName) {
    GenericDelegator delegator = (GenericDelegator)pageContext.getServletContext().getAttribute("delegator");
    Security security = (Security)pageContext.getServletContext().getAttribute("security");
    GenericValue userLogin = (GenericValue)pageContext.getSession().getAttribute(SiteDefs.USER_LOGIN);
    
    String workEffortId = pageContext.getRequest().getParameter("WORK_EFFORT_ID");
    GenericValue workEffort = null;
    try { workEffort = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId)); }
    catch(GenericEntityException e) { Debug.logWarning(e); }

    //get a collection of workEffortPartyAssignments, if empty then this user CANNOT view the event, unless they have permission to view all
    Collection workEffortPartyAssignments = null;
    if(userLogin != null && userLogin.get("partyId") != null && workEffortId != null) {
      try { workEffortPartyAssignments = delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", workEffortId, "partyId", userLogin.get("partyId"))); }
      catch(GenericEntityException e) { Debug.logWarning(e); }
    }
    Boolean canView = (workEffortPartyAssignments != null && workEffortPartyAssignments.size()>0)?new Boolean(true):new Boolean(false);
    if(!canView.booleanValue() && security.hasEntityPermission("WORKEFFORTMGR", "_VIEW", pageContext.getSession())) {
      canView = new Boolean(true);
    }
    
    if(workEffortId != null) pageContext.setAttribute(workEffortIdAttrName, workEffortId);
    if(workEffort != null) pageContext.setAttribute(workEffortAttrName, workEffort);
    if(canView != null) pageContext.setAttribute(canViewAttrName, canView);
    if(workEffortPartyAssignments != null) pageContext.setAttribute(partyAssignsAttrName, workEffortPartyAssignments);
  }
    
  public static void getMonthWorkEfforts(PageContext pageContext, String attributeName) {
  }
}
