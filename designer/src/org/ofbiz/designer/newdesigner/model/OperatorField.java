package org.ofbiz.designer.newdesigner.model;

import java.io.*;
import org.ofbiz.designer.util.*;
import java.util.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.generic.*;

public class OperatorField extends BaseModel implements IOperatorField {
    private OperatorField() {
    }

    public static IOperatorField createModelProxy(String ID) {
        OperatorField model = new OperatorField();
        model.setDataElement("ID", ID);
        return(IOperatorField)GuiModelProxy.newProxyInstance(model, "org.ofbiz.designer.newdesigner.model.IOperatorFieldWrapper");
    }

    public String getID() {
        return(String)getDataElement("ID");
    }

    public String getCondition() {
        return(String)getDataElement("condition");
    }

    public void setCondition(String condition) {
        setDataElement("condition", condition);
    }

    public IOperatorModel getOperator() {
        return(IOperatorModel)getRelationshipAt("operator", 0);
    }

    public void setOperator(IOperatorModel model) {
        addRelationship("operator", model);
    }

    public IOperatorModel getParentOperator() {
        return(IOperatorModel)getRelationshipAt("parentOperator", 0);
    }

    public void setParentOperator(IOperatorModel model) {
        addRelationship("parentOperator", model);
    }

    public IOperatorModel createOperator(String operatorType, String operatorID, boolean terminal) {
        if(operatorID == null)
            operatorID = "Operator" + Math.random();
        IOperatorModel operator = OperatorModel.createModelProxy(operatorID, terminal);
        operator.setOperatorType(operatorType);
        if(operatorType.equals(OperatorType.TASK)) {
            IOperatorField field = operator.createField(operatorID);
            field.setCondition(operatorID);
        }
        operator.setParentOperatorEditor(getParentOperator().getParentOperatorEditor());
        operator.setPredecessor(this);
        return operator;
    }

    public void neighborChanged(IRelationshipNode obj) {
        WARNING.println("**** NOT IMPLEMENTED " + obj);
    }

    public HashSet getModifyMethods() {
        HashSet set = new HashSet();
        set.add("setCondition");
        set.add("setOperator");
        set.add("setParentOperator");
        set.add("createOperator");
        return set;
    }

    // abstract method implementation from BaseModel
    // format [[name, complement-name, relationship-order]..];
    protected static final Object[][] relationships = { 
        {"parentOperator", "operatorField", SINGLE},
        {"operator", "predecessor", SINGLE},
    };
    protected static final Object[][] dataElements = { 
        {"condition", "java.lang.String"},
        {"ID", "java.lang.String"},
    };

    public Object[][] getRelationships() {
        return relationships;
    }

    public Object[][] getDataElements() {
        return dataElements;
    }

    public void neighborDying(IRelationshipNode source) {
        if(source.equals(getOperator())) 
            die();
    }

    public String toString() {
        String returnString = "OpField:Condition:" + getCondition();
        return returnString;
    }
}
