package org.ofbiz.designer.newdesigner;

import javax.swing.event.*;
import org.ofbiz.designer.util.*;
import javax.swing.*;
import org.ofbiz.designer.networkdesign.*;
import java.util.*;
import org.ofbiz.designer.pattern.*;
import java.io.*;
import org.ofbiz.designer.generic.*;

public class DataPermissions extends DataPermissionsView {
    ITaskWrapper taskWrapper;
    IListModel listModel;

    public DataPermissions(ITaskWrapper _taskWrapper) {
        taskWrapper = _taskWrapper;

        listModel =  ListModelImpl.createModelProxy();
        objectList.setModel(listModel);
        new ListTranslator((IListWrapper)listModel, taskWrapper, "getDataSecurityMaskNames", "", "", BaseTranslator.UPDATE_MODEL);      
        if(listModel.getSize() > 0)
            objectList.setSelectedIndex(0);
    }

    public static void launchDataPermissions(String taskName, XmlWrapper xml) {
        WFFrame frame = new WFFrame("Data Permissions");
        ITaskWrapper taskWrapper = (ITaskWrapper)xml.getIdRef(taskName);
        DataPermissions view = new DataPermissions(taskWrapper);
        frame.getContentPane().add(view);
        frame.setVisible(true);
        DocSaver.add(xml, frame);
    }

    public static void main(String[] args) {
        if(args.length != 2) {
            WARNING.println("specify <filename> <taskname>");
            return;
        }
        String fileName = args[0];
        if(!fileName.startsWith(".xml")) fileName += ".xml";
        String taskName = args[1];
        XmlWrapper xml  = XmlWrapper.openDocument(new File(fileName));
        launchDataPermissions(taskName, xml);
    }

    private int lastValue = -1;
    public void valueChanged(ListSelectionEvent e) {
        int index = objectList.getSelectedIndex();
        if(index != lastValue) lastValue = index;
        else return;

        String dataName = (String)objectList.getSelectedValue();
        IDataSecurityMask data = taskWrapper.getDataSecurityMaskByName(dataName);
        if(data ==null) data = taskWrapper.createDataSecurityMask(dataName);

        int count = data.getFieldMaskCount();

        fieldComboBoxes.removeAllElements();
        fieldLabels.removeAllElements();
        rightPanel.removeAll();

        boolean pureInput = taskWrapper.isPureInputParameter(dataName);

        String accessTypesMethod = null;
        if(pureInput) accessTypesMethod = "getPureInputAccessTypes";
        else accessTypesMethod = "getAccessTypes";
        for(int i=0; i<count; i++) {
            IFieldMaskWrapper fieldMask = (IFieldMaskWrapper)data.getFieldMaskAt(i);
            String fieldName = fieldMask.getFieldnameAttribute();

            IComboBoxModel comboModel =  ComboBoxModelImpl.createModelProxy();
            JComboBox comboBox = new JComboBox(comboModel);
            new ComboBoxTranslator((IComboBoxWrapper)comboModel, fieldMask, accessTypesMethod, "", "AccessType", BaseTranslator.UPDATE_MODEL);      

            fieldComboBoxes.addElement(comboBox);
            fieldLabels.addElement(new JLabel(fieldName));
        }

        relayout();
    }
}
