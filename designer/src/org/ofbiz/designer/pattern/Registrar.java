
/**
 *	Registrar.java
 * 
 *	This object provides the core functionality of all DataProxy generated wrapper 
 *	classes.  This enables the "Registerable"s to be notified anytime any potentially
 *	data-changing method gets called on the wrapper.
 * 
 */
package org.ofbiz.designer.pattern;

import java.util.*;
import org.ofbiz.designer.util.*;

public class Registrar implements IRegistrar {
    HashSet invokerSet = new HashSet();
    Object proxy = null;
    public String proxyType = null;

    public void setProxy(Object proxy, String type) {
        this.proxy = proxy;
        proxyType = type;
    }
    public void register(IRegisterable dataProxyUser) {
        invokerSet.add(dataProxyUser);
    }
    public void unregister(IRegisterable dataProxyUser) {
        invokerSet.remove(dataProxyUser);
    }

    public String getProxyType() {
        return proxyType;
    }

    public void fire() {
        Iterator it = invokerSet.iterator();
        while(it.hasNext()) {
            ((IRegisterable)it.next()).dataChanged(proxy, proxyType);
        }
    }
    
    public void fireDataGone() {
        Iterator it = invokerSet.iterator();
        while(it.hasNext()) {
            ((IRegisterable)it.next()).dataGone(proxy, proxyType);
        }
    }
};

