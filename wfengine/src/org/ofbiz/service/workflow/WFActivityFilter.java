/* $Id$
 * @(#)WFActivityFilter.java   Fri Aug 17 12:18:07 GMT+02:00 2001
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
 * (#)WFActivityFilter.java 
 */
package org.ofbiz.service.workflow;





import java.util.Collection;
import java.io.Serializable;

/**
 * Mit WFActivityFilter k�nnen die Eintr�ge in der Worklist eingeschr�nkt werden
 * @author Oliver Wieland
 * @version 1.0
 */
 
public interface WFActivityFilter  extends Serializable  {
	// Methoden

	/**	 
	 * Wenn ein Eintrag in der Worklist erschienen soll, mu� die Methode true zur�ckliefern, sonst false.

Diese Methode mu� von einem konkreten Filter �berschreiben werden
	 * @param pActivity Wert f�r Activity
	 * @return boolean
	 */
	public boolean accept(WFActivity pActivity) ;


}