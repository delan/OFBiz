package org.ofbiz.designer.util;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.event.*;

public class GetStringFromList extends JDialog implements ActionListener {
    private static final String OK = "   Select   ";
    private static final String CANCEL = " Cancel ";
    JList listField = new JList();
    WFButton cancelButton = new WFButton(CANCEL);
    WFButton okButton = new WFButton(OK);

	String value = null;

    GetStringFromList(JFrame owner) {
        super( owner, "String Dialog for: " + owner.getTitle(), true);
        Container cp = getContentPane();
        cp.setLayout(null);
        setSize(250, 120);
        setResizable(false);
        cp.add(listField);
        cp.add(cancelButton);
        cp.add(okButton);
        cancelButton.addActionListener(this);
		okButton.addActionListener(this);
		/*
        listField.addListSelectionListener(new ListSelectionListener() {
                                               public void valueChanged(ListSelectionEvent e) {
                                                   setVisible(false);
                                               }
                                           });
	   */
        relayout();
    }

    GetStringFromList(JFrame owner, String title) {
        super( owner, title, true);
        Container cp = getContentPane();
        cp.setLayout(null);
        setSize(250, 120);
        setResizable(false);
        cp.add(listField);
        cp.add(cancelButton);
        cp.add(okButton);
        cancelButton.addActionListener(this);
		okButton.addActionListener(this);
		/*
        listField.addListSelectionListener(new ListSelectionListener() {
                                               public void valueChanged(ListSelectionEvent e) {
                                                   setVisible(false);
                                               }
                                           });
	   */
        relayout();
    }

    public void relayout() {
        setSize(250, 180); 
        listField.setBounds(10, 10, getWidth()-20, 100);
        cancelButton.setBounds(10, 110, 80, 30);
        okButton.setBounds(100, 110, 120, 30);
    }

    public static String getString(JFrame owner, final Vector choices) {
        if(owner == null)
            owner = new JFrame();
        GetStringFromList dialog = new GetStringFromList(owner);                
        dialog.listField.setModel(new DefaultListModel() {
                                      public Object getElementAt(int index) {
                                          return choices.elementAt(index);
                                      }
                                      public int getSize() {
                                          return choices.size();
                                      }
                                  });
        dialog.relayout();
        dialog.setVisible(true);
        return dialog.value;
    }

    public static String getString(JFrame owner, final Vector choices, String frameTitle) {
        if(owner == null)
            owner = new JFrame();
        GetStringFromList dialog = new GetStringFromList(owner, frameTitle);                
        dialog.listField.setModel(new DefaultListModel() {
                                      public Object getElementAt(int index) {
                                          return choices.elementAt(index);
                                      }
                                      public int getSize() {
                                          return choices.size();
                                      }
                                  });
        dialog.relayout();
        dialog.setVisible(true);
        return dialog.value;
    }

    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        if(command.equals(CANCEL)) {
    		value = null;
			setVisible(false);
        } else if(command.equals(OK)) {
    		value = (String)listField.getSelectedValue();
			if (value != null) 
				setVisible(false);
        }
    }

    public static void main(String[] args) {
        String[] choices = {
            "string", "long", "boolean", "double"
        };
        Vector vec = new Vector();
        vec.addElement("string");
        vec.addElement("long");
        vec.addElement("boolean");
        vec.addElement("double");
        String value = GetStringFromList.getString(null, vec);
        LOG.println("value is " + value);
    }
}
