/* $Id$
 * @(#)WFRole.java   Sun Aug 12 13:22:41 GMT+02:00 2001
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
 * Class WFRole - no documentation available
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFRole implements Serializable  {

	
	// Attribute instance 'name'
	private String name;
	
	
		
	/**
	 * Empty constructor
	 */
	WFRole() {
			}

	/**
	 * Constructor with all attributes 
	 * @param pName Initial value for attribute 'name'
	 */
	WFRole(
		String pName) {		
				
		name = pName;
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
	

	// Link attribute of association 'Members '
			
	private Collection members;

	/**
	 * Getter of association 'Members'
	 * @return Currents contents of association 'Members'
	 */
	public Collection getMembers() {
		return members != null ? members : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'Members'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllMembers
	 * @param pMembers List containing the new elements for association  'Members'. 
	 */
	public void setMembers (Collection pMembers) {
		removeAllMembers();	
		if (pMembers != null ) {
			addAllToMembers( pMembers );
		}
	}

	/**
	 * Removes all elements from assoziation 'Members'
	 */
	public void removeAllMembers() {
		if (members == null) return; // nothing to do
		
		for(Iterator it = members.iterator(); it.hasNext();) {
			WFResource lElement = (WFResource) it.next();
			lElement.unlinkRoles( this );
			removeMembers( lElement );				
		}		
	}

	/**
	 * Removes pMembers from assoziation 'Members'
	 * @param pMembers element to remove
	 */
	public void removeMembers(WFResource pMembers) {
		if (members != null) {
			members.remove( pMembers );
			pMembers.unlinkRoles( this ); // notify other end
			notifyRemoveMembers( pMembers ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pMembersList to association 'Members'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pMembersList.size()</code>)
	 */
	public int addAllToMembers (Collection pMembersList) {
		if (pMembersList == null) {
			throw new RuntimeException("Attempted to add null container to WFRole#Members!");
		}
		int lInserted=0;
		for(Iterator it = pMembersList.iterator(); it.hasNext(); ) {
			try {
				WFResource lMembers = (WFResource)it.next();				
				addMembers( lMembers );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pMembers to association 'Members'
	 * @param pMembers Element to add
	 */
	public void addMembers (WFResource pMembers) {
		if (pMembers == null) {
			throw new RuntimeException("Attempted to add null object to WFRole#Members!");
		}
		
		if (members == null) {
			members = new ArrayList();
		}
		members.add(pMembers);
		
		pMembers.linkRoles(this); // notify other end
		
		notifyRemoveMembers( pMembers ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'Members'
	 */
	private void notifyAddMembers(WFResource pMembers) {
		//System.out.println("Add " + pMembers + " to WFRole#Members");
	}		
	
	/**
	 * Hook for 'remove' on association 'Members'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveMembers(WFResource pMembers) {
		//System.out.println("Remove " + pMembers + " from WFRole#Members");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkMembers(WFResource pMembers) {		
		if (members == null) {
			members = new ArrayList();
		}
		members.add(pMembers);
		notifyAddMembers( pMembers ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkMembers(WFResource pMembers) {
		if (members == null) return;members.remove(pMembers);
		notifyRemoveMembers( pMembers ); // notify ourselves
	}	

	/**
	 * String representation of WFRole
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFRole");	
		return lRet.toString();
	}
}








