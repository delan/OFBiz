package org.ofbiz.designer.newdesigner.LatticeEditor.model;

import org.ofbiz.designer.newdesigner.LatticeEditor.*;
import java.awt.*;
import java.util.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.newdesigner.model.*;
import org.ofbiz.designer.generic.*;
import java.awt.dnd.*;
import org.ofbiz.designer.util.*;

public class LatticeNodeModel extends BaseModel implements ILatticeNodeModel {
    protected LatticeNodeModel() {
        setDescription("");
        setColor(new Color(0,0,0));
        setName("New");
    }

    public static ILatticeNodeModel createModelProxy() {
        ILatticeNodeModel newModel = new LatticeNodeModel();
        ILatticeNodeModelWrapper proxy = null;
        try {
            proxy = (ILatticeNodeModelWrapper)GuiModelProxy.newProxyInstance(newModel,"latticeeditor.model.ILatticeNodeModelWrapper");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return proxy;
    }


    public ILatticeModel getParent() {
        return(ILatticeModel)getRelationshipAt("parent",0);
    }

    public void setParent(ILatticeModel aParent) {
        if(getParent()!=null)
            removeRelationship("parent",getParent());
        addRelationship("parent",aParent);
    }

    public Point getLocation() {
        return(Point) getDataElement("location");
    }

    public void setLocation(Point p) {
        setDataElement("location",p);
    }

    public String getName() {
        return(String) getDataElement("name");
    }

    public void setName(String aName) {
        setDataElement("name",aName);
    }

    public Color getColor() {
        return(Color) getDataElement("color");
    }

    public void setColor(Color aColor) {
        setDataElement("color",aColor);
    }

    public String getDescription() {
        return(String) getDataElement("description");
    }

    public void setDescription(String aDescription) {
        setDataElement("description",aDescription);
    }

    /*
    public HashSet getModifyMethods(){
        HashSet returnSet = new HashSet();
        returnSet.add("setColor");
        returnSet.add("setName");
        returnSet.add("setDescription");
        returnSet.add("setLocation");
        return returnSet;
    }
    */

    public void setId(String idIn) {
        setDataElement("id",idIn);
    }

    public String getId() {
        return(String)getDataElement("id");
    }

    public Object[][] getRelationships() {
        Object[][] returnObj = {
            {"parent","latticeNodes",SINGLE},
            {"highLatticeLinks","low",MULTIPLE},
            {"lowLatticeLinks","high",MULTIPLE}

        };
        return returnObj; 
    }

    public Object[][] getDataElements() {
        Object[][] returnObj = {
            {"id","java.lang.String"},
            {"name","java.lang.String"},
            {"color","java.awt.Color"},
            {"location","java.awt.Point"},
            {"description","java.lang.String"},
        };
        return returnObj;
    }

    public int getHighLatticeLinkCount() {
        return getRelationshipCount("highLatticeLinks");
    }
    public int getLowLatticeLinkCount() {
        return getRelationshipCount("lowLatticeLinks");
    }



    public ILatticeLinkModel getHighLatticeLinkAt(int index) {
        return(ILatticeLinkModel)getRelationshipAt("highLatticeLinks",index);
    }
    public ILatticeLinkModel getLowLatticeLinkAt(int index) {
        return(ILatticeLinkModel)getRelationshipAt("lowLatticeLinks",index);
    }



    public void neighborChanged(IRelationshipNode neighbor) {
        //purposefully not implemented.
    }

    public void neighborDying(IRelationshipNode neighbor) {
        //purposefully not implemented.
    }

    public Rectangle getBounds() {
        return new Rectangle(getLocation().x,getLocation().y,200,60);
    }

    protected static HashSet modifyMethods = new HashSet();
    static {
        modifyMethods.add("setColor");
        modifyMethods.add("setName");
        modifyMethods.add("setDescription");
        modifyMethods.add("setLocation");
    }

    public HashSet getModifyMethods() {
        return modifyMethods;
    }
}
