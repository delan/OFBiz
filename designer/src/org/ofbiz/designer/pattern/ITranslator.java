
/**
 *	ITranslator.java
 * 
 *	This is the interface between GUI-model and data.  This interface is meant to be used in
 *	conjunction with the GuiModelProxy and DataProxy framework.  The translator gets notified by the
 *	dataproxy object whenever certain specifiable methods get called within the underlying
 *	data object.  This notification is a cue for the translator to update its GUI-model.
 *	Conversely, the translator also gets invoked anytime any change occurs in the GUI-model
 *  as a result of user input.  This is its cue to update the underlying data.
 * 
 */


package org.ofbiz.designer.pattern;

import java.awt.Window;

public interface ITranslator extends IRegisterable {
    //public Object getModel() ;
    public void updateData ();
    public void updateModel () ;
    public void setDataObject (IRegistrar dataObject) ;
    public void setDataObjectAt (IRegistrar dtdObj, int index) ;
    public IRegistrar getDataObject();
    public IRegistrar getDataObjectAt(int i);
    public void setDataParent(IRegistrar dtdObj, IDataParent parent);
    public IDataParent getDataParent(IRegistrar dtdObj);
    public void close();
}