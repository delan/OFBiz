package org.ofbiz.designer.newdesigner.model;

import java.io.*;
import org.ofbiz.designer.util.*;
import java.util.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.newdesigner.operatoreditor.*;
import org.ofbiz.designer.generic.*;

public class OperatorEditorPanelModel extends BaseModel implements IOperatorEditorPanelModel {
    public String getOperatorEditorType() {
        return(String)getDataElement("type");
    }

    private OperatorEditorPanelModel() {
    }

    public static IOperatorEditorPanelModel createModelProxy() {
        OperatorEditorPanelModel model = new OperatorEditorPanelModel();
        return(IOperatorEditorPanelModel)GuiModelProxy.newProxyInstance(model, "org.ofbiz.designer.newdesigner.model.IOperatorEditorPanelModelWrapper");
    }

    public void setEditorOperatorType(String operatorType) {
        setDataElement("type", operatorType);
    }

    public String getEditorOperatorType() {
        return(String)getDataElement("type");
    }

    public String getTaskName() {
        return(String)getDataElement("taskName");
    }

    public void setTaskName(String taskName) {
        setDataElement("taskName", taskName);
    }

    public IOperatorModel getStartingOperator() {
        return(IOperatorModel)getRelationshipAt("startingOperator", 0);
    }

    public void setStartingOperator(IOperatorModel newStartingOperator) {
        IOperatorModel oldStartingOperator = getStartingOperator();
        removeRelationship("startingOperator", oldStartingOperator);
        addRelationship("startingOperator", newStartingOperator);
    }

    public void addOperator(IOperatorModel operator) {
        addRelationship("Operator", operator);
    }

    public int getOperatorCount() {
        return getRelationshipCount("Operator");
    }

    public IOperatorModel createStartingOperator(String operatorType, String operatorID, boolean terminal) {
        if(operatorID == null)
            operatorID = "Operator" + Math.random();
        IOperatorModel operator = OperatorModel.createModelProxy(operatorID, terminal);
        operator.setOperatorType(operatorType);
        setStartingOperator(operator);
        addOperator(operator);
        return operator;
    }

    private Vector getOtherTasksVector() {
        Vector vec = (Vector)getDataElement("otherTask");
        if(vec == null) {
            vec = new Vector();
            setDataElement("otherTask", vec);
        }
        return vec;
    }

    public int getOtherTaskCount() {
        Vector vec = getOtherTasksVector();
        return vec.size();
    }

    public String getOtherTaskAt(int i) {
        Vector vec = getOtherTasksVector();
        try {
            return(String)vec.elementAt(i);
        } catch(ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public boolean containsOtherTask(String otherTask) {
        int count = getOtherTaskCount();
        for(int i=0;i<count;i++)
            if(getOtherTaskAt(i).equals(otherTask)) return true;
        return false;
    }

    public void addOtherTask(String otherTask) {
        Vector vec = getOtherTasksVector();
        vec.addElement(otherTask);
        fireSynchronizeNode();
    }

    public void removeOtherTaskAt(int i) {
        Vector vec = getOtherTasksVector();
        try {
            vec.removeElementAt(i);
        } catch(ArrayIndexOutOfBoundsException e) {
        }
        fireSynchronizeNode();
    }

    public void removeOtherTask(String otherTask) {
        int count = getOtherTaskCount();
        for(int i=0;i<count;i++)
            if(getOtherTaskAt(i).equals(otherTask)) {
                removeOtherTaskAt(i);
                fireSynchronizeNode();
                return;
            }
    }

    // abstract method implementation from BaseModel
    // format [[name, complement-name, relationship-order]..];
    protected static final Object[][] relationships = { 
        {"startingOperator", "operatorEditor", SINGLE},
        {"Operator", "parentOperatorEditor", MULTIPLE},
    };
    protected static final Object[][] dataElements = { 
        {"taskName", "java.lang.String"},
        {"otherTask", "java.org.ofbiz.designer.util.Vector"},
        {"type", "java.lang.String"},
    };

    public Object[][] getRelationships() {
        return relationships;
    }

    public Object[][] getDataElements() {
        return dataElements;
    }

    protected static HashSet modifyMethods = new HashSet();
    static {
        modifyMethods.addAll(BaseModel.modifyMethods);
        modifyMethods.add("setTaskName");
        modifyMethods.add("setStartingOperator");
        modifyMethods.add("addOtherTask");
        modifyMethods.add("removeOtherTaskAt");
        modifyMethods.add("removeOtherTask");
    }

    public HashSet getModifyMethods() {
        return modifyMethods;
    }

    public void neighborChanged(IRelationshipNode source) {
        WARNING.println("NOT IMPLEMENTED");
    }

    public void neighborDying(IRelationshipNode source) {
        synchronizeGui();
    }
}
