package org.ofbiz.designer.dataclass;

import org.ofbiz.designer.pattern.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class DataClassSupportClass extends AbstractDataSupportClass implements IDataClassSupportClass {

	public IParent createParent() {
		IParent newParent = new Parent();
		IUrl newUrl = new Url();
		newUrl.setHrefAttribute("");
		newParent.setUrl(newUrl);
		((IDataClass) getDtdObject()).setParent(newParent);
		return newParent;
	}
	
	
}
