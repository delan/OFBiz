package org.ofbiz.designer.newdesigner.operatoreditor;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.networkdesign.*;
import org.ofbiz.designer.newdesigner.model.*;
import org.ofbiz.designer.util.*;

class FieldTranslator extends BaseTranslator {
    public FieldTranslator(IOperatorFieldWrapper model, IFieldWrapper field, String mode) {
        super(model, field);
        synchronize(mode);
    }

    public void updateDataImpl() {
        IOperatorField model = (IOperatorField)getGuiModel();
        IFieldWrapper field = (IFieldWrapper)getDataObject();

        // synchronize conditions
        String dataCondition = field.getConditionAttribute();
        String modelCondition = model.getCondition();

        if(!match(dataCondition, modelCondition))
            field.setConditionAttribute(modelCondition);

        // synchronize operators
        if(model.getOperator() != null && model.getOperator().isTerminalOperator()) {
            String modeltaskID = model.getOperator().getFieldAt(0).getCondition();
            String dataTaskID = field.getTaskAttribute();
            if(!match(modeltaskID, dataTaskID))
                field.setTaskAttributeByName(modeltaskID);
        } else {
            IOperatorModelWrapper opModel = (IOperatorModelWrapper)model.getOperator();
            IOperator opData = field.getOperator();

            if(opModel == null && opData != null)
                field.setOperator(null);
            else if(opModel != null && opData == null) {
                IOperatorWrapper op = (IOperatorWrapper)field.createOperator(opModel.getID());
                new OperatorTranslator(opModel, op, BaseTranslator.UPDATE_DATA);
                field.setOperator(op);
            } else if(opModel != null && opData != null) {
                String modelOpID = opModel.getID();
                String dataOpID = field.getOperator().getIdAttribute();
                if(!match(modelOpID, dataOpID)) {
                    field.removeOperator();
                    IOperatorWrapper op = (IOperatorWrapper)field.createOperator(modelOpID);
                    new OperatorTranslator(opModel, op, BaseTranslator.UPDATE_DATA);
                    field.setOperator(op);
                }
            }
        }
    }

    public void updateModelImpl() {
        LOG.println("***** FIELD");
        IOperatorField model = (IOperatorField)getGuiModel();
        IFieldWrapper field = (IFieldWrapper)getDataObject();

        String dataCondition = field.getConditionAttribute();
        String modelCondition = model.getCondition();

        if(!match(modelCondition, dataCondition))
            model.setCondition(dataCondition);

        // synchronize operators
        if(field.getTaskAttribute() != null) {
            String taskAttribute = ((ITask)field.getXml().getIdRef(field.getTaskAttribute())).getNameAttribute();

            IOperatorModelWrapper nextModel = (IOperatorModelWrapper)model.getOperator();
            if(nextModel == null || !nextModel.isTerminalOperator() || !nextModel.getID().equals(taskAttribute)) {
                model.setOperator(null);
                model.createOperator(OperatorType.TASK, taskAttribute, true);
            }
        } else {
            IOperatorWrapper dataOp = (IOperatorWrapper)field.getOperator();
            IOperatorModel modelOp = model.getOperator();
            if(dataOp == null) {
                if(modelOp != null)
                    model.setOperator(null);
            } else {
                if(modelOp == null) {
                    String operatorType = dataOp.getTypeAttribute();
                    LOG.println("OPERATORTYPE is " + operatorType);
                    IOperatorModelWrapper opModel = (IOperatorModelWrapper)model.createOperator(operatorType, dataOp.getIdAttribute(), false);
                    new OperatorTranslator(opModel, dataOp, BaseTranslator.UPDATE_MODEL);
                } else {
                    String dataOpID = dataOp.getIdAttribute();
                    String modelOpID = modelOp.getID();
                    if(!match(dataOpID, modelOpID)) {
                        model.setOperator(null);
                        String operatorType = dataOp.getTypeAttribute();
                        IOperatorModelWrapper opModel = (IOperatorModelWrapper)model.createOperator(operatorType, dataOpID, false);
                        new OperatorTranslator(opModel, dataOp, BaseTranslator.UPDATE_MODEL);
                    }
                }
            }
        }
    }

    private static boolean match(String x, String y) {
        return((x == null && y == null) ||
               (x != null && y != null && x.equals(y)));
    }
}
