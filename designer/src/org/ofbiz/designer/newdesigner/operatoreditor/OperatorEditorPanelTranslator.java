package org.ofbiz.designer.newdesigner.operatoreditor;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.networkdesign.*;
import org.ofbiz.designer.newdesigner.model.*;
import java.util.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.generic.*;

class OperatorEditorPanelTranslator extends BaseTranslator {
    String opEditorType;
    public OperatorEditorPanelTranslator(IOperatorEditorPanelModelWrapper model, Vector vec, String _opEditorType, String mode) {
        super(model, vec);
        if(vec.size() != 2 || !(vec.elementAt(0) instanceof IOperatorWrapper) || !(vec.elementAt(1) instanceof ITaskWrapper))
            throw new RuntimeException("Bad parameters");
        opEditorType = _opEditorType;
        synchronize(mode);
    }

    public void updateDataImpl() {
        // implementation not required
    }

    public void updateModelImpl() {
        IOperatorEditorPanelModel model = (IOperatorEditorPanelModel)getGuiModel();
        IOperatorWrapper _operator = (IOperatorWrapper)getDataObjectAt(0);
        ITaskWrapper task = (ITaskWrapper)getDataObjectAt(1);

        int num = model.getOtherTaskCount();
        HashSet otherTasks = new HashSet();
        for(int i=0;i<num;i++)
            otherTasks.add(model.getOtherTaskAt(i));

        if(opEditorType.equals(OperatorEditorType.INPUT_OPERATOR)) {
            String[] arcs = IDRefHelper.getReferenceArray(task.getInarcsAttribute());
            if(arcs == null) return;
            for(int i=0; i<arcs.length; i++) {
				IArc  arc = ((IArc)task.getXml().getIdRef(arcs[i]));
				String sourceID = arc.getSourceAttribute();         
				if (arc.getArctypeAttribute().equals("Alternative")) 
					sourceID = ((IArc)task.getXml().getIdRef(arc.getSourceAttribute())).getSourceAttribute();
				String sourceName = ((ITask)task.getXml().getIdRef(sourceID)).getNameAttribute();
                if(!otherTasks.contains(sourceName)) model.addOtherTask(sourceName);
                else otherTasks.remove(sourceName);
            }
            if(task.getParentTask().getRealization().getNetworkTaskRealization().getFirsttaskAttribute().equals(task.getIdAttribute())) {
                String sourceName = task.getParentTask().getNameAttribute();
                if(!otherTasks.contains(sourceName)) model.addOtherTask(sourceName);
                else otherTasks.remove(sourceName);
            }
        } else {
            String[] arcs = IDRefHelper.getReferenceArray(task.getOutarcsAttribute());
            org.ofbiz.designer.util.LOG.println("OperatorEditorTranslator:updateModelImpl: "+arcs.length );
            if(arcs == null) return;
            for(int i=0; i<arcs.length; i++) {
                IArc arc = (IArc)task.getXml().getIdRef(arcs[i]);
                if(arc.getArctypeAttribute().equals("Success")) {
                    String destinationID = arc.getDestinationAttribute();
                    String destinationName = ((ITask)task.getXml().getIdRef(destinationID)).getNameAttribute();
                    if(!otherTasks.contains(destinationName)) 
                        model.addOtherTask(destinationName);
                    else otherTasks.remove(destinationName);
                }
            }
            if(task.getParentTask().getRealization().getNetworkTaskRealization().getLasttaskAttribute().equals(task.getIdAttribute())) {
                String destinationName = task.getParentTask().getNameAttribute();
                if(!otherTasks.contains(destinationName)) model.addOtherTask(destinationName);
                else otherTasks.remove(destinationName);
            }
        }

        Iterator it = otherTasks.iterator();
        while(it.hasNext()) 
            model.removeOtherTask((String)it.next());

        if(!task.getNameAttribute().equals(model.getTaskName()))
            model.setTaskName(task.getNameAttribute());

        IOperatorModel startingOperator = model.getStartingOperator();
        if(startingOperator == null || !_operator.getIdAttribute().equals(startingOperator.getID())) {
            model.setStartingOperator(null);
            IOperatorModelWrapper operatorModel = (IOperatorModelWrapper)model.createStartingOperator(_operator.getTypeAttribute(), _operator.getIdAttribute(), false);
            new OperatorTranslator(operatorModel, _operator, BaseTranslator.UPDATE_MODEL);
        }
    }
}
