/* $Id$
 * @(#)WFExpression.java   Fri Aug 17 12:18:02 GMT+02:00 2001
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
import org.ofbiz.service.workflow.WFContext;
import org.ofbiz.service.workflow.WFException;



/**
 * Class to evaluate simple boolean expressions
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFExpression implements Serializable  {

	
	// Attribute instance 'expression'
	private String expression;
	
	
		
	/**
	 * Empty constructor
	 */
	WFExpression() {
			}

	/**
	 * Constructor with all attributes 
	 * @param pExpression Initial value for attribute 'expression'
	 */
	WFExpression(
		String pExpression) {		
				
		expression = pExpression;
	}
		
	/**
	 * Getter for attribute 'expression'
	 * 
	 * @return Value of attribute expression
	 */
	public String getExpression()  {
		return expression;
	}
	
	/**
	 * Setter for attribute 'expression'
	 * 
	 * @param pExpression new value for attribute expression
	 */
	public void setExpression(String pExpression)  {
		if (expression == pExpression) return;		
		if ( !notifyAttributeChangeExpression( pExpression ) ) return;
		expression = pExpression;
	}
	
	/**
	 * This method is called, before the attribute 'Expression' is set to a new
	 * value.
	 * @param pExpression New Value for attribute 'Expression'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeExpression(String pExpression) {		
		return true;
	}
	
	/**
	 * Method evaluateBoolean
	 * @param pCtx Value for parameter Ctx
	 * @return boolean
	 */
	public boolean evaluateBoolean(WFContext pCtx) {				
		// !!NO_CODE!!
		return true;
	}
	

	/**
	 * String representation of WFExpression
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFExpression");	
		return lRet.toString();
	}
}








