/*
 * $Id: EntityParticipantMappingMgr.java,v 1.1 2004/07/11 23:26:28 ajzeneski Exp $
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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.shark.container.SharkContainer;
import org.ofbiz.shark.transaction.JtaTransaction;
import org.ofbiz.base.util.UtilMisc;

import org.enhydra.shark.api.internal.working.CallbackUtilities;
import org.enhydra.shark.api.internal.partmappersistence.ParticipantMap;
import org.enhydra.shark.api.internal.partmappersistence.ParticipantMappingManager;
import org.enhydra.shark.api.RootException;
import org.enhydra.shark.api.ParticipantMappingTransaction;
import org.enhydra.shark.api.TransactionException;

/**
 * Shark Participant Mappings Implementation
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class EntityParticipantMappingMgr implements ParticipantMappingManager {

    public static final String module = EntityParticipantMappingMgr.class.getName();

    protected CallbackUtilities callBack = null;

    public void configure(CallbackUtilities callbackUtilities) throws RootException {
        this.callBack = callbackUtilities;
    }

    public boolean saveParticipantMapping(ParticipantMappingTransaction mappingTransaction, ParticipantMap participantMap) throws RootException {
        ((EntityParticipantMap) participantMap).store();
        return true;
    }

    public boolean deleteParticipantMapping(ParticipantMappingTransaction mappingTransaction, ParticipantMap participantMap) throws RootException {
        if (!doesParticipantMappingExist(mappingTransaction, participantMap)) {
            return false;
        }
        ((EntityParticipantMap) participantMap).remove();
        return true;
    }

    public List getAllParticipantMappings(ParticipantMappingTransaction mappingTransaction) throws RootException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        List lookupList = null;
        try {
            lookupList = delegator.findAll("WfParticipantMap");
        } catch (GenericEntityException e) {
            throw new RootException(e);
        }
        if (lookupList != null) {
            List compiledList = new ArrayList();
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                compiledList.add(EntityParticipantMap.getInstance(v));
            }
            return compiledList;
        } else {
            return new ArrayList();
        }
    }

    public boolean doesParticipantMappingExist(ParticipantMappingTransaction mappingTransaction, ParticipantMap participantMap) throws RootException {
        List mappings = getParticipantMappings(mappingTransaction, participantMap.getPackageId(), participantMap.getProcessDefinitionId(), participantMap.getParticipantId());
        if (mappings != null && mappings.size() > 0) {
            return true;
        }
        return false;
    }

    public ParticipantMap createParticipantMap() {
        return new EntityParticipantMap(SharkContainer.getDelegator());
    }

    public List getParticipantMappings(ParticipantMappingTransaction mappingTransaction, String packageId, String processDefId, String participantId) throws RootException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfParticipantMap", UtilMisc.toMap("packageId", packageId, "processDefId", processDefId, "participantId", participantId));
        } catch (GenericEntityException e) {
            throw new RootException(e);
        }
        if (lookupList != null) {
            List compiledList = new ArrayList();
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                compiledList.add(EntityParticipantMap.getInstance(v));
            }
            return compiledList;
        } else {
            return new ArrayList();
        }
    }

    public List getParticipantMappings(ParticipantMappingTransaction mappingTransaction, String userName) throws RootException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfParticipantMap", UtilMisc.toMap("userName", userName));
        } catch (GenericEntityException e) {
            throw new RootException(e);
        }
        if (lookupList != null) {
            List compiledList = new ArrayList();
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                compiledList.add(EntityParticipantMap.getInstance(v));
            }
            return compiledList;
        } else {
            return new ArrayList();
        }
    }

    public boolean deleteParticipantMappings(ParticipantMappingTransaction mappingTransaction, String packageId, String processDefId, String participantId) throws RootException {
        List participants = this.getParticipantMappings(mappingTransaction, packageId, processDefId, participantId);
        if (participants != null) {
            Iterator i = participants.iterator();
            while (i.hasNext()) {
                EntityParticipantMap map = (EntityParticipantMap) i.next();
                map.remove();
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteParticipantMappings(ParticipantMappingTransaction mappingTransaction, String userName) throws RootException {
        List participants = this.getParticipantMappings(mappingTransaction, userName);
        if (participants != null) {
            Iterator i = participants.iterator();
            while (i.hasNext()) {
                EntityParticipantMap map = (EntityParticipantMap) i.next();
                map.remove();
            }
            return true;
        } else {
            return false;
        }
    }

    public List getUsernames(ParticipantMappingTransaction mappingTransaction, String packageId, String processDefId, String participantId) throws RootException {
        List participants = this.getParticipantMappings(mappingTransaction, packageId, processDefId, participantId);
        List compiledList = new ArrayList();
        if (participants != null) {
            Iterator i = participants.iterator();
            while (i.hasNext()) {
                EntityParticipantMap map = (EntityParticipantMap) i.next();
                compiledList.add(map.getUsername());
            }
        }
        return compiledList;
    }

    public ParticipantMappingTransaction getParticipantMappingTransaction() throws TransactionException {
        return new JtaTransaction();
    }
}
