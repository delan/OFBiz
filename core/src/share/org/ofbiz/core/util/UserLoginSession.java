/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.core.util;

import java.util.*;
import javax.servlet.http.*;

/**
 * UserLoginSession - Helper methods
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class UserLoginSession {
	
	/**
	 * Gets an attribute from the session or null if attribute name is not found.	 * @param name The name of the attribute to look up	 * @param session The HttpSession object	 * @return Object The value of the named object	 */
	public static Object getAttribute(String name, HttpSession session) {
		Map uls = getSession(session);
		return uls.get(name);
	}
	
	/**
	 * Sets an attribute in the UserLoginSession	 * @param name The name of the attribute to set	 * @param value The value of the attribute	 * @param session The HttpSession object	 */
	public static void setAttribute(String name, Object value, HttpSession session) {
		Map uls = getSession(session);
		uls.put(name, value);
	}
			
	/**
	 * Gets the UserLoginSession Map	 * @param session The HttpSession object	 * @return Map UserLoginSession Map	 */
	public static Map getSession(HttpSession session) {
		return (Map) session.getAttribute("userLoginSession");
	}
		

}
