/* $Id$
 * @(#)WFLogger.java   Fri Aug 17 12:18:05 GMT+02:00 2001
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
 * Revision 1.2  2001/08/10 11:16:41  owieland
 * Change comments DE -> US
 *
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
 * Class to encapsulate logging
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFLogger implements Serializable  {

	
	// Attribute instance 'level'
	private static WFMessageType level;
	
	
		
	/**
	 * Empty constructor
	 */
	WFLogger() {
			}

	/**
	 * Constructor with all attributes 
	 * @param pLevel Initial value for attribute 'level'
	 */
	WFLogger(
		WFMessageType pLevel) {		
				
		level = pLevel;
	}
		
	/**
	 * Getter for attribute 'level'
	 * Threshold for output messages. With 
	 * @return Value of attribute level
	 */
	public WFMessageType getLevel()  {
		return level;
	}
	
	/**
	 * Setter for attribute 'level'
	 * Threshold for output messages. With 
	 * @param pLevel new value for attribute level
	 */
	public static void setLevel(WFMessageType pLevel)  {
		if (level == pLevel) return;		
		if ( !notifyAttributeChangeLevel( pLevel ) ) return;
		level = pLevel;
	}
	
	/**
	 * This method is called, before the attribute 'Level' is set to a new
	 * value.
	 * @param pLevel New Value for attribute 'Level'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private static boolean notifyAttributeChangeLevel(WFMessageType pLevel) {
		return true;
	}
	
	/**
	 * Allgemeine Meldung
	 * @param pMessage Message text
	 * @param pType Severity level
	 * @param pObject Object that emitted this message
	 */
	public static void log(String pMessage, WFMessageType pType, Object pObject) {
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method logDebug
	 * @param pSender Value for parameter Sender
	 * @param pMessage Value for parameter Message
	 */
	public static void logDebug(Object pSender, String pMessage) {
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method logInfo
	 * @param pSender Value for parameter Sender
	 * @param pMessage Value for parameter Message
	 */
	public static void logInfo(Object pSender, String pMessage) {
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method logWarning
	 * @param pSender Value for parameter Sender
	 * @param pMessage Value for parameter Message
	 */
	public static void logWarning(Object pSender, String pMessage) {
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method logError
	 * @param pSender Value for parameter Sender
	 * @param pMessage Value for parameter Message
	 */
	public static void logError(Object pSender, String pMessage) {
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method logFatal
	 * @param pSender Value for parameter Sender
	 * @param pMessage Value for parameter Message
	 */
	public static void logFatal(Object pSender, String pMessage) {
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Logs an exception with stacktrace
	 * @param pException Exception to log
	 */
	public static void logException(Throwable pException) {
		// !!NO_CODE!!
		/* NULL */;
	}

}








