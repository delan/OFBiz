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

public class DomainProperties extends JPanel {
    private XmlWrapper xml;
    private Hashtable allSecurityDomains = new Hashtable();
    private ModifiedJComboBox securityDomainField;

    public static void launchEditor(XmlWrapper taskXml, String domainName, Point location, boolean enabled) {
        WFFrame frame = new WFFrame("Domain Changer");
        frame.setLocation(location);
        frame.setSize(122, 59);
        DomainProperties editor = new DomainProperties(taskXml, domainName);
        editor.securityDomainField.setEnabled(enabled);
        DocSaver.add(taskXml, frame);

        frame.getContentPane().add(editor);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        if(args.length < 2) {
            System.err.println("enter <filename> <domainname> as parameter");
            return;
        }

        if(!args[0].endsWith(".xml")) args[0] += ".xml";
        String fileName = args[0];
        String domainName = args[1];

        XmlWrapper xml = XmlWrapper.openDocument(new File(fileName));
        ConsoleSpacer.init();
        launchEditor(xml, domainName, new Point(10, 10), true);
    }

    private void getAllSecurityDomains(IDomainWrapper domainWrapper) {
        String xmlDir = System.getProperty("WF_XMLDIR");
        String dtdDir = System.getProperty("WF_DTDDIR");

        String href = domainWrapper.getUrlAttribute();
        if(href == null) {
            WARNING.println("security domain is null");
            return;
        }
        int poundIndex = href.indexOf("#"); 
        href = href.substring(0, poundIndex);
        XmlWrapper securitydomainXml = null;
        try {
            href = xmlDir + "\\domainenv\\" + href;
            href = XmlWrapper.fixURL(href);
            securitydomainXml = XmlWrapper.openDocument(new URL(href));
        } catch(MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        IDomainEnvWrapper domainEnv = (IDomainEnvWrapper)securitydomainXml.getRoot();
        int count = domainEnv.getDomainInfoCount();
        for(int i=0; i<count; i++) {
            IDomainInfo domainInfo = domainEnv.getDomainInfoAt(i);
            allSecurityDomains.put(domainInfo.getName(), domainInfo);
        }
    }

    public DomainProperties(XmlWrapper _xml, String domainName) {
        securityDomainField = new ModifiedJComboBox(new Vector());
        add(securityDomainField);
        xml = _xml;
        final IDomainWrapper domainWrapper = (IDomainWrapper)xml.getIdRef(domainName);
        getAllSecurityDomains(domainWrapper);
        IComboBoxModel securityDomainFieldModel =  ComboBoxModelImpl.createModelProxy();
        securityDomainField.setModel(securityDomainFieldModel);
        new MySecurityDomainTranslator((IComboBoxWrapper)securityDomainFieldModel, domainWrapper, allSecurityDomains, BaseTranslator.UPDATE_MODEL);     
    }
}

class MySecurityDomainTranslator extends BaseTranslator {
    Hashtable allSecurityDomains;   

    public MySecurityDomainTranslator(IComboBoxWrapper modelIn, IDomainWrapper domainWrapper, Hashtable allSecurityDomainsIn, String mode) {
        super(modelIn, domainWrapper);
        allSecurityDomains = allSecurityDomainsIn;
        synchronize(mode);
    }

    public void updateModelImpl() {
        IComboBoxModel model = (IComboBoxModel)getGuiModel();
        IDomainWrapper domainWrapper = (IDomainWrapper)getDataObject();

        model.removeAllElements();
        String domain = domainWrapper.getUrlAttribute();
        int poundIndex = domain.indexOf("#");
        domain = domain.substring(poundIndex+1, domain.length());

        Enumeration keys = allSecurityDomains.keys();
        while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            model.addElement(key);
        }
        if(model.getIndexOf(domain) < 0)
            throw new RuntimeException("invalid domain " + domain);

        model.setSelectedItem(domain);
    }

    public void updateDataImpl() {
        IComboBoxModel model = (IComboBoxModel)getGuiModel();
        IDomainWrapper domainWrapper = (IDomainWrapper)getDataObject();

        String domain = (String)model.getSelectedItem();
        String href = domainWrapper.getUrlAttribute();
        int poundIndex = href.indexOf("#");
        href = href.substring(0, poundIndex+1) + domain;
        ((IDomain)domainWrapper).setUrlAttribute(href);
    }
}

