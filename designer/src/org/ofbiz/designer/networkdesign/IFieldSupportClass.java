package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;

public interface IFieldSupportClass extends IDataSupportClass {
    IOperator createOperator(String opID);
    public void setTaskAttributeByName(String taskName);
}
