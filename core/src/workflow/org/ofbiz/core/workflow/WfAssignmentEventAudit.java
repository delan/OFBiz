/*
 * $Id$
 *
 * Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.workflow;


/**
 * WfAssignmentEventAudit - Workflow Assignment Event Audit Interface
 *
 *@author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 *@created    October 29, 2001
 *@version    1.0
 */

public interface WfAssignmentEventAudit extends WfEventAudit {

    /**
     * @throws WfException
     * @return
     */
    public String oldResourceKey() throws WfException;

    /**
     * @throws WfException
     * @return
     */
    public String oldResourceName() throws WfException;

    /**
     * @throws WfException
     * @return
     */
    public String newResourceKey() throws WfException;

    /**
     * @throws WfException
     * @return
     */
    public String newResourceName() throws WfException;

} // interface WfAssignmentEventAuditOperations
