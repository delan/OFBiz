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
 *@created    November 2, 2001
 *@version    1.0
 */

import org.ofbiz.core.workflow.WfExecutionObject;
import org.ofbiz.core.workflow.WfException;
import org.ofbiz.core.workflow.HistoryNotAvailable;
import org.ofbiz.core.workflow.CannotStop;
import org.ofbiz.core.workflow.NotRunning;
import org.ofbiz.core.workflow.CannotResume;
import org.ofbiz.core.workflow.CannotSuspend;
import org.ofbiz.core.workflow.NotSuspended;
import org.ofbiz.core.workflow.AlreadySuspended;
import org.ofbiz.core.workflow.InvalidData;
import org.ofbiz.core.workflow.InvalidState;
import org.ofbiz.core.workflow.UpdateNotAllowed;
import org.ofbiz.core.workflow.TransitionNotAllowed;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class WfExecutionObjectImpl implements WfExecutionObject {
    
    // Attribute instance 'name'
    private String name;
    
    // Attribute instance 'description'
    private String description;
    
    // Attribute instance 'priority'
    private int priority;
    
    // Attribute instance 'context'
    private HashMap context;
    
    // Attribute instance 'history'
    private ArrayList history;
    
    /**
     * Creates new WfExecutionObjectImpl
     * @param pName Initial value for attribute 'name'
     * @param pDescription Initial value for attribute 'description'
     */
    public WfExecutionObjectImpl(String pName) {
        name = pName;
    }
    
    /**
     * Getter for attribute 'name'.
     * @throws WfException General workflow exception.
     * @return Name of the object.
     */
    public String name() throws WfException { return name; }
    
    /**
     * Setter for attribute 'name'
     * @param newValue Set the name of the object.
     * @throws WfException General workflow exception.
     */
    public void setName(String newValue) throws WfException {
        name = newValue;
    }
    
    /**
     * Setter for attribute 'priority'.
     * @param newValue
     * @throws WfException General workflow exception
     */
    public void setPriority(int newValue) throws WfException {
        priority = newValue;
    }
    
    /**
     * Getter for attribute 'priority'.
     * @throws WfException General workflow exception.
     * @return Getter Priority of
     */
    public int priority() throws WfException { return priority; }
    
    /**
     * Retrieve the current state of this process or activity.
     * @throws WfException General workflow exception
     * @return Current state.
     */
    public String state() throws WfException {
        return new String("");
    }
    
    /**
     * Retrieve the list of all valid states.
     * @throws WfException General workflow exception.
     * @return List of valid states.
     */
    public List validStates() throws WfException {
        return new ArrayList();
    }
    
    /**
     * Getter for history count.
     * @throws WfException Generall workflow exception
     * @throws HistoryNotAvailable History can not be retrieved
     * @return Count of history Elements
     */
    public int howManyHistory() throws WfException, HistoryNotAvailable {
        return 0;
    }
    
    /**
     * Abort the execution of this process or activity.
     * @throws WfException General workflow exception.
     * @throws CannotStop The execution cannot be sopped.
     * @throws NotRunning The process or activity is not yet running.
     */
    public void abort() throws WfException, CannotStop, NotRunning {
    }
    
    /**
     * @throws WfException General workflow exception.
     * @return
     */
    public int whileOpen() throws WfException {
        return 0;
    }
    
    /**
     * @throws WfException General workflow exception.
     * @return Reason for not running.
     */
    public int whyNotRunning() throws WfException {
        return 0;
    }
    
    /**
     * Getter for attribute 'key'.
     * @throws WfException General workflow exception.
     * @return Key of the object.
     */
    public String key() throws WfException {
        return new String("");
    }
    
    /**
     * Predicate to check if a 'member' is an element of the history.
     * @param member An element of the history.
     * @throws WfException General workflow exception.
     * @return true if the element of the history, false otherwise.
     */
    public boolean isMemberOfHistory(WfExecutionObject member)
    throws WfException {
        return false;
    }
    
    /**
     * @param newValue Set new process data.
     * @throws WfException General workflow exception.
     * @throws InvalidData The data is invalid.
     * @throws UpdateNotAllowed Update the context is not allowed.
     */
    public void setProcessContext(Map newValue)
    throws WfException, InvalidData, UpdateNotAllowed {
        context = new HashMap(newValue);
    }
    
    /**
     * Getter for attribute 'context'.
     * @throws WfException General workflow exception.
     * @return Process context.
     */
    public Map processContext() throws WfException {return context;}
    
    /**
     * @throws WfException General workflow exception.
     * @return Current state of this object.
     */
    public int workflowState() throws WfException {
        return 0;
    }
    
    /**
     * Terminate this process or activity.
     * @throws WfException General workflow exception
     * @throws CannotStop
     * @throws NotRunning
     */
    public void terminate() throws WfException, CannotStop, NotRunning {
    }
    
    /**
     * Setter for attribute 'description'.
     * @param newValue New value for attribute 'description'.
     * @throws WfException General workflow exception.
     */
    public void setDescription(String newValue) throws WfException {
        description = newValue;
    }
    
    /**
     * Getter for attribute 'description'.
     * @throws WfException General workflow exception.
     * @return Description of this object.
     */
    public String description() throws WfException {return description;}
    
    /**
     * Getter for timestamp of last state change.
     * @throws WfException General workflow exception.
     * @return Timestamp of last state change.
     */
    public Timestamp lastStateTime() throws WfException {
        //Calendar rightNow = Calendar.getInstance();
        //return new Timestamp(rightNow.time());
        return new Timestamp(0);
    }
    
    /**
     * Getter for history sequence.
     * @param maxNumber Maximum number of element in result list.
     * @throws WfException General workflow exception.
     * @throws HistoryNotAvailable
     * @return List of History objects.
     */
    public List getSequenceHistory(int maxNumber)
    throws WfException, HistoryNotAvailable {
        return history;
    }
    
    /**
     * Search in the history for specific elements.
     * @param query Search criteria.
     * @param namesInQuery elements to search.
     * @throws WfException General workflow exception
     * @throws HistoryNotAvailable
     * @return Found history elements that meet the search criteria.
     */
    public Iterator getIteratorHistory(String query, Map namesInQuery)
    throws WfException, HistoryNotAvailable {
        return history.iterator();
    }
    
    /**
     * Resume this process or activity.
     * @throws WfException General workflow exception.
     * @throws CannotResume
     * @throws NotRunning
     * @throws NotSuspended
     */
    public void resume() throws WfException, CannotResume,
    NotRunning, NotSuspended {
    }
    
    /**
     * @throws WfException General workflow exception.
     * @return Termination art of this process ot activity.
     */
    public int howClosed() throws WfException {
        return 0;
    }
    
    /**
     * Set new state for this process or activity.
     * @param newState New state to be set.
     * @throws WfException General workflow exception.
     * @throws InvalidState The state is invalid.
     * @throws TransitionNotAllowed The transition is not allowed.
     */
    public void changeState(String newState) throws WfException, InvalidState,
    TransitionNotAllowed {
    }
    
    /**
     * Suspend this process or activity.
     * @throws WfException General workflow exception.
     * @throws CannotSuspend
     * @throws NotRunning
     * @throws AlreadySuspended
     */
    public void suspend() throws WfException, CannotSuspend, NotRunning,
    AlreadySuspended {
    }
}
