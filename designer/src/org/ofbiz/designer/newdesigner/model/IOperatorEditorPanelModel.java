package org.ofbiz.designer.newdesigner.model;

import java.util.*;
import org.ofbiz.designer.util.*;

public interface IOperatorEditorPanelModel extends IBaseModel, org.ofbiz.designer.pattern.IModel {
    public String getOperatorEditorType();
    public void setEditorOperatorType(String operatorType);
    public String getTaskName();
    public void setTaskName(String taskName);
    public IOperatorModel getStartingOperator();
    public void setStartingOperator(IOperatorModel newStartingOperator);
    public void addOperator(IOperatorModel operator) ;
    public int getOperatorCount();
    public IOperatorModel createStartingOperator(String operatorType, String operatorID, boolean terminal);

    public int getOtherTaskCount();
    public String getOtherTaskAt(int i);
    public boolean containsOtherTask(String otherTask);
    public void addOtherTask(String otherTask);
    public void removeOtherTaskAt(int i);
    public void removeOtherTask(String otherTask);
}
