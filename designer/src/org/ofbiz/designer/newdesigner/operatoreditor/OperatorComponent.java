package org.ofbiz.designer.newdesigner.operatoreditor;

import org.ofbiz.designer.util.*;
import org.ofbiz.designer.newdesigner.model.*;
import java.awt.*;
import java.util.*;
import java.awt.dnd.*;
import java.io.*;
import org.ofbiz.designer.pattern.*;
import java.awt.event.*;
import org.ofbiz.designer.newdesigner.popup.*;

class OperatorComponent extends AbstractView implements ActionListener {
    public static Hashtable modelGui = new Hashtable();
    public static final int fieldHeight = 20;

    String operatorType;
    String editorType;
    Vector conditions = new Vector();   

    public OperatorComponent(IOperatorModel modelIn, String _editorType) {
        if(!_editorType.equals(OperatorEditorType.INPUT_OPERATOR) && !_editorType.equals(OperatorEditorType.OUTPUT_OPERATOR))
            throw new RuntimeException("Unknown Editor type " + _editorType);
        editorType = _editorType;
        setModel(modelIn);
        synchronize();
        modelGui.put(modelIn, this);
        OperatorComponentDragAdapter myDragAdapter = new OperatorComponentDragAdapter(this);
        final WFPopup popup = new OperatorPopup();
        if(popup != null)
            addMouseListener(new PopupMouseListener(popup, this));

    }

    public void setModel(final IOperatorModel _model) {
        super.setModel(_model);
        if(_model.getOperatorType().equals(OperatorType.OR_OPERATOR) && editorType.equals(OperatorEditorType.OUTPUT_OPERATOR))
            addMouseListener(new MouseAdapter() {
                                 public void mouseClicked(MouseEvent e) {
                                     if(_model.getFieldCount() == 0) return;
                                     int y = e.getPoint().y;
                                     int fieldIndex = y/fieldHeight;
                                     String existingCondition = _model.getFieldAt(fieldIndex).getCondition();
                                     Point p = e.getPoint();
                                     p.translate(OperatorComponent.this.getLocationOnScreen().x, OperatorComponent.this.getLocationOnScreen().y);
                                     String newCondition = GetStringDialog.getString(null, existingCondition, "Firing condition", p);
                                     if(newCondition == null) return;
                                     _model.getFieldAt(fieldIndex).setCondition(newCondition);
                                     synchronize();
                                     repaint();
                                 }
                             });


        if(_model.getOperatorType().equals(OperatorType.TASK))
            addMouseListener(new MouseAdapter() {
                                 public void mouseClicked(MouseEvent e) {
                                     Point p = e.getPoint();
                                     p.translate(OperatorComponent.this.getLocationOnScreen().x, OperatorComponent.this.getLocationOnScreen().y);
                                     LOG.println("_model is " + _model);
                                     LOG.println("_model.getParentOperatorEditor() is " + _model.getParentOperatorEditor());
                                     GetStringDialog.getString(null, _model.getFieldAt(0).getCondition(), "Task Name", p);
                                 }
                             });
    }

    private void drawEllipse(Graphics g, Color color) {
        int x = -getWidth(), y = getHeight()/2-fieldHeight/2, width = 2*getWidth(), height = fieldHeight-1, startAngle = -90, sweep = 180;
        Color save = g.getColor();
        g.setColor(color);
        g.fillArc(x, y, width, height, startAngle, sweep);
        g.setColor(save);
        g.drawArc(x, y, width, height, startAngle, sweep);
        g.drawLine(0, y, 0, getHeight()/2+fieldHeight/2);

    }

    public void paintComponent(Graphics g) {
        if(operatorType.equals(OperatorType.AND_OPERATOR))
            drawEllipse(g, Color.red);
        else if(operatorType.equals(OperatorType.TASK)) {
            drawBox(g, 0, 0, getWidth()-1, getHeight()-1, Color.green);
            if(conditions.elementAt(0) != null)
                g.drawString(""+conditions.elementAt(0), 10, fontHeight);
        } else if(editorType.equals(OperatorEditorType.INPUT_OPERATOR))
            drawEllipse(g, Color.blue);
        else {
            int count = conditions.size();
            if(count < 1)
                drawBox(g, 0, 0, getWidth()-1, getHeight()-1, Color.gray);
            else {
                int y = 0, fieldHeight = (getHeight()-1)/count;
                for(int i=0; i<count; i++) {
                    drawBox(g, 0, y, getWidth()-1, fieldHeight, Color.gray);
                    y += fontHeight;
                    if(conditions.elementAt(i) != null)
                        g.drawString(""+conditions.elementAt(i), 10, y);
                    y += (fieldHeight - fontHeight);
                }
            }
        }
    }
    private int fontHeight = fieldHeight*3/4;   
    private void drawBox(Graphics g, int x, int y, int width, int height, Color color) {
        Color save = g.getColor();
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(save);
        g.drawRect(x, y, width, height);
    }

    public void synchronize() {
        IOperatorModel operatorModel = (IOperatorModel)getModel();
        operatorType = operatorModel.getOperatorType();
        if(!conditions.isEmpty())
            conditions.removeAllElements();
        if((editorType.equals(OperatorEditorType.OUTPUT_OPERATOR) && operatorType.equals(OperatorType.OR_OPERATOR)) || operatorType.equals(OperatorType.TASK)) {
            int count = operatorModel.getFieldCount();
            for(int i=0; i<count; i++)
                conditions.addElement(operatorModel.getFieldAt(i).getCondition());
        }
    }

    class OperatorComponentDragAdapter extends DragAdapter {
        OperatorComponent comp = null;

        OperatorComponentDragAdapter(OperatorComponent _comp) {
            super(_comp);
            comp = _comp;
        }

        public void readObjectOnDrop(ObjectInputStream is, Point dropLocation) throws Exception{
            OperatorEditorPanel panel = (OperatorEditorPanel)comp.getParent();
            Container parentFrame = comp.getRootPane().getParent();
            IOperatorModel operatorModel = (IOperatorModel)comp.getModel();

            if(operatorModel.getOperatorType().equals(OperatorType.TASK)) return;
            String type  = (String)is.readObject();
            String value = (String)is.readObject();

            operatorModel.beginTransaction();
            {
                IOperatorFieldWrapper operatorField = (IOperatorFieldWrapper)operatorModel.createField(null);
                LOG.println("OPERATORCOMPONENT created field");
                if(type.equals(OperatorEditor.OPERATOR)) {
                    IOperatorModel newOperator = operatorField.createOperator(OperatorEditor.OPERATOR, null, false);
                    newOperator.setOperatorType(value);
                } else if(type.equals(OperatorEditor.TASK))
                    operatorField.createOperator(OperatorEditor.TASK, value, true);
                else throw new RuntimeException("Invalid ListType");
            }
            operatorModel.commitTransaction();
            panel.synchronize();
            parentFrame.validate();
            parentFrame.repaint();
        }
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(command.equals(ActionEvents.SET_TYPE_AND)) {
            String type = ((IOperatorModel)getModel()).getOperatorType();
            if(!type.equals(OperatorType.AND_OPERATOR)) 
                ((IOperatorModel)getModel()).setOperatorType(OperatorType.AND_OPERATOR);
            
            //getRootPane().getParent().repaint();
        } else if(command.equals(ActionEvents.SET_TYPE_OR)) {
            String type = ((IOperatorModel)getModel()).getOperatorType();
            if(!type.equals(OperatorType.OR_OPERATOR)) 
                ((IOperatorModel)getModel()).setOperatorType(OperatorType.OR_OPERATOR);
            getRootPane().getParent().repaint();
        } else if(command.equals(ActionEvents.DELETE)) {
            if ((((IOperatorModel)getModel()).getPredecessor()) == null)
                WARNING.println("cannot delete first operator");
            else ((IBaseModel)getModel()).die();
        } else
            WARNING.println("unhandle command " + command);
    }
}
