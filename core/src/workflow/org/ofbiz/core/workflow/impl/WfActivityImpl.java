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
import org.ofbiz.core.workflow.WfAssignment;

import org.ofbiz.core.workflow.WfException;
import org.ofbiz.core.workflow.InvalidData;
import org.ofbiz.core.workflow.ResultNotAvailable;
import org.ofbiz.core.workflow.CannotComplete;

public class WfActivityImpl extends WfExecutionObjectImpl
implements WfActivity
{
    // Attribute instance 'process'
    private WfProcess process;
    
    // Attribute instance 'result'
    private HashMap result;
    
    // Attribute instance 'assignments'    
    private ArrayList assignments;
    
    /**
     * Creates new WfProcessImpl
     * @param pName Initial value for attribute 'name'
     * @param pDescription Initial value for attribute 'description'
     */
    public WfActivityImpl(String pName, String pDescription) {
        super(pName, pDescription);
    }

    /**
     * Complete this activity.
     * @throws WfException General workflow exception.
     * @throws CannotComplete Cannot complete the activity
     */
    public void complete() throws WfException, CannotComplete {
    }
    
    /**
     * Check if a specific assignment is the member of assignment objects of
     * this activity.
     * @param member Assignment object.
     * @throws WfException General workflow exception.
     * @return true if the assignment is a member of this activity.
     */
    public boolean isMemberOfAssignment(WfAssignment member) throws 
    WfException {
        return true;
    }
    
    /**
     * Getter for the process of this activity.
     * @throws WfException General workflow exception.
     * @return WfProcess Process to which this activity belong.
     */
    public WfProcess container() throws WfException {
        return process;
    }
    
    /**
     * Assign Result for this activity.
     * @param newResult New result.
     * @throws WfException General workflow exception.
     * @throws InvalidData Data is invalid
     */
    public void setResult(Map newResult) throws WfException, InvalidData {
        result = new HashMap(newResult);
    }
    
    /**
     * Retrieve amount of Assignment objects.
     * @throws WfException General workflow exception.
     * @return Amount of current assignments.
     */
    public int howManyAssignment() throws WfException {
        if (assignments == null)
            return 0;
        else
            return assignments.size();
    }
    
    /**
     * Retrieve the Result map of this activity.
     * @throws WfException General workflow exception.
     * @throws ResultNotAvailable No result is available.
     * @return
     */
    public Map result() throws WfException, ResultNotAvailable {
        return result;
    }
    
    /**
     * Retrieve all assignments of this activity.
     * @param maxNumber the high limit of number of assignment in result set.
     * @throws WfException General workflow exception.
     * @return  List of WfAssignment objects.
     */
    public List getSequenceAssignment(int maxNumber) throws WfException {
        return assignments;
    }
    
    /**
     * Retrieve the Iterator of Assignments objects.
     * @throws WfException General workflow exception.
     * @return Assignment Iterator.
     */
    public Iterator getIteratorAssignment() throws WfException {
        if (assignments == null)
            return (new ArrayList()).iterator();
        else
            return assignments.iterator();
    }    
}
