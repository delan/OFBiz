package org.ofbiz.designer.util;

import javax.swing.JToolBar;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.border.EtchedBorder;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;

public class AbstractToolBar extends JToolBar implements ActionListener{
    Vector listeners = new Vector();
    FlowLayout toolBarFlow;

    public static final String COMBO_BOX = "WFButtonComboBox";
    public static final String RADIO_BUTTON = "WFRadioButton";

    JComponent[] buttons = null;
    String[][] buttonInfo = null;

    public void setData(String[][] buttonInfoIn) {
        buttonInfo = buttonInfoIn;
        setLayout(new MyFlowLayout(FlowLayout.LEFT));
        buttons = new JComponent[buttonInfo.length+1];
        for(int i=0; i<buttonInfo.length; i++){

            if(buttonInfo[i][0].equals(COMBO_BOX)) {  // add a WFButton
                try {
                    buttons[i] = (WFButtonComboBox)Class.forName("workflow.org.ofbiz.designer.util."
                                                                 +buttonInfo[i][2]+"_ComboBox").newInstance();
                    buttons[i].setToolTipText(buttonInfo[i][1]);
                } catch(Exception e) {
                    e.printStackTrace();
                    continue;
                }
            } else if(buttonInfo[i][0].equals(RADIO_BUTTON)) {  // add a WFButton
                try {
                    buttons[i] = (WFRadioButton)Class.forName("workflow.org.ofbiz.designer.util."
                                                              +buttonInfo[i][2]+"_RadioButton").newInstance();
                    buttons[i].setToolTipText(buttonInfo[i][1]);
                    ((WFRadioButton)buttons[i]).setText(buttonInfo[i][2]);
                } catch(Exception e) {
                    e.printStackTrace();
                    continue;
                }
            } else {  // add a WFComboBox
                buttons[i] = new KButton(buttonInfo[i][0]);
                buttons[i].setToolTipText(buttonInfo[i][1]);
                ((KButton)buttons[i]).setActionCommand(buttonInfo[i][2]);
            }



            add(buttons[i]);
        }
        setFloatable(false);
        setBorder(new EtchedBorder());
        listenToMenus();
    }

    private void listenToMenus() {
        for(int i=0; i<buttonInfo.length; i++) {
            if((buttonInfo[i][0].equals(COMBO_BOX))) // add WFButton listener
                ((WFButtonComboBox)buttons[i]).addActionListener(this);
            else if((buttonInfo[i][0].equals(RADIO_BUTTON))) // add WFButton listener
                ((WFRadioButton)buttons[i]).addActionListener(this);
            else  // add WFComboBox listener
                ((JButton)buttons[i]).addActionListener(this);
        }
    }

    public void addActionListener(ActionListener listener){
        if(listeners.contains(listener))
            return;
        listeners.addElement(listener);
    }

    public void removeActionListener(ActionListener listener) {
        listeners.removeElement(listener);
    }

    public void actionPerformed(ActionEvent e){
        String command = e.getActionCommand();
        if(command.equals(Constants.REDRAW_MENU)) {
            validate();
            repaint();
            return;
        }
        Component comp = (Component) e.getSource();
        for(int i=0; i < listeners.size(); i++) {
            ((ActionListener)listeners.elementAt(i)).actionPerformed(e);
        }
    }   
}
