
/**
 *	IDataSupportClass.java
 * 
 *	This is root interface for all Support classes.  
 * 
 */
package org.ofbiz.designer.pattern;

public  interface IDataSupportClass {
    public void setDtdObject(Object dtdObjIn);
    public Object getDtdObject();
    public void setXml(XmlWrapper xml);
    public XmlWrapper getXml();
    public Object getIdRef( String id );
    public void setIdRef( String key, Object value );
    public void removeIdRef(String key);
    public void notifyElementAdded(Object newObject, Object dataObject);
    public void notifyElementRemoved(Object newObject, Object dataObject);
    //public void notifyDataChanged();
    //public void resolveIDRefs() ;
}
