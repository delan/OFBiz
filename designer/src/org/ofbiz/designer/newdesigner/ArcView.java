package org.ofbiz.designer.newdesigner;

import javax.swing.*;
import java.awt.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.newdesigner.model.*;
import java.util.*;

import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.awt.event.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.newdesigner.popup.*;

import org.ofbiz.designer.networkdesign.*;

public class ArcView extends AbstractView implements ActionListener {
    private float paintThickNess = 1;
    private float clickThickNess = 3;

    public ArcView(final IArcModel _model) {
        setModel(_model);

        final WFPopup popup = _model.getPopup();
        if(popup != null)
            addMouseListener(new PopupMouseListener(popup, this));
        addMouseListener(new MouseAdapter() {
                             public void mouseClicked(MouseEvent e) {
                                 if(e.getClickCount()>=2) 
                                     launchArcEditor();
                             }
                         });
    }

    public boolean contains(int x, int y) {
        if(line == null) return false;
        return line.containingRect(clickThickNess).contains(x, y);
    }

    private Arrow line;
    public void setBounds() {
        IArcModel model = (IArcModel)getModel();
        try {
            Rectangle bounds = model.getBounds();
            if(bounds == null)
                return;
            int x1 = bounds.x;
            int y1 = bounds.y;
            int width = bounds.width;
            int height = bounds.height;

            if(width < 20) {
                x1 -= (20 - width)/2;
                width = 20;             
            }
            if(height < 20) {
                y1 -= (20 - height)/2;
                height = 20;                
            }
            setBounds(x1, y1, width, height);
            setLine();
        } catch(Exception e) {
            WARNING.println("EXCEPTION " + e.getMessage());
        }
    }

    private void setLine() {
        IArcModel model = (IArcModel)getModel();
        if(model.getSource() instanceof IContainerModel)
            setLineSourceIsNode();
        else
            setLineSourceIsArc();
    }

    private void setLineSourceIsNode() {
        IArcModel model = (IArcModel)getModel();

        try {
            IContainerModel source = (IContainerModel)model.getSource();
            IContainerModel destination = model.getDestination();

            int x1 = source.getAbsoluteBounds(model.getParent()).x + source.getBounds().width/2 - getX();
            int y1 = source.getAbsoluteBounds(model.getParent()).y + source.getBounds().height/2 - getY();
            int x2 = destination.getAbsoluteBounds(model.getParent()).x + destination.getBounds().width/2 - getX();
            int y2 = destination.getAbsoluteBounds(model.getParent()).y + destination.getBounds().height/2 - getY();

            /*
            if (x1 > x2) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
            }
            if (y1 > y2) {
                int temp = y1;
                y1 = y2;
                y2 = temp;
            }
            
            if (source.getAbsoluteBounds(model.getParent()).x > destination.getAbsoluteBounds(model.getParent()).x) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
            }
            if (source.getAbsoluteBounds(model.getParent()).y > destination.getAbsoluteBounds(model.getParent()).y) {
                int temp = y1;
                y1 = y2;
                y2 = temp;
            }
            */

            line = new Arrow(x1, y1, x2, y2);
            float length = line.getLength();
            float width = source.getBounds().width;
            float height = source.getBounds().height;
            float diag = (float)Math.sqrt(width*width+height*height)/2;
            line.setLengthFromHead(length-diag);

            length = line.getLength();
            width = destination.getBounds().width;
            height = destination.getBounds().height;
            diag = (float)Math.sqrt(width*width+height*height)/2;
            line.setLengthFromTail(length-diag);

            line.setThickness((int)paintThickNess);
            if(model.DASHED.equals(model.getLineStyle()))
                //line.dotted = true;
                fail = true;
        } catch(Exception e) {
            //WARNING.println("EXCEPTION " + e.getMessage());
        }
    }

    private void setLineSourceIsArc() {
        IArcModel model = (IArcModel)getModel();

        try {
            IArcModel source = (IArcModel)model.getSource();
            IContainerModel destination = model.getDestination();

            IContainerModel ssource = (IContainerModel)source.getSource();
            IContainerModel sdestination = source.getDestination();

            int sx1 = ssource.getAbsoluteBounds(model.getParent()).x + ssource.getBounds().width/2 - getX();
            int sy1 = ssource.getAbsoluteBounds(model.getParent()).y + ssource.getBounds().height/2 - getY();

            int sx2 = sdestination.getAbsoluteBounds(model.getParent()).x + sdestination.getBounds().width/2 - getX();
            int sy2 = sdestination.getAbsoluteBounds(model.getParent()).y + sdestination.getBounds().height/2 - getY();

            int x1 = (sx1+sx2)/2;
            int y1 = (sy1+sy2)/2;

            int x2 = destination.getAbsoluteBounds(model.getParent()).x + destination.getBounds().width/2 - getX();
            int y2 = destination.getAbsoluteBounds(model.getParent()).y + destination.getBounds().height/2 - getY();

            /*
            if (x1 > x2) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
            }
            if (y1 > y2) {
                int temp = y1;
                y1 = y2;
                y2 = temp;
            }

            if (source.getAbsoluteBounds(model.getParent()).x > destination.getAbsoluteBounds(model.getParent()).x) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
            }
            if (source.getAbsoluteBounds(model.getParent()).y > destination.getAbsoluteBounds(model.getParent()).y) {
                int temp = y1;
                y1 = y2;
                y2 = temp;
            }
            */

            line = new Arrow(x1, y1, x2, y2);
            float length = line.getLength();
            float width = destination.getBounds().width;
            float height = destination.getBounds().height;
            float diag = (float)Math.sqrt(width*width+height*height)/2;
            line.setLengthFromTail(length-diag);

            line.setThickness((int)paintThickNess);
            if(model.DASHED.equals(model.getLineStyle()))
                //line.dotted = true;
                fail = true;
        } catch(Exception e) {
            //WARNING.println("EXCEPTION " + e.getMessage());
        }
    }

    private boolean fail = false;

    public void synchronize() {
        IArcModel model = (IArcModel)getModel();
        try {
            if((model == null || model.getParent() == null || model.getParent().getGui() == null) && getParent() != null)
                getParent().remove(this);
            else if((model == null || model.getParent() == null || model.getParent().getGui() == null) && getParent() == null)
                ;
            else if(getParent() == null)
                ((ContainerView)model.getParent().getGui()).add(this);
            else if(getParent() != model.getParent().getGui())
                ((ContainerView)model.getParent().getGui()).add(this);

            setBounds();
            ((ContainerView)((IArcModel)getModel()).getParent().getTopLevelContainer().getGui()).repaint();
        } catch(Exception e) {
        }
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setStroke(new BasicStroke(4));
        Color bkup = g.getColor();
        if(fail) g.setColor(Color.red);
        else if(((IArcModel)getModel()).getSource() instanceof IArcModel) {
            g.setColor(Color.yellow);
        }
        else g.setColor(Color.black);
        if(line != null)
            line.paint(g2d);
        g.setColor(bkup);
    }

    public void actionPerformed(ActionEvent e) {
        IArcModelWrapper model = (IArcModelWrapper)getModel();
        String command = e.getActionCommand();
        if(command.equals(ActionEvents.DELETE))
            ((IArcModel)getModel()).die();
        else if(command.equals(ActionEvents.ADD_ARC_SOURCE_SUCCESS)) {
            if(model.getSource() instanceof INetworkEditorComponentModel) {
                NetworkEditor top = top();
                top.entityAddingArc = this;
                top.successArc = true;
            }
        } else if(command.equals(ActionEvents.ARC_ED)) 
            launchArcEditor();
        else WARNING.println("unhandled actionEvent " + command);
    }

    private void launchArcEditor() {
        IArcModelWrapper model = (IArcModelWrapper)getModel();
        ITaskWrapper sourceTask = (ITaskWrapper)((INetworkEditorComponentModelWrapper)model.getSourceTask()).getTranslator().getDataObject();
        ITaskWrapper destinationTask = (ITaskWrapper)((INetworkEditorComponentModelWrapper)model.getDestination()).getTranslator().getDataObject();

        XmlWrapper xml = sourceTask.getXml();
        String source = sourceTask.getIdAttribute();
        String destination = destinationTask.getIdAttribute();

        ArcEditor.launchArcEditor(xml, source, destination);
    }

    private NetworkEditor top() {
        NetworkEditor top = (NetworkEditor)getParent();
        while(top.getParent() instanceof NetworkEditor)
            top = (NetworkEditor)top.getParent();
        return top;
    }
}

