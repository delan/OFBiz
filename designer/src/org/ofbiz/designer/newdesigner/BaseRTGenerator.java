package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.dataclass.*;
import org.ofbiz.designer.networkdesign.*;
import java.util.*;
import java.net.*;
import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.util.*;

abstract class BaseRTGenerator {
    static final String xmlDir = System.getProperty("WF_XMLDIR");
    static final String dtdDir = System.getProperty("WF_DTDDIR");

    static final IArcWrapper getConnectingArc(ITaskWrapper source, ITaskWrapper destination) {
        String[] arcs = IDRefHelper.getReferenceArray(destination.getInarcsAttribute());
        for(int i=0; i<arcs.length; i++) {
            IArcWrapper arc = (IArcWrapper)source.getXml().getIdRef(arcs[i]);
            if(arc.getSourceTaskAttribute().equals(source.getIdAttribute()))
                return arc;
        }
        WARNING.println("returning null");
        return null;
    }

    static final Vector getInputDNF(ITaskWrapper task) {
        if(task.getInputOperator() == null) {
            String[] inarcs = IDRefHelper.getReferenceArray(task.getInarcsAttribute());
            Vector returnVec = new Vector();
            if(inarcs.length > 1) throw new RuntimeException("No input operator specified for org.ofbiz.designer.task " + task.getNameAttribute());
            else if(inarcs.length == 0) return returnVec;
            IArcWrapper arc = (IArcWrapper)task.getXml().getIdRef(inarcs[0]);
            Vector firstElement = new Vector();
            firstElement.addElement(task.getXml().getIdRef(arc.getSourceAttribute()));
            returnVec.addElement(firstElement);
            return returnVec;
        }
        IOperatorWrapper op = (IOperatorWrapper)task.getInputOperator().getOperator();
        return getInputDNF(op);
    }

    static final Vector getOutputDNF(ITaskWrapper task) {
        if(task.getOutputOperator() == null || task.getOutputOperator().getOperator().getFieldCount() == 0) {
            String[] outarcs = IDRefHelper.getReferenceArray(task.getOutarcsAttribute());
            Vector returnVec = new Vector();
            if(outarcs.length == 0) return returnVec;
            IArcWrapper arc = (IArcWrapper)task.getXml().getIdRef(outarcs[0]);
            if(arc.getArctypeAttribute().equals("Fail")) return returnVec;
            Object[] triple = new Object[4];
            triple[0] = task.getXml().getIdRef(arc.getDestinationAttribute());
            if(arc.getAlternativetransitionAttribute() != null) {
                IArcWrapper temp  = (IArcWrapper)task.getXml().getIdRef(arc.getAlternativetransitionAttribute());
                triple[3] = task.getXml().getIdRef(temp.getDestinationAttribute());
            }
            triple[1] = "true";
            int mcount = arc.getMappingCount();
            String objList = "";                    
            for(int j=0; j<mcount; j++) {
                IMapping mapping = arc.getMappingAt(j);
                String temp = mapping.getFirstElementAttribute();
                IOutput output = (IOutput)task.getXml().getIdRef(temp);
                objList += output.getVariablenameAttribute() + " ";
            }
            triple[2] = objList;
            returnVec.addElement(triple);
            return returnVec;
			
        }
        IOperatorWrapper op = (IOperatorWrapper)task.getOutputOperator().getOperator();
        return getOutputDNF(task, op);
    }

    static final Vector getOutputDNF(ITaskWrapper task, IOperatorWrapper op) {
        Vector returnVec = new Vector();
        String operatorType = op.getTypeAttribute();
        if(!operatorType.equals("and") && !operatorType.equals("or"))
            throw new RuntimeException("Invalid operator type for " + task.getNameAttribute());

        int count = op.getFieldCount();
        for(int i=0; i<count; i++) {
            org.ofbiz.designer.networkdesign.IField field = op.getFieldAt(i);
            if(field.getOperator() != null)
                returnVec.addAll(getOutputDNF(task, (IOperatorWrapper)field.getOperator()));
            else if(field.getTaskAttribute() != null) {
                Object[] triple = new Object[4];
                ITaskWrapper destinationTask = (ITaskWrapper)op.getXml().getIdRef(field.getTaskAttribute());
                triple[0] = op.getXml().getIdRef(field.getTaskAttribute());

                if(operatorType.equals("or")) {
                    triple[1] = field.getConditionAttribute();
                    if(triple[1] == null)
                        throw new RuntimeException("Condition is null for org.ofbiz.designer.task " + task.getNameAttribute());
                } else
                    triple[1] = "true";
                IArcWrapper arc = getConnectingArc(task, destinationTask);
                if(arc.getAlternativetransitionAttribute() != null) {
                    IArcWrapper temp  = (IArcWrapper)task.getXml().getIdRef(arc.getAlternativetransitionAttribute());
                    triple[3] = task.getXml().getIdRef(temp.getDestinationAttribute());
                }

                int mcount = arc.getMappingCount();
                String objList = "";                    
                for(int j=0; j<mcount; j++) {
                    IMapping mapping = arc.getMappingAt(j);
                    String temp = mapping.getFirstElementAttribute();
                    IOutput output = (IOutput)task.getXml().getIdRef(temp);
                    objList += output.getVariablenameAttribute() + " ";
                }
                triple[2] = objList;
				org.ofbiz.designer.util.LOG.println("XOXOXOXOXOXO taskName "+task.getNameAttribute()+" "+i);
                returnVec.addElement(triple);
            } else WARNING.println("Empty field encountered");
        }
		
		org.ofbiz.designer.util.LOG.println("2 XOXOXOXOXOXO taskName "+task.getNameAttribute());


        if(op.getTypeAttribute().equals("or")) {
            // flatten conditions
            int vecSize = returnVec.size();
            String condition = null;
            for(int i=0; i<vecSize; i++) {
                Object[] triple = (Object[])returnVec.elementAt(i);
                String temp = (String)triple[1];
                if(condition != null) triple[1] = (String)triple[1] + " && !(" + condition + ")";
                if(condition == null) condition = temp;
                else condition += " || " + temp;
            }
        }
        return returnVec;
    }

    static final Vector getInputDNF(IOperatorWrapper op) {
        Vector returnVec = new Vector();
        if(op.getTypeAttribute().equals("or")) {
            int count = op.getFieldCount();
            for(int i=0; i<count; i++) {
                org.ofbiz.designer.networkdesign.IField field = op.getFieldAt(i);
                if(field.getOperator() != null)
                    returnVec.addAll(getInputDNF((IOperatorWrapper)field.getOperator()));
                else if(field.getTaskAttribute() != null) {
                    Vector tempVec = new Vector();
                    tempVec.addElement(op.getXml().getIdRef(field.getTaskAttribute()));
                    returnVec.addElement(tempVec);
                } else WARNING.println("Empty field encountered");
            }
            return returnVec;
        } else if(op.getTypeAttribute().equals("and")) {
            int count = op.getFieldCount();
            Vector temp = new Vector();
            for(int i=0; i<count; i++) {
                org.ofbiz.designer.networkdesign.IField field = op.getFieldAt(i);
                if(field.getTaskAttribute () == null)
                    continue;
                temp.add(op.getXml().getIdRef(field.getTaskAttribute()));
            }
            int opCount = 0;
            for(int i=0; i<count; i++) {
                org.ofbiz.designer.networkdesign.IField field = op.getFieldAt(i);
                if(field.getOperator() == null)
                    continue;
                opCount++;
                Vector subDNF = getInputDNF((IOperatorWrapper)field.getOperator());
                subDNF.addElement(temp);
                returnVec.addAll(subDNF);
            }
            if(opCount == 0)
                returnVec.add(temp);
            return returnVec;
        } else throw new RuntimeException("Cannot handle operator type " + op.getTypeAttribute());
    }

    static final IDataClass getDataFromUrl(String url) {
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

    static final HashSet getData(ITask task) {
        HashSet returnSet = new HashSet();
        int count = task.getInvocationCount();
        for(int i=0;i<count;i++) {
            IInvocation invocation = task.getInvocationAt(i);
            int pcount = invocation.getParameterCount();
            for(int j=0;j<pcount;j++) {
                org.ofbiz.designer.networkdesign.IParameter param = invocation.getParameterAt(j);
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

    static final HashSet getException(ITask task) {
        HashSet returnSet = new HashSet();
        int count = task.getTaskExceptionCount();
        for(int i=0;i<count;i++) {
            String exceptionName = task.getTaskExceptionAt(i).getDatatypeurlAttribute();
            returnSet.add(exceptionName);
        }
        return returnSet;
    }

    static final Hashtable getDataHashtable(ITask task) {
        Hashtable returnSet = new Hashtable();
        int count = task.getInvocationCount();
        for(int i=0;i<count;i++) {
            IInvocation invocation = task.getInvocationAt(i);
            int pcount = invocation.getParameterCount();
            for(int j=0;j<pcount;j++) {
                org.ofbiz.designer.networkdesign.IParameter param = invocation.getParameterAt(j);
                IDataClass data = getDataFromUrl(param.getDatatypeurlAttribute());
                returnSet.put(param.getVariablenameAttribute(), data);
            }
        }

        count = task.getOutputCount();
        for(int i=0;i<count;i++) {
            IOutput output = task.getOutputAt(i);
            IDataClass data = getDataFromUrl(output.getDatatypeurlAttribute());
            returnSet.put(output.getVariablenameAttribute(), data);
        }
        return returnSet;
    }

    static final String DATA = "Data";
    static final String EXCEPTION = "Exception";
    static final HashSet getAllData(ITaskWrapper mainTask, String mode) {
        if(!(DATA.equals(mode)) && !(EXCEPTION.equals(mode)))
            throw new RuntimeException("Invalid mode");
        Vector tasks = new Vector();
        HashSet returnSet = new HashSet();
        tasks.addElement(mainTask);
        while(tasks.size() > 0) {
            Object obj = tasks.elementAt(0);
            if(obj instanceof ITask) {
                ITask task = (ITask)obj;
                if(task.getRealization().getNetworkTaskRealization() != null)
                    tasks.addElement(task.getRealization().getNetworkTaskRealization());
                if(mode.equals(DATA))
                    returnSet.addAll(getData(task));
                else
                    returnSet.addAll(getException(task));
            } else if(obj instanceof INetworkTaskRealization) {
                INetworkTaskRealization nr = (INetworkTaskRealization)obj;
                int count = nr.getDomainCount();
                for(int i=0;i<count;i++)
                    tasks.addElement(nr.getDomainAt(i));
            } else if(obj instanceof IDomain) {
                IDomain domain = (IDomain)obj;
                int count = domain.getCompartmentCount();
                for(int i=0;i<count;i++)
                    tasks.addElement(domain.getCompartmentAt(i));
                String[] taskRefs = IDRefHelper.getReferenceArray(domain.getTasksAttribute());
                count = taskRefs.length;
                for(int i=0;i<count;i++)
                    tasks.addElement(mainTask.getXml().getIdRef(taskRefs[i]));
            } else if(obj instanceof ICompartment) {
                ICompartment compartment = (ICompartment)obj;
                int count = compartment.getCompartmentCount();
                for(int i=0;i<count;i++)
                    tasks.addElement(compartment.getCompartmentAt(i));
                String[] taskRefs = IDRefHelper.getReferenceArray(compartment.getTasksAttribute());
                count = taskRefs.length;
                for(int i=0;i<count;i++)
                    tasks.addElement(mainTask.getXml().getIdRef(taskRefs[i]));
            }
            tasks.removeElementAt(0);
        }
        return returnSet;
    }

/*
static final IArcWrapper getExceptionArc(ITaskWrapper org.ofbiz.designer.task, String exception) {
    String[] eArcs = IDRefHelper.getReferenceArray(org.ofbiz.designer.task.getExceptionarcsAttribute());
    int count = eArcs.length;
    for (int i=0;i<count;i++) {
        IArcWrapper arc = (IArcWrapper)org.ofbiz.designer.task.getXml().getIdRef(eArcs[i]);
        if (arc.getExceptionAttribute().equals(exception))
            return arc;
    }
    return null;
}
    */


/*
static final String getExceptionMapping(ITaskWrapper org.ofbiz.designer.task, String exception) {
    IArcWrapper arc = getExceptionArc(org.ofbiz.designer.task, exception);
    String returnString = "";
    int count = arc.getMappingCount();
    for (int i=0;i<count;i++) {
        IMapping mapping = arc.getMappingAt(i);
        returnString += " " + ((IOutput)org.ofbiz.designer.task.getXml().getIdRef(mapping.getFirstElementAttribute())).getVariablenameAttribute();
    }
    return returnString;
}
    */

    static final String convert(String str) {
        if(str.equals("string"))
            return "String";
        else if(str.equals("long"))
            return "int";
        else throw new RuntimeException("Cannot handle " + str);
    }

    static final void remove(HashSet set, String type, String name) {
        Iterator it = set.iterator();
        while(it.hasNext()) {
            String[] pair = (String[])it.next();
            if(pair[0].equals(type) && pair[1].equals(name)) {
                set.remove(pair);
                return;
            }
        }
        throw new RuntimeException("Did not find " + type + "  " + name + " in hashset");
    }


    static final boolean contains(HashSet set, String type, String name) {
        Iterator it = set.iterator();
        while(it.hasNext()) {
            String[] pair = (String[])it.next();
            if(pair[0].equals(type) && pair[1].equals(name))
                return true;
        }
        return false;
    }

    static final HashSet getCreateList(ITaskWrapper task) {
        HashSet returnSet = new HashSet();         
        int count = task.getOutputCount();
        for(int i=0; i<count; i++) {
            IOutput output = task.getOutputAt(i);
            if(!contains(returnSet, output.getDatatypeurlAttribute(), output.getVariablenameAttribute())) {
                String[] pair = new String[2];
                pair[0] = output.getDatatypeurlAttribute();
                pair[1] = output.getVariablenameAttribute();
                returnSet.add(pair);
            }
        }

        count = task.getInvocationCount();
        for(int i=0; i<count; i++) {
            IInvocation invocation = task.getInvocationAt(i);
            int iCount = invocation.getParameterCount();
            for(int j=0; j<iCount; j++) {
                org.ofbiz.designer.networkdesign.IParameter parameter = invocation.getParameterAt(j);
                if(contains(returnSet, parameter.getDatatypeurlAttribute(), parameter.getVariablenameAttribute()))
                    remove(returnSet, parameter.getDatatypeurlAttribute(), parameter.getVariablenameAttribute());
            }
        }
        return returnSet;
    }
}
