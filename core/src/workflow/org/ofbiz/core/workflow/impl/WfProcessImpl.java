/*
 * $Id$
 */

package org.ofbiz.core.workflow.impl;

import bsh.*;
import java.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.workflow.*;

/**
 * <p><b>Title:</b> WfProcessImpl
 * <p><b>Description:</b> Workflow Process Object implementation
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
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
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@author     David Ostrovsky (d.ostrovsky@gmx.de)
 *@created    November 15, 2001
 *@version    1.0
 */

public class WfProcessImpl extends WfExecutionObjectImpl implements WfProcess {
        
    private WfRequester requester;
    private WfProcessMgr manager;
    private List steps;
    private Map result;   
    
    /**
     * Creates new WfProcessImpl
     * @param valueObject The GenericValue object of this WfProcess.
     * @param dataObject The GenericValue object of the stored runtime data.
     * @param manager The WfProcessMgr invoking this process.
     */
    public WfProcessImpl(GenericValue valueObject, GenericValue dataObject, WfProcessMgr manager) throws WfException {
        super(valueObject,dataObject);
        this.manager = manager;
        this.requester = null;
        this.result = new HashMap();
        this.steps = new ArrayList();                
        makeSteps();
    }
    
    // Build the steps from the definition
    private void makeSteps() throws WfException {
        // Build up the activities (steps)
        Collection activityEntities = null;
        try {
            activityEntities = valueObject.getRelated("WorkflowActivity");
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
        if ( activityEntities != null ) {
            Iterator i = activityEntities.iterator();
            while ( i.hasNext() ) {
                GenericValue value = (GenericValue) i.next();
                GenericValue data = null;
                
                // Check for stored runtime info
                if ( dataObject != null ) {
                    Collection c = null;
                    try {
                        c = dataObject.getRelated("ParentWorkEffort");
                    }
                    catch ( GenericEntityException e ) {
                        throw new WfException(e.getMessage(),e);
                    }
                    Iterator di = c.iterator();
                    while ( di.hasNext() && data == null ) {
                        GenericValue obj = (GenericValue) di.next();
                        if ( obj.getString("workflowActivityId").equals(value.getString("activityId")) )
                            data = obj;
                    }
                }
                
                WfActivity activity = WfFactory.newWfActivity(value,dataObject,this); // create the activity object
                activity.setDispatcher(dispatcher,serviceLoader);                                 // set the dispatcher for the activity
                steps.add(activity);               
            }
        }
    }
    
    /**
     * Set the originator of this process.
     * @param newValue The Requestor of this process.
     * @throws WfException General workflow exception.
     * @throws CannotChangeRequester Requestor cannot be changed.
     */
    public void setRequester(WfRequester newValue) throws WfException,
    CannotChangeRequester {
        requester = newValue;
    }
    
    /**
     * Retrieve the List of activities of this process.
     * @param maxNumber High limit of elements in the result set.
     * @throws WfException General workflow exception.
     * @return List of WfActivity objects.
     */
    public List getSequenceStep(int maxNumber) throws WfException {
        if ( maxNumber > 0 )
            return new ArrayList(steps.subList(0, maxNumber-1));
        return steps;
    }
    
    /**
     * Start the process.
     * @throws WfException General workflow exception.
     * @throws CannotStart Process cannot be started.
     * @throws AlreadyRunning Process is already running.
     */
    public void start() throws WfException, CannotStart, AlreadyRunning {
        if (steps.size() == 0)
            throw new CannotStart("No Activities exist");
        
        if (workflowStateType().equals("open.running"))
            throw new AlreadyRunning("Process is already running");
        
        if ( valueObject.get("defaultStartActivityId") == null )
            throw new CannotStart("Initial activity is not defined");
        
        changeState("open.running");
        
        // start the first activity (using the defaultStartActivityId of this definition)
        startActivity(getActivity(valueObject.getString("defaultStartActivityId")));        
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
     * Retrieve the Iterator of activities of this process.
     * @throws WfException General workflow exception.
     * @return Iterator of WfActivity objects.
     */
    public Iterator getIteratorStep() throws WfException {
        return steps.iterator();
    }
    
    /**
     * Check if some activity is a member of this process.
     * @param member Some activity.
     * @throws WfException General workflow exception.
     * @return true if the specific activity is amember of this process,
     * false otherwise.
     */
    public boolean isMemberOfStep(WfActivity member) throws WfException {
        return steps.contains(member);
    }
    
    /**
     * Retrieve the iterator of activities in some specific state.
     * @param state Specific state.
     * @throws WfException General workflow exception.
     * @throws InvalidState State is invalid.
     * @return Iterator of activities in specific state
     */
    public Iterator getActivitiesInState(String state) throws WfException,
    InvalidState {
        ArrayList res = new ArrayList();
        Iterator i = steps.iterator();
        while ( i.hasNext() ) {
            WfActivity a = (WfActivity)i.next();
            if ( a.state().equals(state) )
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
        if (result == null)
            throw new ResultNotAvailable("Result is null");
        return result;
    }
    
    /**
     * Retrieve the amount of activities in this process.
     * @throws WfException General workflow exception.
     * @return Number of activities of this process
     */
    public int howManyStep() throws WfException {
        return steps.size();
    }
    
    /**
     * Receives activity results.
     * @param activity WfActivity sending the results.
     * @param results Map of the results.
     * @throws WfException
     */
    public void receiveResults(WfActivity activity, Map results) throws WfException {
        // implement me
    }
    
    /**
     * Receives notification when an activity has completed.
     * @param activity WfActivity which has completed.
     * @throws WfException
     */
    public void activityComplete(WfActivity activity) throws WfException {
        if ( !activity.state().equals("closed.completed") )
            throw new WfException("Activity state is not completed");
        queueNext(activity);
    }
    
    public String executionObjectType() {
        return "WfProcess";
    }
    
    // Queues the next activities for processing
    private void queueNext(WfActivity fromActivity) throws WfException {
        List nextTrans = getTransFrom(fromActivity);
        Iterator i = nextTrans.iterator();
        while ( i.hasNext() ) {
            GenericValue trans = (GenericValue) i.next();
                        
            // Get the activity definition
            GenericValue toActivityVo = null;
            try {
                toActivityVo = trans.getRelatedOne("ToWorkflowActivity");
            }
            catch ( GenericEntityException e ) {
                throw new WfException(e.getMessage(),e);
            }
            
            WfActivity toActivity = getActivity(toActivityVo.getString("activityId"));
            
            // get the transaction restriction
            GenericValue restriction = null;
            try {
                restriction = toActivity.getDefinitionObject().getRelatedOne("WorkflowTransRestriction");
            }
            catch ( GenericEntityException e ) {
                throw new WfException(e.getMessage(),e);
            }
            
            // check for a join
            String join = "WJT_AND"; // default join is AND
            if ( restriction != null && restriction.get("joinTypeEnumId") != null )
                join = restriction.getString("joinTypeEnumId");
            
            // activate if XOR or test the join transition(s)
            if ( join.equals("WJT_XOR") )
                startActivity(toActivity);
            else
                joinTransition(toActivity, trans);
        }
    }
    
    // Follows the and-join transition
    private void joinTransition(WfActivity toActivity, GenericValue transition) throws WfException {
        // get all TO transitions to this activity
        Collection toTrans = null;
        try {
            toActivity.getDefinitionObject().getRelated("ToWorkflowTransition");
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
        
        // get a list of followed transition to this activity        
        Collection followed = null;
        try {
            Map fields = UtilMisc.toMap("workEffortId",toActivity.getRuntimeObject().getString("workEffortId"));                                   
            followed = toActivity.getDelegator().findByAnd("WorkEffortTransBox",fields);
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
        
        // check to see if all transition requirements are met
        if ( toTrans.size() == (followed.size() + 1) ) {
            startActivity(toActivity);
            try {
                Map fields = UtilMisc.toMap("workEffortId",toActivity.getRuntimeObject().getString("workEffortId"));                                                                                   
                getDelegator().removeByAnd("WorkEffortTransBox", fields);
            }
            catch ( GenericEntityException e ) {
                throw new WfException(e.getMessage(),e);
            }
        }            
        else {
            try {
                Map fields = new HashMap();
                fields.put("packageId",transition.getString("packageId"));
                fields.put("processId",transition.getString("processId"));                
                fields.put("transitionId",transition.getString("transitionId"));
                fields.put("workEffortId",toActivity.getRuntimeObject().getString("workEffortId"));
                GenericValue obj = getDelegator().makeValue("WorkEffortTransBox", fields);
                getDelegator().create(obj);
            }
            catch ( GenericEntityException e ) {
                throw new WfException(e.getMessage(),e);
            }
        }
    }
    
    // Activates an activity object
    private void startActivity(WfActivity activity) throws WfException {
        // TODO add to list of running activities
        // TODO test for manual/automatic
        try {
            activity.activate();
        }
        catch ( AlreadyRunning e ) {
            throw new WfException(e.getMessage(),e);
        }
        catch ( CannotStart e ) {
            throw new WfException(e.getMessage(),e);
        }
    }
    
    // Determine the next activity or activities
    private List getTransFrom(WfActivity fromActivity) throws WfException {
        List transList = new ArrayList();
        // get the from transitions
        Collection fromCol = null;
        try {
            fromCol = fromActivity.getDefinitionObject().getRelated("FromWorkflowTransition");
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
        
        // get the transaction restriction
        GenericValue restriction = null;
        try {
            restriction = fromActivity.getDefinitionObject().getRelatedOne("WorkflowTransRestriction");
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
        
        // check for a split 
        String split = "WST_AND"; // default split is AND
        if ( restriction != null && restriction.get("splitTypeEnumId") != null )
            split = restriction.getString("splitTypeEnumId");
        
        // Iterate through the possible transitions
        boolean transitionOk = false;
        Iterator fromIt = fromCol.iterator();
        while ( fromIt.hasNext() ) {
            GenericValue transition = (GenericValue) fromIt.next();
            // evaluate the condition expression
            transitionOk = evalCondition(transition.getString("conditionExpr"));
            if ( transitionOk ) {
                transList.add(transition);
                if ( split.equals("WST_XOR") )
                    break;
            }
        }
        
        return transList;
    }
    
    // Gets a specific activity by its key
    private WfActivity getActivity(String key) throws WfException {
        Iterator i = getIteratorStep();
        while ( i.hasNext() ) {
            WfActivity a = (WfActivity) i.next();
            if ( a.key().equals(key) )
                return a;
        }
        throw new WfException("Activity not a member of this process");
    }
    
    // Evaluate the transition condition
    private boolean evalCondition(String condition) {
        Interpreter bsh = new Interpreter();
        Object o = null;
        if ( condition == null || condition.equals("") )
            return true;
        try {
            // Set the context for the condition
            Set keySet = context.keySet();
            Iterator i = keySet.iterator();
            while ( i.hasNext() ) {
                Object key = i.next();
                Object value = context.get(key);
                bsh.set((String)key,value);
            }
            // evaluate the condition
            o = bsh.eval(condition);
        }
        catch ( EvalError e ) {
            return false;
        }
        if ( o instanceof Number )
            return ( ((Number)o).doubleValue()  == 0 ) ? false : true;
        else
            return ( !o.toString().equalsIgnoreCase("true") ) ? false : true;
    }
}
