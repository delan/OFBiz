package org.ofbiz.designer.generic;

import javax.swing.text.*;
import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.util.*;

public class PlainDocumentModel extends PlainDocument implements IDocument {
    private IView view;

    public HashSet getModifyMethods() {
        HashSet methods = new HashSet();
        methods.add("remove");
        methods.add("insertString");
        return methods;
    }

    private PlainDocumentModel() {
    }

    public static IDocumentWrapper createModelProxy() {
        PlainDocumentModel model = new PlainDocumentModel();
        return(IDocumentWrapper)GuiModelProxy.newProxyInstance(model, "org.ofbiz.designer.generic.IDocumentWrapper");
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