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
package org.ofbiz.content.email;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.HttpClient;
import org.ofbiz.base.util.HttpClientException;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * Email Services
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      2.0
 */
public class EmailServices {
    
    public final static String module = EmailServices.class.getName();

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
        // first check to see if sending mail is enabled
        String mailEnabled = UtilProperties.getPropertyValue("general.properties", "mail.notifications.enabled", "N");
        if (!"Y".equalsIgnoreCase(mailEnabled)) {
            // no error; just return as if we already processed
            Debug.logImportant("Mail notifications disabled in general.properties", module);
            return ServiceUtil.returnSuccess();
        }      
        String sendTo = (String) context.get("sendTo");
        String sendCc = (String) context.get("sendCc");
        String sendBcc = (String) context.get("sendBcc");
        String subject = (String) context.get("subject");
        
        // check to see if we should redirect all mail for testing
        String redirectAddress = UtilProperties.getPropertyValue("general.properties", "mail.notifications.redirectTo");
        if (UtilValidate.isNotEmpty(redirectAddress)) {
            String originalRecipients = " [To: " + sendTo + ", Cc: " + sendCc + ", Bcc: " + sendBcc + "]";
            subject = subject + originalRecipients;
            sendTo = redirectAddress;
            sendCc = null;
            sendBcc = null;
        }
        
        String sendFrom = (String) context.get("sendFrom");
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
}

