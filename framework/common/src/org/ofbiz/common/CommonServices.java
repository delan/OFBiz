/*
 * $Id: CommonServices.java 7788 2006-06-13 18:26:15Z jonesde $
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
package org.ofbiz.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.transaction.xa.XAException;
import javax.mail.internet.MimeMessage;
import javax.mail.Address;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.ByteWrapper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.ServiceXaWrapper;
import org.ofbiz.service.mail.MimeMessageWrapper;

/**
 * Common Services
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      2.0
 */
public class CommonServices {

    public final static String module = CommonServices.class.getName();

    /**
     * Generic Test Service
     *@param dctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map testService(DispatchContext dctx, Map context) {
        Map response = ServiceUtil.returnSuccess();

        if (context.size() > 0) {
            Iterator i = context.keySet().iterator();

            while (i.hasNext()) {
                Object cKey = i.next();
                Object value = context.get(cKey);

                System.out.println("---- SVC-CONTEXT: " + cKey + " => " + value);
            }
        }
        if (!context.containsKey("message")) {
            response.put("resp", "no message found");
        } else {
            System.out.println("-----SERVICE TEST----- : " + (String) context.get("message"));
            response.put("resp", "service done");
        }

        System.out.println("----- SVC: " + dctx.getName() + " -----");
        return response;
    }

    public static Map blockingTestService(DispatchContext dctx, Map context) {
        System.out.println("-----SERVICE BLOCKING----- : 30 seconds");
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
        }
        return CommonServices.testService(dctx, context);
    }

    public static Map testWorkflowCondition(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        result.put("evaluationResult", new Boolean(true));
        return result;
    }

    public static Map testRollbackListener(DispatchContext dctx, Map context) {
        ServiceXaWrapper xar = new ServiceXaWrapper(dctx);
        xar.setRollbackService("testScv", context);
        try {
            xar.enlist();
        } catch (XAException e) {
            Debug.logError(e, module);
        }
        return ServiceUtil.returnError("Rolling back!");
    }

    public static Map testCommitListener(DispatchContext dctx, Map context) {
        ServiceXaWrapper xar = new ServiceXaWrapper(dctx);
        xar.setCommitService("testScv", context);
        try {
            xar.enlist();
        } catch (XAException e) {
            Debug.logError(e, module);
        }
        return ServiceUtil.returnSuccess();
    }

    /**
     * Create Note Record
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map createNote(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp noteDate = (Timestamp) context.get("noteDate");
        String partyId = (String) context.get("partyId");
        String noteName = (String) context.get("noteName");
        String note = (String) context.get("note");
        String noteId = delegator.getNextSeqId("NoteData");
        if (noteDate == null) {
            noteDate = UtilDateTime.nowTimestamp();
        }


        // check for a party id
        if (partyId == null) {
            if (userLogin != null && userLogin.get("partyId") != null)
                partyId = userLogin.getString("partyId");
        }

        Map fields = UtilMisc.toMap("noteId", noteId, "noteName", noteName, "noteInfo", note,
                "noteParty", partyId, "noteDateTime", noteDate);

        try {
            GenericValue newValue = delegator.makeValue("NoteData", fields);

            delegator.create(newValue);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Could update note data (write failure): " + e.getMessage());
        }
        Map result = ServiceUtil.returnSuccess();

        result.put("noteId", noteId);
        return result;
    }

    /**
     * Service for setting debugging levels.
     *@param dctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map setDebugLevels(DispatchContext dctx, Map context) {
        Boolean verbose = (Boolean) context.get("verbose");
        Boolean timing = (Boolean) context.get("timing");
        Boolean info = (Boolean) context.get("info");
        Boolean important = (Boolean) context.get("important");
        Boolean warning = (Boolean) context.get("warning");
        Boolean error = (Boolean) context.get("error");
        Boolean fatal = (Boolean) context.get("fatal");

        if (verbose != null)
            Debug.set(Debug.VERBOSE, verbose.booleanValue());
        else
            Debug.set(Debug.VERBOSE, false);
        if (timing != null)
            Debug.set(Debug.TIMING, timing.booleanValue());
        else
            Debug.set(Debug.TIMING, false);
        if (info != null)
            Debug.set(Debug.INFO, info.booleanValue());
        else
            Debug.set(Debug.INFO, false);
        if (important != null)
            Debug.set(Debug.IMPORTANT, important.booleanValue());
        else
            Debug.set(Debug.IMPORTANT, false);
        if (warning != null)
            Debug.set(Debug.WARNING, warning.booleanValue());
        else
            Debug.set(Debug.WARNING, false);
        if (error != null)
            Debug.set(Debug.ERROR, error.booleanValue());
        else
            Debug.set(Debug.ERROR, false);
        if (fatal != null)
            Debug.set(Debug.FATAL, fatal.booleanValue());
        else
            Debug.set(Debug.FATAL, false);

        return ServiceUtil.returnSuccess();
    }

    public static Map forceGc(DispatchContext dctx, Map context) {
        System.gc();
        return ServiceUtil.returnSuccess();
    }

    /**
     * Echo service; returns exactly what was sent.
     * This service does not have required parameters and does not validate
     */
     public static Map echoService(DispatchContext dctx, Map context) {
         context.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
         return context;
     }

    /**
     * Return Error Service; Used for testing error handling
     */
    public static Map returnErrorService(DispatchContext dctx, Map context) {
        return ServiceUtil.returnError("Return Error Service : Returning Error");
    }

    /**
     * Return TRUE Service; ECA Condition Service
     */
    public static Map conditionTrueService(DispatchContext dctx, Map context) {
        Map result = ServiceUtil.returnSuccess();
        result.put("conditionReply", Boolean.TRUE);
        return result;
    }

    /**
     * Return FALSE Service; ECA Condition Service
     */
    public static Map conditionFalseService(DispatchContext dctx, Map context) {
        Map result = ServiceUtil.returnSuccess();
        result.put("conditionReply", Boolean.FALSE);
        return result;
    }

    /** Cause a Referential Integrity Error */
    public static Map entityFailTest(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();

        // attempt to create a DataSource entity w/ an invalid dataSourceTypeId
        GenericValue newEntity = delegator.makeValue("DataSource", null);
        newEntity.set("dataSourceId", "ENTITY_FAIL_TEST");
        newEntity.set("dataSourceTypeId", "ENTITY_FAIL_TEST");
        newEntity.set("description", "Entity Fail Test - Delete me if I am here");
        try {
            delegator.create(newEntity);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Unable to create test entity");
        }

        /*
        try {
            newEntity.remove();
        } catch(GenericEntityException e) {
            Debug.logError(e, module);
        }
        */

        return ServiceUtil.returnSuccess();
    }

    /** Test entity sorting */
    public static Map entitySortTest(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        Set set = new TreeSet();

        set.add(delegator.getModelEntity("Person"));
        set.add(delegator.getModelEntity("PartyRole"));
        set.add(delegator.getModelEntity("Party"));
        set.add(delegator.getModelEntity("ContactMech"));
        set.add(delegator.getModelEntity("PartyContactMech"));
        set.add(delegator.getModelEntity("OrderHeader"));
        set.add(delegator.getModelEntity("OrderItem"));
        set.add(delegator.getModelEntity("OrderContactMech"));
        set.add(delegator.getModelEntity("OrderRole"));
        set.add(delegator.getModelEntity("Product"));
        set.add(delegator.getModelEntity("RoleType"));

        Iterator i = set.iterator();
        while (i.hasNext()) {
            Debug.log(((ModelEntity)i.next()).getEntityName(), module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map makeALotOfVisits(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        int count = ((Integer) context.get("count")).intValue();

        for (int i = 0; i < count; i++ ) {
            GenericValue v = delegator.makeValue("Visit", null);
            String seqId = delegator.getNextSeqId("Visit").toString();

            v.set("visitId", seqId);
            v.set("userCreated", "N");
            v.set("sessionId", "NA-" + seqId);
            v.set("serverIpAddress", "127.0.0.1");
            v.set("serverHostName", "localhost");
            v.set("webappName", "webtools");
            v.set("initialLocale", "en_US");
            v.set("initialRequest", "http://localhost:8080/webtools/control/main");
            v.set("initialReferrer", "http://localhost:8080/webtools/control/main");
            v.set("initialUserAgent", "Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en-us) AppleWebKit/124 (KHTML, like Gecko) Safari/125.1");
            v.set("clientIpAddress", "127.0.0.1");
            v.set("clientHostName", "localhost");
            v.set("fromDate", UtilDateTime.nowTimestamp());

            try {
                delegator.create(v);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map displayXaDebugInfo(DispatchContext dctx, Map context) {
        if (TransactionUtil.debugResources) {
            if (TransactionUtil.debugResMap != null && TransactionUtil.debugResMap.size() > 0) {
                TransactionUtil.logRunningTx();
            } else {
                Debug.log("No running transaction to display.", module);
            }
        } else {
            Debug.log("Debug resources is disabled.", module);
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map byteWrapperTest(DispatchContext dctx, Map context) {
        ByteWrapper wrapper1 = (ByteWrapper) context.get("byteWrapper1");
        ByteWrapper wrapper2 = (ByteWrapper) context.get("byteWrapper2");
        String fileName1 = (String) context.get("saveAsFileName1");
        String fileName2 = (String) context.get("saveAsFileName2");
        String ofbizHome = System.getProperty("ofbiz.home");
        String outputPath1 = ofbizHome + (fileName1.startsWith("/") ? fileName1 : "/" + fileName1);
        String outputPath2 = ofbizHome + (fileName2.startsWith("/") ? fileName2 : "/" + fileName2);

        try {
            RandomAccessFile file1 = new RandomAccessFile(outputPath1, "rw");
            RandomAccessFile file2 = new RandomAccessFile(outputPath2, "rw");
            file1.write(wrapper1.getBytes());
            file2.write(wrapper2.getBytes());
        } catch (FileNotFoundException e) {
            Debug.logError(e, module);
        } catch (IOException e) {
            Debug.logError(e, module);
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map uploadTest(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        ByteWrapper wrapper = (ByteWrapper) context.get("uploadFile");
        String fileName = (String) context.get("_uploadFile_fileName");
        String contentType = (String) context.get("_uploadFile_contentType");

        Map createCtx = new HashMap();
        createCtx.put("binData", wrapper);
        createCtx.put("dataResourceTypeId", "OFBIZ_FILE");
        createCtx.put("dataResourceName", fileName);
        createCtx.put("dataCategoryId", "PERSONAL");
        createCtx.put("statusId", "CTNT_PUBLISHED");
        createCtx.put("mimeTypeId", contentType);
        createCtx.put("userLogin", userLogin);

        Map createResp = null;
        try {
            createResp = dispatcher.runSync("createFile", createCtx);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        if (ServiceUtil.isError(createResp)) {
            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createResp));
        }

        GenericValue dataResource = (GenericValue) createResp.get("dataResource");
        if (dataResource != null) {
            Map contentCtx = new HashMap();
            contentCtx.put("dataResourceId", dataResource.getString("dataResourceId"));
            contentCtx.put("localeString", ((Locale) context.get("locale")).toString());
            contentCtx.put("contentTypeId", "DOCUMENT");
            contentCtx.put("mimeTypeId", contentType);
            contentCtx.put("contentName", fileName);
            contentCtx.put("statusId", "CTNT_PUBLISHED");
            contentCtx.put("userLogin", userLogin);

            Map contentResp = null;
            try {
                contentResp = dispatcher.runSync("createContent", contentCtx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (ServiceUtil.isError(contentResp)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(contentResp));
            }
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map mcaTest(DispatchContext dctx, Map context) {
        MimeMessageWrapper wrapper = (MimeMessageWrapper) context.get("messageWrapper");
        MimeMessage message = wrapper.getMessage();
        try {
            if (message.getAllRecipients() != null) {
               Debug.log("To: " + UtilMisc.toListArray(message.getAllRecipients()), module);
            }
            if (message.getFrom() != null) {
               Debug.log("From: " + UtilMisc.toListArray(message.getFrom()), module);
            }
            Debug.log("Subject: " + message.getSubject(), module);
            if (message.getSentDate() != null) {
                Debug.log("Sent: " + message.getSentDate().toString(), module);
            }
            if (message.getReceivedDate() != null) {
                Debug.log("Received: " + message.getReceivedDate().toString(), module);
            }
        } catch (Exception e) {
            Debug.logError(e, module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map ping(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String message = (String) context.get("message");
        if (message == null) {
            message = "PONG";
        }

        long count = -1;
        try {
            count = delegator.findCountByAnd("SequenceValueItem", null);
        } catch (GenericEntityException e) {
            Debug.logError(e.getMessage(), module);
            return ServiceUtil.returnError("Unable to connect to datasource!");
        }

        if (count > 0) {
            Map result = ServiceUtil.returnSuccess();
            result.put("message", message);
            return result;
        } else {
            return ServiceUtil.returnError("Invalid count returned from database");
        }
    }
}

