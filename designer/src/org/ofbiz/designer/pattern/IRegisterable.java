
/**
 *	IRegisterable.java
 * 
 *	This is the interface that is used by any class which needs notification from the
 *	DataProxy objects.
 */


package org.ofbiz.designer.pattern;

public interface IRegisterable {
    public void dataChanged(Object proxy, String type);
    public void dataGone(Object proxy, String type);
    //public void close();// bookkeeping before close
};