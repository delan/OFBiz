package org.ofbiz.designer.util;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class SaveDialog extends JDialog implements ActionListener {
    private SaveDialog() {
        super((Frame)null, "", true);
        addComponentListener(new ComponentAdapter() {
                                 public void componentShown(ComponentEvent howdy) {
                                     getContentPane().setLayout(null);
                                     int count = getContentPane().getComponentCount();
                                     int x = 0, y = 0;
                                     JButton button = null;
                                     for(int i=0;i<count;i++) {
                                         button = (JButton)getContentPane().getComponent(i);
                                         button.setLocation(x, y);
                                         x += button.getWidth();
                                     }
                                     if(button != null)
                                         setSize(x+10, 60);
                                     removeComponentListener(this);
                                 }
                             });
    }
    public static void main(String[] args) {
        Vector vec = new Vector();
        int count = args.length;
        for(int i=0;i<count;i++) {
            vec.addElement(args[i]);
        }
        //String result = getResult(args[0], args[1]);
        String result = getResult(null, vec);
        LOG.println("result is " + result);
    }

    public static String getResult(Point p, String choice1) {
        Vector vec = new Vector();
        vec.addElement(choice1);
        return getResult(p, vec);
    }
    public static String getResult(Point p, String choice1, String choice2) {
        Vector vec = new Vector();
        vec.addElement(choice1);
        vec.addElement(choice2);
        return getResult(p, vec);
    }
    public static String getResult(Point p, String choice1, String choice2, String choice3) {
        Vector vec = new Vector();
        vec.addElement(choice1);
        vec.addElement(choice2);
        vec.addElement(choice3);
        return getResult(p, vec);
    }
    public static String getResult(Point p, Vector choices) {
         final SaveDialog d = new SaveDialog();
        d.getContentPane().setLayout(new FlowLayout());
        for(int i=0;i<choices.size();i++) {
            String temp = (String)choices.elementAt(i);              
            JButton button = new JButton(temp);
            button.addActionListener(d);
            d.getContentPane().add(button);
        }
        d.setLocation(p);
        d.setVisible(true);
        return d.returnValue;
    }

    private String returnValue = null;
    public void actionPerformed(ActionEvent e) {
        returnValue = e.getActionCommand();
        setVisible(false);
    }
}
