package org.ofbiz.designer.newdesigner.LatticeEditor.model;

import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.newdesigner.LatticeEditor.*;
import java.awt.*;
import org.ofbiz.designer.newdesigner.model.*;
import org.ofbiz.designer.util.*;
import java.awt.dnd.*;

public interface ILatticeNodeModel extends IBaseModel {
	public Point getLocation();	
	public String getName();
	public void setName(String aName);
	public Color getColor();
	public void setColor(Color aColor);
	public String getDescription();
	public void setDescription(String aDescription);
	public ILatticeModel getParent();
	public void setParent(ILatticeModel aParent);
	public void setId(String idIn);
	public String getId();
	public void setLocation(Point p);
	
	public int getHighLatticeLinkCount();
	public int getLowLatticeLinkCount();
	
	
	public ILatticeLinkModel getHighLatticeLinkAt(int index);
	public ILatticeLinkModel getLowLatticeLinkAt(int index);
	
	
	public Rectangle getBounds();
}
