/* $Id$
 * @(#)WFDataType.java   Fri Aug 17 12:18:00 GMT+02:00 2001
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
 * @created Fri Aug 17 12:18:00 GMT+02:00 2001
 * @version 1.0
 */

/*
 * $Log$
 * Revision 1.1  2001/08/10 11:00:31  owieland
 * Import correction
 *
 */
package org.ofbiz.core.workflow.datatypes;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.ofbiz.service.workflow.WFException;



/**
 * Class WFDataType - no documentation available
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFDataType implements Serializable  {

	
	// Attribute instance 'name'
	private String name;
	
	
		
	/**
	 * Empty constructor
	 */
	WFDataType() {
			}

	/**
	 * Constructor with all attributes 
	 * @param pName Initial value for attribute 'name'
	 */
	WFDataType(
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
	 * @param pName new value for attribute name
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
	 * String representation of WFDataType
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFDataType");	
		return lRet.toString();
	}
}








