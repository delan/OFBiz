package org.ofbiz.designer.newdesigner.DataEditor.model;

import org.ofbiz.designer.pattern.*;
import javax.swing.table.*;
import java.util.*;
import javax.swing.event.*;
import org.ofbiz.designer.util.*;

public class FieldListModel extends SmartAddRowTableModel implements IFieldListModel {

    public static final int TYPE_INDEX = 0;
    public static final int NAME_INDEX = 1;
    public static final int DEFAULT_INDEX = 2;

    private static Object[] columnLabels = {"Type","Name","Default"};

    private FieldListModel() {
        super(columnLabels);
    }

    public static IFieldListModel createModelProxy() {
        IFieldListModel newModel = new FieldListModel();
        IFieldListModelWrapper proxy = null;
        try {
            proxy = (IFieldListModelWrapper)GuiModelProxy.newProxyInstance(newModel,"dataeditor.model.IFieldListModelWrapper");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return(IFieldListModel)proxy;
    }

    public Vector addNewField(String type, String name, String defaultVal) {
        Vector newField = new Vector();
        newField.add(type);
        newField.add(name);
        newField.add(defaultVal);
        addRow(newField);
        return newField;
    }

    public Vector getField(String name) {
        Vector fieldModels = getDataVector();
        Vector fieldModel;
        for(int i=0;i<fieldModels.size()-1;i++) {
            fieldModel = (Vector)fieldModels.get(i);
            if(((String)fieldModel.get(FieldListModel.NAME_INDEX)).equals(name))
                return fieldModel;
        }
        return null;
    }

    public void removeField(Vector theField) {
        removeRow(getDataVector().indexOf(theField));
    }

    public HashSet getModifyMethods() {
        HashSet returnObj = new HashSet();
        returnObj.add("addNewField");
        returnObj.add("removeField");
        returnObj.add("setValueAt");
        returnObj.add("addRow");
        returnObj.add("removeRow");
        return returnObj;
    }

    public Vector getFields() {
        return getDataVector();
    }

    public void dataGone() {
        WARNING.println("DATA HAS BEEN REMOVED !!");
    }
}
