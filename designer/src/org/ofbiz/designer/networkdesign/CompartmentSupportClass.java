package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.generic.*;
//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class CompartmentSupportClass extends AbstractDataSupportClass implements ICompartmentSupportClass {
    public ICompartment createCompartment(String ID) {
        ICompartment newCompartment = new Compartment();
        newCompartment.setIdAttribute(ID);
        compartment().addCompartment(newCompartment);
        //getXml().setIdRef(ID, newCompartment);
        notifyElementAdded(newCompartment, compartment());
        return newCompartment;
    }

    public void setUrlAttribute(String modelURL){
        String[] tasks = IDRefHelper.getReferenceArray(compartment().getTasksAttribute());
        for(int i=0;i<tasks.length;i++) {
            ITask task = (ITask)getXml().getIdRef(tasks[i]);
            task.setSecuritydomainurlAttribute(modelURL);
            //notifyDataChanged(org.ofbiz.designer.task);
        }

        int count = compartment().getCompartmentCount();
        for(int i=0;i<count;i++) {
            ICompartment compartment = compartment().getCompartmentAt(i);
            ICompartmentWrapper compartmentWrapper = (ICompartmentWrapper)getXml().getIdRef(compartment.getIdAttribute()); // get the proxy
            compartmentWrapper.setUrlAttribute(modelURL);
        }
    }
    
    private ICompartment compartment() {
        return(ICompartment)getDtdObject();
    }
}
