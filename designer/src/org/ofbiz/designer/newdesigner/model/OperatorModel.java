package org.ofbiz.designer.newdesigner.model;

import java.io.*;
import org.ofbiz.designer.util.*;
import java.util.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.generic.*;

public class OperatorModel extends BaseModel implements Serializable, IOperatorModel {
    private OperatorModel() {
    }

    public static IOperatorModel createModelProxy(String ID, boolean terminal) {
        OperatorModel model = new OperatorModel();
        model.setDataElement("ID", ID);
        model.setDataElement("terminal", new Boolean(terminal));
        return(IOperatorModel)GuiModelProxy.newProxyInstance(model, "org.ofbiz.designer.newdesigner.model.IOperatorModelWrapper");
    }

    public boolean isTerminalOperator() {
        Boolean value = (Boolean)getDataElement("terminal");
        return value.booleanValue();
    }

    public String getID() {
        return(String)getDataElement("ID");
    }

    public String getOperatorType() {
        return(String)getDataElement("type");
    }

    public void setOperatorType(String operatorType) {
        setDataElement("type", operatorType);
    }

    public IOperatorField getPredecessor() {
        return(IOperatorField)getRelationshipAt("predecessor", 0);
    }

    public void setPredecessor(IOperatorField field) {
        setRelationship("predecessor", field);
    }

    public IOperatorEditorPanelModel getParentOperatorEditor() {
        return(IOperatorEditorPanelModel)getRelationshipAt("parentOperatorEditor", 0);
    }

    public void setParentOperatorEditor(IOperatorEditorPanelModel parent) {
        setRelationship("parentOperatorEditor", parent);
    }

    public int getFieldCount() {
        return getRelationshipCount("operatorField");
    }

    public IOperatorField getFieldAt(int i) {
        return(IOperatorField)getRelationshipAt("operatorField", i);
    }

    public int getIndexOfField(IOperatorField field) {
        return getIndexOf("operatorField", field);
    }

    public IOperatorField createField(String fieldID) {
        if(fieldID == null)
            fieldID = "Field" + Math.random();
        IOperatorField field = OperatorField.createModelProxy(fieldID);
        field.setParentOperator(this);
        return field;
    }

    public void addField(IOperatorField field) {
        addRelationship("operatorField", field);
    }

    public void setFields(Vector arg0) {
        removeAllRelationshipElements("operatorField");
        for(int i=0; i<arg0.size(); i++) {
            IOperatorField field = (IOperatorField)arg0.elementAt(i);
            addRelationship("operatorField", field);
        }
    }

    public void insertFieldAt(IOperatorField field, int index) {
        insertRelationshipAt("operatorField", field, index);
    }

    public void setFieldAt(IOperatorField field, int index) {
        IOperatorField temp = (IOperatorField)getRelationshipAt("operatorField", index);
        removeRelationship("operatorField", temp);
        insertRelationshipAt("operatorField", field, index);
    }

    public void removeField(IOperatorField field) {
        removeRelationship("operatorField", field);
    }

    public void removeFieldAt(int index) {
        IOperatorField temp = (IOperatorField)getRelationshipAt("operatorField", index);
        removeRelationship("operatorField", temp);
    }

    public void removeAllFields() {
        removeAllRelationshipElements("operatorField");
    }

    public boolean containsField(IOperatorField field) {
        int count = getFieldCount();
        for(int i=0;i<count;i++) {
            IOperatorField temp = getFieldAt(i);
            if(temp.equals(field))
                return true;
        }
        return false;
    }

    private int counter = 0;
    public void neighborChanged(IRelationshipNode obj) {
        LOG.println("YYYYYYYYYYYY");
        // if obj is parentOperatorEditor and I am terminal,  check if what I point to is still valid, if not die
        if(obj instanceof IOperatorEditorPanelModel && 
                    isTerminalOperator() && 
                    !getParentOperatorEditor().containsOtherTask(getFieldAt(0).getCondition())) 
            die();
    }

    // abstract method implementation from BaseModel
    // format [[name, complement-name, relationship-order]..];
    protected static final Object[][] relationships = { 
        {"operatorEditor", "startingOperator", SINGLE},
        {"parentOperatorEditor", "Operator", SINGLE},
        {"operatorField", "parentOperator", MULTIPLE},
        {"predecessor", "operator", SINGLE},
    };

    protected static final Object[][] dataElements = { 
        {"ID", "java.lang.String"},
        {"terminal", "java.lang.Boolean"},
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
        modifyMethods.add("setOperatorType");
        modifyMethods.add("addField");
        modifyMethods.add("createField");
        modifyMethods.add("setFields");
        modifyMethods.add("insertFieldAt");
        modifyMethods.add("setFieldAt");
        modifyMethods.add("removeField");
        modifyMethods.add("removeFieldAt");
        modifyMethods.add("removeAllFields");
    }

    public HashSet getModifyMethods() {
        return modifyMethods;
    }

/*
    public HashSet getModifyMethods() {
        HashSet set = new HashSet();
        set.add("setOperatorType");
        set.add("addField");
        set.add("createField");
        set.add("setFields");
        set.add("insertFieldAt");
        set.add("setFieldAt");
        set.add("removeField");
        set.add("removeFieldAt");
        set.add("removeAllFields");
        return set;
    }
    */

    public void neighborDying(IRelationshipNode source) {
    }

    public String toString() {
        String returnString = " Type:" + getOperatorType();
        returnString += " parentField:" + getPredecessor();
        returnString += " getParentOperatorEditor:" + getParentOperatorEditor();
        returnString += " fields:" + getFieldCount();
        return returnString;
    }
}
