/* $Id$
 * @(#)WFContext.java   Sun Aug 12 13:22:41 GMT+02:00 2001
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
package org.ofbiz.wfengine;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.ofbiz.wfengine.WFException;



/**
 * Context of an activity or process. Currently they are represented by key-value-pairs.
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFContext implements Serializable  {

	
	
		
	/**
	 * Empty constructor
	 */
	WFContext() {
	}

	/**
	 * Liefert den Wert zu einem Schlüssel
	 * @param pName Value for parameter Name
	 * @return Object
	 */
	public Object getValue(String pName) {				
		// !!NO_CODE!!
		return null /* Object */;
	}
	
	/**
	 * Setzt einen Wert
	 * @param pName Value for parameter Name
	 * @param pValue Value for parameter Value
	 */
	public void setValue(String pName, Object pValue) {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Liefert alle Schlüssel des Kontextes
	 * @return Collection
	 */
	public Collection getNames() {				
		// !!NO_CODE!!
		return null /* Collection */;
	}
	

	/**
	 * String representation of WFContext
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFContext");	
		return lRet.toString();
	}
}








