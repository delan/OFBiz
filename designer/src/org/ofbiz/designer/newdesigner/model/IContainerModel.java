package org.ofbiz.designer.newdesigner.model;

import java.awt.*;
//import org.ofbiz.designer.newdesigner.IContainerViewer;
import org.ofbiz.designer.util.*;
import javax.swing.*;
import java.util.*;

public interface IContainerModel extends IBaseModel, org.ofbiz.designer.pattern.IModel{
	public Rectangle getAbsoluteBounds(IContainerModel reference);

	public void removeChildArc(IArcModel child);
	public void removeAllChildArcs();
	public void addChildArc(IArcModel child);
	
	public void removeOutgoingArc(IArcModel arc);
	public void removeAllOutgoingArcs();
	public void addOutgoingArc(IArcModel arc);
	
	public void removeIncomingArc(IArcModel arc);
	public void removeAllIncomingArcs();
	public void addIncomingArc(IArcModel arc);

	boolean isAncestorOf(IContainerModel model);
	
	public IContainerModel getParentContainer();
	public IContainerModel getTopLevelContainer();
	//public String getName();
	public String getID();
	public void setID(String ID);
	//public void setName(String name);
	public Rectangle getBounds();
	public void setBounds(Rectangle bounds);
	public void setSize(Dimension dim);
	public Vector getNeighbors(IContainerModel originator);
	public IContainerModel getChildContainerByNameRecursive(String name);
	public IContainerModel getChildContainerByName(String name);

	public int getChildContainerCount();
	public IContainerModel getChildContainerAt(int i);
	public IContainerModel createChildContainer();
	
	public int getChildArcCount();
	public IArcModel getChildArcAt(int i);
	public int getIncomingArcCount();
	public IArcModel getIncomingArcAt(int i);
	public int getOutgoingArcCount();
	public IArcModel getOutgoingArcAt(int i);
	public boolean containsChildContainer(IContainerModel child);
	public boolean containsChildArc(IArcModel child);
	public void setLocation(Point location);
	public void addChildContainer(IContainerModel child);
	public void setParentContainer(IContainerModel parentContainerIn);
	public void removeChildContainer(IContainerModel child);
	public void removeAllChildContainers();
	public Point getLocation();
	public WFPopup getPopup();
}



