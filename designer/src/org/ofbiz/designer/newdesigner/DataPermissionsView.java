package org.ofbiz.designer.newdesigner;

import javax.swing.*;
import javax.swing.event.*;
import org.ofbiz.designer.util.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public abstract class DataPermissionsView extends JPanel implements ListSelectionListener{
	DataPermissionsView(){
		init();
		addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent e){
				relayout();
			}
		});
	}
	
	public void init(){
		objectList.addListSelectionListener(this);
		setLayout(new BorderLayout());
		add(objectList, BorderLayout.WEST);
		add(rightPanel, BorderLayout.CENTER);
	}
	
	public abstract void valueChanged(ListSelectionEvent e);
	
	protected JList objectList = new JList();
	protected JPanel rightPanel = new JPanel((LayoutManager)null);
	protected Vector fieldLabels = new Vector();
	protected Vector fieldComboBoxes = new Vector();
	
	private static final int comboBoxWidth = 100;
	private static final int componentHeight = 20;
	
	protected void relayout(){
		int width = rightPanel.getWidth();
		int y = 10;
		for (int i=0; i<fieldComboBoxes.size(); i++){
			JComboBox comboBox = (JComboBox)fieldComboBoxes.elementAt(i);
			JLabel label = (JLabel)fieldLabels.elementAt(i);
			
			label.setBounds(10, y, width-comboBoxWidth-10, componentHeight);
			comboBox.setBounds(width - comboBoxWidth - 10, y, comboBoxWidth, componentHeight);
			y += componentHeight + 2;
			rightPanel.add(label);
			rightPanel.add(comboBox);
		}
		validate();
		repaint();
	}
}


