package org.ofbiz.designer.newdesigner.LatticeEditor.model;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.generic.*;
import java.util.*;
import org.ofbiz.designer.newdesigner.LatticeEditor.*;
import java.awt.*;
import org.ofbiz.designer.newdesigner.model.*;


public interface ILatticeModel extends IBaseModel {
	public ILatticeNodeModel addLatticeNode(Point aPos, String id);
	public void addLatticeNode(ILatticeNodeModel aModel);
	public ILatticeLinkModel addLatticeLink(ILatticeNodeModel high, ILatticeNodeModel low);
	public void addLatticeLink(ILatticeLinkModel aRelationship);
	//public void removeLatticeLink(ILatticeLinkModel removal) ;
	//public void removeLatticeNode(ILatticeNodeModel aDomain);
	public ILatticeNodeModel getLatticeNodeAt(int index);
	public ILatticeLinkModel getLatticeLinkAt(int index);
	public int getLatticeNodeCount();
	public int getLatticeLinkCount();
	public Hashtable latticeNodesHashedById();
	public Hashtable latticeLinksHashedById();
	public void removeRedundancy(ILatticeNodeModel low, ILatticeNodeModel high);
	public boolean checkAncestors(ILatticeNodeModel start, ILatticeNodeModel target);
	public String getId();
	public void setId(String newId);
	public String getName();
	public void setName(String newName);	
}
