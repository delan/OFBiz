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
package org.ofbiz.shark.requester;

import java.util.Map;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericRequester;
import org.ofbiz.service.ModelService;

import org.enhydra.shark.api.SharkTransaction;
import org.enhydra.shark.api.client.wfbase.BaseException;
import org.enhydra.shark.api.client.wfmodel.InvalidPerformer;
import org.enhydra.shark.api.client.wfmodel.WfEventAudit;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      3.3
 */
public class SimpleRequester extends AbstractRequester {

    public static final String module = SimpleRequester.class.getName();
    protected GenericRequester req = null;
    protected ModelService model = null;

    // new requester
    public SimpleRequester(GenericValue userLogin, ModelService model, GenericRequester req) {
        super(userLogin);
        this.model = model;
        this.setServiceRequester(req);

    }

    public SimpleRequester(GenericValue userLogin, ModelService model) {
        this(userLogin, model, null);
    }

    // -------------------
    // WfRequester methods
    // -------------------

    public void receive_event(WfEventAudit event) throws BaseException, InvalidPerformer {
        if (this.req != null) {
            Map out = model.makeValid(this.getWRD(event, null), ModelService.OUT_PARAM);
            req.receiveResult(out);
        }
    }

    public void receive_event(SharkTransaction sharkTransaction, WfEventAudit event) throws BaseException, InvalidPerformer {
        this.receive_event(event);
    }

    // -------------
    // local methods
    // -------------

    public void setServiceRequester(GenericRequester req) {
        this.req = req;
    }

    public GenericRequester getServiceRequester() {
        return this.req;
    }
}
