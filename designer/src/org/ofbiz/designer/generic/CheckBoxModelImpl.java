package org.ofbiz.designer.generic;

import javax.swing.text.*;
import org.ofbiz.designer.pattern.*;
import java.util.*;
import javax.swing.*;
import org.ofbiz.designer.util.*;

public class CheckBoxModelImpl extends DefaultButtonModel implements ICheckBoxModel, IGuiModel {
//public class CheckBoxModelImpl implements ICheckBoxModel, IGuiModel{
    private IView view;

    public HashSet getModifyMethods() {
        HashSet methods = new HashSet();
        methods.add("setSelected");
        return methods;
    }

    private CheckBoxModelImpl() {
    }

    public static ICheckBoxWrapper createModelProxy() {
        CheckBoxModelImpl model = new CheckBoxModelImpl();
        return(ICheckBoxWrapper)GuiModelProxy.newProxyInstance(model, "org.ofbiz.designer.generic.ICheckBoxWrapper");
    }

    public void synchronizeGui() {
        if(view != null)
            view.synchronize();
    }

    public void setGui(IView viewIn) {
        view = viewIn;
    }

    public void dataGone() {
        WARNING.println("DATA HAS BEEN REMOVED !!");
    }
}