/*
 * $Id$
 */

package org.ofbiz.core.workflow.impl;

import java.io.*;
import java.util.*;
import org.ofbiz.core.entity.*;
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
 *@created    December 18, 2001
 *@version    1.2
 */

public class WfActivityImpl extends WfExecutionObjectImpl implements WfActivity {
    
    protected String process;    
     
    /**
     * Create a new WfActivityImpl
     * @param value GenericValue object of the WorkflowActivity entity
     * @param process The WorkEffort ID of the parent process
     * @throws WfException
     */
    public WfActivityImpl(GenericValue value, String process) throws WfException {
        super(value,process);
        this.process = process;
        init();
    }
    
    /**
     * Create a new WfActivityImpl
     * @param value GenericValue object of the WorkflowActivity entity
     * @param process The WorkEffort ID of the parent process
     * @throws WfException
     */
    public WfActivityImpl(GenericDelegator delegator, String workEffortId) throws WfException {
        super(delegator,workEffortId);
        this.process = getRuntimeObject().getString("workEffortParentId");
    }
            
    private void init() throws WfException {        
        GenericValue valueObject = getDefinitionObject();
        GenericValue performer = null;
        if ( valueObject.get("performerParticipantId") != null ) {
            try {
                performer = valueObject.getRelatedOne("PerformerWorkflowParticipant");
            }
            catch ( GenericEntityException e ) {
                throw new WfException(e.getMessage(),e);
            }
        }
        if ( performer != null )
            createAssignments(performer);
    }
            
    private void createAssignments(GenericValue performer) throws WfException {
    	GenericValue valueObject = getDefinitionObject();
        boolean assignAll = false;
        if ( valueObject.get("acceptAllAssignments") != null )
            assignAll = valueObject.getBoolean("acceptAllAssignments").booleanValue();
        
        if ( !assignAll ) {
            if ( performer != null ) {
                Debug.logInfo("[WfActivity.createAssignments] : (S) Single assignment");
                assign(WfFactory.getWfResource(performer),false);
            }
            return;
        }
        // check for a party group
        if ( performer.get("partyId") != null && !performer.getString("partyId").equals("_NA_") ) {
            GenericValue partyType = null;
            GenericValue groupType = null;
            try {
                Map fields1 = UtilMisc.toMap("partyId",performer.getString("partyId"));
                GenericValue v1 = getDelegator().findByPrimaryKey("Party",fields1);
                partyType = v1.getRelatedOne("PartyType");
                Map fields2 = UtilMisc.toMap("partyTypeId","PARTY_GROUP");
                groupType = getDelegator().findByPrimaryKeyCache("PartyType",fields2);
            }
            catch ( GenericEntityException e ) {
                throw new WfException(e.getMessage(),e);
            }
            if ( EntityTypeUtil.isType(partyType,groupType) ) {
                // party is a group
                Collection partyRelations = null;
                try {
                    Map fields = UtilMisc.toMap("partyIdFrom",performer.getString("partyId"),"partyRelationshipTypeId","GROUP_ROLLUP");
                    partyRelations = getDelegator().findByAnd("PartyRelationship",fields);
                }
                catch ( GenericEntityException e ) {
                    throw new WfException(e.getMessage(),e);
                }
                // make assignments for these parties
                Debug.logInfo("[WfActivity.createAssignments] : Group assignment");
                Iterator i = partyRelations.iterator();
                while ( i.hasNext() ) {
                    GenericValue value = (GenericValue) i.next();
                    assign(WfFactory.getWfResource(getDelegator(),null,null,value.getString("partyIdTo"),null),true);
                }
            }
            else {
                // not a group
                Debug.logInfo("[WfActivity.createAssignments] : (G) Single assignment");
                assign(WfFactory.getWfResource(performer),false);
            }
        }
        // check for role types
        else if ( performer.get("roleTypeId") != null && !performer.getString("roleTypeId").equals("_NA_") ) {
            Collection partyRoles = null;
            try {
                Map fields = UtilMisc.toMap("roleTypeId",performer.getString("roleTypeId"));
                partyRoles = getDelegator().findByAnd("PartyRole",fields);
            }
            catch ( GenericEntityException e ) {
                throw new WfException(e.getMessage(),e);
            }
            // loop through the roles and create assignments
            Debug.logInfo("[WfActivity.createAssignments] : Role assignment");
            Iterator i = partyRoles.iterator();
            while ( i.hasNext() ) {
                GenericValue value = (GenericValue) i.next();
                assign(WfFactory.getWfResource(value.getDelegator(),null,null,value.getString("partyId"),null),true);
            }
        }
    }
    
    private List getAssignments() throws WfException {
        List assignments = new ArrayList();
        Collection c = null;                
        try {
            c = getDelegator().findByAnd("WorkEffortPartyAssignment",UtilMisc.toMap("workEffortId",runtimeKey()));
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
        if ( c == null )
            return assignments;
        
        Iterator i = c.iterator();
        while ( i.hasNext() ) {
            GenericValue value = (GenericValue) i.next();
            String party = value.getString("partyId");
            String role = value.getString("roleTypeId");
            String status = value.getString("statusId");
            java.sql.Timestamp from = value.getTimestamp("fromDate");
            if ( status.equals("CAL_SENT") || status.equals("CAL_ACCEPTED") || status.equals("CAL_TENTATIVE") )
                assignments.add(WfFactory.getWfAssignment(getDelegator(),runtimeKey(),party,role,from));
        }            
        return assignments;
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
        String mode = getDefinitionObject().getString("startModeEnumId");
        if ( mode == null )
            throw new CannotStart("Start mode cannot be null");        
        if ( mode.equals("WAM_AUTOMATIC") ) {
            // set the status of the assignments
            Iterator i = getIteratorAssignment();
            while ( i.hasNext() )
                ((WfAssignment)i.next()).changeStatus("CAL_ACCEPTED");
            startActivity();
        }
        
        // check the assignment status
        else if ( howManyAssignment() > 0 && checkAssignStatus(1) )
            startActivity();
                        
    }
    
    /**
     * Assign this activity to a resource
     * @param WfResource to assign this activity to
     * @param append Append to end if existing list (true) or replace existing (false)
     * @throws WfException
     */
    public void assign(WfResource resource, boolean append) throws WfException {
        if ( !append ) {
            Iterator ai = getIteratorAssignment();
            while ( ai.hasNext() ) {
                WfAssignment a = (WfAssignment) ai.next();
                a.remove();
            }            
        }
        
        WfAssignment assign = WfFactory.getWfAssignment(this,resource,null);        
    }
    
    /**
     * Complete this activity.
     * @throws WfException General workflow exception.
     * @throws CannotComplete Cannot complete the activity
     */
    public void complete() throws WfException, CannotComplete {
        // check to make sure all assignements are complete
        if ( !checkAssignStatus(2) )
            throw new CannotComplete("All assignments have not been completed");
        try {
            container().receiveResults(this,result());
        }
        catch ( InvalidData e ) {
            throw new CannotComplete("Invalid result data was passed",e);
        }
        try {
            changeState("closed.completed");
        }
        catch ( InvalidState is ) {
            throw new CannotComplete(is.getMessage(),is);
        }
        catch ( TransitionNotAllowed tna ) {
            throw new CannotComplete(tna.getMessage(),tna);
        }
        
        container().activityComplete(this);
    }
    
    /**
     * Check if a specific assignment is a member of this activity.
     * @param member Assignment object.
     * @throws WfException General workflow exception.
     * @return true if the assignment is a member of this activity.
     */
    public boolean isMemberOfAssignment(WfAssignment member) throws
    WfException {
        return getAssignments().contains(member);
    }
    
    /**
     * Getter for the process of this activity.
     * @throws WfException General workflow exception.
     * @return WfProcess Process to which this activity belong.
     */
    public WfProcess container() throws WfException {
        return WfFactory.getWfProcess(delegator,process);
    }
    
    /**
     * Assign Result for this activity.
     * @param newResult New result.
     * @throws WfException General workflow exception.
     * @throws InvalidData Data is invalid
     */
    public void setResult(Map newResult) throws WfException, InvalidData {
        Map context = processContext();
        context.putAll(newResult);  
        setSerializedData(context);
    }
    
    /**
     * Retrieve amount of Assignment objects.
     * @throws WfException General workflow exception.
     * @return Amount of current assignments.
     */
    public int howManyAssignment() throws WfException {
        return getAssignments().size();
    }
    
    /**
     * Retrieve the Result map of this activity.
     * @throws WfException General workflow exception.
     * @throws ResultNotAvailable No result is available.
     * @return Map of results from this activity
     */
    public Map result() throws WfException, ResultNotAvailable {
        // Get the results from the signature.
        Map resultSig = container().manager().resultSignature();
        Map results = new HashMap();
        Map context = processContext();
        if ( resultSig != null ) {
            Set resultKeys = resultSig.keySet();
            Iterator i = resultKeys.iterator();
            while ( i.hasNext() ) {
                Object key = i.next();
                if ( context.containsKey(key) )
                    results.put(key,context.get(key));
            }
        }
        return results;
    }
    
    /**
     * Retrieve all assignments of this activity.
     * @param maxNumber the high limit of number of assignment in result set (0 for all).
     * @throws WfException General workflow exception.
     * @return  List of WfAssignment objects.
     */
    public List getSequenceAssignment(int maxNumber) throws WfException {
        if ( maxNumber > 0 )
            return getAssignments().subList(0,(maxNumber-1));
        return getAssignments();
    }
    
    /**
     * Retrieve the Iterator of Assignments objects.
     * @throws WfException General workflow exception.
     * @return Assignment Iterator.
     */
    public Iterator getIteratorAssignment() throws WfException {
        return getAssignments().iterator();
    }
    
    public String executionObjectType() {
        return "WfActivity";
    }
    
    // Checks to see if we can complete
    private void checkComplete() throws WfException, CannotComplete {
        String mode = getDefinitionObject().getString("finishModeEnumId");
        if ( mode == null )
            throw new CannotComplete("Finish mode cannot be null");
        
        // Default mode is MANUAL -- only finish if we are automatic
        if ( mode.equals("WAM_AUTOMATIC") ) {
            // set the status of the assignments
            Iterator i = getIteratorAssignment();
            while ( i.hasNext() )
                ((WfAssignment)i.next()).changeStatus("CAL_COMPLETE");
            this.complete();
        }
    }
    
    // Checks the staus of all assignments
    // type 1 -> accept status
    // type 2 -> complete status
    private boolean checkAssignStatus(int type) throws WfException {
        boolean acceptAll = false;
        boolean completeAll = false;
        GenericValue valueObject = getDefinitionObject();
        if ( valueObject.get("acceptAllAssignments") != null )
            acceptAll = valueObject.getBoolean("acceptAllAssignments").booleanValue();
        if ( valueObject.get("completeAllAssignments") != null )
            completeAll = valueObject.getBoolean("completeAllAssignments").booleanValue();
        
        if ( type == 2 && completeAll ) {
            Debug.logInfo("[WfActivity.checkAssignStatus] : Checking completeAll");
            Iterator i = getIteratorAssignment();
            while ( i.hasNext() ) {
                WfAssignment a = (WfAssignment) i.next();
                if ( !a.status().equals("CAL_COMPLETE") )
                    return false;
            }
        }
        
        if ( type == 1 && acceptAll ) {
            Debug.logInfo("[WfActivity.checkAssignStatus] : Checking acceptAll");
            Iterator i = getIteratorAssignment();
            while ( i.hasNext() ) {
                WfAssignment a = (WfAssignment) i.next();
                if ( !a.status().equals("CAL_ACCEPTED") )
                    return false;
            }
        }
        
        return true;
    }
    
    // Starts or activates this automatic activity
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
        String type = getDefinitionObject().getString("activityTypeEnumId");
        if ( type == null )
            throw new WfException("Illegal activity type");
        
        if ( type.equals("WAT_NO") )
            return;                   // NO implementation requires MANUAL FinishMode
        else if ( type.equals("WAT_ROUTE") )
            this.checkComplete();     // ROUTE goes directly to complete status
        else if ( type.equals("WAT_TOOL") )
            this.runTool();       // TOOL will invoke a procedure (service) or an application
        else if ( type.equals("WAT_SUBFLOW") )
            this.runSubFlow(); // Begin a sub workflow
        else if ( type.equals("WAT_LOOP") )
            this.runLoop();      // A LOOP control activity
        else
            throw new WfException("Illegal activity type");
    }
    
    // Runs a TOOL activity - there can be 0..n
    private void runTool() throws WfException {
        Collection tools = null;
        try {
            tools = getDefinitionObject().getRelated("WorkflowActivityTool");
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
        if ( tools == null )
            this.checkComplete();  // Null tools mean nothing to do (same as route?)
        
        List waiters = new ArrayList();
        Iterator i = tools.iterator();
        while ( i.hasNext() ) {
            GenericValue thisTool = (GenericValue) i.next();
            String toolId = thisTool.getString("toolId");
            String params = thisTool.getString("actualParameters");
            waiters.add(this.runService(toolId,params));
        }
        
        while ( waiters.size() > 0 ) {
            Iterator wi = waiters.iterator();
            Collection remove = new ArrayList();
            while ( wi.hasNext() ) {
                GenericResultWaiter thw = (GenericResultWaiter) wi.next();
                if ( thw.isCompleted() ) {
                    try {
                        this.setResult(thw.getResult());
                        remove.add(thw);
                    }
                    catch ( IllegalStateException e ) {
                        throw new WfException("Unknown error",e);
                    }
                }
            }
            waiters.removeAll(remove);
        }
        
        this.checkComplete();
    }
    
    // Runs a LOOP activity
    private void runLoop() throws WfException {
        // implement me
    }
    
    // Runs a SUBFLOW activity
    private void runSubFlow() throws WfException {
        GenericValue subFlow = null;
        try {
            subFlow = getDefinitionObject().getRelatedOne("WorkflowActivitySubFlow");
        }
        catch ( GenericEntityException e ) {
            throw new WfException(e.getMessage(),e);
        }
        if ( subFlow == null )
            return;
        
        String type = "WSE_SYNCHR";
        if ( subFlow.get("executionEnumId") != null )
            type = subFlow.getString("executionEnumId");
        
        // Build a model service
        ModelService service = new ModelService();
        service.name = service.toString();
        service.engineName = "workflow";
        service.location = subFlow.getString("packageId");
        service.invoke = subFlow.getString("subFlowProcessId");
        service.contextInfo = null;  // TODO FIXME
        
        String actualParameters = subFlow.getString("actualParameters");
        GenericResultWaiter waiter = this.runService(service,actualParameters);
        if ( type.equals("WSE_SYNCHR") ) {
            Map subResult = waiter.waitForResult();
            this.setResult(subResult);
        }
        
        this.checkComplete();
    }
    
    // Invoke the procedure (service) -- This will include sub-workflows
    private GenericResultWaiter runService(String serviceName, String params) throws WfException {       
        DispatchContext dctx = getDispatcher().getLocalContext(getServiceLoader());
        ModelService service = null;
        try {
            service = dctx.getModelService(serviceName);
        }
        catch ( GenericServiceException e ) {
            throw new WfException(e.getMessage(),e);
        }
        if ( service == null )
            throw new WfException("Cannot determine model service for service name");
        return runService(service,params);
    }
    
    private GenericResultWaiter runService(ModelService service, String params) throws WfException {
        Map ctx = this.actualContext(params);
        GenericResultWaiter waiter = new GenericResultWaiter();
        try {
            getDispatcher().runAsync(getServiceLoader(),service,ctx,waiter);
        }
        catch ( GenericServiceException e ) {
            throw new WfException(e.getMessage(),e);
        }
        return waiter;
    }
    
    // Gets the actual context parameters from the context based on the actual paramters field
    private Map actualContext(String actualParameters) throws WfException {
        Map actualContext = new HashMap();
        Map context = processContext();
        if ( actualParameters != null ) {
            List params = StringUtil.split(actualParameters,",");
            Iterator i = params.iterator();
            while ( i.hasNext() ) {
                Object key = i.next();
                if ( context.containsKey(key) )
                    actualContext.put(key,context.get(key));
                else
                    throw new WfException("Context does not contain the key: '" + (String)key + "'");
            }
        }
        return actualContext;
    }
    
}
