package org.ofbiz.designer.util;

import java.awt.event.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.font.FontRenderContext;
import javax.swing.*;
import java.util.Vector;

public class ChoiceDialog extends JDialog {
    private String alertMessage = null;
    private Container cp = null;
    private int dialogHeight = 100;
    private int dialogWidth = 250;

    private JTextArea textArea = null;
    private WFButton okButton = null;
    private WFButton cancelButton = null;

    private boolean returnValue = false;


    private ChoiceDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        cp = getContentPane();
        cp.setLayout(null);
    }

    public static boolean showChoiceDialog(Frame owner, String alert) {
        ChoiceDialog alertD = null;
        if(owner == null) {
            owner = new Frame();
            alertD = new ChoiceDialog(owner, "  Alert !", true);
        } else {
            alertD = new ChoiceDialog(owner, owner.getName() + " - Alert !", true);
        }
        alertD.alertMessage = alert;
        alertD.buildChoiceDialog();
        alertD.setVisible(true);
        System.err.println("done");
        return alertD.returnValue;
    }

    public static boolean showChoiceDialog(Frame owner, String alert, int widthIn) {
        ChoiceDialog alertD = null;
        if(owner == null) {
            owner = new Frame();
            alertD = new ChoiceDialog(owner, "  Alert !", true);
        } else {
            alertD = new ChoiceDialog(owner, owner.getName() + " - Alert !", true);
        }
        alertD.alertMessage = alert;
        alertD.dialogWidth = widthIn;
        alertD.buildChoiceDialog();
        alertD.setVisible(true);
        return alertD.returnValue;
    }

    public static boolean showChoiceDialog(Frame owner, String alert, String title) {
        ChoiceDialog alertD = null;
        if(owner == null) {
            owner = new Frame();
            alertD = new ChoiceDialog(owner, "  Alert !", true);
        } else {
            alertD = new ChoiceDialog(owner, title + " - Alert !", true);
        }
        alertD.alertMessage = alert;
        alertD.buildChoiceDialog();
        alertD.setVisible(true);
        return alertD.returnValue;
    }

    public static boolean showChoiceDialog(Frame owner, String alert, String title, int widthIn) {
        System.err.println(4);
        ChoiceDialog alertD = null;
        if(owner == null) {
            owner = new Frame();
            alertD = new ChoiceDialog(owner, "  Alert !", true);
        } else {
            alertD = new ChoiceDialog(owner, title + " - Alert !", true);
        }
        alertD.alertMessage = alert;
        alertD.dialogWidth = widthIn;
        alertD.buildChoiceDialog();
        alertD.setVisible(true);
        return alertD.returnValue;
    }

    public static ChoiceDialog getChoiceDialog(Frame owner, String alert) {
        ChoiceDialog alertD = null;
        if(owner == null) {
            owner = new Frame();
            alertD = new ChoiceDialog(owner, "  Alert !", true);
        } else {
            alertD = new ChoiceDialog(owner, " - Alert !", true);
        }
        alertD.alertMessage = alert;
        alertD.buildChoiceDialog();
        return alertD;
    }

    public boolean getReturnValue() {
        return returnValue;
    }

    void buildChoiceDialog() {
        cp.removeAll();
        System.err.println("setting bounds " + dialogWidth + " " + dialogHeight);
        setBounds(0, 0, dialogWidth, dialogHeight);
        cp.setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.setBackground(getBackground());
        textArea.setBorder(null);
        textArea.setText(alertMessage);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        System.err.println("border is " + textArea.getBorder());

        okButton = new WFButton("OK");
        cancelButton = new WFButton("CANCEL");
        okButton.addActionListener(new ActionListener() {
                                       public void actionPerformed(ActionEvent e) {
                                           returnValue = true;
                                           setVisible(false);
                                       }
                                   });
        cancelButton.addActionListener(new ActionListener() {
                                           public void actionPerformed(ActionEvent e) {
                                               returnValue = false;
                                               setVisible(false);
                                           }
                                       });
        cp.add(textArea, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        cp.add(buttonPanel, BorderLayout.SOUTH);
        System.err.println("setting visible true");
        cp.setVisible(true);
        //new Throwable().printStackTrace();
    }

    public static void main(String[] args) {
        System.err.println(80+870+16+556+69+28+1926+380);
        ChoiceDialog ad = ChoiceDialog.getChoiceDialog(null, args[0]);
        ad.setTitle("hi");
        ad.setBounds(10, 10, 200, 100);
        ad.setVisible(true);
        LOG.println("ad.getReturnValue() is " + ad.getReturnValue());
        System.exit(0);
    }
}