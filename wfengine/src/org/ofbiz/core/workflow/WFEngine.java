/* $Id$
 * @(#)WFEngine.java   Fri Aug 17 12:18:02 GMT+02:00 2001
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
 */
package org.ofbiz.core.workflow;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import org.ofbiz.service.workflow.WFActivity;
import org.ofbiz.service.workflow.WFActivityFilter;
import org.ofbiz.service.workflow.WFContext;
import org.ofbiz.service.workflow.WFEvent;
import org.ofbiz.service.workflow.WFException;
import org.ofbiz.service.workflow.WFListener;
import org.ofbiz.service.workflow.WFPrincipal;
import org.ofbiz.service.workflow.WFProcessID;
import org.ofbiz.service.workflow.Workflow;



/**
 * Class WFEngine - no documentation available
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFEngine implements Workflow {

	
	// Attribute instance 'properties'
	private Properties properties;
	
	// Attribute instance 'processDescriptions'
	private HashMap processDescriptions;
	
	
		
	/**
	 * Empty constructor
	 */
	WFEngine() {
			}

	/**
	 * Constructor with all attributes 
	 * @param pProperties Initial value for attribute 'properties'
	 * @param pProcessDescriptions Initial value for attribute 'processDescriptions'
	 */
	WFEngine(
		Properties pProperties,
		HashMap pProcessDescriptions) {		
				
		properties = pProperties;		
		processDescriptions = pProcessDescriptions;
	}
		
	/**
	 * Getter for attribute 'properties'
	 * 
	 * @return Value of attribute properties
	 */
	public Properties getProperties()  {
		return properties;
	}
	
	/**
	 * Setter for attribute 'properties'
	 * 
	 * @param pProperties Neuer Wert des Attributes properties
	 */
	public void setProperties(Properties pProperties)  {
		if (properties == pProperties) return;		
		if ( !notifyAttributeChangeProperties( pProperties ) ) return;
		properties = pProperties;
	}
	
	/**
	 * This method is called, before the attribute 'Properties' is set to a new
	 * value.
	 * @param pProperties New Value for attribute 'Properties'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeProperties(Properties pProperties) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'processDescriptions'
	 * 
	 * @return Value of attribute processDescriptions
	 */
	public HashMap getProcessDescriptions()  {
		return processDescriptions;
	}
	
	/**
	 * Setter for attribute 'processDescriptions'
	 * 
	 * @param pProcessDescriptions Neuer Wert des Attributes processDescriptions
	 */
	public void setProcessDescriptions(HashMap pProcessDescriptions)  {
		if (processDescriptions == pProcessDescriptions) return;		
		if ( !notifyAttributeChangeProcessDescriptions( pProcessDescriptions ) ) return;
		processDescriptions = pProcessDescriptions;
	}
	
	/**
	 * This method is called, before the attribute 'ProcessDescriptions' is set to a new
	 * value.
	 * @param pProcessDescriptions New Value for attribute 'ProcessDescriptions'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeProcessDescriptions(HashMap pProcessDescriptions) {		
		return true;
	}
	
	/**
	 * Method createProcess
	 * @param pDescr Value for parameter Descr
	 * @return WFProcess
	 */
	public WFProcess createProcess(WFProcessDescription pDescr) {				
		// !!NO_CODE!!
		return null /* WFProcess */;
	}
	
	/**
	 * Method createID
	 * @return WFProcessID
	 */
	public WFProcessID createID() {				
		// !!NO_CODE!!
		return null /* WFProcessID */;
	}
	
	/**
	 * Method emitEvent
	 * @param pEvent Value for parameter Event
	 */
	public void emitEvent(WFEvent pEvent) {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method refreshWorklists
	 */
	public void refreshWorklists() {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method save
	 */
	public void save() {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method load
	 */
	public void load() {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method getProperty
	 * @param pName Value for parameter Name
	 * @return String
	 */
	public String getProperty(String pName) {				
		// !!NO_CODE!!
		return "";
	}
	
	/**
	 * Method importWFScheme
	 * @param pFilename Value for parameter Filename
	 */
	public void importWFScheme(String pFilename) {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method exportWFScheme
	 * @param pFile Value for parameter File
	 */
	public void exportWFScheme(String pFile) {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Cancel a running process on user request
	 * @param pId Value for parameter Id
	 * @param pReason Value for parameter Reason
	 */
	public void abortProcess(WFProcessID pId, String pReason)  throws WFException {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Adds a listener
	 * @param pListener Value for parameter Listener
	 */
	public void addListener(WFListener pListener) {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Creates a new workflow process. The invoker must have the right to do that.
	 * @param pInitiator Value for parameter Initiator
	 * @param pName Value for parameter Name
	 * @return WFProcessID
	 */
	public WFProcessID createProcess(WFPrincipal pInitiator, String pName)  throws WFException {				
		// !!NO_CODE!!
		return null /* WFProcessID */;
	}
	
	/**
	 * Method getActiveProcesses
	 * @param pUser Value for parameter User
	 * @return Collection
	 */
	public Collection getActiveProcesses(WFPrincipal pUser) {				
		// !!NO_CODE!!
		return null /* Collection */;
	}
	
	/**
	 * Method getProcesses
	 * @param pUser Value for parameter User
	 * @return Collection
	 */
	public Collection getProcesses(WFPrincipal pUser) {				
		// !!NO_CODE!!
		return null /* Collection */;
	}
	
	/**
	 * Returns the worklist of the user
	 * @param pUser Value for parameter User
	 * @return Collection
	 */
	public Collection getWorklist(WFPrincipal pUser) {				
		// !!NO_CODE!!
		return null /* Collection */;
	}
	
	/**
	 * Returns the worklist of the user
	 * @param pUser Value for parameter User
	 * @param pFilter Value for parameter Filter
	 * @return Collection
	 */
	public Collection getWorklist(WFPrincipal pUser, WFActivityFilter pFilter) {				
		// !!NO_CODE!!
		return null /* Collection */;
	}
	
	/**
	 * Logs in a user and returns a user session object
	 * @param pUser Value for parameter User
	 * @param pPassword Value for parameter Password
	 * @return WFPrincipal
	 */
	public WFPrincipal login(String pUser, String pPassword) {				
		// !!NO_CODE!!
		return null /* WFPrincipal */;
	}
	
	/**
	 * Removes an existing listener
	 * @param pListener Value for parameter Listener
	 */
	public void removeListener(WFListener pListener) {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Resumes a previous suspended process. If worng state, an exception is thrown
	 * @param pUser User session object
	 * @param pId Id of suspended activity
	 */
	public void resumeProcess(WFPrincipal pUser, String pId)  throws WFException {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Suspends a process.
	 * @param pActivity Aktivität (bzw. Prozess), der suspendiert werden soll
	 * @param pResumeDate Date, when the engine should resume the activity. If null, the activity must be manually resumed
	 */
	public void suspendProcess(WFActivity pActivity, Date pResumeDate)  throws WFException {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Marks the given activity as 'completed' and returns the next activity, if unique and the same user can perform this activity. Otherwise null
	 * @param pId Value for parameter Id
	 * @param pContext Value for parameter Context
	 * @return WFActivity
	 */
	public WFActivity updateProcess(WFProcessID pId, WFContext pContext)  throws WFException {				
		// !!NO_CODE!!
		return null /* WFActivity */;
	}
	
	/**
	 * Markiert die aktuelle Aktivität als beendet und liefert die nächste Aktivität. Wenn es keine weiteren Aktivitäten mehr gibt oder der Prozess sich aufteilt, wird null zurückgegeben.

Zusätzlich kann hier die Aktivität vorgegeben werden (notwendig bei Verzweigungen)
	 * @param pId Value for parameter Id
	 * @param pContext Value for parameter Context
	 * @param pActivity Value for parameter Activity
	 * @return WFActivity
	 */
	public WFActivity updateProcess(WFProcessID pId, WFContext pContext, WFActivity pActivity) {				
		// !!NO_CODE!!
		return null /* WFActivity */;
	}
	
	/**
	 * Method getRealName
	 * @param pUser Value for parameter User
	 * @return String
	 */
	public String getRealName(WFPrincipal pUser) {				
		// !!NO_CODE!!
		return "";
	}
	
	/**
	 * Method getHistory
	 * @param pUser Value for parameter User
	 * @param pActivity Value for parameter Activity
	 * @return Collection
	 */
	public Collection getHistory(WFPrincipal pUser, WFActivity pActivity) {				
		// !!NO_CODE!!
		return null /* Collection */;
	}
	
	/**
	 * Method getGroupName
	 * @param pUser Value for parameter User
	 * @return String
	 */
	public String getGroupName(WFPrincipal pUser) {				
		// !!NO_CODE!!
		return "";
	}
	

	// Link attribute of association 'Storage '
			
	private Storage storage;

	/**
	 * Getter of association 'Storage'
	 * @return Current value of association 'Storage'.
	 * @throws RuntimeException, if value is null
	 */
	public Storage getStorage() {
		if (storage == null) {	
			// This should never happen. If so, fix your code :-)			
			throw new RuntimeException("Invalid aggregate: WFEngine#Storage is null!");
		}
		return storage;
	}

	/**
	 * Setter of association 'Storage'.
	 * @param pStorage New value for association 'Storage'
	 */
	public void setStorage (Storage pStorage) {
		if (pStorage == null && storage != null) {
			storage.unlinkEngine( this );
		}
		storage = pStorage;		 
		storage.linkEngine(this);
	}
		
	/**
	 * Checks, if aggregate 'Storage' contains elements
	 * @return true, if association contains no elements, otherwise false
	 */
	public boolean isStorageNull() {		
		return storage == null;
	}
	
	
	
	/**
	 * Internal use only
	 */
	public void linkStorage(Storage pStorage) {		
		
		if (storage != null) {
			storage.unlinkEngine(this); // Alte Beziehung löschen
		}
		storage = pStorage;
		
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkStorage(Storage pStorage) {
		storage = null;
		
	}	

	// Link attribute of association 'Resources '
			
	private Collection resources;

	/**
	 * Getter of association 'Resources'
	 * @return Currents contents of association 'Resources'
	 */
	public Collection getResources() {
		return resources != null ? resources : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'Resources'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllResources
	 * @param pResources List containing the new elements for association  'Resources'. 
	 */
	public void setResources (Collection pResources) {
		removeAllResources();	
		if (pResources != null ) {
			addAllToResources( pResources );
		}
	}

	/**
	 * Removes all elements from assoziation 'Resources'
	 */
	public void removeAllResources() {
		if (resources == null) return; // nothing to do
		
		for(Iterator it = resources.iterator(); it.hasNext();) {
			WFResource lElement = (WFResource) it.next();
			
			removeResources( lElement );				
		}		
	}

	/**
	 * Removes pResources from assoziation 'Resources'
	 * @param pResources element to remove
	 */
	public void removeResources(WFResource pResources) {
		if (resources != null) {
			resources.remove( pResources ); // notify other end
			notifyRemoveResources( pResources ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pResourcesList to association 'Resources'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pResourcesList.size()</code>)
	 */
	public int addAllToResources (Collection pResourcesList) {
		if (pResourcesList == null) {
			throw new RuntimeException("Attempted to add null container to WFEngine#Resources!");
		}
		int lInserted=0;
		for(Iterator it = pResourcesList.iterator(); it.hasNext(); ) {
			try {
				WFResource lResources = (WFResource)it.next();				
				addResources( lResources );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pResources to association 'Resources'
	 * @param pResources Element to add
	 */
	public void addResources (WFResource pResources) {
		if (pResources == null) {
			throw new RuntimeException("Attempted to add null object to WFEngine#Resources!");
		}
		
		if (resources == null) {
			resources = new ArrayList();
		}
		resources.add(pResources);
		
		notifyRemoveResources( pResources ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'Resources'
	 */
	private void notifyAddResources(WFResource pResources) {
		//System.out.println("Add " + pResources + " to WFEngine#Resources");
	}		
	
	/**
	 * Hook for 'remove' on association 'Resources'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveResources(WFResource pResources) {
		//System.out.println("Remove " + pResources + " from WFEngine#Resources");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkResources(WFResource pResources) {		
		if (resources == null) {
			resources = new ArrayList();
		}
		resources.add(pResources);
		notifyAddResources( pResources ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkResources(WFResource pResources) {
		if (resources == null) return;resources.remove(pResources);
		notifyRemoveResources( pResources ); // notify ourselves
	}	

	// Link attribute of association 'ActiveProcesses '
			
	private Collection activeProcesses;

	/**
	 * Getter of association 'ActiveProcesses'
	 * @return Currents contents of association 'ActiveProcesses'
	 */
	public Collection getActiveProcesses() {
		return activeProcesses != null ? activeProcesses : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'ActiveProcesses'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllActiveProcesses
	 * @param pActiveProcesses List containing the new elements for association  'ActiveProcesses'. 
	 */
	public void setActiveProcesses (Collection pActiveProcesses) {
		removeAllActiveProcesses();	
		if (pActiveProcesses != null ) {
			addAllToActiveProcesses( pActiveProcesses );
		}
	}

	/**
	 * Removes all elements from assoziation 'ActiveProcesses'
	 */
	public void removeAllActiveProcesses() {
		if (activeProcesses == null) return; // nothing to do
		
		for(Iterator it = activeProcesses.iterator(); it.hasNext();) {
			WFProcess lElement = (WFProcess) it.next();
			lElement.unlinkEngine( this );
			removeActiveProcesses( lElement );				
		}		
	}

	/**
	 * Removes pActiveProcesses from assoziation 'ActiveProcesses'
	 * @param pActiveProcesses element to remove
	 */
	public void removeActiveProcesses(WFProcess pActiveProcesses) {
		if (activeProcesses != null) {
			activeProcesses.remove( pActiveProcesses );
			pActiveProcesses.unlinkEngine( this ); // notify other end
			notifyRemoveActiveProcesses( pActiveProcesses ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pActiveProcessesList to association 'ActiveProcesses'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pActiveProcessesList.size()</code>)
	 */
	public int addAllToActiveProcesses (Collection pActiveProcessesList) {
		if (pActiveProcessesList == null) {
			throw new RuntimeException("Attempted to add null container to WFEngine#ActiveProcesses!");
		}
		int lInserted=0;
		for(Iterator it = pActiveProcessesList.iterator(); it.hasNext(); ) {
			try {
				WFProcess lActiveProcesses = (WFProcess)it.next();				
				addActiveProcesses( lActiveProcesses );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pActiveProcesses to association 'ActiveProcesses'
	 * @param pActiveProcesses Element to add
	 */
	public void addActiveProcesses (WFProcess pActiveProcesses) {
		if (pActiveProcesses == null) {
			throw new RuntimeException("Attempted to add null object to WFEngine#ActiveProcesses!");
		}
		
		if (activeProcesses == null) {
			activeProcesses = new ArrayList();
		}
		activeProcesses.add(pActiveProcesses);
		
		pActiveProcesses.linkEngine(this); // notify other end
		
		notifyRemoveActiveProcesses( pActiveProcesses ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'ActiveProcesses'
	 */
	private void notifyAddActiveProcesses(WFProcess pActiveProcesses) {
		//System.out.println("Add " + pActiveProcesses + " to WFEngine#ActiveProcesses");
	}		
	
	/**
	 * Hook for 'remove' on association 'ActiveProcesses'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveActiveProcesses(WFProcess pActiveProcesses) {
		//System.out.println("Remove " + pActiveProcesses + " from WFEngine#ActiveProcesses");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkActiveProcesses(WFProcess pActiveProcesses) {		
		if (activeProcesses == null) {
			activeProcesses = new ArrayList();
		}
		activeProcesses.add(pActiveProcesses);
		notifyAddActiveProcesses( pActiveProcesses ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkActiveProcesses(WFProcess pActiveProcesses) {
		if (activeProcesses == null) return;activeProcesses.remove(pActiveProcesses);
		notifyRemoveActiveProcesses( pActiveProcesses ); // notify ourselves
	}	

	// Link attribute of association 'Processes '
			
	private Collection processes;

	/**
	 * Getter of association 'Processes'
	 * @return Currents contents of association 'Processes'
	 */
	public Collection getProcesses() {
		return processes != null ? processes : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'Processes'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllProcesses
	 * @param pProcesses List containing the new elements for association  'Processes'. 
	 */
	public void setProcesses (Collection pProcesses) {
		removeAllProcesses();	
		if (pProcesses != null ) {
			addAllToProcesses( pProcesses );
		}
	}

	/**
	 * Removes all elements from assoziation 'Processes'
	 */
	public void removeAllProcesses() {
		if (processes == null) return; // nothing to do
		
		for(Iterator it = processes.iterator(); it.hasNext();) {
			WFProcessDescription lElement = (WFProcessDescription) it.next();
			lElement.unlinkContainer( this );
			removeProcesses( lElement );				
		}		
	}

	/**
	 * Removes pProcesses from assoziation 'Processes'
	 * @param pProcesses element to remove
	 */
	public void removeProcesses(WFProcessDescription pProcesses) {
		if (processes != null) {
			processes.remove( pProcesses );
			pProcesses.unlinkContainer( this ); // notify other end
			notifyRemoveProcesses( pProcesses ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pProcessesList to association 'Processes'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pProcessesList.size()</code>)
	 */
	public int addAllToProcesses (Collection pProcessesList) {
		if (pProcessesList == null) {
			throw new RuntimeException("Attempted to add null container to WFEngine#Processes!");
		}
		int lInserted=0;
		for(Iterator it = pProcessesList.iterator(); it.hasNext(); ) {
			try {
				WFProcessDescription lProcesses = (WFProcessDescription)it.next();				
				addProcesses( lProcesses );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pProcesses to association 'Processes'
	 * @param pProcesses Element to add
	 */
	public void addProcesses (WFProcessDescription pProcesses) {
		if (pProcesses == null) {
			throw new RuntimeException("Attempted to add null object to WFEngine#Processes!");
		}
		
		if (processes == null) {
			processes = new ArrayList();
		}
		processes.add(pProcesses);
		
		pProcesses.linkContainer(this); // notify other end
		
		notifyRemoveProcesses( pProcesses ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'Processes'
	 */
	private void notifyAddProcesses(WFProcessDescription pProcesses) {
		//System.out.println("Add " + pProcesses + " to WFEngine#Processes");
	}		
	
	/**
	 * Hook for 'remove' on association 'Processes'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveProcesses(WFProcessDescription pProcesses) {
		//System.out.println("Remove " + pProcesses + " from WFEngine#Processes");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkProcesses(WFProcessDescription pProcesses) {		
		if (processes == null) {
			processes = new ArrayList();
		}
		processes.add(pProcesses);
		notifyAddProcesses( pProcesses ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkProcesses(WFProcessDescription pProcesses) {
		if (processes == null) return;processes.remove(pProcesses);
		notifyRemoveProcesses( pProcesses ); // notify ourselves
	}	

	// Link attribute of association 'Scheduler '
			
	private Scheduler scheduler;

	/**
	 * Getter of association 'Scheduler'
	 * @return Current value of association 'Scheduler'.
	 * @throws RuntimeException, if value is null
	 */
	public Scheduler getScheduler() {
		if (scheduler == null) {	
			// This should never happen. If so, fix your code :-)			
			throw new RuntimeException("Invalid aggregate: WFEngine#Scheduler is null!");
		}
		return scheduler;
	}

	/**
	 * Setter of association 'Scheduler'.
	 * @param pScheduler New value for association 'Scheduler'
	 */
	public void setScheduler (Scheduler pScheduler) {
		scheduler = pScheduler;		 
		
	}
		
	/**
	 * Checks, if aggregate 'Scheduler' contains elements
	 * @return true, if association contains no elements, otherwise false
	 */
	public boolean isSchedulerNull() {		
		return scheduler == null;
	}
	
	
	
	/**
	 * Internal use only
	 */
	public void linkScheduler(Scheduler pScheduler) {		
		
		scheduler = pScheduler;
		
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkScheduler(Scheduler pScheduler) {
		scheduler = null;
		
	}	

	// Link attribute of association 'CompletedProcesses '
			
	private Collection completedProcesses;

	/**
	 * Getter of association 'CompletedProcesses'
	 * @return Currents contents of association 'CompletedProcesses'
	 */
	public Collection getCompletedProcesses() {
		return completedProcesses != null ? completedProcesses : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'CompletedProcesses'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllCompletedProcesses
	 * @param pCompletedProcesses List containing the new elements for association  'CompletedProcesses'. 
	 */
	public void setCompletedProcesses (Collection pCompletedProcesses) {
		removeAllCompletedProcesses();	
		if (pCompletedProcesses != null ) {
			addAllToCompletedProcesses( pCompletedProcesses );
		}
	}

	/**
	 * Removes all elements from assoziation 'CompletedProcesses'
	 */
	public void removeAllCompletedProcesses() {
		if (completedProcesses == null) return; // nothing to do
		
		for(Iterator it = completedProcesses.iterator(); it.hasNext();) {
			WFProcess lElement = (WFProcess) it.next();
			
			removeCompletedProcesses( lElement );				
		}		
	}

	/**
	 * Removes pCompletedProcesses from assoziation 'CompletedProcesses'
	 * @param pCompletedProcesses element to remove
	 */
	public void removeCompletedProcesses(WFProcess pCompletedProcesses) {
		if (completedProcesses != null) {
			completedProcesses.remove( pCompletedProcesses ); // notify other end
			notifyRemoveCompletedProcesses( pCompletedProcesses ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pCompletedProcessesList to association 'CompletedProcesses'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pCompletedProcessesList.size()</code>)
	 */
	public int addAllToCompletedProcesses (Collection pCompletedProcessesList) {
		if (pCompletedProcessesList == null) {
			throw new RuntimeException("Attempted to add null container to WFEngine#CompletedProcesses!");
		}
		int lInserted=0;
		for(Iterator it = pCompletedProcessesList.iterator(); it.hasNext(); ) {
			try {
				WFProcess lCompletedProcesses = (WFProcess)it.next();				
				addCompletedProcesses( lCompletedProcesses );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pCompletedProcesses to association 'CompletedProcesses'
	 * @param pCompletedProcesses Element to add
	 */
	public void addCompletedProcesses (WFProcess pCompletedProcesses) {
		if (pCompletedProcesses == null) {
			throw new RuntimeException("Attempted to add null object to WFEngine#CompletedProcesses!");
		}
		
		if (completedProcesses == null) {
			completedProcesses = new ArrayList();
		}
		completedProcesses.add(pCompletedProcesses);
		
		notifyRemoveCompletedProcesses( pCompletedProcesses ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'CompletedProcesses'
	 */
	private void notifyAddCompletedProcesses(WFProcess pCompletedProcesses) {
		//System.out.println("Add " + pCompletedProcesses + " to WFEngine#CompletedProcesses");
	}		
	
	/**
	 * Hook for 'remove' on association 'CompletedProcesses'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveCompletedProcesses(WFProcess pCompletedProcesses) {
		//System.out.println("Remove " + pCompletedProcesses + " from WFEngine#CompletedProcesses");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkCompletedProcesses(WFProcess pCompletedProcesses) {		
		if (completedProcesses == null) {
			completedProcesses = new ArrayList();
		}
		completedProcesses.add(pCompletedProcesses);
		notifyAddCompletedProcesses( pCompletedProcesses ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkCompletedProcesses(WFProcess pCompletedProcesses) {
		if (completedProcesses == null) return;completedProcesses.remove(pCompletedProcesses);
		notifyRemoveCompletedProcesses( pCompletedProcesses ); // notify ourselves
	}	

	// Link attribute of association 'SuspendedProcesses '
			
	private Collection suspendedProcesses;

	/**
	 * Getter of association 'SuspendedProcesses'
	 * @return Currents contents of association 'SuspendedProcesses'
	 */
	public Collection getSuspendedProcesses() {
		return suspendedProcesses != null ? suspendedProcesses : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'SuspendedProcesses'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllSuspendedProcesses
	 * @param pSuspendedProcesses List containing the new elements for association  'SuspendedProcesses'. 
	 */
	public void setSuspendedProcesses (Collection pSuspendedProcesses) {
		removeAllSuspendedProcesses();	
		if (pSuspendedProcesses != null ) {
			addAllToSuspendedProcesses( pSuspendedProcesses );
		}
	}

	/**
	 * Removes all elements from assoziation 'SuspendedProcesses'
	 */
	public void removeAllSuspendedProcesses() {
		if (suspendedProcesses == null) return; // nothing to do
		
		for(Iterator it = suspendedProcesses.iterator(); it.hasNext();) {
			WFProcess lElement = (WFProcess) it.next();
			
			removeSuspendedProcesses( lElement );				
		}		
	}

	/**
	 * Removes pSuspendedProcesses from assoziation 'SuspendedProcesses'
	 * @param pSuspendedProcesses element to remove
	 */
	public void removeSuspendedProcesses(WFProcess pSuspendedProcesses) {
		if (suspendedProcesses != null) {
			suspendedProcesses.remove( pSuspendedProcesses ); // notify other end
			notifyRemoveSuspendedProcesses( pSuspendedProcesses ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pSuspendedProcessesList to association 'SuspendedProcesses'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pSuspendedProcessesList.size()</code>)
	 */
	public int addAllToSuspendedProcesses (Collection pSuspendedProcessesList) {
		if (pSuspendedProcessesList == null) {
			throw new RuntimeException("Attempted to add null container to WFEngine#SuspendedProcesses!");
		}
		int lInserted=0;
		for(Iterator it = pSuspendedProcessesList.iterator(); it.hasNext(); ) {
			try {
				WFProcess lSuspendedProcesses = (WFProcess)it.next();				
				addSuspendedProcesses( lSuspendedProcesses );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pSuspendedProcesses to association 'SuspendedProcesses'
	 * @param pSuspendedProcesses Element to add
	 */
	public void addSuspendedProcesses (WFProcess pSuspendedProcesses) {
		if (pSuspendedProcesses == null) {
			throw new RuntimeException("Attempted to add null object to WFEngine#SuspendedProcesses!");
		}
		
		if (suspendedProcesses == null) {
			suspendedProcesses = new ArrayList();
		}
		suspendedProcesses.add(pSuspendedProcesses);
		
		notifyRemoveSuspendedProcesses( pSuspendedProcesses ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'SuspendedProcesses'
	 */
	private void notifyAddSuspendedProcesses(WFProcess pSuspendedProcesses) {
		//System.out.println("Add " + pSuspendedProcesses + " to WFEngine#SuspendedProcesses");
	}		
	
	/**
	 * Hook for 'remove' on association 'SuspendedProcesses'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveSuspendedProcesses(WFProcess pSuspendedProcesses) {
		//System.out.println("Remove " + pSuspendedProcesses + " from WFEngine#SuspendedProcesses");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkSuspendedProcesses(WFProcess pSuspendedProcesses) {		
		if (suspendedProcesses == null) {
			suspendedProcesses = new ArrayList();
		}
		suspendedProcesses.add(pSuspendedProcesses);
		notifyAddSuspendedProcesses( pSuspendedProcesses ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkSuspendedProcesses(WFProcess pSuspendedProcesses) {
		if (suspendedProcesses == null) return;suspendedProcesses.remove(pSuspendedProcesses);
		notifyRemoveSuspendedProcesses( pSuspendedProcesses ); // notify ourselves
	}	

	/**
	 * String representation of WFEngine
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFEngine");	
		return lRet.toString();
	}
}








