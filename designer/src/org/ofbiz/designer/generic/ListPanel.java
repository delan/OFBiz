package org.ofbiz.designer.generic;

import javax.swing.*;
import java.awt.*;

public class ListPanel extends JPanel{
	public JList list = new JList();
	public JLabel label = new JLabel();
								
	public ListPanel(String listName){
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JScrollPane jScrollList = new JScrollPane();
		list.setName(listName);
		label.setText(listName);
		jScrollList.getViewport().add(list);
		add(label);
		add(jScrollList);
	}
}

