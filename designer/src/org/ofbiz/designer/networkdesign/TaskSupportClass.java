package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.dataclass.*;
import java.io.*;
import java.net.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class TaskSupportClass extends AbstractDataSupportClass implements ITaskSupportClass {
    private static final String xmlDir = System.getProperty("WF_XMLDIR");
    private static final String dtdDir = System.getProperty("WF_DTDDIR");

    public IDataSecurityMask getDataSecurityMaskByName(String dataName) {
        String paramID = getIDByName(dataName);
        int count = task().getDataSecurityMaskCount();
        for(int i=0; i<count; i++) {
            IDataSecurityMask mask = task().getDataSecurityMaskAt(i);
            if(mask.getDatanameAttribute().equals(paramID))
                return mask;
        }
        return null;
    }

    private String getIDByName(String name){
        if(task().getInvocationCount() == 0) 
            throw new RuntimeException("Invocation count is 0");
        int count = task().getInvocationAt(0).getParameterCount();
        for(int i=0;i<count;i++) {
            IParameter param = task().getInvocationAt(0).getParameterAt(i);
            if(param.getVariablenameAttribute().equals(name)) 
                return param.getIdAttribute();
        }
        return null;
    }

    public Vector getDataSecurityMaskNames() {
        int count = task().getDataSecurityMaskCount();
        Vector returnVec = new Vector();
        for(int i=0; i<count; i++) {
            String paramID = task().getDataSecurityMaskAt(i).getDatanameAttribute();
            String paramName = ((IParameter)getXml().getIdRef(paramID)).getVariablenameAttribute();
            returnVec.addElement(paramName);
        }
        return returnVec;
    }

    public Vector getInvocationParameters() {
        int count = task().getInvocationCount();
        if(count > 1)
            throw new RuntimeException("Cannot handle multiple invocations");
        if(count == 0)
            return new Vector();

        Vector returnVec = new Vector();
        IInvocation invocation = task().getInvocationAt(0);
        count = invocation.getParameterCount();
        for(int i=0; i<count; i++) {
            String name = invocation.getParameterAt(i).getVariablenameAttribute();
            String type = invocation.getParameterAt(i).getDatatypeurlAttribute();
            returnVec.addElement(type + " " + name);
        }
        return returnVec;
    }

    public Vector getOutputNames() {
        int count = task().getOutputCount();
        Vector returnVec = new Vector();
        for(int i=0; i<count; i++) {
            String name = task().getOutputAt(i).getVariablenameAttribute();
            String type = task().getOutputAt(i).getDatatypeurlAttribute();
            returnVec.addElement(type + " " + name);
        }
        return returnVec;
    }

    private static IDataSecurityMask getMaskByName(ITask task, String dataName) {
        int count = task.getDataSecurityMaskCount();
        for(int i=0; i<count; i++) {
            IDataSecurityMask mask = task.getDataSecurityMaskAt(i);
            if(mask.getDatanameAttribute().equals(dataName))
                return mask;
        }
        return null;
    }

    private static String getAccessByFieldName(IDataSecurityMask mask, String fieldName) {
        if(mask == null) return null;
        int count = mask.getFieldMaskCount();
        for(int i=0;i<count;i++) {
            IFieldMask fieldMask = mask.getFieldMaskAt(i);
            if(fieldMask.getFieldnameAttribute().equals(fieldName)) 
                return fieldMask.getAccesstypeAttribute();
        }
        return null;
    }

    public Vector getExplodedReadableParameters() {
        Vector returnVec = new Vector();
        int count = task().getInvocationCount();
        for(int i=0; i<count; i++) {
            IInvocation invocation = task().getInvocationAt(i);
            int paramCount = invocation.getParameterCount();
            for(int j=0; j<paramCount; j++) {
                IParameter param = invocation.getParameterAt(j);
                String dataURL = param.getDatatypeurlAttribute();

                dataURL = fixDataURL(dataURL);
                String varName = param.getVariablenameAttribute();

                IDataClass data = (IDataClass)getXml().getHref(dataURL);
                IFieldList fl = data.getFieldList();
                int fieldCount = fl.getFieldCount();
                IDataSecurityMask mask = getMaskByName(task(), varName);
                for(int k=0; k<fieldCount; k++) {
                    org.ofbiz.designer.dataclass.IField field = fl.getFieldAt(k);
                    String access = getAccessByFieldName(mask, field.getName());
                    if(access != null && access.equals("NoAccess")) {
                        continue;
                    }
                    String str = field.getType() + " " + field.getName();
                    returnVec.addElement(str);
                }
            }
        }
        return returnVec;
    }

    public Vector getExplodedWritableParameters() {
        Vector returnVec = new Vector();
        int paramCount = task().getOutputCount();
        jLoop:
        for(int j=0; j<paramCount; j++) {
            IOutput param = task().getOutputAt(j);
            String dataURL = param.getDatatypeurlAttribute();
            dataURL = fixDataURL(dataURL);
            String varName = param.getVariablenameAttribute();

            IDataClass data = (IDataClass)getXml().getHref(dataURL);
            IFieldList fl = data.getFieldList();
            int fieldCount = fl.getFieldCount();
            IDataSecurityMask mask = getMaskByName(task(), varName);
            for(int k=0; k<fieldCount; k++) {
                org.ofbiz.designer.dataclass.IField field = fl.getFieldAt(k);
                String access = getAccessByFieldName(mask, field.getName());
                if(access != null && !access.equals("FullControl")) 
                    continue jLoop;
                String str = field.getType() + " " + field.getName();
                returnVec.addElement(str);
            }
        }
        return returnVec;
    }


    public Vector getExceptionNames() {
        int count = task().getTaskExceptionCount();
        Vector returnVec = new Vector();
        for(int i=0; i<count; i++) 
            returnVec.addElement(task().getTaskExceptionAt(i).getDatatypeurlAttribute());
        return returnVec;
    }

    public Vector getConstraintNames() {
        int count = task().getConstraintCount();
        Vector returnVec = new Vector();
        for(int i=0; i<count; i++) 
            returnVec.addElement(task().getConstraintAt(i));
        return returnVec;
    }

    public IDataSecurityMask createDataSecurityMask(String dataName) {
        throw new RuntimeException("Not implemented");
    }

    public boolean isPureInputParameter(String dataName) {
        int count = task().getOutputCount();
        for(int i=0; i<count; i++) 
            if(task().getOutputAt(i).getVariablenameAttribute().equals(dataName))
                return false;

        return true;
    }

    public Vector getInputArcs() {
        String inarcs = task().getInarcsAttribute();
        Vector inArcVector = new Vector();
        if(inarcs == null) return inArcVector;
        StringTokenizer stk = new StringTokenizer(inarcs);
        while(stk.hasMoreTokens()) {
            String next = stk.nextToken();
            IArc arc = (IArc)getXml().getIdRefRaw(next);
            String sourceID = arc.getSourceAttribute();
            String sourceName = null;
            if(getXml().getIdRefRaw(sourceID) instanceof ITask) 
                sourceName = ((ITask)getXml().getIdRefRaw(sourceID)).getNameAttribute();
            else {
                IArc arc2 = (IArc)getXml().getIdRefRaw(sourceID);
                sourceName = ((ITask)getXml().getIdRefRaw(arc2.getSourceAttribute())).getNameAttribute();
            }

            inArcVector.addElement(sourceName);
        }
        return inArcVector;
    }

    public boolean isFirstTask(){
        if(getParentTask() == null) return false;
        String first = getParentTask().getRealization().getNetworkTaskRealization().getFirsttaskAttribute();
        if(first == null) return false;
        else if(first.equals(task().getIdAttribute())) return true;
        else return false;
    }

    public boolean isLastTask(){
        if(getParentTask() == null) return false;
        String last = getParentTask().getRealization().getNetworkTaskRealization().getLasttaskAttribute();
        if(last == null) return false;
        else if(last.equals(task().getIdAttribute())) return true;
        else return false;
    }

    public Vector getInputArcsAndParent() {
        Vector inArcVector = getInputArcs();
        if(isFirstTask()) inArcVector.addElement(getParentTask().getNameAttribute());
        return inArcVector;
    }

    public Vector getOutputArcsAndParent() {
        Vector outArcVector = getOutputArcs();
        if(isLastTask()) outArcVector.addElement(getParentTask().getNameAttribute());
        return outArcVector;
    }

    public Vector getOutputArcs() {
        String outarcs = task().getOutarcsAttribute();
        Vector outArcVector = new Vector();
        if(outarcs == null) return outArcVector;
        StringTokenizer stk = new StringTokenizer(outarcs);
        while(stk.hasMoreTokens()) {
            String next = stk.nextToken();
            IArc arc = (IArc)getXml().getIdRefRaw(next);
            String destinationID = arc.getDestinationAttribute();
            String destinationName = ((ITask)getXml().getIdRefRaw(destinationID)).getNameAttribute();
            outArcVector.addElement(destinationName);
        }
        return outArcVector;
    }

    public Vector getExceptionArcs() {
        String exceptionArcs = task().getOutarcsAttribute();
        Vector exceptionArcVector = new Vector();
        if(exceptionArcs == null) return exceptionArcVector;
        StringTokenizer stk = new StringTokenizer(exceptionArcs);
        while(stk.hasMoreTokens()){
            String next = stk.nextToken();
            IArc arc = (IArc)getXml().getIdRef(next);
            if(arc.getArctypeAttribute().equals("Fail")){
                String destinationID = arc.getDestinationAttribute();
                String destinationName = ((ITask)getXml().getIdRefRaw(destinationID)).getNameAttribute();
                exceptionArcVector.addElement(destinationName);
            }
        }
        return exceptionArcVector;
    }

    public static final String SYNCHRONIZATIONTASKIN = "SynchronizationTaskIn";
    public static final String SYNCHRONIZATIONTASKOUT = "SynchronizationTaskOut";

    public static final String COLLABORATIONREALIZATION = "CollaborationRealization";
    public static final String HUMANREALIZATION = "HumanRealization";
    public static final String NONTRANSACTIONALTASKREALIZATION = "NonTransactionalTaskRealization";
    public static final String TRANSACTIONALTASKREALIZATION = "TransactionalTaskRealization";
    public static final String NONTRANSACTIONALNETWORK = "NonTransactionalNetwork";
    public static final String TRANSACTIONALNETWORK = "TransactionalNetwork";
    public static final String OPEN2PCNETWORK = "Open2PCNetwork";
    public static final String COMPOSITENETWORK = "CompositeNetwork";

    public static final String AND="and";
    public static final String OR="or";
    public static final String Loop="loop";

    static Vector simpleTaskTypes = new Vector();
    static Vector syncTaskTypes = new Vector();
    static Vector networkTaskTypes = new Vector();
    static Vector allTaskTypes = new Vector();
    static {
        simpleTaskTypes.addElement(COLLABORATIONREALIZATION);
        simpleTaskTypes.addElement(HUMANREALIZATION);
        simpleTaskTypes.addElement(NONTRANSACTIONALTASKREALIZATION);
        simpleTaskTypes.addElement(TRANSACTIONALTASKREALIZATION);

        networkTaskTypes.addElement(NONTRANSACTIONALNETWORK);
        networkTaskTypes.addElement(TRANSACTIONALNETWORK);
        networkTaskTypes.addElement(OPEN2PCNETWORK);
        networkTaskTypes.addElement(COMPOSITENETWORK);

        syncTaskTypes.addElement(SYNCHRONIZATIONTASKOUT);
        syncTaskTypes.addElement(SYNCHRONIZATIONTASKIN);

        allTaskTypes.addAll(simpleTaskTypes);
        allTaskTypes.addAll(networkTaskTypes);
        allTaskTypes.addAll(syncTaskTypes);
    }

    public Vector getTaskTypes() {
        return allTaskTypes;
    }

    public String getTaskType() {
        IRealization realization = task().getRealization();
        if(realization.getSyncRealization() != null) {
            ISyncRealization sr = realization.getSyncRealization();
            return sr.getSyncTypeAttribute();
        }
        try {
            return realization.getNetworkTaskRealization().getRealizationtypeAttribute();
        } catch(NullPointerException e) {
            // not a network org.ofbiz.designer.task
        }
        if(realization.getSimpleRealization() == null) return null;
        ISimpleRealization simpleRealization = realization.getSimpleRealization();
        if(simpleRealization.getCollaborationRealization() != null) return COLLABORATIONREALIZATION;
        else if(simpleRealization.getHumanRealization() != null) return HUMANREALIZATION;
        else if(simpleRealization.getNonTransactionalTaskRealization() != null) return NONTRANSACTIONALTASKREALIZATION;
        else if(simpleRealization.getTransactionalTaskRealization() != null) return TRANSACTIONALTASKREALIZATION;
        else return null;
    }

    public void setTaskType(String taskType) {
        //if(!SYNCREALIZATION.equals(taskType) && !allTaskTypes.contains(taskType)) throw new RuntimeException("Invalid tasktype " + taskType);
        if(taskType.equals(getTaskType())) {
            WARNING.println("duplicate call to settasktype " + taskType);
            return;
        }
        IRealization realization = task().getRealization();

        if(simpleTaskTypes.contains(taskType)) {
            if(realization.getNetworkTaskRealization() != null) realization.setNetworkTaskRealization(null);
            if(realization.getSimpleRealization() == null)
                realization.setSimpleRealization(new SimpleRealization());

            ISimpleRealization simpleRealization = realization.getSimpleRealization();

            if(taskType.equals(COLLABORATIONREALIZATION)) {
                ICollaborationRealization cr = new CollaborationRealization();
                ICollaborationObject co = new CollaborationObject();
                cr.setCollaborationObject(co);
                simpleRealization.setCollaborationRealization(cr);
            } else if(taskType.equals(HUMANREALIZATION)) {
                IHumanRealization hr = new HumanRealization();
                simpleRealization.setHumanRealization(hr);
            } else if(taskType.equals(NONTRANSACTIONALTASKREALIZATION)) {
                INonTransactionalTaskRealization nr = new NonTransactionalTaskRealization();
                //nr.setTaskInvocation("");
                ICorbaInvocation ci = new CorbaInvocation();
                IForwardMappingList fl = new ForwardMappingList();
                IReverseMappingList rl = new ReverseMappingList();
                ci.setForwardMappingList(fl);
                ci.setReverseMappingList(rl);
                ci.setObjectmarkerAttribute("null");
                ci.setServernameAttribute("null");
                ci.setServerhostAttribute("null");
                ci.setClassnameAttribute("null");
                ci.setMethodnameAttribute("null");
                ci.setReturnvalueAttribute("null");
                nr.setCorbaInvocation(ci);
                simpleRealization.setNonTransactionalTaskRealization(nr);
            } else if(taskType.equals(TRANSACTIONALTASKREALIZATION))
                simpleRealization.setTransactionalTaskRealization(new TransactionalTaskRealization());
        } else if(syncTaskTypes.contains(taskType)) {
            if(realization.getNetworkTaskRealization() != null) realization.setNetworkTaskRealization(null);
            if(realization.getSimpleRealization() != null) realization.setSimpleRealization(null);
            if(realization.getSyncRealization() == null) {
                ISyncRealization sr = new SyncRealization();
                sr.setSyncTypeAttribute(taskType);
                sr.setPCDATA("DEFAULT SYNC DATA");
                realization.setSyncRealization(sr);
            }
        } else if(networkTaskTypes.contains(taskType)) {
            if(realization.getSimpleRealization() != null) realization.setSimpleRealization(null);
            if(realization.getNetworkTaskRealization() == null) realization.setNetworkTaskRealization(new NetworkTaskRealization());
            INetworkTaskRealization nr = realization.getNetworkTaskRealization();
            nr.setRealizationtypeAttribute(taskType);
            IDomain domain = new Domain();
            String domainID = getXml().generateUniqueName("Domain");
            domain.setIdAttribute(domainID);
            domain.setUrlAttribute(task().getSecuritydomainurlAttribute());
            domain.setXAttribute("0");
            domain.setYAttribute("0");
            domain.setWidthAttribute("100");
            domain.setHeightAttribute("100");
            nr.addDomain(domain);

            IInputMappingList il = new InputMappingList();
            IOutputMappingList ol = new OutputMappingList();
            nr.setInputMappingList(il);
            nr.setOutputMappingList(ol);
            //getXml().setIdRef(domainID, domain);
            notifyElementAdded(domain, nr);
        }
        notifyDataModified(task());
    }

    public void addOutputParameter(String paramStr) {
        StringTokenizer stk = new StringTokenizer(paramStr);
        String dataType = stk.nextToken();
        String variableName = stk.nextToken();

        //String paramID = org.ofbiz.designer.task().getIdAttribute() +  Math.random();
        String paramID = getXml().generateUniqueName(task().getIdAttribute()+"_param");
        IOutput output = new Output();
        output.setIdAttribute(paramID);
        output.setDatatypeurlAttribute(dataType);
        output.setVariablenameAttribute(variableName);

        task().addOutput(output);
        notifyElementAdded(output, task());
    }

    public void addInvocationParameter(String paramStr) {
        StringTokenizer stk = new StringTokenizer(paramStr);
        String dataType = stk.nextToken();
        String variableName = stk.nextToken();

        //String paramID = org.ofbiz.designer.task().getIdAttribute() +  Math.random();
        String paramID = getXml().generateUniqueName(task().getIdAttribute() +  "_input");
        if(task().getInvocationCount() == 0) task().addInvocation(new Invocation());
        IParameter parameter = new Parameter();
        parameter.setIdAttribute(paramID);
        parameter.setDatatypeurlAttribute(dataType);
        parameter.setVariablenameAttribute(variableName);

        task().getInvocationAt(0).addParameter(parameter);
        //getXml().setIdRef(parameter.getIdAttribute(), parameter);
        notifyElementAdded(parameter, task());
        if (initializeSecurityMasks())
            notifyDataModified(task());
    }

    public void addTaskException(String paramStr) {
        //String paramID = org.ofbiz.designer.task().getIdAttribute() +  Math.random();
        String paramID = getXml().generateUniqueName(task().getIdAttribute() +  "_exception");
        ITaskException exception = new TaskException();
        exception.setIdAttribute(paramID);
        exception.setDatatypeurlAttribute(paramStr);

        task().addTaskException(exception);
        notifyElementAdded(exception, task());
    }

    public IRoles getRolesByName(String selected) {
        int count = task().getRolesCount();
        for(int i=0; i<count; i++) {
            IRoles roles = task().getRolesAt(i);
            String roledomain = roles.getRoledomainAttribute();
            if(selected.equals(roledomain))
                return roles;
        }
        WARNING.println("could not find org.ofbiz.designer.roledomain for " + selected);
        return null;
    }

    public void removeInArcByName(String name) {
        String inarcs = task().getInarcsAttribute();
        StringTokenizer stk = new StringTokenizer(inarcs);
        INetworkDesign context = (INetworkDesign)getXml().getRoot();

        while(stk.hasMoreTokens()) {
            String next = stk.nextToken();
            IArc arc = (IArc)getXml().getIdRefRaw(next);
            if(arc.getSourceAttribute().equals(name)) {
                context.removeArc(arc);
                notifyElementRemoved(arc, task());
                return;
            }
        }

        throw new RuntimeException("Not implemented");
    }

    public void removeInvocationParameter(String paramStr) {
        if(task().getInvocationCount() == 0) {
            WARNING.println(paramStr + " parameter does not exist in invocation");
            return;
        }
        IInvocation invocation = task().getInvocationAt(0);
        StringTokenizer stk = new StringTokenizer(paramStr);
        String paramType = stk.nextToken();
        String paramName = stk.nextToken();

        for(int i=0; i<invocation.getParameterCount(); i++) {
            IParameter parameter = invocation.getParameterAt(i);
            String type = parameter.getDatatypeurlAttribute();
            String name = parameter.getVariablenameAttribute();
            if(type.equals(paramType) && name.equals(paramName)) {
                IParameter obj = invocation.getParameterAt(i);
                invocation.removeParameterAt(i);
                getXml().removeIdRef(obj.getIdAttribute());
                //notifyElementRemoved(obj, org.ofbiz.designer.task());
                if (initializeSecurityMasks())
                    notifyDataModified(task());
                return;
            }
        }

        WARNING.println(paramStr + " parameter does not exist in invocation");
    }

    public void removeOutputParameter(String paramStr) {
        StringTokenizer stk = new StringTokenizer(paramStr);
        String paramType = stk.nextToken();
        String paramName = stk.nextToken();

        for(int i=0; i<task().getOutputCount(); i++) {
            IOutput output = task().getOutputAt(i);
            String type = output.getDatatypeurlAttribute();
            String name = output.getVariablenameAttribute();
            if(type.equals(paramType) && name.equals(paramName)) {
                Object obj = task().getOutputAt(i);
                task().removeOutputAt(i);
                notifyElementRemoved(obj, task());
                return;
            }
        }

        WARNING.println(paramStr + " output does not exist");
    }

    public void removeTaskException(String paramStr) {
        for(int i=0; i<task().getTaskExceptionCount(); i++) {
            String exception = task().getTaskExceptionAt(i).getDatatypeurlAttribute();
            if(exception.equals(paramStr)) {
                Object obj = task().getTaskExceptionAt(i);
                task().removeTaskExceptionAt(i);
                notifyElementRemoved(obj, task());
                return;
            }
        }

        WARNING.println(paramStr + " output does not exist");
    }

    public boolean isForeign() {
        if(task().getForeigntaskAttribute() == null) return false;
        if(task().getForeigntaskAttribute().equals("false")) return false;
        if(task().getForeigntaskAttribute().equals("true")) return false;
        throw new RuntimeException("Invalid foreign attribute value");
    }

    public void setForeign(boolean value) {
        if(task().getForeigntaskAttribute() == null) {
            task().setForeigntaskAttribute("" + value);
            notifyDataModified(task());
            return;
        }
        if(value && task().getForeigntaskAttribute().equals("true")) return;
        if(!value && task().getForeigntaskAttribute().equals("false")) return;
        task().setForeigntaskAttribute("" + value);
        notifyDataModified(task());
    }

    public IOperator createInputOperator() {
        IInputOperator inputOperator = new InputOperator();
        IOperator operator = new Operator();
        //String ID = "Operator" + Math.random();
        String ID = getXml().generateUniqueName("Operator");
        operator.setIdAttribute(ID);
        operator.setTypeAttribute(AND);

        inputOperator.setOperator(operator);
        task().setInputOperator(inputOperator);
        //getXml().setIdRef(ID, operator);
        notifyElementAdded(operator, task());
        return operator;
    }

    public IOperator createOutputOperator() {
        IOutputOperator outputOperator = new OutputOperator();
        IOperator operator = new Operator();
        String ID = getXml().generateUniqueName("Operator");
        operator.setIdAttribute(ID);
        operator.setTypeAttribute(AND);

        outputOperator.setOperator(operator);
        task().setOutputOperator(outputOperator);
        //getXml().setIdRef(ID, operator);
        notifyElementAdded(operator, task());
        return operator;
    }

    public ITask getParentTask() {
        INetworkDesign context = (INetworkDesign)getXml().getRoot();
        int taskCount = context.getTaskCount();
        for(int i=0; i<taskCount; i++) {
            ITaskWrapper tempTask = (ITaskWrapper)context.getTaskAt(i);
            if(tempTask.containsTask(task()))
                return tempTask;
        }
        return null;
    }

    public boolean containsTaskRecursive(ITask childTask) {
        Vector all = new Vector();
        if(task().getRealization().getNetworkTaskRealization() != null)
            all.addElement(task().getRealization().getNetworkTaskRealization());
        else
            return false;
        String childID = childTask.getIdAttribute();
        while(true) {
            if(all.size() == 0) return false;
            Object current = all.elementAt(0);
            if(current instanceof ITask) {
                ITask temp = (ITask)current;
                if(temp.getIdAttribute().equals(childID)) return true;
                if(temp.getRealization().getNetworkTaskRealization() != null)
                    all.addElement(temp.getRealization().getNetworkTaskRealization());
            } else if(current instanceof INetworkTaskRealization) {
                INetworkTaskRealization temp = (INetworkTaskRealization)current;
                int count = temp.getDomainCount();
                for(int i=0; i<count; i++)
                    all.addElement(temp.getDomainAt(i));
            } else if(current instanceof IDomain) {
                IDomain temp = (IDomain)current;
                int count = temp.getCompartmentCount();
                for(int i=0; i<count; i++)
                    all.addElement(temp.getCompartmentAt(i));
                String[] taskIDs = IDRefHelper.getReferenceArray(temp.getTasksAttribute());
                count = taskIDs.length;
                for(int i=0; i<count; i++)
                    all.addElement(getXml().getIdRefRaw(taskIDs[i]));
            } else if(current instanceof ICompartment) {
                ICompartment temp = (ICompartment)current;
                int count = temp.getCompartmentCount();
                for(int i=0; i<count; i++)
                    all.addElement(temp.getCompartmentAt(i));
                String[] tasks = IDRefHelper.getReferenceArray(temp.getTasksAttribute());
                for(int i=0; i<tasks.length; i++)
                    if(tasks[i].equals(childID)) return true;
            }
            all.removeElementAt(0);
        }
    }

    public boolean containsTask(ITask childTask) {
        Vector all = new Vector();
        if(task().getRealization().getNetworkTaskRealization() != null)
            all.addElement(task().getRealization().getNetworkTaskRealization());
        else
            return false;
        String childID = childTask.getIdAttribute();
        while(true) {
            if(all.size() == 0) return false;
            Object current = all.elementAt(0);
            if(current instanceof ITask) {
                ITask temp = (ITask)current;
                if(temp.getIdAttribute().equals(childID)) return true;
            } else if(current instanceof INetworkTaskRealization) {
                INetworkTaskRealization temp = (INetworkTaskRealization)current;
                int count = temp.getDomainCount();
                for(int i=0; i<count; i++)
                    all.addElement(temp.getDomainAt(i));
            } else if(current instanceof IDomain) {
                IDomain temp = (IDomain)current;
                int count = temp.getCompartmentCount();
                for(int i=0; i<count; i++)
                    all.addElement(temp.getCompartmentAt(i));
                String[] taskIDs = IDRefHelper.getReferenceArray(temp.getTasksAttribute());
                count = taskIDs.length;
                for(int i=0; i<count; i++)
                    all.addElement(getXml().getIdRefRaw(taskIDs[i]));
            } else if(current instanceof ICompartment) {
                ICompartment temp = (ICompartment)current;
                int count = temp.getCompartmentCount();
                for(int i=0; i<count; i++)
                    all.addElement(temp.getCompartmentAt(i));
                String[] tasks = IDRefHelper.getReferenceArray(temp.getTasksAttribute());
                for(int i=0; i<tasks.length; i++)
                    if(tasks[i].equals(childID)) return true;
            }
            all.removeElementAt(0);
        }
    }

    public void setSecuritydomainurlAttribute(String url) {
        String oldUrl = task().getSecuritydomainurlAttribute();
        task().setSecuritydomainurlAttribute(url);
        if(task().getRealization().getNetworkTaskRealization() != null) {
            INetworkTaskRealization nr = task().getRealization().getNetworkTaskRealization();
            int count = nr.getDomainCount();
            for(int i=0; i<count; i++) {
                IDomain domain = nr.getDomainAt(i);
                String domainUrl = domain.getUrlAttribute();
                if(match(oldUrl, domainUrl)) {
                    domain = (IDomain)getXml().getIdRef(domain.getIdAttribute()); // get the proxy
                    domain.setUrlAttribute(url);
                    //notifyDataChanged(domain);
                }
            }
            notifyDataModified(task());
            return;
        }
    }

    private String fixDataURL(String dataURL) {
        // fix url of data
        File root = (File)getXml().getKey();
        String cd = root.getAbsolutePath();
        int index = cd.lastIndexOf("\\");
        cd = cd.substring(0, index);
        // david
        //dataURL = "file:///" + cd + "\\..\\data\\" + dataURL + ".xml";
        dataURL = "file:///" + cd + "\\data\\" + dataURL + ".xml";
        return dataURL;
    }

    // create a default security mask (least restrictive) for all data fields
    private boolean initializeSecurityMasks() {
        boolean returnVal = false; // changed ?
        HashSet existingNames = new HashSet();
        int count = task().getDataSecurityMaskCount();
        for(int i=count-1; i>=0; i--){
            String paramID = task().getDataSecurityMaskAt(i).getDatanameAttribute();
            if(getXml().getIdRef(paramID) == null) {
                task().removeDataSecurityMaskAt(i);
                returnVal = true;
            }
            else {
                LOG.println("********** found existing mask for " + task().getDataSecurityMaskAt(i).getDatanameAttribute());
                existingNames.add(task().getDataSecurityMaskAt(i).getDatanameAttribute());
            }
        }

        //HashSet dataSet = getDataTypes(org.ofbiz.designer.task());
        HashSet dataSet = getDataNameAndType(task());
        HashSet temp = (HashSet)dataSet.clone();
        Iterator it = temp.iterator();
        while(it.hasNext()) {
            Object[] triple = (Object[])it.next();
            IDataClass data = (IDataClass)triple[0];
            String dataName = (String)triple[1];
            String paramID = (String)triple[2];
            if(existingNames.contains(paramID))
                dataSet.remove(triple);
        }

        if(dataSet.size() == 0) return returnVal;
        it = dataSet.iterator();
        while(it.hasNext()) {
            Object[] triple = (Object[])it.next();
            IDataClass data = (IDataClass)triple[0];
            String dataName = (String)triple[1];
            String paramID = (String)triple[2];
            IDataSecurityMask dataSecurityMask = new DataSecurityMask();
            LOG.println("******** creating mask for " +    paramID);
            dataSecurityMask.setDatanameAttribute(paramID);
            int fieldCount = data.getFieldList().getFieldCount();
            for(int i=0; i<fieldCount; i++) {
                org.ofbiz.designer.dataclass.IField field = data.getFieldList().getFieldAt(i);
                IFieldMask fieldMask = new FieldMask();
                fieldMask.setAccesstypeAttribute("FullControl");
                fieldMask.setFieldnameAttribute(field.getName());
                dataSecurityMask.addFieldMask(fieldMask);          
            }
            task().addDataSecurityMask(dataSecurityMask);
            returnVal = true;
            //notifyDataChanged();
        }
        return returnVal;
    }

    private static IDataClass getDataFromUrl(String url) {
        URL dataUrl = null;
        try {
            dataUrl = new URL(XmlWrapper.fixURL(xmlDir + "\\data\\" + url + ".xml"));
        } catch(Exception e) {
            e.printStackTrace();
        }
        XmlWrapper xml = XmlWrapper.openDocument(dataUrl);
        IDataClass data = (IDataClass)xml.getRoot();
        return data;
    }

    //public static HashSet getData(ITask org.ofbiz.designer.task) {
    private static HashSet getDataTypes(ITask task) {
        HashSet returnSet = new HashSet();
        int count = task.getInvocationCount();
        for(int i=0;i<count;i++) {
            IInvocation invocation = task.getInvocationAt(i);
            int pcount = invocation.getParameterCount();
            for(int j=0;j<pcount;j++) {
                IParameter param = invocation.getParameterAt(j);
                IDataClass data = getDataFromUrl(param.getDatatypeurlAttribute());
                returnSet.add(data);
            }
        }

        count = task.getOutputCount();
        for(int i=0;i<count;i++) {
            IOutput output = task.getOutputAt(i);
            IDataClass data = getDataFromUrl(output.getDatatypeurlAttribute());
            returnSet.add(data);
        }
        return returnSet;
    }

    private static HashSet getDataNameAndType(ITask task) {
        HashSet returnSet = new HashSet();
        int count = task.getInvocationCount();
        for(int i=0;i<count;i++) {
            IInvocation invocation = task.getInvocationAt(i);
            int pcount = invocation.getParameterCount();
            for(int j=0;j<pcount;j++) {
                IParameter param = invocation.getParameterAt(j);
                Object[] triple = new Object[3];
                triple[0] = getDataFromUrl(param.getDatatypeurlAttribute());
                triple[1] = param.getVariablenameAttribute();
                triple[2] = param.getIdAttribute();
                returnSet.add(triple);
            }
        }

/*
        count = org.ofbiz.designer.task.getOutputCount();
        for(int i=0;i<count;i++) {
            IOutput output = org.ofbiz.designer.task.getOutputAt(i);
            Object[] triple = new Object[3];
            triple[0] = getDataFromUrl(output.getDatatypeurlAttribute());
            triple[1] = output.getVariablenameAttribute();
            triple[2] = output.getIdAttribute();
            returnSet.add(triple);
        }
        */
        return returnSet;
    }

    private ITask task() {
        return(ITask)getDtdObject();
    }

    private static boolean match(String str1, String str2) {
        return(str1 == null && str2 == null) || (str1 != null && str1.equals(str2));
    }
}
