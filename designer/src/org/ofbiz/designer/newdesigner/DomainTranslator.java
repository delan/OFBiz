/**		PL_STARTSKIP									
 *		DO NOT MODIFY COMMENTS 
 *
 * 		Changes made to this file needs to be replicated to CompartmentTranslator
 *		via script
 * 
 *		PL_STOPSKIP */


package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.networkdesign.*;
import org.ofbiz.designer.newdesigner.model.*;
import org.ofbiz.designer.util.*;
import java.awt.*;
import java.net.*;
import org.ofbiz.designer.domainenv.*;
import java.util.*;
import org.ofbiz.designer.generic.*;

class DomainTranslator extends BaseTranslator {
    //public DomainTranslator(INetworkEditorComponentModelWrapper model, IDomainWrapper domainWrapper, String mode) {
    public DomainTranslator(INetworkEditorComponentModelWrapper model, Vector vec, String mode) {
        //super(model, domainWrapper);
        super(model, vec);
        if(!(vec.elementAt(0) instanceof IDomainWrapper) || !(vec.elementAt(1) instanceof IDomainInfoWrapper) )
            throw new RuntimeException("INVALID DATA TYPES");
        synchronize(mode);
    }

    public void updateDataImpl() {
        // PL_STARTSKIP
        // synchronize urls
        String modelURL = model().getDomainURL();
        String dataURL = domain().getUrlAttribute();
        if (!match(modelURL, dataURL))
            ((IDomain)domain()).setUrlAttribute(modelURL);
        // PL_STOPSKIP
        synchronizeDataDimensions();
        synchronizeDataCompartments();
        synchronizeDataTasks();
    }

    public void updateModelImpl() {
        synchronizeModelDimensions();
        // PL_STARTSKIP
        // synchronize urls
        String modelURL = model().getDomainURL();
        String dataURL = domain().getUrlAttribute();
        if (!match(modelURL, dataURL)) {
            model().setDomainURL(dataURL);
            if(dataURL != null) {
                int index = dataURL.indexOf("#");
                dataURL = dataURL.substring(index+1, dataURL.length());
                model().setDisplayName(dataURL);
            }
        }

        // synchronize colors
        java.awt.Color modelColor = model().getColor();
        java.awt.Color dataColor = null;
        try {
            // David: crash here ...
            //dataColor = domainInfo().getAWTColor();
            //if (!dataColor.equals(modelColor)) model().setColor(dataColor);
        } catch (NullPointerException e) {}


        // PL_STOPSKIP
        synchronizeModelCompartments();
        synchronizeModelTasks();
    }

    private void synchronizeModelDimensions() {
        Rectangle modelBounds = model().getBounds();
        int x = (domain().getXAttribute()==null)?-1: Integer.parseInt(domain().getXAttribute());
        int y = (domain().getYAttribute()==null)?-1: Integer.parseInt(domain().getYAttribute());
        int width = (domain().getWidthAttribute()==null)?-1: Integer.parseInt(domain().getWidthAttribute());
        int height = (domain().getHeightAttribute()==null)?-1: Integer.parseInt(domain().getHeightAttribute());

        Rectangle dataBounds = new Rectangle(x, y, width, height);
        if (!dataBounds.equals(modelBounds))
            model().setBounds(dataBounds);
    }

    private void synchronizeDataDimensions() {
        Rectangle modelBounds = model().getBounds();
        int x = (domain().getXAttribute()==null)?-1: Integer.parseInt(domain().getXAttribute());
        int y = (domain().getYAttribute()==null)?-1: Integer.parseInt(domain().getYAttribute());
        int width = (domain().getWidthAttribute()==null)?-1: Integer.parseInt(domain().getWidthAttribute());
        int height = (domain().getHeightAttribute()==null)?-1: Integer.parseInt(domain().getHeightAttribute());

        if (x != modelBounds.x) domain().setXAttribute("" + modelBounds.x);
        if (y != modelBounds.y) domain().setYAttribute("" + modelBounds.y);
        if (width != modelBounds.width) domain().setWidthAttribute("" + modelBounds.width);
        if (height != modelBounds.height) domain().setHeightAttribute("" + modelBounds.height);
    }

    private void synchronizeModelTasks() {
        String[] tasksArr = IDRefHelper.getReferenceArray(domain().getTasksAttribute());
        int dataChildCount = tasksArr.length;
        int modelChildCount = model().getChildContainerCount();

        // remove tasks that are in model but not in data
        for (int i=modelChildCount-1;i>=0; i--) {
            INetworkEditorComponentModel childModel = (INetworkEditorComponentModel)model().getChildContainerAt(i);
            if (childModel.getModelType().equals(INetworkEditorComponentModel.COMPARTMENTTYPE)) continue;
            if (!containsTask(childModel.getID())) model().removeChildContainer(childModel);
        }

        // add tasks that are in data but not in model
        for (int i=0; i<dataChildCount; i++) {
            String taskID = tasksArr[i];
            if (!containsChild(taskID)) {
                INetworkEditorComponentModelWrapper newModel = (INetworkEditorComponentModelWrapper)model().createChildContainer(taskID, INetworkEditorComponentModel.TASKTYPE);
                ITaskWrapper task = (ITaskWrapper)domain().getXml().getIdRef(taskID);
                new TaskTranslator(newModel, task, BaseTranslator.UPDATE_MODEL);
            }
        }       
    }

    private void synchronizeDataTasks() {
        String[] tasksArr = IDRefHelper.getReferenceArray(domain().getTasksAttribute());
        int dataChildCount = tasksArr.length;
        int modelChildCount = model().getChildContainerCount();

        // remove tasks that are in data but not in model
        String newTasks = "";
        for (int i=dataChildCount-1;i>=0; i--)
            if (!containsChild(tasksArr[i])) {
                IContainerModel childModel = model().getChildContainerByName(tasksArr[i]);
                if (model().getDeletedChildren().contains(tasksArr[i])) {
                    INetworkDesignWrapper context = (INetworkDesignWrapper)domain().getXml().getRoot();
                    ITask task = (ITask)domain().getXml().getIdRef(tasksArr[i]);
                    context.removeTask(task);
                    model().getDeletedChildren().remove(childModel);
                }
            } else newTasks += " " + tasksArr[i];

        if (newTasks.trim().length() == 0)
            newTasks = null;

        if (!IDRefHelper.idrefsMatch(domain().getTasksAttribute(), newTasks)) {
            if (newTasks == null) domain().removeTasksAttribute();
            else domain().setTasksAttribute(newTasks);
        }

        // add tasks that are in model but not in data
        String additionalTasks = "";
        for (int i=0; i<modelChildCount; i++) {
            INetworkEditorComponentModelWrapper child = (INetworkEditorComponentModelWrapper)model().getChildContainerAt(i);
            if (child.COMPARTMENTTYPE.equals(child.getModelType())) continue;
            String taskID = child.getID();
            if (!containsTask(taskID)) {
                INetworkDesignWrapper context = (INetworkDesignWrapper)domain().getXml().getRoot();
                ITaskWrapper task = (ITaskWrapper)domain().getXml().getIdRef(taskID);
                if (task == null) {
                    task = (ITaskWrapper)context.createTask(taskID);
                    //domain().getXml().setIdRef(taskID, org.ofbiz.designer.task);
                }
                new TaskTranslator(child, task, BaseTranslator.UPDATE_DATA);
                additionalTasks += " " + taskID;
            }
        }
        if (additionalTasks.trim().length() != 0) {
            String existing = domain().getTasksAttribute();
            if (existing == null) domain().setTasksAttribute(additionalTasks);
            else domain().setTasksAttribute(existing + additionalTasks);
        }

    }

    private void synchronizeModelCompartments() {
        int dataChildCount = domain().getCompartmentCount();
        int modelChildCount = model().getChildContainerCount();

        // remove compartments that are in model but not in data
        for (int i=modelChildCount-1;i>=0; i--) {
            INetworkEditorComponentModel childModel = (INetworkEditorComponentModel)model().getChildContainerAt(i);
            if (childModel.getModelType().equals(INetworkEditorComponentModel.TASKTYPE)) continue;
            if (!containsCompartment(childModel.getID())) model().removeChildContainer(childModel);
        }

        // add compartments that are in data but not in model
        for (int i=0; i<dataChildCount; i++) {
            ICompartmentWrapper compartment = (ICompartmentWrapper)domain().getCompartmentAt(i);
            String compartmentID = compartment.getIdAttribute();
            if (!containsChild(compartmentID)) {
                INetworkEditorComponentModelWrapper newModel = (INetworkEditorComponentModelWrapper)model().createChildContainer(compartmentID, INetworkEditorComponentModel.COMPARTMENTTYPE);
                new CompartmentTranslator(newModel, compartment, BaseTranslator.UPDATE_MODEL);
            }
        }
    }

    private void synchronizeDataCompartments() {
        int dataChildCount = domain().getCompartmentCount();
        int modelChildCount = model().getChildContainerCount();

        // remove compartments that are in data but not in model
        for (int i=dataChildCount-1;i>=0; i--)
            if (!containsChild(domain().getCompartmentAt(i).getIdAttribute())) domain().removeCompartmentAt(i);

            // add compartments that are in model but not in data
        for (int i=0; i<modelChildCount; i++) {
            INetworkEditorComponentModelWrapper compartment = (INetworkEditorComponentModelWrapper)model().getChildContainerAt(i);
            if (INetworkEditorComponentModel.TASKTYPE.equals(compartment.getModelType())) continue;
            String compartmentID = compartment.getID();
            if (!containsCompartment(compartmentID)) {
                ICompartmentWrapper iComp = (ICompartmentWrapper)domain().createCompartment(compartmentID);
                if (compartment.getTranslator() != null) {
                    compartment.getTranslator().close();
                    compartment.setTranslator(null);
                }
                new CompartmentTranslator(compartment, iComp, BaseTranslator.UPDATE_DATA);
            }
        }
    }

    private boolean containsCompartment(String ID) {
        int dataChildCount = domain().getCompartmentCount();
        for (int j=0; j<dataChildCount; j++) {
            String compartmentID = ((ICompartmentWrapper)domain().getCompartmentAt(j)).getIdAttribute();
            if (match(ID, compartmentID)) return true;
        }
        return false;
    }

    private boolean containsTask(String ID) {
        String[] tasks = IDRefHelper.getReferenceArray(domain().getTasksAttribute());
        for (int j=0; j<tasks.length; j++)
            if (match(ID, tasks[j])) return true;
        return false;
    }

    private boolean containsChild(String ID) {
        int modelChildCount = model().getChildContainerCount();
        for (int j=0; j<modelChildCount; j++) {
            String modelID = ((INetworkEditorComponentModel)model().getChildContainerAt(j)).getID();
            if (match(modelID, ID)) return true;
        }
        return false;
    }

    public INetworkEditorComponentModel model() {
        return(INetworkEditorComponentModel)getGuiModel();
    }

    public IDomainWrapper domain() {
        return(IDomainWrapper)getDataObjectAt(0);
    }

    //PL_STARTSKIP
    public IDomainInfoWrapper domainInfo() {
        String url = domain().getUrlAttribute();
        if(url == null) 
            return null;
        url = XmlWrapper.XMLDIR + "\\domainenv" + url;
        return(IDomainInfoWrapper)domain().getXml().getHref(url);
    }
    //PL_STOPSKIP

    private static boolean match(String x, String y) {
        return((x == null && y == null) ||
               (x != null && y != null && x.equals(y)));
    }
}
