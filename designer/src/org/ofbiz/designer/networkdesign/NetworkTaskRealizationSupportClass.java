package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.util.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class NetworkTaskRealizationSupportClass extends AbstractDataSupportClass implements INetworkTaskRealizationSupportClass {
    public Vector getInputMappingNames() {
        IMapping[] mappings = workflow().getInputMappingList().getMappings();
        return getMappingNames(mappings);
    }

    public Vector getOutputMappingNames() {
        IMapping[] mappings = workflow().getOutputMappingList().getMappings();
        return getMappingNames(mappings);
    }

    public void addInputMappingByName(String mappingStr) {
        ITask source = parentTask();
        ITask destination = (ITask)getXml().getIdRefRaw(workflow().getFirsttaskAttribute());
        IMapping mapping = createMappingByName(source, destination, mappingStr, INPUT);
        workflow().getInputMappingList().addMapping(mapping);
        notifyElementAdded(mapping, workflow());
    }

    public void addOutputMappingByName(String mappingStr) {
        ITask source = (ITask)getXml().getIdRefRaw(workflow().getLasttaskAttribute());
        ITask destination = parentTask();
        IMapping mapping = createMappingByName(source, destination, mappingStr, OUTPUT);
        workflow().getOutputMappingList().addMapping(mapping);
        notifyElementAdded(mapping, workflow());
    }

    public void removeInputMappingByName(String mappingStr) {
        ITask source = parentTask();
        ITask destination = (ITask)getXml().getIdRefRaw(workflow().getFirsttaskAttribute());
        IMapping mapping = getMappingByName(source, destination, mappingStr);
        //workflow().getInputMappingList().removeMapping(mapping);
        removeMappingFromList(workflow().getInputMappingList(), mapping);
        notifyElementRemoved(mapping, workflow());
    }

    public void removeOutputMappingByName(String mappingStr) {
        ITask source = (ITask)getXml().getIdRefRaw(workflow().getLasttaskAttribute());
        ITask destination = parentTask();
        IMapping mapping = getMappingByName(source, destination, mappingStr);
        //workflow().getOutputMappingList().removeMapping(mapping);
        removeMappingFromList(workflow().getOutputMappingList(), mapping);
        notifyElementRemoved(mapping, workflow());
    }

    private void removeMappingFromList(Object list, IMapping mapping) {
        if(list instanceof IInputMappingList) {
            IInputMappingList iList = (IInputMappingList)list;
            int index = getIndexOfMapping(iList.getMappings(), mapping);
            iList.removeMappingAt(index);
        } else if(list instanceof IOutputMappingList) {
            IOutputMappingList iList = (IOutputMappingList)list;
            int index = getIndexOfMapping(iList.getMappings(), mapping);
            iList.removeMappingAt(index);
        } else throw new RuntimeException("Invalid list type " + list.getClass().getName());
    }

    private int getIndexOfMapping(IMapping[] mappings, IMapping mapping) {
        for(int i=0;i<mappings.length;i++) {
            if(mappings[i].getFirstElementAttribute().equals(mapping.getFirstElementAttribute()) && 
               mappings[i].getSecondElementAttribute().equals(mapping.getSecondElementAttribute())) 
                return i;
        }
        throw new RuntimeException("Could not find mapping " + mapping);
    }

    public IDomain createDomain(String ID) {
        IDomain domain = new Domain();
        domain.setIdAttribute(ID);
        workflow().addDomain(domain);
        //getXml().setIdRef(ID, domain);
        notifyElementAdded(domain, workflow());
        return domain;
    }

    private Vector getMappingNames(IMapping[] mappings) {
        Vector returnVec = new Vector();
        for(int i=0; i<mappings.length; i++) {
            String firstParam =  getNameFromID(mappings[i].getFirstElementAttribute());
            String secondParam =  getNameFromID(mappings[i].getSecondElementAttribute());
            returnVec.addElement(firstParam + " --> " + secondParam);
        }
        return returnVec;
    }

    private ITask parentTask() {
        INetworkDesign context = (INetworkDesign)getXml().getRoot();
        int count = context.getTaskCount();
        for(int i=0; i<count; i++) {
            ITask task = context.getTaskAt(i);
            INetworkTaskRealization nr = task.getRealization().getNetworkTaskRealization();
            if(nr != null && (nr.hashCode() == workflow().hashCode()))
                return task;
        }
        return null;
    }

    private String getNameFromID(String ID) {
        Object obj = getXml().getIdRef(ID);
        if(obj instanceof IParameter)
            return((IParameter)obj).getVariablenameAttribute();
        else if(obj instanceof IOutput)
            return((IOutput)obj).getVariablenameAttribute();
        else throw new RuntimeException(ID + " is not ID of IParameter or IOutput");
    }

    private String getInputParameterIDByName(ITask source, String name) {
        IParameter[] inputs = source.getInvocationAt(0).getParameters();
        for(int i=0; i<inputs.length; i++) {
            if(inputs[i].getVariablenameAttribute().equals(name))
                return inputs[i].getIdAttribute();
        }
        throw new RuntimeException("couldn't find source parameter");
    }

    private String getOutputIDByName(ITask source, String name) {
        IOutput[] outputs = source.getOutputs();
        for(int i=0; i<outputs.length; i++) {
            if(outputs[i].getVariablenameAttribute().equals(name))
                return outputs[i].getIdAttribute();
        }
        throw new RuntimeException("couldn't find source output");
    }

    private void split(String mappingString, StringBuffer param1, StringBuffer param2) {
        StringTokenizer stk = new StringTokenizer(mappingString);
        param1.delete(0, param1.length());
        param2.delete(0, param2.length());

        param1.insert(0, stk.nextToken());
        stk.nextToken(); // ignore the -->
        param2.insert(0, stk.nextToken());
    }

    private static final String INPUT = "INPUT";
    private static final String OUTPUT = "OutPUT";
    private IMapping createMappingByName(ITask source, ITask destination, String mappingStr, String mode) {
        if(!INPUT.equals(mode) && !OUTPUT.equals(mode)) 
            throw new RuntimeException("Invalid mode " + mode);
        LOG.println("mode is " + mode);
        StringBuffer p1 = new StringBuffer(), p2 = new StringBuffer();
        split(mappingStr, p1, p2);
        String sourceID, destinationID;

        if(mode.equals(OUTPUT)) {
            sourceID = getOutputIDByName(source, p1.toString());
            destinationID = getOutputIDByName(destination, p2.toString());
        } else {
            sourceID = getInputParameterIDByName(source, p1.toString());
            destinationID = getInputParameterIDByName(destination, p2.toString());
        }

        IMapping mapping = new Mapping();
        mapping.setFirstElementAttribute(sourceID);
        mapping.setSecondElementAttribute(destinationID);
        return mapping;
    }

    private IMapping getMappingByName(ITask source, ITask destination, String mappingStr) {
        StringBuffer p1 = new StringBuffer(), p2 = new StringBuffer();
        split(mappingStr, p1, p2);
        String sourceID = getOutputIDByName(source, p1.toString());
        String destinationID = getOutputIDByName(destination, p2.toString());

        IMapping mapping = new Mapping();
        mapping.setFirstElementAttribute(sourceID);
        mapping.setSecondElementAttribute(destinationID);
        return mapping;
    }

    INetworkTaskRealization workflow() {
        return(INetworkTaskRealization)getDtdObject();
    }
}
