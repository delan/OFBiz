/*
 * $Id$
 */

package org.ofbiz.commonapp.common;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Common Services
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    January 06, 2002
 *@version    1.0
 */

public class CommonServices {
    
    /** Basic JavaMail Service */
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
        
        if ( sendType == null )
            sendType = "mail.smtp.host";
        if ( contentType == null )
            contentType = "text/html";
        
        try {
            Properties props = new Properties();
            props.put(sendType, sendVia);
            Session session = Session.getDefaultInstance(props);
            
            MimeMessage mail = new MimeMessage(session);
            mail.setFrom(new InternetAddress(sendFrom));
            mail.setSubject(subject);
            mail.addRecipients(Message.RecipientType.TO, sendTo);
            
            if (UtilValidate.isNotEmpty(sendCc))
                mail.addRecipients(Message.RecipientType.CC, sendCc);
            if (UtilValidate.isNotEmpty(sendBcc))
                mail.addRecipients(Message.RecipientType.BCC, sendBcc);
            
            mail.setContent(body, contentType);
            
            Transport.send(mail);
        }
        catch ( Exception e ) {
            e.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,"Exception: " + e.getMessage());
            return result;
        }
        
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
}

