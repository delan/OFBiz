/* $Id$
 * @(#)WFActivityImpl.java   Fri Aug 17 12:18:02 GMT+02:00 2001
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
 * @created Fri Aug 17 12:18:02 GMT+02:00 2001
 * @version 1.0
 */

/*
 * $Log$
 * Revision 1.1  2001/08/10 10:43:15  owieland
 * Add missing files from import
 *
 */
package org.ofbiz.core.workflow;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.ofbiz.service.workflow.WFActivity;
import org.ofbiz.service.workflow.WFContext;
import org.ofbiz.service.workflow.WFDurationUnit;
import org.ofbiz.service.workflow.WFEvent;
import org.ofbiz.service.workflow.WFException;
import org.ofbiz.service.workflow.WFListener;
import org.ofbiz.service.workflow.WFState;



/**
 * Repräsentiert eine Aktivität zur Laufzeit
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFActivityImpl extends WFExecutionObject  implements WFActivity, WFListener {

	
	
		
	/**
	 * Empty constructor
	 */
	WFActivityImpl() {
		super();	
	}
	/**
	 * Method createID
	 */
	protected void createID() {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Returns the next activities
	 * @return Collection
	 */
	protected Collection next() {				
		// !!NO_CODE!!
		return null /* Collection */;
	}
	
	/**
	 * Method initFromDescription
	 * @param pDescr Value for parameter Descr
	 */
	public void initFromDescription(WFActivityDescription pDescr) {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method assignTo
	 * @param pResource Value for parameter Resource
	 */
	public void assignTo(WFResource pResource) {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method removeFromAllWorklists
	 */
	public void removeFromAllWorklists() {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method addToWorklist
	 * @param pResource Value for parameter Resource
	 */
	public void addToWorklist(WFResource pResource) {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method previous
	 * @return Collection
	 */
	public Collection previous() {				
		// !!NO_CODE!!
		return null /* Collection */;
	}
	
	/**
	 * Liefert die eindeutige Identifikationsnummer der Aktivität
	 * @return String
	 */
	public String getId() {				
		// !!NO_CODE!!
		return "";
	}
	
	/**
	 * Liefert den namen der Aktivität
	 * @return String
	 */
	public String getName() {				
		// !!NO_CODE!!
		return "";
	}
	
	/**
	 * Liefert den Auslöser (Ressource,)  der den Prozess gestartet hat
	 * @return String
	 */
	public String getInitiator() {				
		// !!NO_CODE!!
		return "";
	}
	
	/**
	 * Liefertt den Kontext der aktuellen Aktivität
	 * @return WFContext
	 */
	public WFContext getContext() {				
		// !!NO_CODE!!
		return null /* WFContext */;
	}
	
	/**
	 * Liefert den Status der Aktivität
	 * @return WFState
	 */
	public WFState getState() {				
		// !!NO_CODE!!
		return null /* WFState */;
	}
	
	/**
	 * Liefert den Zeitstempel der letzten Änderung
	 * @return long
	 */
	public long getLastModified() {				
		// !!NO_CODE!!
		return 0L;
	}
	
	/**
	 * Liefert den Erzeugungszeitpunkt
	 * @return long
	 */
	public long getCreationTime() {				
		// !!NO_CODE!!
		return 0L;
	}
	
	/**
	 * Method getCompletionTime
	 * @return long
	 */
	public long getCompletionTime() {				
		// !!NO_CODE!!
		return 0L;
	}
	
	/**
	 * Method getStartTime
	 * @return long
	 */
	public long getStartTime() {				
		// !!NO_CODE!!
		return 0L;
	}
	
	/**
	 * Method getDuration
	 * @return long
	 */
	public long getDuration() {				
		// !!NO_CODE!!
		return 0L;
	}
	
	/**
	 * Method getDurationUnit
	 * @return WFDurationUnit
	 */
	public WFDurationUnit getDurationUnit() {				
		// !!NO_CODE!!
		return null /* WFDurationUnit */;
	}
	
	/**
	 * Method handleEvent
	 * @param pEvent Value for parameter Event
	 */
	public void handleEvent(WFEvent pEvent) {				
		// !!NO_CODE!!
		/* NULL */;
	}
	

	// Link attribute of association 'Process '
			
	private WFProcess process;

	/**
	 * Getter of association 'Process'
	 * @return Current value of association 'Process'.
	 * @throws RuntimeException, if value is null
	 */
	public WFProcess getProcess() {
		if (process == null) {	
			// This should never happen. If so, fix your code :-)			
			throw new RuntimeException("Invalid aggregate: WFActivityImpl#Process is null!");
		}
		return process;
	}

	/**
	 * Setter of association 'Process'.
	 * @param pProcess New value for association 'Process'
	 */
	public void setProcess (WFProcess pProcess) {
		if (pProcess == null && process != null) {
			process.unlinkSteps( this );
		}
		process = pProcess;		 
		process.linkSteps(this);
	}
		
	/**
	 * Checks, if aggregate 'Process' contains elements
	 * @return true, if association contains no elements, otherwise false
	 */
	public boolean isProcessNull() {		
		return process == null;
	}
	
	
	
	/**
	 * Internal use only
	 */
	public void linkProcess(WFProcess pProcess) {		
		
		if (process != null) {
			process.unlinkSteps(this); // Alte Beziehung löschen
		}
		process = pProcess;
		
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkProcess(WFProcess pProcess) {
		process = null;
		
	}	

	// Link attribute of association 'Worklists '
			
	private Collection worklists;

	/**
	 * Getter of association 'Worklists'
	 * @return Currents contents of association 'Worklists'
	 */
	public Collection getWorklists() {
		return worklists != null ? worklists : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'Worklists'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllWorklists
	 * @param pWorklists List containing the new elements for association  'Worklists'. 
	 */
	public void setWorklists (Collection pWorklists) {
		removeAllWorklists();	
		if (pWorklists != null ) {
			addAllToWorklists( pWorklists );
		}
	}

	/**
	 * Removes all elements from association 'Worklists'
	 */
	public void removeAllWorklists() {
		if (worklists == null) return; // nothing to do
		
		for(Iterator it = worklists.iterator(); it.hasNext();) {
			Worklist lElement = (Worklist) it.next();
			lElement.unlinkContents( this );
			removeWorklists( lElement );				
		}		
	}

	/**
	 * Removes pWorklists from association 'Worklists'
	 * @param pWorklists element to remove
	 */
	public void removeWorklists(Worklist pWorklists) {
		if (worklists != null) {
			worklists.remove( pWorklists );
			pWorklists.unlinkContents( this ); // notify other end
			notifyRemoveWorklists( pWorklists ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pWorklistsList to association 'Worklists'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pWorklistsList.size()</code>)
	 */
	public int addAllToWorklists (Collection pWorklistsList) {
		if (pWorklistsList == null) {
			throw new RuntimeException("Attempted to add null container to WFActivityImpl#Worklists!");
		}
		int lInserted=0;
		for(Iterator it = pWorklistsList.iterator(); it.hasNext(); ) {
			try {
				Worklist lWorklists = (Worklist)it.next();				
				addWorklists( lWorklists );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pWorklists to association 'Worklists'
	 * @param pWorklists Element to add
	 */
	public void addWorklists (Worklist pWorklists) {
		if (pWorklists == null) {
			throw new RuntimeException("Attempted to add null object to WFActivityImpl#Worklists!");
		}
		
		if (worklists == null) {
			worklists = new ArrayList();
		}
		worklists.add(pWorklists);
		
		pWorklists.linkContents(this); // notify other end
		
		notifyRemoveWorklists( pWorklists ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'Worklists'
	 */
	private void notifyAddWorklists(Worklist pWorklists) {
		//System.out.println("Add " + pWorklists + " to WFActivityImpl#Worklists");
	}		
	
	/**
	 * Hook for 'remove' on association 'Worklists'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveWorklists(Worklist pWorklists) {
		//System.out.println("Remove " + pWorklists + " from WFActivityImpl#Worklists");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkWorklists(Worklist pWorklists) {		
		if (worklists == null) {
			worklists = new ArrayList();
		}
		worklists.add(pWorklists);
		notifyAddWorklists( pWorklists ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkWorklists(Worklist pWorklists) {
		if (worklists == null) return;worklists.remove(pWorklists);
		notifyRemoveWorklists( pWorklists ); // notify ourselves
	}	

	// Link attribute of association 'Next '
			
	private Collection next;

	/**
	 * Getter of association 'Next'
	 * @return Currents contents of association 'Next'
	 */
	public Collection getNext() {
		return next != null ? next : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'Next'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllNext
	 * @param pNext List containing the new elements for association  'Next'. 
	 */
	public void setNext (Collection pNext) {
		removeAllNext();	
		if (pNext != null ) {
			addAllToNext( pNext );
		}
	}

	/**
	 * Removes all elements from association 'Next'
	 */
	public void removeAllNext() {
		if (next == null) return; // nothing to do
		
		for(Iterator it = next.iterator(); it.hasNext();) {
			WFTransition lElement = (WFTransition) it.next();
			lElement.unlinkFrom( this );
			removeNext( lElement );				
		}		
	}

	/**
	 * Removes pNext from association 'Next'
	 * @param pNext element to remove
	 */
	public void removeNext(WFTransition pNext) {
		if (next != null) {
			next.remove( pNext );
			pNext.unlinkFrom( this ); // notify other end
			notifyRemoveNext( pNext ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pNextList to association 'Next'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pNextList.size()</code>)
	 */
	public int addAllToNext (Collection pNextList) {
		if (pNextList == null) {
			throw new RuntimeException("Attempted to add null container to WFActivityImpl#Next!");
		}
		int lInserted=0;
		for(Iterator it = pNextList.iterator(); it.hasNext(); ) {
			try {
				WFTransition lNext = (WFTransition)it.next();				
				addNext( lNext );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pNext to association 'Next'
	 * @param pNext Element to add
	 */
	public void addNext (WFTransition pNext) {
		if (pNext == null) {
			throw new RuntimeException("Attempted to add null object to WFActivityImpl#Next!");
		}
		
		if (next == null) {
			next = new ArrayList();
		}
		next.add(pNext);
		
		pNext.linkFrom(this); // notify other end
		
		notifyRemoveNext( pNext ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'Next'
	 */
	private void notifyAddNext(WFTransition pNext) {
		//System.out.println("Add " + pNext + " to WFActivityImpl#Next");
	}		
	
	/**
	 * Hook for 'remove' on association 'Next'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveNext(WFTransition pNext) {
		//System.out.println("Remove " + pNext + " from WFActivityImpl#Next");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkNext(WFTransition pNext) {		
		if (next == null) {
			next = new ArrayList();
		}
		next.add(pNext);
		notifyAddNext( pNext ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkNext(WFTransition pNext) {
		if (next == null) return;next.remove(pNext);
		notifyRemoveNext( pNext ); // notify ourselves
	}	

	// Link attribute of association 'Previous '
			
	private Collection previous;

	/**
	 * Getter of association 'Previous'
	 * @return Currents contents of association 'Previous'
	 */
	public Collection getPrevious() {
		return previous != null ? previous : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'Previous'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllPrevious
	 * @param pPrevious List containing the new elements for association  'Previous'. 
	 */
	public void setPrevious (Collection pPrevious) {
		removeAllPrevious();	
		if (pPrevious != null ) {
			addAllToPrevious( pPrevious );
		}
	}

	/**
	 * Removes all elements from association 'Previous'
	 */
	public void removeAllPrevious() {
		if (previous == null) return; // nothing to do
		
		for(Iterator it = previous.iterator(); it.hasNext();) {
			WFTransition lElement = (WFTransition) it.next();
			lElement.unlinkTo( this );
			removePrevious( lElement );				
		}		
	}

	/**
	 * Removes pPrevious from association 'Previous'
	 * @param pPrevious element to remove
	 */
	public void removePrevious(WFTransition pPrevious) {
		if (previous != null) {
			previous.remove( pPrevious );
			pPrevious.unlinkTo( this ); // notify other end
			notifyRemovePrevious( pPrevious ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pPreviousList to association 'Previous'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pPreviousList.size()</code>)
	 */
	public int addAllToPrevious (Collection pPreviousList) {
		if (pPreviousList == null) {
			throw new RuntimeException("Attempted to add null container to WFActivityImpl#Previous!");
		}
		int lInserted=0;
		for(Iterator it = pPreviousList.iterator(); it.hasNext(); ) {
			try {
				WFTransition lPrevious = (WFTransition)it.next();				
				addPrevious( lPrevious );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pPrevious to association 'Previous'
	 * @param pPrevious Element to add
	 */
	public void addPrevious (WFTransition pPrevious) {
		if (pPrevious == null) {
			throw new RuntimeException("Attempted to add null object to WFActivityImpl#Previous!");
		}
		
		if (previous == null) {
			previous = new ArrayList();
		}
		previous.add(pPrevious);
		
		pPrevious.linkTo(this); // notify other end
		
		notifyRemovePrevious( pPrevious ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'Previous'
	 */
	private void notifyAddPrevious(WFTransition pPrevious) {
		//System.out.println("Add " + pPrevious + " to WFActivityImpl#Previous");
	}		
	
	/**
	 * Hook for 'remove' on association 'Previous'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemovePrevious(WFTransition pPrevious) {
		//System.out.println("Remove " + pPrevious + " from WFActivityImpl#Previous");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkPrevious(WFTransition pPrevious) {		
		if (previous == null) {
			previous = new ArrayList();
		}
		previous.add(pPrevious);
		notifyAddPrevious( pPrevious ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkPrevious(WFTransition pPrevious) {
		if (previous == null) return;previous.remove(pPrevious);
		notifyRemovePrevious( pPrevious ); // notify ourselves
	}	

	/**
	 * String representation of WFActivityImpl
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFActivityImpl");	
		return lRet.toString();
	}
}








