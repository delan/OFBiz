/* $Id$
 * @(#)WFActivity.java   Fri Aug 17 12:18:07 GMT+02:00 2001
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
 * @created Fri Aug 17 12:18:07 GMT+02:00 2001
 * @version 1.0
 */

/*
 * $Log$
 */
/*
 * (#)WFActivity.java 
 */
package org.ofbiz.service.workflow;





import java.util.Collection;
import java.io.Serializable;

/**
 * Proxy object for an activity.
 * @author Oliver Wieland
 * @version 1.0
 */
 
public interface WFActivity  extends Serializable  {
	// Methoden

	/**	 
	 * Liefert die eindeutige Identifikationsnummer der Aktivität
	 * @return String
	 */
	public String getId() ;

	/**	 
	 * Liefert den namen der Aktivität
	 * @return String
	 */
	public String getName() ;

	/**	 
	 * Liefert den Auslöser (Ressource,)  der den Prozess gestartet hat
	 * @return String
	 */
	public String getInitiator() ;

	/**	 
	 * Liefertt den Kontext der aktuellen Aktivität
	 * @return WFContext
	 */
	public WFContext getContext() ;

	/**	 
	 * Liefert den Status der Aktivität
	 * @return WFState
	 */
	public WFState getState() ;

	/**	 
	 * Liefert den Zeitstempel der letzten Änderung
	 * @return long
	 */
	public long getLastModified() ;

	/**	 
	 * Liefert den Erzeugungszeitpunkt
	 * @return long
	 */
	public long getCreationTime() ;

	/**	 
	 * Methode getCompletionTime
	 * @return long
	 */
	public long getCompletionTime() ;

	/**	 
	 * Methode getStartTime
	 * @return long
	 */
	public long getStartTime() ;

	/**	 
	 * Methode getDuration
	 * @return long
	 */
	public long getDuration() ;

	/**	 
	 * Methode getDurationUnit
	 * @return WFDurationUnit
	 */
	public WFDurationUnit getDurationUnit() ;


}