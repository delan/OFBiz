/* $Id$
 * @(#)ProcessIDImpl.java   Fri Aug 17 12:18:02 GMT+02:00 2001
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
import org.ofbiz.service.workflow.WFProcessID;



/**
 * Class ProcessIDImpl - no documentation available
 * @author Oliver Wieland
 * @version 1.0
 */

public class ProcessIDImpl implements WFProcessID {

	
	// Attribute instance 'leaseTime'
	private long leaseTime;
	
	
		
	/**
	 * Empty constructor
	 */
	ProcessIDImpl() {
			}

	/**
	 * Constructor with all attributes 
	 * @param pLeaseTime Initial value for attribute 'leaseTime'
	 */
	ProcessIDImpl(
		long pLeaseTime) {		
				
		leaseTime = pLeaseTime;
	}
		
	/**
	 * Getter for attribute 'leaseTime'
	 * Dauer der Session in ms. Wenn der Wert abgelaufen ist, wird die Session automatisch ungültig. Wenn -1, ist die Session unbegrenzt gültig.
	 * @return Value of attribute leaseTime
	 */
	public long getLeaseTime()  {
		return leaseTime;
	}
	
	/**
	 * Setter for attribute 'leaseTime'
	 * Dauer der Session in ms. Wenn der Wert abgelaufen ist, wird die Session automatisch ungültig. Wenn -1, ist die Session unbegrenzt gültig.
	 * @param pLeaseTime new value for attribute leaseTime
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
	 * Macht das Sessionobjekt ungültig
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
	 * Liefert die eindeutige ID des Prozesses
	 * @return long
	 */
	public long getId() {				
		// !!NO_CODE!!
		return 0L;
	}
	
	/**
	 * Prüft, ob das Handle noch gültig ist. Wenn nicht, deutet dies entweder auf Zeitüberschreitung oder auf einen bereits beendeten Prozess hin.
	 * @return boolean
	 */
	public boolean isValid() {				
		// !!NO_CODE!!
		return true;
	}
	

	/**
	 * String representation of ProcessIDImpl
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("ProcessIDImpl");	
		return lRet.toString();
	}
}








