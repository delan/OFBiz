package org.ofbiz.designer.util;

import javax.swing.*;
import java.util.*;
import javax.swing.event.*;
import java.awt.*;

public class KButton extends JButton{
    static Hashtable table = new Hashtable();

    public KButton(final String title){
        super(title);
        addAncestorListener(new AncestorListener(){
                               public void ancestorAdded(AncestorEvent event) {
                                   doit();
                               }
                               public void ancestorRemoved(AncestorEvent event) {
                                   doit();
                               }
                               public void ancestorMoved(AncestorEvent event) {
                                   doit();
                               }
                               private void doit(){
                                   Container parent = getParent();
                                   if(parent == null) {
                                       WARNING.println("PARENT IS NULL");
                                       return;
                                   }
                                   //String title = KButton.this.getText();
                                   for(int i=0; i<title.length(); i++) {
                                       char c = title.charAt(i);
                                       if(isAcceptableMnemonic(c)) {
                                           setMnemonic(c);
                                           break;
                                       }
                                   }
                                   removeAncestorListener(this);
                                   repaint();
                               }
                           });
    }

    private boolean isAcceptableMnemonic(char c){
        Object rootPane = getRootPane();
        Object value = table.get(rootPane);
        HashSet set;
        if(value == null) {
            set = new HashSet();
            table.put(rootPane, set);
        } else set = (HashSet)value;

        return !set.contains("" + c);
    }

    public void setMnemonic(char c){
        super.setMnemonic(c);
        ((HashSet)table.get(getRootPane())).add("" + c);
    }
}
