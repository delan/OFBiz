/*
 * Created by IntelliJ IDEA.
 * User: Oliver Wieland
 * Date: Jul 27, 2001
 * Time: 4:19:24 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.networkdesign.INetworkDesignWrapper;
import org.ofbiz.designer.networkdesign.ITaskWrapper;
import org.ofbiz.designer.networkdesign.INetworkTaskRealization;
import org.ofbiz.designer.networkdesign.IDomainWrapper;

import java.util.Hashtable;
import java.util.Vector;

class DomainTable {
    private Hashtable domainTable = new Hashtable();

    public DomainTable(INetworkDesignWrapper context) {
        int count = context.getTaskCount();
        for(int i=count-1; i>=0; i--)
            init((ITaskWrapper)context.getTaskAt(i));
    }

    private void init(ITaskWrapper task) {
        INetworkTaskRealization nr =  task.getRealization().getNetworkTaskRealization();
        if(nr == null) return;
        int count = nr.getDomainCount();
        for(int i=0; i<count; i++)
            init((IDomainWrapper)nr.getDomainAt(i), task);
    }

    private void init(IDomainWrapper domain, ITaskWrapper task) {
        Vector allTaskIDs = domain.getAllTaskIDs();
        for(int i=0; i<allTaskIDs.size(); i++)
            domainTable.put(allTaskIDs.elementAt(i), domain);
    }

    public IDomainWrapper getDomain(String taskID) {
        return(IDomainWrapper)domainTable.get(taskID);
    }
}
