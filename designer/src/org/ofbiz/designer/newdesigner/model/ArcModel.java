package org.ofbiz.designer.newdesigner.model;

import java.io.*;
import java.util.*;
import org.ofbiz.designer.util.*;
import java.awt.*;
import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.newdesigner.popup.*;

public class ArcModel extends BaseModel implements Serializable, IArcModel {
    private ArcModel() {
    }

    public static IArcModel createModelProxy(String ID) {
        if(ID == null)
            ID = "Arc" + Math.random();
        ArcModel model = new ArcModel();
        model.setDataElement("ID", ID);
        return(IArcModel)GuiModelProxy.newProxyInstance(model, "org.ofbiz.designer.newdesigner.model.IArcModelWrapper");
    }

    public IArcModel myModelProxy() {
        return(IArcModel)GuiModelProxy.newProxyInstance(this, "org.ofbiz.designer.newdesigner.model.IArcModelWrapper");
    }

    private static int tempInt = 0;

    private void fixBoundsSourceIsNode() {
        try {
            Rectangle sourceRect = ((IContainerModel)getSource()).getAbsoluteBounds(getParent());
            Rectangle destinationRect = getDestination().getAbsoluteBounds(getParent());
            Line tempLine = new Line(sourceRect.x + sourceRect.width/2, 
                                     sourceRect.y + sourceRect.height/2, 
                                     destinationRect.x + destinationRect.width/2, 
                                     destinationRect.y + destinationRect.height/2);
            setLine(tempLine);
        } catch(Exception e) {
        }
    }

    private void fixBoundsSourceIsArc() {
        try {
            Rectangle sourceRect = ((IArcModel)getSource()).getAbsoluteBounds(getParent());
            Rectangle destinationRect = getDestination().getAbsoluteBounds(getParent());
            Line tempLine = new Line(sourceRect.x + sourceRect.width/2, 
                                     sourceRect.y + sourceRect.height/2, 
                                     destinationRect.x + destinationRect.width/2, 
                                     destinationRect.y + destinationRect.height/2);
            setLine(tempLine);
        } catch(Exception e) {
        }
    }

    public Rectangle getAbsoluteBounds(IContainerModel reference) {
        Rectangle returnValue = null;
        try{
            returnValue = (Rectangle)getBounds().clone();
        } catch(NullPointerException e){
            return null;
        }

        if(this == reference) return returnValue;
        if(reference == null) return returnValue;
        IContainerModel temp = getParent();
        while(temp != reference) {
            if(temp == null) return null;
            returnValue.x += temp.getBounds().x; 
            returnValue.y += temp.getBounds().y;
            temp = temp.getParentContainer();
        }
        return returnValue;
    }

    private void fixBounds() {
        if(getSource() == null || getSource() instanceof IContainerModel)
            fixBoundsSourceIsNode();
        else if(getSource() instanceof IArcModel)
            fixBoundsSourceIsArc();
        else throw new RuntimeException("Bad Source Type");
    }

    public void neighborChanged(IRelationshipNode obj) {
        fixParent();
        fixBounds();
    }

    public void fixParent() {
        IContainerModel temp = null;
        if(getSource() instanceof IContainerModel)
            temp = getCommonAncestor((IContainerModel)getSource(), getDestination());
        else {
            if(getSource() == null) temp = null;
            else temp = getCommonAncestor(((IArcModel)getSource()).getParent(), getDestination());
        }

        if(temp != null) addRelationship("parent", temp);
        else addRelationship("parent", null);
    }

    public String getID() {
        return(String)getDataElement("ID");
    }

    private Line getLine() {
        return(Line)getDataElement("line");
    }

    private void setLine(Line line) {
        setDataElement("line", line);
    }

    /*
    public void removeOutgoingArc(IArcModel child) {
        removeRelationship("outgoingArcs", child);
    }
                                                                                                                                                                                
    public void addOutgoingArc(IArcModel child) {
        addRelationship("outgoingArcs", child);
    }

    public void removeAllOutgoingArcs() {
        removeAllRelationshipElements("outgoingArcs");
    }

    public int getOutgoingArcCount(){
        return getRelationshipCount("outgoingArcs");
    }

    public IArcModel getOutgoingArcAt(int i){
        return (IArcModel)getRelationshipAt("outgoingArcs", i);
    }

    public boolean containsOutgoingArc(IArcModel child){
        int count = getOutgoingArcCount();
        for(int i=0;i<count;i++) {
            IArcModel arc = getOutgoingArcAt(i);
            if(arc == child) 
                return true;
        }
        return false;
    }
    */

    public void setOutgoingArc(IArcModel arc) {
        removeAllRelationshipElements("outgoingArcs");
        addRelationship("outgoingArcs", arc);
    }

    public IArcModel getOutgoingArc() {
        return(IArcModel)getRelationshipAt("outgoingArcs", 0);
    }

    public IArcModel createOutgoingArc(String destinationID, String sourceArcID, String newArcID){
        //public IArcModel createOutgoingArc(String destinationID, String arcID) {
        if(ArcLoader.existsPendingArc(sourceArcID, destinationID)) {
            IArcModelWrapper arc = (IArcModelWrapper)ArcLoader.getPendingArc(sourceArcID, destinationID);
            ArcLoader.removePendingArc(sourceArcID, destinationID);
            arc.setSource(this);
            return arc;
        }
        IArcModelWrapper arc = (IArcModelWrapper )ArcModel.createModelProxy(newArcID);
        //arc.setLineStyle(arcType);
        INetworkEditorComponentModel sourceTaskModel = (INetworkEditorComponentModel)getSource();
        INetworkEditorComponentModel destinationModel = ((INetworkEditorComponentModel)sourceTaskModel.getTopLevelContainer()).getChildContainerByIDRecursive(destinationID);
        ArcLoader.addPendingArc(sourceArcID, destinationID, arc);       
        if(destinationModel != null) {
            arc.setDestination(destinationModel);
            ArcLoader.removePendingArc(sourceArcID, destinationID);
        }
        arc.setSource(this);
        return arc;
    }




    public String getLineStyle() {
        return(String)getDataElement("lineStyle");
    }

    public void setLineStyle(String style) {
        setDataElement("lineStyle", style);
    }

    public String getException() {
        return(String)getDataElement("exception");
    }

    public void setException(String exception) {
        setDataElement("exception", exception);
    }

    /*
    public String getAlternativeTransition() {
        return(String)getDataElement("alternativeTransition");
    }

    public void setAlternativeTransition(String alternativeTransition) {
        setDataElement("alternativeTransition", alternativeTransition);
    }
    */

    public static IContainerModel getCommonAncestor(IContainerModel model1, IContainerModel model2) {
        if(model1 == null || model2 == null) return null;
        if(model2.isAncestorOf(model1)) return model2;

        while(true) {
            if(model1 == null || model1.isAncestorOf(model2)) return model1;
            else model1 = model1.getParentContainer();
        }
    }

    public String toString() {
        String returnString = "[";
        IContainerModel source = (IContainerModel)getRelationshipAt("source", 0);
        IContainerModel destination = (IContainerModel)getRelationshipAt("destination", 0);

        if(source != null) returnString += source.getID() + "-->";
        else returnString += "null-->";
        if(destination != null) returnString += destination.getID() + "]";
        else returnString += "null]";
        return returnString;
    }

    public Rectangle getBounds() {
        if(getLine() == null) return null;
        else return getLine().getBounds();
    }

    public WFPopup getPopup() {
        return new ArcPopup();
    }

    protected static final Object[][] relationships = { 
        {"source", "outgoingArcs", SINGLE},
        {"destination", "incomingArcs", SINGLE},
        {"outgoingArcs", "source", MULTIPLE}, //alternative arcs
        {"parent", "childArcs", SINGLE},
    };
    protected static final Object[][] dataElements = { 
        {"line", "org.ofbiz.designer.util.Line"},
        {"lineStyle", "java.lang.String"},
        {"exception", "java.lang.String"},
        //{"alternativeTransition", "java.lang.String"},
        {"ID", "java.lang.String"},
    };


    public Object[][] getRelationships() {
        return relationships;
    }   

    public Object[][] getDataElements() {
        return dataElements;
    }   

    // relationship methods
    public IContainerModel getDestination() {
        return(IContainerModel)getRelationshipAt("destination", 0);
    }

    public synchronized void setDestination(IContainerModel destination) {
        if(destination == null)
            die();
        else{
            removeAllRelationshipElements("destination");
            addRelationship("destination", destination);
            fixParent();
            fixBounds();
        }
    }

    public synchronized void setSource(IRelationshipNode source) {
        if(source == null)
            die();
        else{
            removeAllRelationshipElements("source");
            addRelationship("source", source);
            fixParent();
            fixBounds();
        }
    }
    public IRelationshipNode getSource() {
        return getRelationshipAt("source", 0);
    }

    public IContainerModel getSourceTask() {
        IRelationshipNode temp = getSource();
        if(temp instanceof IContainerModel)
            return(IContainerModel)temp;
        else
            return((IArcModel)temp).getSourceTask();
    }

    public IContainerModel getParent() {
        return(IContainerModel)getRelationshipAt("parent", 0);
    }

    protected static HashSet modifyMethods = new HashSet();
    static {
        modifyMethods.addAll(BaseModel.modifyMethods);
        modifyMethods.add("setSource");     
        modifyMethods.add("setDestination");        
        modifyMethods.add("setAlternativeTransition");        
        modifyMethods.add("removeOutgoingArc");        
        modifyMethods.add("addOutgoingArc");        
        modifyMethods.add("removeAllOutgoingArcs");        
    }

    public HashSet getModifyMethods() {
        return modifyMethods;
    }

    public void neighborDying(IRelationshipNode source) {
        try {
            if(source instanceof IArcModel &&  ((IArcModel)source).getID().equals(getOutgoingArc().getID())) {
                setOutgoingArc(null);
                return;
            }
        } catch(NullPointerException e) {
        }
        setSource(null);
        setDestination(null);
        die();
    }
}

