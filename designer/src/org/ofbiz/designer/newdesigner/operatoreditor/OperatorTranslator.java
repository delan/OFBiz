package org.ofbiz.designer.newdesigner.operatoreditor;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.networkdesign.*;
import org.ofbiz.designer.newdesigner.model.*;
import org.ofbiz.designer.util.*;
import java.util.*;

class OperatorTranslator extends BaseTranslator {
    public OperatorTranslator(IOperatorModelWrapper model, IOperatorWrapper operator, String mode) {
        super(model, operator);
        synchronize(mode);
    }

    public void updateDataImpl() {
        IOperatorModel operatorModel = (IOperatorModel)getGuiModel();
        IOperatorWrapper operatorData = (IOperatorWrapper)getDataObject();

        // synchronize operator type
        String dataType = operatorData.getTypeAttribute();
        String modelType = operatorModel.getOperatorType();
        if(!match(dataType, modelType))
            operatorData.setTypeAttribute(modelType);

        // remove fields in data that aren't in model
        int dataFieldCount = operatorData.getFieldCount();
        int modelFieldCount = operatorModel.getFieldCount();
        loop:
        for(int i=dataFieldCount-1;i>=0;i--) {
            IField fieldData = operatorData.getFieldAt(i);
            for(int j=0;j<modelFieldCount;j++) {
                IOperatorField fieldModel = operatorModel.getFieldAt(j);                   
                if(fieldModel.getID().equals(fieldData.getIdAttribute()))
                    continue loop;
            }
            operatorData.removeFieldAt(i);
        }

        // add fields to data that aren't in data
        dataFieldCount = operatorData.getFieldCount();
        loop:
        for(int i=modelFieldCount-1;i>=0;i--) {
            IOperatorFieldWrapper fieldModel = (IOperatorFieldWrapper)operatorModel.getFieldAt(i);
            for(int j=0;j<dataFieldCount;j++) {
                IField fieldData = operatorData.getFieldAt(j);                   
                if(fieldModel.getID().equals(fieldData.getIdAttribute()))
                    continue loop;
            }
            IFieldWrapper newField = (IFieldWrapper)operatorData.createField(fieldModel.getID());
            new FieldTranslator(fieldModel, newField, BaseTranslator.UPDATE_DATA);
            operatorData.addField(newField);
        }
    }

    public void updateModelImpl() {
        IOperatorModel operatorModel = (IOperatorModel)getGuiModel();
        IOperatorWrapper operatorData = (IOperatorWrapper)getDataObject();

        // synchronize operator type
        String dataType = operatorData.getTypeAttribute();
        String modelType = operatorModel.getOperatorType();
        if(!dataType.equals(modelType))
            operatorModel.setOperatorType(dataType);

        // remove fields in model that aren't in data
        int dataFieldCount = operatorData.getFieldCount();
        int modelFieldCount = operatorModel.getFieldCount();
        loop:
        for(int i=modelFieldCount-1;i>=0;i--) {
            IOperatorField fieldModel = operatorModel.getFieldAt(i);
            for(int j=0;j<dataFieldCount;j++) {
                IField fieldData = operatorData.getFieldAt(j);                   
                if(fieldModel.getID().equals(fieldData.getIdAttribute()))
                    continue loop;
            }
            fieldModel.getOperator().die();
        }

        // add fields to model that aren't in model
        modelFieldCount = operatorModel.getFieldCount();
        loop:
        for(int i=dataFieldCount-1;i>=0;i--) {
            IFieldWrapper fieldData = (IFieldWrapper)operatorData.getFieldAt(i);
            for(int j=0;j<modelFieldCount;j++) {
                IOperatorField fieldModel = operatorModel.getFieldAt(j);                   
                if(fieldModel.getID().equals(fieldData.getIdAttribute()))
                    continue loop;
            }
            IOperatorFieldWrapper nf = (IOperatorFieldWrapper)operatorModel.createField(fieldData.getIdAttribute());
            new FieldTranslator(nf, fieldData, BaseTranslator.UPDATE_MODEL);
        }

    }

    private static boolean match(String x, String y) {
        return((x == null && y == null) ||
               (x != null && y != null && x.equals(y)));
    }
}
