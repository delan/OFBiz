package org.ofbiz.designer.newdesigner.model;

import java.util.*;
import java.awt.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.newdesigner.popup.*;
import javax.swing.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.newdesigner.ContainerView;

public class NetworkEditorComponentModel extends ContainerModel implements INetworkEditorComponentModel {
    private static HashSet allowedTypes = new HashSet();
    private static HashSet allowedTaskTypes = new HashSet();
    private static HashSet simpleTaskTypes = new HashSet();
    private static HashSet networkTaskTypes = new HashSet();
    private static HashSet specialTaskTypes = new HashSet();
    static{
        allowedTypes.add(TASKTYPE);
        allowedTypes.add(DOMAINTYPE);
        allowedTypes.add(COMPARTMENTTYPE);
        allowedTypes.add(WORKFLOWTYPE);

        simpleTaskTypes.add(HUMANREAL);
        simpleTaskTypes.add(NONTRANSACTIONALREAL);
        simpleTaskTypes.add(TRANSACTIONALREAL);
        simpleTaskTypes.add(COLLABORATIONREAL);

        networkTaskTypes.add(NONTRANSACTIONALNETWORK);
        networkTaskTypes.add(TRANSACTIONALNETWORK);
        networkTaskTypes.add(COMPOSITENETWORK);
        networkTaskTypes.add(OPEN2PCNETWORK);

        specialTaskTypes.add(STARTTASK);
        specialTaskTypes.add(SYNCHRONIZATIONTASKIN);
        specialTaskTypes.add(SYNCHRONIZATIONTASKOUT);
        specialTaskTypes.add(RESULTBADTASK);
        specialTaskTypes.add(RESULTGOODTASK);

        allowedTaskTypes.addAll(simpleTaskTypes);
        allowedTaskTypes.addAll(networkTaskTypes);
        allowedTaskTypes.addAll(specialTaskTypes);


    }

    public static INetworkEditorComponentModel createModelProxy(String ID) {
        final NetworkEditorComponentModel model = new NetworkEditorComponentModel();
        model.setID(ID);
        return(INetworkEditorComponentModelWrapper)GuiModelProxy.newProxyInstance(model, "org.ofbiz.designer.newdesigner.model.INetworkEditorComponentModelWrapper");
    }

    public String getTaskReference() {
        return(String)getDataElement("taskReference");
    }

    public void setTaskReference(String taskReference) {
        setDataElement("taskReference", taskReference);
    }

    public String getDisplayName() {
        return(String)getDataElement("displayName");
    }

    public void setDisplayName(String displayName) {
        setDataElement("displayName", displayName);
    }

    public String getStartTask() {
        return(String)getDataElement("startTask");
    }

    public void setStartTask(String startTask) {
        setDataElement("startTask", startTask);
    }

    public String getEndTask() {
        return(String)getDataElement("endTask");
    }

    public void setEndTask(String endTask) {
        setDataElement("endTask", endTask);
    }

    public String getDomainURL() {
        return(String)getDataElement("domainURL");
    }

    public void setDomainURL(String url) {
        if(url == null) 
            return;
        setDataElement("domainURL", url);
        if (DOMAINTYPE.equals(getModelType())){
            int index = url.indexOf("#");
            url = url.substring(index+1, url.length());
            setDisplayName(url);
        }
    }

    public String getTaskType() {
        return(String)getDataElement("taskType");
    }

    public void setTaskType(String taskTypeIn) {
        String modelType = getModelType();
        if (modelType != null && !modelType.equals(TASKTYPE))
            throw new RuntimeException(modelType + " is not a Task type");
        if (!allowedTaskTypes.contains(taskTypeIn)){
            LOG.println("allowedTaskTypes is " + allowedTaskTypes);
            throw new RuntimeException("BAD TASK TYPE " + taskTypeIn);
        }
        setDataElement("taskType", taskTypeIn);
    }

    public Color getColor() {
        Color color = (Color)getDataElement("color");
        if (color != null) return color;
        else if (getParentContainer() != null && getParentContainer() instanceof NetworkEditorComponentModel) return((NetworkEditorComponentModel)getParentContainer()).getColor();
        else return null;
    }

    public String getHref() {
        String taskType = getTaskType();
        if (!taskType.equals(NONTRANSACTIONALNETWORK))
            return null;
        else
            return(String)getDataElement("href");
    }

    public void setHref(String href) {
        String taskType = getTaskType();
        if (!taskType.equals(NONTRANSACTIONALNETWORK)) {
            WARNING.println("WRONG TYPE");
            return;
        } else
            setDataElement("href", href);
    }

    public void setColor(Color colorIn) {
        String modelType = getModelType();
        if (modelType != null && !modelType.equals(DOMAINTYPE)) throw new RuntimeException("Not a Domain type");
        else setDataElement("color", colorIn);
    }

    public String getModelType() {
        return(String)getDataElement("modelType");
    }

    public void setModelType(String modelTypeIn) {
        if (!allowedTypes.contains(modelTypeIn))
            throw new RuntimeException("INCORRECT MODEL TYPE");
        setDataElement("modelType", modelTypeIn);
        if (modelTypeIn.equals(TASKTYPE))
            popup = new TaskPopup();
        else if (modelTypeIn.equals(DOMAINTYPE))
            popup = new DomainPopup();
        else if (modelTypeIn.equals(COMPARTMENTTYPE))
            popup = new CompartmentPopup();
        else if (modelTypeIn.equals(WORKFLOWTYPE))
            popup = new WorkFlowPopup();
        else
            throw new RuntimeException("Unknown modelType");
    }

	private static final String gifDir = System.getProperty("GIFDIR");
    public ImageIcon getIcon() {
        String taskType = (String)getDataElement("taskType");

        if (taskType == null) return null;
        else if (taskType.equals(HUMANREAL)) return new ImageIcon(gifDir + "/humanReal.gif");
        else if (taskType.equals(NONTRANSACTIONALREAL)) return new ImageIcon(gifDir + "/nonTransactionalReal.gif");
        else if (taskType.equals(TRANSACTIONALREAL)) return new ImageIcon(gifDir + "/transactionalReal.gif");
        else if (taskType.equals(COLLABORATIONREAL)) return new ImageIcon(gifDir + "/collaborationReal.gif");

        else if (taskType.equals(NONTRANSACTIONALNETWORK)) return new ImageIcon(gifDir + "/nonTransactionalWorkFlowReal.gif");
        else if (taskType.equals(TRANSACTIONALNETWORK)) return new ImageIcon(gifDir + "/transactionalWorkFlowReal.gif");
        else if (taskType.equals(COMPOSITENETWORK)) return new ImageIcon(gifDir + "/compositeWorkFlowReal.gif");
        else if (taskType.equals(OPEN2PCNETWORK)) return new ImageIcon(gifDir + "/open2PCWorkFlowReal.gif");

        else if (taskType.equals(RESULTBADTASK)) return new ImageIcon(gifDir + "/resultBadTask.gif");
        else if (taskType.equals(RESULTGOODTASK)) return new ImageIcon(gifDir + "/resultGoodTask.gif");
        else if (taskType.equals(STARTTASK)) return new ImageIcon(gifDir + "/startTask.gif");
        else if (taskType.equals(SYNCHRONIZATIONTASKIN)) return new ImageIcon(gifDir + "/in.gif");
        else if (taskType.equals(SYNCHRONIZATIONTASKOUT)) return new ImageIcon(gifDir + "/out.gif");
        else throw new RuntimeException("Unknown taskType " + taskType);
    }

    public IArcModel createChildArc(String ID) {
        IArcModel child = ArcModel.createModelProxy(ID);
        addChildArc(child);
        return child;
    }

    public IContainerModel createChildContainer(String name, String modelType) {
        INetworkEditorComponentModel child = (INetworkEditorComponentModel)createModelProxy(name);
        child.setDisplayName(name);
        child.setModelType(modelType);
        if (modelType.equals(TASKTYPE))
            child.setTaskType(HUMANREAL);

        // HACK
        if (modelType.equals(DOMAINTYPE) && getChildContainerCount() > 0)
            child.setColor(((INetworkEditorComponentModel)getChildContainerAt(0)).getColor());

        child.setSize(INetworkEditorComponentModel.taskDimension);
        child.setDomainURL(getDomainURL());
        addChildContainer(child);
        return child;
    }

    public INetworkEditorComponentModel getChildContainerByID(String ID) {
        int count = getChildContainerCount();
        for (int i=0; i<count; i++) {
            INetworkEditorComponentModel child = (INetworkEditorComponentModel)getChildContainerAt(i);
            if (child.getID().equals(ID)) return child;
        }
        return null;
    }

    public INetworkEditorComponentModel getChildContainerByIDRecursive(String ID) {
        INetworkEditorComponentModel child = getChildContainerByID(ID);
        if (child != null) return child;
        int count = getChildContainerCount();
        for (int i=0; i<count; i++) {
            INetworkEditorComponentModel subChild = ((INetworkEditorComponentModel)getChildContainerAt(i)).getChildContainerByIDRecursive(ID);
            if (subChild != null)
                return subChild;
        }
        return null;
    }

    public IArcModel getChildArcByID(String ID) {
        int count = getChildArcCount();
        for (int i=0; i<count; i++) {
            IArcModel child = (IArcModel)getChildArcAt(i);
            if (child.getID().equals(ID)) return child;
        }
        return null;
    }

    public IArcModel getChildArcByIDRecursive(String ID) {
        IArcModel child = getChildArcByID(ID);
        if (child != null) return child;
        int count = getChildContainerCount();
        for (int i=0; i<count; i++) {
            IArcModel subChild = ((INetworkEditorComponentModel)getChildContainerAt(i)).getChildArcByIDRecursive(ID);
            if (subChild != null)
                return subChild;
        }
        return null;
    }

    public IArcModel createIncomingArc(String sourceID, String arcID, String arcType) {
        if(!getModelType().equals(TASKTYPE)) 
            throw new RuntimeException("Invalid model type");
        if (ArcLoader.existsPendingArc(sourceID, getID())) {
            IArcModelWrapper arc = (IArcModelWrapper)ArcLoader.getPendingArc(sourceID, getID());
            ArcLoader.removePendingArc(sourceID, getID());
            arc.setDestination(this);
            return arc;
        }
        IArcModelWrapper arc = (IArcModelWrapper )ArcModel.createModelProxy(arcID);
        arc.setLineStyle(arcType);
        IRelationshipNode sourceModel = null;
        sourceModel = ((INetworkEditorComponentModel)getTopLevelContainer()).getChildContainerByIDRecursive(sourceID);
        if(sourceModel == null) 
            sourceModel = ((INetworkEditorComponentModel)getTopLevelContainer()).getChildArcByIDRecursive(sourceID);
        
        ArcLoader.addPendingArc(sourceID, getID(), arc);
        if (sourceModel != null) {
            arc.setSource(sourceModel);
            ArcLoader.removePendingArc(sourceID, getID());
        }
        arc.setDestination(this);
        return arc;
    }

    public IArcModel createOutgoingArc(String destinationID, String arcID, String arcType) {
        if(!getModelType().equals(TASKTYPE)) 
            throw new RuntimeException("Invalid model type");
        if (ArcLoader.existsPendingArc(getID(), destinationID)) {
            IArcModelWrapper arc = (IArcModelWrapper)ArcLoader.getPendingArc(getID(), destinationID);
            ArcLoader.removePendingArc(getID(), destinationID);
            arc.setSource(this);
            return arc;
        }
        IArcModelWrapper arc = (IArcModelWrapper )ArcModel.createModelProxy(arcID);
        arc.setLineStyle(arcType);
        INetworkEditorComponentModel destinationModel = ((INetworkEditorComponentModel)getTopLevelContainer()).getChildContainerByIDRecursive(destinationID);
        ArcLoader.addPendingArc(getID(), destinationID, arc);       
        if (destinationModel != null) {
            arc.setDestination(destinationModel);
            ArcLoader.removePendingArc(getID(), destinationID);
        }
        arc.setSource(this);
        return arc;
    }

    private WFPopup popup;
    public WFPopup getPopup() {
        return popup;
    }

    protected static HashSet modifyMethods = new HashSet();
    static {
        modifyMethods.addAll(ContainerModel.modifyMethods);
        modifyMethods.add("setDomainURL");
        modifyMethods.add("setDisplayName");
        modifyMethods.add("setTaskType");
        modifyMethods.add("setStartTask");
        modifyMethods.add("setEndTask");
    }

    public HashSet getModifyMethods() {
        return modifyMethods;
    }

    protected static Object[][] dataElements = { 
        {"taskReference", "java.lang.String"},
        {"modelType", "java.lang.String"},
        {"taskType", "java.lang.String"},
        {"startTask", "java.lang.String"},// for network tasks
        {"endTask", "java.lang.String"}, // for network tasks
        {"displayName", "java.lang.String"},
        {"domainURL", "java.lang.String"}, // for domains
        {"color", "java.awt.Color"},
        {"href", "java.lang.String"},
    };

    static {
        Object[][] dataElementsTemp = dataElements;
        int baseSize = ContainerModel.dataElements.length;
        dataElements = new Object[baseSize+dataElementsTemp.length][2];
        for (int i=0; i<baseSize; i++) dataElements[i] = ContainerModel.dataElements[i];
        for (int i=0; i<dataElementsTemp.length; i++) dataElements[i+baseSize] = dataElementsTemp[i];
    }

    public Object[][] getRelationships() {
        return relationships;
    }

    public Object[][] getDataElements() {
        return dataElements;
    }

    private HashSet deletedChildren = new HashSet();
    public HashSet getDeletedChildren() {
        return deletedChildren;
    }

    public void neighborDying(IRelationshipNode source) {
        if (source instanceof INetworkEditorComponentModel) {
            INetworkEditorComponentModel deletedChild = (INetworkEditorComponentModel)source;
            if (containsChildContainer(deletedChild)) {
                deletedChildren.add((deletedChild).getID());
                ContainerView.modelInstances.remove("" + getTopLevelContainer().hashCode() + "_" + deletedChild.getID());
            }
        }
    }

    public boolean isSimpleTask() {
        return simpleTaskTypes.contains(getTaskType());
    }

    public boolean isSpecialTask() {
        return specialTaskTypes.contains(getTaskType());
    }

    public boolean isPrimaryDomain(){
        if(!getModelType().equals(DOMAINTYPE)) 
            return false;
        return getParentContainer().getChildContainerAt(0).getID().equals(getID());
    }
}
