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
package org.ofbiz.shark.requester;

import java.sql.Timestamp;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;

import org.enhydra.shark.api.client.wfmodel.WfEventAudit;
import org.enhydra.shark.api.client.wfmodel.InvalidPerformer;
import org.enhydra.shark.api.client.wfbase.BaseException;
import org.enhydra.shark.api.SharkTransaction;

/**
 * OFBiz -> Shark Logging Requester
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      3.1
 */
public class LoggingRequester extends AbstractRequester {

    public static final String module = LoggingRequester.class.getName();

    /// --------------------
    // factory constructors
    // --------------------

    // new requester
    public LoggingRequester() {
        this(RequesterFactory.getNextId(), UtilDateTime.nowTimestamp());
    }

    // restore requester
    public LoggingRequester(String requesterId, Timestamp fromDate) {
        super(requesterId, fromDate);
    }

    // -------------------
    // WfRequester methods
    // -------------------

    public void receive_event(WfEventAudit event) throws BaseException, InvalidPerformer {
        Debug.log("Received event - " + event.event_type(), module);
    }

    public void receive_event(SharkTransaction sharkTransaction, WfEventAudit event) throws BaseException, InvalidPerformer {
        Debug.log("Received event - " + event.event_type(), module);
    }

    // --------------------------
    // PersistedRequester methods
    // --------------------------

    public String getClassName() {
        return this.getClass().getName();
    }

    public String getDataString() throws PersistentRequesterException {
        return null; // no data
    }

    public void restoreData(String data) throws PersistentRequesterException {
        // nothing to restore
    }
}
