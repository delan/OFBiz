package org.ofbiz.designer.newdesigner;

import java.awt.event.ActionListener;
import org.ofbiz.designer.newdesigner.popup.*;
import org.ofbiz.designer.util.AbstractToolBar;
import org.ofbiz.designer.util.WFButtonComboBox;
import org.ofbiz.designer.util.WFRadioButton;

public class WFToolBar extends AbstractToolBar{
	// each button is represented by an ordered triple
	// button title, tooltip, and ActionCommand
	String[][] buttonInfo =
	{
		{"Select", "select component", ActionEvents.SELECT},
				{COMBO_BOX, "add new domain", "Domain"},
				{COMBO_BOX, "add new org.ofbiz.designer.task", "Task"},
				{COMBO_BOX, "add new workflow org.ofbiz.designer.task", "WFTask"},
				{COMBO_BOX, "add new dataflow arc", "Arc"},
				{"Del", "delete selection", ActionEvents.DELETE},
				{"Help", "Map Editor help", ActionEvents.HELP_INDEX},
				{RADIO_BUTTON, "toggle fail arc display", "ShowFailArcs"}
	};

	public WFToolBar(){
		setData(buttonInfo);
	}

}




