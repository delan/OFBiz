package org.ofbiz.designer.domainenv;

import org.ofbiz.designer.pattern.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class DomainInfoSupportClass extends AbstractDataSupportClass implements IDomainInfoSupportClass {
	public java.awt.Color getAWTColor(){
		IDomainInfo data = (IDomainInfo)getDtdObject();
		IColor color = data.getColor();
		String st1 = color.getNumber1();
		String st2 = color.getNumber2();
		String st3 = color.getNumber3();
		
		int r = Integer.parseInt(st1);
		int g = Integer.parseInt(st2);
		int b = Integer.parseInt(st3);
		
		return new java.awt.Color(r, g, b);
	}
}
