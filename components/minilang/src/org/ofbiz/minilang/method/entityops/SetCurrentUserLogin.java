/*
 * $Id: SetCurrentUserLogin.java,v 1.1 2003/08/17 06:06:11 ajzeneski Exp $
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
package org.ofbiz.minilang.method.entityops;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.minilang.SimpleMethod;
import org.ofbiz.minilang.method.ContextAccessor;
import org.ofbiz.minilang.method.MethodContext;
import org.ofbiz.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Uses the delegator to create the specified value object entity in the datasource
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.1 $
 * @since      2.0
 */
public class SetCurrentUserLogin extends MethodOperation {
    
    public static final String module = SetCurrentUserLogin.class.getName();
    
    ContextAccessor valueAcsr;

    public SetCurrentUserLogin(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        valueAcsr = new ContextAccessor(element.getAttribute("value-name"));
    }

    public boolean exec(MethodContext methodContext) {
        GenericValue userLogin = (GenericValue) valueAcsr.get(methodContext);
        if (userLogin == null) {
            Debug.logWarning("In SetCurrentUserLogin a value was not found with the specified valueName: " + valueAcsr + ", not setting", module);
            return true;
        }

        methodContext.setUserLogin(userLogin, this.simpleMethod.getUserLoginEnvName());
        return true;
    }
}
