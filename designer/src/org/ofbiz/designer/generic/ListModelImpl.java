package org.ofbiz.designer.generic;

import org.ofbiz.designer.pattern.*;
import java.util.*;
import javax.swing.*;
import org.ofbiz.designer.util.*;

public class ListModelImpl extends DefaultListModel implements IListModel, IGuiModel {
    private IView view;

    public HashSet getModifyMethods() {
        HashSet methods = new HashSet();
        methods.add("remove");
        methods.add("addElement");
        methods.add("insertElementAt");
        return methods;
    }

    private ListModelImpl() {
    }

    public static IListWrapper createModelProxy() {
        ListModelImpl model = new ListModelImpl();
        return(IListWrapper)GuiModelProxy.newProxyInstance(model, "org.ofbiz.designer.generic.IListWrapper");
    }

    public void synchronizeGui() {
        if(view != null)
            view.synchronize();
    }

    public void setGui(IView viewIn) {
        if(!equals(viewIn.getModel()))
            throw new RuntimeException("The model of the view " + viewIn.getModel() + " does not equal " +  this);
        view = viewIn;
    }

    public void dataGone() {
        WARNING.println("DATA HAS BEEN REMOVED !!");
    }
}
