package org.ofbiz.designer.newdesigner;

import java.awt.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.pattern.*;
import java.io.*;
import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.networkdesign.*;
import java.net.*;
import java.util.*;

public class DesignSplitter {
    public static void splitDesign(XmlWrapper xml) {
        DesignSplitter d = new DesignSplitter();
        Vector allDomains = getAllDomains(xml);
        int count = allDomains.size();
        for(int i=0;i<count;i++) {
            String domain = (String)allDomains.elementAt(i);

            splitDesign(xml, domain);
            LOG.println("done splitting " + domain);
        }
    }

    private static void splitDesign(XmlWrapper xml, String domain) {
        Object url = xml.getKey();
        String target;

        if(url instanceof File) {
            File file = (File)url;
            String fileName = file.getName();
            if(fileName.endsWith(".xml"))
                fileName = fileName.substring(0, fileName.length()-".xml".length());
            target = file.getParent() + "\\" + fileName + "_" + domain + ".xml";
        } else if(url instanceof URL) {
            URL file = (URL)url;
            String fileName = file.getFile();
            String suffix = fileName.substring(fileName.lastIndexOf("/")+1, fileName.length());
            suffix = suffix.substring(0, suffix.lastIndexOf(".xml"));
            String prefix = fileName.substring(0, fileName.lastIndexOf("/"));
            target = prefix + "/" + suffix + "_" + domain + ".xml";
            if(target.startsWith("/"))
                target = target.substring(1, target.length());
        } else throw new RuntimeException("cannot handle type " + url.getClass().getName());

        File targetFile = new File(target);
        xml.saveDocument(targetFile);

        XmlWrapper tempXml = XmlWrapper.openDocument(targetFile);
        INetworkDesignWrapper context = (INetworkDesignWrapper)tempXml.getRoot();


        // record domains and parents of all tasks
        DomainTable dTable = recordDomains(context);
        ParentTaskTable pTable = recordParents(context);

        // record removable tasks
        HashSet removeSet = getRemovableTasks(context, domain);

        // put their arcs and mappings in hashtable
        ArcAndMappingTable amTable = new ArcAndMappingTable(removeSet);

        // if top level org.ofbiz.designer.task is removable, reset security domain to make it non-removable
        {
            ITask topTask = topTask(context, pTable);
            if(removeSet.contains(topTask)) {
                String topTaskUrl = topTask.getSecuritydomainurlAttribute();
                int poundIndex = topTaskUrl.indexOf("#");
                String prefix = topTaskUrl.substring(0, poundIndex);
                topTaskUrl = prefix + "#" + domain;

                // to prevent calling support class method
                ((ITask)DataProxy.unwrapObject(topTask)).setSecuritydomainurlAttribute(topTaskUrl);
                removeSet.remove(topTask);
            }
        }

        // flatten their domains
        flattenDomains(removeSet, domain, pTable);

        //remove tasks
        Iterator it = removeSet.iterator();
        while(it.hasNext())
            context.removeTask((ITask)it.next());

        // remove inner domains for other tasks
        removeInnerDomains(context, domain);

        // remove doublenull arcs
        // loop twice to remove transition arcs
        int count = context.getArcCount();
        for(int i=count-1; i>=0; i--) {
            IArc arc = context.getArcAt(i);
            if(arc.getSourceAttribute() == null && arc.getDestinationAttribute() == null){
                LOG.println("REMOVING ARC " + context.getArcCount());
                context.removeArcAt(i);
                LOG.println("done REMOVING ARC " + context.getArcCount());
            }
        }
        LOG.println("AND NOW TRANSITION ARCS");
        count = context.getArcCount();
        for(int i=count-1; i>=0; i--) {
            IArc arc = context.getArcAt(i);
            if(arc.getSourceAttribute() == null && arc.getDestinationAttribute() == null){
                LOG.println("REMOVING ARC " + context.getArcCount());
                context.removeArcAt(i);
                LOG.println("done REMOVING ARC " + context.getArcCount());
            }
        }


        // fix single-null arcs
        count = context.getArcCount();
        LOG.println("count is " + count);
        Hashtable syncTaskCood = new Hashtable();
        for(int i=count-1; i>=0; i--) {
            IArcWrapper arc = (IArcWrapper)context.getArcAt(i);
            if(arc.getSourceAttribute() == null){
                LOG.println("arc.getDestinationAttribute() is " + arc.getDestinationAttribute());
                fixArc(context, arc, amTable, dTable, syncTaskCood, INARC);
            }
            else if(arc.getDestinationAttribute() == null)
                fixArc(context, arc, amTable, dTable, syncTaskCood, OUTARC);
        }
        tempXml.saveDocument();
    }

    private static ITaskWrapper topTask(INetworkDesignWrapper context, ParentTaskTable pTable) {
        int count = context.getTaskCount();
        for(int i=0;i<count;i++) {
            ITaskWrapper task = (ITaskWrapper)context.getTaskAt(i);
            if(pTable.getParentTask(task.getIdAttribute()) == null)
                return task;
        }
        return null;
    }

    private static Vector getAllDomains(XmlWrapper xml) {
        HashSet set = new HashSet();
        INetworkDesign context = (INetworkDesign)xml.getRoot();
        int count = context.getTaskCount();
        for(int i=0;i<count;i++) {
            ITask task = context.getTaskAt(i);
            String security = task.getSecuritydomainurlAttribute();
            int poundIndex = security.indexOf("#");
            security = security.substring(poundIndex+1, security.length());
            set.add(security);
        }

        Vector vec = new Vector();
        Iterator it = set.iterator();
        while(it.hasNext())
            vec.addElement(it.next());
        return vec;
    }

    private static DomainTable recordDomains(INetworkDesignWrapper context) {
        return new DomainTable(context);
    }

    private static ParentTaskTable recordParents(INetworkDesignWrapper context) {
        return new ParentTaskTable(context);
    }

    // find tasks that need to be removed
    public static HashSet getRemovableTasks(INetworkDesignWrapper context, String domain) {
        HashSet removeSet = new HashSet();
        int count=context.getTaskCount();
        for(int i=count-1; i>=0; i--) {
            ITask task = context.getTaskAt(i);
            String url = task.getSecuritydomainurlAttribute();
            int poundIndex = url.indexOf("#");
            url = url.substring(poundIndex+1, url.length());
            if(!url.equals(domain))
                removeSet.add(task);
        }
        return removeSet;
    }

    private static void flattenDomains(HashSet removeSet, String domain, ParentTaskTable table) {
        Iterator it = removeSet.iterator();      
        while(it.hasNext()) {
            ITask task = (ITask)it.next();
            INetworkTaskRealization nr =  task.getRealization().getNetworkTaskRealization();
            if(nr == null) continue;
            int dcount = nr.getDomainCount();
            for(int i=0; i<dcount; i++) {
                IDomain child = nr.getDomainAt(i);
                ITask parent = table.getParentTask(task.getIdAttribute());
                while(true) {
                    if(parent == null) throw new RuntimeException("Parent is null for " + task.getNameAttribute());
                    if(!removeSet.contains(parent)) break;
                    parent = table.getParentTask(parent.getIdAttribute());
                }

                String durl = child.getUrlAttribute();
                int dpoundIndex = durl.indexOf("#");
                durl = durl.substring(dpoundIndex+1, durl.length());
                if(durl.equals(domain))
                    parent.getRealization().getNetworkTaskRealization().addDomain(child);
            }
        }
    }

    private static void removeInnerDomains(INetworkDesignWrapper context, String domain) {
        int count = context.getTaskCount();
        for(int i=count-1; i>=0; i--) {
            ITask task = context.getTaskAt(i);
            INetworkTaskRealization nr =  task.getRealization().getNetworkTaskRealization();
            if(nr == null) continue;
            int domaincount = nr.getDomainCount();
            for(int j=domaincount-1; j>=0; j--) {
                IDomain tempDomain = nr.getDomainAt(j);
                String url = tempDomain.getUrlAttribute();
                int poundIndex = url.indexOf("#");
                url = url.substring(poundIndex+1, url.length());
                if(!url.equals(domain))
                    nr.removeDomainAt(j);
            }
        }
    }

    private static final String INARC = "INARC";
    private static final String OUTARC = "OUTARC";

    private static void fixArc(INetworkDesignWrapper context, IArcWrapper arc, ArcAndMappingTable amTable, DomainTable dpTable, Hashtable syncTaskCood, String mode) {
        if(!INARC.equals(mode) && !OUTARC.equals(mode)) throw new RuntimeException("Invalid mode");

        ITask thisTask = null;
        XmlWrapper xml = context.getXml();

        LOG.println("arc.getDestinationAttribute() is " + arc.getDestinationAttribute());
        LOG.println("arc.getSourceAttribute() is " + arc.getSourceAttribute());
        if(mode.equals(INARC)) 
            thisTask = (ITask)xml.getIdRef(arc.getDestinationAttribute());
        else 
            thisTask = (ITask)xml.getIdRef(arc.getSourceTaskAttribute());

        ITask oldOtherTask = amTable.getOtherTask(arc.getIdAttribute());               
        ITaskWrapper newOtherTask = (ITaskWrapper)context.createTask(xml.generateUniqueName("Sync"));

        if(mode.equals(INARC)) {
            newOtherTask.setNameAttribute("Sync_" + oldOtherTask.getNameAttribute() + "_" + thisTask.getNameAttribute());
            int tempCount = oldOtherTask.getOutputCount();
            for(int j=0; j<tempCount; j++) {
                newOtherTask.addOutput(oldOtherTask.getOutputAt(j));
            }
            newOtherTask.setTaskType(TaskSupportClass.SYNCHRONIZATIONTASKIN);
            newOtherTask.setOutarcsAttribute(arc.getIdAttribute());    
        } else {
            newOtherTask.setNameAttribute("Sync_" + thisTask.getNameAttribute() + "_" + oldOtherTask.getNameAttribute());
            int tempCount = oldOtherTask.getInvocationCount();
            for(int j=0; j<tempCount; j++) {
                newOtherTask.addInvocation(oldOtherTask.getInvocationAt(j));
            }
            newOtherTask.setTaskType(TaskSupportClass.SYNCHRONIZATIONTASKOUT);
            newOtherTask.setInarcsAttribute(arc.getIdAttribute());    
        }

        IDomainWrapper thisDomain = dpTable.getDomain(thisTask.getIdAttribute());
        String existing = thisDomain.getTasksAttribute();
        String newAttr = IDRefHelper.addIDRef(existing, newOtherTask.getIdAttribute());
        thisDomain.setTasksAttribute(newAttr);

        context.addTask(newOtherTask);

        ((ITask)newOtherTask).setSecuritydomainurlAttribute(thisDomain.getUrlAttribute());
        Point cood = (Point)syncTaskCood.get(thisDomain.getIdAttribute());
        if(cood == null) {
            int x = Integer.parseInt(thisDomain.getWidthAttribute()) - 75;
            int y = 10;                                                     
            cood = new Point(x, y);
            syncTaskCood.put(thisDomain.getIdAttribute(), cood);
        }
        newOtherTask.setXAttribute("" + cood.x);
        newOtherTask.setYAttribute("" + cood.y);
        newOtherTask.setDescription("Synchronization IN Task");
        cood.y  += 50;

        int mappingCount = arc.getMappingCount();
        if(mode.equals(INARC)) {
            arc.setSourceAttribute(newOtherTask.getIdAttribute());
            for(int i=0; i<mappingCount; i++) {
                IMapping mapping = arc.getMappingAt(i);
                mapping.setFirstElementAttribute(amTable.getOtherElement(mapping));
            }
        } else {
            arc.setDestinationAttribute(newOtherTask.getIdAttribute());
            for(int i=0; i<mappingCount; i++) {
                IMapping mapping = arc.getMappingAt(i);
                mapping.setSecondElementAttribute(amTable.getOtherElement(mapping));
            }
        }
    }
}




