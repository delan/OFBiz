package org.ofbiz.designer.pattern;

import javax.swing.*;
import org.ofbiz.designer.util.*;

public abstract class AbstractView extends JComponent implements IView{
	private IModel model;
	
	public IModel getModel(){
		return model;
	}
	
	public void setModel(IModel modelIn){
		if (model == modelIn)
			return;
		if (modelIn != null && !(modelIn instanceof IModelProxySupportClass))
			throw new RuntimeException("Model is not a Proxy");
		if (model != null)
			model.setGui(null);
		model = modelIn;
		model.setGui(this);
	}
}
