/* $Id$
 * @(#)WFActivityType.java   Fri Aug 17 12:18:05 GMT+02:00 2001
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
 * Type of activity (plain, split, join, loop, etc.)
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFActivityType implements Serializable, Comparable {

	
	/** Index of value 'plain' */
	public static final int PLAIN = 1;	
	/**
	 * 
	 */
	public static final WFActivityType Plain = new WFActivityType(PLAIN);
	/** String representation of 'plain' */
	
	public static final String _Plain = "Plain";
	
	/** Index of value 'join' */
	public static final int JOIN = 2;	
	/**
	 * 
	 */
	public static final WFActivityType Join = new WFActivityType(JOIN);
	/** String representation of 'join' */
	
	public static final String _Join = "Join";
	
	/** Index of value 'split' */
	public static final int SPLIT = 3;	
	/**
	 * 
	 */
	public static final WFActivityType Split = new WFActivityType(SPLIT);
	/** String representation of 'split' */
	
	public static final String _Split = "Split";
	
	/** Index of value 'loop' */
	public static final int LOOP = 4;	
	/**
	 * 
	 */
	public static final WFActivityType Loop = new WFActivityType(LOOP);
	/** String representation of 'loop' */
	
	public static final String _Loop = "Loop";
	
	/** Index of value 'other' */
	public static final int OTHER = 5;	
	/**
	 * 
	 */
	public static final WFActivityType Other = new WFActivityType(OTHER);
	/** String representation of 'other' */
	
	public static final String _Other = "Other";
	
	/** Index of value 'event' */
	public static final int EVENT = 6;	
	/**
	 * 
	 */
	public static final WFActivityType Event = new WFActivityType(EVENT);
	/** String representation of 'event' */
	
	public static final String _Event = "Event";
	
	/** Index of value 'tool' */
	public static final int TOOL = 7;	
	/**
	 * 
	 */
	public static final WFActivityType Tool = new WFActivityType(TOOL);
	/** String representation of 'tool' */
	
	public static final String _Tool = "Tool";
	
	
	public static final WFActivityType NO_VALUE = new WFActivityType();
	public static final int NOT_INITIALIZED = 0;
	public static final int MAXIMUM = 7;
	
	private int value;
	private static final WFActivityType[] INSTANCES = {
		NO_VALUE,		
		Plain,		
		Join,		
		Split,		
		Loop,		
		Other,		
		Event,		
		Tool
	};

	private static final String[] VALUES = {
		"?",
    		_Plain,
    		_Join,
    		_Split,
    		_Loop,
    		_Other,
    		_Event,
    		_Tool
	};

	/**
	 * Creates an uninitialized enumeration of WFActivityType
	 */
	private WFActivityType() {
		this( NOT_INITIALIZED );
	}

	/**
	 * Constructor with predefined value
	 * @param pValue index of instance (equal to INSTANCES[ pValue ])
	 */
	private WFActivityType(int pValue) {
		if (pValue < 0 || pValue > MAXIMUM) {
			throw new IllegalArgumentException("Value"+pValue+" is not allowed for enumeration WFActivityType, valid range is 0.."+MAXIMUM);
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
	 * String representation of enumeration 'WFActivityType'
	 * @return 'WFActivityType: <value as string>'
	 */
	public String toString() {
		return "WFActivityType:" + getValue();
	}

	/**
     	 * Returns the verbose value of enumeration WFActivityType
     	 * @return String value of current WFActivityType instance
     	 */
	public String getValue() {
		if (value > 0 && value <= MAXIMUM) {
			return VALUES[ value ];
		} else {
			throw new IllegalStateException("Value "+
				value+" is not allowed for enumeration WFActivityType, valid range is 0.."+MAXIMUM);
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
	public static WFActivityType[] getInstances() {
		return INSTANCES;
	}
	
	/**
	 * Gets the corresponding instance of pValue. If pValue is invalid, an
	 * IllegalArgumentException is thrown.
	 * @return corresponding instance (equal to INSTANCES[ pValue ])
	 */
	public static WFActivityType getInstance(int pValue) {
		if (pValue > 0 && pValue <= MAXIMUM) {
			return INSTANCES[ pValue ];
		} else {
			throw new IllegalArgumentException("Value "+
				pValue+" is not allowed for enumeration WFActivityType, valid range is 0.."+MAXIMUM);
		}
	}
	
	/*
	 * Compare this instance with another object
	 * @param pObject to compare with
	 * @return true, if 1) Object is of type WFActivityType and 2) values are equal
	 */
	public boolean equals(Object pObject) {
		return pObject != null &&
		   getClass().getName().equals( pObject.getClass().getName() ) ?
		       value == ( (WFActivityType) pObject ).value:
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
		       value - ( (WFActivityType) pObject ).value:
		       -1;
	}
}





