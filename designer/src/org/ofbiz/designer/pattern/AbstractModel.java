package org.ofbiz.designer.pattern;

import org.ofbiz.designer.util.*;

public class AbstractModel implements IModel{
	private IView view;
	private IModel realModel;

	public AbstractModel(IModel realModelIn){
		realModel = realModelIn;
	}
	
	public AbstractModel(){
	}
	
	public void setRealModel(IModel realModelIn){
		realModel = realModelIn;
	}
	
	public void setGui(IView viewIn){
		//if (!equals(viewIn.getModel()))
		if (view == viewIn)
			return;
		
		if (viewIn != null && 
				(viewIn.getModel() == null || 
				 !viewIn.getModel().equals(realModel)
				 )
			) 
			throw new RuntimeException("The model of the view " + viewIn.getModel() + " does not equal " +  realModel);
		view = viewIn;
	}
	
	public IView getGui(){
		return view;
	}
	
	public void synchronizeGui(){
		if (view != null)
			view.synchronize();
	}
}
