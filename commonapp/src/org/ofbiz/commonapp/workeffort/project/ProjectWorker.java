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

package org.ofbiz.commonapp.workeffort.project;


import java.sql.*;
import java.util.*;
import javax.servlet.jsp.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.util.*;


/**
 * WorkEffortWorker - Worker class to reduce code in JSPs & make it more reusable
 *
 *@author     <a href="mailto:dustin@dscv.org">Dustin Caldwell</a>
 *@version    1.0
 *@created    August 13, 2002
 */
public class ProjectWorker {

    public static void getAssignedProjects(PageContext pageContext, String projectsAttrName) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");
        GenericValue userLogin = (GenericValue) pageContext.getSession().getAttribute(SiteDefs.USER_LOGIN);

        Collection validWorkEfforts = null;

        if (userLogin != null && userLogin.get("partyId") != null) {
            try {
                validWorkEfforts = delegator.findByAnd("WorkEffortAndPartyAssign",
                            UtilMisc.toList(new EntityExpr("partyId", EntityOperator.EQUALS, userLogin.get("partyId")),
                                new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "WF_COMPLETED"),
                                new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "WF_TERMINATED"),
                                new EntityExpr("currentStatusId", EntityOperator.NOT_EQUAL, "WF_ABORTED"),
                                new EntityExpr("workEffortTypeId", EntityOperator.EQUALS, "TASK"),
                                new EntityExpr("workEffortPurposeTypeId", EntityOperator.EQUALS, "WEPT_PROJECT")),
                            UtilMisc.toList("priority"));
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }
        }
        if (validWorkEfforts == null || validWorkEfforts.size() <= 0)
            return;

        pageContext.setAttribute(projectsAttrName, validWorkEfforts);
    }

    public static void getAllAssignedProjects(PageContext pageContext, String projectsAttrName) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");
        GenericValue userLogin = (GenericValue) pageContext.getSession().getAttribute(SiteDefs.USER_LOGIN);

        Collection validWorkEfforts = null;

        if (userLogin != null && userLogin.get("partyId") != null) {
            try {
                validWorkEfforts = delegator.findByAnd("WorkEffortAndPartyAssign",
                            UtilMisc.toList(new EntityExpr("partyId", EntityOperator.EQUALS, userLogin.get("partyId")),
                                new EntityExpr("workEffortTypeId", EntityOperator.EQUALS, "TASK"),
                                new EntityExpr("workEffortPurposeTypeId", EntityOperator.EQUALS, "WEPT_PROJECT")),
                            UtilMisc.toList("priority"));
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }
        }
        if (validWorkEfforts == null || validWorkEfforts.size() <= 0)
            return;

        pageContext.setAttribute(projectsAttrName, validWorkEfforts);
    }

    public static void getAllProjectPhases(PageContext pageContext, String phasesAttrName) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");
        GenericValue userLogin = (GenericValue) pageContext.getSession().getAttribute(SiteDefs.USER_LOGIN);

        String projectWorkEffortId = pageContext.getRequest().getParameter("projectWorkEffortId");

        // if there was no parameter, check the request attribute, this may be a newly created entity
        if (projectWorkEffortId == null)
            projectWorkEffortId = (String) pageContext.getRequest().getAttribute("projectWorkEffortId");

        Collection relatedWorkEfforts = null;

        if (userLogin != null && userLogin.get("partyId") != null) {
            try {
                relatedWorkEfforts = delegator.findByAnd("WorkEffortAssoc",
                            UtilMisc.toList(new EntityExpr("workEffortIdFrom", EntityOperator.EQUALS, projectWorkEffortId),
                                new EntityExpr("workEffortAssocTypeId", EntityOperator.EQUALS, "WORK_EFF_BREAKDOWN")));
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }
        }

        Collection validWorkEfforts = new ArrayList();

        if (relatedWorkEfforts != null) {
            Iterator relatedWorkEffortsIter = relatedWorkEfforts.iterator();

            try {
                while (relatedWorkEffortsIter.hasNext()) {
                    GenericValue workEffortAssoc = (GenericValue) relatedWorkEffortsIter.next();
                    GenericValue workEffort = workEffortAssoc.getRelatedOne("ToWorkEffort");

                    // only get phases
                    if ("TASK".equals(workEffort.getString("workEffortTypeId")) &&
                        ("WEPT_PHASE".equals(workEffort.getString("workEffortPurposeTypeId")))) {
                        validWorkEfforts.add(workEffort);
                    }
                }
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }
        }
        if (validWorkEfforts == null || validWorkEfforts.size() <= 0)
            return;

        pageContext.setAttribute(phasesAttrName, validWorkEfforts);
    }

    public static void getAllPhaseTasks(PageContext pageContext, String tasksAttrName) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");
        GenericValue userLogin = (GenericValue) pageContext.getSession().getAttribute(SiteDefs.USER_LOGIN);

        String phaseWorkEffortId = pageContext.getRequest().getParameter("phaseWorkEffortId");

        // if there was no parameter, check the request attribute, this may be a newly created entity
        if (phaseWorkEffortId == null)
            phaseWorkEffortId = (String) pageContext.getRequest().getAttribute("phaseWorkEffortId");

        Collection relatedWorkEfforts = null;

        if (userLogin != null && userLogin.get("partyId") != null) {
            try {
                relatedWorkEfforts = delegator.findByAnd("WorkEffortAssoc",
                            UtilMisc.toList(new EntityExpr("workEffortIdFrom", EntityOperator.EQUALS, phaseWorkEffortId),
                                new EntityExpr("workEffortAssocTypeId", EntityOperator.EQUALS, "WORK_EFF_BREAKDOWN")));
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }
        }

        Collection validWorkEfforts = new ArrayList();

        if (relatedWorkEfforts != null) {
            Iterator relatedWorkEffortsIter = relatedWorkEfforts.iterator();

            try {
                while (relatedWorkEffortsIter.hasNext()) {
                    GenericValue workEffortAssoc = (GenericValue) relatedWorkEffortsIter.next();
                    GenericValue workEffort = workEffortAssoc.getRelatedOne("ToWorkEffort");

                    // only get phases
                    if ("TASK".equals(workEffort.getString("workEffortTypeId"))) {
                        validWorkEfforts.add(workEffort);
                    }
                }
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }
        }
        if (validWorkEfforts == null || validWorkEfforts.size() <= 0)
            return;

        pageContext.setAttribute(tasksAttrName, validWorkEfforts);
    }

    public static void getTaskNotes(PageContext pageContext, String notesAttrName) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");
        GenericValue userLogin = (GenericValue) pageContext.getSession().getAttribute(SiteDefs.USER_LOGIN);

        String workEffortId = pageContext.getRequest().getParameter("workEffortId");

        // if there was no parameter, check the request attribute, this may be a newly created entity
        if (workEffortId == null)
            workEffortId = (String) pageContext.getRequest().getAttribute("workEffortId");

        Collection notes = null;

        if (userLogin != null && userLogin.get("partyId") != null) {
            try {
                notes = delegator.findByAnd("WorkEffortNoteAndData",
                            UtilMisc.toList(new EntityExpr("workEffortId", EntityOperator.EQUALS, workEffortId)),
                            UtilMisc.toList("noteDateTime"));
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }
        }
        if (notes == null || notes.size() <= 0)
            return;

        pageContext.setAttribute(notesAttrName, notes);
    }

}
