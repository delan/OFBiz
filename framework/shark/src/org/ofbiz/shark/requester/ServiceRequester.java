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

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.shark.container.SharkContainer;

import org.enhydra.shark.api.SharkTransaction;
import org.enhydra.shark.api.client.wfbase.BaseException;
import org.enhydra.shark.api.client.wfmodel.InvalidPerformer;
import org.enhydra.shark.api.client.wfmodel.SourceNotAvailable;
import org.enhydra.shark.api.client.wfmodel.WfEventAudit;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.1
 */
public class ServiceRequester extends AbstractRequester {

    public static final String module = ServiceRequester.class.getName();
    public static final int ASYNC = 0;
    public static final int SYNC = 1;

    protected Map initialContext = new HashMap();
    protected String serviceName = null;
    protected String eventType = null;
    protected int serviceMode = 1;

    // new requester
    public ServiceRequester(GenericValue userLogin, String eventType) {
        super(userLogin);
        this.setEventType(eventType);
    }

    public ServiceRequester(GenericValue userLogin) {
        super(userLogin);
    }

    // -------------------
    // WfRequester methods
    // -------------------

    public void receive_event(WfEventAudit event) throws BaseException, InvalidPerformer {
        if (this.getEventType() == null || this.getEventType().equals(event.event_type())) {
            try {
                this.run(event);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                throw new BaseException(e);
            }
        }
    }

    public void receive_event(SharkTransaction trans, WfEventAudit event) throws BaseException, InvalidPerformer {
        receive_event(event);
    }

    // -------------
    // local methods
    // -------------


    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventType() {
        return this.eventType;
    }

    public void setService(String serviceName, int serviceMode) {
        this.serviceName = serviceName;
        this.serviceMode = serviceMode;
    }

    public void setService(String serviceName) {
        this.setService(serviceName, ServiceRequester.ASYNC);
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public int getServiceMode() {
        return this.serviceMode;
    }

    public void setInitalContextValues(Map initialContext) {
        this.initialContext = new HashMap(initialContext);
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
        String serviceName = this.getServiceName();
        if (serviceName != null) {
            int mode = this.getServiceMode();
            if (mode == ServiceRequester.SYNC) {
                dispatcher.runSyncIgnore(serviceName, serviceContext);
            } else {
                dispatcher.runAsync(serviceName, serviceContext);
            }
        } else {
            Debug.logWarning("ServiceRequester -> receive_event() called with no service defined!", module);
        }
    }

    private Map makeServiceContext(WfEventAudit event, LocalDispatcher dispatcher) throws GenericServiceException {
        DispatchContext dctx = dispatcher.getDispatchContext();
        try {
            return dctx.getModelService(this.getServiceName()).makeValid(getWRD(event, initialContext), ModelService.IN_PARAM);
        } catch (BaseException e) {
            throw new GenericServiceException(e);
        }
    }
}
