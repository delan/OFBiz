/* $Id$
 * @(#)WFSplit.java   Fri Aug 17 12:18:05 GMT+02:00 2001
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
 * Class WFSplit - no documentation available
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFSplit extends WFActivityImpl  implements Serializable  {

	
	// Attribute instance 'splitType'
	private WFSplitType splitType;
	
	
		
	/**
	 * Empty constructor
	 */
	WFSplit() {
		super();	
	}

	/**
	 * Constructor with all attributes 
	 * @param pSplitType Initial value for attribute 'splitType'
	 */
	WFSplit(
		WFSplitType pSplitType) {		
				
		splitType = pSplitType;
	}
		
	/**
	 * Getter for attribute 'splitType'
	 * 
	 * @return Value of attribute splitType
	 */
	public WFSplitType getSplitType()  {
		return splitType;
	}
	
	/**
	 * Setter for attribute 'splitType'
	 * 
	 * @param pSplitType Neuer Wert des Attributes splitType
	 */
	public void setSplitType(WFSplitType pSplitType)  {
		if (splitType == pSplitType) return;		
		if ( !notifyAttributeChangeSplitType( pSplitType ) ) return;
		splitType = pSplitType;
	}
	
	/**
	 * This method is called, before the attribute 'SplitType' is set to a new
	 * value.
	 * @param pSplitType New Value for attribute 'SplitType'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeSplitType(WFSplitType pSplitType) {		
		return true;
	}
	

	/**
	 * String representation of WFSplit
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFSplit");	
		return lRet.toString();
	}
}








