package org.ofbiz.designer.newdesigner.LatticeEditor.model;

import org.ofbiz.designer.newdesigner.LatticeEditor.*;
import java.util.*;
import java.awt.*;
import org.ofbiz.designer.generic.*;
import javax.swing.text.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.newdesigner.model.*;


public class LatticeModel extends BaseModel implements ILatticeModel {


    protected LatticeModel() {
    }

    /*
    public static ILatticeModel createModelProxy() {
        ILatticeModel newModel = new LatticeModel();
        ILatticeModelWrapper proxy = null;
        try {
            proxy = (ILatticeModelWrapper)GuiModelProxy.newProxyInstance(newModel,"latticeeditor.model.ILatticeModelWrapper");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return proxy;
    }
    */

    public Object[][] getRelationships() {
        Object[][] returnObj = {
            {"latticeNodes","parent",MULTIPLE},
            {"latticeLinks","parent",MULTIPLE}
        };
        return returnObj; 
    }

    public Object[][] getDataElements() {
        Object[][] returnObj = {
            {"id","java.lang.String"},
            {"name","java.lang.String"}
        };
        return returnObj;
    }

    public String getId() {
        return(String)getDataElement("id");
    }

    public void setId(String newId) {
        setDataElement("id",newId);
    }

    public String getName() {
        return(String)getDataElement("name");
    }

    public void setName(String newName) {
        setDataElement("name",newName);
    }

    public ILatticeNodeModel addLatticeNode(Point aPos, String id) {
        ILatticeNodeModel newNode = createNewNode();
        int idNum;
        String newIdName = "Node0";
        int i = 0;
        while(latticeNodesHashedById().containsKey(newIdName)) {
            newIdName = "Node" + i++;
        }
        newNode.beginTransaction();
        newNode.setLocation(aPos);
        newNode.setId(newIdName);
        newNode.setParent(this);        
        addRelationship("latticeNodes",newNode);
        newNode.commitTransaction();
        return newNode;
    }

    public void addLatticeNode(ILatticeNodeModel newNode) {
        newNode.beginTransaction();
        newNode.setParent(this);
        addRelationship("latticeNodes",newNode);
        newNode.commitTransaction();
    }


    public ILatticeLinkModel addLatticeLink(ILatticeNodeModel high, ILatticeNodeModel low) {
        if(checkAncestors(high,low)) {
            org.ofbiz.designer.util.LOG.println("Cyclical relationship.");
            return null;
        }
        if(checkAncestors(low,high)) {
            org.ofbiz.designer.util.LOG.println("Redundant relationship.");
            return null;
        }
        removeRedundancy(low,high);
        ILatticeLinkModel newLink = LatticeLinkModel.createModelProxy();

        newLink.beginTransaction();
        newLink.setHigh(high);
        newLink.setHighBounds(high.getBounds());
        newLink.setLow(low);
        newLink.setLowBounds(low.getBounds());
        newLink.setId(high.getId()+low.getId());
        newLink.setParent(this);
        addRelationship("latticeLinks",newLink);
        newLink.commitTransaction();
        return newLink;
    }

    public void addLatticeLink(ILatticeLinkModel newLink) {
        ILatticeNodeModel high = newLink.getHigh();
        ILatticeNodeModel low = newLink.getLow();
        newLink.beginTransaction();
        newLink.setParent(this);
        addRelationship("latticeLinks",newLink);
        newLink.commitTransaction();
    }

    public void removeRedundancy(ILatticeNodeModel low, ILatticeNodeModel high) {
        ILatticeLinkModel currLink;
        for(int i=0;i<low.getHighLatticeLinkCount();i++) {
            currLink = (ILatticeLinkModel) low.getHighLatticeLinkAt(i);
            if(checkAncestors(high,currLink.getHigh())) {
                currLink.die();
                return;
            }
        }
    }

    /*
    public void removeLatticeLink(ILatticeLinkModel removal) {
        removeRelationship("latticeLinks",removal);
    }
    
    public void removeLatticeNode(ILatticeNodeModel removal) {
        removeRelationship("latticeNodes",removal);
    }
    */


    public boolean checkAncestors(ILatticeNodeModel start, ILatticeNodeModel target) {
        Vector nodeList;
        Vector nextNodeList = new Vector();

        if(start.equals(target)) return true;

        for(int i=0;i<start.getHighLatticeLinkCount();i++) {
            nextNodeList.add(((ILatticeLinkModel) start.getHighLatticeLinkAt(i)).getHigh());
        }

        ILatticeNodeModel currNode;

        while(!nextNodeList.isEmpty()) {
            nodeList = nextNodeList;
            nextNodeList = new Vector();
            for(int i=0;i<nodeList.size();i++) {
                currNode = (ILatticeNodeModel) nodeList.get(i);
                if(currNode.equals(target)) return true;
                for(int j=0;j<currNode.getHighLatticeLinkCount();j++)
                    nextNodeList.add(((ILatticeLinkModel) currNode.getHighLatticeLinkAt(j)).getHigh());
            }
        }   
        return false;
    }

    /*
    public HashSet getModifyMethods(){
        HashSet returnSet = new HashSet();
        returnSet.add("addLatticeNode");
        returnSet.add("removeLatticeNode");
        returnSet.add("addLatticeLink");
        returnSet.add("removeLatticeLink");
        returnSet.add("setId");
        returnSet.add("setName");
        return returnSet;
    }
    */

    public ILatticeNodeModel getLatticeNodeAt(int index) {
        return(ILatticeNodeModel)getRelationshipAt("latticeNodes",index);
    }

    public ILatticeLinkModel getLatticeLinkAt(int index) {
        return(ILatticeLinkModel)getRelationshipAt("latticeLinks",index);
    }



    public int getLatticeNodeCount() {
        return getRelationshipCount("latticeNodes");
    }

    public int getLatticeLinkCount() {
        return getRelationshipCount("latticeLinks");
    }



    public Hashtable latticeNodesHashedById() {
        Hashtable returnObj = new Hashtable();
        ILatticeNodeModel aNode;
        for(int i=0;i<getLatticeNodeCount();i++) {
            aNode = getLatticeNodeAt(i);
            returnObj.put(aNode.getId(),aNode);
        }
        return returnObj;
    }

    public Hashtable latticeLinksHashedById() {
        Hashtable returnObj = new Hashtable();
        ILatticeLinkModel aLink;
        for(int i=0;i<getLatticeLinkCount();i++) {
            aLink = (ILatticeLinkModel)getLatticeLinkAt(i);
            returnObj.put(aLink.getId(),aLink);
        }
        return returnObj;
    }



    public void neighborChanged(IRelationshipNode neighbor) {
        //purposefully not implemented.
    }

    public void neighborDying(IRelationshipNode neighbor) {
        //purposefully not implemented.
    }

    public ILatticeNodeModel createNewNode() {
        return(ILatticeNodeModel) LatticeNodeModel.createModelProxy();
    }

    //protected HashSet getModifyMethodsImpl() {

    protected static HashSet modifyMethods = new HashSet();
    static {
        modifyMethods.add("setName");
        modifyMethods.add("setId");
        modifyMethods.add("addLatticeNode");
        modifyMethods.add("addLatticeLink");
    }

    public HashSet getModifyMethods() {
        return modifyMethods;
    }
}
