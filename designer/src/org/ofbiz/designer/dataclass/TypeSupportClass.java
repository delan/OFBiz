package org.ofbiz.designer.dataclass;

import org.ofbiz.designer.pattern.*;
import java.util.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class TypeSupportClass extends AbstractDataSupportClass implements ITypeSupportClass {
	
	private static final HashSet simpleTypes = new HashSet();
	
	static {
		simpleTypes.add("boolean");
		simpleTypes.add("char");
		simpleTypes.add("wchar");
		simpleTypes.add("string");
		simpleTypes.add("wstring");
		simpleTypes.add("short");
		simpleTypes.add("long");
		simpleTypes.add("long long");
		simpleTypes.add("float");
		simpleTypes.add("any");
		simpleTypes.add("unsigned short");
		simpleTypes.add("unsigned long");
		simpleTypes.add("unsigned long long");
		simpleTypes.add("double");
		simpleTypes.add("octet");
	}
	
	public String toString() {
		String type = null;
		IType theType = (IType)getDtdObject();
		IUrl theUrl = ((IType)getDtdObject()).getSimpleTypeOrUrl().getUrl();
		if(theUrl==null)
			type = theType.getSimpleTypeOrUrl().getSimpleType();
		else
			type = theUrl.getHrefAttribute();
		
		for(int j=0;j<theType.getDimensionCount();j++) {
			type = type+"["+theType.getDimensionAt(j)+"]";
		}
		return type;
		
	}
	
	public void parseAndSet(String typeStr) {
		String typeName = getTypeNameFromType(typeStr);
		IType theType = (IType)getDtdObject();
		if(simpleTypes.contains(typeName)) {
			theType.getSimpleTypeOrUrl().setSimpleType(typeName);
		}
		else {
			IUrl newUrl = new Url();
			newUrl.setHrefAttribute(typeName);
			theType.getSimpleTypeOrUrl().setUrl(newUrl);
		}
		theType.removeAllDimensions();
		
		/*
		for(int k=(fieldTypeData.getDimensionCount()-1);k>=0;k--) {
			fieldTypeData.removeDimensionAt(k);
		}
		*/
		
		Vector dims = getDimensionsFromType(typeStr);
		for(int j=0;j<dims.size();j++) {
			theType.addDimension(((Integer)dims.get(j)).toString());
		}
	}
	
	private String getTypeNameFromType(String type) {
		String returnStr = "";
		for(int i=0;i<type.length();i++) {
			if(type.charAt(i)=='[')
				return returnStr;
			returnStr = returnStr+type.charAt(i);
		}
		return returnStr;
	}
	
	public Vector getDimensionsFromType(String type) {
		Vector returnObj = new Vector();
		String numStr = null;
		boolean readingDimSize = false;
		
		for(int i=0;i<type.length();i++) {
			switch(type.charAt(i)) {
			case '[':
				numStr = "";
				readingDimSize=true;
				break;
			case ']':
				returnObj.add(Integer.valueOf(numStr));
				readingDimSize = false;
				break;
			default:
				if(readingDimSize)
					if(Character.isDigit(type.charAt(i)))
						numStr = numStr+type.charAt(i);
					else throw new RuntimeException("Array size specification must be an integer.");
			}
		}
		if(readingDimSize)
				throw new RuntimeException("Bracket in Dimension Size specification not closed.");
		
			return returnObj;
	}
}
