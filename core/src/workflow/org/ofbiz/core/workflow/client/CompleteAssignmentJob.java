/*
 * $Id$
 *
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.workflow.client;

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.job.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.workflow.*;

/**
 * Workflow Client API - Complete Assignment Async-Job
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    March 5, 2001
 *@version    1.0
 */
public class CompleteAssignmentJob extends AbstractJob {

    protected WfAssignment assign;
    protected Map result;

    public CompleteAssignmentJob(WfAssignment assign, Map result) {
        super(assign.toString());
        this.assign = assign;
        this.result = result;
        runtime = new Date().getTime();
    }

    public void exec() {
        try {
            assign.setResult(result);
            assign.complete();
        } catch (Exception e) {
            Debug.logError("Complete Assignment Failed.");
            e.printStackTrace();
        }
        finish();
    }

    protected void finish() {
        runtime = -1;
    }

}

