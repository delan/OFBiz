package org.ofbiz.designer.newdesigner.DataEditor;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.dataclass.*;
import org.ofbiz.designer.newdesigner.DataEditor.model.IFieldListModelWrapper;
import org.ofbiz.designer.newdesigner.DataEditor.model.IFieldListModel;
import org.ofbiz.designer.newdesigner.DataEditor.model.FieldListModel;

import java.util.*;

public class ParamListTranslator extends BaseTranslator {


	public ParamListTranslator(IFieldListModelWrapper modelIn, IParametersListWrapper wrapperIn, String direction) {
		super(modelIn, wrapperIn);
		synchronize(direction);
	}
	
	public void updateModelImpl(){
		
		IFieldListModel model = (IFieldListModel) getGuiModel();
		IParametersListWrapper wrapper = (IParametersListWrapper)getDataObject();
		
		
		Vector paramModels = model.getFields(); 
		Vector paramModel;
		IParameter paramDatum;
		
		for(int i=paramModels.size()-2;i>=0;i--) {
			paramModel = (Vector)paramModels.get(i);
			paramDatum = wrapper.getParameter((String)paramModel.get(FieldListModel.NAME_INDEX));
			if(paramDatum == null) {
				model.removeField(paramModel);
			}
		}
		
		paramModels = model.getFields();
		IParameter[] paramData = wrapper.getParameters();
		
		for(int i = 0; i < paramData.length; i++) {
			paramModel = model.getField(paramData[i].getName());
			
			if(paramModel==null) {
				paramModel = model.addNewField("",paramData[i].getName(),"");
			}
				
			String defVal = paramData[i].getDefaultValue();	  
			if(defVal==null)
				defVal = "";
			
			paramModel.setElementAt(defVal,FieldListModel.DEFAULT_INDEX);
			
			paramModel.setElementAt(((ITypeWrapper)paramData[i].getType()).toString(),FieldListModel.TYPE_INDEX);
		}
	}
	
		
	public void updateDataImpl(){
		
		IFieldListModel model = (IFieldListModel) getGuiModel();
		IParametersListWrapper wrapper = (IParametersListWrapper)getDataObject();
		
		
		Vector paramModels = model.getFields(); 
		Vector paramModel;
		String paramName;
		String typeStr;
		String defVal;
		IParameter paramDatum;
		
		for(int i=paramModels.size()-2;i>=0;i--) {
			paramModel = (Vector)paramModels.get(i);
			paramName = (String)paramModel.get(FieldListModel.NAME_INDEX);
			typeStr=(String)paramModel.get(FieldListModel.TYPE_INDEX);
			defVal = (String)paramModel.get(FieldListModel.DEFAULT_INDEX);
			if((typeStr!=null)&&(paramName!=null)) {
				paramDatum = wrapper.getParameter(paramName);
				if(paramDatum==null) {
					paramDatum = wrapper.createParameter();
					paramDatum.setName(paramName);
				}
	
				((ITypeWrapper)paramDatum.getType()).parseAndSet(typeStr);
	
				if(defVal!=null) {
					paramDatum.setDefaultValue(defVal);
				}
			}
		}
		
		
		paramModels = model.getFields();
		IParameter[] paramData = wrapper.getParameters();
		
		for(int i = paramData.length-1; i >= 0; i--) {
			paramModel = model.getField(paramData[i].getName());
			if(paramModel==null) {
				wrapper.removeParameterAt(i);
			}
		}

	}            

}
