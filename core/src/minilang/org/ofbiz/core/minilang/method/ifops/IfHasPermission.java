/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.minilang.method.ifops;

import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;

/**
 * Iff the user has the specified permission, process the sub-operations. Otherwise
 * process else operations if specified.
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    February 19, 2002
 *@version    1.0
 */
public class IfHasPermission extends MethodOperation {
    
    List subOps = new LinkedList();
    List elseSubOps = null;
    
    String permission;
    String action;

    public IfHasPermission(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        this.permission = element.getAttribute("permission");
        this.action = element.getAttribute("action");
        
        SimpleMethod.readOperations(element, subOps, simpleMethod);
        
        Element elseElement = UtilXml.firstChildElement(element, "else");
        if (elseElement != null) {
            elseSubOps = new LinkedList();
            SimpleMethod.readOperations(elseElement, elseSubOps, simpleMethod);
        }
    }

    public boolean exec(MethodContext methodContext) {
        //if conditions fails, always return true; if a sub-op returns false 
        // return false and stop, otherwise return true
        //return true;
        
        //only run subOps if element is empty/null
        boolean runSubOps = false;
        
        //if no user is logged in, treat as if the user does not have permission: do not run subops
        GenericValue userLogin = methodContext.getUserLogin();
        if (userLogin != null) {
            Security security = methodContext.getSecurity();
            if (action != null && action.length() > 0) {
                //run hasEntityPermission
                if (security.hasEntityPermission(permission, action, userLogin)) {
                    runSubOps = true;
                }
            } else {
                //run hasPermission
                if (security.hasPermission(permission, userLogin)) {
                    runSubOps = true;
                }
            }
        }

        if (runSubOps) {
            return SimpleMethod.runSubOps(subOps, methodContext);
        } else {
            if (elseSubOps != null) {
                return SimpleMethod.runSubOps(elseSubOps, methodContext);
            } else {
                return true;
            }
        }
    }
}
