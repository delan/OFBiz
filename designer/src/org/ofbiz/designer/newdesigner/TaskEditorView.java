package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.util.*;
import java.util.*;
import org.ofbiz.designer.pattern.*;
import java.io.*;
//import org.ofbiz.designer.newdesigner.model.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.networkdesign.*;
import org.ofbiz.designer.roledomain.*;
import org.ofbiz.designer.domainenv.*;
import org.ofbiz.designer.newdesigner.popup.*;
import javax.swing.event.*;

public class TaskEditorView extends JTabbedPane implements ActionListener {
    public static void mainOrig(String[] args) {
        WFFrame frame = new WFFrame("Task Editor");     
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception{
        final WFFrame frame = new WFFrame("Task Editor");
        frame.getContentPane().add(generateGui());
        //frame.setJMenuBar(new TaskEditorMenuBar());
        frame.setJMenuBar(new WFMenuBar());
        frame.setVisible(true);
    }

    private JPanel generalPanel, advancedPanel, networkPanel, parametersPanel, constraintsPanel;

    public TaskEditorView() {
        addTab( "General", generalPanel = getGeneralPanel());
        addTab("Advanced", advancedPanel = getAdvancedPanel());
        addTab("Network", networkPanel = getNetworkPanel());
        addTab("Parameters", parametersPanel = getParametersPanel());
        addTab("Constraints", constraintsPanel = getConstraintsPanel());

        addComponentListener(new ComponentAdapter() {
                                 public void componentResized(ComponentEvent e) {
                                     relayout();
                                 }
                             });

        // startup thread
        // setup layout when it is safe to do so
        new Thread() {
            public void run() {
                while(TaskEditorView.this.getRootPane() == null || !TaskEditorView.this.getRootPane().getParent().isVisible())
                    SafeThread.sleep(100);
                relayout();
            }
        }.start();

        // register with all actionEventProducers
        editRealizationButton.addActionListener(this); 
        viewRealizationButton.addActionListener(this); 
        createNewButton.addActionListener(this); 
        browseButton.addActionListener(this); 
        importButton.addActionListener(this);
        addButton.addActionListener(this); 
        addAllButton.addActionListener(this); 
        removeButton.addActionListener(this); 
        removeAllButton.addActionListener(this); 
        editButton.addActionListener(this);
        inputOperatorEditorButton.addActionListener(this); 
        inputTaskEditorButton.addActionListener(this); 
        inputArcEditorButton.addActionListener(this); 
        //inputDeleteArcButton.addActionListener(this);
        outputOperatorEditorButton.addActionListener(this); 
        outputTaskEditorButton.addActionListener(this); 
        outputArcEditorButton.addActionListener(this); 
        //outputDeleteArcButton.addActionListener(this);
        //exceptionOperatorEditorButton.addActionListener(this); 
        exceptionTaskEditorButton.addActionListener(this); 
        exceptionArcEditorButton.addActionListener(this); 
        //exceptionDeleteArcButton.addActionListener(this);
        newInvocationButton.addActionListener(this); 
        //emptyInvocationButton.addActionListener(this); 
        removeInvocationButton.addActionListener(this); 
        removeAllInvocationsButton.addActionListener(this); 
        //editInvocationButton.addActionListener(this); 
        //invocationPermissionsButton.addActionListener(this);
        newOutputButton.addActionListener(this); 
        removeOutputButton.addActionListener(this); 
        removeAllOutputsButton.addActionListener(this); 
        //editOutputButton.addActionListener(this); 
        //outputPermissionsButton.addActionListener(this);
        newExceptionButton.addActionListener(this); 
        removeExceptionButton.addActionListener(this); 
        removeAllExceptionsButton.addActionListener(this); 
        //editExceptionButton.addActionListener(this); 
        exceptionHandlerButton.addActionListener(this);
        newConstraint.addActionListener(this); 
        removeConstraint.addActionListener(this); 
        removeAllConstraints.addActionListener(this); 
        editConstraint.addActionListener(this);

    }

    protected void relayout() {
        if(getRootPane() == null) return;

        int x = 10, y = 10, labelHeight = 20;
        Rectangle bounds = generalPanel.getBounds();

        // layout general panel
        x = y = 10;
        bounds = generalPanel.getBounds();

        nameLabel.setBounds(x, y, bounds.width-2*x, labelHeight);
        nameField.setBounds(x, y += nameLabel.getBounds().height + 10, bounds.width-2*x, 30);
        typeLabel.setBounds(x, y += nameField.getBounds().height + 10, bounds.width-2*x, labelHeight);
        typeField.setBounds(x, y += typeLabel.getBounds().height + 10, bounds.width-2*x, 30);
        descriptionLabel.setBounds(x, y += typeField.getBounds().height + 10, bounds.width-2*x, labelHeight);
        descriptionField.setBounds(x, y += descriptionLabel.getBounds().height + 10, bounds.width-2*x, bounds.height - y-10);

        // layout advanced panel
        x = y = 10;

        hostLabel.setBounds(x, y, bounds.width-2*x, labelHeight);
        hostField.setBounds(x, y+= hostLabel.getBounds().height + 10, bounds.width-2*x, 30);
        realizationLabel.setBounds(x, y+= hostField.getBounds().height + 10, bounds.width-2*x, labelHeight);

        foreignTaskField.setBounds(x, y+= realizationLabel.getBounds().height + 10, 20, 20);
        x += 30;
        foreignTaskLabel.setBounds(x, y, 100, labelHeight);
        x += 110;

        int ysave = y;
        int buttonWidth = (bounds.width - x - 20)/2;
        editRealizationButton.setBounds(x, y, buttonWidth, 45);
        viewRealizationButton.setBounds(x, y += editRealizationButton.getBounds().height + 2, buttonWidth, 45);

        y = ysave;
        x += buttonWidth+10;

        createNewButton.setBounds(x, y, buttonWidth, 30);
        browseButton.setBounds(x, y += createNewButton.getBounds().height + 1, buttonWidth, 30);
        importButton.setBounds(x, y += browseButton.getBounds().height + 1, buttonWidth, 30);

        x = 10;

        securityDomainLabel.setBounds(x, y+= 60, bounds.width-2*x, labelHeight);
        securityDomainField.setBounds(x, y+= securityDomainLabel.getBounds().height + 10, bounds.width-2*x, 30);

        compartmentLabel.setBounds(x, y+= securityDomainField.getBounds().height + 10, bounds.width-2*x, labelHeight);
        compartmentField.setBounds(x, y+= compartmentLabel.getBounds().height + 10, bounds.width-2*x, 30);

        roleDomainLabel.setBounds(x, y+= compartmentField.getBounds().height + 10, bounds.width-2*x, labelHeight);
        roleDomainField.setBounds(x, y+= roleDomainLabel.getBounds().height + 10, bounds.width-2*x, 30);


        int tempWidth = (bounds.width - 40)/3;
        y += roleDomainField.getBounds().height + 10;
        ysave = y;
        currentRolesLabel.setBounds(x, y, tempWidth, labelHeight);
        currentRolesField.setBounds(x, y+= currentRolesLabel.getBounds().height + 10, tempWidth, bounds.height - y - 20);

        addButton.setBounds(x += currentRolesField.getBounds().getWidth() + 10, y = ysave+currentRolesLabel.getBounds().height+10, tempWidth, 30);
        addAllButton.setBounds(x, y+= addButton.getBounds().height + 1, tempWidth, 30);
        removeButton.setBounds(x, y+= addAllButton.getBounds().height + 1, tempWidth, 30);
        removeAllButton.setBounds(x, y+= removeButton.getBounds().height + 1, tempWidth, 30);
        editButton.setBounds(x, y+= removeAllButton.getBounds().height + 1, tempWidth, 30);

        availableRolesLabel.setBounds(x += editButton.getBounds().getWidth() + 10, y= ysave, bounds.width-x-10, labelHeight);
        availableRolesField.setBounds(x, y+= availableRolesLabel.getBounds().height + 10, bounds.width-x -10, bounds.height - y - 20);

        // layout network panel
        x = y = 10;

        int tempHeight = (bounds.height - 70 - 3*labelHeight)/3;

        inputArcsLabel.setBounds(x, y, bounds.width - 200, labelHeight);
        inputArcsField.setBounds(x, y += inputArcsLabel.getBounds().height + 10, bounds.width - 200, tempHeight);
        outputArcsLabel.setBounds(x, y += inputArcsField.getBounds().height + 10, bounds.width - 200, labelHeight);
        outputArcsField.setBounds(x, y += outputArcsLabel.getBounds().height + 10, bounds.width - 200, tempHeight);
        exceptionArcsLabel.setBounds(x, y += outputArcsField.getBounds().height + 10, bounds.width - 200, labelHeight);
        exceptionArcsField.setBounds(x, y += exceptionArcsLabel.getBounds().height + 10, bounds.width - 200, tempHeight);

        x = bounds.width - 180;
        y = 20 + labelHeight;

        inputOperatorEditorButton.setBounds(x, y, bounds.width - x - 10, 30);
        inputTaskEditorButton.setBounds(x, y += inputOperatorEditorButton.getBounds().height + 1, bounds.width - x - 10, 30);
        inputArcEditorButton.setBounds(x, y += inputTaskEditorButton.getBounds().height + 1, bounds.width - x - 10, 30);
        //inputDeleteArcButton.setBounds(x, y += inputArcEditorButton.getBounds().height + 1, bounds.width - x - 10, 30);

        outputOperatorEditorButton.setBounds(x, y = 2*labelHeight + tempHeight + 40, bounds.width - x - 10, 30);
        outputTaskEditorButton.setBounds(x, y += outputOperatorEditorButton.getBounds().height + 1, bounds.width - x - 10, 30);
        outputArcEditorButton.setBounds(x, y += outputTaskEditorButton.getBounds().height + 1, bounds.width - x - 10, 30);
        //outputDeleteArcButton.setBounds(x, y += outputArcEditorButton.getBounds().height + 1, bounds.width - x - 10, 30);

        exceptionTaskEditorButton.setBounds(x, y = 3*labelHeight + 2*tempHeight + 60, bounds.width - x - 10, 30);
        exceptionArcEditorButton.setBounds(x, y += exceptionTaskEditorButton.getBounds().height + 1, bounds.width - x - 10, 30);
        //exceptionDeleteArcButton.setBounds(x, y += exceptionArcEditorButton.getBounds().height + 1, bounds.width - x - 10, 30);

        // layout parameters panel
        x = y = 10;

        invocationsLabel.setBounds(x, y, bounds.width - 200, labelHeight);
        invocationsField.setBounds(x, y += invocationsLabel.getBounds().height + 10, bounds.width - 200, tempHeight);
        outputsLabel.setBounds(x, y += invocationsField.getBounds().height + 10, bounds.width - 200, labelHeight);
        outputsField.setBounds(x, y += outputsLabel.getBounds().height + 10, bounds.width - 200, tempHeight);
        exceptionsLabel.setBounds(x, y += outputsField.getBounds().height + 10, bounds.width - 200, labelHeight);
        exceptionsField.setBounds(x, y += exceptionsLabel.getBounds().height + 10, bounds.width - 200, tempHeight);

        x = bounds.width - 180;
        y = 20 + labelHeight;

        newInvocationButton.setBounds(x, y, bounds.width - x - 10, 30);
        //emptyInvocationButton.setBounds(x, y += newInvocationButton.getBounds().height + 1, bounds.width - x - 10, 30);
        removeInvocationButton.setBounds(x, y += newInvocationButton.getBounds().height + 1, bounds.width - x - 10, 30);
        removeAllInvocationsButton.setBounds(x, y += removeInvocationButton.getBounds().height + 1, bounds.width - x - 10, 30);
        //editInvocationButton.setBounds(x, y += removeAllInvocationsButton.getBounds().height + 1, bounds.width - x - 10, 30);
        //invocationPermissionsButton.setBounds(x, y += editInvocationButton.getBounds().height + 1, bounds.width - x - 10, 30);

        newOutputButton.setBounds(x, y = 2*labelHeight + tempHeight + 40, bounds.width - x - 10, 30);
        removeOutputButton.setBounds(x, y += newOutputButton.getBounds().height + 1, bounds.width - x - 10, 30);
        removeAllOutputsButton.setBounds(x, y += removeOutputButton.getBounds().height + 1, bounds.width - x - 10, 30);
        //editOutputButton.setBounds(x, y += removeAllOutputsButton.getBounds().height + 1, bounds.width - x - 10, 30);
        //outputPermissionsButton.setBounds(x, y += editOutputButton.getBounds().height + 1, bounds.width - x - 10, 30);

        newExceptionButton.setBounds(x, y = 3*labelHeight + 2*tempHeight + 60, bounds.width - x - 10, 30);
        removeExceptionButton.setBounds(x, y += newExceptionButton.getBounds().height + 1, bounds.width - x - 10, 30);
        removeAllExceptionsButton.setBounds(x, y += removeExceptionButton.getBounds().height + 1, bounds.width - x - 10, 30);
        exceptionHandlerButton.setBounds(x, y += removeAllExceptionsButton.getBounds().height + 1, bounds.width - x - 10, 30);
        //editExceptionButton.setBounds(x, y += removeAllExceptionsButton.getBounds().height + 1, bounds.width - x - 10, 30);

        // layout constraints panel

        x = y = 10;

        tempHeight = bounds.height - 280 - 4*labelHeight;

        constraintsLabel.setBounds(x, y, bounds.width - 200, labelHeight);
        constraintsField.setBounds(x, y += constraintsLabel.getBounds().height + 10, bounds.width - 200, 200);
        dataPermissionsLabel.setBounds(x, y += constraintsField.getBounds().height + 10, bounds.width - 200, labelHeight);
        dataPermissions.setBounds(x, y += dataPermissionsLabel.getBounds().height + 10, bounds.width - 200, tempHeight);
        timeoutLabel.setBounds(x, y += dataPermissions.getBounds().height + 10, bounds.width - 200, labelHeight);
        timeoutField.setBounds(x, y += timeoutLabel.getBounds().height + 10, bounds.width - 200, 30);

        x = bounds.width - 180;
        y = 20 + labelHeight;

        newConstraint.setBounds(x, y, bounds.width - x - 10, 30);
        removeConstraint.setBounds(x, y += newConstraint.getBounds().height + 1, bounds.width - x - 10, 30);
        removeAllConstraints.setBounds(x, y += removeConstraint.getBounds().height + 1, bounds.width - x - 10, 30);
        editConstraint.setBounds(x, y += removeAllConstraints.getBounds().height + 1, bounds.width - x - 10, 30);
    }

    // general panel components
    protected JLabel nameLabel, typeLabel, descriptionLabel;
    protected JTextField nameField;
    protected ModifiedJComboBox typeField;
    protected JTextArea descriptionField;

    // advanced  panel components
    protected JLabel hostLabel, realizationLabel, foreignTaskLabel, securityDomainLabel, compartmentLabel, roleDomainLabel, currentRolesLabel, availableRolesLabel;
    protected JTextField hostField, compartmentField;
    protected JCheckBox foreignTaskField;
    protected KButton editRealizationButton, viewRealizationButton, createNewButton, browseButton, importButton;
    protected ModifiedJComboBox securityDomainField, roleDomainField;
    protected JList currentRolesField, availableRolesField;
    protected KButton addButton, addAllButton, removeButton, removeAllButton, editButton;


    // network panel components
    protected JLabel inputArcsLabel, outputArcsLabel, exceptionArcsLabel;
    protected JList inputArcsField, outputArcsField, exceptionArcsField;
    protected KButton inputOperatorEditorButton, inputTaskEditorButton, inputArcEditorButton;
    protected KButton outputOperatorEditorButton, outputTaskEditorButton, outputArcEditorButton;
    protected KButton exceptionTaskEditorButton, exceptionArcEditorButton;

    // parameters panel components
    protected JLabel invocationsLabel, outputsLabel, exceptionsLabel;
    protected JList invocationsField, outputsField, exceptionsField;
    protected KButton newInvocationButton, removeInvocationButton, removeAllInvocationsButton;//, editInvocationButton;//, invocationPermissionsButton;
    protected KButton newOutputButton, removeOutputButton, removeAllOutputsButton;//, editOutputButton; //outputPermissionsButton;
    protected KButton newExceptionButton, removeExceptionButton, removeAllExceptionsButton, exceptionHandlerButton;//, editExceptionButton;

    // constraints panel components
    protected JLabel constraintsLabel, timeoutLabel, dataPermissionsLabel;
    protected JList constraintsField;
    protected JTextField timeoutField;
    protected KButton newConstraint, removeConstraint, removeAllConstraints, editConstraint;
    protected JPanel dataPermissions;

    private JPanel getGeneralPanel() {
        JPanel panel = new JPanel();

        nameLabel = new JLabel("Task Name");
        nameField = new JTextField();

        typeLabel = new JLabel("Task Type");
        typeField = new ModifiedJComboBox(new Vector());

        descriptionLabel = new JLabel("Task Description");
        descriptionField = new JTextArea();

        panel.setLayout(null);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(typeLabel);
        panel.add(typeField);
        panel.add(descriptionLabel);
        panel.add(descriptionField);

        return panel;
    }

    private JPanel getAdvancedPanel() {
        JPanel panel = new JPanel();

        hostLabel = new JLabel("Host");
        hostField = new JTextField();
        realizationLabel = new JLabel("Task Realization");
        foreignTaskLabel = new JLabel("Foreign Task");
        foreignTaskField = new JCheckBox();

        editRealizationButton = new KButton(ActionEvents.EDIT_REALIZATION);
        viewRealizationButton = new KButton(ActionEvents.VIEW_REALIZATION);
        createNewButton = new KButton(ActionEvents.CREATE_NEW_REALIZATION);
        browseButton = new KButton(ActionEvents.BROWSE_REALIZATION);
        importButton = new KButton(ActionEvents.IMPORT_URL);

        securityDomainLabel = new JLabel("Security Domain");
        Vector types = new Vector();
        securityDomainField = new ModifiedJComboBox(types);

        compartmentLabel = new JLabel("Compartments");
        compartmentField = new JTextField();

        roleDomainLabel = new JLabel("RoleDomain");
        types = new Vector();
        roleDomainField = new ModifiedJComboBox(types);

        currentRolesLabel = new JLabel("Current Roles");
        types = new Vector();
        currentRolesField = new JList(types);
        availableRolesLabel = new JLabel("Available Roles");
        types = new Vector();
        availableRolesField = new JList(types);

        addButton = new KButton(ActionEvents.ADD_ROLE);
        addAllButton = new KButton(ActionEvents.ADD_ALL_ROLES);
        removeButton = new KButton(ActionEvents.REMOVE_ROLE);
        removeAllButton = new KButton(ActionEvents.REMOVE_ALL_ROLES);
        editButton = new KButton(ActionEvents.EDIT_ROLE);

        panel.setLayout(null);

        panel.add(hostLabel);
        panel.add(hostField);
        panel.add(realizationLabel);
        panel.add(foreignTaskLabel);
        panel.add(foreignTaskField);
        panel.add(editRealizationButton);
        panel.add(viewRealizationButton);
        panel.add(createNewButton);
        panel.add(browseButton);
        panel.add(importButton);
        panel.add(securityDomainLabel);
        panel.add(securityDomainField);
        panel.add(compartmentLabel);
        panel.add(compartmentField);
        panel.add(roleDomainLabel);
        panel.add(roleDomainField);
        panel.add(currentRolesLabel);
        panel.add(currentRolesField);
        panel.add(availableRolesLabel);
        panel.add(availableRolesField);
        panel.add(addButton);
        panel.add(addAllButton);
        panel.add(removeButton);
        panel.add(removeAllButton);
        panel.add(editButton);

        return panel;
    }

    private JPanel getNetworkPanel() {
        JPanel panel = new JPanel();

        inputArcsLabel = new JLabel("Input Arcs");
        outputArcsLabel = new JLabel("Output Arcs");
        exceptionArcsLabel = new JLabel("Exception Arcs");

        Vector types = new Vector();
        inputArcsField = new JList(types);
        types = new Vector();
        outputArcsField = new JList(types);
        types = new Vector();
        exceptionArcsField = new JList(types);

        inputOperatorEditorButton = new KButton(ActionEvents.INPUT_OPERATOR_EDITOR);
        inputTaskEditorButton = new KButton(ActionEvents.INPUT_TASK_EDITOR);
        inputArcEditorButton = new KButton(ActionEvents.INPUT_ARC_EDITOR);
        //inputDeleteArcButton = new KButton(ActionEvents.INPUT_DELETE_ARC);

        outputOperatorEditorButton = new KButton(ActionEvents.OUTPUT_OPERATOR_EDITOR);
        outputTaskEditorButton = new KButton(ActionEvents.OUTPUT_TASK_EDITOR);
        outputArcEditorButton = new KButton(ActionEvents.OUTPUT_ARC_EDITOR);
        //outputDeleteArcButton = new KButton(ActionEvents.OUTPUT_DELETE_ARC);

        //exceptionOperatorEditorButton = new KButton(ActionEvents.EXCEPTION_OPERATOR_EDITOR);
        exceptionTaskEditorButton = new KButton(ActionEvents.EXCEPTION_TASK_EDITOR);
        exceptionArcEditorButton = new KButton(ActionEvents.EXCEPTION_ARC_EDITOR);
        //exceptionDeleteArcButton = new KButton(ActionEvents.EXCEPTION_DELETE_ARC);

        panel.setLayout(null);

        panel.add(inputArcsLabel);
        panel.add(outputArcsLabel);
        panel.add(exceptionArcsLabel);

        panel.add(inputArcsField);
        panel.add(outputArcsField);
        panel.add(exceptionArcsField);

        panel.add(inputOperatorEditorButton);
        panel.add(inputTaskEditorButton);
        panel.add(inputArcEditorButton);
        //panel.add(inputDeleteArcButton);

        panel.add(outputOperatorEditorButton);
        panel.add(outputTaskEditorButton);
        panel.add(outputArcEditorButton);
        //panel.add(outputDeleteArcButton);

        //panel.add(exceptionOperatorEditorButton);
        panel.add(exceptionTaskEditorButton);
        panel.add(exceptionArcEditorButton);
        //panel.add(exceptionDeleteArcButton);

        return panel;
    }

    private JPanel getParametersPanel() {
        JPanel panel = new JPanel();

        invocationsLabel = new JLabel("Invocations");
        outputsLabel = new JLabel("Outputs");
        exceptionsLabel = new JLabel("Exceptions");

        Vector types = new Vector();
        invocationsField = new JList(types);
        types = new Vector();
        outputsField = new JList(types);
        types = new Vector();
        exceptionsField = new JList(types);

        new ListDragAdapter(invocationsField, ListDragAdapter.TYPEANDNAME, "input-param");
        new ListDragAdapter(outputsField, ListDragAdapter.TYPEANDNAME, "output-param");
        new ListDragAdapter(exceptionsField, ListDragAdapter.TYPEONLY, null);

        newInvocationButton = new KButton(ActionEvents.NEW_INVOCATION);
        //emptyInvocationButton = new KButton(ActionEvents.EMPTY_INVOCATION);
        removeInvocationButton = new KButton(ActionEvents.REMOVE_INVOCATION);
        removeAllInvocationsButton = new KButton(ActionEvents.REMOVE_ALL_INVOCATION);
        //editInvocationButton = new KButton(ActionEvents.EDIT_INVOCATION);
        //invocationPermissionsButton = new KButton(INVOCATION_PERMISSIONS);

        newOutputButton = new KButton(ActionEvents.NEW_OUTPUT);
        removeOutputButton = new KButton(ActionEvents.REMOVE_OUTPUT);
        removeAllOutputsButton = new KButton(ActionEvents.REMOVE_ALL_OUTPUTS);
        //editOutputButton = new KButton(ActionEvents.EDIT_OUTPUT);
        //outputPermissionsButton = new KButton(OUTPUT_PERMISSIONS);

        newExceptionButton = new KButton(ActionEvents.NEW_EXCEPTION);
        removeExceptionButton = new KButton(ActionEvents.REMOVE_EXCEPTION);
        removeAllExceptionsButton = new KButton(ActionEvents.REMOVE_ALL_EXCEPTIONS);
        //editExceptionButton = new KButton(EDIT_EXCEPTION);
        exceptionHandlerButton = new KButton(ActionEvents.EXCEPTION_HANDLER);

        panel.setLayout(null);

        panel.add(invocationsLabel);
        panel.add(outputsLabel);
        panel.add(exceptionsLabel);

        panel.add(invocationsField);
        panel.add(outputsField);
        panel.add(exceptionsField);

        panel.add(newInvocationButton);
        //panel.add(emptyInvocationButton);
        panel.add(removeInvocationButton);
        panel.add(removeAllInvocationsButton);
        //panel.add(editInvocationButton);
        //panel.add(invocationPermissionsButton);

        panel.add(newOutputButton);
        panel.add(removeOutputButton);
        panel.add(removeAllOutputsButton);
        //panel.add(editOutputButton);
        //panel.add(outputPermissionsButton);

        panel.add(newExceptionButton);
        panel.add(removeExceptionButton);
        panel.add(removeAllExceptionsButton);
        //panel.add(editExceptionButton);
        panel.add(exceptionHandlerButton);

        return panel;
    }

    private JPanel getConstraintsPanel() {
        JPanel panel = new JPanel();

        constraintsLabel = new JLabel("Constraints");
        timeoutLabel = new JLabel("Timeout");

        Vector types = new Vector();
        constraintsField = new JList(types);
        timeoutField = new JTextField();

        newConstraint = new KButton(ActionEvents.NEW_CONSTRAINT);        
        removeConstraint = new KButton(ActionEvents.REMOVE_CONSTRAINT);      
        removeAllConstraints = new KButton(ActionEvents.REMOVE_ALL_CONSTRAINTS);     
        editConstraint = new KButton(ActionEvents.EDIT_CONSTRAINT);      

        dataPermissionsLabel = new JLabel("Data Permissions");
        dataPermissions = new JPanel();

        panel.setLayout(null);

        panel.add(constraintsLabel);
        panel.add(timeoutLabel);
        panel.add(constraintsField);
        panel.add(timeoutField);
        panel.add(newConstraint);
        panel.add(removeConstraint);
        panel.add(removeAllConstraints);
        panel.add(editConstraint);
        panel.add(dataPermissionsLabel);
        panel.add(dataPermissions);

        return panel;
    }

    private static TaskEditorView generateGui() {
        return new TaskEditorView();
    }

    public void actionPerformed(ActionEvent e) {
        WARNING.println("NOT IMPLEMENTED");
    }

}

class ListDragAdapter extends DragAdapter {
    JList comp;
    String type, variablePrefix;

    public static final String TYPEONLY = "TYPEONLY";
    public static final String TYPEANDNAME = "TYPEANDNAME";

    ListDragAdapter(JList _comp, String _type, String _variablePrefix) {
        super(_comp);
        comp = _comp;
        variablePrefix = _variablePrefix;
        type = _type;
        if(!_type.equals(TYPEONLY) && !_type.equals(TYPEANDNAME)) throw new RuntimeException("Invalid type");
        if(type.equals(TYPEANDNAME) && variablePrefix == null) throw new RuntimeException("Variable prefix is null !");
    }

    public void writeObjectOnDrag(ObjectOutputStream os, Point dragLocation) throws Exception{
        os.writeObject((String)comp.getSelectedValue());
    }

    void handleDropTYPEONLY(String value) {
        StringTokenizer stk = new StringTokenizer(value);
        value = stk.nextToken();

        int count = ((IListModel)comp.getModel()).getSize();
        HashSet variableNames = new HashSet();
        for(int i=0; i<count; i++) {
            String element = (String)((IListModel)comp.getModel()).elementAt(i);
            variableNames.add(element);
        }
        if(variableNames.contains(value)) return;
        ((IListModel)comp.getModel()).addElement(value);
    }

    void handleDropTYPEANDNAME(String value, Point dropLocation) {
        /*
        int count = ((IListModel)comp.getModel()).getSize();
        HashSet variableNames = new HashSet();
        for(int i=0; i<count; i++) {
            String element = (String)((IListModel)comp.getModel()).elementAt(i);
            StringTokenizer stk = new StringTokenizer(element);
            stk.nextToken();
            variableNames.add(stk.nextToken());
        }
        int temp = 0;
        String variableName = variablePrefix + temp++;
        while(variableNames.contains(variableName))
            variableName = variablePrefix + temp++;
        */
        Point p = dropLocation;
        p.translate(comp.getLocationOnScreen().x, comp.getLocationOnScreen().y);
        String variableName = GetStringDialog.getString((JFrame)comp.getRootPane().getParent(), "", "Input variable name", p);
        if(variableName != null)
            ((IListModel)comp.getModel()).addElement(value + " " + variableName);
    }

    public void readObjectOnDrop(ObjectInputStream is, Point dropLocation) throws Exception{
        String value = (String)is.readObject();
        if(type.equals(TYPEANDNAME)) handleDropTYPEANDNAME(value, dropLocation);
        else handleDropTYPEONLY(value);
    }
}


