/*
 * $Id$
 *
 * Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.workflow.impl;

import bsh.*;

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.workflow.*;

/**
 * WfProcessImpl - Workflow Process Object implementation
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@author     David Ostrovsky (d.ostrovsky@gmx.de)
 *@created    December 18, 2001
 *@version    1.2
 */

public class WfProcessImpl extends WfExecutionObjectImpl implements WfProcess {

    public static final String module = WfProcessImpl.class.getName();

    private WfRequester requester;
    private WfProcessMgr manager;

    /**
     * Creates new WfProcessImpl
     * @param valueObject The GenericValue object of this WfProcess.
     * @param manager The WfProcessMgr invoking this process.
     */
    public WfProcessImpl(GenericValue valueObject, WfProcessMgr manager) throws WfException {
        super(valueObject, null);
        this.manager = manager;
        this.requester = null;
    }

    /**
     * Creates new WfProcessImpl
     * @param delegator The GenericDelegator to be used with this process
     * @param workEffortId The WorkEffort ID of this process
     * @throws WfException
     */
    public WfProcessImpl(GenericDelegator delegator, String workEffortId) throws WfException {
        super(delegator, workEffortId);
        this.manager = WfFactory.getWfProcessMgr(delegator, packageId, processId);
        this.requester = null;
    }

    /**
     * Set the originator of this process.
     * @param newValue The Requestor of this process.
     * @throws WfException General workflow exception.
     * @throws CannotChangeRequester Requestor cannot be changed.
     */
    public void setRequester(WfRequester newValue) throws WfException, CannotChangeRequester {

        requester = newValue;
    }

    /**
     * Retrieve the List of activities of this process.
     * @param maxNumber High limit of elements in the result set.
     * @throws WfException General workflow exception.
     * @return List of WfActivity objects.
     */
    public List getSequenceStep(int maxNumber) throws WfException {
        if (maxNumber > 0)
            return new ArrayList(activeSteps().subList(0, maxNumber - 1));
        return activeSteps();
    }

    /**
     * Start the process.
     * @throws WfException General workflow exception.
     * @throws CannotStart Process cannot be started.
     * @throws AlreadyRunning Process is already running.
     */
    public void start() throws WfException, CannotStart, AlreadyRunning {
        if (workflowStateType().equals("open.running"))
            throw new AlreadyRunning("Process is already running");

        if (getDefinitionObject().get("defaultStartActivityId") == null)
            throw new CannotStart("Initial activity is not defined");

        changeState("open.running");

        // start the first activity (using the defaultStartActivityId of this definition)
        GenericValue start = null;
        try {
            start = getDefinitionObject().getRelatedOne("DefaultStartWorkflowActivity");
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
        if (start == null)
            throw new CannotStart("No initial activity set");

        startActivity(start);
    }

    /**
     * Retrieve the WfProcessMgr of this process.
     * @throws WfException General workflow exception.
     * @return WfProcessMgr
     */
    public WfProcessMgr manager() throws WfException {
        return manager;
    }

    /**
     * Retrieve the requestor of this process.
     * @throws WfException General workflow exception.
     * @return WfRequestor of this process.
     */
    public WfRequester requester() throws WfException {
        return requester;
    }

    /**
     * Retrieve the Iterator of active activities of this process.
     * @throws WfException General workflow exception.
     * @return Iterator of WfActivity objects.
     */
    public Iterator getIteratorStep() throws WfException {
        return activeSteps().iterator();
    }

    /**
     * Check if some activity is a member of this process.
     * @param member Some activity.
     * @throws WfException General workflow exception.
     * @return true if the specific activity is amember of this process,
     * false otherwise.
     */
    public boolean isMemberOfStep(WfActivity member) throws WfException {
        return activeSteps().contains(member);
    }

    /**
     * Retrieve the iterator of activities in some specific state.
     * @param state Specific state.
     * @throws WfException General workflow exception.
     * @throws InvalidState State is invalid.
     * @return Iterator of activities in specific state
     */
    public Iterator getActivitiesInState(String state) throws WfException, InvalidState {
        ArrayList res = new ArrayList();
        Iterator i = getIteratorStep();
        while (i.hasNext()) {
            WfActivity a = (WfActivity) i.next();
            if (a.state().equals(state))
                res.add(a);
        }
        return res.iterator();
    }

    /**
     * Retrieve the result for this process.
     * @throws WfException General workflow exception.
     * @throws ResultNotAvailable No result is available.
     * @return Result Map.
     */
    public Map result() throws WfException, ResultNotAvailable {
        Map resultSig = manager().resultSignature();
        Map results = new HashMap();
        Map context = processContext();
        if (resultSig != null) {
            Set resultKeys = resultSig.keySet();
            Iterator i = resultKeys.iterator();
            while (i.hasNext()) {
                Object key = i.next();
                if (context.containsKey(key))
                    results.put(key, context.get(key));
            }
        }
        return results;
    }

    /**
     * Retrieve the amount of activities in this process.
     * @throws WfException General workflow exception.
     * @return Number of activities of this process
     */
    public int howManyStep() throws WfException {
        return activeSteps().size();
    }

    /**
     * Receives activity results.
     * @param activity WfActivity sending the results.
     * @param results Map of the results.
     * @throws WfException
     */
    public synchronized void receiveResults(WfActivity activity, Map results) throws WfException, InvalidData {
        Map context = processContext();
        context.putAll(results);
        setSerializedData(context);
    }

    /**
     * Receives notification when an activity has completed.
     * @param activity WfActivity which has completed.
     * @throws WfException
     */
    public synchronized void activityComplete(WfActivity activity) throws WfException {
        if (!activity.state().equals("closed.completed"))
            throw new WfException("Activity state is not completed");
        Debug.logInfo("Activity: " + activity.name() + " is complete", module);
        queueNext(activity);
    }

    public String executionObjectType() {
        return "WfProcess";
    }

    // Queues the next activities for processing
    private void queueNext(WfActivity fromActivity) throws WfException {
        List nextTrans = getTransFrom(fromActivity);
        if (nextTrans.size() > 0) {
            Iterator i = nextTrans.iterator();
            while (i.hasNext()) {
                GenericValue trans = (GenericValue) i.next();

                // Get the activity definition
                GenericValue toActivity = null;
                try {
                    toActivity = trans.getRelatedOne("ToWorkflowActivity");
                } catch (GenericEntityException e) {
                    throw new WfException(e.getMessage(), e);
                }

                // check for a join
                String join = "WJT_AND"; // default join is AND
                if (toActivity.get("joinTypeEnumId") != null)
                    join = toActivity.getString("joinTypeEnumId");

                // activate if XOR or test the join transition(s)
                if (join.equals("WJT_XOR"))
                    startActivity(toActivity);
                else
                    joinTransition(toActivity, trans);
            }
        } else {
            this.finishProcess();
        }
    }

    // Follows the and-join transition
    private void joinTransition(GenericValue toActivity,
                                GenericValue transition) throws WfException {
        // get all TO transitions to this activity
        GenericValue dataObject = getRuntimeObject();
        Collection toTrans = null;
        try {
            toTrans = toActivity.getRelated("ToWorkflowTransition");
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }

        // get a list of followed transition to this activity
        Collection followed = null;
        try {
            Map fields = new HashMap();
            fields.put("processWorkEffortId", dataObject.getString("workEffortId"));
            fields.put("toActivityId", toActivity.getString("activityId"));
            followed = getDelegator().findByAnd("WorkEffortTransBox", fields);
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }

        // check to see if all transition requirements are met
        if (toTrans.size() == (followed.size() + 1)) {
            startActivity(toActivity);
            try {
                Map fields = new HashMap();
                fields.put("processWorkEffortId", dataObject.getString("workEffortId"));
                fields.put("toActivityId", toActivity.getString("activityId"));
                getDelegator().removeByAnd("WorkEffortTransBox", fields);
            } catch (GenericEntityException e) {
                throw new WfException(e.getMessage(), e);
            }
        } else {
            try {
                Map fields = new HashMap();
                fields.put("processWorkEffortId", dataObject.getString("workEffortId"));
                fields.put("toActivityId", toActivity.getString("activityId"));
                fields.put("transitionId", transition.getString("transitionId"));
                GenericValue obj =
                        getDelegator().makeValue("WorkEffortTransBox", fields);
                getDelegator().create(obj);
            } catch (GenericEntityException e) {
                throw new WfException(e.getMessage(), e);
            }
        }
    }

    // Activates an activity object
    private void startActivity(GenericValue value) throws WfException {
        WfActivity activity = WfFactory.getWfActivity(value, workEffortId);
        activity.setServiceLoader(getServiceLoader());
        activity.setProcessContext(contextKey());
        try {
            activity.activate();
        } catch (AlreadyRunning e) {
            throw new WfException(e.getMessage(), e);
        } catch (CannotStart e) {
            throw new WfException(e.getMessage(), e);
        }
    }

    // Determine the next activity or activities
    private List getTransFrom(WfActivity fromActivity) throws WfException {
        List transList = new ArrayList();
        // get the from transitions
        Collection fromCol = null;
        try {
            fromCol = fromActivity.getDefinitionObject().getRelated("FromWorkflowTransition");
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }

        // check for a split
        String split = "WST_AND"; // default split is AND
        if (fromActivity.getDefinitionObject().get("splitTypeEnumId") != null)
            split = fromActivity.getDefinitionObject().getString("splitTypeEnumId");

        // Iterate through the possible transitions
        boolean transitionOk = false;
        Iterator fromIt = fromCol.iterator();
        while (fromIt.hasNext()) {
            GenericValue transition = (GenericValue) fromIt.next();
            // evaluate the condition expression
            transitionOk = evalCondition(transition.getString("conditionExpr"));
            if (transitionOk) {
                transList.add(transition);
                if (split.equals("WST_XOR"))
                    break;
            }
        }

        Debug.logInfo("Transitions: " + transList.size(), module);
        return transList;
    }

    // Gets a specific activity by its key
    private WfActivity getActivity(String key) throws WfException {
        Iterator i = getIteratorStep();
        while (i.hasNext()) {
            WfActivity a = (WfActivity) i.next();
            if (a.key().equals(key))
                return a;
        }
        throw new WfException("Activity not an active member of this process");
    }

    // Evaluate the transition condition
    private boolean evalCondition(String condition) throws WfException {
        Map context = processContext();
        Interpreter bsh = new Interpreter();
        Object o = null;
        if (condition == null || condition.equals(""))
            return true;
        try {
            // Set the context for the condition
            Set keySet = context.keySet();
            Iterator i = keySet.iterator();
            while (i.hasNext()) {
                Object key = i.next();
                Object value = context.get(key);
                bsh.set((String) key, value);
            }
            // evaluate the condition
            o = bsh.eval(condition);
        } catch (EvalError e) {
            return false;
        }
        if (o instanceof Number)
            return (((Number) o).doubleValue() == 0) ? false : true;
        else
            return (!o.toString().equalsIgnoreCase("true")) ? false : true;
    }

    // Complete this workflow
    private void finishProcess() throws WfException {
        changeState("closed.completed");
        if (requester != null) {
            WfEventAudit audit = WfFactory.getWfEventAudit(this, null); // this will need to be updated
            try {
                requester.receiveEvent(audit);
            } catch (InvalidPerformer e) {
                throw new WfException(e.getMessage(), e);
            }
        }
    }

    // Get the active process activities
    private List activeSteps() throws WfException {
        List steps = new ArrayList();
        Collection c = null;
        try {
            c = getDelegator().findByAnd("WorkEffort",
                                         UtilMisc.toMap("workEffortParentId", runtimeKey()));
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
        if (c == null)
            return steps;
        Iterator i = c.iterator();
        while (i.hasNext()) {
            GenericValue v = (GenericValue) i.next();
            if (v.get("currentStatusId") != null &&
                    getOMGStatus(v.getString("currentStatusId")).startsWith("open."))
                steps.add(WfFactory.getWfActivity(getDelegator(), v.getString("workEffortId")));
        }
        return steps;
    }
}

