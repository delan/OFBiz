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

package org.ofbiz.commonapp.common;


import java.net.*;
import java.util.*;
import java.sql.Timestamp;
import javax.mail.*;
import javax.mail.internet.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;


/**
 * Common Services
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    January 06, 2002
 *@version    1.0
 */
public class CommonServices {

    /**
     * Generic Test Service
     *@param ctx The DispatchContext that this service is operating in
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

    /**
     * JavaMail Service that gets body content from a URL
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map sendMailFromUrl(DispatchContext ctx, Map context) {
        // pretty simple, get the content and then call the sendMail method below
        String bodyUrl = (String) context.remove("bodyUrl");
        Map bodyUrlParameters = (Map) context.remove("bodyUrlParameters");

        URL url = null;

        try {
            url = new URL(bodyUrl);
        } catch (MalformedURLException e) {
            Debug.logWarning(e);
            return ServiceUtil.returnError("Malformed URL: " + bodyUrl + "; error was: " + e.toString());
        }

        HttpClient httpClient = new HttpClient(url, bodyUrlParameters);
        String body = null;

        try {
            body = httpClient.get();
        } catch (HttpClientException e) {
            Debug.logWarning(e);
            return ServiceUtil.returnError("Error getting content: " + e.toString());
        }

        context.put("body", body);
        Map result = sendMail(ctx, context);

        result.put("body", body);
        return result;
    }

    /**
     * Basic JavaMail Service
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map sendMail(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        String sendTo = (String) context.get("sendTo");
        String sendCc = (String) context.get("sendCc");
        String sendBcc = (String) context.get("sendBcc");
        String sendFrom = (String) context.get("sendFrom");
        String subject = (String) context.get("subject");
        String body = (String) context.get("body");
        String sendType = (String) context.get("sendType");
        String sendVia = (String) context.get("sendVia");
        String contentType = (String) context.get("contentType");

        if (sendType == null) {
            sendType = "mail.smtp.host";
        }
        if (contentType == null) {
            contentType = "text/html";
        }

        try {
            Properties props = new Properties();

            props.put(sendType, sendVia);
            Session session = Session.getDefaultInstance(props);

            MimeMessage mail = new MimeMessage(session);

            mail.setFrom(new InternetAddress(sendFrom));
            mail.setSubject(subject);
            mail.addRecipients(Message.RecipientType.TO, sendTo);

            if (UtilValidate.isNotEmpty(sendCc)) {
                mail.addRecipients(Message.RecipientType.CC, sendCc);
            }
            if (UtilValidate.isNotEmpty(sendBcc)) {
                mail.addRecipients(Message.RecipientType.BCC, sendBcc);
            }

            mail.setContent(body, contentType);

            Transport.send(mail);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceUtil.returnError("Cannot send mail: " + e.getMessage());
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
        GenericDelegator delegator = (GenericDelegator) ctx.getDelegator();
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
     *@param ctx The DispatchContext that this service is operating in
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
}

