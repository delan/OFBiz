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
package org.ofbiz.pos.adaptor;

import java.util.Map;
import java.sql.Timestamp;

import org.ofbiz.service.GenericServiceCallback;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.pos.screen.PosScreen;
import org.ofbiz.pos.event.SecurityEvents;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.3
 */
public class SyncCallbackAdaptor implements GenericServiceCallback {

    public static final String module = SyncCallbackAdaptor.class.getName();

    protected PosScreen screen = null;
    protected Timestamp txStamp = null;
    protected String entitySyncId = null;
    protected boolean enabled = true;

    public SyncCallbackAdaptor(PosScreen pos, String entitySyncId, Timestamp txStamp) {
        this.screen = pos;
        this.entitySyncId = entitySyncId;
        this.txStamp = txStamp;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected void internalReceiveEvent(Map context, Object obj) {
        String ctxSyncId = (String) context.get("entitySyncId");
        if (ctxSyncId != null && entitySyncId.equals(ctxSyncId)) {
            GenericValue entitySync = null;
            try {
                entitySync = screen.getSession().getDelegator().findByPrimaryKey("EntitySync", UtilMisc.toMap("entitySyncId", entitySyncId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
            if (entitySync != null) {
                Timestamp lastSync = entitySync.getTimestamp("lastSuccessfulSynchTime");
                if (lastSync.after(txStamp)) {
                    this.setEnabled(false);
                    screen.showDialog("dialog/error/terminalclosed");
                    screen.refresh();
                    SecurityEvents.logout(screen);
                }
            }
        }
    }

    public void receiveEvent(Map context) {
        this.internalReceiveEvent(context, null);
    }

    public void receiveEvent(Map context, Map result) {
        this.internalReceiveEvent(context, result);
    }

    public void receiveEvent(Map context, Throwable error) {
        this.internalReceiveEvent(context, error);
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
