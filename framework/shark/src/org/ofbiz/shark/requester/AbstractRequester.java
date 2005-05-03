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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.shark.container.SharkContainer;

import org.enhydra.shark.api.client.wfmodel.WfProcessIterator;
import org.enhydra.shark.api.client.wfmodel.WfProcess;
import org.enhydra.shark.api.client.wfmodel.WfEventAudit;
import org.enhydra.shark.api.client.wfmodel.InvalidPerformer;
import org.enhydra.shark.api.client.wfmodel.WfRequester;
import org.enhydra.shark.api.client.wfmodel.SourceNotAvailable;
import org.enhydra.shark.api.client.wfbase.BaseException;
import org.enhydra.shark.api.client.wfservice.AdminInterface;
import org.enhydra.shark.api.client.wfservice.ExecutionAdministration;
import org.enhydra.shark.api.client.wfservice.ConnectFailed;
import org.enhydra.shark.api.client.wfservice.NotConnected;
import org.enhydra.shark.api.SharkTransaction;
import org.enhydra.shark.WfProcessIteratorWrapper;

/**
 * Shark Workflow Abstract Requester
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.1
 */
public abstract class AbstractRequester implements WfRequester, Serializable {

    public static final String module = LoggingRequester.class.getName();
    protected transient GenericDelegator delegator = null;
    protected transient GenericValue userLogin = null;
    protected String delegatorName = null;
    protected String userLoginId = null;
    protected List performerIds = new ArrayList();

    public AbstractRequester(GenericValue userLogin) {
        this.delegator = userLogin.getDelegator();
        this.userLogin = userLogin;

        this.delegatorName = delegator.getDelegatorName();
        this.userLoginId = userLogin.getString("userLoginId");
    }

    public void addPerformer(WfProcess process) throws BaseException {
        performerIds.add(process.key());
    }

    public int how_many_performer() throws BaseException {
        return performerIds.size();
    }

    public int how_many_performer(SharkTransaction trans) throws BaseException {
        return performerIds.size();
    }

    public WfProcessIterator get_iterator_performer() throws BaseException {
        return new WfProcessIteratorImpl(this.getPerformers());
    }

    public WfProcessIterator get_iterator_performer(SharkTransaction trans) throws BaseException {
        return new WfProcessIteratorImpl(trans, this.getPerformers());
    }

    public WfProcess[] get_sequence_performer(int i) throws BaseException {
        if (i > how_many_performer()) {
            i = how_many_performer();
        }
        return (WfProcess[]) this.getPerformers().subList(0, i).toArray();
    }

    public WfProcess[] get_sequence_performer(SharkTransaction trans, int i) throws BaseException {
        if (i > how_many_performer()) {
            i = how_many_performer();
        }
        return (WfProcess[]) this.getPerformers().subList(0, i).toArray();
    }

    public boolean is_member_of_performer(WfProcess process) throws BaseException {
        return performerIds.contains(process.key());
    }

    public boolean is_member_of_performer(SharkTransaction trans, WfProcess process) throws BaseException {
        return performerIds.contains(process.key());
    }

    public abstract void receive_event(WfEventAudit event) throws BaseException, InvalidPerformer;

    public abstract void receive_event(SharkTransaction trans, WfEventAudit event) throws BaseException, InvalidPerformer;

    protected Map getWRD(WfEventAudit event, Map initialContext) throws BaseException {
        Map wrdMap = new HashMap();

        // set the initial context (overrided by any new data)
        if (initialContext != null) {
            wrdMap.putAll(initialContext);
        }

        // these are static values available to the service
        wrdMap.put("eventType", event.event_type());
        wrdMap.put("activityId", event.activity_key());
        wrdMap.put("activityName", event.activity_name());
        wrdMap.put("processId", event.process_key());
        wrdMap.put("processName", event.process_name());
        wrdMap.put("processMgrName", event.process_mgr_name());
        wrdMap.put("processMgrVersion", event.process_mgr_version());
        wrdMap.put("eventTime", event.time_stamp().getTimestamp());

        // all WRD is also available to the service, but what this contains is not known
        try {
            Map wrd = new HashMap(event.source().process_context());
            if (wrd != null) {
                wrdMap.put("_WRDMap", wrd);
                wrdMap.putAll(wrd);
            }
        } catch (SourceNotAvailable e) {
            Debug.logError(e, "No WRD available since event.source() cannot be obtained", module);
        }

        return wrdMap;
    }

    protected synchronized GenericDelegator getDelegator() {
        if (this.delegator == null && this.delegatorName != null) {
            this.delegator = GenericDelegator.getGenericDelegator(this.delegatorName);
        }
        return this.delegator;
    }

    protected synchronized GenericValue getUserLogin() throws GenericEntityException {
        if (userLogin == null && this.userLoginId != null) {
            GenericDelegator delegator = this.getDelegator();
            if (delegator != null) {
                this.userLogin = delegator.findByPrimaryKey("UserLogin",
                        UtilMisc.toMap("userLoginId", this.userLoginId));
            }
        }
        return this.userLogin;
    }

    protected List getPerformers() {
        GenericValue userLogin = null;
        List performers = null;
        try {
            userLogin = this.getUserLogin();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        if (userLogin != null) {
            AdminInterface admin = SharkContainer.getAdminInterface();
            ExecutionAdministration exAdmin = admin.getExecutionAdministration();
            boolean connected = true;
            try {
                exAdmin.connect(userLogin.getString("userLoginId"), userLogin.getString("currentPassword"), null, null);
            } catch (BaseException e) {
                Debug.logError(e, module);
                connected = false;
            } catch (ConnectFailed e) {
                Debug.logError(e, module);
                connected = false;
            }

            if (connected) {
                performers = new ArrayList(performerIds.size());
                Iterator i = performerIds.iterator();
                try {
                    while (i.hasNext()) {
                        String processId = (String) i.next();
                        exAdmin.getProcess(processId);
                    }
                } catch (Exception e) {
                    Debug.logError(e, module);
                    performers = null;
                } finally {
                    try {
                        exAdmin.disconnect();
                    } catch (BaseException e) {
                        Debug.logError(e, module);
                    } catch (NotConnected e) {
                        Debug.logError(e, module);
                    }
                }
            }
        }
        return performers;
    }

    protected class WfProcessIteratorImpl extends WfProcessIteratorWrapper implements Serializable {

        public WfProcessIteratorImpl(SharkTransaction trans, List performers) throws BaseException {
            super(trans, null, performers);
        }

        public WfProcessIteratorImpl(List performers) throws BaseException {
            super(null, null, performers);
        }
    }
}
