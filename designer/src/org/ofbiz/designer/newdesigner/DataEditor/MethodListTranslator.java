package org.ofbiz.designer.newdesigner.DataEditor;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.dataclass.*;
import org.ofbiz.designer.newdesigner.DataEditor.model.*;


import java.util.*;

public class MethodListTranslator extends BaseTranslator {


	public MethodListTranslator(IMethodListModelWrapper modelIn, IMethodListWrapper wrapperIn, String direction) {
		super(modelIn, wrapperIn);
		synchronize(direction);
	}
	
	private IMethod getmethodInData(String name) {
		IMethodListWrapper wrapper = (IMethodListWrapper)getDataObject();
		IMethod[] theList = wrapper.getMethods();
		for(int i=0;i<theList.length;i++) {
			if(((String)theList[i].getName()).equals(name))
				return theList[i];
		}
		return null;
	}
	
	
	public void updateModelImpl(){
		
		IMethodListModel model = (IMethodListModel) getGuiModel();
		IMethodListWrapper wrapper = (IMethodListWrapper)getDataObject();
		
		
		Vector methodModels = model.getMethods(); 
		Vector methodModel;
		IMethod methodDatum;
		
		for(int i=methodModels.size()-2;i>=0;i--) {
			methodModel = (Vector)methodModels.get(i);
			methodDatum = wrapper.getMethod((String)methodModel.get(MethodListModel.NAME_INDEX));
			if(methodDatum == null) {
				model.removeMethod(methodModel);
			}
			
		}
		
		methodModels = model.getMethods();
		IMethod[] methodData = wrapper.getMethods();
		
		for(int i = 0; i < methodData.length; i++) {
			methodModel = model.getMethod(methodData[i].getName());
			
			if(methodModel==null) {
				methodModel = model.addNewMethod("",methodData[i].getName());
				new ExceptionListTranslator((IExceptionListModelWrapper)model.getExceptionListAt(methodModels.size()-2),
											(IMethodWrapper)methodData[i],
											BaseTranslator.UPDATE_MODEL);
				new ParamListTranslator((IFieldListModelWrapper)model.getParamListAt(methodModels.size()-2),
											(IParametersListWrapper)methodData[i].getParametersList(),
											BaseTranslator.UPDATE_MODEL);
			}
			
			methodModel.setElementAt(((ITypeWrapper)methodData[i].getType()).toString(),MethodListModel.TYPE_INDEX);
		}
	}
	
		
	public void updateDataImpl(){
		
		IMethodListModel model = (IMethodListModel) getGuiModel();
		IMethodListWrapper wrapper = (IMethodListWrapper)getDataObject();
		
		
		Vector methodModels = model.getMethods(); 
		Vector methodModel;
		String methodName;
		String typeStr;
		IMethod methodDatum;
		
		for(int i=methodModels.size()-2;i>=0;i--) {
			methodModel = (Vector)methodModels.get(i);
			methodName = (String)methodModel.get(MethodListModel.NAME_INDEX);
			typeStr=(String)methodModel.get(MethodListModel.TYPE_INDEX);
			if((typeStr!=null)&&(methodName!=null)) {
				methodDatum = getmethodInData(methodName);
				if(methodDatum==null) {
					methodDatum = wrapper.createMethod();
					methodDatum.setName(methodName);
					new ExceptionListTranslator((IExceptionListModelWrapper)model.getExceptionListAt(i),
												(IMethodWrapper)methodDatum,
												BaseTranslator.UPDATE_DATA);
					new ParamListTranslator((IFieldListModelWrapper)model.getParamListAt(i),
												(IParametersListWrapper)methodDatum.getParametersList(),
												BaseTranslator.UPDATE_DATA);
				}
	
				((ITypeWrapper)methodDatum.getType()).parseAndSet(typeStr);
			}
		}
		
		
		methodModels = model.getMethods();
		IMethod[] methodData = wrapper.getMethods();
		
		for(int i = methodData.length-1; i >= 0; i--) {
			methodModel = model.getMethod(methodData[i].getName());
			if(methodModel==null) {
				wrapper.removeMethodAt(i);
			}
		}

	}            

}

