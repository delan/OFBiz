package org.ofbiz.designer.newdesigner;

import javax.swing.*;
import org.ofbiz.designer.util.*;
import java.awt.event.*;
import javax.swing.border.*;
import org.ofbiz.designer.newdesigner.popup.*;
import java.util.*;
import org.ofbiz.designer.generic.*;

class CorbaRealizationView extends JPanel implements ActionListener {
	JLabel objectLabel = new JLabel("Object Marker");
	JLabel serverNameLabel = new JLabel("Server Name");
	JLabel serverHostLabel = new JLabel("Server Host");
	JLabel classNameLabel = new JLabel("Class Name");
	JLabel methodNameLabel = new JLabel("Method Name");
	JLabel returnValueLabel = new JLabel("Return Value");
	JLabel parametersLabel = new JLabel("Parameters");
	JLabel tmInputParametersLabel = new JLabel("Task Input Parameters");
	JLabel tmOutputParametersLabel = new JLabel("Task Output Parameters");
	JLabel forwardMappingLabel = new JLabel("Input Mapping");
	JLabel reverseMappingLabel = new JLabel("Return Value Mapping");

	JTextField objectField, serverNameField, serverHostField, classNameField, methodNameField, returnValueField, returnValueMapping;

	JList parametersList, tmInputParametersList, tmOutputParametersList, forwardMappingList;

	IListWrapper parameters, tmInputParameters, tmOutputParameters, fmappings;//, rmappings;
	IDocumentWrapper objectMarker, serverName, serverHost, className, methodName, returnValue, rMapping;

	JButton[] buttons = null;
	{
		String[] buttonTitles = {
			ActionEvents.NEW_PARAMETER, 
			ActionEvents.REMOVE_PARAMETER, 
			ActionEvents.REMOVE_ALL_PARAMTERS, 
			ActionEvents.ADD_INPUT_MAPPING, 
			ActionEvents.REMOVE_INPUT_MAPPING, 
			ActionEvents.REMOVE_ALL_INPUT_MAPPING, 
			ActionEvents.SET_REVERSE_MAPPING, 
			ActionEvents.UNSET_REVERSE_MAPPING
		};

		buttons = new JButton[buttonTitles.length];
		for (int i=0; i<buttons.length; i++) {
			buttons[i] = new JButton(buttonTitles[i]);
			buttons[i].addActionListener(this);
		}
	}

	public static void main(String[] args) {
		WFFrame frame = new WFFrame("Corba Realization");
		frame.getContentPane().add(new CorbaRealizationView());
		frame.setVisible(true);
	}

	public CorbaRealizationView() {
		objectField = new JTextField("objectField");
		serverNameField = new JTextField("serverNameField");
		serverHostField = new JTextField("serverHostField");
		classNameField = new JTextField("classNameField");
		methodNameField = new JTextField("methodNameField");
		returnValueField = new JTextField("returnValueField");
		returnValueMapping = new JTextField("returnValueMapping");
		parametersList = new JList();
		tmInputParametersList = new JList();
		tmOutputParametersList = new JList();
		forwardMappingList = new JList();

		returnValueMapping.setEnabled(false);

		// the following fields have default borders different from a textfield 
		Border border = objectField.getBorder();
		parametersList.setBorder(border);
		tmInputParametersList.setBorder(border);
		tmOutputParametersList.setBorder(border);
		forwardMappingList.setBorder(border);

		setLayout(null);
		add(objectLabel);
		add(serverNameLabel);
		add(serverHostLabel);
		add(classNameLabel);
		add(methodNameLabel);
		add(returnValueLabel);
		add(parametersLabel);
		add(tmInputParametersLabel);
		add(tmOutputParametersLabel);
		add(forwardMappingLabel);
		add(reverseMappingLabel);
		add(objectField);
		add(serverNameField);
		add(serverHostField);
		add(classNameField);
		add(methodNameField);
		add(returnValueField);
		add(parametersList);
		add(tmInputParametersList);
		add(tmOutputParametersList);
		add(forwardMappingList);
		add(returnValueMapping);

		for (int i=0; i<buttons.length; i++)
			add(buttons[i]);

		addComponentListener(new ComponentAdapter() {
								 public void componentResized(ComponentEvent e) {
									 relayout();
								 }
							 });
		relayout();
	}


	public void relayout() {
		int width = getBounds().width;
		int height = getBounds().height;

		int labelHeight = 30;
		int margin = 10;
		int fieldWidth = (width - 4*margin)/3;
		int fieldHeight = 30;
		int buttonWidth = 200;
		int listWidth = (width - buttonWidth - 5*margin)/3;
		int listHeight = height - 3*labelHeight - 2*fieldHeight - 2*margin;
		int buttonHeight = listHeight/8;
		if (buttonHeight>30) {
			buttonHeight = 30;
		}
		int x=margin, y=margin;

		objectLabel.setBounds(x, y, fieldWidth, labelHeight);
		serverNameLabel.setBounds(x+=fieldWidth+margin, y, fieldWidth, labelHeight);
		serverHostLabel.setBounds(x+=fieldWidth+margin, y, fieldWidth, labelHeight);

		x = margin;
		y += labelHeight;

		objectField.setBounds(x, y, fieldWidth, fieldHeight);
		serverNameField.setBounds(x+=fieldWidth+margin, y, fieldWidth, fieldHeight);
		serverHostField.setBounds(x+=fieldWidth+margin, y, fieldWidth, fieldHeight);

		x = margin;
		y += fieldHeight;

		classNameLabel.setBounds(x, y, fieldWidth, labelHeight);
		methodNameLabel.setBounds(x+=fieldWidth+margin, y, fieldWidth, labelHeight);
		returnValueLabel.setBounds(x+=fieldWidth+margin, y, fieldWidth, labelHeight);

		x = margin;
		y += labelHeight;

		classNameField.setBounds(x, y, fieldWidth, fieldHeight);
		methodNameField.setBounds(x+=fieldWidth+margin, y, fieldWidth, fieldHeight);
		returnValueField.setBounds(x+=fieldWidth+margin, y, fieldWidth, fieldHeight);

		x = margin;
		y += fieldHeight;

		tmInputParametersLabel.setBounds(x+=buttonWidth+margin, y, listWidth, labelHeight);
		parametersLabel.setBounds(x+=listWidth+margin, y, listWidth, labelHeight);
		forwardMappingLabel.setBounds(x+=listWidth+margin, y, listWidth, labelHeight);

		x = margin;
		y += labelHeight;
		int tempy = y;

		buttons[0].setBounds(x, y, buttonWidth, buttonHeight);
		for (int i=1; i<buttons.length; i++)
			buttons[i].setBounds(x, y+=buttonHeight, buttonWidth, buttonHeight);

		int temp = (listHeight-2*labelHeight-fieldHeight)/2;
		tmInputParametersList.setBounds(x += buttonWidth+margin, y=tempy, listWidth, temp);
		tmOutputParametersLabel.setBounds(x, y+=temp, listWidth, labelHeight);
		tmOutputParametersList.setBounds(x, y+=labelHeight, listWidth, temp);
		reverseMappingLabel.setBounds(x, y+=temp, listWidth, labelHeight);
		returnValueMapping.setBounds(x, y+=labelHeight, listWidth, fieldHeight);

		parametersList.setBounds(x += listWidth+margin, y=tempy, listWidth, listHeight);

		forwardMappingList.setBounds(x += listWidth+margin, y, listWidth, listHeight);
	}

	Vector choices = new Vector();
	{
		choices.addElement("string");
		choices.addElement("long");
		choices.addElement("boolean");
		choices.addElement("double");
	}
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals(ActionEvents.NEW_PARAMETER)) {
			String value = GetStringFromList.getString(null, choices, "New Parameter");            
			if (value == null) return;
			int count = parameters.getSize();
			HashSet existingNames = new HashSet();
			for (int i=0; i<count; i++) {
				String element = (String)parameters.getElementAt(i);
				StringTokenizer stk = new StringTokenizer(element);
				stk.nextToken();
				String name = stk.nextToken();
				existingNames.add(name);
			}
			int i=0;
			String varName = "param0";
			while (existingNames.contains(varName))
				varName = "param" + i++;
			parameters.addElement(value + " " + varName);
		} else if (command.equals(ActionEvents.REMOVE_PARAMETER)) {
			int index = parametersList.getSelectedIndex();
			if (index != -1) {
				parameters.remove(index);
			}

		} else if (command.equals(ActionEvents.REMOVE_ALL_PARAMTERS)) {
			int size = parameters.getSize();
			for (int i=size-1; i>=0; i--) 
				parameters.remove(i);
		} else if (command.equals(ActionEvents.ADD_INPUT_MAPPING)) {
			int sourceIndex = tmInputParametersList.getSelectedIndex();
			int targetIndex = parametersList.getSelectedIndex();
			if (sourceIndex != -1 && targetIndex != -1) {
				String source = (String)tmInputParameters.getElementAt(sourceIndex);
				String destination = (String)parameters.getElementAt(targetIndex);
				String result = source + " --> " + destination;
				if (!fmappings.contains(result))
					fmappings.addElement(result);
			}
		} else if (command.equals(ActionEvents.REMOVE_INPUT_MAPPING)) {
			int index = forwardMappingList.getSelectedIndex();
			if (index != -1) 
				fmappings.remove(index);
		} else if (command.equals(ActionEvents.REMOVE_ALL_INPUT_MAPPING)) {
			int size = fmappings.getSize();
			for (int i=size-1; i>=0; i--) 
				fmappings.remove(i);
		} else if (command.equals(ActionEvents.SET_REVERSE_MAPPING)) {
			int index = tmOutputParametersList.getSelectedIndex();
			if (index != -1) {
				String source = (String)tmOutputParameters.getElementAt(index);
				String destination = returnValueField.getText();
				String result = source + " <-- " + destination;
				returnValueMapping.setText(result);
			}
		} else 
			LOG.println("unhandled command " + command);
	}
}

