/*
 * $Id$
 */

package org.ofbiz.core.workflow.impl;

import java.util.*;
import org.ofbiz.core.entity.*;
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
    private Collection transitions;
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
        result = new HashMap();
        steps = new ArrayList();
        try {
            transitions = valueObject.getRelated("WorkflowTransition");
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
        makeSteps();
    }
    
    // Build the steps from the definition
    private void makeSteps() throws WfException {
        // Build up the activities (steps)
        Collection activityEntities = null;        
        try {
            activityEntities = valueObject.getRelatedCache("WorkflowActivity"); 
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
        
        WfActivity activity = (WfActivity)steps.get(0);
        
        try {
            // TODO use defines and not hard coded
            activity.changeState("Start");
        } catch (InvalidState ise) {
            throw new WfException("InvalidState exeption", ise);
        } catch (TransitionNotAllowed tna) {
            throw new WfException("TransitionNotAllowed exeption", tna);
        }
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
    
    public String executionObjectType() {
        return "WfProcess";
    }
}
