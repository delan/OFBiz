package org.ofbiz.designer.newdesigner.popup;

import org.ofbiz.designer.util.WFPopup;

public class OperatorPopup extends WFPopup {
	// {label, actionCommand}
	static String[][] menuItemInfo = {
		{"Delete", ActionEvents.DELETE},
		 {"Set Type AND", ActionEvents.SET_TYPE_AND},
		 {"Set Type OR", ActionEvents.SET_TYPE_OR},
		 {"Help", ActionEvents.HELP_CONTEXT}
	};

	public OperatorPopup(){
		super(menuItemInfo, "org.ofbiz.designer.newdesigner.popup");
  }
}

