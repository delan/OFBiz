package org.ofbiz.designer.newdesigner.DataEditor;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.dataclass.*;
import org.ofbiz.designer.newdesigner.DataEditor.model.IExceptionListModelWrapper;
import org.ofbiz.designer.newdesigner.DataEditor.model.IExceptionListModel;
import org.ofbiz.designer.newdesigner.DataEditor.model.ExceptionListModel;

import java.util.*;

public class ExceptionListTranslator extends BaseTranslator {


	public ExceptionListTranslator(IExceptionListModelWrapper modelIn, IMethodWrapper wrapperIn, String direction) {
		super(modelIn, wrapperIn);
		synchronize(direction);
	}
	
	public void updateModelImpl(){
		
		IExceptionListModel model = (IExceptionListModel) getGuiModel();
		IMethodWrapper wrapper = (IMethodWrapper)getDataObject();
		
		
		Vector exceptionModels = model.getExceptions(); 
		Vector exceptionModel;
		String exceptionDatum;
		
		for(int i=exceptionModels.size()-2;i>=0;i--) {
			exceptionModel = (Vector)exceptionModels.get(i);
			if(!wrapper.containsException((String)exceptionModel.get(ExceptionListModel.NAME_INDEX))) {
				model.removeException(exceptionModel);
			}
		}
		
		exceptionModels = model.getExceptions();
		String[] exceptionData = wrapper.getExceptions();
		
		for(int i = 0; i < wrapper.getExceptionCount(); i++) {
			exceptionModel = model.getException(wrapper.getExceptionAt(i));
			
			if(exceptionModel==null) {
				exceptionModel = model.addNewException(wrapper.getExceptionAt(i));
			}
		
		}
	}
	
		
	public void updateDataImpl(){
		
		IExceptionListModel model = (IExceptionListModel) getGuiModel();
		IMethodWrapper wrapper = (IMethodWrapper)getDataObject();
		
		
		Vector exceptionModels = model.getExceptions(); 
		Vector exceptionModel;
		String exceptionName;
		
		for(int i=exceptionModels.size()-2;i>=0;i--) {
			exceptionModel = (Vector)exceptionModels.get(i);
			exceptionName = (String)exceptionModel.get(ExceptionListModel.NAME_INDEX);
			if(!wrapper.containsException(exceptionName)) {
				wrapper.addException(exceptionName);
			}
		}
		
		
		exceptionModels = model.getExceptions();
		
		for(int i = wrapper.getExceptionCount()-1; i >= 0; i--) {
			exceptionModel = model.getException(wrapper.getExceptionAt(i));
			if(exceptionModel==null) {
				wrapper.removeExceptionAt(i);
			}
		}

	}            

}
