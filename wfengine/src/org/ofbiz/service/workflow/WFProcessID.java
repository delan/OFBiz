/* $Id$
 * @(#)WFProcessID.java   Fri Aug 17 12:18:06 GMT+02:00 2001
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
 * @created Fri Aug 17 12:18:06 GMT+02:00 2001
 * @version 1.0
 */

/*
 * $Log$
 */
/*
 * (#)WFProcessID.java 
 */
package org.ofbiz.service.workflow;





import java.util.Collection;
import java.io.Serializable;

/**
 * Sessionobjekt f�r einen Prozess
 * @author Oliver Wieland
 * @version 1.0
 */
 
public interface WFProcessID  extends Serializable  {
	// Methoden

	/**	 
	 * Liefert die eindeutige ID des Prozesses
	 * @return long
	 */
	public long getId() ;

	/**	 
	 * Pr�ft, ob das Handle noch g�ltig ist. Wenn nicht, deutet dies entweder auf Zeit�berschreitung oder auf einen bereits beendeten Prozess hin.
	 * @return boolean
	 */
	public boolean isValid() ;


}