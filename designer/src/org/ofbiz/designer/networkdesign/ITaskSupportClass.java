package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import java.util.*;

public interface ITaskSupportClass extends IDataSupportClass {
    IDataSecurityMask getDataSecurityMaskByName(String dataName);
    Vector getDataSecurityMaskNames();
    Vector getInvocationParameters();
    Vector getOutputNames();
    public Vector getExplodedReadableParameters();
    public Vector getExplodedWritableParameters();
    Vector getExceptionNames();
    Vector getConstraintNames();
    IDataSecurityMask createDataSecurityMask(String dataName);
    boolean isPureInputParameter(String dataName);

    Vector getInputArcs();
    Vector getOutputArcs();
    // also show vertical relationship (for start and end tasks)
    Vector getInputArcsAndParent();
    Vector getOutputArcsAndParent();
    
    Vector getExceptionArcs();
    Vector getTaskTypes();
    String getTaskType();
    void setTaskType(String taskType);
    void addOutputParameter(String paramStr);
    void addInvocationParameter(String paramStr);
    void addTaskException(String paramStr);
    IRoles getRolesByName(String selected);
    void removeInArcByName(String name);            
    void removeInvocationParameter(String paramStr);
    void removeOutputParameter(String paramStr);
    void removeTaskException(String paramStr);
    boolean isForeign();
    void setForeign(boolean value);
    public IOperator createInputOperator();
    public IOperator createOutputOperator();
    public ITask getParentTask();
    public boolean containsTaskRecursive(ITask childTask);
    public boolean containsTask(ITask childTask);
    public void setSecuritydomainurlAttribute(String url);
    //void removeOutputByName(String name);			
}
