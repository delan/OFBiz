/* $Id$
 * @(#)Scheduler.java   Sun Aug 12 13:22:41 GMT+02:00 2001
 *
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Oliver Wieland (wieland.oliver@t-online.de)
 * @created Sun Aug 12 13:22:41 GMT+02:00 2001
 * @version 1.0
 */

/*
 * $Log$
 */
package org.ofbiz.wfengine.impl;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.ofbiz.wfengine.WFException;



/**
 * Class Scheduler - no documentation available
 * @author Oliver Wieland
 * @version 1.0
 */

public class Scheduler implements Serializable  {

	
	// Attribute instance 'refreshInterval'
	private long refreshInterval;
	
	
		
	/**
	 * Empty constructor
	 */
	Scheduler() {
			}

	/**
	 * Constructor with all attributes 
	 * @param pRefreshInterval Initial value for attribute 'refreshInterval'
	 */
	Scheduler(
		long pRefreshInterval) {		
				
		refreshInterval = pRefreshInterval;
	}
		
	/**
	 * Getter for attribute 'refreshInterval'
	 * 
	 * @return Value of attribute refreshInterval
	 */
	public long getRefreshInterval()  {
		return refreshInterval;
	}
	
	/**
	 * Setter for attribute 'refreshInterval'
	 * 
	 * @param pRefreshInterval Neuer Wert des Attributes refreshInterval
	 */
	public void setRefreshInterval(long pRefreshInterval)  {
		if (refreshInterval == pRefreshInterval) return;		
		if ( !notifyAttributeChangeRefreshInterval( pRefreshInterval ) ) return;
		refreshInterval = pRefreshInterval;
	}
	
	/**
	 * This method is called, before the attribute 'RefreshInterval' is set to a new
	 * value.
	 * @param pRefreshInterval New Value for attribute 'RefreshInterval'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeRefreshInterval(long pRefreshInterval) {		
		return true;
	}
	

	// Link attribute of association 'Queue '
			
	private Collection queue;

	/**
	 * Getter of association 'Queue'
	 * @return Currents contents of association 'Queue'
	 */
	public Collection getQueue() {
		return queue != null ? queue : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'Queue'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllQueue
	 * @param pQueue List containing the new elements for association  'Queue'. 
	 */
	public void setQueue (Collection pQueue) {
		removeAllQueue();	
		if (pQueue != null ) {
			addAllToQueue( pQueue );
		}
	}

	/**
	 * Removes all elements from assoziation 'Queue'
	 */
	public void removeAllQueue() {
		if (queue == null) return; // nothing to do
		
		for(Iterator it = queue.iterator(); it.hasNext();) {
			WFExecutionObject lElement = (WFExecutionObject) it.next();
			
			removeQueue( lElement );				
		}		
	}

	/**
	 * Removes pQueue from assoziation 'Queue'
	 * @param pQueue element to remove
	 */
	public void removeQueue(WFExecutionObject pQueue) {
		if (queue != null) {
			queue.remove( pQueue ); // notify other end
			notifyRemoveQueue( pQueue ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pQueueList to association 'Queue'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pQueueList.size()</code>)
	 */
	public int addAllToQueue (Collection pQueueList) {
		if (pQueueList == null) {
			throw new RuntimeException("Attempted to add null container to Scheduler#Queue!");
		}
		int lInserted=0;
		for(Iterator it = pQueueList.iterator(); it.hasNext(); ) {
			try {
				WFExecutionObject lQueue = (WFExecutionObject)it.next();				
				addQueue( lQueue );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pQueue to association 'Queue'
	 * @param pQueue Element to add
	 */
	public void addQueue (WFExecutionObject pQueue) {
		if (pQueue == null) {
			throw new RuntimeException("Attempted to add null object to Scheduler#Queue!");
		}
		
		if (queue == null) {
			queue = new ArrayList();
		}
		queue.add(pQueue);
		
		notifyRemoveQueue( pQueue ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'Queue'
	 */
	private void notifyAddQueue(WFExecutionObject pQueue) {
		//System.out.println("Add " + pQueue + " to Scheduler#Queue");
	}		
	
	/**
	 * Hook for 'remove' on association 'Queue'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveQueue(WFExecutionObject pQueue) {
		//System.out.println("Remove " + pQueue + " from Scheduler#Queue");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkQueue(WFExecutionObject pQueue) {		
		if (queue == null) {
			queue = new ArrayList();
		}
		queue.add(pQueue);
		notifyAddQueue( pQueue ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkQueue(WFExecutionObject pQueue) {
		if (queue == null) return;queue.remove(pQueue);
		notifyRemoveQueue( pQueue ); // notify ourselves
	}	

	/**
	 * String representation of Scheduler
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("Scheduler");	
		return lRet.toString();
	}
}








