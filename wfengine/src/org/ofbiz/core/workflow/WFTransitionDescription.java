/* $Id$
 * @(#)WFTransitionDescription.java   Fri Aug 17 12:18:05 GMT+02:00 2001
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
 * Revision 1.1  2001/08/10 10:43:15  owieland
 * Add missing files from import
 *
 */
package org.ofbiz.core.workflow;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.ofbiz.service.workflow.WFException;



/**
 * Class WFTransitionDescription - no documentation available
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFTransitionDescription implements Serializable  {

	
	// Attribute instance 'id'
	private String id;
	
	// Attribute instance 'condition'
	private String condition;
	
	
		
	/**
	 * Empty constructor
	 */
	WFTransitionDescription() {
			}

	/**
	 * Constructor with all attributes 
	 * @param pId Initial value for attribute 'id'
	 * @param pCondition Initial value for attribute 'condition'
	 */
	WFTransitionDescription(
		String pId,
		String pCondition) {		
				
		id = pId;		
		condition = pCondition;
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
	 * @param pId new value for attribute id
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
	 * Getter for attribute 'condition'
	 * 
	 * @return Value of attribute condition
	 */
	public String getCondition()  {
		return condition;
	}
	
	/**
	 * Setter for attribute 'condition'
	 * 
	 * @param pCondition new value for attribute condition
	 */
	public void setCondition(String pCondition)  {
		if (condition == pCondition) return;		
		if ( !notifyAttributeChangeCondition( pCondition ) ) return;
		condition = pCondition;
	}
	
	/**
	 * This method is called, before the attribute 'Condition' is set to a new
	 * value.
	 * @param pCondition New Value for attribute 'Condition'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeCondition(String pCondition) {		
		return true;
	}
	

	// Link attribute of association 'To '
			
	private WFActivityDescription to;

	/**
	 * Getter of association 'To'
	 * @return Current value of association 'To'.
	 * @throws RuntimeException, if value is null
	 */
	public WFActivityDescription getTo() {
		if (to == null) {	
			// This should never happen. If so, fix your code :-)			
			throw new RuntimeException("Invalid aggregate: WFTransitionDescription#To is null!");
		}
		return to;
	}

	/**
	 * Setter of association 'To'.
	 * @param pTo New value for association 'To'
	 */
	public void setTo (WFActivityDescription pTo) {
		if (pTo == null && to != null) {
			to.unlinkIn( this );
		}
		to = pTo;		 
		to.linkIn(this);
	}
		
	/**
	 * Checks, if aggregate 'To' contains elements
	 * @return true, if association contains no elements, otherwise false
	 */
	public boolean isToNull() {		
		return to == null;
	}
	
	
	
	/**
	 * Internal use only
	 */
	public void linkTo(WFActivityDescription pTo) {		
		
		if (to != null) {
			to.unlinkIn(this); // Alte Beziehung löschen
		}
		to = pTo;
		
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkTo(WFActivityDescription pTo) {
		to = null;
		
	}	

	// Link attribute of association 'From '
			
	private WFActivityDescription from;

	/**
	 * Getter of association 'From'
	 * @return Current value of association 'From'.
	 * @throws RuntimeException, if value is null
	 */
	public WFActivityDescription getFrom() {
		if (from == null) {	
			// This should never happen. If so, fix your code :-)			
			throw new RuntimeException("Invalid aggregate: WFTransitionDescription#From is null!");
		}
		return from;
	}

	/**
	 * Setter of association 'From'.
	 * @param pFrom New value for association 'From'
	 */
	public void setFrom (WFActivityDescription pFrom) {
		if (pFrom == null && from != null) {
			from.unlinkOut( this );
		}
		from = pFrom;		 
		from.linkOut(this);
	}
		
	/**
	 * Checks, if aggregate 'From' contains elements
	 * @return true, if association contains no elements, otherwise false
	 */
	public boolean isFromNull() {		
		return from == null;
	}
	
	
	
	/**
	 * Internal use only
	 */
	public void linkFrom(WFActivityDescription pFrom) {		
		
		if (from != null) {
			from.unlinkOut(this); // Alte Beziehung löschen
		}
		from = pFrom;
		
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkFrom(WFActivityDescription pFrom) {
		from = null;
		
	}	

	/**
	 * String representation of WFTransitionDescription
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFTransitionDescription");	
		return lRet.toString();
	}
}








