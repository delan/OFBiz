package org.ofbiz.designer.generic;

import javax.swing.*;
import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.util.*;

public class ComboBoxModelImpl extends DefaultComboBoxModel implements IComboBoxModel, IGuiModel {
    private IView view;

    public HashSet getModifyMethods() {
        HashSet methods = new HashSet();
        methods.add("removeElement");
        methods.add("removeElementAt");
        methods.add("setSelectedItem");
        return methods;
    }

    public void removeAllElements() {
        while(getSize() > 0)
            removeElementAt(0);
    }

    private ComboBoxModelImpl() {
    }

    public static IComboBoxWrapper createModelProxy() {
        ComboBoxModelImpl model = new ComboBoxModelImpl();
        return(IComboBoxWrapper)GuiModelProxy.newProxyInstance(model, "org.ofbiz.designer.generic.IComboBoxWrapper");
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
