/*
 * $Id: EntityPersistentMgr.java,v 1.1 2004/07/11 23:26:27 ajzeneski Exp $
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.shark.instance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.shark.container.SharkContainer;

import org.enhydra.shark.api.RootException;
import org.enhydra.shark.api.SharkTransaction;
import org.enhydra.shark.api.internal.instancepersistence.*;
import org.enhydra.shark.api.internal.working.CallbackUtilities;

/**
 * Shark Persistance Manager Implementation
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class EntityPersistentMgr implements PersistentManagerInterface {

    public static final String module = EntityPersistentMgr.class.getName();

    protected CallbackUtilities callBackUtil = null;

    public void configure(CallbackUtilities callBackUtil) throws RootException {
        this.callBackUtil = callBackUtil;
    }

    public void shutdownDatabase() throws PersistenceException {
    }

    // store methods
    public void persist(ProcessMgrPersistenceInterface processMgr, SharkTransaction trans) throws PersistenceException {
        try {
            ((ProcessMgr) processMgr).store();
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void persist(ProcessPersistenceInterface process, SharkTransaction trans) throws PersistenceException {
        try {
            ((Process) process).store();
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void persist(ActivityPersistenceInterface activity, SharkTransaction trans) throws PersistenceException {
        try {
            ((Activity) activity).store();
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void persist(ResourcePersistenceInterface resource, SharkTransaction trans) throws PersistenceException {
        try {
            ((Resource) resource).store();
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void persist(AssignmentPersistenceInterface assignment, SharkTransaction trans) throws PersistenceException {
        try {
            ((Assignment) assignment).store();
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void persist(AssignmentPersistenceInterface assignment, String oldResUname, SharkTransaction trans) throws PersistenceException {
        // TODO: look into this, if it is used only for re-assigning then have it expire/create new assignment
        persist(assignment, trans);
    }

    public void persist(ProcessVariablePersistenceInterface processVariable, SharkTransaction trans) throws PersistenceException {
        try {
            ((ProcessVariable) processVariable).store();
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void persist(ActivityVariablePersistenceInterface activityVariable, SharkTransaction trans) throws PersistenceException {
        try {
            ((ActivityVariable) activityVariable).store();
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void persist(AndJoinEntryInterface andJoin, SharkTransaction trans) throws PersistenceException {
        try {
            ((AndJoinEntry) andJoin).store();
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void persist(DeadlinePersistenceInterface dpe, SharkTransaction ti) throws PersistenceException {
        try {
            ((Deadline) dpe).store();
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    // restore methods
    public ProcessMgrPersistenceInterface restoreProcessMgr(String mgrName, SharkTransaction trans) throws PersistenceException {
        return ProcessMgr.getInstance(mgrName);
    }

    public ProcessPersistenceInterface restoreProcess(String processId, SharkTransaction trans) throws PersistenceException {
        return Process.getInstance(processId);
    }

    public ActivityPersistenceInterface restoreActivity(String activityId, SharkTransaction trans) throws PersistenceException {
        return Activity.getInstance(activityId);
    }

    public ResourcePersistenceInterface restoreResource(String resourceId, SharkTransaction trans) throws PersistenceException {
        return Resource.getInstance(resourceId);
    }

    public AssignmentPersistenceInterface restoreAssignment(String activityId, String userName, SharkTransaction trans) throws PersistenceException {
        return Assignment.getInstance(activityId, userName);
    }

    public boolean restore(ProcessVariablePersistenceInterface processVariablePersistenceInterface, SharkTransaction trans) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: ProcessVariablePersistenceInterface ::", module);
        if (processVariablePersistenceInterface == null) {
            return false;
        }
        try {
            ((ProcessVariable) processVariablePersistenceInterface).reload();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new PersistenceException(e);
        }
        return true;
    }

    public boolean restore(ActivityVariablePersistenceInterface activityVariablePersistenceInterface, SharkTransaction trans) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: ActivityVariablePersistenceInterface ::", module);
        if (activityVariablePersistenceInterface == null) {
            return false;
        }
        try {
            ((ActivityVariable) activityVariablePersistenceInterface).reload();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new PersistenceException(e);
        }
        return true;
    }

    // remove/delete methods
    public void deleteProcessMgr(String mgrName, SharkTransaction trans) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: deleteProcessMgr ::", module);
        try {
            ((ProcessMgr) restoreProcessMgr(mgrName, trans)).remove();
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void deleteProcess(String processId, boolean admin, SharkTransaction trans) throws PersistenceException {
        // TODO: add configuration to remove process on complete
        if (admin) return;
        // TODO: add code to delete activities
        if (Debug.infoOn()) Debug.log(":: deleteProcess ::", module);
        try {
            ((Process) restoreProcess(processId, trans)).remove();
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void deleteActivity(String activityId, SharkTransaction trans) throws PersistenceException {
        // TODO: add code to delete assignments
        if (Debug.verboseOn()) Debug.log(":: deleteActivity ::", module);
        try {
            ((Activity) restoreActivity(activityId, trans)).remove();
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void deleteResource(String userName, SharkTransaction trans) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: deleteResource ::", module);
        try {
            ((Resource) restoreResource(userName, trans)).remove();
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void deleteAssignment(String activityId, String userName, SharkTransaction trans) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: deleteAssignment ::", module);
        try {
            ((Assignment) restoreAssignment(activityId, userName, trans)).remove();
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void deleteAndJoinEntries(String procId, String asDefId, String aDefId, SharkTransaction trans) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: deleteAndJoinEntries ::", module);
        GenericDelegator delegator = SharkContainer.getDelegator();
        try {
            delegator.removeByAnd("WfAndJoin", UtilMisc.toMap("processId", procId,
                    "activitySetDefId", asDefId, "activityDefId", aDefId));
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void deleteDeadlines(String procId, SharkTransaction trans) throws PersistenceException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        try {
            delegator.removeByAnd("WfDeadline", UtilMisc.toMap("processId", procId));
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void deleteDeadlines(String procId, String actId, SharkTransaction trans) throws PersistenceException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        try {
            delegator.removeByAnd("WfDeadline", UtilMisc.toMap("processId", procId, "activityId", actId));
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void delete(ProcessVariablePersistenceInterface processVariablePersistenceInterface, SharkTransaction trans) throws PersistenceException {
        // don't delete variables
    }

    public void delete(ActivityVariablePersistenceInterface activityVariablePersistenceInterface, SharkTransaction trans) throws PersistenceException {
        // don't delete variables
    }

    public List getAllProcessMgrs(SharkTransaction trans) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: getAllProcessMgrs ::", module);
        GenericDelegator delegator = SharkContainer.getDelegator();
        List createdList = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findAll("WfProcessMgr");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new PersistenceException(e);
        }
        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                createdList.add(ProcessMgr.getInstance(v));
            }
        }
        if (Debug.verboseOn()) Debug.log("ProcessMgr : " + createdList.size(), module);
        return createdList;
    }

    public List getAllResources(SharkTransaction trans) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: getAllResources ::", module);
        GenericDelegator delegator = SharkContainer.getDelegator();
        List createdList = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findAll("WfResource");
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                createdList.add(Resource.getInstance(v));
            }
        }
        return createdList;
    }

    public List getAllAssignments(SharkTransaction trans) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: getAllAssignments ::", module);
        GenericDelegator delegator = SharkContainer.getDelegator();
        List createdList = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findAll("WfAssignment");
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                createdList.add(Assignment.getInstance(v));
            }
        }
        return createdList;
    }

    public List getAllProcesses(SharkTransaction trans) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: getAllProcesses ::", module);
        GenericDelegator delegator = SharkContainer.getDelegator();
        List createdList = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findAll("WfProcess");
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                createdList.add(Process.getInstance(v));
            }
        }
        return createdList;
    }

    public List getAllActivities(SharkTransaction trans) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: getAllActivities ::", module);
        GenericDelegator delegator = SharkContainer.getDelegator();
        List createdList = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findAll("WfActivity");
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                createdList.add(Activity.getInstance(v));
            }
        }
        return createdList;
    }

    public List getAllProcessesForMgr(String mgrName, SharkTransaction trans) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: getAllProcessesForMgr ::", module);
        GenericDelegator delegator = SharkContainer.getDelegator();
        List createdList = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfProcess", UtilMisc.toMap("mgrName", mgrName));
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                createdList.add(Process.getInstance(v));
            }
        }
        return createdList;
    }

    public List getAllRunningProcesses(SharkTransaction trans) throws PersistenceException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        List runningStates = UtilMisc.toList("open.running");
        List order = UtilMisc.toList("startedTime");
        List createdList = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findByCondition("WfProcess",
                    makeStateListCondition("currentState", runningStates, EntityOperator.OR), null, order);
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
        if (!UtilValidate.isEmpty(lookupList)) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                createdList.add(Process.getInstance(v));
            }
        }
        return createdList;
    }

    public List getAllFinishedProcesses(SharkTransaction trans) throws PersistenceException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        List finsihedStates = UtilMisc.toList("closed.completed", "closed.terminated", "closed.aborted");
        List order = UtilMisc.toList("lastStateTime");
        List createdList = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findByCondition("WfProcess",
                    makeStateListCondition("currentState", finsihedStates, EntityOperator.OR), null, order);
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
        if (!UtilValidate.isEmpty(lookupList)) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                createdList.add(Process.getInstance(v));
            }
        }
        return createdList;
    }

    private EntityCondition makeStateListCondition(String field, List states, EntityJoinOperator op) throws GenericEntityException {
        if (states != null) {
            List exprs = new LinkedList();
            Iterator i = states.iterator();
            while (i.hasNext()) {
                exprs.add(new EntityExpr(field, EntityOperator.EQUALS, i.next()));
            }
            return new EntityConditionList(exprs, op);
        } else {
            throw new GenericEntityException("Cannot create entity condition from list :" + states);
        }
    }

    public List getAllActivitiesForProcess(String processId, SharkTransaction trans) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: getAllActivitiesForProcess ::", module);
        GenericDelegator delegator = SharkContainer.getDelegator();
        List createdList = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfActivity", UtilMisc.toMap("processId", processId));
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                createdList.add(Activity.getInstance(v));
            }
        }
        return createdList;
    }

    /**
    * Returns all assignments for the resource, no matter if its activity
    * is in "closed" state (or some of its sub-states).
    */
    public List getAllAssignmentsForResource(String user, SharkTransaction trans) throws PersistenceException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        List createdList = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfAssignment", UtilMisc.toMap("userName", user));
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                createdList.add(Assignment.getInstance(v));
            }
        }
        return createdList;
    }

    /**
    * Returns all assignments which activity is not in "closed" state, or some
    * of its sub-states.
    */
    public List getAllAssignmentsForNotClosedActivitiesForResource(String user, SharkTransaction trans) throws PersistenceException {
        List allAssignments = getAllAssignmentsForResource(user, trans);
        List notClosed = new ArrayList();
        Iterator i = allAssignments.iterator();
        while (i.hasNext()) {
            Assignment as = (Assignment) i.next();
            Activity at = Activity.getInstance(as.getActivityId());
            if (!at.getState().startsWith("closed")) {
                notClosed.add(as);
            }
        }
        return notClosed;
    }

    /**
    * Returns only the assignments that can be currently executed by the resource
    * with a given username. This means the ones which activity is not finished
    * and not accepted (it doesn't have getResourceUsername() field set), and the
    * ones which activity is accepted by this resource (its getResourceUsername()
    * field is set to the resource with given username).
    */
    public List getAllValidAssignmentsForResource(String user, SharkTransaction trans) throws PersistenceException {
        List allAssignments = getAllAssignmentsForResource(user, trans);
        List valid = new ArrayList();
        Iterator i = allAssignments.iterator();
        while (i.hasNext()) {
            Assignment as = (Assignment) i.next();
            Activity at = Activity.getInstance(as.getActivityId());
            if (!at.getState().startsWith("closed")) {
                if (at.getResourceUsername() == null || user.equals(at.getResourceUsername())) {
                    valid.add(as);
                }
            }
        }
        return valid;
    }

    /**
    * Returns all assignments that are ever created for that activity, no
    * matter if activity is already in "closed" state or some of its sub-states.
    */
    public List getAllAssignmentsForActivity(String activityId, SharkTransaction trans) throws PersistenceException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        List createdList = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfAssignment", UtilMisc.toMap("activityId", activityId));
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                createdList.add(Assignment.getInstance(v));
            }
        }
        return createdList;
    }

    /**
    * If activity is in "closed" state, or some of its sub-states, returns an
    * empty list, otherwise returns all assignments that are ever created for
    * that activity.
    */
    public List getAllAssignmentsForNotClosedActivity(String activityId, SharkTransaction trans) throws PersistenceException {
        Activity at = Activity.getInstance(activityId);
        if (at.getState().startsWith("closed")) {
            return new ArrayList();
        } else {
            return getAllAssignmentsForActivity(activityId, trans);
        }
    }

    /**
    * If activity is in "closed" state, or some of its sub-states, returns an
    * empty list, otherwise it returns either all assignments that are ever
    * created for that activity if activity is not accepted, or just the
    * assignment for the resource that accepted activity.
    */
    public List getAllValidAssignmentsForActivity(String activityId, SharkTransaction trans) throws PersistenceException {
        Activity at = Activity.getInstance(activityId);
        if (at.getState().startsWith("closed")) {
            return new ArrayList();
        }

        List assignments = getAllAssignmentsForActivity(activityId, trans);
        if (at.getResourceUsername() == null) {
            return assignments;
        }

        List valid = new ArrayList();
        Iterator i = assignments.iterator();
        while (i.hasNext()) {
            Assignment as = (Assignment) i.next();
            if (at.getResourceUsername().equals(as.getResourceUsername())) {
                valid.add(as);
            }
        }
        return valid;                    
    }

    public List getAllVariablesForProcess(String processId, SharkTransaction trans) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: getAllVariablesForProcess ::", module);
        GenericDelegator delegator = SharkContainer.getDelegator();
        List createdList = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfProcessVariable", UtilMisc.toMap("processId", processId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new PersistenceException(e);
        }
        if (lookupList != null && lookupList.size() > 0) {
            Debug.log("Lookup list contains : " + lookupList.size(), module);
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                createdList.add(ProcessVariable.getInstance(v));
            }
        } else {
            Debug.log("Lookup list empty", module);
        }

        if (Debug.verboseOn()) Debug.log("Returning list : " + createdList.size(), module);
        //Debug.log(new Exception(), "Stack Trace", module);
        return createdList;
    }

    public List getAllVariablesForActivity(String activityId, SharkTransaction trans) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: getAllVariablesForActivity ::", module);
        GenericDelegator delegator = SharkContainer.getDelegator();
        List createdList = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfActivityVariable", UtilMisc.toMap("activityId", activityId));
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                createdList.add(ActivityVariable.getInstance(v));
            }
        }
        return createdList;
    }

    public List getResourceRequestersProcessIds(String userName, SharkTransaction trans) throws PersistenceException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        List idList = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfProcess", UtilMisc.toMap("resourceReqId", userName));
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
        if (!UtilValidate.isEmpty(lookupList)) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                idList.add(v.getString("processId"));
            }
        }
        return idList;
    }

    public List getAndJoinEntries(String procId, String asDefId, String aDefId, SharkTransaction trans) throws PersistenceException {
        List createdList = new ArrayList();
        List lookupList = getAndJoinValues(procId, asDefId, aDefId);

        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                createdList.add(AndJoinEntry.getInstance(v));
            }
        }
        return createdList;
    }

    public int howManyAndJoinEntries(String procId, String asDefId, String aDefId, SharkTransaction trans) throws PersistenceException {
        List lookupList = getAndJoinValues(procId, asDefId, aDefId);
        return lookupList.size();
    }

    public List getAllDeadlinesForProcess(String procId, SharkTransaction trans) throws PersistenceException {
        List lookupList = getDeadlineValues(UtilMisc.toList(new EntityExpr("processId", EntityOperator.EQUALS, procId)));
        return getDealineObjects(lookupList);
    }

    public List getAllDeadlinesForProcess(String procId, long timeLimit, SharkTransaction trans) throws PersistenceException {
        List lookupList = getDeadlineValues(UtilMisc.toList(new EntityExpr("processId", EntityOperator.EQUALS, procId),
                new EntityExpr("timeLimit", EntityOperator.LESS_THAN, new Long(timeLimit))));
        return getDealineObjects(lookupList);
    }

    public List getAllDeadlinesForActivity(String procId, String actId, SharkTransaction trans) throws PersistenceException {
        List lookupList = getDeadlineValues(UtilMisc.toList(new EntityExpr("processId", EntityOperator.EQUALS, procId),
                new EntityExpr("activityId", EntityOperator.EQUALS, actId)));
        return getDealineObjects(lookupList);
    }

    public List getAllDeadlinesForActivity(String procId, String actId, long timeLimit, SharkTransaction trans) throws PersistenceException {
        List lookupList = getDeadlineValues(UtilMisc.toList(new EntityExpr("processId", EntityOperator.EQUALS, procId),
                new EntityExpr("activityId", EntityOperator.EQUALS, actId),
                new EntityExpr("timeLimit", EntityOperator.LESS_THAN, new Long(timeLimit))));
        return getDealineObjects(lookupList);
    }

    public int getExecuteCount(String procId, String asDefId, String aDefId, SharkTransaction trans) throws PersistenceException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        long count = 0;
        try {
            count = delegator.findCountByAnd("WfActivity", UtilMisc.toMap("processId", procId, "setDefinitionId", asDefId, "definitionId", aDefId));
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }

        return (int) count;
    }

    private List getAndJoinValues(String processId, String activitySetDefId, String activityDefId) throws PersistenceException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfAndJoin", UtilMisc.toMap("processId", processId,
                    "activitySetDefId", activitySetDefId, "activityDefId", activityDefId));
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
        if (lookupList == null) {
            lookupList = new ArrayList();
        }
        return lookupList;
    }

    private List getDeadlineValues(List exprList) throws PersistenceException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        List lookupList = null;
        if (exprList == null) {
            lookupList = new ArrayList();
        } else {
            try {
                lookupList = delegator.findByAnd("WfDeadline", exprList);
            } catch (GenericEntityException e) {
                throw new PersistenceException(e);
            }
            if (lookupList == null) {
                lookupList = new ArrayList();
            }
        }
        return lookupList;
    }

    private List getDealineObjects(List deadlineValues) {
        List deadlines = new ArrayList();
        if (deadlineValues != null) {
            Iterator i = deadlineValues.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                deadlines.add(Deadline.getInstance(v));
            }
        }
        return deadlines;
    }

    // create methods
    public ActivityPersistenceInterface createActivity() {
        return new Activity(SharkContainer.getDelegator());
    }

    public ProcessPersistenceInterface createProcess() {
        return new Process(SharkContainer.getDelegator());
    }

    public ProcessMgrPersistenceInterface createProcessMgr() {
        return new ProcessMgr(SharkContainer.getDelegator());
    }

    public AssignmentPersistenceInterface createAssignment() {
        return new Assignment(SharkContainer.getDelegator());
    }

    public ResourcePersistenceInterface createResource() {
        return new Resource(SharkContainer.getDelegator());
    }

    public ProcessVariablePersistenceInterface createProcessVariable() {
        return new ProcessVariable(SharkContainer.getDelegator());
    }

    public ActivityVariablePersistenceInterface createActivityVariable() {
        return new ActivityVariable(SharkContainer.getDelegator());
    }

    public AndJoinEntryInterface createAndJoinEntry() {
        return new AndJoinEntry(SharkContainer.getDelegator());
    }

    public DeadlinePersistenceInterface createDeadline() {
        return new Deadline(SharkContainer.getDelegator());
    }

    public synchronized String getNextId(String string) throws PersistenceException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        return delegator.getNextSeqId("SharkWorkflowSeq").toString();        
    }
}
