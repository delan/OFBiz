package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.util.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class NetworkDesignSupportClass extends AbstractDataSupportClass implements INetworkDesignSupportClass {
    public ITask createTask(String taskID) {
        ITask task = new Task();
        task.setIdAttribute(taskID);
        task.setRealization(new Realization());
        ne().addTask(task);
        //getXml().setIdRef(taskID, org.ofbiz.designer.task);
        notifyElementAdded(task, ne());
        return task;
    }

    public IArc createArc(String arcID, String arcType, String sourceID, String destinationID) {
        if (!arcType.equals("Success") && !arcType.equals("Fail") && !arcType.equals("Alternative"))
            throw new RuntimeException("Invalid arcType");

        IArc arc = new Arc();
        arc.setIdAttribute(arcID);
        arc.setArctypeAttribute(arcType);
        arc.setSourceAttribute(sourceID);
        arc.setDestinationAttribute(destinationID);
        ne().addArc(arc);
        getXml().setIdRef(arcID, arc);
        notifyElementAdded(arc, ne());
        return arc;
    }

    private INetworkDesign ne(){
        return (INetworkDesign)getDtdObject();
    }
}
