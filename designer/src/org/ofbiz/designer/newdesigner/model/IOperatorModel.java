package org.ofbiz.designer.newdesigner.model;

import java.util.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.pattern.*;

public interface IOperatorModel extends IBaseModel, org.ofbiz.designer.pattern.IModel {
    public boolean isTerminalOperator();
    public String getID();
    public String getOperatorType();
    public void setOperatorType(String operatorType);
    public IOperatorField getPredecessor();
    public void setPredecessor(IOperatorField field);
    public IOperatorEditorPanelModel getParentOperatorEditor();
    public void setParentOperatorEditor(IOperatorEditorPanelModel parent);

    public int getFieldCount();
    public IOperatorField getFieldAt(int i);
    public IOperatorField createField(String fieldID);
    public void addField(IOperatorField field);
    public int getIndexOfField(IOperatorField field);
    public void setFields(Vector arg0);
    public void insertFieldAt(IOperatorField field, int index);
    public void setFieldAt(IOperatorField field, int index);
    public void removeField(IOperatorField field);
    public void removeFieldAt(int index);
    public void removeAllFields();
}
