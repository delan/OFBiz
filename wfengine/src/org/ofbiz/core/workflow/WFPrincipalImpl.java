/* $Id$
 * @(#)WFPrincipalImpl.java   Fri Aug 17 12:18:05 GMT+02:00 2001
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
import org.ofbiz.service.workflow.WFPrincipal;



/**
 * Class WFPrincipalImpl - no documentation available
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFPrincipalImpl implements WFPrincipal {

	
	// Attribute instance 'leaseTime'
	private long leaseTime;
	
	
		
	/**
	 * Empty constructor
	 */
	WFPrincipalImpl() {
			}

	/**
	 * Constructor with all attributes 
	 * @param pLeaseTime Initial value for attribute 'leaseTime'
	 */
	WFPrincipalImpl(
		long pLeaseTime) {		
				
		leaseTime = pLeaseTime;
	}
		
	/**
	 * Getter for attribute 'leaseTime'
	 * 
	 * @return Value of attribute leaseTime
	 */
	public long getLeaseTime()  {
		return leaseTime;
	}
	
	/**
	 * Setter for attribute 'leaseTime'
	 * 
	 * @param pLeaseTime Neuer Wert des Attributes leaseTime
	 */
	public void setLeaseTime(long pLeaseTime)  {
		if (leaseTime == pLeaseTime) return;		
		if ( !notifyAttributeChangeLeaseTime( pLeaseTime ) ) return;
		leaseTime = pLeaseTime;
	}
	
	/**
	 * This method is called, before the attribute 'LeaseTime' is set to a new
	 * value.
	 * @param pLeaseTime New Value for attribute 'LeaseTime'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeLeaseTime(long pLeaseTime) {		
		return true;
	}
	
	/**
	 * Method createID
	 */
	protected void createID() {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method invalidate
	 */
	public void invalidate() {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method refresh
	 */
	public void refresh() {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method getId
	 * @return String
	 */
	public String getId() {				
		// !!NO_CODE!!
		return "";
	}
	
	/**
	 * Method isValid
	 * @return boolean
	 */
	public boolean isValid() {				
		// !!NO_CODE!!
		return true;
	}
	

	/**
	 * String representation of WFPrincipalImpl
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFPrincipalImpl");	
		return lRet.toString();
	}
}








