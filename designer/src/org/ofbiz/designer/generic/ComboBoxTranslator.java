package org.ofbiz.designer.generic;

import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.util.*;


//  WARNING !! 
//  Use org.ofbiz.designer.util.ModifiedJComboBox instead of javax.swing.JComboBox for the GUI
//  read ModifiedJComboBox for details.

public class ComboBoxTranslator extends BaseTranslator {
    String getMethodName, removeMethodName, selectedMethodRoot;

    // getMethodName is the name of a method with the signature
    // public Vector getMethodName();
    // removeMethodName is the name of a method with the signature
    // public void removeMethodName(int x);
    // selectedMethodName correspond to 2 methods with the signatures
    // public String getselectedMethodName();
    // public String setselectedMethodName();

/*
    public ComboBoxTranslator(IRegistrar objIn, String getMethodNameIn, String removeMethodNameIn, String selectedMethodRootIn) {
        super("org.ofbiz.designer.generic.IComboBoxModel", "org.ofbiz.designer.generic.ComboBoxModelImpl", objIn);
        if (getMethodNameIn == null || removeMethodNameIn == null || selectedMethodRootIn == null)
            throw new RuntimeException("NULL PARAMETER ENCOUNTERED!!");
        getMethodName = getMethodNameIn;
        removeMethodName = removeMethodNameIn;
        selectedMethodRoot = selectedMethodRootIn;
    }
    */

    public ComboBoxTranslator(IComboBoxWrapper modelIn, IRegistrar dataObjectIn, String getMethodNameIn, String removeMethodNameIn, String selectedMethodRootIn, String mode) {
        super(modelIn, dataObjectIn);
        if (getMethodNameIn == null || removeMethodNameIn == null || selectedMethodRootIn == null)
            throw new RuntimeException("NULL PARAMETER ENCOUNTERED!!");
        getMethodName = getMethodNameIn;
        removeMethodName = removeMethodNameIn;
        selectedMethodRoot = selectedMethodRootIn;
        synchronize(mode);
    }

    public ComboBoxTranslator(IComboBoxWrapper modelIn, Vector dataVec, String getMethodNameIn, String removeMethodNameIn, String selectedMethodRootIn, String mode) {
        super(modelIn, dataVec);
        if (getMethodNameIn == null || removeMethodNameIn == null || selectedMethodRootIn == null)
            throw new RuntimeException("NULL PARAMETER ENCOUNTERED!!");
        getMethodName = getMethodNameIn;
        removeMethodName = removeMethodNameIn;
        selectedMethodRoot = selectedMethodRootIn;
        synchronize(mode);
    }

    public void updateModelImpl () {
        IComboBoxModel model = (IComboBoxModel)getGuiModel();

        try {
            if (getDataObjectAt(0) == null) {
                int size = model.getSize();
                for (int i=size-1; i>=0; i--)
                    model.removeElementAt(i);
                return;
            }

            Vector newValue = null;
            try {
                newValue = (Vector)getDataObjectAt(0).getClass().getMethod(getMethodName, null).invoke(getDataObjectAt(0), null);
            } catch (NoSuchMethodException e) {
                LOG.println("WARNING !! method \"Vector " + getMethodName + "()\" does not exist in " + getDataObjectAt(0).getProxyType());
                throw e;
            }

            // add values present in data but not in model
            int size = newValue.size();
            for (int i=size-1; i>=0; i--) {
                String str = (String)newValue.elementAt(i);
                if (model.getIndexOf(str) < 0)
                    model.addElement(str);
            }

            // remove values present in model but not in data
            size = model.getSize();
            for (int i=size-1; i>=0; i--) {
                String str = (String)model.getElementAt(i);
                if (!newValue.contains(str)) {
                    model.removeElementAt(i);
                    i++;
                }
            }

            // set selected item
            String selectedObj = null;
            try {
                selectedObj = (String)getDataObjectAt(0).getClass().getMethod("get" + selectedMethodRoot, null).invoke(getDataObjectAt(0), null);
            } catch (NoSuchMethodException e) {
                WARNING.println("could not find method get" + selectedMethodRoot);
                e.printStackTrace();
                return;
            }
            model.setSelectedItem(selectedObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDataImpl () {
        if (selectedMethodRoot == null) {
            LOG.println("selectedMethodRoot is NULL");
            return;
        }
        

        // update Data only sets value of selected index
        // GUI cannot change items in the dropdown list

        IComboBoxModel model = (IComboBoxModel)getGuiModel();
        String selectedItem = (String)model.getSelectedItem();
        try {
            LOG.println("setting " + selectedMethodRoot + ":" + selectedItem);
            if (selectedItem == null) {
                LOG.println("removing");
                getDataObjectAt(0).getClass().getMethod("remove" + selectedMethodRoot, null).invoke(getDataObjectAt(0), null);
            } else {
                LOG.println("setting");
                Class[] params = {String.class};
                Object[] paramValues = {selectedItem};
                getDataObjectAt(0).getClass().getMethod("set" + selectedMethodRoot, params).invoke(getDataObjectAt(0), paramValues);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    public void updateDataImpl () {
        if (getMethodName == null){
            LOG.println("getMethodName is NULL");
            return;
        }
        LOG.println("getMethodName is " + getMethodName + ":" + getDataObjectAt(0).getClass().getName());
        LOG.println("removeMethodName is >" + removeMethodName + "<");
        IComboBoxModel model = (IComboBoxModel)getGuiModel();
        
        if (getDataObject() == null) return;
        try{
            Vector newValue = (Vector)getDataObject().getClass().getMethod(getMethodName, null).invoke(getDataObject(), null);

            // add values present in model but not in data
            int size = model.getSize();
            
            for (int i=size-1; i>=0; i--){
                String str = (String)model.getElementAt(i);
                if (!newValue.contains(str))
                    newValue.addElement(str);
            }

            // remove values present in data but not in model
            size = newValue.size();
            for (int i=size-1; i>=0; i--){
                String str = (String)newValue.elementAt(i);
                if (model.getIndexOf(str) < 0){
                    Class[] params = {String.class};
                    Object[] paramValues = {str};
                    getDataObject().getClass().getMethod(removeMethodName, params).invoke(getDataObject(), paramValues);
                    i++;
                }
            }
            
            // set selected item
            String selectedItem = (String)model.getSelectedItem();
            Class[] params = {String.class};
            Object[] paramValues = {selectedItem};
            getDataObject().getClass().getMethod("set" + selectedMethodRoot, params).invoke(getDataObject(), paramValues);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    */
}

