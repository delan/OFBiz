package org.ofbiz.designer.newdesigner.LatticeEditor.model;

import org.ofbiz.designer.newdesigner.LatticeEditor.*;
import java.util.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.newdesigner.model.*;
import org.ofbiz.designer.generic.*;
import java.awt.*;

public class LatticeLinkModel extends BaseModel implements ILatticeLinkModel {
	
	private LatticeLinkModel() {
	}
	
	public static ILatticeLinkModel createModelProxy() {
		ILatticeLinkModel newModel = new LatticeLinkModel();
		ILatticeLinkModelWrapper proxy = null;
		try {
			proxy = (ILatticeLinkModelWrapper)GuiModelProxy.newProxyInstance(newModel,"latticeeditor.model.ILatticeLinkModelWrapper");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return proxy;
	}
	
	public Object[][] getRelationships(){
		Object[][] returnObj = {
			{"parent","latticeLinks",SINGLE},
			{"high","lowLatticeLinks",SINGLE},
			{"low","highLatticeLinks",SINGLE}
		};
		return returnObj; 
	}
	
	public Object[][] getDataElements() {
		Object[][] returnObj = {
			{"id","java.lang.String"},
			{"highBounds","java.awt.Rectangle"},
			{"lowBounds","java.awt.Rectangle"}
		};
		return returnObj;
	}
	
	public ILatticeModel getParent() {
		return (ILatticeModel)getRelationshipAt("parent",0);
	}
	
	public void setParent(ILatticeModel aParent) {
		if(getParent()!=null)
			removeRelationship("parent",getParent());
		addRelationship("parent",aParent);
	}
	
	public void setHigh(ILatticeNodeModel high) {
		if(getHigh()!=null)
			removeRelationship("high",getHigh());
		addRelationship("high",high);
	}
	
	public void setHighBounds(Rectangle newBounds) {
		setDataElement("highBounds",newBounds);
	}
	
	public Rectangle getHighBounds() {
		return (Rectangle)getDataElement("highBounds");
	}
	
	public void setLow(ILatticeNodeModel low) {
		if(getLow()!=null)
			removeRelationship("low",getLow());
		addRelationship("low",low);
	}
	
	public void setLowBounds(Rectangle newBounds) {
		setDataElement("lowBounds",newBounds);
	}
	
	public Rectangle getLowBounds() {
		return (Rectangle)getDataElement("lowBounds");
	}
	
	public ILatticeNodeModel getHigh() {
		return (ILatticeNodeModel)getRelationshipAt("high",0);
	}
	
	public ILatticeNodeModel getLow() {
		return (ILatticeNodeModel)getRelationshipAt("low",0);
	}
	
	/*
	public HashSet getModifyMethods(){      
		HashSet returnSet = new HashSet();  
		return returnSet;                   
	}
	*/

	public String getId() {
		return (String)getDataElement("id");
	}
	
	public void setId(String anId) {
		setDataElement("id",anId);
	}
	
	public void neighborChanged(IRelationshipNode neighbor) {
		setHighBounds(getHigh().getBounds());
		setLowBounds(getLow().getBounds());
	}
	
	public void neighborDying(IRelationshipNode neighbor) {
		die();
	}
	
        protected static HashSet modifyMethods = new HashSet();
	static {
		modifyMethods.add("setHigh");		
		modifyMethods.add("setLow"); 		
	}
	
	public HashSet getModifyMethods(){
		return modifyMethods;
	}
}
