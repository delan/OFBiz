/* $Id$
 * @(#)WFResource.java   Fri Aug 17 12:18:05 GMT+02:00 2001
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
import org.ofbiz.service.workflow.WFActivity;
import org.ofbiz.service.workflow.WFException;



/**
 * Base class for all workflow resources
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
	

	// Link attribute of association 'Group '
			
	private WFGroup group;

	/**
	 * Getter of association 'Group'
	 * @return Current value of association 'Group'.
	 */
	public WFGroup getGroup() {
		
		return group;
	}

	/**
	 * Setter of association 'Group'.
	 * @param pGroup New value for association 'Group'
	 */
	public void setGroup (WFGroup pGroup) {
		if (pGroup == null && group != null) {
			group.unlinkMembers( this );
		}
		group = pGroup;		 
		group.linkMembers(this);
	}
	
	
	
	/**
	 * Internal use only
	 */
	public void linkGroup(WFGroup pGroup) {		
		
		if (group != null) {
			group.unlinkMembers(this); // Alte Beziehung löschen
		}
		group = pGroup;
		
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkGroup(WFGroup pGroup) {
		group = null;
		
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

	/**
	 * String representation of WFResource
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFResource");	
		return lRet.toString();
	}
}








