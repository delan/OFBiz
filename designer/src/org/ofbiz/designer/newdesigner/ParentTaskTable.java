/*
 * Created by IntelliJ IDEA.
 * User: Oliver Wieland
 * Date: Jul 27, 2001
 * Time: 4:18:03 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.networkdesign.*;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;

class ParentTaskTable {
    private Hashtable parentTable = new Hashtable();

    public ParentTaskTable(INetworkDesignWrapper context) {
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
            parentTable.put(allTaskIDs.elementAt(i), task);
    }

    public ITaskWrapper getParentTask(String taskID) {
        return(ITaskWrapper)parentTable.get(taskID);
    }

    public Vector getChildren(ITaskWrapper parent){
        Enumeration keys = parentTable.keys();
        Vector returnVec = new Vector();
        while(keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if(((ITask)parentTable.get(key)).getIdAttribute() == parent.getIdAttribute())
                returnVec.addElement(key);
        }
        return returnVec;
    }
}