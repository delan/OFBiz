package org.ofbiz.designer.newdesigner.model;

import java.util.*;
import org.ofbiz.designer.newdesigner.*;
import java.awt.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.generic.*;

public interface IArcModel extends IBaseModel, org.ofbiz.designer.pattern.IModel {
    public static final String DASHED = "DASHED";
    void fixParent();
    public String getID();
    public void setSource(IRelationshipNode sourceIn);
    public IContainerModel getDestination();
    public void setDestination(IContainerModel destinationIn);
    public IRelationshipNode getSource();
    public IContainerModel getSourceTask() ;
    public String getLineStyle();
    public void setLineStyle(String style);
    public String getException();
    public void setException(String exception);
    /*
    public String getAlternativeTransition();
    public void setAlternativeTransition(String alternativeTransition);
    */
    public IContainerModel getParent();
    public String toString();
    public Rectangle getBounds();
    public WFPopup getPopup();

    /*
    public void removeOutgoingArc(IArcModel arc);
    public void removeAllOutgoingArcs();
    public void addOutgoingArc(IArcModel arc);
    */
    public void setOutgoingArc(IArcModel arc);
    public IArcModel getOutgoingArc();
    public IArcModel createOutgoingArc(String destinationID, String sourceArcID, String newArcID);

    public Rectangle getAbsoluteBounds(IContainerModel reference);
}
