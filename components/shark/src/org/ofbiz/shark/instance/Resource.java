/*
 * $Id: Resource.java,v 1.1 2004/04/22 15:41:03 ajzeneski Exp $
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.shark.instance;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.shark.container.SharkContainer;

import org.enhydra.shark.api.internal.instancepersistence.*;

/**
 * Persistance Object
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class Resource implements ResourcePersistenceInterface {

    public static final String module = Resource.class.getName();

    protected GenericDelegator delegator = null;
    protected GenericValue resource = null;
    protected boolean newValue = false;

    protected Resource() {}

    protected Resource(GenericDelegator delegator, String name) throws PersistenceException {
        this.delegator = delegator;
        if (this.delegator != null) {
            try {
                this.resource = delegator.findByPrimaryKey("WfResource", UtilMisc.toMap("userName", name));
            } catch (GenericEntityException e) {
                throw new PersistenceException(e);
            }
        } else {
            Debug.logError("Invalid delegator object passed", module);
        }
    }

    protected Resource(GenericValue resource) {
        this.resource = resource;
        this.delegator = resource.getDelegator();
    }

    public Resource(GenericDelegator delegator) {
        this.newValue = true;
        this.delegator = delegator;
        this.resource = delegator.makeValue("WfResource", null);
    }

    public static Resource getInstance(GenericValue resource) throws PersistenceException {
        Resource res = new Resource(resource);
        if (res.isLoaded()) {
            return res;
        }
        return null;
    }

    public static Resource getInstance(String name) throws PersistenceException {
        Resource res = new Resource(SharkContainer.getDelegator(), name);
        if (res.isLoaded()) {
            return res;
        }
        return null;
    }

    public boolean isLoaded() {
        if (resource == null) {
            return false;
        }
        return true;
    }

    public void setUsername(String s) {
        resource.set("userName", s);
    }

    public String getUsername() {
        return resource.getString("userName");
    }

    public void setName(String s) {
        resource.set("resourceName", s);
    }

    public String getName() {
        return resource.getString("resourceName");
    }

    public void store() throws GenericEntityException {
        if (newValue) {
            delegator.createOrStore(resource);
            newValue = false;
        } else {
            delegator.store(resource);
        }
    }

    public void reload() throws GenericEntityException {
        if (!newValue) {
            resource.refresh();
        }
    }

    public void remove() throws GenericEntityException {
        if (!newValue) {
            delegator.removeValue(resource);
            Debug.log("**** REMOVED : " + this, module);
        }
    }
}
