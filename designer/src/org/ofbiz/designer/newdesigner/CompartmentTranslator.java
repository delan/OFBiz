//WARNING !! DO NOT MODIFY THIS FILE
//THIS FILE IS GENERATED VIA SCRIPT
//MODIFY c:\workflow\org.ofbiz.designer.newdesigner\src\DomainTranslator.java INSTEAD




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

class CompartmentTranslator extends BaseTranslator {
    public CompartmentTranslator(INetworkEditorComponentModelWrapper model, ICompartmentWrapper compartmentWrapper, String mode) {
        super(model, compartmentWrapper);
        synchronize(mode);
    }

    public void updateDataImpl() {
        synchronizeDataDimensions();
        synchronizeDataCompartments();
        synchronizeDataTasks();
    }

    public void updateModelImpl() {
        synchronizeModelDimensions();
        synchronizeModelCompartments();
        synchronizeModelTasks();
    }

    private void synchronizeModelDimensions() {
        Rectangle modelBounds = model().getBounds();
        int x = (compartment().getXAttribute()==null)?-1: Integer.parseInt(compartment().getXAttribute());
        int y = (compartment().getYAttribute()==null)?-1: Integer.parseInt(compartment().getYAttribute());
        int width = (compartment().getWidthAttribute()==null)?-1: Integer.parseInt(compartment().getWidthAttribute());
        int height = (compartment().getHeightAttribute()==null)?-1: Integer.parseInt(compartment().getHeightAttribute());

        Rectangle dataBounds = new Rectangle(x, y, width, height);
        if (!dataBounds.equals(modelBounds))
            model().setBounds(dataBounds);
    }

    private void synchronizeDataDimensions() {
        Rectangle modelBounds = model().getBounds();
        int x = (compartment().getXAttribute()==null)?-1: Integer.parseInt(compartment().getXAttribute());
        int y = (compartment().getYAttribute()==null)?-1: Integer.parseInt(compartment().getYAttribute());
        int width = (compartment().getWidthAttribute()==null)?-1: Integer.parseInt(compartment().getWidthAttribute());
        int height = (compartment().getHeightAttribute()==null)?-1: Integer.parseInt(compartment().getHeightAttribute());

        if (x != modelBounds.x) compartment().setXAttribute("" + modelBounds.x);
        if (y != modelBounds.y) compartment().setYAttribute("" + modelBounds.y);
        if (width != modelBounds.width) compartment().setWidthAttribute("" + modelBounds.width);
        if (height != modelBounds.height) compartment().setHeightAttribute("" + modelBounds.height);
    }

    private void synchronizeModelTasks() {
        String[] tasksArr = IDRefHelper.getReferenceArray(compartment().getTasksAttribute());
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
                ITaskWrapper task = (ITaskWrapper)compartment().getXml().getIdRef(taskID);
                new TaskTranslator(newModel, task, BaseTranslator.UPDATE_MODEL);
            }
        }       
    }

    private void synchronizeDataTasks() {
        String[] tasksArr = IDRefHelper.getReferenceArray(compartment().getTasksAttribute());
        int dataChildCount = tasksArr.length;
        int modelChildCount = model().getChildContainerCount();

        // remove tasks that are in data but not in model
        String newTasks = "";
        for (int i=dataChildCount-1;i>=0; i--)
            if (!containsChild(tasksArr[i])) {
                IContainerModel childModel = model().getChildContainerByName(tasksArr[i]);
                if (model().getDeletedChildren().contains(tasksArr[i])) {
                    INetworkDesignWrapper context = (INetworkDesignWrapper)compartment().getXml().getRoot();
                    ITask task = (ITask)compartment().getXml().getIdRef(tasksArr[i]);
                    context.removeTask(task);
                    model().getDeletedChildren().remove(childModel);
                }
            } else newTasks += " " + tasksArr[i];

        if (newTasks.trim().length() == 0)
            newTasks = null;

        if (!IDRefHelper.idrefsMatch(compartment().getTasksAttribute(), newTasks)) {
            if (newTasks == null) compartment().removeTasksAttribute();
            else compartment().setTasksAttribute(newTasks);
        }

        // add tasks that are in model but not in data
        String additionalTasks = "";
        for (int i=0; i<modelChildCount; i++) {
            INetworkEditorComponentModelWrapper child = (INetworkEditorComponentModelWrapper)model().getChildContainerAt(i);
            if (child.COMPARTMENTTYPE.equals(child.getModelType())) continue;
            String taskID = child.getID();
            if (!containsTask(taskID)) {
                INetworkDesignWrapper context = (INetworkDesignWrapper)compartment().getXml().getRoot();
                ITaskWrapper task = (ITaskWrapper)compartment().getXml().getIdRef(taskID);
                if (task == null) {
                    task = (ITaskWrapper)context.createTask(taskID);
                    //compartment().getXml().setIdRef(taskID, org.ofbiz.designer.task);
                }
                LOG.println("creating a new org.ofbiz.designer.task translator");
                new TaskTranslator(child, task, BaseTranslator.UPDATE_DATA);
                additionalTasks += " " + taskID;
            }
        }
        if (additionalTasks.trim().length() != 0) {
            String existing = compartment().getTasksAttribute();
            if (existing == null) compartment().setTasksAttribute(additionalTasks);
            else compartment().setTasksAttribute(existing + additionalTasks);
        }

    }

    private void synchronizeModelCompartments() {
        int dataChildCount = compartment().getCompartmentCount();
        int modelChildCount = model().getChildContainerCount();

        // remove compartments that are in model but not in data
        for (int i=modelChildCount-1;i>=0; i--) {
            INetworkEditorComponentModel childModel = (INetworkEditorComponentModel)model().getChildContainerAt(i);
            if (childModel.getModelType().equals(INetworkEditorComponentModel.TASKTYPE)) continue;
            if (!containsCompartment(childModel.getID())) model().removeChildContainer(childModel);
        }

        // add compartments that are in data but not in model
        for (int i=0; i<dataChildCount; i++) {
            ICompartmentWrapper compartment = (ICompartmentWrapper)compartment().getCompartmentAt(i);
            String compartmentID = compartment.getIdAttribute();
            if (!containsChild(compartmentID)) {
                INetworkEditorComponentModelWrapper newModel = (INetworkEditorComponentModelWrapper)model().createChildContainer(compartmentID, INetworkEditorComponentModel.COMPARTMENTTYPE);
                new CompartmentTranslator(newModel, compartment, BaseTranslator.UPDATE_MODEL);
            }
        }
    }

    private void synchronizeDataCompartments() {
        int dataChildCount = compartment().getCompartmentCount();
        int modelChildCount = model().getChildContainerCount();

        // remove compartments that are in data but not in model
        for (int i=dataChildCount-1;i>=0; i--)
            if (!containsChild(compartment().getCompartmentAt(i).getIdAttribute())) compartment().removeCompartmentAt(i);

            // add compartments that are in model but not in data
        for (int i=0; i<modelChildCount; i++) {
            INetworkEditorComponentModelWrapper compartment = (INetworkEditorComponentModelWrapper)model().getChildContainerAt(i);
            if (INetworkEditorComponentModel.TASKTYPE.equals(compartment.getModelType())) continue;
            String compartmentID = compartment.getID();
            if (!containsCompartment(compartmentID)) {
                ICompartmentWrapper iComp = (ICompartmentWrapper)compartment().createCompartment(compartmentID);
                LOG.println("compartment.getTranslator() is " + compartment.getTranslator());
                if (compartment.getTranslator() != null) {
                    compartment.getTranslator().close();
                    compartment.setTranslator(null);
                }
                LOG.println("compartment.getTranslator() is " + compartment.getTranslator());
                new CompartmentTranslator(compartment, iComp, BaseTranslator.UPDATE_DATA);
            }
        }
    }

    private boolean containsCompartment(String ID) {
        int dataChildCount = compartment().getCompartmentCount();
        for (int j=0; j<dataChildCount; j++) {
            String compartmentID = ((ICompartmentWrapper)compartment().getCompartmentAt(j)).getIdAttribute();
            if (match(ID, compartmentID)) return true;
        }
        return false;
    }

    private boolean containsTask(String ID) {
        String[] tasks = IDRefHelper.getReferenceArray(compartment().getTasksAttribute());
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

    public ICompartmentWrapper compartment() {
        return(ICompartmentWrapper)getDataObject();
    }


    private static boolean match(String x, String y) {
        return((x == null && y == null) ||
               (x != null && y != null && x.equals(y)));
    }
}
