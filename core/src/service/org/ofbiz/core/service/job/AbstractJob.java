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
package org.ofbiz.core.service.job;

import java.util.*;

import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * Abstract Service Job - Invokes a service
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public abstract class AbstractJob implements Job {

    public static final String module = AbstractJob.class.getName();

    protected long runtime = -1;
    protected long sequence = 0;
    private String jobName;

    protected AbstractJob(String jobName) {
        this.jobName = jobName;
    }

    /**
     * Returns the time to run in milliseconds.
     */
    public long getRuntime() {
        return runtime;
    }

    /**
     * Returns true if this job is still valid.
     */
    public boolean isValid() {
        if (runtime > 0)
            return true;
        return false;
    }

    /**
     * Returns the name of this Job.
     */
    public String getJobName() {
        return this.jobName;
    }

    /**
     *  Executes the Job.
     * @param dctx The DispatchContext used to invoke the service
     */
    public abstract void exec();
}
