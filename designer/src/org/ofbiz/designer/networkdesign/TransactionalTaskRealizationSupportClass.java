package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import java.util.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class TransactionalTaskRealizationSupportClass extends AbstractDataSupportClass implements ITransactionalTaskRealizationSupportClass {
    public Vector getInputNames(){
        Vector returnVec = new Vector();
        int count = data().getTransactionalInputCount();
        for(int i=0;i<count;i++) {
            String element = data().getTransactionalInputAt(i);
            returnVec.addElement(element);
        }
        return returnVec;
    }
    public Vector getOutputNames(){
        Vector returnVec = new Vector();
        int count = data().getTransactionalOutputCount();
        for(int i=0;i<count;i++) {
            String element = data().getTransactionalOutputAt(i);
            returnVec.addElement(element);
        }
        return returnVec;
    }

    private ITransactionalTaskRealization data(){
        return (ITransactionalTaskRealization)getDtdObject();
    }
}
