package org.ofbiz.designer.newdesigner.operatoreditor;

import javax.swing.*;
import org.ofbiz.designer.newdesigner.model.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.networkdesign.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import org.ofbiz.designer.util.*;
import java.io.*;
import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.newdesigner.*;
import org.ofbiz.designer.newdesigner.popup.*;

public class OperatorEditor extends JPanel implements ActionListener {
    public static final String OPERATOR = "OPERATOR";
    public static final String TASK = "TASK";

    private ITaskWrapper taskRef = null;

    public OperatorEditor(XmlWrapper taskXmlIn, String taskName, String mode) {
        if(!mode.equals(OperatorEditorType.INPUT_OPERATOR) && !mode.equals(OperatorEditorType.OUTPUT_OPERATOR))
            throw new RuntimeException("Unknown mode " + mode);

        taskXml = taskXmlIn;
        initOperatorEditor(taskXmlIn, taskName, mode);
    }

    private  void initOperatorEditor(XmlWrapper taskXmlIn, String taskName, String mode) {
        taskRef = (ITaskWrapper)taskXmlIn.getIdRef(taskName);
        IOperatorWrapper operator = null;
        if(mode.equals(OperatorEditorType.INPUT_OPERATOR)) {
            IInputOperator op = taskRef.getInputOperator();
            if(op != null) operator = (IOperatorWrapper)op.getOperator();
            else operator = (IOperatorWrapper)taskRef.createInputOperator();
        } else {
            IOutputOperator op = taskRef.getOutputOperator();
            if(op != null) operator = (IOperatorWrapper)op.getOperator();
            else operator = (IOperatorWrapper)taskRef.createOutputOperator();
        }
        final IOperatorEditorPanelModel panelModel = OperatorEditorPanelModel.createModelProxy();
        panelModel.setEditorOperatorType(mode);
        Vector vec = new Vector();
        vec.addElement(operator);
        vec.addElement(taskRef);
        new OperatorEditorPanelTranslator(((IOperatorEditorPanelModelWrapper)panelModel), vec, mode, BaseTranslator.UPDATE_MODEL);

        setLayout(new BorderLayout());

        final JSplitPane outerSplitPane = new JSplitPane();
        JSplitPane innerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        vec = new Vector();
        vec.addElement(OperatorType.LOOP_OPERATOR.toString());
        vec.addElement(OperatorType.AND_OPERATOR.toString());
        vec.addElement(OperatorType.OR_OPERATOR.toString());
        OperatorEditorList list1 = new OperatorEditorList(vec, OPERATOR);

        IListModel taskListModel =  ListModelImpl.createModelProxy();
        if(mode.equals(OperatorEditorType.INPUT_OPERATOR)) 
            new ListTranslator((IListWrapper)taskListModel, taskRef, "getInputArcsAndParent", "", "", BaseTranslator.UPDATE_MODEL);     
        else new ListTranslator((IListWrapper)taskListModel, taskRef, "getOutputArcsAndParent", "", "", BaseTranslator.UPDATE_MODEL);     
        OperatorEditorList list2 = new OperatorEditorList(TASK);
        list2.setModel(taskListModel);

        OperatorEditorPanel operatorEditorPanel = new OperatorEditorPanel(panelModel);
        JScrollPane pane0 = new JScrollPane(operatorEditorPanel);

        JScrollPane pane1 = new JScrollPane(list1);
        JScrollPane pane2 = new JScrollPane(list2);

        // workaround for a repaint problem with JScrollPane
        scrollPaneBugWorkaround(pane0, pane1, pane2);
        
        // end workaround

        innerSplitPane.setLeftComponent(pane1);
        innerSplitPane.setRightComponent(pane2);
        innerSplitPane.setDividerLocation(70);

        outerSplitPane.setLeftComponent(pane0);
        outerSplitPane.setRightComponent(innerSplitPane);
        new Thread() {
            public void run() {
                while(getRootPane() == null)
                    SafeThread.sleep(200);
                outerSplitPane.setDividerLocation(getRootPane().getParent().getWidth()-200);
            }
        }.start();

        add(outerSplitPane, BorderLayout.CENTER);
    }

    private void scrollPaneBugWorkaround(JScrollPane pane0, JScrollPane pane1, JScrollPane pane2){
        AdjustmentListener adjustmentListener = new  AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                repaint();
            }
        };
        pane0.getVerticalScrollBar().addAdjustmentListener(adjustmentListener);
        pane0.getHorizontalScrollBar().addAdjustmentListener(adjustmentListener);
        pane1.getVerticalScrollBar().addAdjustmentListener(adjustmentListener);
        pane1.getHorizontalScrollBar().addAdjustmentListener(adjustmentListener);
        pane2.getVerticalScrollBar().addAdjustmentListener(adjustmentListener);
        pane2.getHorizontalScrollBar().addAdjustmentListener(adjustmentListener);
    }

    public static void main(String[] args) {
        if(args.length != 3) {
            System.err.println("enter <filename> <taskname> <MODE>");
            System.err.println("Mode is either " + OperatorEditorType.INPUT_OPERATOR + " or " + OperatorEditorType.OUTPUT_OPERATOR);
            return;
        }
        String fileName = args[0];
        String taskName = args[1];
        String mode = args[2];

        if(!fileName.endsWith(".xml"))
            fileName += ".xml";
        if(!mode.equals(OperatorEditorType.INPUT_OPERATOR) && !args[2].equals(OperatorEditorType.OUTPUT_OPERATOR)) {
            System.err.println("invalid mode, should be " + OperatorEditorType.INPUT_OPERATOR + " or " + OperatorEditorType.OUTPUT_OPERATOR);
            return;
        }

        XmlWrapper taskXml = XmlWrapper.openDocument(new File(fileName));
        ConsoleSpacer.init();
        launchOperatorEditor(taskXml, taskName, mode);
    }

    public static void launchOperatorEditor(XmlWrapper xml, String taskName, String mode) {
        OFrame frame = new OFrame(mode);
        BasicMenuBar bm = new BasicMenuBar();
        frame.setJMenuBar(bm);
        OperatorEditor operatorEditor = new OperatorEditor(xml, taskName, mode);
        String displayMode = mode.equals(OperatorEditorType.INPUT_OPERATOR)?"Input Operator":"Output Operator";
        frame.setTitle(displayMode + " for " + operatorEditor.taskRef.getNameAttribute());
        bm.addActionListener(operatorEditor);
        DocSaver.add(xml, frame);
        frame.getContentPane().add(operatorEditor);
        operatorEditor.frame = frame;
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(ActionEvents.SAVE_EXIT)) {
            DocSaver.remove(frame);
            taskXml.saveDocument();
            frame.dispose();
            System.exit(0);
        } else if(e.getActionCommand().equals(ActionEvents.DISCARD_QUIT)) {
            DocSaver.remove(frame);
            frame.dispose();
            System.exit(0);
        }
    }
    private WFFrame frame = null;
    private XmlWrapper taskXml = null;
}

class OFrame extends WFFrame {
    public OFrame(String title) {
        super(title);
    }
    public Rectangle getDefaultBounds() {
        return new Rectangle(120, 120, 661, 368);
    }
}

