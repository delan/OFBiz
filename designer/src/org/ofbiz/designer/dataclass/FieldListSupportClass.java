package org.ofbiz.designer.dataclass;

import org.ofbiz.designer.pattern.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class FieldListSupportClass extends AbstractDataSupportClass implements IFieldListSupportClass {

	public IField createField() {
		IField newField = new Field();
		newField.setType(new Type());
		newField.getType().setSimpleTypeOrUrl(new SimpleTypeOrUrl());
		((IFieldList) getDtdObject()).addField(newField);
		return newField;
	}
	
	public IField getField(String name) {
		IFieldList fieldList = (IFieldList)getDtdObject();
		IField[] theList = fieldList.getFields();
		for(int i=0;i<theList.length;i++) {
			if(((String)theList[i].getName()).equals(name))
				return theList[i];
		}
		return null;
	}

}
