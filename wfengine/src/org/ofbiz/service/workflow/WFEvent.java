/* $Id$
 * @(#)WFEvent.java   Fri Aug 17 12:18:07 GMT+02:00 2001
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
 * (#)WFEvent.java 
 */
package org.ofbiz.service.workflow;





import java.util.Collection;
import java.io.Serializable;

/**
 * Interface for all events of the workflow engine
 * @author Oliver Wieland
 * @version 1.0
 */
 
public interface WFEvent  extends Serializable  {
	// Methoden

	/**	 
	 * Liefert die Ereignisquelle. Wenn null, ist die Workflowengine selbst die Quelle (Systemmeldung)
	 * @return WFActivity
	 */
	public WFActivity getSource() ;

	/**	 
	 * Liefert den Nachrichtentext des Ereignisses
	 * @return String
	 */
	public String getMessage() ;

	/**	 
	 * Liefert den Ereignistyp
	 * @return WFEventType
	 */
	public WFEventType getType() ;


}