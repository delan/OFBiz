// test

/**
 *	AbstractDataSupportClass.java
 * 
 *	This is the base class for all Support classes.  Support classes enable the user to
 *	add functionality to the dxml classes that are not datacentric.  This base class
 *	retains a handle to the underlying (hidden) dxml object so that user defined methods
 *	can directly invoke methods in the underlying dxml object.
 */

package org.ofbiz.designer.pattern;

import java.util.*;
import java.lang.reflect.*;
import org.ofbiz.designer.generic.IDRefHelper;
import org.ofbiz.designer.util.*;

public abstract class AbstractDataSupportClass implements IDataSupportClass {
    XmlWrapper xmlObj = null;
    Object dtdObj = null;

    public void setDtdObject(Object dtdObjIn) {
        dtdObj = dtdObjIn;
    }

    public Object getDtdObject() {
        return dtdObj;
    }

    public void setXml(XmlWrapper xml) {
        xmlObj = xml;
    }

    public XmlWrapper getXml() {
        return xmlObj;
    }

    public Object getIdRef( String id ) {
        return xmlObj.getIdRefRaw( id );
    }

    public void setIdRef( String key, Object value ) {
        xmlObj.setIdRef( key, value );
    }

    public void removeIdRef(String key) {
        xmlObj.removeIdRef(key);
    }

    public void notifyDataModified(Object dataObject) {
        try {
            ((IRegistrar)DataProxy.proxies.get(dataObject)).fire();
        } catch(NullPointerException e) {
        }
    }
    public void notifyElementAdded(Object newObject, Object dataObject) {
        getXml().addIDsToTree(newObject);
        getXml().initializeReverseLookupTable();
        try {
            ((IRegistrar)DataProxy.proxies.get(dataObject)).fire();
        } catch(NullPointerException e) {
        }
    }
    public void notifyElementRemoved(Object newObject, Object dataObject) {
        getXml().removeIDsFromTree(newObject);
        getXml().initializeReverseLookupTable();
        try {
            ((IRegistrar)DataProxy.proxies.get(dataObject)).fire();
        } catch(NullPointerException e) {
        }
    }
}
