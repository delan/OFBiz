
/**
 *	MultipleTextAreaGUI.java
 * 
 *	This is a utility GUI template class that, as the name implies, represents a 
 *	panel containing one or more TextArea subcomponents.  The component names and 
 *	labels of each of the TextAreas can be specified by String arrays.  The component
 *	name is used to subsequently bind the TextArea with a model from a dxml generated
 *	object.
 * 
 */


package org.ofbiz.designer.generic;

import javax.swing.*;

public class MultipleTextAreaGUI extends JPanel{
	public MultipleTextAreaGUI (String[] componentNames, String[] labels){
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		for (int i=0; i<labels.length; i++)
			addField(componentNames[i], labels[i]);
	}

	void addField(String componentName,String label){
		JTextArea textArea = new JTextArea();
		textArea.setName(componentName);
		JScrollPane scrollPane = new JScrollPane();
		add(new JLabel(label));
		add(scrollPane);
		scrollPane.getViewport().add(textArea);
	}
}
