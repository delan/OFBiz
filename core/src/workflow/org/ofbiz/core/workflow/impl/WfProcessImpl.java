/*
 * $Id$
 */

package org.ofbiz.core.workflow.impl;

/**
 * <p><b>Title:</b> WfExecutionObjectImpl
 * <p><b>Description:</b> Workflow Execution Object implementation
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
 *@author     David Ostrovsky (d.ostrovsky@gmx.de)
 *@created    November 15, 2001
 *@version    1.0
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import org.ofbiz.core.workflow.WfProcess;
import org.ofbiz.core.workflow.WfActivity;
import org.ofbiz.core.workflow.WfRequester;
import org.ofbiz.core.workflow.WfProcessMgr;

import org.ofbiz.core.workflow.WfException;
import org.ofbiz.core.workflow.CannotStart;
import org.ofbiz.core.workflow.AlreadyRunning;
import org.ofbiz.core.workflow.InvalidState;
import org.ofbiz.core.workflow.ResultNotAvailable;
import org.ofbiz.core.workflow.CannotChangeRequester;

public class WfProcessImpl extends WfExecutionObjectImpl
implements WfProcess {

    // Attribute instance 'requestor'
    private WfRequester requestor;
    
    // Attribute instance 'steps'
    private ArrayList steps;
    
    // Attribute instance 'manager'
    private WfProcessMgr manager;
    
    // Attribute instance 'result'
    private HashMap result;
    
    /**
     * Creates new WfProcessImpl
     * @param pName Initial value for attribute 'name'
     * @param pDescription Initial value for attribute 'description'
     */
    public WfProcessImpl(String pName, String pDescriprion) {
        super(pName, pDescriprion);
    }

    /**
     * Set the originator of this process.
     * @param newValue The Requestor of this process.
     * @throws WfException General workflow exception.
     * @throws CannotChangeRequester Requestor cannot be changed.
     */
    public void setRequester(WfRequester newValue) throws WfException, 
    CannotChangeRequester { 
        requestor = newValue; 
    }
    
    /**
     * Retrieve the List of activities of this process.
     * @param maxNumber High limit of elements in the result set.
     * @throws WfException General workflow exception.
     * @return List of WfActivity objects.
     */
    public List getSequenceStep(int maxNumber) throws WfException {
        return steps;
    }
    
    /**
     * Start the process.
     * @throws WfException General workflow exception.
     * @throws CannotStart Process cannot be started.
     * @throws AlreadyRunning Process is already running.
     */
    public void start() throws WfException, CannotStart, AlreadyRunning {
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
        return requestor;
    }
    
    /**
     * Retrieve the Iterator of activities of this process.
     * @throws WfException General workflow exception.
     * @return Iterator of WfActivity objects.
     */
    public Iterator getIteratorStep() throws WfException {
        if (steps == null)
            return (new ArrayList()).iterator();
        else
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
        return true;
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
        return (new ArrayList()).iterator();
    }
    
    /**
     * Retrieve the result for this process.
     * @throws WfException General workflow exception.
     * @throws ResultNotAvailable No result is available.
     * @return Result Map.
     */
    public Map result() throws WfException, ResultNotAvailable {
        return result;
    }
    
    /**
     * Retrieve the amount of activities in this process.
     * @throws WfException General workflow exception.
     * @return Number of activities of this process
     */
    public int howManyStep() throws WfException {
        if (steps == null) 
            return 0;
        else
            return steps.size();
    }    
}
