package org.ofbiz.designer.newdesigner.LatticeEditor;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.generic.*;
import java.util.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.roledomain.*;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.IRoleModelWrapper;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.IRoleModel;

import java.awt.*;

							


public class RoleTranslator extends BaseTranslator {
	
	
	RoleTranslator(IRoleModelWrapper modelIn, IRoleWrapper wrapperIn, String direction) {
		super(modelIn, wrapperIn);
		synchronize(direction);
	}
	
	public void updateModelImpl(){
		IRoleWrapper wrapper = (IRoleWrapper)getDataObject();
		IRoleModel model = (IRoleModel)getGuiModel();
		     
		model.beginTransaction();
		
		try{
			model.setLocation(new Point(Integer.parseInt(wrapper.getPosition().getNumber1()),
												   Integer.parseInt(wrapper.getPosition().getNumber2())));
		} catch (NullPointerException e){}
		model.setName(wrapper.getName());
		model.setId(wrapper.getIdAttribute());
		model.setColor(new java.awt.Color(Integer.parseInt(wrapper.getColor().getNumber1()),
										  Integer.parseInt(wrapper.getColor().getNumber2()),
										  Integer.parseInt(wrapper.getColor().getNumber3())));
		model.setDescription(wrapper.getDescription());
		model.setPriveleges(wrapper.getPrivileges());
		
		model.commitTransaction();
		
	}
		
	public void updateDataImpl(){
		IRoleWrapper wrapper = (IRoleWrapper)getDataObject();
		IRoleModel model = (IRoleModel)getGuiModel();
		
		wrapper.setName(model.getName());
		
		IPositionWrapper posWrap = (IPositionWrapper) wrapper.getPosition();
		if(posWrap!=null) {
			posWrap.setNumber1(Integer.toString(model.getLocation().x));
			posWrap.setNumber2(Integer.toString(model.getLocation().y));
		}
		
		
		
		IColorWrapper colorWrap = (IColorWrapper) wrapper.getColor();
		if(colorWrap!=null) {
			colorWrap.setNumber1(Integer.toString(model.getColor().getRed()));
			colorWrap.setNumber2(Integer.toString(model.getColor().getGreen()));
			colorWrap.setNumber3(Integer.toString(model.getColor().getBlue()));
		}
		
		wrapper.setDescription(model.getDescription());
		wrapper.setPrivileges(model.getPriveleges());
	}
}
