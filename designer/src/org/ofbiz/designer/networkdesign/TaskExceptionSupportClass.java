package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.util.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class TaskExceptionSupportClass extends AbstractDataSupportClass implements ITaskExceptionSupportClass {
    public ILocalHandler createLocalHandler(){
        if(exception().getLocalHandler() != null) 
            throw new RuntimeException("LocalHandler already exists!");

        ILocalHandler lh = new LocalHandler();
        exception().setLocalHandler(lh);
        notifyElementAdded(lh, exception());
        return lh;
    }

    private ITaskException exception(){
        return (ITaskException)getDtdObject();
    }
}
