package org.ofbiz.designer.newdesigner.operatoreditor;

import javax.swing.*;
import org.ofbiz.designer.newdesigner.model.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.pattern.*;

class OperatorEditorPanel extends AbstractView implements IView {
    private Hashtable columnEntryPositions;
    private Hashtable columnIndices;

    public static final int wideFieldWidth = 140;
    public static final int narrowFieldWidth = wideFieldWidth/4;
    public static final int columnSeparation = 60;
    public static final int arrowComponentSpacing = 3;

    private TaskComponent taskComponent = null;

    public OperatorEditorPanel(IOperatorEditorPanelModel modelIn) {
        setModel(modelIn);
        setPreferredSize(new Dimension(200, 700));
        setLayout(null);
        synchronize();
        revalidate();
    }

    public void synchronize() {
        IOperatorEditorPanelModel model = (IOperatorEditorPanelModel)getModel();
        removeAll();
        taskComponent = new TaskComponent(model, model.getOperatorEditorType());
        taskComponent.setBounds(10, 10, wideFieldWidth, OperatorComponent.fieldHeight);
        add(taskComponent);

        columnEntryPositions = columnIndices = null;
        synchronize(model.getStartingOperator(), 0);
        if(model.getOperatorEditorType().equals(OperatorEditorType.INPUT_OPERATOR))
            swapCoordinates();
    }

    private void synchronize(final IOperatorModel _model, int column) {
        IOperatorEditorPanelModel editorModel = (IOperatorEditorPanelModel)getModel();
        if(columnEntryPositions == null) columnEntryPositions = new Hashtable();
        if(columnIndices == null) columnIndices = new Hashtable();

        if(_model == null) return;
        final OperatorComponent op = new OperatorComponent(_model, editorModel.getOperatorEditorType());

        int columnEntryPosition = 10, columnEntryIndex = 0;
        try {
            columnEntryPosition = ((Integer)columnEntryPositions.get("" + column)).intValue();
        } catch(NullPointerException e) {}
        try {
            columnEntryIndex = ((Integer)columnIndices.get("" + column)).intValue();
        } catch(NullPointerException e) {}
        int fieldCount = _model.getFieldCount();
        if(fieldCount == 0) fieldCount = 1;

        int fieldWidth;
        if(isWideComponent(op)) fieldWidth = wideFieldWidth;
        else fieldWidth = narrowFieldWidth;

        op.setBounds((column+1)*(wideFieldWidth + columnSeparation), columnEntryPosition, fieldWidth, op.fieldHeight*fieldCount);

        IOperatorField previousField = _model.getPredecessor();
        add(op);
        OperatorComponent previousOperator = null;
        int index = 0;
        if(previousField != null) {
            previousOperator = (OperatorComponent)OperatorComponent.modelGui.get(previousField.getParentOperator());
            index = previousField.getParentOperator().getIndexOfField(previousField);
        } else
            previousOperator = taskComponent;

        Rectangle previousBounds = previousOperator.getBounds();
        Rectangle currentBounds = op.getBounds();

        IOperatorModel previousOpModel = (IOperatorModel)previousOperator.getModel();
        String previousOpType = previousOpModel.getOperatorType();

        int p1y = previousBounds.y;
        if(isWideComponent(previousOperator))
            p1y += index*(OperatorComponent.fieldHeight-1) + OperatorComponent.fieldHeight/2;
        else p1y += (int)(previousBounds.getHeight()/2);

        int tempX = previousBounds.x + wideFieldWidth;
        Point p1 = new Point(previousBounds.x + previousBounds.width, p1y);
        Point p2 = new Point(currentBounds.x, currentBounds.y + currentBounds.height/2);

        int medianX = (p2.x+tempX)/2 + ((p2.y > p1.y)?-1:1)*arrowComponentSpacing*columnEntryIndex;
        if(editorModel.getOperatorEditorType().equals(OperatorEditorType.OUTPUT_OPERATOR))
            add(createPolyArrow(p1, medianX, p2));
        else
            add(createPolyArrow(p2, medianX, p1));

        columnEntryPositions.put("" + column, new Integer(columnEntryPosition + 20 + OperatorComponent.fieldHeight*fieldCount));
        columnIndices.put("" + column, new Integer(columnEntryIndex+1));
        fieldCount = _model.getFieldCount();
        for(int i=0; i<fieldCount; i++) {
            IOperatorModel newModel = _model.getFieldAt(i).getOperator();
            if(newModel != null) synchronize(newModel, column+1);
        }

        validate();
        repaint();
    }

    private boolean isWideComponent(OperatorComponent op) {
        IOperatorEditorPanelModel editorModel = (IOperatorEditorPanelModel)getModel();
        return op.operatorType.equals(OperatorType.TASK) || (editorModel.getOperatorEditorType().equals(OperatorEditorType.OUTPUT_OPERATOR) && !op.operatorType.equals(OperatorType.AND_OPERATOR));
    }

    private PolyArrowComponent createPolyArrow(Point p1, int medianX, Point p2) {
        Vector vec = new Vector();
        vec.addElement(p1);
        vec.addElement(new Point(medianX, p1.y));
        vec.addElement(new Point(medianX, p2.y));
        vec.addElement(p2);
        return new PolyArrowComponent(vec);
    }

    private void swapCoordinates() {
        int count = getComponentCount();
        Rectangle bounds = getComponent(0).getBounds();
        int minx = bounds.x;
        int maxx = bounds.x + bounds.width;
        for(int i=1; i<count; i++) {
            bounds = getComponent(i).getBounds();
            if(bounds.x < minx) minx = bounds.x;
            if(bounds.x + bounds.width > maxx) maxx = bounds.x + bounds.width;
        }

        for(int i=0; i<count; i++) {
            Component comp = getComponent(i);
            bounds = comp.getBounds();
            bounds.x = maxx-bounds.x-bounds.width + 10;
            comp.setBounds(bounds);
            if(comp instanceof PolyArrowComponent)
                ((PolyArrowComponent)comp).swapOrientation();
        }
    }
}
