package org.ofbiz.designer.newdesigner.popup;

import org.ofbiz.designer.util.*;

public class DomainInsertPopup extends WFSubMenu {
	//public static final String ALTERNATIVE_TASK_SUBMENU = "AlternativeTaskSubMenu";
	//public static final String EXISTING_ALTERNATIVE_TASK_SUBMENU = "ExistingAlternativeTaskSubMenu";
	//public static final String DELETE_ALTERNATIVE_TASK_SUBMENU = "DeleteAlternativeTaskSubMenu";
	
	// {label, actionCommand}
	static String[][] menuItemInfo = {
		{"Insert Human Task", ActionEvents.INSERT_HUMAN_TASK},
		{"Insert Transactional Task", ActionEvents.INSERT_TRANSACTIONAL_TASK},
		{"Insert NonTransactional Task", ActionEvents.INSERT_NONTRANSACTIONAL_TASK},
		{"Insert Collaboration Task", ActionEvents.INSERT_COLLABORATION_TASK},
		{"", ActionEvents.SEPERATOR},
		{"Insert NonTransactional Workflow", ActionEvents.INSERT_NONTRANSACTIONAL_WORKFLOW},
		{"Insert Transactional Workflow", ActionEvents.INSERT_TRANSACTIONAL_WORKFLOW},
		{"Insert Composite Workflow", ActionEvents.INSERT_COMPOSITE_WORKFLOW},
		{"Insert Open2PC Workflow", ActionEvents.INSERT_OPEN2PC_WORKFLOW},
		{"", ActionEvents.SEPERATOR},
		{"Insert Compartment", ActionEvents.INSERT_COMPARTMENT}
	};

	public DomainInsertPopup(){
		//super(menuItemInfo, "org.ofbiz.designer.newdesigner.popup");
		setMenu(menuItemInfo);
	}
}
