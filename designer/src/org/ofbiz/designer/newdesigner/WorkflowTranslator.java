package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.networkdesign.*;
import org.ofbiz.designer.newdesigner.model.*;
import org.ofbiz.designer.util.*;
import java.util.*;
import org.ofbiz.designer.domainenv.*;

class WorkflowTranslator extends BaseTranslator {
    String domainURL = null;
    public WorkflowTranslator(INetworkEditorComponentModelWrapper model, INetworkTaskRealizationWrapper realization, String _domainURL, String mode) {
        super(model, realization);
        domainURL = _domainURL;
        synchronize(mode);
    }

    public void updateDataImpl() {
        String modelStartTask = model().getStartTask();
        String dataStartTask = workflow().getFirsttaskAttribute();
        if(modelStartTask != null && !modelStartTask.equals(dataStartTask))
            workflow().setFirsttaskAttribute(modelStartTask);
        String modelEndTask = model().getEndTask();
        String dataEndTask = workflow().getLasttaskAttribute();
        if(modelEndTask != null && !modelEndTask.equals(dataEndTask))
            workflow().setLasttaskAttribute(modelEndTask);
        synchronizeDataDomains();
    }

    public void updateModelImpl() {
        String modelType = model().getModelType();
        if(modelType == null || !modelType.equals(INetworkEditorComponentModel.WORKFLOWTYPE))
            model().setModelType(INetworkEditorComponentModel.WORKFLOWTYPE);

        String modelStartTask = model().getStartTask();
        String dataStartTask = workflow().getFirsttaskAttribute();
        if(dataStartTask != null && !dataStartTask.equals(modelStartTask))
            model().setStartTask(dataStartTask);
        String modelEndTask = model().getEndTask();
        String dataEndTask = workflow().getLasttaskAttribute();
        if(dataEndTask != null && !dataEndTask.equals(modelEndTask))
            model().setEndTask(dataEndTask);
        synchronizeModelDomains();
    }

    private void synchronizeModelDomains() {
        int dataChildCount = workflow().getDomainCount();
        int modelChildCount = model().getChildContainerCount();

        // remove domains that are in model but not in data
        for(int i=modelChildCount-1;i>=0; i--) {
            INetworkEditorComponentModel childModel = (INetworkEditorComponentModel)model().getChildContainerAt(i);
            if(childModel.getModelType().equals(INetworkEditorComponentModel.TASKTYPE)) continue;
            if(!containsDomain(childModel.getID())) model().removeChildContainer(childModel);
        }

        // add domains that are in data but not in model
        for(int i=0; i<dataChildCount; i++) {
            IDomainWrapper domain = (IDomainWrapper)workflow().getDomainAt(i);
            String domainID = domain.getIdAttribute();
            if(!containsChild(domainID)) {
                INetworkEditorComponentModelWrapper newModel = (INetworkEditorComponentModelWrapper)model().createChildContainer(domainID, INetworkEditorComponentModel.DOMAINTYPE);
                IDomainInfoWrapper domainInfo = (IDomainInfoWrapper)workflow().getXml().getHref(System.getProperty("WF_XMLDIR") + 
                "\\domainenv" +  domainURL);
                Vector vec = new Vector();
                vec.addElement(domain);
                vec.addElement(domainInfo);
                new DomainTranslator(newModel, vec, BaseTranslator.UPDATE_MODEL);
            }
        }
    }

    private void synchronizeDataDomains() {
        int dataChildCount = workflow().getDomainCount();
        int modelChildCount = model().getChildContainerCount();

        // remove domains that are in data but not in model
        for(int i=dataChildCount-1;i>=0; i--)
            if(!containsChild(workflow().getDomainAt(i).getIdAttribute())) workflow().removeDomainAt(i);

            // add domains that are in model but not in data
        for(int i=0; i<modelChildCount; i++) {
            INetworkEditorComponentModelWrapper newModel = (INetworkEditorComponentModelWrapper)model().getChildContainerAt(i);
            String domainID = newModel.getID();
            if(!containsDomain(domainID)) {
                IDomainWrapper domain = (IDomainWrapper)workflow().createDomain(domainID);
                //new DomainTranslator(newModel, domain, BaseTranslator.UPDATE_DATA);
                //IDomainInfoWrapper domainInfo = (IDomainInfoWrapper)workflow().getXml().getHref(domainURL);
                IDomainInfoWrapper domainInfo = (IDomainInfoWrapper)workflow().getXml().getHref(System.getProperty("WF_XMLDIR") + 
                "\\domainenv" +  domainURL);
                Vector vec = new Vector();
                vec.addElement(domain);
                vec.addElement(domainInfo);
                new DomainTranslator(newModel, vec, BaseTranslator.UPDATE_DATA);
            }
        }
    }

    private boolean containsDomain(String ID) {
        int dataChildCount = workflow().getDomainCount();
        for(int j=0; j<dataChildCount; j++) {
            String domainID = ((IDomainWrapper)workflow().getDomainAt(j)).getIdAttribute();
            if(match(ID, domainID)) return true;
        }
        return false;
    }

    private boolean containsChild(String ID) {
        int modelChildCount = model().getChildContainerCount();
        for(int j=0; j<modelChildCount; j++) {
            String modelID = ((INetworkEditorComponentModel)model().getChildContainerAt(j)).getID();
            if(match(modelID, ID)) return true;
        }
        return false;
    }

    private INetworkEditorComponentModel model() {
        return(INetworkEditorComponentModel)getGuiModel();
    }

    private INetworkTaskRealizationWrapper workflow() {
        return(INetworkTaskRealizationWrapper)getDataObjectAt(0);
    }

    private static boolean match(String x, String y) {
        return((x == null && y == null) ||
               (x != null && y != null && x.equals(y)));
    }
}
