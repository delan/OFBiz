/*
 * $Id$
 *
 * Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.shark.audit;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.3
 */

public abstract class AuditEntityObject {

    protected transient GenericDelegator delegator = null;
    protected EntityAuditMgr mgr = null;
    protected String delegatorName = null;

    public AuditEntityObject(EntityAuditMgr mgr, GenericDelegator delegator) {
        this.delegatorName = delegator.getDelegatorName();
        this.delegator = delegator;
        this.mgr = mgr;
    }

    public EntityAuditMgr getAuditManager() {
        return this.mgr;
    }

    public GenericDelegator getGenericDelegator() {
        if (this.delegator == null && delegatorName != null) {
            this.delegator = GenericDelegator.getGenericDelegator(delegatorName);
        }
        return this.delegator;
    }

    public abstract void store() throws GenericEntityException;

    public abstract void reload() throws GenericEntityException;

    public abstract void remove() throws GenericEntityException;
}
