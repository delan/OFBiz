/*
 * Created by IntelliJ IDEA.
 * User: Oliver Wieland
 * Date: Jul 27, 2001
 * Time: 3:53:32 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.util.ModifiedJComboBox;
import org.ofbiz.designer.util.WARNING;
import org.ofbiz.designer.generic.IListModel;
import org.ofbiz.designer.generic.IComboBoxModel;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.StringTokenizer;

abstract class ArcEditorView extends JPanel implements ActionListener {
    JList list1, list2, list3;// = new JList();
    ModifiedJComboBox eBox;

    public static final String ADD = "Add";
    public static final String REMOVE = "Remove";
    public static final String REMOVEALL = "RemoveAll";
    public static final String POLICIES = "Policies";

    JButton add = new JButton(ADD);
    JButton remove = new JButton(REMOVE);
    JButton removeAll = new JButton(REMOVEALL);
    JButton policies = new JButton(POLICIES);

    JLabel label1 = new JLabel();
    JLabel label2 = new JLabel();
    JLabel label3 = new JLabel();
    JLabel label4 = new JLabel("Exceptions");

    ArcEditorView() {
        setLayout(null);
        Vector vec = new Vector();
        vec.add("hello");
        vec.add("hi");
        Vector vec2 = (Vector)vec.clone();
        list1 = new JList(vec);
        list2 = new JList(vec2);
        list3 = new JList();
        eBox = new ModifiedJComboBox(new Vector());
        //altBox = new ModifiedJComboBox(new Vector());
        addComponentListener(new ComponentAdapter() {
                                 public void componentResized(ComponentEvent e) {
                                     relayout();
                                 }
                             });

        add(list1);
        add(list2);
        add(list3);
        add(eBox);
        //add(altBox);
        add(label1);
        add(label2);
        add(label3);
        add(label4);
        //add(label5);
        add(add);
        add(remove);
        add(removeAll);
        add(policies);

        add.addActionListener(this);
        remove.addActionListener(this);
        removeAll.addActionListener(this);
        policies.addActionListener(this);
    }

    public void relayout() {
        int width = getWidth();
        int height = getHeight();
        int buttonWidth = 120;
        int margin = 10;
        int labelHeight = 20;
        int fieldWidth = (width - buttonWidth - 5*margin)/4;
        int buttonHeight = height/4;
        if(buttonHeight > 50) buttonHeight = 50;

        label1.setBounds(margin, margin, fieldWidth, labelHeight);
        label2.setBounds(fieldWidth + 2*margin, margin, fieldWidth, labelHeight);
        label3.setBounds(2*fieldWidth + 3*margin, margin, 2*fieldWidth, labelHeight);

        list1.setBounds(margin, labelHeight + 2*margin, fieldWidth, height-3*margin-labelHeight);
        list2.setBounds(fieldWidth + 2*margin, labelHeight + 2*margin, fieldWidth, height-3*margin-labelHeight);
        if(eBox == null)
            list3.setBounds(2*fieldWidth + 3*margin, labelHeight + 2*margin, 2*fieldWidth, height-3*margin-labelHeight);
        else
            list3.setBounds(2*fieldWidth + 3*margin, labelHeight + 2*margin, 2*fieldWidth, height-5*margin-2*labelHeight-buttonHeight);

        add.setBounds(4*fieldWidth + 4*margin, labelHeight + 2*margin, buttonWidth, buttonHeight);
        remove.setBounds(4*fieldWidth + 4*margin, labelHeight + buttonHeight + 2*margin, buttonWidth, buttonHeight);
        removeAll.setBounds(4*fieldWidth + 4*margin, labelHeight + 2*buttonHeight + 2*margin, buttonWidth, buttonHeight);
        if(policies != null) policies.setBounds(4*fieldWidth + 4*margin, labelHeight + 3*buttonHeight + 2*margin, buttonWidth, buttonHeight);
        if(label4 != null) label4.setBounds(2*fieldWidth + 3*margin, height-labelHeight-buttonHeight-2*margin, 2*fieldWidth + buttonWidth + margin, labelHeight);
        if(eBox != null) eBox.setBounds(2*fieldWidth + 3*margin, height-buttonHeight-margin, 2*fieldWidth + buttonWidth + margin, buttonHeight);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if(command.equals(ADD)) {
            String val1 = (String)list1.getSelectedValue();
            String val2 = (String)list2.getSelectedValue();
            StringTokenizer stk = new StringTokenizer(val1);
            stk.nextToken();
            val1 = stk.nextToken();
            stk = new StringTokenizer(val2);
            stk.nextToken();
            val2 = stk.nextToken();

            String newValue = val1 + " --> " + val2;
            mappings.addElement(newValue);
        } else if(command.equals(REMOVE)) {
            if(list3.getSelectedIndex() >= list3.getModel().getSize() || list3.getSelectedIndex() < 0) return;
            mappings.remove(list3.getSelectedIndex());
            repaint();
        } else if(command.equals(REMOVEALL)) {
            int size = mappings.getSize();
            for(int i=size-1; i>=0; i--)
                mappings.remove(i);
            repaint();
        } else WARNING.println("unhandled actionEvent " + command);
    }

    Vector existingResults = new Vector();

    void addString(String value) {
        if(!containsString(value))
            existingResults.addElement(value);
    }
    boolean containsString(String value) {
        int count = existingResults.size();
        for(int i=0; i<count; i++)
            if(existingResults.elementAt(i).equals(value))
                return true;
        return false;
    }

    IListModel inputParams, outputParams, mappings;
    IComboBoxModel exceptions, alternativeTask;
}
