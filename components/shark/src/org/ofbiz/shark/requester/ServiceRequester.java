/*
 * $Id: ServiceRequester.java,v 1.1 2004/04/22 15:41:08 ajzeneski Exp $
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
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.shark.container.SharkContainer;
import org.ofbiz.entity.serialize.XmlSerializer;
import org.ofbiz.entity.serialize.SerializeException;

import org.enhydra.shark.api.client.wfmodel.WfEventAudit;
import org.enhydra.shark.api.client.wfmodel.InvalidPerformer;
import org.enhydra.shark.api.client.wfmodel.SourceNotAvailable;
import org.enhydra.shark.api.client.wfbase.BaseException;
import org.enhydra.shark.api.SharkTransaction;
import org.xml.sax.SAXException;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class ServiceRequester extends AbstractRequester {

    public static final String module = ServiceRequester.class.getName();
    public static final int ASYNC = 0;
    public static final int SYNC = 1;

    protected Map serviceContext = new HashMap();

    // --------------------
    // factory constructors
    // --------------------

    // new requester
    public ServiceRequester() {
        this(RequesterFactory.getNextId(), UtilDateTime.nowTimestamp());
    }

    // restore requester
    public ServiceRequester(String requesterId, Timestamp fromDate) {
        super(requesterId, fromDate);
    }

    // -------------------
    // WfRequester methods
    // -------------------

    public void receive_event(WfEventAudit event) throws BaseException, InvalidPerformer {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receive_event(SharkTransaction trans, WfEventAudit event) throws BaseException, InvalidPerformer {
        receive_event(event);
    }

    // --------------------------
    // PersistedRequester methods
    // --------------------------

    public String getClassName() {
        return this.getClass().getName();
    }

    public String getDataString() throws PersistentRequesterException {
        String xmlData = null;
        try {
            xmlData = XmlSerializer.serialize(serviceContext);
        } catch (SerializeException e) {
            throw new PersistentRequesterException(e);
        } catch (IOException e) {
            throw new PersistentRequesterException(e);
        }
        return xmlData;
    }

    public void restoreData(String data) throws PersistentRequesterException {
        Map xmlData = null;
        try {
            xmlData = (Map) XmlSerializer.deserialize(data, SharkContainer.getDelegator());
        } catch (SerializeException e) {
            throw new PersistentRequesterException(e);
        } catch (SAXException e) {
            throw new PersistentRequesterException(e);
        } catch (ParserConfigurationException e) {
            throw new PersistentRequesterException(e);
        } catch (IOException e) {
            throw new PersistentRequesterException(e);
        }
    }

    // -------------
    // local methods
    // -------------


    public void setService(String serviceName, int serviceMode) {
        serviceContext.put("_service_name", serviceName);
        serviceContext.put("_service_mode", new Integer(serviceMode));
    }

    public void setService(String serviceName) {
        this.setService(serviceName, ServiceRequester.ASYNC);
    }

    public String getServiceName() {
        return (String) serviceContext.get("_service_name");
    }

    public int getServiceMode() {
        return ((Integer) serviceContext.get("_service_mode")).intValue();
    }

    public void setInitalContextValues(Map initialContext) {
        serviceContext.putAll(initialContext);
    }

    private void run(WfEventAudit event) throws GenericServiceException {
        // get the dispatcher
        LocalDispatcher dispatcher = SharkContainer.getDispatcher();
        if (dispatcher == null) {
            throw new GenericServiceException("Cannot run service with null dispatcher");
        }

        // get the service context
        Map serviceContext = makeServiceContext(event, dispatcher);

        // invoke the service
        String serviceName = getServiceName();
        if (serviceName != null) {
            int mode = getServiceMode();
            if (mode == ServiceRequester.SYNC) {
                dispatcher.runSyncIgnore(serviceName, serviceContext);
            } else {
                dispatcher.runAsync(serviceName, serviceContext);
            }
        }
    }

    private Map makeServiceContext(WfEventAudit event, LocalDispatcher dispatcher) throws GenericServiceException {
        DispatchContext dctx = dispatcher.getDispatchContext();
        try {
            return dctx.getModelService(this.getServiceName()).makeValid(getWRD(event), ModelService.IN_PARAM);
        } catch (BaseException e) {
            throw new GenericServiceException(e);
        }
    }

    private Map getWRD(WfEventAudit event) throws BaseException {
        Map wrdMap = new HashMap();

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
}
