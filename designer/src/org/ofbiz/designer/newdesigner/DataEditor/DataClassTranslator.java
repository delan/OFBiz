package org.ofbiz.designer.newdesigner.DataEditor;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.dataclass.*;
import org.ofbiz.designer.newdesigner.DataEditor.model.IDataClassModelWrapper;
import org.ofbiz.designer.newdesigner.DataEditor.model.IFieldListModelWrapper;
import org.ofbiz.designer.newdesigner.DataEditor.model.IMethodListModelWrapper;
import org.ofbiz.designer.newdesigner.DataEditor.model.IDataClassModel;


public class DataClassTranslator extends BaseTranslator {
	
	DataClassTranslator(IDataClassModelWrapper modelIn, IDataClassWrapper wrapperIn, String direction) {
		super(modelIn, wrapperIn);
		synchronize(direction);
		new FieldListTranslator((IFieldListModelWrapper)modelIn.getFieldList(),(IFieldListWrapper)wrapperIn.getFieldList(),direction);
		new MethodListTranslator((IMethodListModelWrapper)modelIn.getMethodList(),(IMethodListWrapper)wrapperIn.getMethodList(),direction);
	}
	
	public void updateModelImpl(){
		IDataClassWrapper wrapper = (IDataClassWrapper)getDataObject();
		IDataClassModel model = (IDataClassModel)getGuiModel();
		
		model.setName(wrapper.getName());
		model.setPackage(wrapper.getPackage());
		if(wrapper.getParent()!=null) {
			model.setParent(wrapper.getParent().getUrl().getHrefAttribute());
		}
		else {
			model.setParent(null);
		}
		
		
	}
		
	public void updateDataImpl(){
		IDataClassWrapper wrapper = (IDataClassWrapper)getDataObject();
		IDataClassModel model = (IDataClassModel)getGuiModel();
		
		wrapper.setName(model.getName());
		wrapper.setPackage(model.getPackage());
		if(model.getParent()==null) {
			wrapper.removeParent();
		}
		else {
			if(wrapper.getParent()==null) {
				wrapper.createParent();
			}
			wrapper.getParent().getUrl().setHrefAttribute(model.getParent());
		}
	}
}
