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
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.shark.container.SharkContainer;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;

import org.enhydra.shark.api.client.wfservice.NotConnected;
import org.enhydra.shark.api.client.wfservice.ConnectFailed;
import org.enhydra.shark.api.client.wfservice.ExecutionAdministration;
import org.enhydra.shark.api.client.wfbase.BaseException;
import org.enhydra.shark.api.client.wfmodel.WfProcess;
import org.enhydra.shark.api.client.wfmodel.CannotChangeRequester;
import org.enhydra.shark.api.client.wfmodel.WfProcessIterator;

/**
 * OFBiz -> Shark Requester Factory
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      3.1
 */
public class RequesterFactory {

    public static final String module = RequesterFactory.class.getName();
    private static Map loadedRequesters = new HashMap();

    public static PersistentRequester getLoadedRequester(String requesterId) {
        return (PersistentRequester) loadedRequesters.get(requesterId);
    }

    public static PersistentRequester getNewRequester(String requesterClass) {
        return RequesterFactory.getNewRequester(requesterClass, RequesterFactory.getNextId(), UtilDateTime.nowTimestamp());
    }

    public static PersistentRequester getNewRequester(String requesterClass, String requesterId, Timestamp fromDate) {
        Object[] params = new Object[2];
        params[0] = requesterId;
        params[1] = fromDate;

        PersistentRequester req = null;
        try {
            req = (PersistentRequester) ObjectType.getInstance(requesterClass, params);
        } catch (IllegalAccessException e) {
            Debug.logError(e, module);
        } catch (InstantiationException e) {
            Debug.logError(e, module);
        } catch (ClassNotFoundException e) {
            Debug.logError(e, module);
        } catch (NoSuchMethodException e) {
            Debug.logError(e, module);
        } catch (InvocationTargetException e) {
            Debug.logError(e, module);
        }
        
        return req;
    }

    public static int restoreRequesters(GenericValue userLogin) {
        GenericDelegator delegator = SharkContainer.getDelegator();
        EntityListIterator eli = null;
        try {
            eli = delegator.findListIteratorByCondition("WfRequester", null, null, null);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }

        // need an ExecutionAdministration reference
        ExecutionAdministration exAdmin = SharkContainer.getAdminInterface().getExecutionAdministration();

        // connect to the engine
        try {
            exAdmin.connect(userLogin.getString("userLoginId"), userLogin.getString("currentPassword"), null, null);
        } catch (BaseException e) {
            Debug.logError(e, module);
        } catch (ConnectFailed e) {
            Debug.logError(e, module);
        }

        int restored = 0;
        if (eli != null) {
            GenericValue requesterValue;
            while (((requesterValue = (GenericValue) eli.next()) != null)) {
                // field values
                String requesterId = requesterValue.getString("requesterId");
                Timestamp fromDate = requesterValue.getTimestamp("fromDate");
                String className = requesterValue.getString("className");
                String classData = requesterValue.getString("classData");

                // restore the WfRequester object
                PersistentRequester req = RequesterFactory.getLoadedRequester(requesterId);
                if (req == null) {
                    req = RequesterFactory.getNewRequester(className, requesterId, fromDate);
                }

                if (req != null) {
                    // restore the requester data
                    try {
                        req.restoreData(classData);
                    } catch (PersistentRequesterException e) {
                        Debug.logError(e, "Cannot restore requester; problem with data map", module);
                        req = null; // null out the requester
                        continue;   // continue on to the next
                    }

                    // locate the WfProcess object to attach
                    WfProcess proc = null;
                    try {
                        proc = exAdmin.getProcess(requesterValue.getString("processId"));
                    } catch (NotConnected e) {
                        Debug.logError(e, module);
                    } catch (BaseException e) {
                        Debug.logError(e, module);
                    }

                    if (proc != null) {
                        try {
                            // set the requester w/ the process
                            proc.set_requester(req);
                            // set the process w/ the requester
                            req.addPerformer(proc);
                            // save the requester
                            loadedRequesters.put(req.getRequesterId(), req);
                        } catch (CannotChangeRequester e) {
                            Debug.logError(e, module);
                        } catch (BaseException e) {
                            Debug.logError(e, module);
                        }
                        restored++;
                    }
                }
            }

            try {
                eli.close();
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }

        // disconnect from the engine
        try {
            exAdmin.disconnect();
        } catch (NotConnected e) {
            Debug.logError(e, module);
        } catch (BaseException e) {
           Debug.logError(e, module);
        }
        
        return restored;
    }

    public static int storeRequester(PersistentRequester req) {
        GenericDelegator delegator = SharkContainer.getDelegator();
        int stored = 0;
        if (req != null) {
            String requesterId = req.getRequesterId();
            Timestamp fromDate = req.getFromDate();
            List performers = null;
            try {
                WfProcessIterator it = req.get_iterator_performer();
                int iteratorSize = it.how_many();
                performers = Arrays.asList(it.get_next_n_sequence(iteratorSize));
            } catch (BaseException e) {
                Debug.logError(e, module);
            } catch (Throwable t) {
                Debug.logError(t, module);
            }

            if (performers != null) {
                Iterator i = performers.iterator();
                while (i.hasNext()) {
                    GenericValue requester = delegator.makeValue("WfRequester", null);
                    WfProcess proc = (WfProcess) i.next();
                    String processId = null;
                    try {
                        processId = proc.key();
                    } catch (BaseException e) {
                        Debug.logError(e, module);
                    }
                    Debug.log("Storing requester [" + stored + "]: " + requesterId + " / " + processId, module);

                    String classData = null;
                    try {
                        classData = req.getDataString();
                    } catch (PersistentRequesterException e) {
                        Debug.logError(e, "Problem serializing the requester data; unable to persist", module);
                        return 0;
                    }

                    if (requesterId != null && processId != null) {
                        requester.set("requesterId", requesterId);
                        requester.set("processId", processId);
                        requester.set("fromDate", fromDate);
                        requester.set("className", req.getClassName());
                        requester.set("classData", classData);
                        try {
                            delegator.createOrStore(requester);
                            loadedRequesters.put(requesterId, req);
                            stored++;
                        } catch (GenericEntityException e) {
                            Debug.logError(e, module);
                        }
                    }                    
                }
            }
        }
        return stored;
    }

    public static String getNextId() {
        GenericDelegator delegator = SharkContainer.getDelegator();
        return delegator.getNextSeqId("WfRequester").toString();
    }
}
