/* $Id$
 * @(#)InvalidStateException.java   Fri Aug 17 12:18:02 GMT+02:00 2001
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
 *//*
 * (#)InvalidStateException.java 
 * Exception
 */
package org.ofbiz.core.workflow;


import org.ofbiz.service.workflow.WFException;




/**
 * Exception InvalidStateException
 * @author Oliver Wieland
 * @version 1.0
 */

public class InvalidStateException extends WFException  {

	/**
	 * Constructor with error message
	 * @param pMessage Error message
	 */
	public InvalidStateException(String pMessage) {
		super(pMessage);
	}
	
	/**
	 * Constructor with error message and nested exception
	 * @param pMessage Error message
	 * @param pNestedException Root cause
	 */
	public InvalidStateException(String pMessage, Throwable pNestedException) {
		super(pMessage, pNestedException);
	}	

	/**
	 * String representation of exception InvalidStateException
	 * @return Error message, if nested exception is present, the message of the nested execption 
	 * is appended
	 */
	public String toString() {
		return super.toString();
	}
	
}



