package org.ofbiz.designer.newdesigner.LatticeEditor.model;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.generic.*;
import javax.swing.text.*;
import java.util.*;

public class RoleModel extends LatticeNodeModel implements IRoleModel {

    private RoleModel() {
        setPriveleges("");
    }

    public static ILatticeNodeModel createModelProxy() {
        IRoleModel newModel = new RoleModel();
        IRoleModelWrapper proxy = null;
        try {
            proxy = (IRoleModelWrapper)GuiModelProxy.newProxyInstance(newModel,"latticeeditor.model.IRoleModelWrapper");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return(ILatticeNodeModel)proxy;
    }

    public Object[][] getDataElements() {
        Object[][] superData = super.getDataElements();
        int numData = superData.length;
        Object[][] returnObj = new Object[numData+1][2];
        for(int i=0;i<numData;i++) {
            returnObj[i] = superData[i];
        }                              
        returnObj[numData][0]="priveleges";
        returnObj[numData][1]="java.lang.String";
        return returnObj;
    }

    public String getPriveleges() {
        return(String)getDataElement("priveleges");
    }

    public void setPriveleges(String privelegesIn) {
        setDataElement("priveleges",privelegesIn);                                      
    }

    protected static HashSet modifyMethods = new HashSet();
    static {
        modifyMethods.addAll(LatticeNodeModel.modifyMethods);
        modifyMethods.add("setPriveleges");
    }

    public HashSet getModifyMethods() {
        return modifyMethods;
    }

}
