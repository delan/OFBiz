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

class ExceptionHandler extends JPanel {
    private XmlWrapper xml;
    private JTextField retries, delay, rethrow, email;
    private JLabel retriesLabel, delayLabel, rethrowLabel, emailLabel;

    public static void launchEditor(XmlWrapper taskXml, String exceptionID, Point location, boolean enabled) {
        ITaskExceptionWrapper exception = (ITaskExceptionWrapper)taskXml.getIdRef(exceptionID);
        EHFrame frame = new EHFrame("Exception Handler");
        frame.setLocation(location);
        ILocalHandler lh = exception.getLocalHandler();
        if(lh == null) 
            lh = exception.createLocalHandler();
        ExceptionHandler editor = new ExceptionHandler(taskXml, ((ILocalHandlerWrapper)lh));
        DocSaver.add(taskXml, frame);

        frame.getContentPane().add(editor);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        if(args.length < 2) {
            System.err.println("enter <filename> <exceptionID> as parameter");
            return;
        }
        
        if(!args[0].endsWith(".xml")) args[0] += ".xml";
        String fileName = args[0];
        String exceptionID = args[1];

        XmlWrapper xml = XmlWrapper.openDocument(new File(fileName));
        ConsoleSpacer.init();
        launchEditor(xml, exceptionID, new Point(10, 10), true);
    }

    public ExceptionHandler(XmlWrapper _xml, ILocalHandlerWrapper lh) {
        setLayout(null);
        retries = new JTextField();
        delay = new JTextField();
        rethrow = new JTextField();
        email = new JTextField();

        retriesLabel = new JLabel("Retries");
        delayLabel = new JLabel("Delay(Sec)");
        rethrowLabel = new JLabel("Rethrow");
        emailLabel = new JLabel("Email");
        
        add(retries);
        add(delay);
        add(rethrow);
        add(email);
        add(retriesLabel);
        add(delayLabel);
        add(rethrowLabel);
        add(emailLabel);

        addComponentListener(new ComponentAdapter(){
                public void componentResized(ComponentEvent e){
                    relayout();
                }
        });

        IDocumentWrapper retriesModel =  PlainDocumentModel.createModelProxy();
        IDocumentWrapper delayModel =  PlainDocumentModel.createModelProxy();
        IDocumentWrapper rethrowModel =  PlainDocumentModel.createModelProxy();
        IDocumentWrapper emailModel =  PlainDocumentModel.createModelProxy();

        retries.setDocument(retriesModel);
        delay.setDocument(delayModel);
        rethrow.setDocument(rethrowModel);
        email.setDocument(emailModel);

        LOG.println("adding document translators");
        new DocumentTranslator(retriesModel, lh, "RetrytimesAttribute", DocumentTranslator.UPDATE_MODEL);
        new DocumentTranslator(delayModel, lh, "RetrydelayAttribute", DocumentTranslator.UPDATE_MODEL);
        new DocumentTranslator(rethrowModel, lh, "RethrowexceptionAttribute", DocumentTranslator.UPDATE_MODEL);
        new DocumentTranslator(emailModel, lh, "EmailAttribute", DocumentTranslator.UPDATE_MODEL);
        
        xml = _xml;
    }

    public void relayout() {
        int width = getWidth();
        int height = getHeight();
        
        int margin = 10;
        int fieldWidth = (width - 2*margin)/2;
        int labelHeight = 30;
        int fieldHeight = (height - 2*margin - 2*labelHeight)/2;

        int x=0, y=0;
        retriesLabel.setBounds(x=margin, y=margin, fieldWidth, labelHeight);
        delayLabel.setBounds(x+=fieldWidth, y, fieldWidth, labelHeight);
        retries.setBounds(x=margin, y+=labelHeight, fieldWidth, fieldHeight);
        delay.setBounds(x+=fieldWidth, y, fieldWidth, fieldHeight);
        rethrowLabel.setBounds(x=margin, y+=fieldHeight, fieldWidth, labelHeight);
        emailLabel.setBounds(x+=fieldWidth, y, fieldWidth, labelHeight);
        rethrow.setBounds(x=margin, y+=labelHeight, fieldWidth, fieldHeight);
        email.setBounds(x+=fieldWidth, y, fieldWidth, fieldHeight);
    }
}

class EHFrame extends WFFrame {
    public EHFrame(String title) {
        super(title);
    }
    public Rectangle getDefaultBounds() {
        return new Rectangle(100, 130, 300, 200);
    }
}




