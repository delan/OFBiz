package org.ofbiz.designer.pattern;

import org.ofbiz.designer.util.*;
import java.util.*;

public abstract class BaseTranslator implements ITranslator {
    private Vector dataObjVector;
    private  IGuiModel guiModel = null;
    private Hashtable dataParents = new Hashtable();

    public static final String UPDATE_DATA = "UPDATE_DATA";
    public static final String UPDATE_MODEL = "UPDATE_MODEL";

    protected BaseTranslator(IModelProxySupportClass modelIn, IRegistrar dataObjectIn) {
        if(modelIn != null) modelIn.setTranslator(this);
        else throw new RuntimeException("Model is null !!");
        guiModel = (IGuiModel) modelIn.getRawModel();
        if(dataObjectIn != null)
            dataObjectIn.register(this);
        else
            throw new RuntimeException("Data is null !!");
        dataObjVector = new Vector();
        dataObjVector.addElement(dataObjectIn);
    }

    protected BaseTranslator(IModelProxySupportClass modelIn, Vector dataObjVectorIn) {
        if(modelIn != null) modelIn.setTranslator(this);
        else throw new RuntimeException("Model is null !!");
        guiModel = (IGuiModel) modelIn.getRawModel();
        dataObjVector = dataObjVectorIn;
        if(dataObjVector == null) throw new RuntimeException("DataObjVector is null!!");
        for(int i=0; i<dataObjVectorIn.size(); i++) {
            IRegistrar dataObject = getDataObjectAt(i);
            if(dataObject != null) dataObject.register(this);
            else throw new RuntimeException("DataElement[" + i + "] is null !!");
        }
    }

    protected void synchronize(String mode) {
        if(UPDATE_DATA.equals(mode)) updateData();
        else if(UPDATE_MODEL.equals(mode)) updateModel();
        else throw new RuntimeException("Unknown mode " + mode);
    }

    public IRegistrar getDataObject() {
        if(dataObjVector.size()>1) {
            String msg = "\n\nCannot call this method when dataObjVector.size()>1\n";
            msg += "Use getDataObjectAt(int) instead\n";
            throw new RuntimeException(msg);
        }
        return(IRegistrar)dataObjVector.elementAt(0);
    }

    protected IGuiModel getGuiModel() {
        return guiModel;
    }

    public IRegistrar getDataObjectAt(int i) {
        return(IRegistrar)dataObjVector.elementAt(i);
    }

    private boolean updatingData = false;
    private boolean updatingModel = false;

    public void updateData () {
        if(dataObjVector == null) throw new RuntimeException("targetObj Vector is null");
        if(updatingModel) return;

        updatingData = true;
        updateDataImpl();
        updatingData = false;
    }

    public void updateModel () {
        if(guiModel == null) throw new RuntimeException("guiModel is null");
        if(updatingData) return;

        updatingModel = true;
        updateModelImpl();
        updatingModel = false;
    }

    public abstract void updateDataImpl ();
    public abstract void updateModelImpl ();

    public void setDataObject (IRegistrar dtdObj) {
        if(dataObjVector.size() > 1) throw new RuntimeException("Do not use this method with multiple data objects!!");
        setDataObjectAt(dtdObj, 0);
    }

    public void setDataObjectAt (IRegistrar dtdObj, int index) {
        if(dtdObj == null) throw new RuntimeException("null parameter encountered!!");
        if(index > dataObjVector.size() || index < 0)
            throw new RuntimeException("invalid index!!");
        if(dataObjVector.contains(dtdObj))
            throw new RuntimeException("dtdObj already exists!!");

        IDataParent oldParent = null;
        if(index != dataObjVector.size()) {
            IRegistrar dataObject = (IRegistrar)dataObjVector.elementAt(index);
            dataObject.unregister(this);
            dataObjVector.removeElementAt(index);   
            oldParent = getDataParent(dataObject);
            if(oldParent != null) oldParent.removeChildData(dataObject);
        }

        dtdObj.register(this);
        dataObjVector.insertElementAt(dtdObj, index);
        if(oldParent != null) setDataParent(dtdObj, oldParent);
        updateModel();
    }

    public void setDataParent(IRegistrar dtdObj, IDataParent parent) {
        dataParents.put(dtdObj, parent);
    }

    public IDataParent getDataParent(IRegistrar dtdObj) {
        return(IDataParent)dataParents.get(dtdObj);
    }

    public void close() {
        for(int i=0; i<dataObjVector.size(); i++) {
            IRegistrar dataObject = (IRegistrar)dataObjVector.elementAt(i);
            dataObject.unregister(this);
        }
    }

    public void dataGone(Object proxy, String type) {
        guiModel.dataGone();
    }

    // This method is invoked from underlying data
    public void dataChanged(Object proxy, String type) {
        updateModel();
    }
}