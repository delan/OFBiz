/*
 * $Id: EntityPersistentManager.java,v 1.1 2004/04/22 15:41:01 ajzeneski Exp $
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

import org.enhydra.shark.api.internal.instancepersistence.*;
import org.enhydra.shark.api.internal.working.CallbackUtilities;
import org.enhydra.shark.api.RootException;
import org.enhydra.shark.api.SharkTransaction;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.shark.container.SharkContainer;

/**
 * Shark Persistance Manager Implementation
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class EntityPersistentManager implements PersistentManagerInterface {

    public static final String module = EntityPersistentManager.class.getName();

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

    public void persist(AssignmentEventAuditPersistenceInterface assignmentEvent, SharkTransaction trans) throws PersistenceException {
        try {
            ((AssignmentEventAudit) assignmentEvent).store();
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void persist(CreateProcessEventAuditPersistenceInterface processEvent, SharkTransaction trans) throws PersistenceException {
        try {
            ((CreateProcessEventAudit) processEvent).store();
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void persist(DataEventAuditPersistenceInterface dataEvent, SharkTransaction trans) throws PersistenceException {
        try {
            ((DataEventAudit) dataEvent).store();
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
    }

    public void persist(StateEventAuditPersistenceInterface stateEvent, SharkTransaction trans) throws PersistenceException {
        try {
            ((StateEventAudit) stateEvent).store();
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

    public boolean restore(AssignmentEventAuditPersistenceInterface assignmentEventAuditPersistenceInterface, SharkTransaction trans) throws PersistenceException {
        if (assignmentEventAuditPersistenceInterface == null) {
            return false;
        }
        return true;
    }

    public boolean restore(CreateProcessEventAuditPersistenceInterface createProcessEventAuditPersistenceInterface, SharkTransaction trans) throws PersistenceException {
        if (createProcessEventAuditPersistenceInterface == null) {
            return false;
        }
        return true;
    }

    public boolean restore(DataEventAuditPersistenceInterface dataEventAuditPersistenceInterface, SharkTransaction trans) throws PersistenceException {
        if (dataEventAuditPersistenceInterface == null) {
            return false;
        }
        return true;
    }

    public boolean restore(StateEventAuditPersistenceInterface stateEventAuditPersistenceInterface, SharkTransaction trans) throws PersistenceException {
        if (stateEventAuditPersistenceInterface == null) {
            return false;
        }
        return true;
    }

    // history methods
    public List restoreProcessHistory(String processId, SharkTransaction trans) throws PersistenceException {
        List processHistory = new ArrayList();
        processHistory.addAll(getCreateProcessEvents(processId));
        processHistory.addAll(getProcessDataEvents(processId));
        processHistory.addAll(getProcessStateEvents(processId));
        if (Debug.verboseOn()) Debug.log(":: restoreProcessHistory :: " + processHistory.size(), module);
        return processHistory;
    }

    public List restoreActivityHistory(String processId, String activityId, SharkTransaction trans) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: restoreActivityHistory ::", module);
        List activityHistory = new ArrayList();
        activityHistory.addAll(getAssignmentEvents(processId, activityId));
        activityHistory.addAll(getActivityDataEvents(processId, activityId));
        activityHistory.addAll(getActivityStateEvents(processId, activityId));
        if (Debug.verboseOn()) Debug.log(":: restoreActivityHistory :: " + activityHistory.size(), module);
        return activityHistory;
    }

    // process history
    private List getCreateProcessEvents(String processId) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: getCreateProcessEvents ::", module);
        GenericDelegator delegator = SharkContainer.getDelegator();
        List createProcessEvents = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfEventAudit", UtilMisc.toMap("auditType", "processCreated", "processId", processId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new PersistenceException(e);
        }
        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                if (v != null) {
                    createProcessEvents.add(new CreateProcessEventAudit(delegator, v.getString("eventAuditId")));
                }
            }
        }
        return createProcessEvents;
    }

    private List getProcessStateEvents(String processId) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: getProcessStateEvents ::", module);
        GenericDelegator delegator = SharkContainer.getDelegator();
        List stateEvents = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfEventAudit", UtilMisc.toMap("auditType", "processStateChanged", "processId", processId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new PersistenceException(e);
        }
        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                if (v != null) {
                    stateEvents.add(new StateEventAudit(delegator, v.getString("eventAuditId")));
                }
            }
        }
        return stateEvents;
    }

    private List getProcessDataEvents(String processId) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: getProcessDataEvents ::", module);
        GenericDelegator delegator = SharkContainer.getDelegator();
        List dataEvents = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfEventAudit", UtilMisc.toMap("auditType", "processContextChanged", "processId", processId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new PersistenceException(e);
        }
        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                if (v != null) {
                    dataEvents.add(new DataEventAudit(delegator, v.getString("eventAuditId")));
                }
            }
        }
        return dataEvents;
    }

    // activity history
    private List getAssignmentEvents(String processId, String activityId) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: getAssignmentEvents ::", module);
        GenericDelegator delegator = SharkContainer.getDelegator();
        List assignmentEvents = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfEventAudit", UtilMisc.toMap("auditType", "activityAssignmentChanged", "processId", processId, "activityId", activityId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new PersistenceException(e);
        }
        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                if (v != null) {
                    assignmentEvents.add(new AssignmentEventAudit(delegator, v.getString("eventAuditId")));
                }
            }
        }
        return assignmentEvents;
    }

    private List getActivityStateEvents(String processId, String activityId) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: getActivityStateEvents ::", module);
        GenericDelegator delegator = SharkContainer.getDelegator();
        List stateEvents = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfEventAudit", UtilMisc.toMap("auditType", "activityStateChanged", "processId", processId, "activityId", activityId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new PersistenceException(e);
        }
        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                if (v != null) {
                    stateEvents.add(new StateEventAudit(delegator, v.getString("eventAuditId")));
                }
            }
        }
        return stateEvents;
    }

    private List getActivityDataEvents(String processId, String activityId) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: getActivityDataEvents ::", module);
        GenericDelegator delegator = SharkContainer.getDelegator();
        List dataEvents = new ArrayList();
        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfEventAudit", UtilMisc.toMap("auditType", "activityContextChanged", "processId", processId, "activityId", activityId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new PersistenceException(e);
        }
        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                if (v != null) {
                    dataEvents.add(new DataEventAudit(delegator, v.getString("eventAuditId")));
                }
            }
        }
        return dataEvents;
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

    public void deleteProcess(String processId, SharkTransaction trans) throws PersistenceException {
        // TODO: add configuration to remove process on complete
        if (true) return;
        // TODO: add code to delete activities
        if (Debug.verboseOn()) Debug.log(":: deleteProcess ::", module);
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

    public void deleteAndJoinEntries(String processId, String activityDefId, SharkTransaction trans) throws PersistenceException {
        if (Debug.verboseOn()) Debug.log(":: deleteAndJoinEntries ::", module);
        List andJoinList = getAndJoinEntries(processId, activityDefId, trans);
        if (andJoinList != null && andJoinList.size() > 0) {
            Iterator i = andJoinList.iterator();
            while (i.hasNext()) {
                AndJoinEntry aje = (AndJoinEntry) i.next();
                try {
                    aje.remove();
                } catch (GenericEntityException e) {
                    throw new PersistenceException(e);
                }
            }
        }
    }

    public void delete(ProcessVariablePersistenceInterface processVariablePersistenceInterface, SharkTransaction trans) throws PersistenceException {
        // don't delete variables
    }

    public void delete(ActivityVariablePersistenceInterface activityVariablePersistenceInterface, SharkTransaction trans) throws PersistenceException {
        // don't delete variables
    }

    public void delete(AssignmentEventAuditPersistenceInterface assignmentEventAuditPersistenceInterface, SharkTransaction trans) throws PersistenceException {
        // don't delete events
    }

    public void delete(CreateProcessEventAuditPersistenceInterface createProcessEventAuditPersistenceInterface, SharkTransaction trans) throws PersistenceException {
        // don't delete events
    }

    public void delete(DataEventAuditPersistenceInterface dataEventAuditPersistenceInterface, SharkTransaction trans) throws PersistenceException {
        // don't delete events
    }

    public void delete(StateEventAuditPersistenceInterface stateEventAuditPersistenceInterface, SharkTransaction trans) throws PersistenceException {
        // don't delete events
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
        return null;
    }

    public List getAndJoinEntries(String processId, String activityDefId, SharkTransaction trans) throws PersistenceException {
        List createdList = new ArrayList();
        List lookupList = getAndJoinValues(processId, activityDefId);

        if (lookupList != null && lookupList.size() > 0) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                createdList.add(AndJoinEntry.getInstance(v));
            }
        }
        return createdList;
    }

    public int howManyAndJoinEntries(String processId, String activityDefId, SharkTransaction trans) throws PersistenceException {
        List lookupList = getAndJoinValues(processId, activityDefId);
        return lookupList.size();
    }

    public int getExecuteCount(String processId, String activityDefId, SharkTransaction trans) throws PersistenceException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        long count = 0;
        try {
            count = delegator.findCountByAnd("WfActivity", UtilMisc.toMap("processId", processId, "definitionId", activityDefId));
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }

        return (int) count;
    }

    private List getAndJoinValues(String processId, String activityDefId) throws PersistenceException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfAndJoin", UtilMisc.toMap("processId", processId, "activityDefId", activityDefId));
        } catch (GenericEntityException e) {
            throw new PersistenceException(e);
        }
        if (lookupList == null) {
            lookupList = new ArrayList();
        }
        return lookupList;
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

    public AssignmentEventAuditPersistenceInterface createAssignmentEventAudit() {
        return new AssignmentEventAudit(SharkContainer.getDelegator());
    }

    public CreateProcessEventAuditPersistenceInterface createCreateProcessEventAudit() {
        return new CreateProcessEventAudit(SharkContainer.getDelegator());
    }

    public DataEventAuditPersistenceInterface createDataEventAudit() {
        return new DataEventAudit(SharkContainer.getDelegator());
    }

    public StateEventAuditPersistenceInterface createStateEventAudit() {
        return new StateEventAudit(SharkContainer.getDelegator());
    }

    public AndJoinEntryInterface createAndJoinEntry() {
        return new AndJoinEntry(SharkContainer.getDelegator());
    }

    public synchronized String getNextId(String string) throws PersistenceException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        return delegator.getNextSeqId("SharkWorkflowSeq").toString();        
    }
}
