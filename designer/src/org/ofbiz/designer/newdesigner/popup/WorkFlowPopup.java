package org.ofbiz.designer.newdesigner.popup;

import org.ofbiz.designer.util.*;

public class WorkFlowPopup extends WFPopup {
	// {label, actionCommand}
	static String[][] menuItemInfo = {
		{"Insert Domain", ActionEvents.ADD_DOMAIN}, 
		{"View up", ActionEvents.VIEW_UP}, 
                //{"Show Properties", ActionEvents.PROPERTIES_WF},
		//{"Find", ActionEvents.FIND}, {"Help", ActionEvents.HELP_CONTEXT}
	};

	public WorkFlowPopup(){
		super(menuItemInfo, "org.ofbiz.designer.newdesigner.popup");
  }
}


