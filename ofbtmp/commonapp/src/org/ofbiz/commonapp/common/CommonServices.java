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
package org.ofbiz.commonapp.common;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.service.DispatchContext;
import org.ofbiz.core.service.ModelService;
import org.ofbiz.core.service.ServiceUtil;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.HttpClient;
import org.ofbiz.core.util.HttpClientException;
import org.ofbiz.core.util.UtilDateTime;
import org.ofbiz.core.util.UtilMisc;
import org.ofbiz.core.util.UtilProperties;
import org.ofbiz.core.util.UtilValidate;

/**
 * Common Services
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class CommonServices {
    
    public final static String module = CommonServices.class.getName();

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
    
    public static Map testWorkflowCondition(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        result.put("evaluationResult", new Boolean(true));
        return result;
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
            Debug.logWarning(e, module);
            return ServiceUtil.returnError("Malformed URL: " + bodyUrl + "; error was: " + e.toString());
        }

        HttpClient httpClient = new HttpClient(url, bodyUrlParameters);
        String body = null;

        try {
            body = httpClient.post();
        } catch (HttpClientException e) {
            Debug.logWarning(e, module);
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
        String authUser = (String) context.get("authUser");
        String authPass = (String) context.get("authPass");
        String contentType = (String) context.get("contentType");
        boolean useSmtpAuth = false;      
          
        // define some default       
        if (sendType == null || sendType.equals("mail.smtp.host")) {
            sendType = "mail.smtp.host";
            if (sendVia == null || sendVia.length() == 0) {            
                sendVia = UtilProperties.getPropertyValue("general.properties", "mail.smtp.relay.host", "localhost");
            }
            if (authUser == null || authUser.length() == 0) {
                authUser = UtilProperties.getPropertyValue("general.properties", "mail.smtp.auth.user");
            }
            if (authPass == null || authPass.length() == 0) {
                authPass = UtilProperties.getPropertyValue("general.properties", "mail.smtp.auth.password");
            }
            if (authUser != null && authUser.length() > 0) {
                useSmtpAuth = true;
            }
        } else if (sendVia == null) {
            return ServiceUtil.returnError("Parameter sendVia is required when sendType is not mail.smtp.host");
        }
                       
        if (contentType == null) {
            contentType = "text/html";
        }
                     
        try {
            Properties props = System.getProperties();
            props.put(sendType, sendVia);
            if (useSmtpAuth) {
                props.put("mail.smtp.auth", "true");
            }
            
            Session session = Session.getInstance(props);

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
            mail.saveChanges();

            Transport trans = session.getTransport("smtp");
            if (!useSmtpAuth) {
                trans.connect();
            } else {
                trans.connect(sendVia, authUser, authPass);
            }
            trans.sendMessage(mail, mail.getAllRecipients()); 
            trans.close();                  
        } catch (Exception e) {
            Debug.logError(e, "Cannot send mail message", module);
            return ServiceUtil.returnError("Cannot send mail; see logs");
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
    
    /** 
     * Echo service; returns exactly what was sent. 
     * This service does not have required parameters and does not validate
     */
     public static Map echoService(DispatchContext dctx, Map context) {
         context.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
         return context;
     }
}

