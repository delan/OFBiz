package org.ofbiz.designer.newdesigner.popup;

import org.ofbiz.designer.util.*;

public class TaskPopup extends WFPopup {
	//public static final String ALTERNATIVE_TASK_SUBMENU = "AlternativeTaskSubMenu";
	//public static final String EXISTING_ALTERNATIVE_TASK_SUBMENU = "ExistingAlternativeTaskSubMenu";
	//public static final String DELETE_ALTERNATIVE_TASK_SUBMENU = "DeleteAlternativeTaskSubMenu";
	
	// {label, actionCommand}
	static String[][] menuItemInfo = {
		{"Delete", ActionEvents.DELETE},
        {"", ActionEvents.SEPERATOR},
	    {"Add success arc", ActionEvents.ADD_ARC_SOURCE_SUCCESS}, 
		{"Add fail arc", ActionEvents.ADD_ARC_SOURCE_FAIL},
        {"", ActionEvents.SEPERATOR},
		{"Operator input arcs", ActionEvents.OPERATOR_ED_INPUTS},
		{"Operator success arcs", ActionEvents.OPERATOR_ED_SUCCESS}, 
		//{"Operator fail arcs", ActionEvents.OPERATOR_ED_FAIL},
        {"", ActionEvents.SEPERATOR},
	    {"View Realization", ActionEvents.VIEW_REALIZATION}, 
		{"View up", ActionEvents.VIEW_UP},
        {"Task Editor", ActionEvents.TASK_ED}, 
        {"", ActionEvents.SEPERATOR},
        {"Set As StartTask", ActionEvents.SET_AS_STARTTASK}, 
        {"Set As EndTask", ActionEvents.SET_AS_ENDTASK}, 
        {"UnSet As StartTask", ActionEvents.UNSET_AS_STARTTASK}, 
        {"UnSet As EndTask", ActionEvents.UNSET_AS_ENDTASK}, 
		//{"Alternative Task", WFPopup.DYNAMICSUBMENU + "InsertArcSubMenu"},
		//{"Alternative Task", DYNAMICSUBMENU + ALTERNATIVE_TASK_SUBMENU},
		//{"", ActionEvents.SEPERATOR},
		//{"Help", ActionEvents.HELP_CONTEXT}
	};

	public TaskPopup(){
		super(menuItemInfo, "org.ofbiz.designer.newdesigner.popup");
	}
}
