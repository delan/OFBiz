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

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.service.job.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.workflow.*;
import org.ofbiz.core.workflow.client.*;

/**
 * WfProcessImpl - Workflow Process Object implementation
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @author     David Ostrovsky (d.ostrovsky@gmx.de) 
 * @version    $Revision$
 * @since      2.0
 */
public class WfProcessImpl extends WfExecutionObjectImpl implements WfProcess {

    public static final String module = WfProcessImpl.class.getName();

    private WfRequester requester = null;
    private WfProcessMgr manager = null;

    /**
     * Method WfProcessImpl.
     * @param valueObject
     * @param manager
     * @throws WfException
     */
    public WfProcessImpl(GenericValue valueObject, WfProcessMgr manager) throws WfException {
        super(valueObject, null);
        this.manager = manager;
        this.requester = null;
    }

    /**
     * @see org.ofbiz.core.workflow.impl.WfExecutionObjectImpl#WfExecutionObjectImpl(org.ofbiz.core.entity.GenericDelegator, java.lang.String)
     */
    public WfProcessImpl(GenericDelegator delegator, String workEffortId) throws WfException {
        super(delegator, workEffortId);
        if (activityId != null && activityId.length() > 0)
            throw new WfException("Execution object is not of type WfProcess.");
        this.manager = WfFactory.getWfProcessMgr(delegator, packageId, packageVersion, processId, processVersion);
        this.requester = null;
    }

    /**
     * @see org.ofbiz.core.workflow.WfProcess#setRequester(org.ofbiz.core.workflow.WfRequester)
     */
    public void setRequester(WfRequester newValue) throws WfException, CannotChangeRequester {
        if (requester != null)
            throw new CannotChangeRequester();
        requester = newValue;
    }

    /**
     * @see org.ofbiz.core.workflow.WfProcess#getSequenceStep(int)
     */
    public List getSequenceStep(int maxNumber) throws WfException {
        if (maxNumber > 0)
            return new ArrayList(activeSteps().subList(0, maxNumber - 1));
        return activeSteps();
    }
  
    /**
     * @see org.ofbiz.core.workflow.WfProcess#start()
     */
    public void start() throws WfException, CannotStart, AlreadyRunning {
        if (state().equals("open.running"))
            throw new AlreadyRunning("Process is already running");

        if (getDefinitionObject().get("defaultStartActivityId") == null)
            throw new CannotStart("Initial activity is not defined.");

        changeState("open.running");

        // start the first activity (using the defaultStartActivityId)
        GenericValue start = null;

        try {
            start = getDefinitionObject().getRelatedOne("DefaultStartWorkflowActivity");
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e.getNested());
        }
        if (start == null)
            throw new CannotStart("No initial activity set");

        if (Debug.verboseOn()) 
            Debug.logVerbose("[WfProcess.start] : Started the workflow process.", module);
        startActivity(start);
    }
  
    /**
     * @see org.ofbiz.core.workflow.WfProcess#manager()
     */
    public WfProcessMgr manager() throws WfException {
        return manager;
    }
    
    /**
     * @see org.ofbiz.core.workflow.WfProcess#requester()
     */
    public WfRequester requester() throws WfException {
        return requester;
    }
   
    /**
     * @see org.ofbiz.core.workflow.WfProcess#getIteratorStep()
     */
    public Iterator getIteratorStep() throws WfException {
        return activeSteps().iterator();
    }
   
    /**
     * @see org.ofbiz.core.workflow.WfProcess#isMemberOfStep(org.ofbiz.core.workflow.WfActivity)
     */
    public boolean isMemberOfStep(WfActivity member) throws WfException {
        return activeSteps().contains(member);
    }
    
    /**
     * @see org.ofbiz.core.workflow.WfProcess#getActivitiesInState(java.lang.String)
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
     * @see org.ofbiz.core.workflow.WfProcess#result()
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
     * @see org.ofbiz.core.workflow.WfProcess#howManyStep()
     */
    public int howManyStep() throws WfException {
        return activeSteps().size();
    }
  
    /**
     * @see org.ofbiz.core.workflow.WfProcess#receiveResults(org.ofbiz.core.workflow.WfActivity, java.util.Map)
     */
    public synchronized void receiveResults(WfActivity activity, Map results) throws WfException, InvalidData {
        Map context = processContext();
        context.putAll(results);
        setSerializedData(context);
    }
    
    /**
     * @see org.ofbiz.core.workflow.WfProcess#activityComplete(org.ofbiz.core.workflow.WfActivity)
     */
    public synchronized void activityComplete(WfActivity activity) throws WfException {
        if (!activity.state().equals("closed.completed"))
            throw new WfException("Activity state is not completed");
        if (Debug.verboseOn()) Debug.logVerbose("[WfProcess.activityComplete] : Activity (" + activity.name() + ") is complete", module);
        queueNext(activity);
    }

    /**
     * @see org.ofbiz.core.workflow.impl.WfExecutionObjectImpl#executionObjectType()
     */
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

                if (Debug.verboseOn()) Debug.logVerbose("[WfProcess.queueNext] : " + join + " join.", module);

                // activate if XOR or test the join transition(s)
                if (join.equals("WJT_XOR"))
                    startActivity(toActivity);
                else
                    joinTransition(toActivity, trans);
            }
        } else {
            if (Debug.verboseOn()) 
                Debug.logVerbose("[WfProcess.queueNext] : No transitions left to follow.", module);
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

        if (Debug.verboseOn()) Debug.logVerbose("[WfProcess.joinTransition] : toTrans (" + toTrans.size() + ") followed (" +
                (followed.size() + 1) + ")", module);

        // check to see if all transition requirements are met
        if (toTrans.size() == (followed.size() + 1)) {
            Debug.logVerbose("[WfProcess.joinTransition] : All transitions have followed.", module);
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
            Debug.logVerbose("[WfProcess.joinTransition] : Waiting for transitions to finish.", module);
            try {
                Map fields = new HashMap();
                fields.put("processWorkEffortId", dataObject.getString("workEffortId"));
                fields.put("toActivityId", toActivity.getString("activityId"));
                fields.put("transitionId", transition.getString("transitionId"));
                GenericValue obj = getDelegator().makeValue("WorkEffortTransBox", fields);

                getDelegator().create(obj);
            } catch (GenericEntityException e) {
                throw new WfException(e.getMessage(), e);
            }
        }
    }

    // Activates an activity object
    private void startActivity(GenericValue value) throws WfException {
        WfActivity activity = WfFactory.getWfActivity(value, workEffortId);
        GenericResultWaiter req = new GenericResultWaiter();

        if (Debug.verboseOn()) Debug.logVerbose("[WfProcess.startActivity] : Attempting to start activity (" + activity.name() + ")", module);
        
        // using the StartActivityJob class to run the activity within its own thread        
        try {            
            Job activityJob = new StartActivityJob(activity, req);            
            this.getDispatcher().getJobManager().runJob(activityJob);  
        } catch (JobManagerException e) {
            throw new WfException("Problems with job queue", e);
        }
         
        // the GenericRequester object will hold any exceptions; and report the job as failed       
        if (req.status() == GenericResultWaiter.SERVICE_FAILED) {
            Exception excep = req.getException();
            if (excep instanceof CannotStart)
                Debug.logVerbose("[WfProcess.startActivity] : Cannot start activity. Waiting for manual start.", module);
            else if (excep instanceof AlreadyRunning) 
                throw new WfException("Activity already running", excep);
            else            
                throw new WfException("Activity error", excep);
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
        GenericValue otherwise = null;

        Iterator fromIt = fromCol.iterator();

        while (fromIt.hasNext()) {
            GenericValue transition = (GenericValue) fromIt.next();

            if (transition.get("conditionTypeEnumId") != null &&
                transition.getString("conditionTypeEnumId").equals("WTC_OTHERWISE")) {
                otherwise = transition;
                continue;
            }
            // evaluate the condition expression
            transitionOk = evalCondition(transition.getString("conditionExpr").trim());
            if (transitionOk) {
                transList.add(transition);
                if (split.equals("WST_XOR"))
                    break;
            }
        }

        // we only use otherwise transitions for XOR splits
        if (split.equals("WST_XOR") && transList.size() == 0 && otherwise != null) {
            transList.add(otherwise);
            Debug.logVerbose("Used OTHERWISE Transition.", module);
        }

        if (Debug.verboseOn()) Debug.logVerbose("[WfProcess.getTransFrom] : Transitions: " + transList.size(), module);
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

    // Complete this workflow
    private void finishProcess() throws WfException {
        changeState("closed.completed");
        Debug.logVerbose("[WfProcess.finishProcess] : Workflow Complete. Calling back to requester.", module);
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
            c = getDelegator().findByAnd("WorkEffort", UtilMisc.toMap("workEffortParentId", runtimeKey()));
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

