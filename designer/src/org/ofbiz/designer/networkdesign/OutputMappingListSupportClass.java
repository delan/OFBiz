package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class OutputMappingListSupportClass extends AbstractDataSupportClass implements IOutputMappingListSupportClass {
    public void addMappingByParameterID(String sourceID, String destinationID) {
        IMapping mapping = new Mapping();
        mapping.setFirstElementAttribute(sourceID);
        mapping.setSecondElementAttribute(destinationID);
        list().addMapping(mapping);
        notifyElementAdded(mapping, list());
    }

    private OutputMappingList list(){
        return (OutputMappingList)getDtdObject();
    }
}
