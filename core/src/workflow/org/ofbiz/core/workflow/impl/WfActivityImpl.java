/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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

import java.io.*;
import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.workflow.*;

/**
 * WfActivityImpl - Workflow Activity Object implementation
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @author     David Ostrovsky (d.ostrovsky@gmx.de)
 * @author     Oswin Ondarza and Manuel Soto 
 * @version    $Revision$
 * @since      2.0
 */
public class WfActivityImpl extends WfExecutionObjectImpl implements WfActivity {

    public static final String module = WfActivityImpl.class.getName();

    protected String process = null;

    /**
     * Create a new WfActivityImpl
     * @param value GenericValue object of the WorkflowActivity entity
     * @param process The WorkEffort ID of the parent process
     * @throws WfException
     */
    public WfActivityImpl(GenericValue value, String process) throws WfException {
        super(value, process);
        this.process = process;
        init();
    }

    public WfActivityImpl(GenericDelegator delegator, String workEffortId) throws WfException {
        super(delegator, workEffortId);
        if (activityId == null || activityId.length() == 0)
            throw new WfException("Execution object is not of type WfActivity");
        this.process = getRuntimeObject().getString("workEffortParentId");
    }

    private void init() throws WfException {
        GenericValue valueObject = getDefinitionObject();
        GenericValue performer = null;

        if (valueObject.get("performerParticipantId") != null) {
            try {
                performer = valueObject.getRelatedOne("PerformerWorkflowParticipant");
            } catch (GenericEntityException e) {
                throw new WfException(e.getMessage(), e);
            }
        }
        if (performer != null)
            createAssignments(performer);

        // set the service loader the same as the parent
        this.setServiceLoader(container().getRuntimeObject().getString("serviceLoaderName"));

        // set the activity context
        this.setProcessContext(container().contextKey());

        // check for inheritPriority attribute
        boolean inheritPriority = valueObject.getBoolean("inheritPriority").booleanValue() || false;

        if (inheritPriority) {
            GenericValue runTime = getRuntimeObject();
            Map context = processContext();

            if (context.containsKey("previousActivity")) {
                String previousActivity = (String) context.get("previousActivity");
                WfActivity pAct = WfFactory.getWfActivity(getDelegator(), previousActivity);

                if (pAct != null) {
                    try {
                        runTime.set("priority", new Long(pAct.priority()));
                        runTime.store();
                    } catch (GenericEntityException e) {
                        throw new WfException(e.getMessage(), e);
                    }
                }
            }
        }

        boolean limitAfterStart = valueObject.getBoolean("limitAfterStart").booleanValue();

        if (Debug.infoOn())
            Debug.logInfo("[WfActivity.init]: limitAfterStart - " + limitAfterStart, module);
        if (!limitAfterStart
            && valueObject.get("limitService") != null
            && !valueObject.getString("limitService").equals("")) {
            Debug.logVerbose("[WfActivity.init]: limit service is not after start, setting up now.", module);
            setLimitService();
        }
    }

    private void createAssignments(GenericValue performer) throws WfException {
        GenericValue valueObject = getDefinitionObject();
        boolean assignAll = false;

        if (valueObject.get("acceptAllAssignments") != null)
            assignAll = valueObject.getBoolean("acceptAllAssignments").booleanValue();

        String performerType = performer.getString("participantTypeId");

        if (performerType != null && (performerType.equals("RESOURCE") || performerType.equals("ROLE"))) {
            // We are a dynamic performer
            performer = getDynamicPerformer(performer);
        }

        if (!assignAll) {
            if (performer != null) {
                Debug.logVerbose("[WfActivity.createAssignments] : (S) Single assignment", module);
                assign(WfFactory.getWfResource(performer), false);
            }
            return;
        }

        // check for a party group
        if (performer.get("partyId") != null && !performer.getString("partyId").equals("_NA_")) {
            GenericValue partyType = null;
            GenericValue groupType = null;

            try {
                Map fields1 = UtilMisc.toMap("partyId", performer.getString("partyId"));
                GenericValue v1 = getDelegator().findByPrimaryKey("Party", fields1);

                partyType = v1.getRelatedOne("PartyType");
                Map fields2 = UtilMisc.toMap("partyTypeId", "PARTY_GROUP");
                groupType = getDelegator().findByPrimaryKeyCache("PartyType", fields2);
            } catch (GenericEntityException e) {
                throw new WfException(e.getMessage(), e);
            }
            if (EntityTypeUtil.isType(partyType, groupType)) {
                // party is a group
                Collection partyRelations = null;
                try {
                    Map fields = UtilMisc.toMap("partyIdFrom", performer.getString("partyId"), 
                            "partyRelationshipTypeId", "GROUP_ROLLUP");                                                                                                                                       
                    partyRelations = getDelegator().findByAnd("PartyRelationship", fields);
                } catch (GenericEntityException e) {
                    throw new WfException(e.getMessage(), e);
                }

                // make assignments for these parties
                Debug.logVerbose("[WfActivity.createAssignments] : Group assignment", module);
                Iterator i = partyRelations.iterator();

                while (i.hasNext()) {
                    GenericValue value = (GenericValue) i.next();
                    assign(
                        WfFactory.getWfResource(getDelegator(), null, null, value.getString("partyIdTo"), null),
                        true);
                }
            } else {
                // not a group
                Debug.logVerbose("[WfActivity.createAssignments] : (G) Single assignment", module);
                assign(WfFactory.getWfResource(performer), false);
            }
        } // check for role types
        else if (performer.get("roleTypeId") != null && !performer.getString("roleTypeId").equals("_NA_")) {
            Collection partyRoles = null;

            try {
                Map fields = UtilMisc.toMap("roleTypeId", performer.getString("roleTypeId"));
                partyRoles = getDelegator().findByAnd("PartyRole", fields);
            } catch (GenericEntityException e) {
                throw new WfException(e.getMessage(), e);
            }

            // loop through the roles and create assignments
            Debug.logVerbose("[WfActivity.createAssignments] : Role assignment", module);
            Iterator i = partyRoles.iterator();

            while (i.hasNext()) {
                GenericValue value = (GenericValue) i.next();
                assign(
                    WfFactory.getWfResource(value.getDelegator(), null, null, value.getString("partyId"), null),
                    true);
            }
        }
    }

    private List getAssignments() throws WfException {
        List assignments = new ArrayList();
        Collection c = null;

        try {
            c = getDelegator().findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", runtimeKey()));
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
        if (c == null)
            return assignments;

        Iterator i = c.iterator();

        while (i.hasNext()) {
            GenericValue value = (GenericValue) i.next();
            String party = value.getString("partyId");
            String role = value.getString("roleTypeId");
            String status = value.getString("statusId");
            java.sql.Timestamp from = value.getTimestamp("fromDate");

            if (status.equals("CAL_SENT") || status.equals("CAL_ACCEPTED") || status.equals("CAL_TENTATIVE"))
                assignments.add(WfFactory.getWfAssignment(getDelegator(), runtimeKey(), party, role, from));
        }
        return assignments;
    }

    // create a new assignment
    private WfAssignment assign(WfResource resource, boolean append) throws WfException {
        if (!append) {
            Iterator ai = getIteratorAssignment();
            while (ai.hasNext()) {
                WfAssignment a = (WfAssignment) ai.next();
                a.remove();
            }
        }

        WfAssignment assign = WfFactory.getWfAssignment(this, resource, null, true);
        return assign;
    }

    // check for a dynamic performer
    private GenericValue getDynamicPerformer(GenericValue performer) throws WfException {
        GenericValue newPerformer = new GenericValue(performer);
        Map context = processContext();
        String expr = null;
        String field = null;
        Object value = null;

        if (newPerformer.get("partyId") != null && newPerformer.getString("partyId").length() > 0) {
            // first try partyId
            expr = newPerformer.getString("partyId");
            if (expr != null && expr.trim().toLowerCase().startsWith("expr:")) {
                try {
                    value = BshUtil.eval(expr.trim().substring(5).trim(), context);
                } catch (bsh.EvalError e) {
                    throw new WfException("Bsh evaluation error occured.", e);
                }
                field = "partyId";
            }
        }
        if (value == null) {
            // then try roleTypeId
            expr = newPerformer.getString("roleTypeId");
            if (expr != null && expr.trim().toLowerCase().startsWith("expr:")) {
                try {
                    value = BshUtil.eval(expr.trim().substring(5).trim(), context);
                } catch (bsh.EvalError e) {
                    throw new WfException("Bsh evaluation error occured.", e);
                }                    
                field = "roleTypeId";
            }
        }
        if (field != null && value != null) {
            if (Debug.verboseOn())
                Debug.logVerbose("Evaluated expression: " + expr + " Result: " + value, module);
            if (value instanceof String) {
                String resp = (String) value;
                newPerformer.set(field, resp);
            } else {
                throw new WfException("Expression did not return a String");
            }
        }
        return newPerformer;
    }

    /**
     * Activates this activity.
     * @throws WfException
     * @throws CannotStart
     * @throws AlreadyRunning
     */
    public void activate() throws WfException, CannotStart, AlreadyRunning {
        // make sure we aren't already running
        if (this.state().equals("open.running"))
            throw new AlreadyRunning();

        // check the start mode
        String mode = getDefinitionObject().getString("startModeEnumId");

        if (mode == null)
            throw new WfException("Start mode cannot be null");

        if (mode.equals("WAM_AUTOMATIC")) {
            Iterator i = getIteratorAssignment();
            while (i.hasNext())
                 ((WfAssignment) i.next()).changeStatus("CAL_ACCEPTED"); // accept all assignments (AUTO)
            startActivity();
        } else if (howManyAssignment() > 0 && checkAssignStatus(1)) {
            startActivity();
        } else {
            throw new CannotStart();
        }
    }

    /**
     * Complete this activity.
     * @throws WfException General workflow exception.
     * @throws CannotComplete Cannot complete the activity
     */
    public void complete() throws WfException, CannotComplete {
        // check to make sure all assignements are complete
        if (howManyAssignment() > 0 && !checkAssignStatus(2))
            throw new CannotComplete("All assignments have not been completed");
        try {
            container().receiveResults(this, result());
        } catch (InvalidData e) {
            throw new CannotComplete("Invalid result data was passed", e);
        }
        try {
            changeState("closed.completed");
        } catch (InvalidState is) {
            throw new CannotComplete(is.getMessage(), is);
        } catch (TransitionNotAllowed tna) {
            throw new CannotComplete(tna.getMessage(), tna);
        }

        container().activityComplete(this);
    }

    /**
     * Check if a specific assignment is a member of this activity.
     * @param member Assignment object.
     * @throws WfException General workflow exception.
     * @return true if the assignment is a member of this activity.
     */
    public boolean isMemberOfAssignment(WfAssignment member) throws WfException {
        return getAssignments().contains(member);
    }

    /**
     * Getter for the process of this activity.
     * @throws WfException General workflow exception.
     * @return WfProcess Process to which this activity belong.
     */
    public WfProcess container() throws WfException {
        return WfFactory.getWfProcess(delegator, process);
    }

    /**
     * Assign Result for this activity.
     * @param newResult New result.
     * @throws WfException General workflow exception.
     * @throws InvalidData Data is invalid
     */
    public void setResult(Map newResult) throws WfException, InvalidData {
        if (newResult != null && newResult.size() > 0) {
            if (Debug.verboseOn())
                Debug.logVerbose(
                    "[WfActivity.setResult]: putting (" + newResult.size() + ") keys into context.",
                    module);
            Map context = processContext();
            context.putAll(newResult);
            setSerializedData(context);
        }
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
     * Retrieve all assignments of this activity.
     * @param maxNumber the high limit of number of assignment in result set (0 for all).
     * @throws WfException General workflow exception.
     * @return  List of WfAssignment objects.
     */
    public List getSequenceAssignment(int maxNumber) throws WfException {
        if (maxNumber > 0)
            return getAssignments().subList(0, (maxNumber - 1));
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

        if (mode == null)
            throw new CannotComplete("Finish mode cannot be null");

        // Default mode is MANUAL -- only finish if we are automatic
        if (mode.equals("WAM_AUTOMATIC")) {
            // set the status of the assignments
            Iterator i = getIteratorAssignment();

            while (i.hasNext())
                 ((WfAssignment) i.next()).changeStatus("CAL_COMPLETED");
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

        if (valueObject.get("acceptAllAssignments") != null)
            acceptAll = valueObject.getBoolean("acceptAllAssignments").booleanValue();
        if (valueObject.get("completeAllAssignments") != null)
            completeAll = valueObject.getBoolean("completeAllAssignments").booleanValue();

        String statusString = null;

        if (type == 1)
            statusString = "CAL_ACCEPTED";
        else if (type == 2)
            statusString = "CAL_COMPLETED";
        else
            throw new WfException("Invalid status type");

        boolean foundOne = false;

        Iterator i = getIteratorAssignment();

        while (i.hasNext()) {
            WfAssignment a = (WfAssignment) i.next();

            if (a.status().equals(statusString)) {
                foundOne = true;
            } else {
                if ((type == 2 && completeAll) || (type == 1 && acceptAll))
                    return false;
            }
        }

        if ((type == 2 && completeAll) || (type == 1 && acceptAll)) {
            return true;
        } else {
            Debug.logVerbose("[checkAssignStatus] : need only one assignment to pass", module);
            if (foundOne)
                return true;
            Debug.logVerbose("[checkAssignStatus] : found no assignment(s)", module);
            return false;
        }
    }

    // Starts or activates this automatic activity
    private void startActivity() throws WfException, CannotStart {
        try {
            changeState("open.running");
        } catch (InvalidState is) {
            throw new CannotStart(is.getMessage(), is);
        } catch (TransitionNotAllowed tna) {
            throw new CannotStart(tna.getMessage(), tna);
        }
        // check the limit service
        boolean limitAfterStart = getDefinitionObject().getBoolean("limitAfterStart").booleanValue();

        if (limitAfterStart
            && getDefinitionObject().get("limitService") != null
            && !getDefinitionObject().getString("limitService").equals("")) {
            Debug.logVerbose("[WfActivity.init]: limit service is after start, setting up now.", module);
            setLimitService();
        }

        // set the new previousActivity
        Map context = processContext();

        context.put("previousActivity", workEffortId);
        this.setProcessContext(context);

        // set the estimatedStartDate
        try {
            GenericValue v = getRuntimeObject();

            v.set("estimatedStartDate", UtilDateTime.nowTimestamp());
            v.store();
        } catch (GenericEntityException e) {
            Debug.logWarning("Could not set 'estimatedStartDate'.", module);
            e.printStackTrace();
        }

        // get the type of this activity
        String type = getDefinitionObject().getString("activityTypeEnumId");

        if (type == null)
            throw new WfException("Illegal activity type");

        WfActivityAbstractImplementation executor = WfActivityImplementationFact.getConcretImplementation(type, this);
        executor.run();
        this.setResult(executor.getResult());
        if (executor.isComplete())
            this.checkComplete();
    }

    // schedule the limit service to run
    private void setLimitService() throws WfException {
        String serviceLoader = null;

        try {
            serviceLoader = getServiceLoader();
        } catch (WfException e) {
            serviceLoader = container().getRuntimeObject().getString("serviceLoaderName");
        }
        if (serviceLoader == null)
            throw new WfException("Cannot get dispatch service loader name");

        ServiceDispatcher ds = getDispatcher(serviceLoader);

        if (ds == null)
            throw new WfException("Cannot find dispatcher for the associated loader");

        DispatchContext dctx = ds.getLocalContext(serviceLoader);
        String limitService = getDefinitionObject().getString("limitService");
        ModelService service = null;

        try {
            service = dctx.getModelService(limitService);
            Debug.logVerbose("[WfActivity.setLimitService] : Found service model.", module);
        } catch (GenericServiceException e) {
            Debug.logError(e, "[WfActivity.setLimitService] : Cannot get service model.", module);
        }
        if (service == null) {
            Debug.logWarning("[WfActivity.setLimitService] : Cannot determine limit service, ignoring.", module);
            return;
        }

        List inNames = service.getParameterNames(ModelService.IN_PARAM, false);
        String params = StringUtil.join(inNames, ",");
        Map serviceContext = actualContext(params, null, inNames);

        Double timeLimit = null;

        if (getDefinitionObject().get("timeLimit") != null)
            timeLimit = getDefinitionObject().getDouble("timeLimit");
        if (timeLimit == null)
            return;

        String durationUOM = null;

        if (container().getDefinitionObject().getString("durationUomId") != null)
            durationUOM = container().getDefinitionObject().getString("durationUomId");
        if (durationUOM == null)
            return;

        char durChar = durationUOM.charAt(0);
        Calendar cal = Calendar.getInstance();

        cal.setTime(new Date());
        switch (durChar) {
            case 'Y' :
                cal.add(Calendar.YEAR, timeLimit.intValue());
                break;

            case 'M' :
                cal.add(Calendar.MONTH, timeLimit.intValue());
                break;

            case 'D' :
                cal.add(Calendar.DATE, timeLimit.intValue());
                break;

            case 'h' :
                cal.add(Calendar.HOUR, timeLimit.intValue());
                break;

            case 'm' :
                cal.add(Calendar.MINUTE, timeLimit.intValue());
                break;

            case 's' :
                cal.add(Calendar.SECOND, timeLimit.intValue());
                break;

            default :
                throw new WfException("Invalid duration unit");
        }

        long startTime = cal.getTime().getTime();
        Map context = new HashMap();

        context.put("serviceName", limitService);
        context.put("serviceContext", serviceContext);
        context.put("workEffortId", runtimeKey());

        try {
            dctx.getDispatcher().schedule("wfLimitInvoker", context, startTime); // yes we are hard coded!
        } catch (GenericServiceException e) {
            throw new WfException(e.getMessage(), e);
        }
        if (Debug.infoOn())
            Debug.logInfo(
                "[WfActivity.setLimitService]: Set limit service (" + limitService + " ) to run at " + startTime,
                module);
    }

    Map actualContext(String actualParameters, String extendedAttr, List contextSignature) throws WfException {
        Map actualContext = new HashMap();
        Map context = processContext();

        // extended attributes take priority over context attributes
        Map extendedAttributes = StringUtil.strToMap(extendedAttr);

        if (extendedAttributes != null && extendedAttributes.size() > 0)
            context.putAll(extendedAttributes);

        // setup some internal buffer parameters
        GenericValue userLogin = null;

        if (context.containsKey("runAsUser")) {
            userLogin = getUserLogin((String) context.get("runAsUser"));
            actualContext.put("userLogin", userLogin);
        } else if (context.containsKey("workflowOwnerId")) {
            userLogin = getUserLogin((String) context.get("workflowOwnerId"));
        }

        context.put("userLogin", userLogin);
        context.put("workEffortId", runtimeKey());

        if (actualParameters != null) {
            List params = StringUtil.split(actualParameters, ",");
            Iterator i = params.iterator();

            while (i.hasNext()) {
                Object key = i.next();
                String keyStr = (String) key;

                if (keyStr != null && keyStr.trim().toLowerCase().startsWith("expr:")) {
                    try {
                        BshUtil.eval(keyStr.trim().substring(5).trim(), context);
                    } catch (bsh.EvalError e) {
                        throw new WfException("Bsh evaluation error.", e);
                    }
                } else if (keyStr != null && keyStr.trim().toLowerCase().startsWith("name:")) {
                    List couple = StringUtil.split(keyStr.trim().substring(5).trim(), "=");
                    if (contextSignature.contains(((String) couple.get(0)).trim()))
                        actualContext.put(((String) couple.get(0)).trim(), context.get(couple.get(1)));
                } else if (context.containsKey(key)) {
                    if (contextSignature.contains(key))
                        actualContext.put(key, context.get(key));
                } else if (!actualContext.containsKey(key))
                    throw new WfException("Context does not contain the key: '" + (String) key + "'");
            }
        }
        return actualContext;
    }

    // Gets a UserLogin object for service invocation
    // This allows a workflow to invoke a service as a specific user
    private GenericValue getUserLogin(String userId) throws WfException {
        GenericValue userLogin = null;
        try {
            userLogin = getDelegator().findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userId));
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
        return userLogin;
    }
}
