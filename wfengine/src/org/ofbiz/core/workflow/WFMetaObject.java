/* $Id$
 * @(#)WFMetaObject.java   Fri Aug 17 12:18:05 GMT+02:00 2001
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
import org.ofbiz.service.workflow.WFException;



/**
 * Base class for all meta objects
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFMetaObject implements Serializable  {

	
	// Attribute instance 'id'
	private String id;
	
	// Attribute instance 'name'
	private String name;
	
	// Attribute instance 'description'
	private String description;
	
	// Attribute instance 'priority'
	private int priority;
	
	
		
	/**
	 * Empty constructor
	 */
	WFMetaObject() {
			}

	/**
	 * Constructor with all attributes 
	 * @param pId Initial value for attribute 'id'
	 * @param pName Initial value for attribute 'name'
	 * @param pDescription Initial value for attribute 'description'
	 * @param pPriority Initial value for attribute 'priority'
	 */
	WFMetaObject(
		String pId,
		String pName,
		String pDescription,
		int pPriority) {		
				
		id = pId;		
		name = pName;		
		description = pDescription;		
		priority = pPriority;
	}
		
	/**
	 * Getter for attribute 'id'
	 * 
	 * @return Value of attribute id
	 */
	public String getId()  {
		return id;
	}
	
	/**
	 * Setter for attribute 'id'
	 * 
	 * @param pId Neuer Wert des Attributes id
	 */
	public void setId(String pId)  {
		if (id == pId) return;		
		if ( !notifyAttributeChangeId( pId ) ) return;
		id = pId;
	}
	
	/**
	 * This method is called, before the attribute 'Id' is set to a new
	 * value.
	 * @param pId New Value for attribute 'Id'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeId(String pId) {		
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
	 * Getter for attribute 'description'
	 * 
	 * @return Value of attribute description
	 */
	public String getDescription()  {
		return description;
	}
	
	/**
	 * Setter for attribute 'description'
	 * 
	 * @param pDescription Neuer Wert des Attributes description
	 */
	public void setDescription(String pDescription)  {
		if (description == pDescription) return;		
		if ( !notifyAttributeChangeDescription( pDescription ) ) return;
		description = pDescription;
	}
	
	/**
	 * This method is called, before the attribute 'Description' is set to a new
	 * value.
	 * @param pDescription New Value for attribute 'Description'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeDescription(String pDescription) {		
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
	 * Method acceptRole
	 * @param pRole Value for parameter Role
	 * @return boolean
	 */
	public boolean acceptRole(WFResource pRole) {				
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
			throw new RuntimeException("Attempted to add null container to WFMetaObject#Participants!");
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
			throw new RuntimeException("Attempted to add null object to WFMetaObject#Participants!");
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
		//System.out.println("Add " + pParticipants + " to WFMetaObject#Participants");
	}		
	
	/**
	 * Hook for 'remove' on association 'Participants'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveParticipants(WFResource pParticipants) {
		//System.out.println("Remove " + pParticipants + " from WFMetaObject#Participants");		
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

	/**
	 * String representation of WFMetaObject
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFMetaObject");	
		return lRet.toString();
	}
}








