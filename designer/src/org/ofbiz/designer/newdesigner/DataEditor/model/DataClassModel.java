package org.ofbiz.designer.newdesigner.DataEditor.model;

import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.util.*;

public class DataClassModel extends AbstractModel implements IDataClassModel {

    private String packageName = "Package Name not Initialized";
    private String parent = "Parent not Initialized";
    private String name = "Name not Initialized";
    private IFieldListModel theFieldList = null;
    private IMethodListModel theMethodList = null;

    private DataClassModel() {
        setRealModel(this);
        theFieldList = FieldListModel.createModelProxy();
        theMethodList = MethodListModel.createModelProxy();
    }

    public static IDataClassModel createModelProxy() {
        IDataClassModel newModel = new DataClassModel();
        IDataClassModelWrapper proxy = null;
        try {
            proxy = (IDataClassModelWrapper)GuiModelProxy.newProxyInstance(newModel,"dataeditor.model.IDataClassModelWrapper");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return(IDataClassModel)proxy;
    }

    public IFieldListModel getFieldList() {
        return theFieldList;
    }

    public IMethodListModel getMethodList() {
        return theMethodList;
    }

    public void setName(String newName) {
        name = newName;
    }

    public String getName() {
        return name;
    }

    public HashSet getModifyMethods() {
        HashSet returnObj = new HashSet();
        returnObj.add("setName");
        returnObj.add("setParent");
        returnObj.add("setPackage");
        return returnObj;
    }

    public void setPackage(String packageIn) {
        packageName = packageIn;
    }

    public String getPackage() {
        return packageName;
    }

    public void setParent(String parentIn) {
        parent = parentIn;
    }

    public String getParent() {
        return parent;
    }

    public void dataGone() {
        WARNING.println("DATA HAS BEEN REMOVED !!");
    }

}
