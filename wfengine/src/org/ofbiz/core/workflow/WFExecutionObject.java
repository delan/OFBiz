/* $Id$
 * @(#)WFExecutionObject.java   Fri Aug 17 12:18:05 GMT+02:00 2001
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
 * @created Fri Aug 17 12:18:05 GMT+02:00 2001
 * @version 1.0
 */

/*
 * $Log$
 */
package org.ofbiz.core.workflow;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.ofbiz.service.workflow.WFContext;
import org.ofbiz.service.workflow.WFEventAudit;
import org.ofbiz.service.workflow.WFException;
import org.ofbiz.service.workflow.WFState;



/**
 * Class WFExecutionObject - no documentation available
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFExecutionObject implements Serializable  {

	
	// Attribute instance 'iD'
	private String iD;
	
	// Attribute instance 'name'
	private String name;
	
	// Attribute instance 'priority'
	private int priority;
	
	// Attribute instance 'state'
	private WFState state;
	
	// Attribute instance 'context'
	private WFContext context;
	
	// Attribute instance 'lastModified'
	private long lastModified;
	
	// Attribute instance 'creationTime'
	private long creationTime;
	
	// Attribute instance 'completionTime'
	private long completionTime;
	
	
		
	/**
	 * Empty constructor
	 */
	WFExecutionObject() {
			}

	/**
	 * Constructor with all attributes 
	 * @param pID Initial value for attribute 'iD'
	 * @param pName Initial value for attribute 'name'
	 * @param pPriority Initial value for attribute 'priority'
	 * @param pState Initial value for attribute 'state'
	 * @param pContext Initial value for attribute 'context'
	 * @param pLastModified Initial value for attribute 'lastModified'
	 * @param pCreationTime Initial value for attribute 'creationTime'
	 * @param pCompletionTime Initial value for attribute 'completionTime'
	 */
	WFExecutionObject(
		String pID,
		String pName,
		int pPriority,
		WFState pState,
		WFContext pContext,
		long pLastModified,
		long pCreationTime,
		long pCompletionTime) {		
				
		iD = pID;		
		name = pName;		
		priority = pPriority;		
		state = pState;		
		context = pContext;		
		lastModified = pLastModified;		
		creationTime = pCreationTime;		
		completionTime = pCompletionTime;
	}
		
	/**
	 * Getter for attribute 'iD'
	 * 
	 * @return Value of attribute iD
	 */
	public String getID()  {
		return iD;
	}
	
	/**
	 * Setter for attribute 'iD'
	 * 
	 * @param pID Neuer Wert des Attributes iD
	 */
	public void setID(String pID)  {
		if (iD == pID) return;		
		if ( !notifyAttributeChangeID( pID ) ) return;
		iD = pID;
	}
	
	/**
	 * This method is called, before the attribute 'ID' is set to a new
	 * value.
	 * @param pID New Value for attribute 'ID'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeID(String pID) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'name'
	 * 
	 * @return Value of attribute name
	 */
	public String getName()  {
		return name;
	}
	
	/**
	 * Setter for attribute 'name'
	 * 
	 * @param pName Neuer Wert des Attributes name
	 */
	public void setName(String pName)  {
		if (name == pName) return;		
		if ( !notifyAttributeChangeName( pName ) ) return;
		name = pName;
	}
	
	/**
	 * This method is called, before the attribute 'Name' is set to a new
	 * value.
	 * @param pName New Value for attribute 'Name'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeName(String pName) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'priority'
	 * 
	 * @return Value of attribute priority
	 */
	public int getPriority()  {
		return priority;
	}
	
	/**
	 * Setter for attribute 'priority'
	 * 
	 * @param pPriority Neuer Wert des Attributes priority
	 */
	public void setPriority(int pPriority)  {
		if (priority == pPriority) return;		
		if ( !notifyAttributeChangePriority( pPriority ) ) return;
		priority = pPriority;
	}
	
	/**
	 * This method is called, before the attribute 'Priority' is set to a new
	 * value.
	 * @param pPriority New Value for attribute 'Priority'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangePriority(int pPriority) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'state'
	 * 
	 * @return Value of attribute state
	 */
	public WFState getState()  {
		return state;
	}
	
	/**
	 * Setter for attribute 'state'
	 * 
	 * @param pState Neuer Wert des Attributes state
	 */
	public void setState(WFState pState)  {
		if (state == pState) return;		
		if ( !notifyAttributeChangeState( pState ) ) return;
		state = pState;
	}
	
	/**
	 * This method is called, before the attribute 'State' is set to a new
	 * value.
	 * @param pState New Value for attribute 'State'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeState(WFState pState) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'context'
	 * 
	 * @return Value of attribute context
	 */
	public WFContext getContext()  {
		return context;
	}
	
	/**
	 * Setter for attribute 'context'
	 * 
	 * @param pContext Neuer Wert des Attributes context
	 */
	public void setContext(WFContext pContext)  {
		if (context == pContext) return;		
		if ( !notifyAttributeChangeContext( pContext ) ) return;
		context = pContext;
	}
	
	/**
	 * This method is called, before the attribute 'Context' is set to a new
	 * value.
	 * @param pContext New Value for attribute 'Context'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeContext(WFContext pContext) {		
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
	 * Getter for attribute 'creationTime'
	 * Creation timestamp
	 * @return Value of attribute creationTime
	 */
	public long getCreationTime()  {
		return creationTime;
	}
	
	/**
	 * Setter for attribute 'creationTime'
	 * Creation timestamp
	 * @param pCreationTime Neuer Wert des Attributes creationTime
	 */
	public void setCreationTime(long pCreationTime)  {
		if (creationTime == pCreationTime) return;		
		if ( !notifyAttributeChangeCreationTime( pCreationTime ) ) return;
		creationTime = pCreationTime;
	}
	
	/**
	 * This method is called, before the attribute 'CreationTime' is set to a new
	 * value.
	 * @param pCreationTime New Value for attribute 'CreationTime'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeCreationTime(long pCreationTime) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'completionTime'
	 * Completion timestamp
	 * @return Value of attribute completionTime
	 */
	public long getCompletionTime()  {
		return completionTime;
	}
	
	/**
	 * Setter for attribute 'completionTime'
	 * Completion timestamp
	 * @param pCompletionTime Neuer Wert des Attributes completionTime
	 */
	public void setCompletionTime(long pCompletionTime)  {
		if (completionTime == pCompletionTime) return;		
		if ( !notifyAttributeChangeCompletionTime( pCompletionTime ) ) return;
		completionTime = pCompletionTime;
	}
	
	/**
	 * This method is called, before the attribute 'CompletionTime' is set to a new
	 * value.
	 * @param pCompletionTime New Value for attribute 'CompletionTime'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeCompletionTime(long pCompletionTime) {		
		return true;
	}
	
	/**
	 * Put the object in suspend state. If wrong state, an exception is thrown
	 */
	public void suspend()  throws WFException {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Resume a suspended process/activity. If wrong state, an exception is thrown
	 */
	public void resume()  throws WFException {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method abort
	 */
	public void abort()  throws WFException {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method terminate
	 */
	public void terminate()  throws WFException {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method start
	 * @param pUser Value for parameter User
	 * @param pCtx Value for parameter Ctx
	 */
	public void start(WFResource pUser, WFContext pCtx)  throws WFException {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method complete
	 */
	public void complete()  throws WFException {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method checkFreeResources
	 * @return boolean
	 */
	public boolean checkFreeResources() {				
		// !!NO_CODE!!
		return true;
	}
	

	// Link attribute of association 'Participants '
			
	private Collection participants;

	/**
	 * Getter of association 'Participants'
	 * @return Currents contents of association 'Participants'
	 */
	public Collection getParticipants() {
		return participants != null ? participants : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'Participants'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllParticipants
	 * @param pParticipants List containing the new elements for association  'Participants'. 
	 */
	public void setParticipants (Collection pParticipants) {
		removeAllParticipants();	
		if (pParticipants != null ) {
			addAllToParticipants( pParticipants );
		}
	}

	/**
	 * Removes all elements from assoziation 'Participants'
	 */
	public void removeAllParticipants() {
		if (participants == null) return; // nothing to do
		
		for(Iterator it = participants.iterator(); it.hasNext();) {
			WFResource lElement = (WFResource) it.next();
			
			removeParticipants( lElement );				
		}		
	}

	/**
	 * Removes pParticipants from assoziation 'Participants'
	 * @param pParticipants element to remove
	 */
	public void removeParticipants(WFResource pParticipants) {
		if (participants != null) {
			participants.remove( pParticipants ); // notify other end
			notifyRemoveParticipants( pParticipants ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pParticipantsList to association 'Participants'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pParticipantsList.size()</code>)
	 */
	public int addAllToParticipants (Collection pParticipantsList) {
		if (pParticipantsList == null) {
			throw new RuntimeException("Attempted to add null container to WFExecutionObject#Participants!");
		}
		int lInserted=0;
		for(Iterator it = pParticipantsList.iterator(); it.hasNext(); ) {
			try {
				WFResource lParticipants = (WFResource)it.next();				
				addParticipants( lParticipants );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pParticipants to association 'Participants'
	 * @param pParticipants Element to add
	 */
	public void addParticipants (WFResource pParticipants) {
		if (pParticipants == null) {
			throw new RuntimeException("Attempted to add null object to WFExecutionObject#Participants!");
		}
		
		if (participants == null) {
			participants = new ArrayList();
		}
		participants.add(pParticipants);
		
		notifyRemoveParticipants( pParticipants ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'Participants'
	 */
	private void notifyAddParticipants(WFResource pParticipants) {
		//System.out.println("Add " + pParticipants + " to WFExecutionObject#Participants");
	}		
	
	/**
	 * Hook for 'remove' on association 'Participants'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveParticipants(WFResource pParticipants) {
		//System.out.println("Remove " + pParticipants + " from WFExecutionObject#Participants");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkParticipants(WFResource pParticipants) {		
		if (participants == null) {
			participants = new ArrayList();
		}
		participants.add(pParticipants);
		notifyAddParticipants( pParticipants ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkParticipants(WFResource pParticipants) {
		if (participants == null) return;participants.remove(pParticipants);
		notifyRemoveParticipants( pParticipants ); // notify ourselves
	}	

	// Link attribute of association 'History '
			
	private Collection history;

	/**
	 * Getter of association 'History'
	 * @return Currents contents of association 'History'
	 */
	public Collection getHistory() {
		return history != null ? history : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'History'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllHistory
	 * @param pHistory List containing the new elements for association  'History'. 
	 */
	public void setHistory (Collection pHistory) {
		removeAllHistory();	
		if (pHistory != null ) {
			addAllToHistory( pHistory );
		}
	}

	/**
	 * Removes all elements from assoziation 'History'
	 */
	public void removeAllHistory() {
		if (history == null) return; // nothing to do
		
		for(Iterator it = history.iterator(); it.hasNext();) {
			WFEventAudit lElement = (WFEventAudit) it.next();
			lElement.unlinkSource( this );
			removeHistory( lElement );				
		}		
	}

	/**
	 * Removes pHistory from assoziation 'History'
	 * @param pHistory element to remove
	 */
	public void removeHistory(WFEventAudit pHistory) {
		if (history != null) {
			history.remove( pHistory );
			pHistory.unlinkSource( this ); // notify other end
			notifyRemoveHistory( pHistory ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pHistoryList to association 'History'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pHistoryList.size()</code>)
	 */
	public int addAllToHistory (Collection pHistoryList) {
		if (pHistoryList == null) {
			throw new RuntimeException("Attempted to add null container to WFExecutionObject#History!");
		}
		int lInserted=0;
		for(Iterator it = pHistoryList.iterator(); it.hasNext(); ) {
			try {
				WFEventAudit lHistory = (WFEventAudit)it.next();				
				addHistory( lHistory );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pHistory to association 'History'
	 * @param pHistory Element to add
	 */
	public void addHistory (WFEventAudit pHistory) {
		if (pHistory == null) {
			throw new RuntimeException("Attempted to add null object to WFExecutionObject#History!");
		}
		
		if (history == null) {
			history = new ArrayList();
		}
		history.add(pHistory);
		
		pHistory.linkSource(this); // notify other end
		
		notifyRemoveHistory( pHistory ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'History'
	 */
	private void notifyAddHistory(WFEventAudit pHistory) {
		//System.out.println("Add " + pHistory + " to WFExecutionObject#History");
	}		
	
	/**
	 * Hook for 'remove' on association 'History'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveHistory(WFEventAudit pHistory) {
		//System.out.println("Remove " + pHistory + " from WFExecutionObject#History");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkHistory(WFEventAudit pHistory) {		
		if (history == null) {
			history = new ArrayList();
		}
		history.add(pHistory);
		notifyAddHistory( pHistory ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkHistory(WFEventAudit pHistory) {
		if (history == null) return;history.remove(pHistory);
		notifyRemoveHistory( pHistory ); // notify ourselves
	}	

	/**
	 * String representation of WFExecutionObject
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFExecutionObject");	
		return lRet.toString();
	}
}








