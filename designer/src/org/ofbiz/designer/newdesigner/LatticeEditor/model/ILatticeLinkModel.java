package org.ofbiz.designer.newdesigner.LatticeEditor.model;

import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.newdesigner.LatticeEditor.*;
import java.awt.*;
import org.ofbiz.designer.newdesigner.model.*;


public interface ILatticeLinkModel extends IBaseModel {
	public ILatticeNodeModel getHigh();
	public ILatticeNodeModel getLow();
	public void setHigh(ILatticeNodeModel high);
	public void setLow(ILatticeNodeModel low);
	public ILatticeModel getParent();
	public void setParent(ILatticeModel aParent);
	public String getId();
	public void setId(String anId);
	public void setHighBounds(Rectangle newLocation);
	public Rectangle getHighBounds();
	public void setLowBounds(Rectangle newLocation);
	public Rectangle getLowBounds();
}