package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.util.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class FieldSupportClass extends AbstractDataSupportClass implements IFieldSupportClass {
    public IOperator createOperator(String opID) {
        IOperator op = new Operator();
        op.setIdAttribute(opID);
        //field().setOperator(op);
        //getXml().setIdRef(opID, op);
        //notifyElementAdded(op, field());
        return op;
    }

    public void setTaskAttributeByName(String taskName){
        INetworkDesign context = (INetworkDesign)getXml().getRoot();
        int count = context.getTaskCount();
        for(int i=0; i<count; i++) {
            ITask task = context.getTaskAt(i);
            if (taskName.equals(task.getNameAttribute())){
                field().setTaskAttribute(task.getIdAttribute());
                notifyDataModified(field());
                return;
            }
        }
    }

    private IField field(){
        return (IField)getDtdObject();
    }
}
