/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.core.workflow.impl;

import java.io.*;
import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.workflow.*;

/**
 * WfActivitySubFlowImplementation.java
 *
 *@author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a> 
 *@author     Oswin Ondarza and Manuel Soto
 *@created    Oct 22, 2002
 *@version    $Revision$
 */
public class WfActivitySubFlowImplementation extends WfActivityAbstractImplementation {

    public static final String module = WfActivitySubFlowImplementation.class.getName();

    public WfActivitySubFlowImplementation(WfActivityImpl wfActivity) {
        super(wfActivity);
    }

    /**
     * @see org.ofbiz.core.workflow.impl.WfActivityAbstractImplementation#run()
     */
    public void run() throws WfException {
        GenericValue subFlow = null;
        try {
            subFlow = getActivity().getDefinitionObject().getRelatedOne("WorkflowActivitySubFlow");
        } catch (GenericEntityException e) {
            throw new WfException(e.getMessage(), e);
        }
        if (subFlow == null)
            return;

        String type = "WSE_SYNCHR";
        if (subFlow.get("executionEnumId") != null)
            type = subFlow.getString("executionEnumId");

        // Build a model service
        ModelService service = new ModelService();
        service.name = service.toString();
        service.engineName = "workflow";
        service.location = subFlow.getString("packageId");
        service.invoke = subFlow.getString("subFlowProcessId");
        //service.contextInfo = null; // TODO FIXME

        String actualParameters = subFlow.getString("actualParameters");
        GenericResultWaiter waiter = runService(service, actualParameters, null);
        if (type.equals("WSE_SYNCHR")) {
            Map subResult = waiter.waitForResult();
            this.setResult(subResult);
        }
        setComplete(true);
    }
}
