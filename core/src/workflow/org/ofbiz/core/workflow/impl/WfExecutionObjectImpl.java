/*
 * $Id$
 */

package org.ofbiz.core.workflow.impl;

import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.workflow.*;

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
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@author     David Ostrovsky (d.ostrovsky@gmx.de)
 *@created    November 2, 2001
 *@version    1.0
 */
public abstract class WfExecutionObjectImpl implements WfExecutionObject {
       
    // The value objects for this execution object (wfprocess,wfactivity)
    protected GenericValue valueObject;
    protected GenericValue runtimeData;
        
    // Attributes of this object
    protected Map context;               
    protected List history;       
    protected LocalDispatcher dispatcher;
    
    /**
     * Creates new WfExecutionObjectImpl
     * @param valueObject The GenericValue object.     
     */
    public WfExecutionObjectImpl(GenericValue valueObject) {
        // set the value object
        this.valueObject = valueObject;                
        
        // set the local dispatcher
        dispatcher = null;
        
        // set the history
        history = null;
        
        // set the context              
        context = null;
        
        // set the state
        try {
            changeState("open.not_running.not_started");
        }
        catch ( WfException e ) {
            e.printStackTrace();
        }
    }
    
    /**
     * Getter for attribute 'name'.
     * @throws WfException General workflow exception.
     * @return Name of the object.
     */
    public String name() throws WfException { 
        return valueObject.getString("objectName");
    }
    
    /**
     * Setter for attribute 'name'
     * @param newValue Set the name of the object.
     * @throws WfException General workflow exception.
     */
    public void setName(String newValue) throws WfException {
        try {
            valueObject.set("objectName",newValue);
            valueObject.store();
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }                
    }
    
    /**
     * Setter for attribute 'priority'.
     * @param newValue
     * @throws WfException General workflow exception
     */
    public void setPriority(int newValue) throws WfException {
        try {
            valueObject.set("objectPriority",new Integer(newValue));
            valueObject.store();
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }                        
    }
    
    /**
     * Getter for attribute 'priority'.
     * @throws WfException General workflow exception.
     * @return Getter Priority of
     */
    public int priority() throws WfException { 
        if ( valueObject.get("objectPriority") != null )
            return valueObject.getInteger("objectPriority").intValue();                
        return 0;  // change to default priority value
    }
    
    /**
     * Retrieve the current state of this process or activity.
     * @throws WfException General workflow exception
     * @return Current state.
     */
    public String state() throws WfException {         
        return valueObject.getString("objectState");            
    }
            
    /**
     * Retrieve the list of all valid states.
     * @throws WfException General workflow exception.
     * @return List of valid states.
     */
    public List validStates() throws WfException {
         String statesArr[] = { "open.running",  "open.not_running.not_started",
          "open.not_running.suspended",  "closed.completed", "closed.terminated",
          "closed.aborted" };
          List possibleStates = Arrays.asList(statesArr);
          String currentState = state();
          if ( currentState.startsWith("closed") )
              return new ArrayList();
          if ( !currentState.startsWith("open") )
              throw new WfException("Currently in an unknown state.");
          if ( currentState.equals("open.running") ) {
              possibleStates.remove("open.running");
              possibleStates.remove("open.not_running.not_started");
              return possibleStates;
          }
          if ( currentState.equals("open.not_running.not_started") ) {
              possibleStates.remove("open.not_running.not_started");
              possibleStates.remove("open.not_running.suspended");
              possibleStates.remove("closed.completed");
              possibleStates.remove("closed.terminated");      
              possibleStates.remove("closed.aborted");
              return possibleStates;
          }
          if ( currentState.equals("open.not_running.suspended") ) {
              possibleStates.remove("open.not_running.suspended");
              possibleStates.remove("open.not_running.not_started");
              possibleStates.remove("closed.complete");
              possibleStates.remove("closed.terminated");
              possibleStates.remove("closed.aborted");
              return possibleStates;
          }                     
        return new ArrayList();
    }
    
    /**
     * Getter for history count.
     * @throws WfException Generall workflow exception
     * @throws HistoryNotAvailable History can not be retrieved
     * @return Count of history Elements
     */
    public int howManyHistory() throws WfException, HistoryNotAvailable {
        if ( history.size() < 1 )
            throw new HistoryNotAvailable();
        return history.size();
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
    public List whileOpenType() throws WfException {
        String[] list = { "running", "not_running" };
        return Arrays.asList(list);
    }
    
    /**
     * @throws WfException General workflow exception.
     * @return Reason for not running.
     */
    public List whyNotRunningType() throws WfException {
        String[] list = { "not_started", "suspended" };
        return Arrays.asList(list);
    }
    
    /**
     * Getter for attribute 'key'.
     * @throws WfException General workflow exception.
     * @return Key of the object.
     */
    public String key() throws WfException {
        return valueObject.getString("executionObjectId");
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
    public List workflowStateType() throws WfException {        
        String[] list = { "open", "closed" };
        return Arrays.asList(list);
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
        try {
            valueObject.set("description",newValue);
            valueObject.store();
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
    }
    
    /**
     * Getter for attribute 'description'.
     * @throws WfException General workflow exception.
     * @return Description of this object.
     */
    public String description() throws WfException {
        return valueObject.getString("description");
    }
    
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
    public List howClosedType() throws WfException {
        String[] list = { "completed", "terminated", "aborted" };
        return Arrays.asList(list);               
    }
    
    /**
     * Set new state for this process or activity.
     * @param newState New state value to be set.
     * @throws WfException General workflow exception.
     * @throws InvalidState The state is invalid.
     * @throws TransitionNotAllowed The transition is not allowed.
     */
    public void changeState(String newState) throws WfException, InvalidState,
      TransitionNotAllowed {
          // Test is transaction is allowed???
          if ( validStates().contains(newState) ) {
              try {
                  valueObject.set("objectState",newState);
                  valueObject.store();
              }
              catch ( GenericEntityException e ) {
                  throw new WfException(e.getMessage(),e);
              }
          }
          else {
              throw new InvalidState();
          }                    
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

  /**
   * Returns the delegator being used by this workflow
   * @return GenericDelegator used for this workflow
   * @throws WfException
   */
  public GenericDelegator getDelegator() throws WfException {      
          return valueObject.getDelegator();
  }
  
  /**
   * Returns the workflow local dispatcher
   * @return LocalDispatcher for this workflow
   * @throws WfException
   */
  public LocalDispatcher getDispatcher() throws WfException {
      if ( dispatcher == null )
          throw new WfException("No dispacher set.");
      return dispatcher;
  }
  
  /**
   * Sets the LocalDispatcher for this workflow
   * @param dispatcher The LocalDispatcher to be used with this workflow
   * @throws WfException
   */
  public void setDispatcher(LocalDispatcher dispatcher) throws WfException {
      this.dispatcher = dispatcher;
  }
  
    /** 
     * Returns the type of execution object
     * @return String name of this execution object type
     */
    public abstract String executionObjectType();
}
