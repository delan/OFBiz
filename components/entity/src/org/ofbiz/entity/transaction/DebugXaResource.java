/*
 * $Id: DebugXaResource.java,v 1.1 2004/05/25 06:19:14 ajzeneski Exp $
 *
 *  Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.entity.transaction;

import javax.transaction.xa.Xid;
import javax.transaction.xa.XAException;

import org.ofbiz.base.util.Debug;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class DebugXaResource extends GenericXaResource {

    public static final String module = DebugXaResource.class.getName();
    public Exception ex = null;

    public DebugXaResource() {
        this.ex = new Exception();
    }

    public void commit(Xid xid, boolean onePhase) throws XAException {
        TransactionUtil.debugResMap.remove(xid);
        if (Debug.verboseOn()) Debug.logVerbose("Xid : " + xid.toString() + " cleared [commit]", module);
    }

    public void rollback(Xid xid) throws XAException {
        TransactionUtil.debugResMap.remove(xid);
        if (Debug.verboseOn()) Debug.logVerbose("Xid : " + xid.toString() + " cleared [rollback]", module);
    }

    public void log() {
        Debug.log("Xid : " + xid, module);
        Debug.log(ex, module);
    }
}
