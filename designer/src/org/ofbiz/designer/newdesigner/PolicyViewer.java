package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.networkdesign.*;
import java.awt.event.*;
import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.util.*;
import java.io.*;
import org.ofbiz.designer.domainenv.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;

public class PolicyViewer extends JPanel {
    private XmlWrapper xml;
    private Hashtable allSecurityDomains = new Hashtable();
    private JTextArea receivePolicyField, sendPolicyField;
    private JLabel receiveLabel, sendLabel;

    public static void launchEditor(XmlWrapper domainXml, String fromDomain, String toDomain, Point location, boolean enabled) {
        WFFrame frame = new WFFrame("Policy Viewer");
        PolicyViewer editor = new PolicyViewer(domainXml, fromDomain, toDomain);
        editor.receivePolicyField.setEnabled(enabled);
        editor.sendPolicyField.setEnabled(enabled);
        DocSaver.add(domainXml, frame);

        frame.getContentPane().add(editor);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        if(args.length < 3) {
            System.err.println("enter <filename> <fromdomain> <todomain> as parameter");
            return;
        }

        if(!args[0].endsWith(".xml")) args[0] += ".xml";
        String fileName = args[0];
        String fromDomain = args[1];
        String toDomain = args[2];

        XmlWrapper xml = XmlWrapper.openDocument(new File(fileName));
        ConsoleSpacer.init();
        launchEditor(xml, fromDomain, toDomain, new Point(10, 10), true);
    }

    public void relayout() {
        int width = getBounds().width;
        int height = getBounds().height;

        int labelHeight = 30;
        int margin = 10;
        int textAreaHeight = (height-2*labelHeight-2*margin)/2;
        receiveLabel.setBounds(margin, margin, width-2*margin, labelHeight);
        receivePolicyField.setBounds(margin, margin+labelHeight, width-2*margin, textAreaHeight);
        sendLabel.setBounds(margin, margin+labelHeight+textAreaHeight, width-2*margin, labelHeight);
        sendPolicyField.setBounds(margin, margin+2*labelHeight+textAreaHeight, width-2*margin, textAreaHeight);
    }

    public PolicyViewer(XmlWrapper _xml, String fromDomain, String toDomain) {
        receiveLabel = new JLabel(toDomain + "'s receive policy");
        sendLabel = new JLabel(fromDomain + "'s send policy");
        receivePolicyField = new JTextArea("recieve Policy");
        sendPolicyField = new JTextArea("send Policy");

        receivePolicyField.setLineWrap(true);
        sendPolicyField.setLineWrap(true);

        setLayout(null);
        add(receivePolicyField);
        add(sendPolicyField);
        add(receiveLabel);
        add(sendLabel);

        xml = _xml;
        IDomainEnvWrapper de = (IDomainEnvWrapper)xml.getRoot();

        IPolicyRecordWrapper receivePolicyWrapper = (IPolicyRecordWrapper)de.getReceivePolicy(fromDomain, toDomain);
        IPolicyRecordWrapper sendPolicyWrapper = (IPolicyRecordWrapper)de.getSendPolicy(fromDomain, toDomain);

        IDocumentWrapper receivePolicyModel =  (IDocumentWrapper)PlainDocumentModel.createModelProxy();
        IDocumentWrapper sendPolicyModel =  (IDocumentWrapper)PlainDocumentModel.createModelProxy();

        receivePolicyField.setDocument(receivePolicyModel);
        sendPolicyField.setDocument(sendPolicyModel);

        new DocumentTranslator(receivePolicyModel, receivePolicyWrapper, "PCDATA", DocumentTranslator.UPDATE_MODEL);
        new DocumentTranslator(sendPolicyModel, sendPolicyWrapper, "PCDATA", DocumentTranslator.UPDATE_MODEL);
        addComponentListener(new ComponentAdapter() {
                                 public void componentResized(ComponentEvent e) {
                                     relayout();
                                 }
                             });
    }
}

