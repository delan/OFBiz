/* $Id$
 * @(#)WFSplitType.java   Fri Aug 17 12:18:05 GMT+02:00 2001
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




/**
 * Declares all split types
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFSplitType implements Serializable, Comparable {

	
	/** Index of value 'xor' */
	public static final int XOR = 1;	
	/**
	 * 
	 */
	public static final WFSplitType Xor = new WFSplitType(XOR);
	/** String representation of 'xor' */
	
	public static final String _Xor = "Xor";
	
	/** Index of value 'and' */
	public static final int AND = 2;	
	/**
	 * 
	 */
	public static final WFSplitType And = new WFSplitType(AND);
	/** String representation of 'and' */
	
	public static final String _And = "And";
	
	
	public static final WFSplitType NO_VALUE = new WFSplitType();
	public static final int NOT_INITIALIZED = 0;
	public static final int MAXIMUM = 2;
	
	private int value;
	private static final WFSplitType[] INSTANCES = {
		NO_VALUE,		
		Xor,		
		And
	};

	private static final String[] VALUES = {
		"?",
    		_Xor,
    		_And
	};

	/**
	 * Creates an uninitialized enumeration of WFSplitType
	 */
	private WFSplitType() {
		this( NOT_INITIALIZED );
	}

	/**
	 * Constructor with predefined value
	 * @param pValue index of instance (equal to INSTANCES[ pValue ])
	 */
	private WFSplitType(int pValue) {
		if (pValue < 0 || pValue > MAXIMUM) {
			throw new IllegalArgumentException("Value"+pValue+" is not allowed for enumeration WFSplitType, valid range is 0.."+MAXIMUM);
		}
		value = pValue;
	}

	/**
	 * Returns the actual index of the enumeration type
	 * @return actual index of the enumeration type
	 */
	public int getIndex() {
		return value;
	}

	/**
	 * String representation of enumeration 'WFSplitType'
	 * @return 'WFSplitType: <value as string>'
	 */
	public String toString() {
		return "WFSplitType:" + getValue();
	}

	/**
     	 * Returns the verbose value of enumeration WFSplitType
     	 * @return String value of current WFSplitType instance
     	 */
	public String getValue() {
		if (value > 0 && value <= MAXIMUM) {
			return VALUES[ value ];
		} else {
			throw new IllegalStateException("Value "+
				value+" is not allowed for enumeration WFSplitType, valid range is 0.."+MAXIMUM);
		}
	}

	/**
 	 * List of all values as text
	 * @return List of all values 
	 */
	public static String[] getValues() {
		return VALUES;
	}

	/**
 	 * List of all instances
 	 * @return List of all instances
	 */
	public static WFSplitType[] getInstances() {
		return INSTANCES;
	}
	
	/**
	 * Gets the corresponding instance of pValue. If pValue is invalid, an
	 * IllegalArgumentException is thrown.
	 * @return corresponding instance (equal to INSTANCES[ pValue ])
	 */
	public static WFSplitType getInstance(int pValue) {
		if (pValue > 0 && pValue <= MAXIMUM) {
			return INSTANCES[ pValue ];
		} else {
			throw new IllegalArgumentException("Value "+
				pValue+" is not allowed for enumeration WFSplitType, valid range is 0.."+MAXIMUM);
		}
	}
	
	/*
	 * Compare this instance with another object
	 * @param pObject to compare with
	 * @return true, if 1) Object is of type WFSplitType and 2) values are equal
	 */
	public boolean equals(Object pObject) {
		return pObject != null &&
		   getClass().getName().equals( pObject.getClass().getName() ) ?
		       value == ( (WFSplitType) pObject ).value:
		       false;
	}
	
	/*
	 * Compare this instance with another object
	 * @param pObject to compare with
	 * @return 0, if pObject is equal to 'this', <p>>0 , if this.value > pObject.value <p>else a negative value
	 */
	public int compareTo(Object pObject) {
		return pObject != null &&
		   getClass().getName().equals( pObject.getClass().getName() ) ?
		       value - ( (WFSplitType) pObject ).value:
		       -1;
	}
}





