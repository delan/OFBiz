package org.ofbiz.designer.newdesigner.LatticeEditor;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.roledomain.*;
import java.io.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeModel;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeNodeModel;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeModelWrapper;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.RoleDomainModel;

public class RoleDomainView extends LatticeView {
    protected RoleDialog nodeDialog;

    private static final String XML_DIR = System.getProperty("WF_XMLDIR");
    private static final String DTD_DIR = System.getProperty("WF_DTDDIR");
    static {
		LOG.println("XML_DIR is " + XML_DIR);
		LOG.println("DTD_DIR is " + DTD_DIR);
    }


    static {
        DATA_DIR = DATA_DIR = XML_DIR + "/roledomain/";
        ADD_BUTTON_NAME = "New Role";
        TITLE = "Role Editor";
        ROOT_XML_TAG = "RoleDomain";
        DTD_FILE = new File(DTD_DIR+"RoleDomain.dtd");
        DEFAULT_XML = new File(DATA_DIR+"DefaultRoleDomain.xml");
    }
    
    public RoleDomainView() {
    }

    public static void main(String args[]) {
        RoleDomainView roleView = new RoleDomainView();
        System.out.println(DEFAULT_XML);
        XmlWrapper newXml = XmlWrapper.openDocument(DEFAULT_XML);
        roleView.setXML(newXml);

        ILatticeModel roleModel = roleView.getNewModel();
        roleView.createAppropriateTranslator(roleModel, BaseTranslator.UPDATE_MODEL);
        roleView.setModel(roleModel);

    }

    public void createAppropriateTranslator(ILatticeModel newModel, String initialTranslationDirection) {
        new RoleDomainTranslator((ILatticeModelWrapper) newModel,(IRoleDomainWrapper)theXml.getRoot(),initialTranslationDirection);
    }

    public String getIdFromXml() {
        return((IRoleDomainWrapper)theXml.getRoot()).getIdAttribute();
    }

    public ILatticeModel getNewModel() {
        return RoleDomainModel.createModelProxy();
    }

    protected void createLatticeNodeViews() {
        ILatticeNodeModel currNode;
        LatticeNodeView newView;

        for(int i=0; i<theModel.getLatticeNodeCount(); i++) {
            currNode = (ILatticeNodeModel)theModel.getLatticeNodeAt(i);
            newView = new LatticeNodeView(currNode, workspace);
            workspace.add(newView);
        }
    }

    protected void loseTranslator() {
        ((ILatticeModelWrapper)theModel).getTranslator().close();
        ((ILatticeModelWrapper)theModel).setTranslator(null);
    }

    public void doNodePropertyEdit(ILatticeNodeModel modelIn) {
        nodeDialog.activate(modelIn);
    }

    protected void initNodeDialog() {
        nodeDialog = new RoleDialog(this);
    }
}