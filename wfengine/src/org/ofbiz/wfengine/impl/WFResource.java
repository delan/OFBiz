/* $Id$
 * @(#)WFResource.java   Sun Aug 12 13:22:40 GMT+02:00 2001
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



/**
 * Base class for all workflow resources (users, machines)
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFResource implements Serializable  {

	
	// Attribute instance 'id'
	private long id;
	
	// Attribute instance 'name'
	private String name;
	
	// Attribute instance 'password'
	private String password;
	
	
		
	/**
	 * Empty constructor
	 */
	WFResource() {
			}

	/**
	 * Constructor with all attributes 
	 * @param pId Initial value for attribute 'id'
	 * @param pName Initial value for attribute 'name'
	 * @param pPassword Initial value for attribute 'password'
	 */
	WFResource(
		long pId,
		String pName,
		String pPassword) {		
				
		id = pId;		
		name = pName;		
		password = pPassword;
	}
		
	/**
	 * Getter for attribute 'id'
	 * 
	 * @return Value of attribute id
	 */
	public long getId()  {
		return id;
	}
	
	/**
	 * Setter for attribute 'id'
	 * 
	 * @param pId Neuer Wert des Attributes id
	 */
	public void setId(long pId)  {
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
	private boolean notifyAttributeChangeId(long pId) {		
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
	 * Getter for attribute 'password'
	 * 
	 * @return Value of attribute password
	 */
	public String getPassword()  {
		return password;
	}
	
	/**
	 * Setter for attribute 'password'
	 * 
	 * @param pPassword Neuer Wert des Attributes password
	 */
	public void setPassword(String pPassword)  {
		if (password == pPassword) return;		
		if ( !notifyAttributeChangePassword( pPassword ) ) return;
		password = pPassword;
	}
	
	/**
	 * This method is called, before the attribute 'Password' is set to a new
	 * value.
	 * @param pPassword New Value for attribute 'Password'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangePassword(String pPassword) {		
		return true;
	}
	
	/**
	 * Method acceptActivity
	 * @param pActivity Value for parameter Activity
	 * @return boolean
	 */
	public boolean acceptActivity(WFActivity pActivity) {				
		// !!NO_CODE!!
		return true;
	}
	
	/**
	 * Method resourceInRole
	 * @param pRolename Value for parameter Rolename
	 * @return boolean
	 */
	public boolean resourceInRole(String pRolename) {				
		// !!NO_CODE!!
		return true;
	}
	

	// Link attribute of association 'Worklist '
			
	private Worklist worklist;

	/**
	 * Getter of association 'Worklist'
	 * @return Current value of association 'Worklist'.
	 * @throws RuntimeException, if value is null
	 */
	public Worklist getWorklist() {
		if (worklist == null) {	
			// This should never happen. If so, fix your code :-)			
			throw new RuntimeException("Invalid aggregate: WFResource#Worklist is null!");
		}
		return worklist;
	}

	/**
	 * Setter of association 'Worklist'.
	 * @param pWorklist New value for association 'Worklist'
	 */
	public void setWorklist (Worklist pWorklist) {
		if (pWorklist == null && worklist != null) {
			worklist.unlinkResource( this );
		}
		worklist = pWorklist;		 
		worklist.linkResource(this);
	}
		
	/**
	 * Checks, if aggregate 'Worklist' contains elements
	 * @return true, if association contains no elements, otherwise false
	 */
	public boolean isWorklistNull() {		
		return worklist == null;
	}
	
	
	
	/**
	 * Internal use only
	 */
	public void linkWorklist(Worklist pWorklist) {		
		
		if (worklist != null) {
			worklist.unlinkResource(this); // Alte Beziehung löschen
		}
		worklist = pWorklist;
		
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkWorklist(Worklist pWorklist) {
		worklist = null;
		
	}	

	// Link attribute of association 'Roles '
			
	private Collection roles;

	/**
	 * Getter of association 'Roles'
	 * @return Currents contents of association 'Roles'
	 */
	public Collection getRoles() {
		return roles != null ? roles : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'Roles'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllRoles
	 * @param pRoles List containing the new elements for association  'Roles'. 
	 */
	public void setRoles (Collection pRoles) {
		removeAllRoles();	
		if (pRoles != null ) {
			addAllToRoles( pRoles );
		}
	}

	/**
	 * Removes all elements from assoziation 'Roles'
	 */
	public void removeAllRoles() {
		if (roles == null) return; // nothing to do
		
		for(Iterator it = roles.iterator(); it.hasNext();) {
			WFRole lElement = (WFRole) it.next();
			lElement.unlinkMembers( this );
			removeRoles( lElement );				
		}		
	}

	/**
	 * Removes pRoles from assoziation 'Roles'
	 * @param pRoles element to remove
	 */
	public void removeRoles(WFRole pRoles) {
		if (roles != null) {
			roles.remove( pRoles );
			pRoles.unlinkMembers( this ); // notify other end
			notifyRemoveRoles( pRoles ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pRolesList to association 'Roles'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pRolesList.size()</code>)
	 */
	public int addAllToRoles (Collection pRolesList) {
		if (pRolesList == null) {
			throw new RuntimeException("Attempted to add null container to WFResource#Roles!");
		}
		int lInserted=0;
		for(Iterator it = pRolesList.iterator(); it.hasNext(); ) {
			try {
				WFRole lRoles = (WFRole)it.next();				
				addRoles( lRoles );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pRoles to association 'Roles'
	 * @param pRoles Element to add
	 */
	public void addRoles (WFRole pRoles) {
		if (pRoles == null) {
			throw new RuntimeException("Attempted to add null object to WFResource#Roles!");
		}
		
		if (roles == null) {
			roles = new ArrayList();
		}
		roles.add(pRoles);
		
		pRoles.linkMembers(this); // notify other end
		
		notifyRemoveRoles( pRoles ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'Roles'
	 */
	private void notifyAddRoles(WFRole pRoles) {
		//System.out.println("Add " + pRoles + " to WFResource#Roles");
	}		
	
	/**
	 * Hook for 'remove' on association 'Roles'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveRoles(WFRole pRoles) {
		//System.out.println("Remove " + pRoles + " from WFResource#Roles");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkRoles(WFRole pRoles) {		
		if (roles == null) {
			roles = new ArrayList();
		}
		roles.add(pRoles);
		notifyAddRoles( pRoles ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkRoles(WFRole pRoles) {
		if (roles == null) return;roles.remove(pRoles);
		notifyRemoveRoles( pRoles ); // notify ourselves
	}	

	/**
	 * String representation of WFResource
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFResource");	
		return lRet.toString();
	}
}








