/* $Id$
 * @(#)WFTransition.java   Sun Aug 12 13:22:41 GMT+02:00 2001
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
import org.ofbiz.wfengine.WFContext;



/**
 * Represents a connection between two activity instances
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFTransition implements Serializable  {

	
	
		
	/**
	 * Empty constructor
	 */
	WFTransition() {
			}
	/**
	 * Method canProceed
	 * @param pCtx Value for parameter Ctx
	 * @return boolean
	 */
	public boolean canProceed(WFContext pCtx) {				
		// !!NO_CODE!!
		return true;
	}
	

	// Link attribute of association 'From '
			
	private WFActivityImpl from;

	/**
	 * Getter of association 'From'
	 * @return Current value of association 'From'.
	 * @throws RuntimeException, if value is null
	 */
	public WFActivityImpl getFrom() {
		if (from == null) {	
			// This should never happen. If so, fix your code :-)			
			throw new RuntimeException("Invalid aggregate: WFTransition#From is null!");
		}
		return from;
	}

	/**
	 * Setter of association 'From'.
	 * @param pFrom New value for association 'From'
	 */
	public void setFrom (WFActivityImpl pFrom) {
		if (pFrom == null && from != null) {
			from.unlinkNext( this );
		}
		from = pFrom;		 
		from.linkNext(this);
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
	public void linkFrom(WFActivityImpl pFrom) {		
		
		if (from != null) {
			from.unlinkNext(this); // Alte Beziehung löschen
		}
		from = pFrom;
		
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkFrom(WFActivityImpl pFrom) {
		from = null;
		
	}	

	// Link attribute of association 'To '
			
	private WFActivityImpl to;

	/**
	 * Getter of association 'To'
	 * @return Current value of association 'To'.
	 * @throws RuntimeException, if value is null
	 */
	public WFActivityImpl getTo() {
		if (to == null) {	
			// This should never happen. If so, fix your code :-)			
			throw new RuntimeException("Invalid aggregate: WFTransition#To is null!");
		}
		return to;
	}

	/**
	 * Setter of association 'To'.
	 * @param pTo New value for association 'To'
	 */
	public void setTo (WFActivityImpl pTo) {
		if (pTo == null && to != null) {
			to.unlinkPrevious( this );
		}
		to = pTo;		 
		to.linkPrevious(this);
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
	public void linkTo(WFActivityImpl pTo) {		
		
		if (to != null) {
			to.unlinkPrevious(this); // Alte Beziehung löschen
		}
		to = pTo;
		
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkTo(WFActivityImpl pTo) {
		to = null;
		
	}	

	// Link attribute of association 'Condition '
			
	private WFExpression condition;

	/**
	 * Getter of association 'Condition'
	 * @return Current value of association 'Condition'.
	 */
	public WFExpression getCondition() {
		
		return condition;
	}

	/**
	 * Setter of association 'Condition'.
	 * @param pCondition New value for association 'Condition'
	 */
	public void setCondition (WFExpression pCondition) {
		condition = pCondition;		 
		
	}
	
	
	
	/**
	 * Internal use only
	 */
	public void linkCondition(WFExpression pCondition) {		
		
		condition = pCondition;
		
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkCondition(WFExpression pCondition) {
		condition = null;
		
	}	

	/**
	 * String representation of WFTransition
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFTransition");	
		return lRet.toString();
	}
}








