package org.ofbiz.designer.newdesigner.LatticeEditor;

import javax.swing.*;
import javax.swing.border.*;
import org.ofbiz.designer.generic.*;
import java.awt.event.*;
import java.awt.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.PolicyModelContainer;

public class PolicyDialog extends JDialog {

    protected PolicyModelContainer sendModel, receiveModel;
    protected String fromId, toId;
    protected JTextArea sendArea,receiveArea;
    protected JButton okButton,cancelButton,sendKillButton,receiveKillButton;
    protected ArrowComponent theArrow;
    protected JLabel ownerLabel, betweenLabel;

    public PolicyDialog(DomainEnvView env) {
        super((JFrame) env);
        initDialog();
    }

    public void activate(PolicyModelContainer sendModelIn, PolicyModelContainer receiveModelIn) {
        sendModel = sendModelIn;
        receiveModel = receiveModelIn;
        fromId = sendModel.getFromDomain().getId();
        toId = sendModel.getToDomain().getId();

        setTitle("Policy Editor");
        setLocationRelativeTo(getParent());
        ownerLabel.setText("Policies of "+sendModel.getFromDomain().getName());
        betweenLabel.setText("In relation to "+sendModel.getToDomain().getName());


        try {
            sendArea.setDocument(sendModel);
            receiveArea.setDocument(receiveModel);
        } catch(Exception e) {
            e.printStackTrace();
        }
        show();
    }

    private void initDialog() {
        setBounds(300,300,300,300);
        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel,BoxLayout.Y_AXIS));

        Box box1 = Box.createHorizontalBox();
        ownerLabel = new JLabel();
        box1.add(ownerLabel);
        labelPanel.add(box1);

        box1 = Box.createHorizontalBox();
        betweenLabel = new JLabel();
        box1.add(betweenLabel);
        labelPanel.add(box1);

        labelPanel.setBorder(new EtchedBorder());
        getContentPane().add(labelPanel);

        box1 = Box.createHorizontalBox();
        box1.add(new JLabel("Send Policy: "));
        getContentPane().add(box1);

        box1 = Box.createHorizontalBox();
        sendArea = new JTextArea();

        sendArea.setPreferredSize(new Dimension(300,150));
        box1.add(sendArea);
        getContentPane().add(box1);

        box1 = Box.createHorizontalBox();
        box1.add(new JLabel("Receive Policy: "));
        getContentPane().add(box1);

        box1 = Box.createHorizontalBox();
        receiveArea = new JTextArea();

        receiveArea.setPreferredSize(new Dimension(300,150));
        box1.add(receiveArea);
        getContentPane().add(box1);

        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent e) {
                                  if(sendArea.getDocument().getLength()==0) {
                                      sendModel.die();
                                  }
                                  if(receiveArea.getDocument().getLength()==0) {
                                      receiveModel.die();
                                  }
                                  hide();
                              }
                          });

    }
}
