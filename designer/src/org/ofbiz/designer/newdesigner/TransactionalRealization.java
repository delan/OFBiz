package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.networkdesign.*;
import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import org.ofbiz.designer.newdesigner.popup.*;

class TransactionalRealization extends TransactionalRealizationView {
    XmlWrapper xml;

    public TransactionalRealization(XmlWrapper _xml, String taskID) {
        xml = _xml;
        ITaskWrapper task = (ITaskWrapper)xml.getIdRef(taskID);
        ITransactionalTaskRealizationWrapper realization = (ITransactionalTaskRealizationWrapper)task.getRealization().getSimpleRealization().getTransactionalTaskRealization();
        LOG.println("realization is " + realization);

        inputParams =  ListModelImpl.createModelProxy();        
        outputParams =  ListModelImpl.createModelProxy();       

        urlDoc = PlainDocumentModel.createModelProxy();
        dbmsDoc = PlainDocumentModel.createModelProxy();
        userNameDoc = PlainDocumentModel.createModelProxy();
        passwdDoc = PlainDocumentModel.createModelProxy();
        queryDoc = PlainDocumentModel.createModelProxy();

        new ListTranslator(inputParams, realization, "getInputNames", "removeTransactionalInput", "addTransactionalInput", BaseTranslator.UPDATE_MODEL);        
        new ListTranslator(outputParams, realization, "getOutputNames", "removeTransactionalOutput", "addTransactionalOutput", BaseTranslator.UPDATE_MODEL);     

        new DocumentTranslator(urlDoc, realization, "UrlAttribute", BaseTranslator.UPDATE_MODEL); 
        new DocumentTranslator(dbmsDoc, realization, "DatabaseAttribute", BaseTranslator.UPDATE_MODEL); 
        new DocumentTranslator(userNameDoc, realization, "UsernameAttribute", BaseTranslator.UPDATE_MODEL); 
        new DocumentTranslator(passwdDoc, realization, "UserpasswordAttribute", BaseTranslator.UPDATE_MODEL); 
        new DocumentTranslator(queryDoc, realization, "QueryAttribute", BaseTranslator.UPDATE_MODEL); 

        urlField.setDocument(urlDoc);
        dbmsField.setDocument(dbmsDoc);
        userNameField.setDocument(userNameDoc);
        passwdField.setDocument(passwdDoc);
        queryField.setDocument(queryDoc);
        inputs.setModel(inputParams);
        outputs.setModel(outputParams);

    }

    public static void launchEditor(XmlWrapper xml, String taskID) {
        TrFrame frame = new TrFrame("Transactional Realization");
        BasicMenuBar bm = new BasicMenuBar();
        frame.setJMenuBar(bm);
        TransactionalRealization editor = new TransactionalRealization(xml, taskID);
        bm.addActionListener(editor);
        editor.frame = frame;

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(editor);
        frame.setVisible(true);
        editor.relayout();
        editor.repaint();

        DocSaver.add(xml, frame);
    }

    public static void main(String[] args) {
        if(args.length != 2) {
            LOG.println("input <filename> <taskID>");
            return;
        }
        String fileName = args[0];
        String taskID = args[1];
        XmlWrapper xml = XmlWrapper.openDocument(new File(fileName));
        launchEditor(xml, taskID);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals(ActionEvents.SAVE_EXIT)) {
            DocSaver.remove(frame);
            xml.saveDocument();
            frame.dispose();
        } else if(e.getActionCommand().equals(ActionEvents.DISCARD_QUIT)) {
            DocSaver.remove(frame);
            frame.dispose();
        } else super.actionPerformed(e);
    }
    private WFFrame frame = null;
}

class TrFrame extends WFFrame{
    public TrFrame(String title){
        super(title);
    }
    public Rectangle getDefaultBounds() {
        return new Rectangle(150, 150, 537, 725);
    }
}

