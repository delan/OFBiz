package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.util.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class ArcSupportClass extends AbstractDataSupportClass implements IArcSupportClass {
        public Vector getMappingNames() {
        int count = arc().getMappingCount();
        Vector returnVec = new Vector();
        for(int i=0; i<count; i++) {
            IMapping mapping = arc().getMappingAt(i);
            String first = mapping.getFirstElementAttribute();
            String second = mapping.getSecondElementAttribute();
            IOutput firstParam = (IOutput)getXml().getIdRefRaw(first);
            IParameter secondParam = (IParameter)getXml().getIdRefRaw(second);
            returnVec.addElement(firstParam.getVariablenameAttribute() + " --> " + secondParam.getVariablenameAttribute());
        }
        return returnVec;
    }

    public void addMappingByParameterID(String sourceID, String destinationID) {
        IMapping mapping = new Mapping();
        mapping.setFirstElementAttribute(sourceID);
        mapping.setSecondElementAttribute(destinationID);
        arc().addMapping(mapping);
        notifyElementAdded(mapping, arc());
    }

    public void addMappingByName(String mappingStr) {
        Object temp = getXml().getIdRefRaw(arc().getSourceAttribute());
        ITask source = null;
        if(temp instanceof ITask)
            source = (ITask)temp;
        else
            source = (ITask) getXml().getIdRefRaw(((IArc)temp).getSourceAttribute());

        ITask destination = (ITask)getXml().getIdRefRaw(arc().getDestinationAttribute());

        StringTokenizer stk = new StringTokenizer(mappingStr);
        String param1Name = stk.nextToken();
        stk.nextToken(); // ignore the -->
        String param2Name = stk.nextToken();

        IOutput[] outputs = source.getOutputs();
        String sourceID = null, destinationID = null;
        for(int i=0; i<outputs.length; i++) {
            if(outputs[i].getVariablenameAttribute().equals(param1Name)) {
                sourceID = outputs[i].getIdAttribute();
                break;
            }
        }
        if(sourceID == null) {
            WARNING.println("couldn't find source parameter");
            return;
        }

        IParameter[] inputs = destination.getInvocationAt(0).getParameters();
        for(int i=0; i<inputs.length; i++) {
            if(inputs[i].getVariablenameAttribute().equals(param2Name)) {
                destinationID = inputs[i].getIdAttribute();
                break;
            }
        }
        if(destinationID == null) {
            WARNING.println("couldn't find destination parameter");
            return;
        }
        IMapping mapping = new Mapping();
        mapping.setFirstElementAttribute(sourceID);
        mapping.setSecondElementAttribute(destinationID);

        arc().addMapping(mapping);
        notifyElementAdded(mapping, arc());
    }

    public void removeMappingByName(String mappingStr) {
        ITask source = (ITask)getXml().getIdRefRaw(arc().getSourceAttribute());
        ITask destination = (ITask)getXml().getIdRefRaw(arc().getDestinationAttribute());

        StringTokenizer stk = new StringTokenizer(mappingStr);
        String param1Name = stk.nextToken();
        stk.nextElement();// ignore the -->
        String param2Name = stk.nextToken();

        IOutput[] outputs = source.getOutputs();
        String sourceID = null, destinationID = null;
        for(int i=0; i<outputs.length; i++) {
            if(outputs[i].getVariablenameAttribute().equals(param1Name)) {
                sourceID = outputs[i].getIdAttribute();
                break;
            }
        }
        if(sourceID == null) {
            WARNING.println("couldn't find source parameter");
            return;
        }

        IParameter[] inputs = destination.getInvocationAt(0).getParameters();
        for(int i=0; i<inputs.length; i++) {
            if(inputs[i].getVariablenameAttribute().equals(param2Name)) {
                destinationID = inputs[i].getIdAttribute();
                break;
            }
        }
        if(destinationID == null) {
            WARNING.println("couldn't find destination parameter");
            return;
        }


        IMapping[] mappings = arc().getMappings();
        for(int i=0; i<mappings.length; i++) {
            IMapping mapping = mappings[i];
            String sourceStr = mapping.getFirstElementAttribute();
            String destinationStr = mapping.getSecondElementAttribute();

            IOutput output = (IOutput)getXml().getIdRefRaw(sourceStr);
            IParameter input = (IParameter)getXml().getIdRefRaw(destinationStr);

            if(output.getIdAttribute().equals(sourceID) && input.getIdAttribute().equals(destinationID)) {
                Object newObject = arc().getMappingAt(i);
                arc().removeMappingAt(i);
                notifyElementRemoved(newObject, arc());
                return;
            }
        }
        WARNING.println("could not find mapping " + mappingStr);
    }

    public Vector getAllTaskNames() {
        String sourceID = arc().getSourceAttribute();
        String destinationID = arc().getDestinationAttribute();
        INetworkDesign context = (INetworkDesign)getXml().getRoot();
        int count = context.getTaskCount();
        Vector returnVec = new Vector();
        for(int i=0; i<count; i++) {
            String id = context.getTaskAt(i).getIdAttribute();
            if(!id.equals(sourceID) && !id.equals(destinationID)) {
                ITask task = (ITask)getXml().getIdRefRaw(id);
                String name = task.getNameAttribute();
                if(name != null) {
                    returnVec.addElement(task.getNameAttribute());
                }
            }
        }
        return returnVec;
    }

    public Vector getSourceExceptionNames() {
        ITaskWrapper source = (ITaskWrapper)getXml().getIdRef(getSourceTaskAttribute());
        return source.getExceptionNames();
    }

    public void removeAlternativeTransitionByName() {
        arc().removeAlternativetransitionAttribute();
    }

    public String getAlternativeTransitionByName() {
        String ID = arc().getAlternativetransitionAttribute();    
        if(ID == null) 
            return null;
        IArc arc = (IArc)getXml().getIdRefRaw(ID);
        ITask task = (ITask)getXml().getIdRefRaw(arc.getDestinationAttribute());
        return task.getNameAttribute();
    }

    public void setAlternativeTransitionByName(String name) {
        if(name == null) {
            arc().removeAlternativetransitionAttribute();
            return;
        }
        String sourceID = arc().getSourceAttribute();
        String destinationID = arc().getDestinationAttribute();
        INetworkDesign context = (INetworkDesign)getXml().getRoot();
        int count = context.getTaskCount();
        for(int i=0; i<count; i++) {
            String id = context.getTaskAt(i).getIdAttribute();
            if(!id.equals(sourceID) && !id.equals(destinationID)) {
                ITask task = (ITask)getXml().getIdRefRaw(id);
                if(name.equals(task.getNameAttribute())) {
                    arc().setAlternativetransitionAttribute(task.getIdAttribute());
                    return;
                }
            }
        }
    }

    public String getSourceTaskAttribute() {
        String sourceID = arc().getSourceAttribute();
        if(sourceID == null)
            return null;
        Object source = getXml().getIdRefRaw(sourceID);
        if(source instanceof ITask)
            return sourceID;
        IArc sourceArc = (IArc)source;
        return sourceArc.getSourceAttribute();
    }

    public IArc arc() {
        return(IArc)getDtdObject();
    }

}
