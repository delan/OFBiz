package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.util.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class RealizationSupportClass extends AbstractDataSupportClass implements IRealizationSupportClass {
    public void createSimpleRealization(String realizationType) {
        ISimpleRealization is = realization().getSimpleRealization();
        if(is == null) is = new SimpleRealization();
        if(realizationType.equals(TaskSupportClass.HUMANREALIZATION)) {
            IHumanRealization hr = new HumanRealization();
            is.setHumanRealization(hr);
            notifyElementAdded(hr, is);
        } else if(realizationType.equals(TaskSupportClass.TRANSACTIONALTASKREALIZATION)) {
            ITransactionalTaskRealization tr = new TransactionalTaskRealization();
            is.setTransactionalTaskRealization(tr);
            notifyElementAdded(tr, is);
        } else if(realizationType.equals(TaskSupportClass.NONTRANSACTIONALTASKREALIZATION)) {
            INonTransactionalTaskRealization nr = new NonTransactionalTaskRealization();
            is.setNonTransactionalTaskRealization(nr);
            notifyElementAdded(nr, is);
        } else if(realizationType.equals(TaskSupportClass.COLLABORATIONREALIZATION)) {
            ICollaborationRealization cr = new CollaborationRealization();
            is.setCollaborationRealization(cr);
            notifyElementAdded(cr, is);
        } else throw new RuntimeException("Invalid realization type " + realizationType);

        realization().setSimpleRealization(is);
        notifyElementAdded(is, realization());
    }

    public void createNetworkRealization(String realizationType) {
        INetworkTaskRealization in = realization().getNetworkTaskRealization();
        if(in == null) in = new NetworkTaskRealization();
        in.setRealizationtypeAttribute(realizationType);
        realization().setNetworkTaskRealization(in);
        notifyElementAdded(in, realization());
    }

    private IRealization realization(){
        return (IRealization)getDtdObject();
    }
}
