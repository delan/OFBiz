/*
 * Created by IntelliJ IDEA.
 * User: Oliver Wieland
 * Date: Jul 27, 2001
 * Time: 4:02:59 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.util.DragAdapter;
import org.ofbiz.designer.util.WARNING;
import org.ofbiz.designer.newdesigner.model.IContainerModel;

import java.awt.*;
import java.awt.dnd.DropTargetDragEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

class ContainerViewDragAdapter extends DragAdapter {
    ContainerView containerView = null;
    ContainerViewDragAdapter(ContainerView _containerView) {
        super(_containerView);
        containerView = _containerView;
    }

    public void handleDrop(String dragType, IContainerModel draggedModel, Point dropLocation, Point dragLocation) {
        ContainerView draggedComponent = (ContainerView)draggedModel.getGui();
        if (tempResizeContainer != null)
            tempResizeContainer.resizeBox = null;
        if (dragType.equals(RESIZE))
            ((IContainerModel)draggedComponent.getModel()).setBounds(DNDEngine.resizeBounds);
        else if (draggedComponent == containerView) {
            int newX = containerView.getLocation().x + dropLocation.x - dragLocation.x;
            int newY = containerView.getLocation().y + dropLocation.y - dragLocation.y;
            draggedModel.setLocation(new Point(newX, newY));
        } else if (draggedComponent.getParent() instanceof ContainerView && !draggedComponent.isAncestorOf(containerView)) {
            if (draggedComponent.getParent() != containerView)
                ((IContainerModel)containerView.getModel()).addChildContainer(draggedModel);
            draggedModel.setLocation(new Point(dropLocation.x - dragLocation.x, dropLocation.y - dragLocation.y));
        } else
            WARNING.println("WARNING ! not a drag source");
    }

    public void readObjectOnDrop(ObjectInputStream is, Point dropLocation) throws Exception{
        IContainerModel myModel = (IContainerModel)containerView.getModel();
        String dragType = (String)is.readObject();

        String key = (String)is.readObject();
        String name = key.substring(key.indexOf("_")+1, key.length());
        IContainerModel draggedModel = (IContainerModel)containerView.modelInstances.get(key);
        containerView.modelInstances.remove(key);
        String newKey = myModel.getTopLevelContainer().hashCode() + "_" + name;
        containerView.modelInstances.put(newKey, draggedModel);

        Point dragLocation = (Point)is.readObject();
        handleDrop(dragType, draggedModel, dropLocation, dragLocation);
    }

    final String MOVE = "MOVE";
    final String RESIZE = "RESIZE";

    public void writeObjectOnDrag(ObjectOutputStream os, Point dragLocation) throws Exception{
        DNDEngine.xResize = 0; // not a resize
        DNDEngine.yResize = 0; // not a resize

        Rectangle bounds = ((IContainerModel)containerView.getModel()).getBounds();

        if (dragLocation.x <= 5 ) DNDEngine.xResize = -1; // resize on left bounds
        if (bounds.width - dragLocation.x <= 5) DNDEngine.xResize = 1; // resize of right bounds
        if (dragLocation.y <= 5) DNDEngine.yResize = -1; // resize of top bounds
        if (bounds.height - dragLocation.y <= 5) DNDEngine.yResize = 1; // resize of bottom bounds

        if (DNDEngine.xResize != 0 || DNDEngine.yResize != 0) {
            os.writeObject(RESIZE);
            DNDEngine.dragObject = containerView;
        } else
            os.writeObject(MOVE);

        //
        //os.writeObject(containerView.getName());
        IContainerModel myModel = (IContainerModel)containerView.getModel();
        String key = myModel.getTopLevelContainer().hashCode() + "_" + myModel.getID();
        os.writeObject(key);

        os.writeObject(dragLocation);
    }

    private ContainerView tempResizeContainer = null;
    public void dragOver(DropTargetDragEvent e) {
        if (!(DNDEngine.resize() && DNDEngine.dragObject != null &&
              (DNDEngine.dragObject.getParent() == containerView || DNDEngine.dragObject == containerView)))
            return;

        Point currentLocation = e.getLocation();
        Rectangle myBounds = containerView.getBounds();

        if (DNDEngine.dragObject == containerView) {
            currentLocation.x += myBounds.x;
            currentLocation.y += myBounds.y;
        }

        Rectangle bounds = ((ContainerView)DNDEngine.dragObject).getBounds();
        if (DNDEngine.xResize == -1) {
            bounds.width += bounds.x - currentLocation.x;
            bounds.x = currentLocation.x;
        } else if (DNDEngine.xResize == 1)
            bounds.width += currentLocation.x - (bounds.x + bounds.width);

        if (DNDEngine.yResize == -1) {
            bounds.height += bounds.y - currentLocation.y;
            bounds.y = currentLocation.y;
        } else if (DNDEngine.yResize == 1)
            bounds.height += currentLocation.y - (bounds.y + bounds.height);

        if (DNDEngine.dragObject == containerView) {
            if (containerView.getParent() instanceof ContainerView) {
                tempResizeContainer = ((ContainerView)containerView.getParent());
                ((ContainerView)containerView.getParent()).resizeBox = bounds;
            }
            DNDEngine.resizeBounds = bounds;
            containerView.repaint();
        } else {
            tempResizeContainer = containerView;
            containerView.resizeBox = bounds;
            DNDEngine.resizeBounds = bounds;
            containerView.getParent().repaint();
        }
    }
}
