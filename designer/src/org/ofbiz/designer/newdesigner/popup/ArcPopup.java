package org.ofbiz.designer.newdesigner.popup;

import org.ofbiz.designer.util.WFPopup;

public class ArcPopup extends WFPopup {
	// {label, actionCommand}
	static String[][] menuItemInfo = {
		//{"Delete", WFPopup.DYNAMICSUBMENU + "DeleteArcSubMenu"}, // <-------- do not delete
		{"Delete", ActionEvents.DELETE},
		 {"View up", ActionEvents.VIEW_UP},
		 {"Alternative Transititon", ActionEvents.ADD_ARC_SOURCE_SUCCESS},
		//{"Arc Editor", WFPopup.DYNAMICSUBMENU + "ArcEditorSubMenu"},// <-------- do not delete
		{"Arc Editor", ActionEvents.ARC_ED},
		 {"Help", ActionEvents.HELP_CONTEXT}
	};

	public ArcPopup(){
		super(menuItemInfo, "org.ofbiz.designer.newdesigner.popup");
  }
}

