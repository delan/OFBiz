/*
 * $Id$
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
package org.ofbiz.shark.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.shark.container.SharkContainer;
import org.ofbiz.shark.transaction.JtaTransaction;

import org.enhydra.shark.api.ApplicationMappingTransaction;
import org.enhydra.shark.api.RootException;
import org.enhydra.shark.api.TransactionException;
import org.enhydra.shark.api.internal.appmappersistence.ApplicationMap;
import org.enhydra.shark.api.internal.appmappersistence.ApplicationMappingManager;
import org.enhydra.shark.api.internal.working.CallbackUtilities;

/**
 * Shark Application Mappings Implementation
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      3.1
 */
public class EntityApplicationMappingMgr implements ApplicationMappingManager {

    public static final String module = EntityApplicationMappingMgr.class.getName();
    protected CallbackUtilities callBack = null;

    public void configure(CallbackUtilities callbackUtilities) throws RootException {
        this.callBack = callbackUtilities;
    }

    public boolean saveApplicationMapping(ApplicationMappingTransaction mappingTransaction, ApplicationMap applicationMap) throws RootException {
        ((EntityApplicationMap) applicationMap).store();
        return true;
    }

    public boolean deleteApplicationMapping(ApplicationMappingTransaction mappingTransaction, ApplicationMap applicationMap) throws RootException {
        ((EntityApplicationMap) applicationMap).remove();
        return true;
    }

    public boolean updateApplicationMapping(ApplicationMappingTransaction mappingTransaction, ApplicationMap applicationMap) throws RootException {
        return saveApplicationMapping(mappingTransaction, applicationMap);
    }

    public List getAllApplicationMappings(ApplicationMappingTransaction mappingTransaction) throws RootException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        List lookupList = null;
        try {
            lookupList = delegator.findAll("WfApplicationMap");
        } catch (GenericEntityException e) {
            throw new RootException(e);
        }
        if (lookupList != null) {
            List compiledList = new ArrayList();
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                compiledList.add(EntityApplicationMap.getInstance(v));
            }
            return compiledList;
        } else {
            return new ArrayList();
        }
    }

    public ApplicationMap createApplicationMap() {
        return new EntityApplicationMap(SharkContainer.getDelegator());
    }

    public boolean deleteApplicationMapping(ApplicationMappingTransaction mappingTransaction, String packageId, String processDefId, String appDefId) throws RootException {
        EntityApplicationMap app = (EntityApplicationMap) this.getApplicationMap(mappingTransaction, packageId, processDefId, appDefId);
        if (app != null && app.isLoaded()) {
            app.remove();
            return true;
        } else {
            return false;
        }
    }

    public ApplicationMap getApplicationMap(ApplicationMappingTransaction mappingTransaction, String packageId, String processDefId, String appDefId) throws RootException {
        return EntityApplicationMap.getInstance(packageId, processDefId, appDefId);
    }

    public ApplicationMappingTransaction getApplicationMappingTransaction() throws TransactionException {
        return new JtaTransaction();
    }
}
