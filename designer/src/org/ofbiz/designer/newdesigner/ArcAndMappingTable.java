/*
 * Created by IntelliJ IDEA.
 * User: Oliver Wieland
 * Date: Jul 27, 2001
 * Time: 4:20:18 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.networkdesign.ITaskWrapper;
import org.ofbiz.designer.networkdesign.IMapping;
import org.ofbiz.designer.networkdesign.IArc;
import org.ofbiz.designer.generic.IDRefHelper;

import java.util.Hashtable;
import java.util.HashSet;
import java.util.Iterator;

class ArcAndMappingTable {
    private Hashtable arcTable = new Hashtable();
    private Hashtable mappingTable = new Hashtable();

    ArcAndMappingTable(HashSet taskSet) {
        Iterator it = taskSet.iterator();
        while(it.hasNext())
            init((ITaskWrapper)it.next());
    }

    public ITaskWrapper getOtherTask(String arcID) {
        return(ITaskWrapper)arcTable.get(arcID);
    }

    public String getOtherElement(IMapping mapping) {
        return(String)mappingTable.get(mapping);
    }

    private void init(ITaskWrapper task) {
        String[] arcs = IDRefHelper.getReferenceArray(task.getInarcsAttribute());
        for(int j=0; j<arcs.length; j++)
            initInArc(task, arcs[j]);
        arcs = IDRefHelper.getReferenceArray(task.getOutarcsAttribute());
        for(int j=0; j<arcs.length; j++)
            initOutArc(task, arcs[j]);
    }

    private void initOutArc(ITaskWrapper task, String arcID) {
        arcTable.put(arcID, task);
        IArc arc = (IArc)task.getXml().getIdRef(arcID);
        int mappingCount = arc.getMappingCount();
        for(int k=0; k<mappingCount; k++) {
            IMapping mapping = arc.getMappingAt(k);
            mappingTable.put(mapping, mapping.getFirstElementAttribute());
        }
    }

    private void initInArc(ITaskWrapper task, String arcID) {
        arcTable.put(arcID, task);
        IArc arc = (IArc)task.getXml().getIdRef(arcID);
        int mappingCount = arc.getMappingCount();
        for(int k=0; k<mappingCount; k++) {
            IMapping mapping = arc.getMappingAt(k);
            mappingTable.put(mapping, mapping.getSecondElementAttribute());
        }
    }
}


