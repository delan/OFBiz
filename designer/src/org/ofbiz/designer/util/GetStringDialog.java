package org.ofbiz.designer.util;

import java.awt.Point;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JFrame;

public class GetStringDialog extends JDialog implements ActionListener {
    private static final String OK = "   Ok   ";
    private static final String CANCEL = " Cancel ";
    private String returnValue = null;
    JTextField textField = new JTextField();
    WFButton okButton = new WFButton(OK);
    WFButton cancelButton = new WFButton(CANCEL);

    GetStringDialog(JFrame owner) {
        super(owner, "String Dialog for: " + owner.getTitle(), true);
        Container cp = getContentPane();
        cp.setLayout(null);
        setSize(250, 120);
        setResizable(false);
        textField.setBounds(20, 10, 210, 30);
        okButton.setBounds( 30, 50, 80, 30);
        cancelButton.setBounds( 140, 50, 80, 30);
        cp.add(textField);
        cp.add(okButton);
        cp.add(cancelButton);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        okButton.setMnemonic('O');
    }

    GetStringDialog(JFrame owner, String title) {
        super(owner, title, true);
        Container cp = getContentPane();
        cp.setLayout(null);
        setSize(250, 120);
        setResizable(false);
        textField.setBounds(20, 10, 210, 30);
        okButton.setBounds( 30, 50, 80, 30);
        cancelButton.setBounds( 140, 50, 80, 30);
        cp.add(textField);
        cp.add(okButton);
        cp.add(cancelButton);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        okButton.setMnemonic('O');
    }

    /*
    public static String getString(JFrame owner, String text) {
        if(owner == null)
            owner = new JFrame();
        GetStringDialog dialog = new GetStringDialog(owner);
        if(text == null)
            dialog.textField.setText("");
        else
            dialog.textField.setText(text);
        dialog.textField.selectAll();
        dialog.setVisible(true);
        return dialog.returnValue;
    }

    public static String getString(JFrame owner, String text, String title) {
        if(owner == null)
            owner = new JFrame();
        GetStringDialog dialog = new GetStringDialog(owner, title);
        if(text == null)
            dialog.textField.setText("");
        else
            dialog.textField.setText(text);
        dialog.textField.selectAll();
        dialog.setVisible(true);
        return dialog.returnValue;
    }

    */
    public static String getString(JFrame owner, String text, String title, Point location) {
        if(owner == null)
            owner = new JFrame();
        GetStringDialog dialog = new GetStringDialog(owner, title);
        dialog.setLocation(location);
        if(text == null)
            dialog.textField.setText("");
        else
            dialog.textField.setText(text);
        dialog.setVisible(true);
        return dialog.returnValue;
    }

    public static String getString(JFrame owner, String text, Point location) {
        if(owner == null)
            owner = new JFrame();
        GetStringDialog dialog = new GetStringDialog(owner);
        dialog.setLocation(location);
        if(text == null)
            dialog.textField.setText("");
        else
            dialog.textField.setText(text);
        dialog.setVisible(true);
        return dialog.returnValue;
    }


    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        //LOG.println("command is " + command);
        if(command.equals(OK)) {
            setVisible(false);
            returnValue = textField.getText();
        } else if(command.equals(CANCEL)) {
            //System.err.println("got a CANCEL");
            setVisible(false);
            returnValue = null;
        }
    }

}
