package org.ofbiz.designer.generic;

import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.util.*;
import java.lang.reflect.*;


public class CheckBoxTranslator extends BaseTranslator {
	String isMethodName, setMethodName;
	
	// isMethodName is the name of a method with the signature
	// public boolean isMethodName();
	// setMethodName is the name of a method with the signature
	// public void setMethodName(boolean value);
	
	public CheckBoxTranslator(ICheckBoxWrapper modelIn, IRegistrar dataObjectIn, String _isMethodName, String _setMethodName, String mode) {
		super(modelIn, dataObjectIn);
		if (_isMethodName == null || _setMethodName == null) 
			throw new RuntimeException("NULL PARAMETER ENCOUNTERED!!");
		isMethodName = _isMethodName;
		setMethodName = _setMethodName;
		synchronize(mode);
	}
	
	public void updateDataImpl() {
		try{
			CheckBoxModelImpl model = (CheckBoxModelImpl)getGuiModel();
			boolean oldValue = ((Boolean)getDataObject().getClass().getMethod(isMethodName, null).invoke(getDataObject(), null)).booleanValue();
			LOG.println("model.isSelected() is " + model.isSelected());
			LOG.println("oldValue is " + oldValue);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void updateModelImpl () {
		try{
			CheckBoxModelImpl model = (CheckBoxModelImpl)getGuiModel();
			boolean oldValue = ((Boolean)getDataObject().getClass().getMethod(isMethodName, null).invoke(getDataObject(), null)).booleanValue();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}

