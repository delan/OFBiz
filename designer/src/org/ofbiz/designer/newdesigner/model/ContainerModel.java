package org.ofbiz.designer.newdesigner.model;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.ofbiz.designer.util.*;
import java.io.*;
import org.ofbiz.designer.newdesigner.*;
import java.lang.reflect.*;
import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.pattern.*;

public class ContainerModel extends BaseModel implements Serializable, IContainerModel {
    protected ContainerModel() {
    }

    public static IContainerModel createModelProxy() {
        ContainerModel model = new ContainerModel();
        return(IContainerModel)GuiModelProxy.newProxyInstance(model, "org.ofbiz.designer.newdesigner.model.IContainerModelWrapper");
    }

    private void validate() {
        for(int i=0; i<getRelationshipCount("childContainers"); i++)
            for(int j=i+1; j<getRelationshipCount("childContainers"); j++)
                if(((ContainerModel)getRelationshipAt("childContainers", i)).getID().equals(((ContainerModel)getRelationshipAt("childContainers", j)).getID()))
                    throw new RuntimeException("Validation failure, duplicate child names :" + 
                                               ((ContainerModel)getRelationshipAt("childContainers", i)).getID() + " in " + 
                                               getID() + " at indices " + i + ":" + j);
    }

    //public String getName(){ return (String)getDataElement("name");}
    public String getID() {
        return(String)getDataElement("ID");
    }
    public Rectangle getBounds() {
        return(Rectangle)getDataElement("bounds");
    }
    public Point getLocation() {
        try {
            return getBounds().getLocation();
        } catch(NullPointerException e) {
            return null;
        }
    }

    public void setID(String ID) {
        setDataElement("ID", ID);
    }

    public void setBounds(Rectangle boundsIn) {
        setDataElement("bounds", boundsIn);
    }

    public void setSize(Dimension dim) {
        Rectangle rect = null;
        try {
            rect = (Rectangle)getBounds().clone();
        } catch(NullPointerException e) {
            rect = new Rectangle();
        }
        rect.setSize(dim);
        setBounds(rect);
    }

    public void setLocation(Point location) {
        Rectangle rect = null;
        try {
            rect = (Rectangle)getBounds().clone();
        } catch(NullPointerException e) {
            rect = new Rectangle();
        }
        rect.setLocation(location);
        setBounds(rect);
    }

    public Rectangle getAbsoluteBounds(IContainerModel reference) {
        Rectangle returnValue = (Rectangle)getBounds().clone();

        if(this == reference) return returnValue;
        if(reference == null) return returnValue;
        IContainerModel temp = getParentContainer();
        while(temp != reference) {
            if(temp == null) return null;
            returnValue.x += temp.getBounds().x; 
            returnValue.y += temp.getBounds().y;
            temp = temp.getParentContainer();
        }
        return returnValue;
    }

    public boolean isAncestorOf(IContainerModel model) {
        IContainerModel temp = model;
        while(temp != null) {
            if(temp.equals(this))
                return true;
            else {
                if(temp.getID().equals(getID())) {
                    WARNING.println("MISMATCH FOR " + getID());
                    WARNING.println("my hashcode is " + hashCode() + " temp.hashCode is " + temp.hashCode());
                }
                temp = temp.getParentContainer();
            }
        }
        return false;
    }

    public String toString() {
        String returnString = "[" + getID();        
        if(getRelationshipCount("parentContainer") != 0) returnString += " parent:" + ((IContainerModel)getRelationshipAt("parentContainer", 0)).getID();
        returnString += " childContainers:" + getRelationshipCount("childContainers");
        returnString += " childArcs:" + getRelationshipCount("childArcs");
        returnString += " incomingArcs:" + getRelationshipCount("incomingArcs");
        returnString += " outgoingArcs:" + getRelationshipCount("outgoingArcs");
        returnString += "]";
        return returnString;
    }

    public void fireSynchronizeNode() {
        Vector neighbors = getNeighbors(this);
        for(int i=0; i<neighbors.size(); i++)
            ((IBaseModel)neighbors.elementAt(i)).neighborChanged(this);
    }

    public void neighborChanged(IRelationshipNode changeSource) {
        fireSynchronizeNode();      
    }

    public Vector getNeighbors(IContainerModel originator) {
        Vector returnVec = new Vector();
        for(int i=0; i<getIncomingArcCount(); i++)
            returnVec.addElement(getIncomingArcAt(i));

        for(int i=0; i<getOutgoingArcCount(); i++)
            returnVec.addElement(getOutgoingArcAt(i));

        for(int i=0; i<getChildContainerCount(); i++) {
            Vector temp = ((IContainerModel)getChildContainerAt(i)).getNeighbors(originator);
            for(int j=0; j<temp.size(); j++) {
                IArcModel arc = (IArcModel)temp.elementAt(j);
                if(arc.getParent() != originator && !originator.isAncestorOf(arc.getParent()))
                    returnVec.addElement(arc);
            }
        }
        return returnVec;
    }

    public WFPopup getPopup() {
        return null;
    }

    public IContainerModel getTopLevelContainer() {
        IContainerModel returnValue = this;
        while(returnValue.getParentContainer() != null)
            returnValue = returnValue.getParentContainer();

        return returnValue;
    }

    public synchronized void setParentContainer(IContainerModel parentContainerIn) {
        removeParentContainer(getParentContainer());
        addParentContainer(parentContainerIn);
    }



    // abstract method implementation from BaseModel
    // format [[name, complement-name, relationship-order]..];
    protected static final Object[][] relationships  = { 
        {"parentContainer", "childContainers", SINGLE},
        {"childContainers", "parentContainer", MULTIPLE},
        {"childArcs", "parent", MULTIPLE},
        {"incomingArcs", "destination", MULTIPLE},
        {"outgoingArcs", "source", MULTIPLE},
    };

    protected static final Object[][] dataElements = { 
        //{"name", "java.lang.String"},
        {"bounds", "java.awt.Rectangle"},
        {"ID", "java.lang.String"},
    };

    public Object[][] getRelationships() {
        return relationships;
    }

    public Object[][] getDataElements() {
        return dataElements;
    }

    // RELATIONSHIP METHODS
    public IContainerModel getParentContainer() {
        return(IContainerModel)getRelationshipAt("parentContainer", 0);
    }
    public int getChildContainerCount() {
        return getRelationshipCount("childContainers");
    }
    public IContainerModel getChildContainerAt(int i) {
        return(IContainerModel)getRelationshipAt("childContainers", i);
    }
    public int getChildArcCount() {
        return getRelationshipCount("childArcs");
    }
    public IArcModel getChildArcAt(int i) {
        return(IArcModel)getRelationshipAt("childArcs", i);
    }
    public int getIncomingArcCount() {
        return getRelationshipCount("incomingArcs");
    }
    public IArcModel getIncomingArcAt(int i) {
        return(IArcModel)getRelationshipAt("incomingArcs", i);
    }
    public int getOutgoingArcCount() {
        return getRelationshipCount("outgoingArcs");
    }
    public IArcModel getOutgoingArcAt(int i) {
        return(IArcModel)getRelationshipAt("outgoingArcs", i);
    }


    public synchronized void removeParentContainer(IContainerModel parentContainerIn) {
        removeRelationship("parentContainer", parentContainerIn);
    }

    public synchronized void addParentContainer(IContainerModel parentContainerIn) {
        addRelationship("parentContainer", parentContainerIn);
    }

    public void removeChildContainer(IContainerModel child) {
        removeRelationship("childContainers", child);
    }

    public synchronized void addChildContainer(IContainerModel child) {
        addRelationship("childContainers", child);
    }

    public IContainerModel createChildContainer() {
        IContainerModel child = ContainerModel.createModelProxy();
        addChildContainer(child);
        return child;
    }

    public void removeChildArc(IArcModel child) {
        removeRelationship("childArcs", child);
    }

    public void addChildArc(IArcModel child) {
        addRelationship("childArcs", child);
    }

    public void removeIncomingArc(IArcModel child) {
        LOG.println("NN");
        removeRelationship("incomingArcs", child);
    }

    public void addIncomingArc(IArcModel child) {
        addRelationship("incomingArcs", child);
    }

    public IContainerModel getChildContainerByNameRecursive(String name) {
        IContainerModel child = getChildContainerByName(name);
        if(child != null) return child;
        int count = getChildContainerCount();
        for(int i=0; i<count; i++) {
            IContainerModel subChild = getChildContainerAt(i).getChildContainerByNameRecursive(name);
            if(subChild != null) return subChild;
        }
        return null;
    }

    public IContainerModel getChildContainerByName(String name) {
        int count = getChildContainerCount();
        for(int i=0; i<count; i++) {
            IContainerModel child = getChildContainerAt(i);
            if(child.getID().equals(name))
                return child;
        }
        return null;
    }

    public void removeOutgoingArc(IArcModel child) {
        LOG.println("NN");
        removeRelationship("outgoingArcs", child);
    }

    public void addOutgoingArc(IArcModel child) {
        addRelationship("outgoingArcs", child);
    }

    public void removeAllChildContainers() {
        removeAllRelationshipElements("childContainers");
    }

    public boolean containsChildContainer(IContainerModel child) {
        return containsRelationship("childContainers", child);
    }

    public void removeAllChildArcs() {
        removeAllRelationshipElements("childArcs");
    }

    public void removeAllIncomingArcs() {
        removeAllRelationshipElements("incomingArcs");
    }

    public void removeAllOutgoingArcs() {
        removeAllRelationshipElements("outgoingArcs");
    }

    public boolean containsIncomingArc(IArcModel arc) {
        return containsRelationship("incomingArcs", arc);
    }

    public boolean containsOutgoingArc(IArcModel arc) {
        return containsRelationship("outgoingArcs", arc);
    }

    public boolean containsChildArc(IArcModel child) {
        return containsRelationship("childArcs", child);
    }

    public void neighborDying(IRelationshipNode source) {
    }

    protected static HashSet modifyMethods = new HashSet();
    static {
        modifyMethods.addAll(BaseModel.modifyMethods);
        modifyMethods.add("createChildContainer");
        modifyMethods.add("removeChildContainer");
        modifyMethods.add("neighborChanged");
        modifyMethods.add("removeChildArc");
        modifyMethods.add("removeIncomingArc");
        modifyMethods.add("createIncomingArc");
        modifyMethods.add("setSize");
        modifyMethods.add("setName");
        modifyMethods.add("setBounds");
        modifyMethods.add("addParentContainer");
        modifyMethods.add("addOutgoingArc");
        modifyMethods.add("neighborDying");
        modifyMethods.add("addChildContainer");
        modifyMethods.add("setParentContainer");
        modifyMethods.add("addChildArc");
        modifyMethods.add("removeAllChildArcs");
        modifyMethods.add("removeOutgoingArc");
        modifyMethods.add("removeAllIncomingArcs");
        modifyMethods.add("addIncomingArc");
        modifyMethods.add("createOutgoingArc");
        modifyMethods.add("removeParentContainer");
        modifyMethods.add("removeAllChildContainers");
        modifyMethods.add("removeAllOutgoingArcs");
        modifyMethods.add("setLocation");
    }

    public HashSet getModifyMethods() {
        return modifyMethods;
    }

    public void fireDying() {
        if(getParentContainer() != null) getParentContainer().neighborDying(this);
        Vector neighbors = getNeighbors(this);
        for(int i=0; i<neighbors.size(); i++)
            ((IBaseModel)neighbors.elementAt(i)).neighborDying(this);
    }
}
