package org.ofbiz.designer.newdesigner.LatticeEditor;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.domainenv.*;
import java.io.*;
import java.util.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.*;


public class DomainEnvView extends LatticeView {
    LatticeToolButton policyTool;
    LatticeNodeDialog nodeDialog;

    private static final String XML_DIR = System.getProperty("WF_XMLDIR");
    private static final String DTD_DIR = System.getProperty("WF_DTDDIR");
    static {
		LOG.println("XML_DIR is " + XML_DIR);
		LOG.println("DTD_DIR is " + DTD_DIR);
    }

    static {
        DATA_DIR = DATA_DIR = XML_DIR + "/org.ofbiz.designer.domainenv/";
        ADD_BUTTON_NAME = "New Domain";
        TITLE = "Domain Editor";
        ROOT_XML_TAG = "DomainEnv";
        DTD_FILE = new File(DTD_DIR+"DomainEnv.dtd");
        DEFAULT_XML = new File(DATA_DIR+"DefaultDomain.xml");
    }

                                    
    public DomainEnvView() {
        policyTool = new LatticeToolButton("Policy");
        mainToolBar.add(policyTool);
    }



    public static void main(String args[]) {
        DomainEnvView domainView = new DomainEnvView();
        XmlWrapper newXml = XmlWrapper.openDocument(DEFAULT_XML);
        domainView.setXML(newXml);

        ILatticeModel domainModel = domainView.getNewModel();
        domainView.createAppropriateTranslator(domainModel,BaseTranslator.UPDATE_MODEL);
        domainView.setModel(domainModel);
    }

    public void createAppropriateTranslator(ILatticeModel newModel, String initialTranslationDirection) {
        new DomainEnvTranslator((IDomainEnvModelWrapper) newModel,(IDomainEnvWrapper)theXml.getRoot(),initialTranslationDirection);
    }

    public String getIdFromXml() {
        return((IDomainEnvWrapper)theXml.getRoot()).getIdAttribute();
    }

    public ILatticeModel getNewModel() {
        return DomainEnvModel.createModelProxy();
    }

    public LatticeToolButton getPolicyTool() {
        return policyTool;
    }

    public void doPolicyEdit(LatticeNodeView fromDView, LatticeNodeView toDView) {
        IDomainModel fromDomain = (IDomainModel)fromDView.getModel();
        IDomainModel toDomain = (IDomainModel)toDView.getModel();

        String sendId = fromDomain.getId()+toDomain.getId()+"Send";
        String receiveId = fromDomain.getId()+toDomain.getId()+"Receive";

        PolicyModelContainer sendModel = null;
        PolicyModelContainer receiveModel = null;

        Hashtable policyHash = ((IDomainEnvModel)theModel).policiesHashedByFromToType();

        Object[] keys = policyHash.keySet().toArray();

        if(!(policyHash.containsKey(sendId))) {
            sendModel = ((IDomainEnvModel)theModel).addPolicy(fromDomain,toDomain,"Send");
        } else
            sendModel = (PolicyModelContainer)policyHash.get(sendId);

        if(!(policyHash.containsKey(receiveId))) {
            receiveModel = ((IDomainEnvModel)theModel).addPolicy(fromDomain,toDomain,"Receive");
        } else
            receiveModel = (PolicyModelContainer)policyHash.get(receiveId);

        PolicyDialog policyDialog = new PolicyDialog(this);
        policyDialog.activate(sendModel,receiveModel);

    }

    protected void createLatticeNodeViews() {
        IDomainModel currDomain;
        DomainView newView;

        for(int i=0; i<theModel.getLatticeNodeCount(); i++) {
            currDomain = (IDomainModel)theModel.getLatticeNodeAt(i);
            newView = new DomainView(currDomain, workspace);
            workspace.add(newView);
        }

    }

    protected void loseTranslator() {
        ((IDomainEnvModelWrapper)theModel).getTranslator().close();
        ((IDomainEnvModelWrapper)theModel).setTranslator(null);
    }

    public void doNodePropertyEdit(ILatticeNodeModel modelIn) {
        nodeDialog.activate(modelIn);
    }

    protected void initNodeDialog() {
        nodeDialog = new LatticeNodeDialog(this);
    }


}

