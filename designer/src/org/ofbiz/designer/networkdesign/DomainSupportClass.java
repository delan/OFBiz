package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.util.*;
//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class DomainSupportClass extends AbstractDataSupportClass implements IDomainSupportClass {
    public ICompartment createCompartment(String ID) {
        IDomain domain = (IDomain)getDtdObject();
        ICompartment compartment = new Compartment();
        compartment.setIdAttribute(ID);
        compartment.setNameAttribute(ID);
        domain.addCompartment(compartment);
        //getXml().setIdRef(ID, compartment);
        notifyElementAdded(compartment, domain);
        return compartment;
    }

    public Vector getAllTaskIDs() {
        Vector returnVec = new Vector();
        Vector all = new Vector();
        all.addElement(domain());
        while (all.size()>0) {
            Object element = all.elementAt(0);
            if (element instanceof String)
                returnVec.addElement(element);
            else if (element instanceof ICompartment) {
                ICompartment comp = (ICompartment)element;
                for (int k=0; k<comp.getCompartmentCount(); k++)
                    all.addElement(comp.getCompartmentAt(k));
                String[] compTaskIDs = IDRefHelper.getReferenceArray(comp.getTasksAttribute());
                for (int k=0; k<compTaskIDs.length; k++)
                    all.addElement(compTaskIDs[k]);
            } else if (element instanceof IDomain) {
                IDomain comp = (IDomain)element;
                for (int k=0; k<comp.getCompartmentCount(); k++)
                    all.addElement(comp.getCompartmentAt(k));
                String[] compTaskIDs = IDRefHelper.getReferenceArray(comp.getTasksAttribute());
                for (int k=0; k<compTaskIDs.length; k++)
                    all.addElement(compTaskIDs[k]);
            }
            all.removeElementAt(0);
        }

        return returnVec;
    }

    public void setUrlAttribute(String modelURL){
        domain().setUrlAttribute(modelURL);
        String[] tasks = IDRefHelper.getReferenceArray(domain().getTasksAttribute());
        for(int i=0;i<tasks.length;i++) {
            ITask task = (ITask)getXml().getIdRef(tasks[i]);
            task.setSecuritydomainurlAttribute(modelURL);
            //notifyDataChanged(org.ofbiz.designer.task);
        }

        int count = domain().getCompartmentCount();
        for(int i=0;i<count;i++) {
            ICompartment compartment = domain().getCompartmentAt(i);
            ICompartmentWrapper compartmentWrapper = (ICompartmentWrapper)getXml().getIdRef(compartment.getIdAttribute()); // get the proxy
            compartmentWrapper.setUrlAttribute(modelURL);
        }
        notifyDataModified(domain());
    }
    
    private IDomain domain() {
        return(IDomain)getDtdObject();
    }
}
