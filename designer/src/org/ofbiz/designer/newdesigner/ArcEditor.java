package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.util.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.event.*;
import org.ofbiz.designer.pattern.*;
import java.io.*;
import org.ofbiz.designer.networkdesign.*;
import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.newdesigner.popup.*;
import org.ofbiz.designer.domainenv.*;
import java.net.*;


public class ArcEditor extends ArcEditorView {
    private IArcWrapper arc = null;
    private XmlWrapper xml;

    private static IArcWrapper getArc(ITaskWrapper source, ITaskWrapper destination) {
        String inarcs = destination.getInarcsAttribute();
        StringTokenizer stk = new StringTokenizer(inarcs);
        while(stk.hasMoreTokens()) {
            IArcWrapper arc = (IArcWrapper)source.getXml().getIdRef(stk.nextToken());
            if(arc.getSourceTaskAttribute().equals(source.getIdAttribute()))
                return arc;
        }
        return null;
    }

    public ArcEditor(XmlWrapper _xml, String source, String destination) {
        xml = _xml;

        ITaskWrapper sourceWrapper = null;
        if(xml.getIdRef(source) instanceof ITask) 
            sourceWrapper = (ITaskWrapper)xml.getIdRef(source);
        else {
            IArc arc = (IArc)xml.getIdRef(source);
            sourceWrapper = (ITaskWrapper)xml.getIdRef(arc.getSourceAttribute());
        }

        ITaskWrapper destinationWrapper = (ITaskWrapper)xml.getIdRef(destination);

        arc = (IArcWrapper)getArc(sourceWrapper, destinationWrapper);
        if(arc.getArctypeAttribute().equals("Success")) {
            remove(eBox);
            remove(label4);
            eBox = null;
            label4 = null;
        }

        inputParams =  ListModelImpl.createModelProxy();        
        outputParams =  ListModelImpl.createModelProxy();       
        mappings =  ListModelImpl.createModelProxy();       
        exceptions =  ComboBoxModelImpl.createModelProxy();     
        alternativeTask =  ComboBoxModelImpl.createModelProxy();     


        new ListTranslator((IListWrapper)inputParams, sourceWrapper, "getOutputNames", "", "", BaseTranslator.UPDATE_MODEL);        
        new ListTranslator((IListWrapper)outputParams, destinationWrapper, "getInvocationParameters", "", "", BaseTranslator.UPDATE_MODEL);     
        new ListTranslator((IListWrapper)mappings, arc, "getMappingNames", "removeMappingByName", "addMappingByName", BaseTranslator.UPDATE_MODEL);     

        Vector vec = new Vector();
        vec.addElement(arc);
        vec.addElement(sourceWrapper);
        new ComboBoxTranslator((IComboBoxWrapper)exceptions, vec, "getSourceExceptionNames", "", "ExceptionAttribute", BaseTranslator.UPDATE_MODEL);        
        new ComboBoxTranslator((IComboBoxWrapper)alternativeTask, arc, "getAllTaskNames", "", "AlternativeTransitionByName", BaseTranslator.UPDATE_MODEL);        

        list1.setModel(inputParams);
        list2.setModel(outputParams);
        list3.setModel(mappings);
        if(eBox != null) eBox.setModel(exceptions);
        //altBox.setModel(alternativeTask);

        label1.setText(sourceWrapper.getNameAttribute() + " outputs");
        label2.setText(destinationWrapper.getNameAttribute() + " inputs");
        label3.setText(sourceWrapper.getNameAttribute() + " --> " + destinationWrapper.getNameAttribute() + " mapping");
        if(label4 != null) label4.setText("Exceptions");
    }

    public static void launchArcEditor(XmlWrapper xml, String source, String destination) {
        ArcEditor arcEditor = new ArcEditor(xml, source, destination);
        String sourceName = ((ITask)xml.getIdRef(source)).getNameAttribute();
        String destinationName = ((ITask)xml.getIdRef(destination)).getNameAttribute();
        String titleString = sourceName + " --> " + destinationName + " [" + arcEditor.arc.getArctypeAttribute() + "]";
        AFrame frame = new AFrame(titleString);
        BasicMenuBar bm = new BasicMenuBar();
        frame.setJMenuBar(bm);
        bm.addActionListener(arcEditor);
        arcEditor.frame = frame;

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(arcEditor);
        frame.setVisible(true);
        arcEditor.relayout();
        arcEditor.validate();
        arcEditor.repaint();

        DocSaver.add(xml, frame);
    }

    public static void launchArcEditor(XmlWrapper xml, IArc arc) {
        WFFrame frame = new WFFrame("Arc Editor");
        ArcEditor arcEditor = new ArcEditor(xml, arc.getSourceAttribute(), arc.getDestinationAttribute());

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(arcEditor);
        frame.setVisible(true);
        arcEditor.relayout();
        arcEditor.validate();
        arcEditor.repaint();

        DocSaver.add(xml, frame);
    }

    public static void main(String[] args) {
        String sourceFileName = args[0];
        String source = args[1];
        String destination = args[2];
        XmlWrapper xml = XmlWrapper.openDocument(new File(sourceFileName));
        launchArcEditor(xml, source, destination);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(ActionEvents.SAVE_EXIT)) {
            DocSaver.remove(frame);
            xml.saveDocument();
            frame.dispose();
        } else if(e.getActionCommand().equals(ActionEvents.DISCARD_QUIT)) {
            DocSaver.remove(frame);
            frame.dispose();
        } else if(e.getActionCommand().equals(POLICIES)) {
            String xmlDir = System.getProperty("WF_XMLDIR");
            String dtdDir = System.getProperty("WF_DTDDIR");

            String sourceSec = ((ITaskWrapper)xml.getIdRef(arc.getSourceTaskAttribute())).getSecuritydomainurlAttribute();
            String destinationSec = ((ITaskWrapper)xml.getIdRef(arc.getDestinationAttribute())).getSecuritydomainurlAttribute();

            sourceSec = xmlDir + "\\src\\org\\ofbiz\\designer\\domainenv\\" + sourceSec;
            destinationSec = xmlDir + "\\src\\org\\ofbiz\\designer\\domainenv\\" + destinationSec;

            String prefix, sourceSuffix, destinationSuffix;
            int poundIndex = sourceSec.indexOf("#");
            String sourcePrefix = sourceSec.substring(0, poundIndex);
            sourceSuffix = sourceSec.substring(poundIndex+1, sourceSec.length());

            poundIndex = sourceSec.indexOf("#");
            String destionationprefix = sourceSec.substring(0, poundIndex);
            if(!sourcePrefix.equals(destionationprefix))
                throw new RuntimeException("Source prefix " + sourcePrefix + " does not equal destination prefix " + destionationprefix);
            prefix = destionationprefix;
            destinationSuffix = destinationSec.substring(poundIndex+1, destinationSec.length());

            IDomainEnvWrapper de = null;
            try {
                de = (IDomainEnvWrapper)XmlWrapper.openDocument(new URL(XmlWrapper.fixURL(sourceSec))).getRoot();
            } catch(MalformedURLException me) {
                me.printStackTrace();
            }
            PolicyViewer.launchEditor(de.getXml(), sourceSuffix, destinationSuffix, null, true);
        } else super.actionPerformed(e);
    }
    private WFFrame frame = null;
}




