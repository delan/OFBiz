package org.ofbiz.designer.generic;

import org.ofbiz.designer.pattern.*;
import javax.swing.*;
import java.util.*;
import javax.swing.text.*;
import java.lang.reflect.*;
import java.awt.Window;
import java.awt.event.*;
import org.ofbiz.designer.util.*;

public class DocumentTranslator extends  BaseTranslator {
    String targetMethodSuffix;

    public DocumentTranslator(IDocumentWrapper modelIn, IRegistrar dataObjectIn, String methodSuffix, String mode) {
        super(modelIn, dataObjectIn);
        if(methodSuffix == null)
            throw new RuntimeException("NULL PARAMETER ENCOUNTERED!!");
        targetMethodSuffix = methodSuffix;
        synchronize(mode);
    }

    public void updateDataImpl () {
        if(targetMethodSuffix == null) {
            WARNING.println("targetMethodSuffix is NULL");
            return;
        }
        PlainDocumentModel guiModel = (PlainDocumentModel) getGuiModel();

        try {
            Class[] params = {String.class};
            Method setMethod = getDataObject().getClass().getMethod("set" + targetMethodSuffix, params);
            String result = guiModel.getText(0, guiModel.getLength());
            Object[] paramValues = {result};

            setMethod.invoke(getDataObject(), paramValues);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void updateModelImpl () {
        PlainDocumentModel guiModel = (PlainDocumentModel) getGuiModel();

        try {
            if(getDataObject() == null) {
                guiModel.remove(0, guiModel.getLength());
                guiModel.insertString(0, "", null);
                return;
            }

            String newValue = null;
            try {
                newValue = (String)getDataObject().getClass().getMethod("get" + targetMethodSuffix, null).invoke(getDataObject(), null);
            } catch(NoSuchMethodException e) {
                WARNING.println("method name is get" + targetMethodSuffix);
                throw e;
            }
            if(newValue == null) newValue = "";
            String oldValue = guiModel.getText(0, guiModel.getLength());
            if(newValue.equals(oldValue))
                return;
            guiModel.remove(0, guiModel.getLength());
            guiModel.insertString(0, newValue, null);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

