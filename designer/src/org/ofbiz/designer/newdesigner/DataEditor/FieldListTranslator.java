package org.ofbiz.designer.newdesigner.DataEditor;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.dataclass.*;
import java.util.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.newdesigner.DataEditor.model.IFieldListModelWrapper;
import org.ofbiz.designer.newdesigner.DataEditor.model.IFieldListModel;
import org.ofbiz.designer.newdesigner.DataEditor.model.FieldListModel;

public class FieldListTranslator extends BaseTranslator {
	public FieldListTranslator(IFieldListModelWrapper modelIn, IFieldListWrapper wrapperIn, String direction) {
		super(modelIn, wrapperIn);
		synchronize(direction);
	}
	
	public void updateModelImpl(){
		Object modelObj = getGuiModel();
		IFieldListModel model = null;
		try{
			//IFieldListModel model = (IFieldListModel) getGuiModel();
			model = (IFieldListModel) getGuiModel();
		} catch (Exception e){
			LOG.println(":::");
			e.printStackTrace();
		}
		IFieldListWrapper wrapper = (IFieldListWrapper)getDataObject();
		
		
		Vector fieldModels = model.getFields(); 
		Vector fieldModel;
		IField fieldDatum;
		
		for(int i=fieldModels.size()-2;i>=0;i--) {
			fieldModel = (Vector)fieldModels.get(i);
			fieldDatum = wrapper.getField((String)fieldModel.get(FieldListModel.NAME_INDEX));
			if(fieldDatum == null) {
				model.removeField(fieldModel);
			}
		}
		
		fieldModels = model.getFields();
		IField[] fieldData = wrapper.getFields();
		
		for(int i = 0; i < fieldData.length; i++) {
			fieldModel = model.getField(fieldData[i].getName());
			
			if(fieldModel==null) {
				fieldModel = model.addNewField("",fieldData[i].getName(),"");
			}
				
			String defVal = fieldData[i].getDefaultValue();	  
			if(defVal==null)
				defVal = "";
			
			fieldModel.setElementAt(defVal,FieldListModel.DEFAULT_INDEX);
			
			fieldModel.setElementAt(((ITypeWrapper)fieldData[i].getType()).toString(),FieldListModel.TYPE_INDEX);
		}
	}
	
		
	public void updateDataImpl(){
		
		IFieldListModel model = (IFieldListModel) getGuiModel();
		IFieldListWrapper wrapper = (IFieldListWrapper)getDataObject();
		
		
		Vector fieldModels = model.getFields(); 
		Vector fieldModel;
		String fieldName;
		String typeStr;
		String defVal;
		IField fieldDatum;
		
		for(int i=fieldModels.size()-2;i>=0;i--) {
			fieldModel = (Vector)fieldModels.get(i);
			fieldName = (String)fieldModel.get(FieldListModel.NAME_INDEX);
			typeStr=(String)fieldModel.get(FieldListModel.TYPE_INDEX);
			defVal = (String)fieldModel.get(FieldListModel.DEFAULT_INDEX);
			if((typeStr!=null)&&(fieldName!=null)) {
				fieldDatum = wrapper.getField(fieldName);
				if(fieldDatum==null) {
					fieldDatum = wrapper.createField();
					fieldDatum.setName(fieldName);
				}
	
				((ITypeWrapper)fieldDatum.getType()).parseAndSet(typeStr);
				
				if(defVal!=null) {
					fieldDatum.setDefaultValue(defVal);
				}
			}
		}
		
		
		fieldModels = model.getFields();
		IField[] fieldData = wrapper.getFields();
		
		for(int i = fieldData.length-1; i >= 0; i--) {
			fieldModel = model.getField(fieldData[i].getName());
			if(fieldModel==null) {
				wrapper.removeFieldAt(i);
			}
		}

	}            

}
