/*
 * $Id$
 */

package org.ofbiz.core.workflow.impl;

import java.io.*;
import java.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.serialize.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.workflow.*;

/**
 * <p><b>Title:</b> WfActivityImpl
 * <p><b>Description:</b> Workflow Activity Object implementation
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

public class WfActivityImpl extends WfExecutionObjectImpl implements WfActivity {
        
    private WfProcess process;          
    private List assignments;
    private Map result;       
         
    /**
     * Creates new WfProcessImpl
     * @param valueObject The GenericValue object of this WfActivity.
     * @param dataObject The GenericValue object of the stored runtime data.
     * @param process The WfProcess object which created this WfActivity.
     */
    public WfActivityImpl(GenericValue valueObject, GenericValue dataObject, WfProcess process) throws WfException {
        super(valueObject,dataObject);
        this.process = process;
        result = new HashMap();
        assignments = new ArrayList();
        GenericValue performer = null;
        try {
            performer = valueObject.getRelatedOne("PerformerWorkflowParticipant");
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
        
        WfResource resource = WfFactory.newWfResource(performer);
        WfAssignment assign = WfFactory.newWfAssignment(this,resource);
        assignments.add(assign);
    }
    
    /**
     * Activates this activity.
     * @throws WfException
     * @throws CannotStart
     * @throws AlreadyRunning
     */
    public void activate() throws WfException, CannotStart, AlreadyRunning {
        if ( this.state().equals("open.running") )
            throw new AlreadyRunning();
        
        // test the activity mode
        String mode = valueObject.getString("startModeEnumId");
        if ( mode == null )
            throw new CannotStart("Start mode cannot be null");
        
        // Default mode is MANUAL -- only start if we are automatic
        if ( mode.equals("WAM_AUTOMATIC") )
            startActivity(); 
        else 
            assignActivity();
    }
    
    /**
     * Complete this activity.
     * @throws WfException General workflow exception.
     * @throws CannotComplete Cannot complete the activity
     */
    public void complete() throws WfException, CannotComplete {
        changeState("closed.complete");
        // implement me
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
        return assignments.contains(member);
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
        this.result = result;
        try {            
            GenericValue runtimeData = null;
            if ( dataObject.get("resultDataId") == null ) {                
                String seqId = getDelegator().getNextSeqId("RuntimeData").toString();                
                runtimeData = getDelegator().makeValue("RuntimeData",UtilMisc.toMap("runtimeDataId",seqId));
                dataObject.set("resultDataId",seqId);
                dataObject.store();
            }
            else {
                runtimeData = dataObject.getRelatedOne("ResultRuntimeData");
            }
            runtimeData.set("runtimeInfo",XmlSerializer.serialize(context));
            runtimeData.store();
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
        catch ( SerializeException e ) {
            throw new InvalidData(e.getMessage(),e);
        }
        catch ( FileNotFoundException e ) {
            throw new InvalidData(e.getMessage(),e);
        }
        catch ( IOException e ) {
            throw new InvalidData(e.getMessage(),e);
        }            
    }
    
    /**
     * Retrieve amount of Assignment objects.
     * @throws WfException General workflow exception.
     * @return Amount of current assignments.
     */
    public int howManyAssignment() throws WfException {
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
        if ( maxNumber > 0 ) 
            return assignments.subList(0,(maxNumber-1));
        return assignments;
    }
    
    /**
     * Retrieve the Iterator of Assignments objects.
     * @throws WfException General workflow exception.
     * @return Assignment Iterator.
     */
    public Iterator getIteratorAssignment() throws WfException {
        return assignments.iterator();
    }
    
    public String executionObjectType() {
        return "WfActivity";
    }
    
    // Assigns the activity to a task list
    private void assignActivity() throws WfException {
        // implement me
    }
    
    // Starts or activates this activity
    private void startActivity() throws WfException, CannotStart {
        try {
            changeState("open.running");
        }
        catch ( InvalidState is ) {
            throw new CannotStart(is.getMessage(),is);
        }
        catch ( TransitionNotAllowed tna ) {
            throw new CannotStart(tna.getMessage(),tna);
        }
        // get the type of this activity
        String type = valueObject.getString("activityTypeEnumId");
        if ( type == null )
            throw new WfException("Illegal activity type");
        
        if ( type.equals("WAT_ROUTE") )
            complete();
        else if ( type.equals("WAT_TOOL") )
            runTool();
        else if ( type.equals("WAT_SUBFLOW") )
            runSubFlow();
        else if ( type.equals("WAT_LOOP") )
            runLoop();
        else
            throw new WfException("Illegal activity type");
    }
    
    // Runs a TOOL activity
    private void runTool() throws WfException {
        // implement me
    }
    
    // Runs a LOOP activity
    private void runLoop() throws WfException {
        // implement me
    }
    
    // Runs a SUBFLOW activity
    private void runSubFlow() throws WfException {
        // implement me
    }
        
}
