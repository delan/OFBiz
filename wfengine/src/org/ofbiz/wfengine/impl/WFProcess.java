/* $Id$
 * @(#)WFProcess.java   Sun Aug 12 13:22:40 GMT+02:00 2001
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
 * @created Sun Aug 12 13:22:40 GMT+02:00 2001
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
import org.ofbiz.wfengine.WFActivity;
import org.ofbiz.wfengine.WFContext;
import org.ofbiz.wfengine.WFPrincipal;
import org.ofbiz.wfengine.WFProcessID;



/**
 * Represents a process instance
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFProcess extends WFExecutionObject  implements Serializable  {

	
	// Attribute instance 'started'
	private long started;
	
	// Attribute instance 'completed'
	private long completed;
	
	// Attribute instance 'lastModified'
	private long lastModified;
	
	// Attribute instance 'initiator'
	private WFPrincipal initiator;
	
	// Attribute instance 'processId'
	private WFProcessID processId;
	
	// Attribute instance 'finalContext'
	private WFContext finalContext;
	
	// Attribute instance 'duration'
	private long duration;
	
	
		
	/**
	 * Empty constructor
	 */
	WFProcess() {
		super();	
	}

	/**
	 * Constructor with all attributes 
	 * @param pStarted Initial value for attribute 'started'
	 * @param pCompleted Initial value for attribute 'completed'
	 * @param pLastModified Initial value for attribute 'lastModified'
	 * @param pInitiator Initial value for attribute 'initiator'
	 * @param pProcessId Initial value for attribute 'processId'
	 * @param pFinalContext Initial value for attribute 'finalContext'
	 * @param pDuration Initial value for attribute 'duration'
	 */
	WFProcess(
		long pStarted,
		long pCompleted,
		long pLastModified,
		WFPrincipal pInitiator,
		WFProcessID pProcessId,
		WFContext pFinalContext,
		long pDuration) {		
				
		started = pStarted;		
		completed = pCompleted;		
		lastModified = pLastModified;		
		initiator = pInitiator;		
		processId = pProcessId;		
		finalContext = pFinalContext;		
		duration = pDuration;
	}
		
	/**
	 * Getter for attribute 'started'
	 * Timestamp of process start
	 * @return Value of attribute started
	 */
	public long getStarted()  {
		return started;
	}
	
	/**
	 * Setter for attribute 'started'
	 * Timestamp of process start
	 * @param pStarted Neuer Wert des Attributes started
	 */
	public void setStarted(long pStarted)  {
		if (started == pStarted) return;		
		if ( !notifyAttributeChangeStarted( pStarted ) ) return;
		started = pStarted;
	}
	
	/**
	 * This method is called, before the attribute 'Started' is set to a new
	 * value.
	 * @param pStarted New Value for attribute 'Started'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeStarted(long pStarted) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'completed'
	 * Timestamp of process completion
	 * @return Value of attribute completed
	 */
	public long getCompleted()  {
		return completed;
	}
	
	/**
	 * Setter for attribute 'completed'
	 * Timestamp of process completion
	 * @param pCompleted Neuer Wert des Attributes completed
	 */
	public void setCompleted(long pCompleted)  {
		if (completed == pCompleted) return;		
		if ( !notifyAttributeChangeCompleted( pCompleted ) ) return;
		completed = pCompleted;
	}
	
	/**
	 * This method is called, before the attribute 'Completed' is set to a new
	 * value.
	 * @param pCompleted New Value for attribute 'Completed'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeCompleted(long pCompleted) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'lastModified'
	 * Timestamp of last change
	 * @return Value of attribute lastModified
	 */
	public long getLastModified()  {
		return lastModified;
	}
	
	/**
	 * Setter for attribute 'lastModified'
	 * Timestamp of last change
	 * @param pLastModified Neuer Wert des Attributes lastModified
	 */
	public void setLastModified(long pLastModified)  {
		if (lastModified == pLastModified) return;		
		if ( !notifyAttributeChangeLastModified( pLastModified ) ) return;
		lastModified = pLastModified;
	}
	
	/**
	 * This method is called, before the attribute 'LastModified' is set to a new
	 * value.
	 * @param pLastModified New Value for attribute 'LastModified'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeLastModified(long pLastModified) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'initiator'
	 * Initiator (resource) which started the process
	 * @return Value of attribute initiator
	 */
	public WFPrincipal getInitiator()  {
		return initiator;
	}
	
	/**
	 * Setter for attribute 'initiator'
	 * Initiator (resource) which started the process
	 * @param pInitiator Neuer Wert des Attributes initiator
	 */
	public void setInitiator(WFPrincipal pInitiator)  {
		if (initiator == pInitiator) return;		
		if ( !notifyAttributeChangeInitiator( pInitiator ) ) return;
		initiator = pInitiator;
	}
	
	/**
	 * This method is called, before the attribute 'Initiator' is set to a new
	 * value.
	 * @param pInitiator New Value for attribute 'Initiator'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeInitiator(WFPrincipal pInitiator) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'processId'
	 * Session id of process
	 * @return Value of attribute processId
	 */
	public WFProcessID getProcessId()  {
		return processId;
	}
	
	/**
	 * Setter for attribute 'processId'
	 * Session id of process
	 * @param pProcessId Neuer Wert des Attributes processId
	 */
	public void setProcessId(WFProcessID pProcessId)  {
		if (processId == pProcessId) return;		
		if ( !notifyAttributeChangeProcessId( pProcessId ) ) return;
		processId = pProcessId;
	}
	
	/**
	 * This method is called, before the attribute 'ProcessId' is set to a new
	 * value.
	 * @param pProcessId New Value for attribute 'ProcessId'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeProcessId(WFProcessID pProcessId) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'finalContext'
	 * Process data after finishing the process
	 * @return Value of attribute finalContext
	 */
	public WFContext getFinalContext()  {
		return finalContext;
	}
	
	/**
	 * Setter for attribute 'finalContext'
	 * Process data after finishing the process
	 * @param pFinalContext Neuer Wert des Attributes finalContext
	 */
	public void setFinalContext(WFContext pFinalContext)  {
		if (finalContext == pFinalContext) return;		
		if ( !notifyAttributeChangeFinalContext( pFinalContext ) ) return;
		finalContext = pFinalContext;
	}
	
	/**
	 * This method is called, before the attribute 'FinalContext' is set to a new
	 * value.
	 * @param pFinalContext New Value for attribute 'FinalContext'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeFinalContext(WFContext pFinalContext) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'duration'
	 * 
	 * @return Value of attribute duration
	 */
	public long getDuration()  {
		return duration;
	}
	
	/**
	 * Setter for attribute 'duration'
	 * 
	 * @param pDuration Neuer Wert des Attributes duration
	 */
	public void setDuration(long pDuration)  {
		if (duration == pDuration) return;		
		if ( !notifyAttributeChangeDuration( pDuration ) ) return;
		duration = pDuration;
	}
	
	/**
	 * This method is called, before the attribute 'Duration' is set to a new
	 * value.
	 * @param pDuration New Value for attribute 'Duration'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeDuration(long pDuration) {		
		return true;
	}
	
	/**
	 * Initializes an instance from a meta object
	 * @param pDescr Value for parameter Descr
	 */
	public void initFromDescription(WFProcessDescription pDescr) {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method findActivityWithId
	 * @param pId Id of activity
	 * @return WFActivityImpl
	 */
	public WFActivityImpl findActivityWithId(String pId) {				
		// !!NO_CODE!!
		return null /* WFActivityImpl */;
	}
	

	// Link attribute of association 'Steps '
			
	private Collection steps;

	/**
	 * Getter of association 'Steps'
	 * @return Currents contents of association 'Steps'
	 */
	public Collection getSteps() {
		return steps != null ? steps : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'Steps'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllSteps
	 * @param pSteps List containing the new elements for association  'Steps'. 
	 */
	public void setSteps (Collection pSteps) {
		removeAllSteps();	
		if (pSteps != null ) {
			addAllToSteps( pSteps );
		}
	}

	/**
	 * Removes all elements from assoziation 'Steps'
	 */
	public void removeAllSteps() {
		if (steps == null) return; // nothing to do
		
		for(Iterator it = steps.iterator(); it.hasNext();) {
			WFActivityImpl lElement = (WFActivityImpl) it.next();
			lElement.unlinkProcess( this );
			removeSteps( lElement );				
		}		
	}

	/**
	 * Removes pSteps from assoziation 'Steps'
	 * @param pSteps element to remove
	 */
	public void removeSteps(WFActivityImpl pSteps) {
		if (steps != null) {
			steps.remove( pSteps );
			pSteps.unlinkProcess( this ); // notify other end
			notifyRemoveSteps( pSteps ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pStepsList to association 'Steps'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pStepsList.size()</code>)
	 */
	public int addAllToSteps (Collection pStepsList) {
		if (pStepsList == null) {
			throw new RuntimeException("Attempted to add null container to WFProcess#Steps!");
		}
		int lInserted=0;
		for(Iterator it = pStepsList.iterator(); it.hasNext(); ) {
			try {
				WFActivityImpl lSteps = (WFActivityImpl)it.next();				
				addSteps( lSteps );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pSteps to association 'Steps'
	 * @param pSteps Element to add
	 */
	public void addSteps (WFActivityImpl pSteps) {
		if (pSteps == null) {
			throw new RuntimeException("Attempted to add null object to WFProcess#Steps!");
		}
		
		if (steps == null) {
			steps = new ArrayList();
		}
		steps.add(pSteps);
		
		pSteps.linkProcess(this); // notify other end
		
		notifyRemoveSteps( pSteps ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'Steps'
	 */
	private void notifyAddSteps(WFActivityImpl pSteps) {
		//System.out.println("Add " + pSteps + " to WFProcess#Steps");
	}		
	
	/**
	 * Hook for 'remove' on association 'Steps'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveSteps(WFActivityImpl pSteps) {
		//System.out.println("Remove " + pSteps + " from WFProcess#Steps");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkSteps(WFActivityImpl pSteps) {		
		if (steps == null) {
			steps = new ArrayList();
		}
		steps.add(pSteps);
		notifyAddSteps( pSteps ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkSteps(WFActivityImpl pSteps) {
		if (steps == null) return;steps.remove(pSteps);
		notifyRemoveSteps( pSteps ); // notify ourselves
	}	

	// Link attribute of association 'Initial '
			
	private WFActivityImpl initial;

	/**
	 * Getter of association 'Initial'
	 * @return Current value of association 'Initial'.
	 * @throws RuntimeException, if value is null
	 */
	public WFActivityImpl getInitial() {
		if (initial == null) {	
			// This should never happen. If so, fix your code :-)			
			throw new RuntimeException("Invalid aggregate: WFProcess#Initial is null!");
		}
		return initial;
	}

	/**
	 * Setter of association 'Initial'.
	 * @param pInitial New value for association 'Initial'
	 */
	public void setInitial (WFActivityImpl pInitial) {
		initial = pInitial;		 
		
	}
		
	/**
	 * Checks, if aggregate 'Initial' contains elements
	 * @return true, if association contains no elements, otherwise false
	 */
	public boolean isInitialNull() {		
		return initial == null;
	}
	
	
	
	/**
	 * Internal use only
	 */
	public void linkInitial(WFActivityImpl pInitial) {		
		
		initial = pInitial;
		
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkInitial(WFActivityImpl pInitial) {
		initial = null;
		
	}	

	// Link attribute of association 'Engine '
			
	private WFEngine engine;

	/**
	 * Getter of association 'Engine'
	 * @return Current value of association 'Engine'.
	 * @throws RuntimeException, if value is null
	 */
	public WFEngine getEngine() {
		if (engine == null) {	
			// This should never happen. If so, fix your code :-)			
			throw new RuntimeException("Invalid aggregate: WFProcess#Engine is null!");
		}
		return engine;
	}

	/**
	 * Setter of association 'Engine'.
	 * @param pEngine New value for association 'Engine'
	 */
	public void setEngine (WFEngine pEngine) {
		if (pEngine == null && engine != null) {
			engine.unlinkActiveProcesses( this );
		}
		engine = pEngine;		 
		engine.linkActiveProcesses(this);
	}
		
	/**
	 * Checks, if aggregate 'Engine' contains elements
	 * @return true, if association contains no elements, otherwise false
	 */
	public boolean isEngineNull() {		
		return engine == null;
	}
	
	
	
	/**
	 * Internal use only
	 */
	public void linkEngine(WFEngine pEngine) {		
		
		if (engine != null) {
			engine.unlinkActiveProcesses(this); // Alte Beziehung löschen
		}
		engine = pEngine;
		
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkEngine(WFEngine pEngine) {
		engine = null;
		
	}	

	// Link attribute of association 'OpenActivities '
			
	private Collection openActivities;

	/**
	 * Getter of association 'OpenActivities'
	 * @return Currents contents of association 'OpenActivities'
	 */
	public Collection getOpenActivities() {
		return openActivities != null ? openActivities : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'OpenActivities'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllOpenActivities
	 * @param pOpenActivities List containing the new elements for association  'OpenActivities'. 
	 */
	public void setOpenActivities (Collection pOpenActivities) {
		removeAllOpenActivities();	
		if (pOpenActivities != null ) {
			addAllToOpenActivities( pOpenActivities );
		}
	}

	/**
	 * Removes all elements from assoziation 'OpenActivities'
	 */
	public void removeAllOpenActivities() {
		if (openActivities == null) return; // nothing to do
		
		for(Iterator it = openActivities.iterator(); it.hasNext();) {
			WFActivity lElement = (WFActivity) it.next();
			
			removeOpenActivities( lElement );				
		}		
	}

	/**
	 * Removes pOpenActivities from assoziation 'OpenActivities'
	 * @param pOpenActivities element to remove
	 */
	public void removeOpenActivities(WFActivity pOpenActivities) {
		if (openActivities != null) {
			openActivities.remove( pOpenActivities ); // notify other end
			notifyRemoveOpenActivities( pOpenActivities ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pOpenActivitiesList to association 'OpenActivities'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pOpenActivitiesList.size()</code>)
	 */
	public int addAllToOpenActivities (Collection pOpenActivitiesList) {
		if (pOpenActivitiesList == null) {
			throw new RuntimeException("Attempted to add null container to WFProcess#OpenActivities!");
		}
		int lInserted=0;
		for(Iterator it = pOpenActivitiesList.iterator(); it.hasNext(); ) {
			try {
				WFActivity lOpenActivities = (WFActivity)it.next();				
				addOpenActivities( lOpenActivities );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pOpenActivities to association 'OpenActivities'
	 * @param pOpenActivities Element to add
	 */
	public void addOpenActivities (WFActivity pOpenActivities) {
		if (pOpenActivities == null) {
			throw new RuntimeException("Attempted to add null object to WFProcess#OpenActivities!");
		}
		
		if (openActivities == null) {
			openActivities = new ArrayList();
		}
		openActivities.add(pOpenActivities);
		
		notifyRemoveOpenActivities( pOpenActivities ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'OpenActivities'
	 */
	private void notifyAddOpenActivities(WFActivity pOpenActivities) {
		//System.out.println("Add " + pOpenActivities + " to WFProcess#OpenActivities");
	}		
	
	/**
	 * Hook for 'remove' on association 'OpenActivities'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveOpenActivities(WFActivity pOpenActivities) {
		//System.out.println("Remove " + pOpenActivities + " from WFProcess#OpenActivities");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkOpenActivities(WFActivity pOpenActivities) {		
		if (openActivities == null) {
			openActivities = new ArrayList();
		}
		openActivities.add(pOpenActivities);
		notifyAddOpenActivities( pOpenActivities ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkOpenActivities(WFActivity pOpenActivities) {
		if (openActivities == null) return;openActivities.remove(pOpenActivities);
		notifyRemoveOpenActivities( pOpenActivities ); // notify ourselves
	}	

	// Link attribute of association 'CompletedActivities '
			
	private Collection completedActivities;

	/**
	 * Getter of association 'CompletedActivities'
	 * @return Currents contents of association 'CompletedActivities'
	 */
	public Collection getCompletedActivities() {
		return completedActivities != null ? completedActivities : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'CompletedActivities'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllCompletedActivities
	 * @param pCompletedActivities List containing the new elements for association  'CompletedActivities'. 
	 */
	public void setCompletedActivities (Collection pCompletedActivities) {
		removeAllCompletedActivities();	
		if (pCompletedActivities != null ) {
			addAllToCompletedActivities( pCompletedActivities );
		}
	}

	/**
	 * Removes all elements from assoziation 'CompletedActivities'
	 */
	public void removeAllCompletedActivities() {
		if (completedActivities == null) return; // nothing to do
		
		for(Iterator it = completedActivities.iterator(); it.hasNext();) {
			WFActivity lElement = (WFActivity) it.next();
			
			removeCompletedActivities( lElement );				
		}		
	}

	/**
	 * Removes pCompletedActivities from assoziation 'CompletedActivities'
	 * @param pCompletedActivities element to remove
	 */
	public void removeCompletedActivities(WFActivity pCompletedActivities) {
		if (completedActivities != null) {
			completedActivities.remove( pCompletedActivities ); // notify other end
			notifyRemoveCompletedActivities( pCompletedActivities ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pCompletedActivitiesList to association 'CompletedActivities'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pCompletedActivitiesList.size()</code>)
	 */
	public int addAllToCompletedActivities (Collection pCompletedActivitiesList) {
		if (pCompletedActivitiesList == null) {
			throw new RuntimeException("Attempted to add null container to WFProcess#CompletedActivities!");
		}
		int lInserted=0;
		for(Iterator it = pCompletedActivitiesList.iterator(); it.hasNext(); ) {
			try {
				WFActivity lCompletedActivities = (WFActivity)it.next();				
				addCompletedActivities( lCompletedActivities );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pCompletedActivities to association 'CompletedActivities'
	 * @param pCompletedActivities Element to add
	 */
	public void addCompletedActivities (WFActivity pCompletedActivities) {
		if (pCompletedActivities == null) {
			throw new RuntimeException("Attempted to add null object to WFProcess#CompletedActivities!");
		}
		
		if (completedActivities == null) {
			completedActivities = new ArrayList();
		}
		completedActivities.add(pCompletedActivities);
		
		notifyRemoveCompletedActivities( pCompletedActivities ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'CompletedActivities'
	 */
	private void notifyAddCompletedActivities(WFActivity pCompletedActivities) {
		//System.out.println("Add " + pCompletedActivities + " to WFProcess#CompletedActivities");
	}		
	
	/**
	 * Hook for 'remove' on association 'CompletedActivities'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveCompletedActivities(WFActivity pCompletedActivities) {
		//System.out.println("Remove " + pCompletedActivities + " from WFProcess#CompletedActivities");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkCompletedActivities(WFActivity pCompletedActivities) {		
		if (completedActivities == null) {
			completedActivities = new ArrayList();
		}
		completedActivities.add(pCompletedActivities);
		notifyAddCompletedActivities( pCompletedActivities ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkCompletedActivities(WFActivity pCompletedActivities) {
		if (completedActivities == null) return;completedActivities.remove(pCompletedActivities);
		notifyRemoveCompletedActivities( pCompletedActivities ); // notify ourselves
	}	

	// Link attribute of association 'SuspendedActivities '
			
	private Collection suspendedActivities;

	/**
	 * Getter of association 'SuspendedActivities'
	 * @return Currents contents of association 'SuspendedActivities'
	 */
	public Collection getSuspendedActivities() {
		return suspendedActivities != null ? suspendedActivities : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'SuspendedActivities'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllSuspendedActivities
	 * @param pSuspendedActivities List containing the new elements for association  'SuspendedActivities'. 
	 */
	public void setSuspendedActivities (Collection pSuspendedActivities) {
		removeAllSuspendedActivities();	
		if (pSuspendedActivities != null ) {
			addAllToSuspendedActivities( pSuspendedActivities );
		}
	}

	/**
	 * Removes all elements from assoziation 'SuspendedActivities'
	 */
	public void removeAllSuspendedActivities() {
		if (suspendedActivities == null) return; // nothing to do
		
		for(Iterator it = suspendedActivities.iterator(); it.hasNext();) {
			WFActivity lElement = (WFActivity) it.next();
			
			removeSuspendedActivities( lElement );				
		}		
	}

	/**
	 * Removes pSuspendedActivities from assoziation 'SuspendedActivities'
	 * @param pSuspendedActivities element to remove
	 */
	public void removeSuspendedActivities(WFActivity pSuspendedActivities) {
		if (suspendedActivities != null) {
			suspendedActivities.remove( pSuspendedActivities ); // notify other end
			notifyRemoveSuspendedActivities( pSuspendedActivities ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pSuspendedActivitiesList to association 'SuspendedActivities'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pSuspendedActivitiesList.size()</code>)
	 */
	public int addAllToSuspendedActivities (Collection pSuspendedActivitiesList) {
		if (pSuspendedActivitiesList == null) {
			throw new RuntimeException("Attempted to add null container to WFProcess#SuspendedActivities!");
		}
		int lInserted=0;
		for(Iterator it = pSuspendedActivitiesList.iterator(); it.hasNext(); ) {
			try {
				WFActivity lSuspendedActivities = (WFActivity)it.next();				
				addSuspendedActivities( lSuspendedActivities );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pSuspendedActivities to association 'SuspendedActivities'
	 * @param pSuspendedActivities Element to add
	 */
	public void addSuspendedActivities (WFActivity pSuspendedActivities) {
		if (pSuspendedActivities == null) {
			throw new RuntimeException("Attempted to add null object to WFProcess#SuspendedActivities!");
		}
		
		if (suspendedActivities == null) {
			suspendedActivities = new ArrayList();
		}
		suspendedActivities.add(pSuspendedActivities);
		
		notifyRemoveSuspendedActivities( pSuspendedActivities ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'SuspendedActivities'
	 */
	private void notifyAddSuspendedActivities(WFActivity pSuspendedActivities) {
		//System.out.println("Add " + pSuspendedActivities + " to WFProcess#SuspendedActivities");
	}		
	
	/**
	 * Hook for 'remove' on association 'SuspendedActivities'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveSuspendedActivities(WFActivity pSuspendedActivities) {
		//System.out.println("Remove " + pSuspendedActivities + " from WFProcess#SuspendedActivities");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkSuspendedActivities(WFActivity pSuspendedActivities) {		
		if (suspendedActivities == null) {
			suspendedActivities = new ArrayList();
		}
		suspendedActivities.add(pSuspendedActivities);
		notifyAddSuspendedActivities( pSuspendedActivities ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkSuspendedActivities(WFActivity pSuspendedActivities) {
		if (suspendedActivities == null) return;suspendedActivities.remove(pSuspendedActivities);
		notifyRemoveSuspendedActivities( pSuspendedActivities ); // notify ourselves
	}	

	/**
	 * String representation of WFProcess
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFProcess");	
		return lRet.toString();
	}
}








