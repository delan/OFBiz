package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.util.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class OperatorSupportClass extends AbstractDataSupportClass implements IOperatorSupportClass {
    public IField createField(String fieldID) {
        IField field = new Field();
        field.setIdAttribute(fieldID);
        //op().addField(field);
        //getXml().setIdRef(fieldID, field);
        //notifyElementAdded(field, op());
        return field;
    }

    private IOperator op(){
        return (IOperator)getDtdObject();
    }
}
