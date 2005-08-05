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
package org.ofbiz.service.mail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.GenericValue;

import org.w3c.dom.Element;


/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.3
 */
public class ServiceMcaCondition implements java.io.Serializable {

    public static final String module = ServiceMcaCondition.class.getName();
    public static final int CONDITION_FIELD = 1;
    public static final int CONDITION_HEADER = 2;
    public static final int CONDITION_SERVICE = 3;

    protected String serviceName = null;
    protected String headerName = null;
    protected String fieldName = null;
    protected String operator = null;
    protected String value = null;

    public ServiceMcaCondition(Element condElement, int condType) {
        switch (condType) {
            case CONDITION_FIELD:
                // fields: from|to|subject|body|sent-date|receieved-date
                this.fieldName = condElement.getAttribute("field-name");
                // operators: equals|not-equals|empty|not-empty|matches|not-matches
                this.operator = condElement.getAttribute("operator");
                // value to compare
                this.value = condElement.getAttribute("value");
                break;
            case CONDITION_HEADER:
                // free form header name
                this.headerName = condElement.getAttribute("header-name");
                // operators: equals|not-equals|empty|not-empty|matches|not-matches
                this.operator = condElement.getAttribute("operator");
                // value to compare
                this.value = condElement.getAttribute("value");
                break;
            case CONDITION_SERVICE:
                this.serviceName = condElement.getAttribute("service-name");
                break;
        }
    }

    public boolean eval(LocalDispatcher dispatcher, MimeMessageWrapper messageWrapper, GenericValue userLogin) {
        boolean passedCondition = false;
        if (serviceName != null) {
            Map result = null;
            try {
                result = dispatcher.runSync(serviceName, UtilMisc.toMap("messageWrapper", messageWrapper, "userLogin", userLogin));
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return false;
            }
            if (result == null) {
                Debug.logError("Service MCA Condition Service [" + serviceName + "] returned null!", module);
                return false;
            } else {
                if (ServiceUtil.isError(result)) {
                    Debug.logError(ServiceUtil.getErrorMessage(result), module);
                    return false;
                } else {
                    Boolean reply = (Boolean) result.get("conditionReply");
                    if (reply == null) {
                        reply = Boolean.FALSE;
                    }
                    return reply.booleanValue();
                }
            }
            // invoke the condition service
        } else if (headerName != null) {
            // compare the header field
            MimeMessage message = messageWrapper.getMessage();
            String[] headerValue = null;
            try {
                headerValue = message.getHeader(headerName);
            } catch (MessagingException e) {
                Debug.logError(e, module);
            }

            if (headerValue != null) {
                for (int i = 0; i < headerValue.length; i++) {
                    if ("equals".equals(operator)) {
                        if (headerValue[i].equals(value)) {
                            passedCondition = true;
                            break;
                        }
                    } else if ("not-equals".equals(operator)) {
                        if (!headerValue[i].equals(value)) {
                            passedCondition = true;
                        } else {
                            passedCondition = false;
                        }
                    } else if ("matches".equals(operator)) {
                        if (headerValue[i].matches(value)) {
                            passedCondition = true;
                            break;
                        }
                    } else if ("not-matches".equals(operator)) {
                        if (!headerValue[i].matches(value)) {
                            passedCondition = true;
                        } else {
                            passedCondition = false;
                        }
                    } else if ("not-empty".equals(operator)) {
                        passedCondition = true;
                        break;
                    }
                }
            } else if ("empty".equals(operator)) {
                passedCondition = true;
            }
        } else if (fieldName != null) {
            MimeMessage message = messageWrapper.getMessage();
            String[] fieldValue = null;
            try {
                fieldValue = this.getFieldValue(message, fieldName);
            } catch (MessagingException e) {
                Debug.logError(e, module);
            } catch (IOException e) {
                Debug.logError(e, module);
            }

            if (fieldValue != null) {
                for (int i = 0; i < fieldValue.length; i++) {
                    if ("equals".equals(operator)) {
                        if (fieldValue[i].equals(value)) {
                            passedCondition = true;
                            break;
                        }
                    } else if ("not-equals".equals(operator)) {
                        if (!fieldValue[i].equals(value)) {
                            passedCondition = true;
                        } else {
                            passedCondition = false;
                        }
                    } else if ("matches".equals(operator)) {
                        if (fieldValue[i].matches(value)) {
                            passedCondition = true;
                            break;
                        }
                    } else if ("not-matches".equals(operator)) {
                        if (!fieldValue[i].matches(value)) {
                            passedCondition = true;
                        } else {
                            passedCondition = false;
                        }
                    } else if ("not-empty".equals(operator)) {
                        passedCondition = true;
                        break;
                    }
                }
            } else if ("empty".equals(operator)) {
                passedCondition = true;
            }
        } else {
            passedCondition = false;
        }

        return passedCondition;
    }

    protected String[] getFieldValue(MimeMessage message, String fieldName) throws MessagingException, IOException {
        String[] values = null;
        if ("to".equals(fieldName)) {
            Address[] addrs = message.getRecipients(MimeMessage.RecipientType.TO);
            if (addrs != null) {
                values = new String[addrs.length];
                for (int i = 0; i < addrs.length; i++) {
                    values[i] = addrs[i].toString();
                }
            }
        } else if ("cc".equals(fieldName)) {
            Address[] addrs = message.getRecipients(MimeMessage.RecipientType.CC);
            if (addrs != null) {
                values = new String[addrs.length];
                for (int i = 0; i < addrs.length; i++) {
                    values[i] = addrs[i].toString();
                }
            }
        } else if ("bcc".equals(fieldName)) {
            Address[] addrs = message.getRecipients(MimeMessage.RecipientType.BCC);
            if (addrs != null) {
                values = new String[addrs.length];
                for (int i = 0; i < addrs.length; i++) {
                    values[i] = addrs[i].toString();
                }
            }
        } else if ("from".equals(fieldName)) {
            Address[] addrs = message.getFrom();
            if (addrs != null) {
                values = new String[addrs.length];
                for (int i = 0; i < addrs.length; i++) {
                    values[i] = addrs[i].toString();
                }
            }
        } else if ("subject".equals(fieldName)) {
            values = new String[1];
            values[0] = message.getSubject();
        } else if ("send-date".equals(fieldName)) {
            values = new String[1];
            values[0] = message.getSentDate().toString();
        } else if ("received-date".equals(fieldName)) {
            values = new String[1];
            values[0] = message.getReceivedDate().toString();
        } else if ("body".equals(fieldName)) {
            List bodyParts = this.getBodyText(message);
            values = new String[bodyParts.size()];
            for (int i = 0; i < bodyParts.size(); i++) {
                values[i] = (String) bodyParts.get(i);
            }
        }
        return values;
    }

    private List getBodyText(Part part) throws MessagingException, IOException {
        Object c = part.getContent();
        if (c instanceof String) {
            return UtilMisc.toList(c);
        } else if (c instanceof Multipart) {
            List textContent = new ArrayList();
            int count = ((Multipart) c).getCount();
            for (int i = 0; i < count; i++) {
                BodyPart bp = ((Multipart) c).getBodyPart(i);
                textContent.addAll(this.getBodyText(bp));
            }
            return textContent;
        } else {
            return new ArrayList();
        }
    }
}
