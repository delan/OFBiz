package org.ofbiz.designer.newdesigner.popup;

import org.ofbiz.designer.util.*;

public class CompartmentPopup extends WFPopup {
	// {label, actionCommand}
	static String[][] menuItemInfo = {
		//{"Insert", SUBMENU + "InsertCompartmentMenu"}, 
													   
	    {"Delete", ActionEvents.DELETE}, 
		{"Insert", SUBMENU +  "DomainInsertPopup"}, {"", ActionEvents.SEPERATOR},
		{"View up", ActionEvents.VIEW_UP},
	   // {"Compartment Editor", ActionEvents.COMPARTMENT_ED}, 
		//{"Find", ActionEvents.FIND},
	    //{"Help", ActionEvents.HELP_CONTEXT}
	};

	public CompartmentPopup(){
		super(menuItemInfo, "org.ofbiz.designer.newdesigner.popup");
  }
}

