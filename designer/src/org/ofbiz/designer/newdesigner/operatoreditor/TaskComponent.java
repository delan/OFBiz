package org.ofbiz.designer.newdesigner.operatoreditor;

import java.awt.*;
import org.ofbiz.designer.newdesigner.model.*;
import org.ofbiz.designer.util.*;

class TaskComponent extends OperatorComponent {
    String taskName;

    public TaskComponent(IOperatorEditorPanelModel modelIn, String editorType) {
        super(modelIn.getStartingOperator(), editorType);
        taskName = modelIn.getTaskName();
    }

    public void paintComponent(Graphics g) {
        g.drawRect(0, 0, getWidth()-1, getHeight()-1);
        g.drawString(taskName, 10, getHeight()*3/4);
    }
}
