package org.ofbiz.designer.newdesigner.DataEditor.model;

import org.ofbiz.designer.pattern.*;
import javax.swing.table.*;
import java.util.*;
import javax.swing.event.*;
import org.ofbiz.designer.util.*;

public class ExceptionListModel extends SmartAddRowTableModel implements IExceptionListModel {

    public static final int NAME_INDEX = 0;

    private static Object[] columnLabels = {"Name"};

    private ExceptionListModel() {
        super(columnLabels);
    }

    public static IExceptionListModel createModelProxy() {
        IExceptionListModel newModel = new ExceptionListModel();
        IExceptionListModelWrapper proxy = null;
        try {
            proxy = (IExceptionListModelWrapper)GuiModelProxy.newProxyInstance(newModel,"dataeditor.model.IExceptionListModelWrapper");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return(IExceptionListModel)proxy;
    }

    public Vector addNewException(String name) {
        Vector newException = new Vector();
        newException.add(name);
        addRow(newException);
        return newException;
    }

    public Vector getException(String name) {
        Vector exceptionModels = getDataVector();
        Vector exceptionModel;
        for(int i=0;i<exceptionModels.size()-1;i++) {
            exceptionModel = (Vector)exceptionModels.get(i);
            if(((String)exceptionModel.get(ExceptionListModel.NAME_INDEX)).equals(name))
                return exceptionModel;
        }
        return null;
    }

    public void removeException(Vector theException) {
        removeRow(getDataVector().indexOf(theException));
    }

    public HashSet getModifyMethods() {
        HashSet returnObj = new HashSet();
        returnObj.add("addNewException");
        returnObj.add("removeException");
        returnObj.add("setValueAt");
        returnObj.add("addRow");
        returnObj.add("removeRow");
        return returnObj;
    }

    public Vector getExceptions() {
        return getDataVector();
    }
    public void dataGone() {
        WARNING.println("DATA HAS BEEN REMOVED !!");
    }

}
