/*
 * $Id: CommonServices.java,v 1.5 2003/12/03 18:51:40 ajzeneski Exp $
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
package org.ofbiz.common;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.transaction.xa.XAException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.ServiceXaWrapper;

/**
 * Common Services
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.5 $
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
        Map response = new HashMap();

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
        Timestamp now = UtilDateTime.nowTimestamp();
        String partyId = (String) context.get("partyId");
        String noteName = (String) context.get("noteName");
        String note = (String) context.get("note");
        String noteId = null;

        // create the note id
        Long newId = delegator.getNextSeqId("NoteData");

        if (newId == null) {
            return ServiceUtil.returnError("ERROR: Could not create note data (id generation failure)");
        } else {
            noteId = newId.toString();
        }

        // check for a party id
        if (partyId == null) {
            if (userLogin != null && userLogin.get("partyId") != null)
                partyId = userLogin.getString("partyId");
        }

        Map fields = UtilMisc.toMap("noteId", noteId, "noteName", noteName, "noteInfo", note,
                "noteParty", partyId, "noteDateTime", now);

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
}

