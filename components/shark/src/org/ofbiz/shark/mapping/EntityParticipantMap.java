/*
 * $Id: EntityParticipantMap.java,v 1.2 2004/07/03 19:54:26 jonesde Exp $
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

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.shark.container.SharkContainer;

import org.enhydra.shark.api.internal.mappersistence.ParticipantMap;
import org.enhydra.shark.api.RootException;

/**
 * Shark Participant Map Implementation
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.2 $
 * @since      3.1
 */
public class EntityParticipantMap implements ParticipantMap {

    protected GenericDelegator delegator = null;
    protected GenericValue participant = null;
    protected boolean newValue = false;

    protected EntityParticipantMap() {}

    protected EntityParticipantMap(GenericDelegator delegator, String packageId, String processDefId, String participantId) throws RootException {
        this.delegator = delegator;
        try {
            this.participant = delegator.findByPrimaryKey("WfParticipantMap", UtilMisc.toMap("packageId", packageId, "processDefId", processDefId, "participantId", participantId));
        } catch (GenericEntityException e) {
            throw new RootException(e);
        }
    }

    protected EntityParticipantMap(GenericValue application) {
        this.participant = application;
        this.delegator = application.getDelegator();
    }

    public EntityParticipantMap(GenericDelegator delegator) {
        this.newValue = true;
        this.delegator = delegator;

        this.participant = delegator.makeValue("WfParticipantMap", UtilMisc.toMap("participantMapId", delegator.getNextSeqId("WfParticipantMap")));
    }

    public static EntityParticipantMap getInstance(GenericValue participant) throws RootException {
        EntityParticipantMap part = new EntityParticipantMap(participant);
        if (part.isLoaded()) {
            return part;
        }
        return null;
    }

    public static EntityParticipantMap getInstance(String packageId, String processDefId, String participantId) throws RootException {
        EntityParticipantMap part = new EntityParticipantMap(SharkContainer.getDelegator(), packageId, processDefId, participantId);
        if (part.isLoaded()) {
            return part;
        }
        return null;
    }

    public boolean isLoaded() {
        if (participant == null) {
            return false;
        }
        return true;
    }

    public void setParticipantId(String participantId) {
        participant.set("participantId", participantId);
    }

    public String getParticipantId() {
        return participant.getString("participantId");
    }

    public void setPackageId(String packageId) {
        participant.set("packageId", packageId);
    }

    public String getPackageId() {
        return participant.getString("participantId");
    }

    public void setProcessDefinitionId(String processDefId) {
        participant.set("processDefId", processDefId);
    }

    public String getProcessDefinitionId() {
        return participant.getString("processDefId");
    }

    public void setUsername(String userName) {
        participant.set("userName", userName);
    }

    public String getUsername() {
        return participant.getString("userName");
    }

    public boolean getIsGroupUser() {
        return (participant.getBoolean("isGroupUser") != null ? participant.getBoolean("isGroupUser").booleanValue() : false);
    }

    public void setIsGroupUser(boolean isGroupUser) {
        participant.set("isGroupUser", new Boolean(isGroupUser));
    }

    public void store() throws RootException {
        if (newValue) {
            try {
                delegator.create(participant);
                newValue = false;
            } catch (GenericEntityException e) {
                throw new RootException(e);
            }
        } else {
            try {
                delegator.store(participant);
            } catch (GenericEntityException e) {
                throw new RootException(e);
            }
        }
    }

    public void reload() throws RootException {
        if (!newValue) {
            try {
                delegator.refresh(participant);
            } catch (GenericEntityException e) {
                throw new RootException(e);
            }
        }
    }

    public void remove() throws RootException {
        if (!newValue) {
            try {
                delegator.removeValue(participant);
            } catch (GenericEntityException e) {
                throw new RootException(e);
            }
        }
    }
}
