package org.ofbiz.designer.newdesigner.DataEditor.model;

import org.ofbiz.designer.pattern.*;
import javax.swing.table.*;
import java.util.*;
import javax.swing.event.*;
import org.ofbiz.designer.util.*;

public class MethodListModel extends SmartAddRowTableModel implements IMethodListModel {

    public static final int TYPE_INDEX = 0;
    public static final int NAME_INDEX = 1;

    private static Object[] columnLabels = {"Type","Name"};

    private Vector exceptionLists;
    private Vector paramLists;

    private MethodListModel() {
        super(columnLabels);
        exceptionLists = new Vector();
        paramLists = new Vector();
        exceptionLists.add(ExceptionListModel.createModelProxy());
        paramLists.add(FieldListModel.createModelProxy());
    }

    public static IMethodListModel createModelProxy() {
        IMethodListModel newModel = new MethodListModel();
        IMethodListModelWrapper proxy = null;
        try {
            proxy = (IMethodListModelWrapper)GuiModelProxy.newProxyInstance(newModel,"dataeditor.model.IMethodListModelWrapper");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return(IMethodListModel)proxy;
    }

    public Vector addNewMethod(String type, String name) {
        Vector newMethod = new Vector();
        newMethod.add(type);
        newMethod.add(name);
        addRow(newMethod);
        exceptionLists.add(ExceptionListModel.createModelProxy());
        paramLists.add(FieldListModel.createModelProxy());
        return newMethod;
    }

    public Vector getMethod(String name) {
        Vector methodModels = getDataVector();
        Vector methodModel;
        for(int i=0;i<methodModels.size()-1;i++) {
            methodModel = (Vector)methodModels.get(i);
            if(((String)methodModel.get(MethodListModel.NAME_INDEX)).equals(name))
                return methodModel;
        }
        return null;
    }

    public void removeMethod(Vector theMethod) {
        int index = getDataVector().indexOf(theMethod);
        removeRow(index);
    }

    public HashSet getModifyMethods() {
        HashSet returnObj = new HashSet();
        returnObj.add("addNewMethod");
        returnObj.add("removeMethod");
        returnObj.add("setValueAt");
        returnObj.add("addRow");
        returnObj.add("removeRow");
        return returnObj;
    }

    public Vector getMethods() {
        return getDataVector();
    }

    public IExceptionListModel getExceptionListAt(int index) {
        return(IExceptionListModel)exceptionLists.get(index);
    }

    public IFieldListModel getParamListAt(int index) {
        return(IFieldListModel)paramLists.get(index);
    }

    public void addNewBlankRow() {
        super.addNewBlankRow();
        exceptionLists.add(ExceptionListModel.createModelProxy());
        paramLists.add(FieldListModel.createModelProxy());
    }

    public void removeRow(int index) {
        super.removeRow(index);
        exceptionLists.removeElementAt(index);
        paramLists.removeElementAt(index);
    }
    public void dataGone() {
        WARNING.println("DATA HAS BEEN REMOVED !!");
    }


}