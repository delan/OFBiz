package org.ofbiz.designer.generic;

import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.util.*;
import java.lang.reflect.*;

public class ListTranslator extends BaseTranslator {
    String getMethodName, removeMethodName, addMethodName;

    // getMethodName is the name of a method with the signature
    // public Vector getMethodName();
    // removeMethodName is the name of a method with the signature
    // public void removeMethodName(String obj);
    // addMethodName is the name of a method with the signature
    // public void addMethodName(String obj);

    public ListTranslator(IListWrapper modelIn, IRegistrar dataObjectIn, String getMethodNameIn, String removeMethodNameIn, String addMethodNameIn, String mode) {
        super(modelIn, dataObjectIn);
        if(getMethodNameIn == null || removeMethodNameIn == null)
            throw new RuntimeException("NULL PARAMETER ENCOUNTERED!!");
        getMethodName = getMethodNameIn;
        removeMethodName = removeMethodNameIn;
        addMethodName = addMethodNameIn;
        synchronize(mode);
    }

    public void updateDataImpl() {
        if(getMethodName == null) {
            WARNING.println("targetMethodSuffix is NULL");
            return;
        }
        ListModelImpl model = (ListModelImpl)getGuiModel();
        try {
            Vector newValue = (Vector)getDataObject().getClass().getMethod(getMethodName, null).invoke(getDataObject(), null);

            // add values present in model but not in data
            int size = model.getSize();
            for(int i=size-1; i>=0; i--) {
                String str = (String)model.elementAt(i);

                if(!newValue.contains(str)) {
                    Class[] params = {String.class};
                    Object[] paramValues = {str};
                    try {
                        getDataObject().getClass().getMethod(addMethodName, params).invoke(getDataObject(), paramValues);
                    } catch(NoSuchMethodException e) {
                        throw new RuntimeException("Could not find method " + addMethodName + " in " + getDataObject());
                    }
                    //newValue.addElement(str);
                }
            }

            // remove values present in data but not in model
            size = newValue.size();
            for(int i=size-1; i>=0; i--) {
                String str = (String)newValue.elementAt(i);
                if(!model.contains(str)) {
                    Class[] params = {String.class};
                    Object[] paramValues = {str};
                    getDataObject().getClass().getMethod(removeMethodName, params).invoke(getDataObject(), paramValues);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void updateModelImpl () {
        ListModelImpl model = (ListModelImpl)getGuiModel();
        try {
            if(getDataObject() == null) {
                int size = model.getSize();
                for(int i=size-1; i>=0; i--)
                    model.remove(i);
                return;
            }

            Vector newValue = null;
            try {
                newValue = (Vector)getDataObject().getClass().getMethod(getMethodName, null).invoke(getDataObject(), null);
            } catch(NoSuchMethodException e) {
                WARNING.println("WARNING !! method \"Vector " + getMethodName + "()\" does not exist in " + getDataObject().getProxyType());
                throw e;
            }

            // add values present in data but not in model
            int size = newValue.size();
            for(int i=size-1; i>=0; i--) {
                String str = (String)newValue.elementAt(i);
                if(!model.contains(str))
                    model.addElement(str);
            }


            // remove values present in model but not in data
            size = model.getSize();
            for(int i=size-1; i>=0; i--) {
                String str = (String)model.elementAt(i);
                if(!newValue.contains(str))
                    model.remove(i);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

