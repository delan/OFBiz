/*
 * Created by IntelliJ IDEA.
 * User: Oliver Wieland
 * Date: Jul 27, 2001
 * Time: 3:51:45 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.pattern.XmlWrapper;
import org.ofbiz.designer.pattern.BaseTranslator;
import org.ofbiz.designer.networkdesign.ITaskWrapper;
import org.ofbiz.designer.networkdesign.INetworkTaskRealizationWrapper;
import org.ofbiz.designer.networkdesign.ITask;
import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.newdesigner.popup.ActionEvents;
import org.ofbiz.designer.util.WFFrame;

import java.awt.*;
import java.awt.event.ActionEvent;

class VerticalMappingEditor extends ArcEditorView {
    private XmlWrapper xml;

    public static final String INPUT = " inputs";
    public static final String OUTPUT = " outputs";
    private String mode = null;

    public VerticalMappingEditor(XmlWrapper _xml, String source, String destination, String _mode) {
        if(!INPUT.equals(_mode) && !OUTPUT.equals(_mode))
            throw new RuntimeException("Invalid Exception " + _mode);

        mode = _mode;
        xml = _xml;

        remove(eBox);
        remove(label4);
        remove(policies);
        eBox = null;
        label4 = null;
        policies = null;

        ITaskWrapper sourceWrapper = (ITaskWrapper)xml.getIdRef(source);
        ITaskWrapper destinationWrapper = (ITaskWrapper)xml.getIdRef(destination);
        INetworkTaskRealizationWrapper nr = null;
        if(mode.equals(INPUT)) nr = (INetworkTaskRealizationWrapper)sourceWrapper.getRealization().getNetworkTaskRealization();
        else nr = (INetworkTaskRealizationWrapper)destinationWrapper.getRealization().getNetworkTaskRealization();

        inputParams =  ListModelImpl.createModelProxy();
        outputParams =  ListModelImpl.createModelProxy();
        mappings =  ListModelImpl.createModelProxy();
        exceptions =  ComboBoxModelImpl.createModelProxy();
        alternativeTask =  ComboBoxModelImpl.createModelProxy();



        if(mode.equals(INPUT)) {
            new ListTranslator((IListWrapper)inputParams, sourceWrapper, "getInvocationParameters", "", "", BaseTranslator.UPDATE_MODEL);
            new ListTranslator((IListWrapper)outputParams, destinationWrapper, "getInvocationParameters", "", "", BaseTranslator.UPDATE_MODEL);
            new ListTranslator((IListWrapper)mappings, nr, "getInputMappingNames", "removeInputMappingByName", "addInputMappingByName", BaseTranslator.UPDATE_MODEL);
        } else {
            new ListTranslator((IListWrapper)inputParams, sourceWrapper, "getOutputNames", "", "", BaseTranslator.UPDATE_MODEL);
            new ListTranslator((IListWrapper)outputParams, destinationWrapper, "getOutputNames", "", "", BaseTranslator.UPDATE_MODEL);
            new ListTranslator((IListWrapper)mappings, nr, "getOutputMappingNames", "removeOutputMappingByName", "addOutputMappingByName", BaseTranslator.UPDATE_MODEL);
        }

        list1.setModel(inputParams);
        list2.setModel(outputParams);
        list3.setModel(mappings);

        label1.setText(sourceWrapper.getNameAttribute() + _mode);
        label2.setText(destinationWrapper.getNameAttribute() + _mode);
        label3.setText(sourceWrapper.getNameAttribute() + " --> " + destinationWrapper.getNameAttribute() + " mapping");
    }

    public static void launchEditor(XmlWrapper xml, String source, String destination, String mode) {
        VerticalMappingEditor mappingEditor = new VerticalMappingEditor(xml, source, destination, mode);
        String sourceName = ((ITask)xml.getIdRef(source)).getNameAttribute();
        String destinationName = ((ITask)xml.getIdRef(destination)).getNameAttribute();
        String titleString = sourceName + " --> " + destinationName;
        AFrame frame = new AFrame(titleString);
        BasicMenuBar bm = new BasicMenuBar();
        frame.setJMenuBar(bm);
        bm.addActionListener(mappingEditor);
        mappingEditor.frame = frame;

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(mappingEditor);
        frame.setVisible(true);
        mappingEditor.relayout();
        mappingEditor.validate();
        mappingEditor.repaint();

        DocSaver.add(xml, frame);
    }

    public void actionPerformed(ActionEvent e) {
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
