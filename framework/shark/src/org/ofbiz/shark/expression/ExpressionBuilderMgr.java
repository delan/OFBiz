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
package org.ofbiz.shark.expression;

import org.enhydra.shark.api.client.wfservice.ExpressionBuilderManager;
import org.enhydra.shark.api.common.ActivityIteratorExpressionBuilder;
import org.enhydra.shark.api.common.AssignmentIteratorExpressionBuilder;
import org.enhydra.shark.api.common.EventAuditIteratorExpressionBuilder;
import org.enhydra.shark.api.common.ProcessIteratorExpressionBuilder;
import org.enhydra.shark.api.common.ProcessMgrIteratorExpressionBuilder;
import org.enhydra.shark.api.common.ResourceIteratorExpressionBuilder;

/**
 * Expression Builder Manager Object
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.3
 */
public class ExpressionBuilderMgr implements ExpressionBuilderManager {

    public ActivityIteratorExpressionBuilder getActivityIteratorExpressionBuilder() {
        throw new RuntimeException("Not implemented yet!");
    }

    public AssignmentIteratorExpressionBuilder getAssignmentIteratorExpressionBuilder() {
        throw new RuntimeException("Not implemented yet!");
    }

    public EventAuditIteratorExpressionBuilder getEventAuditIteratorExpressionBuilder() {
        throw new RuntimeException("Not implemented yet!");
    }

    public ProcessIteratorExpressionBuilder getProcessIteratorExpressionBuilder() {
        throw new RuntimeException("Not implemented yet!");
    }

    public ProcessMgrIteratorExpressionBuilder getProcessMgrIteratorExpressionBuilder() {
        return new ProcessMgrIteratorCondExprBldr();
    }

    public ResourceIteratorExpressionBuilder getResourceIteratorExpressionBuilder() {
        throw new RuntimeException("Not implemented yet!");
    }
}
