package org.ofbiz.designer.newdesigner;

import javax.swing.*;
import org.ofbiz.designer.util.*;
import java.awt.event.*;
import javax.swing.border.*;
import org.ofbiz.designer.newdesigner.popup.*;
import org.ofbiz.designer.generic.*;

class TransactionalRealizationView extends JPanel implements ActionListener{
    JLabel urlLabel = new JLabel("DBMS URL");
    JLabel dbmsLabel = new JLabel("Database Name");
    JLabel userNamelLabel = new JLabel("Username");
    JLabel passwdLabel = new JLabel("Password");
    JLabel queryLabel = new JLabel("Query");
    JLabel inputsLabel = new JLabel("Inputs");
    JLabel outputsLabel = new JLabel("Outputs");

    JTextField urlField, dbmsField, userNameField, passwdField;
    JTextArea queryField;
    JList inputs, outputs;

    JButton newInput = new JButton(ActionEvents.NEW_INPUT);
    JButton editInput = new JButton(ActionEvents.EDIT_INPUT);
    JButton deleteInput = new JButton(ActionEvents.DELETE_INPUT);
    JButton moveInputUp = new JButton(ActionEvents.MOVE_INPUT_UP);
    JButton moveInputDown = new JButton(ActionEvents.MOVE_INPUT_DOWN);
    JButton newOutput = new JButton(ActionEvents.NEW_OUTPUT);
    JButton editOutput = new JButton(ActionEvents.EDIT_OUTPUT);
    JButton deleteOutput = new JButton(ActionEvents.DELETE_OUTPUT);
    JButton moveOutputUp = new JButton(ActionEvents.MOVE_OUTPUT_UP);
    JButton moveOutputDown = new JButton(ActionEvents.MOVE_OUTPUT_DOWN);

    public TransactionalRealizationView() {
        urlField = new JTextField();
        dbmsField = new JTextField();
        userNameField = new JTextField();
        passwdField = new JTextField();

        queryField = new JTextArea();

        inputs = new JList();
        outputs = new JList();

        // the following fields have default borders different from a textfield 
        Border border = urlField.getBorder();
        queryField.setBorder(border);
        inputs.setBorder(border);
        outputs.setBorder(border);


        setLayout(null);
        add(urlLabel);
        add(dbmsLabel);
        add(userNamelLabel);
        add(passwdLabel);
        add(queryLabel);
        add(inputsLabel);
        add(outputsLabel);
        add(urlField);
        add(dbmsField);
        add(userNameField);
        add(passwdField);
        add(queryField);
        add(inputs);
        add(outputs);

        add(newInput);
        add(editInput);
        add(deleteInput);
        add(moveInputUp);
        add(moveInputDown);
        add(newOutput);
        add(editOutput);
        add(deleteOutput);
        add(moveOutputUp);
        add(moveOutputDown);

        newInput.addActionListener(this);
        editInput.addActionListener(this);
        deleteInput.addActionListener(this);
        moveInputUp.addActionListener(this);
        moveInputDown.addActionListener(this);
        newOutput.addActionListener(this);
        editOutput.addActionListener(this);
        deleteOutput.addActionListener(this);
        moveOutputUp.addActionListener(this);
        moveOutputDown.addActionListener(this);


        addComponentListener(new ComponentAdapter() {
                                 public void componentResized(ComponentEvent e) {
                                     relayout();
                                 }
                             });
        relayout();
    }


    private static final int labelHeight = 30;
    private static final int textFieldHeight = 30;
    private int fieldHeight, buttonHeight;
    private static final int buttonsWidth = 150;
    private static final int margin = 10;

    public void relayout() {
        int width = getBounds().width;
        int height = getBounds().height;
        fieldHeight = (height - 7*labelHeight -4*textFieldHeight-margin)/3;
        buttonHeight = fieldHeight/5;
        int x = margin, y = 0;
        urlLabel.setBounds(x, y, width - 2*margin, labelHeight);
        urlField.setBounds(x, y+=labelHeight, width - 2*margin, textFieldHeight);
        dbmsLabel.setBounds(x, y+=textFieldHeight, width - 2*margin, labelHeight);
        dbmsField.setBounds(x, y+=labelHeight, width - 2*margin, textFieldHeight);
        userNamelLabel.setBounds(x, y+=textFieldHeight, width - 2*margin, labelHeight);
        userNameField.setBounds(x, y+=labelHeight, width - 2*margin, textFieldHeight);
        passwdLabel.setBounds(x, y+=textFieldHeight, width - 2*margin, labelHeight);
        passwdField.setBounds(x, y+=labelHeight, width - 2*margin, textFieldHeight);

        queryLabel.setBounds(x, y+=textFieldHeight, width - 2*margin, labelHeight);
        queryField.setBounds(x, y+=labelHeight, width - 2*margin, fieldHeight);

        inputsLabel.setBounds(x, y+=fieldHeight, width-3*margin-buttonsWidth, labelHeight);
        inputs.setBounds(x, y+=labelHeight, width-3*margin-buttonsWidth, fieldHeight);

        newInput.setBounds(x=width-margin-buttonsWidth, y, buttonsWidth, buttonHeight);
        editInput.setBounds(x, y+=buttonHeight, buttonsWidth, buttonHeight);
        deleteInput.setBounds(x, y+=buttonHeight, buttonsWidth, buttonHeight);
        moveInputDown.setBounds(x, y+=buttonHeight, buttonsWidth, buttonHeight);
        moveInputUp.setBounds(x, y+=buttonHeight, buttonsWidth, buttonHeight);

        outputsLabel.setBounds(x=margin, y+=buttonHeight, width-3*margin-buttonsWidth, labelHeight);
        outputs.setBounds(x, y+=labelHeight, width-3*margin-buttonsWidth, fieldHeight);

        newOutput.setBounds(x=width-margin-buttonsWidth, y, buttonsWidth, buttonHeight);
        editOutput.setBounds(x, y+=buttonHeight, buttonsWidth, buttonHeight);
        deleteOutput.setBounds(x, y+=buttonHeight, buttonsWidth, buttonHeight);
        moveOutputDown.setBounds(x, y+=buttonHeight, buttonsWidth, buttonHeight);
        moveOutputUp.setBounds(x, y+=buttonHeight, buttonsWidth, buttonHeight);
    }

    public void actionPerformed(ActionEvent e){
        String command = e.getActionCommand();
        if(command.equals(ActionEvents.NEW_INPUT)) {
            String newInput = GetStringDialog.getString(null, null, getLocationOnScreen());
            if(newInput != null) {
                inputParams.addElement(newInput);
            }
        } 
        else if(command.equals(ActionEvents.NEW_OUTPUT)) {
            String newOutput = GetStringDialog.getString(null, null, getLocationOnScreen());
            if(newOutput != null) {
                outputParams.addElement(newOutput);
            }
        }
        else if(command.equals(ActionEvents.DELETE_INPUT)) {
            int index = inputs.getSelectedIndex();
            inputParams.remove(index);
        } 
        else if(command.equals(ActionEvents.DELETE_OUTPUT)) {
            int index = outputs.getSelectedIndex();
            outputParams.remove(index);
        } 
        else if(command.equals(ActionEvents.EDIT_INPUT)) {
            int index = inputs.getSelectedIndex();
            String oldValue = (String)inputParams.getElementAt(index);
            String newElement = GetStringDialog.getString(null, oldValue, getLocationOnScreen());
            if(newElement != null) {
                inputParams.remove(index);
                inputParams.insertElementAt(newElement, index);
            }
        } 
        else if(command.equals(ActionEvents.EDIT_OUTPUT)) {
            int index = outputs.getSelectedIndex();
            String oldValue = (String)outputParams.getElementAt(index);
            String newElement = GetStringDialog.getString(null, oldValue, getLocationOnScreen());
            if(newElement != null) {
                outputParams.remove(index);
                outputParams.insertElementAt(newElement, index);
            }
        } 
        else if(command.equals(ActionEvents.MOVE_INPUT_DOWN)) {
            int index = inputs.getSelectedIndex();
            if(index == inputParams.getSize()-1) {
                return;
            }
            Object obj = inputParams.getElementAt(index);
            inputParams.remove(index);
            inputParams.insertElementAt(obj, index+1);
            inputs.setSelectedIndex(index+1);
        }
        else if(command.equals(ActionEvents.MOVE_INPUT_UP)) {
            int index = inputs.getSelectedIndex();
            if(index == 0) {
                return;
            }
            Object obj = inputParams.getElementAt(index);
            inputParams.remove(index);
            inputParams.insertElementAt(obj, index-1);
            inputs.setSelectedIndex(index-1);
        }
        else if(command.equals(ActionEvents.MOVE_OUTPUT_DOWN)) {
            int index = outputs.getSelectedIndex();
            if(index == outputParams.getSize()-1) {
                return;
            }
            Object obj = outputParams.getElementAt(index);
            outputParams.remove(index);
            outputParams.insertElementAt(obj, index+1);
            outputs.setSelectedIndex(index+1);
        }
        else if(command.equals(ActionEvents.MOVE_INPUT_UP)) {
            int index = outputs.getSelectedIndex();
            if(index == 0) {
                return;
            }
            Object obj = outputParams.getElementAt(index);
            outputParams.remove(index);
            outputParams.insertElementAt(obj, index-1);
            outputs.setSelectedIndex(index-1);
        }
        else
            LOG.println("unhandled command " + command);
    }
    
    IListWrapper inputParams, outputParams;
    IDocumentWrapper urlDoc, dbmsDoc, userNameDoc, passwdDoc, queryDoc;


}

