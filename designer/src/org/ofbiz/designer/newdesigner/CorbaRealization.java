package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.networkdesign.*;
import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import org.ofbiz.designer.newdesigner.popup.*;

class CorbaRealization extends CorbaRealizationView {
    XmlWrapper xml;

    public CorbaRealization(XmlWrapper _xml, String taskID) {
        xml = _xml;
        ITaskWrapper task = (ITaskWrapper)xml.getIdRef(taskID);
        ICorbaInvocationWrapper realization = (ICorbaInvocationWrapper)task.getRealization().getSimpleRealization().getNonTransactionalTaskRealization().getCorbaInvocation();

        parameters =  ListModelImpl.createModelProxy();     
        tmInputParameters =  ListModelImpl.createModelProxy();       
        tmOutputParameters =  ListModelImpl.createModelProxy();       
        fmappings =  ListModelImpl.createModelProxy();       
        //rmappings =  ListModelImpl.createModelProxy();       

        objectMarker = PlainDocumentModel.createModelProxy();
        serverName = PlainDocumentModel.createModelProxy();
        serverHost = PlainDocumentModel.createModelProxy();
        className = PlainDocumentModel.createModelProxy();
        methodName = PlainDocumentModel.createModelProxy();
        returnValue = PlainDocumentModel.createModelProxy();
        rMapping = PlainDocumentModel.createModelProxy();


        new ListTranslator(parameters, realization, "getParametersNames", "removeParameterByName", "addParameterByName", BaseTranslator.UPDATE_MODEL);      
        new ListTranslator(tmInputParameters, task, "getExplodedReadableParameters", "", "", BaseTranslator.UPDATE_MODEL);        
        new ListTranslator(tmOutputParameters, task, "getExplodedWritableParameters", "", "", BaseTranslator.UPDATE_MODEL);        
        new ListTranslator(fmappings, realization, "getForwardMappings", "removeForwardMapping", "addForwardMapping", BaseTranslator.UPDATE_MODEL);     
        //new ListTranslator(rmappings, realization, "getReverseMappings", "removeReverseMapping", "addReverseMapping", BaseTranslator.UPDATE_MODEL);     

        new DocumentTranslator(objectMarker, realization, "ObjectmarkerAttribute", BaseTranslator.UPDATE_MODEL); 
        new DocumentTranslator(serverName, realization, "ServernameAttribute", BaseTranslator.UPDATE_MODEL); 
        new DocumentTranslator(serverHost, realization, "ServerhostAttribute", BaseTranslator.UPDATE_MODEL); 
        new DocumentTranslator(className, realization, "ClassnameAttribute", BaseTranslator.UPDATE_MODEL); 
        new DocumentTranslator(methodName, realization, "MethodnameAttribute", BaseTranslator.UPDATE_MODEL); 
        new DocumentTranslator(returnValue, realization, "ReturnvalueAttribute", BaseTranslator.UPDATE_MODEL); 
        new DocumentTranslator(rMapping, realization, "ReturnValueMapping", BaseTranslator.UPDATE_MODEL); 

        objectField.setDocument(objectMarker);
        serverNameField.setDocument(serverName);
        serverHostField.setDocument(serverHost);
        classNameField.setDocument(className);
        methodNameField.setDocument(methodName);
        returnValueField.setDocument(returnValue);
        returnValueMapping.setDocument(rMapping);

        parametersList.setModel(parameters);
        tmInputParametersList.setModel(tmInputParameters);
        tmOutputParametersList.setModel(tmOutputParameters);
        forwardMappingList.setModel(fmappings);
    }

    public static void launchEditor(XmlWrapper xml, String taskID) {
        LOG.println("taskID is " + taskID);
        CFrame frame = new CFrame("Corba Realization");
        BasicMenuBar bm = new BasicMenuBar();
        frame.setJMenuBar(bm);
        CorbaRealization editor = new CorbaRealization(xml, taskID);
        bm.addActionListener(editor);
        editor.frame = frame;

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(editor);
        frame.setVisible(true);
        DocSaver.add(xml, frame);
    }

    public static void main(String[] args) {
        if(args.length != 2) {
            LOG.println("input <filename> <taskID>");
            return;
        }

        String fileName = args[0];
		LOG.println("fileName is " + fileName);
        if(!fileName.endsWith(".xml")) {
            fileName += ".xml";
        }
        String taskID = args[1];
        XmlWrapper xml = XmlWrapper.openDocument(new File(fileName));
        launchEditor(xml, taskID);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals(ActionEvents.SAVE_EXIT)) {
            DocSaver.remove(frame);
            xml.saveDocument();
            frame.dispose();
            System.exit(0);
        } else if(e.getActionCommand().equals(ActionEvents.DISCARD_QUIT)) {
            DocSaver.remove(frame);
            frame.dispose();
            System.exit(0);
        } else super.actionPerformed(e);
    }
    private WFFrame frame = null;
}

class CFrame extends WFFrame{
    public CFrame(String title){
        super(title);
    }
    public Rectangle getDefaultBounds() {
        return new Rectangle(140, 140, 907, 538);
    }
}

