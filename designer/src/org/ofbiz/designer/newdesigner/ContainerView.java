package org.ofbiz.designer.newdesigner;

import javax.swing.*;  
import java.util.*;
import java.awt.*;
import java.awt.dnd.*;
import org.ofbiz.designer.util.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.io.*;
import org.ofbiz.designer.newdesigner.model.*;
import org.ofbiz.designer.pattern.*;


public class ContainerView extends AbstractView implements ActionListener {
    public static Hashtable modelInstances = new Hashtable();

    public ContainerView(final IContainerModel modelIn) {
        setLayout(null);
        setModel(modelIn);
        //modelInstances.put(modelIn.getTopLevelContainer().hashCode() + "_" + modelIn.getName(), modelIn);

        final WFPopup popup = modelIn.getPopup();
        if (popup != null)
            addMouseListener(new PopupMouseListener(popup, this));

        setForeground(Color.white);
        setDragAdapter();
    }

    protected void setDragAdapter() {
        new ContainerViewDragAdapter(this);
    }

    public void setModel(IContainerModel _model) {
        IContainerModel oldModel = (IContainerModel)getModel();
        if (_model.equals(oldModel))
            return;

        // TBD - when do we remove ?
        //if (oldModel != null) 
        //		modelInstances.remove(modelIn.getTopLevelContainer().hashCode() + "_" + oldModel.getName());

        super.setModel(_model);
        modelInstances.put("" + _model.getTopLevelContainer().hashCode() + "_" + _model.getID(), _model);
    }

    protected Rectangle resizeBox = null;
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        g.clearRect(0, 0, getWidth()-1, getHeight()-1);
        g.drawRect(0, 0, getWidth()-1, getHeight()-1);
        g.setColor(getForeground());
        g.fillRect(1, 1, getWidth()-2, getHeight()-2);
        g.setColor(Color.black);
        if (resizeBox != null)
            g.drawRect(resizeBox.x, resizeBox.y, resizeBox.width, resizeBox.height);
        g.setColor(getForeground());
    }

    public boolean containsChild(Object comp) {
        for (int i=0; i<getComponentCount(); i++)
            if (getComponent(i) == comp)
                return true;
        return false;
    }

    public boolean containsChildArc(ArcView comp) {
        for (int i=0; i<getComponentCount(); i++)
            if (getComponent(i) == comp)
                return true;
        return false;
    }

    public Component add(Component comp) {
        if (comp instanceof ArcView)
            return super.add(comp, 0);
        else
            return super.add(comp);
    }

    public void synchronize() {
        IContainerModel model = (IContainerModel)getModel();
        try {
            if ((getParent() instanceof ContainerView) && (getBounds()==null || !getBounds().equals(model.getBounds())))
                setBounds(model.getBounds());
            if (getName()==null || !getName().equals(model.getID()))
                setName(model.getID());

            for (int i=0; i<model.getChildContainerCount(); i++) {
                ContainerView child = (ContainerView)model.getChildContainerAt(i).getGui();
                if (child == null) add(child = new NetworkEditor((INetworkEditorComponentModel)model.getChildContainerAt(i)));
                else if (!containsChild(child)) add(child);
                child.synchronize();
            }

            for (int i=0; i<model.getChildArcCount(); i++) {
                IArcModel childArc = model.getChildArcAt(i);
                ArcView child = (ArcView)childArc.getGui();
                if (child == null) add(child = new ArcView(childArc));
                else if (!containsChildArc(child)) add(child);
                child.synchronize();
            }

            Component[] comps = getComponents();
            for (int i=0; i<comps.length; i++)
                if (comps[i] instanceof ContainerView && !model.containsChildContainer((IContainerModel)((ContainerView)comps[i]).getModel()))
                    remove(comps[i]);
                else if (comps[i] instanceof ArcView && !model.containsChildArc((IArcModel)((ArcView)comps[i]).getModel()))
                    remove(comps[i]);


            if (getRootPane() != null && ((JFrame)getRootPane().getParent()).getContentPane() == getParent())
                ;// do nothing;
            else if ((model == null || model.getParentContainer() == null || model.getParentContainer().getGui() == null) && getParent() != null)
                getParent().remove(this);
            else if ((model == null || model.getParentContainer() == null || model.getParentContainer().getGui() == null) && getParent() == null)
                ; // do nothing;
            else if (getParent() == null)
                ((ContainerView)model.getParentContainer().getGui()).add(this);
            else if (getParent() != model.getParentContainer().getGui()) {
                ContainerView temp = (ContainerView)model.getParentContainer().getGui();
                getParent().remove(this);
                temp.add(this);
            }
            ((ContainerView)((IContainerModel)getModel()).getTopLevelContainer().getGui()).repaint();
        } catch (Exception e) {
            e.printStackTrace();
            WARNING.println("EXCEPTION " + e.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {
        WARNING.println("received " + e.getActionCommand());
    }
}


