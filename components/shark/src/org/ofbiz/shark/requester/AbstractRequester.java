/*
 * $Id: AbstractRequester.java,v 1.1 2004/04/22 15:41:06 ajzeneski Exp $
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
import java.sql.Timestamp;

import org.enhydra.shark.api.client.wfmodel.WfProcessIterator;
import org.enhydra.shark.api.client.wfmodel.WfProcess;
import org.enhydra.shark.api.client.wfmodel.WfEventAudit;
import org.enhydra.shark.api.client.wfmodel.InvalidPerformer;
import org.enhydra.shark.api.client.wfbase.BaseException;
import org.enhydra.shark.api.SharkTransaction;
import org.enhydra.shark.WfProcessIteratorWrapper;

/**
 * Shark Workflow Abstract Requester
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public abstract class AbstractRequester implements PersistentRequester {

    public static final String module = LoggingRequester.class.getName();
    protected List performers = new ArrayList();
    protected String requesterId = null;
    protected Timestamp fromDate = null;

    /**
     * ALL subclasses MUST call this method and MUST have a matching public constructor
     */
    protected AbstractRequester(String requesterId, Timestamp fromDate) {
        this.requesterId = requesterId;
        this.fromDate = fromDate;
    }

    public void addPerformer(WfProcess process) {
        performers.add(process);
        RequesterFactory.storeRequester(this);
    }

    public String getRequesterId() {
        return requesterId;
    }

    public Timestamp getFromDate() {
        return this.fromDate;
    }

    public int how_many_performer() throws BaseException {
        return performers.size();
    }

    public int how_many_performer(SharkTransaction trans) throws BaseException {
        return performers.size();
    }

    public WfProcessIterator get_iterator_performer() throws BaseException {
        return new WfProcessIteratorImpl(new ArrayList(performers));
    }

    public WfProcessIterator get_iterator_performer(SharkTransaction trans) throws BaseException {
        return new WfProcessIteratorImpl(trans, new ArrayList(performers));
    }

    public WfProcess[] get_sequence_performer(int i) throws BaseException {
        if (i > how_many_performer()) {
            i = how_many_performer();
        }
        return (WfProcess[]) performers.subList(0, i).toArray();
    }

    public WfProcess[] get_sequence_performer(SharkTransaction trans, int i) throws BaseException {
        if (i > how_many_performer()) {
            i = how_many_performer();
        }
        return (WfProcess[]) performers.subList(0, i).toArray();
    }

    public boolean is_member_of_performer(WfProcess process) throws BaseException {
        return performers.contains(process);
    }

    public boolean is_member_of_performer(SharkTransaction trans, WfProcess process) throws BaseException {
        return performers.contains(process);
    }

    public abstract void receive_event(WfEventAudit event) throws BaseException, InvalidPerformer;

    public abstract void receive_event(SharkTransaction trans, WfEventAudit event) throws BaseException, InvalidPerformer;

    protected class WfProcessIteratorImpl extends WfProcessIteratorWrapper {

        public WfProcessIteratorImpl(SharkTransaction trans, List performers) throws BaseException {
            super(trans, null, performers);
        }

        public WfProcessIteratorImpl(List performers) throws BaseException {
            super(null, null, performers);
        }
    }
}
