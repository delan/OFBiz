/* $Id$
 * @(#)WFMessageType.java   Sun Aug 12 13:22:40 GMT+02:00 2001
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
 * @created Sun Aug 12 13:22:40 GMT+02:00 2001
 * @version 1.0
 */

/*
 * $Log$
 */

package org.ofbiz.wfengine.impl;


import java.io.Serializable;




/**
 * Represents all message kinds
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFMessageType implements Serializable, Comparable {

	
	/** Index of value 'debug' */
	public static final int DEBUG = 1;	
	/**
	 * Debug
	 */
	public static final WFMessageType Debug = new WFMessageType(DEBUG);
	/** String representation of 'debug' */
	
	public static final String _Debug = "Debug";
	
	/** Index of value 'info' */
	public static final int INFO = 2;	
	/**
	 * Normal information
	 */
	public static final WFMessageType Info = new WFMessageType(INFO);
	/** String representation of 'info' */
	
	public static final String _Info = "Info";
	
	/** Index of value 'warning' */
	public static final int WARNING = 3;	
	/**
	 * Warning
	 */
	public static final WFMessageType Warning = new WFMessageType(WARNING);
	/** String representation of 'warning' */
	
	public static final String _Warning = "Warning";
	
	/** Index of value 'error' */
	public static final int ERROR = 4;	
	/**
	 * Recoverable error
	 */
	public static final WFMessageType Error = new WFMessageType(ERROR);
	/** String representation of 'error' */
	
	public static final String _Error = "Error";
	
	/** Index of value 'fatal' */
	public static final int FATAL = 5;	
	/**
	 * Unrecoverable error
	 */
	public static final WFMessageType Fatal = new WFMessageType(FATAL);
	/** String representation of 'fatal' */
	
	public static final String _Fatal = "Fatal";
	
	
	public static final WFMessageType NO_VALUE = new WFMessageType();
	public static final int NOT_INITIALIZED = 0;
	public static final int MAXIMUM = 5;
	
	private int value;
	private static final WFMessageType[] INSTANCES = {
		NO_VALUE,		
		Debug,		
		Info,		
		Warning,		
		Error,		
		Fatal
	};

	private static final String[] VALUES = {
		"?",
    		_Debug,
    		_Info,
    		_Warning,
    		_Error,
    		_Fatal
	};

	/**
	 * Creates an uninitialized enumeration of WFMessageType
	 */
	private WFMessageType() {
		this( NOT_INITIALIZED );
	}

	/**
	 * Constructor with predefined value
	 * @param pValue index of instance (equal to INSTANCES[ pValue ])
	 */
	private WFMessageType(int pValue) {
		if (pValue < 0 || pValue > MAXIMUM) {
			throw new IllegalArgumentException("Value"+pValue+" is not allowed for enumeration WFMessageType, valid range is 0.."+MAXIMUM);
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
	 * String representation of enumeration 'WFMessageType'
	 * @return 'WFMessageType: <value as string>'
	 */
	public String toString() {
		return "WFMessageType:" + getValue();
	}

	/**
     	 * Returns the verbose value of enumeration WFMessageType
     	 * @return String value of current WFMessageType instance
     	 */
	public String getValue() {
		if (value > 0 && value <= MAXIMUM) {
			return VALUES[ value ];
		} else {
			throw new IllegalStateException("Value "+
				value+" is not allowed for enumeration WFMessageType, valid range is 0.."+MAXIMUM);
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
	public static WFMessageType[] getInstances() {
		return INSTANCES;
	}
	
	/**
	 * Gets the corresponding instance of pValue. If pValue is invalid, an
	 * IllegalArgumentException is thrown.
	 * @return corresponding instance (equal to INSTANCES[ pValue ])
	 */
	public static WFMessageType getInstance(int pValue) {
		if (pValue > 0 && pValue <= MAXIMUM) {
			return INSTANCES[ pValue ];
		} else {
			throw new IllegalArgumentException("Value "+
				pValue+" is not allowed for enumeration WFMessageType, valid range is 0.."+MAXIMUM);
		}
	}
	
	/*
	 * Compare this instance with another object
	 * @param pObject to compare with
	 * @return true, if 1) Object is of type WFMessageType and 2) values are equal
	 */
	public boolean equals(Object pObject) {
		return pObject != null &&
		   getClass().getName().equals( pObject.getClass().getName() ) ?
		       value == ( (WFMessageType) pObject ).value:
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
		       value - ( (WFMessageType) pObject ).value:
		       -1;
	}
}





