package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.networkdesign.*;
import org.ofbiz.designer.newdesigner.model.*;
import org.ofbiz.designer.util.*;
import java.awt.*;
import java.util.*;
import java.lang.reflect.*;
import org.ofbiz.designer.generic.*;

public class TaskTranslator extends BaseTranslator {
    public TaskTranslator(INetworkEditorComponentModelWrapper model, ITaskWrapper field, String mode) {
        super(model, field);
        synchronize(mode);
    }

    public void updateDataImpl() {
        String modelName = model().getDisplayName();
        String dataName = task().getNameAttribute();
        //if (modelName != null && !modelName.equals(dataName)) {
        if(!match(modelName, dataName)) 
            task().setNameAttribute(modelName);


        String modelURL = model().getDomainURL();
        String dataURL = task().getSecuritydomainurlAttribute();
        if(modelURL != null && !modelURL.equals(dataURL))
            ((ITask)task()).setSecuritydomainurlAttribute(modelURL);


        synchronizeDataDimensions();

        String modelTaskType = model().getTaskType();
        String dataTaskType = task().getTaskType();
        if(modelTaskType != null && !modelTaskType.equals(dataTaskType))
            task().setTaskType(modelTaskType);

        //synchronizeDataRealization();
        synchronizeDataIncomingArcs();
        synchronizeDataOutgoingArcs();
    }

    public void updateModelImpl() {
        String modelName = model().getDisplayName();
        String dataName = task().getNameAttribute();
        //if (dataName != null && !dataName.equals(modelName))
        if(!match(modelName, dataName))
            model().setDisplayName(dataName);

        String modelURL = model().getDomainURL();
        String dataURL = task().getSecuritydomainurlAttribute();
        if(!match(modelURL, dataURL))
            model().setDomainURL(dataURL);

        synchronizeModelDimensions();

        String modelTaskType = model().getTaskType();
        String dataTaskType = task().getTaskType();
        if(!match(modelTaskType, dataTaskType))
            model().setTaskType(dataTaskType);

        synchronizeModelIncomingArcs();
        synchronizeModelOutgoingArcs();
    }

    private boolean containsInArcData(String ID) {
        String[] inarcsArr = IDRefHelper.getReferenceArray(task().getInarcsAttribute());
        for(int j=0; j<inarcsArr.length; j++)
            if(match(ID, inarcsArr[j])) return true;
        return false;
    }

    private ITaskWrapper task() {
        return(ITaskWrapper)getDataObject();
    }

    private INetworkEditorComponentModel model() {
        return(INetworkEditorComponentModel)getGuiModel();
    }

    private boolean containsOutArcData(String ID) {
        String[] outarcsArr = IDRefHelper.getReferenceArray(task().getOutarcsAttribute());
        for(int j=0; j<outarcsArr.length; j++)
            if(match(ID, outarcsArr[j])) return true;
        return false;
    }

    private boolean containsInArcModel(String ID) {
        int modelCount = model().getIncomingArcCount();
        for(int j=0; j<modelCount; j++)
            if(match(ID, model().getIncomingArcAt(j).getID())) return true;
        return false;
    }

    private boolean containsOutArcModel(String ID) {
        int modelCount = model().getOutgoingArcCount();
        for(int j=0; j<modelCount; j++)
            if(match(ID, model().getOutgoingArcAt(j).getID())) return true;
        return false;
    }

    private void synchronizeModelDimensions() {
        Rectangle modelBounds = model().getBounds();
        int x = (task().getXAttribute()==null)?-1: Integer.parseInt(task().getXAttribute());
        int y = (task().getYAttribute()==null)?-1: Integer.parseInt(task().getYAttribute());

        Rectangle dataBounds = new Rectangle(x, y, modelBounds.width, modelBounds.height);
        if(!dataBounds.equals(modelBounds))
            model().setBounds(dataBounds);
    }

    private void synchronizeDataDimensions() {
        Rectangle modelBounds = model().getBounds();
        int x = (task().getXAttribute()==null)?-1: Integer.parseInt(task().getXAttribute());
        int y = (task().getYAttribute()==null)?-1: Integer.parseInt(task().getYAttribute());

        if(x != modelBounds.x) task().setXAttribute("" + modelBounds.x);
        if(y != modelBounds.y) task().setYAttribute("" + modelBounds.y);
    }

    private void synchronizeModelIncomingArcs() {
        String[] arcs = IDRefHelper.getReferenceArray(task().getInarcsAttribute());
        int dataCount = arcs.length;
        int modelCount = model().getIncomingArcCount();

        // remove inarcs that are in model but not in data
        for(int i=modelCount-1;i>=0; i--) {
            IArcModel childModel = (IArcModel)model().getIncomingArcAt(i);
            if(!containsInArcData(childModel.getID())){
                LOG.println("&&&&&&&&&&&&&&&&&&& SETTING ARC DESTINATION NULL");
                childModel.setDestination(null);
                //model().removeIncomingArc(childModel);
            }
        }

        // add inarcs that are in data but not in model
        for(int i=0; i<dataCount; i++) {
            if(!containsInArcModel(arcs[i])) {
                IArcWrapper arc = (IArcWrapper)task().getXml().getIdRef(arcs[i]);
                IArcModelWrapper arcModel = null;
                if(arc.getArctypeAttribute().equals("Fail"))
                    arcModel = (IArcModelWrapper)model().createIncomingArc(arc.getSourceAttribute(), arcs[i], arcModel.DASHED);
                else
                    arcModel = (IArcModelWrapper)model().createIncomingArc(arc.getSourceAttribute(), arcs[i], null);
                //new ArcTranslator(arcModel, arc, BaseTranslator.UPDATE_MODEL);
            }
        }
    }

    private INetworkDesignWrapper context(){
        return (INetworkDesignWrapper)task().getXml().getRoot();
    }

    private void synchronizeDataIncomingArcs() {
        String[] arcs = IDRefHelper.getReferenceArray(task().getInarcsAttribute());
        int dataCount = arcs.length;
        int modelCount = model().getIncomingArcCount();

        // remove inarcs that are in data but not in model
        Vector temp = new Vector();
        for(int i=dataCount-1;i>=0; i--)
            if(!containsInArcModel(arcs[i]))
                temp.addElement(arcs[i]);

        for(int i=0; i<temp.size(); i++) {
            IArc arc = (IArc)task().getXml().getIdRef((String)temp.elementAt(i));
            context().removeArc(arc);
        }


        // add inarcs that are in model but not in data
        String additionalArcs = " ";
        for(int i=0; i<modelCount; i++) {
            IArcModelWrapper arcModel = (IArcModelWrapper)model().getIncomingArcAt(i);
            String modelID = arcModel.getID();
            if(!containsInArcData(modelID)) {
                if(arcModel.getSource() == null) continue;
                IArcWrapper newArc = (IArcWrapper)context().getXml().getIdRef(modelID);
                if(newArc == null) {
                    if(arcModel.DASHED.equals(arcModel.getLineStyle()))
                        newArc = (IArcWrapper)context().createArc(modelID, "Fail", ((INetworkEditorComponentModel)arcModel.getSource()).getID(), task().getIdAttribute());
                    else if(arcModel.getSource() instanceof INetworkEditorComponentModel)
                        newArc = (IArcWrapper)context().createArc(modelID, "Success", ((INetworkEditorComponentModel)arcModel.getSource()).getID(), task().getIdAttribute());
                    else {
                        String sourceID = ((IArcModel)arcModel.getSource()).getID();
                        IArcWrapper sourceArc = (IArcWrapper)task().getXml().getIdRef(sourceID);
                        newArc = (IArcWrapper)context().createArc(modelID, "Alternative", sourceID, task().getIdAttribute());
                        context().getXml().setIdRef(modelID, newArc);
                        sourceArc.setAlternativetransitionAttribute(modelID);
                    }
                    context().getXml().setIdRef(modelID, newArc);
                }
                //new ArcTranslator(arcModel, newArc, BaseTranslator.UPDATE_DATA);
                additionalArcs += modelID + " ";
            }
        }

        if(additionalArcs.trim().length() != 0) {
            String tmp = task().getInarcsAttribute();
            if(tmp == null) task().setInarcsAttribute(additionalArcs);
            else task().setInarcsAttribute(tmp+additionalArcs);
        }
    }

    private void synchronizeModelOutgoingArcs() {
        String[] arcs = IDRefHelper.getReferenceArray(task().getOutarcsAttribute());
        int dataCount = arcs.length;
        int modelCount = model().getOutgoingArcCount();

        // remove outarcs that are in model but not in data
        for(int i=modelCount-1;i>=0; i--) {
            IArcModel childModel = (IArcModel)model().getOutgoingArcAt(i);
            if(!containsOutArcData(childModel.getID())){
                LOG.println("&&&&&&&&&&&&&&&&&&& SETTING ARC SOURCE NULL");
                childModel.setSource(null);
                //model().removeOutgoingArc(childModel);
            }
        }

        // add outarcs that are in data but not in model
        for(int i=0; i<dataCount; i++) {
            if(!containsOutArcModel(arcs[i])) {
                IArcWrapper arc = (IArcWrapper)task().getXml().getIdRef(arcs[i]);
                IArcModelWrapper arcModel = null;
                if(arc.getArctypeAttribute().equals("Fail"))
                    arcModel = (IArcModelWrapper)model().createOutgoingArc(arc.getDestinationAttribute(), arcs[i], arcModel.DASHED);
                else 
                    arcModel = (IArcModelWrapper)model().createOutgoingArc(arc.getDestinationAttribute(), arcs[i], null);
                if(arc.getAlternativetransitionAttribute() != null) {
                    IArcWrapper altArc = (IArcWrapper)task().getXml().getIdRef(arc.getAlternativetransitionAttribute());
                    arcModel.createOutgoingArc(altArc.getDestinationAttribute(), arcs[i], arc.getAlternativetransitionAttribute());
                }
                //new ArcTranslator(arcModel, arc, BaseTranslator.UPDATE_MODEL);
            }
        }
    }

    private void synchronizeDataOutgoingArcs() {
        
        String[] arcs = IDRefHelper.getReferenceArray(task().getOutarcsAttribute());
        int dataCount = arcs.length;
        int modelCount = model().getOutgoingArcCount();

        // remove outarcs that are in data but not in model
        Vector temp = new Vector();
        for(int i=dataCount-1;i>=0; i--)
            if(!containsOutArcModel(arcs[i]))
                temp.addElement(arcs[i]);

        for(int i=0; i<temp.size(); i++) {
            IArc arc = (IArc)task().getXml().getIdRef((String)temp.elementAt(i));
            context().removeArc(arc);
        }

        // add outarcs that are in model but not in data
        String additionalArcs = " ";
        for(int i=0; i<modelCount; i++) {
            IArcModelWrapper arcModel = (IArcModelWrapper)model().getOutgoingArcAt(i);
            String modelID = arcModel.getID();
            if(!containsOutArcData(modelID)) {
                if(arcModel.getDestination() == null) continue;
                IArcWrapper newArc = null;
                newArc = (IArcWrapper)context().getXml().getIdRef(modelID);
                if(newArc == null) {
                    String type = "Success";
                    if(arcModel.DASHED.equals(arcModel.getLineStyle())) type = "Fail";
                    newArc = (IArcWrapper)context().createArc(modelID, type, task().getIdAttribute(), ((INetworkEditorComponentModel)arcModel.getDestination()).getID());
                }
                context().getXml().setIdRef(modelID, newArc);
                additionalArcs += modelID + " ";
            }
        }

        if(additionalArcs.trim().length() != 0) {
            String tmp = task().getOutarcsAttribute();
            if(tmp == null)
                task().setOutarcsAttribute(additionalArcs);
            else task().setOutarcsAttribute(tmp+additionalArcs);
        }
    }

    private static boolean match(String x, String y) {
        return((x == null && y == null) || (x != null && y != null && x.equals(y)));
    }
}
