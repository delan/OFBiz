/* $Id$
 * @(#)WFJoin.java   Fri Aug 17 12:18:02 GMT+02:00 2001
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



/**
 * Class WFJoin - no documentation available
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFJoin extends WFActivityImpl  implements Serializable  {

	
	// Attribute instance 'joinType'
	private WFJoinType joinType;
	
	
		
	/**
	 * Empty constructor
	 */
	WFJoin() {
		super();	
	}

	/**
	 * Constructor with all attributes 
	 * @param pJoinType Initial value for attribute 'joinType'
	 */
	WFJoin(
		WFJoinType pJoinType) {		
				
		joinType = pJoinType;
	}
		
	/**
	 * Getter for attribute 'joinType'
	 * 
	 * @return Value of attribute joinType
	 */
	public WFJoinType getJoinType()  {
		return joinType;
	}
	
	/**
	 * Setter for attribute 'joinType'
	 * 
	 * @param pJoinType new value for attribute joinType
	 */
	public void setJoinType(WFJoinType pJoinType)  {
		if (joinType == pJoinType) return;		
		if ( !notifyAttributeChangeJoinType( pJoinType ) ) return;
		joinType = pJoinType;
	}
	
	/**
	 * This method is called, before the attribute 'JoinType' is set to a new
	 * value.
	 * @param pJoinType New Value for attribute 'JoinType'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeJoinType(WFJoinType pJoinType) {		
		return true;
	}
	

	/**
	 * String representation of WFJoin
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFJoin");	
		return lRet.toString();
	}
}








