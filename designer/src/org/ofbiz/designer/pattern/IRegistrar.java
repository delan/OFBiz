
/**
 *	IRegistrar.java
 * 
 *	This is one of the 3 primary facets of the DataProxy generated wrapper classes.  This
 *	class provides all the core notification functionality.
 */


package org.ofbiz.designer.pattern;

public interface IRegistrar {
    public void setProxy(Object proxy, String type);
    public String getProxyType();
    public void register(IRegisterable dataProxyUser);
    public void unregister(IRegisterable dataProxyUser);
    public void fire();
    public void fireDataGone();
};